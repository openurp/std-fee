/*
 * OpenURP, Agile University Resource Planning Solution.
 *
 * Copyright © 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful.
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openurp.std.fee.pay.impl

import java.io.OutputStreamWriter
import java.net.{HttpURLConnection, URL}
import java.time.format.DateTimeFormatter
import java.time.{Duration, Instant, LocalDateTime, ZoneId}
import java.{util => ju}

import com.google.gson.Gson
import org.beangle.commons.activation.MediaTypes
import org.beangle.commons.bean.Initializing
import org.beangle.commons.codec.digest.Digests
import org.beangle.commons.io.IOs
import org.beangle.commons.logging.Logging
import org.beangle.commons.net.http.{HttpUtils, Https}
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.openurp.std.fee.app.model.FeeTypeConfig
import org.openurp.std.fee.model.{Bill, Order, Product}
import org.openurp.std.fee.pay.PayService

/** SUFE 支付服务
 */
class SufePayServiceImpl extends PayService with Logging with Initializing {
  var entityDao: EntityDao = _

  var products: Map[Int, Product] = Map.empty

  //one hour
  var orderIdleSeconds = 30 * 60

  override def init(): Unit = {
    val configs = entityDao.getAll(classOf[FeeTypeConfig])
    products = configs.map { config =>
      (config.feeType.id, Product(config.productId, config.secret))
    }.toMap
  }

  override def getOrCreateOrder(bill: Bill, params: Map[String, String]): Order = {
    val query = OqlBuilder.from(classOf[Order], "pr")
    query.where("pr.bill=:bill", bill)
    val orders = entityDao.search(query)
    if (orders.nonEmpty) {
      val order = orders.head
      val now = Instant.now
      val remindSeconds = Duration.between(now, order.expiredAt).getSeconds
      if (remindSeconds < 2 * 60) { //less than 2 minutes
        refreshBillByOrder(bill, order)
        if (bill.payed <= 0) {
          entityDao.remove(order)
          createOrder(bill, params)
        } else {
          order
        }
      } else {
        order
      }
    } else {
      createOrder(bill, params)
    }
  }

  override def refreshBill(bill: Bill): Order = {
    val query = OqlBuilder.from(classOf[Order], "pr")
    query.where("pr.bill=:bill", bill)
    val orders = entityDao.search(query)
    orders foreach { order =>
      refreshBillByOrder(bill, order)
    }
    orders.headOption.orNull
  }

  private def refreshBillByOrder(bill: Bill, order: Order): Unit = {
    if (!order.paid) {
      val o = checkOrder(products(bill.feeType.id), order.code)
      order.status = o.status
      order.paid = o.paid
      order.channel = o.channel
      order.payAt = o.payAt
    }
    updateBillByOrder(bill, order)
  }

  private def updateBillByOrder(bill: Bill, order: Order): Unit = {
    if (bill.payed <= 0) {
      if (order.paid) {
        bill.payed = bill.amount
        bill.payAt = order.payAt
        bill.updatedBy = "系统自动核对"
        bill.remark = Some("")
      }
    }
    entityDao.saveOrUpdate(bill, order)
  }

  protected[impl] def createOrder(bill: Bill, params: Map[String, String]): Order = {
    val std = bill.std
    val inputs = Map("inputIdNo" -> std.person.get.code, "inputStuNo" -> std.user.code, "inputStuName" -> std.user.name, "inputPhone" -> std.user.mobile.getOrElse("--"))
    val order = createOrder(products(bill.feeType.id), bill.amount, inputs)
    order.bill = bill
    order.std = bill.std
    order.amount = bill.amount
    order.createdAt = Instant.now
    order.expiredAt = Instant.now.plusSeconds(orderIdleSeconds)
    entityDao.saveOrUpdate(order)
    order
  }

  /** 创建订单
   *
   * @param product
   * @param amount
   * @param inputs
   * @return
   */
  protected[impl] def createOrder(product: Product, amount: Int, inputs: Map[String, String]): Order = {
    val url = new URL("https://mp.sufe.edu.cn/jfapi/shufe/pay/api/create")
    val httpCon = url.openConnection.asInstanceOf[HttpURLConnection]
    Https.noverify(httpCon)
    httpCon.setDoOutput(true)
    httpCon.setRequestMethod("POST")
    httpCon.setRequestProperty("Content-Type", MediaTypes.ApplicationJson.toString)
    val os = httpCon.getOutputStream
    val osw = new OutputStreamWriter(os, "UTF-8")
    osw.write(prepareData(product, amount, inputs))
    osw.flush()
    osw.close()
    os.close() //don't forget to close the OutputStream
    httpCon.connect()
    //read the inputstream and print it
    val lines = IOs.readString(httpCon.getInputStream)
    if (lines.contains("success")) {
      parseOrderCreation(lines)
    } else {
      logger.error(s"Create Order error for bill ${inputs},api response ${lines}")
      null
    }
  }

  protected[impl] def parseOrderCreation(res: String): Order = {
    val rs = new Gson().fromJson(res, classOf[java.util.Map[_, _]])
    if (rs.get("status") == "success") {
      val data = rs.get("data").asInstanceOf[ju.Map[_, _]]
      val orderNo = data.get("orderNo").toString
      val oauthPayUrl = data.get("oauthPayUrl").toString
      val order = new Order()
      order.code = orderNo
      order.status = "PAY_REQUESTED"
      order.payUrl = oauthPayUrl
      order
    } else {
      null
    }
  }

  /** 准备创建订单的JSON数据
   *
   * @param product
   * @param amount
   * @param inputs
   * @return
   */
  protected[impl] def prepareData(product: Product, amount: Int, inputs: Map[String, String]): String = {
    val data = new java.util.HashMap[String, Any]
    data.put("productId", product.id)
    data.put("payAmount", amount)
    val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    data.put("timestamp", timestamp)
    data.put("sign", sign(product, timestamp, amount))
    inputs foreach { case (k, v) =>
      data.put(k, v)
    }
    new Gson().toJson(data)
  }

  /** 签名订单信息
   *
   * @param product
   * @param timestamp
   * @param amount
   * @return
   */
  protected[impl] def sign(product: Product, timestamp: String, amount: Int): String = {
    val waitSign = s"productId=${product.id}&payAmount=${amount}&timestamp=${timestamp}&secret=${product.secret}"
    Digests.md5Hex(Digests.md5Hex(waitSign)).toUpperCase()
  }

  /** 核对订单
   *
   * @param product
   * @param orderNo
   * @return
   */
  protected[impl] def checkOrder(product: Product, orderNo: String): Order = {
    val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    val queryString = s"productId=${product.id}&timestamp=${timestamp}&secret=${product.secret}"
    val sign = Digests.md5Hex(Digests.md5Hex(queryString)).toUpperCase()
    val url = s"https://mp.sufe.edu.cn/jfadmin/admin/open/pay/order/get?orderNo=${orderNo}&" + queryString + s"&sign=${sign}"
    val res =HttpUtils.getText(url)
    if(res.status ==200){
      parseOrderStatus(res.getText)
    }else{
      null
    }
  }

  protected[impl] def parseOrderStatus(res: String): Order = {
    val rs = new Gson().fromJson(res, classOf[java.util.Map[_, _]])
    val data = rs.get("data").asInstanceOf[ju.Map[_, _]]
    val orderNo = data.get("orderNo").toString
    val status = data.get("orderStatus").toString
    val order = new Order()

    order.code = orderNo
    order.status = status
    if (status == "PAY_COMPLETED") {
      order.paid = true
      val completeTime = data.get("completeTime").toString
      val payAt = LocalDateTime.parse(completeTime.replace(' ', 'T'))
      order.payAt = Some(payAt.atZone(ZoneId.systemDefault()).toInstant)
      order.channel = Some(data.get("platForm").toString)
    }
    order
  }

}

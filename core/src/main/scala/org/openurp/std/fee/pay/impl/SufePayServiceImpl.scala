/*
 * Copyright (C) 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openurp.std.fee.pay.impl

import com.google.gson.Gson
import org.beangle.commons.activation.MediaTypes
import org.beangle.commons.bean.Initializing
import org.beangle.commons.codec.binary.Base64
import org.beangle.commons.codec.digest.Digests
import org.beangle.commons.io.{Files, IOs}
import org.beangle.commons.lang.Strings
import org.beangle.commons.logging.Logging
import org.beangle.commons.net.http.{HttpUtils, Https}
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.ems.app.EmsApp
import org.openurp.base.model.User
import org.openurp.std.fee.app.model.FeeTypeConfig
import org.openurp.std.fee.model.{Bill, Order}
import org.openurp.std.fee.pay.impl.SufePayServiceImpl.getInvoice
import org.openurp.std.fee.pay.{FeeClient, PayService}

import java.io.{File, FileInputStream, OutputStreamWriter}
import java.net.{HttpURLConnection, URL}
import java.time.format.DateTimeFormatter
import java.time.{Duration, Instant, LocalDateTime, ZoneId}
import java.util as ju

object SufePayServiceImpl {
  def getInvoice(orderNo: String, systemCode: String, systemKey: String): Tuple2[String, String] = {
    val seed = s"order_no=${orderNo}&systemCode=${systemCode}&systemKey=${systemKey}"
    val md5Key = Digests.md5Hex(seed).toUpperCase()

    val content = s"order_no=${orderNo}&systemCode=${systemCode}&md5Key=${md5Key}"

    val url = new URL("http://wechatpay.shufe.edu.cn/orderbill.ashx")
    val httpCon = url.openConnection.asInstanceOf[HttpURLConnection]
    Https.noverify(httpCon)
    httpCon.setDoOutput(true)
    httpCon.setRequestMethod("POST")
    httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
    val os = httpCon.getOutputStream
    val osw = new OutputStreamWriter(os, "UTF-8")
    osw.write(content)
    osw.flush()
    osw.close()
    os.close() //don't forget to close the OutputStream
    httpCon.connect()
    val lines = IOs.readString(httpCon.getInputStream)
    if (lines.contains("FAIL")) {
      val error = Strings.substringBetween(lines, "<return_msg><![CDATA[", "]]>")
      ("FAIL", error)
    } else if (lines.contains("error")) {
      val error = Strings.substringBetween(lines, "error:\"", "\"")
      ("ERROR", error)
    } else {
      val imgbase64 = Strings.substringBetween(lines, "[<img src='", "'/>]]></img>")
      if(Strings.isBlank(imgbase64)){
        ("ERROR", Strings.substringBetween(lines, "<img><![CDATA[", "]]></img>"))
      }else{
        ("SUCCESS", imgbase64)
      }
    }
  }

}

/** SUFE 支付服务
 */
class SufePayServiceImpl extends PayService with Logging with Initializing {
  var entityDao: EntityDao = _

  var clients: Map[Int, FeeClient] = Map.empty

  //one hour
  var orderIdleSeconds = 30 * 60

  override def init(): Unit = {
    val cfgs = entityDao.getAll(classOf[FeeTypeConfig])
    clients = cfgs.map { cfg =>
      (cfg.feeType.id, FeeClient(cfg.productId, cfg.systemCode, cfg.systemKey, cfg.secret))
    }.toMap
  }

  override def getInvoiceUrl(order: Order): (Option[String],String) = {
    val repo = EmsApp.getBlobRepository(true)
    order.invoicePath match {
      case Some(p) => (repo.path(p),"SUCCESS")
      case None =>
        if (order.paid && order.payAt.isDefined) {
          val client = clients(order.bill.feeType.id)
          val result = getInvoice(order.code, client.systemCode, client.systemKey)
          if (result._1 == "SUCCESS") {
            val bytes = Base64.decode(result._2.substring("data:image/png;base64,".length))
            val f = File.createTempFile("invoice", ".png")
            val os = Files.writeOpen(f)
            os.write(bytes)
            os.close()
            val repo = EmsApp.getBlobRepository(true)
            val std = order.bill.std
            try {
              val meta = repo.upload("/fee/invoice/" + order.payAt.get.atZone(ZoneId.systemDefault()).getYear,
                new FileInputStream(f), order.code + ".png", std.code + " " + std.name)
              order.invoicePath = Some(meta.filePath)
              entityDao.saveOrUpdate(order)
              f.delete()
              (repo.path(meta.filePath),"SUCCESS")
            } catch {
              case e: Throwable =>
                e.printStackTrace()
                (None,e.getMessage)
            }
          } else {
            (None,result._2)
          }
        } else {
          (None,"订单未支付")
        }
    }
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
      val o = checkOrder(clients(bill.feeType.id), order.code)
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
    val user = entityDao.findBy(classOf[User],"school"-> std.project.school,"code"->  std.code).head
    val inputs = Map("inputIdNo" -> std.person.code, "inputStuNo" -> std.code, "inputStuName" -> std.name, "inputPhone" -> user.mobile.getOrElse("--"))
    val order = createOrder(clients(bill.feeType.id), bill.amount, inputs)
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
   * @param client
   * @param amount
   * @param inputs
   * @return
   */
  protected[impl] def createOrder(client: FeeClient, amount: Int, inputs: Map[String, String]): Order = {
    val url = new URL("https://mp.sufe.edu.cn/jfapi/shufe/pay/api/create")
    val httpCon = url.openConnection.asInstanceOf[HttpURLConnection]
    Https.noverify(httpCon)
    httpCon.setDoOutput(true)
    httpCon.setRequestMethod("POST")
    httpCon.setRequestProperty("Content-Type", MediaTypes.ApplicationJson.toString)
    val os = httpCon.getOutputStream
    val osw = new OutputStreamWriter(os, "UTF-8")
    osw.write(prepareData(client, amount, inputs))
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
   * @param client
   * @param amount
   * @param inputs
   * @return
   */
  protected[impl] def prepareData(client: FeeClient, amount: Int, inputs: Map[String, String]): String = {
    val data = new java.util.HashMap[String, Any]
    data.put("productId", client.id)
    data.put("payAmount", amount)
    val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    data.put("timestamp", timestamp)
    data.put("sign", sign(client, timestamp, amount))
    inputs foreach { case (k, v) =>
      data.put(k, v)
    }
    new Gson().toJson(data)
  }

  /** 签名订单信息
   *
   * @param client
   * @param timestamp
   * @param amount
   * @return
   */
  protected[impl] def sign(client: FeeClient, timestamp: String, amount: Int): String = {
    val waitSign = s"productId=${client.id}&payAmount=${amount}&timestamp=${timestamp}&secret=${client.secret}"
    Digests.md5Hex(Digests.md5Hex(waitSign)).toUpperCase()
  }

  /** 核对订单
   *
   * @param product
   * @param orderNo
   * @return
   */
  protected[impl] def checkOrder(product: FeeClient, orderNo: String): Order = {
    val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    val queryString = s"productId=${product.id}&timestamp=${timestamp}&secret=${product.secret}"
    val sign = Digests.md5Hex(Digests.md5Hex(queryString)).toUpperCase()
    val url = s"https://mp.sufe.edu.cn/jfadmin/admin/open/pay/order/get?orderNo=${orderNo}&" + queryString + s"&sign=${sign}"
    val res = HttpUtils.getText(url)
    if (res.status == 200) {
      parseOrderStatus(res.getText)
    } else {
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

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

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import org.openurp.std.fee.pay.{FeeClient}
import org.scalatest.matchers.should.Matchers
import org.scalatest.funspec.AnyFunSpec

class SufePayServiceTest extends AnyFunSpec with Matchers {

  var client = FeeClient(15,"xx","xx", "123456")

  describe("PayService") {
    it("sign") {
      val sign = new SufePayServiceImpl().sign(client, "20200106102331", 1200)
      sign should be("9135748B92E1D4E2DE6803DF78A0C8DE")
    }
    it("prepareData") {
      val data = new SufePayServiceImpl().prepareData(client, 1, Map.empty)
      println(data)
    }

    it("createOrder") {
      //val inputs = Map("inputIdNo" -> "342126198009097474", "inputStuNo" -> "097474", "inputStuName" -> "段体华")
      //val da = SufePayServiceImpl.createOrder(15, "L4OA1RQ75VK4WOP9LL9BN6L3RN5YS8YU", 1, inputs)
      //println(da)
    }
    it("parseOrderCreation") {
      val response =
        """{
          |  "status": "success",
          |  "code": null,
          |  "message": null,
          |  "data": {
          |    "timestamp": "20191223172943",
          |    "productId": 2,
          |    "sign": "9899078F51AA4080FC1633BF5E949B90",
          |    "orderNo": "201912231729433960002004",
          |    "oauthPayUrl": "https://mp.sufe.edu.cn/jfapi/shufe/callback/oauth/pay?orderNo=201912231729433960002004&pId=2&timestamp=20191223172943&sign=9899078F51AA4080FC1633BF5E949B90"
          |  }
          |}""".stripMargin
      val order = new SufePayServiceImpl().parseOrderCreation(response)
      order.code should be("201912231729433960002004")
      println(order.payUrl)
    }
    it("parseOrderStatus") {
      val response =
        """
          |{"status":"success","code":null,"message":null,
          |  "data":{
          |     "orderNo":"202001070914267580015046",
          |     "productName":"成人高等教育学费",
          |     "productNum":1,
          |     "paidAmount":1,
          |     "completeTime":"2020-01-07 09:14:27",
          |     "orgId":"shufe-ali",
          |     "userId":"2088412220511390",
          |     "inputStuNo":"097474",
          |     "inputIdNo":"342126198009097474",
          |     "inputStuName":"段体华",
          |     "inputPhone":null,
          |     "accountName":"继续教育学院夜大学费",
          |     "aliAccount":"2022",
          |     "productId":15,
          |     "orderStatus":"PAY_COMPLETED",
          |     "platForm":"AliPay",
          |     "outTradeNo":null,
          |     "outTradeRemark":null
          |   }
          |}
          |""".stripMargin

      val order = new SufePayServiceImpl().parseOrderStatus(response)
      val formater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault())
      println((order.channel, order.status, order.paid, formater.format(order.payAt.get)))
    }
    it("checkOrder") {
      val order = new SufePayServiceImpl().checkOrder(client, "202001070914267580015046")
      val formater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault())
      if(null!=order){
        println((order.channel, order.status, order.paid, formater.format(order.payAt.get)))
      }
    }
  }
}

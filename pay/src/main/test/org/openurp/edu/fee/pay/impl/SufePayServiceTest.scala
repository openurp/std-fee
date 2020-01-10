package org.openurp.edu.fee.pay.impl

import java.time.ZoneId
import java.time.format.DateTimeFormatter

import org.junit.runner.RunWith
import org.openurp.edu.fee.model.Product
import org.scalatest.Matchers
import org.scalatest.funspec.AnyFunSpec
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SufePayServiceTest extends AnyFunSpec with Matchers {

  var product = Product(15, "L4OA1RQ75VK4WOP9LL9BN6L3RN5YS8YU")

  describe("PayService") {
    it("sign") {
      var testProduct = Product(15, "123456")
      val sign = new SufePayServiceImpl().sign(testProduct, "20200106102331", 1200)
      sign should be("9135748B92E1D4E2DE6803DF78A0C8DE")
    }
    it("prepareData") {
      val data = new SufePayServiceImpl().prepareData(product, 1, Map.empty)
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
      println(order.channel, order.status, order.paid, formater.format(order.payAt.get))
    }
    it("checkOrder") {
      val order = new SufePayServiceImpl().checkOrder(product, "202001070914267580015046")
      val formater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault())
      println(order.channel, order.status, order.paid, formater.format(order.payAt.get))
    }
  }
}

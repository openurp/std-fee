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

package org.openurp.std.fee.web.action.std

import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.web.action.annotation.{mapping, param}
import org.beangle.web.action.view.{Status, View}
import org.beangle.webmvc.support.action.EntityAction
import org.openurp.base.model.User
import org.openurp.base.std.model.Student
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.fee.app.model.OnlinePaySetting
import org.openurp.std.fee.model.{Bill, Order}
import org.openurp.std.fee.pay.PayService

import java.time.LocalDate

class BillAction extends EntityAction[Bill] with ProjectSupport {

  var payService: PayService = _

  var entityDao: EntityDao = _

  def index(): View = {
    val std = getUser(classOf[Student])
    val query = OqlBuilder.from(classOf[Bill], "bill")
    query.where("bill.std=:std", std)
    val bills = entityDao.search(query)
    put("bills", bills)
    put("std", std)

    val orderQuery = OqlBuilder.from(classOf[Order], "o")
    orderQuery.where("o.std=:std", std)
    val orders = entityDao.search(orderQuery).map(x => x.bill.id -> x).toMap
    put("orders", orders)

    val settingQuery = OqlBuilder.from(classOf[OnlinePaySetting], "s")
    settingQuery.where("s.endOn >= :now", LocalDate.now())
    val settings = entityDao.search(settingQuery)
    put("settings", settings)
    forward()
  }

  def displayUserInfo(): View = {
    val std = getUser(classOf[Student])
    put("std", std)
    put("user", findUser(std))
    put("bill", entityDao.get(classOf[Bill], longId("bill")))
    forward()
  }

  def saveUserInfo(): View = {
    val std = getUser(classOf[Student])
    val bill = entityDao.get(classOf[Bill], longId("bill"))
    val mobile = get("mobile")

    if (mobile.nonEmpty) {
      val user = findUser(std)
      user.mobile = mobile
      entityDao.saveOrUpdate(user)
      redirect("pay", "id=" + bill.id, "info.save.success")
    } else {
      redirect("displayUserInfo", "id=" + bill.id, "缺少手机号码")
    }
  }

  private def findUser(std: Student): User = {
    entityDao.findBy(classOf[User], "school" -> std.project.school, "code" -> std.code).head
  }

  @mapping("pay/{id}")
  def pay(@param("id") id: Long): View = {
    val bill = entityDao.get(classOf[Bill], id)
    val std = getUser(classOf[Student])
    put("bill", bill)
    put("user", findUser(std))
    put("std", std)
    if (bill.std == std && bill.payed <= 0) {
      val order = payService.getOrCreateOrder(bill, Map.empty)
      put("order", order)
    }
    forward()
  }

  @mapping("check/{id}")
  def check(@param("id") id: Long): View = {
    val bill = entityDao.get(classOf[Bill], id)
    val std = getUser(classOf[Student])
    put("bill", bill)
    put("std", std)
    var order: Order = null
    if (bill.std == std) {
      order = payService.refreshBill(bill)
    }
    put("order", order)
    forward("pay")
  }

  @mapping(value = "displayInvoice/{id}")
  def displayInvoice(id: String): View = {
    val order = entityDao.get(classOf[Order], id.toLong)
    val std = getUser(classOf[Student])
    if (order.std != std) return Status.NotFound

    payService.getInvoiceUrl(order) match {
      case (Some(p), _) => put("invoicePath", p)
      case (None, msg) => put("error", msg)
    }
    put("order", order)
    forward()
  }

  @mapping(value = "invoice/{id}")
  def invoice(id: String): View = {
    val order = entityDao.get(classOf[Order], id.toLong)
    val std = getUser(classOf[Student])
    if (order.std != std) return Status.NotFound
    payService.getInvoiceUrl(order) match {
      case (Some(p), _) => redirect(to(p), "download")
      case (None, msg) =>
        put("error", msg)
        forward()
    }
  }
}

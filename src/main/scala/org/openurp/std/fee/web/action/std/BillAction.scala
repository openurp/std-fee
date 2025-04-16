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

import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.annotation.{mapping, param}
import org.beangle.webmvc.support.action.EntityAction
import org.beangle.webmvc.view.{Status, View}
import org.openurp.base.model.User
import org.openurp.base.std.model.Student
import org.openurp.starter.web.support.StudentSupport
import org.openurp.std.fee.app.model.OnlinePaySetting
import org.openurp.std.fee.model.{Bill, Debt, Order}
import org.openurp.std.fee.pay.PayService

import java.time.LocalDate

class BillAction extends StudentSupport, EntityAction[Bill] {

  var payService: PayService = _

  override def projectIndex(std: Student): View = {
    put("debts", entityDao.findBy(classOf[Debt], "std", std))
    put("bills", entityDao.findBy(classOf[Bill], "std", std))
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
    val std = getStudent
    put("std", std)
    put("user", findUser(std))
    put("bill", entityDao.get(classOf[Bill], getLongId("bill")))
    forward()
  }

  def saveUserInfo(): View = {
    val std = getStudent
    val bill = entityDao.get(classOf[Bill], getLongId("bill"))
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
    val std = getStudent
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
    val std = getStudent
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
    val std = getStudent
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
    val std = getStudent
    if (order.std != std) return Status.NotFound
    payService.getInvoiceUrl(order) match {
      case (Some(p), _) => redirect(to(p), "download")
      case (None, msg) =>
        put("error", msg)
        forward()
    }
  }
}

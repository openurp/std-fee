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
package org.openurp.std.fee.student.web.action

import java.time.LocalDate

import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.api.annotation.{mapping, param}
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.EntityAction
import org.openurp.edu.base.model.Student
import org.openurp.std.fee.app.model.OnlinePaySetting
import org.openurp.std.fee.model.{Bill, Order}
import org.openurp.std.fee.pay.PayService
import org.openurp.edu.web.ProjectSupport

class BillAction extends EntityAction[Bill] with ProjectSupport {

  var payService: PayService = _

  def index(): View = {
    val std = getUser(classOf[Student])
    val query = OqlBuilder.from(classOf[Bill], "bill")
    query.where("bill.std=:std", std)
    val bills = entityDao.search(query)
    put("bills", bills)
    put("std", std)

    val settingQuery = OqlBuilder.from(classOf[OnlinePaySetting], "s")
    settingQuery.where("s.endOn >= :now", LocalDate.now())
    val settings = entityDao.search(settingQuery)
    put("settings", settings)
    forward()
  }

  def displayUserInfo(): View = {
    put("std", getUser(classOf[Student]))
    put("bill", entityDao.get(classOf[Bill], longId("bill")))
    forward()
  }

  def saveUserInfo(): View = {
    val std = getUser(classOf[Student])
    val bill = entityDao.get(classOf[Bill], longId("bill"))
    val mobile = get("mobile")
    if (mobile.nonEmpty) {
      std.user.mobile = mobile
      entityDao.saveOrUpdate(std.user)
      redirect("pay", "id=" + bill.id, "info.save.success")
    } else {
      redirect("displayUserInfo", "id=" + bill.id, "缺少手机号码")
    }
  }

  @mapping("pay/{id}")
  def pay(@param("id") id: Long): View = {
    val bill = entityDao.get(classOf[Bill], id)
    val std = getUser(classOf[Student])
    put("bill", bill)
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

}

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

package org.openurp.std.fee.web.action.admin

import org.beangle.webmvc.annotation.mapping
import org.beangle.webmvc.support.action.RestfulAction
import org.beangle.webmvc.view.View
import org.openurp.base.model.Project
import org.openurp.code.edu.model.EducationLevel
import org.openurp.code.std.model.FeeType
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.fee.model.Order
import org.openurp.std.fee.pay.PayService

class OrderAction extends RestfulAction[Order] with ProjectSupport {

  var payService: PayService = _

  override protected def indexSetting(): Unit = {
    given project: Project = getProject

    put("feeTypes", getCodes(classOf[FeeType]))
    put("levels", getCodes(classOf[EducationLevel]))
  }

  override def simpleEntityName: String = {
    "o"
  }

  def check(): View = {
    val ids = getLongIds("o")
    val orders = entityDao.find(classOf[Order], ids)
    orders foreach { order =>
      if !order.paid then payService.refreshBill(order.bill)
    }
    redirect("search", "info.save.success")
  }

  @mapping(value = "displayInvoice/{id}")
  def displayInvoice(id: String): View = {
    val order = entityDao.get(classOf[Order], id.toLong)
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
    payService.getInvoiceUrl(order) match {
      case (Some(p), _) => redirect(to(p), "download")
      case (None, msg) =>
        put("error", msg)
        forward()
    }
  }
}

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
package org.openurp.std.fee.admin.web.action

import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.code.edu.model.EducationLevel
import org.openurp.edu.web.ProjectSupport
import org.openurp.std.fee.model.{FeeType, Order}
import org.openurp.std.fee.pay.PayService

class OrderAction extends RestfulAction[Order] with ProjectSupport {

  var payService: PayService = _

  override protected def indexSetting(): Unit = {
    put("feeTypes", getCodes(classOf[FeeType]))
    put("levels", getCodes(classOf[EducationLevel]))
  }

  override def simpleEntityName: String = {
    "o"
  }

  def check(): View = {
    val ids = longIds("o")
    val orders = entityDao.find(classOf[Order], ids)
    orders foreach { order =>
      if (!order.paid) {
        payService.refreshBill(order.bill)
      }
    }
    redirect("search", "info.save.success")
  }
}

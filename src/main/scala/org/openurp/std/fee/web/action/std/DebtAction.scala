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

import org.beangle.webmvc.support.action.EntityAction
import org.beangle.webmvc.view.View
import org.openurp.base.std.model.Student
import org.openurp.starter.web.support.StudentSupport
import org.openurp.std.fee.model.Debt

class DebtAction extends StudentSupport, EntityAction[Debt] {

  override def projectIndex(std: Student): View = {
    put("debts", entityDao.findBy(classOf[Debt], "std", std))
    put("std", std)
    forward()
  }

  def portalet(): View = {
    val std = getStudent
    put("debts", entityDao.findBy(classOf[Debt], "std", std))
    put("std", std)
    forward()
  }
}

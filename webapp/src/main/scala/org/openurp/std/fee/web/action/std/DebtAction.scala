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

import org.beangle.data.dao.EntityDao
import org.beangle.web.action.support.ActionSupport
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.EntityAction
import org.openurp.base.std.model.Student
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.fee.model.Debt

class DebtAction extends ActionSupport, EntityAction[Debt], ProjectSupport {
  var entityDao: EntityDao = _

  def index(): View = {
    val std = getUser(classOf[Student])
    put("debts", entityDao.findBy(classOf[Debt], "std", std))
    put("std", std)
    forward()
  }

  def portalet():View={
    val std = getUser(classOf[Student])
    put("debts", entityDao.findBy(classOf[Debt], "std", std))
    put("std", std)
    forward()
  }
}

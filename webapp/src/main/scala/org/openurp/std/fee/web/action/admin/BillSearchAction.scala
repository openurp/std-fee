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

import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.support.action.{ExportSupport, RestfulAction}
import org.openurp.base.model.Project
import org.openurp.code.edu.model.EducationLevel
import org.openurp.code.std.model.FeeType
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.fee.model.Bill

class BillSearchAction extends RestfulAction[Bill], ProjectSupport, ExportSupport[Bill] {

  override def indexSetting(): Unit = {
    given project: Project = getProject

    put("feeTypes", getCodes(classOf[FeeType]))
    put("levels", getCodes(classOf[EducationLevel]))
    put("currentSemester", getSemester)
    put("project", project)
  }

  override protected def getQueryBuilder: OqlBuilder[Bill] = {
    val query = super.getQueryBuilder
    getBoolean("paid") foreach { payed =>
      if (payed) {
        query.where("bill.payed > 0")
      } else {
        query.where("bill.payed <= 0")
      }
    }
    getBoolean("student_inschool") foreach { inschool =>
      query.where("bill.std.state.inschool=:inschool", inschool)
    }
    query
  }
}

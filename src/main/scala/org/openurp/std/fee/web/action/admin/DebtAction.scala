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

import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.support.action.{ExportSupport, RestfulAction}
import org.beangle.webmvc.view.View
import org.openurp.base.model.{Project, Semester}
import org.openurp.base.std.model.Student
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.fee.model.Debt

import java.time.Instant

class DebtAction extends RestfulAction[Debt], ExportSupport[Debt], ProjectSupport {

  override def indexSetting(): Unit = {
    given project: Project = getProject

    put("project", project)
    put("departs", project.departments)
  }

  override protected def getQueryBuilder: OqlBuilder[Debt] = {
    given project: Project = getProject

    val query = super.getQueryBuilder
    query.where("debt.std.state.department in(:departs)", getDeparts)
    query
  }

  def reset(): View = {
    put("semester", entityDao.get(classOf[Semester], getIntId("debt.semester")))
    forward()
  }

  def batchReset(): View = {
    var code = get("std_codes").orNull
    code = Strings.replace(code, "，", ",")
    val codes = Strings.split(code)
    val students = entityDao.findBy(classOf[Student], "user.code", codes)
    val uts = students.map { std =>
      val ut = new Debt
      ut.std = std
      ut.updatedAt = Instant.now
      ut
    }
    val exists = OqlBuilder.from(classOf[Debt], "ut")
    exists.where("ut.std.project=:project", getProject);
    entityDao.remove(entityDao.search(exists))
    saveOrUpdate(uts)
    redirect("search", "info.save.success")
  }
}

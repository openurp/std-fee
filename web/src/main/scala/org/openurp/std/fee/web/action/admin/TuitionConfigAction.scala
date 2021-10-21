/*
 * Copyright (C) 2005, The OpenURP Software.
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
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.code.edu.model.EducationLevel
import org.openurp.base.edu.model.{Direction, Major}
import org.openurp.starter.edu.helper.ProjectSupport
import org.openurp.std.fee.model.{FeeType, TuitionConfig}

class TuitionConfigAction extends RestfulAction[TuitionConfig] with ProjectSupport {

  override def indexSetting(): Unit = {
    put("levels", getCodes(classOf[EducationLevel]))
    put("departments", getDeparts)
    put("majors", findInProject(classOf[Major]))
    super.indexSetting()
  }

  override def editSetting(entity: TuitionConfig): Unit = {
    put("feeTypes", getCodes(classOf[FeeType]))
    put("levels", getCodes(classOf[EducationLevel]))
    put("departments", getDeparts)
    put("majors", findInProject(classOf[Major]))
    put("directions", findInProject(classOf[Direction]))
    super.editSetting(entity)
  }

  /**
   * 打印
   *
   * @return
   */
  def printReview: View = {
    put("tuitionConfigs", entityDao.search(OqlBuilder.from(classOf[TuitionConfig], "feeDefault")))
    forward()
  }

}

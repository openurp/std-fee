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

import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.base.model.Project
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.fee.app.model.OnlinePaySetting
import org.openurp.code.std.model.FeeType

class OnlinePaySettingAction extends RestfulAction[OnlinePaySetting] with ProjectSupport {

  override protected def editSetting(entity: OnlinePaySetting): Unit = {
    given project: Project = getProject

    val feeTypes = getCodes(classOf[FeeType]).toBuffer
    put("feeTypes", feeTypes.subtractAll(entity.feeTypes))
    put("project",getProject)
  }

  override protected def saveAndRedirect(entity: OnlinePaySetting): View = {
    entity.feeTypes.clear()
    entity.feeTypes ++= entityDao.find(classOf[FeeType], getAll("selectTypeId").map(_.toString.toInt))
    super.saveAndRedirect(entity)
  }
}

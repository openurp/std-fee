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
package org.openurp.edu.fee.admin.web.action

import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.edu.web.ProjectSupport
import org.openurp.edu.fee.app.model.OnlinePaySetting
import org.openurp.edu.fee.model.FeeType

class OnlinePaySettingAction extends RestfulAction[OnlinePaySetting] with ProjectSupport {

  override protected def editSetting(entity: OnlinePaySetting): Unit = {
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

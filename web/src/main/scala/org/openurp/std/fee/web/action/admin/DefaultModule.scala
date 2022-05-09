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

import org.beangle.cdi.bind.BindModule
import org.openurp.std.fee.web.action.helper.StudentUtils
import org.openurp.std.fee.pay.impl.SufePayServiceImpl

class DefaultModule extends BindModule {
  override protected def binding(): Unit = {
    bind(classOf[BillAction], classOf[BillSearchAction])
    bind(classOf[OrderAction])

    bind(classOf[OnlinePaySettingAction])
    bind(classOf[FeeTypeConfigAction])

    bind(classOf[BillStatAction])

    bind(classOf[TuitionConfigAction])
    bind(classOf[SufePayServiceImpl])

    bind( classOf[StudentUtils] )
  }
}

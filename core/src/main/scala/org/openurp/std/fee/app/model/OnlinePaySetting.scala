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
package org.openurp.std.fee.app.model

import java.time.LocalDate

import org.beangle.commons.collection.Collections
import org.beangle.data.model.IntId
import org.beangle.data.model.pojo.DateRange
import org.openurp.edu.base.model.Semester
import org.openurp.std.fee.model.FeeType

import scala.collection.mutable

class OnlinePaySetting extends IntId with DateRange {

  var semester: Semester = _

  var notice: String = _

  var feeTypes: mutable.Buffer[FeeType] = Collections.newBuffer[FeeType]

  def suitable(feeType: FeeType): Boolean = {
    feeTypes.contains(feeType) && this.within(LocalDate.now)
  }
}

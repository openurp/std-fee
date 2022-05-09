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

package org.openurp.std.fee.web.data

import org.openurp.base.model.Department

class BillStat {

  var department: Department = _

  var payedCount: Long = _

  var payedValue: Double = _

  var payCount: Long = _

  def this(department: Department, payedCount: Long, payedValue: Double  , payCount: Long)= {
    this()
    this.department = department
    this.payedCount = payedCount
    this.payedValue = payedValue
    this.payCount = payCount
  }

  def getUnpayCount: Long = payCount - payedCount

  def getPayedRate: Double = payedCount * 1.0 / payCount

}

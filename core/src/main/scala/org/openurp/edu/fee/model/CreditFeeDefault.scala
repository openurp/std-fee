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
package org.openurp.edu.fee.model

import org.beangle.data.model.IntId
import org.openurp.code.edu.model.EducationLevel
import org.openurp.edu.base.code.model.CourseType

/**
 * 学分收费标准
 */
class CreditFeeDefault extends IntId {

	/** 学历层次（原：学生类别） */
	var level: EducationLevel = _

	/** 课程类别 */
	var courseType: CourseType = _

	/** 收费金额 */
	var value: Option[Float] = None

}

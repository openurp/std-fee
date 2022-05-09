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

package org.openurp.std.fee.web.action.helper

import org.beangle.commons.collection.Collections
import org.openurp.base.std.model.StudentState
import org.openurp.std.fee.model.TuitionConfig

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class TuitionConfigHelper(configs: collection.Seq[TuitionConfig]) {

  val feeType = configs.headOption.map(_.feeType).orNull

  configs.foreach { cfg =>
    val key = cfg.level.id.toString + "_" + (cfg.duration * 10).toInt + "_" + cfg.major.map(_.id.toString).getOrElse("*") + "_" +
      cfg.direction.map(_.id.toString).getOrElse("*") + "_" + cfg.department.map(_.id.toString).getOrElse("*")
    wildcards.getOrElseUpdate(key, new ArrayBuffer[TuitionConfig]) += cfg
  }
  private val wildcards = Collections.newMap[String, mutable.Buffer[TuitionConfig]]

  def find(ss: StudentState): Option[TuitionConfig] = {
    val directionId = ss.direction.map(_.id.toString).getOrElse("*")
    val prefix = ss.std.level.id.toString + "_" + (ss.std.duration * 10).toInt + "_"
    val first = prefix + ss.major.id + "_" + directionId + "_" + ss.department.id
    val ignoreDirection = prefix + ss.major.id + "_*_" + ss.department.id
    val ignoreDepart = prefix + ss.major.id + "_" + directionId + "_*"
    val ignoreDepartDirection = prefix + ss.major.id + "_*_*"
    val ignoreMajor = prefix + "*_*_*"
    val patterns = List(first, ignoreDirection, ignoreDepart, ignoreDepartDirection, ignoreMajor)
    patterns.find(wildcards.contains(_)) match {
      case Some(k) =>
        wildcards(k).find { m =>
          ss.grade.compareTo(m.fromGrade) >= 0 && ss.grade.compareTo(m.toGrade) <= 0
        }
      case None => None
    }
  }
}

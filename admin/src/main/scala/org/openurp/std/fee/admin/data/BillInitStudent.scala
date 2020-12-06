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
package org.openurp.std.fee.admin.data


import org.beangle.commons.collection.Collections
import org.openurp.base.edu.model.{Semester, Student, StudentState}
import org.openurp.std.fee.model.TuitionConfig

import scala.collection.{immutable, mutable}

class BillInitStudent {

  var semester: Semester = _

  var studentMap = Collections.newMap[Long, Student]

  var stateTuitionConfigMap = Collections.newMap[StudentState, TuitionConfig]

  var currentStateMap = Collections.newMap[Student, StudentState]

  def this(semester: Semester, students: immutable.Seq[Student],
           tuitionConfigMap: mutable.Map[StudentState, TuitionConfig],
           currentStateMap: mutable.Map[Student, StudentState]) = {
    this
    this.semester = semester
    //		val studentMap = Collections.newMap[Long, Student]
    students.foreach(student => {
      this.studentMap.put(student.id, student)
    })
    this.stateTuitionConfigMap = tuitionConfigMap
    this.currentStateMap = currentStateMap
  }


  def getStudents(studentIds: List[Long]): mutable.Seq[Student] = {
    if (null == studentIds) Collections.newBuffer(studentMap.values)
    val students = Collections.newBuffer[Student]
    studentIds.foreach(studentId => {
      students.addOne(studentMap.get(studentId).get)
    })
    students
  }

  def getTuitionConfig(student: Student): TuitionConfig = getTuitionConfig(getCurrentState(student))

  def getTuitionConfig(studentState: StudentState): TuitionConfig = stateTuitionConfigMap.get(studentState).orNull

  def getCurrentState(student: Student): StudentState = currentStateMap.get(student).orNull

  def getCurrentStateMap(studentIds: List[Long]): mutable.Map[Student, StudentState] = {
    val currentStateMap = Collections.newMap[Student, StudentState]
    val students = getStudents(studentIds)
    if (!students.isEmpty) {
      students.foreach(student => {
        val studentState = getCurrentState(student)
        if (null != studentState) currentStateMap.put(student, studentState)
      })
    }
    currentStateMap
  }
}

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

package org.openurp.std.fee.web.action.helper

import org.beangle.commons.collection.Collections
import org.openurp.base.edu.model.{Semester, Student, StudentState}

import java.time.LocalDate
import scala.collection.mutable

class StudentUtils {

  def getStudentState(student: Student, studentStateId: Long): StudentState = {
    if (null == student || studentStateId == 0) return null
    student.states.find(s => s.id.equals(studentStateId)).orNull
  }

  def getStudentStates(student: Student, semester: Semester): mutable.Buffer[StudentState] = {
    getStudentStates(student, semester.beginOn, semester.endOn)
  }

  def getStudentStates(student: Student, fromAt: LocalDate, toAt: LocalDate): mutable.Buffer[StudentState] = {
    val studentStates = Collections.newBuffer[StudentState]
    if (student != null && fromAt != null) {
      student.states.foreach(state => {
        if ((null == toAt || state.beginOn.isBefore(toAt)) && state.endOn.get.isAfter(fromAt)) studentStates.addOne(state) //endOn.get注意
      })
    }
    studentStates
  }

  def getStudents(states: mutable.Buffer[StudentState]): mutable.Buffer[Student] = {
    val students = Collections.newBuffer[Student]
    states.foreach(state => {
      students.addOne(state.std)
    })
    students
  }
}

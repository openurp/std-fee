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

import java.time.Instant

import org.beangle.commons.collection.{Collections, Order}
import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.security.Securities
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.code.edu.model.EducationLevel
import org.openurp.edu.base.model.{Semester, Student, StudentState}
import org.openurp.edu.base.web.ProjectSupport
import org.openurp.edu.fee.admin.data.BillInitStudent
import org.openurp.edu.fee.admin.utils.StudentUtils
import org.openurp.edu.fee.model.{Bill, FeeType, TuitionConfig}


class BillAction extends RestfulAction[Bill] with ProjectSupport {

  var studentUtils: StudentUtils = _
  var sessionInitStudentMap = Collections.newMap[String, BillInitStudent]

  override def indexSetting(): Unit = {
    put("feeTypes", getCodes(classOf[FeeType]))
    put("levels", getCodes(classOf[EducationLevel]))
    put("project",getProject)
    put("currentSemester", getCurrentSemester)
    super.indexSetting()
  }

  override protected def getQueryBuilder: OqlBuilder[Bill] = {
    val query = super.getQueryBuilder
    getBoolean("paid") foreach { payed =>
      if (payed) {
        query.where("bill.payed > 0")
      } else {
        query.where("bill.payed <= 0")
      }
    }
    getBoolean("student_inschool") foreach { inschool =>
      query.where("bill.std.state.inschool=:inschool", inschool)
    }
    query
  }


  def initIndex(): View = {
    put("semester", entityDao.get(classOf[Semester], intId("bill.semester")))
    put("departments", getDeparts)
    forward()
  }

  def getFirstSemesterInCurrentYear(semesterId: Int): Semester = {
    val builder = OqlBuilder.from(classOf[Semester], "semester")
    val hql = new StringBuilder()
    hql.append("exists (")
    hql.append("  from ").append(classOf[Semester].getName).append(" semester1")
    hql.append(" where to_char(semester1.beginOn, 'yyyy') = to_char(semester.beginOn, 'yyyy')")
    hql.append("   and semester1.id = :semesterId")
    hql.append(")")
    builder.where(hql.toString, semesterId)
    builder.orderBy(Order.parse("semester.beginOn"))
    val semesters = entityDao.search(builder)
    if (semesters.isEmpty) {
      null
    }
    else {
      semesters.head
    }
  }

  def toInitStudentList(): View = {
    val sessionId = request.getSession.getId
    sessionInitStudentMap.remove(sessionId)
    val semester = getFirstSemesterInCurrentYear(intId("bill.semester"))
    // 获取在指定学年学期期间在校的学生
    val builder1 = OqlBuilder.from(classOf[Student], "student")
    populateConditions(builder1)
    var hql = new StringBuilder()
    hql.append("exists (")
    hql.append("  from student.states studentState")
    hql.append(" where studentState.std = student")
    hql.append("   and studentState.inschool = true")
    hql.append("   and studentState.beginOn <= :endOn")
    hql.append("   and studentState.endOn >= :beginOn")
    hql.append(")")
    builder1.where(hql.toString, semester.endOn, semester.beginOn)
    var students = entityDao.search(builder1)

    // 获得上面所获学生所有历史应缴记录
    val builder2 = OqlBuilder.from(classOf[Bill].getName + " bill")
    hql = new StringBuilder()
    hql.append("exists (")
    hql.append("  from ").append(classOf[StudentState].getName).append(" studentState")
    hql.append(" where studentState.std = bill.std")
    hql.append("   and studentState.inschool = true")
    hql.append("   and studentState.beginOn <= :endOn")
    hql.append("   and studentState.endOn >= :beginOn")
    hql.append(")")
    builder2.where(hql.toString, semester.endOn, semester.beginOn)
    builder2.groupBy("bill.std.id")
    builder2.select("bill.std.id, sum(bill.amount)")
    val stats: Seq[Array[Any]] = entityDao.search(builder2)
    val sumFeeMap = Collections.newMap[Long, Long]
    stats.foreach(data => {
      sumFeeMap.put(data(0).asInstanceOf[Long], data(1).asInstanceOf[Long])
    })

    val builder3 = OqlBuilder.from(classOf[Bill], "bill")
    builder3.where(hql.toString, semester.endOn, semester.beginOn)
    builder3.where("bill.semester = :semester", semester)
    val feesBySemester = entityDao.search(builder3)
    val feeBySemesterMap = feesBySemester.map(e => (e.std, e)).toMap

    // 根据上面找出的学生学籍状态，找到对应匹配到的默认收费标准
    val builder4 = OqlBuilder.from(classOf[TuitionConfig], "tuitionConfig")
    // FIXME 2018-12-05 zhouqi 下面两个条件是暂时的，等到数据补缮后才去除
    builder4.where("tuitionConfig.fromGrade is not null")
    builder4.where("tuitionConfig.toGrade is not null")
    hql = new StringBuilder()
    hql.append("exists (")
    hql.append("  from ").append(classOf[StudentState].getName).append(" studentState")
    hql.append(" where studentState.std.level = tuitionConfig.level")
    hql.append("   and studentState.inschool = true")
    hql.append("   and studentState.beginOn <= :endOn")
    hql.append("   and studentState.endOn >= :beginOn")
    hql.append("   and studentState.grade between tuitionConfig.fromGrade and tuitionConfig.toGrade")
    hql.append("   and (tuitionConfig.department is null or studentState.department = tuitionConfig.department)")
    hql.append("   and (tuitionConfig.major is null or studentState.major = tuitionConfig.major)")
    hql.append(")")
    builder4.where(hql.toString, semester.endOn, semester.beginOn)
    val tuitionConfigs = entityDao.search(builder4)
    val tuitionConfigMap = Collections.newMap[String, TuitionConfig]
    tuitionConfigs.foreach(tuitionConfig => {
      var key = tuitionConfig.fromGrade + "_" + tuitionConfig.toGrade + "_" + tuitionConfig.level.id + "_"
      if (tuitionConfig.department.isEmpty) {
        key = key + "?" + "_"
      }
      else {
        key = key + tuitionConfig.department.get.id + "_"
      }
      if (tuitionConfig.major.isEmpty) {
        key = key + "?"
      }
      else {
        key = key + tuitionConfig.major.get.id
      }
      // 如果不存在，或后者的 id 比已存在的要大
      if (!tuitionConfigMap.contains(key) || tuitionConfig.id.compareTo(tuitionConfigMap.get(key).get.id) > 0) {
        tuitionConfigMap.put(key, tuitionConfig)
      }
    })

    val currentStateMap = Collections.newMap[Student, StudentState]
    val stateTuitionConfigMap = Collections.newMap[StudentState, TuitionConfig]
    students.foreach(student => { // 过滤指定学期已生成缴费记录的学生
      if (!feeBySemesterMap.contains(student)) {
        // 萃取指定学年学期期间的学生学籍状态
        var currentState = new StudentState()
        student.states.foreach(state => {
          if (state.beginOn.isBefore(semester.endOn) && state.endOn.get.isAfter(semester.beginOn)) {
            currentState = state
            currentStateMap.put(student, currentState)
          }
        })
        // 寻找可以被匹配到的收费标准配置
        var defaultKey = ""
        tuitionConfigMap.keySet.foreach(key => {
          val dataInKey = Strings.split(key, "_")
          if (currentState.grade.compareTo(dataInKey(0)) > 0 && currentState.grade.compareTo(dataInKey(1)) < 0) {
            if (student.level.id.toString.equals(dataInKey(2))
              && (dataInKey(3).equals("?") || currentState.department.id.toString.equals(dataInKey(3)))
              && (dataInKey(4).equals("?") || currentState.major.id.toString.equals(dataInKey(4)))) {
              defaultKey = key
            }
          }
        })
        // 如果没有找到，就将过滤该学生
        if (defaultKey != null) {
          var sum = 0
          if (sumFeeMap.contains(student.id)) {
            sum = sumFeeMap.get(student.id).get.toInt
          }
          tuitionConfigMap.get(defaultKey).foreach(tuitionConfig => {
            val defaultValue = tuitionConfig.amount
            if (sum.compareTo(defaultValue) >= 0) {
              currentStateMap.remove(student) // 去除已缴足的学生
            } else {
              stateTuitionConfigMap.put(currentState, tuitionConfig)
            }
          })
        }
      }
    }

    )
    students = currentStateMap.keySet.toSeq
    if (stateTuitionConfigMap.isEmpty) builder1.where("student is null")
    else {
      val beenFDStudents = studentUtils.getStudents(stateTuitionConfigMap.keySet.toBuffer)
      get("hasTuitionConfig").get match {
        case "0" => students = Collections.subtract(students, beenFDStudents)
        case "1" => students = beenFDStudents.toSeq
        case "" =>
      }
    }

    sessionInitStudentMap.put(sessionId, new BillInitStudent(semester, students, stateTuitionConfigMap, currentStateMap))
    put("students", students)
    put("tuitionConfigMap", tuitionConfigMap)
    put("currentStateMap", currentStateMap)
    put("stateTuitionConfigMap", stateTuitionConfigMap)
    forward()
  }


  def billInit(): View = {
    val sessionId = request.getSession.getId
    val sessionData = sessionInitStudentMap.get(sessionId).get
    if (null == sessionData) {
      addMessage("在初始化缴费中的学生数据已过期，可能已经在其它界面处理过了！")
      addMessage("温馨提示：请不要在多个浏览器、窗口或浏览器的标签页中打开同一个界面或模块，如果出现了请关闭所有浏览器后，重新进入系统。")
      forward("errors")
    }
    val students = sessionData.getStudents(longIds("student"))
    val toSaveBills = Collections.newBuffer[Bill]
    students.foreach(student => {
      val currentState = sessionData.getCurrentState(student)
      val tuitionConfig = sessionData.getTuitionConfig(currentState)
      if (tuitionConfig != null) {
        val bill = new Bill
        bill.std = student
        bill.semester = sessionData.semester
        bill.depart = currentState.department
        bill.feeType = tuitionConfig.feeType
        // 总额／学制
        bill.amount = tuitionConfig.amount / Math.ceil(student.duration).toInt
        bill.updatedBy = Securities.user + "(初始化)"
        bill.updatedAt = Instant.now()
        bill.createdAt = Instant.now()
        toSaveBills.addOne(bill)
      }
    })
    entityDao.saveOrUpdate(toSaveBills)
    sessionInitStudentMap.remove(sessionId)
    redirect("index", "初始化成功")
  }

  def beforeInitCheckAjax: View = {
    val sessionData = sessionInitStudentMap.get(request.getSession.getId).get
    if (null == sessionData) put("isOk", false)
    else {
      val studentIds = longIds("student")
      put("isOk", studentIds.isEmpty || !sessionData.getCurrentStateMap(studentIds).isEmpty)
    }
    forward()
  }

  def loadStdAjax: View = {
    val students = entityDao.search(OqlBuilder.from(classOf[Student], "s").where("s.project.id=:projectId and s.user.code = :code", getProject.id, get("code")))
    val std = if (students.isEmpty) null else students.head
    put("std", std)
    forward()
  }


  override protected def saveAndRedirect(bill: Bill): View = {
    bill.std.id = longId("bill.std")
    bill.updatedBy = Securities.user
    super.saveAndRedirect(bill)
  }
}

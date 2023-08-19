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

import org.beangle.commons.collection.Collections
import org.beangle.commons.collection.page.{Page, SinglePage}
import org.beangle.data.dao.OqlBuilder
import org.beangle.data.excel.schema.ExcelSchema
import org.beangle.data.transfer.importer.ImportSetting
import org.beangle.data.transfer.importer.listener.ForeignerListener
import org.beangle.security.Securities
import org.beangle.web.action.annotation.response
import org.beangle.web.action.view.{Stream, View}
import org.beangle.webmvc.support.action.{ExportSupport, ImportSupport, RestfulAction}
import org.openurp.base.model.{Project, Semester}
import org.openurp.base.std.code.FeeType
import org.openurp.base.std.model.{Student, StudentState}
import org.openurp.code.edu.model.EducationLevel
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.fee.config.TuitionConfig
import org.openurp.std.fee.model.Bill
import org.openurp.std.fee.web.action.helper.{BillImportListener, StudentUtils, TuitionConfigHelper}

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.time.Instant

class BillAction extends RestfulAction[Bill], ExportSupport[Bill], ImportSupport[Bill], ProjectSupport {

  var studentUtils: StudentUtils = _

  override def indexSetting(): Unit = {
    given project: Project = getProject

    put("feeTypes", getCodes(classOf[FeeType]))
    put("levels", getCodes(classOf[EducationLevel]))
    put("project", getProject)
    put("currentSemester", getSemester)
    super.indexSetting()
  }

  def initIndex(): View = {
    given project: Project = getProject

    put("semester", entityDao.get(classOf[Semester], getIntId("bill.semester")))
    put("departments", getDeparts)
    forward()
  }

  def toInitStudentList(): View = {
    val semester = getFirstSemesterInCurrentYear(getIntId("bill.semester"))
    val configHelper = getConfigHelper(semester)
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
    builder1.where("not exists(from " + classOf[Bill].getName +
      " b where b.std=student and b.semester=:semester and b.feeType=:tuitionFeeType)", semester, configHelper.feeType)
    builder1.limit(getPageLimit)
    var students = entityDao.search(builder1)
    val totalItems = students.asInstanceOf[Page[_]].totalItems

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

    val currentStateMap = Collections.newMap[Student, StudentState]
    val stateTuitionConfigMap = Collections.newMap[StudentState, TuitionConfig]
    students.foreach { student =>
      // 萃取指定学年学期期间的学生学籍状态
      var currentState = new StudentState()
      student.states.foreach(state => {
        if (state.beginOn.isBefore(semester.endOn) && state.endOn.isAfter(semester.beginOn)) {
          currentState = state
          currentStateMap.put(student, currentState)
        }
      })
      // 寻找可以被匹配到的收费标准配置
      configHelper.find(currentState) match {
        case Some(config) =>
          val sum = sumFeeMap.get(student.id).getOrElse(0L)
          if (sum >= config.amount) {
            currentStateMap.remove(student) // 去除已缴足的学生
          } else {
            stateTuitionConfigMap.put(currentState, config)
          }
        case None =>
      }
    }
    students = currentStateMap.keySet.toSeq
    if (stateTuitionConfigMap.isEmpty) builder1.where("student is null")
    else {
      val beenFDStudents = studentUtils.getStudents(stateTuitionConfigMap.keySet.toBuffer)
      get("hasTuitionConfig", "") match {
        case "0" => students = Collections.subtract(students, beenFDStudents)
        case "1" => students = beenFDStudents.toSeq
        case "" =>
      }
    }
    val limit = getPageLimit
    put("students", new SinglePage(limit.pageIndex, limit.pageSize, totalItems, students.sortBy(_.code)))
    put("currentStateMap", currentStateMap)
    put("stateTuitionConfigMap", stateTuitionConfigMap)
    forward()
  }

  def billInit(): View = {
    val students = entityDao.find(classOf[Student], getLongIds("student"))
    val semester = getFirstSemesterInCurrentYear(getIntId("bill.semester"))
    val configHelper = getConfigHelper(semester)
    val toSaveBills = Collections.newBuffer[Bill]
    students.foreach { student =>
      val ss = studentUtils.getStudentStates(student, semester).head
      configHelper.find(ss).foreach { config =>
        val bill = new Bill
        bill.std = student
        bill.semester = semester
        bill.depart = ss.department
        bill.feeType = config.feeType
        // 总额／学制
        bill.amount = config.amount / Math.ceil(student.duration).toInt
        bill.updatedBy = Securities.user + "(初始化)"
        bill.updatedAt = Instant.now()
        bill.createdAt = Instant.now()
        toSaveBills.addOne(bill)
      }
    }
    entityDao.saveOrUpdate(toSaveBills)
    redirect("toInitStudentList", "初始化成功")
  }

  def getFirstSemesterInCurrentYear(semesterId: Int): Semester = {
    val semester = entityDao.get(classOf[Semester], semesterId)
    val builder = OqlBuilder.from(classOf[Semester], "semester")
    builder.where("semester.schoolYear=:schoolYear", semester.schoolYear)
    builder.orderBy("semester.beginOn")
    entityDao.search(builder).head
  }

  private def getConfigHelper(semester: Semester): TuitionConfigHelper = {
    // 根据上面找出的学生学籍状态，找到对应匹配到的默认收费标准
    val builder4 = OqlBuilder.from(classOf[TuitionConfig], "tuitionConfig")
    val hql = new StringBuilder()
    hql.append("exists (")
    hql.append("  from ").append(classOf[StudentState].getName).append(" studentState")
    hql.append(" where studentState.std.level = tuitionConfig.level")
    hql.append("   and studentState.inschool = true")
    hql.append("   and studentState.beginOn <= :endOn")
    hql.append("   and studentState.endOn >= :beginOn")
    hql.append(")")
    builder4.where(hql.toString, semester.endOn, semester.beginOn)
    new TuitionConfigHelper(entityDao.search(builder4))
  }

  def loadStdAjax(): View = {
    val students = entityDao.search(OqlBuilder.from(classOf[Student], "s").where("s.project.id=:projectId and s.user.code = :code", getProject.id, get("code")))
    val std = if (students.isEmpty) null else students.head
    put("std", std)
    forward()
  }

  @response
  def downloadTemplate(): Any = {
    given project: Project = getProject

    val feeTypes = getCodes(classOf[FeeType]).map(_.name)
    val semesters = entityDao.search(OqlBuilder.from(classOf[Semester], "s").orderBy("s.code")).map(_.code)

    val schema = new ExcelSchema()
    val sheet = schema.createScheet("数据模板")
    sheet.title("收费信息模板")
    sheet.remark("特别说明：\n1、不可改变本表格的行列结构以及批注，否则将会导入失败！\n2、须按照规格说明的格式填写。\n3、可以多次导入，重复的信息会被新数据更新覆盖。\n4、保存的excel文件名称可以自定。")
    sheet.add("学号", "bill.std").length(15).required()
    sheet.add("学年学期", "bill.semester.code").ref(semesters).required()
    sheet.add("收费类型", "bill.feeType.name").ref(feeTypes).required()
    sheet.add("应缴(元)", "bill.amount").decimal().required()
    sheet.add("已缴(元)", "bill.payed").decimal()

    val code = schema.createScheet("数据字典")
    code.add("收费类型").data(feeTypes)
    code.add("学年学期").data(semesters)
    val os = new ByteArrayOutputStream()
    schema.generate(os)
    Stream(new ByteArrayInputStream(os.toByteArray), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "收费信息.xlsx")
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

  override protected def saveAndRedirect(bill: Bill): View = {
    bill.std.id = getLongId("bill.std")
    bill.updatedBy = Securities.user
    super.saveAndRedirect(bill)
  }

  protected override def configImport(setting: ImportSetting): Unit = {
    val fl = new ForeignerListener(entityDao)
    fl.addForeigerKey("name")
    setting.listeners = List(fl, new BillImportListener(getProject, entityDao))
  }
}

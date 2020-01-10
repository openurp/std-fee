package org.openurp.edu.fee.admin.web.action

import java.text.SimpleDateFormat

import org.beangle.commons.collection.{Collections, Order}
import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.edu.base.model.Semester
import org.openurp.edu.base.web.ProjectSupport
import org.openurp.edu.fee.admin.data.BillStat
import org.openurp.edu.fee.model.Bill

import scala.collection.mutable

class BillStatAction extends RestfulAction[Bill] with ProjectSupport {

	override def indexSetting(): Unit = {
		val semesters = entityDao.findBy(classOf[Semester], "calendar", getProject.calendars)
		var semesterMap = Collections.newMap[String, mutable.Buffer[Semester]]
		val format = new java.text.SimpleDateFormat("yyyy")
		semesters.foreach(semester => {
			val year = format.format(java.sql.Date.valueOf(semester.beginOn))
			if (!semesterMap.contains(year)) {
				semesterMap.put(year, Collections.newBuffer[Semester])
				semesterMap.apply(year).addOne(semester)
			}
		})
		put("semesterMap", semesterMap)
		put("semester",getCurrentSemester)
		super.indexSetting()
	}

	/**
	 * “学费收缴情况说明”统计
	 *
	 * @return
	 */
	def stat1: View = {
		val departmentMap = getDeparts.map(t => (t.id, t)).toMap
		val builder = OqlBuilder.from(classOf[Bill].getName + " bill")
		builder.where("bill.semester.id in (:semesterId)", intIds("semester"))
		builder.groupBy("bill.std.state.department.id")
		builder.select("bill.std.state.department.id,sum(case when bill.payed is not null and bill.payed > 0 then 1 else 0 end),sum(case when bill.payed is not null or bill.payed > 0 then bill.payed else 0 end),count(*)")
		val stats: Seq[Array[Any]] = entityDao.search(builder)
		val results = Collections.newBuffer[BillStat]
		stats.foreach(data => {
			results.addOne(new BillStat(departmentMap.apply(data(0).asInstanceOf[Int]), data(1).asInstanceOf[Long], data(2).asInstanceOf[Long], data(3).asInstanceOf[Long]))
		})
		put("results", results)
		forward()
	}

	def detail1: View = {
		val builder = OqlBuilder.from(classOf[Bill], "bill")
		builder.where("bill.std.project in (:project)", getProject)
		builder.where("bill.std.state.department in (:departs)", getDeparts)
		val spans = getProject.levels
		if (!spans.isEmpty) builder.where("bill.std.level in (:spans)", spans)
		builder.where("bill.std.state.department.id = (:departmentId)", intId("department"))
		builder.where("bill.semester.id in (:semesterIds)", intIds("semester"))
		builder.where("bill.payed is null or bill.payed = 0")
		var orderBy = get("orderBy").orNull
		get("orderBy") match {
			case Some(value) => orderBy += ",bill.id"
			case None => orderBy = "bill.id"
		}
		builder.orderBy(Order.parse(orderBy))
		put("bills", entityDao.search(builder))
		forward()
	}

}

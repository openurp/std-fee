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

import org.beangle.commons.collection.{Collections, Order}
import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.support.action.RestfulAction
import org.beangle.webmvc.view.View
import org.openurp.base.model.{Project, Semester}
import org.openurp.code.std.model.FeeType
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.fee.model.Bill
import org.openurp.std.fee.web.data.BillStat

class BillStatAction extends RestfulAction[Bill] with ProjectSupport {

  override def indexSetting(): Unit = {
    given project: Project = getProject

    put("feeTypes", getCodes(classOf[FeeType]))
    put("currentSemester", getSemester)
    put("project", getProject)
    super.indexSetting()
  }

  /**
   * “学费收缴情况说明”统计
   *
   * @return
   */
  override def search(): View = {
    given project: Project = getProject

    val departmentMap = getDeparts.map(t => (t.id, t)).toMap
    val builder = OqlBuilder.from(classOf[Bill].getName + " bill")
    builder.where("bill.semester.id =:semesterId", getIntId("semester"))
    builder.where("bill.feeType.id =:typeId", getIntId("feeType"))
    builder.groupBy("bill.std.state.department.id")
    builder.select("bill.std.state.department.id,sum(case when bill.payed is not null and bill.payed > 0 then 1 else 0 end)," +
      "sum(case when bill.payed is not null or bill.payed > 0 then bill.payed else 0 end)/100.0,count(*)")
    val stats: Seq[Array[Any]] = entityDao.search(builder)
    val results = Collections.newBuffer[BillStat]
    stats.foreach(data => {
      results.addOne(new BillStat(departmentMap.apply(data(0).asInstanceOf[Int]), data(1).asInstanceOf[Long],
        data(2).asInstanceOf[Number], data(3).asInstanceOf[Long]))
    })
    put("results", results)
    put("feeTypeId", getIntId("feeType"))
    put("semesterId", getIntId("semester"))
    forward()
  }

  def detail(): View = {
    given project: Project = getProject

    val builder = OqlBuilder.from(classOf[Bill], "bill")
    builder.where("bill.std.project in (:project)", project)
    builder.where("bill.std.state.department in (:departs)", getDeparts)
    builder.where("bill.feeType.id =:typeId", getIntId("feeType"))
    val spans = getProject.levels
    if (spans.nonEmpty) builder.where("bill.std.level in (:spans)", spans)
    builder.where("bill.std.state.department.id = (:departmentId)", getIntId("department"))
    builder.where("bill.semester.id =:semesterId", getIntId("semester"))
    builder.where("bill.payed is null or bill.payed = 0")
    var orderBy = get("orderBy").orNull
    get("orderBy") match {
      case Some(value) => orderBy = orderBy + ",bill.id"
      case None => orderBy = "bill.id"
    }
    builder.orderBy(Order.parse(orderBy))
    put("bills", entityDao.search(builder))
    put("semester", entityDao.get(classOf[Semester], getIntId("semester")))
    forward()
  }

}

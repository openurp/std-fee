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

import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.doc.transfer.importer.{ImportListener, ImportResult}
import org.beangle.security.Securities
import org.openurp.base.model.Project
import org.openurp.base.std.model.Student
import org.openurp.std.fee.model.{Bill, Order}

import java.time.Instant

class BillImportListener(project: Project, entityDao: EntityDao) extends ImportListener {
  override def onStart(tr: ImportResult): Unit = {}

  override def onFinish(tr: ImportResult): Unit = {}

  override def onItemStart(tr: ImportResult): Unit = {
    val data = transfer.curData
    //按照stdCode,feeTypeName,semesterCode作为业务主键
    for (stdCode <- data.get("bill.std"); feeTypeName <- data.get("bill.feeType.name");
         semesterCode <- data.get("bill.semester.code")) {
      val stdQuery = OqlBuilder.from(classOf[Student], "s")
      stdQuery.where("s.code=:code and s.project=:project", stdCode.toString, project)
      val stds = entityDao.search(stdQuery)
      if (stds.size != 1) {
        tr.addFailure("不存在的学号", stdCode)
      } else {
        data.put("bill.std", stds.head)

        val query = OqlBuilder.from(classOf[Bill], "t")
          .where("t.std=:std", stds.head)
          .where("t.feeType.name=:feeTypeName", feeTypeName.toString)
          .where("t.semester.code=:semesterCode", semesterCode.toString.trim())
        val bills = entityDao.search(query)

        if (bills.nonEmpty) {
          val orders = entityDao.findBy(classOf[Order], "bill", bills.head)
          //如果没有订单可以更新
          if orders.isEmpty then transfer.current = bills.head
        }
      }
    }
    data.get("bill.payed") foreach { payed =>
      data.put("bill.payed", payed.asInstanceOf[Number].floatValue() * 100)
    }
    data.put("bill.amount", data("bill.amount").asInstanceOf[Number].floatValue() * 100)
  }

  override def onItemFinish(tr: ImportResult): Unit = {
    val bill = transfer.current.asInstanceOf[Bill]
    bill.depart = bill.std.state.get.department
    bill.updatedAt = Instant.now
    bill.updatedBy = Securities.user
    if (!bill.persisted) {
      bill.createdAt = Instant.now
    }
    entityDao.saveOrUpdate(bill)
  }
}

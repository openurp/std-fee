package org.openurp.edu.fee.admin.data

import org.openurp.base.model.Department

class BillStat {

	var department: Department = _

	var payedCount: Long = _

	var payedValue: Long = _

	var payCount: Long = _

	def this(department: Department, payedCount: Long, payedValue: Long, payCount: Long) {
		this
		this.department = department
		this.payedCount = payedCount
		this.payedValue = payedValue
		this.payCount = payCount
	}


	def getUnpayCount: Long = payCount - payedCount

	def getPayedRate: Long = payedCount / payCount * 100

}

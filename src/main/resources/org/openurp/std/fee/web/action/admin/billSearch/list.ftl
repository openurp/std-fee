[#ftl]
[@b.head/]
  [@b.grid items=bills var="bill"]
    [@b.gridbar]
      bar.addItem("${b.text("action.export")}",action.exportData(
      "std.code:学号,std.name:姓名,std.state.grade:年级,std.level.name:学历层次," +
      "std.person.code:证件号码,"+
      "std.state.department.name:院系,std.state.major.name:专业,std.state.squad.name:班级," +
      "semester.code:学年学期,feeType.name:收费类型,amount:应缴,payed:实缴,payAt:缴费时间",null,'fileName=缴费信息'));
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="学号" property="std.code" width="10%"/]
      [@b.col title="姓名" property="std.name" width="9%"]${bill.std.name}[#if (Parameters['student_inschool']!'-')=='0']<sup>${bill.std.state.status.name}</sup>[/#if][/@]
      [@b.col title="年级" property="std.state.grade"  width="7%"/]
      [@b.col title="学历层次" property="std.level.name" width="7%"/]
      [@b.col title="院系" property="std.state.department.name"  width="9%" ]${bill.std.state.department.shortName!bill.std.state.department.name}[/@]
      [@b.col title="专业" property="std.state.major.name"/]
      [@b.col title="收费类型" property="feeType.name"  width="8%"/]
      [@b.col title="应缴" property="amount"  width="7%"]<span[#if bill.amount?default(0) lt 1] style="color: red"[/#if]>${((bill.amount/100.0)?string("0.00#"))!}</span>[/@]
      [@b.col title="实缴" property="payed"  width="7%"]<span[#if bill.payed?default(0) lt 1] style="color: red"[/#if]>${((bill.payed/100.0)?string("0.00#"))!}</span>[/@]
      [@b.col title="实缴时间" property="payAt"  width="12%"]${(bill.payAt?string("yy-MM-dd HH:mm"))!}[/@]
    [/@]
  [/@]
[@b.foot/]

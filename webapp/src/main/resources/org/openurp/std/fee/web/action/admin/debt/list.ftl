[#ftl]
[#macro display name max]
 [#if name?length > max]
 <span style="font-size:0.8em">${name}</span>
 [#else]
 ${name}
 [/#if]
[/#macro]
[@b.head/]
  [@b.grid items=debts var="debt"]
    [@b.gridbar]
      bar.addItem("${b.text("action.export")}",action.exportData(
      "std.code:学号,std.name:姓名,std.state.grade:年级,std.level.name:学历层次," +
      "std.person.code:证件号码,"+
      "std.state.department.name:院系,std.state.major.name:专业,std.state.squad.name:班级,std.state.status.name:学籍状态," +
      "feeType.name:收费类型,amount:欠费(分),updatedAt:统计时间",null,'fileName=欠费信息'));
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="学号" property="std.code" width="13%"/]
      [@b.col title="姓名" property="std.name" width="12%"]
        <div class="text-ellipsis" title="${debt.std.name}">${debt.std.name}</div>
      [/@]
      [@b.col title="年级" property="std.state.grade" width="8%"/]
      [@b.col title="院系" property="std.state.department.name" width="10%"]
        ${(debt.std.state.department.shortName)!debt.std.state.department.name}
      [/@]
      [@b.col title="专业" property="std.state.major.name"]
        [@display (debt.std.state.major.name)!'--'+(debt.std.state.direction.name)!'' 20/]
      [/@]
      [@b.col title="班级" property="std.state.squad.name" width="18%"]
        [@display (debt.std.state.squad.name)!'--' 15/]
      [/@]
      [@b.col title="欠费项目" width="10%" property="feeType.name"/]
      [@b.col title="欠费金额" width="9%" property="amount"]
          ${((debt.amount/100.0)?string("0.00#"))!}
      [/@]
      [@b.col title="统计时间" width="9%" property="updatedAt"]
         ${(debt.updatedAt?string('yy-MM-dd HH:mm'))!}
      [/@]
    [/@]
  [/@]
[@b.foot/]

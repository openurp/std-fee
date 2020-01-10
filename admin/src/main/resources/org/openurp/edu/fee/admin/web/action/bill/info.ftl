[#ftl/]
[@b.head/]
[@b.toolbar title="收费明细"]bar.addBack();[/@]
<table class="formTable" style="width:70%;margin:10px auto auto;">
  <tr><td align="center" colspan="4" class="index_view"><b>修改收费信息</b></td></tr>
    <tr>
    <td class="title" width="20%">学号：</td>
    <td class="brightStyle" width="30%">${bill.std.user.code}</td>
    <td class="title" width="20%">姓名：</td>
    <td class="brightStyle">${bill.std.user.code}</td>
  </tr>
  <tr>
    <td class="title">学生类别：</td>
    <td>${bill.std.stdType.name}</td>
    <td class="title">院系：</td>
    <td>${bill.std.state.department.name}</td>
  </tr>
  <tr>
    <td class="title">专业：</td>
    <td>${bill.std.state.major.name}</td>
    <td class="title">班级：</td>
    <td>${bill.std.state.squad.name}</td>
  </tr>
  <tr>
    <td class="title">学年度：</td>
    <td>${bill.semester.schoolYear}</td>
    <td class="title">学期：</td>
    <td>${bill.semester.name}</td>
  </tr>
  <tr>
    <td class="title">收费部门：</td>
    <td>${bill.depart.name}</td>
    <td class="title">收费类型：</td>
    <td>${bill.type.name}</td>
  </tr>
  <tr>
    <td class="title">应缴费用：</td>
    <td[#if bill.shouldPay?default(0) lt 0] style="color: red"[/#if]>${(bill.shouldPay?string("0.00#"))!}</td>
    <td class="title">缴费金额：</td>
    <td[#if bill.payed?default(0) lt 0] style="color: red"[/#if]>${(bill.payed?string("0.00#"))!}</td>
  </tr>
  <tr>
    <td class="title">发票号：</td>
    <td>${(bill.invoiceCode)!}</td>
    <td class="title">收费方式：</td>
    <td>${bill.mode.name}</td>
  </tr>
  <tr>
    <td class="title">币种：</td>
    <td>${bill.currencyCategory.name}</td>
    <td class="title">汇率：</td>
    <td>${(bill.rate?string("0.00"))!}</td>
  </tr>
  <tr>
    <td class="title">收费人：</td>
    <td>${bill.whoAdded}</td>
    <td class="title">缴费日期：</td>
    <td>${(bill.createdAt?string("yyyy-MM-dd"))!}</td>
  </tr>
  <tr>
    <td class="title">最后修改人：</td>
    <td>${bill.whoModified}</td>
    <td class="title">修改时间：</td>
    <td>${bill.updatedAt?string("yyyy-MM-dd")}</td>
  </tr>
  <tr>
    <td class="title">备注：</td>
    <td colspan="3">${(bill.remark?html)!}</td>
  </tr>
</table>
[@b.foot /]

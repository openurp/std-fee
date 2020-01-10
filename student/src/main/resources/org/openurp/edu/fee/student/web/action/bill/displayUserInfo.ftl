[@b.head/]
[@b.toolbar title="核对订单"]
  bar.addBack();
[/@]
 <div class="container">
  [@b.form name="userForm" theme="list" action="!saveUserInfo" title="核对基本信息、确认联系方式" ]
    [@b.field label="学号"]${std.user.code}[/@]
    [@b.field label="姓名"]${std.user.name}[/@]
    [@b.field label="身份证号"]${(std.person.code)!}[/@]
    [@b.field label="缴费类型"]${bill.feeType.name}[/@]
    [@b.field label="缴费金额"]${(bill.amount*1.0/100)?string("0.##")}元[/@]
    [@b.textfield name="mobile" label="手机" required="true" value=std.user.mobile/]
    [@b.formfoot]
       <input type="hidden" name="billId" value="${bill.id}"/>
       [@b.submit value="确认联系方式，开始缴费"/]
    [/@]
  [/@]
</div>
[@b.foot/]
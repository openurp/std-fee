[#ftl]
[@b.head/]
  [@b.toolbar title="收费明细配置"]
    bar.addBack();
  [/@]
  [@b.form name="billForm" action="!save" target="bills" theme="list"]
    [#assign elementSTYLE = "width: 200px"/]
    [#if (bill.id)?exists || (bill.std.id)?exists]
      [@b.field label="学号"]${bill.std.user.code}[/@]
    [#else]
      [@b.textfield label="学号" name="userCode" value=(bill.std.user.code)! required="true" maxlength="32" style=elementSTYLE comment="在左边输入学号后，点击页面空白处，即可获取该学生信息"/]
      <input type="hidden" name="bill.std.id" value=""/>
    [/#if]
    [@b.field label="姓名"]<span id="fd_stdName" style="display: inline-block;">${(bill.std.user.name)!}</span>[/@]
    [@b.field label="专业"]<span id="fd_major" style="display: inline-block;">${(bill.std.state.major.name)!}</span>[/@]
    [@b.field label="班级"]<span id="fd_squad" style="display: inline-block;">${(bill.std.state.squad.name)!}</span>[/@]
    [@b.field label="学历层次"]<span id="fd_level" style="display: inline-block;">${(bill.std.level.name)!}</span>[/@]
    [@b.field label="院系"]<span id="fd_department" style="display: inline-block;">${(bill.std.state.department.name)!}</span>[/@]
    [#if (bill.id)?exists]
      [@b.field label="学年学期"]<span style="display: inline-block;">${bill.semester.schoolYear}${bill.semester.name}</span>[/@]
      [@b.field label="收费部门"]<span style="display: inline-block;">${bill.depart.name}</span>[/@]
      [@b.field label="交费类型"]<span style="display: inline-block;">${bill.type.name}</span>[/@]
      [@b.field label="应缴费用"]<span style="display: inline-block;">${bill.shouldPay?string("0.00#")}</span>[/@]
      [@b.textfield label="本次缴费" name="bill.payed" value=(bill.payed?string("0.00#"))! required="true" maxlength="64" check="match('number')" style=elementSTYLE/]
    [#else]
      [@eams.semesterCalendar label="学年学期" name="bill.semester.id" required="true" value=(bill.semester)?default(semester) style=elementSTYLE/]
      [@b.select label="收费部门" name="bill.depart.id" items=departments?sort_by(["name"]) value=(bill.depart.id)! empty="..." required="true" style=elementSTYLE/]
      [@b.select label="交费类型" name="bill.type.id" items=feeTypes?sort_by(["name"]) value=(bill.type.id)! empty="..." required="true" style=elementSTYLE/]
      [@b.textfield label="应缴费用" name="bill.shouldPay" value=(bill.shouldPay?string("0.00#"))! required="true" maxlength="64" check="match('number')" style=elementSTYLE/]
      [@b.textfield label="本次缴费" name="bill.payed" value=(bill.payed?string("0.00#"))! maxlength="64" check="match('number')" style=elementSTYLE/]
    [/#if]
    [@b.textfield label="发票号" name="bill.invoiceCode" value=(bill.invoiceCode)! maxlength="32" style=elementSTYLE/]
    [#if (bill.id)?exists]
      [@b.datepicker label="实缴日期" name="bill.payedAt" value=(bill.payedAt)! required="true" style=elementSTYLE comment="（此处为本次实际缴费日期，即最终最后一次缴费时间）"/]
    [#else]
      [@b.datepicker label="实缴日期" name="bill.payedAt" value=(bill.payedAt)! style=elementSTYLE comment="（此处为本次实际缴费日期，即最终最后一次缴费时间）"/]
    [/#if]
    [@b.textarea label="备注" name="bill.remark" value=(bill.remark?html)! maxlength="200" rows="3" style=elementSTYLE/]
    [@b.formfoot]
      <input type="hidden" name="bill.id" value="${(bill.id)!}"/>
      <input type="hidden" name="_params" value="${Parameters["_params"]!}"/>
      [@b.submit value="提交"/]
    [/@]
  [/@]
  <script>
    $(function() {
      function init(form) {
        var formObj = $(form);
        var stdNameObj = formObj.find("#fd_stdName");
        var majorObj = formObj.find("#fd_major");
        var squadObj = formObj.find("#fd_squad");
        var levelObj = formObj.find("#fd_level");
        var departmentObj = formObj.find("#fd_department");

        formObj.find("[name=userCode]").blur(function() {
          stdNameObj.empty();
          majorObj.empty();
          squadObj.empty();
          levelObj.empty();
          departmentObj.empty();
          form["bill.std.id"].value = "";

          var thisObj = $(this);
          var code = thisObj.val().trim();
          if (code.length == 0) {
            alert("请输入一个有效的学号，谢谢！");
          } else {
            $.ajax({
              "type": "POST",
              "url": "${b.url("!loadStdAjax")}",
              "async": false,
              "dataType": "json",
              "data": {
                "code": code
              },
              "success": function(data) {
                if (data.id) {
                  stdNameObj.text(data.user.name);
                  majorObj.text(data.state.major.name);
                  squadObj.text(data.state.squad.name);
                  levelObj.text(data.level.name);
                  departmentObj.text(data.state.department.name);
                  form["bill.std.id"].value = data.id;
                } else {
                  alert("请输入一个有效的学号，谢谢！");
                  thisObj.val("");
                }
              }
            });
          }
        });
      }

      $(document).ready(function() {
        init(document.billForm);
      });
    });
  </script>
[@b.foot/]

[#ftl]
[@b.head/]
  [@b.toolbar title="收费明细配置"]
    bar.addBack();
  [/@]
  [@b.form name="billForm" theme="list" action=b.rest.save(bill) target="bills" ]
    [#assign elementSTYLE = "width: 200px"/]
    [@b.field label="学号"]${bill.std.user.code}[/@]
    <input type="hidden" name="bill.std.id" value="${bill.std.id}"/>
    [@b.field label="姓名"]<span id="fd_stdName" style="display: inline-block;">${(bill.std.user.name)!}</span>[/@]
    [@b.field label="专业"]<span id="fd_major" style="display: inline-block;">${(bill.std.state.major.name)!}</span>[/@]
    [@b.field label="班级"]<span id="fd_squad" style="display: inline-block;">${(bill.std.state.squad.name)!}</span>[/@]
    [@b.field label="学历层次"]<span id="fd_level" style="display: inline-block;">${(bill.std.level.name)!}</span>[/@]
    [@b.field label="院系"]<span id="fd_department" style="display: inline-block;">${(bill.std.state.department.name)!}</span>[/@]
    [@b.field label="学年学期"]<span style="display: inline-block;">${bill.semester.schoolYear}${bill.semester.name}</span>[/@]
    [@b.field label="收费部门"]<span style="display: inline-block;">${bill.depart.name}</span>[/@]
    [@b.field label="交费类型"]<span style="display: inline-block;">${bill.feeType.name}</span>[/@]
    [@b.field label="应缴费用"]<span style="display: inline-block;" >${bill.amount}</span>分[/@]
    [@b.textfield label="本次缴费" name="bill.payed" value=(bill.payed)! required="true" maxlength="64" check="match('number')" style=elementSTYLE  comment="分"/]
    [@b.datepicker label="实缴日期" name="bill.payAt" value=(bill.payedAt)! format="yyyy-MM-dd HH:mm" required="true" style=elementSTYLE comment="（此处为本次实际缴费日期，即最终最后一次缴费时间）"/]
    [@b.textarea label="备注" name="bill.remark" value=(bill.remark?html)! maxlength="200" rows="3" style=elementSTYLE/]
    [@b.formfoot]
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

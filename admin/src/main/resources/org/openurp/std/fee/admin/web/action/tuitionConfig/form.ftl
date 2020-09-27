[#ftl]
[@b.head/]
  [@b.toolbar title="收费明细配置<span style=\"color: blue\">（" + (tuitionConfig.id)?exists?string("修改", "新增") + "）</span>"]
    bar.addBack();
  [/@]
  [@b.form name="tuitionConfigForm" theme="list" action=b.rest.save(tuitionConfig) ]
    [#assign elementSTYLE = "width: 200px"/]
    [#assign s = "tuitionConfig_"/]
    [@b.textfield label="起始年级" name="tuitionConfig.fromGrade" value=(tuitionConfig.fromGrade)! required="true" check="match('yearMonth')" maxlength="6" style=elementSTYLE/]
    [@b.textfield label="截止年级" name="tuitionConfig.toGrade" value=(tuitionConfig.toGrade)! required="true" check="match('yearMonth')" maxlength="6" style=elementSTYLE comment="格式同上"/]
    [@b.select label="学历层次" name="tuitionConfig.level.id" items=levels?sort_by(["name"]) empty="..." value=(tuitionConfig.level.id)! required="true" style=elementSTYLE/]
    [@b.select label="院系所" name="tuitionConfig.department.id" items=departments?sort_by(["name"]) empty="..." value=(tuitionConfig.department.id)! style=elementSTYLE/]
    [@b.select label="专业" name="tuitionConfig.major.id" items=majors?sort_by(["name"]) empty="..." value=(tuitionConfig.major.id)! required="false" style=elementSTYLE /]
    [@b.select label="交费类型" name="tuitionConfig.feeType.id" items=feeTypes?sort_by(["name"]) value=(tuitionConfig.feeType.id)! empty="..." required="true" style=elementSTYLE/]
    [@b.textfield label="总额" name="tuitionConfig.amount" value=(tuitionConfig.amount)!0 required="true" maxlength="64" check="match('number')" style=elementSTYLE comment="单位为分"/]
    [@b.textarea label="备注" name="tuitionConfig.remark" value=(tuitionConfig.remark?html)! maxlength="200" rows="3" style=elementSTYLE/]
    [@b.formfoot]
      [@b.submit value="提交"/]
    [/@]
  [/@]
  <script>
    $(function() {
      function init(form) {
        var submitObj = $(form).find(":submit");
        eval("var prevProcess = function() {" + submitObj.attr("onclick") + "};")
        submitObj.removeAttr("onclick");
        submitObj.click(function() {
          if (form["tuitionConfig.major.id"].value.length) {
            prevProcess();
          } else {
            if (form.onsubmit() && confirm("是否一次性批量生成下拉框中所有专业？")) {
              prevProcess();
            } else {
              return false;
            }
          }
        });
      }
    });
  </script>
[@b.foot/]

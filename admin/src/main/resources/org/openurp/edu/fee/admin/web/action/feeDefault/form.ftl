[#ftl]
[@b.head/]
  [@b.toolbar title="收费明细配置<span style=\"color: blue\">（" + (feeDefault.id)?exists?string("修改", "新增") + "）</span>"]
    bar.addBack();
  [/@]
  [@b.form name="feeDefaultForm" theme="list" action=b.rest.save(feeDefault) ]
    [#assign elementSTYLE = "width: 200px"/]
    [#assign s = "feeDefault_"/]
    [@b.textfield label="起始年级" name="feeDefault.fromGrade" value=(feeDefault.fromGrade)! required="true" check="match('yearMonth')" maxlength="6" style=elementSTYLE comment="正确格式：2017-3；错误格式：2017-10"/]
    [@b.textfield label="截止年级" name="feeDefault.toGrade" value=(feeDefault.toGrade)! required="true" check="match('yearMonth')" maxlength="6" style=elementSTYLE comment="格式同上"/]
    [@b.select label="学历层次" name="feeDefault.level.id" items=levels?sort_by(["name"]) empty="..." value=(feeDefault.level.id)! required="true" style=elementSTYLE/]
    [@b.select label="院系所" name="feeDefault.department.id" items=departments?sort_by(["name"]) empty="..." value=(feeDefault.department.id)! style=elementSTYLE/]
    [#if (feeDefault.id)?exists]
    [@b.select label="专业" name="feeDefault.major.id" items=majors?sort_by(["name"]) empty="..." value=(feeDefault.major.id)! required="true" style=elementSTYLE comment="（仅在“新增”的状态下，才能一次性批量生成）"/]
    [#else]
    [@b.select label="专业" name="feeDefault.major.id" items=majors?sort_by(["name"]) empty="全部所有" style=elementSTYLE comment="（若选“全部所有”，则将此下拉框中所有专业一次性生成，除专业外其它配置条件相同，而又不交叉重复的明细配置）"/]
    [/#if]
    [@b.select label="交费类型" name="feeDefault.type.id" items=feeTypes?sort_by(["name"]) value=(feeDefault.type.id)! empty="..." required="true" style=elementSTYLE/]
    [@b.textfield label="总额" name="feeDefault.value" value=(feeDefault.value)!0 required="true" maxlength="64" check="match('number')" style=elementSTYLE/]
    [@b.textarea label="备注" name="feeDefault.remark" value=(feeDefault.remark?html)! maxlength="200" rows="3" style=elementSTYLE/]
    [@b.formfoot]
      <input type="hidden" name="_params" value="${Parameters["_params"]!}"/>
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
          if (form["feeDefault.major.id"].value.length) {
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

[#ftl]
[@b.head/]
  [@b.grid items=tuitionConfigs var="tuitionConfig"]
    [@b.gridbar]
      bg.form.addInput(action.getForm(), "_params", "${b.paramstring}");
      bar.addItem("${b.text("action.add")}", action.add());
      bar.addItem("${b.text("action.modify")}", action.edit());
      bar.addItem("${b.text("action.delete")}", action.remove());
      bar.addItem("打印预览", function() {
        bg.form.submit(action.getForm(), "${b.url("!printReview")}", "_blank");
      }, "print.png");
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="起始年级" property="fromGrade" width="8%"/]
      [@b.col title="截止年级" property="toGrade" width="8%"/]
      [@b.col title="学历层次" property="level.name" width="8%"/]
      [@b.col title="学制" property="duration" width="8%"/]
      [@b.col title="院系所" property="department.name" ]${(tuitionConfig.department.name)!"--"}[/@]
      [@b.col title="专业" property="major.name"]${(tuitionConfig.major.name)!"--"} ${(tuitionConfig.direction.name)!}[/@]
      [@b.col title="收费类别" property="feeType.name" width="10%"/]
      [@b.col title="总额" property="amount" width="10%"]${(tuitionConfig.amount/100.0)?string('#.00')}[/@]
      [@b.col title="备注" property="remark" width="8%"/]
    [/@]
  [/@]
[@b.foot/]

[#ftl]
[@b.head/]
  [@b.grid items=feeDefaults var="feeDefault"]
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
      [@b.col title="起始年级" property="fromGrade" width="10%"/]
      [@b.col title="截止年级" property="toGrade" width="10%"/]
      [@b.col title="学历层次" property="level.name" width="10%"/]
      [@b.col title="院系所" property="department.name" width="15%"]${(feeDefault.department.name)!"不限"}[/@]
      [@b.col title="专业" property="major.name" width="20%"]${(feeDefault.major.name)!"不限"}[/@]
      [@b.col title="收费类别" property="type.name" width="10%"/]
      [@b.col title="总额" property="value" width="10%"/]
      [@b.col title="备注" property="remark" width="10%"/]
    [/@]
  [/@]
[@b.foot/]

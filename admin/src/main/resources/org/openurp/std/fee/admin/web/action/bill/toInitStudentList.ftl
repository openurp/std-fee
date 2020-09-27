[#ftl]
[@b.head/]
  [@b.grid items=students var="student"]
    [@b.gridbar]
      bar.addItem("初始化", function() {
        var studentIds = bg.input.getCheckBoxValues("student.id");
        if (!studentIds || !studentIds.trim().length) {
          alert("请选择要初始化的学生名单");
          return;
        } else {
          if (!confirm("要初始化生成当前已找到收费标准的所选学生于当前学期的应缴记录吗？") || !toCheck(studentIds)) {
            return false;
          }
          bg.form.addInput(action.getForm(), "studentIds", studentIds);
        }
        bg.form.submit(action.getForm(), "${b.url("!billInit")}", "main");
      }, "new.png");

      function toCheck(studentIds) {
        var isOk = false;
        var dataMap = {};
        if (studentIds) {
          dataMap.studentIds = studentIds;
        }
        $.ajax({
          "type": "POST",
          "url": "${b.url("!beforeInitCheckAjax")}",
          "dataType": "json",
          "async": false,
          "data": dataMap,
          "success": function(data) {
            isOk = data.isOk;
          }
        });
        if (!isOk) {
          alert("当前所选的学生中没有一人找到对应的收费标准，或者当前页面因其它浏览器等已经被处理掉了。\n请关闭所有浏览器后重试试，谢谢！");
        }
        return isOk;
      }
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="学号" property="user.code" width="10%"/]
      [@b.col title="姓名" property="user.name" width="10%"/]
      [@b.col title="学制" property="duration" width="8%"/]
      [@b.col title="年级" property="state.grade" width="8%"/]
      [@b.col title="培养层次" property="level.name" width="10%"/]
      [@b.col title="院系" sortable="false" width="17%"][#assign currentStudentState = currentStateMap.get(student)/]${(currentStudentState.department.name)}[/@]
      [@b.col title="专业" sortable="false" width="17%"]${currentStudentState.major.name}[/@]
      [@b.col title="应缴" sortable="false" width="15%"]${(stateTuitionConfigMap.get(currentStudentState).amount / student.duration?ceiling)!"<span style=\"color: red\">收费标准未找到</span>"}[/@]
    [/@]
  [/@]
[@b.foot/]

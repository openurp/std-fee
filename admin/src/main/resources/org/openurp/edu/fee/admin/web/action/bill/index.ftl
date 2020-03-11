[#ftl]
[@b.head/]
  [@b.toolbar title="收费信息维护"]
    bar.addItem("初始化收费(名单)", function() {
      var form = document.billSearchForm;
      if (!form["bill.semester.id"].value.trim().length) {
        alert("请选择所要初始化的学年学期，谢谢！");
        return false;
      }
      bg.form.submit(form, "${b.url("!initIndex")}", "_blank");
    }, "action-new");
  [/@]
  <table class="indexpanel">
    <tr>
      <td class="index_view">
        [@b.form title="ui.searchForm" name="billSearchForm" action="!search" target="bills" theme="search"]
          <tr>
            <td class="search-item">
              [@edu_base.semester name="bill.semester.id" label="学年学期"  value=currentSemester required="false"/]
              <input type="hidden" name="orderBy" value="bill.payAt desc"/>
            <td>
          </tr>
          [@b.textfields names="bill.std.user.code;学号,bill.std.user.name;姓名"/]
          [@b.select label="学历层次" items=levels?sort_by("code") empty="..."  name="bill.std.level.id"/]
          [@b.select label="收费类型" items=feeTypes?sort_by("code") empty="..."  name="bill.feeType.id"/]
          [@b.select label="缴费状态" items={"1":"已缴","0":"未缴"} empty="..."  name="paid"/]
          [@b.select label="学籍状态" items={"1":"在校","0":"不在校"} empty="..."  name="student_inschool"/]
        [/@]
      </td>
      <td class="index_content">[@b.div id="bills"/]</td>
    </tr>
  </table>
  <script>
    $(function() {
      $(document).ready(function() {
        bg.form.submit(document.billSearchForm, "${b.url("!search")}", "bills");
      });
    });
  </script>
[@b.foot/]

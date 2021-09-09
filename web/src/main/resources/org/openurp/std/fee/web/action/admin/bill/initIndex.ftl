[#ftl]
[@b.head/]
  [@b.toolbar title = "<span style=\"color: blue\">" + semester.schoolYear + "学年</span>将初始化收费学生列表"/]
<div class="search-container">
    <div class="search-panel">
        [@b.form title="ui.searchForm" name="billInitSearchForm" action="!toInitStudentList" target="billInitStudents" theme="search"]
          <input type="hidden" name="bill.semester.id" value="${Parameters["bill.semester.id"]}"/>
          [@b.textfields names="student.user.code;学号,student.user.name;姓名,student.state.grade;年级"/]
          [@b.select label="院系" name="student.state.department.id" items=departments?sort_by("code") empty="..."/]
          [@b.select label="有无标准" name="hasTuitionConfig" items={ "1": "有", "0": "无" } empty="..."/]
        [/@]
      </div>
      <div class="search-list">
      [@b.div id="billInitStudents"/]
      </div>
    </div>
  <script>
    $(function() {
      $(document).ready(function() {
        bg.form.submit(document.billInitSearchForm, "${b.url("!toInitStudentList")}", "billInitStudents");
      });
    });
  </script>
[@b.foot/]

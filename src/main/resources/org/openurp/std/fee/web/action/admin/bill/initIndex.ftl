[#ftl]
[@b.head/]
  [@b.toolbar title = "<span style=\"color: blue\">" + semester.schoolYear + "学年</span>将初始化收费学生列表"/]
<div class="search-container">
    <div class="search-panel">
        [@b.form title="ui.searchForm" name="billInitSearchForm" action="!toInitStudentList" target="billInitStudents" theme="search"]
          <input type="hidden" name="bill.semester.id" value="${Parameters["bill.semester.id"]}"/>
          [@b.textfields names="student.code;学号,student.name;姓名,student.state.grade.code;年级"/]
          [@b.select label="院系" name="student.state.department.id" items=departments?sort_by("code") empty="..."/]
          [@b.select label="有无标准" name="hasTuitionConfig" items={ "1": "有", "0": "无" } empty="..."/]
        [/@]
      </div>
      <div class="search-list">
      [@b.div id="billInitStudents" href="!toInitStudentList?bill.semester.id="+Parameters["bill.semester.id"]/]
      </div>
    </div>
[@b.foot/]

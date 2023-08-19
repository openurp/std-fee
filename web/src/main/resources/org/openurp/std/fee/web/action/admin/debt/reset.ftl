[@b.head/]

  [@b.toolbar title="清空导入"]
    bar.addBack();
  [/@]

  [@b.form name="unpaidForm" action="!batchReset" theme="list"]
     [@b.field label="学年学期"]${semester.schoolYear} ${semester.name}[/@]
     [@b.textarea name="std_codes" label="学号" cols="80" rows="10" maxlength="30000" comment="多个学号用回车或者半角逗号进行分隔"/]
     [@b.formfoot]
       <input type="hidden" name="debt.semester.id" value="${semester.id}"/>
       [@b.submit value="action.submit"/]
     [/@]
  [/@]
[@b.foot/]

[#ftl]
[@b.head/]
  [#include "nav.ftl"/]
  <table class="indexpanel">
    <tr>
      <td class="index_view" >
        [@b.form name="feeDefaultearchForm" action="!search" target="feeDefaults" title="ui.searchForm" theme="search"]
          [@b.select label="学历层次" items=levels?sort_by("code") empty="..."  name="feeDefault.level.id"/]
          [@b.select label="院系" items=departments?sort_by("code") empty="..."  name="feeDefault.department.id"/]
          [@b.select label="专业" items=majors?sort_by("code") empty="..."  name="feeDefault.major.id"/]
        [/@]
      </td>
      <td class="index_content">[@b.div id="feeDefaults" href="!search?orderBy=feeDefault.id"/]
      </td>
    </tr>
  </table>
[@b.foot/]

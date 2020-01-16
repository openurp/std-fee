[#ftl]
[@b.head/]
  [#include "../feeDefault/nav.ftl"/]
  <table class="indexpanel">
    <tr>
      <td class="index_view">
        [@b.form title="ui.searchForm" name="creditFeeDetaultSearchForm" action="!search" target="creditFeeDefaults" theme="search"]
          [@b.select label="学历层次" items=levels?sort_by("code") empty="..."  name="creditFeeDefault.level.id"/]
        [/@]
      </td>
      <td class="index_content">[@b.div id="creditFeeDefaults" href="!search?orderBy=creditFeeDefault.id"/]
    </tr>
  </table>
[@b.foot/]

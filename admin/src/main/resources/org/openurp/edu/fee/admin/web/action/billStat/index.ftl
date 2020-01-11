[#ftl]
[@b.head/]
[@b.toolbar title="收费统计"/]
<table class="indexpanel">
  <tr>
    <td class="index_view">
      [@b.form title="ui.searchForm" name="feeStatForm" action="!search" target="feeStat" theme="search"]
        <tr>
          <td class="search-item">
            [@edu_base.semester name="semester.id" label="学年学期"  value=currentSemester required="true"/]
          <td>
        </tr>
        [@b.select label="收费类型" items=feeTypes?sort_by("code") value=feeTypes[0]  name="feeType.id"/]
      [/@]
    </td>
    <td class="index_content">[@b.div id="feeStat"/]</td>
  </tr>
</table>
<script>
  $(function() {
    $(document).ready(function() {
      bg.form.submit(document.feeStatForm, "${b.url("!search")}", "feeStat");
    });
  });
</script>
[@b.foot/]

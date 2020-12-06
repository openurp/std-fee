[#ftl]
[@b.head/]
[@b.toolbar title="收费统计"/]
<div class="search-container">
    <div class="search-panel">
      [@b.form title="ui.searchForm" name="feeStatForm" action="!search" target="feeStat" theme="search"]
        [@urp_base.semester name="semester.id" label="学年学期"  value=currentSemester required="true"/]
        [@b.select label="收费类型" items=feeTypes?sort_by("code") value=feeTypes[0]  name="feeType.id"/]
      [/@]
      </div>
      <div class="search-list">
    [@b.div id="feeStat"/]
      </div>
    </div>
<script>
  $(function() {
    $(document).ready(function() {
      bg.form.submit(document.feeStatForm, "${b.url("!search")}", "feeStat");
    });
  });
</script>
[@b.foot/]

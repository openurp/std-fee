[#ftl]
[@b.head/]
[@b.toolbar title="学费配置"]
  bar.addBack();
[/@]
  <table class="indexpanel">
    <tr>
      <td class="index_view" >
        [@b.form name="tuitionConfigearchForm" action="!search" target="tuitionConfigs" title="ui.searchForm" theme="search"]
          [@b.select label="学历层次" items=levels?sort_by("code") empty="..."  name="tuitionConfig.level.id"/]
          [@b.select label="院系" items=departments?sort_by("code") empty="..."  name="tuitionConfig.department.id"/]
          [@b.select label="专业" items=majors?sort_by("code") empty="..."  name="tuitionConfig.major.id"/]
        [/@]
      </div>
      <div class="search-list">
      [@b.div id="tuitionConfigs" href="!search?orderBy=tuitionConfig.id"/]
      </div>
    </div>
[@b.foot/]

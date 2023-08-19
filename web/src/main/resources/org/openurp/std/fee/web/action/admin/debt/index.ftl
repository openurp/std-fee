[#ftl]
[@b.head/]
[@b.toolbar title="欠费名单"/]
   <div class="search-container">
     <div class="search-panel">
      [@b.form name="searchForm" action="!search" title="ui.searchForm" target="listFrame" theme="search"]
       [#include "searchForm.ftl"/]
       <input name="orderBy" type="hidden" value="debt.std.code"/>
      [/@]
     </div>
     <div class="search-list">
     [@b.div id="listFrame"/]
     </div>
  </div>
 <script>
  var form = document.searchForm;
  function search(pageNo,pageSize,orderBy){
    form.target="listFrame";
    form.action="${b.url('!search')}";
    bg.form.submit(form)
  }
  search();
 </script>
[@b.foot/]

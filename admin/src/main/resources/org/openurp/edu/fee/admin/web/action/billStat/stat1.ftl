[#ftl]
[@b.head/]
  <table class="gridtable">
    <thead class="gridhead">
      <tr>
        <th>教学站</th>
        <th>已缴费人数</th>
        <th>已缴费金额</th>
        <th>应缴费人数</th>
        <th>欠费人数</th>
        <th>缴费率</th>
      </tr>
    </thead>
    <tbody>[#assign sumPayedCount = 0/][#assign sumPayedValue = 0/][#assign sumPayCount = 0/]
      [#list results?sort_by(["department", "code"]) as result]
      <tr class="${(0 == result_index % 2)?string("griddata-even", "griddata-odd")}">
        <td>${result.department.name}</td>
        <td>${result.payedCount}</td>[#assign sumPayedCount = sumPayedCount + result.payedCount/]
        <td>${result.payedValue}</td>[#assign sumPayedValue = sumPayedValue + result.payedValue/]
        <td>${result.payCount}</td>[#assign sumPayCount = sumPayCount + result.payCount/]
        <td>[#if result.unpayCount gt 0]<a href="javascript:void(0)" name="unpay" data="${result.department.id}">${result.unpayCount}</a>[#else]${result.unpayCount}[/#if]</td>
        <td>${result.payedRate?string("0.##")}%</td>
      </tr>
      [/#list]
      <tr class="gridhead">
        <th>合计</td>
        <th>${sumPayedCount}</th>
        <th>${sumPayedValue}</th>
        <th>${sumPayCount}</th>
        <th>${sumPayCount - sumPayedCount}</th>
        <th>[#if sumPayCount != 0 ]${(sumPayedCount / sumPayCount * 100)?string("0.##")}%[/#if]</th>
      </tr>
    </tbody>
  </table>
  [@b.form name="feeStat1Form" action="!detail1" target="_blank"]
    <input type="hidden" name="semesterIds" value="${Parameters["semesterIds"]}"/>
  [/@]
  <script>
    $(function() {

      function init(unpayObj, form) {
        unpayObj.click(function() {
          bg.form.addInput(form, "departmentId", $(this).attr("data"));
          bg.form.submit(form);
        });
      }

      $(document).ready(function() {
        init($("a[name=unpay]"), document.feeStat1Form);
      });
    });
  </script>
[@b.foot/]

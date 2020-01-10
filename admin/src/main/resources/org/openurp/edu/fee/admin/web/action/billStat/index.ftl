[#ftl]
[@b.head/]
  [@b.toolbar title="收费统计"/]
  <div style="background: url('${base}/static/images/semesterBarBg.png') repeat-x scroll 50% 50% #DEEDF7;border: 1px solid #AED0EA;color: #222222;font-weight: bold;height:28px; padding-top: 3px">
    [@b.form name="feeStatForm" action="!stat1" target="feeStatDiv" theme="html"]
      <table>
        <tr>
          <td>学年度：</td>
          <td>
            <select name="semesterIds" style="width: 100px">
              [#list semesterMap?keys?sort as year]
              <option value="[#list semesterMap[year] as semesterItem]${semesterItem.id}[#if semesterItem_has_next],[/#if][/#list]"[#if semester.beginOn?string("yyyy") == year] selected[/#if]>${year}</option>
              [/#list]
            </select>
          </td>
          <td style="padding-left: 3px">[@b.submit value="查询" style="height: 20px; padding-top: 0px; padding-bottom: 0px"/]</td>
        </tr>
      </table>
    [/@]
  </div>
  [@b.div id="feeStatDiv"/]
  <script>
    $(function() {
      $(document).ready(function() {
        bg.form.submit(document.feeStatForm, "${b.url("!stat1")}", "feeStatDiv");
      });
    });
  </script>
[@b.foot/]

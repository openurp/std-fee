[#ftl]
[@b.head/]
  [@b.toolbar title="支付信息"]
     bar.addItem("支付开关","onlinePaySetting()");
     bar.addItem("支付配置","feeTypeConfig()");
     function onlinePaySetting(){
       bg.form.submit(document.configForm,"${b.url("online-pay-setting!search")}");
     }
     function feeTypeConfig(){
       bg.form.submit(document.configForm,"${b.url("fee-type-config!search")}");
     }
  [/@]
  <table class="indexpanel">
    <tr>
      <td class="index_view">
        [@b.form title="ui.searchForm" name="orderSearchForm" action="!search" target="orders" theme="search"]
          [@b.textfields names="o.std.user.code;学号,o.std.user.name;姓名"/]
          [@b.select label="学历层次" items=levels?sort_by("code") empty="..."  name="o.std.level.id"/]
          [@b.select label="收费类型" items=feeTypes?sort_by("code") empty="..."  name="o.bill.feeType.id"/]
          [@b.select label="缴费状态" items={"1":"已缴","0":"未缴"} empty="..."  name="o.paid"/]
        [/@]
      </td>
      <td class="index_content">[@b.div id="orders"/]</td>
    </tr>
  </table>
  <script>
    $(function() {
      $(document).ready(function() {
        bg.form.submit(document.orderSearchForm, "${b.url("!search?orderBy=o.std.user.code")}", "orders");
      });
    });
  </script>
  [@b.form name="configForm" action="!index"/]
[@b.foot/]

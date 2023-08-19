[@b.head/]

[@b.card class="card-outline"]
 [@b.card_header title="欠费信息"/]
   [@b.card_body]
      <div style="border:1px solid">
      [#if debts?size>0]
      [@b.grid items=debts var="debt" sortable="false"]
        [@b.row]
          [@b.col title="收费类型" property="feeType.name"/]
          [@b.col title="欠费金额" property="amount"]${(debt.amount*1.0/100)?string("0.##")}[/@]
          [@b.col title="统计时间" property="updatedAt"]${((debt.updatedAt)?string("yyyy-MM-dd HH:mm"))!}[/@]
        [/@]
      [/@]
      [#else]
        尚未找到您的欠费信息。
      [/#if]
    </div>
 [/@]
[/@]

[@b.foot/]

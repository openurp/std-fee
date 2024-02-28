[@b.head/]
[#function suitable bill]
    [#list settings as s]
      [#if s.semester == bill.semester && s.suitable(bill.feeType)][#return true/][/#if]
    [/#list]
    [#return false/]
[/#function]

[@b.card class="card-outline"]
 [@b.card_header title="历年费用信息"/]
   [@b.card_body]
      <div style="border:1px solid">
      [@b.grid items=bills?sort_by(["semester","code"])?reverse var="bill" sortable="false"]
        [@b.row]
          [@b.col title="学年学期" property="semester.id"]${bill.semester.schoolYear} ${bill.semester.name}[/@]
          [@b.col title="收费类型" property="feeType.name"/]
          [@b.col title="应缴费用" property="amount"]${(bill.amount*1.0/100)?string("0.##")}[/@]
          [@b.col title="实缴费用" property="payed"]${(bill.payed*1.0/100)?string("0.##")}[/@]
          [@b.col title="缴费时间" property="payAt"]${((bill.payAt)?string("yyyy-MM-dd HH:mm"))!'--'}[/@]
          [@b.col title="操作"]
            [#if bill.payed ==0 && suitable(bill)][@b.a href="!displayUserInfo?billId="+bill.id]付费[/@][/#if]
            [#if orders.get(bill.id)?? && orders.get(bill.id).paid]
              [@b.a target="_blank" href="!displayInvoice?id="+orders.get(bill.id).id]申请开票[/@]
            [/#if]
          [/@]
        [/@]
      [/@]
    </div>
 [/@]
[/@]

  [#if settings?size>0]
    [#assign setting = settings?first/]
<div class="card">
  <div class="card-header">
  ${setting.semester.schoolYear}学年${setting.semester.name}学期 [#list setting.feeTypes as ft]${ft.name}[#if ft_has_next]、[/#if][/#list] 缴费时间：${setting.beginOn?string('yyyy-MM-dd')}~${setting.endOn?string('yyyy-MM-dd')}
  </div>
  <div class="card-body">
      ${setting.notice}
  </div>
</div>
  [#else]
     <div>尚未到在线缴费时间</div>
  [/#if]
[@b.foot/]

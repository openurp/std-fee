[@b.head/]
<div class="container">
[#if error??]
  开票平台提示：${error}
  [@b.a href="!displayInvoice?id="+order.id class="btn btn-primary btn-sm"]<i class="fas fa-redo"></i>刷新[/@]
[#else]
  <img src="${invoicePath}" style="width:600px"/>
  [@b.a href="!invoice?id="+order.id class="btn btn-primary btn-sm" target="_blank"]<i class="fas fa-download"></i>下载[/@]
[/#if]
</div>
[@b.foot/]
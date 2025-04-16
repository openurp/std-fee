[@b.head/]
[@b.toolbar title="扫描支付"]
  bar.addBack();
[/@]
 <div class="container">
  [@b.form name="userForm" theme="list" action="!check?id="+bill.id title="扫描二维码支付" ]
    [@b.field label="学号"]${std.code}[/@]
    [@b.field label="姓名"]${std.name}[/@]
    [@b.field label="身份证号"]${(std.person.code)!}[/@]
    [@b.field label="缴费类型"]${bill.feeType.name}[/@]
    [@b.field label="缴费金额"]${(bill.amount*1.0/100)?string("0.##")}元[/@]
    [@b.field label="手机"]${(user.mobile)!}[/@]
    [@b.field label="订单号"]${(order.code)!}[/@]
    [@b.field label="支付状态" id="order_status"]${order.status}[/@]
    [#if !order.paid]
      [@b.field label="支付二维码"]
       <div id="qrcode"></div>
      [/@]
    [#else]
      [@b.field label="支付时间"]${order.payAt?string("yyyy-MM-dd HH:mm:ss")}[/@]
    [/#if]
    [@b.formfoot]
       [#if !order.paid]
         [@b.submit value="我已经支付，获取支付状态"/]
       [#else]
         [@b.submit value="返回" action="!index"/]
       [/#if]
    [/@]
  [/@]
  [#if !order.paid]
  <script>
   function generateQr(){
       new QRCode(document.getElementById("qrcode"), "${order.payUrl}");
   };
   var qrcode_url='${b.static_url("qrcodejs","qrcode.js")}'
   bg.require(qrcode_url,generateQr)
  </script>
  [/#if]
</div>
[@b.foot/]

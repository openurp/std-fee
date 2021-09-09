[@b.head/]
[@b.toolbar title="支付详情"]
  bar.addBack();
[/@]
  [@b.form name="userForm" theme="list" action="!info?id="+o.id title="" ]
    [@b.field label="学号"]${o.std.user.code}[/@]
    [@b.field label="姓名"]${o.std.user.name}[/@]
    [@b.field label="身份证号"]${(o.std.person.code)!}[/@]
    [@b.field label="缴费类型"]${o.bill.feeType.name}[/@]
    [@b.field label="缴费金额"]${(o.amount*1.0/100)?string("0.##")}元[/@]
    [@b.field label="手机"]${(o.std.user.mobile)!'--'}[/@]
    [@b.field label="订单号"]${(o.code)!}[/@]
    [@b.field label="支付状态" id="order_status"]${o.status}[/@]
    [@b.field label="支付时间"]${(o.payAt?string("yyyy-MM-dd HH:mm:ss"))!'--'}[/@]
    [@b.field label="支付地址"]${o.payUrl?html}[/@]
    [@b.field label="支付二维码"]
       <div id="qrcode"></div>
    [/@]
    [#if o.paid]
    [@b.field label="发票"]
      [@b.div href="!displayInvoice?id="+o.id/]
    [/@]
    [/#if]

  [/@]
  <script>
   function generateQr(){
       new QRCode(document.getElementById("qrcode"), "${o.payUrl}");
   };
   var qrcode_url='${b.static_url("qrcodejs","qrcode.js")}'
   bg.require(qrcode_url,generateQr)
  </script>
[@b.foot/]

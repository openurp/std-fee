[#ftl]
[@b.head/]
  [@b.grid items=oes var="o"]
    [@b.gridbar]
      bar.addItem("${b.text("action.info")}", action.info());
      bar.addItem("核对",action.multi("check","确定核对订单?"));
      bar.addItem("导出",action.exportData("code:订单号,std.code:学号,std.name:姓名,bill.feeType.name:收费类型,createdAt:创建时间,payAt:支付时间,channel:支付渠道,amount:应缴",null,'fileName=订单信息'));
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="学号" property="std.code" width="10%"/]
      [@b.col title="姓名" property="std.name" width="8%"/]
      [@b.col title="年级" property="std.state.grade"  width="7%"/]
      [@b.col title="学历层次" property="std.level.name" width="7%"/]
      [@b.col title="院系" property="std.state.department.name"  width="9%" ]${o.std.state.department.shortName!o.std.state.department.name}[/@]
      [@b.col title="订单号" property="code"][@b.a href="!info?id="+o.id]<span title="${o.status}" style="font-size:0.8em">${o.code}</span>[/@][/@]
      [@b.col title="收费类型" property="bill.feeType.name"  width="8%"/]
      [@b.col title="应缴" property="amount"  width="7%"]<span[#if o.amount?default(0) lt 1] style="color: red"[/#if]>${((o.amount/100.0)?string("0.00#"))!}</span>[/@]
      [@b.col title="实缴时间" property="payAt" width="12%"]<span title="${o.channel!}">${(o.payAt?string("yy-MM-dd HH:mm"))!}<span>[/@]
      [@b.col title="支付渠道" property="channel" width="9%"/]
    [/@]
  [/@]
[@b.foot/]

[#ftl]
[@b.head/]
  [@b.grid items=oes var="o"]
    [@b.gridbar]
      bar.addItem("${b.text("action.info")}", action.info());
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="学号" property="std.user.code" width="10%"/]
      [@b.col title="姓名" property="std.user.name" width="8%"/]
      [@b.col title="年级" property="std.state.grade"  width="7%"/]
      [@b.col title="学历层次" property="std.level.name" width="7%"/]
      [@b.col title="院系" property="std.state.department.name"  width="9%" ]${o.std.state.department.shortName!o.std.state.department.name}[/@]
      [@b.col title="订单号" property="code"  width="18%" ][@b.a href="!info?id="+o.id]<span title="${o.status}" style="font-size:0.8em">${o.code}</span>[/@][/@]
      [@b.col title="收费类型" property="bill.feeType.name"  width="8%"/]
      [@b.col title="应缴" property="amount"  width="7%"]<span[#if o.amount?default(0) lt 1] style="color: red"[/#if]>${((o.amount/100.0)?string("0.00#"))!}</span>[/@]
      [@b.col title="实缴时间" property="payAt" width="12%"]<span title="${o.channel!}">${(o.payAt?string("yy-MM-dd HH:mm"))!}<span>[/@]
      [@b.col title="支付渠道" property="channel" width="9%"/]
    [/@]
  [/@]
[@b.foot/]

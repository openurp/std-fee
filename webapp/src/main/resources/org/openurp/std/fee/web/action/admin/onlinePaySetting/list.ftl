[#ftl]
[@b.head/]
  [@b.toolbar title="支付开关"]
    bar.addBack();
  [/@]
  [@b.grid items=onlinePaySettings var="onlinePaySetting"]
    [@b.gridbar]
      bar.addItem("${b.text("action.add")}", action.add());
      bar.addItem("${b.text("action.edit")}", action.edit());
      bar.addItem("${b.text("action.delete")}", action.remove());
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="学年学期" property="semester.id"  width="35%"]${onlinePaySetting.semester.schoolYear}学年${onlinePaySetting.semester.name}学期[/@]
      [@b.col title="缴费日期" width="40%"]${onlinePaySetting.beginOn}~${onlinePaySetting.endOn}[/@]
      [@b.col title="缴费类型" width="20%"][#list onlinePaySetting.feeTypes as ft]${ft.name}[#if ft_has_next],[/#if][/#list][/@]
    [/@]
  [/@]
[@b.foot/]

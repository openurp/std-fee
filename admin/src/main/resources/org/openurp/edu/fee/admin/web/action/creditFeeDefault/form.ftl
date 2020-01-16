[#ftl]
[@b.head/]
  [@b.toolbar title="收费明细配置"]
    bar.addBack();
  [/@]
  [@b.form name="creditFeeDefaultForm" theme="list" action=b.rest.save(creditFeeDefault) ]
    [#assign elementSTYLE = "width: 200px"/]
    [#assign s = "creditFeeDefault_"/]
    [@b.select id=s + "span" label="学历层次" name="creditFeeDefault.level.id" items=levels empty="..." value=(creditFeeDefault.level.id)! required="true" style=elementSTYLE/]
    [@b.select label="课程类别" name="creditFeeDefault.courseType.id" items=courseTypes?sort_by(["name"]) value=(creditFeeDefault.courseType.id)! empty="..." style=elementSTYLE/]
    [@b.validity]
      $("[name='creditFeeDefault.level.id']", document.creditFeeDefaultForm).require().assert(function() {
        var isOk = false;

        $.ajax({
          "type": "POST",
          "url": "${b.url("!checkAjax")}",
          "async": false,
          "dataType": "json",
          "data": {
            "id": [#if creditFeeDefault.id ??]${creditFeeDefault.id}[#else]""[/#if],
            "spanId": document.creditFeeDefaultForm["creditFeeDefault.level.id"].value,
            "typeId": document.creditFeeDefaultForm["creditFeeDefault.courseType.id"].value
          },
          "success": function(data) {
            isOk = data.isOk;
          }
        });

        return isOk;
      }, "该记录已存在！！！");

    [/@]
    [@b.textfield label="学费" name="creditFeeDefault.value" value=(creditFeeDefault.value)!0 required="true" maxlength="64" check="match('number')" style=elementSTYLE comment="（元/分）"/]
    [@b.formfoot]
      [#--[@b.redirectParams/]--]
      [@b.submit value="提交" /]
    [/@]
  [/@]
[@b.foot/]

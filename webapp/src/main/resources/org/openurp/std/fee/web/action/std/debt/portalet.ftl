      [@b.card class="card-info card-primary card-outline"]
        [#assign title]<i class="fas fa-file-pdf"></i> 费用查询[/#assign]
        [@b.card_header class="border-transparent" title=title  minimal="true" closeable="true"]
        [/@]
        [#if debts?size>0]
        [@b.card_body class="p-0"]
          <div class="table-responsive">
            <table class="table no-margin m-0  compact">
              <tbody>
              [#list debts as debt]
              <tr>
                <td style="width:5%">${debt_index+1}</td>
                <td style="width:30%">${debt.feeType.name}</td>
                <td style="width:33%">欠费 ${(debt.amount*1.0/100)?string("0.##")} 元</td>
                <td  class="text-muted" style="text-align:right">更新于${debt.updatedAt?string("yy-MM-dd HH:mm")}</td>
              </tr>
              [/#list]
              </tbody>
            </table>
          </div>
        [/@]
        [#else]
           [@b.card_body]尚未找到您的欠费信息。[/@]
        [/#if]
      [/@]

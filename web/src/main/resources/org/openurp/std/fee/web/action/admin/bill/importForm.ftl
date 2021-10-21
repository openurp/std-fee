[#ftl]
[@b.head/]
  [@b.toolbar title="收费信息导入"]
    bar.addItem("导入模板下载", function() {
      bg.form.submit("downloadForm");
    },"${base}/static/images/action/download.gif");

    bar.addClose();
  [/@]
  [@b.form action="!importData" theme="list" enctype="multipart/form-data"]
    [@b.messages/]
    <label for="importFile">文件目录:</label>
    <input type="file" name="importFile" value="" id="importFile"/>
    <div style="padding-left: 110px;">
      [@b.submit value="system.button.submit"/]
    </div>
    <div>
      <div style="color: blue">导入说明：</div>
      <table>
        <tr>
          <td width="18px">1.</td>
          <td>上传的是Office97-2003格式的Excel文件。所有信息均要采用文本格式。对于日期和数字等信息也是一样。</td>
        </tr>
        <tr>
          <td>2.</td>
          <td>标注星号的列必填。</td>
        </tr>
      </table>
    </div>
  [/@]
  [@b.form name="downloadForm" action="!downloadTemplate"]
    <input type="hidden" name="file" value="/template/excel/bill.xls"/>
    <input type="hidden" name="display" value="收费信息导入模板"/>
  [/@]
[@b.foot/]

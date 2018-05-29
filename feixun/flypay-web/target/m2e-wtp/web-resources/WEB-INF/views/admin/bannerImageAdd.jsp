<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
	$(function() {
		$('#organizationId').combotree({
			url : '${ctx}/organization/oemProviderTree',
			parentField : 'pid',
			lines : true,
			panelHeight : 'auto',
			value : 'code'
		});

		$('#bannerAddForm').form({
			url : '${ctx}/bannerImage/add',
			onSubmit : function() {
				progressLoad();
				var isValid = $(this).form('validate');
				if (!isValid) {
					progressClose();
				}
				return isValid;
			},
			success : function(result) {
				progressClose();
				result = $.parseJSON(result);
				if (result.success) {
					parent.$.modalDialog.openner_dataGrid.datagrid('reload');
					parent.$.modalDialog.handler.dialog('close');
				} else {
					parent.$.messager.alert('提示', result.msg, 'warning');
				}
			}
		});
	});
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" title="添加BANNER"
		style="overflow: hidden;padding: 3px;">
		<form id="bannerAddForm" method="post" enctype="multipart/form-data">
			<table class="grid">
				<tr>
					<td>Banner名称</td>
					<td><input name="name" type="text" class="easyui-validatebox"
						data-options="required:true "></td>

				</tr>
				<tr>
					<td>Banner后台动作URL</td>
					<td><input name="actionUrl" type="text"
						class="easyui-validatebox" data-options="required:true "></td>
				</tr>
				<tr>
					<td>状态</td>
					<td><select name="status" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0">停用</option>
							<option value="1" selected="selected">正常</option>
					</select></td>
				</tr>
				<tr>
					<td>文件</td>
					<td><input type="file" name="bannerImg" id="bannerImg" /></td>
				</tr>
				<tr>
					<td>运营商</td>
					<td><select id="organizationId" name="organizationId"
						style="width: 140px; height: 29px;" class="easyui-validatebox"
						data-options="required:true"></select></td>
				</tr>
			</table>
		</form>
	</div>
</div>
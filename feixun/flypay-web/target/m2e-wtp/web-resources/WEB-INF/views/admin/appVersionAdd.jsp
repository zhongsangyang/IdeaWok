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
			panelHeight : 'auto'
		});
		$('#appVersionAddForm').form({
			url : '${ctx}/appversion/add',
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
	<div data-options="region:'center',border:false" title=""
		style="overflow: hidden;padding: 3px;">
		<form id="appVersionAddForm" method="post">
			<table class="grid">
				<tr>
					<td>运营商</td>
					<td><select id="organizationId" name="organizationId"
						style="width: 140px; height: 29px;" class="easyui-validatebox"
						data-options="required:true"></select></td>
				</tr>
				<tr>
					<td>版本名称</td>
					<td><input name="versionName" type="text"
						class="easyui-validatebox" data-options="required:true " /></td>
				</tr>
				<tr>
					<td>类型</td>
					<td><select id="appType" name="appType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="ios">ios</option>
							<option value="android">android</option>
					</select></td>
				</tr>
				<tr>
					<td>下载网址</td>
					<td><input name="downloadNet" type="text"
						class="easyui-validatebox" data-options="required:true "></td>
				</tr>
				<tr>
					<td>下载链接</td>
					<td><input name="updateUrl" type="text"
						class="easyui-validatebox" data-options="required:true "></td>
				</tr>

				<tr>
					<td>是否发布</td>
					<td><select name="status" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0" selected="selected">初始化</option>
							<option value="1">发布</option>
					</select></td>
				</tr>
				<tr>
					<td>是否强制更新</td>
					<td><select name="isForce" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0" selected="selected">否</option>
							<option value="1">是</option>
					</select></td>
				</tr>
				<tr>
					<td>更新内容</td>
					<td colspan="3"><textarea rows="3" name="content"
							style="width: 80%" validType='length[0,512]' /></td>
				</tr>
			</table>
		</form>
	</div>
</div>
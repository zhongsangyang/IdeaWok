<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
	$(function() {
		$('#appVersionEditForm').form({
			url : '${ctx}/appversion/edit',
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
					parent.$.messager.alert('错误', result.msg, 'error');
				}
			}
		});
	});
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" title=""
		style="overflow: hidden;padding: 3px;">
		<form id="appVersionEditForm" method="post">
			<table class="grid">
				<input name="id" type="hidden" value="${appversion.id}">
				<input name="version" type="hidden" value="${appversion.version}">
				<tr>
					<td>版本名称</td>
					<td><input name="versionName" type="text"
						class="easyui-validatebox" data-options="required:true "
						value="${appversion.versionName}"></td>
				</tr>
				<tr>
					<td>类型</td>
					<td><select id="appType" name="appType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="ios"
								<c:if test="${appversion.appType == 'ios'}">selected="selected"</c:if>>ios</option>
							<option value="android"
								<c:if test="${appversion.appType == 'android'}">selected="selected"</c:if>>android</option>
					</select></td>
				</tr>
				<tr>
					<td>下载网址</td>
					<td><input name="downloadNet" type="text"
						class="easyui-validatebox" data-options="required:true " value="${appversion.downloadNet}"></td>
				</tr>
				<tr>
					<td>下载链接</td>
					<td><input name="updateUrl" type="text"
						class="easyui-validatebox" data-options="required:true "
						value="${appversion.updateUrl}"></td>
				</tr>
				<tr>
					<td>是否发布</td>
					<td><select name="status" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0"
								<c:if test="${appversion.status == '0'}">selected="selected"</c:if>>初始化</option>
							<option value="1"
								<c:if test="${appversion.status == '1'}">selected="selected"</c:if>>发布</option>
					</select></td>
				</tr>
				<tr>
					<td>是否强制更新</td>
					<td><select name="isForce" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0"
								<c:if test="${appversion.isForce == '0'}">selected="selected"</c:if>>否</option>
							<option value="1"
								<c:if test="${appversion.isForce == '1'}">selected="selected"</c:if>>是</option>
					</select></td>
				</tr>
				<tr>
					<td>更新内容</td>
					<td colspan="3"><textarea rows="3" name="content"
							style="width: 80%" validType='length[0,512]'>${appversion.content}</textarea></td>
				</tr>
			</table>
		</form>
	</div>
</div>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
	$(function() {

		$('#platformOrgConfigEditForm').form({
			url : '${ctx}/platformOrgConfig/edit',
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
		<form id="platformOrgConfigEditForm" method="post">
			<input name="id" type="hidden" value="${platformOrgConfig.id}">
			<table class="grid">
				<tr>
					<td>运营商名称</td>
					<td><input name="orgName" type="text"
						class="easyui-validatebox" disabled="disabled"
						value="${platformOrgConfig.orgName}"></td>
				</tr>
				<tr>
					<td>交易金额手续费率</td>
					<td><input name="platformInputRate" type="text"
						class="easyui-validatebox"
						value="${platformOrgConfig.platformInputRate}"></td>
				</tr>
				<tr>
					<td>实名认证费</td>
					<td><input name="platformAuthenticationFee" type="text"
						class="easyui-validatebox"
						value="${platformOrgConfig.platformAuthenticationFee}"></td>
				</tr>
				<tr>
					<td>短信费</td>
					<td><input name="platformMessageFee" type="text"
						class="easyui-validatebox"
						value="${platformOrgConfig.platformMessageFee}"></td>
				</tr>
				<tr>
					<td>单笔提现手续费</td>
					<td><input name="platformTixianFee" type="text"
						class="easyui-validatebox"
						value="${platformOrgConfig.platformTixianFee}"></td>
				</tr>
				<tr>
					<td>T0代付手续费</td>
					<td><input name="platformT0TixianRate" type="text"
						class="easyui-validatebox"
						value="${platformOrgConfig.platformT0TixianRate}"></td>
				</tr>
			</table>
		</form>
	</div>
</div>
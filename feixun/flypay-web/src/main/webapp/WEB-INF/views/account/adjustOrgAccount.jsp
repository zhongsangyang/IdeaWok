<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
	$(function() {
		$('#adjustOrgAccountForm').form({
			url : '${ctx}/orgAccount/rechargeOrgAccount',
			data : {
				amt : 1,
				type : 1,
				id : 1
			},
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
		<form id="adjustOrgAccountForm" method="post">
			<table class="grid">
				<input id="id" name="id" type="hidden" value="${account.id}">
				<input id="type" name="type" type="hidden" value="${account.type}">
				<tr>
					<td>本人账户余额</td>
					<td><input name="accountAmt" type="text"
						class="easyui-validatebox" value="${accountAmt}"
						readonly="readonly"></td>
				</tr>
				<tr>
					<td>运营商名称</td>
					<td><input name="orgName" type="text"
						class="easyui-validatebox" value="${account.orgName}"
						readonly="readonly"></td>
				</tr>
				<tr>
					<td>充值金额</td>
					<td><input id="amt" name="amt" type="text"
						class="easyui-validatebox" data-options="required:true"></td>
				</tr>
			</table>
		</form>
	</div>
</div>
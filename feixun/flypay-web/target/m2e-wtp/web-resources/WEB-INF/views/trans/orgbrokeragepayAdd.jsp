<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
	$(function() {
		$('#organizationId').combotree({
			url : '${ctx}/organization/tree',
			parentField : 'pid',
			lines : true,
			panelHeight : 'auto'
		});
		$('#sysParamAddForm').form({
			url : '${ctx}/orgBrokeragePay/add',
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
				if (!result.success) {
					parent.$.messager.alert('提示', result.msg, 'warning');
				}
				parent.$.modalDialog.openner_dataGrid.datagrid('reload');
				parent.$.modalDialog.handler.dialog('close');
			}
		});
	});
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" title=""
		style="overflow: hidden;padding: 3px;">
		<form id="sysParamAddForm" method="post">
			<table class="grid">
				<tr>
					<td>运营商</td>
					<td><select id="organizationId" name="orgId"
						style="width: 140px; height: 29px;" class="easyui-validatebox"
						data-options="required:true"></select></td>

				</tr>
				<tr>
					<td>提现金额</td>
					<td><input name="amt" type="text" class="easyui-numberbox"
						data-options="required:true "></td>
				</tr>
			</table>
		</form>
	</div>
</div>
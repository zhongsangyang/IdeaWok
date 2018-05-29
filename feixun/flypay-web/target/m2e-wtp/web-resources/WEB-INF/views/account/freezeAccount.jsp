<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
	$(function() {
		$('#freezeAccountForm').form({
			url : '${ctx}/account/freeze',
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
		<form id="freezeAccountForm" method="post">
			<table class="grid">
				<input id="id" name="id" type="hidden" value="${account.id}">
				<tr>
					<td>状态</td>
					<td><select id="status" name="status" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0"
								<c:if test="${account.status == 0}">selected="selected"</c:if>>正常</option>
							<option value="1"
								<c:if test="${account.status ==1}">selected="selected"</c:if>>平台冻结</option>
							<option value="100"
								<c:if test="${account.status == 100}">selected="selected"</c:if>>跨平台冻结</option>
					</select></td>
				</tr>
				<tr>
					<td>备注</td>
					<td><textarea rows="3" name="remark" style="width: 80%">${account.remark}</textarea></td>
				</tr>
			</table>
		</form>
	</div>
</div>
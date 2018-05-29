<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
	$(function() {
		$('#sysParamAddForm').form({
			url : '${ctx}/sysparam/add',
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
		<form id="sysParamAddForm" method="post">
			<table class="grid">
				<tr>
					<td>参数名称</td>
					<td><input name="paraName" type="text"
						class="easyui-validatebox" data-options="required:true " /></td>
				</tr>
				<tr>
					<td>参数值</td>
					<td><input name="paraValue" type="text"
						class="easyui-validatebox" data-options="required:true "></td>
				</tr>
				<tr>
					<td>描述</td>
					<td colspan="3"><textarea rows="3" name="paraDesc"
							style="width: 80%" validType='length[0,64]' /></td>
				</tr>
			</table>
		</form>
	</div>
</div>
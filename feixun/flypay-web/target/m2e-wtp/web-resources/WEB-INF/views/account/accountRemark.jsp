<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
	$(function() {
		$('#accountRemarkForm').form({
			url : '${ctx}/account/accountRemark',
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
		<form id="accountRemarkForm" method="post">
			<table class="grid">
				<input id="id" name="id" type="hidden" value="${id}">
				<tr>
					<td>内容</td>
					<td><textarea rows="3" id="remark" name="remark"
							maxlength="120" style="width: 80%; height: 180px">${remark}</textarea></td>
				</tr>
			</table>
		</form>
	</div>
</div>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
	$(function() {
		$('#bannerEditForm').form({
			url : '${ctx}/bannerImage/edit',
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
		<form id="bannerEditForm" method="post">
			<table class="grid">
				<input name="id" type="hidden" value="${banner.id}">
				<tr>
					<td>Banner名称</td>
					<td><input name="name" type="text" class="easyui-validatebox"
						data-options="required:true" value="${banner.name}"></td>
				</tr>
				<tr>
					<td>点击动作</td>
					<td><input name="actionUrl" type="text"
						class="easyui-validatebox" data-options="required:true"
						value="${banner.actionUrl}"></td>
				</tr>
				<tr>
					<td>状态</td>
					<td><select name="status" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="1"
								<c:if test="${banner.status == '1'}">selected="selected"</c:if> >正常</option>
							<option value="0" <c:if test="${banner.status == '0'}">selected="selected"</c:if> >停用</option>
					</select></td>
				</tr>
			</table>
		</form>
	</div>
</div>
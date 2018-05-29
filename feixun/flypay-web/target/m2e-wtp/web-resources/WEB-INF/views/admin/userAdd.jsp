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

		$('#roleIds').combotree({
			url : '${ctx}/role/tree',
			multiple : true,
			required : true,
			panelHeight : 'auto'
		});

		$('#userAddForm').form({
			url : '${ctx}/user/add',
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
					parent.$.modalDialog.openner_dataGrid.datagrid('reload');//之所以能在这里调用到parent.$.modalDialog.openner_dataGrid这个对象，是因为user.jsp页面预定义好了
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
		<form id="userAddForm" method="post">
			<table class="grid">
				<tr>
					<td>登录名</td>
					<td><input name="loginName" type="text" placeholder="请输入登录名称"
						class="easyui-validatebox" value=""></td>
					<td>用户类型</td>
					<td><select id="userType" name="userType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="24">普通用户</option>
					</select></td>
				</tr>
				<tr>
					<td>密码</td>
					<td><input name="password" type="password" placeholder="请输入密码"
						class="easyui-validatebox" data-options="required:true"></td>
					<td>运营商</td>
					<td><select id="organizationId" name="organizationId"
						style="width: 140px; height: 29px;" class="easyui-validatebox"
						data-options="required:true"></select></td>
				</tr>
				<tr>
					<td>推荐人code</td>
					<td><input name="pcode" type="text" placeholder="请输入推荐人"
						class="easyui-validatebox" value="" data-options="required:true"></td>
				</tr>
			</table>
		</form>
	</div>
</div>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
	$(function() {
		$('#organizationId').combotree({
			url : '${ctx}/organization/treeByOperator',
			parentField : 'pid',
			lines : true,
			panelWidth : 'auto',
			panelHeight : 256,
		});
		$('#infoListPersonAddForm').form({
			url : '${ctx}/infoList/addPersonInfoList',
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
		<form id="infoListPersonAddForm" method="post">
			<table class="grid">
				<tr>
					<td>运营商</td>
					<td><select id="organizationId" name="organizationId" style="width: 140px; height: 29px;" class="easyui-validatebox" data-options="required:true"></select></td>
				</tr>
				<tr>
					<td>标题</td>
					<td><input name="title" type="text" class="easyui-validatebox"
						data-options="required:true " /></td>
				</tr>
				<tr>
				<td>类型</td>
					<td>
						<select name="infoType" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="1" selected="selected">个人消息</option>
					</select>
						</td>
				</tr>
				<tr>
					<td>手机号</td>
					<td><input name="phone" type="text" class="easyui-validatebox"
						data-options="required:true " /></td>
				</tr>
				<tr>
					<td>内容</td>
					<td colspan="3"><textarea rows="3" name="content"
							style="width: 80%" validType='length[0,512]' /></td>
				</tr>
				<tr>
					<td>是否发布</td>
					<td><select name="status" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0">否</option>
							<option value="1" selected="selected">是</option>
					</select></td>
				</tr>
			</table>
		</form>
	</div>
</div>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script type="text/javascript">
	$(function() {
		$('#organizationId').combotree({
			url : '${ctx}/organization/oemProviderTree',
			parentField : 'pid',
			lines : true,
			panelHeight : 'auto',
			value : '${user.organizationId}'
			// 加载数据是否折叠节点
			/* ,
			onLoadSuccess : function (row, data) {
	             $('#organizationId').combotree('tree').tree("collapseAll");
	        } */
		});
		$('#roleAddForm').form({
			url : '${pageContext.request.contextPath}/role/add',
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
					parent.$.messager.alert('错误', result.msg, 'error');
				}
			}
		});
	});
</script>
<div class="easyui-layout" data-options="fit:true,border:false" >
	<div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;" >
		<form id="roleAddForm" method="post">
			<table class="grid">
				<tr>
					<td>角色名称</td>
					<td><input name="name" type="text" placeholder="请输入角色名称" class="easyui-validatebox span2" data-options="required:true" value=""></td>
				</tr>
				<tr>
					<td>排序</td>
					<td><input name="seq" value="0" class="easyui-numberspinner" style="width: 140px; height: 29px;" required="required" data-options="editable:false"></td>
				</tr>
				<tr>
					<td>备注</td>
					<td colspan="3"><textarea name="description" rows="" cols="" ></textarea></td>
				</tr>
				<tr>
					<td>运营商</td>
					<td><select id="organizationId" name="organizationId" style="width: 140px; height: 29px;" class="easyui-validatebox" data-options="required:true"></select></td>
				</tr>
			</table>
		</form>
	</div>
</div>
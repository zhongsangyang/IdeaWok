<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
	$(function() {
		$('#infoListEditForm').form({
			url : '${ctx}/infoList/edit',
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
		<form id="infoListEditForm" method="post">
			<table class="grid">
				<input name="id" type="hidden" value="${infoList.id}">
				<input name="infoType" type="hidden" value="${infoList.infoType}">
				
				<input name="version" type="hidden" value="${infoList.version}">

				<tr>
					<td>标题</td>
					<td><input name="title" type="text" class="easyui-validatebox"
						data-options="required:true " value="${infoList.title}" /></td>
				</tr>
				<tr>
					<td>内容</td>
					<td colspan="3"><textarea rows="3" id="content" name="content"
							style="width: 80%" validType='length[0,512]'
							>${infoList.content}</textarea></td>
				</tr>
				<tr>
					<td>是否发布</td>
					<td><select id="state" name="status"
						value="${infoList.status}" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0"
								<c:if test="${infoList.status == '0'}">selected="selected"</c:if>>否</option>
							<option value="1"
								<c:if test="${infoList.status == '1'}">selected="selected"</c:if>>是</option>
					</select></td>
				</tr>
				<tr>
					<td>是否展示</td>
					<td><select  id="isShow"
						value="${infoList.isShow}"  name="isShow" class="easyui-combobox" 	data-options="width:140,height:29,editable:false,panelHeight:'auto'">
						<option value="0" <c:if test="${infoList.isShow == '0'}">selected="selected"</c:if>>否</option>
						<option value="1" <c:if test="${infoList.isShow == '1'}">selected="selected"</c:if>>是</option>
					</select></td>
				</tr>
				<tr>
					<td>是否强制</td>
					<td><select id="isForce" name="isForce"   name="isForce"  value="${infoList.isForce}"  class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
						<option value="0" <c:if test="${infoList.isForce == '0'}">selected="selected"</c:if>>否</option>
						<option value="1" <c:if test="${infoList.isForce == '1'}">selected="selected"</c:if>>是</option>
					</select></td>
				</tr>

				<tr>
					<td>强制显示时长</td>
					<td><input id="forceHours" name="forceHours" type="number" value="${infoList.forceHours}" /></td>
				</tr>
			</table>
		</form>
	</div>
</div>
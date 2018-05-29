<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script type="text/javascript">
	$(function() {
		$('#adjustAccountForm').form({
			url : '${ctx}/account/adjustAccount',
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
	function validateOrderNum(){
		var userId = $("#userId").val();
		var adjustType = $("#adjustType").val();
		var amt = $("#amt").val();
		var orderNum = $("#orderNum").val();
		 $.post('${ctx}/account/validateOrderNumAndAmt', {
			 userId : userId,
			 adjustType : adjustType,
			 amt : amt,
			 orderNum : orderNum
		}, function(result) {
			if (result.success) {
				parent.$.messager.alert('提示', result.msg, 'info');
				dataGrid.datagrid('reload');
			}
			progressClose();
		}, 'JSON'); 
	}
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">
		<form id="adjustAccountForm" method="post">
			<table class="grid">
				<input id="id" name="id" type="hidden"  value="${account.id}">
				<input id="userId" name="userId" type="hidden"  value="${account.userId}">
				<tr>
					<td>商户名称</td>
					<td><input  name="realName" type="text" class="easyui-validatebox" data-options="required:true" value="${account.realName}" readonly="readonly"></td>
				</tr>
				<tr>
					<td>调账类型</td>
					<td><select id="adjustType" name="adjustType" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="A">增加</option>
							<option value="B" selected="selected">减少</option>
					</select></td>
				</tr>
				<tr>
					<td>调账金额</td>
					<td><input id ="amt" name="amt" type="text"  class="easyui-validatebox" data-options="required:true" ></td>
				</tr>
				<tr>
					<td>关联订单</td>
					<td><textarea rows="3"  id="orderNum" name="orderNum"  style="width: 80%" class="easyui-validatebox" data-options="required:true" /><button onclick="validateOrderNum()" >验证</button></td>
				</tr>
				<tr>
					<td>调账理由</td>
					<td ><textarea rows="3"  name="description"  style="width: 80%" ></textarea></td>
				</tr>
			</table>
		</form>
	</div>
</div>
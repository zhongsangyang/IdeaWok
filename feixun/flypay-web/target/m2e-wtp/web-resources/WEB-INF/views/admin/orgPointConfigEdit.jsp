<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
	$(function() {

		$('#platformOrgConfigEditForm').form({
			url : '${ctx}/orgPointConfig/edit',
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
		<form id="platformOrgConfigEditForm" method="post">
			<input name="id" type="hidden" value="${orgPointConfig.id}">
			<table class="grid">
				<tr>
					<td>运营商名称</td>
					<td><input name="orgName" type="text"
						class="easyui-validatebox" disabled="disabled"
						value="${orgPointConfig.orgName}"></td>
				</tr>
				<tr>
					<td>支付类型</td>
					<td><select id="payType" name="payType"
						class="easyui-combobox" disabled="disabled"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<c:forEach items="${payTypeObj}" var="obj">
								<option value="${obj.code}"  <c:if test="${orgPointConfig.payType == obj.code}">selected="selected"</c:if>>${obj.text }</option>
							</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td>入账类型</td>
					<td><select id="type" name="type" class="easyui-combobox"
						disabled="disabled"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<c:forEach items="${bigTranTypeObj}" var="obj">
								<option value="${obj.code}"  <c:if test="${orgPointConfig.type == obj.code}">selected="selected"</c:if>>${obj.text }</option>
							</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td>状态</td>
					<td><select id="status" name="status" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0"
								<c:if test="${orgPointConfig.status == 0}">selected="selected"</c:if>>正常</option>
							<option value="1"
								<c:if test="${orgPointConfig.status == 1}">selected="selected"</c:if>>停用</option>
					</select></td>
				</tr>
				<tr>
					<td>最高费率</td>
					<td><input name="topRate" type="text"
						class="easyui-validatebox" value="${orgPointConfig.topRate}"></td>
				</tr>
				<tr>
					<td>中等费率</td>
					<td><input name="midRate" type="text"
						class="easyui-validatebox" value="${orgPointConfig.midRate}"></td>
				</tr>
				<tr>
					<td>最低费率</td>
					<td><input name="lowRate" type="text"
						class="easyui-validatebox" value="${orgPointConfig.lowRate}"></td>
				</tr>
				<tr>
					<td>降低至中费率消耗</td>
					<td><input name="toMidNum" type="text"
						class="easyui-validatebox" value="${orgPointConfig.toMidNum}"></td>
				</tr>
				<tr>
					<td>降低至低费率消耗</td>
					<td><input name="toLowNum" type="text"
						class="easyui-validatebox" value="${orgPointConfig.toLowNum}"></td>
				</tr>
			</table>
		</form>
	</div>
</div>
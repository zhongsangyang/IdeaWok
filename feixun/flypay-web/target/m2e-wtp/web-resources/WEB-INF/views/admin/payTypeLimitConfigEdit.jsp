<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
	$(function() {
		$('#payTypeLimitConfigEditForm').form({
			url : '${ctx}/payTypeLimitConfig/edit',
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
		<form id="payTypeLimitConfigEditForm" method="post">
			<input name="id" type="hidden" value="${payTypeLimitConfig.id}">
			<table class="grid">
				<tr>
					<td>通道类型</td>
					<td><select id="payType" name="channelType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'"
						readonly="readonly">
							<c:forEach items="${payType}" var="obj">
								<option value="${obj.code}"
									<c:if test="${payTypeLimitConfig.payType == obj.code}">selected="selected"</c:if>>${obj.text }</option>
							</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td>大小额类型</td>
					<td><select id="amtType" name="amtType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'"
						readonly="readonly">
							<option value="100"
								<c:if test="${payTypeLimitConfig.amtType ==100}">selected="selected"</c:if>>小额</option>
							<option value="200"
								<c:if test="${payTypeLimitConfig.amtType ==200}">selected="selected"</c:if>>大额</option>
					</select></td>
					<td>服务费</td>
					<td><input type="text" name="srvFee"
						class="easyui-validatebox" data-options="required:true "
						value="${payTypeLimitConfig.srvFee}" /></td>
				</tr>
				<tr>
					<td>最大金额</td>
					<td><input type="text" name="maxAmt"
						class="easyui-validatebox" data-options="required:true "
						value="${payTypeLimitConfig.maxAmt}" /></td>
					<td>最小金额</td>
					<td><input type="text" name="minAmt"
						class="easyui-validatebox" data-options="required:true "
						value="${payTypeLimitConfig.minAmt}" /></td>
				</tr>
				<tr>
					<td>通道CODE</td>
					<td><input type="text" name="code"
						class="easyui-validatebox" data-options="required:false "
						value="${payTypeLimitConfig.code}" /></td>
					<td>通道名称</td>
					<td><input type="text" name="name"
						class="easyui-validatebox" data-options="required:false "
						value="${payTypeLimitConfig.name}" /></td>
				</tr>
				<tr>
					<td>开始时间</td>
					<td><input type="text" name="startTime"
						class="easyui-validatebox" data-options="required:false "
						value="${payTypeLimitConfig.startTime}" /></td>
					<td>开始时间</td>
					<td><input type="text" name="endTime"
						class="easyui-validatebox" data-options="required:false "
						value="${payTypeLimitConfig.endTime}" />例:  浦发。招商</td>
				</tr>
				<tr>
					<td>不支持卡行</td>
					<td><input type="text" name="unSupportCardName"
						class="easyui-validatebox" data-options="required:false"
						value="${payTypeLimitConfig.unSupportCardName}" /></td>
					<td>支付类型名称</td>
					<td><input type="text" name="payTypeName"
						class="easyui-validatebox" data-options="required:false"
						value="${payTypeLimitConfig.payTypeName}" /></td>
				</tr>
				<tr>
					<td>状态</td>
					<td>
						
						<select id="status" name="status"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0"
								<c:if test="${payTypeLimitConfig.status ==0}">selected="selected"</c:if>>正常</option>
							<option value="1"
								<c:if test="${payTypeLimitConfig.status ==1}">selected="selected"</c:if>>停用</option>
					</select>
						
						</td>
				</tr>
			</table>
		</form>
	</div>
</div>
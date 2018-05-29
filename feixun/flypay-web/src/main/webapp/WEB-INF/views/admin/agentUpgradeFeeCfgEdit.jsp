<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
	$(function() {

		$('#organizationId').combotree({
			url : '${ctx}/organization/tree',
			parentField : 'pid',
			lines : true,
			panelHeight : 'auto',
			value : '${user.organizationId}'
		});

		$('#agentSettlementRateConfigEditForm').form({
			url : '${ctx}/agentUpgradeFeeCfg/edit',
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
		<form id="agentSettlementRateConfigEditForm" method="post">
			<table class="grid">
				<tr>
					<td>分润类型</td>
					<td>
					<select id="payType" name="payType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${payType}" var="obj">
								<option value="${obj.code}"  <c:if test="${agentSettlementRateConfig.payType == obj.code}">selected="selected"</c:if>>${obj.text }</option>
							</c:forEach>
					</select>
					</td>
					<td>运行商</td>
					<td><input name="orgName" type="text"
						class="easyui-validatebox" readonly="readonly"
						value="${agentSettlementRateConfig.orgName}"></td>
					<input name="organizationId" type="hidden"
						value="${agentSettlementRateConfig.organizationId}" />
					</td>
				</tr>
				<tr>
					<td>代理类型</td>
					<td>				
					<select id="agentType" name="agentType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${orgAgentLevel}" var="obj">
								<option value="${obj.code}"  <c:if test="${agentSettlementRateConfig.agentType == obj.code}">selected="selected"</c:if>>${obj.text }</option>
							</c:forEach>
					</select>
					</td>
					<td>结算费率</td>
					<td><input type="text" name="settlementRate"
						class="easyui-validatebox" data-options="required:true "
						value="${agentSettlementRateConfig.settlementRate}" /></td>
				</tr>
			</table>
		</form>
	</div>
</div>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script type="text/javascript">
	$(function() {
		$('#agentUserSettlementConfigForm').form({
			url : '${ctx}/userSettlementConfig/edit',
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
	<div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">
		<form id="agentUserSettlementConfigForm" method="post">
			<table class="grid">
					<input name="id" type="hidden"  value="${userSettlementConfig.id}">
				<tr>
					<td>T0手续费</td>
					<td>
					<input name="t0Fee" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.t0Fee}"></td>
				</tr>
				<tr>
					<td>T0最低限额</td>
					<td>
					<input name="minT0Amt" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.minT0Amt}"></td>
				</tr>
				<tr>
					<td>T0最高限额</td>
					<td>
					<input name="maxT0Amt" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.maxT0Amt}"></td>
				</tr>
				
				<tr>
					<td>T1手续费</td>
					<td>
					<input name="t1Fee" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.t1Fee}"></td>
				</tr>
				<tr>
					<td>T1最低限额</td>
					<td>
					<input name="minT1Amt" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.minT1Amt}"></td>
				</tr>
				<tr>
					<td>T1最高限额</td>
					<td>
					<input name="maxT1Amt" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.maxT1Amt}"></td>
				</tr>
				<tr>
					<td>佣金提现手续费</td>
					<td>
					<input name="rabaleFee" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.rabaleFee}"></td>
				</tr>
				<tr>
					<td>佣金提现最低限额</td>
					<td>
					<input name="minRabaleAmt" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.minRabaleAmt}"></td>
				</tr>
				<tr>
					<td>佣金提现最高限额</td>
					<td>
					<input name="maxRabaleAmt" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.maxRabaleAmt}"></td>
				</tr>
				<tr>
					<td>用户付款手续费率</td>
					<td>
					<input name="inputFee" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.inputFee}"></td>
				</tr>
				<tr>
					<td>付款分润手续费率</td>
					<td>
					<input name="shareFee" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.shareFee}"></td>
				</tr>
			</table>
		</form>
	</div>
</div>
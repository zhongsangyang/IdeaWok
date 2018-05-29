<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script type="text/javascript">
	$(function() {
		$('#userSettlementConfigForm').form({
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
		<form id="userSettlementConfigForm" method="post">
			<table class="grid">
				<input name="id" type="hidden"  value="${userSettlementConfig.id}">
				<tr>
					<td>T0手续费</td>
					<td>
					<input name="t0Fee" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.t0Fee}"></td>
					<td>T1手续费</td>
					<td>
					<input name="t1Fee" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.t1Fee}"></td>
				</tr>
				<tr>
					<td>T0最低限额</td>
					<td>
					<input name="minT0Amt" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.minT0Amt}"></td>
					<td>T1最低限额</td>
					<td>
					<input name="minT1Amt" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.minT1Amt}"></td>
				</tr>
				<tr>
					<td>T0最高限额</td>
					<td>
					<input name="maxT0Amt" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.maxT0Amt}"></td>
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
					<td>佣金提现最高限额</td>
					<td>
					<input name="maxRabaleAmt" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.maxRabaleAmt}"></td>
				</tr>
				<tr>
					<td>每日提现额度</td>
					<td>
					<input name="maxTodayOutAmt" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.maxTodayOutAmt}"></td>
					<td>默认付款手续费率</td>
					<td>
					<input name="inputFee" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.inputFee}"></td>
				</tr>
				<tr>
					<td>微信T1小额手续费率</td>
					<td>
					<input name="inputFeeWeixin" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.inputFeeWeixin}"></td>
					<td>微信D0小额手续费率</td>
					<td>
					<input name="inputFeeD0Weixin" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.inputFeeD0Weixin}"></td>
				
				</tr>
				<tr>
					<td>微信T1大额手续费率</td>
					<td>
					<input name="inputFeeBigWeixin" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.inputFeeBigWeixin}"></td>
					<td>微信D0大额手续费率</td>
					<td>
					<input name="inputFeeD0BigWeixin" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.inputFeeD0BigWeixin}"></td>
				
				</tr>
				<tr>
					<td>支付宝T1小额手续费率</td>
					<td>
					<input name="inputFeeAlipay" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.inputFeeAlipay}"></td>
					<td>支付宝D0小额手续费率</td>
					<td>
					<input name="inputFeeD0Alipay" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.inputFeeD0Alipay}"></td>
				
				</tr>
				<tr>
					<td>支付宝T1大额手续费率</td>
					<td>
					<input name="inputFeeBigAlipay" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.inputFeeBigAlipay}"></td>
					<td>支付宝D0大额手续费率</td>
					<td>
					<input name="inputFeeD0BigAlipay" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.inputFeeD0BigAlipay}"></td>
				
				</tr>
				<tr>
					<td>银联T1小额手续费率</td>
					<td>
					<input name="inputFeeYinlian" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.inputFeeYinlian}"></td>
					<td>银联D0小额手续费率</td>
					<td>
					<input name="inputFeeD0Yinlian" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.inputFeeD0Yinlian}"></td>
			
				</tr>
				<tr>
					<td>银联T1大额手续费率</td>
					<td>
					<input name="inputFeeBigYinlian" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.inputFeeBigYinlian}"></td>
					<td>银联D0大额手续费率</td>
					<td>
					<input name="inputFeeD0BigYinlian" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.inputFeeD0BigYinlian}"></td>
			
				</tr>
				<tr>
					<td>京东T1小额手续费率</td>
					<td>
					<input name="inputFeeJingDong" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.inputFeeJingDong}"></td>
					<td>京东D0小额手续费率</td>
					<td>
					<input name="inputFeeD0JingDong" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.inputFeeD0JingDong}"></td>
				</tr>
				<tr>
					<td>京东T1大额手续费率</td>
					<td>
					<input name="inputFeeBigJingDong" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.inputFeeBigJingDong}"></td>
					<td>京东D0大额手续费率</td>
					<td>
					<input name="inputFeeD0BigJingDong" type="text" class="easyui-validatebox" data-options="required:true,validType:['number','fixedLength[8]']" value="${userSettlementConfig.inputFeeD0BigJingDong}"></td>
				</tr>
			</table>
		</form>
	</div>
</div>
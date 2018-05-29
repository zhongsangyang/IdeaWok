<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
	$(function() {
		$('#channelAddForm').form({
			url : '${ctx}/channel/add',
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
		<form id="channelAddForm" method="post">
			<table class="grid">
				<tr>
					<td>渠道名称</td>
					<td><select id="name" name="name" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<c:forEach items="${statementTypeObj}" var="obj">
								<option value="${obj.code}">${obj.text }</option>
							</c:forEach>
					</select></td>
					<td>详细名称</td>
					<td><input name="detailName" type="text"
						class="easyui-validatebox" data-options="required:true "></td>
				</tr>
				<tr>
					<td>渠道账户</td>
					<td><input name="account" type="text"
						class="easyui-validatebox" data-options="required:true "></td>
					<td>支付类型</td>
					<td><select id="type" name="type" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<c:forEach items="${transTypeObj}" var="obj">
								<option value="${obj.code}">${obj.text }</option>
							</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td>用户类型</td>
					<td><select id="userType" name="userType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<c:forEach items="${channelUserTypeObj}" var="obj">
								<option value="${obj.code}">${obj.text }</option>
							</c:forEach>
					</select></td>
					<td>大小额设置</td>
					<td><select name="limitType" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0" selected="selected">通用</option>
							<option value="1">小额</option>
							<option value="2">大额</option>
					</select></td>
				</tr>
				<tr>
					<td>真实费率</td>
					<td><input name="realRate" type="text"
						class="easyui-validatebox" data-options="required:true "></td>
					<td>展示用户费率</td>
					<td><input name="showRate" type="text"
						class="easyui-validatebox" data-options="required:true "></td>
				</tr>
				<tr>
					<td>用户分润费率</td>
					<td><input name="shareRate" type="text"
						class="easyui-validatebox" data-options="required:true "></td>
					<td>通道返佣比例</td>
					<td><input name="commissionRate" type="text"
						class="easyui-validatebox" data-options="required:true "></td>
				</tr>
				<tr>
					<td>单笔最低限额</td>
					<td><input name="minTradeAmt" type="text"
						class="easyui-validatebox" data-options="required:true "></td>
					<td>单笔最高限额</td>
					<td><input name="maxTradeAmt" type="text"
						class="easyui-validatebox" data-options="required:true "></td>
				</tr>
				<tr>
					<td>通道最低限额</td>
					<td><input name="minChannelAmt" type="text"
						class="easyui-validatebox" data-options="required:true "></td>
					<td>通道最高限额</td>
					<td><input name="maxChannelAmt" type="text"
						class="easyui-validatebox" data-options="required:true "></td>
				</tr>
				<tr>
					<td>每日最高值</td>
					<td><input name="maxAmtPerDay" type="text"
						class="easyui-validatebox" data-options="required:true "></td>
					<td>每日每人使用次数</td>
					<td><input name="maxNumPerPersonPerDay" type="number"
						class="maxNumPerPersonPerDay" data-options="required:true "></td>
				</tr>
				<tr>
					<td>使用序号</td>
					<td><input name="seq" type="text" class="easyui-validatebox"
						data-options="required:true "></td>
					<td>是否启用</td>
					<td><select name="status" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0">是</option>
							<option value="1" selected="selected">否</option>
					</select></td>
				</tr>
				<tr>
					<td>配置</td>
					<td colspan="3"><textarea id="config" name="config" style="width: 80%"
							class="easyui-validatebox" data-options="required:true "></textarea>
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>
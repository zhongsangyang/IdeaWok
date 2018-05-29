<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
	$(function() {
		$('#sysParamEditForm').form({
			url : '${ctx}/channel/edit',
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
		<form id="sysParamEditForm" method="post">
			<table class="grid">
				<input name="id" type="hidden" value="${channel.id}">
				<input name="version" type="hidden" value="${channel.version}">
				<tr>
					<td>渠道名称</td>
					<td><select id="name" name="name" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'"
						disabled="disabled">
							<c:forEach items="${statementTypeObj}" var="obj">
								<option value="${obj.code}"
									<c:if test="${channel.name == obj.code}">selected="selected"</c:if>>${obj.text }</option>
							</c:forEach>
					</select></td>
					<td>详细名称</td>
					<td><input name="detailName" type="text"
						class="easyui-validatebox" data-options="required:true "
						value="${channel.detailName}" disabled="disabled"></td>
				</tr>
				<tr>
					<td>渠道账户</td>
					<td><input name="account" type="text"
						class="easyui-validatebox"  value="${channel.account}"  disabled="disabled"></td>
					<td>支付类型</td>
					<td><select id="type" name="type" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'"
						disabled="disabled">
							<c:forEach items="${transTypeObj}" var="obj">
								<option value="${obj.code}"
									<c:if test="${channel.type == obj.code}">selected="selected"</c:if>>${obj.text }</option>
							</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td>用户类型</td>
					<td><select id="userType" name="userType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<c:forEach items="${channelUserTypeObj}" var="obj">
								<option value="${obj.code}"
									<c:if test="${channel.userType == obj.code}">selected="selected"</c:if>>${obj.text }</option>
							</c:forEach>
					</select></td>
					<td>大小额设置</td>
					<td><select name="limitType" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0" <c:if test="${channel.limitType == 0}">selected="selected"</c:if>>通用</option>
							<option value="1" <c:if test="${channel.limitType == 1}">selected="selected"</c:if>>小额</option>
							<option value="2" <c:if test="${channel.limitType == 2}">selected="selected"</c:if>>大额</option>
					</select></td>
				</tr>
				<tr>
					<td>真实费率</td>
					<td><input name="realRate" type="text"
						class="easyui-validatebox" data-options="required:true "
						value="${channel.realRate}"></td>
					<td>展示用户费率</td>
					<td><input name="showRate" type="text"
						class="easyui-validatebox" data-options="required:true "
						value="${channel.showRate}"></td>
				</tr>
				<tr>
					<td>返佣费率</td>
					<td><input name="shareRate" type="text"
						class="easyui-validatebox" data-options="required:true "
						value="${channel.shareRate}"></td>
					<td>通道返佣比例</td>
					<td><input name="commissionRate" type="text"
						class="easyui-validatebox" data-options="required:true "
						value="${channel.commissionRate}"></td>
				</tr>
				<tr>
					<td>单笔最低限额</td>
					<td><input name="minTradeAmt" type="text"
						class="easyui-validatebox" data-options="required:true "
						value="${channel.minTradeAmt}"></td>
					<td>单笔最高限额</td>
					<td><input name="maxTradeAmt" type="text"
						class="easyui-validatebox" data-options="required:true "
						value="${channel.maxTradeAmt}"></td>
				</tr>
				<tr>
					<td>通道最低限额</td>
					<td><input name="minChannelAmt" type="text"
						class="easyui-validatebox" data-options="required:true "
						value="${channel.minChannelAmt}"></td>
					<td>通道最高限额</td>
					<td><input name="maxChannelAmt" type="text"
						class="easyui-validatebox" data-options="required:true "
						value="${channel.maxChannelAmt}"></td>
				</tr>
				<tr>
					<td>每日最高值</td>
					<td><input name="maxAmtPerDay" type="text"
						class="easyui-validatebox" data-options="required:true "
						value="${channel.maxAmtPerDay}"></td>
					<td>每日每人使用次数</td>
					<td><input name="maxNumPerPersonPerDay" type="number"
						class="easyui-validatebox" data-options="required:true "
						value="${channel.maxNumPerPersonPerDay}"></td>
						
				</tr>
				<tr>
					<td>今日累计</td>
					<td><input name="todayAmt" type="text"
						class="easyui-validatebox" data-options="required:true "
						value="${channel.todayAmt}"></td>
				</tr>
				<tr>
					<td>序号</td>
					<td><input name="seq" type="text" class="easyui-validatebox"
						data-options="required:true " value="${channel.seq}"></td>
					<td>是否启用</td>
					<td><select name="status" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'"
						value="${channel.status}">
							<option value="0"
								<c:if test="${channel.status == 0}">selected="selected"</c:if>>是</option>
							<option value="1"
								<c:if test="${channel.status == 1}">selected="selected"</c:if>>否</option>
					</select></td>
				</tr>
					<tr>
					<td>配置</td>
					<td colspan="3">
						<textarea id="config" name="config"  class="easyui-validatebox"  style="width: 80%">${channel.config}</textarea>
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>
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

		$('#orgChannelUserRateConfigEditForm').form({
			url : '${ctx}/orgChannelUserRateConfig/edit',
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
		<form id="orgChannelUserRateConfigEditForm" method="post">
			<table class="grid">
				<tr>
					<td>运行商</td>
					<td><input name="orgName" type="text"
						class="easyui-validatebox" readonly="readonly"
						value="${orgChannelUserRateConfig.orgName}"></td>
					<input name="organizationId" type="hidden"
						value="${orgChannelUserRateConfig.organizationId}" />
					</td>
				</tr>
				<tr>
					<td>通道类型</td>
					<td><select id="channelType" name="channelType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'"
						readonly="readonly">
							<option selected value="">请选择</option>
							<c:forEach items="${payType}" var="obj">
								<option value="${obj.code}"
									<c:if test="${orgChannelUserRateConfig.channelType == obj.code}">selected="selected"</c:if>>${obj.text }</option>
							</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td>用户类型</td>
					<td><%-- <select id="agentType" name="agentType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'"
						readonly="readonly">
							<option selected value="">请选择</option>
							<c:forEach items="${userType}" var="obj">
								<option value="${obj.code}"
									<c:if test="${orgChannelUserRateConfig.agentType == obj.code}">selected="selected"</c:if>>${obj.text }</option>
							</c:forEach>
					</select> --%>
					<input name="agentType" type="text"
						class="easyui-validatebox" readonly="readonly"
						value="${orgChannelUserRateConfig.agentType}"></td>
					</td>
				</tr>
				
				<tr>
					<td>T1利率</td>
					<td><input type="text" name="t1Rate"
						class="easyui-validatebox" data-options="required:true "
						value="${orgChannelUserRateConfig.t1Rate}" /></td>
					<td>D0利率</td>
					<td><input type="text" name="d0Rate"
						class="easyui-validatebox" data-options="required:true "
						value="${orgChannelUserRateConfig.d0Rate}" /></td>
				</tr>
				<tr>
					<td>T1大额利率</td>
					<td><input type="text" name="t1BigRate"
						class="easyui-validatebox" data-options="required:true "
						value="${orgChannelUserRateConfig.t1BigRate}" /></td>
					<td>D0大额利率</td>
					<td><input type="text" name="d0BigRate"
						class="easyui-validatebox" data-options="required:true "
						value="${orgChannelUserRateConfig.d0BigRate}" /></td>
				</tr>
			</table>
		</form>
	</div>
</div>
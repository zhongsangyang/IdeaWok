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

		$('#brokerageConfigEditForm').form({
			url : '${ctx}/brokerageConfig/edit',
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
	$("#agentType").val('${brokerageConfig.agentType}');
	$("#cfgType").val('${brokerageConfig.cfgType}');
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" title=""
		style="overflow: hidden;padding: 3px;">
		<form id="brokerageConfigEditForm" method="post">
			<table class="grid">
				<tr>
					<td>分润类型</td>
					<td><select name="cfgType" id="cfgType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'"
						readonly="readonly">
							<option value="10">佣金分润</option>
							<option value="20">代理分润</option>
					</select></td>
					<td>运行商</td>
					<td><input name="orgName" type="text"
						class="easyui-validatebox" readonly="readonly"
						value="${brokerageConfig.orgName}"></td>
					<input name="organizationId" type="hidden"
						value="${brokerageConfig.organizationId}" />
					</td>
				</tr>
				<tr>
					<td>用户类型</td>
					<td><select name="agentType" id="agentType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'"
						readonly="readonly">
							<option value="21">钻石用户</option>
							<option value="22">金牌用户</option>
							<option value="23">银牌用户</option>
							<option value="24">普通用户</option>
					</select></td>
				
					<td>一级分润(%)</td>
					<td><input type="text" name="firstRate"
						class="easyui-validatebox" data-options="required:true "
						value="${brokerageConfig.firstRate}" /></td>
				</tr>
				<tr>	<td>二级分润(%)</td>
					<td><input type="text" name="secRate"
						class="easyui-validatebox" data-options="required:true "
						value="${brokerageConfig.secRate}" /></td>
					<td>三级分润(%)</td>
					<td><input name="thirdRate" type="text"
						class="easyui-validatebox" data-options="required:true "
						value="${brokerageConfig.thirdRate}"></td>
				</tr>

			</table>
		</form>
	</div>
</div>
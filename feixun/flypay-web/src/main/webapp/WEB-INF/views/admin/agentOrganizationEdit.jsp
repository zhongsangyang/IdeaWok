<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script type="text/javascript">
	

	$(function() {
		
		$('#pid').combotree({
			url : '${ctx}/organization/treeByOperator',
			parentField : 'pid',
			lines : true,
			panelHeight : 'auto',
			value :'${organization.id}'
		});
		
		$('#agentOrganizationEditForm').form({
			url : '${ctx}/organization/editAgentOrganization',
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
					parent.$.modalDialog.openner_treeGrid.treegrid('reload');//之所以能在这里调用到parent.$.modalDialog.openner_treeGrid这个对象，是因为organization.jsp页面预定义好了
					parent.$.modalDialog.handler.dialog('close');
				}
			}
		});
		$("#status").val('${organization.status}');
	});
</script>
<div style="padding: 3px;">
	<form id="agentOrganizationEditForm" method="post">
		<table class="grid">
			<tr>
				<input name="id" type="hidden"  value="${organization.id}">
				<input name="version" type="hidden"  value="${organization.version}">
				<td>编号</td>
				<td><input name="code" type="text" value="${organization.code}" disabled="disabled"/></td>
				<td>名称</td>
				<td><input name="name" type="text" value="${organization.name}"
					placeholder="请输入运营商名称" class="easyui-validatebox"
					data-options="required:true"></td>
			</tr>
			<tr>
				<td>代理级别</td>
				<td>
					<select id="agentLevel" name="agentLevel" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<c:forEach items="${orgAgentLevel}" var="obj">
								<option value="${obj.code}" <c:if test="${organization.agentLevel == obj.code}">selected="selected"</c:if>>${obj.text }</option>
							</c:forEach>
					</select>
				</td>
				
				<td>代理商等级</td>
				<td>
					<select id="orgType" name="orgType"  class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
					  <c:if test="${Type==1 }">
					    <option value="5">老板</option>
						<option value="4">区域合伙人</option>
						<option value="3">超级合伙人</option>
					 </c:if>
					 <c:if test="${Type==2 }">
						<option value="3">一级</option>
						<option value="4">二级</option>
						<option value="5">三级</option>
					 </c:if>	
					</select>
				</td>
				
				
			</tr>
			<tr>
				<td>地址</td>
				<td colspan="3"><input  name="address" style="width: 300px;" value="${organization.address}"/></td>
			</tr>
			<tr>
				<td>状态</td>
				<td><select id="status" name="status" value="${organization.status}" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0" <c:if test="${organization.status == 0}">selected="selected"</c:if>>正常</option>
							<option value="1" <c:if test="${organization.status == 1}">selected="selected"</c:if>>停用</option>
					</select></td>
			</tr>
		</table>
	</form>
</div>

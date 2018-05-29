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
			panelHeight : 256
		});
		var type = '${Type}';
		
		if(type==1){
		   $("#pid").combotree('setValue',116);
		} 
		
		
		$('#agentOrganizationAddForm').form({
			url : '${ctx}/organization/add',
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
		
	});
</script>
<div style="padding: 3px;">
	<form id="agentOrganizationAddForm" method="post">
		<table class="grid">
			<tr>
			<input type="hidden" name="agentType"  value="2"/>
				<td>编号</td>
				<td><input name="code" type="text" placeholder="请输入编号=上级编号+序号" class="easyui-validatebox" data-options="required:true" ></td>
				<td>名称</td>
				<td><input name="name" type="text" placeholder="请输入名称" class="easyui-validatebox" data-options="required:true" ></td>
			</tr>
			<tr>
				<td>代理级别</td>
				<td>
					<select id="agentLevel" name="agentLevel" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<c:forEach items="${orgAgentLevel}" var="obj">
								<option value="${obj.code}" >${obj.text }</option>
							</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>分佣账户手机号码</td>
				<td><input name="userPhone" type="text" placeholder="请输入11位手机号" class="easyui-validatebox" data-options="validType:'validatePhone'" ></td>
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
				<td colspan="3"><input  name="address" style="width: 300px;"/></td>
			</tr>
			<tr>
				<td>上级</td>
				<td colspan="3"><select id="pid" name="pid" style="width:200px;height: 29px;"></select>
				<a class="easyui-linkbutton" href="javascript:void(0)" onclick="$('#pid').combotree('clear');" >清空</a></td>
			</tr>
			<tr>
			<td>机构状态</td>
			<td><select id="status" name="state"  class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
					<option value="0">正常</option>
					<option value="1">停用</option>
			</select></td>
			</tr>
		</table>
	</form>
</div>
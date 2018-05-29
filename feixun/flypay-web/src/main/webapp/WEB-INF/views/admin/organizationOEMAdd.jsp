<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script type="text/javascript">
	

	$(function() {
		
		$('#pid').combotree({
			url : '${ctx}/organization/serviceProviderTree',
			parentField : 'pid',
			lines : true,
			panelHeight : 'auto'
		});
		
		$('#organizationAddForm').form({
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
	<form id="organizationAddForm" method="post">
		<table class="grid">
			<input type="hidden" name="agentType"  value="0"/>
			<!-- OEM等级，固定为2 -->
			<input type="hidden" name="orgType" value="2"/>
			<tr>
				<td>OEM编号</td>
				<td><input name="code" type="text" placeholder="请输入9位OEM编号，例如F20160001" class="easyui-validatebox" data-options="required:true" ></td>
				<td>OEM名称</td>
				<td><input name="name" type="text" placeholder="请输入OEM名称" class="easyui-validatebox" data-options="required:true" ></td>
			</tr>
			<tr>
				<td>App名称</td>
				<td><input name="appName" type="appName" placeholder="App名称" class="easyui-validatebox" data-options="required:true" ></td>
			</tr>
			<tr>
				<td>OEM金牌分润</td>
				<td><input name="goldFee" type="text" placeholder="若为-1，表示全部给运营商" class="easyui-validatebox" data-options="required:true" ></td>
				<td>OEM钻石分润</td>
				<td><input name="diamondFee" type="text" placeholder="若为-1，表示全部给运营商" class="easyui-validatebox" data-options="required:true" ></td>
			</tr>
			<tr>
				<td>上级运营商</td>
				<td colspan="3"><select id="pid" name="pid" style="width:200px;height: 29px;"></select>
				<a class="easyui-linkbutton" href="javascript:void(0)" onclick="$('#pid').combotree('clear');" >清空</a></td>
			</tr>
			<tr>
				<td>机构状态</td>
				<td><select id="status" name="state"  class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
						<option value="0">正常</option>
						<option value="1">停用</option>
				</select></td>
				<td>代理级别</td>
				<td>
				<select id="agentLevel" name="agentLevel" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
						<c:forEach items="${orgAgentLevel}" var="obj">
							<option value="${obj.code}">${obj.text }</option>
						</c:forEach>
				</select>
				</td>
			</tr>
			<tr>
				<td>分佣账户手机号码</td>
				<td><input name="userPhone" type="text" placeholder="请输入11位手机号" class="easyui-validatebox" data-options="validType:'validatePhone'" ></td>
			</tr>
			<tr>
				<td>地址</td>
				<td colspan="3"><input  name="address" style="width: 300px;"/></td>
			</tr>
		</table>
	</form>
</div>
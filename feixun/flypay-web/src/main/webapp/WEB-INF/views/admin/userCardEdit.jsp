<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script type="text/javascript">
	$(function() {
	
		$('#organizationId').combotree({
			url : '${ctx}/organization/tree',
			parentField : 'pid',
			lines : true,
			panelHeight : 'auto',
			value : '${user.organizationId}'
		});
		
		$('#roleIds').combotree({
			url : '${ctx}/role/tree',
			parentField : 'pid',
			lines : true,
			panelHeight : 'auto',
			multiple : true,
			required: true,
			cascadeCheck : false,
			value : $.stringToList('${user.roleIds}')
		});
		
		$('#userEditForm').form({
			url : '${ctx}/user/edit',
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
					parent.$.modalDialog.openner_dataGrid.datagrid('reload');//之所以能在这里调用到parent.$.modalDialog.openner_dataGrid这个对象，是因为user.jsp页面预定义好了
					parent.$.modalDialog.handler.dialog('close');
				} else {
					parent.$.messager.alert('错误', result.msg, 'error');
				}
			}
		});
		$("#userType").val('${user.userType}');
		$("#isAdmin").val('${user.isAdmin}');
		$("#state").val('${user.state}');
	});
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">
		<form id="userEditForm" method="post">
			<div class="light-info" style="overflow: hidden;padding: 3px;">
				<div>密码不修改请留空。</div>
			</div>
			<table class="grid">
				<tr>
					<td>app登录手机</td>
					<td><input name="name" type="text" placeholder="请输入手机号" class="easyui-validatebox"  value="${user.name}"></td>
				</tr>
				<tr>
					<td>用户类型</td>
					<td>
					<select id="userType" name="userType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${userTypeObj}" var="obj">
								<option value="${obj.code}"  <c:if test="${user.userType == obj.code}">selected="selected"</c:if>>${obj.text }</option>
							</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td>登录名</td>
					<td><input name="id" type="hidden"  value="${user.id}">
					<input name="loginName" type="text" placeholder="请输入登录名称" class="easyui-validatebox"  value="${user.loginName}"></td>
				</tr>
				<tr>
					<td>密码</td>
					<td><input type="text" name="password"/></td>
					<td>结算密码</td>
					<td><input type="text" name="stmPsw"  /></td>
				</tr>
				<tr>
					<td>运营商</td>
					<td><select id="organizationId" name="organizationId" style="width: 140px; height: 29px;" class="easyui-validatebox" data-options="required:true"></select></td>
					<td>角色</td>
					<td><input  id="roleIds" name="roleIds" style="width: 140px; height: 29px;"/></td>
				</tr>
				<tr>
					<td>店铺名称</td>
					<td><input name="merchantName" type="text" class="easyui-validatebox"  value="${user.merchantName}"></td>
					<td>店铺所在城市</td>
					<td><input name="merchantCity" type="text"  class="easyui-validatebox"  value="${user.merchantCity}"></td>
				</tr>
				<tr>
				<td>推荐人code</td>
					<td><input name="pid" type="hidden"  value="${user.pid}">
					<input name="agentId" type="hidden"  value="${user.agentId}">
					<input name="pcode" type="text" placeholder="请输入推荐人" class="easyui-validatebox"  value="${user.pcode}"></td>
					<td>管理者</td>
					<td><select name="isAdmin" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0" <c:if test="${user.isAdmin == 0}">selected="selected"</c:if>>否</option>
							<option value="1" <c:if test="${user.isAdmin == 1}">selected="selected"</c:if>>是</option>
					</select></td>
				</tr>
				<tr>
					<td>所在国家</td>
					<td><input name="country" type="text" placeholder="请输入国家名，例如中国" class="easyui-validatebox"  value="${user.country}"></td>
					<td>省份</td>
					<td><input name="province" type="text" placeholder="请输入省，例如山东" class="easyui-validatebox" value="${user.province}"></td>
				</tr>
				<tr>
					<td>用户类型</td>
					<td><select id="state" name="state" value="${user.state}" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0"  <c:if test="${user.state == 0}">selected="selected"</c:if>>正常</option>
							<option value="1"  <c:if test="${user.state == 1}">selected="selected"</c:if>>停用</option>
					</select></td>
				</tr>
								<tr>
					<td>是否认证</td>
					<td><select name="authenticationStatus" value="${user.authenticationStatus}"  class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="-1"  <c:if test="${user.authenticationStatus == -1}">selected="selected"</c:if> >未认证</option>
							<option value="0"  <c:if test="${user.authenticationStatus == 0}">selected="selected"</c:if>>失败</option>
							<option value="1"  <c:if test="${user.authenticationStatus == 1}">selected="selected"</c:if>>已认证</option>
					</select></td>
					<td>是否可结算</td>
					<td><select name="settlementStatus" value="${user.settlementStatus}"  class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0" <c:if test="${user.settlementStatus == 1}">selected="selected"</c:if>> 否</option>
							<option value="1" <c:if test="${user.settlementStatus == 1}">selected="selected"</c:if>>是</option>
					</select></td>
				</tr>
				<tr>
					<td>是否代理商</td>
					<td><select name="isChnl" class="easyui-combobox"  value="${user.isChnl}"  data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0" <c:if test="${user.isChnl == 0}">selected="selected"</c:if>>否</option>
							<option value="1" <c:if test="${user.isChnl == 1}">selected="selected"</c:if>>是</option>
					</select></td>
				</tr>
				<tr>
					<td>认证错误次数</td>
					<td><input name="authErrorNum" type="text" value="${user.authErrorNum}"></td>
					<td>登陆错误次数</td>
					<td><input name="loginErrorNum" type="text" value="${user.loginErrorNum}"></td>
				</tr>
				<tr>
					<td>绑卡错误次数</td>
					<td><input name="authCardErrorNum" type="text" value="${user.authCardErrorNum}"></td>
				</tr>
				
			</table>
		</form>
	</div>
</div>
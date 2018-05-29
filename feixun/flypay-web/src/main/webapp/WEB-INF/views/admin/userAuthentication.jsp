<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
	$(function() {

		$('#userAuthForm').form({
			url : '${ctx}/user/auth',
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
					parent.$.messager.alert('提示', result.msg, 'warning');
				}
			}
		});
	});
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" title=""
		style="padding: 3px;">
		<form id="userAuthForm" method="post">
			<input name="id" type="hidden" value="${user.id}">
			<table class="grid">
				<tr>
					<td>用户姓名</td>
					<td><input type="text" value="${user.realName}"
						readonly="readonly"></td>
					<td>身份证号</td>
					<td><input type="text" value="${user.idNo}"
						readonly="readonly"></td>
				</tr>
				<tr>
					<td>商户名称</td>
					<td><input type="text" value="${user.merchantName}"
						readonly="readonly"></td>
				</tr>
			</table>
			<c:forEach items="${images}" var="obj">
				<img style="width:48%;height:300px;" src="${obj.url}" />
			</c:forEach>
			<table class="grid">
				<tr>
					<td>商家类型</td>
					<td><select id="merchantType" name="merchantType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${merchantTypeObj}" var="obj">
								<option value="${obj.code}"  <c:if test="${user.merchantType == obj.code}">selected="selected"</c:if>>${obj.text }</option>
							</c:forEach>
					</select></td>
							
					<td>认证失败原因</td>
					<td><textarea id="authErrorInfo" name="authErrorInfo"
							placeholder="填写错误原因" style="width: 80%" maxlength="100"
							>${errorInfo}</textarea></td>
				</tr>
			</table>
		</form>
	</div>
</div>
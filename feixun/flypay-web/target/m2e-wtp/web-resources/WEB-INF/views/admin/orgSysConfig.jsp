<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
	$(function() {
		$('#orgSysConfigForm').form({
			url : '${ctx}/organization/orgSysConfigSet',
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
	<form id="orgSysConfigForm" method="post">
		<table class="grid">
			<input name="id" type="hidden" value="${orgSysConfig.id}">
			<tr>
				<td style="width: 80px">短信配置</td>
				<td><textarea id="msgCfg" name="msgCfg"
						style="width: 100%" >${orgSysConfig.msgCfg}</textarea>
						<p>10 注册  &nbsp; &nbsp; &nbsp; 11 忘记密码  </p><p>20 登陆密码 &nbsp; &nbsp; &nbsp;  30 交易密码 </p><p> 40 暂时不用 </p><p> 50 人工认证失败 &nbsp; &nbsp; &nbsp; 60商家认证失败</p>
						</td>
			</tr>
			<tr>
				<td>极光配置</td>
				<td><textarea id="jiguangCfg" name="jiguangCfg"
						style="width: 100%" >${orgSysConfig.jiguangCfg}</textarea></td>
			</tr>
		</table>
	</form>
</div>

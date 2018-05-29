<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script type="text/javascript">
	$(function() {
		$('#organizationChannelForm').form({
			url : '${ctx}/organization/channelFees',
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
	<form id="organizationChannelForm" method="post">
		<table class="grid">
			   <c:forEach var="orgChannel" items="${orgChannels}" >
	               	<input name="id" type="hidden"  value="${orgChannel.id}">
					<input name="version" type="hidden"  value="${orgChannel.version}">
					<input name="orgId" type="hidden"  value="${orgChannel.version}">
					<input name="channelId" type="hidden"  value="${orgChannel.version}">
					<tr>
						<td>通道名称</td>
						<td><input name="channelName" type="text" value="${orgChannel.channelName}" /></td>
						<td>详细名称</td>
						<td><input name="detailName" type="text" value="${orgChannel.detailName}" /></td>
						<td>通道费率</td>
						<td><input name="realRate" type="text" value="${orgChannel.realRate}" class="easyui-validatebox" data-options="required:true" ></td>
					</tr>
			   </c:forEach>
		</table>
	</form>
</div>

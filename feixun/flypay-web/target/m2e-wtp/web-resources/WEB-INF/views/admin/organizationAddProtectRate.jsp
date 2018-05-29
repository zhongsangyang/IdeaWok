<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script type="text/javascript">
	$(function(){
		$('#pid').combotree({
			url : '${ctx}/organization/oemProviderTree',
			parentField : 'pid',
			lines : true,
			panelHeight : 'auto'
		});
		
		$('#organizationAddForm').form({
			url : '${ctx}/organization/addProtectRate',
			onSubmit : function() {		//提交之前的校验
				progressLoad();
				var isValid = $(this).form('validate');
				if (!isValid) {
					progressClose();
				}
				return isValid;
			},
			success: function(result){
				progressClose();
				result = $.parseJSON(result);
				alert(result.msg);
				if(result.success){
					parent.$.modalDialog.openner_treeGrid.treegrid("reload");
					parent.$.modalDialog.handler.dialog("close");
				}
			}
		});
		
	});
</script>
<!-- 添加运营商支付类型保本费率 -->
<div style="padding: 3px;">
	<form id="organizationAddForm" method="post">
		<table class="grid">
			<tr>
				<td>上级运营商</td>
				<td colspan="3"><select id="pid" name="organizationId" style="width:200px;height: 29px;" data-options="required:true"></select>
				<a class="easyui-linkbutton" href="javascript:void(0)" onclick="$('#pid').combotree('clear');" >清空</a></td>
			</tr>
			<tr>
				<td>生效机构类型</td>
				<td colspan="3">
					<select  name="type" style="width:200px;height: 29px;" data-options="required:true">
						<option value = "1">运营商</option>
						<option value = "2">OEM</option>
						<option value = "3">一级代理</option>
						<option value = "4">二级代理</option>
						<option value = "5">三级代理</option>
					</select>
				</td>
			</tr>
			<tr>
				<td>百度支付</td>
				<td><input name="baiduProtectRate" type="text" placeholder="百度支付保本费率" class="easyui-validatebox" data-options="required:true" ></td>
				<td>微信支付</td>
				<td><input name="weixinProtectRate" type="text" placeholder="微信支付保本费率" class="easyui-validatebox" data-options="required:true" ></td>
			</tr>
			<tr>
				<td>QQ支付</td>
				<td><input name="qqProtectRate" type="text" placeholder="QQ支付保本费率" class="easyui-validatebox" data-options="required:true" ></td>
				<td>支付宝</td>
				<td><input name="zhifubaoProtectRate" type="text" placeholder="支付宝保本费率" class="easyui-validatebox" data-options="required:true" ></td>
			</tr>
			<tr>
				<td>京东支付</td>
				<td><input name="jingdongProtectRate" type="text" placeholder="京东支付保本费率" class="easyui-validatebox" data-options="required:true" ></td>
				<td>大额银联</td>
				<td><input name="bigYinlianProtectRate" type="text" placeholder="大额银联保本费率" class="easyui-validatebox" data-options="required:true" ></td>
			</tr>
			<tr>
				<td>银联在线</td>
				<td><input name="yinlianzaixianProtectRate" type="text" placeholder="银联在线保本费率" class="easyui-validatebox" data-options="required:true" ></td>
				<td>银联积分</td>
				<td><input name="yinlianjifenProtectRate" type="text" placeholder="银联积分保本费率" class="easyui-validatebox" data-options="required:true" ></td>
			</tr>
			<tr>
				<td>翼支付</td>
				<td><input name="yizhifuProtectRate" type="text" placeholder="翼支付保本费率" class="easyui-validatebox" data-options="required:true" ></td>
			</tr>
			
			
			
			

		</table>
	</form>
</div>
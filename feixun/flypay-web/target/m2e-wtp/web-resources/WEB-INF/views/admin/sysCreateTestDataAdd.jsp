<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<title>王美凤造数据专用</title>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<jsp:include page="../inc.jsp"></jsp:include>
<script type="text/javascript" charset="utf-8"
	src="${ctx}/uditor/ueditor.config.js"></script>
<script type="text/javascript" charset="utf-8"
	src="${ctx}/uditor/ueditor.all.min.js"> </script>
<script type="text/javascript" charset="utf-8"
	src="${ctx}/uditor/lang/zh-cn/zh-cn.js"></script>
<script type="text/javascript">
	$(function() {
		$('#informationAddForm').form({
			url : '${ctx}/sysCreateTestData/add',
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
					alert(result.msg);
					window.location.href = '${ctx}/sysCreateTestData/manager';
				} else {
					//输出错误信息
					alert(result.msg);
					return;
				}
			}
		});
	});

	/*新增记录*/
	function add() {
		$("#informationAddForm").submit();
	}

	/*返回上一页*/
	function back() {
		window.location.href = '${ctx}/sysCreateTestData/manager';
	}
</script>
</head>
<body>
	<form id="informationAddForm" method="post"
		enctype="multipart/form-data">
		<table class="grid">
			<tr>
				<td>选择你想搞的用户</td>
				<td><select id="user_id" name="user_id" class="easyui-combobox"
					data-options="width:140,height:29,editable:false,panelHeight:'auto'">
						<option value="1">雷安18261542100</option>
						<option value="2">卢强 17321026899</option>
						<option value="10">冯梁13052222696</option>
						<option value="11">欧阳珍 18807069414</option>
						<option value="13">王美凤 18602222264</option>
						<option value="4">孙月  18068089860</option>
						<option value="18">苏容15900348990</option>
				</select></td>
			</tr>
			<tr>
				<td>可提现金额</td>
				<td><input type="text" name=avlBrokerage id="avlBrokerage"
					class="easyui-validatebox" placeholder="请输入"
					data-options="required:true" style="width: 100px"></td>
			</tr>

			<tr>
				<td>总计佣金</td>
				<td><input type="text" name=totalBrokerage id="totalBrokerage"
					class="easyui-validatebox" placeholder="请输入"
					data-options="required:true" style="width: 100px"></td>
			</tr>

			<tr>
				<td>今日佣金</td>
				<td><input type="text" name=todayBrokerage id="todayBrokerage"
					class="easyui-validatebox" placeholder="请输入"
					data-options="required:true" style="width: 100px"></td>
			</tr>

			<tr>
				<td>昨日佣金</td>
				<td><input type="text" name=yesterdayBrokerage
					id="yesterdayBrokerage" class="easyui-validatebox"
					placeholder="请输入" data-options="required:true" style="width: 100px">
				</td>
			</tr>

			<!-- 			<tr> -->
			<!-- 				<td>邀请记录</td> -->
			<!-- 				<td> <input type="text" name=totalPersonNum id="totalPersonNum" class="easyui-validatebox"  style="width: 100px" >  </td> -->
			<!-- 			</tr> -->

			<tr>
				<td>实名认证人数--直接推荐用户</td>
				<td><input type="text" name=zauthTrue_zhijie
					id="zauthTrue_zhijie" class="easyui-validatebox" placeholder="请输入"
					data-options="required:true" style="width: 100px"></td>
			</tr>

			<tr>
				<td>未实名认证人数--直接推荐用户</td>
				<td><input type="text" name=zauthFalse_zhijie
					id="zauthFalse_zhijie" class="easyui-validatebox" placeholder="请输入"
					data-options="required:true" style="width: 100px"></td>
			</tr>

			<tr>
				<td>代理商人数--直接推荐用户</td>
				<td><input type="text" name=dzian_zhijie id="dzian_zhijie"
					class="easyui-validatebox" placeholder="请输入"
					data-options="required:true" style="width: 100px"></td>
			</tr>

			<tr>
				<td>实名认证人数--所有间接推荐用户</td>
				<td><input type="text" name=zauthTrue_suoyou
					id="zauthTrue_suoyou" class="easyui-validatebox" placeholder="请输入"
					data-options="required:true" style="width: 100px"></td>
			</tr>

			<tr>
				<td>未实名认证人数--所有间接推荐用户</td>
				<td><input type="text" name=zauthFalse_suoyou
					id="zauthFalse_suoyou" class="easyui-validatebox" placeholder="请输入"
					data-options="required:true" style="width: 100px"></td>
			</tr>

			<tr>
				<td>代理商--所有间接推荐用户</td>
				<td><input type="text" name=dzian_suoyou id="dzian_suoyou"
					class="easyui-validatebox" placeholder="请输入"
					data-options="required:true" style="width: 100px"></td>
			</tr>


			<tr>
				<td>功能操作</td>
				<td><button type="button" onclick="add()">新增</button>
					<button type="button" onclick="back()">返回</button></td>
			</tr>
		</table>
	</form>
</body>
</html>
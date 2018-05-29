<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<jsp:include page="../inc.jsp"></jsp:include>
<meta http-equiv="X-UA-Compatible" content="edge" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>佣金余额下载</title>
<script type="text/javascript">
	function exportExcelFun() {
	
			var downTime = $("#downTime").val();
			if ("" == downTime || null == downTime) {
				alert("请选择的订单创建日期");
			} else {
				window.location.href = '${ctx}/account/exportExcel?'
						+ $('#searchForm').serialize();
			}
		}

</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false"
		style="height: 180px; overflow: hidden; background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					<td>下载类型</td>
					<td><select id="type" name="type" class="easyui-combobox"
						data-options="width:160,height:29,editable:false,panelHeight:'auto'">
							<option value="account">余额</option>
							<option value="brokerage">佣金</option>
							

					</select></td>
				</tr>

				<th>选择下载日期:</th>
				<td><input id="downTime" name="downTime" placeholder="点击选择时间"
					onclick="WdatePicker({readOnly:true,dateFmt:'yyyyMMdd'})"
					readonly="readonly" /></td>

				<tr>
					<td><a href="javascript:void(0);" class="easyui-linkbutton"
						data-options="iconCls:'icon-download',plain:true"
						onclick="exportExcelFun();">下载</a></td>

				</tr>
			</table>
		</form>
	</div>

</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../inc.jsp"></jsp:include>
<meta http-equiv="X-UA-Compatible" content="edge" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>财务盈利报表</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}/statement/profitDataGrid',
			striped : true,
			rownumbers : true,
			pagination : true,
			singleSelect : true,
			idField : 'id',
			sortName : 'id',
			sortOrder : 'desc',
			pageSize : 15,
			pageList : [ 10, 15, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
			frozenColumns : [ [ {
				width : '150',
				title : '起始日期',
				field : 'statemtentDateStart',
				sortable : true
			}, {
				width : '150',
				title : '结束日期',
				field : 'statemtentDateEnd',
				sortable : true

			} ] ],
			columns : [ [ {
				width : '150',
				title : '历史待提金额',
				field : 'historyUserAmt'
			}, {
				width : '150',
				title : '上一日待提金额',
				field : 'yesterdayUserAmt'
			}, {
				width : '150',
				title : '上一日总输入',
				field : 'totalInputAmt'
			}, {
				width : '150',
				title : '上一日已支出',
				field : 'totalOutAmt'
			}, {
				width : '150',
				title : '误差',
				field : 'errorAmt'
			}, {
				width : '150',
				title : 'T1待提现金额',
				field : 'yesterdayT1Amt'
			}, {
				width : '150',
				title : '上一日冻结金额',
				field : 'yesterdayLockAmt'
			} ] ],
			toolbar : '#toolbar'
		});
	});

	function searchFun() {
		dataGrid.datagrid('load', $.serializeObject($('#searchForm')));
	}
	function cleanFun() {
		$('#searchForm input').val('');
		dataGrid.datagrid('load', {});
	}
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false"
		style="height: 30px; overflow: hidden;background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					<th>处理时间:</th>
					<td><input id="statemtentDateStart" name="statemtentDateStart"
						placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" /> -<input id="statemtentDateEnd"
						name="statemtentDateEnd" placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" /> <a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-search',plain:true"
						onclick="searchFun();">查询</a><a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-clear',plain:true"
						onclick="cleanFun();">清空</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:false,title:'列表：历史+输入-上日待提-支出=0  /输入=用户订单金额+用户流量佣金+用户升级费分润'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
	<div id="toolbar" style="display: none;"></div>
</body>
</html>
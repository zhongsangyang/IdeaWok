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
<title>系统通道收益</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid')
				.datagrid(
						{
							url : '${ctx}/finance/platformChannelTotalProfitGrid',
							striped : true,
							idField : 'id',
							sortName : 'id',
							sortOrder : 'asc',
							pageSize : 15,
							pageList : [ 10, 15, 20, 30, 40, 50, 100, 200, 300,
									400, 500 ],
							frozenColumns : [ [
									{
										width : '80',
										title : '通道名称',
										field : 'orgName',
										formatter : function(value, row, index) {
											var jsonObjs = $
													.parseJSON('${statementType}');
											for (var i = 0; i < jsonObjs.length; i++) {
												var jsonobj = jsonObjs[i];
												if (jsonobj.code == value) {
													return jsonobj.text;
												}
											}
											if (value == '飞付科技') {
												return '<h1><font color=#FF0000 >'
														+ value
														+ ' </font></h1>';
											} else if (value == '代付') {
												return '<h1><font color=#228B22 >'
														+ value
														+ ' </font></h1>';
											} else {
												return value;
											}
										}
									},
									{
										width : '80',
										title : '<font color=#FF0000 >收益</font>',
										field : 'sysProfit',
										formatter : function(value, row, index) {
											if (row.orgName == '飞付科技') {
												return '<h1><font color=#FF0000 >'
														+ value
														+ ' </font></h1>';
											}
											return value;
										}
									} ] ],
							columns : [ [
									{
										width : '120',
										title : '<font color=#FF0000 >交易金额</font>',
										field : 'tradeAmt',
										formatter : function(value, row, index) {
											if (row.orgName == '飞付科技') {
												return value
														+ '<font color=#FF0000 >(收单)</font>';
											}
											return value;
										}
									},
									{
										width : '100',
										title : '用户手续费',
										field : 'userFee'
									},
									{
										width : '150',
										title : '实际手续费=T0+T5+升级',
										field : 'realFee',
										formatter : function(value, row, index) {
											return '<font color=#006400 >'
													+ value + '</font>';
										}
									},
									{
										width : '100',
										title : 'T0手续费成本',
										field : 't0TradeFee',
										formatter : function(value, row, index) {
											if (row.orgName == '代付') {
												return '';
											} else {
												return '<font color=#228B22 >'
														+ value + '</font>';
											}
										}
									},
									{
										width : '100',
										title : 'T5手续费成本',
										field : 't5TradeFee',
										formatter : function(value, row, index) {
											if (row.orgName == '代付') {
												return '';
											} else {
												return '<font color=#228B22 >'
														+ value + '</font>';
											}
										}
									},
									{
										width : '100',
										title : '升级费流量成本',
										field : 'agentTradeFee',
										formatter : function(value, row, index) {
											if (row.orgName == '代付') {
												return '';
											} else {
												return '<font color=#228B22 >'
														+ value + '</font>';
											}
										}
									},
									{
										width : '100',
										title : '<font color=#FF0000 >通道返佣</font>',
										field : 'commissionTradeFee',
										formatter : function(value, row, index) {
											if (row.orgName == '飞付科技') {
												return '<font color=#FF0000 >'
														+ value + '</font>';
											} else {
												return value;
											}
										}
									},
									{
										width : '100',
										title : '<font color=#006400 >短信成本</font>',
										field : 'msgProfit',
										formatter : function(value, row, index) {
											if (row.orgName == '飞付科技') {
												return '<font color=#228B22 >'
														+ value + '</font>';
											} else {
												return '';
											}
										}
									},
									{
										width : '100',
										title : '<font color=#006400 >自动认证成本</font>',
										field : 'authProfit',
										formatter : function(value, row, index) {
											if (row.orgName == '飞付科技') {
												return '<font color=#006400 >'
														+ value + '</font>';
											} else {
												return '';
											}
										}
									}, {
										width : '120',
										title : '起始日期',
										field : 'statemtentDateStart',
										sortable : true
									}, {
										width : '120',
										title : '结束日期',
										field : 'statemtentDateEnd',
										sortable : true

									} ] ]
						});
	});

	function searchFun() {
		dataGrid.datagrid('load', $.serializeObject($('#searchForm')));
	}
	function cleanFun() {
		$('#searchForm input').val('');
		dataGrid.datagrid('load', {});
	}
	function exportExcelFun() {
		window.location.href = '${ctx}/finance/exportExcel?'
				+ $('#searchForm').serialize();
	}
	function exportAgentExcelFun() {
		window.location.href = '${ctx}/finance/exportAgentProfitExcel?'
				+ $('#searchForm').serialize();
	}
	
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false"
		style="height: 30px; overflow: hidden;background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					<th>对账日期:</th>
					<td><input id="statemtentDateStart" name="statemtentDateStart"
						placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" />至 <input id="statemtentDateEnd"
						name="statemtentDateEnd" placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" /> <a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-clear',plain:true"
						onclick="cleanFun();">清空</a> <a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-download',plain:true"
					onclick="exportExcelFun();">下载</a>
					<a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-download',plain:true"
					onclick="exportAgentExcelFun();">下载代理商</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:false,title:'通道收益列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
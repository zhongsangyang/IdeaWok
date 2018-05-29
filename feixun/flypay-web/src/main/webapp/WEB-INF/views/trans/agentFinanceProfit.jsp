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
<title>代理商收益信息</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid').datagrid(
				{
					url : '${ctx}/finance/orgProfitDataGrid',
					striped : true,
					rownumbers : true,
					pagination : true,
					singleSelect : true,
					idField : 'id',
					sortName : 'id',
					sortOrder : 'asc',
					pageSize : 15,
					pageList : [ 10, 15, 20, 30, 40, 50 ],
					frozenColumns : [ [
							{
								width : '100',
								title : '代理商名称',
								field : 'organizationName'
							},
							{
								width : '80',
								title : '<font color=#FF0000 >总收益</font>',
								field : 'sysProfit',
								formatter : function(value, row, index) {
									return '<h1><font color=#FF0000 >' + value
											+ '</font></h1>';
								}
							} ] ],
					columns : [ [ {
						width : '80',
						title : '<font color=#FF0000 >分享收益</font>',
						field : 'brokerageProfit',
						formatter : function(value, row, index) {
							return '<font color=#FF0000 >' + value + '</font>';
						}

					}, {
						width : '100',
						title : '分享剩余收益',
						field : 'brokerageAmt'
					}, {
						width : '100',
						title : '用户升级收益',
						field : 'brokerageAgent'
					}, {
						width : '80',
						title : '<font color=#FF0000 >通道利润</font>',
						field : 'tradeFeeProfit',
						formatter : function(value, row, index) {
							return '<font color=#FF0000 >' + value + '</font>';
						}
					}, {
						width : '100',
						title : 'T0刷卡利润',
						field : 'tradeT0FeeAmt'
					}, {
						width : '100',
						title : '用户升级成本',
						field : 'tradeAgentFeeAmt'
					}, {
						width : '100',
						title : '<font color=#FF0000 >提现利润</font>',
						field : 'tixianProfit',
						formatter : function(value, row, index) {
							return '<font color=#FF0000 >' + value + '</font>';
						}
					}, {
						width : '100',
						title : '提现手续费',
						field : 'tixianT0Profit'
					}, {
						width : '100',
						title : '提现成本',
						field : 'tixianFee'
					}, {
						width : '100',
						title : '<font color=#228B22 >T0垫资成本</font>',
						field : 'tixianAmtFee',
						formatter : function(value, row, index) {
							return '<font color=#228B22 >' + value + '</font>';
						}
					}, {
						width : '100',
						title : '<font color=#228B22 >认证成本</font>',
						field : 'authFee',
						formatter : function(value, row, index) {
							return '<font color=#228B22 >' + value + '</font>';
						}
					}, {
						width : '100',
						title : '<font color=#228B22 >短信成本</font>',
						field : 'msgFee',
						formatter : function(value, row, index) {
							return '<font color=#228B22 >' + value + '</font>';
						}
					}, {
						width : '100',
						title : '金牌个数',
						field : 'globNum'
					}, {
						width : '100',
						title : '金生钻个数',
						field : 'globToDiamondNum'
					}, {
						width : '100',
						title : '钻石个数',
						field : 'diamondNum'
					}, {
						width : '120',
						title : '起始日期',
						field : 'statemtentDateStart'
					}, {
						width : '120',
						title : '结束日期',
						field : 'statemtentDateEnd'
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
					<th>对账日期:</th>
					<td><input id="statemtentDateStart" name="statemtentDateStart"
						placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" />至 <input id="statemtentDateEnd"
						name="statemtentDateEnd" placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" /> <a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon_search',plain:true"
						onclick="searchFun();">查询</a><a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon_cancel',plain:true"
						onclick="cleanFun();">清空</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:false,title:'利润列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
	<div id="toolbar" style="display: none;"></div>
</body>
</html>
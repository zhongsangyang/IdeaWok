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
<title>财务对账单</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}/statement/financeDataGrid',
			striped : true,
			rownumbers : true,
			pagination : true,
			singleSelect : true,
			idField : 'id',
			sortName : 'id',
			sortOrder : 'asc',
			pageSize : 15,
			pageList : [ 10, 15, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
			frozenColumns : [ [ {
				width : '150',
				title : '对账起始日期',
				field : 'statemtentDateStart',
				sortable : true
			}, {
				width : '150',
				title : '对账结束日期',
				field : 'statemtentDateEnd',
				sortable : true
			}, {
				width : '100',
				title : '类型',
				field : 'stsType',
				formatter : function(value, row, index) {
					var jsonObjs = $.parseJSON('${orderType}');
					for (var i = 0; i < jsonObjs.length; i++) {
						var jsonobj = jsonObjs[i];
						if (jsonobj.code == value) {
							return jsonobj.text;
						}
					}
					return value;
				}

			} ] ],
			columns : [ [ {
				width : '100',
				title : '到账类型',
				field : 'payType',
				formatter : function(value, row, index) {
					switch (value) {
					case 0:
						return '即时';
					case 1:
						return 'T1';
					}
					return "上一日总交易";
				}

			}, {
				width : '100',
				title : '系统交易金额',
				field : 'tradeAmt'
			}, {
				width : '100',
				title : '收费金额',
				field : 'feeAmt'
			}, {
				width : '100',
				title : '实收金额',
				field : 'realInputAmt'
			}, {
				width : '100',
				title : '返佣金额',
				field : 'brokerageAmt'
			}, {
				width : '100',
				title : '系统交易笔数',
				field : 'tradeNum'
			}, {
				width : '100',
				title : '流水笔数',
				field : 'amtTradeNum'
			}, {
				width : '100',
				title : '升级费笔数',
				field : 'agentTradeNum'
			}

			] ],
			toolbar : '#toolbar'
		});
	});

	function searchFun() {
		var statemtentDate = $("#statemtentDate").val();
		if (statemtentDate && statemtentDate != '') {
			dataGrid.datagrid('load', $.serializeObject($('#searchForm')));
		} else {
			alert("请您输入对账日期");
		}
	}
	function cleanFun() {
		$('#searchForm input').val('');
		dataGrid.datagrid('load', {});
	}
	function dowoan() {
		if($('#statemtentDate').val()==""){
			alert("请选择时间");
			return false;
		}
		window.location.href = '${ctx}/statement/exportMinShendowoan?statemtentDate='+$('#statemtentDate').val();
	}
	function dailyProfit() {
		if($('#statemtentDate').val()==""){
			alert("请选择时间");
			return false;
		}
		window.location.href = '${ctx}/statement/exportDailyProfitDowoan?statemtentDate='+$('#statemtentDate').val();
	}
	function dailyProfitDetail() {
		if($('#statemtentDate').val()==""){
			alert("请选择时间");
			return false;
		}
		window.location.href = '${ctx}/statement/dailyProfitDetaildowoan?statemtentDate='+$('#statemtentDate').val();
	}
	function ShenFudowoan() {
		if($('#statemtentDate').val()==""){
			alert("请选择时间");
			return false;
		}
		window.location.href = '${ctx}/statement/exportShenFudowoan?statemtentDate='+$('#statemtentDate').val();
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
					<td><input id="statemtentDate" name="statemtentDate"
						placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" /> <a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-search',plain:true"
						onclick="searchFun();">查询</a>
						
						<a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-download',plain:true"
						onclick="dowoan();">民生对账单下载</a>
						
						<a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-download',plain:true"
						onclick="ShenFudowoan();">申付账单下载</a>
						
						<a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-download',plain:true"
						onclick="dailyProfit();">每日盈利下载</a>
						
						<a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-download',plain:true"
						onclick="dailyProfitDetail();">盈利明细下载</a>
						
						
						<a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-clear',plain:true"
						onclick="cleanFun();">清空</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:false,title:'对账单日志列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
	<div id="toolbar" style="display: none;"></div>
</body>
</html>
、<%@ page language="java" contentType="text/html; charset=UTF-8"
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
<title>实时总账户</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}/finance/financeAccountDataGrid',
			striped : true,
			rownumbers : true,
			pagination : true,
			singleSelect : true,
			pageSize : 15,
			pageList : [ 10, 15, 20, 30, 40, 50, 100 ],
			frozenColumns : [ [ {
				width : '120',
				title : '可用金额',
				field : 'avlAmt'
			} ] ],
			columns : [ [ {
				field : 'action',
				width : '150',
				title : 'T10入账总金额',
				formatter : function(value, row, index) {
					return row.t1Amt + row.t2Amt + row.t3Amt + row.t4Amt + row.t5Amt + row.t6Amt + row.t7Amt + row.t8Amt + row.t9Amt + row.t10Amt + row.t11Amt + row.d1Amt + row.d2Amt;
				}
			}, {
				width : '120',
				title : 'D1入账金额',
				field : 'd1Amt'
			}, {
				width : '120',
				title : 'D2入账金额',
				field : 'd2Amt'
			},  {
				width : '120',
				title : 'T1入账金额',
				field : 't1Amt'
			}, {
				width : '120',
				title : 'T2入账金额',
				field : 't2Amt'
			}, {
				width : '120',
				title : 'T3入账金额',
				field : 't3Amt'
			}, {
				width : '120',
				title : 'T4入账金额',
				field : 't4Amt'
			}, {
				width : '120',
				title : 'T5入账金额',
				field : 't5Amt'
			}, {
				width : '120',
				title : 'T6入账金额',
				field : 't6Amt'
			}, {
				width : '120',
				title : 'T7入账金额',
				field : 't7Amt'
			},  {
				width : '120',
				title : 'T8入账金额',
				field : 't8Amt'
			},  {
				width : '120',
				title : 'T9入账金额',
				field : 't9Amt'
			},  {
				width : '120',
				title : 'T10入账金额',
				field : 't10Amt'
			},  {
				width : '120',
				title : 'T11入账金额',
				field : 't11Amt'
			},  {
				width : '120',
				title : '待支付锁定金额',
				field : 'lockOutAmt'
			} ] ]
		});
	});
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false,title:'实时总账户'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
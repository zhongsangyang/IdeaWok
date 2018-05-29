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
<title>T1入可用金额操作</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}/coreOperate/dataGrid',
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
				width : '120',
				title : '日期',
				field : 'dateTime',
				sortable : true
			} ] ],
			columns : [ [ {
				width : '120',
				title : 'T1入可用金额标志',
				field : 't1ToAvlFlag',
				sortable : true,
				formatter : function(value, row, index) {
					switch (value) {
					case 0:
						return '未进行';
					case 1:
						return '已完成';
					}
				}
			}, {
				width : '120',
				title : '操作人',
				field : 'creator',
				sortable : true
			}, {
				width : '150',
				title : '操作时间',
				field : 'createDate',
				sortable : true
			} ] ],
			toolbar : '#toolbar'
		});
	});
	function stepFun() {
		parent.$.messager.confirm('询问', '您确认平台各通道资金已经到位，同意步进用户的T5资金吗？',
				function(b) {
					if (b) {
						progressLoad();
						$.post('${ctx}/coreOperate/sendT5step', {
							id : 1
						}, function(result) {
							if (result.success) {
								dataGrid.datagrid('reload');
							}
							parent.$.messager.alert('提示', result.msg, 'info');
							progressClose();
						}, 'JSON');
					}
				});
	}
	
	function stepFunTwo() {
		parent.$.messager.confirm('询问', '您确认平台各通道资金已经到位，同意步进用户的D1资金吗？',
				function(b) {
					if (b) {
						progressLoad();
						$.post('${ctx}/coreOperate/sendD1step', {
							id : 1
						}, function(result) {
							if (result.success) {
								dataGrid.datagrid('reload');
							}
							parent.$.messager.alert('提示', result.msg, 'info');
							progressClose();
						}, 'JSON');
					}
				});
	}
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false,title:'T1入可用金额列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
	<div id="toolbar" style="display: none;">
		<a onclick="stepFun();" href="javascript:void(0);"
			class="easyui-linkbutton"
			data-options="plain:true,iconCls:'icon_add'">T1入可用金额(${T1Amt})</a>
	   <a onclick="stepFunTwo();" href="javascript:void(0);"
			class="easyui-linkbutton"
			data-options="plain:true,iconCls:'icon_add'">D1入可用金额(${D1Amt})</a>
	</div>
</body>
</html>
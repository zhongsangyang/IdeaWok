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
<title>签约通道管理</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid')
				.datagrid(
						{
							url : '${ctx}/serviceMerchant/dataGrid',
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
										width : '120',
										title : '名称',
										field : 'name',
										formatter : function(value, row, index) {
											var jsonObjs = $
													.parseJSON('${statementType}');
											for (var i = 0; i < jsonObjs.length; i++) {
												var jsonobj = jsonObjs[i];
												if (jsonobj.code == value) {
													return jsonobj.text;
												}
											}
											return "未知类型";

										}
									}, {
										width : '120',
										title : '详细名称',
										field : 'detailName'
									} ] ],
							columns : [ [
									{
										width : '150',
										title : '账户',
										field : 'appId'
									},
									{
										width : '100',
										title : '类型',
										field : 'type',
										sortable : true,
										formatter : function(value, row, index) {
											if (value == '999') {
												return '综合';
											} else if (value == '200') {
												return '支付宝';
											} else if (value == '300') {
												return '微信';
											} else {
												return '否';
											}
										}
									},
									{
										width : '120',
										title : '是否可生成子商户',
										field : 'status',
										sortable : true,
										formatter : function(value, row, index) {
											if (value == '0') {
												return '否';
											} else {
												return '是';
											}
										}
									},
									{
										width : '300',
										title : '配置',
										field : 'config'
									},
									{
										field : 'action',
										title : '操作',
										width : 120,
										formatter : function(value, row, index) {
											var str = $
													.formatString(
															'<a href="javascript:void(0)" onclick="editFun(\'{0}\');" >设置</a>',
															row.id);
											return str;
										}
									} ] ],
							toolbar : '#toolbar'
						});
	});
	function editFun(id) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		$.post('${ctx}/serviceMerchant/edit', {
			id : id
		}, function(result) {
			if (result.success) {
				parent.$.messager.alert('提示', result.msg, 'info');
				dataGrid.datagrid('reload');
			}
			progressClose();
		}, 'JSON');
	}
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false,title:'服务商列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
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
<title>用户异常订单处理</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid')
				.datagrid(
						{
							url : '${ctx}/userExceptionOrder/dataGrid',
							striped : true,
							rownumbers : true,
							pagination : true,
							singleSelect : true,
							idField : 'id',
							sortName : 'id',
							sortOrder : 'asc',
							pageSize : 15,
							pageList : [ 10, 15, 20, 30, 40, 50, 100, 200, 300,
									400, 500 ],
							frozenColumns : [ [ {
								width : '100',
								title : '用户登录名',
								field : 'userPhone',
								sortable : true
							}, {
								width : '100',
								title : '用户名称',
								field : 'userName',
								sortable : true
							}, {
								width : '150',
								title : '订单编号',
								field : 'orderNum',
								sortable : true
							} ] ],
							columns : [ [
									{
										width : '120',
										title : '订单类型',
										field : 'type',
										sortable : true,
										formatter : function(value, row, index) {
											var jsonObjs = $
													.parseJSON('${transType}');
											for (var i = 0; i < jsonObjs.length; i++) {
												var jsonobj = jsonObjs[i];
												if (jsonobj.code == value) {
													return jsonobj.text;
												}
											}
											return "未知类型";
										}
									},
									{
										width : '120',
										title : '金额',
										field : 'amt',
										sortable : true
									},
									{
										width : '120',
										title : '查询次数',
										field : 'scanNum',
										sortable : true
									}, {
										width : '320',
										title : '描述',
										field : 'description'
									},	{
										width : '80',
										title : '订单状态',
										field : 'status',
										sortable : true,
										formatter : function(value, row, index) {
											var jsonObjs = $
													.parseJSON('${orderStatus}');
											for (var i = 0; i < jsonObjs.length; i++) {
												var jsonobj = jsonObjs[i];
												if (jsonobj.code == value) {
													return jsonobj.text;
												}
											}
											return "未知类型";
										}
									}/* ,
									{
										field : 'action',
										title : '操作',
										width : 100,
										formatter : function(value, row, index) {
											var str = $
													.formatString(
															'<a href="javascript:void(0)" onclick="resentSearch(\'{0}\');" >重新查询</a>',
															row.id);
											return str;
										}
									} */ ] ],
							toolbar : '#toolbar'
						});
	});

	function searchFun() {
		dataGrid.datagrid('load', $.serializeObject($('#searchForm')));
	}
	function resentSearch(id) {
		if (id == undefined) {//点击右键菜单才会触发这个
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {//点击操作里面的删除图标会触发这个
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}

		$.post('${ctx}/userExceptionOrder/resentOrderToPingan', {
			id : id
		}, function(result) {
			if (result.success) {
				parent.$.messager.alert('提示', result.msg, 'info');
				dataGrid.datagrid('reload');
			}
			progressClose();
		}, 'JSON');
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
					<th>订单号:</th>
					<td><input name="orderNum" /></td>
					<th>用户编码:</th>
					<td><input name="userPhone" class="easyui-validatebox"
						maxlength="11" /> <a href="javascript:void(0);"
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
	<div data-options="region:'center',border:false,title:'订单列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
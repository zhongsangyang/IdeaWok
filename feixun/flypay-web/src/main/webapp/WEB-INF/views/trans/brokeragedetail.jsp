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
<title>佣金明细</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}/brokerageDetail/dataGrid',
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
				title : '手机号码',
				field : 'phone',
				sortable : true
			}, {
				width : '120',
				title : '用户编码',
				field : 'userCode',
				sortable : true
			}, {
				width : '120',
				title : '用户昵称',
				field : 'userName',
				sortable : true
			} ] ],
			columns : [ [ {
				width : '80',
				title : '交易类型',
				field : 'transType',
				sortable : true,
				formatter : function(value, row, index) {
					var jsonObjs = $.parseJSON('${transType}');
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
				title : '交易金额',
				field : 'amt',
				sortable : true
			}, {
				width : '120',
				title : '交易时间',
				field : 'transDatetime'
			}, {
				width : '120',
				title : '获利者昵称',
				field : 'brokerageUserName'
			}, {
				width : '120',
				title : '获利者编号',
				field : 'brokerageUserCode'
			}, {
				width : '120',
				title : '获利者级别',
				field : 'brokerageUserType'
			}, {
				width : '120',
				title : '所获佣金',
				field : 'brokerage'
			}, {
				width : '200',
				title : '运营商名称',
				field : 'organizationName',
				sortable : true
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
					<th>手机号码:</th>
					<td><input name="phone" /></td>
					<th>获利者手机号:</th>
					<td><input name="brokerageUserCode" class="easyui-validatebox" />
						<a href="javascript:void(0);"
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
	<div data-options="region:'center',border:false,title:'参数列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
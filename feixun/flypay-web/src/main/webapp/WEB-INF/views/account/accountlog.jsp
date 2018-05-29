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
<title>账户交易历史</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}/accountlog/dataGrid',
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
				field : 'loginName',
				sortable : true
			}, {
				width : '120',
				title : '用户名',
				field : 'realName',
				sortable : true
			}, {
				width : '120',
				title : '代理商',
				field : 'organizationName'
			} ] ],
			columns : [ [ {
				width : '80',
				title : '记账类型',
				field : 'type',
				sortable : true,
				formatter : function(value, row, index) {
					if (value == 'C' || value == 'T' || value == 'R' || value == 'B') {
						return '减少';
					} else {
						return '增加';
					}
				}
			}, {
				width : '120',
				title : '变动金额',
				field : 'amt',
				sortable : true
			}, {
				width : '120',
				title : '可用余额',
				field : 'avlAmt',
				sortable : true
			}, {
				width : '120',
				title : '锁定金额',
				field : 'lockOutAmt',
				sortable : true
			}, {
				width : '120',
				title : '记账时间',
				field : 'createTime'
			}, {
				width : '350',
				title : '描述',
				field : 'description'
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
					<th>App登录名:</th>
					<td><input name="loginName" /> <a href="javascript:void(0);"
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
	<div data-options="region:'center',border:false,title:'账户历史列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
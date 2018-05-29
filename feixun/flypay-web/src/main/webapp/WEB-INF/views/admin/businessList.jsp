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
<title>商务合作</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}/business/dataGrid',
			striped : true,
			rownumbers : true,
			pagination : true,
			singleSelect : true,
			idField : 'id',
			sortName : 'id',
			sortOrder : 'desc',
			pageSize : 15,
			pageList : [ 10, 15, 20, 30, 40, 50, 100 ],
			frozenColumns : [ [ 
			{
				width : '150',
				title : '运营商名称',
				field : 'organizationName'
			} ,  {
				width : '120',
				title : '联系人',
				field : 'contactor'
			} ,  {
				width : '120',
				title : '联系人电话',
				field : 'contactPhone'
			}] ],
			columns : [ [ {
				width : '80',
				title : '商务类型',
				field : 'busType',
				sortable : true,
				formatter : function(value, row, index) {
					var jsonObjs = $.parseJSON('${transType}');
					for (var i = 0; i < jsonObjs.length; i++) {
						if (value == 1) {
							return '代理';
						}else if(value == 2){
							return 'OEM';
						}
					}
					return "未知类型";
				}
			}, {
				width : '120',
				title : '网址',
				field : 'companyNet',
				sortable : true
			},{
				width : '120',
				title : '创建时间',
				field : 'createTime',
				sortable : true
			}, {
				width : '320',
				title : '项目描述',
				field : 'busDesc'
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
					<th>联系人电话:</th>
					<td><input name="contactPhone" />
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
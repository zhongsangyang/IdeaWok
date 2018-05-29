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
<title>用户积分</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		$('#organizationId').combotree({
			url : '${ctx}/organization/treeByOperator',
			parentField : 'pid',
			lines : true,
			panelWidth : 'auto',
			panelHeight : 256,
			value : '${user.organizationId}'
		});
		dataGrid = $('#dataGrid').datagrid(
				{
					url : '${ctx}/point/dataGrid',
					striped : true,
					rownumbers : true,
					pagination : true,
					singleSelect : true,
					idField : 'id',
					sortName : 'id',
					sortOrder : 'asc',
					pageSize : 15,
					pageList : [ 10, 15, 20, 30, 40, 50, 100, 200, 300, 400,
							500 ],
					frozenColumns : [ [ {
						width : '120',
						title : '真实名字',
						field : 'realName',
						sortable : true
					}, {
						width : '120',
						title : '手机号',
						field : 'loginName',
						sortable : true
					} ] ],
					columns : [ [
							{
								width : '80',
								title : '当前积分',
								field : 'point',
								sortable : true
							}, {
								width : '120',
								title : '认证1级下线人数',
								field : 'subPersonNum',
								sortable : true
							},
							{
								width : '80',
								title : '账户状态',
								field : 'status',
								sortable : true,
								formatter : function(value, row, index) {
									switch (value) {
									case 0:
										return '正常';
									case 1:
										return '冻结';
									}
								}
							
							}, {
								width : '120',
								title : '代理商',
								field : 'organizationName',
								sortable : true
							},
							{
								field : 'action',
								title : '操作',
								width : 100,
								formatter : function(value, row, index) {
									var t = '冻结';
									if (row.status == 1) {
										t = '解冻';
									}

									var str = $.formatString(
											'<a href="javascript:void(0)" onclick="freezeFun(\'{0}\');" >'
													+ t + '</a>', row.id);
									return str;
								}
							} ] ],
					toolbar : '#toolbar'
				});
	});
	function freezeFun(id) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		$.post('${ctx}/point/freeze', {
			id : id
		}, function(result) {
			if (result.success) {
				dataGrid.datagrid('reload');
			}
			progressClose();
		}, 'JSON');
	}
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
					<th>APP登录名:</th>
					<td><input name="loginName" /></td>
					<th>运营商:</th>
					<td><select id="organizationId" name="organizationId"
						style="width: 140px; height: 29px;" class="easyui-validatebox"></select></td>
					<th>状态:</th>
					<td><select id="status" name="status" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<option value="1">冻结</option>
							<option value="0">正常</option>
					</select> <a href="javascript:void(0);" class="easyui-linkbutton"
						data-options="iconCls:'icon-search',plain:true"
						onclick="searchFun();">查询</a><a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-clear',plain:true"
						onclick="cleanFun();">清空</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:false,title:'账户列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
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
<title>用户佣金</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}/brokerage/dataGrid',
			striped : true,
			rownumbers : true,
			pagination : true,
			singleSelect : true,
			idField : 'id',
			sortName : 'totalBrokerage',
			sortOrder : 'desc',
			pageSize : 15,
			pageList : [ 10, 15, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
			frozenColumns : [ [ {
				width : '120',
				title : 'APP登录名',
				field : 'loginName',
				sortable : true
			}, {
				width : '120',
				title : '姓名',
				field : 'userName',
				sortable : true
			} ] ],
			columns : [ [ {
				width : '120',
				title : '现有佣金',
				field : 'brokerage',
				sortable : true
			}, {
				width : '120',
				title : '升级总佣金',
				field : 'totalAgentBrokerage',
				sortable : true
			}, {
				width : '120',
				title : '交易总佣金',
				field : 'totalTransBrokerage',
				sortable : true
			}, {
				width : '120',
				title : '总佣金',
				field : 'totalBrokerage',
				sortable : true
			}, {
				width : '200',
				title : '运营商名称',
				field : 'organizationName',
				sortable : true
			}, {
				width : '80',
				title : '账户状态',
				field : 'status',
				sortable : true,
				formatter : function(value, row, index) {
					if (value != '0') {
						return '冻结';
					} else {
						return '正常';
					}
				}
			}, {
				field : 'action',
				title : '操作',
				width : 100,
				formatter : function(value, row, index) {
					var t = '冻结';
					if (row.status == 1) {
						t = '解冻';
					}

					var str = $.formatString(
						'<a href="javascript:void(0)" onclick="freezeFun(\'{0}\');" >' + t + '</a>', row.id);
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
		$.post('${ctx}/brokerage/freeze', {
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
	function exportExcelFun() {
		window.location.href = '${ctx}/brokerage/exportExcel';
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
					<td><input name="loginName" class="easyui-validatebox"
						maxlength="11" /></td>
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
						onclick="cleanFun();">清空</a> 
						<!-- <a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-download',plain:true"
						onclick="exportExcelFun();">下载</a> -->
						</td>
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:false,title:'佣金列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
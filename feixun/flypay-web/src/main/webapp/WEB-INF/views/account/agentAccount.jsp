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
<title>商户账户</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid')
				.datagrid(
						{
							url : '${ctx}/account/dataGrid',
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
								width : '120',
								title : '真实名字',
								field : 'realName',
								sortable : true
							}, {
								width : '120',
								title : '手机号',
								field : 'loginName',
								sortable : true
							}, {
								width : '120',
								title : '代理商',
								field : 'organizationName',
								sortable : true
							} ] ],
							columns : [ [
									{
										width : '120',
										title : '可用金额',
										field : 'avlAmt',
										sortable : true
									},
									{
										width : '120',
										title : '月入金额',
										field : 'perMonthInAmt',
										sortable : true
									},
									{
										width : '120',
										title : '月出金额',
										field : 'perMonthOutAmt',
										sortable : true
									},
									{
										width : '150',
										title : '待支付锁定金额',
										field : 'lockOutAmt',
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
											case 100:
												return '跨平台冻结';
											}
										}
									},
									{
										field : 'action',
										title : '操作',
										width : 100,
										formatter : function(value, row, index) {
											var str = '';
// 											var t = '冻结';
// 											if (row.status == 1) {
// 												t = '解冻';
// 											}
											if (row.status == 0
													|| row.status == 1) {

// 												str = $.formatString(
// 														'<a href="javascript:void(0)" onclick="freezeFun(\'{0}\');" >'
// 																+ t + '</a>',
// 														row.id);
// 												str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
											}
											str += $
													.formatString(
															'<a href="javascript:void(0)" onclick="viewRemarkFun(\'{0}\');" >备注</a>',
															row.id);
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
		$.post('${ctx}/account/agentFreeze', {
			id : id
		}, function(result) {
			if (result.success) {
				dataGrid.datagrid('reload');
			}
			progressClose();
		}, 'JSON');
	}
	function viewRemarkFun(id) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$.modalDialog({
			title : '备注',
			width : 400,
			height : 300,
			href : '${ctx}/account/viewRemarktPage?id=' + id,
			buttons : [ {
				text : '确定',
				handler : function() {
					var f = parent.$.modalDialog.handler
							.find('#accountRemarkForm');
					f.submit();
				}
			} ]
		});
	}
	function searchFun() {
		dataGrid.datagrid('load', $.serializeObject($('#searchForm')));
	}
	function cleanFun() {
		$('#searchForm input').val('');
		dataGrid.datagrid('load', {});
	}
	function exportExcelFun() {
		window.location.href = '${ctx}/account/exportExcel';
	}
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false"
		style="height: 30px; overflow: hidden; background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					<th>APP登录名:</th>
					<td><input name="loginName" /></td>
					<th>状态:</th>
					<td><select id="status" name="status" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<option value="1">冻结</option>
							<option value="0">正常</option>
							<option value="100">跨平台冻结</option>
					</select>
					<th>选择年月:</th>
					<td><input name="dateForYM" placeholder="点击选择年月"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM'})"
						readonly="readonly" /> <a href="javascript:void(0);"
						class="easyui-linkbutton"
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
	<div data-options="region:'center',border:false,title:'账户列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
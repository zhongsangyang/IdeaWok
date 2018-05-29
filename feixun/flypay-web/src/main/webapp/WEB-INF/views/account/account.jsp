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
<c:if test="${fn:contains(sessionInfo.resourceList, '/account/freeze')}">
	<script type="text/javascript">
		$.canFreeze = true;
	</script>
</c:if>
<c:if
	test="${fn:contains(sessionInfo.resourceList, '/account/adjustAccount')}">
	<script type="text/javascript">
		$.canAdjustAccount = true;
	</script>
</c:if>
<c:if
	test="${fn:contains(sessionInfo.resourceList, '/account/accountRemark')}">
	<script type="text/javascript">
		$.canAccountRemark = true;
	</script>
</c:if>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>商户账户</title>
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
										width : '80',
										title : '可用金额',
										field : 'avlAmt',
										sortable : true
									},
									{
										width : '80',
										title : '月入金额',
										field : 'perMonthInAmt',
										sortable : true
									},
									{
										width : '80',
										title : '月出金额',
										field : 'perMonthOutAmt',
										sortable : true
									},
									{
										width : '80',
										title : 'T1入账金额',
										field : 't1Amt'
									},
									{
										width : '80',
										title : 'T2入账金额',
										field : 't2Amt'
									},
									{
										width : '80',
										title : 'T3入账金额',
										field : 't3Amt'
									},
									{
										width : '80',
										title : 'T4入账金额',
										field : 't4Amt'
									},
									{
										width : '80',
										title : 'T5入账金额',
										field : 't5Amt'
									},
									{
										width : '80',
										title : 'T6入账金额',
										field : 't6Amt'
									},
									{
										width : '80',
										title : 'T7入账金额',
										field : 't7Amt'
									},
									{
										width : '80',
										title : 'T8入账金额',
										field : 't8Amt'
									},
									{
										width : '80',
										title : 'T9入账金额',
										field : 't9Amt'
									},
									{
										width : '80',
										title : 'T10入账金额',
										field : 't10Amt'
									},
									{
										width : '80',
										title : 'T11入账金额',
										field : 't11Amt'
									},
									{
										width : '80',
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
										width : 120,
										formatter : function(value, row, index) {
											var t = '冻结';
											if (row.status == 100
													|| row.status == 1) {
												t = '解冻'
											}
											var str = '';
											if ($.canFreeze) {
												str += $.formatString(
														'<a href="javascript:void(0)" onclick="freezePageFun(\'{0}\');" >'
																+ t + '</a>',
														row.id);
											}
<%--
											if ($.canAdjustAccount) {
												if ($.canFreeze) {
													str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
												}
												str += $
														.formatString(
																'<a href="javascript:void(0)" onclick="adjustFun(\'{0}\');" >调账</a>',
																row.id);
												if ($.canAccountRemark) {
													str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
												}
											} --%>
	if ($.canFreeze
													&& $.canAdjustAccount
													&& $.canAccountRemark) {
												str += '';
											} else {
												if ($.canFreeze
														&& $.canAccountRemark) {
													str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
												}
											}

											if ($.canAccountRemark) {
												str += $
														.formatString(
																'<a href="javascript:void(0)" onclick="viewRemarkFun(\'{0}\');" >备注</a>',
																row.id);
											}
											return str;
										}
									} ] ],
							toolbar : '#toolbar'
						});
	});

	function freezePageFun(id) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$.messager.confirm('询问', '您确认冻结该用户吗？', function(b) {
			if (b) {
				parent.$.modalDialog({
					title : '冻结商户',
					width : 400,
					height : 300,
					href : '${ctx}/account/freezePage?id=' + id,
					buttons : [ {
						text : '确定',
						handler : function() {
							parent.$.modalDialog.openner_dataGrid = dataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
							var f = parent.$.modalDialog.handler
									.find('#freezeAccountForm');
							f.submit();
						}
					} ]
				});
			}
		});

	}
	function freezeFun(id) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		$.post('${ctx}/account/freeze', {
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

	function adjustFun(id) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$.modalDialog({
			title : '确定',
			width : 600,
			height : 400,
			href : '${ctx}/account/adjustAccountPage?id=' + id,
			buttons : [ {
				text : '提交',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
					var f = parent.$.modalDialog.handler
							.find('#adjustAccountForm');
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
					<th>运营商:</th>
					<td><select id="organizationId" name="organizationId"
						style="width: 140px; height: 29px;" class="easyui-validatebox"></select></td>
					<th>状态:</th>
					<td><select id="status" name="status" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<option value="1">冻结</option>
							<option value="0">正常</option>
							<option value="100">跨平台冻结</option>
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
	<div data-options="region:'center',border:false,title:'账户列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
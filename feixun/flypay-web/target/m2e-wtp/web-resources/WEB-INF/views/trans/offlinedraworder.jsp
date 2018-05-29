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
<c:if
	test="${fn:contains(sessionInfo.resourceList, '/offlineDrawOrder/freeze')}">
	<script type="text/javascript">
		$.canEdit = true;
	</script>
</c:if>
<title>用户订单</title>
<script type="text/javascript">
	var useFul = 0; //0可用  1不可用
	var flag = true; //标志变量，防止用户多次操作
	var dataGrid;
	$(function() {
		/* $('#organizationId').combotree({
			url : '${ctx}/organization/treeByOperator',
			parentField : 'pid',
			lines : true,
			panelWidth : 'auto',
			panelHeight : 256,
			value : '${user.organizationId}'
		}); */
		dataGrid = $('#dataGrid')
			.datagrid(
				{
					url : '${ctx}/offlineDrawOrder/dataGrid',
					
					
					striped : true,
					rownumbers : true,
					pagination : true,
					singleSelect : true,
					idField : 'id',
					sortName : 'id',
					sortOrder : 'desc',
					pageSize : 20,
					pageList : [ 10, 15, 20, 30, 40, 50, 100, 200, 300 ],
					frozenColumns : [ [ {
						width : '60',
						title : '用户ID',
						field : 'userId'
					}, {
						width : '80',
						title : '订单来源',
						field : 'drawSrc',
						formatter : function(value, row, index) {
							if (value == 'Brokerage') {
								return "分润提现";
							} else if (value == 'Account') {
								return "余额提现";
							}
							return "未知类型";
						}
					}, {
						width : '60',
						title : '状态',
						field : 'status',
						formatter : function(value, row, index) {
							if (value == '0') {
								return "初始化";
							} else if (value == '1') {
								return "已完成";
							} else if (value == '2') {
								return "已打批";
							} else if (value == '3') {
								return "已下载";
							} else if (value == '8') {
								return "出款失败";
							} else if (value == '9') {
								return "冻结";
							}
							return "未知类型";
						}
					}, {
						width : '80',
						title : '提现金额',
						field : 'payAmt'
					}, {
						width : '160',
						title : '订单号',
						field : 'orderNo'
					}, {
						width : '100',
						title : '用户登录名',
						field : 'loginName'
					}, {
						width : '160',
						title : '收款账号',
						field : 'accountBankNo'
					} ] ],
					columns : [ [
						{
							width : '100',
							title : '提现前',
							field : 'beforeAvlAmt'
						},
						{
							width : '100',
							title : '提现后',
							field : 'afterAvlAmt'
						},
						{
							width : '60',
							title : '收款人',
							field : 'receiverName'
						},
						{
							width : '120',
							title : '收款银行名称',
							field : 'openBankName'
						},
						{
							width : '200',
							title : '商户流水号',
							field : 'merFlowNo'
						},
						{
							width : '120',
							title : '创建时间',
							field : 'createTime'
						},
						{
							width : '120',
							title : '打批时间',
							field : 'bunchTime'
						},
						{
							width : '120',
							title : '下载时间',
							field : 'downloadTime'
						},
						{
							width : '120',
							title : '完成时间',
							field : 'finihsTime'
						},
						{
							field : 'action',
							title : '操作',
							width : 80,
							formatter : function(value, row, index) {
								var str = '';
								if ($.canEdit) {
									if (row.status == 0 || row.status == 2) {
										str += $
											.formatString(
												'<a href="javascript:void(0)" onclick="freezeOfflineOrder(\'{0}\',9);" >冻结</a>',
												row.id);
									}
									/* str += '&nbsp;&nbsp;|&nbsp;&nbsp;'; */
									if (row.status == 9) {
										str += $
											.formatString(
												'<a href="javascript:void(0)" onclick="unFreezeOfflineOrder(\'{0}\',0);" >解冻</a>',
												row.id);
									}

									if (row.status == 3) {
										str += $
											.formatString(
												'<a href="javascript:void(0)" onclick="payOfflineOrderError(\'{0}\',8);" >出款失败</a>',
												row.id);
									}

									if (row.status == 8) {
										str += $
											.formatString(
												'<a href="javascript:void(0)" onclick="makeOrderDownload(\'{0}\',3);" >已下载</a>',
												row.id);
									}
								}
								return str;
							}
						} ] ],
					toolbar : '#toolbar'
				});
	});

	//倒计时
	function reloadTime() {
		setTimeout('funcTime()', 5000);
	}

	function funcTime() {
		useFul = 0;
		$('#searchData').css('color', '#444444');
	}

	function searchFun() {
		if (useFul == '0') {
			useFul = 1;
			reloadTime(); //倒计时
			$('#searchData').css('color', '#FF0080');
			dataGrid.datagrid('load', $.serializeObject($('#searchForm')));
		} else {
			alert("5秒內不能重复点击查询按钮，请等待");
		}
	}

	function cleanFun() {
	}

	function exportExcelFun() {
		window.location.href = '${ctx}/offlineDrawOrder/exportExcel';
	}

	function finishOrderFun() {
		$.post('${ctx}/offlineDrawOrder/finishOrderFun', {
			/* id : id,
			status : status */
		}, function(result) {
			if (result.success) {
				parent.$.messager.alert('提示', '操作成功', 'info', function() {});
			} else {
				parent.$.messager.alert('提示', '操作失败', 'info', function() {});
			}
			dataGrid.datagrid('reload');
			progressClose();
		}, 'JSON');
	}

	function freezeOfflineOrder(id, status) {
		if (flag) { //标志变量防止用户连续点击多次
			flag = false; //点击一次后设为false
			if (id == undefined) {
				var rows = dataGrid.datagrid('getSelections');
				id = rows[0].id;
			} else {
				dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
			}
			$.post('${ctx}/offlineDrawOrder/freeze', {
				id : id,
				status : status
			}, function(result) {
				if (result.success) {
					parent.$.messager.alert('提示', '操作成功', 'info', function() {
						flag = true; //执行结束改为true
					});
					dataGrid.datagrid('reload');
				}
				progressClose();
			}, 'JSON');
		} else {
			alert("请不要重复操作！");
		}
	}

	function unFreezeOfflineOrder(id, status) {
		if (flag) { //标志变量防止用户连续点击多次
			flag = false; //点击一次后设为false
			if (id == undefined) {
				var rows = dataGrid.datagrid('getSelections');
				id = rows[0].id;
			} else {
				dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
			}
			$.post('${ctx}/offlineDrawOrder/unfreeze', {
				id : id,
				status : status
			}, function(result) {
				if (result.success) {
					parent.$.messager.alert('提示', '操作成功', 'info', function() {
						flag = true; //执行结束改为true
					});
					dataGrid.datagrid('reload');
				}
				progressClose();
			}, 'JSON');
		} else {
			alert("请不要重复操作！");
		}
	}

	function payOfflineOrderError(id, status) {
		if (flag) { //标志变量防止用户连续点击多次
			flag = false; //点击一次后设为false
			if (id == undefined) {
				var rows = dataGrid.datagrid('getSelections');
				id = rows[0].id;
			} else {
				dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
			}
			$.post('${ctx}/offlineDrawOrder/payOfflineOrderError', {
				id : id,
				status : status
			}, function(result) {
				if (result.success) {
					parent.$.messager.alert('提示', '操作成功', 'info', function() {
						flag = true; //执行结束改为true
					});
					dataGrid.datagrid('reload');
				}
				progressClose();
			}, 'JSON');
		} else {
			alert("请不要重复操作！");
		}
	}

	function makeOrderDownload(id, status) {
		if (flag) { //标志变量防止用户连续点击多次
			flag = false; //点击一次后设为false
			if (id == undefined) {
				var rows = dataGrid.datagrid('getSelections');
				id = rows[0].id;
			} else {
				dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
			}
			$.post('${ctx}/offlineDrawOrder/makeOrderDownload', {
				id : id,
				status : status
			}, function(result) {
				if (result.success) {
					parent.$.messager.alert('提示', '操作成功', 'info', function() {
						flag = true; //执行结束改为true
					});
					dataGrid.datagrid('reload');
				}
				progressClose();
			}, 'JSON');
		} else {
			alert("请不要重复操作！");
		}
	}
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false"
		style="height: 100px; overflow: hidden;background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					<td style="width:80px">订单来源</td>
					<td style="width:180px"><select id="drawSrc" name="drawSrc"
						class="easyui-combobox"
						data-options="width:160,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<option value="Brokerage">分润提现</option>
							<option value="Account">余额提现</option>
					</select></td>
					<td style="width:80px">订单状态</td>
					<td><select id="status" name="status" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<option value="0">初始化</option>
							<option value="2">已打批</option>
							<option value="3">已下载</option>
							<option value="8">出款失败</option>
							<option value="9">已冻结</option>
							<option value="1">已完成</option>
					</select></td>
				</tr>

				<tr>
					<td>APP登录名:</td>
					<td><input id="loginName" name="loginName"
						style="width: 160px;" type="text" class="easyui-validatebox" /></td>
					<td>订单号:</td>
					<td><input id="orderNo" name="orderNo" style="width: 160px;"
						type="text" class="easyui-validatebox" /></td>
					<td>收款账号:</td>
					<td><input name="accountBankNo" style="width: 160px;"
						type="text" class="easyui-validatebox" /></td>
				</tr>

				<tr>
					<td colspan="2"><a id="searchData" href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-search',plain:true"
						onclick="searchFun();">查询</a> <a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-download',plain:true"
						onclick="exportExcelFun();">下载</a> <a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-ok',plain:true"
						onclick="finishOrderFun();">完成</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:false,title:'订单列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
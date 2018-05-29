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
<c:if
	test="${fn:contains(sessionInfo.resourceList, '/userSettlementConfig/edit')}">
	<script type="text/javascript">
		$.canEdit = true;
	</script>
</c:if>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户限额配置</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid')
				.datagrid(
						{
							url : '${ctx}/userSettlementConfig/dataGrid',
							fit : true,
							striped : true,
							rownumbers : true,
							pagination : true,
							singleSelect : true,
							idField : 'id',
							sortName : 'id',
							sortOrder : 'desc',
							pageSize : 50,
							pageList : [ 10, 20, 30, 40, 50, 100, 200, 300,
									400, 500 ],
							columns : [ [
									{
										width : '100',
										title : 'APP名称',
										field : 'organizationAppName'
									},
									{
										width : '100',
										title : 'APP登录名',
										field : 'loginName'
									},
									{
										width : '100',
										title : '真实名称',
										field : 'realName'
									},
									{
										width : '100',
										title : 'T0最低限额',
										field : 'minT0Amt'
									},
									{
										width : '100',
										title : 'T0手续费',
										field : 't0Fee'
									},
									{
										width : '100',
										title : 'T0最高限额',
										field : 'maxT0Amt'
									},
									{
										width : '100',
										title : 'T1最低限额',
										field : 'minT1Amt'
									},
									{
										width : '100',
										title : 'T1手续费',
										field : 't1Fee'
									},
									{
										width : '100',
										title : 'T1最高限额',
										field : 'maxT1Amt'
									},
									{
										width : '100',
										title : '返佣提现最低限额',
										field : 'minRabaleAmt'
									},
									{
										width : '100',
										title : '返佣提现手续费',
										field : 'rabaleFee'
									},
									{
										width : '100',
										title : '返佣提现最高限额',
										field : 'maxRabaleAmt'
									},
									{
										width : '100',
										title : '每日提现额度',
										field : 'maxTodayOutAmt'
									},
									{
										width : '100',
										title : '默认付款手续费率',
										field : 'inputFee'
									},
									{
										width : '100',
										title : '微信T1付款手续费率',
										field : 'inputFeeWeixin'
									},
									{
										width : '100',
										title : '支付宝T1付款手续费率',
										field : 'inputFeeAlipay'
									},
									{
										width : '100',
										title : '银联T1付款手续费率',
										field : 'inputFeeYinlian'
									},
									{
										width : '100',
										title : '京东T1付款手续费率',
										field : 'inputFeeJingDong'
									},
									{
										width : '100',
										title : '微信D0付款手续费率',
										field : 'inputFeeD0Weixin'
									},
									{
										width : '100',
										title : '支付宝D0付款手续费率',
										field : 'inputFeeD0Alipay'
									},
									{
										width : '100',
										title : '银联D0付款手续费率',
										field : 'inputFeeD0Yinlian'
									},
									{
										width : '100',
										title : '京东D0付款手续费率',
										field : 'inputFeeD0JingDong'
									},
									{
										width : '100',
										title : '付款分佣费率',
										field : 'shareFee'
									},
									{
										field : 'action',
										title : '操作',
										width : 100,
										formatter : function(value, row, index) {
											if ($.canEdit) {
												var str = $
														.formatString(
																'<a href="javascript:void(0)" onclick="editFun(\'{0}\');" >编辑</a>',
																row.id);
												return str;
											}
											return '';
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
		parent.$.modalDialog({
			title : '编辑',
			width : 650,
			height : 600,
			href : '${ctx}/userSettlementConfig/editPage?id=' + id,
			buttons : [ {
				text : '编辑',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
					var f = parent.$.modalDialog.handler
							.find('#userSettlementConfigForm');
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
	function setMaxT0Fun() {
		var maxT0Amt = $('#maxT0Amt').val();
		if (maxT0Amt && maxT0Amt != '') {
			$.post('${ctx}/userSettlementConfig/setAllUserMaxT0Amt', {
				maxT0Amt : maxT0Amt
			}, function(result) {
				dataGrid.datagrid('load', $.serializeObject($('#searchForm')));
			}, 'JSON');
		}
	}
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false"
		style="height: 70px; overflow: hidden;background-color: #fff">
		<form id="searchForm">
			<table>
				<tr>
					<th>App登录名:</th>
					<td><input name="loginName" placeholder="请输入App登录名" /></td>
					<td><a href="javascript:void(0);" class="easyui-linkbutton"
						data-options="iconCls:'icon-search',plain:true"
						onclick="searchFun();">查询</a><a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-clear',plain:true"
						onclick="cleanFun();">清空</a></td>
				</tr>
				<tr>
					<th>T0最高提现限额:</th>
					<td><input id='maxT0Amt' name="maxT0Amt"
						placeholder="涉及到全局，请谨慎" /></td>
					<td><a href="javascript:void(0);" class="easyui-linkbutton"
						data-options="iconCls:'icon-edit',plain:true"
						onclick="setMaxT0Fun();">设置</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:true,title:'用户限额列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
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
	test="${fn:contains(sessionInfo.resourceList, '/userMerchantConfig/edit')}">
	<script type="text/javascript">
		$.canEdit = true;
	</script>
</c:if>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>子商户配置</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid')
				.datagrid(
						{
							url : '${ctx}/userMerchantConfig/dataGrid',
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
										title : 'APP登录名',
										field : 'loginName'
									},
									{
										width : '80',
										title : '真实名称',
										field : 'realName'
									},
									{
										width : '100',
										title : '公司名称',
										field : 'merchantName'
									},
									{
										width : '80',
										title : '简称',
										field : 'merchantShortName'
									},
									{
										width : '100',
										title : '地址',
										field : 'address'
									},
									{
										width : '100',
										title : '通道名称',
										field : 'serviceMerchantDetailName'
									},
									{
										width : '100',
										title : '类型',
										field : 'type',
										formatter : function(value, row, index) {
											if (value == '999') {
												return '综合';
											} else if (value == '200') {
												return '支付宝';
											} else if (value == '300') {
												return '微信';
											} else {
												return '否';
											}
										}
									},
									{
										width : '150',
										title : '子商户号',
										field : 'subMerchantId'
									},
									{
										width : '300',
										title : '配置',
										field : 'config'
									},
									{
										width : '80',
										title : '状态',
										field : 'status',
										formatter : function(value, row, index) {
											if (value == '0') {
												return '正常';
											} else {
												return '冻结';
											}
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
			width : 600,
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
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:true,title:'子商户配置列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
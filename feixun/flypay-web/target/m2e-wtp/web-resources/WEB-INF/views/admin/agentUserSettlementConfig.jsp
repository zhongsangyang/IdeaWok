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
<c:if test="${fn:contains(sessionInfo.resourceList, '/user/edit')}">
	<script type="text/javascript">
		$.canEdit = true;
	</script>
</c:if>
<c:if test="${fn:contains(sessionInfo.resourceList, '/user/delete')}">
	<script type="text/javascript">
		
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
							pageList : [ 10, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
							columns : [ [
									{
										width : '150',
										title : '运营商名称',
										field : 'organizationName'
									},{
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
										title : '用户付款手续费率',
										field : 'inputFee'
									},
									{
										width : '100',
										title : '用户付款分佣费率',
										field : 'shareFee'
									},
									{
										field : 'action',
										title : '操作',
										width : 100,
										formatter : function(value, row, index) {
											var str = $.formatString(
													'<a href="javascript:void(0)" onclick="editFun(\'{0}\');" >编辑</a>',
													row.id);
											return str;
										}
									} ] ]
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
			height : 500,
			href : '${ctx}/userSettlementConfig/agentEditPage?id=' + id,
			buttons : [ {
				text : '编辑',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
					var f = parent.$.modalDialog.handler.find('#agentUserSettlementConfigForm');
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
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false"
		style="height: 30px; overflow: hidden;background-color: #fff">
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
	<div data-options="region:'center',border:true,title:'用户限额列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
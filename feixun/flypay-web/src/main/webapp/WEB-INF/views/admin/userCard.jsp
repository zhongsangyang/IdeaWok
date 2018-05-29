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
<title>用户银行卡</title>
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
							url : '${ctx}/userCard/dataGrid',
							fit : true,
							striped : true,
							rownumbers : true,
							pagination : true,
							singleSelect : true,
							idField : 'id',
							sortName : 'id',
							sortOrder : 'desc',
							pageSize : 30,
							pageList : [ 10, 20, 30, 40, 50, 100, 200, 300 ],
							columns : [ [
									{
										title : '用户ID',
										field : 'userId',
										hidden : true
									},
									{
										width : '100',
										title : 'APP登录名',
										field : 'loginName'
									},
									{
										width : '100',
										title : '真实姓名',
										field : 'realName'
									},
									{
										width : '150',
										title : '身份证号',
										field : 'idNo'
									},
									{
										width : '150',
										title : '银行卡',
										field : 'cardNo'
									},
									{
										width : '80',
										title : '类型',
										field : 'cardType',
										formatter : function(value, row, index) {
											if (value == 'X') {
												return '信用卡';
											} else {
												return '储蓄卡';
											}
										}
									},
									{
										width : '165',
										title : '银行',
										field : 'bankName'
									},
									{
										width : '100',
										title : '预留手机号',
										field : 'phone'
									},
									{
										width : '80',
										title : '结算卡',
										field : 'isSettlmentCard',
										formatter : function(value, row, index) {
											switch (value) {
											case 0:
												return '否';
											case 1:
												return '是';
											}
										}
									},
									{
										width : '120',
										title : '所属运营商',
										field : 'organizationName'
									},
									{

										width : '35',
										title : '状态',
										field : 'status',
										sortable : true,
										formatter : function(value, row, index) {
											switch (value) {
											case 0:
												return '正常';
											case 1:
												return '停用';
											}
										}
									},
									{
										field : 'action',
										title : '操作',
										width : 100,
										formatter : function(value, row, index) {
											return $.formatString('<a href="javascript:void(0)" onclick="stopUseCard(\'{0}\',\'{1}\');" >停用</a>', row.id, row.userId);
										}
									} ] ],
							toolbar : '#toolbar'
						});
	});

	function stopUseCard(id, userId) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
			userId = rows[0].userId;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$.messager.confirm('询问', '您确定停用用户该张银行卡吗？请慎重！', function(b) {
			if (b) {
				$.post('${ctx}/userCard/stopUseCard', {
					cardId : id,
					userId : userId
				}, function(result) {
					alert(result.msg);
					dataGrid.datagrid('load', $
							.serializeObject($('#searchForm')));
				}, 'JSON');
				return;
			}

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
					<th>APP登录名:</th>
					<td><input name="loginName" placeholder="请输入用户姓名" /></td>
					<th>运营商:</th>
					<td><select id="organizationId" name="organizationId"
						style="width: 140px; height: 29px;" class="easyui-validatebox"></select><a
						href="javascript:void(0);" class="easyui-linkbutton"
						data-options="iconCls:'icon-search',plain:true"
						onclick="searchFun();">查询</a><a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-clear',plain:true"
						onclick="cleanFun();">清空</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:true,title:'银行卡列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
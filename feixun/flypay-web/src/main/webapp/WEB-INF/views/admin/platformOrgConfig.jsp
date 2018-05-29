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
<title>平台运营商配比</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid')
				.datagrid(
						{
							url : '${ctx}/platformOrgConfig/dataGrid',
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
										width : '100',
										title : '运营商名称',
										field : 'orgName'
									},
									{
										width : '100',
										title : '交易金额手续费率',
										field : 'platformInputRate'
									},
									{
										width : '150',
										title : '实名认证费',
										field : 'platformAuthenticationFee'
									},
									{
										width : '150',
										title : '短信费',
										field : 'platformMessageFee'
									},
									{
										width : '100',
										title : '单笔提现手续费',
										field : 'platformTixianFee'
									},
									{
										width : '100',
										title : 'T0垫资手续费',
										field : 'platformT0TixianRate'
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
			width : 350,
			height : 400,
			href : '${ctx}/platformOrgConfig/editPage?id=' + id,
			buttons : [ {
				text : '编辑',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
					var f = parent.$.modalDialog.handler.find('#platformOrgConfigEditForm');
					f.submit();
				}
			} ]
		});
	}
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:true,title:'配置列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
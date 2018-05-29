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
<title>运营商通道降费率</title>
<script type="text/javascript">
	var dataGrid;
	var organizationTree;
	$(function() {
		organizationTree = $('#organizationTree').tree({
			url : '${ctx}/organization/oemProviderTree',
			parentField : 'pid',
			lines : true,
			onClick : function(node) {
				dataGrid.datagrid('load', {
					organizationId : node.id
				});
			}
		});

		dataGrid = $('#dataGrid')
				.datagrid(
						{
							url : '${ctx}/orgPointConfig/dataGrid',
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
										title : '支付类型',
										field : 'payType',
										formatter : function(value, row, index) {
											var jsonObjs = $
													.parseJSON('${payType}');
											for (var i = 0; i < jsonObjs.length; i++) {
												var jsonobj = jsonObjs[i];
												if (jsonobj.code == value) {
													return jsonobj.text;
												}
											}
											return value;
										}
									},
									{
										width : '100',
										title : '入账类型',
										field : 'type',
										formatter : function(value, row, index) {
											var jsonObjs = $
													.parseJSON('${bigTranType}');
											for (var i = 0; i < jsonObjs.length; i++) {
												var jsonobj = jsonObjs[i];
												if (jsonobj.code == value) {
													return jsonobj.text;
												}
											}
											return "未知类型";
										}
									},
									{
										width : '150',
										title : '最高费率',
										field : 'topRate'
									},
									{
										width : '150',
										title : '中等费率',
										field : 'midRate'
									},
									{
										width : '100',
										title : '最低费率',
										field : 'lowRate'
									},
									{
										width : '100',
										title : '降至中费率消耗',
										field : 'toMidNum'
									},
									{
										width : '100',
										title : '降至低费率消耗',
										field : 'toLowNum'
									},
									{
										width : '100',
										title : '状态',
										field : 'status',
										formatter : function(value, row, index) {
											switch (value) {
											case 0:
												return '正常';
											case 1:
												return '停用';
											}
											return "未知";
										}
									},

									{
										field : 'action',
										title : '操作',
										width : 100,
										formatter : function(value, row, index) {
											var str = $
													.formatString(
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
			width : 400,
			height : 450,
			href : '${ctx}/orgPointConfig/editPage?id=' + id,
			buttons : [ {
				text : '编辑',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
					var f = parent.$.modalDialog.handler
							.find('#platformOrgConfigEditForm');
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
	<div data-options="region:'west',border:true,split:false,title:'组织机构'"
		style="width:180px;overflow: hidden; ">
		<ul id="organizationTree"
			style="width:160px;margin: 10px 10px 10px 10px">
		</ul>
	</div>
	<div data-options="region:'center',border:true,title:'配置列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
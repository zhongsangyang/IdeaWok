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
	test="${fn:contains(sessionInfo.resourceList, '/organization/edit')}">
	<script type="text/javascript">
		$.canEdit = true;
	</script>
</c:if>
<title>代理商管理</title>
<script type="text/javascript">
	var treeGrid;
	$(function() {
		treeGrid = $('#treeGrid')
				.treegrid(
						{
							url : '${ctx}/organization/treeGrid',
							idField : 'id',
							treeField : 'name',
							parentField : 'pid',
							fit : true,
							fitColumns : false,
							border : false,
							frozenColumns : [ [ {
								title : 'id',
								field : 'id',
								width : 40,
								hidden : true
							} ] ],
							columns : [ [
									{
										field : 'code',
										title : '编号',
										width : 120
									},
									{
										field : 'name',
										title : '运营商名称',
										width : 360
									},
									{
										width : '80',
										title : '运营类型',
										field : 'agentType',
										sortable : true,
										formatter : function(value, row, index) {
											var jsonObjs = $
													.parseJSON('${orgAgentType}');
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
										width : '80',
										title : '代理等级',
										field : 'agentLevel',
										sortable : true,
										formatter : function(value, row, index) {
											var jsonObjs = $
													.parseJSON('${orgAgentLevel}');
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
										width : '80',
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
										field : 'pid',
										title : '上级资源ID',
										width : 150,
										hidden : true
									},
									{
										field : 'diamondAgent',
										title : '${name1}',
										width : 80
									},
									{
										field : 'goldAgent',
										title : '${name2}',
										width : 80
									},
									{
										width : '120',
										title : '创建时间',
										field : 'createDatetime'
									},
									{
										field : 'action',
										title : '操作',
										width : 200,
										formatter : function(value, row, index) {
											var str = '&nbsp;';
											if ($.canEdit && row.agentType ==2) {
												str += $
														.formatString(
																'<a href="javascript:void(0)" onclick="editFun(\'{0}\');" >编辑</a>',
																row.id);
											}
											return str;
										}
									} ] ],
							toolbar : '#toolbar'
						});
	});

	function editFun(id) {
		if (id != undefined) {
			treeGrid.treegrid('select', id);
		}
		var node = treeGrid.treegrid('getSelected');
		if (node) {
			parent.$.modalDialog({
				title : '编辑',
				width : 550,
				height : 350,
				href : '${ctx}/organization/editAgentPage?id=' + node.id,
				buttons : [ {
					text : '编辑',
					handler : function() {
						parent.$.modalDialog.openner_treeGrid = treeGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
						var f = parent.$.modalDialog.handler
								.find('#agentOrganizationEditForm');
						f.submit();
					}
				} ]
			});
		}
	}


	function addFun() {
		parent.$.modalDialog({
			title : '添加',
			width : 550,
			height : 350,
			href : '${ctx}/organization/addAgentPage',
			buttons : [ {
				text : '添加',
				handler : function() {
					parent.$.modalDialog.openner_treeGrid = treeGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
					var f = parent.$.modalDialog.handler
							.find('#agentOrganizationAddForm');
					f.submit();
				}
			} ]
		});
	}
</script>
</head>
<body>
	<div class="easyui-layout" data-options="fit:true,border:false">
		<div data-options="region:'center',border:false"
			style="overflow: hidden;">
			<table id="treeGrid"></table>
		</div>

		<div id="toolbar" style="display: none;">
			<c:if
				test="${fn:contains(sessionInfo.resourceList, '/organization/add')}">
				<a onclick="addFun();" href="javascript:void(0);"
					class="easyui-linkbutton"
					data-options="plain:true,iconCls:'icon-add'">添加代理</a>
			</c:if>
		</div>
	</div>
</body>
</html>
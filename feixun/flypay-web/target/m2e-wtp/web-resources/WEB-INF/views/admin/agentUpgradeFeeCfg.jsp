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
		$.canDelete = true;
	</script>
</c:if>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>代理商分润结算配置</title>
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
							url : '${ctx}/agentUpgradeFeeCfg/dataGrid',
							fit : true,
							striped : true,
							rownumbers : true,
							pagination : true,
							singleSelect : true,
							idField : 'id',
							pageSize : 50,
							pageList : [ 10, 20, 30, 40, 50, 100, 200, 300,
									400, 500 ],
							columns : [ [
									{
										width : '150',
										title : '机构名称',
										field : 'orgName'
									},
									{
										width : '100',
										title : '代理类型',
										field : 'agentType',
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
										width : '100',
										title : '金牌结算价格',
										field : 'glodFee',
										sortable : true

									},
									{
										width : '100',
										title : '钻石结算价格',
										field : 'diamondFee',
										sortable : true

									},
									{
										width : '100',
										title : '认证单价',
										field : 'authFee',
										sortable : true

									},
									{
										width : '100',
										title : 'T0手续费',
										field : 'tixianT0Fee',
										sortable : true

									},
									{
										width : '100',
										title : 'T1手续费',
										field : 'tixianT1Fee',
										sortable : true

									},
									{
										field : 'action',
										title : '操作',
										width : 100,
										formatter : function(value, row, index) {
											var str = '';
											if ($.canEdit) {
												str += $
														.formatString(
																'<a href="javascript:void(0)" onclick="editFun(\'{0}\',\'{1}\',\'{2}\');" >编辑</a>',
																row.organizationId,
																row.agentType);
											}
											return str;
										}
									} ] ]
						});
	});

	function editFun(id, agentType) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].organization.id;
			agentType = rows[0].agentType;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$.modalDialog({
			title : '编辑',
			width : 500,
			height : 300,
			href : '${ctx}/agentUpgradeFeeCfg/editPage?orgId=' + id
					+ '&agentType=' + agentType,
			buttons : [ {
				text : '编辑',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
					var f = parent.$.modalDialog.handler
							.find('#agentSettlementRateConfigEditForm');
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
	<div data-options="region:'west',border:true,split:false,title:'组织机构'"
		style="width:180px;overflow: hidden; ">
		<ul id="organizationTree"
			style="width:160px;margin: 10px 10px 10px 10px">
		</ul>
	</div>
</body>
</html>
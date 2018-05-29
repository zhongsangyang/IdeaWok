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
<title>机构通道费率配置</title>
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
							url : '${ctx}/orgChannelUserRateConfig/dataGrid',
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
										width : '80',
										title : '运营商ID',
										field : 'organizationId',
										hidden : true
									},
									{
										width : '150',
										title : '机构名称',
										field : 'orgName'
									},
									{
										width : '100',
										title : '支付类型',
										field : 'channelType',
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
										title : '用户类型',
										field : 'agentType',
										formatter : function(value, row, index) {
											var jsonObjs = $
													.parseJSON('${userType}');
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
										title : 'T1利率',
										field : 't1Rate',
										sortable : true
									},
									{
										width : '100',
										title : 'D0利率',
										field : 'd0Rate',
										sortable : true
									},
									{
										width : '100',
										title : 'T1大额利率',
										field : 't1BigRate',
										sortable : true
									},
									{
										width : '100',
										title : 'D0大额利率',
										field : 'd0BigRate',
										sortable : true
									},
									{
										field : 'action',
										title : '操作',
										width : 100,
										formatter : function(value, row, index) {
											var str = $
													.formatString(
															'<a href="javascript:void(0)" onclick="editFun(\'{0}\',\'{1}\',\'{2}\');" >编辑</a>',
															row.organizationId,
															row.agentType,
															row.channelType);
											return str;
										}
									} ] ]
						});
	});

	function editFun(id, agentType, channelType) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].organization.id;
			channelType = rows[0].channelType;
			agentType = rows[0].agentType;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$
				.modalDialog({
					title : '编辑',
					width : 500,
					height : 300,
					href : '${ctx}/orgChannelUserRateConfig/editPage?orgId='
							+ id + '&channelType=' + channelType
							+ '&agentType=' + agentType,
					buttons : [ {
						text : '编辑',
						handler : function() {
							parent.$.modalDialog.openner_dataGrid = dataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
							var f = parent.$.modalDialog.handler
									.find('#orgChannelUserRateConfigEditForm');
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
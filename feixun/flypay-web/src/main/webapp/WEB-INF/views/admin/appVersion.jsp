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
	test="${fn:contains(sessionInfo.resourceList, '/appversion/edit')}">
	<script type="text/javascript">
		$.canEdit = true;
	</script>
</c:if>
<c:if
	test="${fn:contains(sessionInfo.resourceList, '/appversion/delete')}">
	<script type="text/javascript">
		$.canDelete = true;
	</script>
</c:if>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>APP版本管理</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid')
				.datagrid(
						{
							url : '${ctx}/appversion/dataGrid',
							striped : true,
							rownumbers : true,
							pagination : true,
							singleSelect : true,
							idField : 'id',
							sortName : 'id',
							sortOrder : 'desc',
							pageSize : 15,
							pageList : [ 10, 15, 20, 30, 40, 50, 100, 200, 300,
									400, 500 ],
							frozenColumns : [ [ {
								width : '120',
								title : 'APP名称',
								field : 'appName'
							},  {
								width : '120',
								title : '版本名称',
								field : 'versionName',
								sortable : true
							}, {
								width : '120',
								title : '类型',
								field : 'appType',
								sortable : true
							} ] ],
							columns : [ [
									{
										width : '200',
										title : '下载链接',
										field : 'updateUrl'
									},
									{
										width : '200',
										title : '下载网址',
										field : 'downloadNet'
									},
									
									{
										width : '80',
										title : '状态',
										field : 'status',
										sortable : true,
										formatter : function(value, row, index) {
											if (value == 0) {
												return '初始化';
											} else {
												return '已发布';
											}
										}
									},
									{
										width : '80',
										title : '强制更新',
										field : 'isForce',
										sortable : true,
										formatter : function(value, row, index) {
											if (value == 0) {
												return '否';
											} else {
												return '是';
											}
										}
									},
									{
										width : '150',
										title : '创建时间',
										field : 'createTime'
									},
									{
										width : '150',
										title : '创建人',
										field : 'creator'
									},
									{
										field : 'action',
										title : '操作',
										width : 120,
										formatter : function(value, row, index) {
											var str = "";
											if ($.canEdit) {
												str += $
														.formatString(
																'<a href="javascript:void(0)" onclick="editFun(\'{0}\');" >编辑</a>',
																row.id);
											}
											if ($.canDelete && row.status == 0) {
												str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
												str += $
														.formatString(
																'<a href="javascript:void(0)" onclick="deleteFun(\'{0}\');" >删除</a>',
																row.id);
											}
											return str;
										}
									} ] ],
							toolbar : '#toolbar'
						});
	});
	function addFun() {
		parent.$.modalDialog({
			title : '添加',
			width : 500,
			height : 450,
			href : '${ctx}/appversion/addPage',
			buttons : [ {
				text : '添加',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;
					var f = parent.$.modalDialog.handler
							.find('#appVersionAddForm');
					f.submit();
				}
			} ]
		});
	}

	function deleteFun(id) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$.messager.confirm('询问', '您是否要停用该参数？', function(b) {
			if (b) {
				progressLoad();
				$.post('${ctx}/appversion/delete', {
					id : id
				}, function(result) {
					if (result.success) {
						parent.$.messager.alert('提示', result.msg, 'info');
						dataGrid.datagrid('reload');
					}
					progressClose();
				}, 'JSON');
			}
		});
	}

	function editFun(id) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$.modalDialog({
			title : '编辑',
			width : 500,
			height : 450,
			href : '${ctx}/appversion/editPage?id=' + id,
			buttons : [ {
				text : '编辑',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;
					var f = parent.$.modalDialog.handler
							.find('#appVersionEditForm');
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
	<div data-options="region:'center',border:false,title:'渠道列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
	<div id="toolbar" style="display: none;">
	APP名称 :<input name="appName" placeholder="APP名称 " class="easyui-validatebox" />
	<a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-search',plain:true"
						onclick="searchFun();">查询</a>
		<c:if
			test="${fn:contains(sessionInfo.resourceList, '/appversion/add')}">
			<a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-add',plain:true"
						onclick="addFun();">添加</a>
		</c:if>
	</div>
</body>
</html>
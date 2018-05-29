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
<c:if test="${fn:contains(sessionInfo.resourceList, '/infoList/edit')}">
	<script type="text/javascript">
		$.canEdit = true;
	</script>
</c:if>
<c:if
	test="${fn:contains(sessionInfo.resourceList, '/infoList/delete')}">
	<script type="text/javascript">
		$.canDelete = true;
	</script>
</c:if>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>系统公告管理</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid')
				.datagrid(
						{
							url : '${ctx}/infoList/dataGrid',
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
								width : '100',
								title : '登录名',
								field : 'phone'
							}, {
								width : '100',
								title : '真实姓名',
								field : 'userName'
							}, {
								width : '120',
								title : '标题',
								field : 'title',
								sortable : true
							} ] ],
							columns : [ [
									{
										width : '350',
										title : '内容',
										field : 'content'
									},
									{
										width : '150',
										title : '创建时间',
										field : 'createTime'

									},
									{
										width : '150',
										title : '运营商名称',
										field : 'organizationName'
									},
									{
										width : '80',
										title : '是否强制',
										field : 'isForce',
										sortable : true,
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
										width : '80',
										title : '是否展示',
										field : 'isShow',
										sortable : true,
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
										width : '80',
										title : '强制显示时长',
										field : 'forceHours'
									},
									{
										width : '80',
										title : '是否发布',
										field : 'status',
										sortable : true,
										formatter : function(value, row, index) {
											switch (value) {
											case 0:
												return '否';
											case 1:
												return '是';
											case 2:
												return '撤回';
											}
										}
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
											if ($.canDelete) {
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
			height : 500,
			href : '${ctx}/infoList/addPage',
			buttons : [ {
				text : '添加',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;
					var f = parent.$.modalDialog.handler
							.find('#infoListAddForm');
					f.submit();
					dataGrid.datagrid('reload');
				}
			} ]
		});
	}
	function addPersonFun() {
		parent.$.modalDialog({
			title : '添加',
			width : 500,
			height : 500,
			href : '${ctx}/infoList/addPersonPage',
			buttons : [ {
				text : '添加',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;
					var f = parent.$.modalDialog.handler
							.find('#infoListPersonAddForm');
					f.submit();
					dataGrid.datagrid('reload');
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
				$.post('${ctx}/infoList/delete', {
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
			height : 500,
			href : '${ctx}/infoList/editPage?id=' + id,
			buttons : [ {
				text : '编辑',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;
					var f = parent.$.modalDialog.handler
							.find('#infoListEditForm');
					f.submit();
					dataGrid.datagrid('reload');
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
					<th>APP登录名:</th>
					<td><input name="phone" placeholder="请输入用户姓名" /></td>
					<th>公告类型:</th>
					<td><select id="infoType" name="infoType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<option value="2">系统公告</option>
							<option value="1">个人信息</option>
					</select></td>
					<th>创建时间:</th>
					<td><input name="createdatetimeStart" placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" />至<input name="createdatetimeEnd"
						placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" /> <a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-search',plain:true"
						onclick="searchFun();">查询</a><a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-clear',plain:true"
						onclick="cleanFun();">清空</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:false,title:'公告列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
	<div id="toolbar" style="display: none;">
		<c:if test="${fn:contains(sessionInfo.resourceList, '/infoList/add')}">
			<a onclick="addFun();" href="javascript:void(0);"
				class="easyui-linkbutton"
				data-options="plain:true,iconCls:'icon_add'">添加</a>
		</c:if>
		<a onclick="addPersonFun();" href="javascript:void(0);"
			class="easyui-linkbutton"
			data-options="plain:true,iconCls:'icon_add'">发送个人</a>
	</div>
</body>
</html>
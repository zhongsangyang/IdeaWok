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
	test="${fn:contains(sessionInfo.resourceList, '/bannerImage/edit')}">
	<script type="text/javascript">
		$.canEdit = true;
	</script>
</c:if>
<c:if
	test="${fn:contains(sessionInfo.resourceList, '/bannerImage/delete')}">
	<script type="text/javascript">
		$.canDelete = true;
	</script>
</c:if>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Banner管理</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid')
			.datagrid(
				{
					url : '${ctx}/bannerImage/dataGrid',
					striped : true,
					rownumbers : true,
					pagination : true,
					idField : 'id',
					sortName : 'seq',
					sortOrder : 'asc',
					pageSize : 15,
					pageList : [ 10, 15, 20, 30, 40, 50, 100, 200, 300,
						400, 500 ],
					frozenColumns : [ [
						{
							field : 'ck',
							checkbox : true
						},
						{
							width : '100',
							title : 'Banner名称',
							field : 'name'
						},
						{
							width : '160',
							title : '运营商',
							field : 'appName'
						},
						{
							width : '280',
							title : 'Banner链接',
							field : 'imgUrl'
						},
						{
							width : '160',
							title : '点击动作',
							field : 'actionUrl'
						},
						{
							width : '60',
							title : '状态',
							field : 'status',
							formatter : function(value, row, index) {
								switch (value) {
								case '0':
									return '停用';
								case '1':
									return '正常';
								default:
									return '未知';
								}
							}
						}
					] ],
					columns : [ [
						{
							width : '160',
							title : '创建时间',
							field : 'createTime'
						}, {
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
								<%-- if ($.canDelete && row.status == 0) {
									str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
									str += $
										.formatString(
											'<a href="javascript:void(0)" onclick="deleteFun(\'{0}\');" >删除</a>',
											row.id);
								} --%>
								return str;
							}
						}
					] ],
					toolbar : '#toolbar'
				});
	});

	function addFun() {
		parent.$.modalDialog({
			title : '添加Banner',
			width : 650,
			height : 600,
			href : '${ctx}/bannerImage/addPage',
			buttons : [ {
				text : '添加',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;
					var f = parent.$.modalDialog.handler
						.find('#bannerAddForm');
					f.submit();
					dataGrid.datagrid('reload');
				}
			} ]
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
			title : '编辑Banner',
			width : 650,
			height : 600,
			href : '${ctx}/bannerImage/editPage?id=' + id,
			buttons : [ {
				text : '编辑',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;
					var f = parent.$.modalDialog.handler
						.find('#bannerEditForm');
					f.submit();
				}
			} ]
		});
	}
	
	function searchFun() {
	console.log('serch GOGO')
		/* dataGrid.datagrid('load', $.serializeObject($('#searchForm'))); */
	}
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false"
		style="height: 50px; overflow: hidden; background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					<%-- <td>通道名称</td>
					<td><select id="name" name="name" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${statementTypeObj}" var="obj">
								<option value="${obj.code}">${obj.text }</option>
							</c:forEach>
					</select></td> --%>

					<%-- <td>通道类型</td>
					<td><select id="type" name="type" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${transTypeObj}" var="obj">
								<option value="${obj.code}">${obj.text }</option>
							</c:forEach>
					</select></td> --%>

					<%-- <td>用户类型</td>
					<td><select id="userType" name="userType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${userTypeObj}" var="obj">
								<option value="${obj.code}">${obj.text }</option>
							</c:forEach>
					</select></td> --%>

					<th>状态:</th>
					<td><select id="status" name="status" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<option value="1">正常</option>
							<option value="0">停用</option>
					</select> <a href="javascript:void(0);" class="easyui-linkbutton"
						data-options="iconCls:'icon-search',plain:true"
						onclick="searchFun();">查询</a> <%-- <a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-del',plain:true" onclick="deleteFun()">删除</a> --%>
						<%-- <a href="javascript:void(0);" class="easyui-linkbutton"
						data-options="iconCls:'icon-clear',plain:true"
						onclick="cleanFun();">清空</a> --%></td>
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:false,title:'Banner列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
	<div id="toolbar" style="display: none;">
		<c:if
			test="${fn:contains(sessionInfo.resourceList, '/bannerImage/add')}">
			<a onclick="addFun();" href="javascript:void(0);"
				class="easyui-linkbutton"
				data-options="plain:true,iconCls:'icon_add'">添加</a>
		</c:if>
	</div>
</body>
</html>
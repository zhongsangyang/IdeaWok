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
<c:if test="${fn:contains(sessionInfo.resourceList, '/user/authentication')}">
	<script type="text/javascript">
		$.canAuthentication = true;
	</script>
</c:if>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>会员管理</title>
<script type="text/javascript">
	var dataGrid;
	var organizationTree;
	$(function() {
		organizationTree = $('#organizationTree').tree({
			url : '${ctx}/organization/tree',
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
							url : '${ctx}/user/dataGrid',
							fit : true,
							striped : true,
							rownumbers : true,
							pagination : true,
							singleSelect : true,
							idField : 'id',
							sortName : 'id',
							sortOrder : 'desc',
							pageSize : 50,
							pageList : [ 10, 20, 30, 40, 50, 100, 200, 300,
									400, 500 ],
							columns : [ [
									{
										width : '100',
										title : 'APP登录名',
										field : 'name',
										sortable : true
									},
									{
										width : '100',
										title : '真实名称',
										field : 'realName',
										sortable : true
									},
								
									{
										width : '60',
										title : 'code',
										field : 'code',
										sortable : true
									},
									{
										width : '80',
										title : '推广人',
										field : 'parentName',
										sortable : true
									},
									{
										width : '80',
										title : '登录名',
										field : 'loginName',
										sortable : true
									},
									{
										width : '40',
										title : '管理员',
										field : 'isAdmin',
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
										width : '60',
										title : '用户类型',
										field : 'userType',
										sortable : true,
										formatter : function(value, row, index) {
											var jsonObjs = $.parseJSON('${userType}');
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
										width : '60',
										title : '是否认证',
										field : 'authenticationStatus',
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
										width : '60',
										title : '是否可结算',
										field : 'settlementStatus',
										sortable : true,
										formatter : function(value, row, index) {
											switch (value) {
											case 0:
												return '关闭';
											case 1:
												return '开启';
											}
										}
									},
									{
										width : '60',
										title : '是否代理商',
										field : 'isChnl',
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
										title : '运营商ID',
										field : 'organizationId',
										hidden : true
									},
									{
										width : '140',
										title : '所属运营商',
										field : 'organizationName'
									},
									{
										width : '120',
										title : '创建时间',
										field : 'createDatetime',
										sortable : true
									},
									{

										width : '60',
										title : '状态',
										field : 'state',
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
											var str = '';
											if (row.isdefault != 0) {
												if ($.canEdit) {
													str += $
															.formatString(
																	'<a href="javascript:void(0)" onclick="editFun(\'{0}\');" >编辑</a>',
																	row.id);
													if(row.authenticationStatus==2){
														str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
														str += $
														.formatString(
																'<a href="javascript:void(0)" onclick="authenticationFun(\'{0}\');" >认证</a>',
																row.id);
													}
												}
												if ($.canDelete) {
													str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
													str += $
															.formatString(
																	'<a href="javascript:void(0)" onclick="deleteFun(\'{0}\');" >删除</a>',
																	row.id);
												}
											}
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
			width : 650,
			height : 600,
			href : '${ctx}/user/editPage?id=' + id,
			buttons : [ {
				text : '编辑',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
					var f = parent.$.modalDialog.handler.find('#userEditForm');
					f.submit();
				}
			} ]
		});
	}
	function authenticationFun(id) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$.modalDialog({
			title : '用户认证',
			width : 650,
			height : 600,
			href : '${ctx}/user/authenticationPage?id=' + id,
			buttons : [ {
				text : '通过',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
					var f = parent.$.modalDialog.handler.find('#authErrorInfo');
					authFun(id,true,f.val());
				}
			},{
				text : '不通过',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
					var f = parent.$.modalDialog.handler.find('#authErrorInfo');
					authFun(id,false,f.val());
				}
			}  ]
		});
	}
	function authFun(id,status,errorInfo){
		$.post('${ctx}/user/auth', {
			id : id,
			status:status,
			errorInfo:errorInfo
		}, function(result) {
			if (result.success) {
				parent.$.messager.alert('提示', result.msg, 'info');
				dataGrid.datagrid('reload');
				parent.$.modalDialog.openner_dataGrid.datagrid('reload');//之所以能在这里调用到parent.$.modalDialog.openner_dataGrid这个对象，是因为user.jsp页面预定义好了
				parent.$.modalDialog.handler.dialog('close');
			}
		}, 'JSON');
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
					<th>认证状态:</th>
					<td>
						<select id="authenticationStatus" name="authenticationStatus"
							class="easyui-combobox"
							data-options="width:140,height:29,editable:false,panelHeight:'auto'">
								<option selected value="">请选择</option>
								<option value="-1">未认证</option>
								<option value="1">已认证</option>
								<option value="0">认证失败</option>
								<option value="2">认证中</option>
						</select>
					</td>
					<th>创建时间:</th>
					<td><input name="createdatetimeStart" placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd HH:mm:ss'})"
						readonly="readonly" />至<input name="createdatetimeEnd"
						placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd HH:mm:ss'})"
						readonly="readonly" /> <a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-search',plain:true"
						onclick="searchFun();">查询</a><a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-cancel',plain:true"
						onclick="cleanFun();">清空</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:true,title:'用户列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
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
<c:if test="${fn:contains(sessionInfo.resourceList, '/user/agentEdit')}">
	<script type="text/javascript">
		$.canEdit = true;
	</script>
</c:if>
<c:if test="${fn:contains(sessionInfo.resourceList, '/user/agentAuth')}">
	<script type="text/javascript">
		$.canAuth = true;
	</script>
</c:if>
<c:if
	test="${fn:contains(sessionInfo.resourceList, '/user/agentUpgrade')}">
	<script type="text/javascript">
		$.canAgent = true;
	</script>
</c:if>
<c:if
	test="${fn:contains(sessionInfo.resourceList, '/user/agentUpgrade2')}">
	<script type="text/javascript">
		$.canAgent2 = true;
	</script>
</c:if>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户管理</title>
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
					url : '${ctx}/user/agentDataGrid',
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
						// 									{
						// 										width : '80',
						// 										title : '登录名',
						// 										field : 'loginName',
						// 										sortable : true
						// 									},
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
								var jsonObjs = $
									.parseJSON('${userType}');
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
							width : '80',
							title : '商家类型',
							field : 'merchantType',
							sortable : true,
							formatter : function(value, row, index) {
								var jsonObjs = $
									.parseJSON('${merchantType}');
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
							width : '120',
							title : '认证时间',
							field : 'authDateTime',
							sortable : true
						},
						{
							width : '120',
							title : '升钻时间',
							field : 'diamondDateTime',
							sortable : true
						},
						{
							width : '120',
							title : '升金时间',
							field : 'goldDateTime',
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
							width : 300,
							formatter : function(value, row, index) {
								var str = '';
								if (row.isdefault != 0) {
									if ($.canEdit) {
										str += $
											.formatString(
												'<a href="javascript:void(0)" onclick="editFun(\'{0}\');" >编辑</a>',
												row.id);

									}
									if ($.canAuth) {
										if (row.authenticationStatus == 2) {
											str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
											str += $
												.formatString(
													'<a href="javascript:void(0)" onclick="authenticationFun(\'{0}\');" >认证</a>',
													row.id);
										} else if (row.authenticationStatus != 2) {
											str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
											str += $
												.formatString(
													'<a href="javascript:void(0)" onclick="authenticationViewFun(\'{0}\');" >认证查看</a>',
													row.id);
										}
										if (row.merchantType == 90 || row.merchantType == 900) {
											str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
											str += $
												.formatString(
													'<a href="javascript:void(0)" onclick="authenticationMerchantFun(\'{0}\');" >商户认证</a>',
													row.id);
										} else if (row.merchantType == 1) {
											str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
											str += $
												.formatString(
													'<a href="javascript:void(0)" onclick="authenticationMerchantViewFun(\'{0}\');" >查看商家</a>',
													row.id);
										}
									}
									if ($.canAgent) {
										str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
										str += $
											.formatString(
												'<a href="javascript:void(0)" onclick="oneKeyUpgrade(\'{0}\');" >升级代理商</a>',
												row.id);
									}
									if ($.canAgent) {
										str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
										str += $
											.formatString(
												'<a href="javascript:void(0)" onclick="oneKeyUpgrade2(\'{0}\');" >升级运营中心</a>',
												row.id);
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

	function addFun() {
		parent.$.modalDialog({
			title : '运营商顶级用户',
			width : 450,
			height : 300,
			href : '${ctx}/user/addPage',
			buttons : [ {
				text : '添加',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid; //因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
					var f = parent.$.modalDialog.handler.find('#userAddForm');
					f.submit();
				}
			} ]
		});
	}

	function deleteFun(id) {
		if (id == undefined) { //点击右键菜单才会触发这个
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else { //点击操作里面的删除图标会触发这个
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$.messager.confirm('询问', '您是否要删除当前用户？', function(b) {
			if (b) {
				var currentUserId = '${sessionInfo.id}'; /*当前登录用户的ID*/
				if (currentUserId != id) {
					progressLoad();
					$.post('${ctx}/user/delete', {
						id : id
					}, function(result) {
						if (result.success) {
							parent.$.messager.alert('提示', result.msg, 'info');
							dataGrid.datagrid('reload');
						}
						progressClose();
					}, 'JSON');
				} else {
					parent.$.messager.show({
						title : '提示',
						msg : '不可以删除自己！'
					});
				}
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
			width : 650,
			height : 600,
			href : '${ctx}/user/editPage?id=' + id,
			buttons : [ {
				text : '编辑',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid; //因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
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
			buttons : [
				{
					text : '通过',
					handler : function() {
						parent.$.modalDialog.openner_dataGrid = dataGrid; //因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
						var f = parent.$.modalDialog.handler
							.find('#authErrorInfo');
						authFun(id, true, f.val());
					}
				},
				{
					text : '不通过',
					handler : function() {
						parent.$.modalDialog.openner_dataGrid = dataGrid; //因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
						var f = parent.$.modalDialog.handler
							.find('#authErrorInfo');
						authFun(id, false, f.val());
					}
				} ]
		});
	}
	function authenticationMerchantFun(id) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$.modalDialog({
			title : '商户认证',
			width : 650,
			height : 600,
			href : '${ctx}/user/authenticationMerchantPage?id=' + id,
			buttons : [
				{
					text : '通过',
					handler : function() {
						parent.$.modalDialog.openner_dataGrid = dataGrid; //因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
						var f = parent.$.modalDialog.handler
							.find('#authErrorInfo');
						var merchantType = parent.$.modalDialog.handler
							.find('#merchantType');
						authMerchantFun(id, merchantType.val(), true, f.val());
					}
				},
				{
					text : '不通过',
					handler : function() {
						parent.$.modalDialog.openner_dataGrid = dataGrid; //因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
						var f = parent.$.modalDialog.handler
							.find('#authErrorInfo');
						var merchantType = parent.$.modalDialog.handler
							.find('#merchantType');
						authMerchantFun(id, merchantType.val(), false, f.val());
					}
				} ]
		});
	}



	function oneKeyUpgrade(id) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}

		parent.$.messager.confirm('询问', '您确定要升级当前用户为代理商？', function(b) {
			if (b) {
				var currentUserId = '${sessionInfo.id}'; /*当前登录用户的ID*/
				if (currentUserId != id) {
					progressLoad();
					$.post(
						'${ctx}/user/agentUpgrade',
						{
							id : id
						},
						function(result) {
							if (result.success) {
								parent.$.messager.alert('提示', result.msg, 'info');
								dataGrid.datagrid('reload');
							}
							progressClose();
						},
						'JSON');
				} else {
					parent.$.messager.show({
						title : '提示',
						msg : '不可以升级自己！'
					});
				}
			}
		});
	}

	function oneKeyUpgrade2(id) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}

		parent.$.messager.confirm('询问', '您确定要升级当前用户为运营中心？', function(b) {
			if (b) {
				var currentUserId = '${sessionInfo.id}'; /*当前登录用户的ID*/
				if (currentUserId != id) {
					progressLoad();
					$.post(
						'${ctx}/user/agentUpgrade2',
						{
							id : id
						},
						function(result) {
							if (result.success) {
								parent.$.messager.alert('提示', result.msg, 'info');
								dataGrid.datagrid('reload');
							}
							progressClose();
						},
						'JSON');
				} else {
					parent.$.messager.show({
						title : '提示',
						msg : '不可以升级自己！'
					});
				}
			}
		});
	}

	function authenticationMerchantViewFun(id) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$.modalDialog({
			title : '商户认证',
			width : 650,
			height : 600,
			href : '${ctx}/user/authenticationMerchantPage?id=' + id,
			buttons : [ {
				text : '关闭',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid; //因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
					parent.$.modalDialog.handler.dialog('close');
				}
			} ]
		});
	}

	function authenticationViewFun(id) {
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
				text : '关闭',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid; //因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
					parent.$.modalDialog.handler.dialog('close');
				}
			} ]
		});
	}
	function authFun(id, status, errorInfo) {
		$.post('${ctx}/user/agentAuth', {
			id : id,
			status : status,
			errorInfo : errorInfo
		}, function(result) {
			if (result.success) {
				parent.$.messager.alert('提示', result.msg, 'info');
				dataGrid.datagrid('reload');
				parent.$.modalDialog.openner_dataGrid.datagrid('reload'); //之所以能在这里调用到parent.$.modalDialog.openner_dataGrid这个对象，是因为user.jsp页面预定义好了
				parent.$.modalDialog.handler.dialog('close');
			}
		}, 'JSON');
	}
	function authMerchantFun(id, merchantType, authStatus, errorInfo) {
		if (authStatus && merchantType == 90) {
			merchantType = 1;
		} else if (authStatus && merchantType == 900) {
			merchantType = 10;
		} else {
			merchantType = 91;
		}
		$.post('${ctx}/user/authMerchant', {
			id : id,
			merchantType : merchantType,
			errorInfo : errorInfo
		}, function(result) {
			if (result.success) {
				parent.$.messager.alert('提示', result.msg, 'info');
				dataGrid.datagrid('reload');
				parent.$.modalDialog.openner_dataGrid.datagrid('reload'); //之所以能在这里调用到parent.$.modalDialog.openner_dataGrid这个对象，是因为user.jsp页面预定义好了
				parent.$.modalDialog.handler.dialog('close');
			}
		}, 'JSON');
	}

	function searchFun() {
		dataGrid.datagrid('load', $.serializeObject($('#searchForm')));
	}
	function exportFun() {
		window.location.href = '${ctx}/user/exportExcel?'
		+ $('#searchForm').serialize();
	}
	function cleanFun() {
		$('#searchForm input').val('');
		dataGrid.datagrid('load', {});
	}
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false"
		style="height: 100px; overflow: hidden;background-color: #fff">
		<form id="searchForm">
			<table>
				<tr>
					<th>APP登录名:</th>
					<td><input name="loginName" placeholder="请输入用户姓名" /></td>
					<th>运营商:</th>
					<td><select id="organizationId" name="organizationId"
						style="width: 140px; height: 29px;" class="easyui-validatebox"></select></td>
					<th>认证状态:</th>
					<td><select id="authenticationStatus"
						name="authenticationStatus" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<option value="-1">未认证</option>
							<option value="1">已认证</option>
							<option value="0">认证失败</option>
							<option value="2">认证中</option>
					</select></td>
					<th>商户状态:</th>
					<td><select id="merchantType" name="merchantType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${merchantTypeObj}" var="obj">
								<option value="${obj.code}">${obj.text }</option>
							</c:forEach>
					</select></td>
				</tr>
				<tr>
					<th>真实姓名:</th>
					<td><input name="realName" placeholder="真实姓名"
						class="easyui-validatebox" /></td>
					<th>身份证号:</th>
					<td><input name="idNo" placeholder="身份证号"
						class="easyui-validatebox" /></td>
					<th>是否管理员:</th>
					<td><select id="isAdmin" name="isAdmin"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<option value="0">否</option>
							<option value="1">是</option>
					</select></td>
				</tr>
				<tr>
					<th>用户类型:</th>
					<td><select id="userType" name="userType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${userTypeObj}" var="obj">
								<option value="${obj.code}">${obj.text }</option>
							</c:forEach>
					</select></td>
					<th>下线层级:</th>
					<td><select id="subLevel" name="subLevel"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<option value="1">1级下线</option>
							<option value="2">2级下线</option>
							<option value="3">3级下线</option>
					</select></td>
					<th>创建时间:</th>
					<td><input name="createdatetimeStart" placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" /></td>
					<td>&nbsp;&nbsp;&nbsp;&nbsp;至&nbsp;&nbsp;&nbsp;</td>
					<td><input name="createdatetimeEnd" placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" /> <a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-search',plain:true"
						onclick="searchFun();">查询</a><a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-clear',plain:true"
						onclick="cleanFun();">清空</a> <%-- 						<c:if test="${isEdit == 2}"> --%>
						<!-- 						<a href="javascript:void(0);" --> <!-- 						class="easyui-linkbutton" -->
						<!-- 						data-options="iconCls:'icon-redo',plain:true" --> <!-- 						onclick="exportFun();">导出</a> -->
						<%-- 						</c:if> --%></td>
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:true,title:'用户列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
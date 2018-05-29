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
<title>运营商账户</title>
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
		dataGrid = $('#dataGrid').datagrid(
				{
					url : '${ctx}/orgAccount/dataGrid',
					striped : true,
					rownumbers : true,
					pagination : true,
					singleSelect : true,
					idField : 'id',
					sortName : 'id',
					sortOrder : 'asc',
					pageSize : 15,
					pageList : [ 10, 15, 20, 30, 40, 50 ],
					frozenColumns : [ [ {
						width : '120',
						title : '运营商名字',
						field : 'orgName'
					}, {
						width : '120',
						title : '类型',
						field : 'type',
						formatter : function(value, row, index) {
							var jsonObjs = $.parseJSON('${orgAccountType}');
							for (var i = 0; i < jsonObjs.length; i++) {
								var jsonobj = jsonObjs[i];
								if (jsonobj.code == value) {
									return jsonobj.text;
								}
							}
							return "未知类型";
						}
					} ] ],
					columns : [ [
							{
								width : '100',
								title : '可用金额',
								field : 'amt',
								formatter : function(value, row, index) {
									if (value <= row.limitAmt) {
										return '<font color=#FF0000 >' + value + ' </font>';
									} else {
										return value;
									}
								}

							},
							{
								width : '100',
								title : '锁定金额',
								field : 'lockAmt'
							},
							{
								width : '100',
								title : '限定金额',
								field : 'limitAmt'
							},
							{
								width : '80',
								title : '账户状态',
								field : 'status',
								formatter : function(value, row, index) {
									switch (value) {
									case 0:
										return '正常';
									case 1:
										return '冻结';
									}
								}
							},
							{
								field : 'action',
								title : '操作',
								width : 100,
								formatter : function(value, row, index) {
									var str = $.formatString(
											'<a href="javascript:void(0)" onclick="rechargeFun(\'{0}\');" >充值</a>',
											row.id);
									return str;
								}
							} ] ],
					toolbar : '#toolbar'
				});
	});

	function rechargeFun(id) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$.modalDialog({
			title : '确定',
			width : 600,
			height : 400,
			href : '${ctx}/orgAccount/rechargeOrgAccountPage?id=' + id,
			buttons : [ {
				text : '提交',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
					var f = parent.$.modalDialog.handler.find('#adjustOrgAccountForm');
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
	<div data-options="region:'north',border:false"
		style="height: 60px; overflow: hidden;background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					<th>运营商:</th>
					<td><select id="organizationId" name="organizationId"
						style="width: 140px; height: 29px;" class="easyui-validatebox"></select>
						</select> <a href="javascript:void(0);" class="easyui-linkbutton"
						data-options="iconCls:'icon_search',plain:true"
						onclick="searchFun();">查询</a><a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon_cancel',plain:true"
						onclick="cleanFun();">清空</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:false,title:'账户列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
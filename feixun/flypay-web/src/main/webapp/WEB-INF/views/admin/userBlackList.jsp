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
<title>用户黑名单</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid').datagrid(
				{
					url : '${ctx}/user/blackListDataGrid',
					striped : true,
					rownumbers : true,
					pagination : true,
					singleSelect : true,
					idField : 'id',
					sortName : 'id',
					sortOrder : 'desc',
					pageSize : 15,
					pageList : [ 10, 15, 20, 30, 40, 50, 100 ],
					frozenColumns : [ [ {
						width : '100',
						title : '真实姓名',
						field : 'realName'
					}, {
						width : '150',
						title : '身份证号码',
						field : 'idNo'
					} ] ],
					columns : [ [
							{
								width : '50',
								title : '状态',
								field : 'status',
								formatter : function(value, row, index) {
									switch (value) {
									case 1:
										return '已加入';
									case 0:
										return '已移出';
									}
								}
							},
							{
								width : '125',
								title : '创建时间',
								field : 'createTime',
								sortable : true
							},
							{
								field : 'action',
								title : '操作',
								width : 100,
								formatter : function(value, row, index) {
									var str = '';
									if (row.isdefault != 0) {
										var op = '';
										if (row.status == 1) {
											op = '移出黑名单';
										} else {
											str += $.formatString('用户已移出');
										}
										if (row.idNo != null) {
											str += $.formatString(
													'<a href="javascript:void(0)" onclick="blackFun(\'{0}\',\'{1}\');" >'
															+ op + '</a>',
													row.idNo, op);
										}
									}
									return str;
								}
							} ] ],
					toolbar : '#toolbar'
				});
	});

	function searchFun() {
		dataGrid.datagrid('load', $.serializeObject($('#searchForm')));
	}
	function cleanFun() {
		$('#searchForm input').val('');
		dataGrid.datagrid('load', {});
	}

	function blackFun(idNo, op) {
		if (op == "移出黑名单") {
			parent.$.messager.confirm('询问',
					'您是否要把当前用户移出黑名单，关联身份证号的所有账号将恢复正常认证？', function(b) {
						if (b) {
							blackCommonFun(idNo);
						}
					});
		}
	}

	function blackCommonFun(idNo) {
		if (idNo == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			idNo = rows[0].idNo;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		$.post('${ctx}/user/black', {
			idNo : idNo
		}, function(result) {
			if (result.success) {
				$.messager.alert('提示', result.msg, 'info');
				dataGrid.datagrid('reload');
			}
			progressClose();
		}, 'JSON');
	}
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false"
		style="height: 30px; overflow: hidden; background-color: #f4f4f4">
	</div>
	<div data-options="region:'center',border:false,title:'黑名单列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
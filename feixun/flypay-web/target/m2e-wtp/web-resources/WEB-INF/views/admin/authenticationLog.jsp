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
<title>认证管理</title>
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
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}/authentication/dataGrid',
			striped : true,
			rownumbers : true,
			pagination : true,
			singleSelect : true,
			idField : 'id',
			sortName : 'id',
			sortOrder : 'desc',
			pageSize : 15,
			pageList : [ 10, 15, 20, 30, 40, 50, 100, 200 ],
			columns : [ [ {
				width : '100',
				title : 'APP登录名',
				field : 'loginName'
			}, {
				width : '100',
				title : '运营商',
				field : 'organizationName'
			}, {
				width : '100',
				title : '类型',
				field : 'authType',
				formatter : function(value, row, index) {
					switch (value) {
					case 0:
						return '自动';
					case 1:
						return '用户人工认证';
					case 2:
						return '商家人工认证';
					}
				}
			}, {
				width : '100',
				title : '真实姓名',
				field : 'realName'
			}, {
				width : '150',
				title : '卡号',
				field : 'cardNo'
			}, {
				width : '150',
				title : '身份证',
				field : 'idNo'
			}, {
				width : '90',
				title : '预留手机号',
				field : 'phone'
			}, {
				width : '80',
				title : '状态',
				field : 'status',
				formatter : function(value, row, index) {
					switch (value) {
					case 0:
						return '不通过';
					case 1:
						return '通过';
					}
				}
			}, {
				width : '120',
				title : '创建时间',
				field : 'createTime'
			}, {
				width : '60',
				title : '错误码',
				field : 'errorCode'
			}, {
				width : '550',
				title : '错误信息',
				field : 'errorInfo'
			} ] ]
		});
	});

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
		style="height: 30px; overflow: hidden;background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					<th>APP登录名:</th>
					<td><input name="loginName" /></td>
					<th>身份证号:</th>
					<td><input name="idNo" /></td>
					<th>运营商:</th>
					<td><select id="organizationId" name="organizationId" style="width: 140px; height: 29px;" class="easyui-validatebox" ></select></td>
					<th>类型:</th>
					<td><select id="authType"
						name="authType" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<option value="1">用户人工认证</option>
							<option value="2">商家人工认证</option>
							<option value="0">自动认证</option>
					</select> <a href="javascript:void(0);"
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
	<div data-options="region:'center',border:false,title:'实名列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
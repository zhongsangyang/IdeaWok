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
<title>用户订单操作记录</title>
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
			url : '${ctx}/userOrderOperationRecord/dataGrid',
			striped : true,
			rownumbers : true,
			pagination : true,
			singleSelect : true,
			idField : 'id',
			sortName : 'id',
			sortOrder : 'desc',
			pageSize : 15,
			pageList : [ 10, 15, 20, 30, 40, 50, 100, 200, 300 ],
			columns : [ [ {
				width : '100',
				title : '登录名',
				field : 'loginName'
			}, {
				width : '100',
				title : '用户名',
				field : 'realName'
			}, {
				width : '100',
				title : '所属代理商',
				field : 'organizationName'
			}, {
				width : '245',
				title : '订单号',
				field : 'orderNum'
			}, {
				width : '80',
				title : '订单类型',
				field : 'orderType',
				formatter : function(value, row, index) {
					var jsonObjs = $.parseJSON('${orderType}');
					for (var i = 0; i < jsonObjs.length; i++) {
						var jsonobj = jsonObjs[i];
						if (jsonobj.code == value) {
							return jsonobj.text;
						}
					}
					return "未知类型";
				}
			}, {
				width : '80',
				title : '购买用途',
				field : 'transPayType',
				formatter : function(value, row, index) {
					if (value == 10) {
						return " 普通订单";
					}
					return "用户升级";
				}
			}, {
				width : '80',
				title : '订单状态',
				field : 'orderStatus',
				formatter : function(value, row, index) {
					var jsonObjs = $.parseJSON('${orderStatus}');
					for (var i = 0; i < jsonObjs.length; i++) {
						var jsonobj = jsonObjs[i];
						if (jsonobj.code == value) {
							return jsonobj.text;
						}
					}
					return "未知类型";
				}
			}, {
				width : '100',
				title : '操作者',
				field : 'operator'
			}, {
				width : '125',
				title : '操作时间',
				field : 'operationDatetime'
			}, {
				width : '100',
				title : '执行的操作',
				field : 'operationName'
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
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false"
		style="height: 80px; overflow: hidden; background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					<td>订单类型</td>
					<td><select id="orderType" name="orderType" class="easyui-combobox"
						data-options="width:160,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${orderTypeObj}" var="obj">
								<option value="${obj.code}">${obj.text }</option>
							</c:forEach>
					</select></td>
					<td>订单状态</td>
					<td><select id="orderStatus" name="orderStatus" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${orderStatusObj}" var="obj">
								<option value="${obj.code}">${obj.text }</option>
							</c:forEach>
					</select></td>

					<th>APP登录名:</th>
					<td><input id="loginName" name="loginName" type="text"
						class="easyui-validatebox" /></td>
				</tr>
				<th>订单号:</th>
				<td><input name="orderNum" style="width: 150px;" type="text"
					class="easyui-validatebox" /></td>
				<th>运营商:</th>
				<td><select id="organizationId" name="organizationId"
					style="width: 140px; height: 29px;" class="easyui-validatebox"></select></td>
				<td>购买用途</td>
				<td><select id="transPayType" name="transPayType"
					class="easyui-combobox"
					data-options="width:140,height:29,editable:false,panelHeight:'auto'">
						<option selected value="">请选择</option>
						<option value="10">普通订单</option>
						<option value="20">用户升级</option>
				</select><a href="javascript:void(0);" class="easyui-linkbutton"
					data-options="iconCls:'icon-search',plain:true"
					onclick="searchFun();">查询</a> <a href="javascript:void(0);"
					class="easyui-linkbutton"
					data-options="iconCls:'icon-clear',plain:true"
					onclick="cleanFun();">清空</a></td>

				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:false,title:'订单操作记录列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
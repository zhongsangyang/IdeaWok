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
<title>短信管理</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		$('#organizationId').combotree({
			url : '${ctx}/organization/treeByOperator',
			parentField : 'pid',
			lines : true,
			panelWidth : 'auto',
			panelHeight : 256,
		});
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}/message/dataGrid',
			striped : true,
			rownumbers : true,
			pagination : true,
			singleSelect : true,
			idField : 'id',
			sortName : 'id',
			sortOrder : 'asc',
			pageSize : 15,
			pageList : [ 10, 15, 20, 30, 40, 50, 100 ],
			frozenColumns : [ [ {
				width : '100',
				title : '手机号',
				field : 'phone'
			} ] ],
			columns : [ [ {
				width : '120',
				title : '运营商',
				field : 'organizationName'
			}, {
				width : '80',
				title : '短信类型',
				field : 'msgType',
				formatter : function(value, row, index) {
					switch (value) {
					case 10:
						return '注册短信';
					case 11:
						return '忘记密码';
					case 20:
						return '登录密码';
					case 30:
						return '交易密码';
					case 50:
						return '实名认证';
					case 60:
						return '商户认证';
					default:
						return value;
					}
				}
			}, {
				width : '80',
				title : '状态',
				field : 'status',
				formatter : function(value, row, index) {
					switch (value) {
					case 0:
						return '未验证';
					case 1:
						return '已验证';
					default:
						return value;
					}
				}

			}, {
				width : '120',
				title : '创建时间',
				field : 'createTime'
			}

			] ]
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
					<td><input name="phone" /></td>
					<th>运营商:</th>
					<td><select id="organizationId" name="organizationId"
						style="width: 140px; height: 29px;" class="easyui-validatebox"></select>
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
	<div data-options="region:'center',border:false,title:'短信列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
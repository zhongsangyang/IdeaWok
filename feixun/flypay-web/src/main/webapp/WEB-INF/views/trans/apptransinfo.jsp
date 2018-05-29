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
<title>APP流量</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}/appTransinfo/dataGrid',
			striped : true,
			rownumbers : true,
			pagination : true,
			singleSelect : true,
			idField : 'id',
			sortName : 'id',
			sortOrder : 'asc',
			pageSize : 15,
			pageList : [ 10, 15, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
			frozenColumns : [ [ {
				width : '120',
				title : '手机号码',
				field : 'phone',
				sortable : true
			} ] ],
			columns : [ [ {
				width : '80',
				title : '交易类型',
				field : 'transType',
				sortable : true,
				formatter : function(value, row, index) {
					var jsonObjs = $.parseJSON('${transType}');
					for (var i = 0; i < jsonObjs.length; i++) {
						var jsonobj = jsonObjs[i];
						if (jsonobj.code == value) {
							return jsonobj.text;
						}
					}
					return "未知类型";
				}
			}, {
				width : '120',
				title : '金额',
				field : 'amt',
				sortable : true
			}, {
				width : '120',
				title : '交易时间',
				field : 'transDatetime',
				sortable : true
			}, {
				width : '80',
				title : '对账状态',
				field : 'statementStuts',
				sortable : true,
				formatter : function(value, row, index) {
					var jsonObjs = $.parseJSON('${statementStatus}');
					for (var i = 0; i < jsonObjs.length; i++) {
						var jsonobj = jsonObjs[i];
						if (jsonobj.code == value) {
							return jsonobj.text;
						}
					}
					return "未知类型";
				}
			}, {
				width : '120',
				title : '对账时间',
				field : 'statementDatetime'
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
		style="height: 60px; overflow: hidden;background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					<th>手机号码:</th>
					<td><input name="phone" /></td>
					<th>交易类型:</th>
					<td><select id="statementStuts" name="statementStuts"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${statementStatusObj}" var="obj">
								<option value="${obj.code}">${obj.text }</option>
							</c:forEach>
					</select></td>
					<th>交易类型:</th>
					<td><select id="transType" name="transType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${transTypeObj}" var="obj">
								<option value="${obj.code}">${obj.text }</option>
							</c:forEach>
					</select></td>
				</tr>
				<tr>
					<th>交易时间:</th>
					<td colspan="5"><input name="transDatetimeStart" placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd HH:mm:ss'})"
						readonly="readonly" />&nbsp;&nbsp;&nbsp;&nbsp;至&nbsp;&nbsp;&nbsp;<input name="transDatetimeEnd"
						placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd HH:mm:ss'})"
						readonly="readonly" /> &nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:void(0);"
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
	<div data-options="region:'center',border:false,title:'参数列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
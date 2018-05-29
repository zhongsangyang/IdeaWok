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
<title>新闻咨询</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}/news/dataGrid',
			striped : true,
			rownumbers : true,
			pagination : true,
			singleSelect : true,
			idField : 'id',
			sortName : 'id',
			sortOrder : 'desc',
			pageSize : 15,
			pageList : [ 10, 15, 20, 30, 40, 50, 100 ],
			frozenColumns : [ [ 
			{
				field : 'ck',
				checkbox : true	             	   
			},                  
			{
				width : '150',
				title : '标题',
				field : 'title'
			} ,  {
				width : '120',
				title : '下载量',
				field : 'downSum'
			} ,{
				width : '120',
				title : '阅读量',
				field : 'readingSum'
			} ,{
				width : '120',
				title : '创建时间',
				field : 'creatime'
			}] ],
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
	function addFun(){
		window.location.href='${ctx}/news/add';
	}
	function deleteFun(){
		var arr = $('#dataGrid').datagrid('getSelections');
		if (arr.length <= 0) {
			alert("请选择要删除的记录");
			return false;
		}
		$.messager.confirm('提示信息', '确认删除?', function(r) {
			if (r) {
				progressLoad();
				$.ajax({
					type : 'post',
					url : '${ctx}/news/delete',
					cache : false,
					data : {id : arr[0].id},
					dataType : 'json',
					success : function(result) {
						dataGrid.datagrid('load', $.serializeObject($('#searchForm')));
						dataGrid.datagrid('load', {});
						//result = $.parseJSON(result);
						if (result.success) {
							parent.$.messager.alert('提示', result.msg, 'info');
							progressClose();
						} else {
							parent.$.messager.alert('错误', result.msg, 'error');
						}
					}
				}, 'JSON');
			}
		});
	}
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false"
		style="height: 30px; overflow: hidden;background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					<th>日期:</th>
					<td>
					<input name="stateDateStart" placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyyMMdd'})"
						readonly="readonly" />&nbsp;&nbsp;&nbsp;&nbsp;至&nbsp;&nbsp;&nbsp;<input
						name="stateDateEnd" placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyyMMdd'})"
						readonly="readonly" /> 
					<a href="javascript:void(0);"
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
	<div data-options="region:'center',border:false,title:'参数列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
	<div id="toolbar" style="display: none;">
			<a onclick="addFun();" href="javascript:void(0);"
				class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-add'">新增</a>
				
				<a onclick="deleteFun();" href="javascript:void(0);"
				class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-del'">删除</a>
	</div>
</body>
</html>
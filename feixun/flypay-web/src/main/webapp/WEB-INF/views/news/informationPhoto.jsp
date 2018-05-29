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
<title>新闻咨询图片页面</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}/newsPhoto/dataGrid',
			striped : true,
			rownumbers : true,
			pagination : true,
			singleSelect : true,
			idField : 'id',
			sortName : 'id',
			sortOrder : 'desc',
			pageSize : 15,
			pageList : [ 10, 15, 20, 30, 40, 50, 100 ],
			/* frozenColumns : [ 
			    [{
					field : 'ck',
					checkbox : true	             	   
				}] 
			], */
			columns : [ [ 
				{
					width : '50',
					title : '类型',
					field : 'type',
					formatter : function(value) {
						switch (value) {
						case '1':
							return '咨询';
						case '2':
							return '公告';
						}
					}
				} , 
				{
					width : '80',
					title : '展示位置',
					field : 'showLocation',
					formatter : function(value) {
						switch (value) {
						case 'document_library':
							return '默认文案库';
						case 'latest_technology':
							return '最新技术';
						}
					}
				} , 
				{
					width : '50',
					title : '标题1',
					field : 'text1'
				} ,
				{
					width : '120',
					title : '图片1的地址',
					field : 'photo1_url'
				} ,{
					width : '50',
					title : '标题2',
					field : 'text2'
				} ,  {
					width : '120',
					title : '图片2的地址',
					field : 'photo2_url'
				} ,{
					width : '50',
					title : '文本3',
					field : 'text3'
				} ,  {
					width : '120',
					title : '图片3的地址',
					field : 'photo3_url'
				} ,{
					width : '50',
					title : '文本4',
					field : 'text4'
				} ,  {
					width : '120',
					title : '图片4的地址',
					field : 'photo4_url'
				} ,{
					width : '50',
					title : '文本5',
					field : 'text5'
				} ,  {
					width : '120',
					title : '图片5的地址',
					field : 'photo5_url'
				} ,{
					width : '50',
					title : '文本6',
					field : 'text6'
				} ,  {
					width : '120',
					title : '图片6的地址',
					field : 'photo6_url'
				} ,{
					width : '50',
					title : '文本7',
					field : 'text7'
				} ,  {
					width : '120',
					title : '图片7的地址',
					field : 'photo7_url'
				} ,{
					width : '50',
					title : '文本8',
					field : 'text8'
				} ,  {
					width : '120',
					title : '图片8的地址',
					field : 'photo8_url'
				} ,{
					width : '50',
					title : '文本9',
					field : 'text9'
				} ,  {
					width : '120',
					title : '图片9的地址',
					field : 'photo9_url'
				} ,{
					width : '120',
					title : '创建时间',
					field : 'createTime'
				}
			] ],
			toolbar : '#toolbar'
		});
	});

	
	/*删除记录*/
	function deleteRecord(){
		var arr = $('#dataGrid').datagrid('getSelections');
		if(arr.length <= 0){
			alert("请选择要删除的记录");
			return false;
		}
		$.messager.confirm('提示信息','确认删除?',function(r){
			if(r){
				progressLoad();
				progressClose();
				$.ajax({
					type : 'post',
					url : '${ctx}/newsPhoto/delete',
					cache : false,
					data : {id : arr[0].id},
					dataType : 'json',
					success : function(result){
						dataGrid.datagrid('load', $.serializeObject($('#searchForm')));
						//dataGrid.datagrid('load', {});
						if(result.success){
							parent.$.messager.alert('提示', result.msg, 'info');
						}else{
							parent.$.messager.alert('错误',result.msg, 'error');
						}
					}
				},'JSON');
			}
		});
	}
	
	
	/*跳转至添加页面*/
	function addFun(){
		window.location.href='${ctx}/newsPhoto/add';
	}
	
	
	/*按条件查询*/
	function searchFun() {
		dataGrid.datagrid('load', $.serializeObject($('#searchForm')));
	}
	
	/*清空输入条件*/
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
					<th>日期:</th>
					<td>
					<input name="searchDateStart" placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyyMMdd'})"
						readonly="readonly" />&nbsp;&nbsp;&nbsp;&nbsp;至&nbsp;&nbsp;&nbsp;<input
						name="searchDateEnd" placeholder="点击选择时间"
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
				
				<a onclick="deleteRecord();" href="javascript:void(0);"
				class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-del'">删除</a>
	</div>


</body>
</html>

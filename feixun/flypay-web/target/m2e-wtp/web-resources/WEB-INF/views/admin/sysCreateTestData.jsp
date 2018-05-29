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
<title>王美凤造数据专用页面</title>
<script type="text/javascript">
	var dataGrid;
	$(function(){
	dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}/sysCreateTestData/dataGrid',
			striped : true,
			rownumbers : true,
			pagination : true,
			idField : 'id',
			sortName : 'createTime',
			sortOrder : 'desc',
			pageSize : 15,
			pageList : [ 10, 15, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
			columns :[[
			                 {
			                	 	width : '100',
									title : '用户id',
									field : 'user_id'
			                 },{
			                	 	width : '100',
									title : '用户姓名',
									field : 'user_name'
			                 },{
			                	 	width : '100',
			                	 	title : '可提现金额',
									field : 'avlBrokerage'
							 },{
								    width : '100',
									title : '总计佣金',
									field : 'totalBrokerage'
							 },{
								 	width : '100',
									title : '今日佣金',
									field : 'todayBrokerage'
							 },{
								 	width : '100',
									title : '昨日佣金',
									field : 'yesterdayBrokerage'
// 							 },{
// 								 	width : '300',
// 									title : '邀请记录(老版app用到的数据，可不填)',
// 									field : 'totalPersonNum'
							 },{
								 	width : '300',
									title : '实名认证人数--直接推荐用户',
									field : 'zauthTrue_zhijie'
							 },{
								 	width : '300',
									title : '未实名认证人数--直接推荐用户',
									field : 'zauthFalse_zhijie'
							 },{
								 	width : '300',
									title : '代理商人数--直接推荐用户',
									field : 'dzian_zhijie'
								 
							 },{
								 	width : '300',
									title : '实名认证人数--所有间接推荐用户',
									field : 'zauthTrue_suoyou'
							 },{
								 	width : '300',
									title : '未实名认证人数--所有间接推荐用户',
									field : 'zauthFalse_suoyou'
							 },{
								 	width : '300',
									title : '代理商--所有间接推荐用户',
									field : 'dzian_suoyou'
							 },{
								 	width : '125',
									title : '创建时间',
									field : 'create_time',
									sortable : true
							 }
			               ]],
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
	
	function addFun() {
		window.location.href='${ctx}/sysCreateTestData/addPage';
// 		parent.$.modalDialog({
// 			title : '造数据',
// 			width : 450,
// 			height : 300,
// 			href : '${ctx}/sysCreateTestData/addPage',
// 			buttons : [ {
// 				handler : function() {
// 					parent.$.modalDialog.openner_dataGrid = dataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
// 					var f = parent.$.modalDialog.handler.find('#addForm');
// 					f.submit();
// 				}
// 			} ]
// 		});
	}
	
	
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false"
		style="height: 60px; overflow: hidden;background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					<td>你想搞谁？</td>
					<td>
						<select id="user_id" name="user_id" class="easyui-combobox"
							data-options="width:140,height:29,editable:false,panelHeight:'auto'">
								<option selected value="">请选择</option>
								<option value="1">雷安18261542100</option>
								<option value="2">卢强 17321026899</option>
								<option value="10">冯梁13052222696</option>
								<option value="11">欧阳珍 18807069414</option>
								<option value="13">王美凤 18602222264</option>
								<option value="4">孙月  18068089860</option>
								<option value="18">苏容15900348990</option>
						</select>
					</td>
				
					<th>被搞日期:</th>
					<td>
						<input name="searchDateStart" placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" />
						
						&nbsp;&nbsp;&nbsp;&nbsp;至&nbsp;&nbsp;&nbsp;
						
						<input
						name="searchDateEnd" placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" /> 
						
						<a href="javascript:void(0);"
							class="easyui-linkbutton"
							data-options="iconCls:'icon-search',plain:true"
							onclick="searchFun();">查询</a>
						<a href="javascript:void(0);"
							class="easyui-linkbutton"
							data-options="iconCls:'icon-clear',plain:true"
							onclick="cleanFun();">清空</a>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:false,title:'参数列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
	<div id="toolbar" style="display: none;">
			<a onclick="addFun();" href="javascript:void(0);"
				class="easyui-linkbutton"
				data-options="plain:true,iconCls:'icon-add'">添加记录</a>
	</div>
</body>
</html>

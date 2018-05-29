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
<c:if test="${fn:contains(sessionInfo.resourceList, '/channel/edit')}">
	<script type="text/javascript">
		$.canEdit = true;
	</script>
</c:if>
<c:if test="${fn:contains(sessionInfo.resourceList, '/channel/delete')}">
	<script type="text/javascript">
		$.canDelete = true;
	</script>
</c:if>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>通道每日流入金额统计</title>
<script type="text/javascript">
	var dataGrid;
	$(function(){
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}/channelDayAmtMonitorController/dataGrid',
			striped : true,
			rownumbers : true,
			pagination : true,
			idField : 'id',
			sortName : 'sumTodayAmt',
			sortOrder : 'desc',
			pageSize : 15,
			pageList : [ 10, 15, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
			frozenColumns :[[
			                 {
									title : '渠道名称',
									field : 'name',
									formatter : function(value, row, index) {
										if(value == null){
											return null;
										}
										var jsonObjs = $
												.parseJSON('${statementType}');
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
									title : '通道类型',
									field : 'type',
									formatter : function(value, row, index) {
										if(value == null){
											return null;
										}
										var jsonObjs = $.parseJSON('${transType}');
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
									title : '累计流入金额',
									field : 'sumTodayAmt',
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
	
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<!-- 条件选择框 start -->
	<div data-options="region:'north',border:false" style="height: 50px; overflow: hidden; background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					<td>渠道名称</td>
					<td>
						<select id="type" name="name" class="easyui-combobox"
							data-options="width:140,height:29,editable:false,panelHeight:'auto'">
								<option selected value="">请选择</option>
								<c:forEach items="${statementTypeObj}" var="obj">
									<option value="${obj.code}">${obj.text }</option>
								</c:forEach>
						</select>
					</td>
					<td>通道类型</td>
					<td>
						<select id="type" name="type" class="easyui-combobox"
							data-options="width:140,height:29,editable:false,panelHeight:'auto'">
								<option selected value="">请选择</option>
								<c:forEach items="${transTypeObj}" var="obj">
									<option value="${obj.code}">${obj.text }</option>
								</c:forEach>
						</select>
					</td>
					
					<td>起始日期(包含当天)</td>
					<td>
						<input name="createTimeStart" placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" />
					</td>
					<td>&nbsp;&nbsp;&nbsp;&nbsp;至&nbsp;&nbsp;&nbsp;</td>
					<td>结束日期(包含当天)</td>
					<td>
						<input name="createTimeEnd" placeholder="点击选择时间"
							onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
							readonly="readonly" /> 
					</td>
					<td>
						<a href="javascript:void(0);" class="easyui-linkbutton"
						data-options="iconCls:'icon-search',plain:true"
						onclick="searchFun();">查询</a> 
						<a href="javascript:void(0);" class="easyui-linkbutton"
						data-options="iconCls:'icon-clear',plain:true"
						onclick="cleanFun();">清空</a>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<!-- 条件选择框 end -->
	
	<!-- 查询数据显示结果  start -->
	<div data-options="region:'center',border:false,title:'渠道列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
	<!-- 查询数据显示结果  end -->
</body>
</html>
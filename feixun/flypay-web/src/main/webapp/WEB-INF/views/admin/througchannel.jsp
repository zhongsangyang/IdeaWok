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
<title>直通车支付通道管理</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid')
				.datagrid(
						{
							url : '${ctx}/throughchannel/dataGrid',
							striped : true,
							rownumbers : true,
							pagination : true,
							singleSelect : true,
							idField : 'id',
							sortName : 'seq',
							sortOrder : 'asc',
							pageSize : 15,
							pageList : [ 10, 15, 20, 30, 40, 50, 100, 200, 300,
									400, 500 ],
							frozenColumns : [ [
									{
										width : '120',
										title : '渠道名称',
										field : 'name',
										sortable : true,
										formatter : function(value, row, index) {
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
										width : '150',
										title : '渠道详细',
										field : 'detailName'
									},
									{
										width : '120',
										title : '类型',
										field : 'type',
										sortable : true,
										formatter : function(value, row, index) {
											var jsonObjs = $
													.parseJSON('${transType}');
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
										width : '120',
										title : '用户类型',
										field : 'userType',
										sortable : true,
										formatter : function(value, row, index) {
											var jsonObjs = $
													.parseJSON('${channelUserType}');
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
										width : '80',
										title : '通道真实费率',
										field : 'realRate'
									},
									{
										width : '80',
										title : '展示用户费率',
										field : 'showRate'
									},
									{
										width : '100',
										title : '通道返佣费率',
										field : 'commissionRate'
									},
									{
										width : '80',
										title : '单笔最低限额',
										field : 'minTradeAmt'
									},
									{
										width : '80',
										title : '单笔最高限额',
										field : 'maxTradeAmt'
									},
									{
										width : '80',
										title : '通道最低限额',
										field : 'minChannelAmt'
									},
									{
										width : '80',
										title : '通道最高限额',
										field : 'maxChannelAmt'
									},
									{
										width : '80',
										title : '今日累计',
										field : 'todayAmt'
									},
									{
										width : '80',
										title : '每日最高值',
										field : 'maxAmtPerDay'
									},
									{
										width : '60',
										title : '序号',
										field : 'seq'
									},
									{
										width : '80',
										title : '状态',
										field : 'status',
										sortable : true,
										formatter : function(value, row, index) {
											if (value == 3) {
												return '正常';
											}else if(value == 4){
												return '审核中';
											}else if(value == 5){
												return '开通失败';
											}
										}
									},
									{
										field : 'action',
										title : '操作',
										width : '280',
										formatter : function(value, row, index) {
											var str = "";
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
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false"
		style="height: 50px; overflow: hidden;background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					
					<th>通道类型</th>
					<td>
					
					<select id="type" name="type" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${transTypeObj}" var="obj">
								<option value="${obj.code}">${obj.text }</option>
							</c:forEach>
					</select>
					
					
					<a href="javascript:void(0);" class="easyui-linkbutton"
						data-options="iconCls:'icon-search',plain:true"
						onclick="searchFun();">查询</a> <a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-clear',plain:true"
						onclick="cleanFun();">清空</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:false,title:'渠道列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
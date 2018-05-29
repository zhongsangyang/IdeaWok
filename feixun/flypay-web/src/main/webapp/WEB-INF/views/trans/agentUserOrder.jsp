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
<title>用户订单</title>
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
		dataGrid = $('#dataGrid')
				.datagrid(
						{
							url : '${ctx}/userOrder/dataGrid',
							striped : true,
							rownumbers : true,
							pagination : true,
							singleSelect : true,
							idField : 'id',
							sortName : 'id',
							sortOrder : 'desc',
							pageSize : 15,
							pageList : [ 10, 15, 20, 30, 40, 50, 100, 200, 300,
									400, 500 ],
							frozenColumns : [ [ {
								width : '100',
								title : '登录名',
								field : 'userPhone',
								sortable : true
							}, {
								width : '100',
								title : '用户名',
								field : 'userName',
								sortable : true
							} ] ],
							columns : [ [
									{
										width : '200',
										title : '订单号',
										field : 'orderNum'
									},
									{
										width : '220',
										title : '交易流水号',
										field : 'payOrderPayNo'
									},
									{
										width : '80',
										title : '订单类型',
										field : 'type',
										formatter : function(value, row, index) {
											var jsonObjs = $
													.parseJSON('${orderType}');
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
										width : '60',
										title : '下单金额',
										field : 'orgAmt',
										sortable : true
									},
									{
										width : '60',
										title : '费用',
										field : 'fee',
										sortable : true
									},
									{
										width : '80',
										title : '购买用途',
										field : 'transPayType',
										formatter : function(value, row, index) {
											if (value == 10) {
												return " 普通订单";
											}
											return "用户升级";
										}
									},
									{
										width : '80',
										title : '入账类型',
										field : 'inputAccType',
										formatter : function(value, row, index) {
											var jsonObjs = $
													.parseJSON('${bigTranType}');
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
										width : '80',
										title : '提现类型',
										field : 'payType',
										formatter : function(value, row, index) {
											if (1 == value) {
												return "T1";
											}
											return "实时";
										}
									},
									{
										width : '80',
										title : '<font color=#FF0000 >订单状态</font>',
										field : 'status',
										formatter : function(value, row, index) {
											var jsonObjs = $
													.parseJSON('${orderStatus}');
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
										width : '320',
										title : '描述',
										field : 'description'
									},
									{
										width : '320',
										title : '支付错误',
										field : 'errorInfo'
									},
									{
										width : '120',
										title : '创建时间',
										field : 'createTime',
										sortable : true
									},
									{
										width : '120',
										title : '完成时间',
										field : 'payOrderFinishDate',
										sortable : true
									}/* ,
									{
										field : 'action',
										title : '操作',
										width : 350,
										formatter : function(value, row, index) {
											var str = '';
											if (row.status == 300
													&& (row.type == 200
															|| row.type == 210
															|| row.type == 220
															|| row.type == 300
															|| row.type == 310
															|| row.type == 320
															|| row.type == 500
															|| row.type == 520
															|| row.type == 550
															|| row.type == 900
															|| row.type == 910
															|| row.type == 920
															|| row.type == 1000
															|| row.type == 1010
															|| row.type == 1020
															|| row.type == 1100
															|| row.type == 1110
															|| row.type == 1120
															|| row.type == 1200
															|| row.type == 1210 || row.type == 1220)) {
												str += $
														.formatString(
																'<a href="javascript:void(0)" onclick="reSearchOrder(\'{0}\');" >重新查询</a>',
																row.orderNum);
											}
											return str;
										}
										
										
									} */ ] ],
							toolbar : '#toolbar'
						});
	});

	function reSearchOrder(orderNum) {
		if (orderNum) {
			$.post('${ctx}/userOrder/reSearchOrder', {
				orderNum : orderNum
			}, function(result) {
				if (result.success) {
					dataGrid.datagrid('reload');
				}
				progressClose();
			}, 'JSON');
		}
	}
	function exportExcelFun() {

		window.location.href = '${ctx}/userOrder/exportExcel?'
				+ $('#searchForm').serialize();
	}
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
		style="height: 80px; overflow: hidden;background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					<td>订单类型</td>
					<td><select id="type" name="type" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${orderTypeObj}" var="obj">
								<option value="${obj.code}">${obj.text }</option>
							</c:forEach>
					</select></td>
					<td>订单状态</td>
					<td><select id="status" name="status" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${orderStatusObj}" var="obj">
								<option value="${obj.code}">${obj.text }</option>
							</c:forEach>
					</select></td>

					<th>APP登录名:</th>
					<td><input name="userPhone" type="text"
						class="easyui-validatebox" /></td>
					<th>订单创建日期:</th>
					<td><input name="payDatetimeStart" placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" />&nbsp;&nbsp;&nbsp;&nbsp;至&nbsp;&nbsp;&nbsp;<input
						name="payDatetimeEnd" placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" /></td>

				</tr>
				<th>运营商:</th>
				<td><select id="organizationId" name="organizationId"
					style="width: 140px; height: 29px;" class="easyui-validatebox"></select></td>
				<th>订单号:</th>
				<td><input name="orderNum" style="width: 150px;" type="text"
					class="easyui-validatebox" /></td>
				<th>流水号:</th>
				<td><input name="returnOrderNum" style="width: 150px;"
					type="text" class="easyui-validatebox" /></td>
				<td>购买用途</td>
				<td><select id="transPayType" name="transPayType" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<option value="10">普通订单</option>
							<option value="20">用户升级</option>
					</select></td>	<td>收单类型</td>
					<td><select id="cdType" name="cdType" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<option value="C">提现</option>
							<option value="D">收单</option>
					</select><a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-search',plain:true"
					onclick="searchFun();">查询</a> <a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-clear',plain:true"
					onclick="cleanFun();">清空</a>
					 <a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-download',plain:true"
					onclick="exportExcelFun();">下载</a>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:false,title:'订单列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
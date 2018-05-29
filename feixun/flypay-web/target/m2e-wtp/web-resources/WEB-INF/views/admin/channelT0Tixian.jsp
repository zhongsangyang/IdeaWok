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
<title>通道T0提现</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid')
				.datagrid(
						{
							url : '${ctx}/channelT0Tixian/dataGrid',
							striped : true,
							rownumbers : true,
							pagination : true,
							singleSelect : true,
							idField : 'id',
							sortName : 'id',
							sortOrder : 'desc',
							pageSize : 15,
							pageList : [ 10, 15, 20, 30, 40, 50 ],
							frozenColumns : [ [
									{
										width : '120',
										title : '渠道名称',
										field : 'channelName',
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
									}, {
										width : '150',
										title : '渠道详细',
										field : 'channelDetailName'
									}, {
										width : '220',
										title : '订单号',
										sortable : true,
										field : 'orderNum'

									} ] ],
							columns : [ [
									{
										width : '80',
										title : '金额',
										field : 'amt'
									},
									{
										width : '80',
										title : '提现手续费',
										field : 'drawFee'
									},
									{
										width : '100',
										title : '交易手续费',
										field : 'tradeFee'
									},
									{
										width : '150',
										title : '创建时间',
										sortable : true,
										field : 'createDate'
									},
									{
										width : '150',
										title : '完成时间',
										field : 'finishDate'
									},
									{
										width : '80',
										title : '交易状态',
										field : 'status',
										sortable : true,
										formatter : function(value, row, index) {
											if (value == 100) {
												return '成功';
											} else if (value == 200) {
												return '失败';
											} else if (value == 300) {
												return '进行中';
											}
										}
									},
									{
										width : '80',
										title : '对账状态',
										field : 'sltStatus',
										sortable : true,
										formatter : function(value, row, index) {
											if (value == 100) {
												return '成功';
											} else if (value == 200) {
												return '失败';
											} else if (value == 300) {
												return '未开始';
											}
										}
									},
									{
										field : 'action',
										title : '操作',
										width : 120,
										formatter : function(value, row, index) {
											var str = "";
											str += $
													.formatString(
															'<a href="javascript:void(0)" onclick="reSendFun(\'{0}\');" >重新查询</a>',
															row.id);
											return str;
										}
									} ] ],
							toolbar : '#toolbar'
						});
	});
	function reSendFun(id) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		$.post('${ctx}/channelT0Tixian/reSearch', {
			id : id
		}, function(result) {
			if (result.success) {
				parent.$.messager.alert('提示', result.msg, 'info');
				dataGrid.datagrid('reload');
			}
			progressClose();
		}, 'JSON');
	}
	function searchFun() {
		dataGrid.datagrid('load', $.serializeObject($('#searchForm')));
	}
	function exportFun() {
		window.location.href = '${ctx}/channelT0Tixian/exportExcel?'
				+ $('#searchForm').serialize();
	}
	function cleanFun() {
		$('#searchForm input').val('');
		dataGrid.datagrid('load', {});
	}
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false"
		style="height: 60px; overflow: hidden;background-color: #fff">
		<form id="searchForm">
			<table>
				<tr>
					<th>状态:</th>
					<td><select id="status" name="status" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<option value="100">成功</option>
							<option value="200">失败</option>
							<option value="300">未开始</option>
					</select></td>
					<th>创建时间:</th>
					<td><input name="createDatetimeStart" placeholder="开始时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" /></td>
					<td>&nbsp;&nbsp;&nbsp;&nbsp;至&nbsp;&nbsp;&nbsp;</td>
					<td><input name="createDatetimeEnd" placeholder="截止时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" /> <a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-search',plain:true"
						onclick="searchFun();">查询</a><a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-clear',plain:true"
						onclick="cleanFun();">清空</a><a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-redo',plain:true"
						onclick="exportFun();">导出</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:false,title:'提现列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
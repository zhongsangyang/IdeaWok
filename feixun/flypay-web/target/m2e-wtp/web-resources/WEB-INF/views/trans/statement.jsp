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
<title>对账单跟踪</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}/statement/dataGrid',
			striped : true,
			rownumbers : true,
			pagination : true,
			singleSelect : true,
			idField : 'id',
			sortName : 'id',
			sortOrder : 'desc',
			pageSize : 15,
			pageList : [ 10, 15, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
			frozenColumns : [ [ {
				width : '150',
				title : '对账日期',
				field : 'statemtentDate'
			}, {
				width : '100',
				title : '类型',
				field : 'stsType',
				formatter : function(value, row, index) {
					var jsonObjs = $.parseJSON('${orderType}');
					for (var i = 0; i < jsonObjs.length; i++) {
						var jsonobj = jsonObjs[i];
						if (jsonobj.code == value) {
							return jsonobj.text;
						}
					}
					return value;
				}
			} ] ],
			columns : [ [ {
				width : '100',
				title : '对账单成功笔数',
				field : 'successNum'

			}, {
				width : '100',
				title : '对账单成功金额',
				field : 'successAmt'
			}, {
				width : '100',
				title : '对账单费用',
				field : 'feeAmt'
			}, {
				width : '100',
				title : '对账成功交易数量',
				field : 'stsSuccussNum'
			}, {
				width : '100',
				title : '对账失败交易数量',
				field : 'stsFailNum'
			}, {
				width : '100',
				title : '对账自动处理数量',
				field : 'stsAutoDealNum'
			}, {
				width : '100',
				title : '冻结账户数量',
				field : 'stsFreezeAccountNum'
			}, {
				width : '100',
				title : '冻结佣金数量',
				field : 'stsFreezeBrokerageNum'
			}, {
				width : '120',
				title : '创建时间',
				field : 'createDate',
				sortable : true
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
	
	function dowoan() {
		if($('#statemtentDate').val()==""){
			alert("请选择时间");
			return false;
		}
		window.location.href = '${ctx}/statement/downloadPinganStatement?statemtentDate='+$('#statemtentDate').val();
	}
	
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false"
		style="height: 30px; overflow: hidden;background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					<th>处理时间:</th>
					<td><input id="createDateStart" name="createDateStart"
						placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" /> -<input id="createDateEnd"
						name="createDateEnd" placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" /> <a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-search',plain:true"
						onclick="searchFun();">查询</a><a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-clear',plain:true"
						onclick="cleanFun();">清空</a></td>
						<th>对账单日期</th>
						<td><input id="statemtentDate" name="statemtentDate"
						placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
						readonly="readonly" />
						<a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-download',plain:true"
						onclick="dowoan();">下载</a></td>
						
				</tr>
				
			</table>
			<table>
				<tr>
					
					
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:false,title:'对账单日志列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
	<div id="toolbar" style="display: none;"></div>
</body>
</html>
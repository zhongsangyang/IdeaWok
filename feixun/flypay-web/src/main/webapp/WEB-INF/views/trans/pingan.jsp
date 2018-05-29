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
<title>平安请求跟踪</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}/pingan/dataGrid',
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
				title : '交易序列号',
				field : 'tradeSn',
				sortable : true
			}, {
				width : '200',
				title : '文件名',
				field : 'fileName',
				sortable : true

			} ] ],
			columns : [ [ {
				width : '100',
				title : '状态',
				field : 'status',
				sortable : true

			}, {
				width : '100',
				title : '总数',
				field : 'totalNum',
				sortable : true
			}, {
				width : '100',
				title : '总金额',
				field : 'totalAmount',
				sortable : true
			}, {
				width : '300',
				title : '信息描述',
				field : 'description',
				sortable : true
			}, {
				width : '120',
				title : '创建时间',
				field : 'createTime',
				sortable : true
			} ] ],
			toolbar : '#toolbar'
		});
	});
	//提交代付文件
	function sendT1OrderFun() {
		var dealDate = $('#dealDate').val();
		if (dealDate == null || dealDate == '') {
			alert("请输入处理日期");
			return;
		}
		$.post('${ctx}/pingan/sendT1Order', {
			dealDate : dealDate
		}, function(result) {
			alert(result.msg);
			dataGrid.datagrid('load', $.serializeObject($('#searchForm')));
		}, 'JSON');
	}
	//发送代付指令
	function dealT1OrderResult() {
		var dealDate = $('#dealDate').val();
		if (dealDate == null || dealDate == '') {
			alert("请输入处理日期");
			return;
		}
		$.post('${ctx}/pingan/dealT1OrderResult', {
			dealDate : dealDate
		}, function(result) {
			alert(result.msg);
			dataGrid.datagrid('load', $.serializeObject($('#searchForm')));
		}, 'JSON');
	}
	
	function downloadZanshanfuT1OrderFun() {
		var dealDate = $('#dealDate').val();
		if (dealDate == null || dealDate == '') {
			alert("请输入处理日期");
			return;
		}
		window.location.href = '${ctx}/pingan/exportZanshanfuExcel?dealDate='+dealDate;
	}
	
	function searchT1OrderResult() {
		var dealDate = $('#dealDate').val();
		if (dealDate == null || dealDate == '') {
			alert("请输入处理日期");
			return;
		}
		$.post('${ctx}/pingan/searchT1OrderResult', {
			dealDate : dealDate
		}, function(result) {
			alert(result.msg);
			dataGrid.datagrid('load', $.serializeObject($('#searchForm')));
		}, 'JSON');
	}
	var sendT1 = false;
	//发送代付指令
	function sendT1CmdFun() {
		var dealDate = $('#dealDate').val();
		if (dealDate == null || dealDate == '') {
			alert("请输入处理日期");
			return;
		}
		parent.$.messager.confirm('询问', '您是否发送代付指令？', function(b) {
			if (b) {
				$.post('${ctx}/pingan/sendT1CmdFun', {
					dealDate : dealDate
				}, function(result) {
					alert(result.msg);
					dataGrid.datagrid('load', $
							.serializeObject($('#searchForm')));
				}, 'JSON');
				return;
			}
			
		});

	}
	//下载订单
	function manalDownload() {
		var dealDate = $('#dealDate').val();
		if (dealDate == null || dealDate == '') {
			alert("请输入处理日期");
			return;
		}
		$.post('${ctx}/pingan/manualDownloadSts', {
			dealDate : dealDate
		}, function(result) {
			alert(result.msg);
			dataGrid.datagrid('load', $.serializeObject($('#searchForm')));
		}, 'JSON');
	}
	
	//处理对账单
	function dealDownloadSts() {
		var dealDate = $('#dealDate').val();
		if (dealDate == null || dealDate == '') {
			alert("请输入处理日期");
			return;
		}
		$.post('${ctx}/pingan/dealDownloadSts', {
			dealDate : dealDate
		}, function(result) {
			alert(result.msg);
			dataGrid.datagrid('load', $.serializeObject($('#searchForm')));
		}, 'JSON');
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
		style="height: 30px; overflow: hidden;background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					<th>处理时间:</th>
					<td><input id="createTime" name="createTime"
						placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyyMMdd'})"
						readonly="readonly" /> <a href="javascript:void(0);"
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
	<div data-options="region:'center',border:false,title:'对账日志列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
	<div id="toolbar" style="display: none;">
		<c:if
			test="${fn:contains(sessionInfo.resourceList, '/pingan/sendT1')}">
			<input id="dealDate" name="dealDate" placeholder="点击选择时间"
				onclick="WdatePicker({readOnly:true,dateFmt:'yyyyMMdd'})"
				readonly="readonly" />
			<a onclick="sendT1OrderFun();" href="javascript:void(0);"
				class="easyui-linkbutton"
				data-options="plain:true,iconCls:'icon-undo'">发送T1账单</a>
			<a onclick="sendT1CmdFun();" href="javascript:void(0);"
				class="easyui-linkbutton"
				data-options="plain:true,iconCls:'icon-undo'">发送T1代付指令</a>
			<a onclick="searchT1OrderResult();" href="javascript:void(0);"
				class="easyui-linkbutton"
				data-options="plain:true,iconCls:'icon-btn'">查询下载T1结果</a>
			<a onclick="dealT1OrderResult();" href="javascript:void(0);"
				class="easyui-linkbutton"
				data-options="plain:true,iconCls:'icon-btn'">T1订单处理账单</a>
			<a onclick="manalDownload();" href="javascript:void(0);"
				class="easyui-linkbutton"
				data-options="plain:true,iconCls:'icon-btn'">人工下载处理对账单</a>
			<a onclick="dealDownloadSts();" href="javascript:void(0);"
				class="easyui-linkbutton"
				data-options="plain:true,iconCls:'icon-btn'">人工处理对账单</a>
				
				<a onclick="downloadZanshanfuT1OrderFun();" href="javascript:void(0);"
				class="easyui-linkbutton"
				data-options="plain:true,iconCls:'icon-btn'">攒善付T1账单</a>
		</c:if>
	</div>
</body>
</html>
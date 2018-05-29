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
<c:if
	test="${fn:contains(sessionInfo.resourceList, '/userOrder/affirmOrderStatus')}">
	<script type="text/javascript">
		$.canEdit = true;
	</script>
</c:if>
<title>用户订单</title>
<script type="text/javascript">

	var useFul = 0; //0可用  1不可用
	var flag = true; //标志变量，防止用户多次操作
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
					pageList : [ 10, 15, 20, 30, 40, 50, 100, 200, 300 ],
					frozenColumns : [ [ {
						width : '100',
						title : '登录名',
						field : 'userPhone'
					}, {
						width : '100',
						title : '用户名',
						field : 'userName'
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
						/* {
							width : '60',
							title : '入账类型',
							field : 'channelType'
						}, */
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
							width : '60',
							title : '手续费率',
							field : 'personRate',
							sortable : true
						},
						
						{
							width : '60',
							title : '服务费',
							field : 'srvFee',
							sortable : true
						},
						/* {
							width : '60',
							title : '手续费中包含的提现费率',
							field : 'extractFee',
							sortable : true
						}, */
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
							width : '150',
							title : '入账卡尾号（后四位）',
							field : 'cardEndFourNum'
						},
						{
							width : '320',
							title : '支付错误',
							field : 'errorInfo'
						},
						{
							width : '120',
							title : '手续费率',
							field : 'personRate'
						},
						{
							width : '120',
							title : '分润费率',
							field : 'shareRate'
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
						},
						{
							width : '100',
							title : '代理名称',
							field : 'organizationName'
						},
						{
							width : '120',
							title : '通道名称',
							field : 'channelName'
						},
						{
							field : 'action',
							title : '操作',
							width : 350,
							formatter : function(value, row, index) {
								var str = '';


								if ($.canEdit) {
									if (row.status == 300 || row.status == 500) {
										str += $
											.formatString(
												'<a href="javascript:void(0)" onclick="orderAffirmFun(\'{0}\',1);" >确认成功</a>',
												row.id);
										str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
									}
									str += $
										.formatString(
											'<a href="javascript:void(0)" onclick="orderAffirmFun(\'{0}\',0);" >确认失败</a>',
											row.id);
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
										|| row.type == 551
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
										str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
										str += $
											.formatString(
												'<a href="javascript:void(0)" onclick="reSearchOrder(\'{0}\');" >重新查询</a>',
												row.orderNum);
									}
								}
								return str;
							}
						} ] ],
					toolbar : '#toolbar'
				});
	});

	//倒计时
	function reloadTime() {
		setTimeout('funcTime()', 10000);
	}

	function funcTime() {
		useFul = 0;
		$('#searchData').css('color', '#444444');
	}


	function orderAffirmFun(id, status) {
		if (flag) { //标志变量防止用户连续点击多次
			flag = false; //点击一次后设为false
			if (id == undefined) {
				var rows = dataGrid.datagrid('getSelections');
				id = rows[0].id;
			} else {
				dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
			}
			$.post('${ctx}/userOrder/affirmOrderStatus', {
				id : id,
				status : status
			}, function(result) {
				if (result.success) {
					parent.$.messager.alert('提示', '操作成功', 'info', function() {
						flag = true; //执行结束改为true
					});
					dataGrid.datagrid('reload');
				}
				progressClose();
			}, 'JSON');
		} else {
			alert("请不要重复操作！");
		}
	}
	function reSearchOrder(orderNum) {
		flag = false;
		if (orderNum) {
			$.post('${ctx}/userOrder/reSearchOrder', {
				orderNum : orderNum
			}, function(result) {
				if (result.success) {
					parent.$.messager.alert('提示', result.msg, 'info');
					dataGrid.datagrid('reload');
				}
				progressClose();
			}, 'JSON');
		}
	}

	function searchFun() {
		if (useFul == '0') {
			useFul = 1;
			reloadTime(); //倒计时
			$('#searchData').css('color', '#FF0080');
			dataGrid.datagrid('load', $.serializeObject($('#searchForm')));
		} else {
			alert("60秒內不能重复点击查询按钮，请等待。");
		}
	}
	function exportExcelFun() {
	
		window.location.href = '${ctx}/userOrder/exportExcel?'
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
		style="height: 180px; overflow: hidden;background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					<td>订单类型</td>
					<td><select id="type" name="type" class="easyui-combobox"
						data-options="width:160,height:29,editable:false,panelHeight:'auto'">
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
					<td><input id="userPhone" name="userPhone" type="text"
						class="easyui-validatebox" /></td>
				</tr>
				<th>订单号:</th>
				<td><input name="orderNum" style="width: 150px;" type="text"
					class="easyui-validatebox" /></td>
				<th>流水号:</th>
				<td><input name="returnOrderNum" style="width: 150px;"
					type="text" class="easyui-validatebox" /></td>
				<th>运营商:</th>
				<td><select id="organizationId" name="organizationId"
					style="width: 140px; height: 29px;" class="easyui-validatebox"></select></td>


				</tr>
				<th>订单创建日期:</th>
				<td><input id="payDatetimeStart" name="payDatetimeStart" placeholder="点击选择时间"
					onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
					readonly="readonly" /></td>
				<td>&nbsp;&nbsp;&nbsp;&nbsp;至&nbsp;&nbsp;&nbsp;</td>
				<td><input id="payDatetimeEnd" name="payDatetimeEnd" placeholder="点击选择时间"
					onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
					readonly="readonly" /></td>
				<td>收单类型</td>
				<td><select id="cdType" name="cdType" class="easyui-combobox"
					data-options="width:140,height:29,editable:false,panelHeight:'auto'">
						<option selected value="">请选择</option>
						<option value="C">提现</option>
						<option value="D">收单</option>
				</select></td>

				</tr>
				<th>订单完成日期:</th>
				<td><input id="finishDatetimeStart" name="finishDatetimeStart" placeholder="点击选择时间"
					onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
					readonly="readonly" /></td>
				<td>&nbsp;&nbsp;&nbsp;&nbsp;至&nbsp;&nbsp;&nbsp;</td>
				<td><input id="finishDatetimeEnd" name="finishDatetimeEnd" placeholder="点击选择时间"
					onclick="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})"
					readonly="readonly" /></td>
				<td>购买用途</td>
				<td><select id="transPayType" name="transPayType"
					class="easyui-combobox"
					data-options="width:140,height:29,editable:false,panelHeight:'auto'">
						<option selected value="">请选择</option>
						<option value="10">普通订单</option>
						<option value="20">用户升级</option>
				</select></td>
				</tr>
				<tr>
					<td>直通车类型</td>
					<td><select id="cdType" name="ztcType" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<option value="SHENFUZTC">申孚直通车</option>
							<option value="MINGSHENGZHITONGCHE">民生直通车</option>
							<option value="XINKKEZHITONGCHE">新客直通车</option>
							<option value="YILIANZHIFUZTC">易联直通车</option>
							<option value="ZHEYANGZTC">哲扬直通车</option>
							<option value="PINGANPAYZHITONGCHE">平安直通车</option>
					</select></td>
					<td></td>
					<td><a id="searchData" href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-search',plain:true"
						onclick="searchFun();">查询</a> <a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-clear',plain:true"
						onclick="cleanFun();">清空</a> 
						<a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-download',plain:true"
						onclick="exportExcelFun();">下载</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:false,title:'订单列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
</body>
</html>
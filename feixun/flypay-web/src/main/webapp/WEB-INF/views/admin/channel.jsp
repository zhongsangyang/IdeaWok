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
<title>支付通道管理</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid')
			.datagrid(
				{
					url : '${ctx}/channel/dataGrid',
					striped : true,
					rownumbers : true,
					pagination : true,
					// 							singleSelect : true,
					idField : 'id',
					sortName : 'seq',
					sortOrder : 'asc',
					pageSize : 15,
					pageList : [ 10, 15, 20, 30, 40, 50, 100, 200, 300,
						400, 500 ],
					frozenColumns : [ [
						{
							field : 'ck',
							checkbox : true
						},
						{
							width : '120',
							title : '渠道名称',
							field : 'name',
							sortable : true,
							formatter : function(value, row, index) {
								var jsonObjs = $.parseJSON('${statementType}');
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
								if (value == 0) {
									return '正常';
								} else if (value == 1) {
									return '停用';
								} else if (value == 4) {
									return '审核中';
								} else if (value == 5) {
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
								if ($.canEdit) {
									str += $
										.formatString(
											'<a href="javascript:void(0)" onclick="editFun(\'{0}\');" >编辑</a>',
											row.id);
								}
								if (row.name == 'MINSHENG'
									|| row.name == 'XINKE') {
									str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
									str += $
										.formatString(
											'<a href="javascript:void(0)" onclick="channelT0Tixian(\'{0}\',\'{1}\',\'{2}\',\'{3}\');" >T0提现</a>',
											row.id, row.channelAMT, row.countTodayAmt, row.tixianAmt);
									if (row.name == 'MINSHENG') {
										str += '今日可提现余额:'
										+ row.channelAMT;
									}
								}
								return str;
							}
						} ] ],
					toolbar : '#toolbar'
				});
	});

	function addFun() {
		parent.$.modalDialog({
			title : '添加',
			width : 650,
			height : 600,
			href : '${ctx}/channel/addPage',
			buttons : [ {
				text : '添加',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;
					var f = parent.$.modalDialog.handler
						.find('#channelAddForm');
					f.submit();
					dataGrid.datagrid('reload');
				}
			} ]
		});
	}

	function editFun(id) {
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$.modalDialog({
			title : '编辑',
			width : 650,
			height : 600,
			href : '${ctx}/channel/editPage?id=' + id,
			buttons : [ {
				text : '编辑',
				handler : function() {
					parent.$.modalDialog.openner_dataGrid = dataGrid;
					var f = parent.$.modalDialog.handler
						.find('#sysParamEditForm');
					f.submit();
				}
			} ]
		});
	}

	function channelT0Tixian(id, amt, countTodayAmt, TixianAmt) {
		var bi;
		if (isNaN(parseInt(TixianAmt))) {
			bi = parseInt(amt) / countTodayAmt * 100;
		} else {
			bi = (parseInt(TixianAmt) + parseInt(amt)) / countTodayAmt * 100;
		}
		if (id == undefined) {
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		bi = bi.toFixed(2);
		parent.$.messager.confirm('询问', '提现后百分比为:' + bi + '%您确定发送T0提现指令吗？', function(b) {
			if (b) {
				$.post('${ctx}/channel/channelT0Tixian', {
					id : id
				}, function(result) {
					searchFun();
					alert(result.msg);
				}, 'JSON');
				return;
			}
		});
	}
	function searchFun() {
		dataGrid.datagrid('load', $.serializeObject($('#searchForm')));
	}
	function deleteFun() {
		var arr = $('#dataGrid').datagrid('getSelections');
		if (arr.length <= 0) {
			alert("请选择要删除的记录");
			return false;
		}
		$.messager.confirm('提示信息', '确认删除?', function(r) {
			if (r) {
				progressLoad();
				var params = new Array();
				for (var i = 0; i < arr.length; i++) {
					params[i] = arr[i].id;
				}
				$.ajax({
					type : 'post',
					url : '${ctx}/channel/deleteChannel',
					cache : false,
					data : {
						params : params
					},
					dataType : 'json',
					success : function(result) {
						//$('#searchForm input').val('');
						//dataGrid.datagrid('load', {});
						searchFun();
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
	function cleanFun() {
		$('#searchForm input').val('');
		dataGrid.datagrid('load', {});
	}
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false"
		style="height: 50px; overflow: hidden; background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					<td>通道名称</td>
					<td><select id="name" name="name" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${statementTypeObj}" var="obj">
								<option value="${obj.code}">${obj.text }</option>
							</c:forEach>
					</select></td>

					<td>通道类型</td>
					<td><select id="type" name="type" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${transTypeObj}" var="obj">
								<option value="${obj.code}">${obj.text }</option>
							</c:forEach>
					</select></td>
					<td>用户类型</td>
					<td><select id="userType" name="userType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${userTypeObj}" var="obj">
								<option value="${obj.code}">${obj.text }</option>
							</c:forEach>
					</select></td>

					<th>状态:</th>
					<td><select id="status" name="status" class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<option value="1">停用</option>
							<option value="0">正常</option>
							<option value="4">审核中</option>
							<option value="100">待启用</option>
					</select> <a href="javascript:void(0);" class="easyui-linkbutton"
						data-options="iconCls:'icon-search',plain:true"
						onclick="searchFun();">查询</a> <a href="javascript:void(0);"
						class="easyui-linkbutton"
						data-options="iconCls:'icon-del',plain:true" onclick="deleteFun()">删除</a>
						<a href="javascript:void(0);" class="easyui-linkbutton"
						data-options="iconCls:'icon-clear',plain:true"
						onclick="cleanFun();">清空</a></td>
				</tr>
			</table>
		</form>
	</div>
	<div data-options="region:'center',border:false,title:'渠道列表'">
		<table id="dataGrid" data-options="fit:true,border:false"></table>
	</div>
	<div id="toolbar" style="display: none;">
		<c:if test="${fn:contains(sessionInfo.resourceList, '/channel/add')}">
			<a onclick="addFun();" href="javascript:void(0);"
				class="easyui-linkbutton"
				data-options="plain:true,iconCls:'icon_add'">添加</a>
		</c:if>
	</div>
</body>
</html>
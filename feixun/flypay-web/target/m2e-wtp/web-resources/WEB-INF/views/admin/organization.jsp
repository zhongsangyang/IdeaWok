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
<c:if
	test="${fn:contains(sessionInfo.resourceList, '/organization/edit')}">
	<script type="text/javascript">
		$.canEdit = true;
	</script>
</c:if>
<title>运营商管理</title>
<script type="text/javascript">
	var treeGrid;
	$(function() {
		<!--开始加载查询结果-->
		treeGrid = $('#treeGrid')
				.treegrid(
						{
							url : '${ctx}/organization/treeGrid',
							idField : 'id',
							treeField : 'name',
							parentField : 'pid',
							fit : true,
							fitColumns : false,
							border : false,
							frozenColumns : [ [ {
								title : 'id',
								field : 'id',
								width : 40,
								hidden : true
							}, {
								field : 'name',
								title : '运营商名称',
								width : 180
							}, {
								field : 'appName',
								title : 'App名称',
								width : 180
							} ] ],
							columns : [ [
									{
										field : 'code',
										title : '编号',
										width : 100
									},
									{
										width : '80',
										title : '代理类型',
										field : 'agentType',
										sortable : true,
										formatter : function(value, row, index) {
											var jsonObjs = $
													.parseJSON('${orgAgentType}');
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
										title : '是否积分制',
										field : 'pointType',
										formatter : function(value, row, index) {
											if (value != null && value == 1) {
												return "是";
											} else {
												return "否";
											}
										}
									},
									{
										width : '80',
										title : '是否降费率',
										field : 'reductionUserRateType',
										formatter : function(value, row, index) {
											if (value != null && value == 2) {
												return "升级降费率";
											} else if (value != null
													&& value == 1) {
												return "积分降费率";
											} else {
												return "否";
											}
										}
									},
									{
										width : '80',
										title : '分润规则',
										field : 'shareBonusType',
										formatter : function(value, row, index) {
											if (value != null && value == 2) {
												return "升级固定比例，流量利润差";
											} else if (value != null
													&& value == 1) {
												return "固定比例分润";
											} else {
												return "不分润";
											}
										}
									},

									{
										width : '80',
										title : '状态',
										field : 'status',
										sortable : true,
										formatter : function(value, row, index) {
											switch (value) {
											case 0:
												return '正常';
											case 1:
												return '停用';
											}
										}
									},
									{
										field : 'pid',
										title : '上级资源ID',
										width : 150,
										hidden : true
									},
									{
										field : 'diamondAgent',
										title : '钻石用户',
										width : 60
									},
									{
										field : 'goldAgent',
										title : '金牌用户',
										width : 60
									},
									{
										field : 'diamondFee',
										title : '运营商钻石分润',
										width : 60
									},
									{
										field : 'goldFee',
										title : '运营商金牌分润',
										width : 60
									},
									{
										field : 'diamondNum',
										title : '推广人数升钻石',
										width : 80
									},
									{
										field : 'goldNum',
										title : '推广人数升金牌',
										width : 80
									},

									{
										field : 't0Fee',
										title : 't0手续费',
										width : 60
									},
									{
										field : 'minT0Amt',
										title : 't0最低提现额度',
										width : 60
									},
									{
										field : 'maxT0Amt',
										title : 't0最高提现额度',
										width : 60
									},
									{
										field : 't1Fee',
										title : 't1手续费',
										width : 60
									},
									{
										field : 'minT1Amt',
										title : 't1最低提现额度',
										width : 60
									},
									{
										field : 'maxT1Amt',
										title : 't1最高提现额度',
										width : 60
									},
									{
										field : 'rabaleFee',
										title : '返佣手续费',
										width : 60
									},
									{
										field : 'minRabaleAmt',
										title : '返佣最低提现额度',
										width : 60
									},
									{
										field : 'maxRabaleAmt',
										title : '返佣最高提现额度',
										width : 60
									},
									{
										field : 'maxTodayOutAmt',
										title : '单日提现限额',
										width : 60
									},
									{
										field : 'fee',
										title : '提现保留费',
										width : 60
									},

									{
										width : '120',
										title : '创建时间',
										field : 'createDatetime'
									},
									{
										field : 'action',
										title : '操作',
										width : 250,
										formatter : function(value, row, index) {
											var str = '&nbsp;';
											if ($.canEdit) {
												str += $
														.formatString(
																'<a href="javascript:void(0)" onclick="editFun(\'{0}\');" >编辑</a>',
																row.id);
											}
											if (row.agentType == 0||row.agentType == 1) {
												str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
												str += $
														.formatString(
																'<a href="javascript:void(0)" onclick="setChannelFun(\'{0}\');" >设置收款费率</a>',
																row.id);
												str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
												str += $
														.formatString(
																'<a href="javascript:void(0)" onclick="setSysConfigFun(\'{0}\');" >设置短信极光</a>',
																row.id);
											}
											return str;
										}
									} ] ],
							toolbar : '#toolbar'
						});
		<!--加载查询结果结束-->
	});

	function editFun(id) {
		if (id != undefined) {
			treeGrid.treegrid('select', id);
		}
		var node = treeGrid.treegrid('getSelected');
		if (node) {
			parent.$.modalDialog({
				title : '编辑',
				width : 700,
				height : 700,
				href : '${ctx}/organization/editPage?id=' + node.id,
				buttons : [ {
					text : '编辑',
					handler : function() {
						parent.$.modalDialog.openner_treeGrid = treeGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
						var f = parent.$.modalDialog.handler
								.find('#organizationEditForm');
						f.submit();
					}
				} ]
			});
		}
	}

	function setChannelFun(id) {
		if (id != undefined) {
			treeGrid.treegrid('select', id);
		}
		var node = treeGrid.treegrid('getSelected');
		if (node) {
			parent.$.modalDialog({
				title : '设置渠道费率',
				width : 850,
				height : 350,
				href : '${ctx}/organization/channelFeesPage?id=' + node.id,
				buttons : [ {
					text : '设置渠道费率',
					handler : function() {
						parent.$.modalDialog.openner_treeGrid = treeGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
						var f = parent.$.modalDialog.handler
								.find('#organizationChannelForm');
						f.submit();
					}
				} ]
			});
		}
	}
	function setSysConfigFun(id) {
		if (id != undefined) {
			treeGrid.treegrid('select', id);
		}
		var node = treeGrid.treegrid('getSelected');
		if (node) {
			parent.$.modalDialog({
				title : '短信极光配置',
				width : 650,
				height : 400,
				href : '${ctx}/organization/orgSysConfigPage?id=' + node.id,
				buttons : [ {
					text : '设置',
					handler : function() {
						parent.$.modalDialog.openner_treeGrid = treeGrid;
						var f = parent.$.modalDialog.handler
								.find('#orgSysConfigForm');
						f.submit();
					}
				} ]
			});
		}
	}

	function deleteFun(id) {
		if (id != undefined) {
			treeGrid.treegrid('select', id);
		}
		var node = treeGrid.treegrid('getSelected');
		if (node) {
			parent.$.messager.confirm('询问', '您是否要删除当前资源？删除当前资源会连同子资源一起删除!',
					function(b) {
						if (b) {
							progressLoad();
							$.post('${ctx}/organization/delete', {
								id : node.id
							}, function(result) {
								if (result.success) {
									parent.$.messager.alert('提示', result.msg,
											'info');
									treeGrid.treegrid('reload');
								} else {
									parent.$.messager.alert('提示', result.msg,
											'info');
								}
								progressClose();
							}, 'JSON');
						}
					});
		}
	}

	function addFun(type) {
		//0   OEM ， 1 运营商， 2代理商
		parent.$.modalDialog({
			title : '添加',
			width : 700,
			height :700,
			href : '${ctx}/organization/addPage?type=' + type,
			buttons : [ {
				text : '添加',
				handler : function() {
					parent.$.modalDialog.openner_treeGrid = treeGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
					var f = parent.$.modalDialog.handler
							.find('#organizationAddForm');
					f.submit();
				}
			} ]
		});
	}
</script>
</head>
<body>
	<div class="easyui-layout" data-options="fit:true,border:false">
		<div data-options="region:'center',border:false"
			style="overflow: hidden;">
			<table id="treeGrid"></table>
		</div>

		<div id="toolbar" style="display: none;">
			<c:if
				test="${fn:contains(sessionInfo.resourceList, '/organization/add')}">
				<a onclick="addFun(1);" href="javascript:void(0);"
					class="easyui-linkbutton"
					data-options="plain:true,iconCls:'icon-add'">添加运营商</a>
				<a onclick="addFun(0);" href="javascript:void(0);"
					class="easyui-linkbutton"
					data-options="plain:true,iconCls:'icon-add'">添加OEM</a>
				<a onclick="addFun(3);" href="javascript:void(0);"
					class="easyui-linkbutton"
					data-options="plain:true,iconCls:'icon-add'">添加支付类型保本费率</a>
			</c:if>
		</div>
	</div>
</body>
</html>
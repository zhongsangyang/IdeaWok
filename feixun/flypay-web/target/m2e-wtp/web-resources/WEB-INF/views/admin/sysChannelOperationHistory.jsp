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
<title>通道操作记录页面</title>
<script type="text/javascript">
	var dataGrid;
	$(function(){
	dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}/sysChannelOperationHistory/dataGrid',
			striped : true,
			rownumbers : true,
			pagination : true,
			idField : 'id',
			sortName : 'createTime',
			sortOrder : 'desc',
			pageSize : 15,
			pageList : [ 10, 15, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
			frozenColumns :[[
			                 {
			                	 	width : '100',
									title : '行为发起人ID',
									field : 'creatorId'
			                 },{
			                	 	width : '100',
									title : '行为发起人phone',
									field : 'creatorPhone'
			                 },{
			                	 	width : '100',
			                	 	title : '行为发起人姓名',
									field : 'creatorName'
							 },{
									width : '100',
			                	 	title : '记录类型',
									field : 'recordType',
									formatter :function(value){
										if(value ==1){
											return "支付通道";
										}
									}
							 },{
								 	width : '100',
			                	 	title : '行为类型',
									field : 'behaviorType',
									formatter :function(value){
										if(value ==1){
											return "编辑";
										}
									}
							 }
             ]],
             columns : [[ {
            	width : '100',
				title : '通道ID',
				field : 'targetId'
             },{
            	width : '100',
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
             },{
           	 	width : '100',
  				title : '通道详细名称',
  				field : 'detailName' 
             },{
            	width : '100',
   				title : '状态',
   				field : 'status',
				formatter :function(value){
					switch (value){
					case 0:
						return "正常";
					case 1:
						return "停用";
					case 4:
						return "审核中";
					}
				}
             },{
				 	width : '600',
					title : '详情信息',
					field : 'details'
				 
			 },{
				 	width : '125',
					title : '创建时间',
					field : 'createTime',
					sortable : true
			 }
             ]]         
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
		style="height: 100px; overflow: hidden;background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					<td>行为发起人手机号</td>
					<td><input id="creatorPhone" name="creatorPhone" style="width: 150px;" type="text"
					class="easyui-validatebox" /></td>
					
					
					<td>记录类型</td>
					<td>
						<select id="recordType" name="recordType" class="easyui-combobox"
							data-options="width:140,height:29,editable:false,panelHeight:'auto'">
								<option selected value="">请选择</option>
								<option value="1">支付通道</option>
						</select>
					</td>
					
					<td>行为类型</td>
					<td>
						<select id="behaviorType" name="behaviorType" class="easyui-combobox"
							data-options="width:140,height:29,editable:false,panelHeight:'auto'">
								<option selected value="">请选择</option>
								<option value="1">编辑</option>
						</select>
					</td>
					
				</tr>
				
				<tr>
					<td>通道号ID</td>
						<td><input id="targetId" name="targetId" style="width: 150px;" type="text"
						class="easyui-validatebox" /></td>
					
					<td>渠道名称</td>
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
					
					
				</tr>
				
				
				<tr>
					<td>用户类型</td>
					<td><select id="userType" name="userType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${channelUserTypeObj}" var="obj">
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
					</select> 
				
				
				
				
					<th>操作日期:</th>
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
							onclick="searchFun();">查询</a><a href="javascript:void(0);"
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


</body>
</html>

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
<title>对账日志</title>
<script type="text/javascript">
	var dataGrid;
	$(function() {
		dataGrid = $('#dataGrid').datagrid({
			url : '${ctx}/orderStatement/dataGrid',
			striped : true,
			rownumbers : true,
			pagination : true,
			singleSelect : true,
			idField : 'id',
			sortName : 'id',
			sortOrder : 'asc',
			pageSize : 15,
			pageList : [ 10, 15, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
			frozenColumns : [ [ {
				width : '120',
				title : '登录账号',
				field : 'loginName',
				sortable : true
			}, {
				width : '120',
				title : '真实姓名',
				field : 'realName'
			} ] ],
			columns : [ [ {
				width : '120',
				title : '对账日期',
				field : 'statementDate',
				sortable : true
			}, {
				width : '200',
				title : '订单NO',
				field : 'orderNo',
				sortable : true
			}, {
				width : '250',
				title : '支付NO',
				field : 'statementNo',
				sortable : true

			}, {
				width : '120',
				title : '对账类型',
				field : 'statementType',
				sortable : true,
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
			}, {
				width : '120',
				title : '状态',
				field : 'status',
				sortable : true,
				formatter : function(value, row, index) {
					if (value != '0') {
						return '失败';
					} else {
						return '正常';
					}
				}
			}, {
				width : '1020',
				title : '信息描述',
				field : 'errorInfo',
				sortable : true
			}, {
				width : '150',
				title : '创建日期',
				field : 'createTime',
				sortable : true
			}

			] ],
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
		style="height: 30px; overflow: hidden;background-color: #f4f4f4">
		<form id="searchForm">
			<table>
				<tr>
					<th>登录姓名:</th>
					<td><input name="loginName" /></td>
					<th>对账类型:</th>
					<td><select id="statementType" name="statementType"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="">请选择</option>
							<c:forEach items="${statementType}" var="obj">
								<option value="${obj.code}">${obj.text }</option>
							</c:forEach>
					</select></td>
					<th>对账时间:</th>
					<td><input name="statementDateStart" placeholder="点击选择时间"
						onclick="WdatePicker({readOnly:true,dateFmt:'yyyyMMdd'})"
						readonly="readonly" />&nbsp;&nbsp;&nbsp;&nbsp;至&nbsp;&nbsp;&nbsp;<input
						name="statementDateEnd" placeholder="点击选择时间"
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
		<form action="${ctx}/orderStatement/fileUpload" method="post" enctype="multipart/form-data">
		    文件:<input type="file" name="file">
		    <select id="type" name="type"
						class="easyui-combobox"
						data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option selected value="ZANSHANFU">攒善付</option>
							<option value="XINKE">欣客</option>
					</select>
		    <input id="dealDate" name="dealDate"  placeholder="点击选择时间"	 data-options="required:true" onclick="WdatePicker({readOnly:true,dateFmt:'yyyyMMdd'})"/>
		    <input type="submit"  value="对账" >
   		</form>
	</div>
</body>
</html>
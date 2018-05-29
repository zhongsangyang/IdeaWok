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
<title>通道开通</title>
<script type="text/javascript">
	function searchFun() {
		var filepath = $("#fileName").val();
		if(filepath==null||filepath==""){
		  alert("请选择文件txt文件");	
		  return false;
	    }
		var extStart = filepath.lastIndexOf(".");
		var ext = filepath.substring(extStart, filepath.length).toUpperCase();
		if (ext != ".TXT") {
			 alert("请选择文件txt文件");	
             return false;
         }
		var typeChannel = $('#typeChannel').combobox('getValue');
		var typeJUdao = $('#typeJUdao').combobox('getValue');
		if(typeChannel=='1' && typeJUdao=='4'){
			alert("民生不支持京东,请选择其他的");	
			return false;
		}
        if(typeChannel=='2' && typeJUdao=='3'){
			alert("平安不支持QQ,请选择其他的");	
			return false;
		}
		
		$("#from1").submit();
	}
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false,title:'开通通道'">
	  <form action="${ctx}/throughchannel/fileUpload" method="post" enctype="multipart/form-data" id="from1">
	    <div style="position: absolute;width: 1200px;left: 50%;margin-left: -200px;margin-top: 200px;">
	       <select id="typeChannel" name="typeChannel" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
			 <option value="1">民生</option>
			 <option value="2">平安</option>
		   </select>
	       <select id="typeJUdao" name="type" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
			 <option value="1">支付宝</option>
			 <option value="2">微信</option>
			 <option value="3">QQ支付</option>
			 <option value="4">京东支付</option>
			 <option value="1100">翼支付</option>
		   </select>
		   <select id="personId" name="perType" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
			 <option value="1">芦总</option>
			 <option value="2">张总</option>
		   </select>
	      <input type="file" name="fileName" id="fileName"/>  
	      <button type="button" onclick="searchFun();">开通</button>	  
	    </div>
	  </form>
	</div>
</body>
</html>
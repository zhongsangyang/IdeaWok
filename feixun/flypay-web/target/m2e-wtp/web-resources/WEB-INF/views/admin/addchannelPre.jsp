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

</head>
<body class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false,title:'开通通道'">
	  <form action="${ctx}/throughchannel/fileUpload" method="post" enctype="multipart/form-data" id="from1">
	    <div style="position: absolute;width: 400px;left: 50%;margin-left: -200px;margin-top: 200px;">
	               恭喜您,开通成功了  
	    </div>
	  </form>
	</div>
</body>
</html>
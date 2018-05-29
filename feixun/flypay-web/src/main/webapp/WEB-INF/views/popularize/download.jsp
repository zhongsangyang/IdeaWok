<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<head>
<meta charset="UTF-8">
<meta name="viewport"
	content="width=device-width,initial-scale=1,user-scalable=0">
<title>软件下载</title>
<link rel="stylesheet" href="${ctx}/style/css/weui.css" />
<link rel="stylesheet" href="${ctx}/style/css/main.css" />
</head>
<body>

	<div id="download" class="bd">
		<header>

			<img alt="" src="${ctx}/img/down_01.png">

		</header>
		<div id="center" class="container mt30">
			<div class="code">
				<a href="${android}"><img src="${ctx}/img/down_03.png"></a>
			</div>

			<div class="code">
				<a href="${ios}"><img src="${ctx}/img/down_02.png"> </a>
			</div>
		</div>

		<div id="download-box-js" class="body">
			<img
				style="position:absolute; left: 0; top: 0; opacity: 100; z-index: 9"
				src="${ctx}/img/h_01.png">
			<div class="body-box"></div>
		</div>


	</div>
	<script src="${ctx}/jslib/zepto.min.js"></script>
	<script src="${ctx}/jslib/main.js"></script>
	<script>
		$(function() {
			if (nph.isWechatBrowser()) {
				$('#download-box-js').show();
			} else {
				$('#download-box-js').hide();
			}
			$('#download-box-js').click(function(e) {
				$('#download-box-js').hide();
			});
		});
	</script>
</body>
</html>

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
<title>二维码支付</title>
<link rel="stylesheet" href="${ctx}/style/css/weui.css" />
<link rel="stylesheet" href="${ctx}/style/css/main.css" />
<script type="text/javascript">
	function initPayInfo() {
		var url = $('#url').val();
		window.location=url;
	}
	function submitPayInfo() {
		var amt = $('#amt').val();
		var inputUserCode = $('#inputUserCode').val();
		$.ajax({
			type : "POST", //用POST方式传输  
			dataType : "JSON", //数据格式:JSON  
			url : '${ctx}/popularizePay/paymentOrder2', //目标地址  
			data : {
				amt : amt,
				inputUserCode : inputUserCode
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {

			},
			success : function(msg) {
				msg = JSON.parse(msg);
				
				window.location=msg.url;
			}
		});
	};
</script>
</head>
<body onload="javascript:initPayInfo();">
	<div class="bd">
		<div id="center" class="container pt20">
			<h2 class="text-center text-primary">${userName}支付</h2>
			<input id="channelNo" type="hidden" value="${channelNo}">
			<input id="url" type="hidden" value="${url}">
			<div class="weui_cells weui_cells_form">

				<div id="payInfoDiv" class="weui_cell">
					<div class="weui_cell_hd">
						<label class="weui_label">支付金额</label>
					</div>
					<div class="weui_cell_bd weui_cell_primary">
						<input id='amt' class="weui_input" type="number" pattern="[0-9]*"
							placeholder="请输入待付金额">
					</div>
				</div>
				<div id="paybutton" class="weui_btn_area">
					<a class="weui_btn bg-primary" href="javascript:submitPayInfo();"
						id="showTooltips">去支付</a>
				</div>
			</div>
		</div>

	</div>
	<script src="${ctx}/jslib/zepto.min.js"></script>
	<script src="${ctx}/jslib/main.js"></script>
</body>
</html>

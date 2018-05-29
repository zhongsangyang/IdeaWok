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
<title>支付成功</title>
<link rel="stylesheet" href="${ctx}/style/css/weui.css" />
<link rel="stylesheet" href="${ctx}/style/css/main.css" />
<script type="text/javascript">
	function closeInfo() {
		if (nph.isWechatBrowser()) {
			WeixinJSBridge.call('closeWindow');
		} else if (nph.isAlipayBrowser()) {
			AlipayJSBridge.call('closeWebview');
		} else {

			self.close();
			/* $('#paybutton'). */
		}
	}
	function initPayInfo() {
		if (!nph.isWechatBrowser() && !nph.isAlipayBrowser()) {
			$('#paybutton').hide();
		}
	}
</script>
</head>
<body onload="javascript:initPayInfo();">
	<div class="bd">
		<div id="center" class="container pt20">
			<h2  class="text-center text-primary" ><img src="${ctx}/img/pay_success.png" style="width: 50px;  height: 50px"></h2>
			<h2 class="text-center text-primary">${channelName}支付成功！</h2>
		</div>
		<div id="payInfoDiv" class="weui_cell">
			<div class="weui_cell_hd">
				<label class="weui_label">订单金额</label>
			</div>
			<div class="weui_cell_bd weui_cell_primary">
				<input id='amt' class="weui_input" type="text"
					readonly="readonly" value="${amt}元">
			</div>
		</div>
		<div id="payInfoDiv" class="weui_cell">
			<div class="weui_cell_hd">
				<label class="weui_label">订单号</label>
			</div>
			<div class="weui_cell_bd weui_cell_primary">
				<input id='orderNum' class="weui_input" type="text"
					readonly="readonly" value="${orderNum}">
			</div>
		</div>
		<div id="payInfoDiv3" class="weui_cell">
			<div class="weui_cell_hd">
				<label class="weui_label">完成时间</label>
			</div>
			<div class="weui_cell_bd weui_cell_primary">
				<input id='finishtDate' class="weui_input" type="text"
					readonly="readonly" value="${finishtDate}">
			</div>
		</div>
		<div id="payInfoDiv4" class="weui_cell">
		</div>
		<div id="paybutton" class="weui_btn_area">
			<a class="weui_btn bg-primary" href="javascript:closeInfo();"
				id="showTooltips">确定</a>
		</div>
	</div>
	<script src="${ctx}/jslib/zepto.min.js"></script>
	<script src="${ctx}/jslib/main.js"></script>
</body>
</html>

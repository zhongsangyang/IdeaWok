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
<title>二维码支付4</title>
<link rel="stylesheet" href="${ctx}/style/css/weui.css" />
<link rel="stylesheet" href="${ctx}/style/css/main.css" />
<script type="text/javascript">
	function initPayInfo() {
		var errorInfo = $('#errorInfo').val();
		if (errorInfo && errorInfo != '') {
			$('#payInfoDiv').attr("style", "display:none")
			$('#paybutton').attr("style", "display:none")
			$('#errorInfoDiv').attr("style", "display:true")
		}
	}
	function submitPayInfo() {
		var amt = $('#amt').val();
		var userId = $('#userId').val();
		$.ajax({
			type : "POST", //用POST方式传输  
			dataType : "JSON", //数据格式:JSON  
			url : '${ctx}/popularizePay/paymentOrder4', //目标地址  
			data : {
				userId : userId
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {

			},
			success : function(msg) {
				msg = JSON.parse(msg);
				WeixinJSBridge.invoke('getBrandWCPayRequest', {
					"appId" : msg.appId, //公众号名称，由商户传入     
					"timeStamp" : msg.timeStamp, //时间戳，自1970年以来的秒数     
					"nonceStr" : msg.nonceStr, //随机串     
					"package" : msg.package,
					"signType" : msg.signType, //微信签名方式：     
					"paySign" : msg.paySign
				//微信签名 
				}, function(res) {
					if (res.err_msg == "get_brand_wcpay_request：ok") {

					} // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。 
				});

			}
		});
	};
</script>
</head>
<body onload="javascript:initPayInfo();">
	<div class="bd">
		<div id="center" class="container pt20">
			<h2 class="text-center text-primary">${userName}支付</h2>
			<input id="userId" type="hidden" value="${userId}">
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
				<div id="errorInfoDiv" class="weui_cell" style="display:none">
					<div class="weui_cell_hd">
						<label class="weui_label">错误原因</label>
					</div>
					<div class="weui_cell_bd weui_cell_primary">
						<input id='amt' class="weui_input" value="${errorInfo}">
					</div>
				</div>
				<div id="paybutton" class="weui_btn_area">
					<a class="weui_btn bg-primary" href="javascript:submitPayInfo();"
						id="showTooltips">去支付</a>
				</div>
				<input id="inputUserCode" name="inputUserCode" type="hidden"
					value="${inputUserCode}"> <input id="errorInfo"
					name="errorInfo" type="hidden" value="${errorInfo}">
				<div id="payqr" style="width: 43%; margin-left: 10%;display:none">
					<p>
						<img id="imgObj" style="width: 80%; margin-left: 10px" />
					</p>
					<p class="text-center">长按并识别图片二维码</p>
				</div>
			</div>
		</div>

	</div>
	<script src="${ctx}/jslib/zepto.min.js"></script>
	<script src="${ctx}/jslib/main.js"></script>
</body>
</html>

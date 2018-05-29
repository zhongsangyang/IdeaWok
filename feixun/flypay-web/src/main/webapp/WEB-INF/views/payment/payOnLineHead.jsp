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
	var flag = false;
	function submitPayInfo() {
		var amt = $('#amt').val();
		var inputUserCode = $('#inputUserCode').val();
		var payType = $('#payType').val();
		var userId = $('#userId').val();
		if (flag) {
			//走到这里，说明已经请求过一次服务器了，需要重新生成参数
			nph.alert("支付失败", "小主，请重新扫码");
			return;
		}
		flag = true;
		
		if(amt == null || amt =="" || amt == undefined || amt < 12){
			nph.alert("支付失败", "小主，交易金额不能低于12元哦");
			return;
		}
		
		$.ajax({
			type : "POST", //用POST方式传输  
			dataType : "JSON", //数据格式:JSON  
			url : '${ctx}/popularizePay/paymentOnlineOrder', //目标地址  
			data : {
				amt : amt,
				inputUserCode : inputUserCode,
				userId : userId,
				payType : payType
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				nph.alert("支付失败", errorThrown);
			},
			success : function(msg) {
				flag = false;
				msg = JSON.parse(msg);
				if(msg != null && msg.amtError != null){
					nph.alert("支付失败", "小主，交易金额不能低于12元哦");
					return;
				}
				
				if (msg != null && msg.channelName == 'PINGANPAYZHITONGCHE_ZHIQING') {
					window.location = msg.url;
				} else {
					if (nph.isWechatBrowser()) {
						if (msg != null && msg.wxjsapiStr != null) {
							var wxStr = JSON.parse(msg.wxjsapiStr);
							WeixinJSBridge
								.invoke(
									'getBrandWCPayRequest',
									{
										"appId" : wxStr.appId, //公众号名称，由商户传入     
										"timeStamp" : wxStr.timeStamp, //时间戳，自1970年以来的秒数     
										"nonceStr" : wxStr.nonceStr, //随机串     
										"package" : wxStr.package,
										"signType" : wxStr.signType, //微信签名方式：     
										"paySign" : wxStr.paySign
									//微信签名 
									},
									function(res) {
										if (res.err_msg == "get_brand_wcpay_request：ok") {
											nph.alert("微信支付",
												"支付成功");
											WeixinJSBridge
												.call('closeWindow');
										} // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。 
									});
						}

					} else {
						if (msg != null && msg.channelNo != null) {
							AlipayJSBridge.call("tradePay", {
								tradeNO : msg.channelNo
							}, function(result) {
								nph.alert("支付宝支付", JSON
									.stringify(result));
								nph.alert("支付宝支付", "支付完成");
								AlipayJSBridge.call('closeWebview');
							});
						}
					}
				}

			}
		});

	}
	;
</script>
</head>
<body>
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
				<div id="paybutton" class="weui_btn_area">
					<a class="weui_btn bg-primary" href="javascript:submitPayInfo();"
						id="showTooltips">去支付</a>
				</div>
				<input id="inputUserCode" name="inputUserCode" type="hidden"
					value="${inputUserCode}" /> <input id="payType" name="payType"
					type="hidden" value="${payType}" />
			</div>
		</div>

	</div>
	<script src="${ctx}/jslib/zepto.min.js"></script>
	<script src="${ctx}/jslib/main.js"></script>
</body>
</html>

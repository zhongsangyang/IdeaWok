<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<head>
<meta charset="UTF-8">
<meta name="viewport"
	content="width=device-width,initial-scale=1,user-scalable=0">
<title>注册</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/style/css/weui.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/style/css/main.css" />
<script type="text/javascript">
	/*-------------------------------------------*/
	var InterValObj; //timer变量，控制时间  
	var count = 90; //间隔函数，1秒执行  
	var curCount;//当前剩余秒数  
	var codeLength = 6;//验证码长度  
	function sendMessage() {
		curCount = count;
		var phone = $("#phone").val();//手机号码  
		var validateCode = $("#validateCode").val();
		var parentCode = $("#parentCode").val();//推荐人
		var agentId = $("#agentId").val();//运营商

		if (validateCode != null && validateCode.trim() != "") {
			if (parentCode != null && parentCode.trim() != "") {
				if (agentId != null && agentId.trim() != "") {

					if (phone != null && phone.trim() != "" && nph.validatePhone(phone)) {
						//向后台发送处理数据  
						$.ajax({
							type : "POST", //用POST方式传输  
							dataType : "JSON", //数据格式:JSON  
							url : '${pageContext.request.contextPath}/popularize/phoneValidate', //目标地址  
							data : {
								phone : phone,
								validateCode : validateCode,
								parentCode : parentCode,
								agentId : agentId
							},
							error : function(XMLHttpRequest, textStatus, errorThrown) {

							},
							success : function(msg) {
								msg = JSON.parse(msg);
								if (msg.status == "FAILURE") {
									if (msg.errorCode == "E001") {
										nph.downloadAlert("手机号验证", "您此手机号已经注册! \r\n请下载APP开始体验",
												'${pageContext.request.contextPath}/popularize/downloadpage?agentId=' + agentId);
									} else {
										nph.alert("手机号验证", msg.errorInfo);
									}
								} else {
									//设置button效果，开始计时  
									$("#btnSendCode").attr("disabled", "true");
									$("#btnSendCode").val(curCount + "秒内输入");
									InterValObj = window.setInterval(SetRemainTime, 1000); //启动计时器，1秒执行一次  
								}
							}
						});
					} else {
						nph.alert("手机号验证", "手机号码有误，请您检查");
					}
				} else {
					nph.alert("手机号验证", "推广所属运营商不存在，请您联系客服");
				}
			} else {
				nph.alert("手机号验证", "推荐人不允许为空");
			}
		} else {
			nph.alert("手机号验证", "验证码不能为空");
		}
	}
	//timer处理函数  
	function SetRemainTime() {
		phone
		if (curCount == 0) {
			window.clearInterval(InterValObj);//停止计时器  
			$("#btnSendCode").removeAttr("disabled");//启用按钮  
			$("#btnSendCode").val("重新发送验证码");
			code = ""; //清除验证码。如果不清除，过时间后，输入收到的验证码依然有效      
		} else {
			curCount--;
			$("#btnSendCode").val(curCount + "秒内输入");
		}
	}
	var flag = false;
	//timer处理函数  
	function submitRegistInfo() {

		if (flag) {
			return;
		}
		var phone = $("#phone").val();//手机号码  
		var password = $("#password").val();//密码
		var rePassword = $("#rePassword").val();//密码
		var checkCode = $("#checkCode").val();//手机号码  
		var parentCode = $("#parentCode").val();//推荐人
		var agentId = $("#agentId").val();//运营商
		if (password == null || password.trim() == "") {
			nph.alert("APP注册", "密码为必填项");
			return;
		}
		if (rePassword == null || rePassword.trim() == "") {
			nph.alert("APP注册", "请确认登录密码");
			return;
		}
		if (password.trim() != rePassword.trim()) {
			nph.alert("APP注册", "两次输入的密码不一致");
			return;
		}
		if (checkCode == null || checkCode.trim() == "") {
			nph.alert("APP注册", "短信验证码为必填项");
			return;
		}
		if (parentCode == null || parentCode.trim() == "") {
			nph.alert("APP注册", "推荐人不允许为空");
			return;
		}
		if (agentId == null || agentId.trim() == "") {
			nph.alert("APP注册", "推广所属运营商不存在，请您联系客服");
			return;
		}
		if (phone != null && phone.trim() != "" && nph.validatePhone(phone)) {
			//向后台发送处理数据  
			$.ajax({
				type : "POST", //用POST方式传输  
				dataType : "JSON", //数据格式:JSON  
				url : '${pageContext.request.contextPath}/popularize/regist', //目标地址  
				data : {
					phone : phone,
					checkCode : checkCode,
					password : password,
					parentCode : parentCode,
					agentId : agentId
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					flag = false;
				},
				success : function(data) {
					data = JSON.parse(data);
					if (data.status == "SUCCESS" && data.code == "100") {
						flag = true;
						nph.downloadAlert("APP注册", "恭喜您，注册成功! \r\n请下载APP开始体验", '${pageContext.request.contextPath}/popularize/downloadpage?agentId=' + agentId);
					} else if (data.code == "101") {
						flag = false;
						nph.alert("APP注册", "注册失败：请重新获取验证码 ");
					} else if (data.code == "102") {
						flag = false;
						nph.alert("APP注册", "注册失败：确认必填字段已填写");
					} else if (data.code == "103") {
						flag = false;
						nph.alert("APP注册", "注册失败： 短信验证码超时 ，请重试");
					} else if (data.code == "104") {
						flag = false;
						nph.alert("APP注册", "注册失败：短信验证码失败，请重试");
					} else if (data.code == "105") {
						flag = false;
						nph.alert("APP注册", "注册失败：手机号码与发送验证码手机号码不一致，请重试");
					} else if (data.code == "106") {
						flag = false;
						nph.alert("APP注册", "注册失败： 通信异常，请重试 ");
					} else if (data.code == "107") {
						flag = false;
						nph.downloadAlert("APP注册", "该手机已经注册！ \r\n请下载APP开始体验", '${pageContext.request.contextPath}/popularize/downloadpage?agentId=' + agentId);
					} else if (data.code == "108") {
						flag = false;
						nph.alert("APP注册", "注册失败：推荐码无效");
					} else if (data.code == "109") {
						flag = false;
						nph.alert("APP注册", "注册失败：推荐码不存在无法注册");
					} else {
						flag = false;
						nph.alert("APP注册", "注册失败");
					}
				}
			});
		} else {
			flag = false;
			nph.alert("APP注册", "手机号码有误，请您检查");
		}
	}

	function changeImg() {
		var imgSrc = $("#imgObj");
		var src = imgSrc.attr("src");
		imgSrc.attr("src", chgUrl(src));
	}
	//时间戳   
	//为了使每次生成图片不一致，即不让浏览器读缓存，所以需要加上时间戳   
	function chgUrl(url) {
		var timestamp = (new Date()).valueOf();
		if ((url.indexOf("timestamp") >= 0)) {
			url = url.substring(0, url.length - 13) + timestamp
		} else {
			url = url + "?timestamp=" + timestamp;
		}
		return url;
	}
</script>
</head>
<body>

	<div class="bd">

		<div id="center" class="container pt20">
			<input id="agentId" type="hidden" value='${agentId}'">
			<h2 class="text-center text-primary" style="color: #BB962A;">注册信息</h2>

			<div class="weui_cells weui_cells_form">

				<div class="weui_cell">
					<div class="weui_cell_hd">
						<label class="weui_label">手机号</label>
					</div>
					<div class="weui_cell_bd weui_cell_primary">
						<input id='phone' class="weui_input" type="number"
							pattern="[0-9]*" placeholder="请输入注册手机号">
					</div>
				</div>
				<div class="weui_cell weui_vcode">
					<div class="weui_cell_hd">
						<label class="weui_label">图像验证码</label>
					</div>
					<div class="weui_cell_bd weui_cell_primary">
						<input id='validateCode' class="weui_input" type="text"
							placeholder="请输入验证码" maxlength="4">
					</div>
					<div class="weui_cell_ft" style="height: 44px">
						<a href="#" onclick="changeImg()"><img id="imgObj" alt="验证码"
							src="${pageContext.request.contextPath}/code/getCode" /> </a>
					</div>
				</div>
				<div class="weui_cell weui_vcode">
					<div class="weui_cell_hd">
						<label class="weui_label">短信验证码</label>
					</div>
					<div class="weui_cell_bd weui_cell_primary">
						<input id='checkCode' class="weui_input" type="number"
							placeholder="请输入验证码">
					</div>
					<div class="weui_cell_ft" style="height: 44px">
						<input id="btnSendCode" type="button" style="margin-top: 9px"
							class="weui_btn weui_btn_warn weui_btn_mini"
							onclick="sendMessage();" value="发送验证码"></input>
					</div>
				</div>

				<div class="weui_cell">
					<div class="weui_cell_hd">
						<label class="weui_label">登录密码</label>
					</div>
					<div class="weui_cell_bd weui_cell_primary">
						<input id='password' class="weui_input" type="password"
							placeholder="请设置登录密码">
					</div>
				</div>
				<div class="weui_cell">
					<div class="weui_cell_hd">
						<label class="weui_label">确认密码</label>
					</div>
					<div class="weui_cell_bd weui_cell_primary">
						<input id='rePassword' class="weui_input" type="password"
							placeholder="请确认登录密码">
					</div>
				</div>
				<div class="weui_cell">
					<div class="weui_cell_hd">
						<label class="weui_label">推荐人</label>
					</div>
					<div class="weui_cell_bd weui_cell_primary">
						<input id="parentCode" class="weui_input" type="text"
							placeholder="推荐人手机号或推荐编码" value='${parentCode}'
							readonly="readonly">
					</div>
				</div>
				<div class="weui_btn_area">
					<a class="weui_btn bg-primary"
						href="javascript:submitRegistInfo();" id="showTooltips" style="background: #BB962A;">注册</a>
				</div>
				<div class="weui_btn_area">
					<a href="${pageContext.request.contextPath}/popularize/downloadpage?agentId=${agentId}"
						class="weui_btn bg-primary" style="background: #BB962A;">下载APP体验</a>
				</div>
			</div>
		</div>

	</div>
	<script src="${pageContext.request.contextPath}/jslib/zepto.min.js"></script>
	<script src="${pageContext.request.contextPath}/jslib/main.js"></script>
</body>
</html>

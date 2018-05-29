<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>推广注册页面</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
    
    <script src="${pageContext.request.contextPath}/jslib/jquery-3.1.1.js"></script>
    <script src="${pageContext.request.contextPath}/jslib/identifyCode.js"></script>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/style/css/bama.css">
</head>
<body style="overflow-x:hidden;">

    <div class="topDiv">
        <div class="logoDiv">
<!--             <div class="logo"></div> -->
<!--             <div class="logoText"> -->
<!--                 <p class="logoText1">BAMA</p> -->
<!--                 <p class="logoText2">Mobile Pay</p> -->
<!--             </div> -->
<!--             <p class="logoText3">钱庄</p> -->
        </div>
        <div class="bamaBg"></div>
        <input id="agentId" type="hidden" value='${agentId}'">
        <input id="parentCode" type="hidden" value='${parentCode}'">
        <form id="registForm" onSubmit = "return false;">
            <div class="inputDiv"><span class="inputText">手机号</span><input type="text" id="phoneNumber" name="phoneNumber" placeholder="请输入注册手机号" required="required"></div>
            <div class="inputDiv"><span class="inputText">图像验证码</span><input type="text" maxlength="4" id="identifyCode" name="identifyCode" placeholder="请输入验证码" required="required"><button id="code"></button></div>
            <div class="inputDiv"><span class="inputText">短信验证码</span><input type="number" maxlength="6" id="messageCode" name="messageCode" placeholder="请输入验证码" required="required"><button id="sendMessage">发送验证码</button></div>
            <div class="inputDiv"><span class="inputText">登录密码</span><input type="password" id="password" name="password" placeholder="请设置登录密码" required="required"></div>
            <div class="inputDiv"><span class="inputText">确认密码</span><input type="password" id="certainPsw" name="certainPsw" placeholder="请确认登录密码" required="required"></div>
            <div class="inputDiv" style="border:none;border-top: 1px solid #e4e4e4;">推荐人<label id="reference">${parentCode}</label></div>
        </form>
        <div class="recommend"><span style="color:#ff0000">*</span>真实填写本人信息，有助于你成功认证通过</div>
        <button id="registBtn" class="btn" type="button">注册</button>
        <button id="downloadBtn" class="btn">下载APP体验</button>
    </div>

    <div class="bottomDiv">
        <p id="introduce">Bama钱庄是一个全民创富平台，有着激励政策的创业平台，自己管理自己的团队，我的地盘我作主的概念。Bama钱庄的影响和魅力就是：</p>
        
        <div class="charm">
            <p>努力分享一阵子；</p>
            <p>轻松享受一辈子！</p>
        </div>

        <p>一个好的产品</p>
        <p>一个好的模式</p>
        <p>一个好的项目</p>
        <p>你在等待什么</p>

        <ul class="attraction">
            <li><span class="dot"></span>&nbsp;0投资，0风险，整合而不伤人脉！</li>
            <li><span class="dot"></span>&nbsp;努力分享3个月，月赚5万+不再是梦</li>
            <li><span class="dot"></span>&nbsp;全新打造创富平台，目前100万+人参与</li>
            <li><span class="dot"></span>&nbsp;费率低秒到账，裂变式4.0商业模式</li>
            <li><span class="dot"></span>&nbsp;有人教，有人带，Bama人的操作很简单</li>
        </ul>

        <div class="QRcodeDiv">
            <div style="display: flex">
                <div class="codeImg"></div>
                <div class="QRcodeText">
                    <p style="font-family: SimHei;font-size: 3.7vw;font-weight:bold">关注官方微信号了解更多</p>
                    <div style="padding-top: 2vw"><img src="${pageContext.request.contextPath}/style/img/check.png"><span>了解赚钱模式</span></div>
                    <div><img src="${pageContext.request.contextPath}/style/img/check.png"><span>了解推广模式</span></div>
                    <div><img src="${pageContext.request.contextPath}/style/img/check.png"><span>了解运营中心</span></div>
                </div>
            </div>
            <p style="font-family:SimHei;font-size: 4.337969vw;padding-top: 4.172462vw;font-weight:bold">如何关注微信：手机截屏>>微信扫描关注</p>
        </div>

        <div class="footer"></div>

    </div>

 
</body>

<script>
   
    // 刷新验证码
    $('#code').click(function(){
        createCode();
    });

    var isCheckPhone;
    var isCheckIdentifyCode;
    var isVerifyPassword;
    var isCheckPassword;
    var isCheckMessageCode;
    // “注册”button点击事件
    $('#registBtn').click(function(){

        isCheckPhone = checkPhone();
        isCheckIdentifyCode = validate();
        isVerifyPassword = verifyPassword();
        isCheckPassword = checkPassword();
        isCheckMessageCode = checkMessageCode();
    	var phoneNumber = $("#phoneNumber").val();
		var parentCode = $("#parentCode").val();
		var agentId = $("#agentId").val();
		var password = $("#password").val();
		var messageCode = $("#messageCode").val();
		
		
        if (isCheckPhone == false) {
            alert("手机号码有误，请重填");
            return false;
        }else if (isCheckIdentifyCode == false) {
            alert("验证码输入错误！");
            return false ;
        }else if ($("#messageCode").val() == null  || $("#messageCode").val()=='') {
            alert('短信验证码不能为空')
            return false;
        }else if (isVerifyPassword == false) {
            alert('两次输入的密码不一致，请重新输入！')
            return false;
        }else{
        	$.ajax({
				type : "POST", //用POST方式传输  
				dataType : "JSON", //数据格式:JSON  
				url : '${pageContext.request.contextPath}/popularize/regist', //目标地址  
				data : {
					phone : phoneNumber,
					checkCode : messageCode,
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
						alert("恭喜您，注册成功! \r\n请下载APP开始体验${pageContext.request.contextPath}/popularize/downloadpage?agentId="+ agentId);
					} else if (data.code == "101") {
						alert("注册失败：请重新获取验证码 ");
					} else if (data.code == "102") {
						alert("注册失败：确认必填字段已填写");
					} else if (data.code == "103") {
						alert("注册失败： 短信验证码超时 ，请重试");
					} else if (data.code == "104") {
						alert("注册失败：短信验证码失败，请重试");
					} else if (data.code == "105") {
						alert("注册失败：手机号码与发送验证码手机号码不一致，请重试");
					} else if (data.code == "106") {
						flag = false;
						alert("注册失败： 通信异常，请重试 ");
					} else if (data.code == "107") {
						alert("该手机已经注册！ \r\n请下载APP开始体验", '${pageContext.request.contextPath}/popularize/downloadpage?agentId=' + agentId);
					} else if (data.code == "108") {
						alert("注册失败：推荐码无效");
					} else if (data.code == "109") {
						alert("注册失败：推荐码不存在无法注册");
					} else {
					    alert("注册失败");
					}
				}
			});
        }

    });

    // 发送验证码点击事件
    $('#sendMessage').click(function(){
    	var phoneNumber = $("#phoneNumber").val();
		var parentCode = $("#parentCode").val();
		var agentId = $("#agentId").val();
		
		if(phoneNumber==null  || phoneNumber==''){
			 alert('请填写手机号码！');
			 return false;
		}
        countdown();
		$.ajax({
			type : "POST", //用POST方式传输  
			dataType : "JSON", //数据格式:JSON  
			url : '${pageContext.request.contextPath}/popularize/phoneValidate', //目标地址  
			data : {
				phone : phoneNumber,
				parentCode : parentCode,
				agentId : agentId
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {

			},
			success : function(msg) {
				msg = JSON.parse(msg);
				if (msg.status == "FAILURE") {
					if (msg.errorCode == "E001") {
						alert("手机号验证:您此手机号已经注册! \r\n请下载APP开始体验 ${pageContext.request.contextPath}/popularize/downloadpage?agentId="+agentId);
					} else {
						alert("手机号验证:"+msg.errorInfo);
					}
				} 
			}
		});
    });

    // 下载
    $('#downloadBtn').click(function(){
    	window.location.href='${pageContext.request.contextPath}/popularize/downloadpage?agentId=${agentId}';
    });

    // 修改推荐人 
    // $('#reference').html();

</script>
</html>
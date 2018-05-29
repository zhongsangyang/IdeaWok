var code; //在全局定义验证码   

$(document).ready(function () {
    createCode();
});

//产生验证码  
function createCode() {
    code = "";
    var codeLength = 4;//验证码的长度  
    var randomArray = new Array(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
        'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');
    for (var i = 0; i < codeLength; i++) {//循环操作  
        var index = Math.floor(Math.random() * 36);//取得随机数的索引（0~35）  
        code += randomArray[index];//根据索引取得随机数加到code上  
    }
    $('#code').text(code);
}


//校验验证码  
function validate() {
    var inputCode = document.getElementById("identifyCode").value.toUpperCase(); //取得输入的验证码并转化为大写        
    if (inputCode.length <= 0) { //若输入的验证码长度为0  
        return false;
    }
    else if (inputCode != code) { //若输入的验证码与产生的验证码不一致时  
        createCode();//刷新验证码  
        document.getElementById("identifyCode").value = "";//清空文本框 
        return false;
    }
    else { //输入正确时  
        return true;
    }
}

// 手机号正则
function checkPhone() {
    var phone = document.getElementById('phoneNumber').value;
    if (!(/^1[34578]\d{9}$/.test(phone))) {
        return false;
    }
}


// 发送验证码 倒计时
var timer;

function countdown() {
    var count = 60;
    timer = window.setInterval(function () {
        count--;
        $('#sendMessage').css('background-color', '#b1adad');
        $('#sendMessage').html('重新获取(' + count + ')');
        $('#sendMessage').attr('disabled', 'disabled');
        if (count <= 0) {
            $('#sendMessage').html('发送验证码');
            $('#sendMessage').removeAttr('disabled');
            $('#sendMessage').css('background-color', '#ec4f4d');
            clearInterval(timer);
        }

    }, 1000);
}


// 两次密码是否一致
function verifyPassword() {
    var first = $('#password').text();
    var second = $('#certainPsw').text();
    if (first == second) {
        return true;
    } else {
        return false;
    }
}

function checkPassword() {
    var first = $('#password').text();
    if (first.length < 6) {
        return false;
    } else {
        return true;
    }
}

// 短信验证码不能为空
function checkMessageCode() {
    var message = $('#messageCode').text();
    if (message.length == 0) {
        return false;
    } else {
        return true;
    }
}




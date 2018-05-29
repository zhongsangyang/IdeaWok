var nph = new function () {
    this.isWechatBrowser = function () {
        var ua = window.navigator.userAgent.toLowerCase();
        return ua.match(/MicroMessenger/i) == 'micromessenger';
    };
    this.isAlipayBrowser = function () {
        var ua = window.navigator.userAgent.toLowerCase();
        return ua.indexOf("alipay") != -1;
    };
    this.isNumber = function (num) {
        var exp = /^([1-9][\d]{0,7}|0)(\.[\d]{1,2})?$/;
        if (exp.test(num)) {
            return true;
        } else {
            return false;
        }
    };
    this.showMessage = function (msg, timeout) {
        if (!timeout) {
            timeout = 2000;
        }
        if ($('#notification_bar_new').length == 0) {
            $('body')
                .append(
                    "<div id='notification_bar_new' style='margin: auto;position: fixed;top: 0; left: 0; bottom: 0; right: 0;max-height:80px; z-index:100000; text-align: center;'>"
                    + "<div id='notification_msg' style='max-width:80%;max-height:80px; line-height: 20px;background-color:#111;color:#fff;opacity:0.8;font-size:16px;z-index:100000;border-radius:5px;padding:10px;margin: 0 auto;'></div>"
                    + "</div>");
        }
        $('#notification_msg').html(msg);
    }

    this.showLoading = function () {
        if ($('#loading').length == 0) {
            $('body')
                .append(
                    '<style>.loading{position:fixed;left:0;top:0;display:table;width:100%;height:100%;text-align:center;z-index:999;background:#fff;opacity:0.9;}.content{width:40px;height:40px;vertical-align:middle;display:table-cell;}.spinner{margin:auto;width:40px;height:40px;position:relative;}.container1>div,.container2>div,.container3>div{width:10px;height:10px;background-color:#ee9c24;border-radius:100%;position:absolute;-webkit-animation:bouncedelay 1.2s infinite ease-in-out;animation:bouncedelay 1.2s infinite ease-in-out;-webkit-animation-fill-mode:both;animation-fill-mode:both;}.spinner .spinner-container{position:absolute;width:100%;height:100%;}.container2{-webkit-transform:rotateZ(45deg);transform:rotateZ(45deg);}.container3{-webkit-transform:rotateZ(90deg);transform:rotateZ(90deg);}.circle1{top:0;left:0;}.circle2{top:0;right:0;}.circle3{right:0;bottom:0;}.circle4{left:0;bottom:0;}.container2 .circle1{-webkit-animation-delay:-1.1s;animation-delay:-1.1s;}.container3 .circle1{-webkit-animation-delay:-1.0s;animation-delay:-1.0s;}.container1 .circle2{-webkit-animation-delay:-0.9s;animation-delay:-0.9s;}.container2 .circle2{-webkit-animation-delay:-0.8s;animation-delay:-0.8s;}.container3 .circle2{-webkit-animation-delay:-0.7s;animation-delay:-0.7s;}.container1 .circle3{-webkit-animation-delay:-0.6s;animation-delay:-0.6s;}.container2 .circle3{-webkit-animation-delay:-0.5s;animation-delay:-0.5s;}.container3 .circle3{-webkit-animation-delay:-0.4s;animation-delay:-0.4s;}.container1 .circle4{-webkit-animation-delay:-0.3s;animation-delay:-0.3s;}.container2 .circle4{-webkit-animation-delay:-0.2s;animation-delay:-0.2s;}.container3 .circle4{-webkit-animation-delay:-0.1s;animation-delay:-0.1s;}@-webkit-keyframes bouncedelay{0%,80%,100%{-webkit-transform:scale(0.0)}40%{-webkit-transform:scale(1.0)}}@keyframes bouncedelay{0%,80%,100%{transform:scale(0.0);-webkit-transform:scale(0.0);}40%{transform:scale(1.0);-webkit-transform:scale(1.0);}}</style><div id="loading" class="loading"><div class="content"><div class="spinner"><div class="spinner-container container1"><div class="circle1"></div><div class="circle2"></div><div class="circle3"></div><div class="circle4"></div></div><div class="spinner-container container2"><div class="circle1"></div><div class="circle2"></div><div class="circle3"></div><div class="circle4"></div></div><div class="spinner-container container3"><div class="circle1"></div><div class="circle2"></div><div class="circle3"></div><div class="circle4"></div></div></div></div></div>');
        }
        $('#loading').fadeIn('slow');
    }

    this.hideLoading = function () {
        if ($('#loading').length != 0) {
            $('#loading').fadeOut('slow');
        }
    }

    this.alert = function (title, content) {
        if ($('.weui_dialog_alert').length == 0) {
            $('body')
                .append(
                    '<div class="weui_dialog_alert" style="display: none;">'
                    + '<div class="weui_mask"></div>'
                    + '<div class="weui_dialog">'
                    + '<div class="weui_dialog_hd"><strong class="weui_dialog_title">'
                    + title
                    + '</strong></div>'
                    + '<div class="weui_dialog_bd">'
                    + content
                    + '</div>'
                    + '<div class="weui_dialog_ft">'
                    + '<a href="javascript:;" class="weui_btn_dialog primary">确定</a>'
                    + '</div>' + '</div>' + '</div>');
        } else {
            $('.weui_dialog_alert .weui_dialog_title').text(title);
            $('.weui_dialog_alert .weui_dialog_bd').text(content);
        }
        // $('.weui_dialog_alert').fadeIn();
        $('.weui_dialog_alert').css("display", "block");

        $('.weui_dialog_alert .primary').on("click", function () {
            // $('.weui_dialog_alert').fadeOut();
            $('.weui_dialog_alert').css("display", "none");
        })
    };
    this.downloadAlert = function (title, content, url) {
        if ($('.weui_dialog_alert').length == 0) {
            $('body')
                .append(
                    '<div class="weui_dialog_alert" style="display: none;">'
                    + '<div class="weui_mask"></div>'
                    + '<div class="weui_dialog">'
                    + '<div class="weui_dialog_hd"><strong class="weui_dialog_title">'
                    + title
                    + '</strong></div>'
                    + '<div class="weui_dialog_bd">'
                    + content
                    + '</div>'
                    + '<div class="weui_dialog_ft">'
                    + '<a href="'
                    + url
                    + '" class="weui_btn_dialog primary">确定</a>'
                    + '</div>' + '</div>' + '</div>');
        } else {
            $('.weui_dialog_alert .weui_dialog_title').text(title);
            $('.weui_dialog_alert .weui_dialog_bd').text(content);
        }
        // $('.weui_dialog_alert').fadeIn();
        $('.weui_dialog_alert').css("display", "block");

        $('.weui_dialog_alert .primary').on("click", function () {
            // $('.weui_dialog_alert').fadeOut();
            $('.weui_dialog_alert').css("display", "none");
        })
    };

    this.confirm = function (title, content, confirmHandler) {
        if ($('.weui_dialog_confirm').length == 0) {
            $('body')
                .append(
                    '<div class="weui_dialog_confirm" style="display: none;">'
                    + '<div class="weui_mask"></div>'
                    + '<div class="weui_dialog">'
                    + '<div class="weui_dialog_hd"><strong class="weui_dialog_title">'
                    + title
                    + '</strong></div>'
                    + '<div class="weui_dialog_bd">'
                    + content
                    + '</div>'
                    + '<div class="weui_dialog_ft">'
                    + '<a href="javascript:;" class="weui_btn_dialog default cancel">取消</a>'
                    + '<a href="javascript:;" class="weui_btn_dialog primary confirm">确定</a>'
                    + '</div>' + '</div>' + '</div>');
        } else {
            $('.weui_dialog_confirm .weui_dialog_title').text(title);
            $('.weui_dialog_confirm .weui_dialog_bd').text(content);
        }
        $('.weui_dialog_confirm').css("display", "block");

        $('.weui_dialog_confirm .cancel').on("click", function () {
            $('.weui_dialog_confirm').css("display", "none");
        })
        $('.weui_dialog_confirm .confirm').on("click", function () {
            // $('.weui_dialog_confirm').fadeOut();
            $('.weui_dialog_confirm').css("display", "none");
            s();
        })
    };
    var isQRPaying = false;
    this.launchWechatPayx = function (openId, type, province, city) {
        if (!nph.isWechatBrowser()) {
            nph.alert("微信支付", "请通过微信发起支付！");
            return;
        }
        if (isQRPaying) {
            return;
        }
        isQRPaying = true;
        $.ajax({
            type: "GET",
            url: "/wxpay/preQR/" + openId + "/" + type + ".html",
            dataType: "json",
            success: function (data) {
                var url = data.body;
                $('#download-box-js').attr('style', 'display:')
                $('#payQR').attr('src',
                    "/wxpay/preQR/qr?code_url=" + encodeURIComponent(url));
            },
            error: function (data) {
                isPaying = false;
                console.log("error: " + JSON.stringify(data));
            }
        });
    };
    var isPaying = false;
    this.launchWechatPay = function (openId, type, province, city) {
        if (!nph.isWechatBrowser()) {
            nph.alert("微信支付", "请通过微信发起支付！");
            return;
        }
        if (isPaying) {
            return;
        }
        isPaying = true;
        $
            .ajax({
                type: "GET",
                url: "/wxpay/prepare/" + openId + "/" + type + ".html",
                dataType: "json",
                success: function (data) {
                    console.log("success: " + JSON.stringify(data));
                    if (data.state == "SUCCESS") {
                        WeixinJSBridge
                            .invoke(
                                'getBrandWCPayRequest',
                                {
                                    debug: false,
                                    appId: data.body.appId,
                                    timeStamp: data.body.timeStamp,
                                    nonceStr: data.body.nonceStr,
                                    package: data.body.package,
                                    signType: data.body.signType,
                                    paySign: data.body.paySign
                                },
                                function (res) {
                                    isPaying = false;
                                    if (res.err_msg == "get_brand_wcpay_request:ok") {
                                        $
                                            .getJSON(
                                                "/wxpay/paycheck/"
                                                + openId
                                                + "/"
                                                + data.body.orderNum
                                                + ".html",
                                                function (queryDate) {
                                                    if (queryDate.state == "SUCCESS") {
                                                        if (province != null
                                                            && city != null) {
                                                            updateUserAgentAddress(
                                                                province,
                                                                city);
                                                        }
                                                        window.location.href = "/wxweb/shop.html";
                                                        nph
                                                            .showMessage(
                                                                "购买代理，支付成功！",
                                                                2000);
                                                    } else {
                                                        nph
                                                            .showMessage(
                                                                "微信支付",
                                                                "支付失败，请联系客服人员！",
                                                                2000);
                                                    }
                                                });

                                    } else {
                                        if (res.err_msg == "get_brand_wcpay_request:cancel") {
                                            nph
                                                .alert("微信支付",
                                                    "取消支付，如有疑问，请联系客服人员！")
                                        }
                                    }
                                });
                    } else {
                        isPaying = false;
                        if (data.code == 201) {
                            nph.alert("微信支付", "用户未登录，请先登录");
                        } else if (data.code == 501) {
                            nph.alert("微信支付", "系统内部错误");
                        } else if (data.code == 502) {
                            nph.alert("微信支付", "请通过微信发起支付，或者货到付款");
                        } else if (data.code == 503) {
                            nph.alert("微信支付", "无法获得用户ID，请通过微信访问");
                        } else if (data.code == 504) {
                            nph.alert("微信支付", "请不要恶意访问");
                        } else if (data.code == 506) {
                            nph.alert("微信支付", "请重新关注公共号");
                        } else if (data.code == 507) {
                            nph.alert("微信支付", "请确定选择购买的代理类型");
                        } else {
                            $("#wechatPay").css("background", "#33cc99");
                            alert("支付失败，请联系客服！")
                        }
                    }
                },
                error: function (data) {
                    isPaying = false;
                    console.log("error: " + JSON.stringify(data));
                }
            });
    };

    var isWithdraw = false;
    this.withdrawPay = function () {
        if (!nph.isWechatBrowser()) {
            nph.alert("微信提现", "请通过微信发起提现");
            return;
        }
        if (isWithdraw) {
            return;
        }
        isWithdraw = true;
        var openId = $("#openId").val();
        var withDrawAmt = $("#withDrawAmt").val();
        var stmpsw = $("#stmpsw").val();

        $.ajax({
            type: "POST",
            url: "/wxpay/withdraw.html",
            dataType: "json",
            data: {
                "openId": openId,
                "money": withDrawAmt,
                "psw": stmpsw
            },
            success: function (data) {
                console.log("success: " + JSON.stringify(data));
                if (data.state == "SUCCESS") {
                    nph.alert("佣金提现", "提现成功！")
                } else {
                    isWithdraw = false;
                    if (data.code == 501) {
                        nph.alert("佣金提现", "系统内部错误");
                    } else if (data.code == 502) {
                        nph.alert("佣金提现", "请通过微信发起提现");
                    } else if (data.code == 503) {
                        nph.alert("佣金提现", "无法获得用户ID，请通过微信访问");
                    } else if (data.code == 506) {
                        nph.alert("佣金提现", "请重新关注公共号");
                    } else if (data.code == 5061) {
                        nph.alert("佣金提现", "提现密码不正确，请重新输入");
                    } else if (data.code == 507) {
                        nph.alert("佣金提现", "提现金额大于用户可用金额，请重新输入");
                    } else if (data.code == 508) {
                        nph.alert("佣金提现", "提现失败，请重试");
                    } else {
                        $("#wechatPay").css("background", "#33cc99");
                        alert("支付失败，请联系客服！")
                    }
                }
            },
            error: function (data) {
                isWithdraw = false;
                console.log("error: " + JSON.stringify(data));
            }
        });
    };

    this.checkJsApi = function (api, success, fail) {
        wx.checkJsApi({
            jsApiList: [api],
            success: function (res) {
                if (res.checkResult[api]) {
                    success();
                } else {
                    fail();
                }
            }
        });
    }

    this.getLocation = function (options) {
        var defaults = {
            success: function (resp) {
                console.info(JSON.stringify(resp))
            },
            fail: function (resp) {
                console.info(JSON.stringify(resp))
            },
            cancel: function (resp) {
                console.info(JSON.stringify(resp))
            },
            error: function (resp) {
                console.info(JSON.stringify(resp))
            }
        };
        var opts = $.extend(defaults, options);
        initWechat(location.href, ["getLocation"], function () {
            wx.getLocation({
                type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
                success: opts.success,
                fail: opts.fail,
                cancel: opts.cancel
            });
        }, opts.error)
    };
    this.getQueryParam = function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        if (r != null)
            return (r[2]);
        return null;
    }

    this.validatePhone = function (mob) {
        var reg = /^1[3|4|5|7|8]\d{9}$/;
        if (reg.test(mob)) {
            return true;
        } else {
            return false;
        }
    };
    this.updateUserAgentAddress = function (openId, province, city) {
        $.ajax({
            type: "POST",
            url: "/wxweb/shop/updateUserAgentAddress.html",
            dataType: "json",

            data: {
                "openId": openId,
                "province": province,
                "city": city
            },
            success: function (data) {

            }
        })

    };
};

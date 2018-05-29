<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>

<style type="text/css">
.one {
	position: absolute;
	width: 200px;
	height: 200px;
	top: 50%;
	left: 50%;
	margin-top: -100px;
	margin-left: -100px;
	background: red;
}

.two {
	position: fixed;
	width: 180px;
	height: 180px;
	top: 50%;
	left: 50%;
	margin-top: -90px;
	margin-left: -90px;
	background: orange;
}

.three {
	position: fixed;
	width: 160px;
	height: 160px;
	top: 0;
	right: 0;
	bottom: 0;
	left: 0;
	margin: auto;
	background: pink;
}

.four {
	position: absolute;
	width: 140px;
	height: 140px;
	top: 0;
	right: 0;
	bottom: 0;
	left: 0;
	margin: auto;
	background: black;
}

.five {
	display: table-cell;
	vertical-align: middle;
	text-align: center;
	width: 120px;
	height: 120px;
	background: purple;
}

.six {
	width: 100px;
	height: 100px;
	line-height: 100px;
	text-align: center;
	background: gray;
}

.seven {
	width: 90px;
	height: 90px;
	display: -webkit-box;
	-webkit-box-pack: center;
	-webkit-box-align: center;
	background: yellow;
	color: black;
}

.eight {
	position: absolute;
	width: 80px;
	height: 80px;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	-webkit-transform: translate(-50%, -50%);
	-moz-transform: translate(-50%, -50%);
	-ms-transform: translate(-50%, -50%);
	background: green;
}

.nine {
	position: fixed;
	display: block;
	top: 0;
	right: 0;
	bottom: 0;
	left: 0;
	text-align: center;
	background: rgba(0, 0, 0, .5);
}

.nine:before {
	content: '';
	display: inline-block;
	vertical-align: middle;
	height: 100%;
}

.nine .content {
	display: inline-block;
	vertical-align: middle;
	width: 60px;
	height: 60px;
	line-height: 60px;
	color: red;
	background: yellow;
}

</style>
<head>

<title>结束</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<meta charset="UTF-8">
<meta name="viewport"
	content="width=device-width,initial-scale=1,user-scalable=0">
</head>
<body>
	<div align="center">交易結束</div>
</body>
</html>

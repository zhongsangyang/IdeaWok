<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
    <title>新闻咨询新增</title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <script type="text/javascript" charset="utf-8" src="${ctx}/uditor/ueditor.config.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/uditor/ueditor.all.min.js"> </script>
    <script src="${ctx}/jslib/easyui1.4.2/jquery.min.js" type="text/javascript" charset="utf-8"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/uditor/lang/zh-cn/zh-cn.js"></script>

    <style type="text/css">
        div{
            width:100%;
        }
    </style>
</head>
<body>
    <h1>新闻咨询</h1>
       <form action="${ctx}/news/addSub" method="post" id="from1"  enctype="multipart/form-data" >
           <table class="grid">	
           	<tr>
           	  <td>标题</td>
           	  <td><input type="text" name="title" id="title" style="width: 500px;right: 500px"/>  </td>
           	</tr>
           	<tr>
           	  <td>简述</td>
           	  <td><input type="text" name="sketch" id="sketch" style="width: 800px;right: 500px"/>  </td>
           	</tr>
           	<tr>
           	   <td>标题图片</td>
           	   <td> <input type="file" name="file" id="file"/></td>
           	</tr>
           	<tr>
           	   <td>咨询内容</td>
           	   <td><script id="editor" type="text/plain" style="width:1024px;height:500px;"></script></td>
           	</tr>
           	<tr>	
           	    <td>功能操作</td>
           	    <td><button type="button" onclick="from()">新增 </button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
           	        <button type="button" onclick="fanhui()">返回</button>
           	    </td>
           	</tr>
           </table>
        </form>
        
        
        
        
<script type="text/javascript">
    var ue = UE.getEditor('editor');
    function from(){
    	var title = $("#title").val();
    	if(title == null || title ==''){
    		alert("请填写标题");
    		return false;
    	}
    	var sketch = $("#sketch").val();
    	if(sketch == null || sketch ==''){
    		alert("请填写简要");
    		return false;
    	}
    	var filepath = $("#file").val()
		if(filepath==null||filepath==""){
		  alert("请选择图片");	
		  return false;
	    }
    	var extStart = filepath.lastIndexOf(".");
		var ext = filepath.substring(extStart, filepath.length).toUpperCase();
		if (ext == ".PNG" || ext == ".JPG" || ext == ".JPEG" || ext == ".GIF" || ext == ".BMP"){
          
		   }else{
        	 alert("请选择文件png|jpg|jpeg|gif|bmp文件");	
             return false;
          }
    	if(!UE.getEditor('editor').hasContents()){
    		alert("请填写内容");
    		return false;
    	}
//     	alert("cont:"+UE.getEditor('editor').getContent());
    	$("#from1").submit();
    }
    
    function fanhui(){
    	window.location.href='${ctx}/news/manager';
    }
</script>
</body>
</html>
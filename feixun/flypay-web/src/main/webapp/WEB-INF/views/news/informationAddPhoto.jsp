<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
    <title>新闻咨询图片上传页面</title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <jsp:include page="../inc.jsp"></jsp:include>
    <script type="text/javascript" charset="utf-8" src="${ctx}/uditor/ueditor.config.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/uditor/ueditor.all.min.js"> </script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/uditor/lang/zh-cn/zh-cn.js"></script>
	<script type="text/javascript">
		$(function(){
			$('#informationAddPhotoForm').form({
				url : '${ctx}/newsPhoto/addPhotos',
				onSubmit : function(){
					//检查输入
					var text1 = $("#text1").val();
					if(text1 == null || text1 == ''){
						alert("请填写标题1");
						return false;
					}
					
// 					var photo1 = $("#photo1").val();
// 					if(photo1 == null || photo1 ==''){
// 						alert("请输入图片1");
// 						return false;
// 					}
					
// 					var text2 = $("#text2").val();
// 					if(text2 == null || text2 == ''){
// 						alert("请填写标题2");
// 						return false;
// 					}
					
// 					var photo1 = $("#photo2").val();
// 					if(photo1 == null || photo1 ==''){
// 						alert("请输入图片2");
// 						return false;
// 					}
					progressLoad();
				},
				success : function(result){
					progressClose();
					result = $.parseJSON(result);
					if(result.success){
						alert(result.msg);
						window.location.href='${ctx}/newsPhoto/manager';
					}else{
						//输出错误信息
						alert(result.msg);
						return;
					}
				}
			});
		});	
		
		/*新增记录*/
		function add(){
			$("#informationAddPhotoForm").submit();
		}
		
		/*返回上一页*/
		function back(){
			window.location.href='${ctx}/newsPhoto/manager';
		}
		
		/*验证图片大小*/
		function fileChange(target){
			var fileSize = 0;
			/* var isIE = /msie/i.test(navigator.userAgent) && !window.opera;//判断是否为IE浏览器
			if(isIE && !target.files){
				var filePath = target.value;
				var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
				var file = fileSystem.GetFile(filePath);
				fileSize = file.size;
			}else{ */
				
				fileSize = target.files[0].size;
			
			/* } */
			var size = fileSize / 1024;
			if(size > 1024){
				alert("图片附件不能大于1M");
				target.value = "";
				return;
			}
		}
	</script>
</head>
<body>
    <form id="informationAddPhotoForm" method="post" enctype="multipart/form-data">
		<table class="grid">
			<tr>
				<td>标题1</td>
				<td>
				 	<!-- <input type="text" name="text1" id="text1" class="easyui-validatebox" style="width: 500px" > -->
				 	<textarea name="text1" id="text1" style="width:425px;height:50px"></textarea>
				 </td>
				
			</tr>
			<c:if test="${agentId =='F20160001'}">
			  <tr>
				 <td>标题类型</td>
				 <td>
				 	<select id="type" name="type" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'"x">
						<option value="1">咨询</option>
						<option value="2">公告</option>
					</select>
				 </td>
			 </tr>
			</c:if>
			
			
			<c:if test="${agentId =='F20160015'}">
			  <tr>
				 <td>展示位置</td>
				 <td>
				 	<select id="showLocation" name="showLocation" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'"x">
						<option value="document_library">文案库</option>
						<option value="latest_technology">最新技术文案</option>
					</select>
				 </td>
			 </tr>
			</c:if>
			
			
			<tr>
				<td>图片1</td>
				<td> <input type="file" name="photo1" id="photo1" accept="image/png,image/jpeg" onchange="fileChange(this)"></td>
			</tr>
			<tr>
				<td>标题简介2</td>
				<td> 
					<!-- <input type="text" name="text2" id="text2" style="width: 500px" > -->
					<textarea name="text2" id="text2"  style="width:425px;height:50px"></textarea>
				</td>
				
			</tr>
			<tr>
				<td>图片2</td>
				<td> <input type="file" name="photo2" id="photo2" accept="image/png,image/jpeg" onchange="fileChange(this)"></td>
			</tr>
			<tr>
				<td>文本3</td>
				<td>
					<!-- <input type="text" name="text3" id="text3" style="width: 500px" > -->
					<textarea name="text3" id="text3" style="width:425px;height:150px"></textarea>
				</td>
			</tr>
			<tr>
				<td>图片3</td>
				<td> <input type="file" name="photo3" id="photo3" accept="image/png,image/jpeg" onchange="fileChange(this)"></td>
			</tr>
			<tr>
				<td>文本4</td>
				<td>
					<!-- <input type="text" name="text4" id="text4" style="width: 500px" > -->
					<textarea name="text4" id="text4"  style="width:425px;height:150px"></textarea>
				</td>
			</tr>
			<tr>
				<td>图片4</td>
				<td> <input type="file" name="photo4" id="photo4" accept="image/png,image/jpeg" onchange="fileChange(this)"></td>
			</tr>
			<tr>
				<td>文本5</td>
				<td>
					<!-- <input type="text" name="text5" id="text5" style="width: 500px" > -->
					<textarea name="text5" id="text5" style="width:425px;height:150px"></textarea>
				</td>
			</tr>
			<tr>
				<td>图片5</td>
				<td> <input type="file" name="photo5" id="photo5" accept="image/png,image/jpeg" onchange="fileChange(this)"></td>
			</tr>
			<tr>
				<td>文本6</td>
				<td>
					<!-- <input type="text" name="text6" id="text6" style="width: 500px" > -->
					<textarea name="text6" id="text6" style="width:425px;height:150px"></textarea>
				</td>
			</tr>
			<tr>
				<td>图片6</td>
				<td> <input type="file" name="photo6" id="photo6" accept="image/png,image/jpeg" onchange="fileChange(this)"></td>
			</tr>
			
			<tr>
				<td>文本7</td>
				<td>
					<!-- <input type="text" name="text7" id="text7" style="width: 500px" > -->
					<textarea name="text7" id="text7" style="width:425px;height:150px"></textarea>
				</td>
			</tr>
			<tr>
				<td>图片7</td>
				<td> <input type="file" name="photo7" id="photo7" accept="image/png,image/jpeg" onchange="fileChange(this)"></td>
			</tr>
			
			<tr>
				<td>文本8</td>
				<td>
					<!-- <input type="text" name="text8" id="text8" style="width: 500px" > -->
					<textarea name="text8" id="text8" style="width:425px;height:150px"></textarea>
				</td>
			</tr>
			<tr>
				<td>图片8</td>
				<td> <input type="file" name="photo8" id="photo8" accept="image/png,image/jpeg" onchange="fileChange(this)"></td>
			</tr>
			
			<tr>
				<td>文本9</td>
				<td>
					<!-- <input type="text" name="text9" id="text9" style="width: 500px" > -->
					<textarea name="text9" id="text9" style="width:425px;height:150px"></textarea>
				</td>
			</tr>
			<tr>
				<td>图片9</td>
				<td> <input type="file" name="photo9" id="photo9" accept="image/png,image/jpeg" onchange="fileChange(this)"></td>
			</tr>
			
			
				<tr>	
           	    <td>功能操作</td>
           	    <td><button type="button" onclick="add()">新增 </button>
           	        <button type="button" onclick="back()">返回</button>
           	    </td>
           	</tr>
		</table>
	</form>
</body>
</html>
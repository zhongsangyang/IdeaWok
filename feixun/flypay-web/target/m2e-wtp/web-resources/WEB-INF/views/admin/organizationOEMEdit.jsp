<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script type="text/javascript">
	

	$(function() {
		
		$('#pid').combotree({
			url : '${ctx}/organization/tree?flag=false',
			parentField : 'pid',
			lines : true,
			panelHeight : 'auto',
			value :'${organization.pid}'
		});
		
		$('#organizationEditForm').form({
			url : '${ctx}/organization/edit',
			onSubmit : function() {
				progressLoad();
				var isValid = $(this).form('validate');
				if (!isValid) {
					progressClose();
				}
				return isValid;
			},
			success : function(result) {
				progressClose();
				result = $.parseJSON(result);
				if (result.success) {
					parent.$.modalDialog.openner_treeGrid.treegrid('reload');//之所以能在这里调用到parent.$.modalDialog.openner_treeGrid这个对象，是因为organization.jsp页面预定义好了
					parent.$.modalDialog.handler.dialog('close');
				}
			}
		});
		

	$("#status").val('${organization.status}');
	});
</script>
<div style="padding: 3px;">
	<form id="organizationEditForm" method="post">
		<table class="grid">
			<tr>
				<td>编号</td>
				<td><input name="code" type="text" value="${organization.code}" disabled="disabled"/></td>
				<td>运营商名称</td>
				<td><input name="name" type="text" value="${organization.name}" placeholder="请输入运营商名称" class="easyui-validatebox" data-options="required:true" ></td>
			</tr>
			<tr>
				<td>代理类型</td>
				<td>
				<select id="agentType" name="agentType" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'" disabled="disabled">
							<c:forEach items="${orgAgentType}" var="obj">
								<option value="${obj.code}" <c:if test="${organization.agentType == obj.code}">selected="selected"</c:if>>${obj.text }</option>
							</c:forEach>
					</select>
				</td>
				<td>代理级别</td>
				<td>
					<select id="agentLevel" name="agentLevel" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<c:forEach items="${orgAgentLevel}" var="obj">
								<option value="${obj.code}" <c:if test="${organization.agentLevel == obj.code}">selected="selected"</c:if>>${obj.text }</option>
							</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>钻石用户</td>
				<td><input name="diamondAgent" type="text" placeholder="用户费用" class="easyui-validatebox" data-options="required:true" value="${organization.diamondAgent}" disabled="disabled"></td>
				<td>运营商钻石分润</td>
				<td><input name="diamondFee" type="text" placeholder="若为-1，表示全部给运营商" class="easyui-validatebox" data-options="required:true" value="${organization.diamondFee}"  disabled="disabled"></td>
				
			</tr>
			<tr>
				<td>金牌用户</td>
				<td><input name="goldAgent" type="text" placeholder="用户费用" class="easyui-validatebox" data-options="required:true" value="${organization.goldAgent}" disabled="disabled"></td>
				<td>运营商金牌分润</td>
				<td><input name="goldFee" type="text" placeholder="若为-1，表示全部给运营商" class="easyui-validatebox" data-options="required:true" value="${organization.goldFee}" disabled="disabled"></td>
			</tr>
			<tr>
				<td>推广人数升钻石</td>
				<td><input name="diamondNum" type="text" class="easyui-validatebox" value="${organization.diamondNum}" disabled="disabled"></td>
				<td>推广人数升金牌</td>
				<td><input name="goldNum" type="text"  class="easyui-validatebox" value="${organization.goldNum}" disabled="disabled"></td>
			</tr>
			<tr>
				<td>上级资源</td>
				<td colspan="3"><select id="pid" name="pid" style="width: 200px; height: 29px;"></select>
				<a class="easyui-linkbutton" href="javascript:void(0)" onclick="$('#pid').combotree('clear');" >清空</a></td>
				
			</tr>
			<tr>
				<td>机构状态</td>
				<td><select id="status" name="status" value="${organization.status}" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
							<option value="0" <c:if test="${organization.status == 0}">selected="selected"</c:if>>正常</option>
							<option value="1" <c:if test="${organization.status == 1}">selected="selected"</c:if>>停用</option>
					</select></td>
				<td>APP名称</td>
				<td><input name="appName" type="text" class="easyui-validatebox"  value="${organization.appName}"  disabled="disabled"></td>
			</tr>
			<tr>
				<td>T0手续费</td>
				<td><input name="t0Fee" type="text" class="easyui-validatebox"value="${organization.t0Fee}" disabled="disabled"></td>
				<td>T1手续费</td>
				<td><input name="t1Fee" type="text" class="easyui-validatebox" value="${organization.t1Fee}"disabled="disabled"></td>
			</tr>
			<tr>
				<td>T0提现最高金额</td>
				<td><input name="maxT0Amt" type="text"  class="easyui-validatebox" value="${organization.maxT0Amt}"disabled="disabled"></td>
				<td>T1提现最高金额</td>
				<td><input name="maxT1Amt" type="text" class="easyui-validatebox" value="${organization.maxT1Amt}"disabled="disabled"></td>
			</tr>
			<tr>
				<td>T0提现最低金额</td>
				<td><input name="minT0Amt" type="text"  class="easyui-validatebox" value="${organization.minT0Amt}"disabled="disabled"></td>
				<td>T1提现最低金额</td>
				<td><input name="minT1Amt" type="text" class="easyui-validatebox" value="${organization.minT1Amt}"disabled="disabled"></td>
			</tr>
			<tr>
				<td>返佣提现手续费</td>
				<td><input name="rabaleFee" type="text" class="easyui-validatebox"value="${organization.rabaleFee}" disabled="disabled"></td>
				<td>返佣提现最低金额</td>
				<td><input name="minRabaleAmt" type="text"  class="easyui-validatebox"value="${organization.minRabaleAmt}" disabled="disabled"></td>
			</tr>
			<tr>
				<td>返佣提现最高金额</td>
				<td><input name="maxRabaleAmt" type="text"  class="easyui-validatebox" value="${organization.maxRabaleAmt}" disabled="disabled"></td>
				<td>每日提现额度</td>
				<td><input name="maxTodayOutAmt" type="text"  class="easyui-validatebox" value="${organization.maxTodayOutAmt}" disabled="disabled"></td>
				</tr>
			<tr>
				<td><font style="color: red;">是否积分制</font></td>
				<td><select id="pointType" name="pointType"  class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'" disabled="disabled">
						<option value="0" <c:if test="${organization.pointType == 0}">selected="selected"</c:if>>否</option>
						<option value="1" <c:if test="${organization.pointType == 1}">selected="selected"</c:if>>是</option>
				</select></td>
			
				<td><font style="color: red;">是否降费率</font></td>
				<td><select id="reductionUserRateType" name="reductionUserRateType"  class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'"disabled="disabled">
						<option value="0" <c:if test="${organization.reductionUserRateType == 0}">selected="selected"</c:if>>否</option>
						<option value="1" <c:if test="${organization.reductionUserRateType == 1}">selected="selected"</c:if>>积分降费率</option>
						<option value="2" <c:if test="${organization.reductionUserRateType == 2}">selected="selected"</c:if>>升级降费率</option>
				</select></td>
			</tr>
			<tr>
				<td><font style="color: red;">分润规则</font></td>
				<td><select id="shareBonusType" name="shareBonusType"  class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'" disabled="disabled">
						<option value="0" <c:if test="${organization.shareBonusType == 0}">selected="selected"</c:if>>不分润</option>
						<option value="1" <c:if test="${organization.shareBonusType == 1}">selected="selected"</c:if>>固定比例分润</option>
						<option value="2" <c:if test="${organization.shareBonusType == 2}">selected="selected"</c:if>>代理固定比例，流量利率差</option>
				</select></td>
				<td><font style="color: red;">分润层级</font></td>
				<td><select id="shareBonusLevelType" name="shareBonusLevelType"  class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'" disabled="disabled">
						<option value="0" <c:if test="${organization.shareBonusLevelType == 0}">selected="selected"</c:if>>流量+代理为3级</option>
						<option value="1" <c:if test="${organization.shareBonusLevelType == 1}">selected="selected"</c:if>>代理3级+流量2级</option>
						<option value="2" <c:if test="${organization.shareBonusLevelType == 2}">selected="selected"</c:if>>代理2级+流量2级</option>
				</select></td>
			</tr>
			<tr>
				<td><font style="color: red;">用户升级类型</font></td>
				<td><select id="userUpgradeType" name="userUpgradeType"  class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'" disabled="disabled">
						<option value="1" <c:if test="${organization.userUpgradeType == 1}">selected="selected"</c:if>>购买升级</option>
						<option value="2" <c:if test="${organization.userUpgradeType == 2}">selected="selected"</c:if>>推广升级</option>
						<option value="100" <c:if test="${organization.userUpgradeType == 100}">selected="selected"</c:if>>先到先得</option>
				</select></td>
			</tr>
			<tr>
				<td>地址</td>
				<td colspan="3"><input  name="address" style="width: 300px;" value="${organization.address}"/></td>
			</tr>
		</table>
	</form>
</div>

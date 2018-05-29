<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script type="text/javascript">
	

	$(function() {
		$('#organizationAddForm').form({
			url : '${ctx}/organization/add',
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
		
	});
</script>
<div style="padding: 3px;">
	<form id="organizationAddForm" method="post">
		<table class="grid">
		<input type="hidden" name="agentType"  value="1"/>
		<input type="hidden" name="pid"  value="1"/>
		<!-- 代理商等级，固定为1 -->
		<input type="hidden" name="orgType" value="1"/>
			<tr>
				<td>运营商编号</td>
				<td><input name="code" type="text" placeholder="例如F00020002" class="easyui-validatebox" data-options="required:true" ></td>
			</tr>
			<tr>
				<td>运营商名称</td>
				<td><input name="name" type="text" placeholder="请输入运营商名称" class="easyui-validatebox" data-options="required:true" ></td>
				<td>App名称</td>
				<td><input name="appName" type="appName" placeholder="默认App名称" class="easyui-validatebox" data-options="required:true" ></td>
			</tr>
			<tr>
				<td><font style="color: red;">是否积分制</font></td>
				<td><select id="pointType" name="pointType"  class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
						<option value="0">否</option>
						<option value="1">是</option>
				</select></td>
	
				<td><font style="color: red;">是否降费率</font></td>
				<td><select id="reductionUserRateType" name="reductionUserRateType"  class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
						<option value="0" >否</option>
						<option value="1" >积分降费率</option>
						<option value="2" >升级降费率</option>
				</select></td>
			</tr>
			<tr>
				<td><font style="color: red;">分润规则</font></td>
				<td><select id="shareBonusType" name="shareBonusType"  class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
						<option value="0" >不分润</option>
						<option value="1" >固定比例分润</option>
						<option value="2" >代理固定比例，流量利率差</option>
				</select></td>
				<td><font style="color: red;">分润层级</font></td>
				<td><select id="shareBonusLevelType" name="shareBonusLevelType"  class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
						<option value="0" >流量+代理为3级</option>
						<option value="1" >代理3级+流量2级</option>
						<option value="2" >代理2级+流量2级</option>
				</select></td>
			</tr>
			<tr>
				<td><font style="color: red;">用户升级类型</font></td>
				<td><select id="userUpgradeType" name="userUpgradeType"  class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
						<option value="1">购买升级</option>
						<option value="2">推广升级</option>
						<option value="100">先到先得</option>
				</select></td>
			</tr>
			<tr>
				<td>钻石用户</td>
				<td><input name="diamondAgent" type="text" placeholder="升级费用" class="easyui-validatebox" data-options="required:true" ></td>
				<td>运营商钻石分润</td>
				<td><input name="diamondFee" type="text" placeholder="若为-1，表示全部给运营商" class="easyui-validatebox" data-options="required:true" ></td>
			</tr>
			<tr>
				<td>金牌用户</td>
				<td><input name="goldAgent" type="text" placeholder="升级费用" class="easyui-validatebox" data-options="required:true" ></td>
				<td>运营商金牌分润</td>
				<td><input name="goldFee" type="text" placeholder="若为-1，表示全部给运营商" class="easyui-validatebox" data-options="required:true" ></td>
			</tr>
			<tr>
				<td>推广人数升钻石</td>
				<td><input name="diamondNum" placeholder="推广升级，1级下线认证通过总人数"  type="text" class="easyui-validatebox" data-options="required:true" ></td>
				<td>推广人数升金牌</td>
				<td><input name="goldNum" placeholder="推广升级，1级下线认证通过总人数"  type="text"  class="easyui-validatebox"  data-options="required:true" ></td>
			</tr>
			<tr>
				<td>机构状态</td>
				<td><select id="status" name="state"  class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
						<option value="0">正常</option>
						<option value="1">停用</option>
				</select></td>
				<td>代理级别</td>
				<td>
				<select id="agentLevel" name="agentLevel" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
						<c:forEach items="${orgAgentLevel}" var="obj">
							<option value="${obj.code}">${obj.text }</option>
						</c:forEach>
				</select>
				</td>
			</tr>
			<tr>
				<td>T0手续费</td>
				<td><input name="t0Fee" type="text" class="easyui-validatebox" ></td>
				<td>T1手续费</td>
				<td><input name="t1Fee" type="text" class="easyui-validatebox" ></td>
			</tr>
			<tr>
				<td>T0提现最高金额</td>
				<td><input name="maxT0Amt" type="text"  class="easyui-validatebox" ></td>
				<td>T1提现最高金额</td>
				<td><input name="maxT1Amt" type="text" class="easyui-validatebox" ></td>
			</tr>
			<tr>
				<td>T0提现最低金额</td>
				<td><input name="minT0Amt" type="text"  class="easyui-validatebox" ></td>
				<td>T1提现最低金额</td>
				<td><input name="minT1Amt" type="text" class="easyui-validatebox" ></td>
			</tr>
			<tr>
				<td>佣金提现手续费</td>
				<td><input name="rabaleFee" type="text" class="easyui-validatebox" ></td>
				<td>佣金提现最低金额</td>
				<td><input name="minRabaleAmt" type="text"  class="easyui-validatebox" ></td>
			</tr>
			<tr>
				<td>佣金提现最高金额</td>
				<td><input name="maxRabaleAmt" type="text"  class="easyui-validatebox" ></td>
				<td>分佣账户手机号码</td>
				<td><input name="userPhone" type="text" placeholder="请输入11位手机号" class="easyui-validatebox" data-options="validType:'validatePhone'" ></td>
			</tr>
			<tr>
				<td>每日提现额度</td>
				<td><input name="maxTodayOutAmt" type="text"  class="easyui-validatebox" ></td>
			</tr>
			
			<tr>
				<td>地址</td>
				<td colspan="3"><input  name="address" style="width: 300px;"/></td>
			</tr>
		</table>
	</form>
</div>
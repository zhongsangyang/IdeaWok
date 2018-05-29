package com.cn.flypay.controller.sys;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.pageModel.sys.OrgChannelUserRateConfig;
import com.cn.flypay.pageModel.trans.SysRateOperationHistory;
import com.cn.flypay.service.base.ServiceException;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.OrgChannelUserRateConfigService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.trans.SysRateOperationHistoryService;

@Controller
@RequestMapping("/orgChannelUserRateConfig")
public class OrgChannelUserRateConfigController extends BaseController {

	@Autowired
	private OrgChannelUserRateConfigService orgChannelUserRateConfigService;

	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private SysRateOperationHistoryService sysRateOperationHistoryService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		List<Dictionary> payTypeList = dictionaryService.combox("payType");
		request.setAttribute("payType", JSON.toJSONString(payTypeList));
		List<Dictionary> transTypeList = dictionaryService.combox("userType");
		request.setAttribute("userType", JSON.toJSONString(transTypeList));
		return "/admin/orgChannelUserRateConfig";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(OrgChannelUserRateConfig bc, PageFilter ph) {
		Grid grid = new Grid();
		grid.setRows(orgChannelUserRateConfigService.dataGrid(bc, ph));
		grid.setTotal(orgChannelUserRateConfigService.count(bc, ph));
		return grid;
	}

	@RequestMapping("/get")
	@ResponseBody
	public OrgChannelUserRateConfig get(Integer channelType, Integer agentType, Long orgId) {
		return orgChannelUserRateConfigService.get(channelType, agentType, orgId);
	}

	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Long orgId, Integer agentType, Integer channelType) {
		List<Dictionary> transTypeList = dictionaryService.combox("payType");
		request.setAttribute("payType", transTypeList);
		List<Dictionary> userTypeList = dictionaryService.combox("userType");
		request.setAttribute("userType", userTypeList);

		OrgChannelUserRateConfig u = orgChannelUserRateConfigService.get(channelType, agentType, orgId);
		request.setAttribute("orgChannelUserRateConfig", u);
		return "/admin/orgChannelUserRateConfigEdit";
	}

	@RequestMapping("/edit")
	@ResponseBody
	public Json edit(OrgChannelUserRateConfig bc, HttpServletRequest request) {
		Json j = new Json();
		try {
			orgChannelUserRateConfigService.edit(bc);
			j.setSuccess(true);
			j.setMsg("编辑成功！");
			
			//开始保存记录
			SessionInfo o = (SessionInfo) request.getSession().getAttribute(GlobalConstant.SESSION_INFO);
			SysRateOperationHistory req = new SysRateOperationHistory();
			req.setCreatorId(o.getId());
			req.setRecordType(4);	//1 用户费率编辑 2 T0最高提现限额配置  3 积分降费率配置 4 升级降费率配置
			req.setBehaviorType(1);	//1编辑 2设置
			
			List<Dictionary> payTypeList = dictionaryService.combox("payType");	//通道类型
			List<Dictionary> userTypeList = dictionaryService.combox("userType");   //用户类型
			
			//通道类型
			String payType = "";
			for(Dictionary d : payTypeList){
				if(bc.getChannelType().toString().equals(d.getCode())){
					payType = d.getText();
				}
			}
			
			//用户类型
			String userType = "";
			for(Dictionary d : userTypeList){
				if(bc.getAgentType().toString().equals(d.getCode())){
					userType = d.getText();
				}
			}
			String targetinfo = "运营商：" + bc.getOrgName()
								+",通道类型:" + payType
								+",用户类型：" + userType;
			req.setTargetInfo(targetinfo);
			req.setTargetId(bc.getOrganizationId());
			//因此逻辑中操作的数据表id主键被多个列共同制定，而id获取逻辑在此无法调用，所以不指定表的id信息
			req.setDetails("ID为" + o.getId() + "的用户，编辑了升级降费率配置,"
					+"记录来源：管理平台--系统管理--升级降费率配置");
			
			sysRateOperationHistoryService.createHistory(req);
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

}

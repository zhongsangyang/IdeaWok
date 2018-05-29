package com.cn.flypay.controller.sys;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.base.Tree;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.pageModel.sys.OrgChannel;
import com.cn.flypay.pageModel.sys.OrgSysConfig;
import com.cn.flypay.pageModel.sys.Organization;
import com.cn.flypay.pageModel.sys.OrganizationProtectRate;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.base.ServiceException;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.OrgChannelService;
import com.cn.flypay.service.sys.OrgChannelUserRateConfigService;
import com.cn.flypay.service.sys.OrgSysConfigService;
import com.cn.flypay.service.sys.OrganizationProtectRateService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.UserService;

@Controller
@RequestMapping("/organization")
public class OrganizationController extends BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(OrganizationController.class);

	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private OrgChannelService orgChannelService;
	@Autowired
	private UserService userService;
	@Autowired
	private OrgSysConfigService orgSysConfigService;
	@Autowired
	private OrganizationProtectRateService organizationProtectRateService;
	@Autowired
	private OrgChannelUserRateConfigService orgChannelUserRateConfigService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		List<Dictionary> orgAgentTypeList = dictionaryService.combox("orgAgentType");
		request.setAttribute("orgAgentType", JSON.toJSONString(orgAgentTypeList));
		return "/admin/organization";
	}

	@RequestMapping("/agentManager")
	public String agentManager(HttpServletRequest request, HttpSession session) {
		List<Dictionary> orgAgentTypeList = dictionaryService.combox("orgAgentType");
		request.setAttribute("orgAgentType", JSON.toJSONString(orgAgentTypeList));
		List<Dictionary> orgAgentLevelList = dictionaryService.combox("orgAgentLevel");
		request.setAttribute("orgAgentLevel", JSON.toJSONString(orgAgentLevelList));
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		Tuser tuser = userService.getTuser(sessionInfo.getId());
		if (tuser.getAgentId().equals("F20160017")) {
			request.setAttribute("name1", "店长");
			request.setAttribute("name2", "店员");
		} else {
			request.setAttribute("name1", "钻石");
			request.setAttribute("name2", "金牌");
		}
		return "/admin/agentOrganization";
	}

	@RequestMapping("/treeGrid")
	@ResponseBody
	public List<Organization> treeGrid(HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		User operator = userService.get(sessionInfo.getId());
		return organizationService.treeGrid(operator.getOrganizationId());
	}

	@RequestMapping("/treeByOperator")
	@ResponseBody
	public List<Tree> treeByOperator(HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		User operator = userService.get(sessionInfo.getId());
		System.out.println("operator.getOrganizationId()："+operator.getOrganizationId());
		return organizationService.tree(operator.getOrganizationId());
	}

	@RequestMapping("/tree")
	@ResponseBody
	public List<Tree> tree(HttpSession session) {
		return organizationService.tree(session);
	}

	// 查询所有的运营商tree
	@RequestMapping("/serviceProviderTree")
	@ResponseBody
	public List<Tree> serviceProviderTree(HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		User operator = userService.get(sessionInfo.getId());
		return organizationService.treeServiceProviders(operator.getOrganizationId(),
				Organization.agent_type.SERVICE_PROVIDER.getCode());
	}

	@RequestMapping("/oemProviderTree")
	@ResponseBody
	public List<Tree> oemProviderTree(HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		User operator = userService.get(sessionInfo.getId());
		return organizationService.treeServiceProviders(operator.getOrganizationId(),
				Organization.agent_type.OEM.getCode());
	}

	@RequestMapping("/addPage")
	public String addPage(Integer type, HttpServletRequest request) {

		List<Dictionary> orgAgentTypeList = dictionaryService.combox("orgAgentType");
		request.setAttribute("orgAgentType", orgAgentTypeList);
		List<Dictionary> orgAgentLevelList = dictionaryService.combox("orgAgentLevel");
		request.setAttribute("orgAgentLevel", orgAgentLevelList);
		String resultPage = "/admin/";
		switch (type) {
		case 0:// OEM
			resultPage = resultPage + "organizationOEMAdd";
			break;
		case 1:// 运营商
			resultPage = resultPage + "organizationAdd";
			break;
		case 2:// 代理商
			resultPage = resultPage + "organizationAgentAdd";
			break;
		case 3:// 添加运营商保本费率
			resultPage = resultPage + "organizationAddProtectRate";
			break;
		default:
			resultPage = resultPage + "organizationAdd";
			break;
		}
		return resultPage;
	}

	@RequestMapping("/addAgentPage")
	public String addAgentPage(HttpServletRequest request, HttpSession session) {
		List<Dictionary> orgAgentTypeList = dictionaryService.combox("orgAgentType");
		request.setAttribute("orgAgentType", orgAgentTypeList);
		List<Dictionary> orgAgentLevelList = dictionaryService.combox("orgAgentLevel");
		request.setAttribute("orgAgentLevel", orgAgentLevelList);
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		Tuser tuser = userService.getTuser(sessionInfo.getId());
		if (tuser.getAgentId().equals("F20160017")) {
			request.setAttribute("Type", "1");
		} else {
			request.setAttribute("Type", "2");
		}
		return "/admin/agentOrganizationAdd";
	}
	

//	@RequestMapping("/add")
//	@ResponseBody
//	public Json add(Organization organization, HttpSession session) {
//		Json j = new Json();
//		try {
//			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
//			User user = userService.get(sessionInfo.getId());
//			Tuser tuser = userService.getTuser(sessionInfo.getId());
//			organization.setOperateUser(user);
//			Torganization t = organizationService.getTorganizationInCodeTwo(organization.getUserPhone());
//			if(t!=null && t.getOrgType().equals("5")){
//				t.setUserPhone("0011");
//				organizationService.updateUserphone(t);
//			}
//			organizationService.add(organization);
//			if(organization.getOrgType().equals("4") && tuser.getAgentId().equals("F20160001")){
//				Tuser userp = userService.findUserByLoginNameT(organization.getUserPhone(), tuser.getAgentId());
//				if(userp!=null){
//					orgChannelUserRateConfigService.updateOrgChannelUserRate(21, userp.getId(),userp.getAgentId());
//					userp.setUserType(21);
//					userService.updateTuser(userp);
//				}
//			}
//			j.setSuccess(true);
//			j.setMsg("添加成功！");
//		} catch (Exception e) {
//			e.printStackTrace();
//			j.setMsg(e.getMessage());
//		}
//		return j;
//	}
	//TODO
	@RequestMapping("/add")
	@ResponseBody
	public Json add(Organization organization, HttpSession session) {
		Json j = new Json();
		boolean bResult = true;
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			User user = userService.get(sessionInfo.getId());
			Tuser tuser = userService.getTuser(sessionInfo.getId());
			organization.setOperateUser(user);
			//设置代理级别
			organization.setAgentLevel(2);
//			if ("5".equals(organization.getOrgType())) {//代理商
//				organization.setAgentLevel(2);
//			}else{
//				organization.setAgentLevel(1);
//			}
			Torganization org = organizationService.getTorganizationInCodeTwo(organization.getUserPhone());
			if (org != null) {
				// 如果已经存在相关手机号的代理商，不能再次添加
				LOG.info("User[operateUser={}---orgPhone={}] is Agent Already.", tuser.getLoginName(),
						org.getUserPhone());
				bResult = false;
			} else {
				// 目标用户
				Tuser targetUser = userService.findUserByLoginNameT(organization.getUserPhone(), tuser.getAgentId());

				if (targetUser == null) {
					LOG.info("Cant find Target user=[{}]", organization.getUserPhone());
					bResult = false;
				} else {
					LOG.info("ADD new organization for userId={},loginName={}.", targetUser.getId(),
							targetUser.getLoginName());
					organizationService.add(organization);// 添加代理
					if ("5".equals(organization.getOrgType())) {// 代理商
						Integer userType = null;
						if (targetUser.getAgentId().equals("F20160017")) {
							// 夏商特殊处理，老板费率类型为5
							userType = 5;
						} else {
							userType = 22;
						}
						orgChannelUserRateConfigService.updateOrgChannelUserRate(userType, targetUser.getId(),
								targetUser.getAgentId());
						targetUser.setUserType(userType);
						userService.updateTuser(targetUser);
					} else {
						if (targetUser.getAgentId().equals("F20160017")){
							//厦商特殊处理，费率等级为代理商类型
							targetUser.setUserType(Integer.valueOf(organization.getOrgType()));
						    orgChannelUserRateConfigService.updateOrgChannelUserRate(Integer.valueOf(organization.getOrgType()), targetUser.getId(),
								targetUser.getAgentId());
						} else {
							targetUser.setUserType(21);
						    orgChannelUserRateConfigService.updateOrgChannelUserRate(21, targetUser.getId(),
								targetUser.getAgentId());
						}
						userService.updateTuser(targetUser);
					}
				}
			}
			j.setSuccess(bResult);
			String msg = bResult ? "添加成功！" : "添加失败！";
			j.setMsg(msg);
		} catch (Exception e) {
			e.printStackTrace();
			j.setMsg(e.getMessage());
		}
		return j;
	}

	/**
	 * 添加运营商支付类型保本费率表
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/addProtectRate")
	@ResponseBody
	public Json addProtectRate(OrganizationProtectRate organizationProtectRate) {
		Json j = new Json();
		try {
			organizationProtectRateService.add(organizationProtectRate);
			j.setSuccess(true);
			j.setMsg("添加成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/get")
	@ResponseBody
	public Organization get(Long id) {
		return organizationService.get(id);
	}

	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Long id) {
		List<Dictionary> orgAgentTypeList = dictionaryService.combox("orgAgentType");
		request.setAttribute("orgAgentType", orgAgentTypeList);
		List<Dictionary> orgAgentLevelList = dictionaryService.combox("orgAgentLevel");
		request.setAttribute("orgAgentLevel", orgAgentLevelList);

		Organization o = organizationService.get(id);
		request.setAttribute("organization", o);
		return "/admin/organizationEdit";
	}

	@RequestMapping("/editAgentPage")
	public String editAgentPage(HttpServletRequest request, Long id, HttpSession session) {
		List<Dictionary> orgAgentTypeList = dictionaryService.combox("orgAgentType");
		request.setAttribute("orgAgentType", orgAgentTypeList);
		List<Dictionary> orgAgentLevelList = dictionaryService.combox("orgAgentLevel");
		request.setAttribute("orgAgentLevel", orgAgentLevelList);

		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		Tuser tuser = userService.getTuser(sessionInfo.getId());
		if (tuser.getAgentId().equals("F20160017")) {
			request.setAttribute("Type", "1");
		} else {
			request.setAttribute("Type", "2");
		}
		
		Organization o = organizationService.get(id);
		request.setAttribute("organization", o);
		return "/admin/agentOrganizationEdit";
	}

	@RequestMapping("/edit")
	@ResponseBody
	public Json edit(Organization org) throws InterruptedException {
		Json j = new Json();
		try {
			//设置代理级别
			if ("5".equals(org.getOrgType())) {
				org.setAgentLevel(2);
			}else{
				org.setAgentLevel(1);
			}
			organizationService.edit(org);
			j.setSuccess(true);
			j.setMsg("编辑成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/editAgentOrganization")
	@ResponseBody
	public Json editAgentOrganization(Organization org,HttpSession session) throws InterruptedException {
		Json j = new Json();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		Tuser tuser = userService.getTuser(sessionInfo.getId());
		try {
			//设置代理级别
			if ("5".equals(org.getOrgType())) {//代理商
				org.setAgentLevel(2);
			}else{
				org.setAgentLevel(1);
			}
			organizationService.editAgentOrganization(org);
			if(tuser.getAgentId().equals("F20160017")) {
				// 厦商代理级别升级
				Torganization torg = organizationService.getTorganizationInCacheById(org.getId());
				Tuser targetUser = userService.findUserByLoginNameT(torg.getUserPhone(), tuser.getAgentId());
				if (targetUser != null) {
					//厦商特殊处理，费率等级为代理商类型
					orgChannelUserRateConfigService.updateOrgChannelUserRate(Integer.valueOf(torg.getOrgType()), targetUser.getId(),
							targetUser.getAgentId());
				}
			}
			j.setSuccess(true);
			j.setMsg("编辑成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/channelFeesPage")
	public String channelFeesPage(HttpServletRequest request, Long id) {
		List<OrgChannel> lcls = orgChannelService.findOrgChannelByOrgId(id);
		request.setAttribute("orgChannels", lcls);
		return "/admin/organizationChannel";
	}

	@RequestMapping("/channelFees")
	@ResponseBody
	public Json channelFees(HttpServletRequest request) throws InterruptedException {
		Json j = new Json();
		try {
			Map<String, String[]> ms = request.getParameterMap();
			String[] ids = ms.get("id");
			String[] versions = ms.get("version");
			String[] orgIds = ms.get("orgId");
			String[] channelIds = ms.get("channelId");
			String[] realRates = ms.get("realRate");
			String[] startDates = ms.get("startDate");
			String[] endDates = ms.get("endDate");
			String[] status = ms.get("status");
			SessionInfo sessionInfo = (SessionInfo) request.getSession().getAttribute(GlobalConstant.SESSION_INFO);
			User user = userService.get(sessionInfo.getId());
			List<OrgChannel> orgs = new ArrayList<OrgChannel>();
			for (int i = 0; i < ids.length; i++) {
				OrgChannel oc = new OrgChannel(Long.parseLong(ids[i]), Integer.parseInt(versions[i]),
						BigDecimal.valueOf(Double.parseDouble(realRates[i])), user.getLoginName());
				orgs.add(oc);
			}
			orgChannelService.editOrgChannel(orgs);
			j.setSuccess(true);
			j.setMsg("编辑成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/orgSysConfigPage")
	public String orgSysConfigPage(HttpServletRequest request, Long id) {
		OrgSysConfig osc = orgSysConfigService.getByOrgId(id);
		request.setAttribute("orgSysConfig", osc);
		return "/admin/orgSysConfig";
	}

	@RequestMapping("/orgSysConfigSet")
	@ResponseBody
	public Json orgSysConfigSet(HttpServletRequest request, OrgSysConfig orgSysConfig) throws InterruptedException {
		Json j = new Json();
		try {
			orgSysConfigService.edit(orgSysConfig);
			j.setSuccess(true);
			j.setMsg("编辑成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/delete")
	@ResponseBody
	public Json delete(Long id) {
		Json j = new Json();
		try {
			organizationService.delete(id);
			j.setMsg("删除成功！");
			j.setSuccess(true);
		} catch (ServiceException e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}
}

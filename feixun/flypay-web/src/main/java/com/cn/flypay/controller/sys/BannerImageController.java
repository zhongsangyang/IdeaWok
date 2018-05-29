package com.cn.flypay.controller.sys;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.BannerImage;
import com.cn.flypay.pageModel.sys.Organization;
import com.cn.flypay.pageModel.trans.SysChannelOperationHistory;
import com.cn.flypay.service.sys.BannerImageService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.UserImageService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;

@Controller
@RequestMapping("/bannerImage")
public class BannerImageController extends BaseController {

	private Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	private BannerImageService bannerImageService;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private UserImageService imageService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/admin/bannerImageMain";
	}

	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(HttpServletRequest request, BannerImage params, PageFilter ph) {
		Grid grid = new Grid();
		ph.setSort("id");
		ph.setOrder("desc");
		grid.setRows(bannerImageService.dataGrid(params, ph));
		grid.setTotal(bannerImageService.count(params, ph));
		return grid;
	}

	@RequestMapping("/addPage")
	public String addPage(HttpServletRequest request) {
		// List<Dictionary> transTypeList =
		// dictionaryService.combox("transType");
		// request.setAttribute("transTypeObj", transTypeList);
		// List<Dictionary> statementTypeList =
		// dictionaryService.combox("statementType");
		// request.setAttribute("statementTypeObj", statementTypeList);
		//
		// List<Dictionary> channelUserTypeList =
		// dictionaryService.combox("channelUserType");
		// request.setAttribute("channelUserTypeObj", channelUserTypeList);

		return "/admin/bannerImageAdd";
	}

	@RequestMapping(value = "/add", consumes = "multipart/form-data", method = RequestMethod.POST)
	@ResponseBody
	public Json add(HttpServletRequest request, BannerImage params, MultipartFile bannerImg) {
		String name = params.getName();
		String status = params.getStatus();
		String actionUrl = params.getActionUrl();
		String orgId = params.getOrganizationId();

		// String _name = request.getParameter("name");
		// String _status = request.getParameter("status");
		// LOG.info("ADD banner name={}/Rname={},status={}/Rstatus={}", new
		// Object[] { name, _name, status, _status });

		Json j = new Json();
		try {
			// 下面是测试代码
			if (bannerImg != null) {
				String folder = DateUtil.convertDateStrYYYYMMDD(new Date());
				String fileName = imageService.writeImageTofolder(null, folder, bannerImg);
				if (StringUtil.isNotBlank(fileName)) {
					BannerImage bannerImage = new BannerImage();
					bannerImage.setName(name);
					bannerImage.setStatus(status);
					Organization organization = organizationService.get(Long.valueOf(orgId));
					bannerImage.setCode(organization.getCode());
					bannerImage.setAppName(organization.getAppName());
					bannerImage.setImgUrl(folder + fileName);
					bannerImage.setActionUrl(actionUrl);
					bannerImageService.add(bannerImage);
					LOG.info("ADD banner fileName={}", folder + fileName);
				}
			} else {
				/* 参数失败 */
				// returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				// returnJson.put("respDesc",
				// GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
			j.setSuccess(true);
			j.setMsg("添加成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/delete")
	@ResponseBody
	public Json delete(Long id, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			bannerImageService.delete(id);
			j.setMsg("停用成功！");
			j.setSuccess(true);
			LOG.info("用户ID=" + sessionInfo.getId() + "停用系统通道：ID=" + id);
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Long id) {
		// List<Dictionary> transTypeList =
		// dictionaryService.combox("transType");
		// request.setAttribute("transTypeObj", transTypeList);
		// List<Dictionary> statementTypeList =
		// dictionaryService.combox("statementType");
		// request.setAttribute("statementTypeObj", statementTypeList);
		//
		// List<Dictionary> channelUserTypeList =
		// dictionaryService.combox("channelUserType");
		// request.setAttribute("channelUserTypeObj", channelUserTypeList);
		// Channel u = channelService.get(id);
		// request.setAttribute("channel", u);
		BannerImage bannerImage = bannerImageService.get(id);
		request.setAttribute("banner", bannerImage);
		return "/admin/bannerImageEdit";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Json edit(BannerImage param, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			bannerImageService.edit(param);
			j.setSuccess(true);
			j.setMsg("编辑成功！");

			// 开始保存修改记录
//			SysChannelOperationHistory record = new SysChannelOperationHistory();
//			record.setCreatorId(sessionInfo.getId());
//			record.setTargetId(param.getId());
//			record.setRecordType(1); // 记录类型为1 支付通道
//			record.setBehaviorType(1); // 行为类型为1编辑
//			record.setDetails("用户ID=" + sessionInfo.getId() + "编辑" + "sys_channel 表中ID=" + param.getId() + " 的通道，记录来源：管理平台--通道管理--支付通道");
			// sysChannelOperationHistoryService.createHistory(record);

		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}
}

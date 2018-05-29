package com.cn.flypay.controller.account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.model.account.TaccountPoint;
import com.cn.flypay.pageModel.account.AccountPoint;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.SysUserRelateFreezeHistory;
import com.cn.flypay.service.account.AccountPointService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.trans.SysUserRelateFreezeHistoryService;

@Controller
@RequestMapping("/point")
public class PointController extends BaseController {

	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private AccountPointService pointAccountService;
	@Autowired
	private UserService userService;
	@Autowired
	private SysUserRelateFreezeHistoryService sysUserRelateFreezeHistoryService;
	
	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/account/point";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(AccountPoint point, PageFilter ph, HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		User user = userService.get(sessionInfo.getId());
		point.setOperateUser(user);

		Grid grid = new Grid();
		grid.setRows(pointAccountService.dataGrid(point, ph));
		grid.setTotal(pointAccountService.count(point, ph));
		return grid;
	}

	@RequestMapping("/freeze")
	@ResponseBody
	public Json freeze(Long id, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			TaccountPoint ta =pointAccountService.freeze(id);
			
			j.setMsg("操作成功！");
			j.setSuccess(true);
			log.info("用户ID=" + sessionInfo.getId() + "冻结或解冻 account_point 表中：ID=" + id +"的用户积分");
			//添加冻结/解冻记录
			int status = ta.getStatus();	// 0 正常  1冻结
			SysUserRelateFreezeHistory freeze = new SysUserRelateFreezeHistory();
			freeze.setCreatorId(sessionInfo.getId());
			freeze.setTargetId(ta.getUser().getId());	//方法参入参数ID为account_point的id,实际用户id应为user_id
			freeze.setRecordType(3);	//代表记录是属于用户积分类型
			freeze.setBehaviorType(status);
			String remarkStr = (status ==0)? "解冻" : "冻结";
			freeze.setDetails("用户ID=" + sessionInfo.getId() + remarkStr + " account_point 表中ID=" +id +"的用户积分,记录来源:管理平台--交易中心--用户积分");
			System.out.println(freeze.getDetails());
			sysUserRelateFreezeHistoryService.createHistory(freeze);
			
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}
}

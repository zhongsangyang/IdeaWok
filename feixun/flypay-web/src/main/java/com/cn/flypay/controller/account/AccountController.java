package com.cn.flypay.controller.account;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.model.account.Taccount;
import com.cn.flypay.pageModel.account.Account;
import com.cn.flypay.pageModel.account.AccountAdjust;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.Down;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.SysUserRelateFreezeHistory;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.account.AccountService;
import com.cn.flypay.service.account.AccoutLogService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.trans.SysUserRelateFreezeHistoryService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;

@Controller
@RequestMapping("/account")
public class AccountController extends BaseController {
	//private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private AccountService accountService;
	@Autowired
	private UserOrderService orderService;
	@Autowired
	private UserService userService;
	@Autowired
	private AccoutLogService accoutLogService;
	@Autowired
	private SysUserRelateFreezeHistoryService sysUserRelateFreezeHistoryService;
	
	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/account/account";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(Account account, PageFilter ph, HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		User user = userService.get(sessionInfo.getId());
		account.setOperateUser(user);
		Grid grid = new Grid();
		grid.setRows(accountService.dataGrid(account, ph));
		grid.setTotal(accountService.count(account, ph));
		return grid;
	}

	@RequestMapping("/agentManager")
	public String agentManager(HttpServletRequest request) {
		return "/account/agentAccount";
	}

	@RequestMapping("/freeze")
	@ResponseBody
	public Json freeze(Account account, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			Taccount t = accountService.freeze(account);
			j.setMsg("操作成功！");
			j.setSuccess(true);
			log.info("用户ID=" + sessionInfo.getId() + "冻结或解冻 account 表中：ID=" + t.getId()+" 的用户账户");
			//添加  冻结/解冻记录
			SysUserRelateFreezeHistory freeze = new SysUserRelateFreezeHistory();
			int status = account.getStatus();
			String remarkStr = "";
			if(status == 0){
				remarkStr = "解冻";
			}else if(status == 1){
				remarkStr = "冻结";
			}else if(status == 100){
				remarkStr = "跨平台冻结"; 
			}
			freeze.setDetails("用户ID=" + sessionInfo.getId() + remarkStr + "account表ID="+ account.getId() +"用户账户，记录来源：管理平台--交易中心--用户账户");
			log.info(freeze.getDetails());
			
			freeze.setCreatorId(sessionInfo.getId());
			freeze.setTargetId( t.getUser().getId());	//account账户表中对应的user_id才是需要的商户id
			freeze.setRecordType(2);	//代表记录是属于用户账户类型
			//为了统计操作表中类型的连贯与一致性，将跨平台冻结定为2
			if(status == 100){
				freeze.setBehaviorType(2);
			}else{
				freeze.setBehaviorType(status);
			}
			
			sysUserRelateFreezeHistoryService.createHistory(freeze);
			
		} catch (Exception e) {
			log.error(e);
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/agentFreeze")
	@ResponseBody
	public Json agentFreeze(Long id, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			accountService.freeze(id);
			j.setMsg("操作成功！");
			j.setSuccess(true);
			log.info("用户ID=" + sessionInfo.getId() + "冻结或解冻：ID=" + id);
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/freezePage")
	public String freezePage(HttpServletRequest request, Long id) {
		Account account = accountService.get(id);
		request.setAttribute("account", account);
		return "/account/freezeAccount";
	}

	@RequestMapping("/viewRemarktPage")
	public String viewRemarktPage(HttpServletRequest request, Long id) {
		Account account = accountService.get(id);
		request.setAttribute("remark", account.getRemark());
		request.setAttribute("id", id);
		return "/account/accountRemark";
	}

	@RequestMapping("/accountRemark")
	@ResponseBody
	public Json accountRemark(Account account, HttpServletRequest request) {
		Json j = new Json();
		try {
			accountService.editRemark(account);
			j.setMsg("修改成功！");
			j.setSuccess(true);
			// log.info("用户ID=" + sessionInfo.getId() + "停用系统参数：ID=" + id);
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/adjustAccountPage")
	public String adjustAccountPage(HttpServletRequest request, Long id) {
		Account account = accountService.get(id);
		request.setAttribute("account", account);
		return "/account/adjustAccount";
	}

	@RequestMapping("/adjustAccount")
	@ResponseBody
	public Json adjustAccount(AccountAdjust adjust, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			User user = userService.get(sessionInfo.getId());
			accountService.dealAdjustAccount(user, adjust);
			j.setMsg("调账成功！");
			j.setSuccess(true);
			//log.info("用户ID=" + sessionInfo.getId() + "停用系统参数：ID=" + id);
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping("/jupLog")
	@ResponseBody
	public Json adjust(String param,String mon) {
		Json j = new Json();
		try {
			accoutLogService.editadjust(param, mon);
			j.setMsg("成功!");
			j.setSuccess(true);
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/validateOrderNumAndAmt")
	@ResponseBody
	public Json validateOrderNumAndAmt(Long userId, String adjustType, Double amt, String orderNum, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			// accountService.freeze(id);
			if (StringUtil.isNotEmpty(orderNum)) {
				String[] orderNums = orderNum.split(",");
				List<UserOrder> uos = orderService.findUserOrderByOrderNums(orderNums);
				if (uos.size() == orderNums.length) {
					j.setMsg("验证成功！");
					j.setSuccess(true);
				} else {
					j.setSuccess(false);
					j.setMsg("订单号输入错误");
				}
			}
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}
	

	
	@RequestMapping("/exportExcel")
	@ResponseBody
	public void exportExcel(HttpServletRequest request, HttpServletResponse response, HttpSession session,Down down) {
		try {
			String dateStr = down.getDownTime();
			String fileName = "";
			String simpleName = "";
			if("account".equals(down.getType())) {
				fileName = "D://account//" + dateStr + "//account" + dateStr + ".xlsx";
				simpleName = "account" + dateStr + ".xlsx";
			}else {
				fileName = "D://account//" + dateStr + "//brokerage" + dateStr + ".xlsx";
				simpleName = "brokerage" + dateStr + ".xlsx";
			}			
			response.setCharacterEncoding("utf-8");
			response.setContentType("multipart/octet-stream");
			response.setHeader("Content-Disposition", "attachment;fileName=" + simpleName);
			response.setHeader("Content-type", "charset=UTF-8");
			OutputStream ouputStream = response.getOutputStream();
			// 创建读取文件的流
			InputStream in = new FileInputStream(fileName);
			byte[] buffer = new byte[1024 * 20];
			int len = 0;
			while ((len = in.read(buffer)) > 0) {
				ouputStream.write(buffer, 0, len);
			}
			ouputStream.flush();
			ouputStream.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("下载失败", e);
		}
	}	
}

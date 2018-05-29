package com.cn.flypay.controller.account;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

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
import com.cn.flypay.model.trans.Tbrokerage;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.Down;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.Brokerage;
import com.cn.flypay.pageModel.trans.OffLineDrawOrder;
import com.cn.flypay.pageModel.trans.SysUserRelateFreezeHistory;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.trans.BrokerageService;
import com.cn.flypay.service.trans.SysUserRelateFreezeHistoryService;
import com.cn.flypay.utils.DateUtil;

@Controller
@RequestMapping("/brokerage")
public class BrokerageController extends BaseController {
	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private BrokerageService brokerageService;
	@Autowired
	private UserService userService;
	@Autowired
	private SysUserRelateFreezeHistoryService sysUserRelateFreezeHistoryService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/trans/brokerage";
	}

	@RequestMapping("/agentManager")
	public String agentManager(HttpServletRequest request) {
		return "/trans/agentBrokerage";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(Brokerage app, PageFilter ph, HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		User user = userService.get(sessionInfo.getId());
		app.setOperateUser(user);
		Grid grid = new Grid();
		grid.setRows(brokerageService.dataGrid(app, ph));
		grid.setTotal(brokerageService.count(app, ph));
		return grid;
	}

	@RequestMapping("/freeze")
	@ResponseBody
	public Json freeze(Long id, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			// 冻结之后返回手动更改过的状态，规避事物提交的延迟
			Tbrokerage ta = brokerageService.freeze(id);
			j.setMsg("操作成功！");
			j.setSuccess(true);
			log.info("用户ID=" + sessionInfo.getId() + "冻结或解冻：ID=" + id + " 的用户佣金");

			// 添加冻结/解冻记录
			int status = ta.getStatus(); // 0 正常 1冻结
			SysUserRelateFreezeHistory freeze = new SysUserRelateFreezeHistory();
			freeze.setCreatorId(sessionInfo.getId());
			freeze.setTargetId(ta.getUser().getId());
			freeze.setRecordType(1); // 代表记录是属于用户佣金类型
			freeze.setBehaviorType(status);
			String remarkStr = (status == 0) ? "解冻" : "冻结";
			freeze.setDetails("用户ID=" + sessionInfo.getId() + remarkStr + "TRANS_BROKERAGE 表中 ID=" + ta.getId()
					+ " 的用户佣金,记录来源:管理平台--交易中心--用户佣金");
			System.out.println(freeze.getDetails());
			sysUserRelateFreezeHistoryService.createHistory(freeze);
		} catch (Exception e) {
			log.error(e);
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/exportExcel")
	@ResponseBody
	public void exportExcel(HttpServletRequest request, HttpServletResponse response, HttpSession session,Down down) {
		try {
			String dateStr = down.getDownTime();
			String fileName = "D://account//" + dateStr + "//brokerage" + dateStr + ".xlsx";
			String simpleName = "brokerage" + dateStr + ".xlsx";
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

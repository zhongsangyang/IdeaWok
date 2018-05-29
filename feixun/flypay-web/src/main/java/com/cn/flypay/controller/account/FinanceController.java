package com.cn.flypay.controller.account;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.account.AgentFinanceProfit;
import com.cn.flypay.pageModel.account.OrgFinanceProfit;
import com.cn.flypay.pageModel.account.PlatformChannelProfit;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.FinanceAccount;
import com.cn.flypay.pageModel.trans.FinanceStatement;
import com.cn.flypay.service.common.CommonService;
import com.cn.flypay.service.statement.FinanceService;
import com.cn.flypay.service.statement.FinanceStatementService;
import com.cn.flypay.service.statement.OrderStatementService;
import com.cn.flypay.service.statement.StatementService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;

@Controller
@RequestMapping("/finance")
public class FinanceController extends BaseController {

	private Log logger = LogFactory.getLog(getClass());
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private OrderStatementService orderStatementService;
	@Autowired
	private StatementService tradeStatementService;
	@Autowired
	private UserService userService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private UserOrderService orderService;
	@Autowired
	private FinanceStatementService financeStatementService;
	@Autowired
	private FinanceService financeService;

	/**
	 * 平台通道利润
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/platformChannelTotalProfitManager")
	public String platformChannelTotalProfitManager(HttpServletRequest request) {
		List<Dictionary> statementTypeList = dictionaryService.combox("statementType");
		request.setAttribute("statementType", JSON.toJSONString(statementTypeList));
		return "/trans/platformChannelTotalProfit";
	}

	@ResponseBody
	@RequestMapping("/platformChannelTotalProfitGrid")
	public Grid platformChannelTotalProfitGrid(HttpServletRequest request, FinanceStatement statement) {
		Grid grid = new Grid();
		try {
			Date startDate = DateUtil.getBeforeDate(new Date(), 0);
			if (StringUtil.isNotBlank(statement.getStatemtentDateStart())) {
				startDate = DateUtil.getBeforeDate(DateUtil.getDateFromString(statement.getStatemtentDateStart()), 0);
			}
			Date endDate = DateUtil.getEndOfDay(new Date());
			if (StringUtil.isNotBlank(statement.getStatemtentDateEnd())) {
				endDate = DateUtil.getEndOfDay(DateUtil.getDateFromString(statement.getStatemtentDateEnd()));
			}
			List<PlatformChannelProfit> b = financeService.findPlatformChannelProfitListByDateInterval(startDate, endDate, null);
			grid.setRows(b);
			grid.setTotal(b.size() * 1l);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return grid;
	}

	@RequestMapping("/exportExcel")
	@ResponseBody
	public ModelAndView exportExcel(HttpServletRequest request, FinanceStatement statement, HttpServletResponse response, HttpSession session) throws Exception {
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			User user = userService.get(sessionInfo.getId());

			response.setCharacterEncoding("utf-8");
			response.setContentType("multipart/form-data");
			response.setHeader("Content-Disposition", "attachment;fileName=" + DateUtil.convertCurrentDateTimeToString() + ".xls");
			response.setHeader("Content-type", "charset=UTF-8");
			OutputStream ouputStream = response.getOutputStream();

			Date startDate = DateUtil.getBeforeDate(new Date(), 0);
			if (StringUtil.isNotBlank(statement.getStatemtentDateStart())) {
				startDate = DateUtil.getBeforeDate(DateUtil.getDateFromString(statement.getStatemtentDateStart()), 0);
			}
			Date endDate = DateUtil.getEndOfDay(new Date());
			if (StringUtil.isNotBlank(statement.getStatemtentDateEnd())) {
				endDate = DateUtil.getEndOfDay(DateUtil.getDateFromString(statement.getStatemtentDateEnd()));
			}
			Workbook wb = financeService.exportPlatformChannelProfitListByDateInterval(startDate, endDate, user);
			wb.write(ouputStream);
			ouputStream.flush();
			ouputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("下载失败", e);
		}
		return null;
	}

	/**
	 * 运营商收益信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/orgProfitManager")
	public String orgProfitManager(HttpServletRequest request) {
		return "/trans/orgFinanceProfit";
	}

	@ResponseBody
	@RequestMapping("/orgProfitDataGrid")
	public Grid orgProfitDataGrid(FinanceStatement statement, HttpSession session) {
		Grid grid = new Grid();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			User operator = userService.get(sessionInfo.getId());
			Date startDate = DateUtil.getBeforeDate(new Date(), 0);
			if (StringUtil.isNotBlank(statement.getStatemtentDateStart())) {
				startDate = DateUtil.getBeforeDate(DateUtil.getDateFromString(statement.getStatemtentDateStart()), 0);
			}
			Date endDate = DateUtil.getEndOfDay(new Date());
			if (StringUtil.isNotBlank(statement.getStatemtentDateEnd())) {
				endDate = DateUtil.getEndOfDay(DateUtil.getDateFromString(statement.getStatemtentDateEnd()));
			}
			List<OrgFinanceProfit> fpfs = financeService.findFinanceProfitListByDateInterval(startDate, endDate, operator);
			grid.setRows(fpfs);
			grid.setTotal((long) fpfs.size());
		} catch (Exception e) {
			logger.error("运营商收益异常", e);
		}
		return grid;

	}

	/**
	 * 代理商收益信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/agentProfitManager")
	public String agentProfitManager(HttpServletRequest request) {
		return "/trans/agentFinanceProfit";
	}

	@ResponseBody
	@RequestMapping("/agentProfitDataGrid")
	public Grid agentProfitDataGrid(FinanceStatement statement, HttpSession session) {
		Grid grid = new Grid();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			User operator = userService.get(sessionInfo.getId());
			Date startDate = DateUtil.getBeforeDate(new Date(), 0);
			if (StringUtil.isNotBlank(statement.getStatemtentDateStart())) {
				startDate = DateUtil.getBeforeDate(DateUtil.getDateFromString(statement.getStatemtentDateStart()), 0);
			}
			Date endDate = DateUtil.getEndOfDay(new Date());
			if (StringUtil.isNotBlank(statement.getStatemtentDateEnd())) {
				endDate = DateUtil.getEndOfDay(DateUtil.getDateFromString(statement.getStatemtentDateEnd()));
			}
			List<AgentFinanceProfit> fpfs = financeService.findAgentFinanceProfitListByDateInterval(startDate, endDate, operator);
			grid.setRows(fpfs);
			grid.setTotal((long) fpfs.size());
		} catch (Exception e) {
			logger.error("代理商收益异常", e);
		}
		return grid;

	}

	@RequestMapping("/exportAgentProfitExcel")
	@ResponseBody
	public ModelAndView exportAgentProfitExcel(HttpServletRequest request, FinanceStatement statement, HttpServletResponse response, HttpSession session) throws Exception {
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			User user = userService.get(sessionInfo.getId());

			response.setCharacterEncoding("utf-8");
			response.setContentType("multipart/form-data");
			response.setHeader("Content-Disposition", "attachment;fileName=" + DateUtil.convertCurrentDateTimeToString() + ".xls");
			response.setHeader("Content-type", "charset=UTF-8");
			OutputStream ouputStream = response.getOutputStream();

			Date startDate = DateUtil.getBeforeDate(new Date(), 0);
			if (StringUtil.isNotBlank(statement.getStatemtentDateStart())) {
				startDate = DateUtil.getBeforeDate(DateUtil.getDateFromString(statement.getStatemtentDateStart()), 0);
			}
			Date endDate = DateUtil.getEndOfDay(new Date());
			if (StringUtil.isNotBlank(statement.getStatemtentDateEnd())) {
				endDate = DateUtil.getEndOfDay(DateUtil.getDateFromString(statement.getStatemtentDateEnd()));
			}
			Workbook wb = financeService.exportAgentProfitListByDateInterval(startDate, endDate, user);
			wb.write(ouputStream);
			ouputStream.flush();
			ouputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("下载失败", e);
		}
		return null;
	}

	/**
	 * 实时的系统账务总计
	 * 
	 * @param financeAccount
	 * @param ph
	 * @return
	 */
	@RequestMapping("/financeAccountManager")
	public String financeAccountDataGrid(HttpServletRequest request) {
		return "/account/financeAccount";
	}

	@ResponseBody
	@RequestMapping("/financeAccountDataGrid")
	public Grid financeAccountDataGrid(FinanceAccount financeAccount, PageFilter ph) {
		Grid grid = new Grid();
		List<FinanceAccount> bl = financeService.findRealTimeAccount();
		grid.setRows(bl);
		grid.setTotal(1l);
		return grid;
	}

}

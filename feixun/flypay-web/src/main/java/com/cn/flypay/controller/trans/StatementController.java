package com.cn.flypay.controller.trans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.model.util.CollectionUtil;
import com.cn.flypay.pageModel.account.AccountOrderError;
import com.cn.flypay.pageModel.account.FinanceProfit;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.pageModel.trans.FinanceStatement;
import com.cn.flypay.pageModel.trans.Statement;
import com.cn.flypay.service.common.CommonService;
import com.cn.flypay.service.statement.ErrorService;
import com.cn.flypay.service.statement.FinanceService;
import com.cn.flypay.service.statement.FinanceStatementService;
import com.cn.flypay.service.statement.OrderStatementService;
import com.cn.flypay.service.statement.StatementService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.ZipUtil;

@Controller
@RequestMapping("/statement")
public class StatementController extends BaseController {

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
	@Autowired
	private ErrorService errorService;
	@Value("${pingan_statement_root_path}")
	private String pingan_statement_root_path;

	/**
	 * 对账单统计
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		List<Dictionary> statementStatusList = dictionaryService.combox("statementChannelType");
		request.setAttribute("orderType", JSON.toJSONString(statementStatusList));
		return "/trans/statement";
	}

	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(Statement statement, PageFilter ph) {
		Grid grid = new Grid();
		grid.setRows(tradeStatementService.dataGrid(statement, ph));
		grid.setTotal(tradeStatementService.count(statement, ph));
		return grid;
	}

	/**
	 * 下载平安对账单
	 * 
	 * @param statement
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/downloadPinganStatement")
	public ModelAndView downloadPinganStatement(HttpServletResponse response, String statemtentDate) {
		logger.info("---下载" + statemtentDate + "平安对账单 begin---");
		// 获取下载的对账单名称
		List<String> fileNames = financeService.findPinganStatement(statemtentDate);
		if (CollectionUtil.isNotEmpty(fileNames)) {
			// ZIP打包存储路径
			String zipFilePath = pingan_statement_root_path + File.separator + "statement.zip";
			File zipFile = new File(zipFilePath);
			try {
				byte[] buf = new byte[8192];
				int len;
				// zip输出
				ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipFile));
				// 压缩文件到zip
				for (int i = 0; i < fileNames.size(); i++) {
					// 读取每一个文件
					FileInputStream in = new FileInputStream(
							new File(pingan_statement_root_path + File.separator + fileNames.get(i)));
					// 添加到zip
					zout.putNextEntry(new ZipEntry(fileNames.get(i)));
					while ((len = in.read(buf)) > 0) {
						zout.write(buf, 0, len);
					}
					zout.closeEntry();
					in.close();
				}
				zout.close();
				// 设置浏览器下载响应
				response.setCharacterEncoding("utf-8");
				response.setContentType("multipart/form-data");
				response.setHeader("Content-Disposition", "attachment;fileName=" + statemtentDate + ".zip");
				response.setHeader("Content-type", "charset=UTF-8");
				OutputStream ouputStream = response.getOutputStream();
				// 读取压缩文件
				FileInputStream in = new FileInputStream(zipFile);
				// 写出
				while ((len = in.read(buf)) != -1) {

					ouputStream.write(buf, 0, len);
				}
				in.close();
				ouputStream.flush();
				ouputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("---下载平安对账单失败---");
				;
			} finally {
				// 删除压缩包
				if (zipFile.exists()) {
					// 删除已存在的目标文件
					zipFile.delete();
				}
				logger.info("---下载" + statemtentDate + "平安对账单成功---");
			}
		}
		return null;
	}

	/**
	 * 财务统计
	 * 
	 * @param statement
	 * @param ph
	 * @return
	 */
	@RequestMapping("/financeManager")
	public String financeManager(HttpServletRequest request) {
		List<Dictionary> statementStatusList = dictionaryService.combox("statementType");
		request.setAttribute("orderType", JSON.toJSONString(statementStatusList));
		return "/trans/financeStatement";
	}

	@ResponseBody
	@RequestMapping("/financeDataGrid")
	public Grid financeDataGrid(FinanceStatement statement, HttpSession session) {
		Grid grid = new Grid();
		if (StringUtil.isNotBlank(statement.getStatemtentDate())) {
			System.out.println("statement.getStatemtentDate()"+statement.getStatemtentDate());
			List<FinanceStatement> bl = orderService.findFinanceStatementPerDate(statement);
			
			grid.setRows(bl);
			grid.setTotal(1l);
		}
		return grid;
	}

	/**
	 * 系统代付
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/profitManager")
	public String getFinanceAccount(HttpServletRequest request) {
		return "/trans/financeProfit";
	}

	@ResponseBody
	@RequestMapping("/profitDataGrid")
	public Grid profitDataGrid(FinanceProfit statement, PageFilter ph) {
		Grid grid = new Grid();
		grid.setRows(financeStatementService.dataGrid(statement, ph));
		grid.setTotal(financeStatementService.count(statement, ph));
		return grid;
	}

	@RequestMapping("/exportMinShendowoan")
	@ResponseBody
	public ModelAndView exportMinShendowoan(HttpServletResponse response, String statemtentDate) throws Exception {
		response.setCharacterEncoding("utf-8");
		response.setContentType("multipart/form-data");
		response.setHeader("Content-Disposition",
				"attachment;fileName=" + DateUtil.convertCurrentDateTimeToString() + ".xls");
		response.setHeader("Content-type", "charset=UTF-8");
		OutputStream ouputStream = response.getOutputStream();
		Workbook wb = financeService.getMinShendowoan(statemtentDate);
		wb.write(ouputStream);
		ouputStream.flush();
		ouputStream.close();
		return null;
	}

	@RequestMapping("/exportShenFudowoan")
	@ResponseBody
	public ModelAndView exportShenFudowoan(HttpServletResponse response, String statemtentDate) throws Exception {
		response.setCharacterEncoding("utf-8");
		response.setContentType("multipart/form-data");
		response.setHeader("Content-Disposition",
				"attachment;fileName=" + DateUtil.convertCurrentDateTimeToString() + ".xls");
		response.setHeader("Content-type", "charset=UTF-8");
		OutputStream ouputStream = response.getOutputStream();
		Workbook wb = financeService.getShenFudowoan(statemtentDate);
		wb.write(ouputStream);
		ouputStream.flush();
		ouputStream.close();
		return null;
	}

	@RequestMapping("/exportDailyProfitDowoan")
	@ResponseBody
	public ModelAndView exportDailyProfitDowoan(HttpServletResponse response, String statemtentDate) throws Exception {
		response.setCharacterEncoding("utf-8");
		response.setContentType("multipart/form-data");
		response.setHeader("Content-Disposition",
				"attachment;fileName=" + DateUtil.convertCurrentDateTimeToString() + ".xls");
		response.setHeader("Content-type", "charset=UTF-8");
		OutputStream ouputStream = response.getOutputStream();
		Workbook wb = financeService.exportDailyProfitDowoan(statemtentDate);
		wb.write(ouputStream);
		ouputStream.flush();
		ouputStream.close();
		return null;
	}

	@RequestMapping("/errorManager")
	public String errorManager(HttpServletRequest request) {
		return "/trans/error";
	}

	@ResponseBody
	@RequestMapping("/errordataGrid")
	public Grid errordataGrid(AccountOrderError accountOrderError, PageFilter ph) throws Exception {
		Grid grid = new Grid();
		if (StringUtil.isNotBlank(accountOrderError.getCreateTime())) {
			grid.setRows(errorService.dataGrid(accountOrderError, ph));
			grid.setTotal(errorService.count(accountOrderError, ph));
		}
		return grid;
	}

	@RequestMapping("/dailyProfitDetaildowoan")
	@ResponseBody
	public ModelAndView dailyProfitDetaildowoan(HttpServletResponse response, String statemtentDate) throws Exception {
		response.setCharacterEncoding("utf-8");
		response.setContentType("multipart/form-data");
		response.setHeader("Content-Disposition",
				"attachment;fileName=" + DateUtil.convertCurrentDateTimeToString() + ".xls");
		response.setHeader("Content-type", "charset=UTF-8");
		OutputStream ouputStream = response.getOutputStream();
		Workbook wb = financeService.exportProfitDetaildowoan(statemtentDate);
		wb.write(ouputStream);
		ouputStream.flush();
		ouputStream.close();
		return null;
	}
	
	
	@RequestMapping("/downloadTable")
	public String downloadTable(HttpServletRequest request) {
		
		return "/account/down";
	}



}

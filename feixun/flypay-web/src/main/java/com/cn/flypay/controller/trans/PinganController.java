package com.cn.flypay.controller.trans;

import java.io.OutputStream;
import java.util.Date;

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

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.model.trans.TpinganFileDeal;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.PinganFileDeal;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.common.CommonService;
import com.cn.flypay.service.payment.PingAnExpenseService;
import com.cn.flypay.service.payment.PinganFileDealService;
import com.cn.flypay.service.payment.ZanshanfuExpenseService;
import com.cn.flypay.service.statement.OrderStatementService;
import com.cn.flypay.service.statement.StatementService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;

@Controller
@RequestMapping("/pingan")
public class PinganController extends BaseController {

	private Log logger = LogFactory.getLog(getClass());
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private OrderStatementService orderStatementService;
	@Autowired
	private PingAnExpenseService pingAnExpenseService;
	@Autowired
	private ZanshanfuExpenseService zanshanfuExpenseService;
	@Autowired
	private PinganFileDealService pinganFileDealService;
	@Autowired
	private UserService userService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private StatementService tradeStatementService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/trans/pingan";
	}

	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(PinganFileDeal orderStatement, PageFilter ph) {
		Grid grid = new Grid();
		grid.setRows(pinganFileDealService.dataGrid(orderStatement, ph));
		grid.setTotal(pinganFileDealService.count(orderStatement, ph));
		return grid;
	}

	/**
	 * 发送批量的T1代付文件
	 * 
	 * @param dealDate
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sendT1Order")
	public Json sendT1Order(String dealDate) {
		Json j = new Json();
		try {
			TpinganFileDeal fileDeal = pinganFileDealService.getFileDealByFileName(DateUtil.convertDateStrYYYYMMDD(dealDate));
			if (fileDeal != null) {
				if (PinganFileDeal.file_status.file04_success.name().equals(fileDeal.getStatus())
						|| PinganFileDeal.file_status.file02_success.name().equals(pingAnExpenseService.sendFile02ToPingAn(fileDeal).getStatus())) {
					j.setSuccess(true);
					j.setMsg("发送T1代付文件已经成功，无需再次发送！");
				} else if (PinganFileDeal.file_status.file02_fail.name().equals(pingAnExpenseService.sendFile02ToPingAn(fileDeal).getStatus())) {
					/* 重新发送代付文件给平安 */
					fileDeal.setTradeSn(commonService.getUniqueTradeSn());
					pingAnExpenseService.sendFile01ToPingAn(fileDeal);
					j.setSuccess(true);
					j.setMsg("连接平安的通道异常，已再次重新发送T1代付文件！");
				} else {
					pingAnExpenseService.sendFile02ToPingAn(fileDeal);
					j.setMsg("发送T1失败，请查看指令状态");
					logger.info("代付指令已发出，请稍后重试");
				}
			} else {
				/* 发送T1代付文件 */
				fileDeal = pingAnExpenseService.sendBatchT1OrderToPingAN(dealDate);
				if (fileDeal != null) {
					j.setMsg("发送" + dealDate + "的T1代付指令成功！");
				} else {
					j.setMsg("无匹配的T1数据！");
				}
			}
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	/**
	 * 发送T1代付指令
	 * 
	 * @param dealDate
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sendT1CmdFun")
	public Json sendT1CmdFun(String dealDate) {

		Json j = new Json();
		try {
			TpinganFileDeal fileDeal = pinganFileDealService.getFileDealByFileName(DateUtil.convertDateStrYYYYMMDD(dealDate));
			if (fileDeal != null) {
				TpinganFileDeal pd = pinganFileDealService.getPayFileDealByFileName(DateUtil.convertDateStrYYYYMMDD(dealDate));
				if (pd == null) {
					if (PinganFileDeal.file_status.file02_success.name().equals(fileDeal.getStatus()) || PinganFileDeal.file_status.file04_success.name().equals(fileDeal.getStatus())) {

						/* 发送代付指令给平安 */
						pingAnExpenseService.sendKHKF01ToPingan(fileDeal);
						j.setSuccess(true);
						j.setMsg("发送T1代付指令成功！");
					} else {
						j.setMsg("代付命令状态为" + fileDeal.getStatus() + ",不满足发送代付指令的条件，请检查！");
					}
				} else {
					j.setMsg("严重警告：代付指令已经上送，请不要重复下达代付指令");
				}
			} else {
				j.setMsg("代付文件没有查找到，请重新发送代付文件指令");
			}
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	/**
	 * 发送代付查询指令，成功后并处理T1的结果
	 * 
	 * @param dealDate
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/searchT1OrderResult")
	public Json searchT1OrderResult(String dealDate) {
		Json j = new Json();
		try {
			TpinganFileDeal pfd = pinganFileDealService.getPayFileDealByFileName(DateUtil.convertDateStrYYYYMMDD(dealDate));
			if (pfd != null
					&& (pfd.getStatus().equals(PinganFileDeal.file_status.KHKF01_success.name()) || pfd.getStatus().equals(PinganFileDeal.file_status.KHKF02_fail.name())
							|| pfd.getStatus().equals(PinganFileDeal.file_status.KHKF02_success.name()) || pfd.getStatus().equals(PinganFileDeal.file_status.file03_fail.name()))) {
				/* 发送T1代付文件 */
				pingAnExpenseService.sendKHKF02ToPingan(pfd);
				/* 下载成功，或者是下载成功后给了回馈表示 */
				if (PinganFileDeal.file_status.file03_success.name().equals(pfd.getStatus()) || PinganFileDeal.file_status.file04_success.name().equals(pfd.getStatus())) {
					/* 发送T1代付文件 */
					j.setSuccess(true);
					j.setMsg("T1代付已完成成功！" + pfd.getDescription());
				} else {
					j.setSuccess(false);
					j.setMsg(pfd.getDescription());
				}
			} else {
				j.setSuccess(false);
				j.setMsg("请确认已经发送过代付指令，在处理T1结果！");
			}
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	/**
	 * 发送代付查询指令，成功后并处理T1的结果
	 * 
	 * @param dealDate
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/dealT1OrderResult")
	public Json dealT1OrderResult(String dealDate) {
		Json j = new Json();
		try {
			TpinganFileDeal pfd = pinganFileDealService.getPayFileDealByFileName(DateUtil.convertDateStrYYYYMMDD(dealDate));
			if (pfd != null && (PinganFileDeal.file_status.file03_success.name().equals(pfd.getStatus()) || PinganFileDeal.file_status.file04_success.name().equals(pfd.getStatus()))) {
				String flag = pingAnExpenseService.dealBatchT1Result(pfd);
				if ("SUCCESS".equals(flag)) {
					/* 发送T1代付文件 */
					j.setSuccess(true);
					j.setMsg("T1代付已完成成功！");
				} else {
					j.setMsg(flag);
				}
			} else {
				j.setSuccess(false);
				j.setMsg(pfd.getDescription());
			}
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	/**
	 * 发送代付查询指令，成功后并处理T1的结果
	 * 
	 * @param dealDate
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/manualDownloadSts")
	public Json manualDownloadSts(String dealDate) {
		Json j = new Json();
		try {
			pingAnExpenseService.dealDownLoadStatement(DateUtil.convertDateStrYYYYMMDD(dealDate));
			/* 发送T1代付文件 */
			j.setSuccess(true);
			j.setMsg("下载对账单成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	/**
	 * 发送代付查询指令，成功后并处理T1的结果
	 * 
	 * @param dealDate
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/dealDownloadSts")
	public Json dealDownloadSts(String dealDate) {
		Json j = new Json();
		if (StringUtil.isNotBlank(dealDate)) {
			try {
				tradeStatementService.dealBatchPinganStatement(null, dealDate);
				/* 发送T1代付文件 */
				j.setSuccess(true);
				j.setMsg("对账单对账成功！");
			} catch (Exception e) {
				j.setMsg(e.getMessage());
			}
		} else {
			j.setSuccess(false);
			j.setMsg("请检查输入的日期格式！");
		}
		return j;
	}

	@RequestMapping("/exportZanshanfuExcel")
	@ResponseBody
	public ModelAndView exportZanshanfuExcel(HttpServletRequest request, String dealDate, HttpServletResponse response, HttpSession session) throws Exception {
		try {
			response.setCharacterEncoding("utf-8");
			response.setContentType("multipart/form-data");
			response.setHeader("Content-Disposition", "attachment;fileName=" + DateUtil.convertCurrentDateTimeToString() + ".xls");
			response.setHeader("Content-type", "charset=UTF-8");
			OutputStream ouputStream = response.getOutputStream();

			Workbook wb = zanshanfuExpenseService.exportT1Zanshanfu(dealDate);
			if (wb != null) {
				wb.write(ouputStream);
				ouputStream.flush();
				ouputStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("下载失败", e);
		}
		return null;
	}
}

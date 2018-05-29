package com.cn.flypay.controller.sys;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.fastjson.JSON;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.statement.OrderStatement;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.service.payment.ChannelPaymentService;
import com.cn.flypay.service.payment.PinganFileDealService;
import com.cn.flypay.service.statement.OrderStatementService;
import com.cn.flypay.service.statement.StatementService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;

/**
 * 对账日志
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping("/orderStatement")
public class OrderStatementController extends BaseController {

	private Log logger = LogFactory.getLog(getClass());
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private OrderStatementService orderStatementService;
	@Autowired
	private PinganFileDealService pinganFileDealService;
	@Autowired
	private UserService userService;
	@Autowired
	private StatementService tradeStatementService;

	@Autowired
	private ChannelPaymentService pinganPaymentService;
	@Value("${zanshanfu_root_path}")
	private String zanshanfu_root_path;
	@Value("${xinke_root_path}")
	private String xinke_root_path;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		List<Dictionary> statementStatusList = dictionaryService.combox("statementChannelType");
		request.setAttribute("orderType", JSON.toJSONString(statementStatusList));
		request.setAttribute("statementType", statementStatusList);
		return "/trans/orderStatement";
	}

	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(OrderStatement orderStatement, PageFilter ph) {
		Grid grid = new Grid();
		grid.setRows(orderStatementService.dataGrid(orderStatement, ph));
		grid.setTotal(orderStatementService.count(orderStatement, ph));
		return grid;
	}

	@ResponseBody
	@RequestMapping("/dealZanshanfuStatement")
	public Json dealZanshanfuStatement(String dealDate) {
		Json j = new Json();
		try {
			tradeStatementService.checkYLZXstatement(null, dealDate);
			j.setSuccess(true);
			j.setMsg("银联在线_攒善付通道对账完成！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping(value = "/fileUpload")
	public void fileUpload(HttpServletRequest request, HttpServletResponse response) throws IllegalStateException, IOException {
		long startTime = System.currentTimeMillis();
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
		if (multipartResolver.isMultipart(request)) {
			// 将request变成多部分request
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			String dealDate = request.getParameter("dealDate");
			String type = request.getParameter("type");
			if (StringUtil.isNotBlank(dealDate)) {
				Iterator iter = multiRequest.getFileNames();
				while (iter.hasNext()) {
					String item = iter.next().toString();
					// 一次遍历所有文件
					MultipartFile file = multiRequest.getFile(item);
					if (file != null) {
						try {
							if ("ZANSHANFU".equals(type)) {
								String path = zanshanfu_root_path + File.separator + file.getOriginalFilename();
								file.transferTo(new File(path));
								tradeStatementService.checkYLZXstatement(null, dealDate);
							} else if ("XINKE".equals(type)) {
								String path = xinke_root_path + File.separator + file.getOriginalFilename();
								file.transferTo(new File(path));
								tradeStatementService.dealXinkeStatement(xinke_root_path, dealDate);
							}
						} catch (Exception e) {
							logger.error(e);
						}
					}
				}
			} else {
				logger.info("对账日期不能为空");
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("运行时间：" + String.valueOf(endTime - startTime) + "ms");
		response.sendRedirect("manager");
	}

	/**
	 * 查询昨日订单数据
	 * @param dealDate
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/dealPinganPayStatement")
	public Json dealPinganPayStatement(String dealDate) {
		Json j = new Json();
		try {
			Date date = DateUtil.getBeforeDate(new Date(), 1);
			pinganPaymentService.dealDownloadStatement(DateUtil.convertDateStrYYYYMMDD(date));
			j.setSuccess(true);
			j.setMsg("平安支付对账完成！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 下载指定日期的对账单数据
	 * @param dealDate
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/dealPinganPayStatementByTime")
	public Json dealPinganPayStatementByTime(String dealDate) {
		Json j = new Json();
		try {
			pinganPaymentService.dealDownloadStatement(dealDate);
			j.setSuccess(true);
			j.setMsg("平安支付对账完成！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	
}

package com.cn.flypay.controller.trans;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.model.trans.TOffLineDrawOrder;
import com.cn.flypay.model.util.JSON;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.OffLineDrawOrder;
import com.cn.flypay.pageModel.trans.PayOrder;
import com.cn.flypay.service.trans.OfflineDrawOrderService;
import com.cn.flypay.service.trans.UserOrderService;

@Controller
@RequestMapping("/offlineDrawOrder")
public class OfflineDrawOrderController extends BaseController {
	private Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	private OfflineDrawOrderService offlineDrawOrderService;

	@Autowired
	private UserOrderService userOrderService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/trans/offlinedraworder";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(OffLineDrawOrder offlineOrder, PageFilter ph, HttpSession session) {
		Grid grid = new Grid();
		LOG.info("offlineOrder cond = {}", JSON.getDefault().toJSONString(offlineOrder));
		grid.setRows(offlineDrawOrderService.dataGrid(offlineOrder, ph));
		grid.setTotal(offlineDrawOrderService.count(offlineOrder, ph));
		return grid;
	}

	@RequestMapping("/exportExcel")
	@ResponseBody
	public void exportExcel(HttpServletRequest request, OffLineDrawOrder offlineOrder, HttpServletResponse response, HttpSession session) throws Exception {

		try {
			String fileName = offlineDrawOrderService.dealDownloadOrder();
			int idx = fileName.indexOf("5437");
			String simpleName = fileName.substring(idx);
			response.setCharacterEncoding("utf-8");
			response.setContentType("multipart/form-data");
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
			LOG.error("下载失败", e);
		}
	}

	@RequestMapping("/finishOrderFun")
	@ResponseBody
	public Json finishOrderFun(HttpServletRequest request, OffLineDrawOrder offlineOrder, HttpServletResponse response, HttpSession session) throws Exception {
		Json json = new Json();

		List<TOffLineDrawOrder> orders = offlineDrawOrderService.findDownedOrder(" select t from TOffLineDrawOrder t where t.status='3' ");

		for (TOffLineDrawOrder order : orders) {
			userOrderService.affirmDaiFuNotify(true, order.getOrderNo(), new PayOrder());
			Long orderId = offlineDrawOrderService.finishOrder(order.getOrderNo());
			LOG.info("orderId = {}", orderId);
		}

		LOG.info("finishOrderFun Size = {}", orders.size());
		json.setSuccess(true);
		json.setMsg("成功");
		json.setObj(orders.size());
		return json;
	}

	@RequestMapping("/freeze")
	@ResponseBody
	public Json freeze(OffLineDrawOrder offlineOrder, PageFilter ph, HttpSession session) {
		LOG.info("freeze = {}", JSON.getDefault().toJSONString(offlineOrder));
		Json json = new Json();
		int count = offlineDrawOrderService.freezeOrder(offlineOrder, ph);

		json.setSuccess(true);
		json.setObj(count);
		return json;
	}

	@RequestMapping("/unfreeze")
	@ResponseBody
	public Json unfreeze(OffLineDrawOrder offlineOrder, PageFilter ph, HttpSession session) {
		LOG.info("unfreeze = {}", JSON.getDefault().toJSONString(offlineOrder));
		Json json = new Json();
		int count = offlineDrawOrderService.unfreezeOrder(offlineOrder, ph);
		json.setSuccess(true);
		json.setObj(count);
		return json;
	}

	@RequestMapping("/payOfflineOrderError")
	@ResponseBody
	public Json payOfflineOrderError(OffLineDrawOrder offlineOrder, PageFilter ph, HttpSession session) {
		LOG.info("payOfflineOrderError = {}", JSON.getDefault().toJSONString(offlineOrder));
		Json json = new Json();
		int count = offlineDrawOrderService.updateOrder(offlineOrder, ph);
		json.setSuccess(true);
		json.setObj(count);
		return json;
	}

	@RequestMapping("/makeOrderDownload")
	@ResponseBody
	public Json makeOrderDownload(OffLineDrawOrder offlineOrder, PageFilter ph, HttpSession session) {
		LOG.info("makeOrderDownload = {}", JSON.getDefault().toJSONString(offlineOrder));
		Json json = new Json();
		int count = offlineDrawOrderService.updateOrder(offlineOrder, ph);
		json.setSuccess(true);
		json.setObj(count);
		return json;
	}
}

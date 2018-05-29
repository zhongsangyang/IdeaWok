package com.cn.flypay.service.trans.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.trans.TOffLineDrawOrder;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.OffLineDrawOrder;
import com.cn.flypay.service.trans.OfflineDrawOrderService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;

@Service
public class OfflineDrawOrderServiceImpl implements OfflineDrawOrderService {
	private Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Value("${offline_order_root_path}")
	private String offline_order_root_path;

	@Autowired
	private BaseDao<TOffLineDrawOrder> offlineDrawOrderDao;

	@Override
	public List<TOffLineDrawOrder> dataGrid(OffLineDrawOrder order, PageFilter pf) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TOffLineDrawOrder t ";
		List<TOffLineDrawOrder> orders = offlineDrawOrderDao.find(hql + whereHql(order, params) + " order by id desc ", params, pf.getPage(), pf.getRows());
		return orders;
	}

	@Override
	public Long count(OffLineDrawOrder order, PageFilter pf) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select count(t.id) from TOffLineDrawOrder t ";
		Long count = offlineDrawOrderDao.count(hql + whereHql(order, params), params);
		return count;
	}

	private String whereHql(OffLineDrawOrder order, Map<String, Object> params) {
		String hql = "";
		if (order != null) {
			hql += " where 1=1 ";
			try {
				if (StringUtil.isNotEmpty(order.getStatus())) {
					hql += " and t.status=:status ";
					params.put("status", order.getStatus());
				}
				if (StringUtil.isNotEmpty(order.getDrawSrc())) {
					hql += " and t.drawSrc=:drawSrc ";
					params.put("drawSrc", order.getDrawSrc());
				}
				if (StringUtil.isNotEmpty(order.getLoginName())) {
					hql += " and t.loginName =:loginName ";
					params.put("loginName", order.getLoginName());
				}
				if (StringUtil.isNotEmpty(order.getOrderNo())) {
					hql += " and t.orderNo =:orderNo ";
					params.put("orderNo", order.getOrderNo());
				}
				if (StringUtil.isNotEmpty(order.getAccountBankNo())) {
					hql += " and t.accountBankNo =:accountBankNo ";
					params.put("accountBankNo", order.getAccountBankNo());
				}
			} catch (Exception e) {
				LOG.error("whereHql is Error, ", e);
			}
		}
		return hql;
	}

	@Override
	public Long add(OffLineDrawOrder order) {
		TOffLineDrawOrder torder = new TOffLineDrawOrder();
		org.springframework.beans.BeanUtils.copyProperties(order, torder);
		Long id = (Long) offlineDrawOrderDao.save(torder);
		return id;
	}

	@Override
	public int updateBunch() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bunchTime", new Date());
		String hql = " update  TOffLineDrawOrder o set o.status='2',o.bunchTime=:bunchTime where o.status='0' ";
		int countNum = offlineDrawOrderDao.executeHql(hql, params);
		return countNum;
	}

	@Override
	public int updateOrder(OffLineDrawOrder offlineOrder, PageFilter ph) {
		TOffLineDrawOrder order = offlineDrawOrderDao.get(TOffLineDrawOrder.class, offlineOrder.getId());
		order.setStatus(offlineOrder.getStatus());
		offlineDrawOrderDao.saveOrUpdate(order);
		return 1;
	}

	@Override
	public int freezeOrder(OffLineDrawOrder offlineOrder, PageFilter ph) {
		int result = 0;
		TOffLineDrawOrder order = offlineDrawOrderDao.get(TOffLineDrawOrder.class, offlineOrder.getId());
		// TOffLineDrawOrder order = offlineDrawOrderDao.get(" select t from
		// TOffLineDrawOrder t where t.id=:id ");
		String status = order.getStatus();
		if ("0".equals(status) || "2".equals(status)) {// 0初始2打批
			order.setStatus(offlineOrder.getStatus());
			offlineDrawOrderDao.saveOrUpdate(order);
			result = 1;
		}
		return result;
	}

	@Override
	public int unfreezeOrder(OffLineDrawOrder offlineOrder, PageFilter ph) {
		int result = 0;
		TOffLineDrawOrder order = offlineDrawOrderDao.get(TOffLineDrawOrder.class, offlineOrder.getId());
		String status = order.getStatus();
		if ("9".equals(status)) {// 9冻结
			order.setStatus(offlineOrder.getStatus());
			offlineDrawOrderDao.saveOrUpdate(order);
			result = 1;
		}
		return result;
	}

	@Override
	public String dealDownloadOrder() {
		try {
			LOG.info("-------下载Transfer代付信息 start--------");
			String orderHql = " select t from TOffLineDrawOrder t where t.status='2' ";
			List<TOffLineDrawOrder> torders = offlineDrawOrderDao.find(orderHql);

			String totalAmtCountSql = "select SUM(payAmt),count(*) from trans_offline_draw_order t where t.status='2' ";
			List<Object[]> totalAmts = offlineDrawOrderDao.findBySql(totalAmtCountSql);
			BigDecimal totalAmt = (BigDecimal) totalAmts.get(0)[0];
			totalAmt = totalAmt == null ? BigDecimal.ZERO : totalAmt;
			BigInteger count = (BigInteger) totalAmts.get(0)[1];
			LOG.info("collection offline order totalAmt={},count={}", totalAmt, count);

			HSSFWorkbook wb = fillOrderContent(torders, totalAmt, count);
			// Date date = new Date();
			String dateStr = DateUtil.getyyyyMMddToString();
			String fullFileName = writeExcel(wb, "SHSL5437_" + dateStr + "_0001.xls");
			if (StringUtil.isNotEmpty(fullFileName)) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("downloadTime", new Date());
				int updateCount = offlineDrawOrderDao.executeHql(" update TOffLineDrawOrder t set t.status='3',t.downloadTime=:downloadTime where t.status='2' ", params);
				LOG.info("Update offline order download/collect count={}/{}", updateCount, count);
			}
			LOG.info("Excel File name={}", fullFileName);
			return fullFileName;
		} catch (Exception e) {
			LOG.error("-------下载账户信息 error:{}", e.getMessage());
			return null;
		}
	}

	@Override
	public List<TOffLineDrawOrder> findDownedOrder(String downloadHql) {
		List<TOffLineDrawOrder> orders = offlineDrawOrderDao.find(downloadHql);
		return orders;
	}

	@Override
	public Long finishOrder(String orderNo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderNo", orderNo);
		TOffLineDrawOrder offLineDrawOrder = offlineDrawOrderDao.get(" select t from TOffLineDrawOrder t where t.status='3' and t.orderNo=:orderNo ", params);
		offLineDrawOrder.setStatus(TOffLineDrawOrder.STATUS_SUCCESS);
		offLineDrawOrder.setFinihsTime(new Date());
		Long offOrderId = (Long) offlineDrawOrderDao.save(offLineDrawOrder);
		return offOrderId;
	}

	@Override
	public Long finishOrder() {
		Long count = (long) 0;
		count = (long) offlineDrawOrderDao.executeHql(" update TOffLineDrawOrder t set t.status='1' where t.status='3' ");
		return count;
	}

	@Override
	public Workbook dealDownloadOrder(String wbtype) {
		try {
			LOG.info("-------下载Transfer代付信息 start--------");
			String orderHql = " select t from TOffLineDrawOrder t where t.status='2' ";
			List<TOffLineDrawOrder> torders = offlineDrawOrderDao.find(orderHql);

			String totalAmtCountSql = "select SUM(payAmt),count(*) from trans_offline_draw_order t where t.status='2' ";
			List<Object[]> totalAmts = offlineDrawOrderDao.findBySql(totalAmtCountSql);
			BigDecimal totalAmt = (BigDecimal) totalAmts.get(0)[0];
			totalAmt = totalAmt == null ? BigDecimal.ZERO : totalAmt;
			BigInteger count = (BigInteger) totalAmts.get(0)[1];
			LOG.info("collection offline order totalAmt={},count={}", totalAmt, count);

			HSSFWorkbook wb = fillOrderContent(torders, totalAmt, count);
			String fullFileName = writeExcel(wb, "orders.xls");
			if (StringUtil.isNotEmpty(fullFileName)) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("downloadTime", new Date());
				int updateCount = offlineDrawOrderDao.executeHql(" update TOffLineDrawOrder t set t.status='3',t.downloadTime=:downloadTime where t.status='2' ", params);
				LOG.info("Update offline order download/collect count={}/{}", updateCount, count);
			}
			return wb;
		} catch (Exception e) {
			LOG.error("-------下载账户信息 error:{}", e.getMessage());
			return null;
		}
	}

	private String writeExcel(Workbook wb, String fileName) throws Exception {
		String dateDir = DateUtil.getyyyyMMddToString();
		String fileDir = offline_order_root_path + dateDir;
		File excelFileDir = new File(fileDir);
		if (excelFileDir.exists() == false) {
			excelFileDir.mkdirs();
		}
		String fullFileName = excelFileDir + "\\" + fileName;
		FileOutputStream fos = new FileOutputStream(fullFileName);
		wb.write(fos);
		fos.close();
		LOG.info("-------账户信息 写入成功-------");
		return fullFileName;
	}

	private HSSFWorkbook fillOrderContent(List<TOffLineDrawOrder> orders, BigDecimal totalAmt, BigInteger count) {
		int lineNo = 0;
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("代付");
		HSSFRow row0 = sheet.createRow(lineNo++);// 0
		HSSFCell cell00 = row0.createCell(0);
		HSSFCell cell01 = row0.createCell(1);
		HSSFCell cell02 = row0.createCell(2);
		HSSFCell cell03 = row0.createCell(3);
		cell00.setCellValue("批次号");
		cell01.setCellValue("付款日期");
		cell02.setCellValue("批总金额");
		cell03.setCellValue("批总笔数");

		HSSFRow row1 = sheet.createRow(lineNo++);// 1
		HSSFCell cell10 = row1.createCell(0);
		HSSFCell cell11 = row1.createCell(1);
		HSSFCell cell12 = row1.createCell(2);
		HSSFCell cell13 = row1.createCell(3);
		String dateStr = DateUtil.convertDateStrYYYYMMDD(new Date());
		cell10.setCellValue("SHSL5437-" + dateStr);
		cell11.setCellValue(dateStr);
		cell12.setCellValue(totalAmt.toString());
		cell13.setCellValue(count.toString());

		HSSFRow row2 = sheet.createRow(lineNo++);// 2
		HSSFCell cell20 = row2.createCell(0);
		HSSFCell cell21 = row2.createCell(1);
		HSSFCell cell22 = row2.createCell(2);
		HSSFCell cell23 = row2.createCell(3);
		HSSFCell cell24 = row2.createCell(4);
		HSSFCell cell25 = row2.createCell(5);
		HSSFCell cell26 = row2.createCell(6);
		HSSFCell cell27 = row2.createCell(7);
		HSSFCell cell28 = row2.createCell(8);
		HSSFCell cell29 = row2.createCell(9);
		HSSFCell cell210 = row2.createCell(10);
		HSSFCell cell211 = row2.createCell(11);
		HSSFCell cell212 = row2.createCell(12);
		HSSFCell cell213 = row2.createCell(13);

		cell20.setCellValue("商户流水号");
		cell21.setCellValue("收款方类型");
		cell22.setCellValue("账户性质");
		cell23.setCellValue("收款方姓名");
		cell24.setCellValue("开户银行名称");
		cell25.setCellValue("银行账号");
		cell26.setCellValue("银行所在省份");
		cell27.setCellValue("银行所在市");
		cell28.setCellValue("支行名称");
		cell29.setCellValue("金额");
		cell210.setCellValue("联行号");
		cell211.setCellValue("用途");
		cell212.setCellValue("备注");
		cell213.setCellValue("收款方手机号");

		HSSFRow row = null;
		if (orders != null) {
			for (TOffLineDrawOrder order : orders) {
				row = sheet.createRow(lineNo++);
				HSSFCell celli0 = row.createCell(0);
				HSSFCell celli1 = row.createCell(1);
				HSSFCell celli2 = row.createCell(2);
				HSSFCell celli3 = row.createCell(3);
				HSSFCell celli4 = row.createCell(4);
				HSSFCell celli5 = row.createCell(5);
				HSSFCell celli6 = row.createCell(6);
				HSSFCell celli7 = row.createCell(7);
				HSSFCell celli8 = row.createCell(8);
				HSSFCell celli9 = row.createCell(9);
				HSSFCell celli10 = row.createCell(10);
				HSSFCell celli11 = row.createCell(11);
				HSSFCell celli12 = row.createCell(12);
				HSSFCell celli13 = row.createCell(13);
				celli0.setCellValue(order.getMerFlowNo());
				celli1.setCellValue("个人");
				celli2.setCellValue("储蓄卡");
				celli3.setCellValue(order.getReceiverName());
				celli4.setCellValue(order.getOpenBankName());
				celli5.setCellValue(order.getAccountBankNo());
				celli6.setCellValue("");
				celli7.setCellValue("");
				celli8.setCellValue("");
				celli9.setCellValue(order.getPayAmt().toString());
				celli10.setCellValue("");
				celli11.setCellValue("");
				celli12.setCellValue("");
				celli13.setCellValue("");
			}
		}
		return wb;
	}
}

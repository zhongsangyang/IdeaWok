package com.cn.flypay.utils.channel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.flypay.pageModel.statement.ZanshanfuStatement;
import com.cn.flypay.pageModel.statement.ZanshanfuStatementDetail;
import com.cn.flypay.utils.DateUtil;

public class ZanshanfuUtil {
	private static Logger logger = LoggerFactory.getLogger(ZanshanfuUtil.class);

	public static void main(String[] args) throws Exception {
		ZanshanfuUtil.getZanshanfuStatementInfo("D:\\statement\\zanshanfu");
	}
	/**
	 * 获取攒善付对账单信息，并将数据备份到根目录的当天目录下
	 * 
	 * @return
	 * @throws Exception
	 */
	public static ZanshanfuStatement getZanshanfuStatementInfo(String filePath) throws Exception {
		File forder = new File(filePath);
		File bakForder = new File(filePath + File.separator + DateUtil.convertDateStrYYYYMMDD(new Date()));
		if (!bakForder.exists()) {
			bakForder.mkdirs();
		}
		File[] files = forder.listFiles();
		for (File f : files) {
			if (f.isFile()) {
				try {
					FileUtils.copyFileToDirectory(f, bakForder);
					POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(f));
					HSSFWorkbook wb = new HSSFWorkbook(fs);
					HSSFSheet sheet = wb.getSheetAt(0);
					int k = 0;
					ZanshanfuStatement zsf = new ZanshanfuStatement();
					for (Iterator iterator = sheet.rowIterator(); iterator.hasNext();) {
						HSSFRow row = (HSSFRow) iterator.next();
						if (k++ < 5) {
							continue;
						}
						if ("###列表结束###".equals(getCellFormatValue(row.getCell(0)))) {
							break;
						}
						ZanshanfuStatementDetail detail = new ZanshanfuStatementDetail();
						detail.setPayNo(getCellFormatValue(row.getCell(0)).trim());
						detail.setTranDateStr(getCellFormatValue(row.getCell(1)).trim());
						detail.setTranAmt(getCellFormatValue(row.getCell(2)).trim());
						detail.setRealAmt(getCellFormatValue(row.getCell(3)).trim());
						detail.setFeeRate(getCellFormatValue(row.getCell(4)).trim());
						detail.setFee(getCellFormatValue(row.getCell(5)).trim());
						detail.setBalance(getCellFormatValue(row.getCell(6)).trim());
						detail.setStatus(getCellFormatValue(row.getCell(7)).trim());
						detail.setTransChannel(getCellFormatValue(row.getCell(8)).trim());
						detail.setExtraInfo(getCellFormatValue(row.getCell(9)).trim());
						zsf.getDetails().add(detail);
					}
					// 共成功支付: [8笔, 共10.07元]
					HSSFRow h1 = sheet.getRow(k);
					// 用户组: [青铜], 费率: [0.6%], 扣费: [0.13], 扣除费率后: [9.94元]
					HSSFRow h2 = sheet.getRow(k + 1);
					HSSFRow h3 = sheet.getRow(k + 2);// 导出时间: [2016-08-17
														// 15:12:04]
					String f1 = getCellFormatValue(h1.getCell(0));
					Integer sucessNumber = Integer.parseInt(f1.substring(f1.indexOf("[") + 1, f1.indexOf(",") - 1));
					Double sucessAmt = Double.parseDouble(f1.substring(f1.lastIndexOf("共") + 1, f1.indexOf("元") - 1));
					String f2 = getCellFormatValue(h2.getCell(0));
					String groupName = f2.substring(f2.indexOf("用户组: [") + 6, f2.indexOf("]"));
					String feeRate = f2.substring(f2.indexOf("费率: [") + 5, f2.indexOf("], 扣费:") - 1);
					String fee = f2.substring(f2.indexOf("扣费: [") + 5, f2.indexOf("], 扣除费率后:"));
					String totalBalance = f2.substring(f2.indexOf("扣除费率后: [") + 8, f2.lastIndexOf("]") - 1);

					zsf.setTotalSuccessAmt(sucessAmt);
					zsf.setTotalSuccessNum(sucessNumber);
					zsf.setGroupName(groupName);
					zsf.setFeeRate(Double.parseDouble(feeRate));
					zsf.setFee(Double.parseDouble(fee));
					zsf.setTotalBalance(Double.parseDouble(totalBalance));
					f.delete();
					return zsf;
				} catch (IOException e) {
					logger.error("解析Excel文件出错", e);
					throw e;
				}
			}
		}
		return null;
	}

	/**
	 * 根据HSSFCell类型设置数据
	 * 
	 * @param cell
	 * @return
	 */
	public static String getCellFormatValue(HSSFCell cell) {
		String cellvalue = "";
		if (cell != null) {
			// 判断当前Cell的Type
			switch (cell.getCellType()) {
			// 如果当前Cell的Type为NUMERIC
			case HSSFCell.CELL_TYPE_NUMERIC:
			case HSSFCell.CELL_TYPE_FORMULA: {
				// 判断当前的cell是否为Date
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					// 如果是Date类型则，转化为Data格式

					// 方法1：这样子的data格式是带时分秒的：2011-10-12 0:00:00
					// cellvalue = cell.getDateCellValue().toLocaleString();

					// 方法2：这样子的data格式是不带带时分秒的：2011-10-12
					Date date = cell.getDateCellValue();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					cellvalue = sdf.format(date);

				}
				// 如果是纯数字
				else {
					// 取得当前Cell的数值
					cellvalue = String.valueOf(cell.getNumericCellValue());
				}
				break;
			}
			// 如果当前Cell的Type为STRIN
			case HSSFCell.CELL_TYPE_STRING:
				// 取得当前的Cell字符串
				cellvalue = cell.getRichStringCellValue().getString();
				break;
			// 默认的Cell值
			default:
				cellvalue = " ";
			}
		} else {
			cellvalue = "";
		}
		return cellvalue;

	}
}

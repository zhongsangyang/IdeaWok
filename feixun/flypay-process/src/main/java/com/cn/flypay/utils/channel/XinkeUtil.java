package com.cn.flypay.utils.channel;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.flypay.pageModel.statement.XinkeStatement;
import com.cn.flypay.pageModel.statement.XinkeStatementDetail;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;

public class XinkeUtil {
	private static Logger logger = LoggerFactory.getLogger(XinkeUtil.class);

	/**
	 * 获取欣客对账单信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public static XinkeStatement getXinkeStatement(String filePath) throws Exception {
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
					XinkeStatement ms = new XinkeStatement();
					Double totalAml = 0d;
					Long num = 0l;
					List<String> rows = FileUtils.readLines(f, "utf-8");
					if (rows != null && rows.size() > 0) {
						for (int i = 0; i < rows.size(); i++) {
							String row = rows.get(i);
							if (StringUtil.isNotBlank(row) && row.indexOf("|") != -1) {
								XinkeStatementDetail xsd = new XinkeStatementDetail();
								String[] cls = row.split("\\|");
								xsd.setOrderNum(cls[0]);
								xsd.setTradeType(cls[1]);
								xsd.setTradePlate(cls[2]);
								xsd.setTradeDate(cls[3]);
								xsd.setAmt(cls[4]);
								xsd.setOrgNum(cls[5]);
								xsd.setMerchantId(cls[6]);
								xsd.setStatus(cls[7]);
								xsd.setPhone(cls[8]);
								xsd.setSettlementDate(cls[9]);
								totalAml = totalAml + Double.parseDouble(cls[4]);
								ms.getDetails().add(xsd);
								num++;
							}
						}

					}
					ms.setTotalAmt(totalAml);
					ms.setTotalNum(num);
					f.deleteOnExit();
					return ms;
				} catch (IOException e) {
					logger.error("解析欣客对账文件出错", e);
					throw e;
				}
			}
		}
		return null;
	}

	public static void main(String[] args) throws IOException {
		try {
			XinkeStatement ms = getXinkeStatement("c:\\statement\\xinke");
			System.out.println(ms.getTotalNum());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
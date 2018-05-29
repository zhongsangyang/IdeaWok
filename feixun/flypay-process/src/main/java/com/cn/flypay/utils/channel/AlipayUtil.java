package com.cn.flypay.utils.channel;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.flypay.pageModel.statement.AlipayStatement;
import com.cn.flypay.pageModel.statement.AlipayStatementDetail;
import com.cn.flypay.utils.DateUtil;

public class AlipayUtil {
	
	private static Logger logger = LoggerFactory.getLogger(AlipayUtil.class);

	
	/**
	 * 获取支付宝对账单信息，并将数据备份到根目录的当天目录下
	 * 
	 * @return
	 * @throws Exception
	 */
	public static AlipayStatement getAlipayStatementInfo(String filePath) throws Exception {
		File forder = new File(filePath);
		File[] files = forder.listFiles();
		AlipayStatement alipyaStm = new AlipayStatement();
		try {
			for (File f : files) {
				if (f.isFile() && f.getName().contains("汇总")) {
					List<String> rows = FileUtils.readLines(f, "GBK");
					for (int i = 0; i < rows.size(); i++) {
						// 门店编号,门店名称,交易订单总笔数,退款订单总笔数,订单金额（元）,商家实收（元）,支付宝优惠（元）,商家优惠（元）,卡消费金额（元）,服务费（元）,分润（元）,实收净额（元）
						String row = rows.get(i);
						if (row.startsWith("合计")) {
							String[] cls = row.split(",");
							alipyaStm.setTotalNum(Long.parseLong(cls[2].trim()));
							alipyaStm.setTotalTradeAmt(Double.parseDouble(cls[4].trim()));
							alipyaStm.setTotalMerchantAmt(Double.parseDouble(cls[5].trim()));
							alipyaStm.setTotalFeeAmt(Double.parseDouble(cls[9].trim()));
							alipyaStm.setTotalRealAmt(Double.parseDouble(cls[11].trim()));
							if (alipyaStm.getTotalNum() == 0) {
								return alipyaStm;
							}
						}
					}
				}
			}
			for (File f : files) {
				if (f.isFile() && f.getName().contains("汇总")) {
					continue;
				}
				List<String> rows = FileUtils.readLines(f, "GBK");
				int k = 0;
				for (int i = 5; i < rows.size(); i++) {
					String row = rows.get(i);
					if (row.startsWith("#")) {
						break;
					}
					String[] cls = row.split(",");
					AlipayStatementDetail detail = new AlipayStatementDetail();
					detail.setAlipayNum(cls[0].trim());
					detail.setOrderNum(cls[1].trim());
					detail.setTranType(cls[2].trim());
					detail.setTranDate(DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", cls[5].trim()));
					detail.setFromUserName(cls[10].trim());
					detail.setOrderAmt(Double.parseDouble(cls[11].trim()));
					detail.setRealAmt(Double.parseDouble(cls[12].trim()));
					detail.setFee(Double.parseDouble(cls[22].trim()));
					alipyaStm.getDetails().add(detail);
				}
			}
		} catch (IOException e) {
			logger.error("解析Excel文件出错", e);
			throw e;
		}
		forder.renameTo(new File(forder + "_ok"));
		return alipyaStm;
	}

}

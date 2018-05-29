package com.cn.flypay.utils.channel;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.flypay.pageModel.statement.MinshengStatement;
import com.cn.flypay.pageModel.statement.MinshengStatementDetail;
import com.cn.flypay.utils.DateUtil;

public class MinshengUtil {
	private static Logger logger = LoggerFactory.getLogger(MinshengUtil.class);

	/**
	 * 获取微信对账单信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public static MinshengStatement getMinshengStatement(String filePath) throws Exception {
		File statementFile = new File(filePath);
		File forder = new File(statementFile.getParent() + File.separator + "sm_stm_"
				+ DateUtil.convertDateStrYYYYMMDD(new Date()));
		if (!forder.exists()) {
			forder.mkdirs();
		}
		if (statementFile.isFile()) {
			try {
				FileUtils.copyFileToDirectory(statementFile, forder);
				MinshengStatement ms = new MinshengStatement();

				Double totalAml = 0d;
				Double totalFee = 0d;
				Long num = 0l;
				List<String> rows = FileUtils.readLines(statementFile, "utf-8");
				for (String row : rows) {
					if (row.startsWith("#")) {
						continue;
					}
					MinshengStatementDetail msd = new MinshengStatementDetail();
					String[] cls = row.split("\\|");
					msd.setCooperator(cls[0]);
					msd.setMerchantCode(cls[1]);
					msd.setSmzfMsgId(cls[2]);
					msd.setReqMsgId(cls[3]);
					totalAml = totalAml + Double.parseDouble(cls[4]);
					msd.setAmount(Double.parseDouble(cls[4]));
					msd.setSettleDate(cls[5]);
					msd.setRespType(cls[6]);
					msd.setRespCode(cls[7]);
					msd.setRespMsg(cls[8]);
					msd.setTransactionType(cls[9]);
					msd.setOriReqMsgId(cls[10]);
					totalFee = totalFee + Double.parseDouble(cls[11]);
					msd.setFee(Double.parseDouble(cls[11]));
					ms.getDetails().add(msd);
					num++;
				}
				ms.setTotalAmt(totalAml);
				ms.setTotalFeeAmt(totalFee);
				ms.setTotalNum(num);
				statementFile.deleteOnExit();
				return ms;
			} catch (IOException e) {
				logger.error("解析民生对账文件出错", e);
				throw e;
			}
		}
		return null;
	}

	public static void main(String[] args) throws IOException {
		try {
			MinshengStatement ms = getMinshengStatement("D:\\statement\\minsheng\\20160921.txt");
			System.out.println(ms.getTotalNum());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
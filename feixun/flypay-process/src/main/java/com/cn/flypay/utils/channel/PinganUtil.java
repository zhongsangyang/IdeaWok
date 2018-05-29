package com.cn.flypay.utils.channel;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.flypay.pageModel.statement.PinganPayStatement;
import com.cn.flypay.pageModel.statement.PinganPayStatementDetail;
import com.cn.flypay.pageModel.statement.PinganStatement;
import com.cn.flypay.pageModel.statement.PinganStatementDetail;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.pingan.PingAnConfig;

public class PinganUtil {

	private static Logger logger = LoggerFactory.getLogger(PinganUtil.class);

	private static String spilte_char = "\\|::\\|";

	private static String pay_spilte_char = "\",\"";

	/**
	 * 获取平安对账单信息，并将数据备份到根目录的当天目录下
	 * 
	 * @return
	 * @throws Exception
	 */
	public static PinganStatement getPinganStatement(String filePath) throws Exception {

		PinganStatement pinganStatement = null;
		File file = new File(filePath);
		if (file.exists()) {
			pinganStatement = new PinganStatement();
			List<String> contents = FileUtils.readLines(file, PingAnConfig.encoding);
			String top = contents.get(0);
			String[] tps = top.split(spilte_char);
			pinganStatement.setTotalNum(Long.parseLong(tps[0]));
			pinganStatement.setTotalAmt(Double.parseDouble(tps[1]));
			pinganStatement.setTotalSuccessNum(Long.parseLong(tps[2]));
			pinganStatement.setTotalSuccessAmt(Double.parseDouble(tps[3]));
			pinganStatement.setTotalFailNum(Long.parseLong(tps[4]));
			pinganStatement.setTotalFailAmt(Double.parseDouble(tps[5]));
			for (int i = 1; i < contents.size(); i++) {
				String row = contents.get(i);
				if (StringUtil.isNotEmpty(row)) {
					String[] rs = row.split(spilte_char);
					PinganStatementDetail psd = new PinganStatementDetail();
					// 交易日期
					psd.setTransDate(rs[0]);
					// 交易时间
					psd.setTransTime(rs[1]);
					// 请算日期
					psd.setStmDate(rs[2]);
					// 订单号
					psd.setOrderNum(rs[3]);
					// 批次号
					psd.setBatchNum(rs[4]);
					// 收款借记卡/账号
					psd.setCardNo(rs[5]);
					// 金额
					psd.setAmt(Double.parseDouble(rs[6]));
					// 实收手续费
					psd.setFee(Double.parseDouble(rs[7]));
					// 记账日期
					psd.setAccDate(rs[8]);
					// 记账流水号
					psd.setAccNum(rs[9]);
					// 错误码
					psd.setErrorCode(rs[10]);
					// 错误消息
					psd.setErrorInfo(rs[11]);
					// 备注
					psd.setRemark(rs[12]);
					pinganStatement.getDetails().add(psd);
				}
			}
			return pinganStatement;
		}
		return null;

	}

	/**
	 * 获取平安对账单信息，并将数据备份到根目录的当天目录下
	 * 
	 * @return
	 * @throws Exception
	 */
	public static PinganPayStatement getPinganPayStatement(String filePath) throws Exception {

		File file = new File(filePath);
		if (file.exists()) {
			PinganPayStatement pps = new PinganPayStatement();
			List<String> contents = FileUtils.readLines(file, PingAnConfig.encoding);
			Long totalNum = 0l;
			Double totalTradeAmt = 0d;
			Double totalFeeAmt = 0d;
			for (int i = 1; i < contents.size(); i++) {
				String row = contents.get(i);
				if (StringUtil.isNotEmpty(row)) {
					String[] rs = row.split(pay_spilte_char);
					PinganPayStatementDetail psd = new PinganPayStatementDetail();
					psd.setWxAlipayNum(rs[1]);
					psd.setOrderNum(rs[2]);
					psd.setMerchName(rs[3]);
					psd.setCreateDate(rs[4]);
					psd.setPaymentDate(rs[5]);
					psd.setOriAmt(Double.parseDouble(rs[6]));

					psd.setPlatformFee(Double.parseDouble(rs[7]));
					psd.setBankFee(Double.parseDouble(rs[8]));
					psd.setSettleFee(Double.parseDouble(rs[9]));
					psd.setRealAmt(Double.parseDouble(rs[10].substring(0, rs[10].length() - 1)));
					pps.getDetails().add(psd);
					totalNum++;
					totalTradeAmt = totalTradeAmt + psd.getOriAmt();
				}
			}
			pps.setTotalAmt(totalTradeAmt);
			pps.setTotalNum(totalNum);
			pps.setTotalFeeAmt(totalFeeAmt);
			return pps;
		}
		return null;

	}

	public static void main(String[] args) {
		try {
			PinganPayStatement pinganStatement = getPinganPayStatement("D:\\平安移动支付平台支付宝20161028对账文件_20161028140554435.csv");
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

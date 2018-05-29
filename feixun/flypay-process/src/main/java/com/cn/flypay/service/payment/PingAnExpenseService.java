package com.cn.flypay.service.payment;

import java.util.Map;

import com.cn.flypay.model.trans.TpinganFileDeal;

public interface PingAnExpenseService {

	/**
	 * 代付同一订单
	 * 
	 * @param u
	 * @param money
	 * @return
	 * @throws Exception
	 */

	public Map<String, String> sendOrderToPingAN(String orderNum) throws Exception;

	public Map<String, String> sendSearchOrderToPingAN(Long orderId) throws Exception;
	
	public Map<String, String> sendSearchOrderToPingANLong(Long orderId) throws Exception;

	/**
	 * 1，系统组装T1代付文件，发送给平安
	 * 
	 * @param dateyyyyMMdd
	 * @throws Exception
	 */
	public TpinganFileDeal sendBatchT1OrderToPingAN(String dateyyyyMMdd) throws Exception;

	/**
	 * 2，系统发送给平安后，平安给出异步回执，告知已经成功接收
	 * 
	 * @param body
	 * @return
	 */
	public TpinganFileDeal dealFile04FeedBack(String body);

	/**
	 * 3，等待平安回馈了成功接收的通知后，系统发起批量代付指令
	 * 
	 * @param fileDeal
	 * @throws Exception
	 */
	public void sendKHKF01ToPingan(TpinganFileDeal fileDeal) throws Exception;

	/**
	 * 4、发起批量代付指令后，发起定时询问指令，咨询平安是否已经处理完成，若处理完成直接下载
	 * 
	 * @param fileDeal
	 * @throws Exception
	 */
	public void sendKHKF02ToPingan(TpinganFileDeal fileDeal) throws Exception;

	public String sendFile01ToPingAn(TpinganFileDeal fileDeal) throws Exception;

	public TpinganFileDeal sendFile02ToPingAn(TpinganFileDeal fileDeal) throws Exception;

	/**
	 * 文件下载
	 * 
	 * @param fileDeal
	 * @throws Exception
	 */
	public void sendFile03ToPingAn(TpinganFileDeal fileDeal) throws Exception;

	/**
	 * 平安成功通知到系统后，组装需要发送的file04的报文体
	 * 
	 * @param fileDeal
	 * @return
	 */
	public String generateFile04ReturnSuccessBody(TpinganFileDeal fileDeal);

	/**
	 * 处理平安回馈的T1回馈结果
	 * 
	 * @param fileDeal
	 */
	public String dealBatchT1Result(TpinganFileDeal fileDeal);

	public void dealDownLoadStatement(String date) throws Exception;

}

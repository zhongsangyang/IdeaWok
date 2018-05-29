package com.cn.flypay.service.statement;

import java.util.List;

import com.cn.flypay.model.trans.TransStatement;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.statement.ZanshanfuStatement;
import com.cn.flypay.pageModel.trans.Statement;

public interface StatementService {

	public List<Statement> dataGrid(Statement statement, PageFilter ph);

	public Long count(Statement statement, PageFilter ph);

	public void add(Statement statement);

	public void add(TransStatement statement);

	/**
	 * 银联在线_攒善付通道
	 * 
	 * @param dateyyyyMMdd
	 * @return
	 */
	ZanshanfuStatement checkYLZXstatement(String filePath, String dateyyyyMMdd);

	/**
	 * 平安提现接口对账
	 * 
	 * @param filePath
	 * @param dateyyyyMMdd
	 */
	String dealBatchPinganStatement(String filePath, String dateyyyyMMdd);

	/**
	 * 微信对账单接口
	 * 
	 * @param filePath
	 * @param dateyyyyMMdd
	 */
	void dealWeixinStatement(String filePath, String dateyyyyMMdd);

	/**
	 * 支付宝对账单接口
	 * 
	 * @param filePath
	 * @param dateyyyyMMdd
	 */
	void dealAlipayStatement(String filePath, String dateyyyyMMdd);

	/**
	 * 民生对账单接口
	 * 
	 * @param filePath
	 * @param dateyyyyMMdd
	 */
	void dealMinshengStatement(String filePath, String dateyyyyMMdd);

	/**
	 * 平安支付对账单
	 * 
	 * @param filePath
	 * @param dateyyyyMMdd
	 */
	void dealPinganStatement(String filePath, String dateyyyyMMdd);

	/**
	 * 欣客对账单
	 * 
	 * @param filePath
	 * @param dateyyyyMMdd
	 */
	void dealXinkeStatement(String filePath, String dateyyyyMMdd);

}

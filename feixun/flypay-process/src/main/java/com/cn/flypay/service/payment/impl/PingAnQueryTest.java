package com.cn.flypay.service.payment.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.XmlMapper;
import com.cn.flypay.utils.pingan.YQUtil;

/**
 * Created by zhoujifeng1 on 16/8/3.
 */
public class PingAnQueryTest {

	private static String bandAcc = "00901025000000065000";
	private static String AcctNo = "11015065535007";
	public static final int HEAD_LEN = 222;
	public static final String CHARSET_DEFAULT = "GBK";
	private static final String fmtTime = "yyyyMMddHHmmss";
	private static final int TIME_OUT = 30000; // 超时时间，单位为毫秒，默认5分钟

	public static void main(String[] args) throws UnknownHostException, IOException {
		try {

			String src = "";
			StringBuffer sb = new StringBuffer();
			sb.append("A00101");// 报文类别和目标系统编号
			/*
			 * 01：GBK缺省 02：UTF8 03：unicode 04：iso-8859-1 建议使用GBK编码
			 */
			sb.append("01");//
			/*
			 * 01:tcpip 缺省 02：http 03：webservice 现在只支持：TCPIP接入
			 */
			sb.append("01");
			/* 银行提供给企业的20位唯一的标识代码 */
			sb.append(bandAcc);// TODO
			/* 接收报文长度 10位 */
			sb.append("%s");// TODO
			/* 交易码 6位 */
			sb.append("KHKF04");// TODO
			sb.append("12345");
			/* 01- 请求 02- 应答 */
			sb.append("01");//

			sb.append("20120620");//
			sb.append("151810");
			sb.append("201206180000011037  ");
			/* 返回码 6位 */
			sb.append("000000");
			/* 返回描述 100位 */
			sb.append("0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
			/*
			 * 后续包标志 1位 0-结束包，1-还有后续包
			 * 同请求方流水号一起运作，用于大报文的拆分，请求方系统流水号要和第一次保持一致（未启用）
			 */
			sb.append("0");

			/*
			 * 如果有后续包请求（未启用） 第一次 000 第二次 001 第三次 002依此增加 请求方系统流水号要和第一次保持一致。
			 */
			sb.append("000");
			/*
			 * 0- 不签名 1- 签名 （填0，企业不管，由银行客户端完成）
			 */
			sb.append("0");
			/*
			 * 0- 裸签（填1，企业不管，由银行客户端完成） 1- PKCS7
			 */
			sb.append("0");
			/*
			 * 签名算法 12位
			 */
			sb.append(" RSA-SHA1   ");
			/*
			 * 签名数据长度 10位
			 */
			sb.append("0000000000");
			/*
			 * 附件数目
			 */
			sb.append("0");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("OrderNumber", "ZXLKF0320160326TV001");
			map.put("AcctNo", AcctNo);
			map.put("BussFlowNo", "8043431608135101023707");
			String t = XmlMapper.mapObject2Xml(map, "Result").replaceAll("\n", "").replaceAll("> <", "><");
			String s =YQUtil.asemblyPackets(bandAcc, "KHKF04", t);
			String length = StringUtil.leftPad(String.valueOf(t.getBytes("GBK").length), 10, "0");

			src = String.format(sb.toString(), length) + t;
			System.out.println(src);
			// src =
			// "A0010101010090102500000006500000000001074001  123450120160812212619YQTEST20160812212619                                                                                                          000001            00000000000<?xml version=\"1.0\" encoding=\"GBK\"?><Result><Account>0512100008226</Account><CcyCode>RMB</CcyCode></Result>";

			OutputStream out = null;
			InputStream in = null;
			Socket socket = null;
			try {
				socket = new Socket("127.0.0.1", 7070);
				socket.setSendBufferSize(4096);
				socket.setTcpNoDelay(true);
				socket.setSoTimeout(TIME_OUT);
				socket.setKeepAlive(true);
				out = socket.getOutputStream();
				in = socket.getInputStream();
				out.write(s.getBytes("GBK"));
				out.flush();

				// 读取报文头
				byte[] head = new byte[HEAD_LEN];
				int headTotal = 0;
				int len = 0;
				while (headTotal < HEAD_LEN) {
					len = in.read(head, headTotal, HEAD_LEN - headTotal);
					headTotal += len;
				}

				System.out.println(new String(head, "GBK"));
				// 读取报文体
				int bodyLen = Integer.parseInt(new String(head, 30, 10, CHARSET_DEFAULT));
				if (bodyLen > 0) {

					byte[] body = new byte[bodyLen];

					int bodyTotal = 0;
					len = 0;

					while (bodyTotal < bodyLen) {
						len = in.read(body, bodyTotal, bodyLen - bodyTotal);
						bodyTotal += len;
					}
					System.out.println(new String(body, "GBK"));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				try {
					if (out != null) {
						out.close();
					}
					if (in != null) {
						in.close();
					}
					if (socket != null) {
						socket.close();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

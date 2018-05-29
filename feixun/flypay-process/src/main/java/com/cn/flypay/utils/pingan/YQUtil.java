package com.cn.flypay.utils.pingan;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 银企直连实用函数
 * 
 * @author ZHANGXUELING871
 * @version 0.1
 * @since 2014-01-03
 */
public class YQUtil {

	public static final int HEAD_LEN = 222;
	public static final String CHARSET_DEFAULT = "GBK";
	private static final String fmtTime = "yyyyMMddHHmmss";
	private static final int TIME_OUT = 120000; // 超时时间，单位为毫秒，默认2分钟

	/**
	 * 组装报文 这里append比较多，是为了展现报文头的各个字段；实际使用中请按需减少
	 * 
	 * @param yqdm
	 *            20位银企代码
	 * @param bsnCode
	 *            交易代码
	 * @param xmlBody
	 *            xml主体报文
	 * @return
	 */
	public static String asemblyPackets(String yqdm, String bsnCode, String xmlBody) {

		Date now = Calendar.getInstance().getTime();

		StringBuilder buf = new StringBuilder();
		buf.append("A00101");
		buf.append("01");// GBK编码
		buf.append("01");// 通讯协议为TCP/IP
		buf.append(yqdm);// 银企代码
		try {
			buf.append(String.format("%010d", xmlBody.getBytes(CHARSET_DEFAULT).length));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buf.append(String.format("%-6s", bsnCode));// 交易码-左对齐
		buf.append("12345");// 操作员代码-用户可自定义
		buf.append("01");// 服务类型 01请求

		String fmtNow = new SimpleDateFormat(fmtTime).format(now);
		buf.append(fmtNow); // 请求日期时间

		String requestLogNo = "YQTEST" + fmtNow;
		buf.append(requestLogNo);// 请求方系统流水号

		buf.append(String.format("%6s", "")); // 返回码
		buf.append(String.format("%100s", ""));

		buf.append(0); // 后续包标志
		buf.append(String.format("%03d", 0));// 请求次数
		buf.append("0");// 签名标识 0不签
		buf.append("1");// 签名数据包格式
		buf.append(String.format("%12s", "")); // 签名算法
		buf.append(String.format("%010d", 0)); // 签名数据长度
		buf.append(0);// 附件数目
		buf.append(xmlBody);// 报文体

		return buf.toString();
	}

	/**
	 * 发送报文
	 * 
	 * @param serverIp
	 *            服务器IP地址（SCP地址）
	 * @param iPort
	 *            端口号
	 * @param packetsRQ
	 *            请求报文头
	 * @return
	 */
	public static Packets send2server(String serverIp, int iPort, String packetsRQ) {
		Packets packetsRP = new Packets();

		OutputStream out = null;
		InputStream in = null;
		Socket socket = null;
		try {
			socket = new Socket(serverIp, iPort);
			socket.setSendBufferSize(4096);
			socket.setTcpNoDelay(true);
			socket.setSoTimeout(TIME_OUT);
			socket.setKeepAlive(true);
			out = socket.getOutputStream();
			in = socket.getInputStream();
			out.write(packetsRQ.getBytes(CHARSET_DEFAULT));
			out.flush();

			// 读取报文头
			byte[] head = new byte[HEAD_LEN];
			int headTotal = 0;
			int len = 0;
			while (headTotal < HEAD_LEN) {
				len = in.read(head, headTotal, HEAD_LEN - headTotal);
				headTotal += len;
			}
			packetsRP.setHead(head);

			// 读取报文体
			int bodyLen = Integer.parseInt(new String(head, 30, 10, CHARSET_DEFAULT));
			if (bodyLen > 0) {
				packetsRP.setLen(bodyLen);

				byte[] body = new byte[bodyLen];

				int bodyTotal = 0;
				len = 0;

				while (bodyTotal < bodyLen) {
					len = in.read(body, bodyTotal, bodyLen - bodyTotal);
					bodyTotal += len;
				}
				packetsRP.setBody(body);
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

		return packetsRP;
	}

	public static Packets send2httpServer(String serverIp, int iPort, String packetsRQ) throws Exception {
		URL url = new URL("http://" + serverIp + ":" + iPort); // 127.0.0.1:7072

		HttpURLConnection http = (HttpURLConnection) url.openConnection();

		http.setConnectTimeout(60000);
		http.setReadTimeout(60000);
		http.setDoOutput(true);
		http.setDoInput(true);
		http.setAllowUserInteraction(false);
		http.setUseCaches(false);
		http.setRequestMethod("POST");
		http.setRequestProperty("content-type", "text/xml; charset=GBK");

		byte[] packets = packetsRQ.getBytes("GBK");
		http.setRequestProperty("Content-Length", String.valueOf(packets.length));
		http.setRequestProperty("Connection", "close");
		http.setRequestProperty("User-Agent", "sdb client");
		http.setRequestProperty("Accept", "text/xml");
		OutputStream out = http.getOutputStream();

		out.write(packets);
		out.flush();

		InputStream in = http.getInputStream();
		int code = http.getResponseCode();
		if (code != 200) {
			throw new Exception("找不到web服务");
		}

		Packets packetRep = new Packets();

		byte[] head = new byte[HEAD_LEN];
		int recvLen = 0;
		while (recvLen < HEAD_LEN) {
			recvLen = in.read(head, recvLen, HEAD_LEN - recvLen);
		}

		packetRep.setHead(head);

		int bodyLen = Integer.parseInt(new String(head, 30, 10, CHARSET_DEFAULT));
		;
		packetRep.setLen(bodyLen);

		if (bodyLen > 0) {
			byte[] body = new byte[bodyLen];
			recvLen = 0;
			while (recvLen < bodyLen) {
				recvLen = in.read(body, recvLen, bodyLen - recvLen);
			}
			packetRep.setBody(body);
		}

		return packetRep;
	}
}

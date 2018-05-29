package com.cn.flypay.utils.qrcode;

public class Qrcode implements java.io.Serializable {

	private static final long serialVersionUID = -3960626391618597L;
	private String url;
	private Long expire_seconds;
	private String ticket;
	private Long sceneId;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getExpire_seconds() {
		return expire_seconds;
	}

	public void setExpire_seconds(Long expire_seconds) {
		this.expire_seconds = expire_seconds;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public Long getSceneId() {
		return sceneId;
	}

	public void setSceneId(Long sceneId) {
		this.sceneId = sceneId;
	}
}
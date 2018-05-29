package com.cn.flypay.pageModel.sys;

import java.io.Serializable;
import java.util.Date;

/**
* @author  weiqy
* @createDate  2018年4月10日 
*/
public class Down implements Serializable{
	
	
	private String type;
	private String downTime;
	
	@Override
	public String toString() {
		return "Down [type=" + type + ", downTime=" + downTime + "]";
	}
	public String getDownTime() {
		return downTime;
	}
	public void setDownTime(String downTime) {
		this.downTime = downTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}


	
	

}

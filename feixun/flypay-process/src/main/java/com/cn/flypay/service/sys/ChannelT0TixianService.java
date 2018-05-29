package com.cn.flypay.service.sys;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.cn.flypay.model.sys.TchannelT0Tixian;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.ChannelT0Tixian;

public interface ChannelT0TixianService {

	public List<ChannelT0Tixian> dataGrid(ChannelT0Tixian param, PageFilter ph);

	public Long count(ChannelT0Tixian param, PageFilter ph);

	public void add(TchannelT0Tixian channelT0Tixian);

	public void add(ChannelT0Tixian channelT0Tixian);

	public void edit(ChannelT0Tixian param);

	public TchannelT0Tixian getTodoTchannelT0TixianByOrderNum(String orderNum);

	public String updateTchannel(TchannelT0Tixian t);

	public ChannelT0Tixian get(Long id);

	Workbook export(ChannelT0Tixian channelT0Tixian);
}

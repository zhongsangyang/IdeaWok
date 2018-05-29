package com.cn.flypay.service.sys.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TchannelT0Tixian;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.ChannelT0Tixian;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.ChannelT0TixianService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.ExcelUtil;
import com.cn.flypay.utils.StringUtil;

@Service
public class ChannelT0TixianServiceImpl implements ChannelT0TixianService {

	@Autowired
	private BaseDao<TchannelT0Tixian> channelT0TixianDao;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private DictionaryService dictionaryService;

	@Override
	public List<ChannelT0Tixian> dataGrid(ChannelT0Tixian param, PageFilter ph) {
		List<ChannelT0Tixian> ul = new ArrayList<ChannelT0Tixian>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TchannelT0Tixian t left join t.channel cl ";
		List<TchannelT0Tixian> l = new ArrayList<TchannelT0Tixian>();
		if (ph == null) {
			l = channelT0TixianDao.find(hql + whereHql(param, params),params);
		} else {
			l = channelT0TixianDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		}
		for (TchannelT0Tixian t : l) {
			ChannelT0Tixian u = new ChannelT0Tixian();
			BeanUtils.copyProperties(t, u);
			if (t.getChannel() != null) {
				u.setChannelDetailName(t.getChannel().getDetailName());
				u.setChannelName(t.getChannel().getName());
			}
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(ChannelT0Tixian param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TchannelT0Tixian t  left join t.channel cl  ";
		return channelT0TixianDao.count("select count(*) " + hql + whereHql(param, params), params);
	}

	private String whereHql(ChannelT0Tixian t0, Map<String, Object> params) {
		String hql = "";
		if (t0 != null) {
			hql += " where 1=1 ";
			if (t0.getStatus() != null) {
				hql += " and t.status = :status";
				params.put("status", t0.getStatus());
			}
			try {
				if (StringUtil.isNotBlank(t0.getCreateDatetimeStart())) {
					hql += " and t.createDate >= :createDatetimeStart";
					params.put("createDatetimeStart", DateUtil.getStartOfDay(DateUtil.convertStringToDate("yyyy-MM-dd", t0.getCreateDatetimeStart())));
				}
				if (StringUtil.isNotBlank(t0.getCreateDatetimeEnd())) {
					hql += " and t.createDate <= :createDatetimeEnd";
					params.put("createDatetimeEnd", DateUtil.getEndOfDay(DateUtil.convertStringToDate("yyyy-MM-dd", t0.getCreateDatetimeEnd())));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return hql;
	}

	private String orderHql(PageFilter ph) {
		String orderString = "";
		if ((ph.getSort() != null) && (ph.getOrder() != null)) {
			orderString = " order by t." + ph.getSort() + " " + ph.getOrder();
		}
		return orderString;
	}

	@Override
	public void add(TchannelT0Tixian channelT0Tixian) {
		channelT0TixianDao.save(channelT0Tixian);
	}

	@Override
	public void edit(ChannelT0Tixian infoList) {
		TchannelT0Tixian t = channelT0TixianDao.get(TchannelT0Tixian.class, infoList.getId());
		channelT0TixianDao.update(t);
	}

	@Override
	public void add(ChannelT0Tixian channelT0Tixian) {
		TchannelT0Tixian t = new TchannelT0Tixian();
		BeanUtils.copyProperties(channelT0Tixian, t);
		t.setChannel(channelService.getTchannelInCache(channelT0Tixian.getChannelId()));
		channelT0TixianDao.save(t);
	}

	@Override
	public TchannelT0Tixian getTodoTchannelT0TixianByOrderNum(String orderNum) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderNum", orderNum);
		String hql = "select t from TchannelT0Tixian t left join  t.channel where t.status=300 and t.orderNum=:orderNum";
		return channelT0TixianDao.get(hql, params);
	}

	@Override
	public String updateTchannel(TchannelT0Tixian t) {
		String updateFlag = GlobalConstant.SUCCESS;
		try {
			channelT0TixianDao.update(t);
		} catch (Exception e) {
			updateFlag = "FAILUER";
			e.printStackTrace();
		}
		return updateFlag;
	}

	@Override
	public ChannelT0Tixian get(Long id) {
		TchannelT0Tixian t = channelT0TixianDao.get("select t from TchannelT0Tixian t left join t.channel cl  where t.id=" + id);
		if (t != null) {
			ChannelT0Tixian ct = new ChannelT0Tixian();
			BeanUtils.copyProperties(t, ct);
			if (t.getChannel() != null) {
				ct.setChannelDetailName(t.getChannel().getDetailName());
				ct.setChannelName(t.getChannel().getName());
				ct.setChannelId(t.getChannel().getId());
			}
			return ct;
		}
		return null;
	}

	@Override
	public Workbook export(ChannelT0Tixian channelT0Tixian) {
		List<ChannelT0Tixian> t0tixians = dataGrid(channelT0Tixian, null);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String[] keys = new String[] { "channelName0", "channelDetailName1", "orderNum2", "amt3", "tixianFee4", "tradeFee5", "createTime6", "finishDate7", "status8" };
		String[] columnNames = new String[] { "渠道名称", "渠道详细", "订单号", "到账金额", "提现手续费", "提现交易费", "创建时间", "完成时间", "交易状态" };
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("sheetName", "用户列表");
		list.add(m);

		Map<String, String> statementTypes = dictionaryService.comboxMap("statementType");
		for (ChannelT0Tixian u : t0tixians) {
			Map<String, Object> contents = new HashMap<String, Object>();
			contents.put(keys[0], statementTypes.get(u.getChannelName()));
			contents.put(keys[1], u.getChannelDetailName());
			contents.put(keys[2], u.getOrderNum());
			contents.put(keys[3], u.getAmt());
			contents.put(keys[4], u.getDrawFee());
			contents.put(keys[5], u.getTradeFee());
			contents.put(keys[6], DateUtil.getStringFromDate(u.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
			contents.put(keys[7], DateUtil.getStringFromDate(u.getFinishDate(), "yyyy-MM-dd HH:mm:ss"));
			contents.put(keys[8], u.getStatus() == 100 ? "成功" : "");
			list.add(contents);
		}
		Workbook wb = ExcelUtil.createWorkBook(list, keys, columnNames);
		return wb;
	}
}

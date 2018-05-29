package com.cn.flypay.service.sys.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.model.sys.TchannelTodayAmtStatistics;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.ChannelDayAmt;
import com.cn.flypay.service.sys.ChannelDayAmtMonitorService;
import com.cn.flypay.utils.StringUtil;
/**
 * 通道每日录入金额统计
 * @author liangchao
 *
 */
@Service
public class ChannelDayAmtMonitorServiceImpl implements ChannelDayAmtMonitorService {
	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private BaseDao<ChannelDayAmt> channelDayAmtDao;
	@Autowired
	private BaseDao<TchannelTodayAmtStatistics> channelTodayAmtStatisticsDao;
	@Autowired
	private BaseDao<Tchannel> channelDao;
	
	/**
	 * 根据页面输入的条件，查询各通道每日录入金额数据
	 */
	@Override
	public List<ChannelDayAmt> dataGrid(ChannelDayAmt channelDayAmt,PageFilter pf) {
		log.info("ChannelDayAmtMonitorService -- dataGrid begin");
		//拼接查询HQL
		Map<String, Object> params = new HashMap<String, Object>();	
		String sql = "";
		//判断前台输入的查询条件
		//先判断时间
		String sql_time ="";
		if(StringUtil.isBlank(channelDayAmt.getCreateTimeStart()) && StringUtil.isBlank(channelDayAmt.getCreateTimeEnd())) {
			//未输入时间条件，默认查询今日
			sql_time+=" and DATE_FORMAT(create_time,'%Y-%m-%d') = DATE_FORMAT(now(),'%Y-%m-%d') ";
		}else{
			if(StringUtil.isNotBlank(channelDayAmt.getCreateTimeStart())){
				sql_time+=" and create_time >= :createTimeStart ";
				params.put("createTimeStart", channelDayAmt.getCreateTimeStart());
			}else if(StringUtil.isNotBlank(channelDayAmt.getCreateTimeEnd())){
				sql_time+=" and create_time <= :createTimeEnd ";
				params.put("createTimeEnd", channelDayAmt.getCreateTimeEnd());
			}
		}
		
		
		
		
		//拼接 查询
		if(channelDayAmt.getType()!=null && StringUtil.isNotBlank(channelDayAmt.getName())){
			sql = "select name,type,sum(today_amt) as sumTodayAmt from sys_channel_todayamt_statistics "
					+" where type = :type and name = :name " +sql_time+ " group by name "; //查询无结果，若不加group by ,会返回一列全部为null 的数据
			params.put("type", channelDayAmt.getType());
			params.put("name", channelDayAmt.getName());
		}else if(channelDayAmt.getType()!=null && StringUtil.isBlank(channelDayAmt.getName())){
			sql = "select name,type,sum(today_amt) as sumTodayAmt from sys_channel_todayamt_statistics "
					+ " where type = :type "+ sql_time +" group by name ";
			params.put("type", channelDayAmt.getType());
		}else if(channelDayAmt.getType()==null && StringUtil.isNotBlank(channelDayAmt.getName())){
			sql = "select name,type,sum(today_amt) as sumTodayAmt from sys_channel_todayamt_statistics "
					+" where name = :name "+ sql_time +" group by type ";
			params.put("name", channelDayAmt.getName());
		}else{
			//name,type都为空
			sql = "select name,sum(today_amt) as sumTodayAmt from sys_channel_todayamt_statistics "
					+ "where 1=1 "+ sql_time +" group by name ";
		}
		//拼接order by 
		sql+=orderHql(pf);
		Session session = channelDayAmtDao.getCurrentSession();
		SQLQuery q = session.createSQLQuery(sql);
		if ((params != null) && !params.isEmpty()) {
			setQueryParameters(params, q);
		}
		//下面的原生sql 需要泛型.class作为方法参数，无法放入basedaoimpl中，所以将dao层放在这里
		List<ChannelDayAmt> rows = q.setFirstResult((pf.getPage() - 1) * pf.getRows()).setMaxResults(pf.getRows()).setResultTransformer(Transformers.aliasToBean(ChannelDayAmt.class)).list();
		log.info("ChannelDayAmtMonitorService -- dataGrid end");
		return rows;
		
	}
	
	/**
	 * 拼接排序条件
	 * @param ph
	 * @return
	 */
	private String orderHql(PageFilter ph) {
		String orderString = "";
		if ((ph.getSort() != null) && (ph.getOrder() != null)) {
			orderString = " order by " + ph.getSort() + " " + ph.getOrder();
		}
		return orderString;
	}
	
	/**
	 * 拼接查询参数
	 * @param params
	 * @param q
	 */
	private void setQueryParameters(Map<String, Object> params, Query q) {
		for (String key : params.keySet()) {
			Object obj = params.get(key);
			// 这里考虑传入的参数是什么类型，不同类型使用的方法不同
			if (obj instanceof Collection<?>) {
				q.setParameterList(key, (Collection<?>) obj);
			} else if (obj instanceof Object[]) {
				q.setParameterList(key, (Object[]) obj);
			} else {
				q.setParameter(key, obj);
			}
		}
	}

	/**
	 * 定时统计每日通道流入金额
	 */
	@Override
	public void addCollectTodayAmtEveryDay() {
		log.info("每日统计通道流入金额--------开始--------- ");
		try {
			//判断今日是否已经重复运行
			String hql_1 = "select t from TchannelTodayAmtStatistics t where DATE_FORMAT(t.createTime,'%Y-%m-%d') =  DATE_FORMAT(now(),'%Y-%m-%d')  ";
			List<TchannelTodayAmtStatistics> result = channelTodayAmtStatisticsDao.find(hql_1);
			if(result.size() >0){
				log.info("每日统计通道流入金额--------数据已经统计过，当前为今日重复运行--------- ");
				return ;
			}
			//查询执行当日的通道统计金额结果集
			String hql_2 = " select t from Tchannel t where t.status=0 ";
			List<Tchannel>  channels = channelDao.find(hql_2);
			//遍历可用通道，提取当日流入金额数据
			for(Tchannel t : channels){
				TchannelTodayAmtStatistics ts = new TchannelTodayAmtStatistics();
				ts.setChannel_id(t.getId());
				ts.setName(t.getName());
				ts.setType(t.getType());
				ts.setToday_Amt(t.getTodayAmt());
				ts.setStatus(t.getStatus());
				ts.setCreateTime(new Date());
				channelTodayAmtStatisticsDao.save(ts);
			}
		} catch (Exception e) {
			log.error("每日统计通道流入金额--------出现异常--------- ",e);
		}
		
		log.info("每日统计通道流入金额--------结束---------");
	}
}

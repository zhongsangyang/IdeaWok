package com.cn.flypay.service.sys;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.SysInformationPhoto;
/**
 * 新闻资讯显示图片管理
 * @author liangchao
 *
 */
public interface SysInformationPhotoService {

	/**
	 * 查询存在的新闻资讯图片
	 * @param userId
	 * @param sys
	 * @param ph
	 * @return
	 */
	public List<SysInformationPhoto> dataGrid(SysInformationPhoto sys,PageFilter ph);
	
	
	/**
	 * 查询存在的新闻资讯图片
	 * @param sys
	 * @param ph
	 * @return
	 */
	public List<SysInformationPhoto> dataGridApp(String agentId);
	
	
	/**
	 * 查询存在的新闻资讯图片
	 * @param sys
	 * @param ph
	 * @return
	 */
	public JSONArray findList(String agentId, PageFilter pf);
	
	/**
	 * 查询存在的新闻资讯图片
	 * @param sys
	 * @param ph
	 * @return
	 */
	public JSONArray findListTwo(String agentId, PageFilter pf);
	
	public JSONArray findDeatil(Long id);
	
	
	
	/**
	 * 查询结果数量
	 * @param sys
	 * @param ph
	 * @return
	 */
	public Long count(SysInformationPhoto sys, PageFilter ph);
	
	/**
	 * 编辑新闻资讯图片数据库的数据记录
	 * @param sys
	 */
	public void deleteRecord(SysInformationPhoto sys);
	
	/**
	 * 向数据库中添加新闻资讯图片
	 * @param sys
	 * @throws Exception
	 */
	public JSONObject add(SysInformationPhoto sys,String agentId) throws Exception;
	
	/**
	 * 增加某个id对应的记录的点击量
	 * @param id
	 * @throws Exception
	 */
	public void addReadNum(Long id) throws Exception;
}

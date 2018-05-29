package com.cn.flypay.controller.sys;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.SysInformationPhoto;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.common.CommonService;
import com.cn.flypay.service.sys.SysInformationPhotoService;
import com.cn.flypay.service.sys.UserService;

/**
 * 新闻资讯显示图片管理
 * @author liangchao
 *
 */
@Controller
@RequestMapping("/newsPhoto")
public class NewsInfoPhotoController extends BaseController {
	
	private Log log = LogFactory.getLog(getClass());
	
	@Value("${upload_wen_root_path}")
	private String upload_wen_root_path;
	
	@Autowired
	private SysInformationPhotoService sysInformationPhotoService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private UserService userService;
	
	
	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/news/informationPhoto";
	}
	
	/**
	 * 查询存在的新闻资讯图片
	 * @param sysInformationPhoto
	 * @param ph
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(HttpSession session,SysInformationPhoto sys,PageFilter ph){
		Grid grid = new Grid();
		
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		Tuser user = userService.getTuser(sessionInfo.getId());
		sys.setAgentId(user.getAgentId());
		sys.setId(user.getId());
		grid.setRows(sysInformationPhotoService.dataGrid(sys, ph));
		grid.setTotal(sysInformationPhotoService.count(sys, ph));
		return grid;
	}
	
	/**
	 * 为段飞飞测试
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/NewsInforQuery")
	public JSONObject NewsInforQuery(SysInformationPhoto sys,PageFilter ph){
		
		JSONObject returnJson = new JSONObject();
		JSONArray ja = new JSONArray();
		for(SysInformationPhoto s : sysInformationPhotoService.dataGrid(sys, ph)){
			JSONObject job = new JSONObject(); 
			converOrderDetailToJson(job,s);
			ja.add(job);
		}
		
		
		returnJson.put("data", ja);
		returnJson.put("num", sysInformationPhotoService.count(sys, ph));
		log.info(returnJson.toJSONString());
		//进行加密
		//return returnJson;
		return commonService.getEncryptBody(returnJson);
	}
	
	/**
	 * 增加某个id对应的记录的点击量
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/addReadNum")
	public JSONObject addReadNum(Long id){
		JSONObject j = new JSONObject();
		try {
			sysInformationPhotoService.addReadNum(id);
			
			j.put("status", "000");
			j.put("message", "新增阅读量成功!");
		} catch (Exception e) {
			j.put("status", "999");
			j.put("message", "新增阅读量失败!" + e.getMessage());
			e.printStackTrace();
		}
		
		return commonService.getEncryptBody(j);
	}
	
	
	
	/**
	 * 将数据转化为json
	 * @param returnJson
	 * @param rows
	 */
	private void converOrderDetailToJson(JSONObject returnJson,SysInformationPhoto sys ){
		returnJson.put("id", sys.getId());
		returnJson.put("text1", sys.getText1());
		returnJson.put("text2", sys.getText2());
		returnJson.put("text3", sys.getText3());
		returnJson.put("text4", sys.getText4());
		returnJson.put("text5", sys.getText5());
		returnJson.put("text6", sys.getText6());
		returnJson.put("text7", sys.getText7());
		returnJson.put("text8", sys.getText8());
		returnJson.put("text9", sys.getText9());
		returnJson.put("photo1_url", sys.getPhoto1_url());
		returnJson.put("photo2_url", sys.getPhoto2_url());
		returnJson.put("photo3_url", sys.getPhoto3_url());
		returnJson.put("photo4_url", sys.getPhoto4_url());
		returnJson.put("photo5_url", sys.getPhoto5_url());
		returnJson.put("photo6_url", sys.getPhoto6_url());
		returnJson.put("photo7_url", sys.getPhoto7_url());
		returnJson.put("photo8_url", sys.getPhoto8_url());
		returnJson.put("photo9_url", sys.getPhoto9_url());
		returnJson.put("status", sys.getStauts());
		/*SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = sdf.format(sys.getCreateTime());
		returnJson.put("createTime", dateStr);*/
		returnJson.put("createTime", sys.getCreateTime());
		returnJson.put("readNumber", sys.getReadNumber());
		
		
	}
	
	
	
	
	@RequestMapping("/add")
	public String add(HttpServletRequest request,HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		Tuser user =  userService.getTuser(sessionInfo.getId());
		request.setAttribute("agentId", user.getAgentId());
		return "/news/informationAddPhoto";
	}
	
	/**
	 * 添加新闻资讯图片
	 * @param sys
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/addPhotos")
	public Json add(SysInformationPhoto sys,HttpSession session){
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			Tuser user = userService.getTuser(sessionInfo.getId());
			sysInformationPhotoService.add(sys,user.getAgentId());
			j.setSuccess(true);
			j.setMsg("添加成功!");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		
		return j;
	}
	
	/**
	 * 更改数据表记录
	 * @param sys
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Json deleteRecord(SysInformationPhoto sys){
		Json j = new Json();
		try {
			sysInformationPhotoService.deleteRecord(sys);
			j.setSuccess(true);
			j.setMsg("删除成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		
		return j;
	}
	
	
}

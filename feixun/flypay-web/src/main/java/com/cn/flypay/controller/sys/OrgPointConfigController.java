package com.cn.flypay.controller.sys;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.pageModel.sys.OrgPointConfig;
import com.cn.flypay.pageModel.trans.SysRateOperationHistory;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.OrgPointConfigService;
import com.cn.flypay.service.trans.SysRateOperationHistoryService;

@Controller
@RequestMapping("/orgPointConfig")
public class OrgPointConfigController extends BaseController {

	@Autowired
	private OrgPointConfigService orgPointConfigService;
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private SysRateOperationHistoryService sysRateOperationHistoryService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		//查询通道payType类别的所有参数  即支付类型的所有类别
		List<Dictionary> payTypeList = dictionaryService.combox("payType");
		request.setAttribute("payType",JSON.toJSONString(payTypeList));
		List<Dictionary> bigTranType = dictionaryService.combox("bigTranType");
		request.setAttribute("bigTranType",JSON.toJSONString(bigTranType));
		
		return "/admin/orgPointConfig";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(OrgPointConfig poc, PageFilter ph) {
		Grid grid = new Grid();
		grid.setRows(orgPointConfigService.dataGrid(poc, ph));
		grid.setTotal(orgPointConfigService.count(poc, ph));
		return grid;
	}

	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Long id) {
		List<Dictionary> payTypeList = dictionaryService.combox("payType");
		request.setAttribute("payTypeObj",payTypeList);
		List<Dictionary> bigTranType = dictionaryService.combox("bigTranType");
		request.setAttribute("bigTranTypeObj",bigTranType);
		OrgPointConfig pof = orgPointConfigService.get(id);
		request.setAttribute("orgPointConfig", pof);
		return "/admin/orgPointConfigEdit";
	}

	@RequestMapping("/edit")
	@ResponseBody
	public Json add(OrgPointConfig param, HttpServletRequest request) {
		SessionInfo o = (SessionInfo) request.getSession().getAttribute(GlobalConstant.SESSION_INFO);
		Json j = new Json();
		try {
			orgPointConfigService.edit(param);
			j.setSuccess(true);
			
			//开始保存记录
			
			SysRateOperationHistory req = new SysRateOperationHistory();
			req.setCreatorId(o.getId());
			req.setRecordType(3);	//1 用户费率编辑 2 T0最高提现限额配置  3 积分降费率配置
			req.setBehaviorType(1);	//1编辑 2设置
			
			//拼接被编辑对象的信息
			
			//运营商名称
			OrgPointConfig pof = orgPointConfigService.get(param.getId());  //orgPointConfig
			String name = pof.getOrgName();
			//支付类型
			List<Dictionary> payTypeList = dictionaryService.combox("payType");
			String payType = "";
			for(Dictionary d:payTypeList){
				if(pof.getPayType().toString().equals(d.getCode())){
					payType = d.getText();
				}
			}
			//入账类型
			String inputAccType = "";
			List<Dictionary> bigTranType = dictionaryService.combox("bigTranType");  //bigTranTypeObj
			for(Dictionary d : bigTranType){
				if(pof.getType().toString().equals(d.getCode())){	
					inputAccType = d.getText();
				}
			}
			
			String targetInfo = "运营商名称:" + name
						+",支付类型:" + payType
						+",入账类型:" + inputAccType;
			req.setTargetInfo(targetInfo);
			req.setTargetId(param.getId());
			req.setDetails("ID为" + o.getId() + "的用户，编辑"
					+ "org_point_config 表中ID="+ param.getId() +"的费率,"
					+"记录来源：管理平台--系统管理--积分降费率配置");
			
			sysRateOperationHistoryService.createHistory(req);
			
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

}

package com.cn.flypay.controller.mobile;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.service.common.CommonService;
import com.cn.flypay.service.common.MobileService;
import com.cn.flypay.service.sys.UserService;

@Controller
@Component
@RequestMapping("/mobileGgh")
public class MobileGghController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MobileService mobileService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private UserService userService;

	/**
	 * 2.3.1 服务商编号查询接口
	 * 
	 * @param param
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/app", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryAgentId(HttpServletRequest request) {
		logger.info("----获取服务商号 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("queryAgentId json={}", json.toJSONString());
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
			returnJson.put("agentId", mobileService.getAgentId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("----获取服务商号 end-----");

		return commonService.getEncryptBody(returnJson);
	}

}

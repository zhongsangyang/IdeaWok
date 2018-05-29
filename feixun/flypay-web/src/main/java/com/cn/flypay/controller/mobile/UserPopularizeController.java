package com.cn.flypay.controller.mobile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.sys.AppVersion;
import com.cn.flypay.pageModel.sys.SysMsgHistory;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.account.AccountService;
import com.cn.flypay.service.sys.AppVersionService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.MsgHistoryService;
import com.cn.flypay.service.sys.SysParamService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.trans.BrokerageService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.MD5Util;
import com.cn.flypay.utils.StringUtil;

@Controller
@RequestMapping("/popularize")
public class UserPopularizeController extends BaseController {

	private Log logger = LogFactory.getLog(getClass());
	@Autowired
	private UserService userService;
	@Autowired
	private BrokerageService brokerageService;
	@Autowired
	private MsgHistoryService msgHistoryService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private SysParamService sysParamService;

	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private AppVersionService appVersionService;

	/**
	 * 宝贝助手/下载页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/registpage", method = RequestMethod.GET)
	public ModelAndView registpage(HttpServletRequest request) {
		String pcode = request.getParameter("pcode");
		String lengthCode = pcode.substring(0, 6);
		request.setAttribute("parentCode", lengthCode);
		User user = userService.getByCode(lengthCode);
		if (StringUtil.isNotBlank(pcode)) {
			try {
				if (user != null && StringUtil.isNotBlank(user.getAgentId())) {
					request.setAttribute("agentId", StringUtil.getAgentId(user.getAgentId()));
				} else {
					logger.error("用户推广码属于任何运营商：推广码" + pcode);
				}
			} catch (Exception e) {
				logger.error("推广注册失败pcode=" + pcode, e);
			}
		}
		if(user.getAgentId().equals("F20160001")){
			ModelAndView mvbama = new ModelAndView("popularize/popularize");
			return mvbama;
		}else if(user.getAgentId().equals("F20160013")){
			ModelAndView mvbama = new ModelAndView("popularize/popularizeBaMa");
			return mvbama;
		}else if(user.getAgentId().equals("F20160017")){
			ModelAndView mvbama = new ModelAndView("popularize/Xiapopularize");
			return mvbama;
		}else{
			ModelAndView mv = new ModelAndView("popularize/popularizeOld");
			return mv;
		}
	}

	/**
	 * 发送短信验证码，并且将验证码信息记录在request.getSession()中。
	 * 
	 * @param request
	 * @param phone
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/phoneValidate", method = RequestMethod.POST)
	public String helpPhoneValidate(HttpSession session, HttpServletRequest request, @RequestParam String phone, @RequestParam String parentCode,
			@RequestParam String agentId) {
		JSONObject json = new JSONObject();
//		if (session.getAttribute("code") != null && StringUtil.isNotBlank(session.getAttribute("code").toString())) {
//			String pagecode = session.getAttribute("code").toString().toUpperCase();
//			if (StringUtil.isNotBlank(validateCode) && validateCode.trim().toUpperCase().equals(pagecode)) {
				if (StringUtil.isNotBlank(parentCode)) {
					User puser = userService.findUserByUserCodeOrPhone(parentCode, agentId);
					if (puser != null) {
						User user = userService.findUserByPhone(phone.toString(), puser.getAgentId());
						if (user == null) {
							try {
								SysMsgHistory msg = msgHistoryService.sendSmsToUserPhone(phone, StringUtil.getAgentId(puser.getAgentId()), 10);
								logger.info("短信内容" + msg.getContent());
								/* 发送成功后，将发送的信息放置到request.getSession()中 */
								if (msg.getId() != null) {
									Map<String, Object> codeMap = new HashMap<String, Object>();
									codeMap.put("code", msg.getValidateCode());
									codeMap.put("createTime", new Date());
									codeMap.put("timeout", 5);
									codeMap.put("phone", phone);
									request.getSession().setAttribute("phoneValidate", codeMap);
									json.put("status", "SUCCESS");
								} else {
									json.put("status", "FAILURE");
									json.put("errorInfo", msg.getContent());
								}
							} catch (Exception e) {
								json.put("status", "FAILURE");
								json.put("errorInfo", "短信发送失败，请稍后重试");
							}
						} else {
							json.put("status", "FAILURE");
							json.put("errorCode", "E001");
							json.put("errorInfo", "该手机号已注册");
						}
					} else {
						json.put("status", "FAILURE");
						json.put("errorCode", "E005");
						json.put("errorInfo", "推荐码已失效，请与推荐人联系");
					}
				} else {
					json.put("status", "FAILURE");
					json.put("errorCode", "E004");
					json.put("errorInfo", "无推荐人，不允许注册");
				}
//			} else {
//				json.put("status", "FAILURE");
//				json.put("errorCode", "E003");
//				json.put("errorInfo", "输入的验证码不正确，请您重试");
//			}
//		} else {
//			json.put("status", "FAILURE");
//			json.put("errorCode", "E002");
//			json.put("errorInfo", "请重新进入该页面");
//		}

		return json.toString();
	}

	/**
	 * 宝贝助手/下载页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/downloadpage")
	public ModelAndView downloadpage(HttpServletRequest request, String agentId) {
		ModelAndView mv = new ModelAndView("popularize/download");
		AppVersion androidVersion = appVersionService.getNewestAppVersion("android", agentId);
		if(!agentId.equals("F20160011")){
			AppVersion iosVersion = appVersionService.getNewestAppVersion("ios", agentId);
			request.setAttribute("ios", iosVersion.getDownloadNet());
		}
		request.setAttribute("android", androidVersion.getDownloadNet());
		return mv;
	}
	
	@RequestMapping(value = "/payFinish")
	public ModelAndView payFinish(HttpServletRequest request, String agentId) {
		ModelAndView mv = new ModelAndView("popularize/payFinish");
		return mv;
	}

	/**
	 * 宝贝助手/注册下载/提交注册信息
	 * 
	 * @param request
	 * @param openId
	 * @param phone
	 * @param checkCode
	 * @param password
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/regist", method = RequestMethod.POST)
	public String regist(HttpServletRequest request, @RequestParam String parentCode, @RequestParam String phone, @RequestParam String checkCode, @RequestParam String password,
			@RequestParam String agentId) {

		JSONObject json = new JSONObject();
		json.put("status", "FAILURE");
		logger.info("用户注册： phone=" + phone + "  parentCode=" + parentCode);
		// 已发送的短信验证信息
		@SuppressWarnings("unchecked")
		Map<String, Object> codeMap = (Map<String, Object>) request.getSession().getAttribute("phoneValidate");
		if (codeMap != null) {
			// 注册的手机号/密码/短信验证码 必须存在
			if (StringUtil.isNotEmpty(phone) && StringUtil.isNotEmpty(password) && StringUtil.isNotEmpty(checkCode)) {
				// 接收验证码的手机号
				String orgPhone = (String) codeMap.get("phone");
				if (StringUtil.isNotEmpty(orgPhone) && orgPhone.equals(phone)) {
					Date createTime = (Date) codeMap.get("createTime");// 创建验证码时间
					Integer timeout = (Integer) codeMap.get("timeout");// 短信验证码超时时间
					// 验证 当前验证码是否在有效时间范围内
					if (DateUtil.getBetweenMins(createTime, new Date()) <= timeout) {
						String orgCode = (String) codeMap.get("code");
						// 验证短信验证码的正确性
						if (StringUtil.isNotEmpty(orgCode) && orgCode.equals(checkCode)) {
							json.put("status", "SUCCESS");
							logger.info("phone=:" + phone);
							// 组装注册请求 发送给APP注册用户
							try {
								if (StringUtil.isNotBlank(parentCode)) {
									User puser = userService.findUserByUserCodeOrPhone(parentCode, StringUtil.getAgentId(agentId));
									if (puser == null) {
										json.put("code", "108");
										return json.toString();
									} else {
										User user = userService.findUserByPhone(phone.toString(), agentId);
										if (user == null) {
											userService.addUser(phone, MD5Util.md5(password), parentCode, agentId);
											json.put("code", "100");
										} else {
											json.put("code", "107");
										}
									}
								} else {
									json.put("code", "109");
								}
							} catch (Exception e) {
								logger.error(e);
								/* 公共号与APP 通信异常 */
								json.put("code", "106");
							}
						} else {
							/* 短信验证码失败 */
							json.put("code", "104");
						}
					} else {
						/* 短信验证码超时 */
						json.put("code", "103");
					}
				} else {
					/* 手机号码与发送验证码手机号码不一致 */
					json.put("code", "105");
				}
			} else {
				/* 确认必填字段都存在 */
				json.put("code", "102");
			}
		} else {
			/* 请重新获取验证码 */
			json.put("code", "101");
		}
		logger.info(json.toString());
		return json.toString();
	}

	/**
	 * 宝贝助手/注册下载/下载软件
	 * 
	 * @param request
	 * @param fileType
	 *            1表示android，2 表示 IOS
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public void helpDownload(HttpServletRequest request, String fileType, HttpServletResponse response) {
		String fileName = "";
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
		response.setCharacterEncoding("utf-8");
		InputStream inputStream = null;
		OutputStream os = null;
		try {
			String realPath = request.getSession().getServletContext().getRealPath("/");
			File file = new File(realPath + "/downapp/" + fileName);
			response.addHeader("Content-Length", "" + file.length());
			inputStream = new FileInputStream(file);
			os = response.getOutputStream();
			byte[] b = new byte[10240];
			int length;
			while ((length = inputStream.read(b)) > 0) {
				os.write(b, 0, length);
			}
			inputStream.close();
			response.flushBuffer();
			os.close();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}
	}
}

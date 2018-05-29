package com.cn.flypay.controller.sys;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.Channel;
import com.cn.flypay.pageModel.sys.SysInformation;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.sys.NewsInforMationService;
import com.cn.flypay.service.sys.ResourceServiceI;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.DateUtil;







@Controller
@RequestMapping("/news")
public class NewsInforMationController extends BaseController {

	private Log log = LogFactory.getLog(getClass());
	@Value("${upload_wen_root_path}")
	private String upload_wen_root_path;
	
	@Autowired
	private NewsInforMationService newsInforMationService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ResourceServiceI resourceService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/news/information";
	}
	
	
	@RequestMapping("/add")
	public String add(HttpServletRequest request) {
		return "/news/ueditor";
	}
	
	
	@RequestMapping("/addSub")
	public String addSub(SysInformation sys,HttpServletRequest request,MultipartFile file, HttpSession session) throws IOException {
		String editorValue = request.getParameter("editorValue");
		String folder = "imgs";
		String fileName = UUID.randomUUID().toString().replaceAll("-", "")+".png";
		String filePath = upload_wen_root_path + File.separator + folder + File.separator + fileName;
		if(null != file && file.getSize()!=0){
			File fold = new File(upload_wen_root_path + File.separator + folder);
			if (!fold.exists()) {
				fold.mkdirs();
			}
			FileUtils.writeByteArrayToFile(new File(filePath), file.getBytes());
		 }
		SessionInfo sessionInfo = (SessionInfo)session.getAttribute(GlobalConstant.SESSION_INFO);
		User user = userService.get(sessionInfo.getId());
		sys.setContext(editorValue);
		sys.setImgePath(fileName);
		newsInforMationService.add(sys,user);
		request.setAttribute("content",editorValue);
		return "/news/ueditordeatil";
	}


	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(SysInformation Sys, PageFilter ph, HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		User user = userService.get(sessionInfo.getId());
		Sys.setOrganizationId(user.getOrganizationId());
		Grid grid = new Grid();
		grid.setRows(newsInforMationService.dataGrid(Sys, ph));
		grid.setTotal(newsInforMationService.count(Sys, ph));
		return grid;
	}
	
	@RequestMapping("/getImg")
	@ResponseBody
	public void getImg(HttpServletRequest request,HttpServletResponse response) throws IOException {
		try {
			String sb = request.getParameter("imgName");
			sb = sb.substring(37, sb.length());
			String filePath = upload_wen_root_path + File.separator + "Btext" + File.separator + sb;
			byte data[] = readFile(filePath);
			response.setContentType("image/jpg"); 
			OutputStream os = response.getOutputStream();
			os.write(data);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping("/getImgTW")
	@ResponseBody
	public void getImgTW(HttpServletRequest request,HttpServletResponse response) throws IOException {
		try {
			String sb = request.getParameter("imgName");
			String filePath = upload_wen_root_path + File.separator + "imgs" + File.separator + sb;
			byte data[] = readFile(filePath);
			response.setContentType("image/jpg"); 
			OutputStream os = response.getOutputStream();
			os.write(data);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping("/getImgTHERE")
	@ResponseBody
	public void getImgTHERE(HttpServletRequest request,HttpServletResponse response) throws IOException {
		try {
			String sb = request.getParameter("imgName");
			String filePath = upload_wen_root_path + File.separator + "news_photos" + File.separator + sb;
			byte data[] = readFile(filePath);
			response.setContentType("image/jpg"); 
			OutputStream os = response.getOutputStream();
			os.write(data);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@ResponseBody
	@RequestMapping("/details")
	public Json details(User user, HttpSession session) {
		Json j = new Json();
		User sysuser = userService.getMemntUser(user);
		SessionInfo sessionInfo = new SessionInfo();
		sessionInfo.setId(sysuser.getId());
		sessionInfo.setLoginname(sysuser.getLoginName());
		sessionInfo.setName(sysuser.getName());
		sessionInfo.setResourceList(userService.listResource(sysuser.getId()));
		sessionInfo.setResourceAllList(resourceService.listAllResource());
		session.setAttribute(GlobalConstant.SESSION_INFO, sessionInfo);
		return j;
	}
	
	@RequestMapping("/detailsText")
	public String detailsText(HttpServletRequest request,String inforId) throws IOException {
		SysInformation sys = newsInforMationService.getSysInfor(inforId);
		request.setAttribute("content",sys.getContext());
		return "/news/detailsText";
	}
	
	
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Json deleteChannel(SysInformation Sys) {
		Json j = new Json();
		try {
			newsInforMationService.edit(Sys);
			j.setSuccess(true);
			j.setMsg("删除成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	
	
	public static byte[] readFile(String filename) throws IOException {
		if (filename == null || filename.equals("")) {
			throw new NullPointerException("无效的文件路径");
		}
		File file = new File(filename);
		long len = file.length();
		byte[] bytes = new byte[(int) len];

		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		int r = bufferedInputStream.read(bytes);
		if (r != len)
			throw new IOException("读取文件不正确");
		bufferedInputStream.close();
		return bytes;
	}

	
}

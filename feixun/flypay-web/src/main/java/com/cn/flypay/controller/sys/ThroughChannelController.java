package com.cn.flypay.controller.sys;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSON;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.Channel;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.service.payment.RouteService;
import com.cn.flypay.service.payment.WeiLianBaoYinLainService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.ThroughChannelService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.task.UserTaskService;

@Controller
@RequestMapping("/throughchannel")
public class ThroughChannelController extends BaseController {

	// private Log log = LogFactory.getLog(getClass());
	private Logger LOG = LoggerFactory.getLogger(ThroughChannelController.class);
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private UserService userService;
	@Autowired
	private ThroughChannelService throughChannelService;
	@Autowired
	private RouteService routeService;
	@Autowired
	private UserTaskService userTaskService;
	

	@Value("${minsheng_path}")
	private String minsheng_path;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		List<Dictionary> statementStatusList = dictionaryService.combox("transType");
		request.setAttribute("transType", JSON.toJSONString(statementStatusList));
		request.setAttribute("transTypeObj", statementStatusList);

		List<Dictionary> userTypeList = dictionaryService.combox("userType");
		request.setAttribute("userTypeObj", userTypeList);

		List<Dictionary> statementTypeList = dictionaryService.combox("statementType");
		request.setAttribute("statementType", JSON.toJSONString(statementTypeList));

		List<Dictionary> channelUserTypeList = dictionaryService.combox("channelUserType");
		request.setAttribute("channelUserType", JSON.toJSONString(channelUserTypeList));
		return "/admin/througchannel";
	}

	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(HttpServletRequest request, Channel channel, PageFilter ph) {
		Grid grid = new Grid();
		channel.setChannelType(2);
		grid.setRows(throughChannelService.dataGrid(channel, ph));
		grid.setTotal(throughChannelService.count(channel, ph));
		return grid;
	}

	@RequestMapping("/addchannel")
	public String addchannel(HttpServletRequest request) {
		return "/admin/addchannel";
	}

	@RequestMapping("/fileUpload")
	public String fileUpload(@RequestParam(value = "fileName") MultipartFile file, String type, String typeChannel,
			String perType) throws Exception {
		String path = minsheng_path + File.separator + file.getOriginalFilename();
		file.transferTo(new File(path));
		List<String> lines = FileUtils.readLines(new File(path), "GBK");
		for (String line : lines) {
			String[] acc = line.split("\\|");
			LOG.info("merch fileUpload typeChannel={}, line={}", new Object[] { typeChannel, line });
			if (typeChannel.equals("1")) {
				throughChannelService.addchannel(acc[0], acc[1], type, acc[2], acc[3], acc[4], acc[5]);
			} else {
				throughChannelService.addPingAnchannel(acc[0], acc[1], type, perType, acc[2], acc[3], acc[4], acc[5]);
			}
		}
		return "/admin/addchannelPre";
	}

}

package com.cn.flypay.service.sys.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TsysInformationPhoto;
import com.cn.flypay.model.util.StringUtil;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.SysInformationPhoto;
import com.cn.flypay.service.sys.SysInformationPhotoService;
import com.cn.flypay.utils.DateUtil;

/**
 * 新闻资讯显示图片管理
 * 
 * @author liangchao
 *
 */
@Service
public class SysInformationPhotoServiceImpl implements SysInformationPhotoService {
	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private BaseDao<TsysInformationPhoto> sysInformationPhotoDao;

	@Value("${upload_wen_root_path}")
	private String upload_wen_root_path;

	@Value("${get_wen_root_path}")
	private String get_wen_root_path;

	/**
	 * 查询存在的新闻资讯图片
	 */
	@Override
	public List<SysInformationPhoto> dataGrid(SysInformationPhoto sys, PageFilter ph) {
		List<SysInformationPhoto> result = new ArrayList<SysInformationPhoto>();
		ph.setSort("createTime");
		ph.setOrder("desc");

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			String hql = "select t from TsysInformationPhoto t  ";
			List<TsysInformationPhoto> s = sysInformationPhotoDao.find(hql + whereHql(sys, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
			for (TsysInformationPhoto t : s) {
				SysInformationPhoto p = new SysInformationPhoto();
				BeanUtils.copyProperties(t, p);
				result.add(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 查询结果数量
	 */
	@Override
	public Long count(SysInformationPhoto sys, PageFilter ph) {
		Long h = null;

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			String hql = "from TsysInformationPhoto t";
			h = sysInformationPhotoDao.count("select count(t.id) " + hql + whereHql(sys, params), params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return h;
	}

	/**
	 * 向数据库中添加新闻资讯图片
	 */
	@Override
	public JSONObject add(SysInformationPhoto sys, String agentId) throws Exception {
		JSONObject json = new JSONObject();
		TsysInformationPhoto t = new TsysInformationPhoto();
		BeanUtils.copyProperties(sys, t);
		if (sys.getPhoto1() != null && sys.getPhoto1().getSize() != 0) {
			t.setPhoto1_url(uploadImage(sys.getPhoto1()));
		}
		if (sys.getPhoto2() != null && sys.getPhoto2().getSize() != 0) {
			t.setPhoto2_url(uploadImage(sys.getPhoto2()));
		}
		if (sys.getPhoto3() != null && sys.getPhoto3().getSize() != 0) {
			t.setPhoto3_url(uploadImage(sys.getPhoto3()));
		}
		if (sys.getPhoto4() != null && sys.getPhoto4().getSize() != 0) {
			t.setPhoto4_url(uploadImage(sys.getPhoto4()));
		}
		if (sys.getPhoto5() != null && sys.getPhoto5().getSize() != 0) {
			t.setPhoto5_url(uploadImage(sys.getPhoto5()));
		}
		if (sys.getPhoto6() != null && sys.getPhoto6().getSize() != 0) {
			t.setPhoto6_url(uploadImage(sys.getPhoto6()));
		}
		if (sys.getPhoto7() != null && sys.getPhoto7().getSize() != 0) {
			t.setPhoto7_url(uploadImage(sys.getPhoto7()));
		}
		if (sys.getPhoto8() != null && sys.getPhoto8().getSize() != 0) {
			t.setPhoto8_url(uploadImage(sys.getPhoto8()));
		}
		if (sys.getPhoto9() != null && sys.getPhoto9().getSize() != 0) {
			t.setPhoto9_url(uploadImage(sys.getPhoto9()));
		}

		t.setStatus(0);
		t.setReadNumber(0L);
		t.setAgentId(agentId);
		t.setType(sys.getType());
		sysInformationPhotoDao.save(t);
		return json;
	}

	/**
	 * 上传图片，返回存放路径
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private String uploadImage(MultipartFile file) throws IOException {
		log.info("upload image " + file.getName() + " begin");
		// 拼接图片目标传送到达的绝对位置
		String folder = "news_photos";
		String fileName = UUID.randomUUID().toString().replaceAll("-", "") + ".png";
		String filePath = upload_wen_root_path + File.separator + folder + File.separator + fileName;
		if (file != null && file.getSize() != 0) {
			// 检查目标文件所在文件夹是否存在
			File fold = new File(upload_wen_root_path + File.separator + folder);
			if (!fold.exists()) {
				fold.mkdirs();
			}
			// 开始上传图片
			FileUtils.writeByteArrayToFile(new File(filePath), file.getBytes());
			log.info("upload image " + file.getName() + " to " + filePath + " over");
			return fileName;
		}
		return null;
	}

	/**
	 * 拼接查询where语句
	 * 
	 * @param sys
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private String whereHql(SysInformationPhoto sys, Map<String, Object> params) throws Exception {
		String hql = "";
		if (sys != null) {
			hql += " where 1=1 and t.status=0 ";
			if (sys.getSearchDateStart() != null && !sys.getSearchDateStart().equals("")) {
				hql += " and t.createTime >= :searchDateStart ";
				params.put("searchDateStart", DateUtil.getStartOfDay(DateUtil.convertStringToDate("yyyyMMdd", sys.getSearchDateStart())));
			}
			if (sys.getSearchDateEnd() != null && !sys.getSearchDateEnd().equals("")) {
				hql += " and t.createTime <= :searchDateEnd ";
				params.put("searchDateEnd", DateUtil.getEndOfDay(DateUtil.convertStringToDate("yyyyMMdd", sys.getSearchDateEnd())));
			}
			// 如果是超级管理员，可以查看各运营商所有的新闻配置
			if (sys.getId() != 1l) {
				if (StringUtil.isNotBlank(sys.getAgentId())) {
					hql += " and t.agentId  like '" + sys.getAgentId() + "%'";
				}
			}

		}
		return hql;
	}

	/**
	 * 拼接排序语句Hql
	 * 
	 * @param ph
	 * @return
	 */
	private String orderHql(PageFilter ph) {
		String orderString = "";
		if ((ph.getSort() != null) && (ph.getOrder() != null)) {
			orderString = " order by t." + ph.getSort() + " " + ph.getOrder();
		}
		return orderString;
	}

	/**
	 * 删除新闻资讯图片数据库的数据记录
	 */
	@Override
	public void deleteRecord(SysInformationPhoto sys) {
		TsysInformationPhoto t = sysInformationPhotoDao.get(TsysInformationPhoto.class, sys.getId());
		t.setStatus(1);
		sysInformationPhotoDao.update(t);
	}

	/**
	 * 增加某个id对应的记录的点击量
	 */
	@Override
	public void addReadNum(Long id) throws Exception {
		String sql = "update sys_information_photo set read_number = read_number +1 where id = " + id + "";
		sysInformationPhotoDao.executeSql(sql);

	}

	/**
	 * 查询存在的新闻资讯图片，text
	 */
	@Override
	public JSONArray findList(String agentId, PageFilter pf) {
		String hql = "select t from TsysInformationPhoto t where t.agentId='" + agentId + "' and t.status=0 ";
		// 如果是金钱龟，则要区分普通文案的显示位置和最新技术的显示位置
		// 金钱龟使用此接口时为显示最新技术
		if (agentId.equals("F20160015")) {
			hql += " and t.showLocation = 'latest_technology'";
		}

		List<TsysInformationPhoto> s = sysInformationPhotoDao.find(hql + orderHql(pf), pf.getPage(), pf.getRows());
		JSONArray ja = new JSONArray();
		for (TsysInformationPhoto t : s) {
			TsysInformationPhoto tion = new TsysInformationPhoto();
			tion.setCreateTime(t.getCreateTime());
			tion.setReadNumber(t.getReadNumber());
			tion.setText1(t.getText1());
			tion.setText2(t.getText2());
			tion.setId(t.getId());
			String turl = get_wen_root_path + "getImgTHERE?imgName=" + t.getPhoto1_url();
			tion.setPhoto1_url(turl);
			ja.add(tion);
		}
		return ja;
	}

	/**
	 * 查询存在的新闻资讯图片,photo_url
	 */
	@Override
	public JSONArray findListTwo(String agentId, PageFilter pf) {
		String hql = "select t from TsysInformationPhoto t where t.agentId='" + agentId + "' and t.status=0 ";
		// 如果是金钱龟，则要区分普通文案的显示位置和最新技术的显示位置
		// 金钱龟使用此接口时为显示普通文案
		if (agentId.equals("F20160015")) {
			hql += " and t.showLocation = 'document_library'";
		}
		List<TsysInformationPhoto> s = sysInformationPhotoDao.find(hql + orderHql(pf), pf.getPage(), pf.getRows());
		JSONArray ja = new JSONArray();
		for (TsysInformationPhoto t : s) {
			TsysInformationPhoto tion = new TsysInformationPhoto();
			tion.setCreateTime(t.getCreateTime());
			tion.setText1(t.getText1());
			String turl = get_wen_root_path + "getImgTHERE?imgName=";
			if (t.getPhoto1_url() == null) {
				tion.setPhoto1_url(null);
			} else {
				tion.setPhoto1_url(turl + t.getPhoto1_url());
			}
			if (t.getPhoto2_url() == null) {
				tion.setPhoto2_url(null);
			} else {
				tion.setPhoto2_url(turl + t.getPhoto2_url());
			}
			if (t.getPhoto3_url() == null) {
				tion.setPhoto3_url(null);
			} else {
				tion.setPhoto3_url(turl + t.getPhoto3_url());
			}
			if (t.getPhoto4_url() == null) {
				tion.setPhoto4_url(null);
			} else {
				tion.setPhoto4_url(turl + t.getPhoto4_url());
			}
			if (t.getPhoto5_url() == null) {
				tion.setPhoto5_url(null);
			} else {
				tion.setPhoto5_url(turl + t.getPhoto5_url());
			}
			if (t.getPhoto6_url() == null) {
				tion.setPhoto6_url(null);
			} else {
				tion.setPhoto6_url(turl + t.getPhoto6_url());
			}

			if (t.getPhoto7_url() == null) {
				tion.setPhoto7_url(null);
			} else {
				tion.setPhoto7_url(turl + t.getPhoto7_url());
			}

			if (t.getPhoto8_url() == null) {
				tion.setPhoto8_url(null);
			} else {
				tion.setPhoto8_url(turl + t.getPhoto8_url());
			}

			if (t.getPhoto9_url() == null) {
				tion.setPhoto9_url(null);
			} else {
				tion.setPhoto9_url(turl + t.getPhoto9_url());
			}

			ja.add(tion);
		}
		return ja;
	}

	/**
	 * 根据id获取图片详情 text+photo_url
	 */
	public JSONArray findDeatil(Long id) {
		String hql = "select t from TsysInformationPhoto t where t.id=" + id;
		TsysInformationPhoto s = sysInformationPhotoDao.get(hql);
		JSONArray ja = new JSONArray();
		TsysInformationPhoto tion = new TsysInformationPhoto();
		tion.setText1(s.getText1());
		tion.setText2(s.getText2());
		tion.setText3(s.getText3());
		tion.setText4(s.getText4());
		tion.setText5(s.getText5());
		tion.setText6(s.getText6());
		tion.setText7(s.getText7());
		tion.setText8(s.getText8());
		tion.setText9(s.getText9());
		tion.setCreateTime(s.getCreateTime());
		String turl = get_wen_root_path + "getImgTHERE?imgName=";
		tion.setPhoto1_url(null);
		if (s.getPhoto2_url() == null) {
			tion.setPhoto2_url(null);
		} else {
			tion.setPhoto2_url(turl + s.getPhoto2_url());
		}
		if (s.getPhoto3_url() == null) {
			tion.setPhoto3_url(null);
		} else {
			tion.setPhoto3_url(turl + s.getPhoto3_url());
		}
		if (s.getPhoto4_url() == null) {
			tion.setPhoto4_url(null);
		} else {
			tion.setPhoto4_url(turl + s.getPhoto4_url());
		}
		if (s.getPhoto5_url() == null) {
			tion.setPhoto5_url(null);
		} else {
			tion.setPhoto5_url(turl + s.getPhoto5_url());
		}
		if (s.getPhoto6_url() == null) {
			tion.setPhoto6_url(null);
		} else {
			tion.setPhoto6_url(turl + s.getPhoto6_url());
		}

		if (s.getPhoto7_url() == null) {
			tion.setPhoto7_url(null);
		} else {
			tion.setPhoto7_url(turl + s.getPhoto7_url());
		}

		if (s.getPhoto8_url() == null) {
			tion.setPhoto8_url(null);
		} else {
			tion.setPhoto8_url(turl + s.getPhoto8_url());
		}

		if (s.getPhoto9_url() == null) {
			tion.setPhoto9_url(null);
		} else {
			tion.setPhoto9_url(turl + s.getPhoto9_url());
		}

		ja.add(tion);
		return ja;
	}

	@Override
	public List<SysInformationPhoto> dataGridApp(String agentId) {
		List<SysInformationPhoto> result = new ArrayList<SysInformationPhoto>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("agentId", agentId);
		try {
			String hql = "select t from TsysInformationPhoto t where t.agentId=:agentId and t.type=2  order by t.createTime desc";
			List<TsysInformationPhoto> s = sysInformationPhotoDao.find(hql, params);
			for (TsysInformationPhoto t : s) {
				SysInformationPhoto p = new SysInformationPhoto();
				p.setText1(t.getText1());
				result.add(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}

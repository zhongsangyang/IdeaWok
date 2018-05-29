package com.cn.flypay.service.sys.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TsysImage;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.service.sys.UserImageService;

@Service
public class UserImageServiceImpl implements UserImageService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Value("${upload_file_root_path}")
	private String upload_file_root_path;

	@Autowired
	private BaseDao<TsysImage> imageDao;

	@Autowired
	private BaseDao<Tuser> userDao;

	@Override
	public String writeImageTofolder(Long userId, String folder, MultipartFile jarFile)
			throws IOException {

		try {
			String orgFileName = jarFile.getOriginalFilename();
			File fold = new File(upload_file_root_path + File.separator + folder);
			if (!fold.exists()) {
				fold.mkdirs();
			}
			String fileName = UUID.randomUUID().toString().replaceAll("-", "") ;
			String filePath = upload_file_root_path + File.separator + folder + File.separator + fileName;
			logger.info("源文件" + orgFileName + "保存到" + fileName);
			FileUtils.writeByteArrayToFile(new File(filePath), jarFile.getBytes());
			return fileName;
		} catch (IOException e) {
			logger.error("上传图片失败", e);
			throw e;
		}
	}

	@Override
	public File getImage(String fileName) {
		String filePath = upload_file_root_path + File.separator + fileName.substring(0, 8) + File.separator
				+ fileName.substring(8);
		File file = new File(filePath);
		if (file.exists()) {
			return file;
		}
		return null;
	}

	@Override
	public String saveTsysImage(List<TsysImage> images) {
		Session session = imageDao.getCurrentSession();
		session.setCacheMode(CacheMode.IGNORE);
		int saveCount = 0;
		for (TsysImage o : images) {
			session.save(o);
			if (++saveCount % images.size() == 0) {
				// session.clear();
				saveCount = 0;
			}
		}
		return GlobalConstant.RESP_CODE_SUCCESS;
	}

}

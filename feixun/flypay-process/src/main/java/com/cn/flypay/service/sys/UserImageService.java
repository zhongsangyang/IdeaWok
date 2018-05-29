package com.cn.flypay.service.sys;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.cn.flypay.model.sys.TsysImage;

public interface UserImageService {

	String writeImageTofolder(Long userId, String folder,MultipartFile jarFile) throws IOException;

	File getImage(String fileName);

	String saveTsysImage(List<TsysImage> images);

}

package com.cn.flypay.service.sys;

import java.util.List;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.BannerImage;

public interface BannerImageService {
	
	public List<BannerImage> dataGrid(BannerImage param, PageFilter ph);

	public Long count(BannerImage param, PageFilter ph);

	public void add(BannerImage param);

	public void edit(BannerImage param);
	
	public void delete(Long id);
	
	public BannerImage get(Long id);


}

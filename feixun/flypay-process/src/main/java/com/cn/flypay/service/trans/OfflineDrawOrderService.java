package com.cn.flypay.service.trans;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.cn.flypay.model.trans.TOffLineDrawOrder;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.OffLineDrawOrder;

public interface OfflineDrawOrderService {

	public List<TOffLineDrawOrder> dataGrid(OffLineDrawOrder order, PageFilter pf);

	public Long count(OffLineDrawOrder order, PageFilter pf);

	public Long add(OffLineDrawOrder order);

	public int updateBunch();

	public int updateOrder(OffLineDrawOrder offlineOrder, PageFilter ph);

	public int freezeOrder(OffLineDrawOrder offlineOrder, PageFilter ph);

	public int unfreezeOrder(OffLineDrawOrder offlineOrder, PageFilter ph);

	public String dealDownloadOrder();

	public Workbook dealDownloadOrder(String type);

	public List<TOffLineDrawOrder> findDownedOrder(String downloadHql);

	public Long finishOrder();

	public Long finishOrder(String orderNo);
}

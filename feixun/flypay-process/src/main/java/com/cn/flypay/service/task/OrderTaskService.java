package com.cn.flypay.service.task;

public interface OrderTaskService {

	void dealProcessOrderAfterOneHours();

	void dealBunchOfflineOrder();

}

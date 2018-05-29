package com.cn.flypay.utils.jpush.api.device;

import java.util.ArrayList;
import java.util.List;

import cn.jiguang.common.resp.BaseResult;

import com.google.gson.annotations.Expose;

public class TagListResult extends BaseResult {

    private static final long serialVersionUID = -5395153728332839175L;
    @Expose public List<String> tags = new ArrayList<String>();
    
}


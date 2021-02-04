package com.dzk.customglide.repository.listener;

import com.dzk.customglide.resource.Value;

public interface ResponseListener {
    void responseSuccess(Value value);
    //异常详情
    void responseException(Exception e);
}

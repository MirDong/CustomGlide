package com.dzk.customglide.repository.listener;

import android.content.Context;

import com.dzk.customglide.resource.Value;

/**
 * 加载外部资源
 */
public interface ILoadData {
    Value loadResource(String path, ResponseListener listener, Context context);
}

package com.dzk.customglide.resource;

import android.graphics.Bitmap;

public class Value {
    private String key;//标记唯一
    private Bitmap mBitmap;
    private ValueCallback callback;
    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public void setCallback(ValueCallback callback) {
        this.callback = callback;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public void recycle(){
        if (null != callback){
            //活动缓存管理监听
            callback.valueNoUseListener(key,this);
        }
    }
}

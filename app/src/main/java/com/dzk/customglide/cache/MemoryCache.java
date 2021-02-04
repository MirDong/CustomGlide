package com.dzk.customglide.cache;

import android.os.Build;
import android.util.LruCache;

import com.dzk.customglide.resource.Value;

/**
 * LRU内存缓存
 */
public class MemoryCache extends LruCache<String,Value> {
    /**
     * 传入元素最大值
     * @param memory_max_size
     */
    public MemoryCache(int memory_max_size) {
        super(memory_max_size);
    }

    /**
     * 重写父类函数，目的是计算每一个元素的大小(Bitmap的大小)
     * 三种方式获取Bitmap的大小
     * 1.bitmap.getRowBytes();//最初,native
     * 2.bitmap.getByteCount();//3.0  java
     * 3.bitmap.getAllocateByteCount(); //4.4
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    protected int sizeOf(String key, Value value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            return value.getBitmap().getAllocationByteCount();
        }
        return value.getBitmap().getByteCount();
    }

}

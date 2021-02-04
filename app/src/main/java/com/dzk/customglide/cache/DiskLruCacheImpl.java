package com.dzk.customglide.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.dzk.customglide.cache.disk_lru_cache.DiskLruCache;
import com.dzk.customglide.resource.Value;
import com.dzk.customglide.utils.Tool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 磁盘缓存封装
 */
public class DiskLruCacheImpl {
    private static final String TAG = "DiskLruCacheImpl";
    private static final String DISK_LRU_CACHE_PATH = "disk_lru_cache_path";
    private final int APP_VERSION = 1;//版本号，一旦修改版本号，之前的缓存失效
    private final int VALUE_COUNT = 1;//通常情况为1
    private final int MAX_SIZE = 10 * 1024 * 1024;
    private DiskLruCache diskLruCache;
    //TODO Android 10需要适配
    public DiskLruCacheImpl() {
        //sd路径
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + DISK_LRU_CACHE_PATH);
        try {
            diskLruCache = DiskLruCache.open(file,APP_VERSION,VALUE_COUNT,MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Value get(String key) {
        Tool.checkNotEmpty(key);
        InputStream inputStream = null;
        DiskLruCache.Snapshot snapshot = null;
        try {
            //获取快照
            snapshot = diskLruCache.get(key);
            if (null != snapshot){
                Value value = new Value();
                //index 不能大于VALUE_COUNT
                inputStream = snapshot.getInputStream(0);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                value.setBitmap(bitmap);
                value.setKey(key);
                return value;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (null != inputStream){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "get:  inputStream.close() error : " + e.getMessage());
                }
            }
        }
        return null;
    }

    public void put(String key, Value value) {
        Tool.checkNotEmpty(key);
        DiskLruCache.Editor editor = null;
        OutputStream outputStream = null;

        try {
            editor = diskLruCache.edit(key);//与SP一个思路
            outputStream = editor.newOutputStream(0);//index不能大于VALUE_COUNT
            value.getBitmap().compress(Bitmap.CompressFormat.PNG,100,outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            //失败
            try {
                editor.abort();
            } catch (IOException ex) {
                ex.printStackTrace();
                Log.d(TAG, "put:  editor abort, error: " + ex.getMessage());
            }
        }finally {
            try {
                editor.commit();
                diskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

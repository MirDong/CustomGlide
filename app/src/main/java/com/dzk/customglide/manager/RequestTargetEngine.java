package com.dzk.customglide.manager;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.dzk.customglide.cache.ActiveCache;
import com.dzk.customglide.cache.DiskLruCacheImpl;
import com.dzk.customglide.cache.MemoryCache;
import com.dzk.customglide.repository.LoadDataManager;
import com.dzk.customglide.fragment.LifeCycleCallback;
import com.dzk.customglide.repository.listener.ResponseListener;
import com.dzk.customglide.resource.Key;
import com.dzk.customglide.resource.Value;
import com.dzk.customglide.resource.ValueCallback;
import com.dzk.customglide.utils.Tool;

public class RequestTargetEngine implements LifeCycleCallback, ValueCallback, ResponseListener {
    private ActiveCache activeCache;//内存缓存
    private MemoryCache memoryCache;//活动缓存
    private DiskLruCacheImpl diskLruCache;//磁盘缓存
//    private final int MEMORY_MAX_SIZE = (int) (Runtime.getRuntime().totalMemory() / 8);
    private final int MEMORY_MAX_SIZE = 60 * 1024 * 1024;
    private String path;
    private Context glideContext;
    private String key;//磁盘缓存使用的key,64位
    private ImageView imageView;
    private static final String TAG = "RequestTargetEngine";

    public RequestTargetEngine() {
        if (activeCache == null) {
            activeCache = new ActiveCache(this);
        }

        if (memoryCache == null) {
            memoryCache = new MemoryCache(MEMORY_MAX_SIZE);
        }

        if (diskLruCache == null) {
            diskLruCache = new DiskLruCacheImpl();
        }
    }

    @Override
    public void glideInitAction() {
        Log.d(TAG, "glideInitAction: Glide生命周期已经开始初始化...");
    }

    @Override
    public void glideStopAction() {
        Log.d(TAG, "glideInitAction: Glide生命周期已经停止...");
        if (activeCache != null){
            activeCache.recycleActive();
        }
    }

    @Override
    public void glideRecycleAction() {
        Log.d(TAG, "glideInitAction: Glide生命周期已经销毁，进行资源释放操作,释放活动资源...");
    }

    public void loadValueInitAction(String path, Context requestManagerContext) {
        this.path = path;
        this.glideContext = requestManagerContext;
        this.key = new Key(path).getKey();
    }

    @Override
    public void valueNoUseListener(String key, Value value) {
        //value不再使用时，加入内存缓存
        if (null != key && null != value) {
            memoryCache.put(key, value);
        }
    }

    public void into(ImageView imageView) {
        this.imageView = imageView;
        Tool.checkNotEmpty(imageView);
        Tool.assertMainThread();//非主线程抛出异常
        Value value = cacheAction();
        if (null != value) {
            imageView.setImageBitmap(value.getBitmap());
        }
    }

    /**
     * 加载资源->缓存机制->sd卡/网络->将资源保存到缓存中
     *
     * @return
     */
    private Value cacheAction() {
        //步骤一 判断活动缓存是否有资源,有则返回，没有继续往下找
        Value value = activeCache.get(key);
        if (null != value) {
            Log.d(TAG, "cacheAction: 本次加载的是在(活动缓存)中的资源...");
            return value;
        }

        //步骤二 判断内存缓存是否有资源,有则剪切到活动缓存(内存缓存->活动缓存),然后返回。没有继续往下找
        value = memoryCache.get(key);
        if (null != value) {
            Log.d(TAG, "cacheAction: 本次加载的是在(内存缓存)中的资源...");
            //移动操作，从内存缓存移动到活动缓存(剪切)
            activeCache.put(key, value);
            //移除内存缓存
            memoryCache.remove(key);
            return value;
        }
        //步骤三 判断磁盘缓存是否有资源,有则将磁盘缓存的元素添加到活动缓存中，没有继续往下找
        value = diskLruCache.get(key);
        if (null != value) {
            Log.d(TAG, "cacheAction: 本次加载的是在(磁盘缓存)中的资源...");
            activeCache.put(key, value);
            return value;
        }
        //步骤四 加载外部资源Http/本地IO
        value = new LoadDataManager().loadResource(path, this, glideContext);
        if (null != value) {
            Log.d(TAG, "cacheAction:本次加载的是在(网络)中的资源... ");
            return value;
        }
        return null;
    }

    @Override
    public void responseSuccess(Value value) {
        if (value != null) {
            //网络请求到的数据保存到缓存中
            saveCache(key, value);
            imageView.setImageBitmap(value.getBitmap());
        }
    }

    private void saveCache(String key, Value value) {
        Log.d(TAG, "saveCache: >>>>>>加载外置资=资源成功后，保存到缓存中...");
        if (null != diskLruCache) {
            diskLruCache.put(key, value);
            activeCache.put(key, value);
        }
    }

    @Override
    public void responseException(Exception e) {
        Log.d(TAG, "responseException: 加载外置资源失败...错误详情: " + e.getMessage());
    }
}

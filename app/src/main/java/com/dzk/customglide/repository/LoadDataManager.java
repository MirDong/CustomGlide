package com.dzk.customglide.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.dzk.customglide.repository.listener.ILoadData;
import com.dzk.customglide.repository.listener.ResponseListener;
import com.dzk.customglide.resource.Value;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;

public class LoadDataManager implements ILoadData,Runnable {
    private static final String TAG = LoadDataManager.class.getSimpleName();
    private String path;
    private ResponseListener responseListener;
    private Context context;
    private Handler mMainHandler;
    @Override
    public Value loadResource(String path, ResponseListener responseListener, Context context) {
        this.path = path;
        this.responseListener = responseListener;
        this.context = context;
        mMainHandler = new Handler(Looper.getMainLooper());
        if (!TextUtils.isEmpty(path)){
            Uri uri = Uri.parse(path);
            if ("Http".equalsIgnoreCase(uri.getScheme()) || "Https".equalsIgnoreCase(uri.getScheme())){
                //线程池异步加载图片
                Executors.newCachedThreadPool().execute(this);
            }
        }else {
            throw new NullPointerException("path is null or empty");
        }
        //TODO 加载本地SD卡资源
        return null;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(path);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(5 * 1000);
            httpURLConnection.connect();
            final int responseCode = httpURLConnection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                inputStream = httpURLConnection.getInputStream();
                 Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                 final Bitmap compressedBitmap = compressAndSample(bitmap);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Value value = new Value();
                        value.setBitmap(compressedBitmap);
                        if(responseListener != null){
                            responseListener.responseSuccess(value);
                        }
                    }
                });
            }else {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(responseListener != null){
                            responseListener.responseException(new IllegalStateException("请求失败，responseCode: " + responseCode));
                        }
                    }
                });
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (null != inputStream){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(null != httpURLConnection){
                httpURLConnection.disconnect();
            }
        }

    }

    private Bitmap compressAndSample(Bitmap bitmap) {
        if (bitmap != null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,50,baos);
            byte[] bytes = baos.toByteArray();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;//宽高压缩四分之一
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
        }
        return null;
    }
}

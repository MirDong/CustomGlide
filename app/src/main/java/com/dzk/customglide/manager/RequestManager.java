package com.dzk.customglide.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.dzk.customglide.fragment.LifeCycleBlankFragment;

import java.util.HashMap;
import java.util.Map;

public class RequestManager {
    private static RequestTargetEngine callback;
    private static final String FRAGMENT_ACTIVITY_TAG = "fragment_activity_tag";
    private Context requestManagerContext;
    private FragmentActivity fragmentActivity;
    private Map<String,Fragment> fragmentMap = new HashMap<>();
    private static final int HANDLER_CONFIRM_MSG = 1 << 2;
    public RequestManager(FragmentActivity fragmentActivity) {
        if (callback == null){
            callback = new RequestTargetEngine();
        }
        this.fragmentActivity = fragmentActivity;
        this.requestManagerContext = fragmentActivity;

        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
        Fragment fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_TAG);
        if (fragment == null){
             fragment = fragmentMap.get(FRAGMENT_ACTIVITY_TAG);
            if (null == fragment){
                fragment = new LifeCycleBlankFragment(callback);
                fragmentMap.put(FRAGMENT_ACTIVITY_TAG,fragment);
                //将fragment提交了，但是不是马上就会成功，而是存放在队列中
                supportFragmentManager.beginTransaction().add(fragment,FRAGMENT_ACTIVITY_TAG).commitAllowingStateLoss();
                //发送空的Message，确保fragment提交成功
                mHandler.sendEmptyMessage(HANDLER_CONFIRM_MSG);
            }
        }
    }
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what){
                case HANDLER_CONFIRM_MSG:
                    fragmentMap.remove(FRAGMENT_ACTIVITY_TAG);
                    break;
                default:
                    break;
            }

            return false;
        }
    });
    public RequestTargetEngine load(String path) {
        //Handler消息移除
        mHandler.removeMessages(HANDLER_CONFIRM_MSG);

        callback.loadValueInitAction(path,requestManagerContext);
        return callback;
    }
}

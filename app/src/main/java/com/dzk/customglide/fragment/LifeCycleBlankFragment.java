package com.dzk.customglide.fragment;

import androidx.fragment.app.Fragment;

public class LifeCycleBlankFragment extends Fragment {
    private LifeCycleCallback callback;
    public LifeCycleBlankFragment(LifeCycleCallback callback){
        this.callback = callback;
    }
    public LifeCycleBlankFragment() {
    }


    @Override
    public void onStart() {
        super.onStart();
        if (callback != null){
            callback.glideInitAction();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (callback != null){
            callback.glideStopAction();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (callback != null){
            callback.glideRecycleAction();
        }
    }
}

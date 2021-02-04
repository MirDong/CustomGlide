package com.dzk.customglide.manager;

import androidx.fragment.app.FragmentActivity;

public class RequestManagerRetriever {
    public RequestManager get(FragmentActivity fragmentActivity) {
        return new RequestManager(fragmentActivity);
    }
}

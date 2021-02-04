package com.dzk.customglide.manager;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.List;


public class Glide {
    private static volatile Glide mInstance;
    private final RequestManagerRetriever retriever;

    public Glide(RequestManagerRetriever retriever) {
        this.retriever = retriever;
    }

    private final static Glide getInstance(RequestManagerRetriever retriever) {
        if (mInstance == null) {
            synchronized (Glide.class) {
                if (mInstance == null) {
                    mInstance = new Glide(retriever);
                }
            }
        }
        return mInstance;
    }

    /*public static RequestManager with(Context context) {

    }*/


    public static RequestManager with(FragmentActivity fragmentActivity) {
        return getRetriever(fragmentActivity).get(fragmentActivity);
    }

    private static RequestManagerRetriever getRetriever(Context context) {
        return Glide.get(context).getRetriever();
    }

    private RequestManagerRetriever getRetriever() {
        return retriever;
    }

    private static Glide get(Context context) {
        return new GlideBuilder(context).build();
    }

    /*public static RequestManager with(Fragment fragment) {

    }*/


    static final class GlideBuilder {

        public GlideBuilder(Context context) {

        }

        Glide build() {
            RequestManagerRetriever retriever = new RequestManagerRetriever();
            return getInstance(retriever);
        }
    }
}

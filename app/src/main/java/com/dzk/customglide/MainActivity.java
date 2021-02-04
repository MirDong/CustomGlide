package com.dzk.customglide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dzk.customglide.databinding.ActivityMainBinding;
import com.dzk.customglide.manager.Glide;

public class MainActivity extends AppCompatActivity {
    private static final String IMAGE_URL = "https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg";
    ActivityMainBinding dataBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        Log.d("glide", "freeMemory: " + Runtime.getRuntime().freeMemory() / (1024 * 1024));
        Log.d("glide", "totalMemory: " + Runtime.getRuntime().totalMemory() / (1024 * 1024));
        Log.d("glide", "maxMemory: " + Runtime.getRuntime().maxMemory() / (1024 * 1024));
    }

    public void t1(View view) {
        Glide.with(this).load(IMAGE_URL).into(dataBinding.image1);
    }

    public void t2(View view) {
        Glide.with(this).load(IMAGE_URL).into(dataBinding.image2);
    }

    public void t3(View view) {
        Glide.with(this).load(IMAGE_URL).into(dataBinding.image3);
    }
}
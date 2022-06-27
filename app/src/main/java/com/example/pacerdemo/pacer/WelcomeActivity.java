 package com.example.pacerdemo.pacer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.pacerdemo.R;
import com.example.pacerdemo.frame.BaseActivity;

public class WelcomeActivity extends BaseActivity {

    public static final int DELAY_MILLIS = 3000;
    private Runnable jumpRunnable;
    private Handler handler;


    @Override
    protected void onInitVariable() {
        handler = new Handler();
        jumpRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(WelcomeActivity.this,HomeActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        };
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {

        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void onRequestData() {

        handler.postDelayed(jumpRunnable, DELAY_MILLIS);
    }
}

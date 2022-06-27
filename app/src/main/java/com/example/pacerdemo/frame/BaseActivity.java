package com.example.pacerdemo.frame;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public abstract class BaseActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.onInitVariable();
        if (this.isHideAppTitle) {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        super.onCreate(savedInstanceState);
        if (this.isHideSystemTitle) {
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN );
        }
        this.onInitView(savedInstanceState);

        this.onRequestData();
        FrameApplication.addToActivityList(this);

    }

    @Override
    protected void onDestroy() {
        FrameApplication.removeFromActivityList(this);
        super.onDestroy();
    }

    protected abstract void onInitVariable();

    protected abstract void onInitView(final Bundle savedInstanceState);

    protected abstract void onRequestData();

    protected boolean isHideAppTitle = true;
    protected boolean isHideSystemTitle = true;


}

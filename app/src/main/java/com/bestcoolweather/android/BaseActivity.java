package com.bestcoolweather.android;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.bestcoolweather.android.util.ScreenUtil;

public abstract class BaseActivity extends AppCompatActivity {

    private ViewGroup mContentView;
    private View mStatusView, mLayoutView;
    private boolean mIsLight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindowParameter();
        setContentView(R.layout.activity_base);
        initContentView();
    }

    private void initContentView() {
        mContentView = findViewByID(R.id.content_view);
        mStatusView = findViewByID(R.id.status_bar);
        if (mLayoutView == null) {
            mLayoutView = getLayoutInflater().inflate(getLayoutId(), null);
        }
        mContentView.removeAllViews();
        mContentView.addView(mLayoutView);
        setStatusBarColor(R.color.colorPrimary);
    }

    public abstract int getLayoutId();


    public <T extends View> T findViewByID(int id) {
        return (T) super.findViewById(id);
    }

    /**
     * 隐藏Toolbar
     */
    public void hideStatusBar() {
        mStatusView.setVisibility(View.GONE);
    }

    /**
     * 设置是否为亮色主题
     *
     * @param isLight
     */
    public void setLightStatusBar(boolean isLight) {
        this.mIsLight = isLight;
    }

    /**
     * 设置状态栏颜色
     *
     * @param color
     */
    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LinearLayout
                    .LayoutParams.MATCH_PARENT, ScreenUtil.getStatusBarHeight(this));
            mStatusView.setBackgroundResource(color);
            mStatusView.setLayoutParams(lParams);
            mStatusView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置状态栏等与Window相关的参数
     */
    protected void initWindowParameter() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0+
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            if (mIsLight) { //如果StatusBar为亮色主题的话，则文字颜色为深色
                option = option | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4~5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    localLayoutParams.flags);
        }
    }

}


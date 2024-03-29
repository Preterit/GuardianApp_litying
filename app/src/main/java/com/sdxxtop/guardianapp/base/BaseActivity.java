package com.sdxxtop.guardianapp.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.sdxxtop.guardianapp.app.App;
import com.sdxxtop.guardianapp.utils.DialogUtil;
import com.sdxxtop.guardianapp.utils.StatusBarUtil;
import com.sdxxtop.guardianapp.utils.SystemUtil;

import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportActivity;

public abstract class BaseActivity extends SupportActivity {

    private Unbinder mUnbinder;
    protected BaseActivity mContext;
    private DialogUtil mDialogUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        mContext = this;
//        setSwipeBackEnable(false);
        if (isInitStatusBar()) {
            initStatusBar();
        }

        mUnbinder = ButterKnife.bind(this);
        initVariables();
        initView();
        initEvent();
        initData();
    }

    /**
     * statusBar 控制
     */
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            if (StatusBarUtil.MIUISetStatusBarLightMode(getWindow(), true)) {//小米MIUI系统
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//Android6.0以上系统
                    StatusBarUtil.android6_SetStatusBarLightMode(getWindow());
                    StatusBarUtil.compat(this);
                } else {
                    StatusBarUtil.compat(this);
                }
            } else if (StatusBarUtil.FlymeSetStatusBarLightMode(getWindow(), true)) {//魅族flyme系统
                StatusBarUtil.compat(this);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//Android6.0以上系统
                StatusBarUtil.android6_SetStatusBarLightMode(getWindow());
                StatusBarUtil.compat(this);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }

        if (mDialogUtil != null) {
            mDialogUtil = null;
        }
    }

    public void statusBar(boolean isDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//Android6.0以上系统
            StatusBarUtil.setDarkStatusIcon(this.getWindow(), isDark);
        }
    }

//    public void showToast(String msg) {
//        if (TextUtils.isEmpty(msg)) {
//            return;
//        }
//        UIUtils.showToast(msg);
//    }

    public void showToast(String msg){
        if (TextUtils.isEmpty(msg)) {
            return;
        }
//        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
        Toast toast=Toast.makeText(this,msg,Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
    }

    public void showLoadingDialog() {
        if (mDialogUtil == null) {
            mDialogUtil = new DialogUtil();
        }
        mDialogUtil.showLoadingDialog(this);
    }

    public void hideLoadingDialog() {
        if (mDialogUtil != null) {
            mDialogUtil.closeLoadingDialog(this);
        }
    }

    /**
     * 当全屏的时候，状态栏继续显示
     * 调用该方法
     * https://blog.csdn.net/a872822645/article/details/74482323
     */
    protected void showStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public boolean isVersionMoreKitkat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return true;
        } else {
            return false;
        }
    }
    public void topViewPadding(View view) {
        if (isVersionMoreKitkat()) {
            view.setPadding(0, SystemUtil.getStatusHeight(App.getContext()), 0, 0);
        }
    }

    protected void initEvent() {
    }

    protected void initData() {
    }

    protected void initView() {
    }

    protected void initVariables() {
    }

    protected boolean isInitStatusBar() {
        return true;
    }

    protected abstract int getLayout();
}

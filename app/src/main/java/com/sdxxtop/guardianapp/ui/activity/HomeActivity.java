package com.sdxxtop.guardianapp.ui.activity;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.PowerManager;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.luck.picture.lib.permissions.RxPermissions;
import com.orhanobut.logger.Logger;
import com.sdxxtop.guardianapp.R;
import com.sdxxtop.guardianapp.base.BaseMvpActivity;
import com.sdxxtop.guardianapp.model.bean.ArticleIndexBean;
import com.sdxxtop.guardianapp.model.bean.InitBean;
import com.sdxxtop.guardianapp.presenter.HomePresenter;
import com.sdxxtop.guardianapp.presenter.contract.HomeContract;
import com.sdxxtop.guardianapp.ui.dialog.DownloadDialog;
import com.sdxxtop.guardianapp.ui.dialog.IosAlertDialog;
import com.sdxxtop.guardianapp.ui.fragment.DataMonitoringFragment;
import com.sdxxtop.guardianapp.ui.fragment.HomeFragment;
import com.sdxxtop.guardianapp.ui.fragment.LearningFragment;
import com.sdxxtop.guardianapp.ui.fragment.MineFragment;
import com.sdxxtop.guardianapp.utils.ReflectUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;
import me.yokeyword.fragmentation.SupportFragment;

public class HomeActivity extends BaseMvpActivity<HomePresenter> implements HomeContract.IView {

    @BindView(R.id.ahn_home_navigation)
    AHBottomNavigation mAHBottomNavigation;
    private int prePosition;
    private SupportFragment[] mFragments = new SupportFragment[4];
    private boolean isAdmin;
    private RxPermissions mRxPermissions;
    private int currentPosition = 0;

    @Override
    protected int getLayout() {
        return R.layout.activity_home;
    }

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    public void showError(String error) {

    }

    @Override
    protected void initVariables() {
        super.initVariables();
        if (getIntent() != null) {
            isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        }
    }

    @Override
    protected void initView() {
        initAHNavigation();

        switchFragment(0);

        mRxPermissions = new RxPermissions(this);
        mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {

            }
        });

        mRxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    startPatrolService();
                }
            }
        });
    }

    /**
     * 忽略电池优化
     */

    public void ignoreBatteryOptimization(Activity activity) {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean hasIgnored = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasIgnored = powerManager.isIgnoringBatteryOptimizations(activity.getPackageName());
            //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
            if (!hasIgnored) {
//                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//                intent.setData(Uri.parse("package:"+activity.getPackageName()));
//                startActivity(intent);
                new IosAlertDialog(this)
                        .builder()
                        .setCancelable(false)
                        .setTitle(" ")
                        .setMsg(" 请设置手机权限,否则定位轨迹会有偏移 ")
                        .setMsg2("  ")
                        .setPositiveButton("去设置", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                OpenAutoStartUtil.jumpStartInterface(HomeActivity.this);
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                ComponentName cn = ComponentName.unflattenFromString("com.android.settings/.Settings$HighPowerApplicationsActivity");
                                intent.setComponent(cn);
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .show();
            }
        }
    }

    private void startPatrolService() {
//        Logger.e("开启了服务");
        ignoreBatteryOptimization(this);
//        if (1 == 1) return;
//        new IosAlertDialog(this)
//                .builder()
//                .setCancelable(false)
//                .setMsg("请设置手机权限,否则定位轨迹会有偏移")
//                .setHeightMsg(" ")
//                .setMsg2(" ")
//                .setPositiveButton("去设置", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        OpenAutoStartUtil.jumpStartInterface(HomeActivity.this);
//                    }
//                })
//                .setNegativeButton("取消", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                })
//                .show();

        mPresenter.getLocationInfo();


//        Intent forgroundService = new Intent(this, BackGroundService.class);
//        startService(forgroundService);
    }

    private void initAHNavigation() {
        int[] tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu);
        navigationAdapter.setupWithBottomNavigation(mAHBottomNavigation, tabColors);

        // Set background color
        mAHBottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

        // Disable the translation inside the CoordinatorLayout
        mAHBottomNavigation.setBehaviorTranslationEnabled(false);
        mAHBottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        // Change colors
        mAHBottomNavigation.setAccentColor(Color.parseColor("#34B26D"));
        mAHBottomNavigation.setInactiveColor(Color.parseColor("#747474"));
        mAHBottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                Logger.i("position = " + position + "wasSelected = " + wasSelected);
//                if (position == 2) {
//                    return false;
//                }
                if (position == 2) {
                    mPresenter.articleIndex(position, wasSelected);
                    return false;
                } else {
                    currentPosition = position;
                    switchFragment(position);
                    itemSelectAnimator(position, wasSelected);
                    return true;
                }
            }
        });
    }

    private List<ArticleIndexBean.ShowBean> showList = new ArrayList<>();

    @Override
    public void showPermission(ArticleIndexBean bean, int position, boolean wasSelected) {
        showList.clear();
        if (bean.getShow() != null && bean.getShow().size() > 0) {
            List<ArticleIndexBean.ShowBean> list = bean.getShow();
            for (ArticleIndexBean.ShowBean showBean : list) {
                if (showBean.getIs_show() == 1) {
                    showList.add(showBean);
                }
            }

            if (showList.size() > 0) {
                if (mFragments[2] != null) {
                    ((LearningFragment) mFragments[2]).replaceList(showList);
                }
                switchFragment(position);
                itemSelectAnimator(position, wasSelected);
                mAHBottomNavigation.setCurrentItem(2, false);
            } else {
                showToast("没有操作权限");
                mAHBottomNavigation.setCurrentItem(currentPosition, false);
            }
        } else {
            mAHBottomNavigation.setCurrentItem(currentPosition, false);
            showToast("没有操作权限");
        }
    }

    private void switchFragment(int position) {
        HomeFragment fragment = findFragment(HomeFragment.class);
        if (fragment == null) {
            mFragments[0] = HomeFragment.newInstance(isAdmin);
            mFragments[1] = DataMonitoringFragment.newInstance();
            mFragments[2] = new LearningFragment();
            mFragments[3] = MineFragment.newInstance(isAdmin);

            loadMultipleRootFragment(R.id.fl_home_container, position,
                    mFragments[0],
                    mFragments[1],
                    mFragments[2],
                    mFragments[3]);
        } else {
            showHideFragment(mFragments[position], mFragments[prePosition]);
        }
        prePosition = position;
    }

    private void itemSelectAnimator(int position, boolean wasSelected) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Logger.i("版本不符合,不进行动画");
            return;
        }

        List<View> views = ReflectUtils.getFieldValueByFieldName("views", mAHBottomNavigation);
        if (views != null) {
            View view = views.get(position);
            ImageView icon = view.findViewById(R.id.bottom_navigation_item_icon);
            final int width = icon.getMeasuredWidth();
            final int height = icon.getMeasuredHeight();
            final float radius = (float) Math.sqrt(width * width + height * height) / 2;//半径
            Animator animator = ViewAnimationUtils.createCircularReveal(icon, width / 2, height / 2, 0, radius);
            animator.setDuration(300);
            animator.start();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.initApp();
    }

    @Override
    public void showInit(InitBean initBean) {
        if (initBean != null) {
            DownloadDialog downloadDialog = new DownloadDialog(this, initBean, new RxPermissions(this));
            downloadDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

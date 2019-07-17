package com.sdxxtop.guardianapp.presenter;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.sdxxtop.guardianapp.TrackService.TrackServiceUtil;
import com.sdxxtop.guardianapp.app.Constants;
import com.sdxxtop.guardianapp.base.RxPresenter;
import com.sdxxtop.guardianapp.model.bean.ArticleIndexBean;
import com.sdxxtop.guardianapp.model.bean.InitBean;
import com.sdxxtop.guardianapp.model.bean.RequestBean;
import com.sdxxtop.guardianapp.model.bean.TrackBean;
import com.sdxxtop.guardianapp.model.bean.TrackInfoBean;
import com.sdxxtop.guardianapp.model.http.callback.IRequestCallback;
import com.sdxxtop.guardianapp.model.http.net.Params;
import com.sdxxtop.guardianapp.model.http.util.RxUtils;
import com.sdxxtop.guardianapp.presenter.contract.HomeContract;
import com.sdxxtop.guardianapp.utils.SpUtil;
import com.sdxxtop.guardianapp.utils.UIUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class HomePresenter extends RxPresenter<HomeContract.IView> implements HomeContract.IPresenter {
    @Inject
    public HomePresenter() {
    }


    public void initApp() {
        Params params = new Params();
        params.put("pi", 1);
        Observable<RequestBean<InitBean>> observable = getEnvirApi().postAppInit(params.getData());
        Disposable disposable = RxUtils.handleDataHttp(observable, new IRequestCallback<InitBean>() {
            @Override
            public void onSuccess(InitBean initBean) {
                mView.showInit(initBean);
            }

            @Override
            public void onFailure(int code, String error) {

            }
        });
        addSubscribe(disposable);
    }


    public void articleIndex(int position, boolean wasSelected) {
        Params params = new Params();
        Observable<RequestBean<ArticleIndexBean>> observable = getEnvirApi().postArticleIndex(params.getData());
        Disposable disposable = RxUtils.handleDataHttp(observable, new IRequestCallback<ArticleIndexBean>() {
            @Override
            public void onSuccess(ArticleIndexBean bean) {
                if (bean != null) {
                    mView.showPermission(bean, position, wasSelected);
                } else {
                    mView.showError("没有操作权限");
                }
            }

            @Override
            public void onFailure(int code, String error) {
            }
        });
        addSubscribe(disposable);

    }

    public void getLocationInfo() {
        Params params = new Params();
        Observable<RequestBean<TrackInfoBean>> observable = getEnvirApi().postFalconFalconGet(params.getData());
        Disposable disposable = RxUtils.handleDataHttp(observable, new IRequestCallback<TrackInfoBean>() {
            @Override
            public void onSuccess(TrackInfoBean bean) {
                if (bean != null) {
                    SpUtil.putLong(Constants.SERVICE_ID, bean.getSid());
                    SpUtil.putLong(Constants.TERMINAL_ID, bean.getTid());
                    SpUtil.putLong(Constants.TRACK_ID, bean.getTrid());
                    TrackServiceUtil util = new TrackServiceUtil();
                    util.stsrtTrackService(bean.getSid(), bean.getTid(), bean.getTrid());

                    handler.sendEmptyMessageDelayed(0, 1000 * 60);
                }
            }

            @Override
            public void onFailure(int code, String error) {
            }
        });



        addSubscribe(disposable);
    }

    public void uploadingPoint() {
        handler.sendEmptyMessageDelayed(0, 1000 * 60);
        Params params = new Params();
        params.put("sid", SpUtil.getLong(Constants.SERVICE_ID, 0));
        params.put("tid", SpUtil.getLong(Constants.TERMINAL_ID, 0));
        params.put("trid", SpUtil.getLong(Constants.TRACK_ID, 0));

        Observable<RequestBean<TrackBean>> observable = getEnvirApi().postFalconReturnTime(params.getData());
        Disposable disposable = RxUtils.handleDataHttp(observable, new IRequestCallback<TrackBean>() {
            @Override
            public void onSuccess(TrackBean bean) {
                UIUtils.showToast("上传成功:" + getCurrentTime()+"\n" +"本次获取轨迹点为"+bean.getNum()+"条");
            }

            @Override
            public void onFailure(int code, String error) {
                UIUtils.showToast("上传失败:" + getCurrentTime());
            }
        });
        addSubscribe(disposable);
    }

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            uploadingPoint();
        }
    };

    public String getCurrentTime() {
        Date t = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(t);
    }
}

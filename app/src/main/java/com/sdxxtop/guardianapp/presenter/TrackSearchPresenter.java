package com.sdxxtop.guardianapp.presenter;


import com.sdxxtop.guardianapp.base.RxPresenter;
import com.sdxxtop.guardianapp.model.bean.RequestBean;
import com.sdxxtop.guardianapp.model.bean.TrackInfoBean;
import com.sdxxtop.guardianapp.model.http.callback.IRequestCallback;
import com.sdxxtop.guardianapp.model.http.net.Params;
import com.sdxxtop.guardianapp.model.http.util.RxUtils;
import com.sdxxtop.guardianapp.presenter.contract.TrackSearchContract;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * 用来copy使用的
 */
public class TrackSearchPresenter extends RxPresenter<TrackSearchContract.IView> implements TrackSearchContract.IPresenter {
    @Inject
    public TrackSearchPresenter() {
    }


    public void loadAllTrack() {
        Params params = new Params();
        Observable<RequestBean<TrackInfoBean>> observable = getEnvirApi().postFalconFalconGet(params.getData());
        Disposable disposable = RxUtils.handleDataHttp(observable, new IRequestCallback<TrackInfoBean>() {
            @Override
            public void onSuccess(TrackInfoBean bean) {
                if (bean!=null){
                    if(mView!=null){
                        mView.checkTrack(bean);
                    }
                }
            }

            @Override
            public void onFailure(int code, String error) {
            }
        });
        addSubscribe(disposable);
    }
}

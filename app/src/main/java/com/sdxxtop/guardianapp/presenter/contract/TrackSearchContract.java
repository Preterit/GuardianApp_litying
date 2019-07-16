package com.sdxxtop.guardianapp.presenter.contract;

import com.sdxxtop.guardianapp.base.BasePresenter;
import com.sdxxtop.guardianapp.base.BaseView;
import com.sdxxtop.guardianapp.model.bean.TrackInfoBean;

/**
 * 用来copy使用的
 */
public interface TrackSearchContract {
    interface IView extends BaseView {

        void checkTrack(TrackInfoBean bean);
    }

    interface IPresenter extends BasePresenter<IView> {

    }
}

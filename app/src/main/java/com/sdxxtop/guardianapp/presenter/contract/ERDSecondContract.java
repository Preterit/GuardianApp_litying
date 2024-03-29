package com.sdxxtop.guardianapp.presenter.contract;

import com.sdxxtop.guardianapp.base.BasePresenter;
import com.sdxxtop.guardianapp.base.BaseView;

/**
 * EventReportDetailSecondContract
 */
public interface ERDSecondContract {
    interface IView extends BaseView {
        void modifyRefresh();
    }

    interface IPresenter extends BasePresenter<IView> {

    }
}

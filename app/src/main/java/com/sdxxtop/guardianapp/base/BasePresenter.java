package com.sdxxtop.guardianapp.base;

public interface BasePresenter<T> {
    void attachView(T t);

    void detachView();
}

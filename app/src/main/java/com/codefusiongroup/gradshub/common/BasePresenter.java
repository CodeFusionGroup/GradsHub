package com.codefusiongroup.gradshub.common;


// every Presenter must implement BasePresenter to override these methods
public interface BasePresenter<V> {

    void subscribe(V view);

    void unsubscribe();

}

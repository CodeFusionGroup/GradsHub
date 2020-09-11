package com.codefusiongroup.gradshub.common;

public interface BasePresenter2<V> {

    void attachView(V view);

    void detachView(V view);

}

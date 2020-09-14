package com.codefusiongroup.gradshub.common;


public interface BaseView<P> {

    void setPresenter(P presenter);

    void showProgressBar();

    void hideProgressBar();

}

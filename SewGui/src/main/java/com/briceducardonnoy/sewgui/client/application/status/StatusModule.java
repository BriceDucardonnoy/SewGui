package com.briceducardonnoy.sewgui.client.application.status;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class StatusModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        bindPresenter(StatusPresenter.class, StatusPresenter.MyView.class, StatusView.class, StatusPresenter.MyProxy.class);
    }
}
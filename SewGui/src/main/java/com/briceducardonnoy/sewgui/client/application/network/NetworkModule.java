package com.briceducardonnoy.sewgui.client.application.network;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class NetworkModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        bindPresenter(NetworkPresenter.class, NetworkPresenter.MyView.class, NetworkView.class, NetworkPresenter.MyProxy.class);
    }
}
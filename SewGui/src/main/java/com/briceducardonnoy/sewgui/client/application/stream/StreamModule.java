package com.briceducardonnoy.sewgui.client.application.stream;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class StreamModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        bindPresenter(StreamPresenter.class, StreamPresenter.MyView.class, StreamView.class, StreamPresenter.MyProxy.class);
    }
}
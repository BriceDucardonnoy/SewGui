package com.briceducardonnoy.sewgui.client.application;

import com.briceducardonnoy.sewgui.client.application.status.StatusModule;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.briceducardonnoy.sewgui.client.application.network.NetworkModule;
import com.briceducardonnoy.sewgui.client.application.stream.StreamModule;

public class ApplicationModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new StreamModule());
		install(new NetworkModule());
		install(new StatusModule());

        bindPresenter(ApplicationPresenter.class, ApplicationPresenter.MyView.class, ApplicationView.class,
                ApplicationPresenter.MyProxy.class);
    }
}
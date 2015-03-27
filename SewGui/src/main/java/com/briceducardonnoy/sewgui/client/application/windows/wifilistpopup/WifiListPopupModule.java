package com.briceducardonnoy.sewgui.client.application.windows.wifilistpopup;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class WifiListPopupModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
            bindPresenterWidget(WifiListPopupPresenter.class, WifiListPopupPresenter.MyView.class, WifiListPopupView.class);
    }
}
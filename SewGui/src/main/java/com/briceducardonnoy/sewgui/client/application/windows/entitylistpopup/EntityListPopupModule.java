package com.briceducardonnoy.sewgui.client.application.windows.entitylistpopup;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class EntityListPopupModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
            bindPresenterWidget(EntityListPopupPresenter.class, EntityListPopupPresenter.MyView.class, EntityListPopupView.class);
    }
}
package com.briceducardonnoy.sewgui.client.application.network;

import javax.inject.Inject;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

class NetworkView extends ViewImpl implements NetworkPresenter.MyView {
    interface Binder extends UiBinder<Widget, NetworkView> {
    }

    @UiField
    SimplePanel main;

    @Inject
    NetworkView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
    }
    
    @Override
    public void setInSlot(Object slot, IsWidget content) {
        if (slot == NetworkPresenter.SLOT_Network) {
            main.setWidget(content);
        } else {
            super.setInSlot(slot, content);
        }
    }
}
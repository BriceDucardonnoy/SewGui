package com.briceducardonnoy.sewgui.client.application.status;

import javax.inject.Inject;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

class StatusView extends ViewImpl implements StatusPresenter.MyView {
    interface Binder extends UiBinder<Widget, StatusView> {
    }

    @UiField HTMLPanel main;

    @Inject
    StatusView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
    }
    
    @Override
    public void setInSlot(Object slot, IsWidget content) {
        if (slot == StatusPresenter.SLOT_Status) {
//            main.setWidget(content);
        	main.clear();
        	main.add(content);
        } else {
            super.setInSlot(slot, content);
        }
    }
}
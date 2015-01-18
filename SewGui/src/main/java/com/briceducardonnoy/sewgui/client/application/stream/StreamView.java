package com.briceducardonnoy.sewgui.client.application.stream;

import javax.inject.Inject;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

class StreamView extends ViewImpl implements StreamPresenter.MyView {
    interface Binder extends UiBinder<Widget, StreamView> {
    }

    @UiField
    SimplePanel main;

    @Inject
    StreamView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
    }
    
    @Override
    public void setInSlot(Object slot, IsWidget content) {
        if (slot == StreamPresenter.SLOT_Stream) {
            main.setWidget(content);
        } else {
            super.setInSlot(slot, content);
        }
    }
}
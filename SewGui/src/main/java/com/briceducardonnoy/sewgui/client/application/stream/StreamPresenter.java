package com.briceducardonnoy.sewgui.client.application.stream;

import com.briceducardonnoy.sewgui.client.application.ApplicationPresenter;
import com.briceducardonnoy.sewgui.client.place.NameTokens;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.presenter.slots.NestedSlot;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
public class StreamPresenter extends Presenter<StreamPresenter.MyView, StreamPresenter.MyProxy>  {
    interface MyView extends View  {
    }
    
    public static final NestedSlot SLOT_Stream = new NestedSlot();

    @NameToken(NameTokens.stream)
    @ProxyCodeSplit
    interface MyProxy extends ProxyPlace<StreamPresenter> {
    }

    @Inject
    StreamPresenter(
            EventBus eventBus,
            MyView view, 
            MyProxy proxy) {
        super(eventBus, view, proxy, ApplicationPresenter.SLOT_SetMainContent);
        
    }
    
    protected void onBind() {
        super.onBind();
    }
    
    protected void onReveal() {
        super.onReveal();
    }
    
    protected void onReset() {
        super.onReset();
    }
}
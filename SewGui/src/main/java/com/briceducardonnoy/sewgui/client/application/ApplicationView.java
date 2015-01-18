package com.briceducardonnoy.sewgui.client.application;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.NavbarBrand;

import com.allen_sauer.gwt.log.client.Log;
import com.briceducardonnoy.sewgui.client.lang.Translate;
import com.briceducardonnoy.sewgui.client.place.NameTokens;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

class ApplicationView extends ViewImpl implements ApplicationPresenter.MyView {
    
	interface Binder extends UiBinder<Widget, ApplicationView> {
    }
    
    private final Translate translate = GWT.create(Translate.class);

    @Inject PlaceManager placeManager;
    
    @UiField SimplePanel main;
    @UiField NavbarBrand brand;
    @UiField AnchorListItem status;
    @UiField AnchorListItem network;
    @UiField AnchorListItem stream;
    
    private PlaceRequest statusGo;
    private PlaceRequest networkGo;
    private PlaceRequest streamGo;

    @Inject
    ApplicationView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
        Log.info(translate.Bonjour() + " from appsView");
        
        statusGo = new PlaceRequest.Builder().nameToken(NameTokens.getStatus()).build();
        networkGo = new PlaceRequest.Builder().nameToken(NameTokens.getNetwork()).build();
        streamGo = new PlaceRequest.Builder().nameToken(NameTokens.getStream()).build();
    }

    @Override
    public void setInSlot(Object slot, IsWidget content) {
        if (slot == ApplicationPresenter.SLOT_SetMainContent) {
            main.setWidget(content);
        } else {
            super.setInSlot(slot, content);
        }
    }
    
    @UiHandler("brand")
    void onBrandSelected(ClickEvent event) {
    	Log.info("Brand clicked");
    	placeManager.revealDefaultPlace();
    	status.setActive(true);// TODO BDY: optimise it
    	network.setActive(false);
    	stream.setActive(false);
    }
    
    @UiHandler("status")
    void onStatusSelected(ClickEvent event) {
    	Log.info("Status clicked");
    	placeManager.revealPlace(statusGo);
    	status.setActive(true);
    	network.setActive(false);
    	stream.setActive(false);
    }
    
    @UiHandler("network")
    void onNetworkSelected(ClickEvent event) {
    	Log.info("Network clicked");
    	placeManager.revealPlace(networkGo);
    	status.setActive(false);
    	network.setActive(true);
    	stream.setActive(false);
    }
    
    @UiHandler("stream")
    void onStreamSelected(ClickEvent event) {
    	Log.info("Stream clicked");
    	placeManager.revealPlace(streamGo);
    	status.setActive(false);
    	network.setActive(false);
    	stream.setActive(true);
    }
    
}
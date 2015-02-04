/**
 * Copyright 2015 © Brice DUCARDONNOY
 * 
 * Permission is hereby granted, free of charge, to any person obtaining 
 * a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * - The above copyright notice and this permission notice shall be included 
 * 	in all copies or substantial portions of the Software.
 * - The Software is provided "as is", without warranty of any kind, express 
 * 	or implied, including but not limited to the warranties of merchantability, 
 * 	fitness for a particular purpose and noninfringement.
 * 
 * In no event shall the authors or copyright holders be liable for any claim, 
 * damages or other liability, whether in an action of contract, tort or otherwise, 
 * arising from, out of or in connection with the software or the use or other 
 * dealings in the Software.
 */
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
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
	
	@UiField Button isBTEnabled;
	@UiField Button btList;
	@UiField Button isConnected;
	@UiField Button connect;
	@UiField Button disconnect;
	@UiField Button subscribe;
	@UiField Button unsubscribe;
	@UiField Button write;

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

	@UiHandler("reload")
	void onReload(ClickEvent event) {
		Window.Location.reload();
	}

	@Override
	public Button getBTBtn() {
		return isBTEnabled;
	}
	
	@Override
	public Button getListBtn() {
		return btList;
	}
	
	@Override
	public Button getConnected() {
		return isConnected;
	}
	
	@Override
	public Button getConnect() {
		return connect;
	}
	
	@Override
	public Button getDisconnect() {
		return disconnect;
	}
	
	@Override
	public Button getSubscribe() {
		return subscribe;
	}
	
	@Override
	public Button getUnsubscribe() {
		return unsubscribe;
	}
	
	@Override
	public Button getWrite() {
		return write;
	}
	
}
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
package com.briceducardonnoy.sewgui.client.application.network;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.allen_sauer.gwt.log.client.Log;
import com.briceducardonnoy.sewgui.client.application.ApplicationPresenter;
import com.briceducardonnoy.sewgui.client.application.context.ApplicationContext;
import com.briceducardonnoy.sewgui.client.application.model.DataModel.Group;
import com.briceducardonnoy.sewgui.client.application.protocol.RequestHelper;
import com.briceducardonnoy.sewgui.client.application.protocol.models.WifiNetwork;
import com.briceducardonnoy.sewgui.client.application.windows.entitylistpopup.EntityListPopupPresenter;
import com.briceducardonnoy.sewgui.client.events.DataModelEvent;
import com.briceducardonnoy.sewgui.client.events.DataModelEvent.DataModelHandler;
import com.briceducardonnoy.sewgui.client.events.WiFiDiscoverEvent;
import com.briceducardonnoy.sewgui.client.events.WiFiDiscoverEvent.WiFiDiscoverHandler;
import com.briceducardonnoy.sewgui.client.place.NameTokens;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

public class NetworkPresenter extends Presenter<NetworkPresenter.MyView, NetworkPresenter.MyProxy>  {
	interface MyView extends View  {
	}
	@ContentSlot
	public static final Type<RevealContentHandler<?>> SLOT_Network = new Type<RevealContentHandler<?>>();

	@NameToken(NameTokens.network)
	@ProxyCodeSplit
	interface MyProxy extends ProxyPlace<NetworkPresenter> {
	}

	private static Logger logger = Logger.getLogger("SewGui");
	private List<HandlerRegistration> handlers;
	@Inject EntityListPopupPresenter<WifiNetwork> wifiListPopup;
	@Inject ApplicationContext context;

	@Inject
	NetworkPresenter(EventBus eventBus, MyView view, MyProxy proxy, ApplicationContext context) {
		super(eventBus, view, proxy, ApplicationPresenter.SLOT_SetMainContent);
		this.context = context;
		handlers = new ArrayList<>();
	}

	protected void onBind() {
		super.onBind();
		registerHandler(getEventBus().addHandler(WiFiDiscoverEvent.getType(), new WiFiDiscoverHandler() {
			@Override
			public void onWiFiDiscover(WiFiDiscoverEvent event) {
				logger.info("Wifi discover event received");
				List<WifiNetwork> wifis = WifiNetwork.toWifiNetwork(event.getMessage(), event.getProtocolVersion());
				logger.info(wifis.toString());
				wifiListPopup.setDevices(wifis);
				addToPopupSlot(wifiListPopup, true);
			}
		}));
	}

	protected void onReveal() {
		super.onReveal();
		if(!context.isPhoneGapAvailable() || !context.isConnected2Device()) {
			logger.info("Not connected => do nothing");
			Log.info("Not connected => do nothing");
			return;
		}
		// Subscribe IDs to DataModel and add handlers
		context.getModel().subscribe(Group.NETWORK);
		handlers.add(getEventBus().addHandler(DataModelEvent.getSerializedType(), dmHandler));
		// Ask network data to remote unit
		byte []request = RequestHelper.getNetwork(context.getCurrentProtocol());
		context.getBluetoothPlugin().write(request, getNetworkCB);
	}
	
	@Override
	protected void onHide() {
		super.onHide();
		context.getModel().unsubscribe(Group.NETWORK);
		// Remove handlers
		for(HandlerRegistration handler : handlers) {
			handler.removeHandler();
		}
	}

	protected void onReset() {
		super.onReset();
	}
	
	/*
	 * Handlers and callback
	 */
	private DataModelHandler dmHandler = new DataModelHandler() {
		@Override
		public void onDataModelUpdated(DataModelEvent event) {
			logger.info("DataModelEvent for network page");
			logger.info(event.getUpdatedIds().toString());
			// TODO BDY: Remove the extra logger line...
		}
	};
	
	private Callback<Object, String> getNetworkCB = new Callback<Object, String>() {
		@Override
		public void onFailure(String reason) {
			logger.warning("Get nework failed " + reason);
			Window.alert("Get network failed " + reason);
		}
		@Override
		public void onSuccess(Object result) {
			logger.info("Get network succeed");
		}
	};
}
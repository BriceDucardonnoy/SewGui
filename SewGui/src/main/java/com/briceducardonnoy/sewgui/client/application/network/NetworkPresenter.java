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
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.gwtbootstrap3.client.ui.Button;

import com.allen_sauer.gwt.log.client.Log;
import com.briceducardonnoy.sewgui.client.application.ApplicationPresenter;
import com.briceducardonnoy.sewgui.client.application.protocol.RequestHelper;
import com.briceducardonnoy.sewgui.client.application.protocol.models.WifiNetwork;
import com.briceducardonnoy.sewgui.client.application.windows.entitylistpopup.EntityListPopupPresenter;
import com.briceducardonnoy.sewgui.client.context.ApplicationContext;
import com.briceducardonnoy.sewgui.client.events.DataModelEvent;
import com.briceducardonnoy.sewgui.client.events.DataModelEvent.DataModelHandler;
import com.briceducardonnoy.sewgui.client.events.DirtyModelEvent;
import com.briceducardonnoy.sewgui.client.events.DirtyModelEvent.DirtyModelHandler;
import com.briceducardonnoy.sewgui.client.events.WiFiDiscoverEvent;
import com.briceducardonnoy.sewgui.client.events.WiFiDiscoverEvent.WiFiDiscoverHandler;
import com.briceducardonnoy.sewgui.client.model.DataModel;
import com.briceducardonnoy.sewgui.client.model.DataModel.Group;
import com.briceducardonnoy.sewgui.client.model.IFormManaged;
import com.briceducardonnoy.sewgui.client.model.IFormManager;
import com.briceducardonnoy.sewgui.client.place.NameTokens;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.presenter.slots.NestedSlot;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

public class NetworkPresenter extends Presenter<NetworkPresenter.MyView, NetworkPresenter.MyProxy> implements IFormManager {
	
	interface MyView extends View  {
		void setPwd(String value);
		void setWidgetEnabled(boolean enableb);
		void setFormActionEnabled(boolean enabled);
		Button getDiscoverWiFiBtn();
		RadioButton getWifiMode();
		RadioButton getEthernetMode();
	}
	
	public static final NestedSlot SLOT_Network = new NestedSlot();

	@NameToken(NameTokens.network)
	@ProxyCodeSplit
	interface MyProxy extends ProxyPlace<NetworkPresenter> {
	}

	private static Logger logger = LogManager.getLogManager().getLogger("SewGui");
	private List<HandlerRegistration> handlers;
	private ApplicationContext context;
	private List<IFormManaged<? extends Comparable<?>>> formWidgets;
	@Inject EntityListPopupPresenter<WifiNetwork> wifiListPopup;

	@Inject
	NetworkPresenter(EventBus eventBus, MyView view, MyProxy proxy, ApplicationContext context) {
		super(eventBus, view, proxy, ApplicationPresenter.SLOT_SetMainContent);
		this.context = context;
		handlers = new ArrayList<>();
		formWidgets = new ArrayList<>();
	}

	@Override
	protected void onBind() {
		super.onBind();
		registerHandler(getView().getDiscoverWiFiBtn().addClickHandler(discoverH));
		registerHandler(getEventBus().addHandler(WiFiDiscoverEvent.getType(), wifiFoundH));
		registerHandler(getEventBus().addHandler(DirtyModelEvent.getType(), dirtyModelH));
		registerHandler(getView().getWifiMode().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				context.getBluetoothPlugin().write(
					RequestHelper.getNetworkWifi(context.getCurrentProtocol()), 
					getNetworkCB);
			}
		}));
		registerHandler(getView().getEthernetMode().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				context.getBluetoothPlugin().write(
					RequestHelper.getNetworkLan(context.getCurrentProtocol()), 
					getNetworkCB);
			}
		}));
		register(ApplicationContext.getFormManagedWidgetFromFormName(getFormGroup()));
	}

	protected void onReveal() {
		super.onReveal();
		// Subscribe IDs to DataModel and add handlers
		context.getModel().subscribe(Group.NETWORK);// Won't be present in the future as widget themselves will ask their value through events
		handlers.add(getEventBus().addHandler(DataModelEvent.getSerializedType(), dmHandler));
		if(!context.isPhoneGapAvailable() || !context.isConnected2Device()) {
			logger.info("Not connected => do nothing");
			Log.info("Not connected => do nothing");
			getView().setWidgetEnabled(false);
			return;
		}
		// Ask network data to remote unit
		byte []request = RequestHelper.getNetworkLan(context.getCurrentProtocol());
		context.getBluetoothPlugin().write(request, getNetworkCB);
		getView().setFormActionEnabled(false);
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

	/*
	 * IFormManager inherited methods
	 */
	@Override
	public void register(List<IFormManaged<?>> widgets) {
		if(widgets == null) {
			// Not normal as we are a IFormManager
			return;
		}
		for(IFormManaged<? extends Comparable<?>> widget : widgets) {
			if(!formWidgets.contains(widget)) {
				Log.info("Register " + widget.getDisplayName() + " in form " + getFormGroup());
				logger.info("Register " + widget.getDisplayName() + " in form " + getFormGroup());
				formWidgets.add(widget);
			}
		}
	}
	
	@Override
	public void submit() {
		// TODO BDY: NYI submit
	}
	
	@Override
	public void cancel() {
		// TODO BDY: NYI cancel
	}
	
	@Override
	public String getFormGroup() {
		return "NETWORK";
	}
	
	/*
	 * Handlers and callback
	 */
	private DataModelHandler dmHandler = new DataModelHandler() {
		@Override
		public void onDataModelUpdated(DataModelEvent event) {
			logger.info("DataModelEvent for network page");
			logger.info(event.getUpdatedIds().toString());
			// If it's related to the connection, send the network informations request
			if(event.getUpdatedIds().contains(DataModel.IS_BLUETOOTH_CONNECTED)) {
				logger.info("Update bluetooth state to " + context.isConnected2Device());
				if(context.isConnected2Device()) {
					byte []request = RequestHelper.getNetworkLan(context.getCurrentProtocol());
					context.getBluetoothPlugin().write(request, getNetworkCB);
				}
				else {
					getView().setWidgetEnabled(false);
				}
				return;
			}
			// TODO BDY: Store local password
			// TODO BDY: Create a system similar of activatePage which (dis)abled the widget depending of the status instead of write manually a "setWidgetEnabled"
			getView().setPwd((String) context.getModel().getValue(DataModel.WiFi_PWD));
			for(IFormManaged<?> formWidget : formWidgets) {
				logger.fine("Update original value for " + formWidget.getDisplayName() + " (" + formWidget.getModelId() + ") with " + 
					context.getModel().getValue(formWidget.getModelId()));
				formWidget.setOriginalValue(context.getModel().getValue(formWidget.getModelId()));
			}
		}
	};
	
	private Callback<Object, String> getNetworkCB = new Callback<Object, String>() {
		@Override
		public void onFailure(String reason) {
			logger.warning("Get nework failed " + reason);
			Window.alert("Get network failed " + reason);
			getView().setWidgetEnabled(false);
		}
		@Override
		public void onSuccess(Object result) {
			logger.info("Get network succeed");
			getView().setWidgetEnabled(true);
		}
	};
	// WiFi
	private ClickHandler discoverH = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if(!context.isPhoneGapAvailable() || !context.isConnected2Device()) {
				logger.info("Not connected => do nothing");
				Log.info("Not connected => do nothing");
				return;
			}
			byte []request = RequestHelper.wifiDiscover(context.getCurrentProtocol());
//			logger.info("Write " + datas);
			context.getBluetoothPlugin().write(request, discoverCB);
		}
	};
	
	private Callback<Object, String> discoverCB = new Callback<Object, String>() {
		@Override
		public void onFailure(String reason) {
			logger.warning("Write failed " + reason);
			Window.alert("Write failed " + reason);
		}
		@Override
		public void onSuccess(Object result) {
			logger.info("Write success: " + result);
		}
	};
	
	private WiFiDiscoverHandler wifiFoundH = new WiFiDiscoverHandler() {
		@Override
		public void onWiFiDiscover(WiFiDiscoverEvent event) {
			logger.info("Wifi discover event received");
			List<WifiNetwork> wifis = WifiNetwork.toWifiNetwork(event.getMessage(), event.getProtocolVersion());
			logger.info(wifis.toString());
			wifiListPopup.setDevices(wifis);
			addToPopupSlot(wifiListPopup, true);
		}
	};
	
	// Model
	private DirtyModelHandler dirtyModelH = new DirtyModelHandler() {
		@Override
		public void onDirtyModel(DirtyModelEvent event) {
			getView().setFormActionEnabled(event.isModelDirty());
		}
	};

}
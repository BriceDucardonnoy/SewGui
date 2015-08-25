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
		void setDhcp(final boolean isDhcp);
		void setWifi(final boolean isWifi);
		void setIp(final String ip);
		void setNetmask(final String netmask);
		void setGateway(final String gateway);
		void setPrimaryDNS(final String dns1);
		void setSecondaryDNS(final String dns2);
		void setEssid(String value);
		void setPwd(String value);
		void setWidgetEnabled(boolean b);
		Button getDiscoverWiFiBtn();
		Button getCancelBtn();
		Button getSubmitBtn();
		List<IFormManaged<?>> getFormWidgets();
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
		register(getView().getFormWidgets());
	}

	protected void onReveal() {
		super.onReveal();
		// List widget registered
		for(IFormManaged<?> widget : formWidgets) {
			Log.info("Have registered " + widget.getName());
			logger.info("Have registered " + widget.getName());
//			handlers.add(widget.asWidget().addDomHandler(Utils.leaveEditableWidgetHandler, BlurEvent.getType()));
		}
		// Subscribe IDs to DataModel and add handlers
		context.getModel().subscribe(Group.NETWORK);
		handlers.add(getEventBus().addHandler(DataModelEvent.getSerializedType(), dmHandler));
		if(!context.isPhoneGapAvailable() || !context.isConnected2Device()) {
			logger.info("Not connected => do nothing");
			Log.info("Not connected => do nothing");
			getView().setWidgetEnabled(false);
			return;
		}
		// Ask network data to remote unit
		byte []request = RequestHelper.getNetwork(context.getCurrentProtocol());
		context.getBluetoothPlugin().write(request, getNetworkCB);
		getView().getCancelBtn().setEnabled(false);
		getView().getSubmitBtn().setEnabled(false);
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
		for(IFormManaged<? extends Comparable<?>> widget : widgets) {
			formWidgets.add(widget);
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
					byte []request = RequestHelper.getNetwork(context.getCurrentProtocol());
					context.getBluetoothPlugin().write(request, getNetworkCB);
				}
				else {
					getView().setWidgetEnabled(false);
				}
				return;
			}
			// TODO BDY: Store local password
			// TODO BDY: Create a system similar of activatePage which (dis)abled the widget depending of the status instead of write manually a "setWidgetEnabled"
			getView().setDhcp((Boolean) context.getModel().getValue(DataModel.IS_DHCP));
			String essid = (String) context.getModel().getValue(DataModel.WiFi_ESSID);
			getView().setWifi(essid != null && !essid.isEmpty());
			getView().setIp((String) context.getModel().getValue(DataModel.IP));
			getView().setNetmask((String) context.getModel().getValue(DataModel.NM));
			getView().setGateway((String) context.getModel().getValue(DataModel.GW));
			getView().setPrimaryDNS((String) context.getModel().getValue(DataModel.PDNS));
			getView().setSecondaryDNS((String) context.getModel().getValue(DataModel.SDNS));
			getView().setEssid((String) context.getModel().getValue(DataModel.WiFi_ESSID));
			getView().setPwd((String) context.getModel().getValue(DataModel.WiFi_PWD));
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

}
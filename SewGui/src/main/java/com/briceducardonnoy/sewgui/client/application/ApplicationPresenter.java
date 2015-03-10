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

import java.util.List;
import java.util.logging.Logger;

import org.gwtbootstrap3.client.ui.Button;

import com.allen_sauer.gwt.log.client.Log;
import com.briceducardonnoy.sewgui.client.application.context.ApplicationContext;
import com.briceducardonnoy.sewgui.client.application.windows.BluetoothListPopupPresenter;
import com.briceducardonnoy.sewgui.client.customCallbacks.ListCallback;
import com.briceducardonnoy.sewgui.client.events.BTDeviceSelectedEvent;
import com.briceducardonnoy.sewgui.client.events.BTDeviceSelectedEvent.BTDeviceSelectedHandler;
import com.briceducardonnoy.sewgui.client.lang.Translate;
import com.briceducardonnoy.sewgui.client.wrappers.BluetoothSerialImpl;
import com.briceducardonnoy.sewgui.client.wrappers.models.BtEntity;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.gwtphonegap.client.PhoneGap;
import com.googlecode.gwtphonegap.client.PhoneGapAvailableEvent;
import com.googlecode.gwtphonegap.client.PhoneGapAvailableHandler;
import com.googlecode.gwtphonegap.client.PhoneGapTimeoutEvent;
import com.googlecode.gwtphonegap.client.PhoneGapTimeoutHandler;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

public class ApplicationPresenter extends Presenter<ApplicationPresenter.MyView, ApplicationPresenter.MyProxy> {
	interface MyView extends View {
		Button getDisconnect();
		Button getWrite();
		Button getConnect2device();
	}

	private static Logger logger = Logger.getLogger("SewGui");

	@Inject ApplicationContext context;
	@Inject BluetoothListPopupPresenter btListPres;
	private PhoneGap phoneGap;
	private Translate translate = GWT.create(Translate.class);
	private int count = 2;
	private String deviceId;
	private boolean need2connect = false;
	
	@ContentSlot
	public static final Type<RevealContentHandler<?>> SLOT_SetMainContent = new Type<>();

	@ProxyStandard
	interface MyProxy extends Proxy<ApplicationPresenter> {
	}
	
	@Inject
	ApplicationPresenter(EventBus eventBus, MyView view, MyProxy proxy, ApplicationContext ctx) {
		super(eventBus, view, proxy, RevealType.Root);
		context = ctx;
		phoneGap = context.getPhoneGap();
	}
	
	@Override
	protected void onBind() {
		super.onBind();
		// PhoneGap initialization
		registerHandler(phoneGap.addHandler(new PhoneGapAvailableHandler() {
			@Override
			public void onPhoneGapAvailable(PhoneGapAvailableEvent event) {
				//start your app - phonegap is ready
				Log.info("PhoneGap available");
				logger.info("PhoneGap is available");
				context.setPhoneGapAvailable(true);
				BluetoothSerialImpl btImpl = new BluetoothSerialImpl();
				btImpl.initialize();
				context.setBlutoothPlugin(btImpl);
				phoneGap.loadPlugin("bluetoothSerialImpl", btImpl);
			}
		}));
		registerHandler(phoneGap.addHandler(new PhoneGapTimeoutHandler() {
			@Override
			public void onPhoneGapTimeout(PhoneGapTimeoutEvent event) {
				//can not start phonegap - something is for with your setup
				Log.error("PhoneGap unavailable");
				context.setPhoneGapAvailable(false);
			}
		}));
		phoneGap.initializePhoneGap();
		// Connect to device
		registerHandler(getView().getConnect2device().addClickHandler(connect2H));
		// Device selected
		registerHandler(getEventBus().addHandler(BTDeviceSelectedEvent.getType(), deviceSelectedHandler));
		// Disonnect insecure
		registerHandler(getView().getDisconnect().addClickHandler(disconnectH));
		// Write
		registerHandler(getView().getWrite().addClickHandler(writeH));
	}
	
	/*
	 * Handlers and callback
	 */
	// Connect 2 device
	private ClickHandler connect2H = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if(!context.isPhoneGapAvailable()) {
				logger.warning("PhoneGap isn't available => do nothing");
				Log.info("PhoneGap isn't available => do nothing");
				Window.alert(translate.PGUnavailable());
				return;
			}
			context.getBluetoothPlugin().isEnabled(isEnabledCB);
		}
	};
	// Is enabled
	private Callback<Boolean, String> isEnabledCB = new Callback<Boolean, String>() {
		@Override
		public void onSuccess(Boolean result) {
//			logger.info("Type of result is " + result.getClass().getSimpleName());
			if(result == null) {
				logger.info(translate.BTNullMsg());
				Window.alert(translate.BTNullMsg());
			}
			else if(result.booleanValue()) {
				logger.info("Bluetooth is activated");
				context.getBluetoothPlugin().list(listCB);
			}
			else {
				Window.alert(translate.BTInactiveMsg());
				logger.info(translate.BTInactiveMsg());
			}
		}
		@Override
		public void onFailure(String reason) {
			Log.error("Failure reason is " + reason);
			logger.severe("Failure reason is " + reason);
			Window.alert("No response from device: " + reason);
		}
	};
	// List
	private ListCallback listCB = new ListCallback() {
		@Override
		public void failure(String reason) {
			logger.severe("Failed to list: " + reason);
			Window.alert("Failed to list: " + reason);
		}
		@Override
		public void success(List<BtEntity> result) {
			logger.info(translate.ListOfRecorededDevices() + ": " + result.toString());
//			Window.alert(translate.ListOfRecorededDevices() + ": " + result.toString());
			btListPres.setDevices(result);
			addToPopupSlot(btListPres, true);
		}
	};
	// BlueTooth device selected
	private BTDeviceSelectedHandler deviceSelectedHandler = new BTDeviceSelectedHandler() {
		@Override
		public void onBTDeviceSelected(BTDeviceSelectedEvent event) {
			deviceId = event.getDeviceId();
			logger.info("Event id received: " + deviceId);
			if(deviceId == null || deviceId.isEmpty()) {
				deviceId = "";
				return;
			}
			// Test if phone is already connected and close connection if yes
			// TESTME BDY: maybe too extreme if phone is connected to another device
			context.getBluetoothPlugin().isConnected(connectedCB);
		}
	};
	// Is connected
	private Callback<Boolean, Boolean> connectedCB = new Callback<Boolean, Boolean>() {
		@Override
		public void onFailure(Boolean reason) {
			// Connect
			context.getBluetoothPlugin().connect(deviceId, false, connectCB);
		}
		@Override
		public void onSuccess(Boolean result) {
			// Disconnect and connect
			need2connect = true;
			disconnectAndConnect();
		}
	};
	
	private void disconnectAndConnect() {
		logger.info("Disconnect");
		context.getBluetoothPlugin().write("EOC\r\n", writeDisconnectCB);
	}
	
	private Callback<Object, String> writeDisconnectCB = new Callback<Object, String>() {
		@Override
		public void onFailure(String reason) {
			logger.info("Write failed " + reason);
			Window.alert("Write disconnection instruction failed: " + reason);
		}

		@Override
		public void onSuccess(Object result) {
			logger.info("Write disconnection success: " + result);
			context.getBluetoothPlugin().disconnect(disconnectBeforeConnectCB);
		}
	};
	
	private Callback<String, String> disconnectBeforeConnectCB = new Callback<String, String>() {
		@Override
		public void onFailure(String reason) {
			Window.alert("Disonnect failed: " + reason);
		}
		@Override
		public void onSuccess(String result) {
			logger.info("Disonnect success type is " + result.getClass().getSimpleName());
			logger.info("Disonnect success: " + result + ". Now connect to " + deviceId);
			context.getBluetoothPlugin().unsubscribe(unsubscribeCB);
		}
	};
	// Connect
	private Callback<String, String> connectCB = new Callback<String, String>() {
		@Override
		public void onFailure(String reason) {
			Window.alert("Connect failed: " + reason);
		}
		@Override
		public void onSuccess(String result) {
//			logger.info("Connect success type is " + result.getClass().getSimpleName());
			logger.info("Connect success: " + result);
			Window.alert("Connection established: " + result);
			context.getBluetoothPlugin().subscribe("\r\n", subscribeCB);
		}
	};
	// Disconnect
	private ClickHandler disconnectH = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			logger.info("Disconnect from " + deviceId);
			context.getBluetoothPlugin().write("EOC\r\n", writeDisconnectCB);
		}
	};
	// Subscribe
	private Callback<String, String> subscribeCB = new Callback<String, String>() {
		@Override
		public void onFailure(String reason) {
			logger.info("Subscribe failed " + reason);
			Window.alert("Subscribe failed " + reason);
		}

		@Override
		public void onSuccess(String result) {
			logger.info("Subscribe success: " + result);
		}
	};
	// Unsubscribe
	private Callback<Object, String> unsubscribeCB = new Callback<Object, String>() {
		@Override
		public void onFailure(String reason) {
			logger.info("Unsubscribe failed " + reason);
			Window.alert("Unsubscribe failed " + reason);
		}

		@Override
		public void onSuccess(Object result) {
			logger.info("Unsubscribe success: " + result);
			if(need2connect) {
				context.getBluetoothPlugin().connect(deviceId, false, connectCB);
				need2connect = false;
			}
			else {
				Window.alert("Disconnect success: " + result);
			}
		}
	};
	// Write
	private ClickHandler writeH = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			String datas = count <= 0 ? "EOC\r\n" : "Il reste " + (count--) + " écritures\r\n";
			logger.info("Write " + datas);
			context.getBluetoothPlugin().write(datas, writeCB);
		}
	};
	private Callback<Object, String> writeCB = new Callback<Object, String>() {
		@Override
		public void onFailure(String reason) {
			logger.info("Write failed " + reason);
			Window.alert("Write failed " + reason);
		}

		@Override
		public void onSuccess(Object result) {
			logger.info("Write success: " + result);
			Window.alert("Write success: " + result);
		}
	};
	/*
	 * cordova create sewPhone com.briceducardonnoy.sewPhone SewPhone
cd sewPhone
cordova platforms add android
cordova build
cordova emulate android
Le répertoire www contient le index.html utilisé par l'émulateur : 
	mettre les script avec les meta SDM pour faire du dev, et mettre le index.html du projet GWT pour tester en mode normal
Pousser l'apk généré par l'émulateur permet aussi de tester soit en mode SDM si le périphérique est relié au réseau et le SDM lancé, soit de tester la version en mode normal.
	 */
}
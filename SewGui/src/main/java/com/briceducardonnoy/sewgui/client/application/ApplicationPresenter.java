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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.gwtbootstrap3.client.ui.Button;

import com.allen_sauer.gwt.log.client.Log;
import com.briceducardonnoy.sewgui.client.application.context.ApplicationContext;
import com.briceducardonnoy.sewgui.client.application.exceptions.IncorrectFrameException;
import com.briceducardonnoy.sewgui.client.application.protocol.RequestHelper;
import com.briceducardonnoy.sewgui.client.application.protocol.models.WifiNetwork;
import com.briceducardonnoy.sewgui.client.application.windows.entitylistpopup.EntityListPopupPresenter;
import com.briceducardonnoy.sewgui.client.customCallbacks.ListCallback;
import com.briceducardonnoy.sewgui.client.events.SewEntitySelectedEvent;
import com.briceducardonnoy.sewgui.client.events.SewEntitySelectedEvent.BTDeviceSelectedHandler;
import com.briceducardonnoy.sewgui.client.lang.Translate;
import com.briceducardonnoy.sewgui.client.wrappers.BluetoothSerialImpl;
import com.briceducardonnoy.sewgui.client.wrappers.models.BtEntity;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.typedarrays.client.Int8ArrayNative;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
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
		Button getDiscoverWiFi();
		Button getConnect2device();
		Button getTestBtn();
	}

	private static Logger logger = Logger.getLogger("SewGui");

	@Inject ApplicationContext context;
	@Inject EntityListPopupPresenter<BtEntity> btListPres;
	private PhoneGap phoneGap;
	private Translate translate = GWT.create(Translate.class);
//	private int count = 2;
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
		registerHandler(getEventBus().addHandler(SewEntitySelectedEvent.getBT_Type(), deviceSelectedHandler));
		// Disonnect insecure
		registerHandler(getView().getDisconnect().addClickHandler(disconnectH));
		// Write
		registerHandler(getView().getDiscoverWiFi().addClickHandler(discoverH));
		// Test
		registerHandler(getView().getTestBtn().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
//				List<BtEntity> bts = new ArrayList<>();
//				bts.add(new BtEntity("Dev1", 42, "00:11:22:33:44:55", "BDY1"));
//				bts.add(new BtEntity("Dev2", 43, "00:11:22:33:44:56", "BDY2"));
//				bts.add(new BtEntity("Dev3", 44, "00:11:22:33:44:57", "BDY3"));
//				bts.add(new BtEntity("Dev4", 45, "00:11:22:33:44:58", "BDY4"));
				List<WifiNetwork> wns = new ArrayList<>();
				wns.add(new WifiNetwork("Wifi A", (short)100, (short)0, (short)90, true));
				wns.add(new WifiNetwork("Wifi B", (short)80, (short)20, (short)30, true));
				wns.add(new WifiNetwork("Wifi C", (short)60, (short)40, (short)70, true));
				wns.add(new WifiNetwork("Wifi D", (short)40, (short)50, (short)40, false));
				wns.add(new WifiNetwork("Wifi E", (short)20, (short)50, (short)40, false));
				wns.add(new WifiNetwork("Wifi F", (short)10, (short)50, (short)40, false));
				wifiListPres.setDevices(wns);
				addToPopupSlot(wifiListPres, true);
			}
		}));
	}
	@Inject EntityListPopupPresenter<WifiNetwork> wifiListPres;
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
		public void onSewEntitySelected(SewEntitySelectedEvent event) {
			deviceId = event.getDevice();
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
			context.setConnected2Device(false);
			context.getBluetoothPlugin().unsubscribe(unsubscribeRawCB);
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
			context.setConnected2Device(true);
			// TODO BDY: update a led to notify the user he is connected
//			Window.alert("Connection established: " + result);
//			context.getBluetoothPlugin().subscribe("\r\n", subscribeCB);
//			context.getBluetoothPlugin().subscribe(String.valueOf(Character.toChars(255)), subscribeCB);// 0xFF
			context.getBluetoothPlugin().subscribeRawData(subscribeRawCB);
			// Ask for protocol
			byte []request = RequestHelper.getVersion();
			context.getBluetoothPlugin().write(request, versionCB);
		}
	};
	private Callback<Object, String> versionCB = new Callback<Object, String>() {
		@Override
		public void onFailure(String reason) {
			logger.info("Version failed " + reason);
			Window.alert("Version failed " + reason);
		}

		@Override
		public void onSuccess(Object result) {
			logger.info("Version success: " + result);
//			Window.alert("Version success: " + result);
		}
	};
	// Disconnect
	private ClickHandler disconnectH = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			logger.info("Disconnect from " + deviceId);
			context.getBluetoothPlugin().write("EOC\r\n", writeDisconnectCB);// TODO BDY: update it
		}
	};
	// Subscribe
	private Callback<JavaScriptObject, String> subscribeRawCB = new Callback<JavaScriptObject, String>() {
		@Override
		public void onFailure(String reason) {
			logger.info("Subscribe failed " + reason);
			Window.alert("Subscribe failed " + reason);
		}

		@Override
		public void onSuccess(JavaScriptObject result) {
			logger.info("Subscribe success type is " + result.getClass().getSimpleName() + "::" + result.toString());
			Int8ArrayNative answer = Int8ArrayNative.create((ArrayBuffer)result.cast());
			logger.info("Subscribe success (sz=" + answer.length() + "bytes): ");
			// Debug info
			String ans = "";
			for(int i = 0 ; i < answer.byteLength() ; i++) {
				ans += "0x" + Integer.toHexString(answer.get(i) & 0xFF) + " ";
			}
			logger.info(ans);
			//
			if(answer.length() >= 4 && (answer.get(0)&0xFF) == 0xFE && answer.get(1) == 0 && (answer.get(3)&0xFF) == 0xFF) {
				// 4 is the size of the smallest command (protocol ask)
				// This is the special request to get protocol version
				context.setDeviceProtocol(answer.get(2) & 0xFF);
				logger.info("Device protocol version is " + context.getDeviceProtocol());
				return;
			}
			try {
				RequestHelper.parseResponse(answer, getEventBus());
			} catch (IncorrectFrameException e) {
				logger.severe("Answer can't be proccessed: " + e.getMessage());
				Window.alert("An error occured in the communication with the device: " + e.getMessage());
				e.printStackTrace();
			}
		}
	};
	// Unsubscribe
	private Callback<Object, String> unsubscribeRawCB = new Callback<Object, String>() {
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
			context.getBluetoothPlugin().write(request, writeCB);
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
//			Window.alert("Write success: " + result);
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
package com.briceducardonnoy.sewgui.client.application;

import java.util.logging.Logger;

import com.allen_sauer.gwt.log.client.Log;
import com.briceducardonnoy.sewgui.client.lang.Translate;
import com.briceducardonnoy.sewgui.client.wrappers.BluetoothSerialImpl;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
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
		Button getBTBtn();
		Button getListBtn();
	}

	@Inject PhoneGap phoneGap;
	private Translate translate = GWT.create(Translate.class);
	private BluetoothSerialImpl btImpl;
	private Logger logger;
	private boolean isPhoneGapAvailable;
	
	@ContentSlot
	public static final Type<RevealContentHandler<?>> SLOT_SetMainContent = new Type<>();

	@ProxyStandard
	interface MyProxy extends Proxy<ApplicationPresenter> {
	}
	
	@Inject
	ApplicationPresenter(EventBus eventBus, MyView view, MyProxy proxy, PhoneGap pg) {
		super(eventBus, view, proxy, RevealType.Root);
		phoneGap = pg;
		logger = Logger.getLogger("SewGui");
//		phoneGap = GWT.create(PhoneGap.class);
		phoneGap.getLog().setRemoteLogServiceUrl("http://192.168.1.46:8080/gwt-log");
	}
	
	@Override
	protected void onBind() {
		super.onBind();
		// PhoneGap init
		registerHandler(phoneGap.addHandler(new PhoneGapAvailableHandler() {
			@Override
			public void onPhoneGapAvailable(PhoneGapAvailableEvent event) {
				//start your app - phonegap is ready
				Log.info("PhoneGap available");
				logger.info("PhoneGap is available");
				isPhoneGapAvailable = true;
				btImpl = new BluetoothSerialImpl();
				btImpl.initialize();
				phoneGap.loadPlugin("bluetoothSerialImpl", btImpl);
			}
		}));
		registerHandler(phoneGap.addHandler(new PhoneGapTimeoutHandler() {
			@Override
			public void onPhoneGapTimeout(PhoneGapTimeoutEvent event) {
				//can not start phonegap - something is for with your setup
				Log.error("PhoneGap unavailable");
				isPhoneGapAvailable = false;
			}
		}));
		phoneGap.initializePhoneGap();
		// Is enabled
		registerHandler(getView().getBTBtn().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(!isPhoneGapAvailable) {
					logger.warning("PhoneGap isn't available => do nothing");
					Log.info("PhoneGap isn't available => do nothing");
					return;
				}
				btImpl.isEnabled(new Callback<Boolean, String>() {
					@Override
					public void onSuccess(Boolean result) {
						Log.info("Success result is " + result);
						logger.info("Success result is " + result);
//						logger.info("Type of result is " + result.getClass().getSimpleName());
						if(result == null) {
							Window.alert("No response, please reload the application");
						}
						else if(result.booleanValue()) {
							Window.alert("Bluetooth is activated");
						}
						else {
							Window.alert(translate.BTInactiveMsg());
						}
					}
					@Override
					public void onFailure(String reason) {
						Log.error("Failure reason is " + reason);
						logger.severe("Failure reason is " + reason);
						Window.alert("No response from device: " + reason);
					}
				});
			}
		}));
		// List
		registerHandler(getView().getListBtn().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				logger.info("List requested");
				btImpl.list(new Callback<JSONArray, String>() {
					@Override
					public void onFailure(String reason) {
						logger.severe("Failed to list: " + reason);
						Window.alert("Failed to list: " + reason);
					}
					@Override
					public void onSuccess(JSONArray result) {
						logger.info("List result is " + result.toString());
						Window.alert("List result is " + result.toString());
					}
				});
			}
		}));
	}
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
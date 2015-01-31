package com.briceducardonnoy.sewgui.client.application;

import java.util.logging.Logger;

import com.allen_sauer.gwt.log.client.Log;
import com.briceducardonnoy.sewgui.client.wrappers.BluetoothSerialImpl;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
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
	}

	private PhoneGap phoneGap;
	private Logger logger;
	private boolean isPhoneGapAvailable;
	
	@ContentSlot
	public static final Type<RevealContentHandler<?>> SLOT_SetMainContent = new Type<>();

	@ProxyStandard
	interface MyProxy extends Proxy<ApplicationPresenter> {
	}
	// TODO BDY: set phonegap as an injectable object
	@Inject
	ApplicationPresenter(EventBus eventBus, MyView view, MyProxy proxy) {
		super(eventBus, view, proxy, RevealType.Root);
		logger = Logger.getLogger("SewGui");
		phoneGap = GWT.create(PhoneGap.class);
		phoneGap.getLog().setRemoteLogServiceUrl("http://192.168.1.46:8080/gwt-log");
		phoneGap.addHandler(new PhoneGapAvailableHandler() {
			@Override
			public void onPhoneGapAvailable(PhoneGapAvailableEvent event) {
				//start your app - phonegap is ready
				Log.info("PhoneGap available");
				logger.info("PhoneGap is available");
				isPhoneGapAvailable = true;
			}
		});
		phoneGap.addHandler(new PhoneGapTimeoutHandler() {
			@Override
			public void onPhoneGapTimeout(PhoneGapTimeoutEvent event) {
				//can not start phonegap - something is for with your setup
				Log.error("PhoneGap unavailable");
				isPhoneGapAvailable = false;
			}
		});
		phoneGap.initializePhoneGap();
	}
	
	@Override
	protected void onBind() {
		super.onBind();
		registerHandler(getView().getBTBtn().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				BluetoothSerialImpl btImpl = new BluetoothSerialImpl();
				btImpl.initialize();
				phoneGap.loadPlugin("bluetoothSerialImpl", btImpl);
				btImpl.isEnabled(new Callback<Boolean, String>() {
					@Override
					public void onSuccess(Boolean result) {
						Log.info("Success result is " + result);
						logger.info("Success result is " + result);
					}
					
					@Override
					public void onFailure(String reason) {
						Log.error("Failure reason is " + reason);
						logger.severe("Failure reason is " + reason);
					}
				});
			}
		}));
	}
}
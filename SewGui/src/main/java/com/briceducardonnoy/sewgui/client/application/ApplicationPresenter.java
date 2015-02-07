package com.briceducardonnoy.sewgui.client.application;

import java.util.List;
import java.util.logging.Logger;

import com.allen_sauer.gwt.log.client.Log;
import com.briceducardonnoy.sewgui.client.application.context.ApplicationContext;
import com.briceducardonnoy.sewgui.client.customCallbacks.ListCallback;
import com.briceducardonnoy.sewgui.client.lang.Translate;
import com.briceducardonnoy.sewgui.client.wrappers.BluetoothSerialImpl;
import com.briceducardonnoy.sewgui.client.wrappers.models.BtEntity;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
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
		Button getConnected();
		Button getConnect();
		Button getDisconnect();
		Button getSubscribe();
		Button getUnsubscribe();
		Button getWrite();
		Button getConnect2device();
	}

	private static Logger logger = Logger.getLogger("SewGui");

//	@Inject PhoneGap phoneGap;
	@Inject ApplicationContext context;
	private PhoneGap phoneGap;
	private Translate translate = GWT.create(Translate.class);
	private int count = 2;
	
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
		// Is enabled
		registerHandler(getView().getBTBtn().addClickHandler(isEnabledH));
		// List
		registerHandler(getView().getListBtn().addClickHandler(listH));
		// TODO BDY: combine connect and disconnect with isConnected
		// Is connected
		registerHandler(getView().getConnected().addClickHandler(connectedH));
		// Connect insecure
		registerHandler(getView().getConnect().addClickHandler(connectH));
		// Disonnect insecure
		registerHandler(getView().getDisconnect().addClickHandler(disconnectH));
		// Subscribe
		registerHandler(getView().getSubscribe().addClickHandler(subscribeH));
		// Unsubscribe
		registerHandler(getView().getUnsubscribe().addClickHandler(unsubscribeH));
		// Write
		registerHandler(getView().getWrite().addClickHandler(writeH));
		// Connect to device
		registerHandler(getView().getConnect2device().addClickHandler(connect2H));
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
	private ClickHandler isEnabledH = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if(!context.isPhoneGapAvailable()) {
				logger.warning("PhoneGap isn't available => do nothing");
				Log.info("PhoneGap isn't available => do nothing");
				return;
			}
			context.getBluetoothPlugin().isEnabled(isEnabledCB);
		}
	};
	private Callback<Boolean, String> isEnabledCB = new Callback<Boolean, String>() {
		@Override
		public void onSuccess(Boolean result) {
			Log.info("Success result is " + result);
			logger.info("Success result is " + result);
//			logger.info("Type of result is " + result.getClass().getSimpleName());
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
	};
	// List
	private ClickHandler listH = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			logger.info("List requested");
			context.getBluetoothPlugin().list(listCB);
		}
	};
	private ListCallback listCB = new ListCallback() {
		@Override
		public void failure(String reason) {
			logger.severe("Failed to list: " + reason);
			Window.alert("Failed to list: " + reason);
		}
		@Override
		public void success(List<BtEntity> result) {
			logger.info("List result is " + result.toString());
			Window.alert("List result is " + result.toString());
		}
	};
	// Is connected
	private ClickHandler connectedH = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			logger.info("Is connected to a device?");
			context.getBluetoothPlugin().isConnected(connectedCB);
		}
	};
	private Callback<Boolean, Boolean> connectedCB = new Callback<Boolean, Boolean>() {
		@Override
		public void onFailure(Boolean reason) {
			Window.alert("Not connected");
		}
		@Override
		public void onSuccess(Boolean result) {
			Window.alert("Connected");
		}
	};
	// Connect
	private ClickHandler connectH = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			logger.info("Connect to 00:1A:7D:DA:71:13");
			context.getBluetoothPlugin().connect("00:1A:7D:DA:71:13", false, connectCB);
		}
	};
	private Callback<String, String> connectCB = new Callback<String, String>() {
		@Override
		public void onFailure(String reason) {
			Window.alert("Connect failed: " + reason);
		}
		@Override
		public void onSuccess(String result) {
			logger.info("Connect success type is " + result.getClass().getSimpleName());
			Window.alert("Connect success: " + result);
		}
	};
	// Disconnect
	private ClickHandler disconnectH = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			logger.info("Disconnect from 00:1A:7D:DA:71:13");
			context.getBluetoothPlugin().disconnect(disconnectCB);
		}
	};
	private Callback<String, String> disconnectCB = new Callback<String, String>() {
		@Override
		public void onFailure(String reason) {
			Window.alert("Disonnect failed: " + reason);
		}
		@Override
		public void onSuccess(String result) {
			logger.info("Disonnect success type is " + result.getClass().getSimpleName());
			Window.alert("Disonnect success: " + result);
		}
	};
	// Subscribe
	private ClickHandler subscribeH = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			logger.info("Subscribe");
			context.getBluetoothPlugin().subscribe("\r\n", subscribeCB);
		}
	};
	private Callback<String, String> subscribeCB = new Callback<String, String>() {
		@Override
		public void onFailure(String reason) {
			logger.info("Subscribe failed " + reason);
			Window.alert("Subscribe failed " + reason);
		}

		@Override
		public void onSuccess(String result) {
			logger.info("Subscribe success: " + result);
			Window.alert("Subscribe success: " + result);
		}
	};
	// Unsubscribe
	private ClickHandler unsubscribeH = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			logger.info("Unsubscribe");
			context.getBluetoothPlugin().unsubscribe(unsubscribeCB);
		}
	};
	private Callback<Object, String> unsubscribeCB = new Callback<Object, String>() {
		@Override
		public void onFailure(String reason) {
			logger.info("Unsubscribe failed " + reason);
			Window.alert("Unsubscribe failed " + reason);
		}

		@Override
		public void onSuccess(Object result) {
			logger.info("Unsubscribe success: " + result);
			Window.alert("Unsubscribe success: " + result);
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
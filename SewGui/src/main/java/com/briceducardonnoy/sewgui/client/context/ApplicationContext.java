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
package com.briceducardonnoy.sewgui.client.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.briceducardonnoy.sewgui.client.application.protocol.RequestHelper;
import com.briceducardonnoy.sewgui.client.events.BelongToThisFormManagerEvent;
import com.briceducardonnoy.sewgui.client.events.BelongToThisFormManagerEvent.BelongToThisFormManagerHandler;
import com.briceducardonnoy.sewgui.client.model.DataModel;
import com.briceducardonnoy.sewgui.client.model.IFormManaged;
import com.briceducardonnoy.sewgui.client.wrappers.BluetoothSerialImpl;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.gwtphonegap.client.PhoneGap;

@Singleton
public class ApplicationContext {
	
	private static Logger logger = Logger.getLogger("SewGui");
	private static DataModel model;
	private static EventBus eventBus;

	private PhoneGap phoneGap;
	private BluetoothSerialImpl bluetoothSerial;
	private int deviceProtocol = 0;
	/**
	 * Mapping between a {@link IFormManager} and its list of registered IDs to populate
	 */
	private HashMap<String, List<IFormManaged<?>>> formRegisteredIds;
	
	@Inject
	ApplicationContext(final PhoneGap pg, final DataModel model, final EventBus eventBus) {
		logger.info("CREATE ApplicationContext");
		phoneGap = pg;// TODO BDY: if network LAN ok, color the LAN icon (green if ping www.google.com, orange if only 8.8.8.8). Same for WiFi (need ping in embed).
		ApplicationContext.model = model;
		ApplicationContext.eventBus = eventBus;
		
		formRegisteredIds = new HashMap<>();
		
		phoneGap.getLog().setRemoteLogServiceUrl("http://192.168.1.46:8080/gwt-log");
		model.updateValue(DataModel.IS_PHONEGAP_AVAILABLE, false, false);
		model.updateValue(DataModel.IS_BLUETOOTH_CONNECTED, false, false);
		
		eventBus.addHandler(BelongToThisFormManagerEvent.getType(), registerWidgetH);
	}
	
//	public static ApplicationContext getInstance() {
//		if(instance == null) {
//			instance = new ApplicationContext();
//		}
//		return instance;
//	}
	
	public DataModel getModel() {
		return model;
	}

	public PhoneGap getPhoneGap() {
		return phoneGap;
	}
	
	public static Integer getIdFromAttributeModelName(final String modelName) {
		return model.getIdFromAttributeModelName(modelName);
	}
	
	public List<IFormManaged<?>> getFormManagedWidgetFromFormName(final String formManagerName) {
		return formRegisteredIds.get(formManagerName);
	}
	
	/**
	 * Convenient method to allow all classes from the app the get
	 * access to the eventBus
	 * @return The application EventBus
	 */
	public static EventBus getEventBus() {
		return eventBus;
	}
	
	public boolean isPhoneGapAvailable() {
		return (boolean) model.getValue(DataModel.IS_PHONEGAP_AVAILABLE);
	}

	public void setPhoneGapAvailable(final boolean isPhoneGapAvailable) {
		model.updateValue(DataModel.IS_PHONEGAP_AVAILABLE, isPhoneGapAvailable);
	}

	public BluetoothSerialImpl getBluetoothPlugin() {
		return bluetoothSerial;
	}

	public void setBlutoothPlugin(final BluetoothSerialImpl btImpl) {
		this.bluetoothSerial = btImpl;
	}
	
	public int getCurrentProtocol() {
		return RequestHelper.VERSION;
	}

	public final int getDeviceProtocol() {
		return deviceProtocol;
	}

	public final void setDeviceProtocol(int deviceProtocol) {
		this.deviceProtocol = deviceProtocol;
	}
	
	public final boolean isConnected2Device() {
		return (boolean) model.getValue(DataModel.IS_BLUETOOTH_CONNECTED); 
	}

	public final void setConnected2Device(final boolean isConnected2Device) {
		model.updateValue(DataModel.IS_BLUETOOTH_CONNECTED, isConnected2Device);
	}
	
	/*
	 * Handlers and callbacks
	 */

	private BelongToThisFormManagerHandler registerWidgetH = new BelongToThisFormManagerHandler() {
		@Override
		public void onBelongToThisFormManager(BelongToThisFormManagerEvent event) {
			logger.fine("RECEIVE EVENT for form " + event.getFormManagerName() + " widget " + event.getWidget().getName());
			if(!formRegisteredIds.containsKey(event.getFormManagerName())) {
				formRegisteredIds.put(event.getFormManagerName(), new ArrayList<IFormManaged<?>>());
			}
			if(!formRegisteredIds.get(event.getFormManagerName()).contains(event.getWidget())) {
				formRegisteredIds.get(event.getFormManagerName()).add(event.getWidget());
			}
		}
	};
}

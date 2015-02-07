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
package com.briceducardonnoy.sewgui.client.application.context;

import com.briceducardonnoy.sewgui.client.wrappers.BluetoothSerialImpl;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlecode.gwtphonegap.client.PhoneGap;

@Singleton
public class ApplicationContext {

	@Inject PhoneGap phoneGap;
	
	private boolean isPhoneGapAvailable;
	private BluetoothSerialImpl bluetoothSerial;
	
	@Inject
	ApplicationContext(final PhoneGap pg) {
		phoneGap = pg;
		phoneGap.getLog().setRemoteLogServiceUrl("http://192.168.1.46:8080/gwt-log");
		isPhoneGapAvailable = false;
	}
	
//	public static ApplicationContext getInstance() {
//		if(instance == null) {
//			instance = new ApplicationContext();
//		}
//		return instance;
//	}

	public PhoneGap getPhoneGap() {
		return phoneGap;
	}

	public boolean isPhoneGapAvailable() {
		return isPhoneGapAvailable;
	}

	public void setPhoneGapAvailable(final boolean isPhoneGapAvailable) {
		this.isPhoneGapAvailable = isPhoneGapAvailable;
	}

	public BluetoothSerialImpl getBluetoothPlugin() {
		return bluetoothSerial;
	}

	public void setBlutoothPlugin(final BluetoothSerialImpl btImpl) {
		this.bluetoothSerial = btImpl;
	}
	
}

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
package com.briceducardonnoy.sewgui.client.wrappers;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Callback;

public class BluetoothSerialImpl implements BluetoothPlugin {

	@Override
	public void initialize() {
		Log.info("Initialize BT");
	}

	@Override
	public void isEnabled(Callback<Boolean, String> callback) {
		isEnabledNative(callback);
	}
	
	private native void isEnabledNative(Callback<Boolean, String> callback) /*-{
		var success = $entry(function() {
	        callback.@com.google.gwt.core.client.Callback::onSuccess(Ljava/lang/Object;)(@java.lang.Boolean::valueOf(Z)(true));
    	});
    	var failure = $entry(function() {
    		//callback.@com.google.gwt.core.client.Callback::onSuccess(Ljava/lang/Object;)(@java.lang.Boolean::valueOf(Z)(false));
    		callback.@com.google.gwt.core.client.Callback::onFailure(Ljava/lang/Object;)("Bluetooth is disabled patate");
    	});
		$wnd.cordova.exec(success, failure, "BluetoothSerial", "isEnabled", []);
	}-*/;

}

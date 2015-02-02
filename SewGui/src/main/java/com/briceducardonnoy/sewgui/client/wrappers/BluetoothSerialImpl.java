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
import com.briceducardonnoy.sewgui.client.customCallbacks.ListCallback;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;

public class BluetoothSerialImpl implements BluetoothPlugin {

	@Override
	public void initialize() {
		Log.info("Initialize BT");
	}

	// Is enabled
	@Override
	public void isEnabled(Callback<Boolean, String> callback) {
		isEnabledNative(callback);
	}
	
	private native void isEnabledNative(Callback<Boolean, String> callback) /*-{
		// CallbackContext return a jsonObject, so we need to cast it to the desired type (boolean or string)
		var success = $entry(function() {
	        callback.@com.google.gwt.core.client.Callback::onSuccess(Ljava/lang/Object;)(@java.lang.Boolean::valueOf(Z)(true));
    	});
    	var failure = $entry(function() {
    		callback.@com.google.gwt.core.client.Callback::onSuccess(Ljava/lang/Object;)(@java.lang.Boolean::valueOf(Z)(false));
//    		callback.@com.google.gwt.core.client.Callback::onFailure(Ljava/lang/Object;)("Bluetooth is disabled");
    	});
		$wnd.cordova.exec(success, failure, "BluetoothSerial", "isEnabled", []);
	}-*/;
	
	// List
	@Override
	public void list(ListCallback callback) {
		listNative(callback);
	}
	
	private native void listNative(Callback<JavaScriptObject, String> callback) /*-{
		var success = $entry(function(a) {
	        callback.@com.google.gwt.core.client.Callback::onSuccess(Ljava/lang/Object;)(a);
    	});
    	var failure = $entry(function() {
    		callback.@com.google.gwt.core.client.Callback::onFailure(Ljava/lang/Object;)("An error occured during list request");
    	});
		$wnd.cordova.exec(success, failure, "BluetoothSerial", "list", []);
	}-*/;

	@Override
	public void isConnected(Callback<Boolean, Boolean> callback) {
		isConnectedImpl(callback);
	}
	
	private native void isConnectedImpl(Callback<Boolean, Boolean> callback) /*-{
		// CallbackContext return a jsonObject, so we need to cast it to the desired type (boolean or string)
		var success = $entry(function() {
	        callback.@com.google.gwt.core.client.Callback::onSuccess(Ljava/lang/Object;)(@java.lang.Boolean::valueOf(Z)(true));
    	});
    	var failure = $entry(function() {
    		callback.@com.google.gwt.core.client.Callback::onFailure(Ljava/lang/Object;)(@java.lang.Boolean::valueOf(Z)(false));
    	});
		$wnd.cordova.exec(success, failure, "BluetoothSerial", "isConnected", []);
	}-*/;

}

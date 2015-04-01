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

import com.briceducardonnoy.sewgui.client.customCallbacks.ListCallback;
import com.briceducardonnoy.sewgui.client.wrappers.models.BtEntity;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.typedarrays.client.Int8ArrayNative;
import com.googlecode.gwtphonegap.client.plugins.PhoneGapPlugin;
//TODO BDY: update bluetooth js
/**
 * Interface for Bluetooth communication
 * @see https://github.com/don/BluetoothSerial
 */
public interface BluetoothPlugin extends PhoneGapPlugin {
	/**
	 * Check if Bluetooth is enabled on device
	 * @param callback Invoked when the request is successfull (boolean) or failed (string reason)
	 */
	public void isEnabled(Callback<Boolean, String> callback);
	/**
	 * Lists bonded devices
	 * @param listCallback The callback listing the devices into <code>BtEntity</code> list.
	 */
	public void list(ListCallback listCallback);
	/**
	 * Check weather or not the device is connected by Bluetooth to another device
	 * Calls the success callback when connected to a peer and the failure callback when not connected.
	 * @param callback Success invoked when device connected. Failure invoked when device is NOT connected.
	 * @see BtEntity
	 */
	public void isConnected(Callback<Boolean, Boolean> callback);
	/**
	 * Connects to a Bluetooth device. The callback is long running.<br>
	 * Success will be called when the connection is successful.<br>
	 * Failure is called if the connection fails, or later if the connection disconnects.<br>
	 * An error message is passed to the failure callback
	 * @param mac The mac address or uuid
	 * @param secure Secure mode or note. IOS doesn't support insecure mode
	 * @param callback Invoked when the connection succeed, failed, or is closed after a connection
	 */
	public void connect(String mac, boolean secure, Callback<String, String> callback);
	/**
	 * Disconnect the current connection
	 * @param callback Invoked when disconnect success/failed
	 */
	public void disconnect(Callback<String, String> callback);
	/**
	 * Registers a callback that is called when data is received. 
	 * A delimiter must be specified.<br/>
	 * The callback is called with the data as soon as the delimiter string is read.<br/>
	 * The callback is a long running callback and will exist until unsubscribe is called.
	 * @param delimiter A string delimiter ("\r\n")
	 * @param callback Invoked when data is received AND <code>delimiter</code> is found.
	 */
	public void subscribe(String delimiter, Callback<String, String> callback);
	/**
	 * Removes any notification added by <code>subscribe</code> and kills the callback
	 * @param callback Invoked when the connection is successful or failed
	 */
	public void unsubscribe(Callback<Object, String> callback);
	/**
	 * Registers a callback that is called when data is received.
	 * The callback is called immediately when data is received.<br/> 
	 * The data is sent to callback as an ArrayBuffer.<br/>
	 * The callback is a long running callback and will exist until unsubscribeRawData is called.
	 * @param callback Invoked when data is received. JavaScriptObject is an ArrayBuffer object.
	 * @see Int8ArrayNative
	 */
	public void subscribeRawData(Callback<JavaScriptObject, String> callback);
	/**
	 * Unsubscribe from a subscription.
	 * Function unsubscribeRawData removes any notification added by subscribeRawData and kills the callback.
	 * @param callback
	 */
	public void unsubscribeRawData(Callback<Object, String> callback);
	/**
	 * Removes any data from the receive buffer
	 * @param callback Invoked when the clear is successful or failed
	 */
	public void clearBuffer(Callback<String, String> callback);
	/**
	 * Writes data to the serial port.<br>
	 * Data can be
	 * <li>ArrayBuffer</li>
	 * <li>string</li>
	 * <li>array of integers</li>
	 * <li>Uint8Array</li>
	 * Internally, string, integer array, and Uint8Array are converted to an ArrayBuffer. String conversion assume 8bit characters.
	 * @param data The data to send
	 * @param callback Is invoked when the connection is successful or when an error occured
	 */
	public void write(Object data, Callback<Object, String> callback);
}

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
import com.google.gwt.core.client.Callback;
import com.googlecode.gwtphonegap.client.plugins.PhoneGapPlugin;

public interface BluetoothPlugin extends PhoneGapPlugin {
	public void isEnabled(Callback<Boolean, String> callback);
	public void list(ListCallback listCallback);
	public void isConnected(Callback<Boolean, Boolean> callback);
	public void connect(String mac, boolean secure, Callback<String, String> callback);
	public void disconnect(Callback<String, String> callback);
}

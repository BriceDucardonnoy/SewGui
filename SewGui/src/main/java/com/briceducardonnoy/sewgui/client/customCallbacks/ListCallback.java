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
package com.briceducardonnoy.sewgui.client.customCallbacks;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.briceducardonnoy.sewgui.client.wrappers.models.BtEntity;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;

public abstract class ListCallback implements Callback<JavaScriptObject, String>{
	
	private static Logger logger = Logger.getLogger("SewGui");
	
	@Override
	public void onFailure(String reason) {
		failure(reason);
	}
	
	@Override
	public void onSuccess(JavaScriptObject result) {
		List<BtEntity> ents = new ArrayList<>();
		if(result == null) success(ents);
		JSONArray array = new JSONArray(result);
		for(int i = 0 ; i < array.size() ; i++) {
			ents.add(new BtEntity(array.get(i)));
			logger.info(ents.get(i).toString());
		}
		success(ents);
	}

	public abstract void success(List<BtEntity> result);
	public abstract void failure(String reason);

}

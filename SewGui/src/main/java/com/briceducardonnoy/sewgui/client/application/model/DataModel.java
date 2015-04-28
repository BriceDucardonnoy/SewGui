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
package com.briceducardonnoy.sewgui.client.application.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.briceducardonnoy.sewgui.client.events.DataModelEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

@Singleton
public class DataModel {

//	HashMap<Integer, Object> globalData;
	private HashMap<Integer, Object> registeredData;
	private List<Integer> subscribedIds;
	
	@Inject EventBus eventBus;
	
	@Inject
	DataModel(EventBus eventBus) {
//		globalData = new HashMap<>(10);
		this.eventBus = eventBus;
		registeredData = new HashMap<>();
		subscribedIds = new ArrayList<>();
	}
	
	/**
	 * List of the IDs that we wish to receive a {@link DataModelEvent} where an update appear.<br />
	 * As it's a single user project, no need to know who the the requester
	 * @param ids The list of IDs to listen
	 */
	public void subscribe(List<Integer> ids) {
		for(Integer id : ids) {
			subscribe(id);
		}
	}
	
	/**
	 * ID that we wish to receive a {@link DataModelEvent} where an update appear.<br />
	 * As it's a single user project, no need to know who the the requester
	 * @param id The ID to listen
	 */
	public void subscribe(Integer id) {
		if(!subscribedIds.contains(id)) {
			subscribedIds.add(id);
		}
	}
	
	public void unsubscribe(List<Integer> ids) {
		for(Integer id : ids) {
			unsubscribe(id);
		}
	}
	
	public void unsubscribe(Integer id) {
		subscribedIds.remove(id);
	}
	
	public Object getValue(Integer id) {
		return registeredData.get(id);
	}
	
	public void updateValues(HashMap<Integer, Object> values) {
		updateValues(values, true);
	}
	
	public void updateValues(HashMap<Integer, Object> values, boolean signal) {
		for(Entry<Integer, Object> entry : values.entrySet()) {
			updateValue(entry.getKey(), entry.getValue(), false);// Send one DataModelEvent for all
		}
		eventBus.fireEvent(new DataModelEvent(new ArrayList<Integer>(values.keySet())));
	}
	
	/**
	 * Update the value which key is <code>id</code> with value <code>value</code>
	 * and fire a {@link DataModelEvent}
	 * @param id The ID of the object
	 * @param value Its new value
	 * @return True if ID has been added or updated (eg. different from previous value)
	 */
	public boolean updateValue(Integer id, Object value) {
		return updateValue(id, value, true);
	}
	
	/**
	 * Update the value with id <code>id</code> to <code>value</code> and fire a {@link DataModelEvent} if asked
	 * @param id The ID of the object
	 * @param value Its new value
	 * @param signal True to fire a DataModelEvent
	 * @return True if the new value is different from the old one or if new key has been created
	 */
	public boolean updateValue(Integer id, Object value, boolean signal) {
		if(value == null) return false;
		boolean ret = !value.equals(registeredData.put(id, value));
		if(signal) {
			eventBus.fireEvent(new DataModelEvent(id));
		}
		return ret;
	}
	
	public void notify4AllKeys() {
		eventBus.fireEvent(new DataModelEvent(new ArrayList<Integer>(registeredData.keySet())));
	}
	
	// TODO BDY: create interface that must implement widgets which wish to be notified for datamodel change. Or let all be done by the presenter
	// TODO BDY: load it from json file?
	
	public final static int IS_PHONEGAP_AVAILABLE;
	public final static int IS_BLUETOOTH_CONNECTED;
	static {
		int i = 0;
		IS_PHONEGAP_AVAILABLE = i++;
		IS_BLUETOOTH_CONNECTED = i++;
	}
	
}

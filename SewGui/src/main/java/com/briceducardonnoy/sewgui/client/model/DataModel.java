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
package com.briceducardonnoy.sewgui.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.briceducardonnoy.sewgui.client.events.DataModelEvent;
import com.briceducardonnoy.sewgui.client.events.DataModelEvent.DataModelHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

@Singleton
public class DataModel {

	private static Logger logger = Logger.getLogger("SewGui");
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
		eventBus.addHandler(DataModelEvent.getRawType(), new DataModelHandler() {
			@Override
			public void onDataModelUpdated(DataModelEvent event) {
				logger.info("Get raw data to integrate (sz=" + event.getValues2update().size() + ")");
				updateValues(event.getValues2update());
			}
		});
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
	
	/**
	 * Group of IDs we wish to receive a {@link DataModelEvent} where an update appear.<br />
	 * As it's a single user project, no need to know who the the requester
	 * @param group The group {@link Group}
	 */
	public void subscribe(Group group) {
		switch (group) {
		case NETWORK:
			subscribe(IP);
			subscribe(NM);
			subscribe(GW);
			subscribe(PDNS);
			subscribe(SDNS);
			subscribe(IS_DHCP);
			subscribe(WiFi_ESSID);
			subscribe(WiFi_PWD);
			break;
		default: break;
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
	
	public void unsubscribe(Group group) {
		switch (group) {
		case NETWORK:
			unsubscribe(IP);
			unsubscribe(NM);
			unsubscribe(GW);
			unsubscribe(PDNS);
			unsubscribe(SDNS);
			unsubscribe(IS_DHCP);
			unsubscribe(WiFi_ESSID);
			unsubscribe(WiFi_PWD);
			break;
		default: break;
		}
	}
	
	public Object getValue(Integer id) {
		return registeredData.get(id);
	}
	
	public void updateValues(HashMap<Integer, Object> values) {
		updateValues(values, true);
	}
	// TODO BDY: write JavaDoc
	public void updateValues(HashMap<Integer, Object> values, boolean signal) {
		if(values == null) {
			logger.warning("HashMap of values to update is null. Skip the update process");
			return;
		}
		for(Entry<Integer, Object> entry : values.entrySet()) {
			updateValue(entry.getKey(), entry.getValue(), false);// Send one DataModelEvent for all
		}
		eventBus.fireEvent(new DataModelEvent(getListOfSubscribedIdsFromHM(values)));
	}
	
	private List<Integer> getListOfSubscribedIdsFromHM(HashMap<Integer, Object> values) {
		List<Integer> notify = new ArrayList<>();
		if(values == null || values.isEmpty()) return notify;
		for(Integer key : values.keySet()) {
			if(subscribedIds.contains(key)) {
				notify.add(key);
			}
		}
		return notify;
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
		if(signal && subscribedIds.contains(id)) {
			eventBus.fireEvent(new DataModelEvent(id));
		}
		return ret;
	}
	
	public void notify4AllKeys() {
		eventBus.fireEvent(new DataModelEvent(new ArrayList<Integer>(registeredData.keySet())));
	}
	
	public void notifyAllSubscribedKeys() {
		eventBus.fireEvent(new DataModelEvent(subscribedIds));
	}
	
	// Load it from json file?
	/*
	 * IDs
	 */
	public final static int IS_PHONEGAP_AVAILABLE;
	public final static int IS_BLUETOOTH_CONNECTED;
	public final static int IP;
	public final static int NM;
	public final static int GW;
	public final static int PDNS;
	public final static int SDNS;
	public final static int WiFi_ESSID;
	public final static int WiFi_PWD;
	public final static int IS_DHCP;
	
	static {
		int i = 0;
		IS_PHONEGAP_AVAILABLE = i++;
		IS_BLUETOOTH_CONNECTED = i++;
		IP = i++;
		NM = i++;
		GW = i++;
		PDNS = i++;
		SDNS = i++;
		IS_DHCP = i++;
		WiFi_ESSID = i++;
		WiFi_PWD = i++;
	}
	
	/*
	 * Groups
	 */
	public enum Group {
		NETWORK;
	}
}

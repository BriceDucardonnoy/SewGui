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
import com.briceducardonnoy.sewgui.client.events.DirtyModelEvent;
import com.briceducardonnoy.sewgui.client.events.DirtyWidgetEvent;
import com.briceducardonnoy.sewgui.client.events.DirtyWidgetEvent.DirtyWidgetHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

@Singleton
public class DataModel {

	private static Logger logger = Logger.getLogger("SewGui");
//	HashMap<Integer, Object> globalData;
	/** Map of ID <-> value */
	private HashMap<Integer, Object> registeredData;
	/** Map between attribute name and int ID */
	private HashMap<String, Integer> attrIds;
	private List<Integer> subscribedIds;
	/** List of widget with a current value different from the one got from the remote unit */
	private List<IFormManaged<?>> dirtyWidgets;
	
	@Inject EventBus eventBus;
	
	@Inject
	DataModel(EventBus eventBus) {
//		globalData = new HashMap<>(10);
		this.eventBus = eventBus;
		registeredData = new HashMap<>();
		attrIds = new HashMap<>();
		subscribedIds = new ArrayList<>();
		dirtyWidgets = new ArrayList<>();
		
		initAttrIdMapping();
		/*
		 * Handlers
		 */
		// The model is never free instead of leaving the application => no need to register the handlers
		eventBus.addHandler(DataModelEvent.getRawType(), new DataModelHandler() {
			@Override
			public void onDataModelUpdated(DataModelEvent event) {
				logger.info("Get raw data to integrate (sz=" + event.getValues2update().size() + ")");
				updateValues(event.getValues2update());
			}
		});
		eventBus.addHandler(DirtyWidgetEvent.getType(), new DirtyWidgetHandler() {
			@Override
			public void onDirtyWidget(DirtyWidgetEvent event) {
				logger.info("DIRTY EVENT SEEN: " + event.isDirty());
				if(event.isDirty()) {
					markWidgetAsDirty(event.getDirtyWidget());
				}
				else {
					markWidgetAsClean(event.getDirtyWidget());
				}
			}
		});
	}
	
	public Integer getIdFromAttributeModelName(String modelName) {
		return attrIds.get(modelName);
	}
	
	/*
	 * Dirty widget management part
	 */
	/**
	 * Indicate if the model contains a {@link IFormManaged} not stored remotely
	 * @return True if at least one data needs to be saved on remote unit
	 */
	public boolean isDirtyModel() {
		return dirtyWidgets.size() > 0;
	}
	
	/**
	 * Mark the model as clean. Eg. all {@link IFormManaged} have been saved<br/>
	 * Fire a {@link DirtyModelEvent} as false if the dirty list wasn't clear
	 */
	public void resetDirtyState() {
		boolean sendIt = isDirtyModel();
		dirtyWidgets.clear();
		if(sendIt) {
			eventBus.fireEvent(new DirtyModelEvent(false));
		}
	}
	
	/**
	 * Get the list of dirty {@link IFormManaged}
	 * @return A list of dirty {@link IFormManaged}
	 */
	public List<IFormManaged<?>> getDirtyWidget() {
		return dirtyWidgets;
	}
	
	/**
	 * Mark a {@link IFormManaged} as dirty if not already<br/>
	 * Fire a {@link DirtyModelEvent} if it's the lonely dirty widget
	 * @param widget The {@link IFormManaged} object to mark as dirty
	 */
	public void markWidgetAsDirty(IFormManaged<?> widget) {
		if(!dirtyWidgets.contains(widget)) {
			dirtyWidgets.add(widget);
			if(dirtyWidgets.size() == 1) {
				eventBus.fireEvent(new DirtyModelEvent(true));
			}
		}
	}
	
	/**
	 * Mark a {@link IFormManaged} as clean if it was seen as dirty<br/>
	 * Fire a {@link DirtyModelEvent} as false if no more widget are marked as dirty.
	 * @param widget The element to be removed from this list, if present
	 */
	public void markWidgetAsClean(IFormManaged<?> widget) {
		boolean dirtyBefore = isDirtyModel();
		if(!dirtyBefore) return;// List of dirty widget already empty
		logger.info("Model dirty before: " + dirtyBefore);
		if(dirtyWidgets.contains(widget)) {// Don't work with radio button because other widget pushed => store ids?
			logger.info("Contains!");
			dirtyWidgets.remove(widget);
		}
		else {
			// In case of RadioBtn, 1 widget per possible value exist. So its pair can be present but not itself
			// => check by using the model ID
			IFormManaged<?> dirty2remove = null;
			for(IFormManaged<?> dirty : dirtyWidgets) {
				if(dirty.getModelId() == widget.getModelId()) {
					logger.info("Found in 2nd pass");
					dirty2remove = dirty;
					break;
				}
			}
			if(dirty2remove != null) {
				dirtyWidgets.remove(dirty2remove);
			}
		}
		logger.info("Model dirty after: " + isDirtyModel());
		if(dirtyBefore && !isDirtyModel()) {
			eventBus.fireEvent(new DirtyModelEvent(false));
		}
	}
	
	/*
	 * Subscription and notification part
	 */
	/**
	 * List of the IDs that we wish to receive a {@link DataModelEvent} where an update appear.<br />
	 * As it's a single user project, no need to know who the requester is
	 * @param ids The list of IDs to listen
	 */
	public void subscribe(List<Integer> ids) {
		for(Integer id : ids) {
			subscribe(id);
		}
	}
	
	/**
	 * ID that we wish to receive a {@link DataModelEvent} where an update appear.<br />
	 * As it's a single user project, no need to know who the requester is
	 * @param id The ID to listen
	 */
	public void subscribe(Integer id) {
		if(!subscribedIds.contains(id)) {
			subscribedIds.add(id);
		}
		// TODO BDY: get values from remote then
	}
	
	/**
	 * Group of IDs we wish to receive a {@link DataModelEvent} where an update appear.<br />
	 * As it's a single user project, no need to know who the requester is
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
	
	/**
	 * List of the IDs that we wish to stop receiving a {@link DataModelEvent} where an update appear.<br />
	 * As it's a single user project, no need to know who the requester is
	 * @param ids The list of IDs to listen
	 */
	public void unsubscribe(List<Integer> ids) {
		for(Integer id : ids) {
			unsubscribe(id);
		}
	}
	
	/**
	 * ID that we wish to stop receiving a {@link DataModelEvent} where an update appear.<br />
	 * As it's a single user project, no need to know who the requester is
	 * @param id The ID to listen
	 */
	public void unsubscribe(Integer id) {
		subscribedIds.remove(id);
	}
	
	/**
	 * Group of IDs we wish to stop receiving a {@link DataModelEvent} where an update appear.<br />
	 * As it's a single user project, no need to know who the requester is
	 * @param group The group {@link Group}
	 */
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
	
	/**
	 * Get the value of a data in stored in the model whatever it's still registered or not.<br/>
	 * <b>WARNING:</b> if the data with id <code>id</code> is no more registered for update,
	 * the value can be obsolete.
	 * @param id The id of the data to get value
	 * @return The value or <code>null</code> if the data isn't found
	 */
	public Object getValue(Integer id) {
		return registeredData.get(id);
	}
	
	/**
	 * Update the values of subscribed IDs present in <code>values</code> and fire one {@link DataModelEvent} with all the IDs
	 * @param values A map of ID <-> value pair
	 */
	public void updateValues(HashMap<Integer, Object> values) {
		updateValues(values, true);
	}
	
	/**
	 * Update the values of subscribed IDs present in <code>values</code> and fire a {@link DataModelEvent} with all the IDs
	 * depending of <code>signal</code>
	 * @param values A map of ID <-> value pair
	 * @param signal Indicates weather or not the {@link DataModelEvent} has to be sent after the updtae
	 */
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
	
	/**
	 * Fire a {@link DataModelEvent} for all registered IDs, whatever they are still subscribed
	 * notification or not
	 */
	public void notify4AllKeys() {
		eventBus.fireEvent(new DataModelEvent(new ArrayList<Integer>(registeredData.keySet())));
	}
	
	/**
	 * Fire a {@link DataModelEvent} for all subscribed IDs
	 */
	public void notifyAllSubscribedKeys() {
		eventBus.fireEvent(new DataModelEvent(subscribedIds));
	}
	
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
	public final static int IS_WIFI;
	
	// Do the ID and Attribute name loading from a json reading in the future
	private void initAttrIdMapping() {
		attrIds.put("ip", IP);
		attrIds.put("netmask", NM);
		attrIds.put("gateway", GW);
		attrIds.put("primaryDns", PDNS);
		attrIds.put("secondaryDns", SDNS);
		attrIds.put("networkConfigMethod", IS_DHCP);
		attrIds.put("networkType", IS_WIFI);
		attrIds.put("essid", WiFi_ESSID);
		attrIds.put("wifiPwd", WiFi_PWD);
	}
	
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
		IS_WIFI = i++;
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

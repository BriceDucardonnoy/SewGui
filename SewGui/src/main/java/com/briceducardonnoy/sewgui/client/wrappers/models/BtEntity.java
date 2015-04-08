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
package com.briceducardonnoy.sewgui.client.wrappers.models;

import java.io.Serializable;

import com.briceducardonnoy.sewgui.client.application.windows.SewEntity;
import com.briceducardonnoy.sewgui.client.events.SewEntitySelectedEvent;
import com.briceducardonnoy.sewgui.client.events.SewEntitySelectedEvent.SewEntitySelectedHandler;
import com.briceducardonnoy.sewgui.client.widgets.ImageButton;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class BtEntity implements SewEntity, Serializable {

	private static final long serialVersionUID = -5264010259511190321L;
	
	private String id;// MAC address
	private Integer clazz;
	private String address;// MAC address
	private String name;// Name stored into device displayed while listing
	
	
	public BtEntity(JSONValue value) {
		JSONObject jso = value.isObject();
		if(jso == null) return;
		id = jso.get("id").toString().replaceAll("\"", "");
		clazz = jso.containsKey("class") ? Integer.parseInt(jso.get("class").toString(), 10) : Integer.parseInt(jso.get("rssi").toString(), 10);
		address = jso.containsKey("address") ? jso.get("address").toString().replaceAll("\"", "") : jso.get("uuid").toString().replaceAll("\"", "");
		name = jso.get("name").toString().replaceAll("\"", "");
	}
	
	public BtEntity(String id, Integer clazz, String address, String name) {
		super();
		this.id = id;
		this.clazz = clazz;
		this.address = address;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getClazz() {
		return clazz;
	}

	public void setClazz(Integer clazz) {
		this.clazz = clazz;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
//		return "id: " + id + ", class: " + clazz + ", @: " + address + ", name: " + name;
		return name + " (" + id + ")";
	}

	@Override
	public Type<SewEntitySelectedHandler> getType() {
		return SewEntitySelectedEvent.getBT_Type();
	}

	@Override
	public ImageButton createImageButtonView() {
		ImageButton ib = new ImageButton(toString());
		
		return ib;
	}

}

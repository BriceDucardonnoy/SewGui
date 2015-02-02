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
package com.briceducardonnoy.sewgui.client.models;

import java.io.Serializable;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class BtEntity implements Serializable {

	private static final long serialVersionUID = -5264010259511190321L;
	
	private String id;// MAC address
	private Integer clazz;
	private String address;// MAC address
	private String name;// Name stored into device displayed while listing
	
	
	public BtEntity(JSONValue value) {
		JSONObject jso = value.isObject();
		if(jso == null) return;
		id = jso.get("id").toString();
		clazz = Integer.parseInt(jso.get("class").toString(), 10);
		address = jso.get("address").toString();
		name = jso.get("name").toString();
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

}

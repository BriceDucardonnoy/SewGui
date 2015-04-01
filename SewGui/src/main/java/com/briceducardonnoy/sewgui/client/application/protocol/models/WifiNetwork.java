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
package com.briceducardonnoy.sewgui.client.application.protocol.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.briceducardonnoy.sewgui.client.application.protocol.RequestHelper;
import com.briceducardonnoy.sewgui.client.application.windows.SewEntity;
import com.briceducardonnoy.sewgui.client.events.SewEntitySelectedEvent.SewEntitySelectedHandler;
import com.google.gwt.event.shared.GwtEvent.Type;

public class WifiNetwork implements SewEntity, Serializable {

	private static final long serialVersionUID = 4898020342523479392L;
	private static Logger logger = Logger.getLogger("SewGui");
	
	private String essid;
	private short level;
	private short noise;
	private short quality;
	private boolean isSecurised;

	public WifiNetwork(List<Byte> array, int protocolVersion) {
		int index;
		
		if(protocolVersion == 1) {
			byte []tmpArray = new byte[33];
			for(index = 0 ; index < 33 ; index++) {// ESSID is 32 bytes encoded
				tmpArray[index] = array.get(index);
			}
			essid = new String(tmpArray).trim();
			level = (short) (array.get(index++) & 0xFF);
			noise = (short) (array.get(index++) & 0xFF);
			quality = (short) (array.get(index++) & 0xFF);
			for(int i = 0; index < 40 ; index++) {
				tmpArray[i++] = array.get(index);
			}
			isSecurised = new String(tmpArray).startsWith("on") ? true : false;
			logger.info(toString());
		}
	}
	
	private static int getSizeOfFrame(int protocolVersion) {
		switch(protocolVersion) {
		case 1: return 40;
		default: return 0;
		}
	}
	
	public static int getNumberOfWifi(List<Byte> array, int protocolVersion) {
		int sz = getSizeOfFrame(protocolVersion);
		switch (protocolVersion) {
		case 0: return 0;
		case 1: return (array.size() - 4 - 3) / sz;
		default: return 0;
		}
	}
	
	@Override
	public String toString() {
		return essid + " (level = " + level + ", noise = " + noise + ", quality = " + quality + " and is " + (isSecurised ? "" : "not ") + "encrypted)";
	}
	
	public static List<WifiNetwork> toWifiNetwork(List<Byte> array, int protocolVersion) {
		List<WifiNetwork> wifis = new ArrayList<>();
		int sz = getSizeOfFrame(protocolVersion);

		for(int i = RequestHelper.getIndexOf1stDataInArray(protocolVersion) ; i < array.size() - sz ; i += sz) {
			wifis.add(new WifiNetwork(array.subList(i, i + sz), protocolVersion));
		}
		
		return wifis;
	}

	public final String getEssid() {
		return essid;
	}

	public final void setEssid(String essid) {
		this.essid = essid;
	}

	public final short getLevel() {
		return level;
	}

	public final void setLevel(short level) {
		this.level = level;
	}

	public final short getNoise() {
		return noise;
	}

	public final void setNoise(short noise) {
		this.noise = noise;
	}

	public final short getQuality() {
		return quality;
	}

	public final void setQuality(short quality) {
		this.quality = quality;
	}

	public final boolean isSecurised() {
		return isSecurised;
	}

	public final void setSecurised(boolean isSecurised) {
		this.isSecurised = isSecurised;
	}

	@Override
	public Type<SewEntitySelectedHandler> getType() {
		// TODO Auto-generated method stub
		return null;
	}

}

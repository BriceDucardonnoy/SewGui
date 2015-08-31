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
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.briceducardonnoy.sewgui.client.application.protocol.RequestHelper;
import com.briceducardonnoy.sewgui.client.model.DataModel;
import com.briceducardonnoy.sewgui.client.utils.Utils;

public class NetworkInfos implements IsPartOfDataModel, Serializable {

	private static final long serialVersionUID = -4171111589426819693L;
	private static Logger logger = Logger.getLogger("SewGui");
	
	private String ip;
	private String nm;
	private String gw;
	private String dns1;
	private String dns2;
	private String essid;
	private String pwd;
	private boolean isDhcp;
	private boolean isWifi;

	public NetworkInfos(List<Byte> array, int protocolVersion) {
		logger.info("Create a network information object");
		int idx = RequestHelper.getIndexOf1stDataInArray(protocolVersion);
		if(protocolVersion == 1) {
			isDhcp = Utils.getIntFromByteList(array, idx) != 0;
			idx += 4;// Size of int
			isWifi = Utils.getIntFromByteList(array, idx) != 0;
			idx += 4;// Size of int
//			ip = Utils.getIpFromByteList(array, idx);
//			idx += 4;// Size of IP
			ip = Utils.getStringFromByteList(array, idx, 16);// 16 = IFNAMSIZ
			idx += 16;
			nm = Utils.getStringFromByteList(array, idx, 16);// 16 = IFNAMSIZ
			idx += 16;
			gw = Utils.getStringFromByteList(array, idx, 16);// 16 = IFNAMSIZ
			idx += 16;
			dns1 = Utils.getStringFromByteList(array, idx, 16);// 16 = IFNAMSIZ
			idx += 16;
			dns2 = Utils.getStringFromByteList(array, idx, 16);// 16 = IFNAMSIZ
			idx += 16;
		}
	}

	public final String getIp() {
		return ip;
	}

	public final void setIp(final String ip) {
		this.ip = ip;
	}

	public final String getNm() {
		return nm;
	}

	public final void setNm(final String nm) {
		this.nm = nm;
	}

	public final String getGw() {
		return gw;
	}

	public final void setGw(final String gw) {
		this.gw = gw;
	}

	public final String getDns1() {
		return dns1;
	}

	public final void setDns1(final String dns1) {
		this.dns1 = dns1;
	}

	public final String getDns2() {
		return dns2;
	}

	public final void setDns2(final String dns2) {
		this.dns2 = dns2;
	}

	public final String getEssid() {
		return essid;
	}

	public final void setEssid(final String essid) {
		this.essid = essid;
	}

	public final String getPwd() {
		return pwd;
	}

	public final void setPwd(final String pwd) {
		this.pwd = pwd;
	}

	public final boolean isDhcp() {
		return isDhcp;
	}

	public final void setDhcp(final boolean isDhcp) {
		this.isDhcp = isDhcp;
	}

	public final boolean isWifi() {
		return isWifi;
	}

	public final void setWifi(final boolean isWifi) {
		this.isWifi = isWifi;
	}

	@Override
	public HashMap<Integer, Object> toHashMap() {
		HashMap<Integer, Object> serialized = new HashMap<>();
		
		serialized.put(DataModel.IP, ip);
		serialized.put(DataModel.NM, nm);
		serialized.put(DataModel.GW, gw);
		serialized.put(DataModel.PDNS, dns1);
		serialized.put(DataModel.SDNS, dns2);
		serialized.put(DataModel.IS_DHCP, isDhcp);
		serialized.put(DataModel.IS_WIFI, isWifi);
		if(isWifi) {
			serialized.put(DataModel.WiFi_ESSID, essid);
			serialized.put(DataModel.WiFi_PWD, pwd);
		}
		else {
			serialized.put(DataModel.WiFi_ESSID, "");
			serialized.put(DataModel.WiFi_PWD, "");
		}
		
		return serialized;
	}

}

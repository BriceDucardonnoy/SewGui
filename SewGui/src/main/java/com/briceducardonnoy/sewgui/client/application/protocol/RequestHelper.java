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
package com.briceducardonnoy.sewgui.client.application.protocol;

import java.util.List;
import java.util.logging.Logger;

import com.briceducardonnoy.sewgui.client.application.exceptions.IncorrectFrameException;
import com.briceducardonnoy.sewgui.client.application.protocol.models.NetworkInfos;
import com.briceducardonnoy.sewgui.client.events.DataModelEvent;
import com.briceducardonnoy.sewgui.client.events.WiFiDiscoverEvent;
import com.briceducardonnoy.sewgui.client.utils.Utils;
import com.google.gwt.typedarrays.client.Int8ArrayNative;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Protocol V1<br/>
 * FE-header-cmd[-parameters]-CRC16-FF<br/>
 * =><br/>
 * FE-ProtocolVersion-Size-CMD-Parameters-CRC16-FF<br/>
 * <hr/>
 * <p>
 * Max size is 255 bytes<br/>
 * Command ID can't be 0xFE or 0xFF (not managed yet because of optimistic code in serializeAndAnswer function)<br/>
 * <li>Header: 2 bytes: <p>
 * 			Version number: 1 byte<br/>
 * 			Byte count of <cmd> and <parameters>: 1 byte
 * 		</p>
 * 	</li>
 * <li>Command: ID of the command to execute. Depending of this one, parameters are present.</li>
 * <li>CRC: 2 bytes from frame without FE and FF</li>
 * <br/>
 * Example in hexa of DISCOVER_WIFI request in protocol V1:<br/>
 * <code>FE-Version-SZ-CMD-CRC_MSB-CRC_LSB-FF</code><br/>
 * <code>FE-01	 -01-00 -CRC_MSB-CRC_LSB-FF</code><br/>
 * </p>
 * <p>
 * <hr/>
 * Byte stuffing applied to the serialized packet<br/>
 * FD => FD 00<br/>
 * FE => FD 01<br/>
 * FF => FD 02<br/>
 * </p>
 */
public class RequestHelper {

	public final static byte VERSION = 1;
	
	public final static byte BASIC_QUESTION_LENGTH 	= 1;// Number of bytes of a question request
	public final static byte EXT_QUESTION_LENGTH 	= 2;
	// Function codes <=> CMD
	public final static byte DISCOVER 		= 0;
	public final static byte STOPPAIRING 	= 1;
	public final static byte GETNETWORK 	= 2;
	public final static byte CONNSTATUS		= 3;
	
	private static Logger logger = Logger.getLogger("SewGui");
	private static final byte []version 		= {(byte) 0xFE, 0, 0, 0, 0, (byte) 0xFF};// Special request to ask for version number (version is 0). No CRC needed.
	private static final byte []discover 		= {(byte) 0xFE, VERSION, BASIC_QUESTION_LENGTH, DISCOVER, 0, 0, (byte) 0xFF};
	private static final byte []stopPairing 	= {(byte) 0xFE, VERSION, BASIC_QUESTION_LENGTH, STOPPAIRING, 0, 0, (byte) 0xFF};
//	private static final byte []getNetwork 		= {(byte) 0xFE, VERSION, QUESTION_LENGTH, GETNETWORK, 0, 0, (byte) 0xFF};
	private static final byte []getNetworkLan 	= {(byte) 0xFE, VERSION, EXT_QUESTION_LENGTH, GETNETWORK, 0, 0, 0, (byte) 0xFF};
	private static final byte []getNetworkWifi 	= {(byte) 0xFE, VERSION, EXT_QUESTION_LENGTH, GETNETWORK, 1, 0, 0, (byte) 0xFF};
	private static final byte []getConnStatus	= {(byte) 0xFE, VERSION, BASIC_QUESTION_LENGTH, CONNSTATUS, 0, 0, (byte) 0xFF};

	static {
		Utils.setCrcIntoByteArray(discover, Utils.getCrc16(discover));
		Utils.setCrcIntoByteArray(stopPairing, Utils.getCrc16(stopPairing));
		Utils.setCrcIntoByteArray(getNetworkLan, Utils.getCrc16(getNetworkLan));
		Utils.setCrcIntoByteArray(getNetworkWifi, Utils.getCrc16(getNetworkWifi));
		Utils.setCrcIntoByteArray(getConnStatus, Utils.getCrc16(getConnStatus));
	}
	
	/**
	 * Check response is valid, eg. check for start and stop flags and check the CRC.<br/>
	 * Then, depending of the <code>code function</code>, fire the corresponding event to allow 
	 * adapted processing.
	 * @param response The byte array responded by the remote unit. Type is Int8ArrayNative 
	 * which is a byte array type from JavaScriptObject.
	 * @param eventBus The eventBus of the caller to be able to fire an event to notify about the command to process
	 * @throws IncorrectFrameException When <code>response</code> has an incorrect format 
	 */
	public static void parseResponse(Int8ArrayNative response, EventBus eventBus) throws IncorrectFrameException {
		int length = response.byteLength();
		if(!Utils.isEqualTo(response.get(0), 0xFE)) throw new IncorrectFrameException("Packet start flag not found. Found " + Integer.toHexString(response.get(0) & 0xFF));
		if(!Utils.isEqualTo(response.get(length - 1), 0xFF)) {
			throw new IncorrectFrameException("Packet end flag not found. Found " + Integer.toHexString(response.get(length - 1) & 0xFF));
		}
		// All is fine, get an array of byte.
		List<Byte> message = Utils.convertInt8Array2ListByte(response, true);
		// Controls CRC
		short crcCalculated = (short) (Utils.getCrc16(message) & 0xFFFF);
		short crcGot = (short) ((response.get(length - 3) << 8) | (response.get(length - 2) & 0xFF));
		if(crcGot != crcCalculated) {
			throw new IncorrectFrameException("CRC error. Received : 0x" + Integer.toHexString(crcGot & 0xFFFF) + " and calculated " +
				Integer.toHexString(crcCalculated & 0xFFFF));
		}
		
		int cmd = -1;
		if(response.get(1) == 1) {// Version
			cmd = response.get(3);
		}
		logger.info("Deal command " + cmd);
		
		switch(cmd) {
		case DISCOVER:
			eventBus.fireEvent(new WiFiDiscoverEvent(response.get(1), message));
			break;
		case GETNETWORK:
			eventBus.fireEvent(new DataModelEvent(new NetworkInfos(message, response.get(1)).toHashMap()));
			break;
		case CONNSTATUS:
			eventBus.fireEvent(new DataModelEvent(Utils.getDataModelHashMapFromNetworkConnectivityPacket(message)));
			break;
		default: logger.warning("Code function unrecognized: " + cmd + " => do nothing");
		}
	}

	/*
	 * Requests helpers functions
	 * Update here to add requests
	 */

	public static byte[] getVersion() {
		return version;
	}
	
	public static byte[] wifiDiscover(int protocol) {
		switch (protocol) {
			case 1: return discover;
			default: return new byte[0];
		}
	}
	
	public static byte[] stopPairing(int protocol) {
		switch (protocol) {
			case 1: return stopPairing;
			default: return new byte[0];
		}
	}
	
	public static byte[] getNetworkLan(int protocol) {
		switch (protocol) {
			case 1: return getNetworkLan;
			default: return new byte[0];
		}
	}
	
	public static byte[] getNetworkWifi(int protocol) {
		switch (protocol) {
			case 1: return getNetworkWifi;
			default: return new byte[0];
		}
	}
	
	public static byte[] getConnectivity(int protocol) {
		switch (protocol) {
			case 1: return getConnStatus;
			default: return new byte[0];
		}
	}

	public static void main(String[] args) {
		System.out.println("FF = " + String.valueOf(Character.toChars(255)));
		logger.info("Discover");
		Utils.setCrcIntoByteArray(discover, Utils.getCrc16(discover));
		logger.info("Stop pairing");
		Utils.setCrcIntoByteArray(stopPairing, Utils.getCrc16(stopPairing));
		logger.info("Get network");
		Utils.setCrcIntoByteArray(getNetworkLan, Utils.getCrc16(getNetworkLan));
		Utils.setCrcIntoByteArray(getNetworkWifi, Utils.getCrc16(getNetworkWifi));
		logger.info("Connectivity");
		Utils.setCrcIntoByteArray(getConnStatus, Utils.getCrc16(getConnStatus));
	}

}

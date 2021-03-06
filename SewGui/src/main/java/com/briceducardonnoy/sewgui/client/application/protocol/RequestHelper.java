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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.briceducardonnoy.sewgui.client.application.exceptions.IncorrectFrameException;
import com.briceducardonnoy.sewgui.client.events.WiFiDiscoverEvent;
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

	// Uses irreducible polynomial:  1 + x^2 + x^15 + x^16
	private static int[] crcTable = {
		0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
		0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440,
		0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
		0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,
		0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,
		0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,
		0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,
		0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,
		0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,
		0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,
		0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,
		0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,
		0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
		0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,
		0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,
		0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,
		0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,
		0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,
		0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,
		0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,
		0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,
		0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,
		0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,
		0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,
		0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,
		0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
		0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,
		0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841,
		0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,
		0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,
		0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,
		0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040,
	};

	public final static byte VERSION = 1;
	// Function codes <=> CMD
	public final static byte DISCOVER = 0;
	
	private static Logger logger = Logger.getLogger("SewGui");
	private static final byte []version = {(byte) 0xFE, 0, 0, 0, 0, (byte) 0xFF};// Special request to ask for version number (version is 0). No CRC needed.
	private static final byte []discover = {(byte) 0xFE, VERSION, 1, DISCOVER, 0, 0, (byte) 0xFF};

	static {
		setCrcIntoByteArray(discover, getCrc16(discover));
	}
	
	private static int getCrc16(byte []datas) {
		int crc = 0x0000;

		for(int i = 1 ; i < datas.length - 3 ; i++) {// Start is 1 because of 0xFE. End is length - 3 because of CRC_MSB, CRC_LSB and 0xFF
			crc = (crc >>> 8) ^ crcTable[(crc ^ datas[i]) & 0xff];
		}

		//		logger.info("CRC16 = " + String.format("0x%04X ", crc) + ": " + crc);
		logger.info("CRC16 = 0x" + Integer.toHexString(crc & 0xFFFF) + ": " + crc);
		return crc;
	}

	private static int getCrc16(Int8ArrayNative datas) {
		int crc = 0x0000;

		for(int i = 1 ; i < datas.length() - 3 ; i++) {// Start is 1 because of 0xFE. End is length - 3 because of CRC_MSB, CRC_LSB and 0xFF
			crc = (crc >>> 8) ^ crcTable[(crc ^ datas.get(i)) & 0xff];
		}

		//		logger.info("CRC16 = " + String.format("0x%04X ", crc) + ": " + crc);
		logger.info("CRC16 = 0x" + Integer.toHexString(crc & 0xFFFF) + ": " + crc);
		return crc;
	}

	private static void setCrcIntoByteArray(byte []datas, int crc) {
		datas[datas.length - 3] = (byte) (crc >> 8);
		datas[datas.length - 2] = (byte) (crc & 0x000000FF);

		String output = "Frame = ";
		for(byte b : datas) {
			//			output += String.format("0x%02X ", b) + " ";
			output += "0x" + Integer.toHexString(b & 0xFF) + " ";
		}
		logger.info(output);
	}

	public static boolean isEqualTo(byte b, int i) {
		return (b & 0xFF) == i;
	}

	public static byte[] getVersion() {
		return version;
	}
	
	public static int getIndexOf1stDataInArray(int protocol) {
		switch(protocol) {
		case 1: return 4;
		case 0:
		default: return -1;
		}
	}

	public static byte[] wifiDiscover(int protocol) {
		if(protocol == 1) return discover;
		return new byte[0];
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
		if(!isEqualTo(response.get(0), 0xFE)) throw new IncorrectFrameException("Packet start flag not found. Found " + Integer.toHexString(response.get(0) & 0xFF));
		if(!isEqualTo(response.get(length - 1), 0xFF)) {
			throw new IncorrectFrameException("Packet end flag not found. Found " + Integer.toHexString(response.get(length - 1) & 0xFF));
		}
		short crcCalculated = (short) (getCrc16(response) & 0xFFFF);
		short crcGot = (short) ((response.get(length - 3) << 8) | (response.get(length - 2) & 0xFF));
		if(crcGot != crcCalculated) {
			throw new IncorrectFrameException("CRC error. Received : 0x" + Integer.toHexString(crcGot & 0xFFFF) + " and calculated " +
				Integer.toHexString(crcCalculated & 0xFFFF));
		}
		// All is fine, get an array of byte.
		List<Byte> message = new ArrayList<>(response.length());
		for(int i = 0 ; i < response.length() ; i++) {
			message.add(response.get(i));
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
		default: logger.warning("Code function unrecognized: " + cmd + " => do nothing");
		}
	}

	public static void main(String[] args) {
		setCrcIntoByteArray(discover, getCrc16(discover));
		System.out.println("FF = " + String.valueOf(Character.toChars(255)));
	}

}

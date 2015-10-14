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
package com.briceducardonnoy.sewgui.client.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.briceducardonnoy.sewgui.client.application.exceptions.IncorrectFrameException;
import com.briceducardonnoy.sewgui.client.application.protocol.RequestHelper;
import com.briceducardonnoy.sewgui.client.events.DataModelEvent;
import com.briceducardonnoy.sewgui.client.model.DataModel;
import com.google.gwt.typedarrays.client.Int8ArrayNative;

public class Utils {
	
//	private static Logger logger = LogManager.getLogManager().getLogger("SewGui");

	/**
	 * Get an int type from a byte list
	 * @param array The array of byte
	 * @param idx The index of the 1st of the 4 bytes to convert into integer
	 * @return The int from byte[<code>idx</code>]<<24 | byte[<code>idx</code>+1]<<16 | byte[<code>idx</code>+2]<<8 | byte[<code>idx</code>+3]
	 */
	public static int getIntFromByteList(List<Byte> array, int idx) {
		if(idx + 4 > array.size()) {
			throw new ArrayIndexOutOfBoundsException("Byte array doesn't contain enough space: " + array.size() + " present but " + (idx+4) + " required");
		}
		return (array.get(idx++) << 24) | (array.get(idx++) << 16) | (array.get(idx++) << 8) | array.get(idx);
	}
	
	public static String getIpFromByteList(List<Byte> array, int idx) {
		short a = (short) (array.get(idx++) & 0x00FF);
		short b = (short) (array.get(idx++) & 0x00FF);
		short c = (short) (array.get(idx++) & 0x00FF);
		short d = (short) (array.get(idx++) & 0x00FF);
		
		return a + "." + b + "." + c + "." + d;
	}
	
	/**
	 * Return a string from the byte array <code>array</code>
	 * @param array The byte array
	 * @param offset The index 0-based of the beginning of the string to create
	 * @param length The max number of characters in the string
	 * @return The converted trimed string or an empty string if <code>array</code> is null or the 
	 * <code>offset + length</code> size is bigger than the array size
	 */
	public static String getStringFromByteList(List<Byte> array, int offset, int length) {
		if(array == null || array.size() < (offset + length)) return "";
		byte []res = new byte[length];
		for(int i = offset, j = 0 ; j < length ; i++) {
			res[j++] = array.get(i);
		}
		
		return new String(res).trim();
	}
	
//	public static BlurHandler leaveEditableWidgetHandler = new BlurHandler() {
//		@Override
//		public void onBlur(BlurEvent event) {
//			IFormManaged<?> widget = (IFormManaged<?>) event.getSource();
//			logger.info("Quit edition of " + widget.getName());
////			fire new dirty event
//			if(widget.isDirty()) {
//				//
//			}
//			else {
//				
//			}
//		}
//	};
	
	/**
	 * Convert the List<Byte> packet received from remote unit with {@link RequestHelper#CONNSTATUS} tag as a HashMap ready
	 * to send through a {@link DataModelEvent#DataModelEvent(HashMap)}
	 * @param packet The byte-UNstuffed packet received from unit
	 * @return A HashMap<Integer, Object> with the good IDs from {@link DataModel}
	 * @throws IncorrectFrameException If the packet isn't valid
	 */
	public static HashMap<Integer, Object> getDataModelHashMapFromNetworkConnectivityPacket(List<Byte> packet) throws IncorrectFrameException {
		if(packet == null) throw new IncorrectFrameException("Network connectivity packet is null");
		if(packet.isEmpty() || packet.size() < 2) throw new IncorrectFrameException("Network connectivity packet is empty or 1 byte");
		
		HashMap<Integer, Object> values2update = new HashMap<>();
		
		switch(packet.get(1)) {// Protocol
			case 1:
				if(packet.get(3) != RequestHelper.CONNSTATUS) throw new IncorrectFrameException("Network connectivity packet hasn't the good command tag");
				int wlan0state = packet.get(4);
				int eth0state = packet.get(5);
				values2update.put(DataModel.LAN_CONN, eth0state);
				values2update.put(DataModel.WAN_CONN, wlan0state);
				break;
			default:
				throw new IncorrectFrameException("Network connectivity unknown protocol version: " + packet.get(1));
		};
		
		return values2update;
	}
	
	/**
	 * Convert a <code>Int8ArrayNative</code> object to a list of bytes
	 * @param response The <code>Int8ArrayNative</code> packet received from remote unit
	 * @param unstuffPacket True if <code>response</code> needs to be unstuffed (eg. FD00 -> FD, FD01 -> FE and FD02 -> FF)
	 * @return A list of bytes
	 */
	public static List<Byte> convertInt8Array2ListByte(Int8ArrayNative response, boolean unstuffPacket) {
		List<Byte> message = new ArrayList<>(response.length());
		for(int i = 0 ; i < response.length() ; i++) {
			if(unstuffPacket && response.get(i) == (byte) 0xFD) {// Byte stuffing
				switch(response.get(i+1)) {
					case 0x00: message.add((byte) 0xFD);
					break;
					case 0x01: message.add((byte) 0xFE);
					break;
					case 0x02: message.add((byte) 0xFF);
					break;
				}
				i++;// Take care, here we increment 'i' inside the loop
				continue;
			}
			message.add(response.get(i));
		}
		return message;
	}
	
}

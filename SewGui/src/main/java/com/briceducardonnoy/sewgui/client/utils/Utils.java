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

import java.util.List;

public class Utils {

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
	
}

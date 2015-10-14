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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.briceducardonnoy.sewgui.client.application.exceptions.IncorrectFrameException;
import com.briceducardonnoy.sewgui.client.model.DataModel;
import com.briceducardonnoy.sewgui.client.utils.Utils;

public class GetNetworkTest {
	
	/*
	 *  Static/DHCP: Static => isDhcp = 0
	 *  WiFi/LAN: LAN => isWifi = 0
	 *  IP: 192.168.1.44
	 *  NM: 255.255.255.0
	 *  GW: 192.168.1.254
	 *  DNS1: 192.168.1.254
	 *  DNS2: 
	 *  
	 *  192 = C0
	 *  (byte) 0x31, (byte) 0x39, (byte) 0x32, (byte) 0x2e, (byte) 0x31, (byte) 0x36, (byte) 0x38 == ASCII for 192.168
	 */
	private static byte []messageFromDeviceRawLAN = {
		(byte) 0xFE, (byte) 0x01, (byte) 0x79, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x31, (byte) 0x39, (byte) 0x32, (byte) 0x2E, (byte) 0x31,
		(byte) 0x36, (byte) 0x38, (byte) 0x2E, (byte) 0x31, (byte) 0x2E, (byte) 0x34, (byte) 0x34, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x32, (byte) 0x35, (byte) 0x35, (byte) 0x2E, (byte) 0x32, (byte) 0x35, (byte) 0x35, (byte) 0x2E, (byte) 0x32,
		(byte) 0x35, (byte) 0x35, (byte) 0x2E, (byte) 0x30, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x31, (byte) 0x39, (byte) 0x32,
		(byte) 0x2E, (byte) 0x31, (byte) 0x36, (byte) 0x38, (byte) 0x2E, (byte) 0x31, (byte) 0x2E, (byte) 0x32, (byte) 0x35, (byte) 0x34,
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x31, (byte) 0x39, (byte) 0x32, (byte) 0x2E, (byte) 0x31, (byte) 0x36, (byte) 0x38,
		(byte) 0x2E, (byte) 0x31, (byte) 0x2E, (byte) 0x32, (byte) 0x35, (byte) 0x34, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xC6, (byte) 0xCB, (byte) 0xFF
	};
	private static List<Byte> messageFromDeviceLan;
	
	/*
	 *  Static/DHCP: Static => isDhcp = 0
	 *  WiFi/LAN: WiFi => isWifi = 1
	 *  IP: 192.168.2.44
	 *  NM: 255.255.255.0
	 *  GW: 
	 *  DNS1: 192.168.1.254
	 *  DNS2: 
	 *  ESSID: TNCAP61EDB5
	 *  
	 *  192 = C0
	 *  (byte) 0x31, (byte) 0x39, (byte) 0x32, (byte) 0x2e, (byte) 0x31, (byte) 0x36, (byte) 0x38 == ASCII for 192.168
	 */
	private static byte []messageFromDeviceRawWiFi = {
		(byte) 0xFE, (byte) 0x01, (byte) 0x79, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x31, (byte) 0x39, (byte) 0x32, (byte) 0x2E, (byte) 0x31,
		(byte) 0x36, (byte) 0x38, (byte) 0x2E, (byte) 0x32, (byte) 0x2E, (byte) 0x34, (byte) 0x34, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x32, (byte) 0x35, (byte) 0x35, (byte) 0x2E, (byte) 0x32, (byte) 0x35, (byte) 0x35, (byte) 0x2E, (byte) 0x32,
		(byte) 0x35, (byte) 0x35, (byte) 0x2E, (byte) 0x30, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x31, (byte) 0x39, (byte) 0x32, (byte) 0x2E, (byte) 0x31, (byte) 0x36, (byte) 0x38,
		(byte) 0x2E, (byte) 0x31, (byte) 0x2E, (byte) 0x32, (byte) 0x35, (byte) 0x34, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x54, (byte) 0x4E, (byte) 0x43, (byte) 0x41, (byte) 0x50,
		(byte) 0x36, (byte) 0x31, (byte) 0x45, (byte) 0x44, (byte) 0x42, (byte) 0x35, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x4F, (byte) 0xC5, (byte) 0xFF
	};
	private static List<Byte> messageFromDeviceWiFi;

	/*
	 * WLAN0 = -1 (nothing)
	 * ETH0 = 0 (access to www.google.com)
	 */
	private static byte []messageFromDeviceRawConnectivity = {
		(byte) 0xFE, (byte) 0x01, (byte) 0x03, (byte) 0x03, (byte) 0xFD, (byte) 0x02, (byte) 0x00, (byte) 0x74, (byte) 0x8C, (byte) 0xFF
	};
	private static List<Byte> messageFromDeviceConnectivity;
	
	/**
	 * Convenient method duplicated for test purpose from {@link Utils#convertInt8Array2ListByte}
	 * Convert a <code>Int8ArrayNative</code> object to a list of bytes
	 * @param response The <code>Int8ArrayNative</code> packet received from remote unit
	 * @param unstuffPacket True if <code>response</code> needs to be unstuffed (eg. FD00 -> FD, FD01 -> FE and FD02 -> FF)
	 * @return A list of bytes
	 */
	private List<Byte> unstuffPacket(List<Byte> response) {
		List<Byte> message = new ArrayList<>(response.size());
		for(int i = 0 ; i < response.size() ; i++) {
			if(response.get(i) == (byte) 0xFD) {// Byte stuffing
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
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		messageFromDeviceLan = new ArrayList<>(messageFromDeviceRawLAN.length);
		for(int i = 0 ; i < messageFromDeviceRawLAN.length ; i++) {
			messageFromDeviceLan.add(messageFromDeviceRawLAN[i]);
		}
		
		messageFromDeviceWiFi = new ArrayList<>(messageFromDeviceRawWiFi.length);
		for(int i = 0 ; i < messageFromDeviceRawWiFi.length ; i++) {
			messageFromDeviceWiFi.add(messageFromDeviceRawWiFi[i]);
		}
		
		messageFromDeviceConnectivity = new ArrayList<>(messageFromDeviceRawConnectivity.length);
		for(int i = 0 ; i < messageFromDeviceRawConnectivity.length ; i++) {
			messageFromDeviceConnectivity.add(messageFromDeviceRawConnectivity[i]);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testLan() {
		System.out.println("Size of frame is " + messageFromDeviceLan.size());
		NetworkInfos info = new NetworkInfos(messageFromDeviceLan, 1);
		assertFalse("DHCP", info.isDhcp());
		assertFalse("WiFi", info.isWifi());
		assertEquals("IP address", "192.168.1.44", info.getIp());
		assertEquals("Netmask", "255.255.255.0", info.getNm());
		assertEquals("Gateway", "192.168.1.254", info.getGw());
		assertEquals("Primary DNS", "192.168.1.254", info.getDns1());
		assertEquals("Secondary DNS", "", info.getDns2());
		assertEquals("ESSID", "", info.getEssid());
	}
	
	@Test
	public final void testWiFi() {
		System.out.println("Size of frame is " + messageFromDeviceWiFi.size());
		NetworkInfos info = new NetworkInfos(messageFromDeviceWiFi, 1);
		assertFalse("DHCP", info.isDhcp());
		assertTrue("WiFi", info.isWifi());
		assertEquals("IP address", "192.168.2.44", info.getIp());
		assertEquals("Netmask", "255.255.255.0", info.getNm());
		assertEquals("Gateway", "", info.getGw());
		assertEquals("Primary DNS", "192.168.1.254", info.getDns1());
		assertEquals("Secondary DNS", "", info.getDns2());
		assertEquals("ESSID", "TNCAP61EDB5", info.getEssid());
	}
	
	@Test
	public final void testNetworkConnectivity() {
		System.out.println("Size of frame is " + messageFromDeviceConnectivity.size());
		HashMap<Integer, Object> values = null;
		try {
			values = Utils.getDataModelHashMapFromNetworkConnectivityPacket(unstuffPacket(messageFromDeviceConnectivity));
		}
		catch (IncorrectFrameException e) {
			fail("IncorrectFrameException occured: " + e.getMessage());
			values = new HashMap<>();
		}
		assertEquals("Number of interface connectivity present", 2, values.size());
		assertTrue("Presence of ETH0 id", values.containsKey(DataModel.LAN_CONN));
		assertTrue("Presence of WLAN0 id", values.containsKey(DataModel.WAN_CONN));
		assertEquals("ETH0 connectivity OK", 0, (int) values.get(DataModel.LAN_CONN));
		assertEquals("WLAN0 connectivity OK", -1, (int) values.get(DataModel.WAN_CONN));
	}

}

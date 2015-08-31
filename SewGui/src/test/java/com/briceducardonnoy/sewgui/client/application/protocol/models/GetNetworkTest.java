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

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GetNetworkTest {
	
	private static List<Byte> messageFromDeviceLan;
	private static List<Byte> messageFromDeviceWiFi;
	/*
	 *  Static/DHCP: Static => isDhcp = 0
	 *  WiFi/LAN: LAN => isWifi = 0
	 *  IP: 192.168.1.44
	 *  NM: 255.255.0.0
	 *  GW: 192.168.1.254
	 *  DNS1: 192.168.1.254
	 *  DNS2: 
	 *  
	 *  192 = C0
	 *  (byte) 0x31, (byte) 0x39, (byte) 0x32, (byte) 0x2e, (byte) 0x31, (byte) 0x36, (byte) 0x38 == ASCII for 192.168
	 */
	private static byte []messageFromDeviceRawLAN = {
		(byte) 0xfe, (byte) 0x01, (byte) 0x59, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, 
		// 192.168...
		(byte) 0x31, (byte) 0x39, (byte) 0x32, (byte) 0x2e, (byte) 0x31, (byte) 0x36, (byte) 0x38, (byte) 0x2e, (byte) 0x31, (byte) 0x2e, (byte) 0x34, (byte) 0x34, 
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x32, (byte) 0x35, (byte) 0x35, (byte) 0x2e, (byte) 0x32, (byte) 0x35, (byte) 0x35, (byte) 0x2e, 
		(byte) 0x30, (byte) 0x2e, (byte) 0x30, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x31, (byte) 0x39, (byte) 0x32, (byte) 0x2e, 
		(byte) 0x31, (byte) 0x36, (byte) 0x38, (byte) 0x2e, (byte) 0x31, (byte) 0x2e, (byte) 0x32, (byte) 0x35, (byte) 0x34, (byte) 0x00, (byte) 0x00, (byte) 0x00, 
		(byte) 0x31, (byte) 0x39, (byte) 0x32, (byte) 0x2e, (byte) 0x31, (byte) 0x36, (byte) 0x38, (byte) 0x2e, (byte) 0x31, (byte) 0x2e, (byte) 0x32, (byte) 0x35, 
		(byte) 0x34, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, 
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x65, (byte) 0xf2, (byte) 0xff
	};
	
	/*
	 *  Static/DHCP: Static => isDhcp = 0
	 *  WiFi/LAN: WiFi => isWifi = 1
	 *  IP: 192.168.2.44
	 *  NM: 255.255.255.0
	 *  GW: 
	 *  DNS1: 192.168.1.254
	 *  DNS2: 
	 *  
	 *  192 = C0
	 *  (byte) 0x31, (byte) 0x39, (byte) 0x32, (byte) 0x2e, (byte) 0x31, (byte) 0x36, (byte) 0x38 == ASCII for 192.168
	 */
	private static byte []messageFromDeviceRawWiFi = {
		(byte) 0xFE, (byte) 0x01, (byte) 0x59, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x31, (byte) 0x39, (byte) 0x32, (byte) 0x2E, (byte) 0x31,
		(byte) 0x36, (byte) 0x38, (byte) 0x2E, (byte) 0x32, (byte) 0x2E, (byte) 0x34, (byte) 0x34, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x32, (byte) 0x35, (byte) 0x35, (byte) 0x2E, (byte) 0x32, (byte) 0x35, (byte) 0x35, (byte) 0x2E, (byte) 0x32,
		(byte) 0x35, (byte) 0x35, (byte) 0x2E, (byte) 0x30, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x31, (byte) 0x39, (byte) 0x32, (byte) 0x2E, (byte) 0x31, (byte) 0x36, (byte) 0x38,
		(byte) 0x2E, (byte) 0x31, (byte) 0x2E, (byte) 0x32, (byte) 0x35, (byte) 0x34, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x62, (byte) 0x86, (byte) 0xFF
	};
	
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
		assertEquals("Netmask", "255.255.0.0", info.getNm());
		assertEquals("Gateway", "192.168.1.254", info.getGw());
		assertEquals("Primary DNS", "192.168.1.254", info.getDns1());
		assertEquals("Secondary DNS", "", info.getDns2());
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
	}

}

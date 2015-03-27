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

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WifiNetworkTest {
	
	/*
	 * SFR WiFi Mobile (sz:33): level=100, noise=0, quality=100, crypted=0x00000800, Encryption key=on
	 * Routeur_f (sz:33): level=100, noise=0, quality=100, crypted=0x00000800, Encryption key=on
	 * SFR WiFi FON (sz:33): level=100, noise=0, quality=100, crypted=0x00008800, Encryption key=off
	 * TNCAP61EDB5 (sz:33): level=90, noise=0, quality=20, crypted=0x00000800, Encryption key=on
	 * NUMERICABLE-A6B3 (sz:33): level=43, noise=0, quality=44, crypted=0x00000800, Encryption key=on
	 * FreeWifi_secure (sz:33): level=26, noise=0, quality=63, crypted=0x00000800, Encryption key=on
	 */
	private static List<Byte> messageFromDevice;
	private static byte []messageFromDeviceRaw = {
			(byte) 0xFE, (byte) 0x01, (byte) 0xF1, (byte) 0x00, (byte) 0x53, (byte) 0x46, (byte) 0x52, (byte) 0x20,
			(byte) 0x57, (byte) 0x69, (byte) 0x46, (byte) 0x69, (byte) 0x20, (byte) 0x4D, (byte) 0x6F, (byte) 0x62, (byte) 0x69, (byte) 0x6C, (byte) 0x65,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x64, (byte) 0x00, (byte) 0x64, (byte) 0x6F,
			(byte) 0x6E, (byte) 0x00, (byte) 0x00, (byte) 0x52, (byte) 0x6F, (byte) 0x75, (byte) 0x74, (byte) 0x65, (byte) 0x75, (byte) 0x72, (byte) 0x5F,
			(byte) 0x66, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x64, (byte) 0x00, (byte) 0x64, (byte) 0x6F, (byte) 0x6E, (byte) 0x00, (byte) 0x00, (byte) 0x53,
			(byte) 0x46, (byte) 0x52, (byte) 0x20, (byte) 0x57, (byte) 0x69, (byte) 0x46, (byte) 0x69, (byte) 0x20, (byte) 0x46, (byte) 0x4F, (byte) 0x4E,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x64,
			(byte) 0x00, (byte) 0x64, (byte) 0x6F, (byte) 0x66, (byte) 0x66, (byte) 0x00, (byte) 0x54, (byte) 0x4E, (byte) 0x43, (byte) 0x41, (byte) 0x50,
			(byte) 0x36, (byte) 0x31, (byte) 0x45, (byte) 0x44, (byte) 0x42, (byte) 0x35, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x5A, (byte) 0x00, (byte) 0x14, (byte) 0x6F, (byte) 0x6E,
			(byte) 0x00, (byte) 0x00, (byte) 0x4E, (byte) 0x55, (byte) 0x4D, (byte) 0x45, (byte) 0x52, (byte) 0x49, (byte) 0x43, (byte) 0x41, (byte) 0x42,
			(byte) 0x4C, (byte) 0x45, (byte) 0x2D, (byte) 0x41, (byte) 0x36, (byte) 0x42, (byte) 0x33, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x2B, (byte) 0x00, (byte) 0x2C, (byte) 0x6F, (byte) 0x6E, (byte) 0x00, (byte) 0x00, (byte) 0x46, (byte) 0x72,
			(byte) 0x65, (byte) 0x65, (byte) 0x57, (byte) 0x69, (byte) 0x66, (byte) 0x69, (byte) 0x5F, (byte) 0x73, (byte) 0x65, (byte) 0x63, (byte) 0x75,
			(byte) 0x72, (byte) 0x65, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x1A, (byte) 0x00,
			(byte) 0x3F, (byte) 0x6F, (byte) 0x6E, (byte) 0x00, (byte) 0x00, (byte) 0xAD, (byte) 0x53, (byte) 0xFF
	};
	private static String []essid = {"SFR WiFi Mobile", "Routeur_f", "SFR WiFi FON", "TNCAP61EDB5", "NUMERICABLE-A6B3", "FreeWifi_secure"};
	private static long []level = {100, 100, 100, 90, 43, 26};
	private static long []noise = {0, 0, 0, 0, 0, 0};
	private static long []quality = {100, 100, 100, 20, 44, 63};
	private static boolean []enc = {true, true, false, true, true, true};

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		messageFromDevice = new ArrayList<>(messageFromDeviceRaw.length);
		for(int i = 0 ; i < messageFromDeviceRaw.length ; i++) {
			messageFromDevice.add(messageFromDeviceRaw[i]);
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
	public final void testNumberOfWifi() {
		assertEquals("Number of wifi detected is invalid", 6l, (long)WifiNetwork.getNumberOfWifi(messageFromDevice, 1));
	}

	@Test
	public final void testToWifiNetwork() {
		System.out.println("Size of frame is " + messageFromDevice.size());
		List<WifiNetwork> wifis = WifiNetwork.toWifiNetwork(messageFromDevice, 1);
		assertEquals("Number of wifi deserialized is invalid", 6l, wifis.size());
		for(int i = 0 ; i < wifis.size() ; i++) {
			assertEquals("Name of network " + (i+1) + " is invalid", essid[i], wifis.get(i).getEssid());
			assertEquals("Level of wifi deserialized is invalid", level[i], (long) wifis.get(i).getLevel());
			assertEquals("Noise of wifi deserialized is invalid", noise[i], (long) wifis.get(i).getNoise());
			assertEquals("Quality of wifi deserialized is invalid", quality[i], (long) wifis.get(i).getQuality());
			assertEquals("Encryption of wifi deserialized is invalid", enc[i], (boolean) wifis.get(i).isSecurised());
		}
	}

}

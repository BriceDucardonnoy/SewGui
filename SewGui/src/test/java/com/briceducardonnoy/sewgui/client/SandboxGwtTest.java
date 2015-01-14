package com.briceducardonnoy.sewgui.client;

import com.google.gwt.junit.client.GWTTestCase;

public class SandboxGwtTest extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "com.briceducardonnoy.sewgui.SewGui";
    }

    public void testSandbox() {
        assertTrue(true);
    }
}
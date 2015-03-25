package com.briceducardonnoy.sewgui.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.typedarrays.client.Int8ArrayNative;

public class WiFiDiscoverEvent extends GwtEvent<WiFiDiscoverEvent.WiFiDiscoverHandler> {
    private static Type<WiFiDiscoverHandler> TYPE = new Type<WiFiDiscoverHandler>();
    
    public interface WiFiDiscoverHandler extends EventHandler {
        void onWiFiDiscover(WiFiDiscoverEvent event);
    }
    
    private int protocolVersion;
    private Int8ArrayNative message;
   
    public WiFiDiscoverEvent(final int protocolVersion, final Int8ArrayNative message) {
    	this.protocolVersion = protocolVersion;
        this.message = message;
    }

    public static Type<WiFiDiscoverHandler> getType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final WiFiDiscoverHandler handler) {
        handler.onWiFiDiscover(this);
    }

    @Override
    public Type<WiFiDiscoverHandler> getAssociatedType() {
        return TYPE;
    }
    
    public int getProtocolVersion() {
		return protocolVersion;
	}
    
    public Int8ArrayNative getMessage() {
        return this.message;
    }
}
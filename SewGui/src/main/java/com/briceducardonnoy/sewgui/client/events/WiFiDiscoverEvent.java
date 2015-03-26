package com.briceducardonnoy.sewgui.client.events;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class WiFiDiscoverEvent extends GwtEvent<WiFiDiscoverEvent.WiFiDiscoverHandler> {
    private static Type<WiFiDiscoverHandler> TYPE = new Type<WiFiDiscoverHandler>();
    
    public interface WiFiDiscoverHandler extends EventHandler {
        void onWiFiDiscover(WiFiDiscoverEvent event);
    }
    
    private int protocolVersion;
    private List<Byte> message;
   
    public WiFiDiscoverEvent(final int protocolVersion, final List<Byte> message) {
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
    
    public List<Byte> getMessage() {
        return this.message;
    }
}
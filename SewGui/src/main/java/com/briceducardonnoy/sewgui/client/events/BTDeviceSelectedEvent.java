package com.briceducardonnoy.sewgui.client.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.EventHandler;
import java.lang.String;
import com.google.gwt.event.shared.HasHandlers;

public class BTDeviceSelectedEvent extends
		GwtEvent<BTDeviceSelectedEvent.BTDeviceSelectedHandler> {

	public static Type<BTDeviceSelectedHandler> TYPE = new Type<BTDeviceSelectedHandler>();
	private String deviceId;

	public interface BTDeviceSelectedHandler extends EventHandler {
		void onBTDeviceSelected(BTDeviceSelectedEvent event);
	}

	public BTDeviceSelectedEvent(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	@Override
	protected void dispatch(BTDeviceSelectedHandler handler) {
		handler.onBTDeviceSelected(this);
	}

	@Override
	public Type<BTDeviceSelectedHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<BTDeviceSelectedHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, String deviceId) {
		source.fireEvent(new BTDeviceSelectedEvent(deviceId));
	}
}

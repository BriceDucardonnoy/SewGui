package com.briceducardonnoy.sewgui.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class SewEntitySelectedEvent extends GwtEvent<SewEntitySelectedEvent.SewEntitySelectedHandler> {
	private static Type<SewEntitySelectedHandler> TYPE = new Type<SewEntitySelectedHandler>();
	private static Type<SewEntitySelectedHandler> BT_TYPE = new Type<SewEntitySelectedHandler>();

	public interface SewEntitySelectedHandler extends EventHandler {
		void onSewEntitySelected(SewEntitySelectedEvent event);
	}

	public interface BTDeviceSelectedHandler extends SewEntitySelectedHandler {
	}

	private final String device;
	private final Type<SewEntitySelectedHandler> type;

	public SewEntitySelectedEvent(final String device, final Type<SewEntitySelectedHandler> type) {
		this.device = device;
		this.type = type;
	}

	public static Type<SewEntitySelectedHandler> getType() {
		return TYPE;
	}
	
	public static Type<SewEntitySelectedHandler> getBT_Type() {
		return BT_TYPE;
	}

	@Override
	protected void dispatch(final SewEntitySelectedHandler handler) {
		handler.onSewEntitySelected(this);
	}

	@Override
	public Type<SewEntitySelectedHandler> getAssociatedType() {
		return type;
	}

	public String getDevice() {
		return this.device;
	}

}
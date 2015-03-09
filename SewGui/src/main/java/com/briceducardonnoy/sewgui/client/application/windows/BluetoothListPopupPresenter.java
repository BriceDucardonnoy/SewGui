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
package com.briceducardonnoy.sewgui.client.application.windows;

import java.util.List;
import java.util.logging.Logger;

import com.briceducardonnoy.sewgui.client.events.BTDeviceSelectedEvent;
import com.briceducardonnoy.sewgui.client.wrappers.models.BtEntity;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;

public class BluetoothListPopupPresenter extends
		PresenterWidget<BluetoothListPopupPresenter.MyView> {

	public interface MyView extends PopupView {
		void setItems(List<BtEntity> items);
		List<HandlerRegistration> getHandlers();
		String getSelectedDeviceId();
	}
	
	private static Logger logger = Logger.getLogger("SewGuiList");
	private List<BtEntity> devices = null;

	@Inject
	public BluetoothListPopupPresenter(final EventBus eventBus, final MyView view) {
		super(eventBus, view);
	}

	@Override
	protected void onBind() {
		super.onBind();
	}
	
	@Override
	protected void onReveal() {
		super.onReveal();
		if(devices == null) {
			logger.warning("No devices specified");
			getView().hide();
		}
		else if(devices.size() == 0) {
			logger.warning("No devices recorded");
			getView().hide();
		}
		else {
			getView().setItems(devices);
		}
	}
	
	@Override
	protected void onHide() {
		super.onUnbind();
		List<HandlerRegistration> hs = getView().getHandlers();
		if(hs != null) {
			for(HandlerRegistration h : hs) {
				h.removeHandler();
			}
			hs.clear();
		}
		if(getView().getSelectedDeviceId() != null && !getView().getSelectedDeviceId().isEmpty()) {
			getEventBus().fireEvent(new BTDeviceSelectedEvent(getView().getSelectedDeviceId()));
		}
	}
	
	public void setDevices(final List<BtEntity> devices) {
		this.devices = devices;
	}
}

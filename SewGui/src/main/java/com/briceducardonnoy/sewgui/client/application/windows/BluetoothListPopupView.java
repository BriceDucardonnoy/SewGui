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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.html.Br;
import org.gwtbootstrap3.client.ui.html.Strong;

import com.briceducardonnoy.sewgui.client.lang.Translate;
import com.briceducardonnoy.sewgui.client.wrappers.models.BtEntity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewImpl;

public class BluetoothListPopupView extends PopupViewImpl implements BluetoothListPopupPresenter.MyView {

	private static Logger logger = Logger.getLogger("SewGuiList");
	
	private Translate translate = GWT.create(Translate.class);
	private final Widget widget;
	private List<HandlerRegistration> handlers;
	private String selectedDeviceId;
	
	private ClickHandler deviceH = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			Button btn = (Button) event.getSource();
			logger.info("Click on " + btn.getId());
			selectedDeviceId = btn.getId();
//			alert.close();
			BluetoothListPopupView.this.hide();
		}
	};

	@UiField PopupPanel main;
	@UiField Alert alert;
	@UiField Strong title;

	public interface Binder extends UiBinder<Widget, BluetoothListPopupView> {
	}

	@Inject
	public BluetoothListPopupView(final EventBus eventBus, final Binder binder) {
		super(eventBus);
		widget = binder.createAndBindUi(this);
//		main.setWidth("50%");
		title.setText(translate.ListOfRecorededDevices());
		handlers = new ArrayList<>();
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setItems(List<BtEntity> items) {
		alert.clear();
		selectedDeviceId = "";
		alert.add(title);
		alert.add(new Br());
		for(BtEntity item : items) {
			Button bt = new Button(item.toString());
			bt.setId(item.getId());
			bt.setWidth("100%");
			bt.setType(ButtonType.PRIMARY);
			handlers.add(bt.addClickHandler(deviceH));
			alert.add(bt);
			alert.add(new Br());
		}
	}
	
	@Override
	public List<HandlerRegistration> getHandlers() {
		return handlers;
	}
	
	@Override
	public String getSelectedDeviceId() {
		return selectedDeviceId;
	}
	
}

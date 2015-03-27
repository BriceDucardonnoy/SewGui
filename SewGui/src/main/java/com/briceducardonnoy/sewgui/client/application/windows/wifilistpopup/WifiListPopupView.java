package com.briceducardonnoy.sewgui.client.application.windows.wifilistpopup;

import java.util.List;
import java.util.logging.Logger;

import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.html.Br;
import org.gwtbootstrap3.client.ui.html.Strong;

import com.briceducardonnoy.sewgui.client.application.protocol.models.WifiNetwork;
import com.briceducardonnoy.sewgui.client.lang.Translate;
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

class WifiListPopupView extends PopupViewImpl implements WifiListPopupPresenter.MyView {
    interface Binder extends UiBinder<Widget, WifiListPopupView> {
    }

    private static Logger logger = Logger.getLogger("SewGuiList");
	
	private Translate translate = GWT.create(Translate.class);
	private List<HandlerRegistration> handlers;
	private String selectedDeviceId;
	
	private ClickHandler deviceH = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			Button btn = (Button) event.getSource();
			logger.info("Click on " + btn.getId());
			selectedDeviceId = btn.getId();
//			alert.close();
			WifiListPopupView.this.hide();
		}
	};
    // TODO BDY: as it's identic with Bluetooth, why not make a single class with generic?
    @UiField PopupPanel main;
	@UiField Alert alert;
	@UiField Strong title;
	
    @Inject
    WifiListPopupView(EventBus eventBus, Binder uiBinder) {
        super(eventBus);
    
        initWidget(uiBinder.createAndBindUi(this));
        title.setText(translate.ListOfDetectedDevices());
    }
    
	@Override
	public void setItems(List<WifiNetwork> items) {
		alert.clear();
		selectedDeviceId = "";
		alert.add(title);
		alert.add(new Br());
		for(WifiNetwork item : items) {
			Button bt = new Button(item.toString());// TODO BDY: make button nicer with icon for encryption and quality
			bt.setId(item.getEssid());
			bt.setWidth("100%");
			bt.setType(ButtonType.PRIMARY);
			handlers.add(bt.addClickHandler(deviceH));
			alert.add(bt);
			alert.add(new Br());alert.add(new Br());
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
package com.briceducardonnoy.sewgui.client.application.windows.entitylistpopup;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.html.Br;
import org.gwtbootstrap3.client.ui.html.Strong;

import com.briceducardonnoy.sewgui.client.application.windows.SewEntity;
import com.briceducardonnoy.sewgui.client.lang.Translate;
import com.briceducardonnoy.sewgui.client.widgets.ImageButton;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewImpl;

class EntityListPopupView extends PopupViewImpl implements EntityListPopupPresenter.MyView {
    interface Binder extends UiBinder<Widget, EntityListPopupView> {
    }

private static Logger logger = Logger.getLogger("SewGuiList");
	
	private Translate translate = GWT.create(Translate.class);
	private List<HandlerRegistration> handlers;
	private String selectedDeviceId;
	
	private ClickHandler deviceH = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			ImageButton btn = (ImageButton) event.getSource();
			logger.info("Click on " + btn.getId());
			selectedDeviceId = btn.getId();
			EntityListPopupView.this.hide();
		}
	};

	@UiField PopupPanel main;
	@UiField HTMLPanel pane;
	@UiField Strong title;

    @Inject
    EntityListPopupView(EventBus eventBus, Binder uiBinder) {
        super(eventBus);
    
        initWidget(uiBinder.createAndBindUi(this));
        title.setText(translate.ListOfDetectedDevices());
        handlers = new ArrayList<>();
    }

	@Override
	public void setItems(List<? extends SewEntity> items) {
		pane.clear();
		selectedDeviceId = "";
		pane.add(title);
		pane.add(new Br());
		for(SewEntity item : items) {
			ImageButton bt = item.createImageButtonView();
			bt.setId(item.getId());
			bt.setWidth("100%");
			bt.setType(ButtonType.PRIMARY);
			handlers.add(bt.addClickHandler(deviceH));
			pane.add(bt);
			pane.add(new Br());
			pane.add(new Br());
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
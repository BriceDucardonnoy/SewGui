package com.briceducardonnoy.sewgui.client.application.windows.entitylistpopup;

import java.util.List;
import java.util.logging.Logger;

import com.briceducardonnoy.sewgui.client.application.windows.SewEntity;
import com.briceducardonnoy.sewgui.client.events.SewEntitySelectedEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;

public class EntityListPopupPresenter<T extends SewEntity> extends PresenterWidget<EntityListPopupPresenter.MyView>  {
	
    interface MyView extends PopupView  {
    	void setItems(List<? extends SewEntity> items);
		List<HandlerRegistration> getHandlers();
		String getSelectedDeviceId();
    }

    private static Logger logger = Logger.getLogger("SewGuiList");
	private List<T> devices;
    
    @Inject
    EntityListPopupPresenter(EventBus eventBus, MyView view) {
        super(eventBus, view);
    }
    
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
    
    protected void onHide() {
        super.onHide();
        List<HandlerRegistration> hs = getView().getHandlers();
		if(hs != null) {
			for(HandlerRegistration h : hs) {
				h.removeHandler();
			}
			hs.clear();
		}
		if(devices.size() > 0 && getView().getSelectedDeviceId() != null && !getView().getSelectedDeviceId().isEmpty()) {
//			getEventBus().fireEvent(new BTDeviceSelectedEvent(getView().getSelectedDeviceId()));
			logger.info("Send a message");
			getEventBus().fireEvent(new SewEntitySelectedEvent(getView().getSelectedDeviceId(), devices.get(0).getType()));
		}
	}
	
	public void setDevices(final List<T> devices) {
		this.devices = devices;
	}
    
}
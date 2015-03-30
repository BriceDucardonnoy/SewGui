package com.briceducardonnoy.sewgui.client.application.windows.wifilistpopup;

import java.util.List;
import java.util.logging.Logger;

import com.briceducardonnoy.sewgui.client.application.protocol.models.WifiNetwork;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;

public class WifiListPopupPresenter extends PresenterWidget<WifiListPopupPresenter.MyView>  {
    interface MyView extends PopupView  {
    	void setItems(List<WifiNetwork> items);
		List<HandlerRegistration> getHandlers();
		String getSelectedDeviceId();
    }
    
    private static Logger logger = Logger.getLogger("SewGuiList");
	private List<WifiNetwork> devices;

    @Inject
    WifiListPopupPresenter(EventBus eventBus, MyView view) {
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
    }
    
    public void setDevices(final List<WifiNetwork> wifis) {
		this.devices = wifis;
	}
    
}
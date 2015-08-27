package com.briceducardonnoy.sewgui.client.events;

import com.briceducardonnoy.sewgui.client.model.IFormManaged;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class BelongToThisFormManagerEvent extends GwtEvent<BelongToThisFormManagerEvent.BelongToThisFormManagerHandler> {
    private static Type<BelongToThisFormManagerHandler> TYPE = new Type<BelongToThisFormManagerHandler>();
    
    public interface BelongToThisFormManagerHandler extends EventHandler {
        void onBelongToThisFormManager(BelongToThisFormManagerEvent event);
    }
    
    private final IFormManaged<?> widget;
    private final String formManagerName;
   
    public BelongToThisFormManagerEvent(final IFormManaged<?> widget, final String formManagerName) {
    	this.widget = widget;
        this.formManagerName = formManagerName;
    }

    public static Type<BelongToThisFormManagerHandler> getType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final BelongToThisFormManagerHandler handler) {
        handler.onBelongToThisFormManager(this);
    }

    @Override
    public Type<BelongToThisFormManagerHandler> getAssociatedType() {
        return TYPE;
    }
    
    public String getFormManagerName() {
        return this.formManagerName;
    }
    
    public IFormManaged<?> getWidget() {
		return widget;
	}
}
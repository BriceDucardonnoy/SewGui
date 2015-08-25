package com.briceducardonnoy.sewgui.client.events;

import com.briceducardonnoy.sewgui.client.model.IFormManaged;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class DirtyWidgetEvent extends GwtEvent<DirtyWidgetEvent.DirtyWidgetHandler> {
    private static Type<DirtyWidgetHandler> TYPE = new Type<DirtyWidgetHandler>();
    
    public interface DirtyWidgetHandler extends EventHandler {
        void onDirtyWidget(DirtyWidgetEvent event);
    }
    
    private final IFormManaged<?> dirtyWidget;
    private final boolean isDirty;
   
    public DirtyWidgetEvent(final IFormManaged<?> dirtyWidget, final boolean isDirty) {
        this.dirtyWidget = dirtyWidget;
        this.isDirty = isDirty;
    }

    public static Type<DirtyWidgetHandler> getType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final DirtyWidgetHandler handler) {
        handler.onDirtyWidget(this);
    }

    @Override
    public Type<DirtyWidgetHandler> getAssociatedType() {
        return TYPE;
    }
    
    public IFormManaged<?> getDirtyWidget() {
        return dirtyWidget;
    }
    
    public boolean isDirty() {
    	return isDirty;
    }
}
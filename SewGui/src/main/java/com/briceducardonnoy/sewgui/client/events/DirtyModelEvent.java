package com.briceducardonnoy.sewgui.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class DirtyModelEvent extends GwtEvent<DirtyModelEvent.DirtyModelHandler> {
    private static Type<DirtyModelHandler> TYPE = new Type<DirtyModelHandler>();
    
    public interface DirtyModelHandler extends EventHandler {
        void onDirtyModel(DirtyModelEvent event);
    }
    
    private final boolean isModelDirty;
   
    public DirtyModelEvent(final boolean isModelDirty) {
        this.isModelDirty = isModelDirty;
    }

    public static Type<DirtyModelHandler> getType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final DirtyModelHandler handler) {
        handler.onDirtyModel(this);
    }

    @Override
    public Type<DirtyModelHandler> getAssociatedType() {
        return TYPE;
    }
    
    public boolean isModelDirty() {
        return isModelDirty;
    }
}
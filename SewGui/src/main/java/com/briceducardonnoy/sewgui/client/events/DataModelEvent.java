package com.briceducardonnoy.sewgui.client.events;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class DataModelEvent extends GwtEvent<DataModelEvent.DataModelHandler> {
    private static Type<DataModelHandler> TYPE = new Type<DataModelHandler>();
    
    public interface DataModelHandler extends EventHandler {
        void onDataModel(DataModelEvent event);
    }
    
    private List<Integer> updatedIds;
   
    public DataModelEvent(final List<Integer> updatedIds) {
        this.updatedIds = updatedIds;
    }
    
    public DataModelEvent(final Integer id) {
    	updatedIds = new ArrayList<>(1);
    	updatedIds.add(id);
    }

    public static Type<DataModelHandler> getType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final DataModelHandler handler) {
        handler.onDataModel(this);
    }

    @Override
    public Type<DataModelHandler> getAssociatedType() {
        return TYPE;
    }

    public List<Integer> getUpdatedIds() {
		return updatedIds;
	}
}
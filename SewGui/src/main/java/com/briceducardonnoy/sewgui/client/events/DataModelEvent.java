package com.briceducardonnoy.sewgui.client.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class DataModelEvent extends GwtEvent<DataModelEvent.DataModelHandler> {
    private static Type<DataModelHandler> SERIALIZED_TYPE = new Type<DataModelHandler>();
    private static Type<DataModelHandler> RAW_TYPE = new Type<>();
    
    public interface DataModelHandler extends EventHandler {
        void onDataModelUpdated(DataModelEvent event);
    }
    
    private List<Integer> updatedIds;
    private HashMap<Integer, Object> values2update;
    private Type<DataModelHandler> type;
   
    public DataModelEvent(final List<Integer> updatedIds) {
        this.updatedIds = updatedIds;
        type = SERIALIZED_TYPE;
    }
    
    public DataModelEvent(final Integer id) {
    	updatedIds = new ArrayList<>(1);
    	updatedIds.add(id);
    	type = SERIALIZED_TYPE;
    }
    
    public DataModelEvent(final HashMap<Integer, Object> values2update) {
    	this.values2update = values2update;
    	type = RAW_TYPE;
    }

    public static Type<DataModelHandler> getSerializedType() {
        return SERIALIZED_TYPE;
    }
    
    public static Type<DataModelHandler> getRawType() {
    	return RAW_TYPE;
    }

    @Override
    protected void dispatch(final DataModelHandler handler) {
        handler.onDataModelUpdated(this);
    }

    @Override
    public Type<DataModelHandler> getAssociatedType() {
        return type;
    }

    public List<Integer> getUpdatedIds() {
		return updatedIds;
	}
    
    public HashMap<Integer, Object> getValues2update() {
		return values2update;
	}
}
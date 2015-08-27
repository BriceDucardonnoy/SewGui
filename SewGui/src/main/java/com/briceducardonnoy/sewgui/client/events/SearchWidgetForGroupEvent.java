package com.briceducardonnoy.sewgui.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class SearchWidgetForGroupEvent extends GwtEvent<SearchWidgetForGroupEvent.SearchWidgetForGroupHandler> {
    private static Type<SearchWidgetForGroupHandler> TYPE = new Type<SearchWidgetForGroupHandler>();
    
    public interface SearchWidgetForGroupHandler extends EventHandler {
        void onSearchWidgetForGroup(SearchWidgetForGroupEvent event);
    }
    
    
    private final String formName;
   
    public SearchWidgetForGroupEvent(final String message) {
        this.formName = message;
    }

    public static Type<SearchWidgetForGroupHandler> getType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final SearchWidgetForGroupHandler handler) {
        handler.onSearchWidgetForGroup(this);
    }

    @Override
    public Type<SearchWidgetForGroupHandler> getAssociatedType() {
        return TYPE;
    }
    
    public String getFormName() {
        return this.formName;
    }
}
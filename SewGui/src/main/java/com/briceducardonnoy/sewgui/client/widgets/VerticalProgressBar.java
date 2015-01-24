package com.briceducardonnoy.sewgui.client.widgets;

import org.gwtbootstrap3.client.ui.ProgressBar;

import com.google.gwt.dom.client.Style;

public class VerticalProgressBar extends ProgressBar {

	public VerticalProgressBar() {
		super();
		setStyleName("vertical-progress-bar");// Style declared in project CSS
	}
	
	@Override
	public void setPercent(final double percent) {
        getElement().getStyle().setHeight(percent, Style.Unit.PCT);
    }

	@Override
    public double getPercent() {
        final String height = getElement().getStyle().getHeight();
        return height == null ? 0 : Double.valueOf(height.substring(0, height.indexOf("%")));
    }

}

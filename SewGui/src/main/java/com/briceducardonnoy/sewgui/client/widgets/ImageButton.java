/**
 * Copyright 2015 © Brice DUCARDONNOY
 * 
 * Permission is hereby granted, free of charge, to any person obtaining 
 * a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * - The above copyright notice and this permission notice shall be included 
 * 	in all copies or substantial portions of the Software.
 * - The Software is provided "as is", without warranty of any kind, express 
 * 	or implied, including but not limited to the warranties of merchantability, 
 * 	fitness for a particular purpose and noninfringement.
 * 
 * In no event shall the authors or copyright holders be liable for any claim, 
 * damages or other liability, whether in an action of contract, tort or otherwise, 
 * arising from, out of or in connection with the software or the use or other 
 * dealings in the Software.
 */
package com.briceducardonnoy.sewgui.client.widgets;

import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ImageButton extends Composite implements HasText {

	private static ImageButtonUiBinder uiBinder = GWT.create(ImageButtonUiBinder.class);

	interface ImageButtonUiBinder extends UiBinder<Widget, ImageButton> {
	}

	public ImageButton() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField HTMLPanel main;
	@UiField Label text;
	
	private ButtonType type;
	private ButtonSize size;
	
	public enum Position {// One position per image/icon
		NORTH,
		SOUTH,
		WEST,
		EAST
	}

	public ImageButton(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
		text.setText(firstName);
//		setStyleName("btn btn-primary");
//		main.setStyleName("btn-warning");
	}

//	@UiHandler("button")
//	void onClick(ClickEvent e) {
//		Window.alert("Hello!");
//	}

	public String getText() {
		return text.getText();
	}
	
	public void setText(final String text) {
		this.text.setText(text);
	}
	
	public ButtonType getType() {
		return type;
	}
	
	public void setType(final ButtonType type) {
		this.type = type;
		main.addStyleName(type.getCssName());
	}

	public final ButtonSize getSize() {
		return size;
	}

	public final void setSize(final ButtonSize size) {
		this.size = size;
		main.addStyleName(size.getCssName());
	}

}

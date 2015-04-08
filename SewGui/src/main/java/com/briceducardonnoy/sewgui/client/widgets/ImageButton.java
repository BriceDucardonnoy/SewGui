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
import org.gwtbootstrap3.client.ui.constants.IconSize;
import org.gwtbootstrap3.client.ui.constants.IconType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasTouchEndHandlers;
import com.google.gwt.event.dom.client.HasTouchStartHandlers;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Button Bootstrap-like with the possibility to add images either at left and right from the text,
 * or at top and bottom from the text.
 * @author Brice DUCARDONNOY
 *
 */
public class ImageButton extends Composite implements HasText, HasClickHandlers, HasTouchStartHandlers, HasTouchEndHandlers {

	private static ImageButtonUiBinder uiBinder = GWT.create(ImageButtonUiBinder.class);

	interface ImageButtonUiBinder extends UiBinder<Widget, ImageButton> {
	}

	public ImageButton() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	private static final String LABELID = "imgBtnText";

	@UiField HTMLPanel main;
	
	private ButtonType type;
	private ButtonSize size;
	
	protected String originalText = "";
	protected String preIcon = "";
	protected String postIcon = "";
	protected String preImg = "";
	protected String postImg = "";
	
	public enum Position {// One position per image/icon
		LEFT,
		RIGHT,
		TOP,
		BOTTOM
	}

	public ImageButton(String text) {
		initWidget(uiBinder.createAndBindUi(this));

		originalText = text;
		main.getElement().setInnerHTML(text);
//		setStyleName("btn btn-primary");
//		main.setStyleName("btn-warning");
	}
	
	public static String getLabelid() {
		return LABELID;
	}

	public void addImage(ImageResource imgRes, Position position, String id) {
		Image img = new Image(imgRes);
		img.setAltText(id);
		img.getElement().setId(id);
		
		switch(position) {
		case TOP:// The setHTML in setText remove the insertFirst/appendChild
			preImg = "<br/>";
		default:
		case LEFT:
//			getElement().insertFirst(img.getElement());
			preImg += img.getElement().toString();
			setText(getText());
			break;
		case BOTTOM:
			postImg = "<br/>";
		case RIGHT:
//			getElement().appendChild(img.getElement());
			postImg += img.getElement().toString();
			setText(getText());
			break;
		};
	}
	
	public void addIcon(IconType icon, Position position, String id) {
		addIcon(icon, position, id, IconSize.NONE);
	}
	
	public void addIcon(IconType icon, Position position, String id, IconSize size) {
//		<i class="glyphicon glyphicon-home" ui:field="bonjour"></i>
		String content = "<i class=\"fa " + icon.getCssName() + " " + size.getCssName() + "\"></i>";
		
		switch(position) {
		case BOTTOM:
			postIcon += "<br/>" + content;
			setText(getText());
			break;
		case RIGHT:
			postIcon += content;
			setText(getText());
			break;
		case TOP:
			preIcon = content + "<br/>";
			setText(getText());
			break;
		case LEFT:
		default:
			preIcon = content;
			setText(getText());
			break;
		};
	}
	
	public void removeImage(String imgId) {
//		main.getWidgetCount()
//		main.getElementById(imgId);
	}
	
	public void removeIcon(String iconId) {
		
	}

	public String getText() {
		return originalText;
	}
	
	public void setText(final String text) {
		originalText = text;
		main.getElement().setInnerHTML(preImg + preIcon + " " + originalText + " " + postIcon + postImg);
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
	
	public String getId() {
		return getElement().getId();
	}
	
	public void setId(final String id) {
		getElement().setId(id);
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

	@Override
	public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
		return addDomHandler(handler, TouchStartEvent.getType());
	}
	
	@Override
	public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
		return addDomHandler(handler, TouchEndEvent.getType());
	}

}

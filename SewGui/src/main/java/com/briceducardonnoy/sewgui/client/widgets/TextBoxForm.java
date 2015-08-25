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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.briceducardonnoy.sewgui.client.context.ApplicationContext;
import com.briceducardonnoy.sewgui.client.events.DirtyWidgetEvent;
import com.briceducardonnoy.sewgui.client.model.IFormManaged;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class TextBoxForm extends TextBox implements IFormManaged<String> {
	private static Logger logger = LogManager.getLogManager().getLogger("SewGui");
	
	private String originalText;
	private String name = "Unknown";
	private String formGroup = "Global";
	private List<HandlerRegistration> handlers;

	@Inject
	public TextBoxForm() {
		super();
		originalText = "";
		handlers = new ArrayList<>();
		handlers.add(addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if(isDirty()) {
					logger.log(Level.FINE, getName() + " is dirty");
					ApplicationContext.getEventBus().fireEvent(new DirtyWidgetEvent(TextBoxForm.this, true));
				}
				else {
					logger.log(Level.FINE, getName() + " is no more dirty");
					ApplicationContext.getEventBus().fireEvent(new DirtyWidgetEvent(TextBoxForm.this, false));
				}
			}
		}));
	}
	
	@Override
	public boolean isDirty() {
		return !originalText.equals(getText());
	}

	@Override
	public void submit() {
		setOriginalValue(getText());
	}

	@Override
	public void cancel() {
		setValue(originalText);
	}

	@Override
	public String getOriginalValue() {
		return originalText;
	}

	@Override
	public void setOriginalValue(final String originalValue) {
		originalText = originalValue;
		setValue(originalValue);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		super.setName(name);
		this.name = name;
	}

	@Override
	public String getFormGroup() {
		return formGroup;
	}
	
	public void setFormGroup(String formGroup) {
		this.formGroup = formGroup;
	}
	
}

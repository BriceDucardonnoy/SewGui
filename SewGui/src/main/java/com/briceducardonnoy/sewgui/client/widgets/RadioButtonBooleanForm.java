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
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class RadioButtonBooleanForm extends RadioButton implements IFormManaged<Boolean> {
	private static Logger logger = LogManager.getLogManager().getLogger("SewGui");
	
	private Boolean originalValue;
	private String formGroup;
	private String modelName;
	private String displayName;
	/** Value from the schema that should turn the radio button ON */
	private boolean trueValue;
	private int modelId = -1;
	private List<HandlerRegistration> handlers;

	@UiConstructor
	public RadioButtonBooleanForm(final String name, final String modelName, final String formGroup, final boolean trueValue) {
		super(name);
		this.modelName = modelName;
		this.formGroup = formGroup;
		this.trueValue = trueValue;
		ApplicationContext.registerFormManagedWidgetFromFormName(this);
		handlers = new ArrayList<>();
		handlers.add(addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(isDirty()) {
					logger.log(Level.FINE, getName() + " is dirty");
					ApplicationContext.getEventBus().fireEvent(new DirtyWidgetEvent(RadioButtonBooleanForm.this, true));
				}
				else {
					logger.log(Level.FINE, getName() + " is no more dirty");
					ApplicationContext.getEventBus().fireEvent(new DirtyWidgetEvent(RadioButtonBooleanForm.this, false));
				}
			}
		}));
	}
	
	/*
	 * IFormManaged methods
	 */
	@Override
	public boolean isDirty() {
		return !getValue().equals(originalValue);
	}

	@Override
	public void submit() {
		setOriginalValue(getValue());
	}

	@Override
	public void cancel() {
		setValue(originalValue);
	}

	@Override
	public Boolean getOriginalValue() {
		return originalValue;
	}

	@Override
	public void setOriginalValue(Object originalValue) {
		if(originalValue == null) return;
		this.originalValue = ((Boolean) originalValue == trueValue);
		setValue(this.originalValue);
	}

	@Override
	public String getFormGroup() {
		return formGroup;
	}
	
	@Override
	public int getModelId() {
		if(modelId == -1) {
			setModelId(ApplicationContext.getIdFromAttributeModelName(modelName));
		}
		return modelId;
	}
	
	public void setModelId(final Integer modelId) {
		if(modelId != null) {
			this.modelId = modelId;
		}
	}

	@Override
	public String getModelName() {
		return modelName;
	}
	
	@Override
	public void setModelName(final String modelName) {
		this.modelName = modelName;
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(final String name) {
		this.displayName = name;
	}

}

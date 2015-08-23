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

import com.briceducardonnoy.sewgui.client.application.model.IFormManaged;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;

public class TextBoxForm extends TextBox implements IFormManaged<String> {
	private String originalText;
//	@Inject FormModel form;
//	
//	@Inject
//	public TextBoxForm() {
//		super();
//		form = new FormModel();
//		form.addWidget(this);
//		originalText = "";
//	}
//	
//	@UiFactory
//	public void createTextBoxForm(FormModel form) {
//		this.form = form;
//	}
	
//	@UiConstructor
	@Inject
	public TextBoxForm() {
		super();
//		form.addWidget(this);
		originalText = "";
	}
	
	
	@Override
	public boolean isDirty() {
		return !originalText.equals(getText());
	}
	
//	public void record() {
//		form.addWidget(this);// Cannot be done in constructor. Do it through an event?
//	}

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
		setText(originalValue);
	}
}

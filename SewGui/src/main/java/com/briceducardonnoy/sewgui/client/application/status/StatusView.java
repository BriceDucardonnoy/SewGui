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
package com.briceducardonnoy.sewgui.client.application.status;

import org.gwtbootstrap3.client.ui.Progress;
import org.gwtbootstrap3.client.ui.ProgressBar;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

class StatusView extends ViewImpl implements StatusPresenter.MyView {
    interface Binder extends UiBinder<Widget, StatusView> {
    }

    @UiField HTMLPanel main;
    @UiField Progress p1;
    @UiField ProgressBar pb1;

	@Inject
	StatusView(Binder uiBinder) {// TODO BDY: test stacked progress bar to have different color
		initWidget(uiBinder.createAndBindUi(this));
		bindSlot(StatusPresenter.SLOT_Status, main);
	}
    
//    @Override
//    public void setInSlot(Object slot, IsWidget content) {
//        if (slot == StatusPresenter.SLOT_Status) {
////            main.setWidget(content);
//        	main.clear();
//        	main.add(content);
//        } else {
//            super.setInSlot(slot, content);
//        }
//    }
}
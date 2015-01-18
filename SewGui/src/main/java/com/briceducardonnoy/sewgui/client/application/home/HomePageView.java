package com.briceducardonnoy.sewgui.client.application.home;

import javax.inject.Inject;

import com.allen_sauer.gwt.log.client.Log;
import com.briceducardonnoy.sewgui.client.lang.Translate;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

class HomePageView extends ViewImpl implements HomePagePresenter.MyView {
	interface Binder extends UiBinder<Widget, HomePageView> {
	}
	
	private final Translate translate = GWT.create(Translate.class);

	@Inject
	HomePageView(Binder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
		Log.info(translate.Bonjour());
	}
}
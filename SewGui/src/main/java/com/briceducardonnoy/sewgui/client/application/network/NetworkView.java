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
package com.briceducardonnoy.sewgui.client.application.network;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Button;

import com.briceducardonnoy.sewgui.client.widgets.TextBoxForm;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

class NetworkView extends ViewImpl implements NetworkPresenter.MyView {
    interface Binder extends UiBinder<Widget, NetworkView> {
    }

	@UiField HTMLPanel main;

	@UiField RadioButton dhcp;
	@UiField RadioButton staticConf;
	@UiField RadioButton ethernet;
	@UiField RadioButton wifi;

	@UiField TextBoxForm wifiText;
	@UiField PasswordTextBox pwdText;
	@UiField CheckBox clearPwd;
	@UiField CheckBox rememberPwd;

	@UiField TextBoxForm ipText;
	@UiField TextBoxForm nmText;
	@UiField TextBoxForm gwText;

	@UiField TextBoxForm dns1Text;
	@UiField TextBoxForm dns2Text;
	
	@UiField Button searchWifiBtn;
	
	@UiField Button cancelBtn;
	@UiField Button submitBtn;

	@Inject
	NetworkView(Binder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
		bindSlot(NetworkPresenter.SLOT_Network, main);
	}

//	@Override
//	public void setInSlot(Object slot, IsWidget content) {
//		if (slot == NetworkPresenter.SLOT_Network) {
//			main.clear();
//			main.add(content);
//		} else {
//			super.setInSlot(slot, content);
//		}
//	}

	@UiHandler("clearPwd")
	public void onShowPassword(ClickEvent event) {
		if (clearPwd.getValue()) {
			pwdText.getElement().setAttribute("type", "text");
		} else {
			pwdText.getElement().setAttribute("type", "password");
		}
	}

	@Override
	public void setDhcp(final boolean isDhcp) {
		if(isDhcp) {
			dhcp.setValue(true);
		}
		else {
			staticConf.setValue(true);
		}
	}

	@Override
	public void setWifi(final boolean isWifi) {
		if(isWifi) {
			wifi.setValue(true);
		}
		else {
			ethernet.setValue(true);
		}
	}

	@Override
	public void setIp(final String ip) {
		ipText.setOriginalText(ip);
	}

	@Override
	public void setNetmask(final String netmask) {
		nmText.setOriginalText(netmask);
	}

	@Override
	public void setGateway(final String gateway) {
		gwText.setOriginalText(gateway);
	}

	@Override
	public void setPrimaryDNS(final String dns1) {
		dns1Text.setOriginalText(dns1);
	}

	@Override
	public void setSecondaryDNS(final String dns2) {
		dns2Text.setOriginalText(dns2);
	}

	@Override
	public void setEssid(String essid) {
		wifiText.setOriginalText(essid);wifiText.record();
	}

	@Override
	public void setPwd(String pwd) {
		pwdText.setText(pwd);
	}

	@Override
	public void setWidgetEnabled(boolean enabled) {
		clearPwd.setEnabled(enabled);
		dhcp.setEnabled(enabled);
		staticConf.setEnabled(enabled);
		ethernet.setEnabled(enabled);
		wifi.setEnabled(enabled);

		wifiText.setEnabled(enabled);
		pwdText.setEnabled(enabled);
		clearPwd.setEnabled(enabled);
		rememberPwd.setEnabled(enabled);

		ipText.setEnabled(enabled);
		nmText.setEnabled(enabled);
		gwText.setEnabled(enabled);

		dns1Text.setEnabled(enabled);
		dns2Text.setEnabled(enabled);
		
		// XXX BDY: play with cancel / submit
	}

	@Override
	public Button getDiscoverWiFiBtn() {
		return searchWifiBtn;
	}
	
	@Override
	public Button getCancelBtn() {
		return cancelBtn;
	}
	
	@Override
	public Button getSubmitBtn() {
		return submitBtn;
	}

}
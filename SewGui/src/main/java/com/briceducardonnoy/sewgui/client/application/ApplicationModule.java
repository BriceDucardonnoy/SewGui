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
package com.briceducardonnoy.sewgui.client.application;

import com.briceducardonnoy.sewgui.client.application.status.StatusModule;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.briceducardonnoy.sewgui.client.application.network.NetworkModule;
import com.briceducardonnoy.sewgui.client.application.stream.StreamModule;
import com.briceducardonnoy.sewgui.client.application.windows.BluetoothListPopupPresenter;
import com.briceducardonnoy.sewgui.client.application.windows.BluetoothListPopupView;
import com.briceducardonnoy.sewgui.client.application.windows.wifilistpopup.WifiListPopupModule;
import com.briceducardonnoy.sewgui.client.application.windows.entitylistpopup.EntityListPopupModule;

public class ApplicationModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new EntityListPopupModule());
		install(new WifiListPopupModule());
		install(new StreamModule());
		install(new NetworkModule());
		install(new StatusModule());

        bindPresenter(ApplicationPresenter.class, ApplicationPresenter.MyView.class, ApplicationView.class,
                ApplicationPresenter.MyProxy.class);

		bindPresenterWidget(BluetoothListPopupPresenter.class,
				BluetoothListPopupPresenter.MyView.class,
				BluetoothListPopupView.class);
    }
}
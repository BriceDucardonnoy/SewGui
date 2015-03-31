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
package com.briceducardonnoy.sewgui.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface SewImagesResources extends ClientBundle {

	public static final SewImagesResources INSTANCE = GWT.create(SewImagesResources.class);
	
//	ImageResource nm_signal_00();
	
	@Source("nm_signal_00.png")
	ImageResource signal00();
	
	@Source("nm_signal_25.png")
	ImageResource signal25();
	
	@Source("nm_signal_50.png")
	ImageResource signal50();
	
	@Source("nm_signal_75.png")
	ImageResource signal75();
	
	@Source("nm_signal_100.png")
	ImageResource signal100();
}

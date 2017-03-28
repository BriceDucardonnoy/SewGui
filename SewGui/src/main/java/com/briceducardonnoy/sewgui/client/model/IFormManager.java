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
package com.briceducardonnoy.sewgui.client.model;

import java.util.List;

/**
 * Interface to implement for presenters which manage {@link IFormManaged} widgets
 * @author Brice DUCARDONNOY
 * @see IFormManaged
 */
public interface IFormManager {
	/**
	 * Register new form-widgets (eg. bound on a model) in the FormManager
	 * @param widgets The widgets to bind
	 */
	public void register(List<IFormManaged<?>> widgets);
	public void submit();
	public void cancel();
	/**
	 * Get the group name of the form.<br/>
	 * This is used in pair with the widgets inside of the current form
	 * @return The name of the group
	 */
	public String getFormGroup();
}

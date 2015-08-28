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

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Interface to implement by a widget which needs a submit / cancel operation in a form.<br/>
 * Extends <code>IsWidget</code> to get access to all kind of handlers
 * @author Brice DUCARDONNOY
 *
 * @param <A> Primitive object like String, Integer, Double...
 */
public interface IFormManaged<A extends Comparable<A>> extends IsWidget {
	/**
	 * Indicates weather or not the widget has its original value changed and not submitted
	 * @return True if widget's value isn't saved
	 */
	public boolean isDirty();
	/**
	 * Save the value in the model and mark the widget as not dirty
	 */
	public void submit();
	/**
	 * Restore the last submitted value
	 */
	public void cancel();
	/**
	 * Get the original value which is used to know if the widget is dirty
	 * @return The original value
	 */
	public A getOriginalValue();
	/**
	 * Set the original value used to get the dirty status.<br/>
	 * The value is casted in the good type
	 * @param originalValue The original value in raw format (eg. not casted)
	 */
	public void setOriginalValue(final Object originalValue);
	/**
	 * Get a name to identify the object in the form
	 * @return The name of the widget
	 */
	public String getName();
	/**
	 * Get the form group the widget belongs to.
	 * @return The name of the group
	 */
	public String getFormGroup();
	/**
	 * Get the id of the attribute it's bound to
	 * @return The ID mapped in {@link DataModel} or -1 if unbound
	 */
	public int getModelId();
	/**
	 * Get the name of the attribute it's bound to
	 * @return The attribute name mapped in {@link DataModel}
	 */
	public String getModelName();
	/**
	 * Set the name of the attribute it's bound to
	 * @param modelName The attribute name to map in {@link DataModel}
	 */
	public void setModelName(final String modelName);
}

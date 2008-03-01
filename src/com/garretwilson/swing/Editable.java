package com.garretwilson.swing;

import com.globalmentor.java.*;

/**Indicates the object can allow editing
@author Garret Wilson
*/
public interface Editable
{

	/**The name of the editable property, if it is bound in any editable object.*/
	public final String EDITABLE_PROPERTY=Editable.class.getName()+Java.PACKAGE_SEPARATOR+"editable";

	/**@return Whether the object can be edited.*/ 
	public boolean isEditable();

	/**Sets whether the object can be edited.*/
	public void setEditable(final boolean newEditable);

}
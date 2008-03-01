package com.garretwilson.swing;

/**Indicates an object can determine whether an imminent closing should be
	allowed.
@author Garret
*/
public interface CanClosable
{
	/**@return <code>true</code> if the object's imminent closing should be
		allowed.
	*/
	public boolean canClose();
}

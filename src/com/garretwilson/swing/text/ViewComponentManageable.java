package com.garretwilson.swing.text;

/**Indicates an object can manage view components with a component manager.
@author Garret Wilson
*/
public interface ViewComponentManageable
{

	/**@return The object managing view components.*/
	public ViewComponentManager getComponentManager();
}

package com.garretwilson.swing;

/**Indicates an implementation that has an action manager to manage actions.
@author Garret Wilson
@see ActionManager
*/
public interface ActionManaged
{
	/**@return The manager of menu and tool actions.*/
	public ActionManager getActionManager();
}

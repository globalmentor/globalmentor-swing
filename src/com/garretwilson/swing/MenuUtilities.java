package com.garretwilson.swing;

import javax.swing.*;

/**Tools for working with Swing menus.
@author Garret Wilson
*/
public class MenuUtilities
{

	/**Retrieves the first menu element that represents the given action.
	This method first checks to see if the given menu element represents the action.
	Then, each submenu present is recursively checked to see if one represents
		the action.
	@param menuElement The menu element hierarchy to check.
	@param action The action for which a menu element should be returned.
	@return The menu element, such as a menu item, that represents the given
		action, or <code>null</code> if no menu element represents the given
		action.
	*/
	public static MenuElement getMenuElement(final MenuElement menuElement, final Action action)
	{
		if(menuElement instanceof AbstractButton)	//if the given menu element is a button
		{
			if(action.equals(((AbstractButton)menuElement).getAction()))	//if the menu element represents the action
			{
				return menuElement;	//return the menu element
			}
		}
		final MenuElement[] subElements=menuElement.getSubElements();	//get the subelements, if any, of the menu element
		for(int i=0; i<subElements.length; ++i)	//look at each subelement
		{
			final MenuElement actionMenuElement=getMenuElement(subElements[i], action);	//see if we can find the menu element from this subelement
			if(actionMenuElement!=null)	//if we found the menu element
			{
				return actionMenuElement;	//return the menu element
			}
		}
		return null;	//show that we couldn't find a menu element for the action
	}
}

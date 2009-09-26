/*
 * Copyright © 1996-2009 GlobalMentor, Inc. <http://www.globalmentor.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**An adapter that can be added as a listener for mouse events, popping
	up a menu when the popup mouse button is pressed.
<p>An instance of this class may installed into a component by calling
	{@link PopupAdapter#install(Component)} or by adding an instance of this class as a mouse
	listener.</p> 
<p>Typical uses of this class include any of the following:</p>
	<ul>
		<li>Constructing the class with a default popup menu to always use.</li>
		<li>Overriding {@link PopupAdapter#getPopupMenu()} to dynamically create a popup
			menu.</li>
		<li>Overriding {@link PopupAdapter#getPopupMenu(Component, int, int)} to
			dynamically create a popup menu based on the specified coordinates in the
			given component.</li>
	</ul>
@author Garret Wilson
*/
public class PopupAdapter extends MouseAdapter
{
	/**The popup menu to be shown by default, or <code>null</code> if there is no
		default popup menu.
	*/
	private JPopupMenu popupMenu;

		/**@return The popup menu to be shown by default, or <code>null</code> if there is no
			default popup menu.
		*/
		public JPopupMenu getPopupMenu() {return popupMenu;}

		/**Sets the popup menu to be shown by default.
		@param popupMenu The popup menu to be shown by default, or <code>null</code>
			if there should be no default popup menu.
		*/
		public void setPopupMenu(final JPopupMenu popupMenu) {this.popupMenu=popupMenu;}

	/**Default constructor.*/
	public PopupAdapter()
	{
		this(null);	//create a popup adapter with no default popup menu
	}

	/**Default popup menu constructor.
	@param popupMenu The popup menu to be shown by default, or <code>null</code>
		if there should be no default popup menu.
	*/
	public PopupAdapter(final JPopupMenu popupMenu)
	{
		setPopupMenu(popupMenu);	//set the default popup menu
	}

	/**Installs the adapter in a component by adding itself as the correct
		listener.
	@param component The component in which the adapter should be installed.
	*/
	public void install(final Component component)
	{
		component.addMouseListener(this);	//listen for mouse events
	}

	/**Uninstalls the adapter from a component by removing itself as a
		listener.
	@param component The component from which the adapter should be uninstalled.
	*/
	public void uninstall(final Component component)
	{
		component.removeMouseListener(this);	//stop listening for mouse events
	}

	/**Invoked when a mouse button has been pressed on a component.
	@param mouseEvent The object containing mouse information about the event.
	*/
	public void mousePressed(final MouseEvent mouseEvent)
	{
		if(mouseEvent.isPopupTrigger())	//if this is the trigger for a popup menu
		{
			showPopupMenu(mouseEvent);	//show a popup menu based upon the mouse event
		}
	}

	/**Invoked when a mouse button has been released on a component.
	@param mouseEvent The object containing mouse information about the event.
	*/
	public void mouseReleased(final MouseEvent mouseEvent)
	{
		if(mouseEvent.isPopupTrigger())	//if this is the trigger for a popup menu
		{
			showPopupMenu(mouseEvent);	//show a popup menu based upon the mouse event
		}
	}

	/**Determines the popup menu to be shown in a component at a certain location.
	<p>This version returns the default popup menu, if there is one.</p>
	@param component The component in whose space the popup menu is to appear.
	@param x The horizontal coordinate in invoker's coordinate space at which the
		popup menu is to be displayed.
	@param y The vertical coordinate in invoker's coordinate space at which the
		popup menu is to be displayed.
	@return The popup menu to be shown at the given coordinates in the
		specified component, or <code>null</code> if no popup menu should be shown.
	@see #getPopupMenu()
	*/
	public JPopupMenu getPopupMenu(final Component component, final int x, final int y)
	{
		return getPopupMenu();	//return the default popup menu, if there is one	
	}

	/**Shows a popup menu based upon the mouse event, which is assumed to be a
		popup trigger.
	@param mouseEvent The object containing mouse information about the event.
	*/
	public void showPopupMenu(final MouseEvent mouseEvent)
	{
		showPopupMenu(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());	//show a popup menu in the component at the location given by the mouse event
	}

	/**Shows a popup menu in the given component at the specified location.
	@param component The component in whose space the popup menu is to appear.
	@param x The horizontal coordinate in invoker's coordinate space at which the
		popup menu is to be displayed.
	@param y The vertical coordinate in invoker's coordinate space at which the
		popup menu is to be displayed.
	@see #getPopupMenu(Component, int, int)
	*/
	public void showPopupMenu(final Component component, final int x, final int y)
	{
		showPopupMenu(getPopupMenu(component, x, y), component, x, y);	//get a popup menu and show it in the component at the given location
	}

	/**Shows a given popup menu in the given component at the specified location.
	@param popupMenu The popup menu to show, or <code>null</code> if no popup
		menu should be shown.
	@param component The component in whose space the popup menu is to appear.
	@param x The horizontal coordinate in invoker's coordinate space at which the
		popup menu is to be displayed.
	@param y The vertical coordinate in invoker's coordinate space at which the
		popup menu is to be displayed.
	*/
	public void showPopupMenu(final JPopupMenu popupMenu, final Component component, final int x, final int y)
	{
		if(popupMenu!=null)	//if we were given a popup menu
		{
			popupMenu.show(component, x, y);	//show the popup menu in the component at the given location
		}
	}

}

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
import javax.swing.*;
import javax.swing.tree.TreePath;

/**An adapter that can be added as a listener for mouse events in a tree,
	popping up a menu when the popup mouse button is pressed.
<p>An instance of this class may installed into a component by calling
	{@link TreePopupAdapter#install(Component)} or by adding an instance of this class as a mouse
	listener.</p> 
<p>This class allows popup menus to be dynamically created based upon a selected
	tree node by overriding {@link TreePopupAdapter#getPopupMenu(TreePath)}. Otherwise the
	default popup menu will be used.</p>
@author Garret Wilson
*/
public class TreePopupAdapter extends PopupAdapter
{
	/**Default constructor.*/
	public TreePopupAdapter()
	{
		super();	//construct the parent class
	}

	/**Default popup menu constructor.
	@param popupMenu The popup menu to be shown by default, or <code>null</code>
		if there should be no default popup menu.
	*/
	public TreePopupAdapter(final JPopupMenu popupMenu)
	{
		super(popupMenu);	//construct the parent class
	}

	/**Determines the popup menu to be shown in a component at a certain location.
	<p>This version returns a popup menu based upon the clicked tree path.</p>
	@param component The component in whose space the popup menu is to appear.
	@param x The horizontal coordinate in invoker's coordinate space at which the
		popup menu is to be displayed.
	@param y The vertical coordinate in invoker's coordinate space at which the
		popup menu is to be displayed.
	@return The popup menu to be shown at the given coordinates in the
		specified component, or <code>null</code> if no popup menu should be shown.
	@see #getPopupMenu(TreePath)
	*/
	public JPopupMenu getPopupMenu(final Component component, final int x, final int y)
	{
		final TreePath treePath;	//try to find a tree path
		if(component instanceof JTree)	//if the component is a tree
		{
			final JTree tree=(JTree)component;	//get the tree
			treePath=tree.getPathForLocation(x, y);	//see which path was clicked
		}
		else	//if the component is not a tree
		{
			treePath=null;	//we cannot find a tree path
		}
		return getPopupMenu(treePath);	//get a popup menu for the tree path
	}

	/**Determines the popup menu to be shown for a given tree path.
	<p>This version returns the default popup menu, if there is one.</p>
	@param treePath The tree path for which a popup menu should be returned, or
		<code>null</code> if a popup menu should be returned for no tree path.
	@return The popup menu to be shown for the given tree path, or
		<code>null</code> if no popup menu should be shown.
	@see #getPopupMenu()
	*/
	public JPopupMenu getPopupMenu(final TreePath treePath)
	{
		return getPopupMenu();	//return the default popup menu, if there is one	
	}

}

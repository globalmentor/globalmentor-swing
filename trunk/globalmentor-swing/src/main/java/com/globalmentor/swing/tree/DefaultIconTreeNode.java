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

package com.globalmentor.swing.tree;

import javax.swing.Icon;
import javax.swing.tree.*;

/**A default tree node that returns an icon to represent the stored user object.
@author Garret Wilson
*/
public class DefaultIconTreeNode extends DefaultMutableTreeNode implements IconTreeNode
{

	/**The icon to display for an open tree node.*/
	private Icon openIcon=null;

		/**@return The icon to display for an open tree node, or <code>null</code> if none is available.*/
		public Icon getOpenIcon() {return openIcon;}

		/**Sets the icon to display for an open tree node.
		@param icon The icon to display when open.
		*/
		public void setOpenIcon(final Icon icon) {openIcon=icon;}

	/**The icon to display for a closed tree node.*/
	private Icon closedIcon=null;

		/**@return The icon to display for a closed tree node, or <code>null</code> if none is available.*/
		public Icon getClosedIcon() {return closedIcon;}

		/**Sets the icon to display for a closed tree node.
		@param icon The icon to display when closed.
		*/
		public void setClosedIcon(final Icon icon) {closedIcon=icon;}

	/**The icon to display for a leaf node.*/
	private Icon leafIcon=null;

		/**@return The icon to display for a leaf node, or <code>null</code> if none is available.*/
		public Icon getLeafIcon() {return leafIcon;}

		/**Sets the icon to display for a leaf node.
		@param icon The icon to display for a leaf.
		*/
		public void setLeafIcon(final Icon icon) {leafIcon=icon;}

	/**Creates a default icon tree node that has no parent and no children, but
		that allows children.
	*/
	public DefaultIconTreeNode()
	{
		super();  //do the default constructing
	}

	/**Creates a default icon tree node that has no parent and no children, but
		that allows children.
	@param icon The icon to display when open, closed, and as a leaf.
	*/
	public DefaultIconTreeNode(final Icon icon)
	{
		super();  //do the default constructing
		setOpenIcon(icon); //set the icons
		setClosedIcon(icon); //set the icons
		setLeafIcon(icon); //set the icons
	}

	/**Creates a default icon tree node with no parent, no children, but that
		allows children, and initializes it with the specified user object.
	@param userObject An object provided by the user that constitutes
		the node's data.
	*/
	public DefaultIconTreeNode(final Object userObject)
	{
		super(userObject);  //construct the parent class
	}

	/**Creates a default icon tree node with no parent, no children, but that
		allows children, and initializes it with the specified user object.
	@param userObject An object provided by the user that constitutes
		the node's data.
	@param icon The icon to display when open, closed, and as a leaf.
	*/
	public DefaultIconTreeNode(final Object userObject, final Icon icon)
	{
		this(userObject);  //do the default constructing
		setOpenIcon(icon); //set the icons
		setClosedIcon(icon); //set the icons
		setLeafIcon(icon); //set the icons
	}

	/**Creates a default tree node with no parent, no children, initialized with
		the specified user object, and that allows children only if specified.
	@param userObject An object provided by the user that constitutes
		the node's data.
	@param allowsChildren If <code>true</code>, the node is allowed to have child
		nodes -- otherwise, it is always a leaf node.
	*/
	public DefaultIconTreeNode(final Object userObject, boolean allowsChildren)
	{
		super(userObject, allowsChildren);  //construct the parent class
	}

	/**Creates a default tree node with no parent, no children, initialized with
		the specified user object, and that allows children only if specified.
	@param userObject An object provided by the user that constitutes
		the node's data.
	@param allowsChildren If <code>true</code>, the node is allowed to have child
		nodes -- otherwise, it is always a leaf node.
	@param icon The icon to display when open, closed, and as a leaf.
	*/
	public DefaultIconTreeNode(final Object userObject, boolean allowsChildren, final Icon icon)
	{
		this(userObject, allowsChildren);  //do the default constructing
		setOpenIcon(icon); //set the icons
		setClosedIcon(icon); //set the icons
		setLeafIcon(icon); //set the icons
	}

}
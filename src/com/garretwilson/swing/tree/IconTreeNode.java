package com.garretwilson.swing.tree;

import java.util.*;
import javax.swing.Icon;
import javax.swing.tree.*;

/**A tree node that returns an icon to represent the stored user object.
@author Garret Wilson
*/
public class IconTreeNode extends DefaultMutableTreeNode
{

	/**The icon to display for the tree node.*/
	private Icon icon=null;

		/**@return The icon to display for the tree node.*/
		public Icon getIcon() {return icon;}

		/**Sets the icon to display for the tree node.
		@param newIcon The icon to display.
		*/
		public void setIcon(final Icon newIcon) {icon=newIcon;}

	/**Creates an icon tree node that has no parent and no children, but that
		allows children.
	*/
	public IconTreeNode()
	{
		super();  //do the default constructing
	}

	/**Creates an icon tree node that has no parent and no children, but that
		allows children.
	@param newIcon The icon to display.
	*/
	public IconTreeNode(final Icon newIcon)
	{
		super();  //do the default constructing
		setIcon(newIcon); //set the icon
	}

	/**Creates an icon tree node with no parent, no children, but that allows
		children, and initializes it with the specified user object.
	@param userObject An object provided by the user that constitutes
		the node's data.
	*/
	public IconTreeNode(final Object userObject)
	{
		super(userObject);  //construct the parent class
	}

	/**Creates an icon tree node with no parent, no children, but that allows
		children, and initializes it with the specified user object.
	@param userObject An object provided by the user that constitutes
		the node's data.
	@param newIcon The icon to display.
	*/
	public IconTreeNode(final Object userObject, final Icon newIcon)
	{
		this(userObject);  //do the default constructing
		setIcon(newIcon); //set the icon
	}

	/**Creates a tree node with no parent, no children, initialized with
		the specified user object, and that allows children only if specified.
	@param userObject An object provided by the user that constitutes
		the node's data.
	@param allowsChildren If <code>true</code>, the node is allowed to have child
		nodes -- otherwise, it is always a leaf node.
	*/
	public IconTreeNode(final Object userObject, boolean allowsChildren)
	{
		super(userObject, allowsChildren);  //construct the parent class
	}

	/**Creates a tree node with no parent, no children, initialized with
		the specified user object, and that allows children only if specified.
	@param userObject An object provided by the user that constitutes
		the node's data.
	@param allowsChildren If <code>true</code>, the node is allowed to have child
		nodes -- otherwise, it is always a leaf node.
	@param newIcon The icon to display.
	*/
	public IconTreeNode(final Object userObject, boolean allowsChildren, final Icon newIcon)
	{
		this(userObject, allowsChildren);  //do the default constructing
		setIcon(newIcon); //set the icon
	}

}
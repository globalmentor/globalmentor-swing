package com.garretwilson.swing.tree;

import java.util.*;
import javax.swing.Icon;
import javax.swing.tree.*;

/**A tree node that returns an icon to represent the stored user object.
@author Garret Wilson
*/
public interface IconTreeNode extends TreeNode
{

	/**@return The icon to display for an open tree node, or <code>null</code> if none is available.*/
	public Icon getOpenIcon();

	/**@return The icon to display for a closed tree node, or <code>null</code> if none is available.*/
	public Icon getClosedIcon();

	/**@return The icon to display for a leaf node, or <code>null</code> if none is available.*/
	public Icon getLeafIcon();

}
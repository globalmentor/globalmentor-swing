package com.garretwilson.swing.tree;

import javax.swing.tree.*;

/**Utilities for working with Swing tree paths.
@author Garret Wilson
*/
public class TreePathUtilities
{

	/**This class cannot be publicly instantiated.*/
	private TreePathUtilities()
	{
	}


	/**Returns a path representing the last path in the tree model.
	@param treeModel The tree model from which to construct a path.
	@return The path recursively composed of the last child of each child.
	*/
	public static TreePath getLastPath(final TreeModel treeModel)
	{
		Object object=treeModel.getRoot(); //get the root node of the tree
		TreePath path=new TreePath(object); //create a path representing just the root
		  //walk the last branch of the tree until we run out of tree nodes, we
			//  find a leaf node, or we run out of child nodes
		while(object instanceof TreeNode) //if this is a tree node
		{
			final TreeNode treeNode=(TreeNode)object; //cast the object to a tree node
			if(!treeNode.isLeaf() && treeNode.getChildCount()>0)  //if this isn't a leaf node and there are child nodes
			{
				object=treeNode.getChildAt(treeNode.getChildCount()-1);  //get the last child
				path=path.pathByAddingChild(object);  //add the last child to our path
			}
			else  //if we find a leaf or a childless node (which should be the same condition)
				break;  //stop looking for the end of the path
		}
		return path;  //return the path we found
	}
}
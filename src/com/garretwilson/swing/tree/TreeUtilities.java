package com.garretwilson.swing.tree;

import javax.swing.*;
import javax.swing.tree.*;

/**Utilities for working with Swing tree paths.
@author Garret Wilson
*/
public class TreeUtilities
{

	/**This class cannot be publicly instantiated.*/
	private TreeUtilities()
	{
	}
	
	/**Removes the given tree node from its parent and selects the remaining node,
		if the remaining node is a <code>DefaultMutableTreeNode</code>.
	@param tree The tree control that holds the model and node.
	@param treeModel The model from which the node should be removed.
	@param treeNode The node to be removed from its parent.
	@see #getRemainingNode
	*/ 
	public static void removeNodeFromParent(final JTree tree, final DefaultTreeModel treeModel, final DefaultMutableTreeNode treeNode)
	{
		final TreeNode remainingNode=getRemainingNode(treeNode);	//see which node will remain after deletion
		treeModel.removeNodeFromParent(treeNode);	//remove the node from the tree
		if(remainingNode instanceof DefaultMutableTreeNode)	//if a node is remaining and it's a DefaultMutableTreeNode
			tree.setSelectionPath(new TreePath(((DefaultMutableTreeNode)remainingNode).getPath()));	//select the new node
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
	
	
	/**Finds the node that would be remaining if this node were to be deleted.
		This node will be the next sibling or, if there is no next sibling, the
		previous sibling. If there are no siblings, the parent node will be
		returned.
	@param treeNode The tree node that is considering deletion.
	@return The node that should be selected should this node be removed, or
		<code>null</code> if there would be no node remaining.
	*/ 
	public static TreeNode getRemainingNode(final DefaultMutableTreeNode treeNode)
	{
		TreeNode remainingNode=treeNode.getNextSibling();	//try to get the next sibling
		if(remainingNode==null)	//if there is no next sibling
		{		
			remainingNode=treeNode.getPreviousSibling();	//try to get the previous sibling
			if(remainingNode==null)	//if there is no previous sibling
			{
				remainingNode=treeNode.getParent();	//get the parent, whether there is one or not
			}
		}
		return remainingNode;	//return the remaining node or null if there isn't one
	}
}
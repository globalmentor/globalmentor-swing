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

package com.garretwilson.swing.tree;

import javax.swing.*;
import javax.swing.tree.*;

/**Utilities for working with Swing tree paths.
@author Garret Wilson
*/
public class Trees
{

	/**This class cannot be publicly instantiated.*/
	private Trees()
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
		<p>This method must be called <em>before</em> the node is removed.</p>
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
	
	/**Finds the node that is remaining after the child at the given index was.
			removed.
		The node returned will be the node at the given index if there is such a
		node, or the node at the previous index if one exists, or the parent node
		if the parent has no more children.
		<p>This method must be called <em>after</em> the node is removed.</p>
	@param parentNode The parent tree node of the child that has been deleted.
	@param index The index at which the child node was located.
	@return The node that should be selected after the node has been removed.
	*/ 
	public static TreeNode getRemainingNode(final TreeNode parentNode, final int index)
	{
		final int childCount=parentNode.getChildCount();	//find out how many nodes are left under this parent
		if(childCount>0)	//if there are remaining children
		{
			final int remainingIndex=Math.min(index, childCount-1);	//we'll use the child in the place of the deleted one, or the last child if that index is no longer available
			return parentNode.getChildAt(remainingIndex);	//get the child that is in place of the deleted node, or the last node, whichever is lowest
		}
		else	//if there are no child nodes left
		{
			return parentNode;	//the parent node should be selected
		} 
	}

	/**Finds a path to the node that is remaining after the child at the given
			index removed.
		The path returned will include the node at the given index if there is such
		a 	node, or the node at the previous index if one exists, or the parent node
		if the parent has no more children.
		<p>If the last component of the parent path is not a <code>TreeNode</code>,
		the parent path will be returned.</p>
		<p>This method must be called <em>after</em> the node is removed.</p>
	@param parentNode The parent tree node of the child that has been deleted.
	@param index The index at which the child node was located.
	@return The node that should be selected after the node has been removed.
	@see TreeModelEvent#getChildIndices
	@see TreeModelEvent#getChildren
	@see TreeModelEvent#getTreePath
	@see #getRemainingNode
	*/ 
	public static TreePath getRemainingPath(final TreePath parentPath, final int index)
	{
		final Object parentNode=parentPath.getLastPathComponent();	//get the last path component
		if(parentNode instanceof TreeNode)	//if the parent is a tree node
		{
			final TreeNode parentTreeNode=(TreeNode)parentNode;	//cast the parent node to a tree node
			final TreeNode remainingNode=getRemainingNode(parentTreeNode, index);	//find out which node would be remaining
				//if the parent node was returned, use the parent path we already have
				//if a child was returned, create a path by adding the child to the parent
			return remainingNode==parentTreeNode ? parentPath : parentPath.pathByAddingChild(remainingNode);
		}
		else	//if the parent isn't a tree node
		{
			return parentPath;	//just return the parent path---we can't access the children of the non-tree node parent		
		}
	}	

}
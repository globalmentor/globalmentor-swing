package com.garretwilson.swing.tree;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import com.garretwilson.swing.event.*;
import com.garretwilson.swing.tree.*;

/**A class that automatically updates a tree selection when nodes are added or
	deleted.
	<ul>
		<li>If the selected node has been deleted, the selection will be updated
			to the next appropriate node.</li>	
	</ul>
@author Garret Wilson
*/
public class TreeModelSelectionUpdateAdapter extends TreeModelAdapter
{

	/**The tree this adapter updates.*/
	private final JTree tree;
	
		/**@return The tree this adapter updates.*/
		protected JTree getTree() {return tree;}
		
	/**Tree constructor.
	@param tree The tree component that will be updated. 
	*/
	public TreeModelSelectionUpdateAdapter(final JTree tree)
	{
		this.tree=tree;	//save the tree
	}

	/**
	 * <p>Invoked after nodes have been inserted into the tree.</p>
	 * 
	 * <p>Use <code>e.getPath()</code> 
	 * to get the parent of the new node(s).
	 * <code>e.getChildIndices()</code>
	 * returns the index(es) of the new node(s)
	 * in ascending order.</p>
	 */
//G***fix	public void treeNodesInserted(final TreeModelEvent treeModelEvent) {}

	/**Invoked after nodes have been removed from the tree.
		If one of the nodes was previously selected, the selection will be moved to
		the next appropriate node.
		<p>If the parent node of the deleted node is not a <code>TreeNode</code>,
		the parent path will be selected.</p>
	@param treeModelEvent The event, with <code>treeModelEvent.getPath()</code>
		returning the former parent of the deleted node(s), and
		<code>treeModelEvent.getChildIndices()</code> returning, in ascending order,
		the index(es) the node(s) had before being deleted.</p>
	*/
	public void treeNodesRemoved(final TreeModelEvent treeModelEvent)
	{
		final JTree tree=getTree();	//get our tree component
		final TreePath parentPath=treeModelEvent.getTreePath();	//get the path of the removed node's parent
		final Object[] children=treeModelEvent.getChildren();	//get the removed children
		for(int i=children.length-1; i>=0; i--)	//look at each of the deleted children, starting at the last deletion
		{
			final TreePath childPath=parentPath.pathByAddingChild(children[i]);	//get the path to this deleted child
			if(tree.isPathSelected(childPath))	//if this path was selected
			{
				tree.setSelectionPath(TreeUtilities.getRemainingPath(parentPath, treeModelEvent.getChildIndices()[i]));	//select the path remaining after the child at this index was deleted
				return;	//don't look for more selections
			}
		}
	}

	/**
	 * <p>Invoked after the tree has drastically changed structure from a
	 * given node down.  If the path returned by e.getPath() is of length
	 * one and the first element does not identify the current root node
	 * the first element should become the new root of the tree.<p>
	 * 
	 * <p>Use <code>e.getPath()</code> 
	 * to get the path to the node.
	 * <code>e.getChildIndices()</code>
	 * returns null.</p>
	 */
//G***fix	public void treeStructureChanged(final TreeModelEvent treeModelEvent) {}

}

package com.garretwilson.swing.tree;

import javax.swing.tree.*;

import com.garretwilson.util.Debug;
import com.globalmentor.marmot.ui.tree.BurrowTreeNode;

/**A tree node that can dynamically load its children when needed.
	Child classes should override <code>loadChildNodes()</code>.
@author Garret Wilson
@see #loadChildNodes
*/
public abstract class DynamicTreeNode extends DefaultMutableTreeNode
{

	/**Whether the child nodes have been loaded.*/
	private boolean isChildNodesLoaded=false;

		/**@return Whether the child nodes have been loaded.*/
		public boolean isChildNodesLoaded() {return isChildNodesLoaded;}

	/**Creates a tree node that has no parent and no children, but which
		allows children.
	*/
	public DynamicTreeNode()
	{
		super();  //construct the parent class
	}

	/**Creates a tree node with no parent, no children, but which allows
		children, and initializes it with the specified user object.
	@param userObject An object provided by the user that constitutes
		the node's data
	*/
	public DynamicTreeNode(final Object userObject)
	{
		super(userObject);  //construct the parent class
	}

  /**@return <code>false</code> so that we won't have to dynamically calculate
		whether we have children until we need to.
	*/
	public boolean isLeaf()
	{
		return false; //assume we're not a leaf
	}

	/**@return The number of children, loading the child nodes dynamically if
		needed.
	@see #ensureChildNodesLoaded
	*/
	public int getChildCount()
	{
		ensureChildNodesLoaded();	//make sure children have been loaded
    return super.getChildCount(); //return the number of children normally, now that we've loaded children if needed
  }
  
	/**Loads children if they haven't already been loaded.
	@see #loadChildNodes
	*/
	public void ensureChildNodesLoaded()
	{
		if(!isChildNodesLoaded()) //if the children are not yet loaded
		{
			isChildNodesLoaded=true;  //show that we've loaded the child nodes (this is done before the actual loading so that future calls to getChildCount() won't cause reloading)
			removeAllChildren();	//make sure we've removed all children before trying to load the children
			loadChildNodes(); //load the child nodes
		}
	}  

	/**Dynamically loads child nodes when needed. Must be overridden to
		appropriately load children.
	*/
	protected abstract void loadChildNodes();
	
	/**Unloads all child nodes and sets the state to unloaded.*/
	public void unloadChildNodes()
	{
		removeAllChildren();	//remove all the children
		isChildNodesLoaded=false;	//show that we have no nodes loaded
	}

}
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

import java.io.IOException;

import javax.swing.tree.*;

import com.globalmentor.swing.AbstractSwingApplication;

/**A tree node that can dynamically load its children when needed.
Child classes should override {@link #loadChildNodes()}.
<p>This class also keeps track of whether there was an error loading nodes,
resetting this error status when the nodes are unloaded.</p>
@author Garret Wilson
@see #loadChildNodes()
*/
public abstract class DynamicTreeNode extends DefaultMutableTreeNode
{

	/**Whether the child nodes have been loaded.*/
	private boolean isChildNodesLoaded=false;

		/**@return Whether the child nodes have been loaded.*/
		public boolean isChildNodesLoaded() {return isChildNodesLoaded;}

	/**The error encountered when trying to load, or <code>null</code> if no error was encountered.*/
	private IOException loadError=null;

		/**@return The error encountered when trying to load, or <code>null</code> if no error was encountered.*/
		public IOException getLoadError() {return loadError;}

		/**Sets the error encountered when trying to load
		@param error The error encountered when trying to load, or <code>null</code> if no error was encountered.
		*/
		protected void setLoadError(final IOException error) {loadError=error;}

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
	If there is an error loading the child nodes, the load error variable will be set.
	@see #loadChildNodes
	*/
	public void ensureChildNodesLoaded()
	{
		if(!isChildNodesLoaded()) //if the children are not yet loaded
		{
			isChildNodesLoaded=true;  //show that we've loaded the child nodes (this is done before the actual loading so that future calls to getChildCount() won't cause reloading)
			removeAllChildren();	//make sure we've removed all children before trying to load the children
			try
			{
				loadChildNodes(); //load the child nodes
			}
			catch(final IOException ioException)	//if there was an error loading the child loads
			{
				loadError=ioException;	//save the load error
				AbstractSwingApplication.displayApplicationError(null, ioException);	//display the error
			}
		}
	}  

	/**Dynamically loads child nodes when needed. Must be overridden to appropriately load children.
	@exception IOException if there is an error loading the child nodes.
	*/
	protected abstract void loadChildNodes() throws IOException;
	
	/**Unloads all child nodes and sets the state to unloaded.
	Any error condition is reset. 
	*/
	public void unloadChildNodes()
	{
		removeAllChildren();	//remove all the children
		isChildNodesLoaded=false;	//show that we have no nodes loaded
		setLoadError(null);	//show that there is no load error.
	}

}
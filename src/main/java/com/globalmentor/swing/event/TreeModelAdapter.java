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

package com.globalmentor.swing.event;

import javax.swing.event.*;

/**
 * A class implementing {@link TreeModelListener} with empty methods. This is a convenience class that allows derived classes to become tree model listeners by
 * only overriding the methods they are interested in.
 * @author Garret Wilson
 */
public class TreeModelAdapter implements TreeModelListener {

	/**
	 * <p>
	 * Invoked after a node (or a set of siblings) has changed in some way. The node(s) have not changed locations in the tree or altered their children arrays,
	 * but other attributes have changed and may affect presentation. Example: the name of a file has changed, but it is in the same location in the file system.
	 * </p>
	 * <p>
	 * To indicate the root has changed, childIndices and children will be null.
	 * </p>
	 * 
	 * <p>
	 * Use <code>e.getPath()</code> to get the parent of the changed node(s). <code>e.getChildIndices()</code> returns the index(es) of the changed node(s).
	 * </p>
	 */
	public void treeNodesChanged(final TreeModelEvent treeModelEvent) {
	}

	/**
	 * <p>
	 * Invoked after nodes have been inserted into the tree.
	 * </p>
	 * 
	 * <p>
	 * Use <code>e.getPath()</code> to get the parent of the new node(s). <code>e.getChildIndices()</code> returns the index(es) of the new node(s) in ascending
	 * order.
	 * </p>
	 */
	public void treeNodesInserted(final TreeModelEvent treeModelEvent) {
	}

	/**
	 * <p>
	 * Invoked after nodes have been removed from the tree. Note that if a subtree is removed from the tree, this method may only be invoked once for the root of
	 * the removed subtree, not once for each individual set of siblings removed.
	 * </p>
	 *
	 * <p>
	 * Use <code>e.getPath()</code> to get the former parent of the deleted node(s). <code>e.getChildIndices()</code> returns, in ascending order, the index(es)
	 * the node(s) had before being deleted.
	 * </p>
	 */
	public void treeNodesRemoved(final TreeModelEvent treeModelEvent) {
	}

	/**
	 * <p>
	 * Invoked after the tree has drastically changed structure from a given node down. If the path returned by e.getPath() is of length one and the first element
	 * does not identify the current root node the first element should become the new root of the tree.
	 * </p>
	 * 
	 * <p>
	 * Use <code>e.getPath()</code> to get the path to the node. <code>e.getChildIndices()</code> returns null.
	 * </p>
	 */
	public void treeStructureChanged(final TreeModelEvent treeModelEvent) {
	}

}

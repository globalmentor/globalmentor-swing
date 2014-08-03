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

package com.globalmentor.swing.rdf.tree;

import java.io.IOException;
import com.globalmentor.rdf.*;
import com.globalmentor.swing.tree.*;

/**
 * A tree node that represents an RDF data model. All resources in the RDF data model will be dynamically loaded.
 * @author Garret Wilson
 */
public class RDFTreeNode extends DynamicTreeNode {

	/** The RDF XML-ifier to use for creating labels. */
	private final RDFXMLGenerator xmlifier;

	/** @return The RDF XML-ifier to use for creating labels. */
	protected RDFXMLGenerator getXMLifier() {
		return xmlifier;
	}

	/**
	 * Convenience function for retrieving the represented RDF data model.
	 * @return The RDF data momdel this tree node represents, already cast to <code>RDF</code>.
	 * @see DefaultMutableTreeNode#getUserObject
	 */
	public RDF getRDF() {
		return (RDF)getUserObject(); //return the user object cast to an RDF
	}

	/**
	 * The object that determines how the resources will be sorted, or <code>null</code> if the resources should not be sorted.
	 */
	//TODO fix	private Comparator comparator=null;

	/**
	 * @return The object that determines how the resources will be sorted, or <code>null</code> if the resources should not be sorted.
	 */
	//TODO fix		public Comparator getComparator() {return comparator;}

	/**
	 * Sets the method of sorting the resources.
	 * @param newComparator The object that determines how the resources will be sorted, or <code>null</code> if the resources should not be sorted.
	 */
	//TODO fix		public void setComparator(final Comparator newComparator) {comparator=newComparator;}

	/**
	 * Constructs a tree node from an RDF data model.
	 * @param rdf The RDF data model represented by this node.
	 * @param rdfXMLifier The RDF XML-ifier to use for creating labels.
	 */
	/*TODO fix
		public RDFTreeNode(final RDF rdf, final RDFXMLifier rdfXMLifier)
		{
			this(rdf, rdfXMLifier, null);	//construct the tree node without a comparator
		}
	*/

	/**
	 * Constructs a tree node from an RDF data model.
	 * @param rdf The RDF data model represented by this node.
	 * @param rdfXMLifier The RDF XML-ifier to use for creating labels.
	 * @param comparator The object that determines how the resources will be sorted, or <code>null</code> if the resources should not be sorted.
	 */
	public RDFTreeNode(final RDF rdf, final RDFXMLGenerator rdfXMLifier/*TODO fix, final Comparator comparator*/) {
		super(rdf); //store the RDF data model as the user object
		xmlifier = rdfXMLifier; //save the XMLifier we'll use for generating labels
		//TODO fix		setComparator(comparator);	//set the comparator
	}

	/** @return <code>true</code> if the RDF data model has no resources. */
	public boolean isLeaf() { //TODO fix to compensate for anonymous resources and resources with no properties
		return getRDF().getResourceCount() == 0; //this is a leaf if there are no resources in this RDF data model
	}

	/**
	 * Dynamically loads child nodes for all resources.
	 * @throws IOException if there is an error loading the child nodes.
	 */
	protected void loadChildNodes() throws IOException {
		//get an iterator to the root RDF resources, sorting them if requested
		//TODO fix		final Iterator rootResourceIterator=getRDF().getRootResourceIterator(getComparator());
		for(final RDFResource resource : getRDF().getRootResources()) {
			//create a new tree node to represent the resource
			final RDFObjectTreeNode rdfResourceNode = new RDFObjectTreeNode(resource, getXMLifier());
			add(rdfResourceNode); //add the resource node to this RDF data model node
		}
	}

	/**
	 * @return A string representation to display as the RDF data model tree node's label.
	 */
	public String toString() {
		return "RDF"; //TODO i18n
	}

}
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
import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;

import com.globalmentor.rdf.*;
import com.globalmentor.rdf.rdfs.*;
import com.globalmentor.swing.tree.*;

/**
 * A tree node that represents an object described in RDF.
 * <p>
 * The object can be either a literal or a resource; if a resource, any properties will be dynamically loaded
 * </p>
 * <p>
 * The RDF object is stored as the user object of the tree node.
 * </p>
 * <p>
 * This class has special support for RDF lists, the contents of which are by default displayed as children of the given property.
 * </p>
 * @author Garret Wilson
 */
public class RDFObjectTreeNode extends DynamicTreeNode {

	/**
	 * Convenience function for retrieving the represented RDF object.
	 * @return The RDF object this tree node represents, already cast to a <code>RDFObject</code>.
	 * @see DefaultMutableTreeNode#getUserObject()
	 */
	public RDFObject getRDFObject() {
		return (RDFResource)getUserObject(); //return the user object cast to an RDF resource
	}

	/** The RDF data model to which the RDF object belongs. */
	//TODO del if not needed	private final RDF rdf;

	/** @return The RDF data model to which the RDF object belongs. */
	//TODO del if not neede		protected RDF getRDF() {return rdf;}

	//TODO make a boolean property setListCollapsed() or something to allow the display of lists to be special-cased or not

	/** The RDF XML-ifier to use for creating labels. */
	private final RDFXMLGenerator xmlGenerator;

	/** @return The RDF XML-ifier to use for creating labels. */
	public RDFXMLGenerator getXMLGenerator() {
		return xmlGenerator;
	}

	/**
	 * The RDF property of which this resource is an object, or <code>null</code> if this object should not be considered the object of any property.
	 */
	private final RDFResource property;

	/**
	 * @return The RDF property of which this resource is an object, or <code>null</code> if this object should not be considered the object of any property.
	 */
	protected RDFResource getProperty() {
		return property;
	}

	/**
	 * The object that determines how the resources will be sorted in a list, or <code>null</code> if the resources in a list should not be sorted.
	 */
	//TODO fix, maybe	private Comparator comparator=null;

	/**
	 * @return The object that determines how the resources will be sorted in, a list, or <code>null</code> if the resources in a list should not be sorted.
	 */
	//TODO fix, maybe		public Comparator getComparator() {return comparator;}

	/**
	 * Sets the method of sorting the resources in lists.
	 * @param newComparator The object that determines how the resources in a list will be sorted, or <code>null</code> if the resources in a list should not be
	 *          sorted.
	 */
	//TODO fix, maybe		public void setComparator(final Comparator newComparator) {comparator=newComparator;}

	/**
	 * Constructs a tree node from an RDF object not in the context of any property. //TODO del @param rdfModel The RDF data model to which the RDF object
	 * belongs.
	 * @param rdfObject The resource to represent in the tree.
	 * @param rdfXMLifier The RDF XML-ifier to use for creating labels.
	 */
	public RDFObjectTreeNode(/*TODO del final RDF rdfModel, */final RDFObject rdfObject, final RDFXMLGenerator rdfXMLifier) {
		this(null, rdfObject, rdfXMLifier); //construct the tree node without any property resource
	}

	/**
	 * Constructs a tree node from an RDF property resource and an RDF object. //TODO del @param rdfModel The RDF data model to which the RDF object belongs.
	 * @param rdfProperty The property of which this object is a resource, or <code>null</code> if this object should not be considered the object of any
	 *          property.
	 * @param rdfObject The resource to represent in the tree.
	 * @param rdfXMLifier The RDF XML-ifier to use for creating labels.
	 */
	public RDFObjectTreeNode(/*TODO del final RDF rdfModel, */final RDFResource rdfProperty, final RDFObject rdfObject, final RDFXMLGenerator rdfXMLifier) {
		super(rdfObject); //store the RDF object as the user object
		//TODO del 		rdf=rdfModel; //save the RDF data model
		property = rdfProperty; //save the property of which this resource is the object
		xmlGenerator = rdfXMLifier; //save the XMLifier we'll use for generating labels
	}

	/**
	 * @return <code>true</code> if this is a literal or a resource with no properties.
	 */
	//TODO fix to compensate for type properties
	public boolean isLeaf() {
		return getUserObject() instanceof RDFLiteral || ((RDFResource)getUserObject()).getPropertyCount() == 0; //this is a leaf if this is a literal or a resource with no properties
	}

	/**
	 * Dynamically loads child nodes for all properties.
	 * @throws IOException if there is an error loading the child nodes.
	 */
	protected void loadChildNodes() throws IOException {
		removeAllChildren(); //remove all children TODO maybe put this in some common place
		//TODO see if we should use this		public void unloadChildNodes()
		if(getUserObject() instanceof RDFResource) { //if we represent an RDF resource
			final RDFResource resource = (RDFResource)getUserObject(); //cast the user object to a resource
			//TODO del when works			if(RDFUtilities.isType(resource, RDFConstants.RDF_NAMESPACE_URI, RDFConstants.LIST_TYPE_NAME))	//if this is a list
			if(resource instanceof RDFListResource) { //if this is a list
				final RDFListResource listResource = (RDFListResource)resource; //cast the resource to a list
				final Iterator iterator = listResource.iterator(); //get an iterator to look at the list elements
				while(iterator.hasNext()) { //while there are more elements
					final RDFObject rdfObject = (RDFObject)iterator.next(); //get the next element of the list
					loadChildNode(null, rdfObject); //load the object without indicating a property
				}
			} else { //if this is a non-list resource
				final Iterator propertyIterator = resource.getPropertyIterator(); //get an iterator to all properties
				while(propertyIterator.hasNext()) { //while there are more properties
					final RDFPropertyValuePair propertyValuePair = (RDFPropertyValuePair)propertyIterator.next(); //get the next property/value pair
					final RDFResource property = propertyValuePair.getProperty(); //get the property resource
					final RDFObject value = propertyValuePair.getPropertyValue(); //get the property value
					loadChildNode(property, value); //load the property-value pair
				}
			}
		}
	}

	/**
	 * Loads a child node to represent a property object and optional property.
	 * @param rdfProperty The property of which the object is a resource, or <code>null</code> if this object should not be considered the object of any property.
	 * @param rdfObject The resource to represent in the new node.
	 */
	protected void loadChildNode(final RDFResource rdfProperty, final RDFObject rdfObject) {
		//create a new tree node to represent the property and value
		final RDFObjectTreeNode rdfPropertyNode = new RDFObjectTreeNode(rdfProperty, rdfObject, getXMLGenerator());
		add(rdfPropertyNode); //add the property node to this resource node		
	}

	/** @return A string representation to display as the tree node's label. */
	public String toString() {
		final Object userObject = getUserObject(); //get the user object we're representing
		final RDFResource property = getProperty(); //see if the object should be considered in the context of a property
		final StringBuilder stringBuilder = new StringBuilder(); //create a new string buffer
		if(property != null) { //if we are the object of a property
			stringBuilder.append(getXMLGenerator().getLabel(property.getURI())); //append "property"
		}
		if(userObject instanceof RDFResource) { //if we're representing a resource
			final RDFResource resource = (RDFResource)userObject; //cast the user object to a resource
			final RDFResource type = RDFResources.getType(resource); //get the type of the resource
			final RDFLiteral label = RDFS.getLabel(resource); //get the label of the resource
			boolean hasPredicateToken = false; //we'll note whether we ever have something to represent the predicate of the statement
			if(type != null) { //if we have a type
				if(property != null && !hasPredicateToken) //if we had a property but no predicate representation
					stringBuilder.append(':'); //append a colon to separate the property from the rest
				if(hasPredicateToken) //if we had something to represent the predicate
					stringBuilder.append(' '); //append a space to separate the rest
				stringBuilder.append('(').append(getXMLGenerator().getLabel(type.getURI())).append(')'); //append "(type)"
				hasPredicateToken = true; //show that we have something to represent the predicate
			}
			if(label != null) { //if there is a label
				if(property != null && !hasPredicateToken) //if we had a property but no predicate representation
					stringBuilder.append(':'); //append a colon to separate the property from the rest
				if(hasPredicateToken) //if we had something to represent the predicate
					stringBuilder.append(' '); //append a space to separate the rest
				stringBuilder.append(label); //append the text of the label
				hasPredicateToken = true; //show that we have something to represent the predicate
			}
			if(resource.getURI() != null) { //if there is no label and this is not a blank node resource
				if(property != null && !hasPredicateToken) //if we had a property but no predicate representation
					stringBuilder.append(':'); //append a colon to separate the property from the rest
				if(hasPredicateToken) //if we had something to represent the predicate
					stringBuilder.append(' '); //append a space to separate the rest
				stringBuilder.append('[').append(getXMLGenerator().getLabel(resource.getURI())).append(']'); //append "[referenceURI]" label
				hasPredicateToken = true; //show that we have something to represent the predicate
			}
			final RDFObject literalValue = RDFResources.getValue(resource); //get the rdf:value property value, if there is one
			if(literalValue instanceof RDFLiteral) { //if this resource has a literal value
				if(property != null && !hasPredicateToken) //if we had a property but no predicate representation
					stringBuilder.append(':'); //append a colon to separate the property from the rest
				if(hasPredicateToken) //if we had something to represent the predicate
					stringBuilder.append(' '); //append a space to separate the rest
				stringBuilder.append('{').append(((RDFLiteral)literalValue).getLexicalForm()).append('}'); //append "{lexicalForm}" label
				hasPredicateToken = true; //show that we have something to represent the predicate				
			}
			return stringBuilder.toString(); //return the resource string we constructed
		} else if(userObject instanceof RDFLiteral) { //if we're representing a literal
			final RDFLiteral literal = (RDFLiteral)userObject; //cast the user oject to an RDF literal
			if(property != null) //if we had a property
				stringBuilder.append(':').append(' '); //append ": " to separate the property from the literal
			stringBuilder.append('"').append(literal.getLexicalForm()).append('"'); //append the literal value in quotes
			return stringBuilder.toString(); //return the literal string we constructed
		} else
			//if we're representing neither a resource nor a literal (this should logically never happen)
			return super.toString(); //return the default string version
	}

}
package com.garretwilson.swing.rdf.tree;

import java.util.*;
import com.garretwilson.swing.tree.*;
import com.garretwilson.rdf.*;
import com.garretwilson.rdf.rdfs.*;

/**A tree node that represents an object described in RDF.
	The object can be either a literal or a resource; if a resource, any
	properties will be dynamically loaded
	The RDF object is stored as the user object of the tree node.
@author Garret Wilson
*/
public class RDFObjectTreeNode extends DynamicTreeNode
{

	/**Convenience function for retrieving the represented RDF object.
	@return The RDF object this tree node represents, already cast to a
		<code>RDFObject</code>.
	@see DefaultMutableTreeNode#getUserObject
	*/
/*G***del if not needed
	public RDFObject getRDFObject()
	{
		return (RDFResource)getUserObject();  //return the user object cast to an RDF resource
	}
*/
	/**The RDF data model to which the RDF object belongs.*/
//G***del if not needed	private final RDF rdf;

	  /**@return The RDF data model to which the RDF object belongs.*/
//G***del if not neede		protected RDF getRDF() {return rdf;}

	/**The RDF XML-ifier to use for creating labels.*/
	private final RDFXMLifier xmlifier;

	  /**@return The RDF XML-ifier to use for creating labels.*/
		protected RDFXMLifier getXMLifier() {return xmlifier;}

	/**The RDF property of which this resource is an object, or <code>null</code>
		if this object should not be considered the object of any property.
	*/
	private final RDFResource property;

		/**@return The RDF property of which this resource is an object, or
		  <code>null</code> if this object should not be considered the object of
			any property.
		*/
		protected RDFResource getProperty() {return property;}


	/**Constructs a tree node from an RDF object not in the context of any
		property.
//G***del	@param rdfModel The RDF data model to which the RDF object belongs.
	@param rdfObject The resource to represent in the tree.
	@param rdfXMLifier The RDF XML-ifier to use for creating labels.
	*/
	public RDFObjectTreeNode(/*G***del final RDF rdfModel, */final RDFObject rdfObject, final RDFXMLifier rdfXMLifier)
	{
		this(null, rdfObject, rdfXMLifier);  //construct the tree node without any property resource
	}

	/**Constructs a tree node from an RDF property resource and an RDF object.
//G***del	@param rdfModel The RDF data model to which the RDF object belongs.
	@param rdfProperty The property of which this object is a resource, or
		<code>null</code> if this object should not be considered the object of
		any property.
	@param rdfObject The resource to represent in the tree.
	@param rdfXMLifier The RDF XML-ifier to use for creating labels.
	*/
	public RDFObjectTreeNode(/*G***del final RDF rdfModel, */final RDFResource rdfProperty, final RDFObject rdfObject, final RDFXMLifier rdfXMLifier)
	{
		super(rdfObject); //store the RDF object as the user object
//G***del 		rdf=rdfModel; //save the RDF data model
		property=rdfProperty; //save the property of which this resource is the object
		xmlifier=rdfXMLifier; //save the XMLifier we'll use for generating labels
	}

  /**@return <code>true</code> if this is a literal or a resource with no
		properties.
	*/  //G***fix to compensate for type properties
	public boolean isLeaf()
	{
		return getUserObject() instanceof RDFLiteral
			  || ((RDFResource)getUserObject()).getPropertyCount()==0;  //this is a leaf if this is a literal or a resource with no properties
	}

	/**Dynamically loads child nodes for all properties.*/
	protected void loadChildNodes()
	{
		removeAllChildren();	//remove all children G***maybe put this in some common place
		if(getUserObject() instanceof RDFResource)  //if we represent an RDF resource
		{
			final Iterator propertyIterator=((RDFResource)getUserObject()).getPropertyIterator();  //get an iterator to all properties
			while(propertyIterator.hasNext()) //while there are more properties
			{
				final RDFPropertyValuePair propertyValuePair=(RDFPropertyValuePair)propertyIterator.next(); //get the next property/value pair
				final RDFResource property=propertyValuePair.getProperty();  //get the property resource
				final RDFObject value=propertyValuePair.getPropertyValue();  //get the property value
					//create a new tree node to represent the property and value
				final RDFObjectTreeNode rdfPropertyNode=new RDFObjectTreeNode(property, value, getXMLifier());
				add(rdfPropertyNode); //add the property node to this resource node
			}
		}
	}

	/**@return A string representation to display as the tree node's label.*/
	public String toString()
	{
		final Object userObject=getUserObject();  //get the user object we're representing
		final RDFResource property=getProperty(); //see if the object should be considered in the context of a property
		final StringBuffer stringBuffer=new StringBuffer(); //create a new string buffer
		if(property!=null)  //if we we are the object of a property
		{
			stringBuffer.append(getXMLifier().getLabel(property)); //append "property:"
		}
		if(userObject instanceof RDFResource) //if we're representing a resource
		{
			final RDFResource resource=(RDFResource)userObject; //cast the user object to a resource
			final RDFResource type=RDFUtilities.getType(resource);  //get the type of the resource
			final RDFLiteral label=RDFSUtilities.getLabel(resource);	//get the label of the resource
			if(type!=null) //if we have a type
			{
				if(property!=null) //if we had a property
					stringBuffer.append(':').append(' '); //append ": " to separate the property from the type
				stringBuffer.append('(').append(getXMLifier().getLabel(type)).append(')'); //append "(type)"
			}
			if(label!=null)	//if there is a label
			{
				if(property!=null && type==null)  //if we had a property, but no type
					stringBuffer.append(':'); //append a colon to separate the property from the label
				if(property!=null || type!=null) //if we had either a property or a type
					stringBuffer.append(' '); //append a space to separate the property and/or type from the label
				stringBuffer.append(label);		//append the text of the label
			} 
			if(resource.getReferenceURI()!=null) //if there is no label and this is not a blank node resource
			{
				if(property!=null && type==null && label==null)  //if we had a property, but no type or label
					stringBuffer.append(':'); //append a colon to separate the property from the reference URI
				if(property!=null || type!=null || label!=null) //if we had a property or a type or a label
					stringBuffer.append(' '); //append a space to separate the property and/or type from the reference URI
				stringBuffer.append('[').append(getXMLifier().getLabel(resource)).append(']');  //append "[referenceURI]" label
//G***del when works			  stringBuffer.append('[').append(resource.getReferenceURI()).append(']');  //append "[referenceURI]"
			}
			return stringBuffer.toString(); //return the resource string we constructed
		}
		else if(userObject instanceof RDFLiteral) //if we're representing a literal
		{
			final RDFLiteral literal=(RDFLiteral)userObject;  //cast the user oject to an RDF literal
			if(property!=null) //if we had a property
				stringBuffer.append(':').append(' '); //append ": " to separate the property from the literal
			stringBuffer.append('"').append(literal.getLexicalForm()).append('"');  //append the literal value in quotes
			return stringBuffer.toString(); //return the literal string we constructed
		}
		else  //if we're representing neither a resource nor a literal (this should logically never happen)
			return super.toString();  //return the default string version
	}

}
package com.garretwilson.swing.rdf.tree;

import java.net.URI;
import java.util.*;
import com.garretwilson.swing.tree.*;
import com.garretwilson.rdf.*;

/**A tree node that represents an RDF data model.
	All resources in the RDF data model will be dynamically loaded.
@author Garret Wilson
*/
public class RDFTreeNode extends DynamicTreeNode
{

	/**The RDF XML-ifier to use for creating labels.*/
	private final RDFXMLifier xmlifier;

	  /**@return The RDF XML-ifier to use for creating labels.*/
		protected RDFXMLifier getXMLifier() {return xmlifier;}

	/**Convenience function for retrieving the represented RDF data model.
	@return The RDF data momdel this tree node represents, already cast to
		<code>RDF</code>.
	@see DefaultMutableTreeNode#getUserObject
	*/
	public RDF getRDF()
	{
		return (RDF)getUserObject();  //return the user object cast to an RDF
	}

	/**Constructs a tree node from an RDF data model.
	@param rdf The RDF data model represented by this node.
	@param rdfXMLifier The RDF XML-ifier to use for creating labels.
	*/
	public RDFTreeNode(final RDF rdf, final RDFXMLifier rdfXMLifier)
	{
		super(rdf); //store the RDF data model as the user object
		xmlifier=rdfXMLifier; //save the XMLifier we'll use for generating labels
	}

  /**@return <code>true</code> if the RDF data model has no resources.*/
	public boolean isLeaf() //G***fix to compensate for anonymous resources and resources with no properties
	{
		return getRDF().getResourceCount()==0;  //this is a leaf if there are no resources in this RDF data model
	}

	/**Dynamically loads child nodes for all resources.*/
	protected void loadChildNodes()
	{
		final Iterator resourceIterator=getRDF().getResourceIterator();  //get an iterator to all the RDF resources
		while(resourceIterator.hasNext()) //while there are resources remaining
		{
			final RDFResource resource=(RDFResource)resourceIterator.next();  //get the next resource
			final URI referenceURI=resource.getReferenceURI(); //get the resource reference URI
			//G***in throwing away anonymous resources, some might have been described at the top of the hierarchy---we should really just check to see which resources are not referenced
//G***check to see if there are no references to this node---if so, we'll probably want to display it anyway
//G***since this exact functionality occurs in RDFXMLifier, create it there and access those methods here
			if(!resource.isAnonymous() //if this is not an anonymous node
				  && resource.getPropertyCount()>0)  //if this resource actually has properties (even properties such as type identifiers are resources, but they don't have properties)
			{
					//create a new tree node to represent the resource
				final RDFObjectTreeNode rdfResourceNode=new RDFObjectTreeNode(resource, getXMLifier());
				add(rdfResourceNode); //add the resource node to this RDF data model node
			}
		}
	}

	/**@return A string representation to display as the RDF data model tree
		node's label.
	*/
	public String toString()
	{
		return "RDF"; //G***i18n
	}

}
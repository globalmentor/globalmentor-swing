package com.garretwilson.swing.rdf.tree;

import javax.swing.tree.*;
import com.garretwilson.rdf.*;
import com.garretwilson.rdf.rdfs.*;

/**A tree node that represents a resource described in RDF.
	The resource is stored as the user object of the tree node.
	The string representation of the node is the rdfs:label property of the
	resource, or the resource reference URI if there is no label present.
@author Garret Wilson
*/
public class RDFResourceTreeNode extends DefaultMutableTreeNode
{

	/**Convenience function for retrieving the represented RDF resource.
	@return The RDF resource this tree node represents, already cast to a
		<code>RDFResource</code>.
	@see DefaultMutableTreeNode#getUserObject
	*/
	public RDFResource getRDFResource()
	{
		return (RDFResource)getUserObject();  //return the user object cast to an RDF resource
	}

	/**Constructs a tree node from an RDF resource.
	@param rdfResource The resource to represent in the tree.
	*/
	public RDFResourceTreeNode(final RDFResource rdfResource)
	{
		super(rdfResource); //store the resource as the user object
	}

	/**@return A string representation to display as the tree node's label.*/
	public String toString()
	{
//G***del		final RDFResource rdfResource=(RDFResource)getUserObject(); //get the resource we're representing
		final RDFResource rdfResource=getRDFResource(); //get the resource we're representing
		final String label=RDFSUtilities.getLabel(rdfResource);  //get the label of the resource, if it has one
		return label!=null ? label : rdfResource.getReferenceURI(); //return the label, or the reference URI if there is no label
	}
}
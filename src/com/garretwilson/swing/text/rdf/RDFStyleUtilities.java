package com.garretwilson.swing.text.rdf;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import static com.garretwilson.swing.text.StyleUtilities.*;
import static com.garretwilson.swing.text.rdf.RDFStyleConstants.*;
import static com.globalmentor.java.Objects.*;

import com.globalmentor.rdf.RDFResource;

/**A collection of utilities for working with XML values used Swing style.
<p>These routines store XML attributes in the Swing attributes as an
	<code>XMLAttribute</code> object, keyed to the attribute name prefixed
	by the namespace URI and '$', if the attribute has a non-<code>null</code>
	namespace.</p>
<p>XML-specific attributes besides the XML attributes themselves are stored
	keyed to names beginning with '$'.</p>
@author Garret Wilson
@see com.garretwilson.swing.text.xml.XMLAttribute
*/
public class RDFStyleUtilities
{
	/**Gets the RDF resource the Swing attributes represent.
	The attribute is not resolved through the parent attribute set hierarchy.
	@param attributeSet The attribute set.
	@return The RDF resource, or <code>null</code> if the element has no RDF resource.
	*/
	public static RDFResource getRDFResource(final AttributeSet attributeSet)
	{
		return asInstance(getDefinedAttribute(attributeSet, RDF_RESOURCE_ATTRIBUTE_NAME), RDFResource.class);	//get the RDF resource
	}

	/**Sets the RDF resource represented by the attribute set.
	@param attributeSet The attribute set
	@param rdfResource The RDF resource.
	*/
	public static void setRDFResource(final MutableAttributeSet attributeSet, final RDFResource rdfResource)
	{
		attributeSet.addAttribute(RDF_RESOURCE_ATTRIBUTE_NAME, rdfResource);	//add the attribute to the attribute set
	}

}

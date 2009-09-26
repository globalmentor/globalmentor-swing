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

package com.globalmentor.swing.text.rdf;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import static com.globalmentor.java.Objects.*;
import static com.globalmentor.swing.text.Styles.*;

import com.globalmentor.rdf.RDFResource;
import com.globalmentor.swing.text.xml.XMLAttribute;

/**A collection of utilities for working with XML values used Swing style.
<p>These routines store XML attributes in the Swing attributes as an
	{@link XMLAttribute} object, keyed to the attribute name prefixed
	by the namespace URI and '$', if the attribute has a non-<code>null</code>
	namespace.</p>
<p>XML-specific attributes besides the XML attributes themselves are stored
	keyed to names beginning with '$'.</p>
@author Garret Wilson
@see com.globalmentor.swing.text.xml.XMLAttribute
*/
public class RDFStyles
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
		attributeSet.addAttribute(RDFStyles.RDF_RESOURCE_ATTRIBUTE_NAME, rdfResource);	//add the attribute to the attribute set
	}

	/**The RDF resource element this set of attributes represents.*/
	public final static String RDF_RESOURCE_ATTRIBUTE_NAME=SWING_ATTRIBUTE_START+"rdfResource";

}

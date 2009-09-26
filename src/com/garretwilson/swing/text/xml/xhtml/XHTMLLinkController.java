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

package com.garretwilson.swing.text.xml.xhtml;

import javax.swing.text.*;
import com.garretwilson.swing.text.xml.*;

import static com.globalmentor.text.xml.xhtml.XHTML.*;

/**Class to watch the associated component and fire hyperlink events on it
	when appropriate.
	<p>This class recognizes the XHTML anchor tag <code>&lt;a&gt;</code>, and
		also allows the parent class to properly interpret XLink links.</p>
*/
public class XHTMLLinkController extends XMLLinkController
{

	/**Default constructor.*/
	public XHTMLLinkController()
	{
	}

	/**Determines whether the specified element represents a link. This version
		returns <code>true</code> for every XHTML anchor element (<code>&lt;a&gt;</code>);
		for all other elements the parent class makes the decision.
	@param element The element in question.
	@return <code>true</code> if the specified element represents a link.
	*/
	protected boolean isLinkElement(final Element element)
	{
		final AttributeSet attributeSet=element.getAttributes();	//get the attributes of this element
		final String elementLocalName=XMLStyles.getXMLElementLocalName(attributeSet); //get the local name of this element
		if(ELEMENT_A.equals(elementLocalName)) //if this is the XHTML <a> element
			return true;	//show that this is a link element
		else	//if this is another element
			return super.isLinkElement(element);	//let the parent class decide
	}

	/**Gets the link href of the specified element if the specified element
		represents a link. This version returns the value of the <code>href</code>
		attribute of an anchor tag, and allows the parent class to determine the
		href for all other elements.
	@param element The element which may be a link element.
	@return The href of the link, or <code>null</code> if the element does not
		represent a link or if the element's href is not present.
	*/
	protected String getLinkElementHRef(final Element element)
	{
		final AttributeSet attributeSet=element.getAttributes();	//get the attributes of this element
		final String elementLocalName=XMLStyles.getXMLElementLocalName(attributeSet); //get the local name of this element
		if(ELEMENT_A.equals(elementLocalName))	//if this is an XHTML <a> element
			return XMLStyles.getXMLAttributeValue(attributeSet, null, ELEMENT_A_ATTRIBUTE_HREF);	//get the href value
		else	//if this is another element
			return super.getLinkElementHRef(element);	//let the parent class try to find an href
	}

}

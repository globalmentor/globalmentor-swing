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

import com.globalmentor.net.ContentType;
import com.globalmentor.net.ContentTypeConstants;

import static com.globalmentor.text.xml.xhtml.XHTML.*;

/**A factory to build views for elements from XHTML based upon the name and/or
	attributes of each element.
@author Garret Wilson
*/
public class XHTMLViewFactory extends XMLViewFactory
{

	/**A static application/java media type for quick reference in the view factory.*/
	protected final static ContentType APPLICATION_JAVA_MEDIA_TYPE=ContentType.getInstance(ContentType.APPLICATION_PRIMARY_TYPE, ContentTypeConstants.JAVA_SUBTYPE);

	/**Creates a view for the given element. If the element specifies a
		namespace and a view factory has been registered for the given namespace,
		the view creation will be delegated to the designated view factory.
		As this class implements <code>ViewsFactory</code>, which allows multiple
		views to be created, this method can optionally indicate multiple views
		are needed by returning <code>null</code>.
	@param element The element this view will represent.
	@param indicateMultipleViews Whether <code>null</code> should be returned to
		indicate multiple views should represent the given element.
	@return A view to represent the given element, or <code>null</code>
		indicating the element should be represented by multiple views.
	*/
	public View create(final Element element, final boolean indicateMultipleViews)
	{
//TODO del			final String elementName=(String)element.getAttributes().getAttribute(StyleConstants.NameAttribute);	//get the name of this element
		final AttributeSet attributeSet=element.getAttributes();  //get the element's attribute set
		if(attributeSet!=null)  //if we have an attribute set
		{
			final String elementLocalName=XMLStyles.getXMLElementLocalName(attributeSet); //get the local name of this element
			//if this element has a name, and it's not a paragraph or a page break (we let the XMLEditorKit create paragraphs and page breaks)
			if(elementLocalName!=null && /*TODO del !XMLStyleConstants.isParagraphView(attributeSet) && */!XMLStyles.isPageBreakView(attributeSet))  //TODO we may not need this line at all when this code gets even more elegant
			{
				if(elementLocalName.equals(ELEMENT_BR))	//if this is the XHTML <br> element
				{
//TODO del System.out.println("Adding a <br> view.");	//TODO del
					return new XMLLineBreakView(element);	//create a line break view to represent the <br> element
				}
				else if(elementLocalName.equals(ELEMENT_HR))	//if this is the XHTML <hr> element
				{
					return new XMLHorizontalRuleView(element);	//TODO testing hr
				}
				else if(elementLocalName.equals(ELEMENT_IMG))	//if this is the XHTML <img> element
				{
//TODO del Log.trace("found <img>, ready to create an XHTMLImageView");	//TODO del
					return new XHTMLImageView(element);	//create an XHTML image view
				}
				else if(elementLocalName.equals(ELEMENT_OBJECT))	//if this is the XHTML <object> element
				{
//TODO del Log.trace("found <object>");	//TODO del
					Element recognizedObjectElement=element;  //start out hoping that we recognize the media type of the object
					while(recognizedObjectElement!=null)  //while we're still trying to find a recognized object
					{
								//get the media type of this object
						final ContentType mediaType=XHTMLSwingText.getObjectMediaType(recognizedObjectElement.getAttributes());
						if(XHTMLSwingText.isMediaTypeSupported(mediaType)) //if we support this media type
						{
							if(mediaType.match(APPLICATION_JAVA_MEDIA_TYPE))  //if this is a Java applet
							{
								return new XHTMLAppletView(element);	//return an applet view for this object element
							}
							else if(mediaType.getPrimaryType().equals(ContentType.IMAGE_PRIMARY_TYPE)) //if this is an image
							{
								return new XHTMLImageView(element, recognizedObjectElement);	//return an image view for the object element, using the recognized object for initialization TODO should we only pass an initialization attribute set?
							}
						}
						else  //since we don't support this media type, try to find a sub-object with a recognized media type
						{
							final Element currentElement=recognizedObjectElement; //make a copy of our reference to the current object
							recognizedObjectElement=null; //set our recognized object element to null in case we can't find child object
							final int childElementCount=currentElement.getElementCount(); //find out how many child elements there are
							for(int i=0; i<childElementCount; ++i)  //look at each child element
							{
								final Element childElement=currentElement.getElement(i); //get a reference to this child element
									//get the child element's name
								final String childElementName=XMLStyles.getXMLElementName(childElement.getAttributes());
								if(ELEMENT_OBJECT.equals(childElementName))  //if this child is an object
								{
									recognizedObjectElement=childElement; //we'll start over and see if we recognize this child object's media type
									break;  //stop looking for a child element TODO what about multiple first-level child objects? cuurrently we take the first one
								}
							}
						}
					}
					return new XMLParagraphView(element); //if we don't recognize any of the objects, just show them as a paragraph TODO this is close but not quite the correct way to do things
				}
				else if(elementLocalName.equals(ELEMENT_OL))	//if this is the XHTML <ol> element
				{
					return new XMLListView(element, View.Y_AXIS, true);	//create an ordered list view
				}
				else if(elementLocalName.equals(ELEMENT_UL))	//if this is the XHTML <ul> element
				{
					return new XMLListView(element, View.Y_AXIS, false);	//create an unordered list view
				}					
			}
		}
		return super.create(element, indicateMultipleViews);	//if we couldn't figure out which kind of view to create, let the parent class decide what to do
	}
}

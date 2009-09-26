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

import java.io.File;
import java.net.URI;

import javax.swing.text.*;
import com.garretwilson.swing.text.xml.XMLStyles;

import static com.globalmentor.text.xml.xhtml.XHTML.*;

import com.globalmentor.io.*;
import com.globalmentor.net.ContentType;
import com.globalmentor.net.ContentTypeConstants;

/**Provides utility functions to manipulate Swing text classes representing XHTML.
@author Garret Wilson
*/
public class XHTMLSwingText
{

	/**This class cannot be publicly instantiated.*/
	private XHTMLSwingText() {}


	/**Gets the element in the hierarchy responsible for the given position that
		represents an image, if any. The current implementation assumes that an
		image can only be the element directly above a character element.
	@param defaultStyledDocument The document containing the position.
	@param pos The position in the document (>=0).
	@return The element representing an image, or <code>null</code> if no elements
		at the given position represent an image.
	@see #isImageElement
	*/
	public static Element getImageElement(final DefaultStyledDocument defaultStyledDocument, final int pos)
	{
    final Element element=defaultStyledDocument.getCharacterElement(pos); //get the leaf element at this position
		if(element!=null) //if we found a character element
		{
			final Element parentElement=element.getParentElement(); //get the parent of the character element
		  if(isImage(parentElement.getAttributes())) //if the parent element is an image
				return parentElement; //return the parent element
		}
		return null;  //show that we did not find an image element at that position
	}

	/**Determines if the specified attribute set represents an image.
		Specifically, this returns <code>true</code> if the XML element's name is
		"img"; or if the element's name is "object" and the type attribute is an
		image or the data attribute references an image file.
	@param attributeSet The attribute set which might represent an image.
	@return <code>true</code> if the specified attribute set represents an image,
		<code>false</code> if otherwise or the specified attribute set was
		<code>null</code>.
	*/
	public static boolean isImage(final AttributeSet attributeSet)  //TODO this should really be in XHTMLSwingTextUtilities
	{
		if(attributeSet!=null) //if a valid attribute set is passed
		{
			final String elementName=XMLStyles.getXMLElementName(attributeSet); //get the name of this element
			if(elementName!=null) //if there is an element name
			{
				if(elementName.equals(ELEMENT_IMG))	//if this is an <img> element
					return true;  //show that this is an image object
				else if(elementName.equals(ELEMENT_OBJECT)) //if this is an <object> element
				{
					final ContentType mediaType=getObjectMediaType(attributeSet); //get the media type of the object
					if(mediaType.getPrimaryType().equals(ContentType.IMAGE_PRIMARY_TYPE)) //if this is an image
						return true;  //show that this is an image object
				}
			}
		}
		return false; //this does not appear to be an image element
	}

	/**Determines if the specified attribute set represents an HTML document.
		An element is considered to be a document element if one of the following
			are true:
		<ul>
			<li>The document has an HTML media type.</li>
			<li>The document element is an <code>&lt;html&gt;</code> element in an
				HTML namespace.</li>
		</ul>
	@param documentAttributeSet The document element's attribute set.
	@return <code>true</code> if the specified attribute set represents an XHTML
		or OEB element, or <code>false</code> if otherwise or the specified
		attribute set was <code>null</code>.
	@see XHTMLUtilities#isHTML(MediaType)
	@see XHTML#isHTMLNamespaceURI(URI)
	*/
	public static boolean isHTMLDocumentElement(final AttributeSet documentAttributeSet)
	{
		final ContentType documentMediaType=XMLStyles.getMediaType(documentAttributeSet);  //get the media type of the document
			//if the document media type is an HTML media type
		if(isHTML(documentMediaType)) 
		{
			return true;  //this is an HTML media type
		}
		else  //if the media type isn't HTML
		{
			final String documentElementLocalName=XMLStyles.getXMLElementLocalName(documentAttributeSet);  //get the document element local name
			final String documentElementNamespaceURIString=XMLStyles.getXMLElementNamespaceURI(documentAttributeSet);  //get the document element namespace URI
			final URI documentElementNamespaceURI=documentElementNamespaceURIString!=null ? URI.create(documentElementNamespaceURIString) : null;
				//TODO change all the XMLStyleUtilities.getXMLElementNamespaceURI() calls to return URIs
			if(ELEMENT_HTML.equals(documentElementLocalName)  //if the document element is an XHTML or OEB <html> element
					&& isHTMLNamespaceURI(documentElementNamespaceURI))	//if the document namespace URI represents an XHTML namespace 
			{
				return true;  //the document element is <html> in the HTML namespace
			}
		}
		return false; //the element doesn't seem to be an HTML document
	}

	/**Determines if the specified attribute set represents an HTML element.
		An element is considered to be an HTML element if one of the following
			are true:
		<ul>
			<li>The element is in an HTML namespace.</li>
			<li>The element is in no namespace but the document has an HTML
				media type.</li>
		  <li>The element is in no namespace but the document element is an
				<code>&lt;html&gt;</code> element in an HTML namespace.</li>
		</ul>
	@param attributeSet The attribute set which might represent an HTML element.
	@param documentAttributeSet The document element's attribute set.
	@return <code>true</code> if the specified attribute set represents an XHTML
		or OEB element, or <code>false</code> if otherwise or the specified
		attribute set was <code>null</code>.
	@see XHTMLUtilities#isHTML(MediaType)
	@see XHTML#isHTMLNamespaceURI(URI)
	@see #isHTMLDocumentElement(AttributeSet)
	*/
	public static boolean isHTMLElement(final AttributeSet attributeSet, final AttributeSet documentAttributeSet)
	{
		final String elementNamespaceURI=XMLStyles.getXMLElementNamespaceURI(attributeSet);  //get the element namespace
		if(elementNamespaceURI!=null)  //if the element has a namespace
		{
				//TODO change all the XMLStyleUtilities.getXMLElementNamespaceURI() calls to return URIs
			if(isHTMLNamespaceURI(URI.create(elementNamespaceURI)))	//if the URI represents an XHTML namespace 
				return true;  //show that this is an XHTML element
		}
		else if(isHTMLDocumentElement(documentAttributeSet))	//if the element has no namespace but the document is an HTML document
		{
			return true;	//this element is in an HTML document
		}
		return false; //the element doesn't seem to be HTML
	}

	/**Determines the reference to the image file represented by the attribute set,
		assuming the XML element (an "img" or "object" element) the attribute set
		represents does in fact represent an image.
		For "img", this return the "src" attribute. For objects, the value of the
		"data" attribute is returned.
	@param attributeSet The attribute set which contains the image information.
	@return The reference to the image file, or <code>null</code> if no reference
		could be found.
	*/
	public static String getImageHRef(final AttributeSet attributeSet)
	{
		if(attributeSet!=null) //if a valid attribute set is passed
		{
			final String elementName=XMLStyles.getXMLElementName(attributeSet); //get the name of this element
			if(elementName!=null) //if there is an element name TODO use ELEMENT_IMG.equals(), etc. so that this line is unecessary
			{
					//see if this is an <img> or <object> element
				if(elementName.equals(ELEMENT_IMG)) //if the corresponding element is an img element
				{
						//get the src attribute, representing the href of the image, or null if not present
					return XMLStyles.getXMLAttributeValue(attributeSet, null, ELEMENT_IMG_ATTRIBUTE_SRC);
				}
				else if(elementName.equals(ELEMENT_OBJECT)) //if the corresponding element is an object element
				{
						//get the data attribute, representing the href of the image, or null if not present
					return XMLStyles.getXMLAttributeValue(attributeSet, null, ELEMENT_OBJECT_ATTRIBUTE_DATA);
				}
			}
		}
		return null;  //show that we couldn't find an image reference
	}

	/**Determines the media type of the given attribute set.
		It is assumed that the given attribute set is in fact for an object element.
	@param attributeSet The attribute set which represents an
		<code>&lt;object&gt;</code>.
	@return The media type of the data specified by the object attribute set,
		<code>null</code> if the media type could not be determined.
	*/
	public static ContentType getObjectMediaType(final AttributeSet attributeSet)
	{
		if(attributeSet!=null)  //if there are attributes
		{
				//see if there is a code type attribute
			final String codeType=XMLStyles.getXMLAttributeValue(attributeSet, null, ELEMENT_OBJECT_ATTRIBUTE_CODETYPE);
			if(codeType!=null)  //if the object has a code type
				return ContentType.getInstance(codeType); //create a media type from the given type
				//see if there is a type attribute, since there is no code type specified
			final String type=XMLStyles.getXMLAttributeValue(attributeSet, null, ELEMENT_OBJECT_ATTRIBUTE_TYPE);
			if(type!=null)  //if the object has a code type
				return ContentType.getInstance(type); //create a media type from the given type and return it
				//see if there is a data attribute, since there is no type specified
			final String data=XMLStyles.getXMLAttributeValue(attributeSet, null, ELEMENT_OBJECT_ATTRIBUTE_DATA);
			if(data!=null)  //if the object has a data attribute
				return Files.getContentType(new File(data)); //try to get a media type from the file and return it
		}
		return null; //we could not determine a media type for this object
	}

	/**Determines if the given media type is recognized and supported.
	@param mediaType The media type to test.
	@return <code>true</code> if the media type is recognized and supported.
	*/
	public static boolean isMediaTypeSupported(final ContentType mediaType) //TODO update these for all media types
	{
			//TODO we should really probably just have a map or something -- or maybe not (this is probably more lightweight)
		final String topLevelType=mediaType.getPrimaryType();  //get the top-level type
		final String subType=mediaType.getSubType();  //get the subtype
		if(ContentType.APPLICATION_PRIMARY_TYPE.equals(topLevelType))  //application/*
		{
			if(ContentTypeConstants.JAVA_SUBTYPE.equals(subType))  //application/java
				return true;  //we support this media type
		}
		else if(ContentType.IMAGE_PRIMARY_TYPE.equals(topLevelType))  //image/*
		{
			if(ContentTypeConstants.GIF_SUBTYPE.equals(subType)  //image/gif
				  || ContentTypeConstants.PNG_SUBTYPE.equals(subType)  //image/png
				  || ContentTypeConstants.JPEG_SUBTYPE.equals(subType))  //image/jpeg
				return true;  //we support this media type
		}
		return false; //show that we don't understand this media type
	}

}
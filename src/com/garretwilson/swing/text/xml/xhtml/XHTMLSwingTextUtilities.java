package com.garretwilson.swing.text.xml.xhtml;

import java.io.File;
import javax.swing.text.*;
import com.garretwilson.io.FileUtilities;
import com.garretwilson.io.MediaType;
import com.garretwilson.swing.text.xml.XMLStyleUtilities;
import com.garretwilson.text.xml.oeb.OEBConstants;
import com.garretwilson.text.xml.xhtml.XHTMLConstants;
import com.garretwilson.util.Debug;

/**Provides utility functions to manipulate Swing text classes.
@author Garret Wilson
*/
public class XHTMLSwingTextUtilities implements XHTMLConstants
{

	/**This class cannot be publicly instantiated.*/
	private XHTMLSwingTextUtilities() {}


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
	public static boolean isImage(final AttributeSet attributeSet)  //G***this should really be in XHTMLSwingTextUtilities
	{
		if(attributeSet!=null) //if a valid attribute set is passed
		{
			final String elementName=XMLStyleUtilities.getXMLElementName(attributeSet); //get the name of this element
			if(elementName!=null) //if there is an element name
			{
				if(elementName.equals(ELEMENT_IMG))	//if this is an <img> element
					return true;  //show that this is an image object
				else if(elementName.equals(ELEMENT_OBJECT)) //if this is an <object> element
				{
					final MediaType mediaType=getObjectMediaType(attributeSet); //get the media type of the object
					if(mediaType.getTopLevelType().equals(MediaType.IMAGE)) //if this is an image
						return true;  //show that this is an image object
				}
			}
		}
		return false; //this does not appear to be an image element
	}

	/**Determines if the specified attribute set represents an HTML element.
		An element is considered to be an HTML element if:
		<ul>
			<li>The element is in the XHTML or OEB namespace.</li>
			<li>The element is in no namespace but the document is of type
				<code>text/html</code>, <code>application/xhtml+xml</code>,
				or <code>text/x-oeb1-document</code>.</li>
		  <li>The element is in no namespace and the document element is an
				<code>&lt;html&gt;</code> element in the XHTML or OEB namespace.</li>
		</ul>
	@param attributeSet The attribute set which might represent an HTML element.
	@param documentAttributeSet The document element's attribute set.
	@return <code>true</code> if the specified attribute set represents an XHTML
		or OEB element, or <code>false</code> if otherwise or the specified
		attribute set was <code>null</code>.
	*/
	public static boolean isHTML(final AttributeSet attributeSet, final AttributeSet documentAttributeSet)
	{
		final String elementNamespaceURI=XMLStyleUtilities.getXMLElementNamespaceURI(attributeSet);  //get the element namespace
		final String elementLocalName=XMLStyleUtilities.getXMLElementLocalName(attributeSet);  //get the element local name
		if(elementNamespaceURI!=null)  //if the element has a namespace
		{
				//if it's part of the XHTML or OEB namespace
			if(XHTMLConstants.XHTML_NAMESPACE_URI.equals(elementNamespaceURI)
					|| OEBConstants.OEB1_DOCUMENT_NAMESPACE_URI.equals(elementNamespaceURI))
				return true;  //show that this is an XHTML element
		}
		else  //if the body element has no namespace
		{
		  final MediaType documentMediaType=XMLStyleUtilities.getMediaType(documentAttributeSet);  //get the media type of the document
				//if the document type is text/html or text/x-oeb1-document or application/xhtml+xml
			if(documentMediaType!=null && (documentMediaType.equals(MediaType.TEXT_HTML)
				|| documentMediaType.equals(MediaType.APPLICATION_XHTML_XML)
				|| documentMediaType.equals(MediaType.TEXT_X_OEB1_DOCUMENT)))
			{
				return true;  //this is an HTML media type
			}
			else  //if the media type isn't HTML
			{
				final String documentElementLocalName=XMLStyleUtilities.getXMLElementLocalName(documentAttributeSet);  //get the document element local name
				final String documentElementNamespaceURI=XMLStyleUtilities.getXMLElementNamespaceURI(documentAttributeSet);  //get the document element local name
				if(XHTMLConstants.ELEMENT_HTML.equals(documentElementLocalName)  //if the document element is an XHTML or OEB <html> element
					&& (XHTMLConstants.XHTML_NAMESPACE_URI.equals(documentElementNamespaceURI) || OEBConstants.OEB1_DOCUMENT_NAMESPACE_URI.equals(documentElementNamespaceURI)))
				{
					return true;  //the document element is <html> in the HTML namespace
				}
			}
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
			final String elementName=XMLStyleUtilities.getXMLElementName(attributeSet); //get the name of this element
			if(elementName!=null) //if there is an element name G***use ELEMENT_IMG.equals(), etc. so that this line is unecessary
			{
					//see if this is an <img> or <object> element
				if(elementName.equals(ELEMENT_IMG)) //if the corresponding element is an img element
				{
						//get the src attribute, representing the href of the image, or null if not present
					return XMLStyleUtilities.getXMLAttributeValue(attributeSet, null, ELEMENT_IMG_ATTRIBUTE_SRC);
				}
				else if(elementName.equals(ELEMENT_OBJECT)) //if the corresponding element is an object element
				{
						//get the data attribute, representing the href of the image, or null if not present
					return XMLStyleUtilities.getXMLAttributeValue(attributeSet, null, ELEMENT_OBJECT_ATTRIBUTE_DATA);
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
	public static MediaType getObjectMediaType(final AttributeSet attributeSet)
	{
		if(attributeSet!=null)  //if there are attributes
		{
				//see if there is a code type attribute
			final String codeType=XMLStyleUtilities.getXMLAttributeValue(attributeSet, null, ELEMENT_OBJECT_ATTRIBUTE_CODETYPE);
			if(codeType!=null)  //if the object has a code type
				return new MediaType(codeType); //create a media type from the given type
				//see if there is a type attribute, since there is no code type specified
			final String type=XMLStyleUtilities.getXMLAttributeValue(attributeSet, null, ELEMENT_OBJECT_ATTRIBUTE_TYPE);
			if(type!=null)  //if the object has a code type
				return new MediaType(type); //create a media type from the given type and return it
				//see if there is a data attribute, since there is no type specified
			final String data=XMLStyleUtilities.getXMLAttributeValue(attributeSet, null, ELEMENT_OBJECT_ATTRIBUTE_DATA);
			if(data!=null)  //if the object has a data attribute
				return FileUtilities.getMediaType(new File(data)); //try to get a media type from the file and return it
		}
		return null; //we could not determine a media type for this object
	}

	/**Determines if the given media type is recognized and supported.
	@param mediaType The media type to test.
	@return <code>true</code> if the media type is recognized and supported.
	*/
	public static boolean isMediaTypeSupported(final MediaType mediaType) //G***update these for all media types
	{
			//G***we should really probably just have a map or something -- or maybe not (this is probably more lightweight)
		final String topLevelType=mediaType.getTopLevelType();  //get the top-level type
		final String subType=mediaType.getSubtype();  //get the subtype
		if(MediaType.APPLICATION.equals(topLevelType))  //application/*
		{
			if(MediaType.JAVA.equals(subType))  //application/java
				return true;  //we support this media type
		}
		else if(MediaType.IMAGE.equals(topLevelType))  //image/*
		{
			if(MediaType.GIF.equals(subType)  //image/gif
				  || MediaType.PNG.equals(subType)  //image/png
				  || MediaType.JPEG.equals(subType))  //image/jpeg
				return true;  //we support this media type
		}
		return false; //show that we don't understand this media type
	}

}
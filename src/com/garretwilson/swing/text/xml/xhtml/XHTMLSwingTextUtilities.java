package com.garretwilson.swing.text.xml.xhtml;

import java.io.File;
import java.net.URI;

import javax.swing.text.*;
import com.garretwilson.io.FileUtilities;
import com.garretwilson.io.MediaType;
import com.garretwilson.swing.text.xml.XMLStyleUtilities;
import com.garretwilson.text.xml.oeb.OEBConstants;
import com.garretwilson.text.xml.xhtml.XHTMLConstants;
import com.garretwilson.text.xml.xhtml.XHTMLUtilities;
import com.garretwilson.util.Debug;

/**Provides utility functions to manipulate Swing text classes representing XHTML.
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

	/**Returns all child elements for which views should be created. If
		a paged view holds multiple documents, for example, the children of those
		document elements will be included. An XHTML document, furthermore, will
		return the contents of its <code>&lt;body&gt;</code> element.
		It is assumed that the ranges precisely enclose any child elements within
		that range, so any elements that start within the given range will be
		included.
	@param newStartOffset This range's starting offset.
	@param newEndOffset This range's ending offset.
	@return An array of elements for which views should be created.
	*/
/*G***del when works
	protected boolean isHTMLDocumentElement(final AttributeSet documentAttributeSet)
	{
		final Element element=getElement(); //get a reference to our element
		final int documentElementCount=element.getElementCount();  //find out how many child elements there are (representing XML documents)
		for(int documentElementIndex=0; documentElementIndex<documentElementCount; ++documentElementIndex) //look at each element representing an XML document
		{
			final Element documentElement=element.getElement(documentElementIndex); //get a reference to this child element
				//if this document starts within our range
			if(documentElement.getStartOffset()>=startOffset && documentElement.getStartOffset()<endOffset)
			{
				final AttributeSet documentAttributeSet=documentElement.getAttributes();  //get the attributes of the document element
				if(XMLStyleUtilities.isPageBreakView(documentAttributeSet)) //if this is a page break element
				{
	Debug.trace("found page break view"); //G***del
					viewChildElementList.add(documentElement);  //add this element to our list of elements; it's not a top-level document like the others G***this is a terrible hack; fix
				}
				else
				{
	//G***del if not needed				Element baseElement=documentElement;  //we'll find out which element to use as the parent; in most documents, that will be the document element; in HTML elements, it will be the <body> element
					final MediaType documentMediaType=XMLStyleUtilities.getMediaType(documentAttributeSet);  //get the media type of the document
					final String documentElementLocalName=XMLStyleUtilities.getXMLElementLocalName(documentAttributeSet);  //get the document element local name
					final String documentElementNamespaceURI=XMLStyleUtilities.getXMLElementNamespaceURI(documentAttributeSet);  //get the document element local name
					final int childElementCount=documentElement.getElementCount();  //find out how many children are in the document
					for(int childIndex=0; childIndex<childElementCount; ++childIndex)  //look at the children of the document element
					{
						final Element childElement=documentElement.getElement(childIndex); //get a reference to the child element
						if(childElement.getStartOffset()>=startOffset && childElement.getStartOffset()<endOffset) //if this child element starts within our range
						{
							final AttributeSet childAttributeSet=childElement.getAttributes();  //get the child element's attributes
							final String childElementLocalName=XMLStyleUtilities.getXMLElementLocalName(childAttributeSet);  //get the child element local name
		Debug.trace("Looking at child: ", childElementLocalName); //G***del
							boolean isHTMLBody=false; //we'll determine if this element is a <body> element of XHTML
							if(XHTMLConstants.ELEMENT_BODY.equals(childElementLocalName))  //if this element is "body"
							{
								//we'll determine if this body element is HTML by one of following:
								//  * the element is in the XHTML or OEB namespace
								//  * the element is in no namespace but the document is of type text/html or text/x-oeb1-document
								//  * the element is in no namespace and the document element is
								//      an <html> element in the XHTML or OEB namespace
								final String childElementNamespaceURI=XMLStyleUtilities.getXMLElementNamespaceURI(childAttributeSet);  //get the child element local name
								if(childElementNamespaceURI!=null)  //if the body element has a namespace
								{
										//if it's part of the XHTML or OEB namespace
									if(XHTMLConstants.XHTML_NAMESPACE_URI.equals(childElementNamespaceURI)
											|| OEBConstants.OEB1_DOCUMENT_NAMESPACE_URI.equals(childElementNamespaceURI))
										isHTMLBody=true;  //show that this is an HTML body element
								}
								else  //if the body element has no namespace
								{
										//if the document type is text/html or text/x-oeb1-document
									if(documentMediaType!=null && (documentMediaType.equals(MediaType.TEXT_HTML) || documentMediaType.equals(MediaType.TEXT_X_OEB1_DOCUMENT)))
										isHTMLBody=true;  //is an HTML body element
									else if(XHTMLConstants.ELEMENT_HTML.equals(documentElementLocalName)  //if the document element is an XHTML or OEB <html> element
											&& (XHTMLConstants.XHTML_NAMESPACE_URI.equals(documentElementNamespaceURI) || OEBConstants.OEB1_DOCUMENT_NAMESPACE_URI.equals(documentElementNamespaceURI)))
										isHTMLBody=true;  //is an HTML body element
								}
							}
							if(isHTMLBody)  //if this element is an XHTML <body> element
							{
								final int bodyChildElementCount=childElement.getElementCount(); //find out how many children the body element has
								for(int bodyChildIndex=0; bodyChildIndex<bodyChildElementCount; ++bodyChildIndex) //look at each of the body element's children
								{
		Debug.trace("Adding body child element: ", bodyChildIndex);
									final Element bodyChildElement=childElement.getElement(bodyChildIndex); //get this child element of the body element
									if(bodyChildElement.getStartOffset()>=startOffset && bodyChildElement.getStartOffset()<endOffset) //if this child element starts within our range
										viewChildElementList.add(bodyChildElement);  //add this body child element to our list of elements
								}
							}
							else  //if this element is not an XHTML <body> element
							{
		Debug.trace("Adding child element: ", childIndex);
								viewChildElementList.add(childElement);  //add this child element to our list of elements
							}
						}
					}
				}
			}
		}
		return (Element[])viewChildElementList.toArray(new Element[viewChildElementList.size()]); //return the views as an array of views
	}
*/

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
	@see XHTMLUtilities#isHTMLNamespaceURI(URI)
	*/
	public static boolean isHTMLDocumentElement(final AttributeSet documentAttributeSet)
	{
		final MediaType documentMediaType=XMLStyleUtilities.getMediaType(documentAttributeSet);  //get the media type of the document
			//if the document media type is an HTML media type
		if(XHTMLUtilities.isHTML(documentMediaType)) 
		{
			return true;  //this is an HTML media type
		}
		else  //if the media type isn't HTML
		{
			final String documentElementLocalName=XMLStyleUtilities.getXMLElementLocalName(documentAttributeSet);  //get the document element local name
			final String documentElementNamespaceURI=XMLStyleUtilities.getXMLElementNamespaceURI(documentAttributeSet);  //get the document element namespace URI
				//TODO change all the XMLStyleUtilities.getXMLElementNamespaceURI() calls to return URIs
			if(XHTMLConstants.ELEMENT_HTML.equals(documentElementLocalName)  //if the document element is an XHTML or OEB <html> element
					&& XHTMLUtilities.isHTMLNamespaceURI(URI.create(documentElementNamespaceURI)))	//if the document namespace URI represents an XHTML namespace 
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
	@see XHTMLUtilities#isHTMLNamespaceURI(URI)
	@see #isHTMLDocumentElement(AttributeSet)
	*/
	public static boolean isHTMLElement(final AttributeSet attributeSet, final AttributeSet documentAttributeSet)
	{
		final String elementNamespaceURI=XMLStyleUtilities.getXMLElementNamespaceURI(attributeSet);  //get the element namespace
		if(elementNamespaceURI!=null)  //if the element has a namespace
		{
				//TODO change all the XMLStyleUtilities.getXMLElementNamespaceURI() calls to return URIs
			if(XHTMLUtilities.isHTMLNamespaceURI(URI.create(elementNamespaceURI)))	//if the URI represents an XHTML namespace 
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
package com.garretwilson.swing.text.xml;

import java.net.URI;
import java.net.URISyntaxException;
import javax.mail.internet.ContentType;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import com.garretwilson.swing.text.StyleUtilities;
import com.garretwilson.swing.text.xml.css.XMLCSSStyleUtilities;
import com.garretwilson.text.xml.XMLUtilities;
import com.garretwilson.util.NameValuePair;

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
public class XMLStyleUtilities extends StyleUtilities implements XMLStyleConstants
{

	/**An empty array of name/value pairs to return when, for example, no
		processing instructions are available.
	*/
	protected final static NameValuePair[] EMPTY_NAME_VALUE_PAIR_ARRAY=new NameValuePair[]{};

	/**The delimiter used for forming combined namespaceURI+localName names for XML attributes.*/
	protected final static char ATTRIBUTE_NAMESPACE_DELIMITER='$';

	/**Searches up the element hierarchy for an element with the given local
		name. The given element itself is not checked.
	@param element The element the ancestors of which should be checked.
	@param localName The local name of the element to find. G***maybe pass a namespace URI as well
	@return The ancestor element with the given XML local name, or <code>null</code>
		if there is no such ancestor element.
	*/
	public static Element getAncestorElement(final Element element, final String localName) //G***probably put this in a better utility class
	{
		Element parentElement=element.getParentElement(); //get the element's parent
		while(parentElement!=null)  //keep looking up the hierarchy until we run out of parents
		{
			final AttributeSet attributeSet=parentElement.getAttributes();  //get the parent element's attribute set
			//G***we should probably make sure this element is in our namespace
			final String elementLocalName=getXMLElementLocalName(attributeSet); //get the local name of this element
			if(localName.equals(elementLocalName))  //if this element has the correct local name
				break;  //we've found the correct ancestor; stop looking
			parentElement=parentElement.getParentElement(); //look at this element's parent
		}
		return parentElement; //return the parent element, which will be null if we didn't find a match
	}

	/**Searches for a direct child element with the given local name.
	@param element The element the children of which should be checked.
	@param localName The local name of the element to find. G***maybe pass a namespace URI as well
	@return The first child element with the given XML local name, or
		<code>null</code> if there is no such child element.
	*/
	public static Element getChildElement(final Element element, final String localName) //G***probably put this in a better utility class
	{
		final int childElementCount=element.getElementCount(); //find out how many child elements there are
		for(int i=0; i<childElementCount; ++i) //look at each child element
		{
			final Element childElement=element.getElement(i); //get a reference to this child element
			final AttributeSet childAttributeSet=childElement.getAttributes();  //get the child element's attribute set
			//G***we should probably make sure this element is in our namespace
			final String elementLocalName=getXMLElementLocalName(childAttributeSet); //get the local name of this element
			if(localName.equals(elementLocalName))  //if this element has the correct local name
				return childElement;  //return the child element we found
		}
		return null;  //show that we couldn't find a matching child element
	}

	/**The name of the map of XML attributes.*/
//G***fix	public final static String XMLAttributesAttributeName="XMLAttributes";

	/**Returns an XML attribute with the given namespace and local name from the attribute set.
		The attribute is resolved by searching the parent attribute set hierarchy.
	@param attributeSet The attribute set, which may be <code>null</code>.
	@param attributeNamespaceURI The namespace URI of the XML attribute.
	@param attributeLocalName The local name of the XML attribute.
	@return The attribute if the attribute is defined up the hierarchy, else <code>null</code>.
	*/
/*G***del if not needed
	public static Object getDefinedAttribute(AttributeSet attributeSet, final String attributeNamespaceURI, final String attributeLocalName)
	{
		return attributeSet!=null ? //make sure there is an attributeset
			(attributeSet.isDefined(attributeName) ? attributeSet.getAttribute(attributeName) : null) :
			null;	//return the attribute if it is defined, or null if it isn't
	}
*/


	/**Determines the name to use in Swing for an XML attribute namespace and local name.
	This name is calculated by prepending the local name with the namespace plus a delimiter
		if a namespace is present.
	@param attributeNamespaceURI The namespace URI of the XML attribute.
	@param attributeLocalName The local name of the XML attribute.
	@return A string suitable for a Swing attribute name to represent an XML attribute.
	@see #ATTRIBUTE_NAMESPACE_DELIMITER
	*/
	protected static String getSwingXMLAttributeName(final String attributeNamespaceURI, final String attributeLocalName)
	{
			//if there is a namespace URI, append it to the local name separated by the delimiter
		return attributeNamespaceURI!=null ? attributeNamespaceURI+ATTRIBUTE_NAMESPACE_DELIMITER+attributeLocalName : attributeLocalName;
	}

	/**Checks to see if an XML attribute with the given namespace and local name exists
		in the given attribute set.
	The attribute is not resolved by searching the parent attribute set hierarchy.
	@param attributeSet The attribute set, which may be <code>null</code>.
	@param attributeNamespaceURI The namespace URI of the XML attribute.
	@param attributeLocalName The local name of the XML attribute.
	@return <code>true</code> if the XML attribute exists in this attribute set, else <code>null</code>.
	*/
	public static boolean isXMLAttributeDefined(final AttributeSet attributeSet, final String attributeNamespaceURI, final String attributeLocalName)
	{
			//see if an attribute with the XML name appears in the attribute set
		return attributeSet.isDefined(getSwingXMLAttributeName(attributeNamespaceURI, attributeLocalName));
	}

	/**Returns an XML attribute with the given namespace and local name from the attribute set.
		The attribute is not resolved by searching the parent attribute set hierarchy.
	@param attributeSet The attribute set, which may be <code>null</code>.
	@param attributeNamespaceURI The namespace URI of the XML attribute.
	@param attributeLocalName The local name of the XML attribute.
	@return The attribute if the attribute is defined in this attribute set, else <code>null</code>.
	*/
	public static XMLAttribute getXMLAttribute(final AttributeSet attributeSet, final String attributeNamespaceURI, final String attributeLocalName)
	{
			//see if an attribute with the XML name appears in the attribute set
		return (XMLAttribute)getDefinedAttribute(attributeSet, getSwingXMLAttributeName(attributeNamespaceURI, attributeLocalName));
	}

	/**Returns an XML attribute with the given namespace and local name from the attribute set.
		The attribute is not resolved by searching the parent attribute set hierarchy.
	@param attributeSet The attribute set, which may be <code>null</code>.
	@param attributeNamespaceURI The namespace URI of the XML attribute.
	@param attributeLocalName The local name of the XML attribute.
	@return The attribute if the attribute is defined in this attribute set, else <code>null</code>.
	*/
	public static String getXMLAttributeValue(final AttributeSet attributeSet, final String attributeNamespaceURI, final String attributeLocalName)
	{
		final XMLAttribute xmlAttribute=getXMLAttribute(attributeSet, attributeNamespaceURI, attributeLocalName);	//get this attribute, if there is one
		return xmlAttribute!=null ? xmlAttribute.getValue() : null;	//return the value of the XML attribute, if there is such an attribute
	}

	/**Adds an XML attribute with the given namespace and qualified name the attribute set.
	@param attributeSet The attribute set.
	@param attributeNamespaceURI The namespace URI of the XML attribute.
	@param attributeQualifiedName The qualified name of the XML attribute.
	@param attributeValue The value of the XML attribute to add. 
	*/
	public static void addXMLAttribute(final MutableAttributeSet attributeSet, final String attributeNamespaceURI, final String attributeQualifiedName, final String attributeValue)
	{
			//create an object to represent this XML attribute and value
		final XMLAttribute xmlAttribute=new XMLAttribute(attributeNamespaceURI, attributeQualifiedName, attributeValue);
			//add attribute object to the attribute set using the appropriate URI+localName Swing key
		attributeSet.addAttribute(getSwingXMLAttributeName(attributeNamespaceURI, XMLUtilities.getLocalName(attributeQualifiedName)), xmlAttribute);	
	}

	/**Gets the namespace of an attribute (encoded in another attribute)
		without resolving up the attribute set hierarchy.
	@param attributeSet The attribute set.
	@param attributeLocalName
	@return The target URI of the element, or <code>null</code> if the element
		has no target URI defined.
	*/
/*G***fix
	public static String getAttributeNamespace(final AttributeSet attributeSet)
	{
		return (URI)getDefinedAttribute(attributeSet, TARGET_URI_ATTRIBUTE_NAME);	//get the target URI if one is defined
	}
*/

	/**Sets the target URI of the element.
	@param attributeSet The attribute set.
	@param uri The element target URI, such as would be the target of a hypertext
		reference.
	*/
/*G***fix
	public static void setTargetURI(final MutableAttributeSet attributeSet, final URI uri)
	{
		attributeSet.addAttribute(TARGET_URI_ATTRIBUTE_NAME, uri);	//add the attribute to the attribute set
	}
*/

	/**Checks to see if the given attribute set represents an anonymous element.
	@param attributeSet The attribute set.
	@return <code>true</code> if the attribute set represents an anonymous element.
	*/
	public static boolean isAnonymous(final AttributeSet attributeSet)
	{
		return ANONYMOUS_XML_ELEMENT_NAME.equals(getXMLElementName(attributeSet)); //see if the attribute set has the anonymous name 
	}
	
	/**Gives the attribute set the anonymous name.
	@param attributeSet The attribute set.
	*/
	public static void setAnonymous(final MutableAttributeSet attributeSet)
	{
		setXMLElementName(attributeSet, ANONYMOUS_XML_ELEMENT_NAME); //show by its name that this is an anonymous element
	}
	/**Checks to see if the page break view attribute is set. The attribute is not
		resolved through the parent attribute set hierarchy.
	@param a The attribute set.
	@return <code>true</code> if the page break view attribute is set, else <code>false</code>.
	*/
	public static boolean isPageBreakView(AttributeSet a)
	{
		final Boolean pageBreakView=(Boolean)getDefinedAttribute(a, PAGE_BREAK_VIEW_ATTRIBUTE_NAME);	//get the page break view attribute
		return pageBreakView!=null ? pageBreakView.booleanValue() : false;	//if there is a page break view attribute, return its value, else return false
	}

	/**Sets the page break view attribute.
	@param a The attribute set
	@param b Whether or not the associated element should have a page break view.
	*/
	public static void setPageBreakView(final MutableAttributeSet a, final boolean b)
	{
		a.addAttribute(PAGE_BREAK_VIEW_ATTRIBUTE_NAME, new Boolean(b));	//add the attribute to the attribute set
	}

	/**Checks to see if the paragraphView attribute is set. The attribute is not
		resolved through the parent attribute set hierarchy.
	@param a The attribute set.
	@return <code>true</code> if the paragraphView is set, else <code>false</code>.
	*/
/*G***del
	public static boolean isParagraphView(AttributeSet a)
	{
		final Boolean paragraphView=(Boolean)getDefinedAttribute(a, PARAGRAPH_VIEW_ATTRIBUTE_NAME);	//get the paragraph view attribute
		return paragraphView!=null ? paragraphView.booleanValue() : false;	//if there is a paragraph view attribute, return its value, else return false
	}
*/

	/**Sets the paragraph view attribute.
	@param a The attribute set.
	@param b Whether or not the associated element should have a paragraph view.
	*/
/*G***del
	public static void setParagraphView(final MutableAttributeSet a, final boolean b)
	{
		a.addAttribute(PARAGRAPH_VIEW_ATTRIBUTE_NAME, new Boolean(b));	//add the attribute to the attribute set
	}
*/

	/**Checks to see if the view or any above it wants antialiased text.
	@param a The attribute set.
	@return <code>true</code> if the resolved antialiased attribute is set, else
		<code>false</code>.
	*/
	public static boolean isAntialias(AttributeSet a)
	{
		final Object antialias=a.getAttribute(ANTIALIAS_ATTRIBUTE_NAME);	//get the antialias attribute, resolving up the hierarchy if needed
		return antialias instanceof Boolean ? ((Boolean)antialias).booleanValue() : false;	//if there is a boolean antialias attribute, return its value, else return false
	}

	/**Sets the antialias attribute for this view.
	@param a The attribute set.
	@param b Whether or not the view should show antialiased text.
	*/
	public static void setAntialias(final MutableAttributeSet a, final boolean b)
	{
		a.addAttribute(ANTIALIAS_ATTRIBUTE_NAME, new Boolean(b));	//add the attribute to the attribute set
	}

	/**Gets the base URI of the document in which the element resides, resolving
		up the attribute set hierarchy.
	@param attributeSet The attribute set.
	@return The base URI, or <code>null</code> if there is no base URI defined
		anywhere up the hierarchy.
	*/
	public static URI getBaseURI(AttributeSet attributeSet)
	{
		return (URI)attributeSet.getAttribute(BASE_URI_ATTRIBUTE_NAME);  //get the name of the base URI, anywhere within the hierarchy
	}

	/**Sets the base URI of the document in which the element resides.
	@param attributeSet The attribute set.
	@param uri The base URI of the document.
	*/
	public static void setBaseURI(final MutableAttributeSet attributeSet, final URI uri)
	{
		attributeSet.addAttribute(BASE_URI_ATTRIBUTE_NAME, uri);	//add the attribute to the attribute set
	}

	/**Calculates the href from the given href relative to any base URI defined in
	  the attribute set.
	@param attributeSet The attribute set.
	@param href The relative or absolute location; if relative, it is interpreted
		in relation to the base URI, if present in the attribute set.
	@return An href that represents the href relative to any base URI defined in the
		attribute set.
	@exception URISyntaxException Thrown if one of the URIs used to determine
		the relative href is invalid.
	@see #getBaseURI
	*/
	public static String getBaseRelativeHRef(final AttributeSet attributeSet, final String href) throws URISyntaxException
	{
		final URI baseURI=getBaseURI(attributeSet);  //get the base URI of the file
		if(baseURI!=null) //if we find a base URI
		{
			return baseURI.relativize(new URI(href)).toString();	//create a relative URI from the base URI and return its string value
//G***this doesn't work; the base URL is not relative to where we're really starting from		  return URLUtilities.getRelativePath(baseURL, URLUtilities.createURL(baseURL, href)); //create an href relative to the document's base URL
//G***del when works		  return URLUtilities.createURL(baseURL, href).toString(); //create an href relative to the document's base URL
		}
		else  //if we couldn't find a base URI
			return href;  //just return the href as it is
	}

	/**Gets the media type of the document in which the element resides, resolving
		up the attribute set hierarchy.
	@param attributeSet The attribute set.
	@return The media type, or <code>null</code> if there is no media type defined
		anywhere up the hierarchy.
	*/
	public static ContentType getMediaType(AttributeSet attributeSet)
	{
		return (ContentType)attributeSet.getAttribute(MEDIA_TYPE_ATTRIBUTE_NAME);  //get the name of the media type, anywhere within the hierarchy
	}

	/**Sets the media type of the document in which the element resides.
	@param attributeSet The attribute set.
	@param mediaType The media type of the document.
	*/
	public static void setMediaType(final MutableAttributeSet attributeSet, final ContentType mediaType)
	{
		attributeSet.addAttribute(MEDIA_TYPE_ATTRIBUTE_NAME, mediaType);	//add the attribute to the attribute set
	}

	/**Gets the target URI of the element without resolving up the attribute set
		hierarchy. This URI is such as might be used for the target of a hypertext
		reference.
	@param attributeSet The attribute set.
	@return The target URI of the element, or <code>null</code> if the element
		has no target URI defined.
	*/
	public static URI getTargetURI(final AttributeSet attributeSet)
	{
		return (URI)getDefinedAttribute(attributeSet, TARGET_URI_ATTRIBUTE_NAME);	//get the target URI if one is defined
	}

	/**Sets the target URI of the element.
	@param attributeSet The attribute set.
	@param uri The element target URI, such as would be the target of a hypertext
		reference.
	*/
	public static void setTargetURI(final MutableAttributeSet attributeSet, final URI uri)
	{
		attributeSet.addAttribute(TARGET_URI_ATTRIBUTE_NAME, uri);	//add the attribute to the attribute set
	}

	/**Gets the public ID of the document type of the document, an element of
		which this set of attributes represents, resolving up the attribute set
		hierarchy.
	@param attributeSet The attribute set.
	@return The document type public ID, or <code>null</code> if the attribute set
		has no document type public ID.
	*/
	public static String getXMLDocTypePublicID(final AttributeSet attributeSet)
	{
		return (String)attributeSet.getAttribute(XML_DOCTYPE_PUBLIC_ID);	//get the XML document type public ID
	}

	/**Sets the XML document type public ID.
	@param attributeSet The attribute set
	@param xmlDocTypePublicID The XML document type public ID.
	*/
	public static void setXMLDocTypePublicID(final MutableAttributeSet attributeSet, final String xmlDocTypePublicID)
	{
		attributeSet.addAttribute(XML_DOCTYPE_PUBLIC_ID, xmlDocTypePublicID);	//add the attribute to the attribute set
	}

	/**Gets the system ID of the document type of the document, an element of
		which this set of attributes represents, resolving up the attribute set
		hierarchy.
	@param attributeSet The attribute set.
	@return The document type system ID, or <code>null</code> if the attribute set
		has no document type system ID.
	*/
	public static String getXMLDocTypeSystemID(final AttributeSet attributeSet)
	{
		return (String)attributeSet.getAttribute(XML_DOCTYPE_SYSTEM_ID);	//get the XML document type system ID
	}

	/**Sets the XML document type system ID.
	@param attributeSet The attribute set
	@param xmlDocTypeSystemID The XML document type system ID.
	*/
	public static void setXMLDocTypeSystemID(final MutableAttributeSet attributeSet, final String xmlDocTypeSystemID)
	{
		attributeSet.addAttribute(XML_DOCTYPE_SYSTEM_ID, xmlDocTypeSystemID);	//add the attribute to the attribute set
	}

	/**Gets the local name of the XML element the Swing attributes represent. The
		attribute is not resolved through the parent attribute set hierarchy.
	@param attributeSet The attribute set.
	@return The local name of the XML element, or <code>null</code> if the element
		has no local name.
	*/
	public static String getXMLElementLocalName(AttributeSet attributeSet)
	{
		return (String)getDefinedAttribute(attributeSet, XML_ELEMENT_LOCAL_NAME_ATTRIBUTE_NAME);	//get the local name of the XML element
	}

	/**Sets the local name of the XML element.
	@param attributeSet The attribute set
	@param xmlElementName The local name of the XML element.
	*/
	public static void setXMLElementLocalName(final MutableAttributeSet attributeSet, final String xmlElementLocalName)
	{
		attributeSet.addAttribute(XML_ELEMENT_LOCAL_NAME_ATTRIBUTE_NAME, xmlElementLocalName);	//add the attribute to the attribute set
	}

	/**Gets the name of the XML element the Swing attributes represent. The
		attribute is not resolved through the parent attribute set hierarchy.
	@param attributeSet The attribute set.
	@return The name of the XML element.
	*/
	public static String getXMLElementName(AttributeSet attributeSet)
	{
		return (String)getDefinedAttribute(attributeSet, XML_ELEMENT_NAME_ATTRIBUTE_NAME);	//get the name of the XML element
	}

	/**Sets the name of the XML element.
	@param attributeSet The attribute set
	@param xmlElementName The name of the XML element.
	*/
	public static void setXMLElementName(final MutableAttributeSet attributeSet, final String xmlElementName)
	{
		attributeSet.addAttribute(XML_ELEMENT_NAME_ATTRIBUTE_NAME, xmlElementName);	//add the attribute to the attribute set
	}

	/**Gets the namespace URI of the XML element the Swing attributes represent.
		The attribute is not resolved through the parent attribute set hierarchy.
	@param attributeSet The attribute set.
	@return The name of the XML element namespace URI, or <code>null</code> if
		this element was not defined in a namespace.
	*/
	public static String getXMLElementNamespaceURI(AttributeSet attributeSet)
	{
		return (String)getDefinedAttribute(attributeSet, XML_ELEMENT_NAMESPACE_URI_ATTRIBUTE_NAME);	//get the namespace URI of the XML element
	}

	/**Sets the namespace URI of the XML element.
	@param attributeSet The attribute set
	@param xmlElementNamespaceURI The namespaceURI of the XML element.
	*/
	public static void setXMLElementNamespaceURI(final MutableAttributeSet attributeSet, final String xmlElementNamespaceURI)
	{
		attributeSet.addAttribute(XML_ELEMENT_NAMESPACE_URI_ATTRIBUTE_NAME, xmlElementNamespaceURI);	//add the attribute to the attribute set
	}

	/**Checks to see whether the XML element represented is an empty element,
		without resolving up the hierarchy.
	@param attributeSet The attribute set.
	@return <code>true</code> if the the represented XML element is an empty
		element, else <code>false</code>.
	*/
	public static boolean isXMLEmptyElement(final AttributeSet attributeSet)
	{
		final Boolean emptyElement=(Boolean)getDefinedAttribute(attributeSet, XML_EMPTY_ELEMENT);	//get the empty element attribute
		return emptyElement!=null ? emptyElement.booleanValue() : false;	//if there is an empty element attribute, return its value, else return false
	}

	/**Sets whether the represented XML element is an empty element.
	@param attributeSet The attribute set
	@param isEmptyElement Whether or not the associated element is an empty element.
	*/
	public static void setXMLEmptyElement(final MutableAttributeSet attributeSet, final boolean isEmptyElement)
	{
		attributeSet.addAttribute(XML_EMPTY_ELEMENT, new Boolean(isEmptyElement));	//add the attribute to the attribute set
	}

	/**Gets the array of XML processing instructions, each represented by a
		name/value pair. The attribute is not resolved up the parent attribute set
		hierarchy.
	@param attributeSet The attribute set.
	@return The non-<code>null</code> array of processing instruction name/value pairs.
	*/
	public static NameValuePair[] getXMLProcessingInstructions(final AttributeSet attributeSet)
	{
		final NameValuePair[] processingInstructions=(NameValuePair[])getDefinedAttribute(attributeSet, XML_PROCESSING_INSTRUCTIONS_ATTRIBUTE_NAME);	//get the XML processing instruction array, without checking up the hierarchy
		  //return the processing instructions or, if there are none, an empty array
		return processingInstructions!=null ? processingInstructions : EMPTY_NAME_VALUE_PAIR_ARRAY;
	}

	/**Sets the array of XML processing instructions.
	@param attributeSet The attribute set.
	@param processingInstructions The array of processing instruction names and
		values.
	*/
	public static void setXMLProcessingInstructions(final MutableAttributeSet attributeSet, final NameValuePair[] processingInstructions)
	{
		attributeSet.addAttribute(XML_PROCESSING_INSTRUCTIONS_ATTRIBUTE_NAME, processingInstructions);	//add the attribute to the attribute set
	}

	/**The array of processing instructions this document contains.*/
//G***del if not needed	public final static String XML_PROCESSING_INSTRUCTIONS_ATTRIBUTE_NAME=SWING_ATTRIBUTE_START+"xmlProcessingInstructions";


	/**Gets the XML element attributes the Swing attributes represent. The
		attribute is not resolved through the parent attribute set hierarchy.
	@param a The attribute set.
	@return The map of XML attributes.
	*/
/*G***fix
	public static NamedNodeMap getXMLAttributes(AttributeSet a)
	{
		return (NamedNodeMap)getDefinedAttribute(a, XMLAttributesAttributeName);	//get the attribute
	}
*/

	/**Sets the map of XML attributes.
	@param a The attribute set
	@param xmlAttributes The map of XML attributes.
	*/
/*G***fix
	public static void setXMLAttributes(final MutableAttributeSet a, final NamedNodeMap xmlAttributes)
	{
		a.addAttribute(XMLAttributesAttributeName, xmlAttributes);	//add the attribute to the attribute set
	}
*/

}

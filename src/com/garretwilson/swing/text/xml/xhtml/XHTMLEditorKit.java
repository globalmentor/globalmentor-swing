package com.garretwilson.swing.text.xml.xhtml;

import java.net.URI;
import java.util.List;

import javax.mail.internet.ContentType;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import com.garretwilson.io.*;
import com.garretwilson.swing.text.StyleUtilities;
import com.garretwilson.swing.text.xml.*;
import com.garretwilson.text.xml.XMLDOMImplementation;
import com.garretwilson.text.xml.xhtml.*;
import static com.garretwilson.text.xml.xhtml.XHTMLConstants.*;
import org.w3c.dom.*;

/**An editor kit for XHTML.
@author Garret Wilson
*/
public class XHTMLEditorKit extends XMLEditorKit 
{

	/**The XHTML media type this editor kit supports, defaulting to <code>application/xhtml+xml</code>.*/
	private ContentType mediaType=new ContentType(ContentTypeConstants.APPLICATION, ContentTypeConstants.XHTML_XML_SUBTYPE, null);

		/**@return The XHTML media type this editor kit supports.*/
		public ContentType getMediaType() {return mediaType;}

		/**Sets the media type this editor kit supports.
		@param newMediaType The new XHTML media type.
		*/
		protected void setMediaType(final ContentType newMediaType) {mediaType=newMediaType;}

	/**Constructor.
	@param uriInputStreamable The source of input streams for resources.
	@exception NullPointerException if the new source of input streams is <code>null</code>.
	*/
	public XHTMLEditorKit(final URIInputStreamable uriInputStreamable)
	{
		super(uriInputStreamable);	//construct the parent class
	}

	/**Constructor that specifies the specific XHTML media type supported.
	@param mediaType The XHTML media type supported. In some instances, such as
		<code>application/xhtml+xml</code>, this indicates a default namespace even
		in the absence of a document namespace identfication.
	@param uriInputStreamable The source of input streams for resources.
	*/
	public XHTMLEditorKit(final ContentType mediaType, final URIInputStreamable uriInputStreamable)
	{
		super(mediaType, uriInputStreamable);	//construct the parent class
	}

	/**Creates a default Swing document containing the minimum structure of an
		XHTML document tree.
	@return The new Swing document containing XHTML.
	 */
	public XMLDocument createDefaultDocument()
	{
		final XMLDocument swingDocument=super.createDefaultDocument();	//create a default document
		final XMLDOMImplementation domImplementation=new XMLDOMImplementation();	//create a new DOM implementation G***later use some Java-specific stuff
			//create an XML document for the XHTML <div> element
		final org.w3c.dom.Document xhtml=domImplementation.createDocument(XHTML_NAMESPACE_URI.toString(), ELEMENT_DIV, null);	//create an XML document for XHTML <div>
//G***del if not needed		XMLUtilities.appendText(xhtml.getDocumentElement(), "\n");	//append a newline to the div
//TODO probably bring this back when the XMLEditorPane can more intelligently handle editing of complex documents final org.w3c.dom.Document xhtml=XHTMLUtilities.createXHTMLDocument();	//create a default XHTML DOM document
		setXML(xhtml, null, getMediaType(), swingDocument);	//set the XHTML into the new document, using our media type TODO make sure the base URI gets updated---somehow (maybe the calling code should do this afterwards)
		return swingDocument;	//return the default document we created
	}

	/**Creates a copy of the editor kit.
	@return A copy of the XML editor kit.
	*/
	public Object clone() {return new XHTMLEditorKit(getMediaType(), getURIInputStreamable());}  //G***why do we need this?

	/**Determines if the specified element represents an empty element&mdash;an
		element that might be declared as <code>EMPTY</code> in a DTD.
	@param attributeSet The attribute set of the element in question.
	@return <code>true</code> if the specified element should be empty.
	*/
	protected boolean isEmptyElement(final AttributeSet attributeSet)
	{
		if(super.isEmptyElement(attributeSet))	//check the parent class; if it thinks this is an empty element
		{
			return true;	//this is an empty element
		}
		final String elementNamespaceURIString=XMLStyleUtilities.getXMLElementNamespaceURI(attributeSet);	//get the namespace URI string G***eventually just store and retrieve URIs if possible
		final URI elementNamespaceURI=elementNamespaceURIString!=null ? URI.create(elementNamespaceURIString) : null;	//create a URI from the string
		final String elementLocalName=XMLStyleUtilities.getXMLElementLocalName(attributeSet);	//get the local name of the element
		return XHTMLUtilities.isEmptyElement(elementNamespaceURI, elementLocalName);	//see if this is an XHTML empty element
	}

	/**Appends information from an XML child node into a list of element specs.
	<p>This version adds an attribute to hide child nodes that are siblings of
		the XHTML body element.</p>
	@param elementSpecList The list of element specs to be inserted into the document.
	@param node The XML element's child node tree.
	@param baseURI The base URI of the document, used for generating full target
		URIs for quick searching, or <code>null</code> if there is no base URI or
		if the base URI is not applicable.
	@return The attribute set used to represent the node; this attribute set
		can be manipulated after the method returns.
	@exception BadLocationException for an invalid starting offset
	@see XMLDocument#insert
	@see XMLDocument#appendElementSpecListContent
	*/
/*TODO del when not needed; make sure default stylesheet hides the <head> element
	protected MutableAttributeSet appendElementSpecListNode(final List elementSpecList, final Node node, final URI baseURI)
	{
		final MutableAttributeSet attributeSet=super.appendElementSpecListNode(elementSpecList, node, baseURI);	//create the default spec list for the node
		final Node parentNode=node.getParentNode();	//get the parent of this node
		if(parentNode!=null && node.getOwnerDocument().getDocumentElement()==node.getParentNode())	//if the parent of this node is the document element
		{
				//if the parent is the <html> element
			if(ELEMENT_HTML.equals(parentNode.getLocalName()) || ELEMENT_HTML.equals(parentNode.getLocalName()))
			{
					//if this is not the <body> element
				if(!ELEMENT_BODY.equals(node.getLocalName()) && !ELEMENT_BODY.equals(node.getLocalName()))
				{
					StyleUtilities.setVisible(attributeSet, false);	//turn off visibility for this node
				}
			}
		}
		return attributeSet;	//return the attribute set
	}
*/

	/**Gets the target ID of of the specified element. This ID represents the
		target of a link. The default target ID (the value of the "id" attribute)
		is located, and if it does not exist the value of the "name" attribute is
		used. G***what about checking the DTD for an element of type ID?
	@param attributeSet The attribute set of the element which may contain a
		target ID.
	//G***del when works	@param element The element which may contain a target ID.
	@return The target ID value of the element, or <code>null</code> if the
		element does not define a target ID.
	*/
	protected String getTargetID(final AttributeSet attributeSet)	//TODO probably put this in XHTMLEditorKit
	{
		String targetID=super.getTargetID(attributeSet); //get the standard "id" attribute value
		if(targetID==null)  //if the "id" attribute didn't have a value
		{
	//G***del when works			final AttributeSet attributeSet=element.getAttributes();	//get the attributes of this element
			targetID=XMLStyleUtilities.getXMLAttributeValue(attributeSet, null, "name");  //return the value of the "name" attribute, if it exists G***use a constant here
		}
		return targetID;  //return whatever target ID we found, if any
	}

}

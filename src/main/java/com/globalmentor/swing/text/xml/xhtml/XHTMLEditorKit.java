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

package com.globalmentor.swing.text.xml.xhtml;

import java.net.URI;

import javax.swing.text.AttributeSet;
import com.globalmentor.io.*;
import com.globalmentor.net.ContentType;
import com.globalmentor.swing.text.xml.*;
import com.globalmentor.xml.XML;
import com.globalmentor.xml.xhtml.*;

import static com.globalmentor.w3c.spec.HTML.*;

import org.w3c.dom.*;

/**
 * An editor kit for XHTML.
 * @author Garret Wilson
 */
public class XHTMLEditorKit extends XMLEditorKit {

	/** The XHTML media type this editor kit supports, defaulting to <code>application/xhtml+xml</code>. */
	private ContentType mediaType = XHTML_CONTENT_TYPE;

	/** @return The XHTML media type this editor kit supports. */
	public ContentType getMediaType() {
		return mediaType;
	}

	/**
	 * Sets the media type this editor kit supports.
	 * @param newMediaType The new XHTML media type.
	 */
	protected void setMediaType(final ContentType newMediaType) {
		mediaType = newMediaType;
	}

	/**
	 * Constructor.
	 * @param uriInputStreamable The source of input streams for resources.
	 * @throws NullPointerException if the new source of input streams is <code>null</code>.
	 */
	public XHTMLEditorKit(final URIInputStreamable uriInputStreamable) {
		super(uriInputStreamable); //construct the parent class
	}

	/**
	 * Constructor that specifies the specific XHTML media type supported.
	 * @param mediaType The XHTML media type supported. In some instances, such as <code>application/xhtml+xml</code>, this indicates a default namespace even in
	 *          the absence of a document namespace identfication.
	 * @param uriInputStreamable The source of input streams for resources.
	 */
	public XHTMLEditorKit(final ContentType mediaType, final URIInputStreamable uriInputStreamable) {
		super(mediaType, uriInputStreamable); //construct the parent class
	}

	/**
	 * Creates a default Swing document containing the minimum structure of an XHTML document tree.
	 * @return The new Swing document containing XHTML.
	 */
	public XMLDocument createDefaultDocument() {
		final XMLDocument swingDocument = super.createDefaultDocument(); //create a default document
		final DOMImplementation domImplementation = XML.createDocumentBuilder(true).getDOMImplementation(); //create a new DOM implementation
		//create an XML document for the XHTML <div> element
		final org.w3c.dom.Document xhtml = domImplementation.createDocument(XHTML_NAMESPACE_URI.toString(), ELEMENT_DIV, null); //create an XML document for XHTML <div>
		//TODO del if not needed		XMLUtilities.appendText(xhtml.getDocumentElement(), "\n");	//append a newline to the div
		//TODO probably bring this back when the XMLEditorPane can more intelligently handle editing of complex documents final org.w3c.dom.Document xhtml=XHTMLUtilities.createXHTMLDocument();	//create a default XHTML DOM document
		setXML(xhtml, null, getMediaType(), swingDocument); //set the XHTML into the new document, using our media type TODO make sure the base URI gets updated---somehow (maybe the calling code should do this afterwards)
		return swingDocument; //return the default document we created
	}

	/**
	 * Creates a copy of the editor kit.
	 * @return A copy of the XML editor kit.
	 */
	public Object clone() {
		return new XHTMLEditorKit(getMediaType(), getURIInputStreamable());
	} //TODO why do we need this?

	/**
	 * Determines if the specified element represents an empty element&mdash;an element that might be declared as <code>EMPTY</code> in a DTD.
	 * @param attributeSet The attribute set of the element in question.
	 * @return <code>true</code> if the specified element should be empty.
	 */
	protected boolean isEmptyElement(final AttributeSet attributeSet) {
		if(super.isEmptyElement(attributeSet)) { //check the parent class; if it thinks this is an empty element
			return true; //this is an empty element
		}
		final String elementNamespaceURIString = XMLStyles.getXMLElementNamespaceURI(attributeSet); //get the namespace URI string TODO eventually just store and retrieve URIs if possible
		final URI elementNamespaceURI = elementNamespaceURIString != null ? URI.create(elementNamespaceURIString) : null; //create a URI from the string
		final String elementLocalName = XMLStyles.getXMLElementLocalName(attributeSet); //get the local name of the element
		return XHTML.isEmptyElement(elementNamespaceURI, elementLocalName); //see if this is an XHTML empty element
	}

	/**
	 * Appends information from an XML child node into a list of element specs.
	 * <p>
	 * This version adds an attribute to hide child nodes that are siblings of the XHTML body element.
	 * </p>
	 * @param elementSpecList The list of element specs to be inserted into the document.
	 * @param node The XML element's child node tree.
	 * @param baseURI The base URI of the document, used for generating full target URIs for quick searching, or <code>null</code> if there is no base URI or if
	 *          the base URI is not applicable.
	 * @return The attribute set used to represent the node; this attribute set can be manipulated after the method returns.
	 * @throws BadLocationException for an invalid starting offset
	 * @see XMLDocument#insert
	 * @see XMLDocument#appendElementSpecListContent
	 */
	/*TODO del when not needed; make sure default stylesheet hides the <head> element
		protected MutableAttributeSet appendElementSpecListNode(final List elementSpecList, final Node node, final URI baseURI)
		{
			final MutableAttributeSet attributeSet=super.appendElementSpecListNode(elementSpecList, node, baseURI);	//create the default spec list for the node
			final Node parentNode=node.getParentNode();	//get the parent of this node
			if(parentNode!=null && node.getOwnerDocument().getDocumentElement()==node.getParentNode()) {	//if the parent of this node is the document element
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

	/**
	 * Gets the target ID of of the specified element. This ID represents the target of a link. The default target ID (the value of the "id" attribute) is
	 * located, and if it does not exist the value of the "name" attribute is used. TODO what about checking the DTD for an element of type ID?
	 * @param attributeSet The attribute set of the element which may contain a target ID. //TODO del when works @param element The element which may contain a
	 *          target ID.
	 * @return The target ID value of the element, or <code>null</code> if the element does not define a target ID.
	 */
	protected String getTargetID(final AttributeSet attributeSet) { //TODO probably put this in XHTMLEditorKit
		String targetID = super.getTargetID(attributeSet); //get the standard "id" attribute value
		if(targetID == null) { //if the "id" attribute didn't have a value
			//TODO del when works			final AttributeSet attributeSet=element.getAttributes();	//get the attributes of this element
			targetID = XMLStyles.getXMLAttributeValue(attributeSet, null, "name"); //return the value of the "name" attribute, if it exists TODO use a constant here
		}
		return targetID; //return whatever target ID we found, if any
	}

}

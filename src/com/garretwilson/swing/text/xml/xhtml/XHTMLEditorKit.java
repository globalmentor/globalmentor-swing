package com.garretwilson.swing.text.xml.xhtml;

import java.net.URI;
import java.util.List;
import javax.swing.text.MutableAttributeSet;
import com.garretwilson.io.*;
import com.garretwilson.swing.text.StyleUtilities;
import com.garretwilson.swing.text.xml.*;
import com.garretwilson.text.xml.xhtml.XHTMLConstants;
import org.w3c.dom.Node;

/**An editor kit for XHTML.
@author Garret Wilson
@see com.garretwilson.text.xml.XMLProcessor
*/
public class XHTMLEditorKit extends XMLEditorKit
{

	/**The XHTML media type this editor kit supports, defaulting to <code>application/xhtml+xml</code>.*/
	private MediaType mediaType=new MediaType(MediaType.APPLICATION, MediaType.XHTML_XML);

		/**@return The XHTML media type this editor kit supports.*/
		public MediaType getMediaType() {return mediaType;}

		/**Sets the media type this editor kit supports.
		@param newMediaType The new XHTML media type.
		*/
		protected void setMediaType(final MediaType newMediaType) {mediaType=newMediaType;}

	/**Default constructor.*/
	public XHTMLEditorKit()
	{
	}

	/**Constructor that specifies the specific XHTML media type supported.
	@param mediaType The XHTML media type supported. In some instances, such as
		<code>application/xhtml+xml</code>, this indicates a default namespace even
		in the absence of a document namespace identfication.
	*/
	public XHTMLEditorKit(final MediaType mediaType)
	{
		setMediaType(mediaType);  //set the requested media type
	}

	/**Creates a copy of the editor kit.
	@return A copy of the XML editor kit.
	*/
	public Object clone() {return new XHTMLEditorKit(getMediaType());}  //G***why do we need this?

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
	protected MutableAttributeSet appendElementSpecListNode(final List elementSpecList, final Node node, final URI baseURI)
	{
		final MutableAttributeSet attributeSet=super.appendElementSpecListNode(elementSpecList, node, baseURI);	//create the default spec list for the node
		final Node parentNode=node.getParentNode();	//get the parent of this node
		if(parentNode!=null && node.getOwnerDocument().getDocumentElement()==node.getParentNode())	//if the parent of this node is the document element
		{
				//if the parent is the <html> element
			if(XHTMLConstants.ELEMENT_HTML.equals(parentNode.getLocalName()) || XHTMLConstants.ELEMENT_HTML.equals(parentNode.getLocalName()))
			{
					//if this is not the <body> element
				if(!XHTMLConstants.ELEMENT_BODY.equals(node.getLocalName()) && !XHTMLConstants.ELEMENT_BODY.equals(node.getLocalName()))
				{
					StyleUtilities.setVisible(attributeSet, false);	//turn off visibility for this node
				}
			}
		}
		return attributeSet;	//return the attribute set
	}
}

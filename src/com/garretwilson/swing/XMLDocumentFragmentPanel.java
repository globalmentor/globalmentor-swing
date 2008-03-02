package com.garretwilson.swing;

import java.net.URI;
import javax.mail.internet.ContentType;
import com.globalmentor.io.*;
import com.globalmentor.text.xml.XMLNodeModel;
import com.globalmentor.text.xml.XMLUtilities;

import org.w3c.dom.*;

/**Panel that displays an XML document fragment and source code.
@author Garret Wilson
*/
public class XMLDocumentFragmentPanel extends XMLPanel<DocumentFragment>
{

	/**Initialization constructor.
	@param model The data model for which this component provides a view.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public XMLDocumentFragmentPanel(final XMLNodeModel<DocumentFragment> model, final boolean initialize)
	{
		this(model, new ContentType(ContentTypes.TEXT_PRIMARY_TYPE, ContentTypeConstants.XML_SUBTYPE, null), initialize);	//construct the panel with a default text/xml media type
	}

	/**Content type constructor.
	@param model The data model for which this component provides a view.
	@param mediaType The content type of the XML.
	*/
	public XMLDocumentFragmentPanel(final XMLNodeModel<DocumentFragment> model, final ContentType mediaType)
	{
		this(model, mediaType, true);	//construct and initialize the panel
	}

	/**Content type and initialization constructor.
	@param model The data model for which this component provides a view.
	@param mediaType The content type of the XML.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public XMLDocumentFragmentPanel(final XMLNodeModel<DocumentFragment> model, final ContentType mediaType, final boolean initialize)
	{
		super(model, mediaType, false);	//construct the parent class without initializing the panel
		if(initialize)  //if we should initialize
			initialize();   //initialize the panel
	}

	/**@return The edited source text, modified as appropriate for the type of model.
	This version wraps the edited text with a dummy element for creating a document fragment.
	*/
	protected String getSourceText()
	{
		final String sourceText=super.getSourceText();	//get the current source text
		if(sourceText.length()>0)	//if there is source text
		{
			final StringBuilder sourceStringBuilder=new StringBuilder();	//create a string builder for building the fragment
			sourceStringBuilder.append("<?xml version=\"1.0\"?>\n");	//append the XML prolog TODO use a constant
			sourceStringBuilder.append("<div");	//append a root element start tag TODO use something HTML-agnostic
			final URI defaultNamespaceURI=XMLUtilities.getDefaultNamespaceURI(getContentType());	//see if we have a default namespace for the media type we're using
			if(defaultNamespaceURI!=null)	//if we know the default namespace
			{
				sourceStringBuilder.append(" xmlns=\"").append(defaultNamespaceURI).append("\"");	//add a default XML namespace declaration attribute
			}
			sourceStringBuilder.append('>');	//finish the root element start tag
			sourceStringBuilder.append(sourceText);	//add the source text
			sourceStringBuilder.append("</div>");	//append the ending tag for the root element to the source code
			return sourceStringBuilder.toString();	//return the source we constructed
		}
		else	//if there is no source text
		{
			return sourceText;	//return the source text (here the empty string) unmodified
		}
	}

	/**Saves the given document contents to the given model.
	This version stores a document fragment in the model representing the children of the given document's document element.
	@param model The model to update.
	@param document The XML document the contents of which to store in the model.
	*/
	protected void saveModel(final XMLNodeModel<DocumentFragment> model, final Document document)
	{
			//extract the child elements into a document fragment and set that in the model
		model.setXML(XMLUtilities.extractChildren(document.getDocumentElement()));
	}

}
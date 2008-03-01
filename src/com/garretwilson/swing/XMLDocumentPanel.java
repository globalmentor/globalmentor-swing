package com.garretwilson.swing;

import javax.mail.internet.ContentType;
import com.garretwilson.text.xml.XMLNodeModel;
import com.globalmentor.io.*;

import org.w3c.dom.*;

/**Panel that displays an XML document.
@author Garret Wilson
*/
public class XMLDocumentPanel extends XMLPanel<Document>	//TODO make the toolbar and status bar an option
{

	/**Model constructor.
	@param model The data model for which this component provides a view.
	*/
	public XMLDocumentPanel(final XMLNodeModel<Document> model)
	{
		this(model, true);	//construct and initialize the panel
	}

	/**Initialization constructor.
	@param model The data model for which this component provides a view.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public XMLDocumentPanel(final XMLNodeModel<Document> model, final boolean initialize)
	{
		this(model, new ContentType(ContentTypes.TEXT_PRIMARY_TYPE, ContentTypeConstants.XML_SUBTYPE, null), initialize);	//construct the panel with a default text/xml media type
	}

	/**Content type constructor.
	@param model The data model for which this component provides a view.
	@param mediaType The content type of the XML.
	*/
	public XMLDocumentPanel(final XMLNodeModel<Document> model, final ContentType mediaType)
	{
		this(model, mediaType, true);	//construct and initialize the panel
	}

	/**Content type and initialization constructor.
	@param model The data model for which this component provides a view.
	@param mediaType The content type of the XML.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public XMLDocumentPanel(final XMLNodeModel<Document> model, final ContentType mediaType, final boolean initialize)
	{
		super(model, mediaType, false);	//construct the parent class without initializing the panel
		if(initialize)  //if we should initialize
			initialize();   //initialize the panel
	}

	/**Saves the given document contents to the given model.
	This version stores the entire document in the model.
	@param model The model to update.
	@param document The XML document the contents of which to store in the model.
	*/
	protected void saveModel(final XMLNodeModel<Document> model, final Document document)
	{
		model.setXML(document);	//put the whole document in the model
	}

}
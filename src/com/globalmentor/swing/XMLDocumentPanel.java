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

package com.globalmentor.swing;

import com.globalmentor.net.ContentType;
import com.globalmentor.net.ContentTypeConstants;
import com.globalmentor.text.xml.XMLNodeModel;

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
		this(model, ContentType.getInstance(ContentType.TEXT_PRIMARY_TYPE, ContentTypeConstants.XML_SUBTYPE), initialize);	//construct the panel with a default text/xml media type
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
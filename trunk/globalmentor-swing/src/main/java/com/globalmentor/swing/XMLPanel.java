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

import java.awt.BorderLayout;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

import com.globalmentor.io.*;
import com.globalmentor.net.ContentType;
import com.globalmentor.net.ContentTypeConstants;

import static com.globalmentor.text.Text.*;

import com.globalmentor.swing.text.xml.*;
import com.globalmentor.swing.unicode.UnicodeStatusBar;
import com.globalmentor.text.xml.URIInputStreamableXMLEntityResolver;
import com.globalmentor.text.xml.XML;
import com.globalmentor.text.xml.XMLNodeModel;
import com.globalmentor.text.xml.XMLSerializer;
import com.globalmentor.text.xml.xhtml.XHTML;

import org.w3c.dom.*;

/**Panel that displays XML and source code.
@author Garret Wilson
*/
public abstract class XMLPanel<N extends Node> extends TabbedViewPanel<XMLNodeModel<N>>	//TODO make the toolbar and status bar an option
{

	/**The default model views supported by this panel.*/
	private final int[] DEFAULT_SUPPORTED_MODEL_VIEWS=new int[] {WYSIWYG_MODEL_VIEW, SOURCE_MODEL_VIEW};

	/**The default default model view of this panel.*/
	private final int DEFAULT_DEFAULT_MODEL_VIEW=WYSIWYG_MODEL_VIEW;

	/**The XML text pane.*/
	private final XMLTextPane xmlTextPane; 

		/**@return The XML text pane.*/
		public XMLTextPane getXMLTextPane() {return xmlTextPane;}

	/**The source text pane.*/
	private final JTextPane sourceTextPane; 

		/**@return The source text pane.*/
		public JTextPane getSourceTextPane() {return sourceTextPane;}

	/**@return <code>true</code> if the text can be edited.*/
/*TODO TODO fix editable
	public boolean isEditable() {return xmlTextPane.isEditable();}
*/	
	/**Sets whether or not the text can be edited.
	@param editable <code>true</code> if the text can be edited, or
		<code>false</code> if the information should be read-only.
	*/
/*TODO TODO fix editable
	public void setEditable(final boolean editable)
	{
		xmlTextPane.setEditable(editable);	//show whether or not the text pane is editable


//TODO fix		xmlTextPane.setBackground(editable ? Color.WHITE : Color.LIGHT_GRAY);	//set the color to match


//TODO del		textPane.setEnabled(editable);	//set the enabled status to match
	}
*/

	/**The XML scroll pane component.*/
	private final JScrollPane xmlScrollPane;

		/**@return The XML scroll pane component.*/
		protected JScrollPane getXMLScrollPane() {return xmlScrollPane;}

	/**The source scroll pane component.*/
	private final JScrollPane sourceScrollPane;

		/**@return The source scroll pane component.*/
		protected JScrollPane getSourceScrollPane() {return sourceScrollPane;}

	/**The status bar showing information about the current Unicode character.*/
	private final UnicodeStatusBar unicodeStatusBar;

		/**@return The status bar showing information about the current Unicode character.*/
		protected final UnicodeStatusBar getUnicodeStatusBar() {return unicodeStatusBar;}

	/**TODO testing.*/
	private final Action emphasisAction;

		/**@return TODO testing*/
		public Action getEmphasisAction() {return emphasisAction;}

	/**Sets the data model.
	@param newModel The data model for which this component provides a view.
	@exception ClassCastException Thrown if the model is not an <code>XMLNodeModel</code>.
	*/
	public void setModel(final XMLNodeModel<N> newModel)
	{
		xmlTextPane.setURIInputStreamable(newModel);	//make sure the text pane knows from where to get input streams
		super.setModel(newModel);	//set the model in the parent class
	}

	/**Model constructor.
	@param model The data model for which this component provides a view.
	*/
	public XMLPanel(final XMLNodeModel<N> model)
	{
		this(model, true);	//construct and initialize the panel
	}

	/**Initialization constructor.
	@param model The data model for which this component provides a view.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public XMLPanel(final XMLNodeModel<N> model, final boolean initialize)
	{
		this(model, ContentType.getInstance(ContentType.TEXT_PRIMARY_TYPE, ContentTypeConstants.XML_SUBTYPE), initialize);	//construct the panel with a default text/xml media type
	}

	/**Content type constructor.
	@param model The data model for which this component provides a view.
	@param mediaType The content type of the XML.
	*/
	public XMLPanel(final XMLNodeModel<N> model, final ContentType mediaType)
	{
		this(model, mediaType, true);	//construct and initialize the panel
	}

	/**Content type and initialization constructor.
	@param model The data model for which this component provides a view.
	@param mediaType The content type of the XML.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public XMLPanel(final XMLNodeModel<N> model, final ContentType mediaType, final boolean initialize)
	{
		super(model, false);	//construct the parent class without initializing the panel
		setSupportedModelViews(DEFAULT_SUPPORTED_MODEL_VIEWS);	//set the model views we support
		setDefaultDataView(DEFAULT_DEFAULT_MODEL_VIEW);	//set the default data view
		xmlTextPane=new XMLTextPane();	//create a new XML text pane
		setContentType(mediaType);	//set the content type 
		xmlScrollPane=new JScrollPane(xmlTextPane);	//create a new scroll pane with the XML text pane inside
//TODO del when works		sourceTextPane=new JTextPane();	//create a new text pane for editing the source
		sourceTextPane=new XMLTextPane();	//create a new text pane for editing the source
		sourceScrollPane=new JScrollPane(sourceTextPane);	//create a new scroll pane with the source text pane inside

		unicodeStatusBar=new UnicodeStatusBar();	//create a new Unicode status bar

//TODO fix		emphasisAction=new EmphasisAction();  //TODO testing
		emphasisAction=new InsertXMLElementAction("<em>emphasis</em>", XHTML.XHTML_NAMESPACE_URI, XHTML.ELEMENT_EM, getXMLTextPane());  //TODO testing


		if(initialize)  //if we should initialize
			initialize();   //initialize the panel
	}

	/**Initialize the user interface.*/
	protected void initializeUI()
	{
		getSourceTextPane().setContentType(ContentType.toString(ContentType.TEXT_PRIMARY_TYPE, PLAIN_SUBTYPE));	//set the content type of the source view to "text/plain"
		addView(WYSIWYG_MODEL_VIEW, getXMLTextPane().getContentType(), getXMLScrollPane());	//add the XML text pane as the WYSIWYG view TODO i18n
		addView(SOURCE_MODEL_VIEW, "XML", getSourceScrollPane());	//add the source XML text pane as the source view TODO i18n

		add(unicodeStatusBar, BorderLayout.SOUTH);	//put the Unicode status bar in the south TODO make this an option

		unicodeStatusBar.attach(xmlTextPane);	//initially set the Unicode status bar to track the XML text pane 
		xmlTextPane.addCaretListener(new CaretListener()
			{
				public void caretUpdate(CaretEvent e)
				{
updateStatus();	//testing; probably put a convenience method to create this listener in XMLTextPane
				}				
			});


		super.initializeUI(); //do the default UI initialization
//TODO check for the content type changing, and update the tab name in response

				//add ourselves to listen to the XML text pane, so that if the XML text pane
				//	changes documents we can add ourselves to the document to listen for modifications
/*TODO del if not needed
		xmlTextPane.addPropertyChangeListener(XMLTextPane.DOCUMENT_PROPERTY, new PropertyChangeListener()
			{
				public void propertyChange(final PropertyChangeEvent propertyChangeEvent)	//if the document property changes
				{
					if(propertyChangeEvent.getOldValue() instanceof javax.swing.text.Document)	//if we know the old document
						((javax.swing.text.Document)propertyChangeEvent.getOldValue()).removeDocumentListener(XMLPanel.this);	//remove ourselves as a document listener
					if(propertyChangeEvent.getNewValue() instanceof javax.swing.text.Document)	//if we know the new document
					((javax.swing.text.Document)propertyChangeEvent.getNewValue()).addDocumentListener(XMLPanel.this);	//add ourselves as a document listener
				}
			});	//TODO transfer to TextApplicationPanel
*/
//TODO del if not needed		setModified(false);	//show that the information has not been modified
	}

	/**@return An array of actions to use in a toolbar, with any
		<code>null</code> actions representing separators.
	*/
/*TODO fix
	public Action[] getToolBarActions()
	{
		return new Action[]{getSaveAction(), getEmphasisAction()};	//return the toolbar actions for this panel
			//TODO probably set many of these actions to be non-focusable; maybe do this in ToolStatusPanel
	}
*/

	/**@return The XML content type of the panel.*/
	public ContentType getContentType()
	{
		return ContentType.getInstance(getXMLTextPane().getContentType());	//return the XML text pane content type
	}	

	/**Sets the XML content type of the panel.
	@param mediaType The content type of the XML.
	*/
	public void setContentType(final ContentType mediaType)
	{
		getXMLTextPane().setContentType(mediaType.toString());	//set the content type of the text pane
	}

	/**Loads the data from the model to the specified view, if necessary.
	@param modelView The view of the data, such as <code>SUMMARY_MODEL_VIEW</code>.
	@exception IOException Thrown if there was an error loading the model.
	*/
	protected void loadModel(final int modelView) throws IOException	//TODO better refactor the Document and DocumentFragment-specific code out of this abstract class
	{
		super.loadModel(modelView);	//do the default loading
		final XMLNodeModel<N> model=getModel();	//get the data model
		final N xml=model.getXML();	//get the XML
		switch(modelView)	//see which view of data we should load
		{
			case WYSIWYG_MODEL_VIEW:	//if we're changing to the WYSIWYG view
				getXMLTextPane().getDocument().removeDocumentListener(getModifyDocumentListener());	//don't listen for changes to the XML text pane
				if(xml!=null)	//if we have XML
				{
					getXMLTextPane().setURIInputStreamable(model);	//make sure the text pane knows from where to get input streams
					if(xml instanceof Document)	//if the model models a document
					{
						getXMLTextPane().setXML((Document)xml, model.getBaseURI(), getContentType());	//put the XML document into the XML text pane
					}
					else if(xml instanceof DocumentFragment)	//if the model models a document fragment
					{
						getXMLTextPane().setXML((DocumentFragment)xml, model.getBaseURI(), getContentType());	//put the XML document into the XML text pane
					}
				}
				else	//if we don't have any XML
				{
					getXMLTextPane().setDocument(getXMLTextPane().getEditorKit().createDefaultDocument());	//create a default document
				}
				getXMLTextPane().getDocument().addDocumentListener(getModifyDocumentListener());	//add ourselves as a document listener to the XML text pane
				break;
			case SOURCE_MODEL_VIEW:	//if we're changing to the source view
				getSourceTextPane().getDocument().removeDocumentListener(getModifyDocumentListener());	//don't listen for changes to the source text pane
				if(xml!=null)	//if we have XML
				{
					final XMLSerializer xmlSerializer=new XMLSerializer(false);	//create an unformatted XML serializer
					String source=null;	//we'll serialize the XML in the model
					if(xml instanceof Document)	//if the model models a document
					{
						source=xmlSerializer.serialize(((Document)xml));	//serialize the XML document to a string
					}
					else if(xml instanceof DocumentFragment)	//if the model models a document fragment
					{
						source=xmlSerializer.serialize((DocumentFragment)xml);	//serialize the XML document fragment to a string
					}
					if(source!=null)	//if we serialized the model
					{					
						getSourceTextPane().setText(source.trim());	//show the XML source in the source text pane, trimming beginning and ending whitespace TODO use a Unicode-aware trim
						getSourceTextPane().setCaretPosition(0);  //scroll to the top of the text
					}
				}
				else	//if we don't have XML
				{
					getSourceTextPane().setDocument(getSourceTextPane().getEditorKit().createDefaultDocument());	//create a default document
				}
				getSourceTextPane().getDocument().addDocumentListener(getModifyDocumentListener());	//add ourselves as a document listener to the source text pane
				break;
		}
	}

	/**Stores the current data being edited to the model, if necessary.
	@param modelView The view of the data, such as <code>SUMMARY_MODEL_VIEW</code>.
	@exception IOException Thrown if there was an error saving the model.
	*/
	protected void saveModel(final int modelView) throws IOException
	{
		super.saveModel(modelView);	//do the default saving
		final XMLNodeModel<N> model=getModel();	//get the data model
		switch(modelView)	//see which view of data we have, in order to get the current XML
		{
			case SOURCE_MODEL_VIEW:	//if we should store the XML source
				{
					final String sourceText=getSourceText();	//get the source text being edited
					if(sourceText.length()>0)	//if we have source text
					{
						final byte[] sourceBytes=sourceText.getBytes(Charsets.UTF_8_CHARSET);	//convert the string to a series of UTF-8 bytes
						final InputStream inputStream=new BufferedInputStream(new ByteArrayInputStream(sourceBytes));	//create an input stream to the source as bytes
						try
						{
							final Document document=XML.parse(inputStream, model.getBaseURI(), true, new URIInputStreamableXMLEntityResolver(model));	//parse the XML document
//TODO del Log.trace("document tree from source");
//TODO del XMLUtilities.printTree(document, System.out);
							saveModel(model, document);	//store the document in the model
						}
						finally
						{
							inputStream.close();	//always close the input stream from the source bytes
						}
					}
					else	//if there is no source text
					{
						model.setXML(null);	//there can be no XML
					}
				}
				break;
			case WYSIWYG_MODEL_VIEW:	//if we should store the XML in the XML text pane
				if(getXMLTextPane().getEditorKit() instanceof XMLEditorKit)	//if the text pane has an XML editor kit
				{
					final XMLEditorKit xmlEditorKit=(XMLEditorKit)getXMLTextPane().getEditorKit();	//get the XML editor kit
					if(getXMLTextPane().getDocument() instanceof XMLDocument)	//if this is an Swing XML document
					{
						final XMLDocument swingDocument=(XMLDocument)getXMLTextPane().getDocument();	//get the Swing document
						final Document document=xmlEditorKit.getXML(swingDocument);	//get the XML DOM document from the Swing document
//TODO del						Log.trace("document tree from WYSIWYG");
//TODO del						XMLUtilities.printTree(document, System.out);
						saveModel(model, document);	//store the document in the model
					}
				}
				break;
		}
	}

	/**@return The edited source text, modified as appropriate for the type of model.
	This version trims the text of beginning and ending whitespace.
	*/
	protected String getSourceText()
	{
		return getSourceTextPane().getText().trim();	//get the current source text, trimming it of beginning and trailing whitespace TODO use a Unicode-aware trim		
	}
	
	/**Saves the given document contents to the given model.
	@param model The model to update.
	@param document The XML document the contents of which to store in the model.
	*/
	protected abstract void saveModel(final XMLNodeModel<N> model, final Document document);
	
	/**Indicates that the view of the data has changed.
	@param oldView The view before the change.
	@param newView The new view of the data
	*/
	protected void onModelViewChange(final int oldView, final int newView)
	{
		super.onModelViewChange(oldView, newView);	//perform the default functionality
		switch(oldView)	//see which view we're changing from
		{
			case WYSIWYG_MODEL_VIEW:	//if we're changing from the WYSIWYG view
				getXMLTextPane().getDocument().removeDocumentListener(getModifyDocumentListener());	//don't listen for changes to the XML text pane any more
				getXMLTextPane().setDocument(getXMLTextPane().getEditorKit().createDefaultDocument());	//remove the content from the XML text pane by installing a new document
				getUnicodeStatusBar().detach(getXMLTextPane());	//remove the Unicode status bar as a listener from this text pane
				break;
			case SOURCE_MODEL_VIEW:	//if we're changing from the source view
				getSourceTextPane().getDocument().removeDocumentListener(getModifyDocumentListener());	//don't listen for changes to the source text pane any more
				getSourceTextPane().setDocument(getSourceTextPane().getEditorKit().createDefaultDocument());	//remove the content from the source text pane by installing a new document
				getUnicodeStatusBar().detach(getSourceTextPane());	//remove the Unicode status bar as a listener from this text pane
				break;
		}
		switch(newView)	//see which view we're changing to
		{
			case WYSIWYG_MODEL_VIEW:	//if we're changing to the WYSIWYG view
				getXMLTextPane().getDocument().addDocumentListener(getModifyDocumentListener());	//add ourselves as a document listener to the XML text pane
				getUnicodeStatusBar().attach(getXMLTextPane());	//add the Unicode status bar as a listener to the text pane
				break;
			case SOURCE_MODEL_VIEW:	//if we're changing to the source view
				getSourceTextPane().getDocument().addDocumentListener(getModifyDocumentListener());	//add ourselves as a document listener to see if the source pane is modified
				getUnicodeStatusBar().attach(getSourceTextPane());	//add the Unicode status bar as a listener to the text pane
				break;
		}
	}

	/**Unloads the open resource, if any, and resets to defaults.*/
/*TODO fix
	public void close()
	{
		getXMLPanel().setXMLModel(new XMLDocumentModel());	//remove all the XML from the XML panel
	}
*/

}
package com.garretwilson.swing;

import java.io.*;
import java.net.URI;
import javax.swing.*;
import javax.swing.event.*;
import com.garretwilson.io.MediaType;
import com.garretwilson.swing.text.xml.*;
import com.garretwilson.text.CharacterEncodingConstants;
import com.garretwilson.text.xml.XMLDocumentFragmentModel;
import com.garretwilson.text.xml.XMLDocumentModel;
import com.garretwilson.text.xml.XMLNamespaceProcessor;
import com.garretwilson.text.xml.XMLNodeModel;
import com.garretwilson.text.xml.XMLProcessor;
import com.garretwilson.text.xml.XMLSerializer;
import com.garretwilson.text.xml.XMLUtilities;
import org.w3c.dom.*;

/**Panel that displays XML and source code.
@author Garret Wilson
*/
public class XMLPanel extends TabbedViewPanel
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
/*G***TODO fix editable
	public boolean isEditable() {return xmlTextPane.isEditable();}
*/	
	/**Sets whether or not the text can be edited.
	@param editable <code>true</code> if the text can be edited, or
		<code>false</code> if the information should be read-only.
	*/
/*G***TODO fix editable
	public void setEditable(final boolean editable)
	{
		xmlTextPane.setEditable(editable);	//show whether or not the text pane is editable


//G***fix		xmlTextPane.setBackground(editable ? Color.WHITE : Color.LIGHT_GRAY);	//set the color to match


//G***del		textPane.setEnabled(editable);	//set the enabled status to match
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

	/**A listener that changes the modification status to <code>true</code> when
		a document has been modified.
	*/
	private final DocumentListener modifyDocumentListener;

		/**A listener that changes the modification status to <code>true</code> when
			a document has been modified.
		*/
		protected DocumentListener getModifyDocumentListener() {return modifyDocumentListener;}

	/**@return The data model for which this component provides a view.
	@see ModelViewablePanel#getModel()
	*/
	public XMLNodeModel getXMLModel() {return (XMLNodeModel)getModel();}

	/**Sets the data model.
	@param model The data model for which this component provides a view.
	@see ModelViewablePanel#setModel(Model)
	*/
	public void setXMLModel(final XMLNodeModel model)
	{
		xmlTextPane.setURIInputStreamable(model.getURIInputStreamable());	//make sure the text pane knows from where to get input streams
		setModel(model);	//set the model
	}

	/**Model constructor.
	@param model The data model for which this component provides a view.
	*/
	public XMLPanel(final XMLNodeModel model)
	{
		this(model, true);	//construct and initialize the panel
	}

	/**Initialization constructor.
	@param model The data model for which this component provides a view.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public XMLPanel(final XMLNodeModel model, final boolean initialize)
	{
		this(model, new MediaType(MediaType.TEXT, MediaType.XML), initialize);	//construct the panel with a default text/xml media type
	}

	/**Content type constructor.
	@param model The data model for which this component provides a view.
	@param mediaType The content type of the XML.
	*/
	public XMLPanel(final XMLNodeModel model, final MediaType mediaType)
	{
		this(model, mediaType, true);	//construct and initialize the panel
	}

	/**Content type and initialization constructor.
	@param model The data model for which this component provides a view.
	@param mediaType The content type of the XML.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public XMLPanel(final XMLNodeModel model, final MediaType mediaType, final boolean initialize)
	{
		super(model, false);	//construct the parent class without initializing the panel
		setSupportedModelViews(DEFAULT_SUPPORTED_MODEL_VIEWS);	//set the model views we support
		setDefaultDataView(DEFAULT_DEFAULT_MODEL_VIEW);	//set the default data view
		xmlTextPane=new XMLTextPane();	//create a new XML text pane
		setContentType(mediaType);	//set the content type 
		xmlScrollPane=new JScrollPane(xmlTextPane);	//create a new scroll pane with the XML text pane inside
		sourceTextPane=new JTextPane();	//create a new text pane for editing the source
		sourceScrollPane=new JScrollPane(sourceTextPane);	//create a new scroll pane with the source text pane inside
		modifyDocumentListener=createModifyDocumentListener();	//create a document listener to change our modification status when a document changes
		if(initialize)  //if we should initialize
			initialize();   //initialize the panel
	}

	/**Initialize the user interface.*/
	protected void initializeUI()
	{
		addView(WYSIWYG_MODEL_VIEW, getXMLTextPane().getContentType(), getXMLScrollPane());	//add the XML text pane as the WYSIWYG view G***i18n
		addView(SOURCE_MODEL_VIEW, "XML", getSourceScrollPane());	//add the source XML text pane as the source view G***i18n
		super.initializeUI(); //do the default UI initialization
//TODO check for the content type changing, and update the tab name in response

				//add ourselves to listen to the XML text pane, so that if the XML text pane
				//	changes documents we can add ourselves to the document to listen for modifications
/*G***del if not needed
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
		setModified(false);	//show that the information has not been modified
	}

	/**@return The XML content type of the panel.*/
	public MediaType getContentType()
	{
		return new MediaType(getXMLTextPane().getContentType());	//return the XML text pane content type
	}	

	/**Sets the XML content type of the panel.
	@param mediaType The content type of the XML.
	*/
	public void setContentType(final MediaType mediaType)
	{
		getXMLTextPane().setContentType(mediaType.toString());	//set the content type of the text pane
	}

	/**Loads the data from the model to the view, if necessary.
	@exception IOException Thrown if there was an error loading the model.
	*/
	protected void loadModel() throws IOException
	{
		super.loadModel();	//do the default loading
		final XMLNodeModel model=getXMLModel();	//get the data model
		switch(getModelView())	//see which view of data we should load
		{
			case WYSIWYG_MODEL_VIEW:	//if we're changing to the WYSIWYG view
				getXMLTextPane().getDocument().removeDocumentListener(getModifyDocumentListener());	//don't listen for changes to the XML text pane
				if(model.getXML()!=null)	//if we have XML
				{
					getXMLTextPane().setURIInputStreamable(model.getURIInputStreamable());	//make sure the text pane knows from where to get input streams
					if(model instanceof XMLDocumentModel)	//if the model models a document
					{
						getXMLTextPane().setXML(((XMLDocumentModel)model).getDocument(), model.getBaseURI(), getContentType());	//put the XML document into the XML text pane
					}
					else if(model instanceof XMLDocumentFragmentModel)	//if the model models a document fragment
					{
						getXMLTextPane().setXML(((XMLDocumentFragmentModel)model).getDocumentFragment(), model.getBaseURI(), getContentType());	//put the XML document into the XML text pane
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
				if(model.getXML()!=null)	//if we have XML
				{
					final XMLSerializer xmlSerializer=new XMLSerializer(false);	//create an unformatted XML serializer
					String source=null;	//we'll serialize the XML in the model
					if(model instanceof XMLDocumentModel)	//if the model models a document
					{
						source=xmlSerializer.serialize(((XMLDocumentModel)model).getDocument());	//serialize the XML document to a string
					}
					else if(model instanceof XMLDocumentFragmentModel)	//if the model models a document fragment
					{
						source=xmlSerializer.serialize(((XMLDocumentFragmentModel)model).getDocumentFragment());	//serialize the XML document fragment to a string
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
	@exception IOException Thrown if there was an error loading the model.
	*/
	protected void saveModel() throws IOException
	{
		super.saveModel();	//do the default saving
		final XMLNodeModel model=getXMLModel();	//get the data model
		switch(getModelView())	//see which view of data we have, in order to get the current XML
		{
			case SOURCE_MODEL_VIEW:	//if we should store the XML source
				{
					final StringBuffer sourceStringBuffer=new StringBuffer(getSourceTextPane().getText().trim());	//get the current source text, trimming it of beginning and trailing whitespace TODO use a Unicode-aware trim
					if(sourceStringBuffer.length()>0)	//if there is source text
					{
						if(model instanceof XMLDocumentFragmentModel)	//if we're modeling a document fragment
						{
							final StringBuffer prologStringBuffer=new StringBuffer("<?xml version=\"1.0\"?>\n");	//construct the XML prolog TODO use a constant here
							prologStringBuffer.append("<div");	//append a root element start tag TODO use something HTML-agnostic
							final URI defaultNamespaceURI=XMLNamespaceProcessor.getDefaultNamespaceURI(getContentType());	//see if we have a default namespace for the media type we're using
							if(defaultNamespaceURI!=null)	//if we know the default namespace
							{
								prologStringBuffer.append(" xmlns=\"").append(defaultNamespaceURI).append("\"");	//add a default XML namespace declaration attribute
							}
							prologStringBuffer.append('>');	//finish the root element start tag
							sourceStringBuffer.insert(0, prologStringBuffer);	//insert the prolog into the source code
							sourceStringBuffer.append("</div>");	//append the ending tag for the root element to the source code
						}
						final XMLProcessor xmlProcessor=new XMLProcessor(model.getURIInputStreamable());	//create an XML processor to read the source
						final byte[] sourceBytes=sourceStringBuffer.toString().getBytes(CharacterEncodingConstants.UTF_8);	//convert the string to a series of UTF-8 bytes
						final InputStream inputStream=new BufferedInputStream(new ByteArrayInputStream(sourceBytes));	//create an input stream to the source as bytes
						try
						{
							final Document document=xmlProcessor.parseDocument(inputStream, model.getBaseURI());	//parse the XML document
//G***del Debug.trace("document tree from source");
//G***del XMLUtilities.printTree(document, System.out);

							if(model instanceof XMLDocumentModel)	//if we're modeling a document
							{
								((XMLDocumentModel)model).setDocument(document);	//put the whole document in the model
							}
							else if(model instanceof XMLDocumentFragmentModel)	//if we're modeling a document fragment
							{
									//extract the child elements into a document fragment and set that in the model
								((XMLDocumentFragmentModel)model).setDocumentFragment(XMLUtilities.extractChildren(document.getDocumentElement()));
							}
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
//G***del						Debug.trace("document tree from WYSIWYG");
//G***del						XMLUtilities.printTree(document, System.out);
						if(model instanceof XMLDocumentModel)	//if we're modeling a document
						{
							((XMLDocumentModel)model).setDocument(document);	//put the whole document in the model
						}
						else if(model instanceof XMLDocumentFragmentModel)	//if we're modeling a document fragment
						{
								//extract the child elements into a document fragment and set that in the model
							((XMLDocumentFragmentModel)model).setDocumentFragment(XMLUtilities.extractChildren(document.getDocumentElement()));
						}
					}
				}
				break;
		}
	}

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
				break;
			case SOURCE_MODEL_VIEW:	//if we're changing from the source view
				getSourceTextPane().getDocument().removeDocumentListener(getModifyDocumentListener());	//don't listen for changes to the source text pane any more
				getSourceTextPane().setDocument(getSourceTextPane().getEditorKit().createDefaultDocument());	//remove the content from the source text pane by installing a new document
				break;
		}
		switch(newView)	//see which view we're changing to
		{
			case WYSIWYG_MODEL_VIEW:	//if we're changing to the WYSIWYG view
				getXMLTextPane().getDocument().addDocumentListener(getModifyDocumentListener());	//add ourselves as a document listener to the XML text pane
				break;
			case SOURCE_MODEL_VIEW:	//if we're changing to the source view
				getSourceTextPane().getDocument().addDocumentListener(getModifyDocumentListener());	//add ourselves as a document listener to see if the source pane is modified
				break;
		}
	}

}
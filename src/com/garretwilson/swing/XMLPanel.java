package com.garretwilson.swing;

import java.io.*;
import java.net.URI;
import javax.swing.*;
import javax.swing.event.*;
import com.garretwilson.io.MediaType;
import com.garretwilson.io.URIInputStreamable;
import com.garretwilson.swing.text.xml.*;
import com.garretwilson.text.CharacterEncodingConstants;
import com.garretwilson.text.xml.XMLProcessor;
import com.garretwilson.text.xml.XMLSerializer;
import com.garretwilson.util.Debug;
import org.w3c.dom.Document;

/**Panel that displays XML and source code.
<p>The canonical XML tree is stored in the currently available view.</p>
@author Garret Wilson
*/
public class XMLPanel extends TabbedViewPanel
{

	/**The default data views supported by this panel.*/
	private final int DEFAULT_SUPPORTED_DATA_VIEWS=WYSIWYG_DATA_VIEW|SOURCE_DATA_VIEW;

	/**The data views supported by the panel, ORed together.*/
	private int supportedDataViews;

		/**@return A value representing the supported data views ORed together.*/
		public int getSupportedDataViews() {return supportedDataViews;}
	
		/**Sets the data views supported by this panel. 
		@param dataViews A value representing the supported data views ORed together.
		*/
		protected void setSupportedDataViews(final int dataViews)
		{
			supportedDataViews=dataViews;	//update the supported data views
		}

	/**The default default data view of this panel.*/
	private final int DEFAULT_DEFAULT_DATA_VIEW=WYSIWYG_DATA_VIEW;

	/**The default data view of this panel.*/
	private int defaultDataView;

		/**@return The default view of the data, such as <code>SUMMARY_DATA_VIEW</code>.*/
		public int getDefaultDataView() {return defaultDataView;}

		/**Sets the default data view.
		@param dataView The default view of the data, such as <code>SUMMARY_DATA_VIEW</code>.
		*/
		public void setDefaultDataView(final int dataView) {defaultDataView=dataView;}

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

	/**@return The base URI of the XML, or <code>null</code> if unknown.
	@see XMLTextPane#getBaseURI
	*/
	public URI getBaseURI() {return xmlTextPane.getBaseURI();}

	/**Sets the base URI.
	@param baseURI The base URI of the XML, or <code>null</code> if unknown.
	@see XMLTextPane#setBaseURI
	*/
	public void setBaseURI(final URI baseURI) {xmlTextPane.setBaseURI(baseURI);}

	/**@return The implementation to use for retrieving an input stream to a URI.
	@see XMLTextPane#getURIInputStreamable()
	*/
	public URIInputStreamable getURIInputStreamable() {return xmlTextPane.getURIInputStreamable();}
		
	/**Sets the implementation to use for retrieving an input stream to a URI.
	@param inputStreamable The implementation to use for accessing a URI for input.
	@see XMLTextPane#setURIInputStreamable()
	*/
	public void setURIInputStreamable(final URIInputStreamable inputStreamable) {xmlTextPane.setURIInputStreamable(inputStreamable);}

	/**A listener that changes the modification status to <code>true</code> when
		a document has been modified.
	*/
	private final DocumentListener modifyDocumentListener;

		/**A listener that changes the modification status to <code>true</code> when
			a document has been modified.
		*/
		protected DocumentListener getModifyDocumentListener() {return modifyDocumentListener;}

	/**The XML document representing the data model, or <code>null</code> if
		there is no XML.
	*/
	private Document xml;

	/**Default constructor.*/
	public XMLPanel()
	{
		this(true);	//construct and initialize the panel
	}

	/**Initialization constructor.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public XMLPanel(final boolean initialize)
	{
		this(new MediaType(MediaType.TEXT, MediaType.XML), initialize);	//construct the panel with a default text/xml media type
	}

	/**Content type constructor.
	@param mediaType The content type of the XML.
	*/
	public XMLPanel(final MediaType mediaType)
	{
		this(mediaType, true);	//construct and initialize the panel
	}

	/**Content type and initialization constructor.
	@param mediaType The content type of the XML.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public XMLPanel(final MediaType mediaType, final boolean initialize)
	{
		super(false);	//construct the parent class without initializing the panel
		supportedDataViews=DEFAULT_SUPPORTED_DATA_VIEWS;	//set the data views we support
		defaultDataView=DEFAULT_DEFAULT_DATA_VIEW;	//set the default data view
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
		super.initializeUI(); //do the default UI initialization
		getTabbedPane().setTabPlacement(JTabbedPane.BOTTOM);
//G***fix		xmlTextPane.setContentType(MediaType.APPLICATION_XHTML_XML);	//set the content type to "application/xhtml+xml" G***maybe allow this panel to support multiple MIME types, and put the setting of the type back into XHTMLResourceKit
		addView(WYSIWYG_DATA_VIEW, getXMLTextPane().getContentType(), getXMLScrollPane());	//add the XML text pane as the WYSIWYG view G***i18n
		addView(SOURCE_DATA_VIEW, "XML", getSourceScrollPane());	//add the source XML text pane as the source view G***i18n
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

	/**Sets the given XML data.
	<p>The installed editor kit for the current content type will be used to
		create a new document, into which the XML data will be loaded. If the
		installed editor kit for the current content type is not an
		<code>XMLEditorKit</code>, no action occurs.</p>
	@param xml The XML document that contains the data.
	@see #setContentType()
	*/
	public void setXML(final Document xml)
	{
		this.xml=xml;	//store the XML
		try
		{
			loadXML(getDataView());	//try to load the XML into our current data view
			setModified(false);	//show that the information has not been modified
		}
		catch(IOException ioException)
		{
			Debug.error(ioException);	//G***fix better
		}
	}

	/**@return The XML document representing the data model, or <code>null</code> if
		there is no XML.
	*/
	public Document getXML()
	{
		try
		{
			saveXML(getDataView());	//store any XML that is being edited, if any XML is being edited
		}
		catch(IOException ioException)	//if there were any problems storing the XML
		{
			return null;	//show that we don't have any XML to return, as we can't store it
		}
		return xml;	//return the XML that was just stored or was already stored
	}

	/**Stores the current XML being edited to the local variable.
	If no XML is being edited, no action occurs.
	@param dataView The view of the data that should be stored.
	@exception IOException Thrown if there is an error storing the XML from the
		data view.
	*/
	protected void saveXML(final int dataView) throws IOException
	{
		switch(dataView)	//see which view of data we have, in order to get the current XML
		{
			case SOURCE_DATA_VIEW:	//if we should store the XML source
				{
					final String sourceString=getSourceTextPane().getText();	//get the current source text
					if(sourceString.length()>0)	//if there is source text
					{
						final XMLProcessor xmlProcessor=new XMLProcessor(getURIInputStreamable());	//create an XML processor to read the source
						final byte[] sourceBytes=sourceString.getBytes(CharacterEncodingConstants.UTF_8);	//convert the string to a series of UTF-8 bytes
						final InputStream inputStream=new BufferedInputStream(new ByteArrayInputStream(sourceBytes));	//create an input stream to the source as bytes
						try
						{
							xml=xmlProcessor.parseDocument(inputStream, getBaseURI());	//parse the document into the XML data model
		
						}
						finally
						{
							inputStream.close();	//always close the input stream from the source bytes
						}
					}
					else	//if there is no source text
					{
						xml=null;	//there can be no XML
					}
				}
				break;
			case WYSIWYG_DATA_VIEW:	//if we should store the XML in the XML text pane
				if(getXMLTextPane().getDocument() instanceof XMLDocument)	//if this is an Swing XML document
				{
					final XMLDocument xmlDocument=(XMLDocument)getXMLTextPane().getDocument();	//get the XML document
					xml=xmlDocument.getXML();	//get the XML from the document
				}
				break;
		}
	}
	
	/**Loads the stored XML from the local variable to the given view.
	@param dataView The view of the data that should be loaded.
	@exception IOException Thrown if there is an error loading the XML into the
		data view.
	*/
	protected void loadXML(final int dataView) throws IOException
	{
		switch(dataView)	//see which view of data we should load
		{
			case WYSIWYG_DATA_VIEW:	//if we're changing to the WYSIWYG view
				getXMLTextPane().getDocument().removeDocumentListener(getModifyDocumentListener());	//don't listen for changes to the XML text pane
				if(xml!=null)	//if we have XML
				{
					getXMLTextPane().setXML(xml, getBaseURI(), getContentType());	//put the XML into the XML text pane
				}
				else	//if we don't have any XML
				{
					getXMLTextPane().setDocument(getXMLTextPane().getEditorKit().createDefaultDocument());	//create a default document
				}
				getXMLTextPane().getDocument().addDocumentListener(getModifyDocumentListener());	//add ourselves as a document listener to the XML text pane
				break;
			case SOURCE_DATA_VIEW:	//if we're changing to the source view
				getSourceTextPane().getDocument().removeDocumentListener(getModifyDocumentListener());	//don't listen for changes to the source text pane
				if(xml!=null)	//if we have XML
				{
					final XMLSerializer xmlSerializer=new XMLSerializer(true);	//create a formatted XML serializer
					final String source=xmlSerializer.serialize(xml);	//serialize the XML to a string
					getSourceTextPane().setText(source);	//show the XML source in the source text pane
					getSourceTextPane().setCaretPosition(0);  //scroll to the top of the text
				}
				else	//if we don't have XML
				{
					getSourceTextPane().setDocument(getSourceTextPane().getEditorKit().createDefaultDocument());	//create a default document
				}
				getSourceTextPane().getDocument().addDocumentListener(getModifyDocumentListener());	//add ourselves as a document listener to the source text pane
				break;
		}
	}

	/**Called when content is inserted into the document.
	@param documentEvent Information about the event.
	@see DocumentListener#insertUpdate(DocumentEvent)
	*/
	public void insertUpdate(final DocumentEvent documentEvent)
	{
		setModified(true);	//show that content has been modified
	}

	/**Called when content is removed from the document.
	@param documentEvent Information about the event.
	@see DocumentListener#removeUpdate(DocumentEvent)
	*/
	public void removeUpdate(final DocumentEvent documentEvent)
	{
		setModified(true);	//show that content has been modified
	}

	/**Called when document content is changed.
	@param documentEvent Information about the event.
	@see DocumentListener#changedUpdate(DocumentEvent)
	*/
	public void changedUpdate(final DocumentEvent documentEvent)
	{
//G***del Debug.trace("modified!");
//G***del		Debug.traceStack("changed");	//G***del
//G***del		setModified(true);	//show that content has been modified
	}	

	/**Indicates that the view of the data has changed.
	@param oldView The view before the change.
	@param newView The new view of the data
	*/
	protected void onDataViewChanged(final int oldView, final int newView)
	{		
		super.onDataViewChanged(oldView, newView);	//perform the default functionality		
		try
		{
			saveXML(oldView);	//store any XML that was being edited in the old view, if any
			switch(oldView)	//see which view we're changing from
			{
				case WYSIWYG_DATA_VIEW:	//if we're changing from the WYSIWYG view
					getXMLTextPane().getDocument().removeDocumentListener(getModifyDocumentListener());	//don't listen for changes to the XML text pane any more
					getXMLTextPane().setDocument(getXMLTextPane().getEditorKit().createDefaultDocument());	//remove the content from the XML text pane by installing a new document
					break;
				case SOURCE_DATA_VIEW:	//if we're changing from the source view
					getSourceTextPane().getDocument().removeDocumentListener(getModifyDocumentListener());	//don't listen for changes to the source text pane any more
					getSourceTextPane().setDocument(getSourceTextPane().getEditorKit().createDefaultDocument());	//remove the content from the source text pane by installing a new document
					break;
			}
		}
		catch(IOException ioException)	//if there were any problems storing the XML G***does this handle all XML errors?
		{
			setDataView(oldView);	//switch back to the old view
			OptionPane.showMessageDialog(getSourceTextPane(), ioException.getMessage(), ioException.getClass().getName(), JOptionPane.ERROR_MESSAGE);	//G***i18n; TODO fix in a common routine
			return;	//don't process further, because this would erase the source
		}
		try
		{
			loadXML(newView);	//try to load the XML into the new view
			switch(newView)	//see which view we're changing to
			{
				case WYSIWYG_DATA_VIEW:	//if we're changing to the WYSIWYG view
					getXMLTextPane().getDocument().addDocumentListener(getModifyDocumentListener());	//add ourselves as a document listener to the XML text pane
					break;
				case SOURCE_DATA_VIEW:	//if we're changing to the source view
					getSourceTextPane().getDocument().addDocumentListener(getModifyDocumentListener());	//add ourselves as a document listener to see if the source pane is modified
					break;
			}
		}
		catch(IOException ioException)
		{
			Debug.error(ioException);	//G***fix better
		}
	}

}
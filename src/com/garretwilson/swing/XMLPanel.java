package com.garretwilson.swing;

import java.beans.*;
import java.io.*;
import java.net.URI;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import com.garretwilson.io.InputStreamUtilities;
import com.garretwilson.io.MediaType;
import com.garretwilson.io.URIInputStreamable;
import com.garretwilson.swing.text.xml.*;
import com.garretwilson.text.CharacterEncoding;
import com.garretwilson.text.CharacterEncodingConstants;
import com.garretwilson.util.Debug;

/**Panel that displays XML and source code.
<p>The canonical XML tree is stored in the currently available view.</p>
@author Garret Wilson
*/
public class XMLPanel extends TabbedViewPanel implements DocumentListener
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
		xmlTextPane.addPropertyChangeListener(XMLTextPane.DOCUMENT_PROPERTY, new PropertyChangeListener()
			{
				public void propertyChange(final PropertyChangeEvent propertyChangeEvent)	//if the document property changes
				{
					if(propertyChangeEvent.getOldValue() instanceof Document)	//if we know the old document
						((Document)propertyChangeEvent.getOldValue()).removeDocumentListener(XMLPanel.this);	//remove ourselves as a document listener
					if(propertyChangeEvent.getNewValue() instanceof Document)	//if we know the new document
					((Document)propertyChangeEvent.getNewValue()).addDocumentListener(XMLPanel.this);	//add ourselves as a document listener
				}
			});	//TODO transfer to TextApplicationPanel
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
	@param xmlDocument The XML document that contains the data.
	@param baseURI The base URI, corresponding to the XML document.
	@param mediaType The media type of the XML document.
	@see #setContentType()
	*/
	public void setXML(final org.w3c.dom.Document xmlDocument, final URI baseURI, final MediaType mediaType)
	{
		setXML(new org.w3c.dom.Document[]{xmlDocument}, new URI[]{baseURI}, new MediaType[]{mediaType});	//set the XML using arrays
		setModified(false);	//show that the information has not been modified
	}

	/**Sets the given XML data.
	<p>The installed editor kit for the current content type will be used to
		create a new document, into which the XML data will be loaded. If the
		installed editor kit for the current content type is not an
		<code>XMLEditorKit</code>, no action occurs.</p>
	@param xmlDocumentArray The array of XML documents that contain the data.
	@param baseURIArray The array of base URIs, corresponding to the XML documents.
	@param mediaTypeArray The array of media types of the documents.
	@see #setContentType()
	*/
	public void setXML(final org.w3c.dom.Document[] xmlDocumentArray, final URI[] baseURIArray, final MediaType[] mediaTypeArray)
	{
		getXMLTextPane().setXML(xmlDocumentArray, baseURIArray, mediaTypeArray);	//tell the XML text pane to set the XML
		setModified(false);	//show that the information has not been modified
	}


	/**Reads XML into the panel.
	@param URI baseURI The base URI of the the input stream.
	@param inputStream The input stream from which to read the resource content.
	@throws IOException Thrown if there is an error reading the resource.
	*/ 
	public void read(final URI baseURI, final InputStream inputStream) throws IOException
	{
		try
		{
			inputStream.mark(Integer.MAX_VALUE);	//G***testing
	//	G***probably set a loading flag here, so that we won't recursively try to load
			setBaseURI(baseURI);	//set the base URI of the XML panel
			getXMLTextPane().setPage(baseURI, inputStream);	//tell the text pane to read from the URL
//G***fix		xmlTextPane.setCaretPosition(0);	//move the caret to the beginning of the document
		}
		catch(IOException ioException)	//if there was an error
		{
			setDataView(XMLPanel.SOURCE_DATA_VIEW);	//switch to the source and try to load the source with no interpretation
			final JTextPane sourceTextPane=getSourceTextPane();	//get a reference to the source text pane
			sourceTextPane.getDocument().removeDocumentListener(this);	//remove ourselves as a document listener
			final Document document=sourceTextPane.getEditorKit().createDefaultDocument();	//create a new document
			sourceTextPane.setDocument(document);	//remove the content from the editor kit by installing a new document
			inputStream.reset();	//G***testing
			final CharacterEncoding encoding=InputStreamUtilities.getBOMEncoding(inputStream);	//try to sense from the byte order mark the encoding of the text
				//use the character encoding we sensed to create a reader, using a default encoding if we couldn't sense one from the byte order mark
			final Reader reader=encoding!=null ? new InputStreamReader(inputStream, encoding.toString()) : new InputStreamReader(inputStream);
			try
			{		
				sourceTextPane.getEditorKit().read(reader, document, 0);	//have the editor kit read the document from the reader
			}
			catch(BadLocationException badLocationException)
			{
				Debug.error(badLocationException);	//this should never occcur
			}
			sourceTextPane.setCaretPosition(0);	//move the caret to the beginning of the document
			sourceTextPane.getDocument().addDocumentListener(this);	//add ourselves as a document listener
			OptionPane.showMessageDialog(sourceTextPane, ioException.getMessage(), ioException.getClass().getName(), JOptionPane.ERROR_MESSAGE);	//G***i18n; TODO fix in a common routine
		}
		setModified(false);	//show that the information has not been modified
	}

	/**Called when content is inserted into the document.
	@param documentEvent Information about the event.
	@see DocumentListener#insertUpdate(DocumentEvent)
	*/
	public void insertUpdate(final DocumentEvent documentEvent)
	{
//G***del Debug.trace("modified!");
		setModified(true);	//show that content has been modified
	}

	/**Called when content is removed from the document.
	@param documentEvent Information about the event.
	@see DocumentListener#removeUpdate(DocumentEvent)
	*/
	public void removeUpdate(final DocumentEvent documentEvent)
	{
Debug.trace("modified!");
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
			//right now there are only two views; if we add another panel, we need to add more checks to make sure the source doesn't go away when switching to the third panel
		switch(newView)	//see what view we're changing to
		{
			case WYSIWYG_DATA_VIEW:	//if we're changing to the WYSIWYG view
				try
				{
					final String sourceString=getSourceTextPane().getText();	//get the current source text
					if(sourceString.length()>0)	//if there is source text
					{
						final byte[] sourceBytes=sourceString.getBytes(CharacterEncodingConstants.UTF_8);	//convert the string to a series of UTF-8 bytes
						final InputStream inputStream=new BufferedInputStream(new ByteArrayInputStream(sourceBytes));	//create an input stream to the source as bytes				
						try
						{
							final XMLTextPane xmlTextPane=getXMLTextPane();	//get a reference to the text pane
							xmlTextPane.setURIInputStreamable(getURIInputStreamable());	//make sure the XML text pane knows how to retrieve input streams
							//TODO improve the transition between InputStream/description to URI/InputStream
							xmlTextPane.setPage(getBaseURI(), inputStream);	//tell the text pane to read from our local array of source bytes
							getSourceTextPane().getDocument().removeDocumentListener(this);	//don't listen for changes to the soure text pane any more
							getSourceTextPane().setDocument(getSourceTextPane().getEditorKit().createDefaultDocument());	//remove the content from the source text pane by installing a new document
	
						}
						finally
						{
							inputStream.close();	//always close the input stream from the source bytes
						}
					}
				}
				catch(UnsupportedEncodingException unsupportedEncodingException)	//this should never happen---UTF-8 should always be supported
				{
					Debug.error(unsupportedEncodingException);
				}
				catch(IOException ioException)	//if an IO error occurs TODO check for other errors
				{
					setDataView(oldView);	//switch back to the old view
					OptionPane.showMessageDialog(getSourceTextPane(), ioException.getMessage(), ioException.getClass().getName(), JOptionPane.ERROR_MESSAGE);	//G***i18n; TODO fix in a common routine
					return;	//don't process further, because this would erase the source
				}
				break;
			case SOURCE_DATA_VIEW:	//if we're changing to the source view
				try
				{
					final Document document=getXMLTextPane().getDocument();  //get the document in the XML text pane
					final XMLEditorKit xmlEditorKit=(XMLEditorKit)xmlTextPane.getEditorKit(); //get the editor kit being used G***make sure this is an XML editor kit
					final ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();	//create an output stream for saving the contents in memory
					try
					{
						xmlEditorKit.write(byteArrayOutputStream, CharacterEncodingConstants.UTF_8, document, 0, document.getLength());  //write the document to a byte array in UTF-8 
					}
					finally
					{
						byteArrayOutputStream.close(); //always close the output stream
					}
					getSourceTextPane().setText(byteArrayOutputStream.toString(CharacterEncodingConstants.UTF_8));	//show the XML source in the source text pane
	//G***del if not needed						getUnicodeStatusBar().updateStatus(getSourceTextPane());	//update the Unicode status
				}
				catch(Exception e)  //if we have a problem writing the data
				{
					getSourceTextPane().setText(e.getMessage());  //show the error message in the text area
					Debug.error(e); //record the error G***fix
				}
				getSourceTextPane().setCaretPosition(0);  //scroll to the top of the text
				getSourceTextPane().getDocument().addDocumentListener(this);	//add ourselves as a document listener to see if the source pane is modified
				break;
		}
	}

}
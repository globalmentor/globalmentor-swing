package com.garretwilson.swing.rdf;

import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import com.garretwilson.swing.*;
import com.garretwilson.swing.rdf.tree.*;
import com.garretwilson.text.CharacterEncodingConstants;
import com.garretwilson.text.xml.XMLDOMImplementation;
import com.garretwilson.text.xml.XMLProcessor;
import com.garretwilson.text.xml.XMLSerializer;
import com.garretwilson.util.Debug;
import com.garretwilson.io.*;
import com.garretwilson.rdf.*;
import org.w3c.dom.*;

/**Panel that displays RDF and allows it to be explored. A tab is
	presented to allow viewing the RDF+XML source code.
<p>This implementation relies on the embedded <code>RDFTree</code> to store the
	canonical copy of the RDF model.</p>
@author Garret Wilson
@see RDFTree
*/
public class RDFPanel extends TabbedViewPanel implements URIInputStreamable
{

	/**The default data views supported by this panel.*/
	private final int DEFAULT_SUPPORTED_DATA_VIEWS=TREE_MODEL_VIEW|SOURCE_MODEL_VIEW;

	/**The data views supported by the panel, ORed together.*/
	private int supportedDataViews;

		/**@return A value representing the supported data views ORed together.*/
		public int getSupportedModelViews() {return supportedDataViews;}
	
		/**Sets the data views supported by this panel. 
		@param dataViews A value representing the supported data views ORed together.
		*/
		protected void setSupportedDataViews(final int dataViews)
		{
			supportedDataViews=dataViews;	//update the supported data views
		}

	/**The default default data view of this panel.*/
	private final int DEFAULT_DEFAULT_DATA_VIEW=TREE_MODEL_VIEW;

	/**The default data view of this panel.*/
	private int defaultDataView;

		/**@return The default view of the data, such as <code>SUMMARY_MODEL_VIEW</code>.*/
		public int getDefaultModelView() {return defaultDataView;}

		/**Sets the default data view.
		@param dataView The default view of the data, such as <code>SUMMARY_MODEL_VIEW</code>.
		*/
		public void setDefaultDataView(final int dataView) {defaultDataView=dataView;}

	/**The tree tab in which the RDF is displayed.*/
	private final RDFTree rdfTree;

		/**@return The tree tab in which the RDF is displayed.*/
		public RDFTree getRDFTree() {return rdfTree;}

	/**The scroll pane for the RDF tree.*/
	protected final JScrollPane rdfScrollPane;

	/**The scroll pane for the XML.*/
	protected final JScrollPane xmlScrollPane;

	/**The source text pane.*/
	private final JTextPane sourceTextPane; 

		/**@return The source text pane.*/
		public JTextPane getSourceTextPane() {return sourceTextPane;}

	/**Whether the RDF can be edited.*/
	private boolean editable;

		/**@return <code>true</code> if the RDF can be edited.*/
		public boolean isEditable() {return editable;}
	
		/**Sets whether or not the RDF can be edited.
		@param editable <code>true</code> if the RDF can be edited, or
			<code>false</code> if the information should be read-only.
		*/
		public void setEditable(final boolean editable)
		{
			this.editable=editable;	//save the new editable status
			sourceTextPane.setEditable(editable);	//set whether the text pane is editable
		}

	/**The DOM implementation used for serializing the RDF.*/
	protected final DOMImplementation domImplementation;

	/**The RDF data model being displayed.*/
	private RDF rdf;

		/**@return The RDF data model being displayed.*/
//G***del when works		public RDF getRDF() {return rdf;}

		/**Sets the RDF data model.
		@param rdf The RDF data model to display.
		*/
		public void setRDF(final RDF rdf)
		{
			setRDF(rdf, null);  //set the RDF without specifying a resource
		}
	
	/**The main resource being displayed, or <code>null</code> if the entire
		data model is being displayed.
	*/
	private RDFResource rdfResource;

		/**@return The main resource being displayed, or <code>null</code> if the
			entire data model is being displayed.
		*/
		public RDFResource getResource() {return rdfResource;}

	/**The base URI of the RDF, or <code>null</code> if unknown.*/
	private URI baseURI=null;
	
		/**@return The base URI of the RDF, or <code>null</code> if unknown.*/
		public URI getBaseURI() {return baseURI;}

		/**Sets the base URI.
		@param baseURI The base URI of the RDF, or <code>null</code> if unknown.
		*/
		public void setBaseURI(final URI baseURI) {this.baseURI=baseURI;}

	/**The implementation to use for retrieving an input stream to a URI.*/
	private URIInputStreamable uriInputStreamable=this;

		/**@return The implementation to use for retrieving an input stream to a URI.*/
		public URIInputStreamable getURIInputStreamable() {return uriInputStreamable;}
		
		/**Sets the implementation to use for retrieving an input stream to a URI.
		@param inputStreamable The implementation to use for accessing a URI for input.
		*/
		public void setURIInputStreamable(final URIInputStreamable inputStreamable) {uriInputStreamable=inputStreamable;}

	/**A listener that changes the modification status to <code>true</code> when
		a document has been modified.
	*/
	private final DocumentListener modifyDocumentListener;

		/**A listener that changes the modification status to <code>true</code> when
			a document has been modified.
		*/
		protected DocumentListener getModifyDocumentListener() {return modifyDocumentListener;}

	/**Default constructor with a default RDF data model.*/
	public RDFPanel()
	{
		this((RDFResource)null);  //create a panel with no resource
	}

	/**RDF data model constructor.
	@param rdf The RDF data model to display.
	*/
	public RDFPanel(final RDF rdf)
	{
		this(rdf, null);  //construct the panel without specifying a resource
	}

	/**Resource constructor with a default RDF data model.
	@param resource The resource to display in the panel, or <code>null</code> if
		all resources should be displayed.
	*/
	public RDFPanel(final RDFResource resource)
	{
		this(new RDF(), resource);	//construct and initialize the panel with a default RDF data model
	}

	/**RDF resource constructor.
	@param rdf The RDF data model in which the resource lies, or the data model
		from which to display resources if no specific resource is specified.
	@param resource The resource to display in the panel, or <code>null</code> if
		all resources should be displayed.
	*/
	public RDFPanel(final RDF rdf, final RDFResource resource)
	{
		this(rdf, resource, true);	//construct and initialize the panel
	}

	/**RDF resource constructor with optional initialization.
	@param rdf The RDF data model in which the resource lies, or the data model
		from which to display resources if no specific resource is specified.
	@param resource The resource to display in the panel, or <code>null</code> if
		all resources should be displayed.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public RDFPanel(final RDF rdf, final RDFResource resource, final boolean initialize)
	{
		super(false);	//construct the parent class without initializing the panel
		supportedDataViews=DEFAULT_SUPPORTED_DATA_VIEWS;	//set the data views we support
		defaultDataView=DEFAULT_DEFAULT_DATA_VIEW;	//set the default data view
		rdfTree=new RDFTree();	//create the RDF tree
		rdfScrollPane=new JScrollPane(rdfTree);
		sourceTextPane=new JTextPane();
		xmlScrollPane=new JScrollPane(sourceTextPane);
		domImplementation=new XMLDOMImplementation();	//create the XML DOM implementation
		modifyDocumentListener=createModifyDocumentListener();	//create a document listener to change our modification status when a document changes
		if(initialize)  //if we should initialize
			initialize();   //initialize the panel
		setRDF(rdf, resource);  //set the resource being displayed
	}

	/**Initialize the user interface.*/
	protected void initializeUI()
	{
		super.initializeUI(); //do the default UI initialization
//G***del; let the calling class do this		sourceTextPane.setEditable(false);	//don't let the text pane be edited
		editable=true;	//default to being editable 
		addView(TREE_MODEL_VIEW, "RDF", rdfScrollPane);	//add the RDF component as the tree view G***i18n
		addView(SOURCE_MODEL_VIEW, "RDF+XML", xmlScrollPane);	//add the XML component as the source view G***i18n
	}

	/**Returns an input stream for the given URI.
	The calling class has the responsibility for closing the input stream.
	@param uri A URI to a resource.
	@return An input stream to the contents of the resource represented by the given URI.
	@exception IOException Thrown if an I/O error occurred.
	*/
	public InputStream getInputStream(final URI uri) throws IOException
	{
		return uri.toURL().openConnection().getInputStream();	//TODO this is used a lot---put in some generic location
	}

	/**Displays the resources of an RDF data model in the panel.
	@param rdfModel The RDF data model from which to display resources.
	*/
/*G***del when works
	public void setRDF(final RDF rdfModel)
	{
		setRDF(rdfModel, null); //set the RDF without showing a particular resource
	}
*/

	/**Displays a single resource in the panel.
	@param rdfModel The RDF data model in which the resource lies, or the data model
		from which to display resources if no specific resource is specified.
	@param resource The resource to display in the panel, or <code>null</code> if
		all resources should be displayed.
	*/
/*G***del when works
	public void setRDF(final RDF rdfModel, final RDFResource resource)
	{
		rdfTree.setRDF(rdfModel, resource); //set the resource in the RDF tree
	}
*/

	/**@return The RDF data model, or <code>null</code> if there is no RDF.
	@exception IOException Thrown if there was an error getting the model.
	*/
	public RDF getRDF() throws IOException
	{
		saveModel(getModelView());	//store the data that is being edited, if any data is being edited
		return rdf;	//return the data that was just stored or was already stored
	}

	/**Sets the RDF data model and resource.
	@param rdf The RDF data model in which the resource lies, or the data model
		from which to display resources if no specific resource is specified.
	@param resource The resource to display in the panel, or <code>null</code> if
		all resources should be displayed.
	*/
	public void setRDF(final RDF rdf, final RDFResource resource)
	{
		this.rdf=rdf;	//store the XML
		rdfResource=resource;	//save the RDF resource
		try
		{
			loadModel(getModelView());	//try to load the model into our current data view; if we succeed
			setModified(false);	//show that the information has not been modified
		}
		catch(IOException ioException)	//if there were any problems saving the model
		{
			OptionPane.showMessageDialog(this, ioException.getMessage(), ioException.getClass().getName(), JOptionPane.ERROR_MESSAGE);	//G***i18n; TODO fix in a common routine
		}		
	}

	/**Loads the data from the model to the given view.
	@param modelView The view of the data that should be loaded.
	@exception IOException Thrown if there was an error loading the model.
	*/
	protected void loadModel(final int modelView) throws IOException
	{
		switch(modelView)	//see which view of data we should load
		{
			case TREE_MODEL_VIEW:	//if we're changing to the tree view
				if(rdf!=null)	//if we have RDF
				{
					getRDFTree().setRDF(rdf, rdfResource); //set the resource in the RDF tree
				}
				else	//if we don't have any RDF
				{
//	TODO fix loading no RDF					getXMLTextPane().setDocument(getXMLTextPane().getEditorKit().createDefaultDocument());	//create a default document
				}
				break;
			case SOURCE_MODEL_VIEW:	//if we're changing to the source view
				getSourceTextPane().getDocument().removeDocumentListener(getModifyDocumentListener());	//don't listen for changes to the source text pane
				if(rdf!=null)	//if we have RDF
				{
						//create an XML document containing the RDF information
					final Document document=rdfTree.getRDFXMLifier().createDocument(rdf, rdfResource, domImplementation);
					final XMLSerializer xmlSerializer=new XMLSerializer(true);  //create a formatted serializer
					final String source=xmlSerializer.serialize(document);	//serialize the XML to a string
					getSourceTextPane().setText(source);	//show the XML source in the source text pane
					getSourceTextPane().setCaretPosition(0);  //scroll to the top of the text
				}
				else	//if we don't have RDF
				{
					getSourceTextPane().setDocument(getSourceTextPane().getEditorKit().createDefaultDocument());	//create a default document
				}
				getSourceTextPane().getDocument().addDocumentListener(getModifyDocumentListener());	//add ourselves as a document listener to the source text pane
				break;
		}
	}

	/**Stores the current data being edited to the model.
	If no model is being edited or there is no valid view, no action occurs.
	@param modelView The view of the model that should be stored.
	@exception IOException Thrown if there was an error loading the model.
	*/
	protected void saveModel(final int modelView) throws IOException
	{
		switch(modelView)	//see which view of data we have, in order to get the current RDF
		{
			case TREE_MODEL_VIEW:	//if we should store the RDF currently in the tree
				rdf=getRDFTree().getRDF();	//store the RDF from the tree
				rdfResource=getRDFTree().getResource();	//store the RDF resource from the tree
				break;
			case SOURCE_MODEL_VIEW:	//if we should store the RDF source
				{
					final String sourceString=getSourceTextPane().getText();	//get the current source text
					if(sourceString.length()>0)	//if there is source text
					{
						final XMLProcessor xmlProcessor=new XMLProcessor(getURIInputStreamable());	//create an XML processor to read the source
						final byte[] sourceBytes=sourceString.getBytes(CharacterEncodingConstants.UTF_8);	//convert the string to a series of UTF-8 bytes
						final InputStream inputStream=new BufferedInputStream(new ByteArrayInputStream(sourceBytes));	//create an input stream to the source as bytes
						try
						{
							final Document document=xmlProcessor.parseDocument(inputStream, getBaseURI());	//parse the document into the XML data model
							final RDFXMLProcessor rdfXMLProcessor=new RDFXMLProcessor();	//create a new RDF processor
							rdf=rdfXMLProcessor.process(document);	//process the RDF from the XML
							rdfResource=null;	//G***fix
								//TODO find some way to find the original resource selected, if any
						}
						catch(URISyntaxException uriSyntaxException)	//TODO fix so that other types of exceptions are accepted; create a general ParseException or SyntaxException
						{
							throw (IOException)new IOException(uriSyntaxException.getMessage()).initCause(uriSyntaxException);
						}
						finally
						{
							inputStream.close();	//always close the input stream from the source bytes
						}
					}
					else	//if there is no source text
					{
						rdf=null;	//there can be no RDF
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
/*G***del if not needed
				case TREE_MODEL_VIEW:	//if we're changing from the tree view
					getXMLTextPane().getDocument().removeDocumentListener(getModifyDocumentListener());	//don't listen for changes to the XML text pane any more
					getXMLTextPane().setDocument(getXMLTextPane().getEditorKit().createDefaultDocument());	//remove the content from the XML text pane by installing a new document
					break;
*/
			case SOURCE_MODEL_VIEW:	//if we're changing from the source view
				getSourceTextPane().getDocument().removeDocumentListener(getModifyDocumentListener());	//don't listen for changes to the source text pane any more
				getSourceTextPane().setDocument(getSourceTextPane().getEditorKit().createDefaultDocument());	//remove the content from the source text pane by installing a new document
				break;
		}
		switch(newView)	//see which view we're changing to
		{
/*G***del if not needed
				case TREE_MODEL_VIEW:	//if we're changing to the tree view
					getXMLTextPane().getDocument().addDocumentListener(getModifyDocumentListener());	//add ourselves as a document listener to the XML text pane
					break;
*/
			case SOURCE_MODEL_VIEW:	//if we're changing to the source view
				getSourceTextPane().getDocument().addDocumentListener(getModifyDocumentListener());	//add ourselves as a document listener to see if the source pane is modified
				break;
		}
	}

}
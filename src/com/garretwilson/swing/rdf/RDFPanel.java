package com.garretwilson.swing.rdf;

import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

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
@author Garret Wilson
*/
public class RDFPanel extends TabbedViewPanel
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
	private final JTree rdfTree;

		/**@return The tree tab in which the RDF is displayed.*/
		public JTree getRDFTree() {return rdfTree;}

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
	public RDFResourceModel getRDFResourceModel() {return (RDFResourceModel)getModel();}

	/**Sets the data model.
	@param model The data model for which this component provides a view.
	@see ModelViewablePanel#setModel(Model)
	*/
	public void setRDFResourceModel(final RDFResourceModel model) {setModel(model);}

	/**Model constructor.
	@param model The data model for which this component provides a view.
	*/
	public RDFPanel(final RDFResourceModel model)
	{
		this(model, true);	//construct and initialize the panel
	}

	/**Model constructor with optional initialization.
	@param model The data model for which this component provides a view.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public RDFPanel(final RDFResourceModel model, final boolean initialize)
	{
		super(model, false);	//construct the parent class without initializing the panel
		supportedDataViews=DEFAULT_SUPPORTED_DATA_VIEWS;	//set the data views we support
		defaultDataView=DEFAULT_DEFAULT_DATA_VIEW;	//set the default data view
		rdfTree=new JTree();	//create the RDF tree
		rdfScrollPane=new JScrollPane(rdfTree);
		sourceTextPane=new JTextPane();
		xmlScrollPane=new JScrollPane(sourceTextPane);
		domImplementation=new XMLDOMImplementation();	//create the XML DOM implementation
		modifyDocumentListener=createModifyDocumentListener();	//create a document listener to change our modification status when a document changes
		if(initialize)  //if we should initialize
			initialize();   //initialize the panel
	}

	/**Initialize the user interface.*/
	protected void initializeUI()
	{
		super.initializeUI(); //do the default UI initialization
//G***del; let the calling class do this		sourceTextPane.setEditable(false);	//don't let the text pane be edited
		editable=true;	//default to being editable 
		rdfTree.setCellRenderer(new RDFResourceTreeCellRenderer()); //render the icons using our cell renderer for RDF
		addView(TREE_MODEL_VIEW, "RDF", rdfScrollPane);	//add the RDF component as the tree view G***i18n
		addView(SOURCE_MODEL_VIEW, "RDF+XML", xmlScrollPane);	//add the XML component as the source view G***i18n
	}

	/**Loads the data from the model to the given view.
	@param modelView The view of the data that should be loaded.
	@exception IOException Thrown if there was an error loading the model.
	*/
	protected void loadModel(final int modelView) throws IOException
	{
		final RDFResourceModel model=getRDFResourceModel();	//get the data model
		switch(modelView)	//see which view of data we should load
		{
			case TREE_MODEL_VIEW:	//if we're changing to the tree view
				if(model.getRDFResource()!=null)	//if we have an RDF resource
				{
						//G***maybe use a common RDFXMLifier
					final TreeNode treeNode=new RDFObjectTreeNode(model.getRDFResource(), new RDFXMLifier()); //create a tree node for the resource
					getRDFTree().setModel(new DefaultTreeModel(treeNode));  //create a new tree model and set the model for the tree
				}
				else	//if we don't have any RDF resource
				{
//TODO for no resource					getRDFTree().setModel(new DefaultTreeModel(treeNode));  //create a new tree model and set the model for the tree
				}
				break;
			case SOURCE_MODEL_VIEW:	//if we're changing to the source view
				getSourceTextPane().getDocument().removeDocumentListener(getModifyDocumentListener());	//don't listen for changes to the source text pane
				if(model.getRDFResource()!=null)	//if we have an RDF resource
				{
						//create an XML document containing the RDF information TODO see about using a commong RDFXMLifier
					final Document document=new RDFXMLifier().createDocument(model.getRDFResource(), domImplementation);
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
		final RDFResourceModel model=getRDFResourceModel();	//get the data model
		switch(modelView)	//see which view of data we have, in order to get the current RDF
		{
			case TREE_MODEL_VIEW:	//if we should store the RDF currently in the tree
				if(getRDFTree().getModel().getRoot() instanceof RDFObjectTreeNode)	//if this tree is showing an RDF object
				{
					final RDFObjectTreeNode rdfObjectNode=(RDFObjectTreeNode)getRDFTree().getModel().getRoot();	//get the RDF object root node
					if(rdfObjectNode.getRDFObject() instanceof RDFResource)	//if the tree node represents an RDF resource
					{
						model.setRDFResource((RDFResource)rdfObjectNode.getRDFObject());	//set the RDF resource in the model
					}
				}
				break;
			case SOURCE_MODEL_VIEW:	//if we should store the RDF source
				{
					final String sourceString=getSourceTextPane().getText();	//get the current source text
					if(sourceString.length()>0)	//if there is source text
					{
						final XMLProcessor xmlProcessor=new XMLProcessor(model.getURIInputStreamable());	//create an XML processor to read the source
						final byte[] sourceBytes=sourceString.getBytes(CharacterEncodingConstants.UTF_8);	//convert the string to a series of UTF-8 bytes
						final InputStream inputStream=new BufferedInputStream(new ByteArrayInputStream(sourceBytes));	//create an input stream to the source as bytes
						try
						{
							final Document document=xmlProcessor.parseDocument(inputStream, model.getBaseURI());	//parse the document into the XML data model
							final RDFXMLProcessor rdfXMLProcessor=new RDFXMLProcessor();	//create a new RDF processor
							final RDF rdf=rdfXMLProcessor.process(document);	//process the RDF from the XML
/*TODO find some way to find the original resource selected, if any
							rdfResource=null;	//G***fix
*/
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
						model.setRDFResource(null);	//there can be no RDF
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
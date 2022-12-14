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

package com.globalmentor.swing.rdf;

import static java.nio.charset.StandardCharsets.*;

import java.io.*;
import java.net.*;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import com.globalmentor.net.ResourceModel;
import com.globalmentor.rdf.*;
import com.globalmentor.swing.*;
import com.globalmentor.swing.rdf.tree.*;
import com.globalmentor.xml.URIInputStreamableXMLEntityResolver;
import com.globalmentor.xml.XML;
import com.globalmentor.xml.XMLSerializer;

import org.w3c.dom.*;

/**
 * Panel that displays RDF and allows it to be explored. A tab is presented to allow viewing the RDF+XML source code.
 * @author Garret Wilson
 */
public class RDFPanel<R extends RDFResource, M extends ResourceModel<R>> extends TabbedViewPanel<M> { //TODO see if we can get away with just using M extends ResouceModel<? extends Resource>

	/** The default model views supported by this panel. */
	private final int[] DEFAULT_SUPPORTED_MODEL_VIEWS = new int[] { TREE_MODEL_VIEW, SOURCE_MODEL_VIEW };

	/** The default default model view of this panel. */
	private final int DEFAULT_DEFAULT_MODEL_VIEW = TREE_MODEL_VIEW;

	/** The tree tab in which the RDF is displayed. */
	private final JTree rdfTree;

	/** @return The tree tab in which the RDF is displayed. */
	public JTree getRDFTree() {
		return rdfTree;
	}

	/** The scroll pane for the RDF tree. */
	protected final JScrollPane rdfScrollPane;

	/** The scroll pane for the XML. */
	protected final JScrollPane xmlScrollPane;

	/** The source text pane. */
	private final JTextPane sourceTextPane;

	/** @return The source text pane. */
	public JTextPane getSourceTextPane() {
		return sourceTextPane;
	}

	/** Whether the RDF can be edited. */
	private boolean editable;

	/** @return <code>true</code> if the RDF can be edited. */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Sets whether or not the RDF can be edited.
	 * @param editable <code>true</code> if the RDF can be edited, or <code>false</code> if the information should be read-only.
	 */
	public void setEditable(final boolean editable) {
		this.editable = editable; //save the new editable status
		sourceTextPane.setEditable(editable); //set whether the text pane is editable
	}

	/** The DOM implementation used for serializing the RDF. */
	protected final DOMImplementation domImplementation;

	/**
	 * Model constructor.
	 * @param model The data model for which this component provides a view.
	 */
	public RDFPanel(final M model) {
		this(model, true); //construct and initialize the panel
	}

	/**
	 * Model constructor with optional initialization.
	 * @param model The data model for which this component provides a view.
	 * @param initialize <code>true</code> if the panel should initialize itself by calling the initialization methods.
	 */
	public RDFPanel(final M model, final boolean initialize) {
		super(model, false); //construct the parent class without initializing the panel
		setSupportedModelViews(DEFAULT_SUPPORTED_MODEL_VIEWS); //set the model views we support
		setDefaultDataView(DEFAULT_DEFAULT_MODEL_VIEW); //set the default data view
		rdfTree = new JTree(); //create the RDF tree
		rdfScrollPane = new JScrollPane(rdfTree);
		sourceTextPane = new JTextPane();
		xmlScrollPane = new JScrollPane(sourceTextPane);
		domImplementation = XML.createDocumentBuilder(true).getDOMImplementation(); //create the XML DOM implementation
		if(initialize) //if we should initialize
			initialize(); //initialize the panel
	}

	/** Initialize the user interface. */
	protected void initializeUI() {
		//TODO del; let the calling class do this		sourceTextPane.setEditable(false);	//don't let the text pane be edited
		editable = true; //default to being editable 
		rdfTree.setCellRenderer(new RDFResourceTreeCellRenderer()); //render the icons using our cell renderer for RDF
		addView(TREE_MODEL_VIEW, "RDF", rdfScrollPane); //add the RDF component as the tree view TODO i18n
		addView(SOURCE_MODEL_VIEW, "RDF+XML", xmlScrollPane); //add the XML component as the source view TODO i18n
		super.initializeUI(); //do the default UI initialization
	}

	/**
	 * Loads the data from the model to the specified view, if necessary.
	 * @param modelView The view of the data, such as <code>SUMMARY_MODEL_VIEW</code>.
	 * @throws IOException Thrown if there was an error loading the model.
	 */
	protected void loadModel(final int modelView) throws IOException {
		super.loadModel(modelView); //do the default loading
		final M model = getModel(); //get the data model
		switch(modelView) { //see which view of data we should load
			case TREE_MODEL_VIEW: //if we're changing to the tree view
				if(model.getResource() != null) { //if we have an RDF resource
					//TODO maybe use a common RDFXMLifier
					final TreeNode treeNode = new RDFObjectTreeNode(model.getResource(), new RDFXMLGenerator()); //create a tree node for the resource
					getRDFTree().setModel(new DefaultTreeModel(treeNode)); //create a new tree model and set the model for the tree
				} else { //if we don't have any RDF resource
					//TODO for no resource					getRDFTree().setModel(new DefaultTreeModel(treeNode));  //create a new tree model and set the model for the tree
				}
				break;
			case SOURCE_MODEL_VIEW: //if we're changing to the source view
				getSourceTextPane().getDocument().removeDocumentListener(getModifyDocumentListener()); //don't listen for changes to the source text pane
				if(model.getResource() != null) { //if we have an RDF resource
					//create an XML document containing the RDF information TODO see about using a commong RDFXMLifier
					final Document document = new RDFXMLGenerator().createDocument(model.getResource(), domImplementation);
					final XMLSerializer xmlSerializer = new XMLSerializer(true); //create a formatted serializer
					final String source = xmlSerializer.serialize(document); //serialize the XML to a string
					getSourceTextPane().setText(source); //show the XML source in the source text pane
					getSourceTextPane().setCaretPosition(0); //scroll to the top of the text
				} else { //if we don't have RDF
					getSourceTextPane().setDocument(getSourceTextPane().getEditorKit().createDefaultDocument()); //create a default document
				}
				getSourceTextPane().getDocument().addDocumentListener(getModifyDocumentListener()); //add ourselves as a document listener to the source text pane
				break;
		}
	}

	/**
	 * Stores the current data being edited to the model, if necessary.
	 * @param modelView The view of the data, such as <code>SUMMARY_MODEL_VIEW</code>.
	 * @throws IOException Thrown if there was an error saving the model.
	 */
	protected void saveModel(final int modelView) throws IOException {
		super.saveModel(modelView); //do the default saving
		final M model = getModel(); //get the data model
		switch(modelView) { //see which view of data we should save
			case TREE_MODEL_VIEW: //if we should store the RDF currently in the tree
				if(getRDFTree().getModel().getRoot() instanceof RDFObjectTreeNode) { //if this tree is showing an RDF object
					final RDFObjectTreeNode rdfObjectNode = (RDFObjectTreeNode)getRDFTree().getModel().getRoot(); //get the RDF object root node
					if(rdfObjectNode.getRDFObject() instanceof RDFResource) { //if the tree node represents an RDF resource
						model.setResource((R)rdfObjectNode.getRDFObject()); //set the RDF resource in the model	TODO fix checking for correct resource type
					}
				}
				break;
			case SOURCE_MODEL_VIEW: //if we should store the RDF source
			{
				final String sourceString = getSourceTextPane().getText(); //get the current source text
				if(sourceString.length() > 0) { //if there is source text
					final byte[] sourceBytes = sourceString.getBytes(UTF_8); //convert the string to a series of UTF-8 bytes
					final InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(sourceBytes)); //create an input stream to the source as bytes
					try {
						final Document document = XML.parse(inputStream, model.getBaseURI(), true, new URIInputStreamableXMLEntityResolver(model)); //parse the XML document
						final RDFXMLProcessor rdfXMLProcessor = new RDFXMLProcessor(); //create a new RDF processor
						final RDFModel rdf = rdfXMLProcessor.processRDF(document); //process the RDF from the XML
						/*TODO find some way to find the original resource selected, if any
													rdfResource=null;	//TODO fix
						*/
					} catch(URISyntaxException uriSyntaxException) { //TODO fix so that other types of exceptions are accepted; create a general ParseException or SyntaxException
						throw (IOException)new IOException(uriSyntaxException.getMessage()).initCause(uriSyntaxException);
					} finally {
						inputStream.close(); //always close the input stream from the source bytes
					}
				} else { //if there is no source text
					model.setResource(null); //there can be no RDF
				}
			}
				break;
		}
	}

	/**
	 * Indicates that the view of the data has changed.
	 * @param oldView The view before the change.
	 * @param newView The new view of the data
	 */
	protected void onModelViewChange(final int oldView, final int newView) {
		super.onModelViewChange(oldView, newView); //perform the default functionality
		switch(oldView) { //see which view we're changing from
		/*TODO del if not needed
						case TREE_MODEL_VIEW:	//if we're changing from the tree view
							getXMLTextPane().getDocument().removeDocumentListener(getModifyDocumentListener());	//don't listen for changes to the XML text pane any more
							getXMLTextPane().setDocument(getXMLTextPane().getEditorKit().createDefaultDocument());	//remove the content from the XML text pane by installing a new document
							break;
		*/
			case SOURCE_MODEL_VIEW: //if we're changing from the source view
				getSourceTextPane().getDocument().removeDocumentListener(getModifyDocumentListener()); //don't listen for changes to the source text pane any more
				getSourceTextPane().setDocument(getSourceTextPane().getEditorKit().createDefaultDocument()); //remove the content from the source text pane by installing a new document
				break;
		}
		switch(newView) { //see which view we're changing to
		/*TODO del if not needed
						case TREE_MODEL_VIEW:	//if we're changing to the tree view
							getXMLTextPane().getDocument().addDocumentListener(getModifyDocumentListener());	//add ourselves as a document listener to the XML text pane
							break;
		*/
			case SOURCE_MODEL_VIEW: //if we're changing to the source view
				getSourceTextPane().getDocument().addDocumentListener(getModifyDocumentListener()); //add ourselves as a document listener to see if the source pane is modified
				break;
		}
	}

}
package com.garretwilson.swing.rdf;

import java.io.IOException;
import javax.swing.*;
import com.garretwilson.swing.*;
import com.garretwilson.swing.rdf.tree.*;
import com.garretwilson.text.xml.XMLDOMImplementation;
import com.garretwilson.text.xml.XMLSerializer;
import com.garretwilson.rdf.*;
import org.w3c.dom.*;

/**Panel that displays RDF and allows it to be explored. A tab is
	presented to allow viewing the RDF+XML source code.
@author Garret Wilson
*/
public class RDFPanel extends TabbedViewPanel
{

	/**The tree tab in which the RDF is displayed.*/
	private final RDFTree rdfTree;

		/**@return The tree tab in which the RDF is displayed.*/
		public RDFTree getRDFTree() {return rdfTree;}

	/**The scroll pane for the RDF treee.*/
	protected final JScrollPane rdfScrollPane;

	/**The scroll pane for the XML.*/
	protected final JScrollPane xmlScrollPane;

	/**The text pane for the XML.*/
	protected final JTextPane xmlTextPane;

	/**The DOM implementation used for serializing the RDF.*/
	protected final DOMImplementation domImplementation;

	/**@return The RDF data model being displayed.*/
	public RDF getRDF() {return rdfTree.getRDF();}

	/**The main resource being displayed, or <code>null</code> if the entire
		data model is being displayed.
	*/
	private RDFResource rdfResource;

		/**@return The main resource being displayed, or <code>null</code> if the
			entire data model is being displayed.
		*/
		public RDFResource getResource() {return rdfResource;}

	/**Default constructor.*/
	public RDFPanel()
	{
		this(new RDF());  //create a panel with defualt RDF
	}

	/**RDF data model constructor.
	@param rdf The RDF data model to display.
	*/
	public RDFPanel(final RDF rdf)
	{
		this(rdf, null);  //construct the panel without specifying a resource
	}

	/**RDF resource constructor.
	@param rdf The RDF data model in which the resource lies, or the data model
		from which to display resources if no specific resource is specified.
	@param resource The resource to display in the panel, or <code>null</code> if
		all resources should be displayed.
	*/
	public RDFPanel(final RDF rdf, final RDFResource resource)
	{
		super(false);	//construct the parent class without initializing the panel
		rdfTree=new RDFTree();	//create the RDF tree
		rdfScrollPane=new JScrollPane(rdfTree);
		xmlTextPane=new JTextPane();
		xmlScrollPane=new JScrollPane(xmlTextPane);
		domImplementation=new XMLDOMImplementation();	//create the XML DOM implementation
		initialize();	//initialize the panel
		setRDF(rdf, resource);  //set the resource being displayed
	}

	/**Initialize the user interface.*/
	protected void initializeUI()
	{
		super.initializeUI(); //do the default UI initialization
		xmlTextPane.setEditable(false);	//don't let the text pane be edited
		getTabbedPane().add(rdfScrollPane, "RDF"); //G**i18n
		setViewComponent(NORMAL_VIEW, rdfScrollPane);	//associate the RDF component with the normal view
		getTabbedPane().add(xmlScrollPane, "RDF+XML");  //G***i18n
		setViewComponent(SOURCE_VIEW, xmlScrollPane);	//associate the XML component with the sourceview
	}

	/**Displays the resources of an RDF data model in the panel.
	@param rdfModel The RDF data model from which to display resources.
	*/
	public void setRDF(final RDF rdfModel)
	{
		setRDF(rdfModel, null); //set the RDF without showing a particular resource
	}

	/**Displays a single resource in the panel.
	@param rdfModel The RDF data model in which the resource lies, or the data model
		from which to display resources if no specific resource is specified.
	@param resource The resource to display in the panel, or <code>null</code> if
		all resources should be displayed.
	*/
	public void setRDF(final RDF rdfModel, final RDFResource resource)
	{
		rdfTree.setRDF(rdfModel, resource); //set the resource in the RDF tree
	}

	/**Indicates that the view of the data has changed.
	@param oldView The view before the change.
	@param newView The new view of the data
	*/
	protected void onViewChanged(final int oldView, final int newView)
	{		
		switch(oldView)	//see what view we're changing from
		{
			case NORMAL_VIEW:	//if we're changing from the normal view
				{
						//create an XML document containing the RDF information
					final Document document=rdfTree.getRDFXMLifier().createDocument(getRDF(), getResource(), domImplementation);
					final XMLSerializer xmlSerializer=new XMLSerializer(true);  //create a formatted serializer
					try
					{
						final String xmlString=xmlSerializer.serialize(document); //serialize the document to a string
						xmlTextPane.setText(xmlString); //set the text in the text area
					}
					catch(IOException ioException)  //if there is an error serializing the XML information
					{
						xmlTextPane.setText(ioException.getMessage());  //show the error message in the text area
					}
					xmlTextPane.setCaretPosition(0);  //scroll to the top of the text				
				}
				break;
		}
		switch(newView)	//see what view we're changing to
		{
			case NORMAL_VIEW:	//if we're changing tothe normal view
				xmlTextPane.setDocument(xmlTextPane.getEditorKit().createDefaultDocument());	//to conserve memory, remove the content from the editor kit by installing a new document
				break;
		}
	}

}
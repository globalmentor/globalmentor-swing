package com.garretwilson.swing.rdf;

import java.io.IOException;
import javax.swing.*;
import javax.swing.event.*;
import com.garretwilson.swing.rdf.tree.*;
import com.garretwilson.text.xml.XMLDOMImplementation;
import com.garretwilson.text.xml.XMLSerializer;
import com.garretwilson.rdf.*;
import org.w3c.dom.*;

/**Tabbed pane that displays RDF and allows it to be explored. A tab is
	presented to allow viewing the RDF+XML source code.
@author Garret Wilson
*/
public class RDFTabbedPane extends JTabbedPane implements ChangeListener
{

  /**The tree tab in which the RDF is displayed.*/
	private final RDFTree rdfTree=new RDFTree();

		/**@return The tree tab in which the RDF is displayed.*/
		public RDFTree getRDFTree() {return rdfTree;}

  JScrollPane rdfScrollPane = new JScrollPane();
  JScrollPane xmlScrollPane = new JScrollPane();
  JTextArea xmlTextArea = new JTextArea();

	/**The DOM implementation used for serializing the RDF.*/
	protected final DOMImplementation domImplementation=new XMLDOMImplementation();

	/**The RDF data model being displayed.*/
//G***del	private RDF rdf;

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
	public RDFTabbedPane()
	{
		this(new RDF());  //create a panel with defualt RDF
	}

	/**RDF data model constructor.
	@param rdf The RDF data model to display.
	*/
	public RDFTabbedPane(final RDF rdf)
	{
		this(rdf, null);  //construct the panel without specifying a resource
	}

	/**RDF resource constructor.
	@param rdf The RDF data model in which the resource lies, or the data model
		from which to display resources if no specific resource is specified.
	@param resource The resource to display in the panel, or <code>null</code> if
		all resources should be displayed.
	*/
	public RDFTabbedPane(final RDF rdf, final RDFResource resource)
	{
		setRDF(rdf, resource);  //set the resource being displayed
		jbInit(); //initialize the UI
	}

	/**Initializes the user interface.*/
	private void jbInit()
  {
    xmlTextArea.setEditable(false);
    addChangeListener(this);
    rdfScrollPane.getViewport().add(rdfTree, null);
    xmlScrollPane.getViewport().add(xmlTextArea, null);
    add(rdfScrollPane,  "RDF"); //G**i18n
    add(xmlScrollPane,  "RDF+XML");  //G***i18n
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

	/**Called when the selected tab changes.
	@param changeEvent The event indicating the change.
	*/
	public void stateChanged(final ChangeEvent changeEvent)
	{
		if(getSelectedComponent()==xmlScrollPane) //if the user has switched to the XML source panel
		{
				//create an XML document containing the RDF information
			final Document document=rdfTree.getRDFXMLifier().createDocument(getRDF(), getResource(), domImplementation);
		  final XMLSerializer xmlSerializer=new XMLSerializer(true);  //create a formatted serializer
			try
			{
				final String xmlString=xmlSerializer.serialize(document); //serialize the document to a string
				xmlTextArea.setText(xmlString); //set the text in the text area
			}
			catch(IOException ioException)  //if there is an error serializing the XML information
			{
				xmlTextArea.setText(ioException.getMessage());  //show the error message in the text area
			}
			xmlTextArea.setCaretPosition(0);  //scroll to the top of the text
		}
		else  //if the user has switched to another panel
		{
			xmlTextArea.setText("");  //clear the text so that it won't take up memory
		}
	}

}
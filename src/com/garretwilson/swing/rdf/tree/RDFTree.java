package com.garretwilson.swing.rdf.tree;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.swing.rdf.tree.*;
import com.garretwilson.text.xml.XMLDOMImplementation;
import com.garretwilson.text.xml.XMLSerializer;
import com.garretwilson.rdf.*;
import org.w3c.dom.*;

/**Class that displays RDF in a tree and allows it to be explored.
@author Garret Wilson
*/
public class RDFTree extends JTree
{
	private RDFXMLifier rdfXMLifier=new RDFXMLifier();  //create an object for serializing RDF G***maybe allow this to be specified externally

	  /**@return The RDF XML-ifier currently being used to generate labels; this
		  object may be replaced at any time.
		*/
		public RDFXMLifier getRDFXMLifier() {return rdfXMLifier;}

	RDFResourceTreeCellRenderer rdfResourceTreeCellRenderer=new RDFResourceTreeCellRenderer();

	/**The DOM implementation used for serializing the RDF.*/
	protected final DOMImplementation domImplementation=new XMLDOMImplementation();

	/**The RDF data model being displayed.*/
	private RDF rdf;

		/**@return The RDF data model being displayed.*/
		public RDF getRDF() {return rdf;}

	/**The main resource being displayed, or <code>null</code> if the entire
		data model is being displayed.
	*/
	private RDFResource rdfResource;

		/**@return The main resource being displayed, or <code>null</code> if the
		  entire data model is being displayed.
		*/
		public RDFResource getResource() {return rdfResource;}

	/**Default constructor.*/
	public RDFTree()
	{
		this(new RDF());  //create a panel with defualt RDF
	}

	/**RDF data model constructor.
	@param rdf The RDF data model to display.
	*/
	public RDFTree(final RDF rdf)
	{
		this(rdf, null);  //construct the panel without specifying a resource
	}

	/**RDF resource constructor.
	@param rdf The RDF data model in which the resource lies, or the data model
		from which to display resources if no specific resource is specified.
	@param resource The resource to display in the panel, or <code>null</code> if
		all resources should be displayed.
	*/
	public RDFTree(final RDF rdf, final RDFResource resource)
	{
		setRDF(rdf, resource);  //set the resource being displayed
		jbInit(); //initialize the UI
	}

	/**Initializes the user interface.*/
	private void jbInit()
  {
//G***del		  //setup the icon for RDF literals
//G***del; this is now done by default		rdfResourceTreeCellRenderer.registerRDFLiteralIcon(IconResources.getIcon(IconResources.STRING_ICON_FILENAME));
//G***del and fix whole class		setCellRenderer(rdfResourceTreeCellRenderer); //render the icons using our cell renderer for RDF
		setCellRenderer(rdfResourceTreeCellRenderer); //render the icons using our cell renderer for RDF
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
		rdf=rdfModel; //save the RDF
		rdfResource=resource; //save the resource
		rdfXMLifier=new RDFXMLifier();  //switch to a new RDF XMLifier, so that new namespace prefixes can be created
		final TreeNode treeNode; //we'll create a resource tree node for either a specific resource for the entire RDF data model
		if(resource!=null)  //if we were given a resource
		{
			treeNode=new RDFObjectTreeNode(resource, rdfXMLifier); //create a tree node for the resources
		}
		else  //if no specific resource was given
		{
			treeNode=new RDFTreeNode(rdf, rdfXMLifier); //create a tree node for the entire RDF data model
		}
		setModel(new DefaultTreeModel(treeNode));  //create a new model and set the model for the tree
	}
}
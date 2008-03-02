package com.garretwilson.swing.rdf.tree;

import java.net.URI;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import com.garretwilson.rdf.*;
import com.globalmentor.text.xml.XMLDOMImplementation;

import org.w3c.dom.*;

/**Class that displays RDF in a tree and allows it to be explored.
@author Garret Wilson
*/
public class RDFTree extends JTree	//TODO eventually delete this class and just allow the model to be set
{
	private RDFXMLGenerator rdfXMLifier=new RDFXMLGenerator();  //create an object for serializing RDF G***maybe allow this to be specified externally

	  /**@return The RDF XML-ifier currently being used to generate labels; this
		  object may be replaced at any time.
		*/
		public RDFXMLGenerator getRDFXMLifier() {return rdfXMLifier;}

	RDFResourceTreeCellRenderer rdfResourceTreeCellRenderer=new RDFResourceTreeCellRenderer();

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

	/**The object that determines how the resources will be sorted,
		or <code>null</code> if the resources should not be sorted.
	*/
//TODO fix	private Comparator comparator=null;

		/**@return The object that determines how the resources will be sorted,
			or <code>null</code> if the resources should not be sorted.
		*/
//TODO fix		public Comparator getComparator() {return comparator;}

		/**Sets the method of sorting the resources.
		@param newComparator The object that determines how the resources will be
			sorted, or <code>null</code> if the resources should not be sorted.
		*/
//TODO fix		public void setComparator(final Comparator newComparator) {comparator=newComparator;}

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
		initializeUI(); //initialize the UI
	}

	/**Initializes the user interface.*/
	private void initializeUI()
  {
//G***del		  //setup the icon for RDF literals
//G***del; this is now done by default		rdfResourceTreeCellRenderer.registerRDFLiteralIcon(IconResources.getIcon(IconResources.STRING_ICON_FILENAME));
//G***del and fix whole class		setCellRenderer(rdfResourceTreeCellRenderer); //render the icons using our cell renderer for RDF
		setCellRenderer(rdfResourceTreeCellRenderer); //render the icons using our cell renderer for RDF
  }

	/**Displays the resources of an RDF data model in the panel.
	<p>The root node is hidden.</p>
	@param rdfModel The RDF data model from which to display resources.
	*/
	public void setRDF(final RDF rdfModel)
	{
		setRDF(rdfModel, null); //set the RDF without showing a particular resource
	}

	/**Displays a single resource in the panel, or if no resource is given, all
		the resources of the data model.
	<p>If all resources are shown, the root node is hidden.</p>	
	@param rdfModel The RDF data model in which the resource lies, or the data model
		from which to display resources if no specific resource is specified.
	@param resource The resource to display in the panel, or <code>null</code> if
		all resources should be displayed.
	*/
	public void setRDF(final RDF rdfModel, final RDFResource resource)
	{
		rdf=rdfModel; //save the RDF
		rdfResource=resource; //save the resource
		rdfXMLifier=new RDFXMLGenerator();  //switch to a new RDF XMLifier, so that new namespace prefixes can be created
		setRootVisible(resource!=null);	//only show the root if we're showing a single resource
		final TreeNode treeNode; //we'll create a resource tree node for either a specific resource for the entire RDF data model
		if(resource!=null)  //if we were given a resource
		{
			treeNode=new RDFObjectTreeNode(resource, rdfXMLifier); //create a tree node for the resources
		}
		else  //if no specific resource was given
		{
			treeNode=new RDFTreeNode(rdf, rdfXMLifier); //create a tree node for the entire RDF data model, using the comparator we have, if any
//TODO fix			treeNode=new RDFTreeNode(rdf, rdfXMLifier, getComparator()); //create a tree node for the entire RDF data model, using the comparator we have, if any
		}
		setModel(new DefaultTreeModel(treeNode));  //create a new model and set the model for the tree
	}
}
package com.garretwilson.swing.rdf.tree;

import java.util.*;
import javax.swing.tree.*;
import com.garretwilson.rdf.*;
import com.garretwilson.rdf.xpackage.FilePropertiesConstants;
import com.garretwilson.rdf.xpackage.XPackageUtilities;
import com.garretwilson.util.*;

/**Utilities for manipulating trees of RDF resources.
@author Garret Wilson
@see FilePropertiesConstants
*/
public class RDFTreeUtilities
{

	/**Creates a tree node that recognizes resources representing a folder
		structure.
	@param rdf The RDF data model.
	@param topLevelResourceList The list of resources, each a <code>Resource</code>,
		that each indicate the URI of a parent resource to appear under the root.
	@return A single tree node under which resoure nodes have been created to
		represent a file structure.
	@see Resource#getReferenceURI
	@see FilePropertiesConstants
	*/
	public static MutableTreeNode createFileResourceTreeNode(final RDF rdf, final List topLevelResourceList)
	{
		final DefaultMutableTreeNode rootNode=new DefaultMutableTreeNode("Resources");  //create the root node G***i18n; fix
			//create the top-level resources
		final Iterator topLevelResourceIterator=topLevelResourceList.iterator();  //get an iterator to all the top-level resources
		while(topLevelResourceIterator.hasNext()) //while there are more top-level resources
		{
			final Resource topLevelResource=(Resource)topLevelResourceIterator.next();  //get the next top-level resource
			final RDFResource topLevelRDFResource=rdf.getResource(topLevelResource.getReferenceURI());  //get the RDF resource representing the top-level resource
			if(topLevelRDFResource!=null)  //if such an RDF resource exists G***fix error handling
			{
				final RDFResourceTreeNode topLevelTreeNode=createFileResourceTreeNode(topLevelRDFResource);  //create a tree node from the top-level resource
				rootNode.add(topLevelTreeNode); //add the tree node to the root
			}
		}
		return rootNode;  //return the root node we constructed
	}

	/**Creates a tree node to represent an RDF resource.
		If the resource is a folder resource that has child resources in an XPackage
		manifest, child resources are recursively created for those child resource.
	@param rdfResource The resource for which a tree node should be created.
	@return A tree node for the given resource.
	*/
	public static RDFResourceTreeNode createFileResourceTreeNode(final RDFResource rdfResource)
	{
		final RDFResourceTreeNode rdfResourceTreeNode=new RDFResourceTreeNode(rdfResource); //create a new tree node to represent the RDF resource
		  //if this is a folder
		if(RDFUtilities.isType(rdfResource, FilePropertiesConstants.FILE_PROPERTIES_NAMESPACE_URI, FilePropertiesConstants.FOLDER_RESOURCE_TYPE_NAME))
		{
			final RDFBagResource manifest=XPackageUtilities.getManifest(rdfResource); //get the resource manifest
			if(manifest!=null)  //if the resource has a manifest
			{
					//get the resources in this folder
				final Collection itemResourceCollection=manifest.getItemCollection(); //get the collection of manifest items
				final Iterator itemResourceIterator=itemResourceCollection.iterator();  //get an iterator to all the contained resources
				while(itemResourceIterator.hasNext()) //while there are more resources
				{
					final RDFResource itemResource=(RDFResource)itemResourceIterator.next();  //get the next resource
Debug.trace("found item resource: ", itemResource);  //G***del
						//create a new tree node to represent the resource, recursively discovering folders
					final RDFResourceTreeNode itemResourceNode=createFileResourceTreeNode(itemResource);
					rdfResourceTreeNode.add(itemResourceNode); //add the resource node to the folder node
				}
			}
		}
		return rdfResourceTreeNode; //return the tree node for the resource
	}

}
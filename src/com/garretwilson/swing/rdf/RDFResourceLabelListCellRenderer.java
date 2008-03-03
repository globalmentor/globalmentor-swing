package com.garretwilson.swing.rdf;

import java.net.URI;

import com.garretwilson.swing.SimpleListCellRenderer;
import com.globalmentor.rdf.RDFLiteral;
import com.globalmentor.rdf.RDFResource;
import com.globalmentor.rdf.RDFUtilities;
import com.globalmentor.rdf.dublincore.DCUtilities;
import com.globalmentor.rdf.rdfs.RDFSUtilities;
import com.globalmentor.rdf.xpackage.XPackageUtilities;

/**A list cell renderer which shows the resource label.
The resource label is determined in the following order:
<ol>
	<li>The <code>rdfs:label</code> property, if available.</li>
	<li>The <code>dc:title</code> property, if available.</li>
	<li>The resource reference URI, if available.</li>
	<li>The <code>xpackage:location</code> <code>xlink:href</code>, if available.</li>
	<li>The string value of the resource.</li>
</ol>
@author Garret Wilson
*/
public class RDFResourceLabelListCellRenderer extends SimpleListCellRenderer
{
	/**Retrieves a label for a resource.
	@param value The value of this list item.
	@return The correct text for this list item.
	@exception ClassCastException if the value is not an RDF resource.
	@see RDFResource
	*/
  protected String getListCellRendererString(final Object value)
	{
  	final RDFResource resource=(RDFResource)value;	//cast the value to an RDF resource
  	String label=null;	//we'll try to find a label
  	final RDFLiteral rdfsLabel=RDFSUtilities.getLabel(resource);	//try to get an rdfs:label
  	if(rdfsLabel!=null)	//if there is an rdfs:label
  	{
  		label=rdfsLabel.getLexicalForm();	//get the lexical form of the label
  	}
  	else	//if there is no rdfs:label
  	{
  		final RDFLiteral dcTitle=RDFUtilities.asLiteral(DCUtilities.getTitle(resource));	//try to get a dc:title
	  	if(dcTitle!=null)	//if there is a dc:title
	  	{
	  		label=dcTitle.getLexicalForm();	//get the lexical form of the title
	  	}
	  	else	//if there is no dc:title
	  	{
	  		final URI referenceURI=resource.getURI();	//try to get the reference URI
		  	if(referenceURI!=null)	//if there is a reference URI
		  	{
		  		label=referenceURI.toString();	//use the string form of the reference URI
		  	}
	  		else	//if there is no reference URI
		  	{
		  		label=XPackageUtilities.getLocationHRef(resource);	//try to get an xpackage:location xlink:href
			  	if(label==null) //if there is no XPackage location href
			  	{
			  		label=resource.toString();	//settle for the string version of the resource
			  	}
		  	}
	  	}
  	}
  	return label;	//return whatever label we found
  }
}

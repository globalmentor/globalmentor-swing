package com.garretwilson.swing.text;

import java.net.URI;

import javax.swing.text.Document;
import com.garretwilson.rdf.RDF;

/**A collection of utility methods for working with Swing text
	<code>Document</code> and derived objects.
@author Garret Wilson
*/
public class DocumentUtilities implements DocumentConstants
{

	/**Sets the location against which to resolve relative URIs. By default this
		will be the document's URI.
	@param document The document the property of which to set.
	@param baseURI The new location against which to resolve relative URIs.
	@see #BASE_URI_PROPERTY
	*/
	public static void setBaseURI(final Document document, final URI baseURI)
	{
		document.putProperty(BASE_URI_PROPERTY, baseURI);	//store the base URI
	}
		
	/**Gets the location against which to resolve relative URIs.
	@param document The document from which to retrieve the property.
	@return The location against which to resolve relative URIs, or <code>null</code>
		if there is no base URI.
	@see #BASE_URI_PROPERTY
	*/
	public static URI getBaseURI(final Document document)
	{
		return (URI)document.getProperty(BASE_URI_PROPERTY);	//return the value of the base URI property
	}
	/**Retrieves whether the document should be paged.
	@param document The document from which to retrieve the property.
	@return <code>true</code> if the document should be paged, else
		<code>false</code> if the document should not be paged or if no paging is
		indicated.
	*/
	public static boolean isPaged(final Document document)
	{
		final Object object=document.getProperty(PAGED_PROPERTY); //get the property from the document
		return object instanceof Boolean ? ((Boolean)object).booleanValue() : false;  //return the value if we have if
	}

	/**Sets whether the document should be paged.
	@param document The document the property of which to set.
	@param paged <code>true</code> if the document should be paged.
	*/
	public static void setPaged(final Document document, final boolean paged)
	{
		document.putProperty(PAGED_PROPERTY, new Boolean(paged)); //store the value in the document
	}

	/**Retrieves the stored RDF data model
	@param document The document from which to retrieve the property.
	@return The RDF data model where metadata is stored, or <code>null</code>
		if there is no RDF metadata stored in the document document.
	*/
	public static RDF getRDF(final Document document)
	{
		final Object rdf=document.getProperty(RDF_PROPERTY); //get the RDF metadata property from the document
		return rdf instanceof RDF ? (RDF)rdf : null;  //return the RDF, if that's really what it is; otherwise, return null
	}

	/**Sets the RDF data model where metadata is stored.
	@param document The document the property of which to set.
	@param rdf The RDF data model.
	*/
	public static void setRDF(final Document document, final RDF rdf)
	{
		document.putProperty(RDF_PROPERTY, rdf); //store the RDF in the document
	}

	/**Retrieves the zoom factor.
	@param document The document from which to retrieve the property.
	@param defaultZoom The default zoom level to use if the property isn't
		specified.
	@return The stored zoom factor or the default value.
	*/
	public static float getZoom(final Document document, final float defaultZoom)
	{
		final Object object=document.getProperty(ZOOM_PROPERTY); //get the property from the document
		return object instanceof Float ? ((Float)object).floatValue() : defaultZoom;  //return the value if we have if
	}

	/**Sets the factor by which text should be zoomed.
	@param document The document the property of which to set.
	@param zoom The amount by which normal text should be multiplied.
	*/
	public static void setZoom(final Document document, final float zoom)
	{
		document.putProperty(ZOOM_PROPERTY, new Float(zoom)); //store the new zoom factor in the document
	}

	/**Determines whether a zoom level is specified for the document.
	@param document The document to check for the property.
	@return <code>true</code> if the property is specified for the document.
	*/
	public static boolean hasZoom(final Document document)
	{
		return document.getProperty(ZOOM_PROPERTY) instanceof Float;
	}

}
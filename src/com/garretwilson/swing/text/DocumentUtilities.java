package com.garretwilson.swing.text;

import javax.swing.text.Document;
import com.garretwilson.rdf.RDF;

/**A collection of utility methods for working with Swing text
	<code>Document</code> and derived objects.
@author Garret Wilson
*/
public class DocumentUtilities implements DocumentConstants
{

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
	@param defaultZoomFactor The default zoom factor to use if the property isn't
		specified.
	@return The stored zoom factor or the default value.
	*/
	public static float getZoomFactor(final Document document, final float defaultZoomFactor)
	{
		final Object object=document.getProperty(ZOOM_FACTOR_PROPERTY); //get the property from the document
		return object instanceof Float ? ((Float)object).floatValue() : defaultZoomFactor;  //return the value if we have if
/*G***del when works
		if(object instanceof Float) //if the property is of the correct type
			return ((Float)object).floatValue();  //get the value
		else  //if we don't get a valid zoom factor back
			return defaultZoomFactor; //return the default value
*/
	}

	/**Sets the factor by which text should be zoomed.
	@param document The document the property of which to set.
	@param zoomFactor The amount by which normal text should be multiplied.
	*/
	public static void setZoomFactor(final Document document, final float zoomFactor)
	{
		document.putProperty(ZOOM_FACTOR_PROPERTY, new Float(zoomFactor)); //store the new zoom factor in the document
	}

	/**Determines whether a zoom factor is specified for the document.
	@param document The document to check for the property.
	@return <code>true</code> if the property is specified for the document.
	*/
	public static boolean hasZoomFactor(final Document document)
	{
		return document.getProperty(ZOOM_FACTOR_PROPERTY) instanceof Float;
	}

}
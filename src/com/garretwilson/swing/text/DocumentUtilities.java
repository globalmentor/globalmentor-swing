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

package com.garretwilson.swing.text;

import java.net.URI;

import javax.swing.text.Document;

import com.globalmentor.java.Booleans;
import com.globalmentor.rdf.RDF;

/**A collection of utility methods for working with Swing text {@link Document} and derived objects.
@author Garret Wilson
*/
public class DocumentUtilities
{

	/**The <code>Boolean</code> property representing antialiased text.*/
//TODO del if not needed	public final static String ANTIALIAS_DOCUMENT_PROPERTY="antialias";

	/**The name of the property that indicates the base URI against which relative URIs should be referenced.*/
	public final static String BASE_URI_PROPERTY="baseURI";

	/**The name of the document property which will contain whether the document
		should be paged, stored as a <code>Boolean</code>.
	*/
	public final static String PAGED_PROPERTY="paged";

	/**The name of the document property which may contain the RDF data model.*/
	public final static String RDF_PROPERTY="rdf";

	/**Whether or not text in this view or any child views should be antialiased, stored as a <code>Boolean</code>.*/
	public final static String ANTIALIAS_PROPERTY="antialias";

	/**The name of the document property which will contain the zoom level stored
		as a <code>Float</code>.
	*/
	public final static String ZOOM_PROPERTY="zoomFactor";

	/**The default zoom level of the text pane.*/
	public final static float DEFAULT_ZOOM=1.20f;

	/**Sets the location against which to resolve relative URIs. By default this
		will be the document's URI.
	@param document The document the property of which to set.
	@param baseURI The new location against which to resolve relative URIs.
	@see DocumentUtilities#BASE_URI_PROPERTY
	*/
	public static void setBaseURI(final Document document, final URI baseURI)
	{
		document.putProperty(BASE_URI_PROPERTY, baseURI);	//store the base URI
	}
		
	/**Gets the location against which to resolve relative URIs.
	@param document The document from which to retrieve the property.
	@return The location against which to resolve relative URIs, or <code>null</code>
		if there is no base URI.
	@see DocumentUtilities#BASE_URI_PROPERTY
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

	/**Retrieves the antialias setting of the document.
	@param document The document from which to retrieve the property.
	@return <code>true</code> if the antialiased attribute is set to <code>true</code>, else <code>false</code>.
	*/
	public static boolean isAntialias(final Document document)
	{
		return Booleans.booleanValue(document.getProperty(ANTIALIAS_PROPERTY)); //get the property from the document as a boolean value
	}

	/**Sets the antialias property.
	@param document The document the property of which to set.
	@param antialias <code>true</code> if text should be antialiased, else <code>false</code>.
	*/
	public static void setAntialias(final Document document, final boolean antialias)
	{
		document.putProperty(ANTIALIAS_PROPERTY, Boolean.valueOf(antialias)); //store the antialias property in the document
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
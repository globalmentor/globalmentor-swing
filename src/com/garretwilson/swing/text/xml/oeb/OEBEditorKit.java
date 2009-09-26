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

package com.garretwilson.swing.text.xml.oeb;

import java.io.*;
import javax.swing.text.*;
import javax.xml.parsers.DocumentBuilder;

import com.garretwilson.swing.text.xml.*;
import com.garretwilson.swing.text.xml.xeb.XEBEditorKit;
import com.globalmentor.io.*;
import com.globalmentor.rdf.*;
import com.globalmentor.text.xml.URIInputStreamableXMLEntityResolver;
import com.globalmentor.text.xml.XML;
import com.globalmentor.text.xml.oeb.*;

/**An editor kit for an OEB publication.
@see XMLEditorKit
@author Garret Wilson
*/
public class OEBEditorKit extends XEBEditorKit
{

	/**Constructor.
	@param uriInputStreamable The source of input streams for resources.
	@exception NullPointerException if the new source of input streams is <code>null</code>.
	*/
	public OEBEditorKit(final URIInputStreamable uriInputStreamable)
	{
		super(uriInputStreamable);	//construct the parent class
	}

	/**Creates a copy of the editor kit.
	@return A copy of the XML editor kit.
	*/
	public Object clone() {return new OEBEditorKit(getURIInputStreamable());}

	/**Returns the MIME type of the data the XML editor kit supports, <code>application/x-oeb1-package+xml</code>.
	@return The MIME type this editor kit supports.
	*/
	public String getContentType() {return OEB.OEB10_PACKAGE_MEDIA_TYPE.toString();}

	/**Inserts content from the given stream which is expected to be in a format
		appropriate for this kind of content handler.
	@param inputStream The stream to read from.
	@param document The destination for the insertion.
	@param pos The location in the document to place the content >= 0.
	@exception IOException Thrown on any I/O error
	@exception BadLocationException Thrown if pos represents an invalid location
		within the document.
	*/
	public void read(final InputStream inputStream, final Document document, final int pos) throws IOException, BadLocationException
	{
		if(document instanceof XMLDocument) //if this is a Swing XML document
		{
			final XMLDocument swingXMLDocument=(XMLDocument)document; //cast the document to a Swing XML document
			final DocumentBuilder documentBuilder=XML.createDocumentBuilder(true, new URIInputStreamableXMLEntityResolver(swingXMLDocument.getURIInputStreamable()));  //create a new processor for loading the package information				
			final OEBPackageProcessor oebPackageProcessor=new OEBPackageProcessor(documentBuilder);
			final RDF packageRDF=oebPackageProcessor.read(inputStream, swingXMLDocument.getBaseURI()); //read the package from the input stream TODO maybe rename this to "process()"
			read(packageRDF, swingXMLDocument, pos);	//read the publication from the RDF data model
		}
		else  //if this is not an XML document we're reading into
		{
			super.read(inputStream, document, pos); //let the parent class do the reading
		}
	}

}

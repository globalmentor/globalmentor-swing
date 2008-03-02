package com.garretwilson.swing.text.xml.oeb;

import java.io.*;
import javax.swing.text.*;

import com.garretwilson.rdf.*;
import com.garretwilson.swing.text.xml.*;
import com.garretwilson.swing.text.xml.xeb.XEBEditorKit;
import com.globalmentor.io.*;
import com.globalmentor.text.xml.XMLProcessor;
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
	public String getContentType() {return OEBConstants.OEB10_PACKAGE_MEDIA_TYPE.toString();}

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
			final XMLProcessor xmlProcessor=new XMLProcessor(swingXMLDocument.getURIInputStreamable());  //create an XML processor that will use the input stream locator of the document for loading other needed documents
				//create a new processor for loading the package information
			final OEBPackageProcessor oebPackageProcessor=new OEBPackageProcessor(xmlProcessor);
			final RDF packageRDF=oebPackageProcessor.read(inputStream, swingXMLDocument.getBaseURI()); //read the package from the input stream G***maybe rename this to "process()"
			read(packageRDF, swingXMLDocument, pos);	//read the publication from the RDF data model
		}
		else  //if this is not an XML document we're reading into
		{
			super.read(inputStream, document, pos); //let the parent class do the reading
		}
	}

}

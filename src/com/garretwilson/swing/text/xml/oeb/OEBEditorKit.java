package com.garretwilson.swing.text.xml.oeb;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.mail.internet.ContentType;
import javax.swing.text.*;
import com.garretwilson.io.*;
import com.garretwilson.rdf.*;
import com.garretwilson.rdf.xpackage.*;
import com.garretwilson.swing.event.*;
import com.garretwilson.swing.text.xml.*;
import com.garretwilson.swing.text.xml.xhtml.XHTMLEditorKit;
import com.garretwilson.text.xml.*;
import com.garretwilson.text.xml.oeb.*;
import com.garretwilson.util.*;

/**An editor kit for an OEB publication.
@see XMLEditorKit
@author Garret Wilson
*/
public class OEBEditorKit extends XHTMLEditorKit implements OEBConstants	//TODO eventually go back to deriving from XMLEditorKit and use an appropriate editor kit for each document in the spine
{

	/**The task of reading a document.*/
	public final static String READ_TASK="READ";

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

	/**Returns the MIME type of the data the XML editor kit supports,
		which is that of an OEB package.
	@return The MIME type this editor kit supports, which is that of an OEB package.
	*/
	public String getContentType() {return OEBConstants.OEB10_PACKAGE_MEDIA_TYPE.toString();}


		/**
		 * Create an uninitialized text storage model
		 * that is appropriate for this type of editor.
		 *
		 * @return the model
		 */
//G***fix
	/**Creates an uninitialized OEB text storage model.
	@return The new OEB document model.
	*/
	public OEBDocument createDefaultDocument()
	{
//G***del Debug.traceStack("Creating default OEB document");  //G***del
		return new com.garretwilson.swing.text.xml.oeb.OEBDocument(getURIInputStreamable());	//create a new Swing OEB document
/*G***fix
	StyleSheet styles = getStyleSheet();
	StyleSheet ss = new StyleSheet();

	ss.addStyleSheet(styles);

	HTMLDocument doc = new HTMLDocument(ss);
	doc.putProperty(HTMLDocument.PARSER_PROPERTY, getParser());
	doc.setAsynchronousLoadPriority(4);
	doc.setTokenThreshold(100);
	return doc;
*/
	}


		/**
		 * Inserts content from the given stream. If <code>doc</code> is
		 * an instance of HTMLDocument, this will read
		 * html 3.2 text. Inserting html into a non-empty document must be inside
		 * the body Element, if you do not insert into the body an exception will
		 * be thrown. When inserting into a non-empty document all tags outside
		 * of the body (head, title) will be dropped.
		 *
		 * @param in  The stream to read from
		 * @param doc The destination for the insertion.
		 * @param pos The location in the document to place the
		 *   content.
		 * @exception IOException on any I/O error
		 * @exception BadLocationException if pos represents an invalid
		 *   location within the document.
		 * @exception RuntimeException (will eventually be a BadLocationException)
		 *            if pos is invalid.
		 */
	public void read(Reader in, Document doc, int pos) throws IOException, BadLocationException
	{
/*G***del
		if(doc instanceof com.garretwilson.swing.text.xml.oeb.OEBDocument)	//make sure this is a document we know how to work with
		{
//G***fix			final XMLProcessor xmlProcessor=new XMLProcessor(in);	//create an XML processor
			try
			{
				final OEBPublication publication=new OEBPublication();
				publication.load(in);	//G****testing
					//create an array of OEB XML documents
				final com.garretwilson.text.xml.XMLDocument[] xmlDocumentArray=new com.garretwilson.text.xml.XMLDocument[publication.getSpineList().size()];
				for(int i=0; i<publication.getSpineList().size(); ++i)	//look at each item in the spine
				{
					final OEBItem item=(OEBItem)publication.getSpineList().get(i);	//get a reference to this item
System.out.println("Loading OEB Item: "+item.getHRef());	//G***del
					item.load();	//make sure this item is loaded
					tidyOEBXMLDocument((com.garretwilson.text.xml.XMLDocument)item.getData());	//tidy up the document (an important step if the document has text directly in the body and such) G***test, comment
					xmlDocumentArray[i]=(com.garretwilson.text.xml.XMLDocument)item.getData();	//G***store this in a variable to speed things up, comment
				}

							//G***testing
				((com.garretwilson.swing.text.xml.oeb.OEBDocument)doc).insert(0, xmlDocumentArray);	//G***testing
*/
/*G***fix
				for(int i=0; i<publication.getSpineList().size(); ++i)	//look at each item in the spine
				{
					final OEBItem item=(OEBItem)publication.getSpineList().get(i);	//get a reference to this item
					((com.garretwilson.swing.text.xml.XMLDocument)doc).insert(doc.getLength(), (com.garretwilson.text.xml.XMLDocument)item.getData());	//G***testing; comment
				}
*/



/*G***fix
				final com.garretwilson.text.xml.XMLDocument xmlDocument=xmlProcessor.parseDocument();	//parse the document
				xmlDocument.getStyleSheetList().add(new DefaultOEBCSSStyleSheet());	//add the default stylesheet for OEB
						//G***do a normalize() somewhere here
				tidyOEBXMLDocument(xmlDocument);	//tidy up the document (an important step if the document has text directly in the body and such)
				final XMLCSSProcessor cssProcessor=new XMLCSSProcessor();	//create a new CSS processor
				cssProcessor.parseStyles(xmlDocument);	//parse this document's styles
						//G***check to make sure the styles are valid OEB styles somewhere here
				cssProcessor.applyxStyles(xmlDocument);	//apply the styles
//G***del			xmlRoot.dump();	//G***check, comment
System.out.println("Finished with file.");	//G***del
				((com.garretwilson.swing.text.xml.XMLDocument)doc).create(xmlDocument);	//create a new document with this OEB XML document tree G***probably change to insert
*/
/*G***del
			}
			catch(Exception ex)
			{
				System.out.println(ex.getMessage());
			}	//G***fix
*/

/*G***fix
			HTMLDocument hdoc = (HTMLDocument) doc;
			Parser p = getParser();
			if (p == null) {
		throw new IOException("Can't load parser");
			}
			if (pos > doc.getLength()) {
		throw new BadLocationException("Invalid location", pos);
			}

			ParserCallback receiver = hdoc.getReader(pos);
			Boolean ignoreCharset = (Boolean)doc.getProperty("IgnoreCharsetDirective");
			p.parse(in, receiver, (ignoreCharset == null) ? false : ignoreCharset.booleanValue());
			receiver.flush();
*/
/*G***del
		}
		else	//if this isn't an XML document
		{
*/
			super.read(in, doc, pos);	//let our parent read the document
//G***del		}
	}

	/**Inserts content from the given stream which is expected to be in a format
		appropriate for this kind of content handler.
	@param inputStream The stream to read from.
	@param document The destination for the insertion.
	@param pos The location in the document to place the content >= 0.
	@exception IOException Thrown on any I/O error
	@exception BadLocationException Thrown if pos represents an invalid location
		within the document.
	*/
	public void read(InputStream inputStream, Document document, int pos) throws IOException, BadLocationException
	{
Debug.trace("reading in an OEB editor kit, with document of type: ", document.getClass().getName());  //G**del
		if(document instanceof OEBDocument) //if this is a Swing OEB document
		{
			OEBDocument oebDocument=(OEBDocument)document; //cast the document to an OEB document
			final XMLProcessor xmlProcessor=new XMLProcessor(oebDocument.getURIInputStreamable());  //create an XML processor that will use the input stream locator of the document for loading other needed documents
			final URI[] baseURIArray; //we'll store here an array of base URIs, corresponding to the XML documents
			final org.w3c.dom.Document[] xmlDocumentArray; //we'll store here an array of XML document trees to load into document
			final ContentType[] mediaTypeArray; //we'll store here an array of media types (for OEB1, these should all be OEB document media types)
//G***del if not needed			final URL publicationURL=oebDocument.getBaseURL();  //get the base URL from the document G***what if we don't get a URL back?

				//create a new processor for loading the package information
			final OEBPackageProcessor oebPackageProcessor=new OEBPackageProcessor(xmlProcessor);
			final RDF packageRDF=oebPackageProcessor.read(inputStream, oebDocument.getBaseURI()); //read the package from the input stream G***maybe rename this to "process()"
			oebDocument.setRDF(packageRDF); //set the RDF used to describe the resources
				//get all the publications listed in the package
		  final Collection publicationCollection=RDFUtilities.getResourcesByType(packageRDF, OEB2Constants.OEB2_PACKAGE_NAMESPACE_URI, OEB2Constants.PUBLICATION_TYPE_NAME);
		  final Iterator publicationIterator=publicationCollection.iterator();  //get an iterator to the publications
		  if(publicationIterator.hasNext())  //if there is at least one publication
			{
				final OEBPublication oebPublication=(OEBPublication)publicationIterator.next();  //get the first publication
Debug.trace("oebDocument.setPublication()");
				oebDocument.setPublication(oebPublication);	//show the OEB document which publication it's associated with
			  final RDFSequenceResource organization=XPackageUtilities.getOrganization(oebPublication); //get the publication's organization
				if(organization!=null)  //if there is an organization
				{
				  final List spineList=new ArrayList(organization.getItemList()); //create a copy of the resources in the organization
Debug.trace("got spine list");  //G***del
						//make sure all documents in the manifest are in our list
						// (we'll add out-of-spine documents to our local spine in this implementation)
					final RDFListResource manifest=XPackageUtilities.getManifest(oebPublication); //get the publication's manifest
					if(manifest!=null)  //if there is a manifest
					{
						final Iterator manifestIterator=manifest.iterator(); //get an iterator of the items in the manifest
						while(manifestIterator.hasNext()) //while there are more manifest items
						{
							final RDFResource manifestItem=(RDFResource)manifestIterator.next(); //get the next OEB item
							final ContentType mediaType=MIMEOntologyUtilities.getMediaType(manifestItem); //get the item's media type
							//if this is an OEB document that is not in the spine
							if(OEB10_DOCUMENT_MEDIA_TYPE.equals(mediaType) && !spineList.contains(manifestItem))
							{
								spineList.add(manifestItem);  //add the item to our local spine
							}
						}
					}
					final int spineItemCount=spineList.size(); //find out how many spine items there are
					xmlDocumentArray=new org.w3c.dom.Document[spineItemCount]; //create an array of OEB XML documents
					baseURIArray=new URI[spineItemCount];  //create an array of URIs
					mediaTypeArray=new ContentType[spineItemCount];  //create an array of media types
					for(int i=0; i<spineItemCount; ++i)	//look at each item in the spine
					{
	//G***del Debug.trace("OEBEditorKit.read() Getting item: "+i+" of "+publication.getSpineList().size());
						final RDFResource item=(RDFResource)spineList.get(i);	//get a reference to this item
						assert item!=null : "Item is null";
Debug.trace("working on item: ", item); //G***del
	//G***del Debug.trace("OEBEditorKit.read() Got item: "+(item==null?"null":"not null"));
	//G***if there is no href (the spine doesn't reference any ID in the manifest), this next line will throw a null-pointer exception
						final String itemHRef=XPackageUtilities.getLocationHRef(item);  //get the item's href
						fireMadeProgress(new ProgressEvent(this, READ_TASK, "Loading OEB Item: "+itemHRef, i, spineItemCount));	//G***testing i18n
	//G***del Debug.trace("Loading OEB Item: "+item.getHRef());	//G***del
						try
						{
							final URI itemURI=oebDocument.getResourceURI(itemHRef); //get the item's URI
							final InputStream itemInputStream=oebDocument.getResourceAsInputStream(itemURI); //get an input stream to the object
							try
							{
								final org.w3c.dom.Document xmlDocument=xmlProcessor.parseDocument(itemInputStream, itemURI);	//parse the document
							  xmlDocument.normalize();  //normalize the document
//TODO del								tidyOEBXMLDocument((com.garretwilson.text.xml.XMLDocument)xmlDocument);	//tidy up the document (an important step if the document has text directly in the body and such) G***test, comment
								baseURIArray[i]=itemURI;  //store the URI of the item
								mediaTypeArray[i]=MIMEOntologyUtilities.getMediaType(item);  //store the media type of the item
								xmlDocumentArray[i]=xmlDocument;	//add the document to our array that we'll pass to the OEB document for insertion
							}
							finally
							{
								itemInputStream.close();  //always close the input stream to the document
							}
						}
						catch(final IllegalArgumentException illegalArgumentException)	//if we can't get the item's URI
						{
							final IOException ioException=new IOException(illegalArgumentException.getMessage());	//create an IO exception from the URI syntax exception
							ioException.initCause(illegalArgumentException);	//show what caused the error
							throw ioException;	//throw an IO exception version of the error
						}
					}
					setXML(xmlDocumentArray, baseURIArray, mediaTypeArray, oebDocument);  //put all the XML documents we loaded into the Swing OEB document
				}
//G***del when works						oebDocument.insert(0, xmlDocumentArray);	//G***testing; possibly should be create
			}
		}
		else  //if this is not an OEB document we're reading into
			super.read(inputStream, document, pos); //let the parent class do the reading
	}

}

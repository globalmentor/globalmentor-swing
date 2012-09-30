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

package com.globalmentor.swing.text.xml.xeb;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.text.*;
import javax.xml.parsers.DocumentBuilder;

import org.urframework.maqro.*;
import org.xml.sax.SAXException;


import static com.globalmentor.net.ContentTypeConstants.*;
import static com.globalmentor.rdf.xeb.RDFXEB.*;


import com.globalmentor.io.*;
import com.globalmentor.log.Log;
import com.globalmentor.net.*;
import com.globalmentor.rdf.*;
import com.globalmentor.rdf.xeb.*;
import com.globalmentor.rdf.xpackage.*;
import com.globalmentor.swing.event.*;
import com.globalmentor.swing.text.xml.*;
import com.globalmentor.swing.text.xml.xhtml.XHTMLEditorKit;
import com.globalmentor.text.xml.URIInputStreamableXMLEntityResolver;
import com.globalmentor.text.xml.XML;
import com.globalmentor.text.xml.oeb.OEB;

/**An editor kit for an XEB publication.
@see XMLEditorKit
@author Garret Wilson
*/
public class XEBEditorKit extends XHTMLEditorKit	//TODO eventually go back to deriving from XMLEditorKit and use an appropriate editor kit for each document in the spine
{

	/**The task of reading a document.*/
	public final static String READ_TASK="READ";

	/**The "text/x-oeb1-document" content type.*/
	protected final static ContentType OEB_DOCUMENT_MEDIA_TYPE=ContentType.getInstance(ContentType.TEXT_PRIMARY_TYPE, OEB.X_OEB1_DOCUMENT_SUBTYPE);

	/**The "application/x-maqro+rdf+xml" content type.*/
	protected final static ContentType MAQRO_MEDIA_TYPE=ContentType.getInstance(ContentType.APPLICATION_PRIMARY_TYPE, X_MAQRO_RDF_XML_SUBTYPE);
	
	/**Constructor.
	@param uriInputStreamable The source of input streams for resources.
	@exception NullPointerException if the new source of input streams is <code>null</code>.
	*/
	public XEBEditorKit(final URIInputStreamable uriInputStreamable)
	{
		super(uriInputStreamable);	//construct the parent class
	}

	/**Creates a copy of the editor kit.
	@return A copy of the XML editor kit.
	*/
	public Object clone() {return new XEBEditorKit(getURIInputStreamable());}

	/**Returns the MIME type of the data the XML editor kit supports, <code>application/x-xebook+rdf+xml</code>.
	@return The MIME type this editor kit supports.
	*/
	public String getContentType() {return ContentType.toString(ContentType.APPLICATION_PRIMARY_TYPE, ContentTypeConstants.X_XEBOOK_RDF_XML_SUBTYPE);}


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
/*TODO del
		if(doc instanceof com.globalmentor.swing.text.xml.oeb.OEBDocument)	//make sure this is a document we know how to work with
		{
//TODO fix			final XMLProcessor xmlProcessor=new XMLProcessor(in);	//create an XML processor
			try
			{
				final OEBPublication publication=new OEBPublication();
				publication.load(in);	//TODO *testing
					//create an array of OEB XML documents
				final com.globalmentor.text.xml.XMLDocument[] xmlDocumentArray=new com.globalmentor.text.xml.XMLDocument[publication.getSpineList().size()];
				for(int i=0; i<publication.getSpineList().size(); ++i)	//look at each item in the spine
				{
					final OEBItem item=(OEBItem)publication.getSpineList().get(i);	//get a reference to this item
System.out.println("Loading OEB Item: "+item.getHRef());	//TODO del
					item.load();	//make sure this item is loaded
					tidyOEBXMLDocument((com.globalmentor.text.xml.XMLDocument)item.getData());	//tidy up the document (an important step if the document has text directly in the body and such) TODO test, comment
					xmlDocumentArray[i]=(com.globalmentor.text.xml.XMLDocument)item.getData();	//TODO store this in a variable to speed things up, comment
				}

							//TODO testing
				((com.globalmentor.swing.text.xml.oeb.OEBDocument)doc).insert(0, xmlDocumentArray);	//TODO testing
*/
/*TODO fix
				for(int i=0; i<publication.getSpineList().size(); ++i)	//look at each item in the spine
				{
					final OEBItem item=(OEBItem)publication.getSpineList().get(i);	//get a reference to this item
					((com.globalmentor.swing.text.xml.XMLDocument)doc).insert(doc.getLength(), (com.globalmentor.text.xml.XMLDocument)item.getData());	//TODO testing; comment
				}
*/



/*TODO fix
				final com.globalmentor.text.xml.XMLDocument xmlDocument=xmlProcessor.parseDocument();	//parse the document
				xmlDocument.getStyleSheetList().add(new DefaultOEBCSSStyleSheet());	//add the default stylesheet for OEB
						//TODO do a normalize() somewhere here
				tidyOEBXMLDocument(xmlDocument);	//tidy up the document (an important step if the document has text directly in the body and such)
				final XMLCSSProcessor cssProcessor=new XMLCSSProcessor();	//create a new CSS processor
				cssProcessor.parseStyles(xmlDocument);	//parse this document's styles
						//TODO check to make sure the styles are valid OEB styles somewhere here
				cssProcessor.applyxStyles(xmlDocument);	//apply the styles
//TODO del			xmlRoot.dump();	//TODO check, comment
System.out.println("Finished with file.");	//TODO del
				((com.globalmentor.swing.text.xml.XMLDocument)doc).create(xmlDocument);	//create a new document with this OEB XML document tree TODO probably change to insert
*/
/*TODO del
			}
			catch(Exception ex)
			{
				System.out.println(ex.getMessage());
			}	//TODO fix
*/

/*TODO fix
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
/*TODO del
		}
		else	//if this isn't an XML document
		{
*/
			super.read(in, doc, pos);	//let our parent read the document
//TODO del		}
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
		if(document instanceof XMLDocument) //if this is a Swing XML document
		{
			final XMLDocument swingXMLDocument=(XMLDocument)document; //cast the document to a Swing XML document	
			final org.w3c.dom.Document xmlDocument=XML.parse(inputStream, swingXMLDocument.getBaseURI(), true, new URIInputStreamableXMLEntityResolver(swingXMLDocument.getURIInputStreamable()));	//parse the document using the input stream locator of the document for loading other needed documents
			xmlDocument.normalize(); //normalize the package description document
			final RDF rdf=new RDF();  //create a new RDF data model
			rdf.registerResourceFactory(XEB_NAMESPACE_URI, new RDFXEB());  //register an XEbook factory TODO use a common instance
			final RDFXMLProcessor rdfProcessor=new RDFXMLProcessor(rdf);	//create a new RDF processor using the RDF data model we already created
			try
			{
				rdfProcessor.processRDF(xmlDocument, swingXMLDocument.getBaseURI());  //parse the RDF from the document
			}
			catch(final URISyntaxException uriSyntaxException)	//if there was an invalid URI
			{
				throw (IOException)new IOException(uriSyntaxException.getMessage()).initCause(uriSyntaxException);
			}
	  	read(rdf, swingXMLDocument, pos);	//read the information from the RDF data model
		}
		else  //if this is not an OEB document we're reading into
		{
			super.read(inputStream, document, pos); //let the parent class do the reading
		}
	}

	/**Inserts content from the given RDF data model, which should contain a publication description.
	@param rdf The RDF data model.
	@param swingXMLDocument The destination for the insertion.
	@param pos The location in the document to place the content (>=0).
	@exception IOException Thrown on any I/O error
	@exception BadLocationException Thrown if pos represents an invalid location within the document.
	*/
	protected void read(final RDF rdf, final XMLDocument swingXMLDocument, int pos) throws IOException, BadLocationException
	{
Log.trace(RDFResources.toString(rdf));
//TODO del if not needed			final URL publicationURL=oebDocument.getBaseURL();  //get the base URL from the document TODO what if we don't get a URL back?
//TODO del if not needed		swingXMLDocument.setRDF(rdf); //set the RDF used to describe the resources
	  final Publication publication=(Publication)RDFResources.getResourceByType(rdf, XEB_NAMESPACE_URI, BOOK_CLASS_NAME);	//get the publication from the data model
	  if(publication!=null)	//if there is a book
	  {
			swingXMLDocument.setPublication(publication);	//show the XML document which publication it's associated with
			final List<RDFResource> itemList=new ArrayList<RDFResource>();	//create a new list to hold spine items
			gatherSpineItems(publication, itemList);	//gather the items in the spine
				//make sure all documents in the manifest are in our list
				// (we'll add out-of-spine documents to our local spine in this implementation)
/*TODO fix
			final RDFListResource<?> manifest=Marmot.getContents(publication); //get the publication's manifest
			if(manifest!=null)  //if there is a manifest
			{
				for(final RDFObject item:manifest)	//for each item in the manifest
				{
					final RDFResource resource=(RDFResource)item;	//assume the item is a resource TODO improve
					final ContentType mediaType=Marmot.getMediaType(resource); //get the item's media type
					//if this is an OEB document that is not in the spine
					if(OEB_DOCUMENT_MEDIA_TYPE.match(mediaType) && !itemList.contains(resource))
					{
						itemList.add(resource);  //add the item to our local spine
					}
				}
			}
*/
			final DocumentBuilder documentBuilder=XML.createDocumentBuilder(true, new URIInputStreamableXMLEntityResolver(swingXMLDocument.getURIInputStreamable()));	//create a document builder using the input stream locator of the document for loading other needed documents
			final ActivityModelIOKit activityModelIOKit=new ActivityModelIOKit(swingXMLDocument.getURIInputStreamable());	//create an IO kit for reading MAQRO activities
			final int spineItemCount=itemList.size(); //find out how many spine items there are
			final ContentData<?>[] contentDataArray=new ContentData[spineItemCount];	//create an array to hold each content data
			for(int i=0; i<spineItemCount; ++i)	//look at each item in the spine
			{
//TODO del Log.trace("OEBEditorKit.read() Getting item: "+i+" of "+publication.getSpineList().size());
				final RDFResource item=itemList.get(i);	//get a reference to this item
				final String itemHRef=XPackage.getLocationHRef(item);  //get the item's href
				if(itemHRef!=null)	//if this item has an href
				{
					fireMadeProgress(new ProgressEvent(this, READ_TASK, "Loading item: "+itemHRef, i, spineItemCount));	//TODO testing i18n
					try
					{
						final URI itemURI=swingXMLDocument.getResourceURI(itemHRef); //get the item's URI
//TODO fix with URF						final ContentType contentType=Marmot.getMediaType(item);	//get the item's content type
						final ContentType contentType=URIs.getContentType(itemURI);	//get the item's content type
//TODO fix						final ContentType contentType=null;	//TODO fix with URF
						if(MAQRO_MEDIA_TYPE.match(contentType))	//if this is a MAQRO activity
						{
							final InputStream itemInputStream=swingXMLDocument.getInputStream(itemURI); //get an input stream to the object
							try
							{
								final ResourceModel<Activity> activityModel=activityModelIOKit.load(itemInputStream, itemURI);	//load this activity
								contentDataArray[i]=new ContentData<Activity>(activityModel.getResource(), itemURI, contentType, item);	//create an object representing the MAQRO activity, giving the item as the description
							}
							finally
							{
								itemInputStream.close();  //always close the input stream to the document
							}
						}
						else	//if this is not a MAQRO activity, assume it is a XML document TODO make sure this is an XML content type, and later add support for PDF, Word, text, and the like
						{
							final InputStream itemInputStream=swingXMLDocument.getInputStream(itemURI); //get an input stream to the object
							try
							{
								final org.w3c.dom.Document xmlDocument=documentBuilder.parse(itemInputStream, itemURI.toString());	//parse the document
							  xmlDocument.normalize();  //normalize the document TODO decide if we want to normalize in both places or not
								contentDataArray[i]=new ContentData<org.w3c.dom.Document>(xmlDocument, itemURI, contentType, item);	//create an object representing the XML document content data, giving the item as the description
							}
							finally
							{
								itemInputStream.close();  //always close the input stream to the document
							}
						}
					}
					catch(final IllegalArgumentException illegalArgumentException)	//if we can't get the item's URI
					{
						throw new IOException(illegalArgumentException.getMessage(), illegalArgumentException);	//create and throw an IO exception from the URI syntax exception
					}
					catch(final SAXException saxException)
					{
						throw new IOException(saxException.getMessage(), saxException);
					}
				}
				else	//if this item has no href
				{
					throw new IOException("Item in spine missing href.");	//TODO i18n; add support for text content
				}
			}
			setXML(contentDataArray, swingXMLDocument);  //put all the data we loaded into the Swing XML document
	  }
	}

	/**Loads all documents in the spine and the spines of any contained bindings.
	@param binding The binding the spine of which to load.
	@param swingXMLDocument The Swing document is the ultimate destination of the loaded spine.
	@param xmlDocumentList	The list to be populated with XML documents.
	@param baseURIList The list to be populated with base URIs.
	@param contentTypeList The list to be populated with content types.
	@exception IOException if there is an error loading any of the documents.
	*/
/*TODO del if not needed
	protected void loadSpine(final Binding binding, final XMLDocument swingXMLDocument, final XMLProcessor xmlProcessor, final List<org.w3c.dom.Document> xmlDocumentList, final List<URI> baseURIList, final List<ContentType> contentTypeList) throws IOException
	{
	  final RDFListResource spine=binding.getSpine(); //get the binding's spine
		if(spine!=null)  //if there is a spine
		{
			final int spineItemCount=spine.size(); //find out how many spine items there are
			for(int i=0; i<spineItemCount; ++i)	//look at each item in the spine
			{
//TODO del Log.trace("OEBEditorKit.read() Getting item: "+i+" of "+publication.getSpineList().size());
				final RDFResource item=spine.get(i);	//get a reference to this item
				if(item instanceof Binding)	//if this item is a sub-binding
				{
					loadSpine((Binding)item, swingXMLDocument, xmlProcessor, xmlDocumentList, baseURIList, contentTypeList);	//load the sub-binding's spine
				}
				else	//if this is a normal item
				{
					final String itemHRef=XPackageUtilities.getLocationHRef(item);  //get the item's href
					if(itemHRef!=null)	//if this item has an href
					{
						fireMadeProgress(new ProgressEvent(this, READ_TASK, "Loading item: "+itemHRef, i, spineItemCount));	//TODO testing i18n
						try
						{
							final URI itemURI=swingXMLDocument.getResourceURI(itemHRef); //get the item's URI
							final ContentType contentType=MIMEOntologyUtilities.getMediaType(item);	//get the item's content type
								//TODO make sure this is an XML content type, and later add support for PDF, Word, text, and the like
							final InputStream itemInputStream=swingXMLDocument.getInputStream(itemURI); //get an input stream to the object
							try
							{
								final org.w3c.dom.Document xmlDocument=xmlProcessor.parseDocument(itemInputStream, itemURI);	//parse the document
							  xmlDocument.normalize();  //normalize the document
								xmlDocumentList.add(xmlDocument);	//add the document which we'll pass to the Swing XML document for insertion
								baseURIList.add(itemURI);	//store the URI of the item
								contentTypeList.add(contentType);  //store the media type of the item
							}
							finally
							{
								itemInputStream.close();  //always close the input stream to the document
							}
						}
						catch(final IllegalArgumentException illegalArgumentException)	//if we can't get the item's URI
						{
							throw (IOException)new IOException(illegalArgumentException.getMessage()).initCause(illegalArgumentException);	//create and trhwo an IO exception from the URI syntax exception
						}
					}
				}
			}
		}
	}
*/

	/**Gathers all items in the spine and the spines of any contained bindings.
	@param binding The binding the spine of which to load.
	@param itemList The list to be populated with spine items.
	*/
	protected void gatherSpineItems(final Binding binding, final List<RDFResource> itemList)
	{
	  final RDFListResource<?> spine=binding.getSpine(); //get the binding's spine
		if(spine!=null)  //if there is a spine
		{
			for(final RDFObject item:spine)	//for each item in the spine
			{
				if(item instanceof Binding)	//if this item is a sub-binding
				{
					gatherSpineItems((Binding)item, itemList);	//gather the sub-binding's spine
				}
				else if(item instanceof RDFResource)	//if this is a normal item
				{
					itemList.add((RDFResource)item);	//add this item to the item list
				}
			}
		}
	}

}

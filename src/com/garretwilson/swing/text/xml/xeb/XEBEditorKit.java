package com.garretwilson.swing.text.xml.xeb;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.mail.internet.ContentType;
import javax.swing.text.*;
import javax.swing.text.Document;

import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleSheet;

import com.garretwilson.io.*;
import com.garretwilson.model.Resource;
import com.garretwilson.model.ResourceModel;
import com.garretwilson.net.URIUtilities;
import com.garretwilson.rdf.*;

import static com.garretwilson.rdf.RDFUtilities.*;
import com.garretwilson.rdf.maqro.*;
import static com.garretwilson.rdf.maqro.MAQROConstants.*;
import com.garretwilson.rdf.xpackage.*;
import com.garretwilson.rdf.xeb.*;

import static com.garretwilson.io.ContentTypeConstants.*;
import static com.garretwilson.rdf.xeb.XEBConstants.*;

import com.garretwilson.swing.ListListModel;
import com.garretwilson.swing.event.*;
import static com.garretwilson.swing.text.rdf.RDFStyleUtilities.*;
import com.garretwilson.swing.text.xml.*;
import com.garretwilson.swing.text.xml.XMLEditorKit.ContentData;
import com.garretwilson.swing.text.xml.css.XMLCSSStyleUtilities;
import com.garretwilson.swing.text.xml.xhtml.XHTMLEditorKit;
import com.garretwilson.text.xml.XMLDOMImplementation;
import com.garretwilson.text.xml.XMLProcessor;
import com.garretwilson.text.xml.XMLUtilities;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSConstants;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSProcessor;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSStyleDeclaration;
import com.garretwilson.text.xml.xhtml.XHTMLConstants;
import com.garretwilson.util.Debug;
import com.garretwilson.util.NameValuePair;

/**An editor kit for an XEB publication.
@see XMLEditorKit
@author Garret Wilson
*/
public class XEBEditorKit extends XHTMLEditorKit	//TODO eventually go back to deriving from XMLEditorKit and use an appropriate editor kit for each document in the spine
{

	/**The task of reading a document.*/
	public final static String READ_TASK="READ";

	/**The "text/x-oeb1-document" content type.*/
	protected final static ContentType OEB_DOCUMENT_MEDIA_TYPE=new ContentType(TEXT, X_OEB1_DOCUMENT_SUBTYPE, null);

	/**The "application/x-maqro+rdf+xml" content type.*/
	protected final static ContentType MAQRO_MEDIA_TYPE=new ContentType(APPLICATION, X_MAQRO_RDF_XML_SUBTYPE, null);
	
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
	public String getContentType() {return ContentTypeUtilities.toString(ContentTypeConstants.APPLICATION, ContentTypeConstants.X_XEBOOK_RDF_XML_SUBTYPE);}


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
		if(document instanceof XMLDocument) //if this is a Swing XML document
		{
			final XMLDocument swingXMLDocument=(XMLDocument)document; //cast the document to a Swing XML document	
			final XMLProcessor xmlProcessor=new XMLProcessor(swingXMLDocument.getURIInputStreamable());  //create an XML processor that will use the input stream locator of the document for loading other needed documents
			final org.w3c.dom.Document xmlDocument=xmlProcessor.parseDocument(inputStream, swingXMLDocument.getBaseURI());	//parse the public description document
			xmlDocument.normalize(); //normalize the package description document
			final RDF rdf=new RDF();  //create a new RDF data model
			rdf.registerResourceFactory(XEB_NAMESPACE_URI, new XEBUtilities());  //register an XEbook factory TODO use a common instance
			final RDFXMLProcessor rdfProcessor=new RDFXMLProcessor(rdf);	//create a new RDF processor using the RDF data model we already created
			try
			{
				rdfProcessor.process(xmlDocument, swingXMLDocument.getBaseURI());  //parse the RDF from the document
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
Debug.trace(RDFUtilities.toString(rdf));
//G***del if not needed			final URL publicationURL=oebDocument.getBaseURL();  //get the base URL from the document G***what if we don't get a URL back?
		swingXMLDocument.setRDF(rdf); //set the RDF used to describe the resources
	  final Publication publication=(Publication)RDFUtilities.getResourceByType(rdf, XEB_NAMESPACE_URI, BOOK_CLASS_NAME);	//get the publication from the data model
	  if(publication!=null)	//if there is a book
	  {
			swingXMLDocument.setPublication(publication);	//show the XML document which publication it's associated with
			final List<RDFResource> itemList=new ArrayList<RDFResource>();	//create a new list to hold spine items
			gatherSpineItems(publication, itemList);	//gather the items in the spine
				//make sure all documents in the manifest are in our list
				// (we'll add out-of-spine documents to our local spine in this implementation)
			final RDFListResource manifest=XPackageUtilities.getManifest(publication); //get the publication's manifest
			if(manifest!=null)  //if there is a manifest
			{
				for(final RDFResource item:manifest)	//for each item in the manifest
				{
					final ContentType mediaType=MIMEOntologyUtilities.getMediaType(item); //get the item's media type
					//if this is an OEB document that is not in the spine
					if(OEB_DOCUMENT_MEDIA_TYPE.match(mediaType) && !itemList.contains(item))
					{
						itemList.add(item);  //add the item to our local spine
					}
				}
			}
			final XMLProcessor xmlProcessor=new XMLProcessor(swingXMLDocument.getURIInputStreamable());  //create an XML processor that will use the input stream locator of the document for loading other needed documents
			final ActivityModelIOKit activityModelIOKit=new ActivityModelIOKit(swingXMLDocument.getURIInputStreamable());	//create an IO kit for reading MAQRO activities
			final int spineItemCount=itemList.size(); //find out how many spine items there are
			final ContentData<?>[] contentDataArray=new ContentData[spineItemCount];	//create an array to hold each content data
			for(int i=0; i<spineItemCount; ++i)	//look at each item in the spine
			{
//G***del Debug.trace("OEBEditorKit.read() Getting item: "+i+" of "+publication.getSpineList().size());
				final RDFResource item=itemList.get(i);	//get a reference to this item
				final String itemHRef=XPackageUtilities.getLocationHRef(item);  //get the item's href
				if(itemHRef!=null)	//if this item has an href
				{
					fireMadeProgress(new ProgressEvent(this, READ_TASK, "Loading item: "+itemHRef, i, spineItemCount));	//G***testing i18n
					try
					{
						final URI itemURI=swingXMLDocument.getResourceURI(itemHRef); //get the item's URI
						final ContentType contentType=MIMEOntologyUtilities.getMediaType(item);	//get the item's content type
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
								final org.w3c.dom.Document xmlDocument=xmlProcessor.parseDocument(itemInputStream, itemURI);	//parse the document
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
						throw (IOException)new IOException(illegalArgumentException.getMessage()).initCause(illegalArgumentException);	//create and trhwo an IO exception from the URI syntax exception
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
//G***del Debug.trace("OEBEditorKit.read() Getting item: "+i+" of "+publication.getSpineList().size());
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
						fireMadeProgress(new ProgressEvent(this, READ_TASK, "Loading item: "+itemHRef, i, spineItemCount));	//G***testing i18n
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
	  final RDFListResource spine=binding.getSpine(); //get the binding's spine
		if(spine!=null)  //if there is a spine
		{
			for(final RDFResource item:spine)	//for each item in the spine
			{
				if(item instanceof Binding)	//if this item is a sub-binding
				{
					gatherSpineItems((Binding)item, itemList);	//gather the sub-binding's spine
				}
				else	//if this is a normal item
				{
					itemList.add(item);	//add this item to the item list
				}
			}
		}
	}

	/**Creates an attribute set for the given RDF resource.
	The attribute set will be given an XML namespace and local name corresponding to the type of the given resource.
	@param resource The RDF resource.
	@return An attribute set reflecting the resource.
	*/
	protected MutableAttributeSet createAttributeSet(final RDFResource resource, final URI baseURI)
	{
		final RDFResource type=getType(resource);	//get the resource type
		final URI namespaceURI=type!=null ? RDFXMLifier.getNamespaceURI(type) : null;	//get the namespace of the resource
		final String localName=type!=null ? RDFXMLifier.getLocalName(type) : null;	//get the local name of the resource
		final MutableAttributeSet attributeSet=namespaceURI!=null && localName!=null	//if we could find both a type namespace and a type local name
				? createAttributeSet(namespaceURI, localName)	//create an attribute set with the given namespace and local name
				: new SimpleAttributeSet();	//if type information isn't available, create a new default attribute set for this element
		setRDFResource(attributeSet, resource);	//store the resource in the attribute set
		return attributeSet;	//return the attribute set we created
	}

	/**Appends element spec objects from content data.
	This implementation adds support for MAQRO activity content data.
	@param elementSpecList The list of element specs to be inserted into the document.
	@param contentData The content to be inserted into the document.
	@param swingXMLDocument The Swing document into which the content will be set.
	@return The attribute set for the content data root.
	@exception IllegalArgumentException if the given content data is not recognized or is not supported.
	*/
	protected MutableAttributeSet appendElementSpecList(final List<DefaultStyledDocument.ElementSpec> elementSpecList, final ContentData<?> contentData, final XMLDocument swingXMLDocument)
	{
		if(contentData.getObject() instanceof Activity)	//if this is a MAQRO activity
		{
			return appendMAQROActivityElementSpecList(elementSpecList, (ContentData<Activity>)contentData, swingXMLDocument);	//append MAQRO activity content
		}
		else	//if we don't recognize this content data
		{
			return super.appendElementSpecList(elementSpecList, contentData, swingXMLDocument);	//append the content data normally
		}
	}

	/**Appends element spec objects from MAQRO activity content data.
	@param elementSpecList The list of element specs to be inserted into the document.
	@param contentData The MAQRO activity content to be inserted into the document.
	@param swingXMLDocument The Swing document into which the content will be set.
	@return The attribute set for the MAQRO activity.
	*/
	protected MutableAttributeSet appendMAQROActivityElementSpecList(final List<DefaultStyledDocument.ElementSpec> elementSpecList, final ContentData<? extends Activity> contentData, final XMLDocument swingXMLDocument)
	{
		final Activity activity=contentData.getObject();	//get a reference to this activity
		final URI baseURI=contentData.getBaseURI(); //get a reference to the base URI
		final ContentType mediaType=contentData.getContentType(); //get a reference to the media type
/*TODO later fix auto-styling of activity elements
		final org.w3c.dom.Element xmlDocumentElement=xmlDocument.getDocumentElement();	//get the root of the document
		final URI publicationBaseURI=swingXMLDocument.getBaseURI();	//get the base URI of the publication TODO do we need to check this for null?
			//if there is a publication, see if we have a description of this resource in the manifest
		final RDFResource description=contentData.getDescription();
			//TODO make sure the stylesheet applier correctly distinguishes between document base URI for internal stylesheets and publication base URI for package-level base URIs
		final CSSStyleSheet[] stylesheets=getXMLStylesheetApplier().getStylesheets(xmlDocument, baseURI, mediaType, description);	//G***testing
		for(int i=0; i<stylesheets.length; getXMLStylesheetApplier().applyStyleSheet(stylesheets[i++], xmlDocumentElement));	//G***testing
			//TODO make sure stylesheets get applied later, too, in our Swing stylesheet application routine
		getXMLStylesheetApplier().applyLocalStyles(xmlDocumentElement);	//apply local styles to the document TODO why don't we create one routine to do all of this?
*/
		return appendElementSpecList(elementSpecList, activity, baseURI);	//append the activity
/*TODO del when works
		final MutableAttributeSet activityAttributeSet=appendElementSpecList(elementSpecList, activity, baseURI);	//append the activity
		if(baseURI!=null) //if there is a base URI
		{
			XMLStyleUtilities.setBaseURI(activityAttributeSet, baseURI); //add the base URI as an attribute
			XMLStyleUtilities.setTargetURI(activityAttributeSet, baseURI);  //because this element is the root of the document, its base URI acts as a linking target as well; store the target URI for quick searching
		}
		if(mediaType!=null) //if there is a media type
		{
			XMLStyleUtilities.setMediaType(activityAttributeSet, mediaType); //add the media type as an attribute
		}
*/
	}

	/**Appends information from a MAQRO activity to a list of element specs.
	@param elementSpecList The list of element specs to be inserted into the document.
	@param activity The MAQRO activity.
	@param baseURI The base URI of the RDF data model.
	@return The attribute set used to represent the resource.
	@exception BadLocationException for an invalid starting offset
	*/
	protected MutableAttributeSet appendElementSpecList(final List<DefaultStyledDocument.ElementSpec> elementSpecList, final Activity activity, final URI baseURI)
	{
		final MutableAttributeSet attributeSet=createAttributeSet(activity, baseURI);	//create and fill an attribute set based upon the RDF resource
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(attributeSet, DefaultStyledDocument.ElementSpec.StartTagType));	//create the beginning of a Swing element to model this resource
		final List<RDFResource> interactions=activity.getInteractions();	//get the activity interactions
		if(interactions!=null)	//if there are choices
		{
			for(final RDFResource interaction:interactions)	//for each interaction
			{
				if(interaction instanceof Question)	//if this interaction is a question
				{
					appendElementSpecList(elementSpecList, (Question)interaction, baseURI);	//append element specs for the interaction
				}
				else
				{
					throw new IllegalArgumentException("Non-question interaction not supported");
				}
			}
		}
		else
		{
			throw new IllegalArgumentException("Activity without interactions not supported");
		}
//G***fix if(!"null".equals(xmlElement.getLocalName()))	//G***testing
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(attributeSet, DefaultStyledDocument.ElementSpec.EndTagType));	//finish the element we started at the beginning of this function
		return attributeSet;  //return the attribute set used for the element
	}

	/**Appends information from a MAQRO question to a list of element specs.
	The children of the element will be any choices the question may have.
	@param elementSpecList The list of element specs to be inserted into the document.
	@param question The MAQRO question.
	@param baseURI The base URI of the RDF data model.
	@return The attribute set used to represent the resource.
	@exception BadLocationException for an invalid starting offset
	*/
	protected MutableAttributeSet appendElementSpecList(final List<DefaultStyledDocument.ElementSpec> elementSpecList, final Question question, final URI baseURI)
	{
		final MutableAttributeSet attributeSet=createAttributeSet(question, baseURI);	//create and fill an attribute set based upon the RDF resource
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(attributeSet, DefaultStyledDocument.ElementSpec.StartTagType));	//create the beginning of a Swing element to model this resource
		final Dialogue query=question.getQuery();	//get the question query
		if(query!=null)	//if the question has a query
		{
			appendElementSpecList(elementSpecList, query, baseURI);	//append element specs for the query dialogue
		}
		final List<RDFResource> choices=question.getChoices();	//get the question choices
		if(choices!=null)	//if there are choices
		{
			final MutableAttributeSet choicesAttributeSet=createAttributeSet(MAQRO_NAMESPACE_URI, CHOICES_PROPERTY_NAME);	//create and fill an attribute set for the choices property
			elementSpecList.add(new DefaultStyledDocument.ElementSpec(choicesAttributeSet, DefaultStyledDocument.ElementSpec.StartTagType));	//create the beginning of a Swing element to model this resource
			for(final RDFResource choice:choices)	//for each choice
			{
				if(choice instanceof Dialogue)	//if this choice is dialogue
				{
					appendElementSpecList(elementSpecList, (Dialogue)choice, baseURI);	//append element specs for the dialogue
				}
				else
				{
					throw new IllegalArgumentException("Non-dialogue choices not supported");
				}
			}
			elementSpecList.add(new DefaultStyledDocument.ElementSpec(choicesAttributeSet, DefaultStyledDocument.ElementSpec.EndTagType));	//finish the element we started at the beginning of this function
			
		}
		else
		{
			throw new IllegalArgumentException("Questions without choices not supported");
		}
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(attributeSet, DefaultStyledDocument.ElementSpec.EndTagType));	//finish the element we started at the beginning of this function
		return attributeSet;  //return the attribute set used for the element
	}

	/**Appends information from MAQRO dialogue to a list of element specs.
	The child element will be the literal value of the dialogue.
	@param elementSpecList The list of element specs to be inserted into the document.
	@param dialogue The MAQRO dialogue.
	@param baseURI The base URI of the RDF data model.
	@return The attribute set used to represent the resource.
	@exception BadLocationException for an invalid starting offset
	*/
	protected MutableAttributeSet appendElementSpecList(final List<DefaultStyledDocument.ElementSpec> elementSpecList, final Dialogue dialogue, final URI baseURI)
	{
		final MutableAttributeSet attributeSet=createAttributeSet(dialogue, baseURI);	//create and fill an attribute set based upon the RDF resource
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(attributeSet, DefaultStyledDocument.ElementSpec.StartTagType));	//create the beginning of a Swing element to model this resource
		final RDFLiteral dialogueValue=dialogue.getValue();	//get the value of this dialogue
		if(dialogueValue instanceof RDFPlainLiteral)	//if the dialogue is a plain literal
		{
			final String text=dialogueValue!=null ? dialogueValue.getLexicalForm() : "X";	//TODO use an object replacement character
			appendElementSpecListContent(elementSpecList, null, null, baseURI, text);	//append the dialogue literal text
		}
		else if(dialogueValue instanceof RDFXMLLiteral)	//if the dialogue is an XML literal
		{
			final RDFXMLLiteral xmlLiteralDialogueValue=(RDFXMLLiteral)dialogueValue;
			appendElementSpecListContent(elementSpecList, xmlLiteralDialogueValue.getDocumentFragment(), attributeSet, baseURI);	//
		}
		else	//if we don't understand the type of dialogue value given (i.e. it's not a plain literal or an XML literal)
		{
			throw new IllegalArgumentException("Unknown dialogue literal type for "+dialogueValue);
		}
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(attributeSet, DefaultStyledDocument.ElementSpec.EndTagType));	//finish the element we started at the beginning of this function
		return attributeSet;  //return the attribute set used for the element
	}

}

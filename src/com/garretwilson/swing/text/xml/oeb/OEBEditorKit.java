package com.garretwilson.swing.text.xml.oeb;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.text.*;
import com.garretwilson.io.*;
import com.garretwilson.rdf.*;
import com.garretwilson.rdf.xpackage.*;
import com.garretwilson.swing.event.*;
import com.garretwilson.swing.text.xml.*;
import com.garretwilson.text.xml.*;
import com.garretwilson.text.xml.oeb.*;
import com.garretwilson.util.*;

/**An editor kit for an OEB publication.
@see com.garretwilson.swing.text.xml.XMLEditorKit
@author Garret Wilson
*/
public class OEBEditorKit extends XMLEditorKit implements OEBConstants
{

	/**The task of reading a document.*/
	public final static String READ_TASK="READ";

	/**A static application/java media type for quick reference in the view factory.*/
//G***del 	protected final static MediaType APPLICATION_JAVA_MEDIA_TYPE=new MediaType(MediaType.APPLICATION, MediaType.JAVA);

	/**The default view factory for an OEB editor kit.*/
//G***del	private static final ViewFactory defaultViewFactory=new OEBViewFactory(); //G***optimize; probably use a soft reference so that this can be collected if needed

	/**Default constructor.*/
	public OEBEditorKit() {}

	/**Creates a copy of the editor kit.
	@return A copy of the XML editor kit.
	*/
	public Object clone() {return new OEBEditorKit();}

	/**Returns the MIME type of the data the XML editor kit supports,
		which is that of an OEB package.
	@return The MIME type this editor kit supports, which is that of an OEB package.
	*/
	public String getContentType() {return OEBConstants.OEB10_PACKAGE_MEDIA_TYPE.toString();}


	/**Returns a factory for producing views for models that use this editor kit.
	@return A factory to produce views for this editor kit.
	@see #OEBViewFactory
	*/
//G***del	public ViewFactory getViewFactory() {return defaultViewFactory;}

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
	public Document createDefaultDocument()
	{
//G***del Debug.traceStack("Creating default OEB document");  //G***del
		return new com.garretwilson.swing.text.xml.oeb.OEBDocument();	//create a new Swing OEB document
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

	/**Tidies up an XML document containing OEB data so that we can display it better.
	@param xmlDocument The document to tidy.
	*/
/*G***del; moved to XMLEditorKit
	protected static void tidyOEBXMLDocument(final com.garretwilson.text.xml.XMLDocument xmlDocument)
	{
		final XMLElement xmlRoot=xmlDocument.getDocumentXMLElement();	//get the root of the document
		for(int i=0; i<xmlRoot.getChildNodes().getLength(); ++i)	//look at each of the first-level child nodes
		{
			final XMLNode xmlNode=(XMLNode)xmlRoot.getChildNodes().item(i);	//get a reference to this node
			if(xmlNode.getNodeName().equals(OEBConstants.ELEMENT_BODY) || xmlNode.getNodeName().equals(OEBConstants.ELEMENT_HTML))	//if this is the body element G***testing HTML
			{
				final XMLElement bodyElement=(XMLElement)xmlNode;	//get a reference to the body element
//G***del Debug.trace("Found body element.");
				XMLNode childNode=(XMLNode)bodyElement.getFirstChild();	//get the first child
				while(childNode!=null)	//while the body has child nodes
				{
//G***del System.out.println("Looking at node: "+childNode.getNodeName());
					XMLNode nextNode=(XMLNode)childNode.getNextSibling();	//get a reference to the next sibling so we'll have it when we need it
					if(childNode.getNodeType()==XMLNode.TEXT_NODE)	//if this is a text node
					{
//G***del System.out.println("This is a text node");
						if(((XMLText)childNode).getData().trim().length()==0)	//if this text node has only whitespace
						{
//G***del System.out.println("The length of text is zero.");
							childNode.getParentNode().removeChild(childNode);	//remove the text child from the list
						}
					}
//G***del System.out.println("Going to next child.");
					childNode=nextNode;	//look at the next node
				}
			}
		}

	}
*/


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
			final MediaType[] mediaTypeArray; //we'll store here an array of media types (for OEB1, these should all be OEB document media types)
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
					final RDFBagResource manifest=XPackageUtilities.getManifest(oebPublication); //get the publication's manifest
					if(manifest!=null)  //if there is a manifest
					{
						final Iterator manifestIterator=manifest.getItemIterator(); //get an iterator of the items in the manifest
						while(manifestIterator.hasNext()) //while there are more manifest items
						{
							final RDFResource manifestItem=(RDFResource)manifestIterator.next(); //get the next OEB item
							final MediaType mediaType=MIMEOntologyUtilities.getMediaType(manifestItem); //get the item's media type
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
					mediaTypeArray=new MediaType[spineItemCount];  //create an array of media types
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
								tidyOEBXMLDocument((com.garretwilson.text.xml.XMLDocument)xmlDocument);	//tidy up the document (an important step if the document has text directly in the body and such) G***test, comment
								baseURIArray[i]=itemURI;  //store the URI of the item
								mediaTypeArray[i]=MIMEOntologyUtilities.getMediaType(item);  //store the media type of the item
								xmlDocumentArray[i]=xmlDocument;	//add the document to our array that we'll pass to the OEB document for insertion
							}
							finally
							{
								itemInputStream.close();  //always close the input stream to the document
							}
						}
						catch(URISyntaxException uriSyntaxException)	//if we can't get the item's URI
						{
							final IOException ioException=new IOException(uriSyntaxException.getMessage());	//create an IO exception from the URI syntax exception
							ioException.initCause(uriSyntaxException);	//show what caused the error
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

/*G***del when works

Debug.trace("Ready to load publication: ", publicationURL); //G***del
			final OEBPublication publication=new OEBPublication(publicationURL);	//create a new publication from the given URL
//G***fix; see if we need				oebDocument.setPublication(publication);	//tell the document when publication is being used
			try
			{
Debug.trace("publication.load()");
				publication.load();	//load the publication G***this can be modified, perhaps, to load other things as well
Debug.trace("oebDocument.setPublication()");
				oebDocument.setPublication(publication);	//show the OEB document which publication it's associated with
//G***del				oebDocument.setInputStreamLocator(publication); //add
Debug.trace("now ready to load publication into RDF");  //G***del
				  //create a new processor for loading the package information
				final OEBPackageProcessor oebPackageProcessor=new OEBPackageProcessor(xmlProcessor);
				final RDF packageRDF=oebPackageProcessor.read(inputStream, publicationURL); //read the package from the input stream

//G***fix all this to use RDF


				final List spineList=new ArrayList(publication.getSpineList()); //create a copy of the spine list
					//make sure all documents in the manifest are in our list
					// (we'll add out-of-spine documents to our local spine in this implementation)
				final Iterator manifestIterator=publication.getManifestIterator();  //get an iterator to the manifest items
				while(manifestIterator.hasNext()) //while there are more manifest items
				{
					final OEBItem manifestItem=(OEBItem)manifestIterator.next();  //get the next manifest item
						//if this is an OEB document that is not in the spine
					if(manifestItem.getMediaType().equals(OEB10_DOCUMENT_MEDIA_TYPE) && !spineList.contains(manifestItem))
					{
						spineList.add(manifestItem);  //add the item to our local spine
					}
				}
				final int spineItemCount=spineList.size(); //find out how many spine items there are
				xmlDocumentArray=new org.w3c.dom.Document[spineItemCount]; //create an array of OEB XML documents
				baseURLArray=new URL[spineItemCount];  //create an array of URLs
				mediaTypeArray=new MediaType[spineItemCount];  //create an array of media types
				for(int i=0; i<spineItemCount; ++i)	//look at each item in the spine
				{
//G***del Debug.trace("OEBEditorKit.read() Getting item: "+i+" of "+publication.getSpineList().size());
					final OEBItem item=(OEBItem)spineList.get(i);	//get a reference to this item
Debug.assert(item!=null, "Item is null");
//G***del Debug.trace("OEBEditorKit.read() Got item: "+(item==null?"null":"not null"));
//G***if there is no href (the spine doesn't reference any ID in the manifest), this next line will throw a null-pointer exception
					fireMadeProgress(new ProgressEvent(this, READ_TASK, "Loading OEB Item: "+item.getHRef(), i, spineItemCount));	//G***testing i18n
//G***del Debug.trace("Loading OEB Item: "+item.getHRef());	//G***del


//G***del if not needed						item.load();	//make sure this item is loaded G***we should probably assert() that this is really an OEB document, since only they can be on the spine

//G***del					final URL itemURL=item.getURL();  //get the item's URL
					final URL itemURL=publication.getURL(item);  //get the item's URL
					final InputStream itemInputStream=oebDocument.getResourceAsInputStream(itemURL);  //get an input stream to the object
					try
					{
						final org.w3c.dom.Document xmlDocument=publication.getXMLProcessor().parseDocument(itemInputStream, itemURL);	//parse the document
											//G***do a normalize() somewhere here
						tidyOEBXMLDocument((com.garretwilson.text.xml.XMLDocument)xmlDocument);	//tidy up the document (an important step if the document has text directly in the body and such) G***test, comment

//G***del when works					final com.garretwilson.text.xml.XMLDocument xmlDocument=(com.garretwilson.text.xml.XMLDocument)item.loadData();	//get the XML document that is the item's data
//G***del					tidyOEBXMLDocument(xmlDocument);	//tidy up the document (an important step if the document has text directly in the body and such) G***test, comment
						baseURLArray[i]=itemURL;  //store the URL of the item
						mediaTypeArray[i]=item.getMediaType();  //store the media type of the item
						xmlDocumentArray[i]=xmlDocument;	//add the document to our array that we'll pass to the OEB document for insertion
					}
					finally
					{
						itemInputStream.close();  //always close the input stream to the document
					}
//G***del when works							calculateTargetIDs(xmlDocument, item.getURL());	//G***testing; comment
				}
				setXML(xmlDocumentArray, baseURLArray, mediaTypeArray, oebDocument);  //put all the XML documents we loaded into the Swing OEB document
//G***del when works						oebDocument.insert(0, xmlDocumentArray);	//G***testing; possibly should be create
			}
			finally
			{
//G***fix; important; we don't want to leave everything open, yet we may want to load something later					publication.close();	//always close the publication when we're finished with it G***if we don't load everything now, this could cause a problem if an OEB item needs to be loaded later
			}
		}
		else  //if this is not an OEB document we're reading into
			super.read(inputStream, document, pos); //let the parent class do the reading
*/
	}

	/**Gets the target ID of of the specified element. This ID represents the
		target of a link. The default target ID (the value of the "id" attribute)
		is located, and if it does not exist the value of the "name" attribute is
		used. G***what about checking the DTD for an element of type ID?
	@param attributeSet The attribute set of the element which may contain a
		target ID.
//G***del when works	@param element The element which may contain a target ID.
	@return The target ID value of the element, or <code>null</code> if the
		element does not define a target ID.
	*/
	protected String getTargetID(final AttributeSet attributeSet)
	{
		String targetID=super.getTargetID(attributeSet); //get the standard "id" attribute value
		if(targetID==null)  //if the "id" attribute didn't have a value
		{
//G***del when works			final AttributeSet attributeSet=element.getAttributes();	//get the attributes of this element
			targetID=XMLStyleUtilities.getXMLAttributeValue(attributeSet, null, "name");  //return the value of the "name" attribute, if it exists G***use a constant here
		}
		return targetID;  //return whatever target ID we found, if any
	}

		/**
		 * Inserts HTML into an existing document.
		 *
		 * @param doc Document to insert into.
		 * @param offset offset to insert HTML at
		 * @param popDepth number of ElementSpec.EndTagType to generate before
		 *        inserting.
		 * @param pushDepth number of ElementSpec.StartTagType with a direction
		 *        of ElementSpec.JoinNextDirection that should be generated
		 *        before inserting, but after the end tags have been generated.
		 * @param insertTag first tag to start inserting into document.
		 * @exception RuntimeException (will eventually be a BadLocationException)
		 *            if pos is invalid.
		 */
/*G***fix
		public void insertHTML(HTMLDocument doc, int offset, String html,
				 int popDepth, int pushDepth,
				 HTML.Tag insertTag) throws
								 BadLocationException, IOException {
	Parser p = getParser();
	if (p == null) {
			throw new IOException("Can't load parser");
	}
	if (offset > doc.getLength()) {
			throw new BadLocationException("Invalid location", offset);
	}

	ParserCallback receiver = doc.getReader(offset, popDepth, pushDepth,
						insertTag);
	Boolean ignoreCharset = (Boolean)doc.getProperty
													("IgnoreCharsetDirective");
	p.parse(new StringReader(html), receiver, (ignoreCharset == null) ?
		false : ignoreCharset.booleanValue());
	receiver.flush();
		}
*/

		/**
		 * Write content from a document to the given stream
		 * in a format appropriate for this kind of content handler.
		 *
		 * @param out  The stream to write to
		 * @param doc The source for the write.
		 * @param pos The location in the document to fetch the
		 *   content.
		 * @param len The amount to write out.
		 * @exception IOException on any I/O error
		 * @exception BadLocationException if pos represents an invalid
		 *   location within the document.
		 */
/*G***fix
		public void write(Writer out, Document doc, int pos, int len)
	throws IOException, BadLocationException {

	if (doc instanceof HTMLDocument) {
			HTMLWriter w = new HTMLWriter(out, (HTMLDocument)doc, pos, len);
	    w.write();
	} else if (doc instanceof StyledDocument) {
	    MinimalHTMLWriter w = new MinimalHTMLWriter(out, (StyledDocument)doc, pos, len);
	    w.write();
	} else {
	    super.write(out, doc, pos, len);
	}
		}
*/

		/**
		 * Called when the kit is being installed into the
		 * a JEditorPane.
		 *
		 * @param c the JEditorPane
		 */
/*G***fix
		public void install(JEditorPane c) {
	c.addMouseListener(linkHandler);
				c.addMouseMotionListener(tmpHandler);
	super.install(c);
		}
*/

		/**
		 * Called when the kit is being removed from the
		 * JEditorPane.  This is used to unregister any
		 * listeners that were attached.
		 *
		 * @param c the JEditorPane
		 */
/*G***fix
		public void deinstall(JEditorPane c) {
	c.removeMouseListener(linkHandler);
				c.removeMouseMotionListener(tmpHandler);
	super.deinstall(c);
		}
*/

    /**
     * Default Cascading Style Sheet file that sets
     * up the tag views.
     */
//G***fix    public static final String DEFAULT_CSS = "default.css";

		/**
		 * Set the set of styles to be used to render the various
		 * html elements.  These styles are specified in terms of
		 * css specifications.  Each document produced by the kit
		 * will have a copy of the sheet which it can add the
		 * document specific styles to.  By default, the StyleSheet
		 * specified is shared by all HTMLEditorKit instances.
		 * This should be reimplemented to provide a finer granularity
		 * if desired.
		 */
/*G***fix
		public void setStyleSheet(StyleSheet s) {
	defaultStyles = s;
		}
*/

		/**
		 * Get the set of styles currently being used to render the
		 * html elements.  By default the resource specified by
		 * DEFAULT_CSS gets loaded, and is shared by all HTMLEditorKit
		 * instances.
		 */
/*G***fix
		public StyleSheet getStyleSheet() {
	if (defaultStyles == null) {
			defaultStyles = new StyleSheet();
			try {
		InputStream is = HTMLEditorKit.getResourceAsStream(DEFAULT_CSS);
		Reader r = new BufferedReader(new InputStreamReader(is));
		defaultStyles.loadRules(r, null);
		r.close();
	    } catch (Throwable e) {
		// on error we simply have no styles... the html
		// will look mighty wrong but still function.
	    }
	}
	return defaultStyles;
		}
*/

		/**
		 * Fetch a resource relative to the HTMLEditorKit classfile.
		 * If this is called on 1.2 the loading will occur under the
		 * protection of a doPrivileged call to allow the HTMLEditorKit
		 * to function when used in an applet.
		 *
		 * @param name the name of the resource, relative to the
		 *  HTMLEditorKit class.
		 * @returns a stream representing the resource
		 */
/*G***fix
		static InputStream getResourceAsStream(String name) {
	try {
			Class klass;
			ClassLoader loader = HTMLEditorKit.class.getClassLoader();
			if (loader != null) {
		klass = loader.loadClass("javax.swing.text.html.ResourceLoader");
			} else {
		klass = Class.forName("javax.swing.text.html.ResourceLoader");
			}
			Class[] parameterTypes = { String.class };
	    Method loadMethod = klass.getMethod("getResourceAsStream", parameterTypes);
	    String[] args = { name };
	    return (InputStream) loadMethod.invoke(null, args);
	} catch (Throwable e) {
	    // If the class doesn't exist or we have some other
	    // problem we just try to call getResourceAsStream directly.
	    return HTMLEditorKit.class.getResourceAsStream(name);
	}
		}
*/

		/**
		 * Fetches the command list for the editor.  This is
		 * the list of commands supported by the superclass
		 * augmented by the collection of commands defined
		 * locally for style operations.
		 *
		 * @return the command list
		 */
/*G***fix
		public Action[] getActions() {
	return TextAction.augmentList(super.getActions(), this.defaultActions);
		}
*/

		/**
		 * Copies the key/values in <code>element</code>s AttributeSet into
		 * <code>set</code>. This does not copy component, icon, or element
		 * names attributes. Subclasses may wish to refine what is and what
		 * isn't copied here. But be sure to first remove all the attributes that
		 * are in <code>set</code>.<p>
		 * This is called anytime the caret moves over a different location.
		 *
		 */
/*G***fix
		protected void createInputAttributes(Element element,
					 MutableAttributeSet set) {
	set.removeAttributes(set);
	set.addAttributes(element.getAttributes());
	set.removeAttribute(StyleConstants.ComposedTextAttribute);

	Object o = set.getAttribute(StyleConstants.NameAttribute);
	if (o instanceof HTML.Tag) {
			HTML.Tag tag = (HTML.Tag)o;
			// PENDING: we need a better way to express what shouldn't be
			// copied when editing...
			if(tag == HTML.Tag.IMG) {
		// Remove the related image attributes, src, width, height
		set.removeAttribute(HTML.Attribute.SRC);
		set.removeAttribute(HTML.Attribute.HEIGHT);
		set.removeAttribute(HTML.Attribute.WIDTH);
		set.addAttribute(StyleConstants.NameAttribute,
				 HTML.Tag.CONTENT);
			}
			else if (tag == HTML.Tag.HR) {
		// Don't copy HR's either.
		set.addAttribute(StyleConstants.NameAttribute,
				 HTML.Tag.CONTENT);
			}
			else if (tag == HTML.Tag.COMMENT) {
		// Don't copy COMMENTs either
		set.addAttribute(StyleConstants.NameAttribute,
				 HTML.Tag.CONTENT);
		set.removeAttribute(HTML.Attribute.COMMENT);
			}
			else if (tag instanceof HTML.UnknownTag) {
		// Don't copy unknowns either:(
		set.addAttribute(StyleConstants.NameAttribute,
				 HTML.Tag.CONTENT);
		set.removeAttribute(HTML.Attribute.ENDTAG);
			}
	}
		}
*/

		/**
		 * Gets the input attributes used for the styled
		 * editing actions.
		 *
		 * @return the attribute set
		 */
/*G***fix
		public MutableAttributeSet getInputAttributes() {
	if (input == null) {
			input = getStyleSheet().addStyle(null, null);
	}
	return input;
		}
*/

		/**
		 * Fetch the parser to use for reading html streams.
		 * This can be reimplemented to provide a different
		 * parser.  The default implementation is loaded dynamically
		 * to avoid the overhead of loading the default parser if
		 * it's not used.  The default parser is the HotJava parser
		 * using an html 3.2 dtd.
		 */
/*G***fix
		protected Parser getParser() {
	if (defaultParser == null) {
			try {
								Class c = Class.forName("javax.swing.text.html.parser.ParserDelegator");
								defaultParser = (Parser) c.newInstance();
			} catch (Throwable e) {
			}
	}
	return defaultParser;
		}
*/

		// --- variables ------------------------------------------
/*G***fix
		MutableAttributeSet input;
		private static StyleSheet defaultStyles = null;
		private MouseListener linkHandler = new LinkController();
		private static Parser defaultParser = null;

		private MouseMotionListener tmpHandler = new TemporaryHandler();
*/

}

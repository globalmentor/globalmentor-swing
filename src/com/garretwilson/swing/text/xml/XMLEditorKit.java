package com.garretwilson.swing.text.xml;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.net.URI;
import java.util.*;
import java.util.List;
import javax.mail.internet.ContentType;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.Document;
import javax.swing.text.Element;

import com.garretwilson.io.*;
import com.garretwilson.lang.*;
import static com.garretwilson.lang.ObjectUtilities.*;
import com.garretwilson.net.*;
import com.garretwilson.rdf.*;
import static com.garretwilson.rdf.xpackage.XPackageUtilities.*;
import com.garretwilson.swing.*;
import com.garretwilson.swing.text.BasicStyledEditorKit;
import com.garretwilson.swing.text.SwingTextUtilities;
import com.garretwilson.swing.text.xml.css.*;
import com.garretwilson.text.*;
import com.garretwilson.text.xml.*;
import com.garretwilson.text.xml.stylesheets.css.*;	//G***del if we don't need
import com.garretwilson.util.*;
import org.w3c.dom.*;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleSheet;

/**An editor kit for XML.
@author Garret Wilson
@see com.garretwilson.text.xml.XMLProcessor
*/
public class XMLEditorKit extends BasicStyledEditorKit
{

	/**The default media type this editor kit supports, <code>text/xml</code>.*/
	protected final static ContentType DEFAULT_MEDIA_TYPE=new ContentType(ContentTypeConstants.TEXT, ContentTypeConstants.XML_SUBTYPE, null);

	/**The default view factory for an XML editor kit.*/
	private final ViewFactory defaultViewFactory=new DefaultXMLViewFactory();

	/**The default link controller for an XML editor kit.*/
	private final XMLLinkController defaultLinkController=new DefaultXMLLinkController();

	/**A map of view factories, each keyed to a namespace URI string.*/
	private Map<String, ViewFactory> namespaceViewFactoryMap=new HashMap<String, ViewFactory>();

			/**Registers a view factory for a particular namespace URI.
			@param namespaceURI The namespace URI that identifies the namespace,
				elements in which will use the given view factory to create views.
			@param viewFactory The view factory that should be associated with the
				given namespace.
			*/
			public void registerViewFactory(final String namespaceURI, final ViewFactory viewFactory)
			{
				namespaceViewFactoryMap.put(namespaceURI, viewFactory); //store the view factory in the map, keyed to the namespace URI
			}

		  /**Retrieves a view factory for the given namespace, if one has been
				registered.
			@param namespaceURI The namespace for which a view factory should be
				returned.
			@return A view factory for creating views for elements in the given
				namepace, or <code>null</code> if no view factory has been registered
				for the given namespace.
		  */
			public ViewFactory getViewFactory(final String namespaceURI)
			{
				return namespaceViewFactoryMap.get(namespaceURI); //return a view factory for the given namespace, if one has been registered
			}

			/**Removes all registered view factories.*/
			public void unregisterViewFactories()
			{
				namespaceViewFactoryMap.clear();  //clear all registered view factoriees
			}

	/**A map of link controllers, each keyed to a namespace URI string.*/
	private Map<String, XMLLinkController> namespaceLinkControllerMap=new HashMap<String, XMLLinkController>();

			/**Registers a link controller for a particular namespace URI.
			@param namespaceURI The namespace URI that identifies the namespace,
				elements in which will use the given link controller for linking.
			@param linkController The link controller that should be associated with the
				given namespace.
			*/
			public void registerLinkController(final String namespaceURI, final XMLLinkController linkController)
			{
				namespaceLinkControllerMap.put(namespaceURI, linkController); //store the link controller in the map, keyed to the namespace URI
			}

		  /**Retrieves a link controller for the given namespace, if one has been
				registered.
			@param namespaceURI The namespace for which a link controller should be
				returned.
			@return A link controller for handling links for elements in the given
				namepace, or <code>null</code> if no view factory has been registered
				for the given namespace.
		  */
			public XMLLinkController getLinkController(final String namespaceURI)
			{
				return namespaceLinkControllerMap.get(namespaceURI); //return a link controller for the given namespace, if one has been registered
			}

			/**Removes all registered link controllers.*/
			public void unregisterLinkControllers()
			{
				namespaceLinkControllerMap.clear();  //clear all registered link controllers
			}

	/**Default actions used by this editor kit to augment the super class default
		actions.
	*/
	private static final Action[] DEFAULT_ACTIONS=
	{
//G***del		new EndLineAction(endLineAction, false)	//G***testing
	};

	/**The object that applies stylesheets to the document.*/
//TODO bring back if needed	private final SwingXMLCSSStylesheetApplier swingStylesheetApplier;

		/**@return The object that applies stylesheets to the document.*/
//TODO bring back if needed		protected SwingXMLCSSStylesheetApplier getSwingStylesheetApplier() {return swingStylesheetApplier;}

	/**The object that applies stylesheets to an XML document.*/
	private final XMLCSSStylesheetApplier xmlStylesheetApplier;

		/**@return The object that applies stylesheets to the XML document.*/
		protected XMLCSSStylesheetApplier getXMLStylesheetApplier() {return xmlStylesheetApplier;}

	/**Constructor which defaults to a content type of <code>text/xml</code>.
	@param uriInputStreamable The source of input streams for resources.
	@exception NullPointerException if the new source of input streams is <code>null</code>.
	*/
	public XMLEditorKit(final URIInputStreamable uriInputStreamable)
	{
		this(DEFAULT_MEDIA_TYPE, uriInputStreamable);	//construct the class with the default media type
	}

	/**Constructor that specifies the specific XML media type supported.
	@param mediaType The XML media type supported. In some instances, such as
		<code>text/html</code>, this indicates a default namespace even in the
		absence of a document namespace identfication.
	@param uriInputStreamable The source of input streams for resources.
	*/
	public XMLEditorKit(final ContentType mediaType, final URIInputStreamable uriInputStreamable)
	{
		super(mediaType, uriInputStreamable);	//construct the parent class
//	TODO bring back if needed		swingStylesheetApplier=new SwingXMLCSSStylesheetApplier();	//create a new Swing stylesheet applier
		xmlStylesheetApplier=new XMLCSSStylesheetApplier(getURIInputStreamable());	//create a new XML stylesheet applier, using ourselves as the input stream locator
	}

	/**Creates a copy of the editor kit.
	@return A copy of the XML editor kit.
	*/
	public Object clone() {return new XMLEditorKit(getMediaType(), getURIInputStreamable());}  //G***why do we need this?; make a real clone, or make sure XHTMLEditorKit overrides this

	/**Returns a factory for producing views for models that use this editor kit.
	@return A factory to produce views for this editor kit.
	@see #DefaultXMLViewFactory
	*/
	public ViewFactory getViewFactory() {return defaultViewFactory;}

	/**Returns a controller for handling hyperlinks.
	@return A class to control hyperlinking.
	@see #DefaulXMLLinkController
	*/
	public XMLLinkController getLinkController() {return defaultLinkController;}

	/**Create an uninitialized text storage model that is appropriate for this type of editor.
	This version return a Swing XML document.
	@return The model.
	*/
	public XMLDocument createDefaultDocument()
	{
		return new XMLDocument(getURIInputStreamable());	//create an XML document, passing along our source of input streams
	}

	/**Reads a given publication and stores it in the given document.
	@param publicationURL The URL of the OEB publication which has the information to load.
	@param doc The destination for the insertion.
	@param pos The location in the document to place the content. G***decide if we want/need this
	@exception IOException on any I/O error
	@exception BadLocationException if pos represents an invalid location within the document.
	@exception RuntimeException (will eventually be a BadLocationException) if pos is invalid.
	*/

/*G***fix somehow
G***fix
	protected void readXML(final URL xmlDocumentURL)  //G**fix throws IOException, BadLocationException
	{


						//G***it would be nice here to simply delegate this to the XMLEditorKit here
						final InputStream xmlInputStream=publicationURL.openConnection().getInputStream();		//connect to the URL and get an input stream
						final XMLProcessor xmlProcessor=new XMLProcessor();	//create a new XML processor
						final com.garretwilson.text.xml.XMLDocument xmlDocument=xmlProcessor.parseDocument(xmlInputStream, publicationURL);	//parse the document
									//G***do a normalize() somewhere here
						xmlDocument.getStyleSheetList().add(new DefaultOEBCSSStyleSheet());	//add the default stylesheet for OEB
						final XMLCSSProcessor cssProcessor=new XMLCSSProcessor();	//create a new CSS processor
						cssProcessor.parseStyles(xmlDocument, publicationURL);	//parse this document's styles
								//G***check to make sure the styles are valid OEB styles somewhere here
						cssProcessor.applyxStyles(xmlDocument);	//apply the styles
						tidyOEBXMLDocument(xmlDocument);	//tidy up the document (an important step if the document has text directly in the body and such) G***test, comment

	//G***del when works					calculateTargetIDs(xmlDocument, publicationURL);	//G***testing; comment
	//G***del when works					oebDocument.insert(0, xmlDocument);	//G***testing
	//G***bring back or fix					((com.garretwilson.swing.text.xml.oeb.OEBDocument)doc).insert(0, xmlDocument);	//G***testing
	//G***fix					oebDocument.insert(0, new com.garretwilson.text.xml.XMLDocument[]{xmlDocument});	//G***testing
						baseURLArray=new URL[]{publicationURL}; //create an array of URLs with our one URL
						xmlDocumentArray=new com.garretwilson.text.xml.XMLDocument[]{xmlDocument};  //create an array with just our OEB document
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
	public void read(final Reader reader, final Document document, final int pos) throws IOException, BadLocationException
	{
		if(document instanceof XMLDocument) //if this is a Swing XML document
		{
			XMLDocument swingXMLDocument=(XMLDocument)document; //cast the document to an XML document
			final URI baseURI=swingXMLDocument.getBaseURI();  //get the base URI from the document
			final XMLProcessor xmlProcessor=new XMLProcessor();	//create a new XML processor
			final org.w3c.dom.Document xmlDocument=xmlProcessor.parseDocument(reader, baseURI);	//parse the document
			xmlDocument.normalize();  //normalize the document
//TODO del			tidyOEBXMLDocument((com.garretwilson.text.xml.XMLDocument)xmlDocument);	//tidy up the document (an important step if the document has text directly in the body and such) G***test, comment
				//read and set any contained RDF
			final RDFXMLProcessor rdfProcessor=new RDFXMLProcessor(); //create a new RDF processor
			final RDF rdf;
			try
			{
				rdf=rdfProcessor.process(xmlDocument, baseURI);	//process any contained RDF
			}
			catch (URISyntaxException e)
			{
				throw new IOException(e.toString());	//TODO fix better
			}  
			swingXMLDocument.setRDF(rdf); //set the RDF in our document
			setXML(xmlDocument, baseURI, getMediaType(), swingXMLDocument);  //G***fix
		}
		else  //if this is not an XML document we're reading into
			super.read(reader, document, pos); //let the parent class do the reading
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
			XMLDocument swingXMLDocument=(XMLDocument)document; //cast the document to an XML document
			final URI baseURI=swingXMLDocument.getBaseURI();  //get the base URI from the document
			final XMLProcessor xmlProcessor=new XMLProcessor();	//create a new XML processor
			final org.w3c.dom.Document xmlDocument=xmlProcessor.parseDocument(inputStream, baseURI);	//parse the document
			xmlDocument.normalize();  //normalize the document
//TODO del		  tidyOEBXMLDocument((com.garretwilson.text.xml.XMLDocument)xmlDocument);	//tidy up the document (an important step if the document has text directly in the body and such) G***test, comment
				//read and set any contained RDF
			final RDFXMLProcessor rdfProcessor=new RDFXMLProcessor(); //create a new RDF processor
			final RDF rdf;
			try
			{
				rdf=rdfProcessor.process(xmlDocument, baseURI);	//process any contained RDF
			}
			catch (URISyntaxException e)
			{
				throw new IOException(e.toString());
			}  
		  swingXMLDocument.setRDF(rdf); //set the RDF in our document
		  setXML(xmlDocument, baseURI, getMediaType(), swingXMLDocument);  //G***fix
		}
		else  //if this is not an XML document we're reading into
			super.read(inputStream, document, pos); //let the parent class do the reading
	}

	/**Write content from a document to the given stream in a format appropriate
		for this kind of content handler. Currently the position and length are
		ignored and the entire document is written.
	@param writer The writer to write to
	@param document The source of the data to write.
	@param pos The location in the document to fetch the content (>=0).
	@param len The amount to write out (>=0).
	@exception IOException Thrown if any I/O error occurs.
	@exception BadLocationException Thrown if the position represents an invalid
		location within the document.
	*/
//TODO fix when XMLSerializere supports writers	public void write(final Writer writer, final Document document, final int pos, final int len) throws IOException, BadLocationException

	/**Writes content from a document to the given stream in a format appropriate
		for this kind of content handler. Currently the position and length are
		ignored and the entire document is written. By default UTF-8 encoding
		is used.
	@param outputStream The stream to write to.
	@param document The source of the data to write.
	@param pos The location in the document to fetch the content (>=0).
	@param len The amount to write out (>=0).
	@exception IOException Thrown if any I/O error occurs.
	@exception BadLocationException Thrown if the position represents an invalid
		location within the document.
	*/
	public void write(final OutputStream outputStream, final Document document, final int pos, final int len) throws IOException, BadLocationException
	{
		write(outputStream, CharacterEncodingConstants.UTF_8, document, pos, len);	//write using UTF-8
	}

	/**Writes content from a document to the given stream in a format appropriate
		for this kind of content handler. Currently the position and length are
		ignored and the entire document is written.
	@param outputStream The stream to write to.
	@param encoding The encoding format to use when serializing.
	@param document The source of the data to write.
	@param pos The location in the document to fetch the content (>=0).
	@param len The amount to write out (>=0).
	@exception IOException Thrown if any I/O error occurs.
	@exception BadLocationException Thrown if the position represents an invalid
		location within the document.
	*/
	public void write(final OutputStream outputStream, final String encoding, final Document document, final int pos, final int len) throws IOException, BadLocationException
	{
		if(document instanceof XMLDocument) //if the document is an XML document
		{
			final org.w3c.dom.Document xmlDocument=getXML(((XMLDocument)document));  //create an XML document from given Swing document
			final XMLSerializer xmlSerializer=new XMLSerializer();  //create an XML serializer G***fix the formatted argument
			xmlSerializer.serialize(xmlDocument, outputStream, encoding);  //write the document to the output stream using the specified encoding
		}
		else  //if the document is not an XML document
			super.write(outputStream, document, pos, len);  //do the default writing
	}

	/**Called when the editor kit is being installed into the <code>JEditorPane</code>.
		This version adds listeners so that the editor kit can be notified of
		mouse events in order to correctly generate hyperlink events.
	@param editorPane The editor pane into which this editor kit is being installed.
	*/
	public void install(JEditorPane editorPane)
	{
Debug.trace("installing XMLEditorKit"); //G***del
		unregisterViewFactories();  //unregister all registered view factories
		if(editorPane instanceof XMLTextPane) //if we're being installed into an XML text pane
		{
			final XMLTextPane xmlTextPane=(XMLTextPane)editorPane;  //cast the editor pane to a text pane
				//get all registered view factories from the XML text pane
			final Iterator<String> viewFactoryNamespaceIterator=xmlTextPane.getViewFactoryNamespaceIterator(); //get an iterator to all namespaces of intalled editor kits
			while(viewFactoryNamespaceIterator.hasNext())  //while there are more namespaces
			{
				final String namespaceURI=viewFactoryNamespaceIterator.next(); //get the next namespace for which a view factory is installed
//G***del Debug.trace("setting view factory registered for namespace: ", namespaceURI); //G***del
				final ViewFactory registeredViewFactory=xmlTextPane.getViewFactory(namespaceURI); //get the view factory associated with this namespace
				registerViewFactory(namespaceURI, registeredViewFactory);  //register this view factory with the the namespace
			}
				//get all registered link controllers from the XML text pane
			final Iterator<String> linkControllerNamespaceIterator=xmlTextPane.getLinkControllerNamespaceIterator(); //get an iterator to all namespaces of intalled link controllers
			while(linkControllerNamespaceIterator.hasNext())  //while there are more namespaces
			{
				final String namespaceURI=linkControllerNamespaceIterator.next(); //get the next namespace for which a link controller is installed
				final XMLLinkController registeredLinkController=xmlTextPane.getLinkController(namespaceURI); //get the link controller associated with this namespace
				registerLinkController(namespaceURI, registeredLinkController);  //register this link controller with the the namespace
			}
		}
		editorPane.addMouseListener(getLinkController());	//tell the editor pane we want our link handler to listen for mouse events
		editorPane.addMouseMotionListener(getLinkController());	//tell the editor pane we want our link handler to listen for mouse movements
		super.install(editorPane);	//do the default installation
	}

	/**Called when the editor kit is being removed from the <code>JEditorPane</code>.
		This is used to unregister any listeners that were attached.
	@param editorPane The editor pane into which this editor kit was installed.
	*/
	public void deinstall(JEditorPane editorPane)
	{
		unregisterViewFactories();  //unregister all registered view factories
		unregisterLinkControllers();  //unregister all registered link controllers
		editorPane.removeMouseListener(getLinkController());	//remove our link handler from the editor pane's list of mouse listeners
		editorPane.removeMouseMotionListener(getLinkController());	//tell the editor pane we no longer care about mouse movements
		super.deinstall(editorPane);	//do the default uninstalling
	}

	/**Fetches the command list for the editor. This is the list of commands
		supported by the superclass augmented by the collection of commands defined
		locally for such things as page operations.
	@return The command list
	*/
	public Action[] getActions()
	{
		return TextAction.augmentList(super.getActions(), DEFAULT_ACTIONS);
	}

		//document information storage methods

	/**Sets the given XML data in the document.
	@param xmlDocument The XML document to set in the Swing document.
	@param baseURI The base URI, corresponding to the XML document.
	@param mediaType The media type of the XML document.
	@param swingXMLDocument The Swing document into which the XML will be set.
	*/
	public void setXML(final org.w3c.dom.Document xmlDocument, final URI baseURI, final ContentType mediaType, final XMLDocument swingXMLDocument)
	{
		setXML(new ContentData[]{new ContentData<org.w3c.dom.Document>(xmlDocument, baseURI, mediaType)}, swingXMLDocument); //set the XML data, creating an array with a single element
	}

	/**Sets the given XML data in the document.
	@param contentDataArray the array of data objects to insert into the document.
	@param swingXMLDocument The Swing document into which the XML will be set.
	*/
	public void setXML(final ContentData<?>[] contentDataArray, final XMLDocument swingXMLDocument)
	{
/*G***fix
		if(false)	//TODO testing newstuff 
		{
			swingXMLDocument.create(xmlDocumentArray, baseURIArray, mediaTypeArray);	//G***testing newstuff
			
		}
		else
*/
		{
			final XMLCSSStylesheetApplier stylesheetApplier=getXMLStylesheetApplier();	//get the stylesheet applier
			stylesheetApplier.clearStyles();	//clear any styles that were present before
			//create a list of element specs for creating the document and store them here
			final DefaultStyledDocument.ElementSpec[] elementSpecList=createElementSpecs(contentDataArray, swingXMLDocument);
			stylesheetApplier.clearStyles();	//clear the styles; we're done with the XML document so we don't need the mappings anymore
			swingXMLDocument.create(elementSpecList);	//create the document from the element specs
		}

Debug.trace("Finished creating document, length: "+swingXMLDocument.getLength());

//G***del elementSpecList[elementSpecList.length-1].setDirection(DefaultStyledDocument.ElementSpec.JoinPreviousDirection);	//G***fix

/*G***fix---this seems to work! make sure there is an ending '\n' before deleting the last character
try
{
Debug.trace("*****************\n****************\n***********");
	if(swingXMLDocument.getLength()>0)
		Debug.trace("last character: \""+swingXMLDocument.getText(swingXMLDocument.getLength()-1, 1)+"\"");
	if(swingXMLDocument.getLength()>1)
		Debug.trace("second to last character: \""+swingXMLDocument.getText(swingXMLDocument.getLength()-2, 1)+"\"");

Debug.trace("removing after-last character");
	if(swingXMLDocument.getLength()>0)
		swingXMLDocument.remove(swingXMLDocument.getLength()-1, 1);

	if(swingXMLDocument.getLength()>0)
		Debug.trace("last character: \""+swingXMLDocument.getText(swingXMLDocument.getLength()-1, 1)+"\"");
	if(swingXMLDocument.getLength()>1)
		Debug.trace("second to last character: \""+swingXMLDocument.getText(swingXMLDocument.getLength()-2, 1)+"\"");

}
catch (BadLocationException e)
{
	Debug.error(e);	//G***del all this
}
*/

	//G***testing; put in correct place		swingDocument.applyStyles(); //G***testing; put in the correct place, and make sure this gets called when repaginating, if we need to
	}

	/**Creates element spec objects from an XML document tree.
	@param xmlDocument The XML document tree.
	@param baseURI The base URI of the document.
	@param mediaType The media type of the document.
	@param swingXMLDocument The Swing document into which the XML will be set.
	@return Am array of element specs defining the XML document.
	*/
/*TODO del if not needed
	protected DefaultStyledDocument.ElementSpec[] createElementSpecs(org.w3c.dom.Document xmlDocument, final URI baseURI, final ContentType mediaType, final XMLDocument swingXMLDocument)
	{
		return createElementSpecs(new org.w3c.dom.Document[]{xmlDocument}, new URI[]{baseURI}, new ContentType[]{mediaType}, swingXMLDocument);  //put the XML document into an array, create the element specs, and return them
	}
*/

	/**Creates element spec objects from a list of XML document trees.
	@param contentDataArray the array of data objects to insert into the document.
	@param swingXMLDocument The Swing document into which the XML will be set.
	@return An array of element specs defining the XML documents.
	*/
	protected DefaultStyledDocument.ElementSpec[] createElementSpecs(final ContentData<?>[] contentDataArray, final XMLDocument swingXMLDocument)
	{
		final List<DefaultStyledDocument.ElementSpec> elementSpecList=new ArrayList<DefaultStyledDocument.ElementSpec>();	//create an array to hold our element specs
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(null, DefaultStyledDocument.ElementSpec.StartTagType));	//create the beginning of a Swing element to enclose all elements
		for(int i=0; i<contentDataArray.length; ++i)	//look at each content data
		{
			if(i>0)	//if this is not the first data to insert
			{
							//G***check to see if we should actually do this, first (from the CSS attributes)
//G***del System.out.println("Adding page break element.");	//G***del
						appendElementSpecListPageBreak(elementSpecList);  //append a page break
			}
			final ContentData<?> contentData=contentDataArray[i];	//get a reference to this content data
			appendElementSpecList(elementSpecList, contentData, swingXMLDocument);	//append element specs for this document
		}
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(null, DefaultStyledDocument.ElementSpec.EndTagType));	//finish the element that encloses all the documents
		return (DefaultStyledDocument.ElementSpec[])elementSpecList.toArray(new DefaultStyledDocument.ElementSpec[elementSpecList.size()]);
	}
	
	/**Appends element spec objects from content data.
	Child classes can override this method for processing of custom content data.
	@param elementSpecList The list of element specs to be inserted into the document.
	@param contentData The content to be inserted into the document.
	@param swingXMLDocument The Swing document into which the content will be set.
	@exception IllegalArgumentException if the given content data is not recognized or is not supported.
	*/
	protected void appendElementSpecList(final List<DefaultStyledDocument.ElementSpec> elementSpecList, final ContentData<?> contentData, final XMLDocument swingXMLDocument)
	{
		if(contentData.getObject() instanceof org.w3c.dom.Document)	//if this is XML document content data
		{
			appendXMLDocumentElementSpecList(elementSpecList, (ContentData<org.w3c.dom.Document>)contentData, swingXMLDocument);	//append XML content
		}
		else	//if we don't recognize this content data
		{
			throw new IllegalArgumentException("Unrecognized content type "+contentData.getObject().getClass().getName());
		}
	}

	/**Appends element spec objects from XML document content data.
	@param elementSpecList The list of element specs to be inserted into the document.
	@param contentData The XML document content to be inserted into the document.
	@param swingXMLDocument The Swing document into which the content will be set.
	*/
	protected void appendXMLDocumentElementSpecList(final List<DefaultStyledDocument.ElementSpec> elementSpecList, final ContentData<? extends org.w3c.dom.Document> contentData, final XMLDocument swingXMLDocument)
	{
		final org.w3c.dom.Document xmlDocument=contentData.getObject();	//get a reference to this document
		xmlDocument.normalize();	//G***do we want to do this here? probably not---or maybe so. Maybe we can normalize on the fly in the Swing document, not in the source
		final URI baseURI=contentData.getBaseURI(); //get a reference to the base URI
		final ContentType mediaType=contentData.getContentType(); //get a reference to the media type
		final org.w3c.dom.Element xmlDocumentElement=xmlDocument.getDocumentElement();	//get the root of the document
		final URI publicationBaseURI=swingXMLDocument.getBaseURI();	//get the base URI of the publication TODO do we need to check this for null?
			//if there is a publication, see if we have a description of this resource in the manifest
		final RDFResource description=contentData.getDescription();
			//TODO make sure the stylesheet applier correctly distinguishes between document base URI for internal stylesheets and publication base URI for package-level base URIs
		final CSSStyleSheet[] stylesheets=getXMLStylesheetApplier().getStylesheets(xmlDocument, baseURI, mediaType, description);	//G***testing
		for(int i=0; i<stylesheets.length; getXMLStylesheetApplier().applyStyleSheet(stylesheets[i++], xmlDocumentElement));	//G***testing
			//TODO make sure stylesheets get applied later, too, in our Swing stylesheet application routine
		getXMLStylesheetApplier().applyLocalStyles(xmlDocumentElement);	//apply local styles to the document TODO why don't we create one routine to do all of this?

		final MutableAttributeSet documentAttributeSet=appendElementSpecList(elementSpecList, xmlDocumentElement, baseURI);	//insert this document's root element into our list our list of elements
		if(baseURI!=null) //if there is a base URI
		{
			XMLStyleUtilities.setBaseURI(documentAttributeSet, baseURI); //add the base URI as an attribute
			XMLStyleUtilities.setTargetURI(documentAttributeSet, baseURI);  //because this element is the root of the document, its base URI acts as a linking target as well; store the target URI for quick searching
		}
		if(mediaType!=null) //if there is a media type
		{
			XMLStyleUtilities.setMediaType(documentAttributeSet, mediaType); //add the media type as an attribute
		}
		final DocumentType documentType=xmlDocument.getDoctype(); //get the XML document's doctype, if any
		if(documentType!=null) //if this document has a doctype
		{
			if(documentType.getPublicId()!=null)  //if the document has a public ID
				XMLStyleUtilities.setXMLDocTypePublicID(documentAttributeSet, documentType.getPublicId());  //store the public ID
			if(documentType.getSystemId()!=null)  //if the document has a public ID
				XMLStyleUtilities.setXMLDocTypeSystemID(documentAttributeSet, documentType.getSystemId());  //store the system ID
		}
			//store the processing instructions
		final List processingInstructionList=XMLUtilities.getNodesByName(xmlDocument, Node.PROCESSING_INSTRUCTION_NODE, "*", false);  //get a list of all the processing instructions in the document G***use a constant here
		if(processingInstructionList.size()>0) //if there are processing instructions
		{
			final NameValuePair[] processingInstructions=new NameValuePair[processingInstructionList.size()];  //create enough name/value pairs for processing instructions
			for(int processingInstructionIndex=0; processingInstructionIndex<processingInstructionList.size(); ++processingInstructionIndex)	//look at each of the processing instruction nodes
			{
				final ProcessingInstruction processingInstruction=(ProcessingInstruction)processingInstructionList.get(processingInstructionIndex);	//get a reference to this processing instruction
				processingInstructions[processingInstructionIndex]=new NameValuePair<String, String>(processingInstruction.getTarget(), processingInstruction.getData()); //create a name/value pair from the processing instruction
/*G***del when works
						//add an attribute representing the processing instruction, prepended by the special characters for a processing instruction
					attributeSet.addAttribute(XMLStyleConstants.XML_PROCESSING_INSTRUCTION_ATTRIBUTE_START+processingInstruction.getTarget(), processingInstruction.getData());
*/
			}
			XMLStyleUtilities.setXMLProcessingInstructions(documentAttributeSet, processingInstructions); //add the processing instructions
		}
/*G***fix
			if(XHTMLSwingTextUtilities.isHTMLDocumentElement(documentAttributeSet);	//see if this is an HTML document
			{
				if(childAttributeSet instanceof MutableAttributeSet)	//G***testing
				{
					final MutableAttributeSet mutableChildAttributeSet=(MutableAttributeSet)childAttributeSet;
					mutableChildAttributeSet.addAttribute("$hidden", Boolean.TRUE);	//G***testing
										
				}
			}
*/
	}

	/**Appends information from an XML element tree into a list of element specs.
	@param elementSpecList The list of element specs to be inserted into the document.
	@param xmlElement The XML element tree.
	@param baseURI The base URI of the document, used for generating full target
		URIs for quick searching.
	@return The attribute set used to represent the element; this attribute set
		can be manipulated after the method returns.
	@exception BadLocationException for an invalid starting offset
	@see XMLDocument#insert
	*/
	protected MutableAttributeSet appendElementSpecList(final List<DefaultStyledDocument.ElementSpec> elementSpecList, final org.w3c.dom.Element xmlElement, final URI baseURI)
	{
//G***del Debug.trace("XMLDocument.appendElementSpecList: element ", xmlElement.getNodeName());	//G***del
		final MutableAttributeSet attributeSet=createAttributeSet(xmlElement, baseURI);	//create and fill an attribute set based upon this element's CSS style
//G***del Debug.trace("Attribute set: ", attributeSet);  //G***del
//G***fix if(!"null".equals(xmlElement.getLocalName()))	//G***testing
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(attributeSet, DefaultStyledDocument.ElementSpec.StartTagType));	//create the beginning of a Swing element to model this XML element
		appendElementSpecListContent(elementSpecList, xmlElement, attributeSet, baseURI);	//append the content of the element
//G***fix if(!"null".equals(xmlElement.getLocalName()))	//G***testing
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(attributeSet, DefaultStyledDocument.ElementSpec.EndTagType));	//finish the element we started at the beginning of this function
		return attributeSet;  //return the attribute set used for the element
	}

	/**Appends the tree contents of an XML node (not including the element tag) into a list of element specs.
	@param elementSpecList The list of element specs to be inserted into the document.
	@param node The XML node tree, such as an element or a document fragment.
	@param attributeSet The attribute set of the element.
	@param baseURI The base URI of the document, used for generating full target
		URIs for quick searching.
	@exception BadLocationException for an invalid starting offset
	@see XMLDocument#insert
	@see XMLDocument#appendElementSpecList
	*/
	protected void appendElementSpecListContent(final List<DefaultStyledDocument.ElementSpec> elementSpecList, final Node node, final MutableAttributeSet attributeSet, final URI baseURI)
	{
		final NodeList childNodeList=node.getChildNodes();  //get the list of child nodes
		final int childNodeCount=childNodeList.getLength();	//see how many child nodes there are
		if(childNodeCount>0)	//if this element has children
		{
			for(int childIndex=0; childIndex<childNodeCount; childIndex++)	//look at each child node
			{
				final Node childNode=childNodeList.item(childIndex);	//look at this node
				appendElementSpecListNode(elementSpecList, childNode, baseURI);	//append this node's information
			}

/*G***fix; transferred elsewhere
//G***fix			assert node.getParentNode() instanceof org.w3c.dom.Element;	//G***fix
//G***fix			final org.w3c.dom.Element parentElement=(org.w3c.dom.Element)node.getParentNode();  //get the parent element
			final CSSStyleDeclaration cssStyle=xmlCSSStylesheetApplier.getStyle(xmlElement);
				//see if the element is inline (text is always inline
			final boolean isInline=XMLCSSUtilities.isDisplayInline(cssStyle);
			if(!isInline)
			{
				appendElementSpecListContent(elementSpecList, xmlElement, null, baseURI, "\n");	//G***testing
			}
*/
		}
		else	//if this element has no children, we'll have to add dummy text
		{
			final char dummyChar;	//we'll decide which character to use for the dummy text
			if(isEmptyElement(attributeSet))	//if this element should remain empty
			{
				XMLStyleUtilities.setXMLEmptyElement(attributeSet, true);	//show that this is an empty element G***see if this is the best way to do this and make sure this gets set for object added during editing
				dummyChar=CharacterConstants.OBJECT_REPLACEMENT_CHAR;	//use the object replacement character as dummy text, because there can never be real text added
			}
			else	//if this element might have text at some point
			{
				dummyChar='\n';	//use an EOL character TODO make sure this is a block element---this could probably really screw up an inline element with no content
			}
				//add a dummy replacment character so that this element will have some text to represent
			elementSpecList.add(new DefaultStyledDocument.ElementSpec(null, DefaultStyledDocument.ElementSpec.ContentType, new char[]{dummyChar}, 0, 1));
		}
	}

	/**Appends information from an XML child node into a list of element specs.
	@param elementSpecList The list of element specs to be inserted into the document.
	@param node The XML element's child node tree.
	@param baseURI The base URI of the document, used for generating full target
		URIs for quick searching, or <code>null</code> if there is no base URI or
		if the base URI is not applicable.
	@return The attribute set used to represent the node; this attribute set
		can be manipulated after the method returns.
	@exception BadLocationException for an invalid starting offset
	@see XMLDocument#insert
	@see XMLDocument#appendElementSpecListContent
	*/
	protected MutableAttributeSet appendElementSpecListNode(final List<DefaultStyledDocument.ElementSpec> elementSpecList, final org.w3c.dom.Node node, final URI baseURI)
	{
//G***del Debug.trace("appending element spec list node: ", node.getNodeName());  //G***del
		switch(node.getNodeType())	//see which type of object this is
		{
			case Node.ELEMENT_NODE:	//if this is an element
				return appendElementSpecList(elementSpecList, (org.w3c.dom.Element)node, baseURI);	//insert this element into our element spec list
			case Node.TEXT_NODE:	//if this is a text node
			case Node.CDATA_SECTION_NODE:	//if this is a CDATA section node
				{
						//G***see if this really slows things down
					final MutableAttributeSet textAttributeSet=createAttributeSet(node, baseURI);	//create and fill an attribute set
					appendElementSpecListContent(elementSpecList, node, textAttributeSet, baseURI, node.getNodeValue());	//append the content
					return textAttributeSet;	//return the attribute set of the text
				}
			default:	//TODO fix for inserting unknown nodes into the Swing document
				return new SimpleAttributeSet();	//create and return a new, empty attribute set 
		}
	}

	/**Appends child text into a list of element specs.
	@param elementSpecList The list of element specs to be inserted into the document.
	@param node The XML node that contains the content, or <code>null</code> if
		there is no node representing the text (text is being inserted manually).
	@param text The text to be inserted.
	@param attributeSet The attribute set representing the text, or
		<code>null</code> if default attributes should be used.
	@param baseURI The base URI of the document, used for generating full target
		URIs for quick searching, or <code>null</code> if there is no base URI or
		if the base URI is not applicable.
	@exception BadLocationException for an invalid starting offset
	@see XMLDocument#insert
	@see XMLDocument#appendElementSpecListContent
	*/
	protected void appendElementSpecListContent(final List<DefaultStyledDocument.ElementSpec> elementSpecList, final org.w3c.dom.Node node, final AttributeSet attributeSet, final URI baseURI, final String text)	//TODO remove the node parameter if not needed
	{
		final AttributeSet textAttributeSet;
		if(attributeSet!=null)	//if there are no attributes provided (artificial text is being manually inserted, for instance)
		{
			textAttributeSet=attributeSet;	//use the attribute set provided	
		}
		else	//if there are no attributes provided (artificial text is being manually inserted, for instance)
		{
			final SimpleAttributeSet simpleAttributeSet=new SimpleAttributeSet();	//create a new attribute for this content
			XMLStyleUtilities.setXMLElementName(simpleAttributeSet, XMLConstants.TEXT_NODE_NAME);	//set the name of the content to ensure it will not get its name from its parent element (this would happen if there was no name explicitely set)
			textAttributeSet=simpleAttributeSet;	//use the default atribute set we created
		}
//G***del Debug.trace("inserting text data: \""+node.getNodeValue()+"\"");  //G***del
		final StringBuffer textStringBuffer=new StringBuffer(text);  //G***testing
		if(textStringBuffer.length()>0) //if there is actually content (don't add empty text)
		{
//G***del Debug.trace("before collapsing whitespace: ", textStringBuffer);  //G***del



			StringBufferUtilities.collapse(textStringBuffer, CharacterConstants.WHITESPACE_CHARS, " ");	//G***testing
//G***del Debug.trace("after collapsing whitespace: ", textStringBuffer);  //G***del
//G***del Debug.trace("Adding text with attributes: ", contentAttributeSet);	//G***del
//G***fix textStringBuffer.append(CharacterConstants.WORD_JOINER_CHAR);	//G***testing
//G***fix textStringBuffer.append(CharacterConstants.ZERO_WIDTH_NO_BREAK_SPACE_CHAR);	//G***testing
//G***fix textStringBuffer.append(ELEMENT_END_CHAR);	//put a dummy character at the end of the element so that caret positioning will work correctly at the end of block views

//G***del	if(node.getParentNode()!=null && "null".equals(XMLStyleUtilities.getXMLElementLocalName(attributeSet.getResolveParent())))
/*G***fix
			if(node.getParentNode()!=null && "div".equals(node.getParentNode().getLocalName()))	//G***testing; fix
			{
textStringBuffer.append('\n');	//G***testing
				
			}
*/

if(node!=null && node.getParentNode() instanceof org.w3c.dom.Element)
{
	final org.w3c.dom.Element parentElement=(org.w3c.dom.Element)node.getParentNode();  //get the parent element
	if(parentElement.getChildNodes().item(parentElement.getChildNodes().getLength()-1)==node)	//if this is the last node
	{
			final CSSStyleDeclaration cssStyle=getXMLStylesheetApplier().getStyle(parentElement);
				//see if the element is inline (text is always inline
			final boolean isInline=XMLCSSUtilities.isDisplayInline(cssStyle);
			if(!isInline)
			{
				textStringBuffer.append('\n');	//G***testing
			}
	}
}



			final String content=textStringBuffer.toString();	//convert the string buffer to a string
			elementSpecList.add(new DefaultStyledDocument.ElementSpec(textAttributeSet, DefaultStyledDocument.ElementSpec.ContentType, content.toCharArray(), 0, content.length()));
		}
	}

	/**Appends a page break to the element spec list.
	@param elementSpecList The list of element specs to be inserted into the document.
	@see XMLDocument#insert
	@see XMLDocument#appendElementSpecList
	*/
	protected void appendElementSpecListPageBreak(final List<DefaultStyledDocument.ElementSpec> elementSpecList)
	{
//G***del Debug.trace("XMLDocument.appendElementSpecListPageBreak()");	//G***del
		final SimpleAttributeSet pageBreakAttributeSet=new SimpleAttributeSet();	//create a page break attribute set G***create this and keep it in the constructor for optimization
//G***del if we can get away with it		XMLStyleConstants.setXMLElementName(pageBreakAttributeSet, XMLCSSStyleConstants.AnonymousAttributeValue); //show by its name that this is an anonymous box G***maybe change this to setAnonymous
		XMLStyleUtilities.setPageBreakView(pageBreakAttributeSet, true);	//show that this element should be a page break view
		final XMLCSSStyleDeclaration cssStyle=new XMLCSSStyleDeclaration(); //create a new style declaration
		cssStyle.setDisplay(XMLCSSConstants.CSS_DISPLAY_BLOCK);	//show that the page break element should be a block element, so no anonymous blocks will be created around it
		XMLCSSStyleUtilities.setXMLCSSStyle(pageBreakAttributeSet, cssStyle);	//store the constructed CSS style in the attribute set
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(pageBreakAttributeSet, DefaultStyledDocument.ElementSpec.StartTagType));	//create the beginning of a page break element spec
//G***fix		final SimpleAttributeSet contentAttributeSet=new SimpleAttributeSet();	//create a new attribute for this content
			//add a dummy object replacment character so that this element will have some text to represent
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(null, DefaultStyledDocument.ElementSpec.ContentType, new char[]{CharacterConstants.OBJECT_REPLACEMENT_CHAR}, 0, 1));
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(pageBreakAttributeSet, DefaultStyledDocument.ElementSpec.EndTagType));	//finish the page break element spec
	}

	/**Creates an attribute set for the described element.
	@param elementNamespaceURI The namespace of the XML element, or <code>null</code> if the namespace is not known.
	@param elementQName The qualified name of the XML element.
	@return An attribute set reflecting the CSS attributes of the element.
	*/
	public static MutableAttributeSet createAttributeSet(final URI elementNamespaceURI, final String elementQName)
	{
		return createAttributeSet(elementNamespaceURI, elementQName, null);  //create an attribute set with no style
	}

	/**Creates an attribute set for the described element.
	@param elementNamespaceURI The namespace of the XML element, or <code>null</code> if the namespace is not known.
	@param elementQName The qualified name of the XML element.
	@param style The CSS style to be used for the attribute set, or <code>null</code> if the CSS style is not known.
	@return An attribute set reflecting the CSS attributes of the element.
	*/
	public static MutableAttributeSet createAttributeSet(final URI elementNamespaceURI, final String elementQName, final CSSStyleDeclaration style)
	{
		final SimpleAttributeSet attributeSet=new SimpleAttributeSet();	//create a new attribute for this element
		XMLStyleUtilities.setXMLElementName(attributeSet, elementQName);	//store the element's name in the attribute set
		if(elementNamespaceURI!=null)  //if the element has a namespace URI specified
			XMLStyleUtilities.setXMLElementNamespaceURI(attributeSet, elementNamespaceURI.toString());	//store the element's namespace URI in the attribute set
		final String localName=XMLUtilities.getLocalName(elementQName);  //get the element's local name from the qualified name
		XMLStyleUtilities.setXMLElementLocalName(attributeSet, localName);	//store the element's local name in the attribute set
		if(style!=null) //if style was given G***should we instead do this unconditionally?
			XMLCSSStyleUtilities.setXMLCSSStyle(attributeSet, style);	//store the CSS style in the attribute set
		return attributeSet;	//return the attribute set we created
	}

	/**Creates an attribute set for the given XML node.
	@param node The XML node, such as an element or text.
	@param baseURI The base URI of the document, used for generating full target
		URIs for quick searching, or <code>null</code> if there is no base URI or
		if the base URI is not applicable.
	@return An attribute set reflecting the CSS attributes of the element.
	*/
	protected MutableAttributeSet createAttributeSet(final Node xmlNode, final URI baseURI)
	{
		final String namespaceURI=xmlNode.getNamespaceURI();  //get the node namespace URI
		final MutableAttributeSet attributeSet=createAttributeSet(namespaceURI!=null ? URI.create(namespaceURI) : null, xmlNode.getNodeName());	//create a new attribute for this node
		//G***give every attribute set a default empty CSS style; later fix this in the application section to create as needed and to clear them before application
//G***del when moved to the set-style routines		XMLCSSStyleConstants.setXMLCSSStyle(attributeSet, new XMLCSSStyleDeclaration());	//give every attribute set a default empty CSS style
		switch(xmlNode.getNodeType())	//see what type of node for which to create an attribute set
		{
			case Node.ELEMENT_NODE: //if this node is an element
				{
					final org.w3c.dom.Element xmlElement=(org.w3c.dom.Element)xmlNode;  //cast the node to an element
					final CSSStyleDeclaration cssStyle=getXMLStylesheetApplier().getStyle(xmlElement);	//see if we've already applied a style to this element
					if(cssStyle!=null)	//if we know the style of the XML element
					{
						XMLCSSStyleUtilities.setXMLCSSStyle(attributeSet, cssStyle);	//store the style in the attributes of our Swing element	
					}
					else
					{
						//give every attribute set a default empty CSS style; if not, this will cause huge performance hits when trying to create them on the fly when styles are applied TODO recheck
						XMLCSSStyleUtilities.setXMLCSSStyle(attributeSet, new XMLCSSStyleDeclaration());
					}
					final NamedNodeMap attributeNodeMap=xmlElement.getAttributes(); //get a reference to the attributes
					//store the XML attributes
					for(int attributeIndex=0; attributeIndex<attributeNodeMap.getLength(); ++attributeIndex)	//look at each of the attributes
					{
						final Attr xmlAttribute=(Attr)attributeNodeMap.item(attributeIndex);	//get a reference to this attribute
							//add this XML attribute to the Swing atribute set as the value of our special XML attribute key
						XMLStyleUtilities.addXMLAttribute(attributeSet, xmlAttribute.getNamespaceURI(), xmlAttribute.getNodeName(), xmlAttribute.getNodeValue());
					}
					final String targetID=getTargetID(attributeSet);  //get the target ID specified in the attribute set
					if(targetID!=null)  //if this attribute set has a target ID
					{
						try
						{
							final URI targetURI=URIUtilities.resolveFragment(baseURI, targetID);	//create a full URI from the target ID used as a fragment
							XMLStyleUtilities.setTargetURI(attributeSet, targetURI);  //store the target URI for quick searching
						}
						catch(IllegalArgumentException illegalArgumentException) {} //ignore any errors and simply don't store the target URL
					}
				}
				break;
			case Node.TEXT_NODE:	//if this is a text node
			case Node.CDATA_SECTION_NODE:	//if this is a CDATA section node
				break;	//do nothing---the node is already set up
		}
		return attributeSet;	//return the attribute set we created
	}

		//document information retrieval methods

	/**Converts the Swing document to an XML document.
	<p>This is a cover method for <code>createXMLDocument</code>.</p>
	@param swingXMLDocument The Swing document from which the XML will be retrieved.
	@return A DOM tree representing the XML document.
	@see #createXMLDocument
	*/
	public org.w3c.dom.Document getXML(final XMLDocument swingXMLDocument)
	{
		return createXMLDocument(swingXMLDocument);  //create an XML document from the Swing document
	}

	/**Converts the Swing document to an XML document.
	@param swingXMLDocument The Swing document from which the XML will be retrieved.
	@return A DOM tree representing the XML document.
	*/
	protected org.w3c.dom.Document createXMLDocument(final XMLDocument swingXMLDocument)
	{
		final Element rootSwingElement=swingXMLDocument.getRootElements()[0]; //get the first root element of the document -- this contains an element tree for each document loaded
		assert rootSwingElement.getElementCount()>0 : "No Swing root element.";  //assert there is at least one root element
//G***del		if(rootSwingElement.getElementCount()>0)  //if there is at least one root element
		final Element swingDocumentElement=rootSwingElement.getElement(0);  //get the first element, which is the root of the document tree
		return createXMLDocument(swingDocumentElement); //create and return a document from this element
	}

	/**Converts the given Swing element tree to an XML document.
	@param swingElement The Swing element containing the data to be converted to
		an XML document.
	@return A DOM tree representing the XML document.
	*/
	protected org.w3c.dom.Document createXMLDocument(final Element swingElement)
	{
		final AttributeSet attributeSet=swingElement.getAttributes();  //get the element's attribute set
		assert attributeSet!=null : "Missing attributes for document element.";  //assert that we have an attribute set
		final String elementName=XMLStyleUtilities.getXMLElementName(attributeSet); //get the name of this element
		final XMLDOMImplementation domImplementation=new XMLDOMImplementation();	//create a new DOM implementation G***later use some Java-specific stuff
		final DocumentType documentType;  //we'll create a document type only if we find a system ID
		final String docTypeSystemID=XMLStyleUtilities.getXMLDocTypeSystemID(attributeSet); //get the document type system ID if there is one
		if(docTypeSystemID!=null) //if we found a system ID
		{
			final String docTypePublicID=XMLStyleUtilities.getXMLDocTypePublicID(attributeSet); //get the document type public ID if there is one
			documentType=domImplementation.createDocumentType(elementName, docTypePublicID, docTypeSystemID);	//create the document type
/*G***fix some day to load the entities and use them in serialization			
					//load the contents of the document type, if we can
			final XMLProcessor xmlProcessor=new XMLProcessor();	//create a new XML processor TODO see that it gets the URIInputStreamable that was used to load this document in the first place---is that stored in the document or Swing element, perhaps? it should be
				//get a reader from the XML processor to read the external document type
			final XMLReader documentTypeReader=xmlProcessor.createReader(XMLStyleUtilities.getBaseURI(attributeSet), documentType.getPublicId(), documentType.getSystemId());
			try
			{
				parseDocumentTypeContent(reader, ownerDocument, documentType.getEntities(), documentType.getParameterEntityXMLNamedNodeMap(), elementDeclarationList, attributeListDeclarationList);
			}
			finally
			{
				documentTypeReader.close();	//always close the document type reader
			}	
*/		
		}
		else  //if there was no system ID
			documentType=null;  //show that we don't have a document type
		final org.w3c.dom.Document xmlDocument=domImplementation.createDocument(null, elementName, documentType);	//create the document
			//create any processing instructions
		final NameValuePair[] processingInstructions=XMLStyleUtilities.getXMLProcessingInstructions(attributeSet);  //get the processing instructions, if any (this will never return null)
			//look at each processing instruction
		for(int processingInstructionIndex=0; processingInstructionIndex<processingInstructions.length; ++processingInstructionIndex)
		{
			final NameValuePair processingInstructionNameValuePair=processingInstructions[processingInstructionIndex];  //get this processing instruction's values
				//create a processing instruction with the correct value
			final ProcessingInstruction processingInstruction=xmlDocument.createProcessingInstruction((String)processingInstructionNameValuePair.getName(), (String)processingInstructionNameValuePair.getValue());
			xmlDocument.insertBefore(processingInstruction, xmlDocument.getDocumentElement()); //add this processing instruction G***do these have to be placed in a certain order---before the document element?
		}
		final org.w3c.dom.Node xmlNode=createXMLNode(xmlDocument, swingElement); //create the root element
		assert xmlNode.getNodeType()==Node.ELEMENT_NODE : "Swing root XML node not an XML element."; //make sure we got back an XML element
		xmlDocument.replaceChild(xmlNode, xmlDocument.getDocumentElement());	//set the document element of the document
		return xmlDocument; //return the document we constructed
	}

	/**Converts the given Swing element to an XML node.
	@param swingElement The Swing element containing the data to be converted to
		an XML node.
	@return A DOM element representing the Swing node.
	*/
	protected org.w3c.dom.Node createXMLNode(final org.w3c.dom.Document xmlDocument, final Element swingElement)
	{
		return createXMLNode(xmlDocument, swingElement, 0);	//create an XML node at the bottom level
	}

	/**Converts the given Swing element to an XML node indenting to the given level.
	@param swingElement The Swing element containing the data to be converted to
		an XML node.
	@param level The zero-based level of indentation.
	@return A DOM element representing the Swing node.
	*/
	protected org.w3c.dom.Node createXMLNode(final org.w3c.dom.Document xmlDocument, final Element swingElement, final int level)
	{
		final AttributeSet attributeSet=swingElement.getAttributes();  //get the element's attribute set
		final String elementKind=swingElement.getName();	//get the kind of element this is (based on the name of the Swing element, not the Swing element's attribute which holds the name of its corresponding XML element)
		if(elementKind!=null) //if the element has a kind
		{
			if(elementKind.equals(AbstractDocument.ContentElementName))	//if this is is content
			{
				try
				{
						//get the text this content Swing element represents
					final StringBuffer stringBuffer=new StringBuffer(swingElement.getDocument().getText(swingElement.getStartOffset(), swingElement.getEndOffset()-swingElement.getStartOffset()));
							//remove every instance of the artificial end-of-block-element character, as well as any hard return that the user might have entered during editing
					StringBufferUtilities.removeEveryChar(stringBuffer, XMLDocument.ELEMENT_END_STRING+'\n');
					return xmlDocument.createTextNode(stringBuffer.toString()); //create a text node with the content and return the node
				}
				catch(BadLocationException badLocationException)  //in the unlikely event that we try to access a bad location
				{
					throw new AssertionError(badLocationException);  //report an error
				}
			}
		}
		assert attributeSet!=null : "Missing attributes for element.";  //assert that we have an attribute set
//G***fix		if(attributeSet!=null)  //if we have an attribute set
		final String elementNamespaceURI=XMLStyleUtilities.getXMLElementNamespaceURI(attributeSet); //get the namespace of this element, if it has one
		final String elementName=XMLStyleUtilities.getXMLElementName(attributeSet); //get the name of this element
		final org.w3c.dom.Element xmlElement=xmlDocument.createElementNS(elementNamespaceURI, elementName);	//create the element
		if(!isEmptyElement(attributeSet))  //if this element isn't an empty element, we'll add children
		{
			boolean hasBlockChild=false;	//we'll see if any of the children have block display; start out assuming they don't
//G***del when works			boolean isInlineChild=true; //each time we'll determine whether this is an inline node so that we can add EOLs for pretty printing if not; for now, assume it is inline
				//create and append the child elements
			for(int childIndex=0; childIndex<swingElement.getElementCount(); ++childIndex)  //look at each of the child elements
			{
				final Element childSwingElement=swingElement.getElement(childIndex); //get this Swing child element
				final org.w3c.dom.Node childXMLNode=createXMLNode(xmlDocument, childSwingElement, level+1); //create an XML node from the child Swing element, specifying that this node will be at the next hierarchy level
				boolean isInlineChild=true; //start by assuming this is an inline child
//G***del when works				final boolean isInlineChild; //we'll determine whether this is an inline node so that we can add EOLs for pretty prining if not
				if(childXMLNode.getNodeType()==Node.ELEMENT_NODE) //if this is an element
				{
						//get the display CSS property for the child element, but don't resolve up the attribute set parent hierarchy G***can we be sure this will be a primitive value?
					final CSSPrimitiveValue cssDisplayProperty=(CSSPrimitiveValue)XMLCSSStyleUtilities.getCSSPropertyCSSValue(childSwingElement.getAttributes(), XMLCSSConstants.CSS_PROP_DISPLAY, false);
					isInlineChild=cssDisplayProperty!=null ? //if the child element knows its CSS display
						XMLCSSConstants.CSS_DISPLAY_INLINE.equals(cssDisplayProperty.getStringValue()) :  //see if the display is "inline"
						true;  //if there is no display, assume it is inline
				}
/*G***del when works
				else  //if this Swing element doesn't represent an XML element
					isInlineChild=true;  //we'll still consider it to be "inline" (it might be just textual content, after all)
*/
				if(!isInlineChild)  //if the child element is not inline
				{
					hasBlockChild=true;	//show that at least one child has block display
					XMLUtilities.appendText(xmlElement, "\n");  //skip to the next line for a pretty formatted XML document
					XMLUtilities.appendText(xmlElement, StringUtilities.createString('\t', level+1));	//indent to the correct level
				}
				xmlElement.appendChild(childXMLNode);  //append the XML node we created
	/*G***del if not needed
				if(!isInlineChild)  //if the child element is not inline
					XMLUtilities.appendText(xmlElement, "\n");  //skip to the next line for a pretty formatted XML document
	*/
			}
//*G**del when works			if(!isInlineChild)  //if the last child element was not inline
			if(hasBlockChild)  //if any of the children were not inline
			{
				XMLUtilities.appendText(xmlElement, "\n");  //skip to the next line for a pretty formatted XML document
				XMLUtilities.appendText(xmlElement, StringUtilities.createString('\t', level));	//indent to the correct level
			}
		}


			//store the attributes
		final Enumeration attributeNameEnumeration=attributeSet.getAttributeNames();  //get an enumeration of attribute names
		while(attributeNameEnumeration.hasMoreElements()) //while there are more attributes
		{
			final Object attributeNameObject=attributeNameEnumeration.nextElement();  //get this attribute name object
/*G***del; why is there a "resolver" attribute with a name of type StyleConstants? Why isn't that a value?
Debug.trace("Current element: ", attributeNameObject); //G***del
Debug.trace("Current element type: ", attributeNameObject.getClass().getName()); //G***del
*/
			final Object attributeValueObject=attributeSet.getAttribute(attributeNameObject);	//get the attribute value (don't worry that this searches the hierarchy---we already know this key exists at this level)
			if(attributeValueObject instanceof XMLAttribute)	//if this Swing attribute is an XML attribute 
			{
				final XMLAttribute xmlAttribute=(XMLAttribute)attributeValueObject;	//cast the object to an XML attribute
					//set the attribute value in the XML element we're constructing
				xmlElement.setAttributeNS(xmlAttribute.getNamespaceURI(), xmlAttribute.getQName(), xmlAttribute.getValue());
			}
/*G***del when works
			if(attributeNameObject instanceof String) //if this attribute name is a string
			{
				final String attributeName=(String)attributeNameObject;  //get this attribute name as a string
				if(XMLUtilities.isName(attributeName))  //if this is a valid XML name (this will ignore all proprietary Swing attributes
				{
					final Object attributeValue=attributeSet.getAttribute(attributeName);  //get the value of the attribute, which should be a string
					Debug.assert(attributeValue instanceof String, "XML attribute is not a string.");
					xmlElement.setAttributeNS(null, attributeName, attributeValue.toString());  //set the attribute value G***fix for namespaces
				}
			}
*/
		}
		return xmlElement;  //return the element we created
	}


	/**Gets the target ID of of the specified element. This ID represents the
		target of a link. By default this is the value of the "id" attribute. G***what about checking the DTD for an element of type ID?
	@param attributeSet The attribute set of the element which may contain a
		target ID.
	@return The target ID value of the element, or <code>null</code> if the
		element does not define a target ID.
	*/
	protected String getTargetID(final AttributeSet attributeSet)  //G***can any of this be made into a generic XML utility, using the DTD ID type?
	{
		return XMLStyleUtilities.getXMLAttributeValue(attributeSet, null, "id");  //return the value of the "id" attribute, if it exists G***use a constant here
	}

	/**Determines if the specified element represents an empty element&mdash;an
		element that might be declared as <code>EMPTY</code> in a DTD.
	@param attributeSet The attribute set of the element in question.
	@return <code>true</code> if the specified element should be empty.
	*/
	protected boolean isEmptyElement(final AttributeSet attributeSet)
	{
		return false;	//default to no empty elements G***it would be nice to get this from the DTD
	}

	/**A factory to build views for an XML document based upon the attributes of
		each element.
		<p>This XML view factory adds a special capability of defining view factories
		for specific namespaces. If an XML element is encountered in a particular
		namespace and a view factory has been registered for that namespace, the
		registered view factory will be used to create the view. Otherwise, this
		view factory will create a view.</p>
		<p>As this factory allows the capability of creating multiple views for
		certain elements, child classes should override
		<code>create(Element, boolean)</code> rather than overriding the normal
		<code>create(Element)</code> method.
	*/
	protected class DefaultXMLViewFactory extends XMLViewFactory
	{

		/**Retreives a view factory for the given namespace.
			This version retrieves any view factory registered in the editor kit for
			the given namespace.
		@param namespaceURI The namespace URI of the element for which a view factory
			should be returned, or <code>null</code> if the element has not namespace.
		@return A view factory for the given namespace, or this view factory
			if no view factory is registered for the given namespace.
		*/
		protected ViewFactory getViewFactory(final String namespaceURI)
		{
			final ViewFactory viewFactory=XMLEditorKit.this.getViewFactory(namespaceURI);  //see if there is a view factory registered for this namespace in the editor kit
		  return viewFactory!=null ? viewFactory : super.getViewFactory(namespaceURI);  //return the view factory if there is one; if not, let the parent class decide on a view factory
		}
	}


	/**A link controller that knows how to handle link entry, exit, and activiation.
		<p>This XML link controller uses the locally registered link controllers
		to determine the appropriate link controller to use for a particular element.</p>
	*/
	protected class DefaultXMLLinkController extends XMLLinkController
	{

		/**Retreives a link controller for the given namespace.
			This version retrieves any link controller registered in the editor kit for
			the given namespace.
		@param namespaceURI The namespace URI of the element for which a link
			controller should be returned, or <code>null</code> if the element has no
			namespace.
		@return A view factory for the given namespace.
		*/
		protected XMLLinkController getLinkController(final String namespaceURI)
		{
			final XMLLinkController linkController=XMLEditorKit.this.getLinkController(namespaceURI);  //see if there is a link controller registered for this namespace in the editor kit
		  return linkController!=null ? linkController : super.getLinkController(namespaceURI);  //return the link controller if there is one; if not, let the parent class decide on a link controller
		}
	}

	/**Data to be inserted into the Swing document, such as an XML document or a MAQRO activity.
	@author Garret Wilson
	*/
	public static class ContentData<O>
	{
		/**The content object.*/
		private final O object;

			/**@return The content object.*/
			public O getObject() {return object;}

		/**The base URI of the object, or <code>null</code> if no base URI is available..*/
		private final URI baseURI;

			/**@return The base URI of the object, or <code>null</code> if no base URI is available.*/
			public URI getBaseURI() {return baseURI;}

		/**The content type of the object.*/
		private final ContentType contentType;

			/**@return The content type of the object.*/
			public ContentType getContentType() {return contentType;}

		/**A description of the object, or <code>null</code> if no description is available.*/
		private final RDFResource description;

			/**@return A description of the object, or <code>null</code> if no description is available.*/
			public RDFResource getDescription() {return description;}

		/**Object and baseURI constructor
		@param object The content object.
		@param baseURI The base URI of the object, or <code>null</code> if no base URI is available..
		@param contentType The content type of the object.
		@exception NullPointerException if the object or content type is <code>null</code>.
		*/
		public ContentData(final O object, final URI baseURI, final ContentType contentType)
		{
			this(object, baseURI, contentType, null);	//construct the data with no description
		}

		/**Object, baseURI, and description constructor
		@param object The content object.
		@param baseURI The base URI of the object, or <code>null</code> if no base URI is available..
		@param contentType The content type of the object.
		@param description A description of the object, or <code>null</code> if no description is available.
		@exception NullPointerException if the object or content type is <code>null</code>.
		*/
		public ContentData(final O object, final URI baseURI, final ContentType contentType, final RDFResource description)
		{
			this.object=checkNull(object, "Object cannot be null");
			this.baseURI=baseURI;
//TODO del when works			this.baseURI=checkNull(baseURI, "Base URI cannot be null");
			this.contentType=checkNull(contentType, "Content type cannot be null");
			this.description=description;
		}
	}

	/**An XML document to be inserted into the Swing document.
	@author Garret Wilson
	*/
/*G***del if not needed
	protected static class XMLDocumentData extends ContentData<org.w3c.dom.Document>
	{
*/
		/**Document and baseURI constructor
		@param document The content document.
		@param baseURI The base URI of the object.
		@exception NullPointerException if the object or base URI is <code>null</code>.
		*/
/*G***del if not needed
		public XMLDocumentData(final org.w3c.dom.Document document, final URI baseURI)
		{
			this(document, baseURI, null);	//construct the data with no description
		}
*/

		/**Document, baseURI, and description constructor
		@param document The content document.
		@param baseURI The base URI of the object.
		@param description A description of the object, or <code>null</code> if no description is available.
		@exception NullPointerException if the object or base URI is <code>null</code>.
		*/
/*G***del if not needed
		public XMLDocumentData(final org.w3c.dom.Document document, final URI baseURI, final RDFResource description)
		{
			super(document, baseURI, description);	//construct the parent class
		}
	}
*/

}

package com.garretwilson.swing.text.xml;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.net.URI;
import java.util.*;
import java.util.List;
import javax.mail.internet.ContentType;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.Document;
import javax.swing.text.Element;
import com.garretwilson.io.*;
import com.garretwilson.lang.*;
import com.garretwilson.net.*;
import com.garretwilson.rdf.*;
import com.garretwilson.sun.demo.jfc.notepad.ElementTreePanel;
import com.garretwilson.swing.*;
import com.garretwilson.swing.event.*;
import com.garretwilson.swing.text.SwingTextUtilities;
import com.garretwilson.swing.text.ViewUtilities;
import com.garretwilson.swing.text.xml.css.*;
import com.garretwilson.text.*;
import com.garretwilson.text.xml.*;
import com.garretwilson.text.xml.oeb.*;	//G***del
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
public class XMLEditorKit extends StyledEditorKit
{

	/**The XML media type this editor kit supports, defaulting to <code>text/xml</code>.*/
	private ContentType mediaType=new ContentType(ContentTypeConstants.TEXT, ContentTypeConstants.XML_SUBTYPE, null);

		/**@return The XML media type this editor kit supports.*/
		public ContentType getMediaType() {return mediaType;}

		/**Sets the media type this editor kit supports.
		@param newMediaType The new XML media type.
		*/
		protected void setMediaType(final ContentType newMediaType) {mediaType=newMediaType;}

	/**The default default cursor.*/
	private static final Cursor DEFAULT_CURSOR=Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

	/**The default cursor to be used when moving items.*/
	private static final Cursor DEFAULT_MOVE_CURSOR=Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

	/**The default cursor to be used when over links.*/
	private static final Cursor DEFAULT_LINK_CURSOR=DEFAULT_MOVE_CURSOR;

	/**The default cursor.*/
	private Cursor defaultCursor=DEFAULT_CURSOR;

		/**@return The default cursor.*/
		public Cursor getDefaultCursor() {return defaultCursor;}

		/**Sets the default cursor.
		@param newDefaultCursor The cursor to use as the default.
		*/
		public void setDefaultCursor(final Cursor newDefaultCursor) {defaultCursor=newDefaultCursor;}

	/**The default cursor to show when over hyperlinks.*/
	private Cursor defaultLinkCursor=DEFAULT_LINK_CURSOR;

		/**@return The default cursor to display when the mouse is over a link.*/
		public Cursor getDefaultLinkCursor() {return defaultLinkCursor;}

		/**Sets the default cursor used for hyperlink.
		@param newDefaultCursor The cursor to display by default when the mouse is over a link.
		*/
		public void setDefaultLinkCursor(final Cursor newLinkCursor) {defaultLinkCursor=newLinkCursor;}

		/**Determines the cursor to use when over a given link in a particular document.
		@param xmlDocument The document in which the link appears.
		@param uri The link for which a cursor should be obtained.
		@return The cursor to display when the mouse is over the given link.
		*/
		public Cursor getLinkCursor(final XMLDocument xmlDocument, final URI uri)
		{
			return getDefaultLinkCursor();	//TODO load a custom cursor based upon the link's media type
		}

	/**The list of progress event listeners.*/
	private EventListenerList progressListenerList=new EventListenerList();

	/**The default view factory for an XML editor kit.*/
	private final ViewFactory defaultViewFactory=new DefaultXMLViewFactory();

	/**The default link controller for an XML editor kit.*/
	private final XMLLinkController defaultLinkController=new DefaultXMLLinkController();

	/**A map of view factories, each keyed to a namespace URI string.*/
	private Map namespaceViewFactoryMap=new HashMap();

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
				return (ViewFactory)namespaceViewFactoryMap.get(namespaceURI); //return a view factory for the given namespace, if one has been registered
			}

			/**Removes all registered view factories.*/
			public void unregisterViewFactories()
			{
				namespaceViewFactoryMap.clear();  //clear all registered view factoriees
			}

	/**A map of link controllers, each keyed to a namespace URI string.*/
	private Map namespaceLinkControllerMap=new HashMap();

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
				return (XMLLinkController)namespaceLinkControllerMap.get(namespaceURI); //return a link controller for the given namespace, if one has been registered
			}

			/**Removes all registered link controllers.*/
			public void unregisterLinkControllers()
			{
				namespaceLinkControllerMap.clear();  //clear all registered link controllers
			}

	/**The identifier for the previous page action.*/
	public static final String PREVIOUS_PAGE_ACTION_NAME="previous-page-action";

	/**The identifier for the next page action.*/
	public static final String NEXT_PAGE_ACTION_NAME="next-page-action";

	/**The identifier for the action to display the element tree.*/
	public static final String DISPLAY_ELEMENT_TREE_ACTION_NAME="display-element-tree-action";

	/**Default actions used by this editor kit to augment the super class default
		actions.
	*/
	private static final Action[] DEFAULT_ACTIONS=
	{
		new PreviousPageAction(PREVIOUS_PAGE_ACTION_NAME),
		new NextPageAction(NEXT_PAGE_ACTION_NAME),
		new DisplayElementTreeAction(DISPLAY_ELEMENT_TREE_ACTION_NAME),
		new BeginAction(beginAction, false),
		new EndAction(endAction, false)
//G***del		new EndLineAction(endLineAction, false)	//G***testing
	};

	/**Default constructor.*/
	public XMLEditorKit() {}

	/**Constructor that specifies the specific XML media type supported.
	@param mediaType The XML media type supported. In some instances, such as
		<code>text/html</code>, this indicates a default namespace even in the
		absence of a document namespace identfication.
	*/
	public XMLEditorKit(final ContentType mediaType)
	{
		setMediaType(mediaType);  //set the requested media type
	}

	/**Creates a copy of the editor kit.
	@return A copy of the XML editor kit.
	*/
	public Object clone() {return new XMLEditorKit(getMediaType());}  //G***why do we need this?

	/**Returns the MIME type of the data the XML editor kit supports, such as
		<code>text/xml</code>.
	@return The MIME type this editor kit supports.
	*/
	public String getContentType() {return getMediaType().toString();}

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

		/**
		 * Create an uninitialized text storage model
		 * that is appropriate for this type of editor.
		 *
		 * @return the model
		 */
//G***fix
	public Document createDefaultDocument()
	{
//G***del Debug.traceStack("Creating default XML document");  //G***del
		return new com.garretwilson.swing.text.xml.XMLDocument();	//G***fix, comment
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
//G***remove this method as soon as we can
	protected static void tidyOEBXMLDocument(final com.garretwilson.text.xml.XMLDocument xmlDocument)  //G***eventually remove this if we can
	{
		final com.garretwilson.text.xml.XMLElement xmlRoot=xmlDocument.getDocumentXMLElement();	//get the root of the document
		for(int i=0; i<xmlRoot.getChildNodes().getLength(); ++i)	//look at each of the first-level child nodes
		{
			final com.garretwilson.text.xml.XMLNode xmlNode=(com.garretwilson.text.xml.XMLNode)xmlRoot.getChildNodes().item(i);	//get a reference to this node
			if(xmlNode.getNodeName().equals(OEBConstants.ELEMENT_BODY) || xmlNode.getNodeName().equals(OEBConstants.ELEMENT_HTML))	//if this is the body element G***testing HTML
			{
				final com.garretwilson.text.xml.XMLElement bodyElement=(com.garretwilson.text.xml.XMLElement)xmlNode;	//get a reference to the body element
//G***del Debug.trace("Found body element.");
				com.garretwilson.text.xml.XMLNode childNode=(com.garretwilson.text.xml.XMLNode)bodyElement.getFirstChild();	//get the first child
				while(childNode!=null)	//while the body has child nodes
				{
//G***del System.out.println("Looking at node: "+childNode.getNodeName());
					com.garretwilson.text.xml.XMLNode nextNode=(com.garretwilson.text.xml.XMLNode)childNode.getNextSibling();	//get a reference to the next sibling so we'll have it when we need it
					if(childNode.getNodeType()==com.garretwilson.text.xml.XMLNode.TEXT_NODE)	//if this is a text node
					{
//G***del System.out.println("This is a text node");
						if(((com.garretwilson.text.xml.XMLText)childNode).getData().trim().length()==0)	//if this text node has only whitespace
						{
//G***del System.out.println("The length of text is zero.");
							childNode.getParentNode().removeChild(childNode);	//remove the text child from the list
						}
					}
//G***del System.out.println("Going to next child.");
					childNode=nextNode;	//look at the next node
				}
			}
			else
			{
//G***fix				final boolean isHTMLDocument=XHTMLSwingTextUtilities.isHTMLDocumentElement(documentAttributeSet);	//see if this is an HTML document
				
				
			}
		}

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
			tidyOEBXMLDocument((com.garretwilson.text.xml.XMLDocument)xmlDocument);	//tidy up the document (an important step if the document has text directly in the body and such) G***test, comment
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
/*G***del when works
		if(doc instanceof com.garretwilson.swing.text.xml.XMLDocument)	//make sure this is a document we know how to work with
		{
//G***fix			final XMLProcessor xmlProcessor=new XMLProcessor(in);	//create an XML processor
			try
			{

				final com.garretwilson.text.xml.XMLDocument xmlDocument=xmlProcessor.parseDocument();	//parse the document
System.out.println("Adding the default OEB stylesheet.");	//G***del
				xmlDocument.getStyleSheetList().add(new DefaultOEBCSSStyleSheet());	//G***put this somewhere else; perhaps in an OEBEditorKit
						//G***do a normalize() somewhere here
System.out.println("Ready to parse stylesheets.");	//G***del
				final XMLCSSProcessor cssProcessor=new XMLCSSProcessor();	//G***testing
				cssProcessor.parseStyles(xmlDocument);	//G***testing
				cssProcessor.applyxStyles(xmlDocument);	//G***testing

				final XMLElement xmlRoot=xmlDocument.getDocumentXMLElement();	//get the root of the document G***change to DOM
//G***del			xmlRoot.dump();	//G***check, comment
				System.out.println("Finished with file.");	//G***del

				final XMLNodeList elementList=(XMLNodeList)xmlDocument.getElementsByTagName("*");	//G***testing; use a constant here
				for(int i=0; i<elementList.size(); ++i)	//G***testing
				{
					final XMLElement element=(XMLElement)elementList.get(i);	//get the element at this index
					if(element.getNodeName().equals("body"))	//G***testing
					{
						((com.garretwilson.swing.text.xml.XMLDocument)doc).create(element);
						return;
					}
				}
				((com.garretwilson.swing.text.xml.XMLDocument)doc).create(xmlDocument);
			}
			catch(Exception ex)
			{
				System.out.println(ex.getMessage());
			}	//G***fix
		}
		else	//if this isn't an XML document
		{
			super.read(in, doc, pos);	//let our parent read the document
		}
*/
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
		  tidyOEBXMLDocument((com.garretwilson.text.xml.XMLDocument)xmlDocument);	//tidy up the document (an important step if the document has text directly in the body and such) G***test, comment
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
	public void write(final Writer writer, final Document document, final int pos, final int len) throws IOException, BadLocationException
	{
/*G***fix when XMLSerializer supports writers
		if(document instanceof XMLDocument) //if the document is an XML document
		{

		final com.garretwilson.text.xml.XMLSerializer xmlSerializer=new com.garretwilson.text.xml.XMLSerializer(true);  //create an XML serializer G***use local package name after we move
		try
		{
			xmlSerializer.serialize(document, System.out);  //G***testing
		}
		catch(Exception e)
		{
			Debug.error(e); //G***fix
		}

			HTMLWriter w = new HTMLWriter(out, (HTMLDocument)doc, pos, len);
	    w.write();
		}
		else  //if the document is not an XML document
	} else if (doc instanceof StyledDocument) {
	    MinimalHTMLWriter w = new MinimalHTMLWriter(out, (StyledDocument)doc, pos, len);
	    w.write();
	} else {
	    super.write(out, doc, pos, len);
	}
*/
		}

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
			final Iterator viewFactoryNamespaceIterator=xmlTextPane.getViewFactoryNamespaceIterator(); //get an iterator to all namespaces of intalled editor kits
			while(viewFactoryNamespaceIterator.hasNext())  //while there are more namespaces
			{
				final String namespaceURI=(String)viewFactoryNamespaceIterator.next(); //get the next namespace for which a view factory is installed
//G***del Debug.trace("setting view factory registered for namespace: ", namespaceURI); //G***del
				final ViewFactory registeredViewFactory=xmlTextPane.getViewFactory(namespaceURI); //get the view factory associated with this namespace
				registerViewFactory(namespaceURI, registeredViewFactory);  //register this view factory with the the namespace
			}
				//get all registered link controllers from the XML text pane
			final Iterator linkControllerNamespaceIterator=xmlTextPane.getLinkControllerNamespaceIterator(); //get an iterator to all namespaces of intalled link controllers
			while(linkControllerNamespaceIterator.hasNext())  //while there are more namespaces
			{
				final String namespaceURI=(String)linkControllerNamespaceIterator.next(); //get the next namespace for which a link controller is installed
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

		/**
		 * Inserts HTML into an existing document.
		 *
		 * @param doc Document to insert into.
		 * @param offset offset to insert HTML at
		 * @param popDepth number of DefaultStyledDocument.ElementSpec.EndTagType to generate before
		 *        inserting.
		 * @param pushDepth number of DefaultStyledDocument.ElementSpec.StartTagType with a direction
		 *        of DefaultStyledDocument.ElementSpec.JoinNextDirection that should be generated
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

	/**Fetches the command list for the editor. This is the list of commands
		supported by the superclass augmented by the collection of commands defined
		locally for such things as page operations.
	@return The command list
	*/
	public Action[] getActions()
	{
		return TextAction.augmentList(super.getActions(), DEFAULT_ACTIONS);
	}

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


	/**Calculates the full unique target ID strings for every element that has
		a target ID.
		These target IDs are stored as attributes for quick lookup later.
	@param xmlDocument The document for which target IDs should be generated.
//G***del	@param baseURL The document's base URL.
	@see XMLStyleConstants#setTargetURL
	@see #getElementTargetID
	*/
//G***del when works	protected void calculateTargetIDs(final org.w3c.dom.Document document, /*G***del when works, final URL baseURL*/)
/*G***del when works
	{
Debug.trace("OEBEditorKit.calculateTargetIDs()");
		final NodeList nodeList=document.getElementsByTagName("*");	//get a list of all elements in the document G***find a better way to do this using iterators
Debug.trace("found nodes: "+nodeList.getLength());  //G***del
		for(int nodeIndex=nodeList.getLength()-1; nodeIndex>=0; --nodeIndex)	//look at each of the nodes
		{
			final org.w3c.dom.Element element=(org.w3c.dom.Element)nodeList.item(nodeIndex);	//get a reference to this item
		  //encode base URL attributes for all link elements
			if(element.getNodeName().equals("a")) //if this is an <a> element G***use a constant
		  {
//G***del Debug.trace("Storing base URL attribute: "+baseURL+" for element: "+element.getNodeName()); //G***del
				element.setAttribute(XMLStyleConstants.BASE_URL_ATTRIBUTE_NAME, baseURL.toString());	//G***testing; use constant; comment
		  }
			try //encode target ID attributes for all elements with "id" or "name" attributes
			{
				final AttributeSet attributeSet

				String idValue=element.getAttributeNS(null, "id");	//get the value of the id attribute, if there is one G***use a constant here
				if(idValue.length()==0) //if there is no ID value
					idValue=element.getAttributeNS(null, "name");	//get the value of the name attribute, if there is one, since we couldn't find an ID attribute G***use a constant here
				if(idValue.length()!=0)	//if this element has an ID attribute
				{
					final URL targetURL=URLUtilities.createURL(baseURL, "#"+idValue);	//create a full URL from the item ID used as a fragment G***use a constant here
//G***del 	Debug.trace("Storing full URL for element ID: "+idValue+" of: "+targetURL);



				  XMLStyleConstants.setBaseURL(element.getAttributes(), targetURL); //store the target
					element.setAttribute(XMLStyleConstants.TARGET_ID_PATH_ATTRIBUTE_NAME, targetURL.toString());	//G***testing; use constant; comment
	//G***del								oebDocument.setLinkTarget(targetURL.toString(), element);	//associate the full target ID with this element
				}
			}
			catch(MalformedURLException e)	//if there's an error generating a URL
			{
				Debug.error(e);	//ignore the error G***perhaps do something with the error in the future, but it's likely that validity-checking on the XML document will keep errors from ever occurring
			}
		}
			//G***this section is just a kludge because each document gets separated from its URL, so links to the files themselves won't work without this; replace it with something better eventually
		final NodeList bodyNodeList=(NodeList)XPath.evaluateLocationPath(xmlDocument.getDocumentElement(), "/body/*");	//get the first element under <body> G***use a special XPath that only returns the first element G***use a constant here
		if(bodyNodeList.getLength()>0)	//if we retrieved at least one value from the document G***if not, have the XML version just use the first element
		{
//G***fix: if the first note is a comment, this will fail
			final org.w3c.dom.Element element=(org.w3c.dom.Element)bodyNodeList.item(0);	//get a reference to the first element under the body
			final String targetIDValue=element.getAttributeNS(null, XMLStyleConstants.TARGET_ID_PATH_ATTRIBUTE_NAME);	//see if we've assigned a target ID to the first element G***turn this into an accessor function
			if(targetIDValue.length()==0)	//if this element doesn't yet have a target ID
			{
//G***del 	Debug.trace("Storing full URL for element: "+element.getNodeValue()+" of: "+baseURL);
					element.setAttribute(XMLStyleConstants.TARGET_ID_PATH_ATTRIBUTE_NAME, baseURL.toString());	//G***testing; use constant; comment
			}
		}
	}
*/

		//document information storage methods

	/**Sets the given XML data in the document.
	@param xmlDocument The XML document to set in the Swing document.
	@param baseURI The base URI, corresponding to the XML document.
	@param mediaType The media type of the XML document.
	@param swingXMLDocument The Swing document into which the XML will be set.
	*/
	public void setXML(final org.w3c.dom.Document xmlDocument, final URI baseURI, final ContentType mediaType, final XMLDocument swingXMLDocument)
	{
		setXML(new org.w3c.dom.Document[]{xmlDocument}, new URI[]{baseURI}, new ContentType[]{mediaType}, swingXMLDocument); //set the XML data, creating arrays each with a single element
	}

	private XMLCSSStylesheetApplier xmlCSSStylesheetApplier=null;	//G***testing
	
	/**Sets the given XML data in the document.
	@param xmlDocumentArray The array of XML documents to set in the Swing document.
	@param baseURIArray The array of base URIs, corresponding to the XML documents.
	@param mediaTypeArray The array of media types of the documents.
	@param swingXMLDocument The Swing document into which the XML will be set.
	*/
	public void setXML(final org.w3c.dom.Document[] xmlDocumentArray, final URI[] baseURIArray, final ContentType[] mediaTypeArray, final XMLDocument swingXMLDocument)
	{

		xmlCSSStylesheetApplier=new XMLCSSStylesheetApplier(swingXMLDocument);	//G***testing

		//create a list of element specs for creating the document and store them here
		final DefaultStyledDocument.ElementSpec[] elementSpecList=createElementSpecs(xmlDocumentArray, baseURIArray, mediaTypeArray);

		xmlCSSStylesheetApplier=null;


		swingXMLDocument.create(elementSpecList);	//create the document from the element specs

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
	@return Am array of element specs defining the XML document.
	*/
	protected DefaultStyledDocument.ElementSpec[] createElementSpecs(org.w3c.dom.Document xmlDocument, final URI baseURI, final ContentType mediaType)
	{
		return createElementSpecs(new org.w3c.dom.Document[]{xmlDocument}, new URI[]{baseURI}, new ContentType[]{mediaType});  //put the XML document into an array, create the element specs, and return them
	}

	/**Creates element spec objects from a list of XML document trees.
	@param xmlDocumentArray The array of XML document trees.
	@param baseURIArray The array of URIs representing the base URIs for each document.
	@param mediaTypeArray The array of media types of the documents.
	@return An array of element specs defining the XML documents.
	*/
	protected DefaultStyledDocument.ElementSpec[] createElementSpecs(org.w3c.dom.Document[] xmlDocumentArray, final URI[] baseURIArray, final ContentType[] mediaTypeArray)
	{
		final List elementSpecList=createElementSpecList(xmlDocumentArray, baseURIArray, mediaTypeArray); //create the list of element specs
			//convert the list to an array and return it
		return (DefaultStyledDocument.ElementSpec[])elementSpecList.toArray(new DefaultStyledDocument.ElementSpec[elementSpecList.size()]);
	}



	/**Creates a list of element spec objects from an aray of XML document trees.
	@param xmlDocumentArray The array of XML document trees.
	@param baseURIArray The array of URIs representing the base URIs for each document.
	@param mediaTypeArray The array of media types of the documents.
	@return A list of element specs defining the XML documents.
	*/
	protected List createElementSpecList(org.w3c.dom.Document[] xmlDocumentArray, final URI[] baseURIArray, final ContentType[] mediaTypeArray)
	{
		//G***maybe check to make sure both arrays are of the same length
		final List elementSpecList=new ArrayList();	//create an array to hold our element specs
//G***del when works		final XMLCSSSimpleAttributeSet attributeSet=new XMLCSSSimpleAttributeSet();	//create a new attribute for the body element
//G***del		final SimpleAttributeSet attributeSet=new SimpleAttributeSet();	//create a new attribute for the body element

//G***fix if(xmlDocumentArray.length>1 || !"null".equals(xmlDocumentArray[0].getDocumentElement().getLocalName()))	//G***testing
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(null, DefaultStyledDocument.ElementSpec.StartTagType));	//create the beginning of a Swing element to enclose all elements

		for(int xmlDocumentIndex=0; xmlDocumentIndex<xmlDocumentArray.length; ++xmlDocumentIndex)	//look at each of the documents they passed to us
		{
//G***del Debug.trace("Looking at XML document: ", xmlDocumentIndex); //G***del
			final org.w3c.dom.Document xmlDocument=xmlDocumentArray[xmlDocumentIndex];	//get a reference to this document


xmlDocument.normalize();	//G***do we want to do this here? probably not---or maybe so. Maybe we can normalize on the fly in the Swing document, not in the source



			final URI baseURI=baseURIArray[xmlDocumentIndex]; //get a reference to the base URI
			final ContentType mediaType=mediaTypeArray[xmlDocumentIndex]; //get a reference to the media type
			final org.w3c.dom.Element xmlDocumentElement=xmlDocument.getDocumentElement();	//get the root of the document


			final CSSStyleSheet[] stylesheets=xmlCSSStylesheetApplier.getStylesheets(xmlDocument, baseURI, mediaType);	//G***testing
			for(int i=0; i<stylesheets.length; xmlCSSStylesheetApplier.applyStyleSheet(stylesheets[i++], xmlDocumentElement));	//G***testing





			if(xmlDocumentIndex>0)	//if this is not the first document to insert
			{
							//G***check to see if we should actually do this, first (from the CSS attributes)
//G***del System.out.println("Adding page break element.");	//G***del
						appendElementSpecListPageBreak(elementSpecList);  //append a page break
			}
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
					processingInstructions[processingInstructionIndex]=new NameValuePair(processingInstruction.getTarget(), processingInstruction.getData()); //create a name/value pair from the processing instruction
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
//G***fix	if(xmlDocumentArray.length>1 || !"null".equals(xmlDocumentArray[0].getDocumentElement().getLocalName()))	//G***testing
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(null, DefaultStyledDocument.ElementSpec.EndTagType));	//finish the element that encloses all the documents
		return elementSpecList; //return the element spec list we constructed
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
	protected MutableAttributeSet appendElementSpecList(final List elementSpecList, final org.w3c.dom.Element xmlElement, final URI baseURI)
	{
//G***del Debug.trace("XMLDocument.appendElementSpecList: element ", xmlElement.getNodeName());	//G***del
		final SimpleAttributeSet attributeSet=createAttributeSet(xmlElement, baseURI);	//create and fill an attribute set based upon this element's CSS style
//G***del Debug.trace("Attribute set: ", attributeSet);  //G***del
//G***fix if(!"null".equals(xmlElement.getLocalName()))	//G***testing
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(attributeSet, DefaultStyledDocument.ElementSpec.StartTagType));	//create the beginning of a Swing element to model this XML element
		appendElementSpecListContent(elementSpecList, xmlElement, attributeSet, baseURI);	//append the content of the element
//G***fix if(!"null".equals(xmlElement.getLocalName()))	//G***testing
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(attributeSet, DefaultStyledDocument.ElementSpec.EndTagType));	//finish the element we started at the beginning of this function
		return attributeSet;  //return the attribute set used for the element
	}

	/**Appends the tree contents of an XML element (not including the element tag)
		into a list of element specs.
	@param elementSpecList The list of element specs to be inserted into the document.
	@param xmlElement The XML element tree.
	@param attributeSet The attribute set of the element.
	@param baseURI The base URI of the document, used for generating full target
		URIs for quick searching.
	@exception BadLocationException for an invalid starting offset
	@see XMLDocument#insert
	@see XMLDocument#appendElementSpecList
	*/
	protected void appendElementSpecListContent(final List elementSpecList, final org.w3c.dom.Element xmlElement, final MutableAttributeSet attributeSet, final URI baseURI)
	{
		final NodeList childNodeList=xmlElement.getChildNodes();  //get the list of child nodes
		final int childNodeCount=childNodeList.getLength();	//see how many child nodes there are
		if(childNodeCount>0)	//if this element has children
		{
			for(int childIndex=0; childIndex<childNodeCount; childIndex++)	//look at each child node
			{
				final Node node=childNodeList.item(childIndex);	//look at this node
				appendElementSpecListNode(elementSpecList, node, baseURI);	//append this node's information
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
	protected MutableAttributeSet appendElementSpecListNode(final List elementSpecList, final org.w3c.dom.Node node, final URI baseURI)
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
	protected void appendElementSpecListContent(final List elementSpecList, final org.w3c.dom.Node node, final AttributeSet attributeSet, final URI baseURI, final String text)
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
			final CSSStyleDeclaration cssStyle=xmlCSSStylesheetApplier.getStyle(parentElement);
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
	protected void appendElementSpecListPageBreak(final List elementSpecList)
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

	/**Creates an attribute set for the given XML node.
	@param node The XML node, such as an element or text.
	@param baseURI The base URI of the document, used for generating full target
		URIs for quick searching, or <code>null</code> if there is no base URI or
		if the base URI is not applicable.
	@return An attribute set reflecting the CSS attributes of the element.
	*/
	protected SimpleAttributeSet createAttributeSet(final Node xmlNode, final URI baseURI)
	{
		//G***allow this to use the static XMLDocument.createAttributeSet after first extracting element information
//G***del Debug.trace("Creating attribute set for node: ", xmlNode.getNodeName()); //G***del


//TODO take advantage of XMLDocument.createAttributeSet


		final SimpleAttributeSet attributeSet=new SimpleAttributeSet();	//create a new attribute for this element
		XMLStyleUtilities.setXMLElementName(attributeSet, xmlNode.getNodeName());	//store the node's name in the attribute set
//G***del		if(xmlNode.getNodeType()==xmlNode.ELEMENT_NODE) //if this node is an element
//G***del			org.w3c.dom.Element xmlElement=(org.w3c.dom.Element)xmlNode;  //cast the node to an element
		final String namespaceURI=xmlNode.getNamespaceURI();  //get the node namespace URI
		if(namespaceURI!=null)  //if the node has a namespace URI specified
			XMLStyleUtilities.setXMLElementNamespaceURI(attributeSet, namespaceURI);	//store the node's namespace URI in the attribute set
		final String localName=xmlNode.getLocalName();  //get the node's local name
		if(localName!=null) //if the node has a local name defined
			XMLStyleUtilities.setXMLElementLocalName(attributeSet, localName);	//store the node's local name in the attribute set
		else  //if this element has no local name defined, we'll use the normal name for the local name G***testing for styling; we should probably parse out the local name from the qname
			XMLStyleUtilities.setXMLElementLocalName(attributeSet, xmlNode.getNodeName());	//store the node's local name in the attribute set G***fix


		//G***give every attribute set a default empty CSS style; later fix this in the application section to create as needed and to clear them before application

//G***del when moved to the set-style routines		XMLCSSStyleConstants.setXMLCSSStyle(attributeSet, new XMLCSSStyleDeclaration());	//give every attribute set a default empty CSS style

		switch(xmlNode.getNodeType())	//see what type of node for which to create an attribute set
		{
			case Node.ELEMENT_NODE: //if this node is an element
				{
					org.w3c.dom.Element xmlElement=(org.w3c.dom.Element)xmlNode;  //cast the node to an element



final CSSStyleDeclaration cssStyle=xmlCSSStylesheetApplier.getStyle(xmlElement);	//see if we've already applied a style to this element
if(cssStyle!=null)
{
	XMLCSSStyleUtilities.setXMLCSSStyle(attributeSet, cssStyle);	
}
else
{
					//give every attribute set a default empty CSS style; if not, this will cause huge performance hits when trying to create them on the fly when styles are applied
					XMLCSSStyleUtilities.setXMLCSSStyle(attributeSet, new XMLCSSStyleDeclaration());
}
					NamedNodeMap attributeNodeMap=xmlElement.getAttributes(); //get a reference to the attributes
					//store the XML attributes
					for(int attributeIndex=0; attributeIndex<attributeNodeMap.getLength(); ++attributeIndex)	//look at each of the attributes
					{
						final Attr xmlAttribute=(Attr)attributeNodeMap.item(attributeIndex);	//get a reference to this attribute
							//add this XML attribute to the Swing atribute set as the value of our special XML attribute key
						XMLStyleUtilities.addXMLAttribute(attributeSet, xmlAttribute.getNamespaceURI(), xmlAttribute.getNodeName(), xmlAttribute.getNodeValue());
					}
/*G***del when works
					if(xmlElement.getChildNodes().getLength()==0)  //if the element has no child nodes
					{
						XMLStyleUtilities.setXMLEmptyElement(attributeSet, true); //show that this is an empty element (dummy child text may be added later, so without this it would be hard to tell)
					}
*/
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
					Debug.error(badLocationException);  //report an error
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
//G***del when works		final AttributeSet attributeSet=element.getAttributes();	//get the attributes of this element
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


	/**
	 * Creates a view from an element.
	 *
	 * @param elem the element
	 * @return the view
	 */
	 /*G***fix
		public View create(Element elem)
		{
		*/
/*G***del
			System.out.println("element: "+elem);
		if(elem.getAttributes().getAttribute(StyleConstants.NameAttribute)!=null && elem.getAttributes().getAttribute(StyleConstants.NameAttribute) instanceof String)
			System.out.println("element.name: "+elem.getAttributes().getAttribute(StyleConstants.NameAttribute));
*/
//G***fix		return new ParagraphView(elem);
//G***fix			return XMLEditorKit.create(elem);

//G***fix		}
//G***fix	}
/*G***fix


			Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
			if (o instanceof HTML.Tag) {
		HTML.Tag kind = (HTML.Tag) o;
		if (kind == HTML.Tag.CONTENT) {
				return new InlineView(elem);
		} else if (kind == HTML.Tag.IMPLIED) {
				String ws = (String) elem.getAttributes().getAttribute(
			CSS.Attribute.WHITE_SPACE);
				if ((ws != null) && ws.equals("pre")) {
			return new LineView(elem);
				}
				return new javax.swing.text.html.ParagraphView(elem);
		} else if ((kind == HTML.Tag.P) ||
				 (kind == HTML.Tag.H1) ||
				 (kind == HTML.Tag.H2) ||
				 (kind == HTML.Tag.H3) ||
				 (kind == HTML.Tag.H4) ||
				 (kind == HTML.Tag.H5) ||
				 (kind == HTML.Tag.H6) ||
				 (kind == HTML.Tag.DT)) {
				// paragraph
				return new javax.swing.text.html.ParagraphView(elem);
		} else if ((kind == HTML.Tag.MENU) ||
			   (kind == HTML.Tag.DIR) ||
			   (kind == HTML.Tag.UL)   ||
				 (kind == HTML.Tag.OL)) {
		    return new ListView(elem);
		} else if (kind == HTML.Tag.BODY) {
		    // reimplement major axis requirements to indicate that the
		    // block is flexible for the body element... so that it can
		    // be stretched to fill the background properly.
		    return new BlockView(elem, View.Y_AXIS) {
												protected SizeRequirements calculateMajorAxisRequirements(int axis, SizeRequirements r) {
                            r = super.calculateMajorAxisRequirements(axis, r);
					r.maximum = Integer.MAX_VALUE;
			    return r;
			}
		    };
		} else if ((kind == HTML.Tag.LI) ||
				 (kind == HTML.Tag.CENTER) ||
			   (kind == HTML.Tag.DL) ||
			   (kind == HTML.Tag.DD) ||
			   (kind == HTML.Tag.HTML) ||
			   (kind == HTML.Tag.DIV) ||
			   (kind == HTML.Tag.BLOCKQUOTE) ||
			   (kind == HTML.Tag.PRE)) {
		    // vertical box
		    return new BlockView(elem, View.Y_AXIS);
		} else if (kind == HTML.Tag.NOFRAMES) {
		    return new NoFramesView(elem, View.Y_AXIS);
		} else if ((kind == HTML.Tag.TH) ||
			   (kind == HTML.Tag.TD)) {
		    return new javax.swing.text.html.TableView.CellView(elem);
		} else if (kind==HTML.Tag.IMG) {
		    return new ImageView(elem);
		} else if (kind == HTML.Tag.ISINDEX) {
		    return new IsindexView(elem);
		} else if (kind == HTML.Tag.HR) {
		    return new HRuleView(elem);
		} else if (kind == HTML.Tag.BR) {
				return new BRView(elem);
		} else if (kind == HTML.Tag.TABLE) {
		    return new javax.swing.text.html.TableView(elem);
		} else if ((kind == HTML.Tag.INPUT) ||
			   (kind == HTML.Tag.SELECT) ||
			   (kind == HTML.Tag.TEXTAREA)) {
		    return new FormView(elem);
		} else if (kind == HTML.Tag.OBJECT) {
		    return new ObjectView(elem);
		} else if (kind == HTML.Tag.FRAMESET) {
                     if (elem.getAttributes().isDefined(HTML.Attribute.ROWS)) {
                         return new FrameSetView(elem, View.Y_AXIS);
                     } else if (elem.getAttributes().isDefined(HTML.Attribute.COLS)) {
                         return new FrameSetView(elem, View.X_AXIS);
										 }
                     throw new Error("Can't build a"  + kind + ", " + elem + ":" +
                                     "no ROWS or COLS defined.");
                } else if (kind == HTML.Tag.FRAME) {
 		    return new FrameView(elem);
                } else if (kind instanceof HTML.UnknownTag) {
		    return new HiddenTagView(elem);
		} else if (kind == HTML.Tag.COMMENT) {
		    return new CommentView(elem);
		} else if ((kind == HTML.Tag.HEAD) ||
			   (kind == HTML.Tag.TITLE) ||
			   (kind == HTML.Tag.META) ||
			   (kind == HTML.Tag.LINK) ||
			   (kind == HTML.Tag.STYLE) ||
			   (kind == HTML.Tag.SCRIPT) ||
			   (kind == HTML.Tag.AREA) ||
			   (kind == HTML.Tag.MAP) ||
			   (kind == HTML.Tag.PARAM) ||
			   (kind == HTML.Tag.APPLET)) {
		    return new HiddenTagView(elem);
		}
		// don't know how to build this....
		throw new Error("Can't build a " + kind + ", " + elem);
	    }

	    // don't know how to build this....
	    throw new Error("Can't build a " + elem);
	}
		}
*/
		// --- Action implementations ------------------------------

/** The bold action identifier
*/
//G***fix    public static final String	BOLD_ACTION = "html-bold-action";
/** The italic action identifier
*/
//G***fix    public static final String	ITALIC_ACTION = "html-italic-action";
/** The paragraph left indent action identifier
*/
//G***fix		public static final String	PARA_INDENT_LEFT = "html-para-indent-left";
/** The paragraph right indent action identifier
*/
//G***fix		public static final String	PARA_INDENT_RIGHT = "html-para-indent-right";
/** The  font size increase to next value action identifier
*/
//G***fix		public static final String	FONT_CHANGE_BIGGER = "html-font-bigger";
/** The font size decrease to next value action identifier
*/
//G***fix		public static final String	FONT_CHANGE_SMALLER = "html-font-smaller";
/** The Color choice action identifier
		 The color is passed as an argument
*/
//G***fix		public static final String	COLOR_ACTION = "html-color-action";
/** The logical style choice action identifier
		 The logical style is passed in as an argument
*/
//G***fix		public static final String	LOGICAL_STYLE_ACTION = "html-logical-style-action";
		/**
		 * Align images at the top.
		 */
//G***fix		public static final String	IMG_ALIGN_TOP = "html-image-align-top";

		/**
		 * Align images in the middle.
		 */
//G***fix		public static final String	IMG_ALIGN_MIDDLE = "html-image-align-middle";

		/**
		 * Align images at the bottom.
		 */
//G***fix		public static final String	IMG_ALIGN_BOTTOM = "html-image-align-bottom";

		/**
		 * Align images at the border.
		 */
//G***fix		public static final String	IMG_BORDER = "html-image-border";


		/** HTML used when inserting tables. */
//G***fix		private static final String INSERT_TABLE_HTML = "<table border=1><tr><td></td></tr></table>";

		/** HTML used when inserting unordered lists. */
//G***fix		private static final String INSERT_UL_HTML = "<ul><li></li></ul>";

		/** HTML used when inserting ordered lists. */
//G***fix		private static final String INSERT_OL_HTML = "<ol><li></li></ol>";

		/** HTML used when inserting hr. */
//G***fix		private static final String INSERT_HR_HTML = "<hr>";

		/** HTML used when inserting pre. */
//G***fix		private static final String INSERT_PRE_HTML = "<pre></pre>";

	/**An abstract <code>Action</code> providing convenience methods for working
		with XML documents.
		<p>NOTE: None of the convenience methods obtain a lock on the document. If
		you have another thread modifying the text these methods may have
		inconsistant behavior, or return the wrong thing.
	*/
	public static abstract class XMLTextAction extends StyledTextAction
	{
		/**Constructor.
		@param name The name of the action.
		*/
		public XMLTextAction(String name)
		{
		  super(name);  //do the default construction with the name of the action
		}

		/**Returns the <code>XMLDocument</code> of the editor pane.
		@param editorPane The editor pane which should contain an XML document.
		@return The <code>XMLDocument</code> of the editor pane.
		@exception IllegalArgumentException Thrown if the editor pane does not
			contain an <code>XMLDocument</code>
		*/
		protected XMLDocument getXMLDocument(final JEditorPane editorPane)
		{
		  final Document document=editorPane.getDocument(); //get the document associated with the editor pane
		  if(document instanceof XMLDocument) //if this is an XML document
				return (XMLDocument)document; //return the document, cast to an XMLDocument
			else  //if the document isn't an XML document
			  throw new IllegalArgumentException("Document must be an XMLDocument."); //show that the document class type is incorrect
		}

		/**Returns an <code>XMLEditorKit</code> from the editor pane.
		@param editorPane The editor pane which should be associated with an XML
			editor kit.
		@return The associated <code>XMLEditorKit</code> for the given editor pane.
		*/
		protected XMLEditorKit getXMLEditorKit(final JEditorPane editorPane)
		{
		  final EditorKit editorKit=editorPane.getEditorKit();  //get the editor kit of the editor pane
		  if(editorKit instanceof XMLEditorKit) //if this is an XML editor kit
			  return (XMLEditorKit)editorKit; //return the editor kit, cast to an XMLEditorKit
		  else  //if the editor kit isn't an XML editor kit
				throw new IllegalArgumentException("EditorKit must be an XMLEditorKit.");
		}

	/**
	 * Returns an array of the Elements that contain <code>offset</code>.
	 * The first elements corresponds to the root.
	 */
/*G***fix
	protected Element[] getElementsAt(HTMLDocument doc, int offset) {
	    return getElementsAt(doc.getDefaultRootElement(), offset, 0);
	}
*/

	/**
	 * Recursive method used by getElementsAt.
	 */
/*G***fix
	private Element[] getElementsAt(Element parent, int offset,
					int depth) {
	    if (parent.isLeaf()) {
		Element[] retValue = new Element[depth + 1];
		retValue[depth] = parent;
		return retValue;
	    }
	    Element[] retValue = getElementsAt(parent.getElement
			  (parent.getElementIndex(offset)), offset, depth + 1);
	    retValue[depth] = parent;
	    return retValue;
	}
*/

	/**
	 * Returns number of elements, starting at the deepest leaf, needed
	 * to get to an element representing <code>tag</code>. This will
	 * return -1 if no elements is found representing <code>tag</code>,
	 * or 0 if the parent of the leaf at <code>offset</code> represents
	 * <code>tag</code>.
	 */
/*G***fix
	protected int elementCountToTag(HTMLDocument doc, int offset,
					HTML.Tag tag) {
	    int depth = -1;
	    Element e = doc.getCharacterElement(offset);
	    while (e != null && e.getAttributes().getAttribute
		   (StyleConstants.NameAttribute) != tag) {
		e = e.getParentElement();
		depth++;
	    }
	    if (e == null) {
		return -1;
	    }
	    return depth;
	}
*/

	/**
	 * Returns the deepest element at <code>offset</code> matching
	 * <code>tag</code>.
	 */
/*G***fix
	protected Element findElementMatchingTag(HTMLDocument doc, int offset,
						 HTML.Tag tag) {
	    Element e = doc.getDefaultRootElement();
	    Element lastMatch = null;
	    while (e != null) {
		if (e.getAttributes().getAttribute
		   (StyleConstants.NameAttribute) == tag) {
		    lastMatch = e;
		}
		e = e.getElement(e.getElementIndex(offset));
	    }
	    return lastMatch;
	}
*/
	}





	/**Action that moves the caret to the beginning of the document.
	<p>This version ensures that the caret is not placed on a hidden view.</p>
	*/
	static class BeginAction extends TextAction
	{
		/* Create this object with the appropriate identifier. */
		BeginAction(String nm, boolean select)
		{
			super(nm);
			this.select= select;
		}

		/** The operation to perform when this action is triggered. */
		public void actionPerformed(ActionEvent e)
		{
			JTextComponent target= getTextComponent(e);
			if (target != null)
			{
				try
				{
					final int beginOffset=SwingTextUtilities.getBegin(target);	//get the beginning offset; if this is an invalid position, don't worry---the corresponding exception will be caught below  
					if(select)
					{
						target.moveCaretPosition(beginOffset);
					}
					else
					{
						target.setCaretPosition(beginOffset);
					}
				}
				catch (BadLocationException badLocationException)
				{
					UIManager.getLookAndFeel().provideErrorFeedback(target);
				}
			}
		}
		private boolean select;
	}

	/**Action that moves the caret to the end of the document.
	<p>This version ensures that the caret is not placed on a hidden view.</p>
	*/
	static class EndAction extends TextAction
	{
		/* Create this object with the appropriate identifier. */
		EndAction(String nm, boolean select)
		{
			super(nm);
			this.select= select;
		}

		/** The operation to perform when this action is triggered. */
		public void actionPerformed(ActionEvent e)
		{
			JTextComponent target= getTextComponent(e);
			if (target != null)
			{
				try
				{
					final int endOffset=SwingTextUtilities.getEnd(target);	//get the ending offset; if this is an invalid position, don't worry---the corresponding exception will be caught below  
					if(select)
					{
						target.moveCaretPosition(endOffset);
					}
					else
					{
						target.setCaretPosition(endOffset);
					}
				}
				catch (BadLocationException badLocationException)
				{
					UIManager.getLookAndFeel().provideErrorFeedback(target);
				}
			}
		}
		private boolean select;
	}



	/**Action to go to the previous available page(es).*/
	protected static class PreviousPageAction extends XMLTextAction
	{

		/**Creates a previous page action with the appropriate name.
		@param name The name of the action.
		*/
		public PreviousPageAction(final String name)
		{
	    super(name);  //do the default construction with the name
		}

		/**The operation to perform when this action is triggered.
		@param actionEvent The action representing the event.
		*/
		public void actionPerformed(ActionEvent actionEvent)
		{
//G***del Debug.notify("XMLEditorKit.PreviousPageAction.actionPerformed()");
		  final JEditorPane editorPane=getEditor(actionEvent); //get the associated editor pane
		  if(editorPane!=null && editorPane instanceof XMLTextPane) //if the editor pane is valid, and it's an instance of an XMLTextPane G***maybe make an XMLTextAction.getXMLEditorPane()
		  {
				final XMLTextPane xmlTextPane=(XMLTextPane)editorPane;  //cast the editor pane to an XML editor pane
				xmlTextPane.goPreviousPage(); //go to the previous page
		  }
		}
	}

	/**Action to go to the next available page(es).*/
	protected static class NextPageAction extends XMLTextAction
	{

		/**Creates a next page action with the appropriate name.
		@param name The name of the action.
		*/
		public NextPageAction(final String name)
		{
	    super(name);  //do the default construction with the name
		}

		/**The operation to perform when this action is triggered.
		@param actionEvent The action representing the event.
		*/
		public void actionPerformed(ActionEvent actionEvent)
		{
//G***del Debug.notify("XMLEditorKit.NextPageAction.actionPerformed()");
		  final JEditorPane editorPane=getEditor(actionEvent); //get the associated editor pane
		  if(editorPane!=null && editorPane instanceof XMLTextPane) //if the editor pane is valid, and it's an instance of an XMLTextPane G***maybe make an XMLTextAction.getXMLEditorPane()
		  {
				final XMLTextPane xmlTextPane=(XMLTextPane)editorPane;  //cast the editor pane to an XML editor pane
				xmlTextPane.goNextPage(); //go to the next page
		  }
		}
	}

	/**Action to display the document element hierarchy.*/
	protected static class DisplayElementTreeAction extends TextAction
	{

		/**Creates an element tree action with the appropriate name.
		@param name The name of the action.
		*/
		public DisplayElementTreeAction(final String name)
		{
			super(name);  //do the default construction with the name
		}

		/**The operation to perform when this action is triggered.
		@param actionEvent The action representing the event.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
//G***del Debug.notify("XMLEditorKit.PreviousPageAction.actionPerformed()");
			final JTextComponent textComponent=getTextComponent(actionEvent);	//get the text component
			if(Debug.isDebug())	//if debugging is turned on
				ViewUtilities.printViews(textComponent, Debug.getOutput());	//print the views to the debug output
			new BasicFrame("Elements", new ElementTreePanel(textComponent)).setVisible(true);	//show a new frame showing elements G***i18n
		}
	}

	/**
	 * @return HTMLDocument of <code>e</code>.
	 */
/*G***fix
	protected HTMLDocument getHTMLDocument(JEditorPane e) {
			Document d = e.getDocument();
			if (d instanceof HTMLDocument) {
		return (HTMLDocument) d;
			}
			throw new IllegalArgumentException("document must be HTMLDocument");
	}
*/

	/**
	 * @return HTMLEditorKit for <code>e</code>.
	 */
/*G***fix
				protected HTMLEditorKit getHTMLEditorKit(JEditorPane e) {
			EditorKit k = e.getEditorKit();
			if (k instanceof HTMLEditorKit) {
		return (HTMLEditorKit) k;
			}
			throw new IllegalArgumentException("EditorKit must be HTMLEditorKit");
	}
*/

	/**
	 * Returns an array of the Elements that contain <code>offset</code>.
	 * The first elements corresponds to the root.
	 */
/*G***fix
	protected Element[] getElementsAt(HTMLDocument doc, int offset) {
			return getElementsAt(doc.getDefaultRootElement(), offset, 0);
	}
*/

	/**
	 * Recursive method used by getElementsAt.
	 */
/*G***fix
	private Element[] getElementsAt(Element parent, int offset,
					int depth) {
			if (parent.isLeaf()) {
		Element[] retValue = new Element[depth + 1];
		retValue[depth] = parent;
		return retValue;
			}
			Element[] retValue = getElementsAt(parent.getElement
				(parent.getElementIndex(offset)), offset, depth + 1);
			retValue[depth] = parent;
			return retValue;
	}
*/

	/**
	 * Returns number of elements, starting at the deepest leaf, needed
	 * to get to an element representing <code>tag</code>. This will
	 * return -1 if no elements is found representing <code>tag</code>,
	 * or 0 if the parent of the leaf at <code>offset</code> represents
	 * <code>tag</code>.
	 */
/*G***fix
	protected int elementCountToTag(HTMLDocument doc, int offset,
					HTML.Tag tag) {
			int depth = -1;
			Element e = doc.getCharacterElement(offset);
	    while (e != null && e.getAttributes().getAttribute
		   (StyleConstants.NameAttribute) != tag) {
		e = e.getParentElement();
		depth++;
	    }
	    if (e == null) {
		return -1;
	    }
	    return depth;
	}
*/

	/**
	 * Returns the deepest element at <code>offset</code> matching
	 * <code>tag</code>.
	 */
/*G***fix
	protected Element findElementMatchingTag(HTMLDocument doc, int offset,
						 HTML.Tag tag) {
			Element e = doc.getDefaultRootElement();
			Element lastMatch = null;
			while (e != null) {
		if (e.getAttributes().getAttribute
			 (StyleConstants.NameAttribute) == tag) {
				lastMatch = e;
		}
		e = e.getElement(e.getElementIndex(offset));
			}
			return lastMatch;
	}
		}
*/

		/**
		 * InsertHTMLTextAction can be used to insert an arbitrary string of HTML
		 * into an existing HTML document. At least two HTML.Tags need to be
		 * supplied. The first Tag, parentTag, identifies the parent in
		 * the document to add the elements to. The second tag, addTag,
		 * identifies the first tag that should be added to the document as
		 * seen in the HTML string. One important thing to remember, is that
		 * the parser is going to generate all the appropriate tags, even if
		 * they aren't in the HTML string passed in.<p>
		 * For example, lets say you wanted to create an action to insert
		 * a table into the body. The parentTag would be HTML.Tag.BODY,
		 * addTag would be HTML.Tag.TABLE, and the string could be something
		 * like &lt;table>&lt;tr>&lt;td>&lt;/td>&lt;/tr>&lt;/table>.
		 * <p>There is also an option to supply an alternate parentTag and
		 * addTag. These will be checked for if there is no parentTag at
		 * offset.
		 */
/*G***fix
		public static class InsertHTMLTextAction extends HTMLTextAction {
	public InsertHTMLTextAction(String name, String html,
						HTML.Tag parentTag, HTML.Tag addTag) {
			this(name, html, parentTag, addTag, null, null);
	}

	public InsertHTMLTextAction(String name, String html,
						HTML.Tag parentTag,
						HTML.Tag addTag,
						HTML.Tag alternateParentTag,
						HTML.Tag alternateAddTag) {
			this(name, html, parentTag, addTag, alternateParentTag,
		 alternateAddTag, true);
	}
*/

	/* public */
/*G***fix
	InsertHTMLTextAction(String name, String html,
						HTML.Tag parentTag,
						HTML.Tag addTag,
						HTML.Tag alternateParentTag,
						HTML.Tag alternateAddTag,
						boolean adjustSelection) {
			super(name);
			this.html = html;
			this.parentTag = parentTag;
			this.addTag = addTag;
			this.alternateParentTag = alternateParentTag;
			this.alternateAddTag = alternateAddTag;
			this.adjustSelection = adjustSelection;
	}
*/

	/**
	 * A cover for HTMLEditorKit.insertHTML. If an exception it
	 * thrown it is wrapped in a RuntimeException and thrown.
	 */
/*G***fix
	protected void insertHTML(JEditorPane editor, HTMLDocument doc,
					int offset, String html, int popDepth,
					int pushDepth, HTML.Tag addTag) {
			try {
		getHTMLEditorKit(editor).insertHTML(doc, offset, html,
								popDepth, pushDepth,
								addTag);
			} catch (IOException ioe) {
		throw new RuntimeException("Unable to insert: " + ioe);
			} catch (BadLocationException ble) {
		throw new RuntimeException("Unable to insert: " + ble);
			}
	}
*/

	/**
	 * This is invoked when inserting at a boundry. It determines
	 * the number of pops, and then the number of pushes that need
	 * to be performed, and then invokes insertHTML.
	 */
/*G***fix
	protected void insertAtBoundry(JEditorPane editor, HTMLDocument doc,
							 int offset, Element insertElement,
							 String html, HTML.Tag parentTag,
							 HTML.Tag addTag) {
			// Find the common parent.
			Element e;
			Element commonParent;
			boolean isFirst = (offset == 0);

			if (offset > 0 || insertElement == null) {
		e = doc.getDefaultRootElement();
		while (e != null && e.getStartOffset() != offset &&
					 !e.isLeaf()) {
				e = e.getElement(e.getElementIndex(offset));
		}
		commonParent = (e != null) ? e.getParentElement() : null;
			}
			else {
		// If inserting at the origin, the common parent is the
		// insertElement.
		commonParent = insertElement;
			}
			if (commonParent != null) {
		// Determine how many pops to do.
		int pops = 0;
		int pushes = 0;
		if (isFirst && insertElement != null) {
				e = commonParent;
				while (e != null && !e.isLeaf()) {
			e = e.getElement(e.getElementIndex(offset));
			pops++;
		    }
		}
		else {
		    e = commonParent;
				offset--;
				while (e != null && !e.isLeaf()) {
			e = e.getElement(e.getElementIndex(offset));
			pops++;
				}

				// And how many pushes
				e = commonParent;
				offset++;
				while (e != null && e != insertElement) {
			e = e.getElement(e.getElementIndex(offset));
			pushes++;
				}
		}
		pops = Math.max(0, pops - 1);

		// And insert!
		insertHTML(editor, doc, offset, html, pops, pushes, addTag);
			}
	}
*/

	/**
	 * If there is an Element with name <code>tag</code> at
	 * <code>offset</code>, this will invoke either insertAtBoundry
	 * or <code>insertHTML</code>. This returns true if there is
	 * a match, and one of the inserts is invoked.
	 */
	/*protected*/
/*G***fix
	boolean insertIntoTag(JEditorPane editor, HTMLDocument doc,
			      int offset, HTML.Tag tag, HTML.Tag addTag) {
	    Element e = findElementMatchingTag(doc, offset, tag);
	    if (e != null && e.getStartOffset() == offset) {
		insertAtBoundry(editor, doc, offset, e, html,
				tag, addTag);
		return true;
	    }
	    else if (offset > 0) {
		int depth = elementCountToTag(doc, offset - 1, tag);
		if (depth != -1) {
		    insertHTML(editor, doc, offset, html, depth, 0, addTag);
		    return true;
		}
	    }
	    return false;
	}
*/

	/**
	 * Called after an insertion to adjust the selection.
	 */
	/* protected */
/*G***fix
	void adjustSelection(JEditorPane pane, HTMLDocument doc,
					 int startOffset, int oldLength) {
			int newLength = doc.getLength();
			if (newLength != oldLength && startOffset < newLength) {
		if (startOffset > 0) {
				String text;
				try {
			text = doc.getText(startOffset - 1, 1);
				} catch (BadLocationException ble) {
			text = null;
				}
				if (text != null && text.length() > 0 &&
			text.charAt(0) == '\n') {
			pane.select(startOffset, startOffset);
		    }
		    else {
			pane.select(startOffset + 1, startOffset + 1);
		    }
		}
		else {
		    pane.select(1, 1);
		}
	    }
	}
*/

				/**
				 * Inserts the html into the document.
				 *
				 * @param e the event
				 */
/*G***fix
				public void actionPerformed(ActionEvent ae) {
			JEditorPane editor = getEditor(ae);
			if (editor != null) {
		HTMLDocument doc = getHTMLDocument(editor);
		int offset = editor.getSelectionStart();
		int length = doc.getLength();
		boolean inserted;
		// Try first choice
		if (!insertIntoTag(editor, doc, offset, parentTag, addTag) &&
		    alternateParentTag != null) {
		    // Then alternate.
		    inserted = insertIntoTag(editor, doc, offset,
					     alternateParentTag,
					     alternateAddTag);
		}
		else {
		    inserted = true;
		}
		if (adjustSelection && inserted) {
		    adjustSelection(editor, doc, offset, length);
		}
	    }
	}
*/

	/** HTML to insert. */
//G***fix	protected String html;
	/** Tag to check for in the document. */
//G***fix	protected HTML.Tag parentTag;
	/** Tag in HTML to start adding tags from. */
//G***fix	protected HTML.Tag addTag;
	/** Alternate Tag to check for in the document if parentTag is
	 * not found. */
//G***fix	protected HTML.Tag alternateParentTag;
	/** Alternate tag in HTML to start adding tags from if parentTag
	 * is not found and alternateParentTag is found. */
//G***fix	protected HTML.Tag alternateAddTag;
	/** True indicates the selection should be adjusted after an insert. */
//G***fix	boolean adjustSelection;
//G***fix		}


		/**
		 * InsertHRAction is special, at actionPerformed time it will determine
		 * the parent HTML.Tag based on the paragraph element at the selection
		 * start.
		 */
/*G***fix
		static class InsertHRAction extends InsertHTMLTextAction {
	InsertHRAction() {
			super("InsertHR", "<hr>", null, HTML.Tag.IMPLIED, null, null,
			false);
	}
*/

				/**
				 * Inserts the html into the document.
				 *
				 * @param e the event
				 */
/*G***fix
				public void actionPerformed(ActionEvent ae) {
			JEditorPane editor = getEditor(ae);
			if (editor != null) {
		HTMLDocument doc = getHTMLDocument(editor);
		int offset = editor.getSelectionStart();
		Element paragraph = doc.getParagraphElement(offset);
		if (paragraph.getParentElement() != null) {
				parentTag = (HTML.Tag)paragraph.getParentElement().
											getAttributes().getAttribute
											(StyleConstants.NameAttribute);
				super.actionPerformed(ae);
		}
			}
	}

		}
*/

	/**Adds a progress listener.
	@param listener The listener to be notified of progress.
	*/
	public void addProgressListener(ProgressListener listener)
	{
		progressListenerList.add(ProgressListener.class, listener);	//add this listener
	}

	/**Removes a progress listener.
	@param listener The listener that should no longer be notified of progress.
	*/
	public void removeProgressListener(ProgressListener listener)
	{
		progressListenerList.remove(ProgressListener.class, listener);
	}

	/**Notifies all listeners that have registered interest for progress that
		progress has been made.
	@param status The status to display.
	*/
	protected void fireMadeProgress(final ProgressEvent progressEvent)
	{
//G***del if not needed		final ProgressEvent progressEvent=new ProgressEvent(this, status);	//create a new progress event
		final Object[] listeners=progressListenerList.getListenerList();	//get the non-null array of listeners
		for(int i=listeners.length-2; i>=0; i-=2)	//look at each listener, from last to first
		{
			if(listeners[i]==ProgressListener.class)	//if this is a progress listener (it should always be)
				((ProgressListener)listeners[i+1]).madeProgress(progressEvent);
     }
	}

	/**Invoked when progress has been made by, for example, the document.
	@param e The event object representing the progress made.
	*/
/*G***del if no needed
	public void madeProgress(final ProgressEvent e)
	{
		fireMadeProgress(e);	//refire that event to our listeners G***perhaps do some manipulation here
	}
*/

}

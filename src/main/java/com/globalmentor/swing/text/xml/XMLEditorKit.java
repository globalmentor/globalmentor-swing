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

package com.globalmentor.swing.text.xml;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.Document;
import javax.swing.text.Element;

import static java.util.Objects.*;

import static com.globalmentor.w3c.spec.XML.*;
import static com.globalmentor.xml.XML.*;
import static java.nio.charset.StandardCharsets.*;

import com.globalmentor.collections.IdentityHashSet;
import com.globalmentor.io.*;
import com.globalmentor.java.*;
import com.globalmentor.log.Log;
import com.globalmentor.model.NameValuePair;
import com.globalmentor.net.*;
import com.globalmentor.rdf.*;
import com.globalmentor.swing.*;
import com.globalmentor.swing.text.BasicStyledEditorKit;
//TODO fix import com.globalmentor.swing.text.rdf.maqro.MAQROXMLElementKit;
import com.globalmentor.swing.text.xml.css.*;
import com.globalmentor.w3c.spec.CSS;
import com.globalmentor.xml.XML;
import com.globalmentor.xml.XMLSerializer;
import com.globalmentor.xml.dom.impl.stylesheets.css.XMLCSSStyleDeclaration;
import com.globalmentor.xml.dom.impl.stylesheets.css.XMLCSSStylesheetApplier;

import org.w3c.dom.*;
import org.w3c.dom.css.*;

/**
 * An editor kit for XML.
 * @author Garret Wilson
 */
public class XMLEditorKit extends BasicStyledEditorKit {

	/** The default media type this editor kit supports, <code>text/xml</code>. */
	protected static final ContentType DEFAULT_MEDIA_TYPE = ContentType.create(ContentType.TEXT_PRIMARY_TYPE, ContentTypeConstants.XML_SUBTYPE);

	/** The map of XML element kits keyed to XML namespaces. */
	protected final Map<String, XMLElementKit> namespaceXMLElementKitMap = new HashMap<String, XMLElementKit>();

	/** The default kit for creating element specs from XML elements. */
	private final DefaultXMLElementKit defaultXMLElementKit;

	/** @return The default kit for creating element specs from XML elements. */
	protected DefaultXMLElementKit getDefaultXMLElementKit() {
		return defaultXMLElementKit;
	}

	/** The kit for creating element specs from MAQRO XML elements. */
	//TODO fix	private final MAQROXMLElementKit maqroXMLElementKit;

	/** @return The kit for creating element specs from MAQRO XML elements. */
	//TODO fix	protected MAQROXMLElementKit getMAQROXMLElementKit() {return maqroXMLElementKit;}

	/** The default view factory for an XML editor kit. */
	private final ViewFactory defaultViewFactory = new DefaultXMLViewFactory();

	/** The default link controller for an XML editor kit. */
	private final XMLLinkController defaultLinkController = new DefaultXMLLinkController();

	/** A map of view factories, each keyed to a namespace URI string. */
	private Map<String, ViewFactory> namespaceViewFactoryMap = new HashMap<String, ViewFactory>();

	/**
	 * Registers a view factory for a particular namespace URI.
	 * @param namespaceURI The namespace URI that identifies the namespace, elements in which will use the given view factory to create views.
	 * @param viewFactory The view factory that should be associated with the given namespace.
	 */
	public void registerViewFactory(final String namespaceURI, final ViewFactory viewFactory) {
		namespaceViewFactoryMap.put(namespaceURI, viewFactory); //store the view factory in the map, keyed to the namespace URI
	}

	/**
	 * Retrieves a view factory for the given namespace, if one has been registered.
	 * @param namespaceURI The namespace for which a view factory should be returned.
	 * @return A view factory for creating views for elements in the given namepace, or <code>null</code> if no view factory has been registered for the given
	 *         namespace.
	 */
	public ViewFactory getViewFactory(final String namespaceURI) {
		return namespaceViewFactoryMap.get(namespaceURI); //return a view factory for the given namespace, if one has been registered
	}

	/** Removes all registered view factories. */
	public void unregisterViewFactories() {
		namespaceViewFactoryMap.clear(); //clear all registered view factoriees
	}

	/** A map of link controllers, each keyed to a namespace URI string. */
	private Map<String, XMLLinkController> namespaceLinkControllerMap = new HashMap<String, XMLLinkController>();

	/**
	 * Registers a link controller for a particular namespace URI.
	 * @param namespaceURI The namespace URI that identifies the namespace, elements in which will use the given link controller for linking.
	 * @param linkController The link controller that should be associated with the given namespace.
	 */
	public void registerLinkController(final String namespaceURI, final XMLLinkController linkController) {
		namespaceLinkControllerMap.put(namespaceURI, linkController); //store the link controller in the map, keyed to the namespace URI
	}

	/**
	 * Retrieves a link controller for the given namespace, if one has been registered.
	 * @param namespaceURI The namespace for which a link controller should be returned.
	 * @return A link controller for handling links for elements in the given namepace, or <code>null</code> if no view factory has been registered for the given
	 *         namespace.
	 */
	public XMLLinkController getLinkController(final String namespaceURI) {
		return namespaceLinkControllerMap.get(namespaceURI); //return a link controller for the given namespace, if one has been registered
	}

	/** Removes all registered link controllers. */
	public void unregisterLinkControllers() {
		namespaceLinkControllerMap.clear(); //clear all registered link controllers
	}

	/**
	 * Default actions used by this editor kit to augment the super class default actions.
	 */
	private static final Action[] DEFAULT_ACTIONS = {
	//TODO del		new EndLineAction(endLineAction, false)	//TODO testing
	};

	/** The object that applies stylesheets to the document. */
	//TODO bring back if needed	private final SwingXMLCSSStylesheetApplier swingStylesheetApplier;

	/** @return The object that applies stylesheets to the document. */
	//TODO bring back if needed		protected SwingXMLCSSStylesheetApplier getSwingStylesheetApplier() {return swingStylesheetApplier;}

	/** The object that applies stylesheets to an XML document. */
	private final XMLCSSStylesheetApplier xmlStylesheetApplier;

	/** @return The object that applies stylesheets to the XML document. */
	protected XMLCSSStylesheetApplier getXMLStylesheetApplier() {
		return xmlStylesheetApplier;
	}

	/**
	 * Constructor which defaults to a content type of <code>text/xml</code>.
	 * @param uriInputStreamable The source of input streams for resources.
	 * @throws NullPointerException if the new source of input streams is <code>null</code>.
	 */
	public XMLEditorKit(final URIInputStreamable uriInputStreamable) {
		this(DEFAULT_MEDIA_TYPE, uriInputStreamable); //construct the class with the default media type
	}

	/**
	 * Constructor that specifies the specific XML media type supported.
	 * @param mediaType The XML media type supported. In some instances, such as <code>text/html</code>, this indicates a default namespace even in the absence of
	 *          a document namespace identfication.
	 * @param uriInputStreamable The source of input streams for resources.
	 */
	public XMLEditorKit(final ContentType mediaType, final URIInputStreamable uriInputStreamable) {
		super(mediaType, uriInputStreamable); //construct the parent class
		defaultXMLElementKit = new DefaultXMLElementKit(); //create the default XML element kit
		//TODO fix		maqroXMLElementKit=new MAQROXMLElementKit(defaultXMLElementKit);	//create the MAQRO XML element kit
		//TODO fix		namespaceXMLElementKitMap.put(MAQRO_NAMESPACE_URI.toString(), maqroXMLElementKit);	//associate the MAQRO XML element kit with the MAQRO namespace
		//	TODO bring back if needed		swingStylesheetApplier=new SwingXMLCSSStylesheetApplier();	//create a new Swing stylesheet applier
		xmlStylesheetApplier = new XMLCSSStylesheetApplier(getURIInputStreamable()); //create a new XML stylesheet applier, using ourselves as the input stream locator
	}

	/**
	 * Creates a copy of the editor kit.
	 * @return A copy of the XML editor kit.
	 */
	public Object clone() {
		return new XMLEditorKit(getMediaType(), getURIInputStreamable());
	} //TODO why do we need this?; make a real clone, or make sure XHTMLEditorKit overrides this

	/**
	 * Returns a factory for producing views for models that use this editor kit.
	 * @return A factory to produce views for this editor kit.
	 * @see DefaultXMLViewFactory
	 */
	public ViewFactory getViewFactory() {
		return defaultViewFactory;
	}

	/**
	 * Returns a controller for handling hyperlinks.
	 * @return A class to control hyperlinking.
	 * @see DefaultXMLLinkController
	 */
	public XMLLinkController getLinkController() {
		return defaultLinkController;
	}

	/**
	 * Create an uninitialized text storage model that is appropriate for this type of editor. This version return a Swing XML document.
	 * @return The model.
	 */
	public XMLDocument createDefaultDocument() {
		return new XMLDocument(getURIInputStreamable()); //create an XML document, passing along our source of input streams
	}

	/**
	 * Reads a given publication and stores it in the given document.
	 * @param publicationURL The URL of the OEB publication which has the information to load.
	 * @param doc The destination for the insertion.
	 * @param pos The location in the document to place the content. TODO decide if we want/need this
	 * @throws IOException on any I/O error
	 * @throws BadLocationException if pos represents an invalid location within the document.
	 * @throws RuntimeException (will eventually be a BadLocationException) if pos is invalid.
	 */

	/*TODO fix somehow
	TODO fix
		protected void readXML(final URL xmlDocumentURL) {	//G**fix throws IOException, BadLocationException


							//TODO it would be nice here to simply delegate this to the XMLEditorKit here
							final InputStream xmlInputStream=publicationURL.openConnection().getInputStream();		//connect to the URL and get an input stream
							final XMLProcessor xmlProcessor=new XMLProcessor();	//create a new XML processor
							final com.globalmentor.text.xml.XMLDocument xmlDocument=xmlProcessor.parseDocument(xmlInputStream, publicationURL);	//parse the document
										//TODO do a normalize() somewhere here
							xmlDocument.getStyleSheetList().add(new DefaultOEBCSSStyleSheet());	//add the default stylesheet for OEB
							final XMLCSSProcessor cssProcessor=new XMLCSSProcessor();	//create a new CSS processor
							cssProcessor.parseStyles(xmlDocument, publicationURL);	//parse this document's styles
									//TODO check to make sure the styles are valid OEB styles somewhere here
							cssProcessor.applyxStyles(xmlDocument);	//apply the styles
							tidyOEBXMLDocument(xmlDocument);	//tidy up the document (an important step if the document has text directly in the body and such) TODO test, comment

		//TODO del when works					calculateTargetIDs(xmlDocument, publicationURL);	//TODO testing; comment
		//TODO del when works					oebDocument.insert(0, xmlDocument);	//TODO testing
		//TODO bring back or fix					((com.globalmentor.swing.text.xml.oeb.OEBDocument)doc).insert(0, xmlDocument);	//TODO testing
		//TODO fix					oebDocument.insert(0, new com.globalmentor.text.xml.XMLDocument[]{xmlDocument});	//TODO testing
							baseURLArray=new URL[]{publicationURL}; //create an array of URLs with our one URL
							xmlDocumentArray=new com.globalmentor.text.xml.XMLDocument[]{xmlDocument};  //create an array with just our OEB document
	*/

	/**
	 * Inserts content from the given stream. If <code>doc</code> is an instance of HTMLDocument, this will read html 3.2 text. Inserting html into a non-empty
	 * document must be inside the body Element, if you do not insert into the body an exception will be thrown. When inserting into a non-empty document all tags
	 * outside of the body (head, title) will be dropped.
	 *
	 * @param reader The stream to read from
	 * @param document The destination for the insertion.
	 * @param pos The location in the document to place the content.
	 * @throws IOException on any I/O error
	 * @throws BadLocationException if pos represents an invalid location within the document.
	 * @throws RuntimeException (will eventually be a BadLocationException) if pos is invalid.
	 */
	public void read(final Reader reader, final Document document, final int pos) throws IOException, BadLocationException {
		/*TODO fix
				if(document instanceof XMLDocument) {	//if this is a Swing XML document
					XMLDocument swingXMLDocument=(XMLDocument)document; //cast the document to an XML document
					final URI baseURI=swingXMLDocument.getBaseURI();  //get the base URI from the document
					final XMLProcessor xmlProcessor=new XMLProcessor();	//create a new XML processor
					final org.w3c.dom.Document xmlDocument=xmlProcessor.parseDocument(reader, baseURI);	//parse the document
					xmlDocument.normalize();  //normalize the document
		*/
		/*TODO del if not needed
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
		*/
		/*TODO fix
					setXML(xmlDocument, baseURI, getMediaType(), swingXMLDocument);  //TODO fix
				}
				else  //if this is not an XML document we're reading into
		*/
		super.read(reader, document, pos); //let the parent class do the reading
	}

	/**
	 * Inserts content from the given stream which is expected to be in a format appropriate for this kind of content handler.
	 * @param inputStream The stream to read from.
	 * @param document The destination for the insertion.
	 * @param pos The location in the document to place the content &gt;= 0.
	 * @throws IOException Thrown on any I/O error
	 * @throws BadLocationException Thrown if pos represents an invalid location within the document.
	 */
	public void read(InputStream inputStream, Document document, int pos) throws IOException, BadLocationException {
		if(document instanceof XMLDocument) { //if this is a Swing XML document
			XMLDocument swingXMLDocument = (XMLDocument)document; //cast the document to an XML document
			final URI baseURI = swingXMLDocument.getBaseURI(); //get the base URI from the document
			final org.w3c.dom.Document xmlDocument = XML.parse(inputStream, baseURI, true);
			/*TODO del
						final XMLProcessor xmlProcessor=new XMLProcessor();	//create a new XML processor
						final org.w3c.dom.Document xmlDocument=xmlProcessor.parseDocument(inputStream, baseURI);	//parse the document
			*/
			xmlDocument.normalize(); //normalize the document
			/*TODO del if not needed
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
			*/
			setXML(xmlDocument, baseURI, getMediaType(), swingXMLDocument); //TODO fix
		} else
			//if this is not an XML document we're reading into
			super.read(inputStream, document, pos); //let the parent class do the reading
	}

	/**
	 * Write content from a document to the given stream in a format appropriate for this kind of content handler. Currently the position and length are ignored
	 * and the entire document is written.
	 * @param writer The writer to write to
	 * @param document The source of the data to write.
	 * @param pos The location in the document to fetch the content (&gt;=0).
	 * @param len The amount to write out (&gt;=0).
	 * @throws IOException Thrown if any I/O error occurs.
	 * @throws BadLocationException Thrown if the position represents an invalid location within the document.
	 */
	//TODO fix when XMLSerializer supports writers	public void write(final Writer writer, final Document document, final int pos, final int len) throws IOException, BadLocationException

	/**
	 * Writes content from a document to the given stream in a format appropriate for this kind of content handler. Currently the position and length are ignored
	 * and the entire document is written. By default UTF-8 encoding is used.
	 * @param outputStream The stream to write to.
	 * @param document The source of the data to write.
	 * @param pos The location in the document to fetch the content (&gt;=0).
	 * @param len The amount to write out (&gt;=0).
	 * @throws IOException Thrown if any I/O error occurs.
	 * @throws BadLocationException Thrown if the position represents an invalid location within the document.
	 */
	public void write(final OutputStream outputStream, final Document document, final int pos, final int len) throws IOException, BadLocationException {
		write(outputStream, UTF_8, document, pos, len); //write using UTF-8
	}

	/**
	 * Writes content from a document to the given stream in a format appropriate for this kind of content handler. Currently the position and length are ignored
	 * and the entire document is written.
	 * @param outputStream The stream to write to.
	 * @param charset The charset to use when serializing.
	 * @param document The source of the data to write.
	 * @param pos The location in the document to fetch the content (&gt;=0).
	 * @param len The amount to write out (&gt;=0).
	 * @throws IOException Thrown if any I/O error occurs.
	 * @throws BadLocationException Thrown if the position represents an invalid location within the document.
	 */
	public void write(final OutputStream outputStream, final Charset charset, final Document document, final int pos, final int len) throws IOException,
			BadLocationException {
		if(document instanceof XMLDocument) { //if the document is an XML document
			final org.w3c.dom.Document xmlDocument = getXML(((XMLDocument)document)); //create an XML document from given Swing document
			final XMLSerializer xmlSerializer = new XMLSerializer(); //create an XML serializer TODO fix the formatted argument
			xmlSerializer.serialize(xmlDocument, outputStream, charset); //write the document to the output stream using the specified encoding
		} else
			//if the document is not an XML document
			super.write(outputStream, document, pos, len); //do the default writing
	}

	/**
	 * Called when the editor kit is being installed into the <code>JEditorPane</code>. This version adds listeners so that the editor kit can be notified of
	 * mouse events in order to correctly generate hyperlink events.
	 * @param editorPane The editor pane into which this editor kit is being installed.
	 */
	public void install(JEditorPane editorPane) {
		Log.trace("installing XMLEditorKit"); //TODO del
		unregisterViewFactories(); //unregister all registered view factories
		if(editorPane instanceof XMLTextPane) { //if we're being installed into an XML text pane
			final XMLTextPane xmlTextPane = (XMLTextPane)editorPane; //cast the editor pane to a text pane
			//get all registered view factories from the XML text pane
			final Iterator<String> viewFactoryNamespaceIterator = xmlTextPane.getViewFactoryNamespaceIterator(); //get an iterator to all namespaces of intalled editor kits
			while(viewFactoryNamespaceIterator.hasNext()) { //while there are more namespaces
				final String namespaceURI = viewFactoryNamespaceIterator.next(); //get the next namespace for which a view factory is installed
				//TODO del Log.trace("setting view factory registered for namespace: ", namespaceURI); //TODO del
				final ViewFactory registeredViewFactory = xmlTextPane.getViewFactory(namespaceURI); //get the view factory associated with this namespace
				registerViewFactory(namespaceURI, registeredViewFactory); //register this view factory with the the namespace
			}
			//get all registered link controllers from the XML text pane
			final Iterator<String> linkControllerNamespaceIterator = xmlTextPane.getLinkControllerNamespaceIterator(); //get an iterator to all namespaces of intalled link controllers
			while(linkControllerNamespaceIterator.hasNext()) { //while there are more namespaces
				final String namespaceURI = linkControllerNamespaceIterator.next(); //get the next namespace for which a link controller is installed
				final XMLLinkController registeredLinkController = xmlTextPane.getLinkController(namespaceURI); //get the link controller associated with this namespace
				registerLinkController(namespaceURI, registeredLinkController); //register this link controller with the the namespace
			}
		}
		editorPane.addMouseListener(getLinkController()); //tell the editor pane we want our link handler to listen for mouse events
		editorPane.addMouseMotionListener(getLinkController()); //tell the editor pane we want our link handler to listen for mouse movements
		super.install(editorPane); //do the default installation
	}

	/**
	 * Called when the editor kit is being removed from the <code>JEditorPane</code>. This is used to unregister any listeners that were attached.
	 * @param editorPane The editor pane into which this editor kit was installed.
	 */
	public void deinstall(JEditorPane editorPane) {
		unregisterViewFactories(); //unregister all registered view factories
		unregisterLinkControllers(); //unregister all registered link controllers
		editorPane.removeMouseListener(getLinkController()); //remove our link handler from the editor pane's list of mouse listeners
		editorPane.removeMouseMotionListener(getLinkController()); //tell the editor pane we no longer care about mouse movements
		super.deinstall(editorPane); //do the default uninstalling
	}

	/**
	 * Fetches the command list for the editor. This is the list of commands supported by the superclass augmented by the collection of commands defined locally
	 * for such things as page operations.
	 * @return The command list
	 */
	public Action[] getActions() {
		return TextAction.augmentList(super.getActions(), DEFAULT_ACTIONS);
	}

	//document information storage methods

	/**
	 * Sets the given XML data in the document.
	 * @param xmlDocument The XML document to set in the Swing document.
	 * @param baseURI The base URI, corresponding to the XML document.
	 * @param mediaType The media type of the XML document.
	 * @param swingXMLDocument The Swing document into which the XML will be set.
	 */
	public void setXML(final org.w3c.dom.Document xmlDocument, final URI baseURI, final ContentType mediaType, final XMLDocument swingXMLDocument) {
		setXML(new ContentData[] { new ContentData<org.w3c.dom.Document>(xmlDocument, baseURI, mediaType) }, swingXMLDocument); //set the XML data, creating an array with a single element
	}

	/**
	 * Sets the given XML data in the document.
	 * @param contentDataArray the array of data objects to insert into the document.
	 * @param swingXMLDocument The Swing document into which the XML will be set.
	 */
	public void setXML(final ContentData<?>[] contentDataArray, final XMLDocument swingXMLDocument) {
		/*TODO fix
				if(false) {	//TODO testing newstuff 
					swingXMLDocument.create(xmlDocumentArray, baseURIArray, mediaTypeArray);	//TODO testing newstuff
					
				}
				else
		*/
		{
			final XMLCSSStylesheetApplier stylesheetApplier = getXMLStylesheetApplier(); //get the stylesheet applier
			stylesheetApplier.clearStyles(); //clear any styles that were present before
			//create a list of element specs for creating the document and store them here
			final DefaultStyledDocument.ElementSpec[] elementSpecList = createElementSpecs(contentDataArray, swingXMLDocument);
			stylesheetApplier.clearStyles(); //clear the styles; we're done with the XML document so we don't need the mappings anymore
			swingXMLDocument.create(elementSpecList); //create the document from the element specs
		}

		Log.trace("Finished creating document, length: " + swingXMLDocument.getLength());

		//TODO del elementSpecList[elementSpecList.length-1].setDirection(DefaultStyledDocument.ElementSpec.JoinPreviousDirection);	//TODO fix

		/*TODO fix---this seems to work! make sure there is an ending '\n' before deleting the last character
		try
		{
		Log.trace("*****************\n****************\n***********");
			if(swingXMLDocument.getLength()>0)
				Log.trace("last character: \""+swingXMLDocument.getText(swingXMLDocument.getLength()-1, 1)+"\"");
			if(swingXMLDocument.getLength()>1)
				Log.trace("second to last character: \""+swingXMLDocument.getText(swingXMLDocument.getLength()-2, 1)+"\"");

		Log.trace("removing after-last character");
			if(swingXMLDocument.getLength()>0)
				swingXMLDocument.remove(swingXMLDocument.getLength()-1, 1);

			if(swingXMLDocument.getLength()>0)
				Log.trace("last character: \""+swingXMLDocument.getText(swingXMLDocument.getLength()-1, 1)+"\"");
			if(swingXMLDocument.getLength()>1)
				Log.trace("second to last character: \""+swingXMLDocument.getText(swingXMLDocument.getLength()-2, 1)+"\"");

		}
		catch (BadLocationException e)
		{
			Log.error(e);	//TODO del all this
		}
		*/

		//TODO testing; put in correct place		swingDocument.applyStyles(); //TODO testing; put in the correct place, and make sure this gets called when repaginating, if we need to
		swingXMLDocument.applyStyles(); //TODO testing; put in the correct place, and make sure this gets called when repaginating, if we need to
	}

	/**
	 * Creates element spec objects from an XML document tree.
	 * @param xmlDocument The XML document tree.
	 * @param baseURI The base URI of the document.
	 * @param mediaType The media type of the document.
	 * @param swingXMLDocument The Swing document into which the XML will be set.
	 * @return Am array of element specs defining the XML document.
	 */
	/*TODO del if not needed
		protected DefaultStyledDocument.ElementSpec[] createElementSpecs(org.w3c.dom.Document xmlDocument, final URI baseURI, final ContentType mediaType, final XMLDocument swingXMLDocument)
		{
			return createElementSpecs(new org.w3c.dom.Document[]{xmlDocument}, new URI[]{baseURI}, new ContentType[]{mediaType}, swingXMLDocument);  //put the XML document into an array, create the element specs, and return them
		}
	*/

	/**
	 * Creates element spec objects from a list of XML document trees.
	 * @param contentDataArray the array of data objects to insert into the document.
	 * @param swingXMLDocument The Swing document into which the XML will be set.
	 * @return An array of element specs defining the XML documents.
	 */
	protected DefaultStyledDocument.ElementSpec[] createElementSpecs(final ContentData<?>[] contentDataArray, final XMLDocument swingXMLDocument) {
		final List<DefaultStyledDocument.ElementSpec> elementSpecList = new ArrayList<DefaultStyledDocument.ElementSpec>(); //create an array to hold our element specs
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(null, DefaultStyledDocument.ElementSpec.StartTagType)); //create the beginning of a Swing element to enclose all elements
		for(int i = 0; i < contentDataArray.length; ++i) { //look at each content data
			if(i > 0) { //if this is not the first data to insert
				//TODO check to see if we should actually do this, first (from the CSS attributes)
				//TODO del System.out.println("Adding page break element.");	//TODO del
				appendElementSpecListPageBreak(elementSpecList); //append a page break
			}
			final ContentData<?> contentData = contentDataArray[i]; //get a reference to this content data
			final MutableAttributeSet contentDataAttributeSet = appendElementSpecList(elementSpecList, contentData, swingXMLDocument); //append element specs for this content data
			//add the document attributes to the base attribute set 
			final URI baseURI = contentData.getBaseURI(); //get a reference to the base URI
			final ContentType mediaType = contentData.getContentType(); //get a reference to the media type
			final RDFResource description = contentData.getDescription(); //get a description of the content
			if(baseURI != null) { //if there is a base URI
				XMLStyles.setBaseURI(contentDataAttributeSet, baseURI); //add the base URI as an attribute
				XMLStyles.setTargetURI(contentDataAttributeSet, baseURI); //because this element is the root of the document, its base URI acts as a linking target as well; store the target URI for quick searching
			}
			if(mediaType != null) { //if there is a media type
				XMLStyles.setMediaType(contentDataAttributeSet, mediaType); //add the media type as an attribute
			}
			if(description != null) { //if there is a description
				XMLStyles.setDocumentDescription(contentDataAttributeSet, description); //add the description as an attribute
			}

		}
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(null, DefaultStyledDocument.ElementSpec.EndTagType)); //finish the element that encloses all the documents
		return (DefaultStyledDocument.ElementSpec[])elementSpecList.toArray(new DefaultStyledDocument.ElementSpec[elementSpecList.size()]);
	}

	/**
	 * Appends element spec objects from content data. Child classes can override this method for processing of custom content data.
	 * @param elementSpecList The list of element specs to be inserted into the document.
	 * @param contentData The content to be inserted into the document.
	 * @param swingXMLDocument The Swing document into which the content will be set.
	 * @return The attribute set for the content data root.
	 * @throws IllegalArgumentException if the given content data is not recognized or is not supported.
	 */
	protected MutableAttributeSet appendElementSpecList(final List<DefaultStyledDocument.ElementSpec> elementSpecList, final ContentData<?> contentData,
			final XMLDocument swingXMLDocument) {
		final Object contentDataObject = contentData.getObject(); //get the content data object
		if(contentDataObject instanceof org.w3c.dom.Document) { //if this is XML document content data
			return appendXMLDocumentElementSpecList(elementSpecList, (ContentData<org.w3c.dom.Document>)contentData, swingXMLDocument); //append XML content
		}
		/*TODO fix
				else if(contentDataObject instanceof Activity) {	//if this is a MAQRO activity
					return appendMAQROActivityElementSpecList(elementSpecList, (ContentData<Activity>)contentData, swingXMLDocument);	//append MAQRO activity content
				}
		*/
		else { //if we don't recognize this content data
			throw new IllegalArgumentException("Unrecognized content type " + contentData.getObject().getClass().getName());
		}
	}

	/**
	 * Appends element spec objects from MAQRO activity content data.
	 * @param elementSpecList The list of element specs to be inserted into the document.
	 * @param contentData The MAQRO activity content to be inserted into the document.
	 * @param swingXMLDocument The Swing document into which the content will be set.
	 * @return The attribute set for the MAQRO activity.
	 */
	/*TODO fix
		protected MutableAttributeSet appendMAQROActivityElementSpecList(final List<DefaultStyledDocument.ElementSpec> elementSpecList, final ContentData<? extends Activity> contentData, final XMLDocument swingXMLDocument)
		{
			final Activity activity=contentData.getObject();	//get a reference to this activity
			final URI baseURI=contentData.getBaseURI(); //get a reference to the base URI
			final ContentType mediaType=contentData.getContentType(); //get a reference to the media type
			return getMAQROXMLElementKit().appendElementSpecList(elementSpecList, activity, baseURI);	//delegate to the MAQRO XML element kit to append the activity
		}
	*/

	/**
	 * Appends element spec objects from XML document content data.
	 * @param elementSpecList The list of element specs to be inserted into the document.
	 * @param contentData The XML document content to be inserted into the document.
	 * @param swingXMLDocument The Swing document into which the content will be set.
	 * @return The attribute set for the XML document.
	 */
	protected MutableAttributeSet appendXMLDocumentElementSpecList(final List<DefaultStyledDocument.ElementSpec> elementSpecList,
			final ContentData<? extends org.w3c.dom.Document> contentData, final XMLDocument swingXMLDocument) {
		final org.w3c.dom.Document xmlDocument = contentData.getObject(); //get a reference to this document
		xmlDocument.normalize(); //TODO do we want to do this here? probably not---or maybe so. Maybe we can normalize on the fly in the Swing document, not in the source
		final URI baseURI = contentData.getBaseURI(); //get a reference to the base URI
		final ContentType mediaType = contentData.getContentType(); //get a reference to the media type
		final org.w3c.dom.Element xmlDocumentElement = xmlDocument.getDocumentElement(); //get the root of the document
		final URI publicationBaseURI = swingXMLDocument.getBaseURI(); //get the base URI of the publication TODO do we need to check this for null?
		//if there is a publication, see if we have a description of this resource in the manifest
		final RDFResource description = contentData.getDescription();
		//TODO put description in base element
		/*TODO fix
					//TODO make sure the stylesheet applier correctly distinguishes between document base URI for internal stylesheets and publication base URI for package-level base URIs
				final CSSStyleSheet[] stylesheets=getXMLStylesheetApplier().getStylesheets(xmlDocument, baseURI, mediaType, description);	//TODO testing
				for(int i=0; i<stylesheets.length; getXMLStylesheetApplier().applyStyleSheet(stylesheets[i++], xmlDocumentElement));	//TODO testing
					//TODO make sure stylesheets get applied later, too, in our Swing stylesheet application routine
				getXMLStylesheetApplier().applyLocalStyles(xmlDocumentElement);	//apply local styles to the document TODO why don't we create one routine to do all of this?
		*/
		final MutableAttributeSet documentAttributeSet = appendElementSpecList(elementSpecList, xmlDocumentElement, baseURI); //insert this document's root element into our list our list of elements
		final DocumentType documentType = xmlDocument.getDoctype(); //get the XML document's doctype, if any
		if(documentType != null) { //if this document has a doctype
			if(documentType.getPublicId() != null) //if the document has a public ID
				XMLStyles.setXMLDocTypePublicID(documentAttributeSet, documentType.getPublicId()); //store the public ID
			if(documentType.getSystemId() != null) //if the document has a public ID
				XMLStyles.setXMLDocTypeSystemID(documentAttributeSet, documentType.getSystemId()); //store the system ID
		}
		//store the processing instructions
		final List processingInstructionList = getNodesByName(xmlDocument, Node.PROCESSING_INSTRUCTION_NODE, "*", false); //get a list of all the processing instructions in the document TODO use a constant here
		if(processingInstructionList.size() > 0) { //if there are processing instructions
			final NameValuePair[] processingInstructions = new NameValuePair[processingInstructionList.size()]; //create enough name/value pairs for processing instructions
			for(int processingInstructionIndex = 0; processingInstructionIndex < processingInstructionList.size(); ++processingInstructionIndex) { //look at each of the processing instruction nodes
				final ProcessingInstruction processingInstruction = (ProcessingInstruction)processingInstructionList.get(processingInstructionIndex); //get a reference to this processing instruction
				processingInstructions[processingInstructionIndex] = new NameValuePair<String, String>(processingInstruction.getTarget(),
						processingInstruction.getData()); //create a name/value pair from the processing instruction
				/*TODO del when works
										//add an attribute representing the processing instruction, prepended by the special characters for a processing instruction
									attributeSet.addAttribute(XMLStyleConstants.XML_PROCESSING_INSTRUCTION_ATTRIBUTE_START+processingInstruction.getTarget(), processingInstruction.getData());
				*/
			}
			XMLStyles.setXMLProcessingInstructions(documentAttributeSet, processingInstructions); //add the processing instructions
		}
		/*TODO fix
					if(XHTMLSwingTextUtilities.isHTMLDocumentElement(documentAttributeSet);	//see if this is an HTML document
					{
						if(childAttributeSet instanceof MutableAttributeSet) {	//TODO testing
							final MutableAttributeSet mutableChildAttributeSet=(MutableAttributeSet)childAttributeSet;
							mutableChildAttributeSet.addAttribute("$hidden", Boolean.TRUE);	//TODO testing
												
						}
					}
		*/
		return documentAttributeSet; //return the attribute set of the document
	}

	/**
	 * Appends information from an XML element tree into a list of element specs. The map of XML element kits is searched to attempt to find an element kit to
	 * create element specs for the given XML element.
	 * @param elementSpecList The list of element specs to be inserted into the document.
	 * @param xmlElement The XML element tree.
	 * @param baseURI The base URI of the document, used for generating full target URIs for quick searching.
	 * @return The attribute set used to represent the element; this attribute set can be manipulated after the method returns.
	 */
	protected MutableAttributeSet appendElementSpecList(final List<DefaultStyledDocument.ElementSpec> elementSpecList, final org.w3c.dom.Element xmlElement,
			final URI baseURI) {
		/*TODO del
				final String namespaceURI=xmlElement.getNamespaceURI();	//get the namespace URI of the element
				XMLElementKit xmlElementKit=namespaceXMLElementKitMap.get(namespaceURI);	//see if there is an XML element kit registered with this namespace
				if(xmlElementKit!=null) {	//TODO del; testing
					Log.trace("found element kit for", namespaceURI);
				}
				if(xmlElementKit==null) {	//if there is no registered XML element kit
					xmlElementKit=getDefaultXMLElementKit();	//use the default XML element kit
				}
				return xmlElementKit.appendElementSpecList(elementSpecList, xmlElement, baseURI);	//tell the XML element kit to create element specs
		*/
		return getDefaultXMLElementKit().appendElementSpecList(elementSpecList, xmlElement, baseURI); //tell the default XML element kit to create element specs, which will delegate as needed to other XML element kits
	}

	/**
	 * Appends a page break to the element spec list.
	 * @param elementSpecList The list of element specs to be inserted into the document.
	 * @see XMLDocument#insert
	 */
	protected void appendElementSpecListPageBreak(final List<DefaultStyledDocument.ElementSpec> elementSpecList) {
		//TODO del Log.trace("XMLDocument.appendElementSpecListPageBreak()");	//TODO del
		final SimpleAttributeSet pageBreakAttributeSet = new SimpleAttributeSet(); //create a page break attribute set TODO create this and keep it in the constructor for optimization
		//TODO del if we can get away with it		XMLStyleConstants.setXMLElementName(pageBreakAttributeSet, XMLCSSStyleConstants.AnonymousAttributeValue); //show by its name that this is an anonymous box TODO maybe change this to setAnonymous
		XMLStyles.setPageBreakView(pageBreakAttributeSet, true); //show that this element should be a page break view
		final XMLCSSStyleDeclaration cssStyle = new XMLCSSStyleDeclaration(); //create a new style declaration
		cssStyle.setDisplay(CSS.CSS_DISPLAY_BLOCK); //show that the page break element should be a block element, so no anonymous blocks will be created around it
		XMLCSSStyles.setXMLCSSStyle(pageBreakAttributeSet, cssStyle); //store the constructed CSS style in the attribute set
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(pageBreakAttributeSet, DefaultStyledDocument.ElementSpec.StartTagType)); //create the beginning of a page break element spec
		//TODO fix		final SimpleAttributeSet contentAttributeSet=new SimpleAttributeSet();	//create a new attribute for this content
		//add a dummy object replacment character so that this element will have some text to represent
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(null, DefaultStyledDocument.ElementSpec.ContentType,
				new char[] { Characters.OBJECT_REPLACEMENT_CHAR }, 0, 1));
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(pageBreakAttributeSet, DefaultStyledDocument.ElementSpec.EndTagType)); //finish the page break element spec
	}

	/**
	 * Creates an attribute set for the described element.
	 * @param elementNamespaceURI The namespace of the XML element, or <code>null</code> if the namespace is not known.
	 * @param elementQName The qualified name of the XML element.
	 * @return An attribute set reflecting the CSS attributes of the element.
	 */
	/*TODO del if not needed
		public static MutableAttributeSet createAttributeSet(final URI elementNamespaceURI, final String elementQName)
		{
			return createAttributeSet(elementNamespaceURI, elementQName, null);  //create an attribute set with no style
		}
	*/

	/**
	 * Creates an attribute set for the described element.
	 * @param elementNamespaceURI The namespace of the XML element, or <code>null</code> if the namespace is not known.
	 * @param elementQName The qualified name of the XML element.
	 * @param style The CSS style to be used for the attribute set, or <code>null</code> if the CSS style is not known.
	 * @return An attribute set reflecting the CSS attributes of the element.
	 */
	/*TODO del if not needed
		public static MutableAttributeSet createAttributeSet(final URI elementNamespaceURI, final String elementQName, final CSSStyleDeclaration style)
		{
			final SimpleAttributeSet attributeSet=new SimpleAttributeSet();	//create a new attribute for this element
			XMLStyleUtilities.setXMLElementName(attributeSet, elementQName);	//store the element's name in the attribute set
			if(elementNamespaceURI!=null)  //if the element has a namespace URI specified
				XMLStyleUtilities.setXMLElementNamespaceURI(attributeSet, elementNamespaceURI.toString());	//store the element's namespace URI in the attribute set
			final String localName=XMLUtilities.getLocalName(elementQName);  //get the element's local name from the qualified name
			XMLStyleUtilities.setXMLElementLocalName(attributeSet, localName);	//store the element's local name in the attribute set
			if(style!=null) //if style was given TODO should we instead do this unconditionally?
				XMLCSSStyleUtilities.setXMLCSSStyle(attributeSet, style);	//store the CSS style in the attribute set
			return attributeSet;	//return the attribute set we created
		}
	*/

	//document information retrieval methods

	/**
	 * Converts the Swing document to an XML document.
	 * <p>
	 * This is a cover method for <code>createXMLDocument</code>.
	 * </p>
	 * @param swingXMLDocument The Swing document from which the XML will be retrieved.
	 * @return A DOM tree representing the XML document.
	 * @see #createXMLDocument
	 */
	public org.w3c.dom.Document getXML(final XMLDocument swingXMLDocument) {
		return createXMLDocument(swingXMLDocument); //create an XML document from the Swing document
	}

	/**
	 * Converts the Swing document to an XML document.
	 * @param swingXMLDocument The Swing document from which the XML will be retrieved.
	 * @return A DOM tree representing the XML document.
	 */
	protected org.w3c.dom.Document createXMLDocument(final XMLDocument swingXMLDocument) {
		final Element rootSwingElement = swingXMLDocument.getRootElements()[0]; //get the first root element of the document -- this contains an element tree for each document loaded
		assert rootSwingElement.getElementCount() > 0 : "No Swing root element."; //assert there is at least one root element
		//TODO del		if(rootSwingElement.getElementCount()>0)  //if there is at least one root element
		final Element swingDocumentElement = rootSwingElement.getElement(0); //get the first element, which is the root of the document tree
		return createXMLDocument(swingDocumentElement); //create and return a document from this element
	}

	/**
	 * Converts the given Swing element tree to an XML document.
	 * @param swingElement The Swing element containing the data to be converted to an XML document.
	 * @return A DOM tree representing the XML document.
	 */
	protected org.w3c.dom.Document createXMLDocument(final Element swingElement) {
		final AttributeSet attributeSet = swingElement.getAttributes(); //get the element's attribute set
		assert attributeSet != null : "Missing attributes for document element."; //assert that we have an attribute set
		final String elementName = XMLStyles.getXMLElementName(attributeSet); //get the name of this element
		final DOMImplementation domImplementation = createDocumentBuilder(true).getDOMImplementation(); //create a new DOM implementation
		final DocumentType documentType; //we'll create a document type only if we find a system ID
		final String docTypeSystemID = XMLStyles.getXMLDocTypeSystemID(attributeSet); //get the document type system ID if there is one
		if(docTypeSystemID != null) { //if we found a system ID
			final String docTypePublicID = XMLStyles.getXMLDocTypePublicID(attributeSet); //get the document type public ID if there is one
			documentType = domImplementation.createDocumentType(elementName, docTypePublicID, docTypeSystemID); //create the document type
			/*TODO fix some day to load the entities and use them in serialization			
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
		} else
			//if there was no system ID
			documentType = null; //show that we don't have a document type
		final org.w3c.dom.Document xmlDocument = domImplementation.createDocument(null, elementName, documentType); //create the document
		//create any processing instructions
		final NameValuePair[] processingInstructions = XMLStyles.getXMLProcessingInstructions(attributeSet); //get the processing instructions, if any (this will never return null)
		//look at each processing instruction
		for(int processingInstructionIndex = 0; processingInstructionIndex < processingInstructions.length; ++processingInstructionIndex) {
			final NameValuePair processingInstructionNameValuePair = processingInstructions[processingInstructionIndex]; //get this processing instruction's values
			//create a processing instruction with the correct value
			final ProcessingInstruction processingInstruction = xmlDocument.createProcessingInstruction((String)processingInstructionNameValuePair.getName(),
					(String)processingInstructionNameValuePair.getValue());
			xmlDocument.insertBefore(processingInstruction, xmlDocument.getDocumentElement()); //add this processing instruction TODO do these have to be placed in a certain order---before the document element?
		}
		final org.w3c.dom.Node xmlNode = createXMLNode(xmlDocument, swingElement); //create the root element
		assert xmlNode.getNodeType() == Node.ELEMENT_NODE : "Swing root XML node not an XML element."; //make sure we got back an XML element
		xmlDocument.replaceChild(xmlNode, xmlDocument.getDocumentElement()); //set the document element of the document
		return xmlDocument; //return the document we constructed
	}

	/**
	 * Converts the given Swing element to an XML node.
	 * @param xmlDocument The XML element tree.
	 * @param swingElement The Swing element containing the data to be converted to an XML node.
	 * @return A DOM element representing the Swing node.
	 */
	protected org.w3c.dom.Node createXMLNode(final org.w3c.dom.Document xmlDocument, final Element swingElement) {
		return createXMLNode(xmlDocument, swingElement, 0); //create an XML node at the bottom level
	}

	/**
	 * Converts the given Swing element to an XML node indenting to the given level.
	 * @param xmlDocument The XML element tree.
	 * @param swingElement The Swing element containing the data to be converted to an XML node.
	 * @param level The zero-based level of indentation.
	 * @return A DOM element representing the Swing node.
	 */
	protected org.w3c.dom.Node createXMLNode(final org.w3c.dom.Document xmlDocument, final Element swingElement, final int level) {
		final AttributeSet attributeSet = swingElement.getAttributes(); //get the element's attribute set
		final String elementKind = swingElement.getName(); //get the kind of element this is (based on the name of the Swing element, not the Swing element's attribute which holds the name of its corresponding XML element)
		if(elementKind != null) { //if the element has a kind
			if(elementKind.equals(AbstractDocument.ContentElementName)) { //if this is is content
				try {
					//get the text this content Swing element represents
					final StringBuilder stringBuilder = new StringBuilder(swingElement.getDocument().getText(swingElement.getStartOffset(),
							swingElement.getEndOffset() - swingElement.getStartOffset()));
					//remove every instance of the artificial end-of-block-element character, as well as any hard return that the user might have entered during editing
					StringBuilders.removeEveryChar(stringBuilder, XMLDocument.ELEMENT_END_STRING + '\n');
					return xmlDocument.createTextNode(stringBuilder.toString()); //create a text node with the content and return the node
				} catch(BadLocationException badLocationException) { //in the unlikely event that we try to access a bad location
					throw new AssertionError(badLocationException); //report an error
				}
			}
		}
		assert attributeSet != null : "Missing attributes for element."; //assert that we have an attribute set
		//TODO fix		if(attributeSet!=null)  //if we have an attribute set
		final String elementNamespaceURI = XMLStyles.getXMLElementNamespaceURI(attributeSet); //get the namespace of this element, if it has one
		final String elementName = XMLStyles.getXMLElementName(attributeSet); //get the name of this element
		final org.w3c.dom.Element xmlElement = xmlDocument.createElementNS(elementNamespaceURI, elementName); //create the element
		if(!isEmptyElement(attributeSet)) { //if this element isn't an empty element, we'll add children
			boolean hasBlockChild = false; //we'll see if any of the children have block display; start out assuming they don't
			//TODO del when works			boolean isInlineChild=true; //each time we'll determine whether this is an inline node so that we can add EOLs for pretty printing if not; for now, assume it is inline
			//create and append the child elements
			for(int childIndex = 0; childIndex < swingElement.getElementCount(); ++childIndex) { //look at each of the child elements
				final Element childSwingElement = swingElement.getElement(childIndex); //get this Swing child element
				final org.w3c.dom.Node childXMLNode = createXMLNode(xmlDocument, childSwingElement, level + 1); //create an XML node from the child Swing element, specifying that this node will be at the next hierarchy level
				boolean isInlineChild = true; //start by assuming this is an inline child
				//TODO del when works				final boolean isInlineChild; //we'll determine whether this is an inline node so that we can add EOLs for pretty prining if not
				if(childXMLNode.getNodeType() == Node.ELEMENT_NODE) { //if this is an element
					//get the display CSS property for the child element, but don't resolve up the attribute set parent hierarchy TODO can we be sure this will be a primitive value?
					final CSSPrimitiveValue cssDisplayProperty = (CSSPrimitiveValue)XMLCSSStyles.getCSSPropertyCSSValue(childSwingElement.getAttributes(),
							CSS.CSS_PROP_DISPLAY, false);
					isInlineChild = cssDisplayProperty != null ? //if the child element knows its CSS display
					CSS.CSS_DISPLAY_INLINE.equals(cssDisplayProperty.getStringValue())
							: //see if the display is "inline"
							true; //if there is no display, assume it is inline
				}
				/*TODO del when works
								else  //if this Swing element doesn't represent an XML element
									isInlineChild=true;  //we'll still consider it to be "inline" (it might be just textual content, after all)
				*/
				if(!isInlineChild) { //if the child element is not inline
					hasBlockChild = true; //show that at least one child has block display
					appendText(xmlElement, "\n"); //skip to the next line for a pretty formatted XML document
					appendText(xmlElement, Strings.createString('\t', level + 1)); //indent to the correct level
				}
				xmlElement.appendChild(childXMLNode); //append the XML node we created
				/*TODO del if not needed
							if(!isInlineChild)  //if the child element is not inline
								XMLUtilities.appendText(xmlElement, "\n");  //skip to the next line for a pretty formatted XML document
				*/
			}
			//*G**del when works			if(!isInlineChild)  //if the last child element was not inline
			if(hasBlockChild) { //if any of the children were not inline
				appendText(xmlElement, "\n"); //skip to the next line for a pretty formatted XML document
				appendText(xmlElement, Strings.createString('\t', level)); //indent to the correct level
			}
		}

		//store the attributes
		final Enumeration attributeNameEnumeration = attributeSet.getAttributeNames(); //get an enumeration of attribute names
		while(attributeNameEnumeration.hasMoreElements()) { //while there are more attributes
			final Object attributeNameObject = attributeNameEnumeration.nextElement(); //get this attribute name object
			/*TODO del; why is there a "resolver" attribute with a name of type StyleConstants? Why isn't that a value?
			Log.trace("Current element: ", attributeNameObject); //TODO del
			Log.trace("Current element type: ", attributeNameObject.getClass().getName()); //TODO del
			*/
			final Object attributeValueObject = attributeSet.getAttribute(attributeNameObject); //get the attribute value (don't worry that this searches the hierarchy---we already know this key exists at this level)
			if(attributeValueObject instanceof XMLAttribute) { //if this Swing attribute is an XML attribute 
				final XMLAttribute xmlAttribute = (XMLAttribute)attributeValueObject; //cast the object to an XML attribute
				//set the attribute value in the XML element we're constructing
				xmlElement.setAttributeNS(xmlAttribute.getNamespaceURI(), xmlAttribute.getQName(), xmlAttribute.getValue());
			}
			/*TODO del when works
						if(attributeNameObject instanceof String) {	//if this attribute name is a string
							final String attributeName=(String)attributeNameObject;  //get this attribute name as a string
							if(XMLUtilities.isName(attributeName)) {	//if this is a valid XML name (this will ignore all proprietary Swing attributes
								final Object attributeValue=attributeSet.getAttribute(attributeName);  //get the value of the attribute, which should be a string
								Debug.assert(attributeValue instanceof String, "XML attribute is not a string.");
								xmlElement.setAttributeNS(null, attributeName, attributeValue.toString());  //set the attribute value TODO fix for namespaces
							}
						}
			*/
		}
		return xmlElement; //return the element we created
	}

	/**
	 * Gets the target ID of of the specified element. This ID represents the target of a link. By default this is the value of the "id" attribute. TODO what
	 * about checking the DTD for an element of type ID?
	 * @param attributeSet The attribute set of the element which may contain a target ID.
	 * @return The target ID value of the element, or <code>null</code> if the element does not define a target ID.
	 */
	protected String getTargetID(final AttributeSet attributeSet) { //TODO can any of this be made into a generic XML utility, using the DTD ID type?
		return XMLStyles.getXMLAttributeValue(attributeSet, null, "id"); //return the value of the "id" attribute, if it exists TODO use a constant here
	}

	/**
	 * Determines if the specified element represents an empty element&mdash;an element that might be declared as <code>EMPTY</code> in a DTD.
	 * @param attributeSet The attribute set of the element in question.
	 * @return <code>true</code> if the specified element should be empty.
	 */
	protected boolean isEmptyElement(final AttributeSet attributeSet) {
		return false; //default to no empty elements TODO it would be nice to get this from the DTD
	}

	/**
	 * A factory to build views for an XML document based upon the attributes of each element.
	 * <p>
	 * This XML view factory adds a special capability of defining view factories for specific namespaces. If an XML element is encountered in a particular
	 * namespace and a view factory has been registered for that namespace, the registered view factory will be used to create the view. Otherwise, this view
	 * factory will create a view.
	 * </p>
	 * <p>
	 * As this factory allows the capability of creating multiple views for certain elements, child classes should override <code>create(Element, boolean)</code>
	 * rather than overriding the normal <code>create(Element)</code> method.
	 */
	protected class DefaultXMLViewFactory extends XMLViewFactory {

		/**
		 * Retreives a view factory for the given namespace. This version retrieves any view factory registered in the editor kit for the given namespace.
		 * @param namespaceURI The namespace URI of the element for which a view factory should be returned, or <code>null</code> if the element has not namespace.
		 * @return A view factory for the given namespace, or this view factory if no view factory is registered for the given namespace.
		 */
		protected ViewFactory getViewFactory(final String namespaceURI) {
			final ViewFactory viewFactory = XMLEditorKit.this.getViewFactory(namespaceURI); //see if there is a view factory registered for this namespace in the editor kit
			return viewFactory != null ? viewFactory : super.getViewFactory(namespaceURI); //return the view factory if there is one; if not, let the parent class decide on a view factory
		}
	}

	/**
	 * A link controller that knows how to handle link entry, exit, and activiation.
	 * <p>
	 * This XML link controller uses the locally registered link controllers to determine the appropriate link controller to use for a particular element.
	 * </p>
	 */
	protected class DefaultXMLLinkController extends XMLLinkController {

		/**
		 * Retreives a link controller for the given namespace. This version retrieves any link controller registered in the editor kit for the given namespace.
		 * @param namespaceURI The namespace URI of the element for which a link controller should be returned, or <code>null</code> if the element has no
		 *          namespace.
		 * @return A view factory for the given namespace.
		 */
		protected XMLLinkController getLinkController(final String namespaceURI) {
			final XMLLinkController linkController = XMLEditorKit.this.getLinkController(namespaceURI); //see if there is a link controller registered for this namespace in the editor kit
			return linkController != null ? linkController : super.getLinkController(namespaceURI); //return the link controller if there is one; if not, let the parent class decide on a link controller
		}
	}

	/**
	 * Data to be inserted into the Swing document, such as an XML document or a MAQRO activity.
	 * @author Garret Wilson
	 */
	public static class ContentData<O> {

		/** The content object. */
		private final O object;

		/** @return The content object. */
		public O getObject() {
			return object;
		}

		/** The base URI of the object, or <code>null</code> if no base URI is available.. */
		private final URI baseURI;

		/** @return The base URI of the object, or <code>null</code> if no base URI is available. */
		public URI getBaseURI() {
			return baseURI;
		}

		/** The content type of the object. */
		private final ContentType contentType;

		/** @return The content type of the object. */
		public ContentType getContentType() {
			return contentType;
		}

		/** A description of the object, or <code>null</code> if no description is available. */
		private final RDFResource description;

		/** @return A description of the object, or <code>null</code> if no description is available. */
		public RDFResource getDescription() {
			return description;
		}

		/**
		 * Object and baseURI constructor
		 * @param object The content object.
		 * @param baseURI The base URI of the object, or <code>null</code> if no base URI is available..
		 * @param contentType The content type of the object.
		 * @throws NullPointerException if the object or content type is <code>null</code>.
		 */
		public ContentData(final O object, final URI baseURI, final ContentType contentType) {
			this(object, baseURI, contentType, null); //construct the data with no description
		}

		/**
		 * Object, baseURI, and description constructor
		 * @param object The content object.
		 * @param baseURI The base URI of the object, or <code>null</code> if no base URI is available..
		 * @param contentType The content type of the object.
		 * @param description A description of the object, or <code>null</code> if no description is available.
		 * @throws NullPointerException if the object or content type is <code>null</code>.
		 */
		public ContentData(final O object, final URI baseURI, final ContentType contentType, final RDFResource description) {
			this.object = requireNonNull(object, "Object cannot be null");
			this.baseURI = baseURI;
			//TODO del when works			this.baseURI=checkNull(baseURI, "Base URI cannot be null");
			this.contentType = requireNonNull(contentType, "Content type cannot be null");
			this.description = description;
		}
	}

	/**
	 * An XML document to be inserted into the Swing document.
	 * @author Garret Wilson
	 */
	/*TODO del if not needed
		protected static class XMLDocumentData extends ContentData<org.w3c.dom.Document>
		{
	*/
	/**
	 * Document and baseURI constructor
	 * @param document The content document.
	 * @param baseURI The base URI of the object.
	 * @throws NullPointerException if the object or base URI is <code>null</code>.
	 */
	/*TODO del if not needed
			public XMLDocumentData(final org.w3c.dom.Document document, final URI baseURI)
			{
				this(document, baseURI, null);	//construct the data with no description
			}
	*/

	/**
	 * Document, baseURI, and description constructor
	 * @param document The content document.
	 * @param baseURI The base URI of the object.
	 * @param description A description of the object, or <code>null</code> if no description is available.
	 * @throws NullPointerException if the object or base URI is <code>null</code>.
	 */
	/*TODO del if not needed
			public XMLDocumentData(final org.w3c.dom.Document document, final URI baseURI, final RDFResource description)
			{
				super(document, baseURI, description);	//construct the parent class
			}
		}
	*/

	/**
	 * A kit for creating element specs.
	 * @author Garret Wilson
	 */
	public interface XMLElementKit {

		/**
		 * Appends information from an XML element tree into a list of element specs.
		 * @param elementSpecList The list of element specs to be inserted into the document.
		 * @param xmlElement The XML element tree.
		 * @param baseURI The base URI of the document, used for generating full target URIs for quick searching.
		 * @return The attribute set used to represent the element; this attribute set can be manipulated after the method returns.
		 */
		public MutableAttributeSet appendElementSpecList(final List<DefaultStyledDocument.ElementSpec> elementSpecList, final org.w3c.dom.Element xmlElement,
				final URI baseURI);

	}

	/**
	 * An element kit that knows how to create element specs based upon XML elements.
	 * @author Garret Wilson
	 */
	public class DefaultXMLElementKit implements XMLElementKit {

		/** The set XML elements we are currently examining; used to prevent infinite recursion when delegating to XML element kits. */
		private final Set<org.w3c.dom.Element> delegatedXMLElementSet = new IdentityHashSet<org.w3c.dom.Element>();

		/**
		 * Appends information from an XML element tree into a list of element specs.
		 * @param elementSpecList The list of element specs to be inserted into the document.
		 * @param xmlElement The XML element tree.
		 * @param baseURI The base URI of the document, used for generating full target URIs for quick searching.
		 * @return The attribute set used to represent the element; this attribute set can be manipulated after the method returns.
		 */
		public MutableAttributeSet appendElementSpecList(final List<DefaultStyledDocument.ElementSpec> elementSpecList, final org.w3c.dom.Element xmlElement,
				final URI baseURI) {
			if(!delegatedXMLElementSet.contains(xmlElement)) { //if we haven't yet tried to create element specs for this XML element
				final String namespaceURI = xmlElement.getNamespaceURI(); //get the namespace URI of the element
				final XMLElementKit xmlElementKit = namespaceXMLElementKitMap.get(namespaceURI); //see if there is an XML element kit registered with this namespace
				if(xmlElementKit != null) { //if we have an XML element kit registered for this namespace
					delegatedXMLElementSet.add(xmlElement); //show that we're getting ready to delegate to this XML element kit
					try {
						return xmlElementKit.appendElementSpecList(elementSpecList, xmlElement, baseURI); //delegate to the installed XML element kit
					} finally {
						delegatedXMLElementSet.remove(xmlElement); //always remove the element from the set of delegated XML elements
					}
				}
			}
			//	TODO del Log.trace("XMLDocument.appendElementSpecList: element ", xmlElement.getNodeName());	//TODO del
			final MutableAttributeSet attributeSet = createAttributeSet(xmlElement, baseURI); //create and fill an attribute set based upon this element's CSS style
			//	TODO del Log.trace("Attribute set: ", attributeSet);  //TODO del
			//	TODO fix if(!"null".equals(xmlElement.getLocalName()))	//TODO testing
			elementSpecList.add(new DefaultStyledDocument.ElementSpec(attributeSet, DefaultStyledDocument.ElementSpec.StartTagType)); //create the beginning of a Swing element to model this XML element
			appendElementSpecListContent(elementSpecList, xmlElement, attributeSet, baseURI); //append the content of the element
			//	TODO fix if(!"null".equals(xmlElement.getLocalName()))	//TODO testing
			elementSpecList.add(new DefaultStyledDocument.ElementSpec(attributeSet, DefaultStyledDocument.ElementSpec.EndTagType)); //finish the element we started at the beginning of this function
			return attributeSet; //return the attribute set used for the element
		}

		/**
		 * Appends the tree contents of an XML node (not including the element tag) into a list of element specs.
		 * @param elementSpecList The list of element specs to be inserted into the document.
		 * @param node The XML node tree, such as an element or a document fragment.
		 * @param attributeSet The attribute set of the element.
		 * @param baseURI The base URI of the document, used for generating full target URIs for quick searching.
		 * @see XMLDocument#insert
		 * @see DefaultXMLElementKit#appendElementSpecList(List, org.w3c.dom.Element, URI)
		 */
		protected void appendElementSpecListContent(final List<DefaultStyledDocument.ElementSpec> elementSpecList, final Node node,
				final MutableAttributeSet attributeSet, final URI baseURI) {
			final NodeList childNodeList = node.getChildNodes(); //get the list of child nodes
			final int childNodeCount = childNodeList.getLength(); //see how many child nodes there are
			if(childNodeCount > 0) { //if this element has children
				for(int childIndex = 0; childIndex < childNodeCount; childIndex++) { //look at each child node
					final Node childNode = childNodeList.item(childIndex); //look at this node
					appendElementSpecListNode(elementSpecList, childNode, baseURI); //append this node's information
				}

				/*TODO fix; transferred elsewhere
				//	TODO fix			assert node.getParentNode() instanceof org.w3c.dom.Element;	//TODO fix
				//	TODO fix			final org.w3c.dom.Element parentElement=(org.w3c.dom.Element)node.getParentNode();  //get the parent element
							final CSSStyleDeclaration cssStyle=xmlCSSStylesheetApplier.getStyle(xmlElement);
								//see if the element is inline (text is always inline
							final boolean isInline=XMLCSSUtilities.isDisplayInline(cssStyle);
							if(!isInline)
							{
								appendElementSpecListContent(elementSpecList, xmlElement, null, baseURI, "\n");	//TODO testing
							}
				*/
			} else { //if this element has no children, we'll have to add dummy text
				final char dummyChar; //we'll decide which character to use for the dummy text
				if(isEmptyElement(attributeSet)) { //if this element should remain empty
					XMLStyles.setXMLEmptyElement(attributeSet, true); //show that this is an empty element TODO see if this is the best way to do this and make sure this gets set for object added during editing
					dummyChar = Characters.OBJECT_REPLACEMENT_CHAR; //use the object replacement character as dummy text, because there can never be real text added
				} else { //if this element might have text at some point
					dummyChar = '\n'; //use an EOL character TODO make sure this is a block element---this could probably really screw up an inline element with no content
				}
				//add a dummy replacment character so that this element will have some text to represent
				elementSpecList.add(new DefaultStyledDocument.ElementSpec(null, DefaultStyledDocument.ElementSpec.ContentType, new char[] { dummyChar }, 0, 1));
			}
		}

		/**
		 * Appends information from an XML child node into a list of element specs.
		 * @param elementSpecList The list of element specs to be inserted into the document.
		 * @param node The XML element's child node tree.
		 * @param baseURI The base URI of the document, used for generating full target URIs for quick searching, or <code>null</code> if there is no base URI or if
		 *          the base URI is not applicable.
		 * @return The attribute set used to represent the node; this attribute set can be manipulated after the method returns.
		 * @see XMLDocument#insert
		 * @see DefaultXMLElementKit#appendElementSpecListContent
		 */
		protected MutableAttributeSet appendElementSpecListNode(final List<DefaultStyledDocument.ElementSpec> elementSpecList, final org.w3c.dom.Node node,
				final URI baseURI) {
			//	TODO del Log.trace("appending element spec list node: ", node.getNodeName());  //TODO del
			switch(node.getNodeType()) { //see which type of object this is
				case Node.ELEMENT_NODE: //if this is an element
					return appendElementSpecList(elementSpecList, (org.w3c.dom.Element)node, baseURI); //insert this element into our element spec list
				case Node.TEXT_NODE: //if this is a text node
				case Node.CDATA_SECTION_NODE: //if this is a CDATA section node
				{
					//TODO see if this really slows things down
					final MutableAttributeSet textAttributeSet = createAttributeSet(node, baseURI); //create and fill an attribute set
					appendElementSpecListContent(elementSpecList, node, textAttributeSet, baseURI, node.getNodeValue()); //append the content
					return textAttributeSet; //return the attribute set of the text
				}
				default: //TODO fix for inserting unknown nodes into the Swing document
					return new SimpleAttributeSet(); //create and return a new, empty attribute set 
			}
		}

		/**
		 * Appends child text into a list of element specs.
		 * @param elementSpecList The list of element specs to be inserted into the document.
		 * @param node The XML node that contains the content, or <code>null</code> if there is no node representing the text (text is being inserted manually).
		 * @param text The text to be inserted.
		 * @param attributeSet The attribute set representing the text, or <code>null</code> if default attributes should be used.
		 * @param baseURI The base URI of the document, used for generating full target URIs for quick searching, or <code>null</code> if there is no base URI or if
		 *          the base URI is not applicable.
		 * @see XMLDocument#insert
		 * @see DefaultXMLElementKit#appendElementSpecListContent
		 */
		protected void appendElementSpecListContent(final List<DefaultStyledDocument.ElementSpec> elementSpecList, final org.w3c.dom.Node node,
				final AttributeSet attributeSet, final URI baseURI, final String text) { //TODO remove the node parameter if not needed
			final AttributeSet textAttributeSet;
			if(attributeSet != null) { //if there are no attributes provided (artificial text is being manually inserted, for instance)
				textAttributeSet = attributeSet; //use the attribute set provided	
			} else { //if there are no attributes provided (artificial text is being manually inserted, for instance)
				final SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet(); //create a new attribute for this content
				XMLStyles.setXMLElementName(simpleAttributeSet, TEXT_NODE_NAME); //set the name of the content to ensure it will not get its name from its parent element (this would happen if there was no name explicitly set)
				textAttributeSet = simpleAttributeSet; //use the default atribute set we created
			}
			//	TODO del Log.trace("inserting text data: \""+node.getNodeValue()+"\"");  //TODO del
			final StringBuilder textStringBuilder = new StringBuilder(text); //TODO testing
			if(textStringBuilder.length() > 0) { //if there is actually content (don't add empty text)
				//	TODO del Log.trace("before collapsing whitespace: ", textStringBuilder);  //TODO del

				StringBuilders.collapse(textStringBuilder, Characters.WHITESPACE_CHARACTERS, " "); //TODO testing
				//	TODO del Log.trace("after collapsing whitespace: ", textStringBuilder);  //TODO del
				//	TODO del Log.trace("Adding text with attributes: ", contentAttributeSet);	//TODO del
				//	TODO fix textStringBuilder.append(CharacterConstants.WORD_JOINER_CHAR);	//TODO testing
				//	TODO fix textStringBuilder.append(CharacterConstants.ZERO_WIDTH_NO_BREAK_SPACE_CHAR);	//TODO testing
				//	TODO fix textStringBuilder.append(ELEMENT_END_CHAR);	//put a dummy character at the end of the element so that caret positioning will work correctly at the end of block views

				//	TODO del	if(node.getParentNode()!=null && "null".equals(XMLStyleUtilities.getXMLElementLocalName(attributeSet.getResolveParent())))
				/*TODO fix
							if(node.getParentNode()!=null && "div".equals(node.getParentNode().getLocalName())) {	//TODO testing; fix
				textStringBuilder.append('\n');	//TODO testing
								
							}
				*/

				if(node != null && node.getParentNode() instanceof org.w3c.dom.Element) {
					final org.w3c.dom.Element parentElement = (org.w3c.dom.Element)node.getParentNode(); //get the parent element
					if(parentElement.getChildNodes().item(parentElement.getChildNodes().getLength() - 1) == node) { //if this is the last node
						final CSSStyleDeclaration cssStyle = getXMLStylesheetApplier().getStyle(parentElement);
						//see if the element is inline (text is always inline
						final boolean isInline = CSS.isDisplayInline(cssStyle);
						if(!isInline) {
							textStringBuilder.append('\n'); //TODO testing
						}
					}
				}

				final String content = textStringBuilder.toString(); //convert the string buffer to a string
				elementSpecList.add(new DefaultStyledDocument.ElementSpec(textAttributeSet, DefaultStyledDocument.ElementSpec.ContentType, content.toCharArray(), 0,
						content.length()));
			}
		}

		/**
		 * Creates an attribute set for the described element.
		 * @param elementNamespaceURI The namespace of the XML element, or <code>null</code> if the namespace is not known.
		 * @param elementQName The qualified name of the XML element.
		 * @return An attribute set reflecting the CSS attributes of the element.
		 */
		public MutableAttributeSet createAttributeSet(final URI elementNamespaceURI, final String elementQName) {
			return createAttributeSet(elementNamespaceURI, elementQName, null); //create an attribute set with no style
		}

		/**
		 * Creates an attribute set for the described element.
		 * @param elementNamespaceURI The namespace of the XML element, or <code>null</code> if the namespace is not known.
		 * @param elementQName The qualified name of the XML element.
		 * @param style The CSS style to be used for the attribute set, or <code>null</code> if the CSS style is not known.
		 * @return An attribute set reflecting the CSS attributes of the element.
		 */
		public MutableAttributeSet createAttributeSet(final URI elementNamespaceURI, final String elementQName, final CSSStyleDeclaration style) {
			final SimpleAttributeSet attributeSet = new SimpleAttributeSet(); //create a new attribute for this element
			XMLStyles.setXMLElementName(attributeSet, elementQName); //store the element's name in the attribute set
			if(elementNamespaceURI != null) //if the element has a namespace URI specified
				XMLStyles.setXMLElementNamespaceURI(attributeSet, elementNamespaceURI.toString()); //store the element's namespace URI in the attribute set
			final String localName = getLocalName(elementQName); //get the element's local name from the qualified name
			XMLStyles.setXMLElementLocalName(attributeSet, localName); //store the element's local name in the attribute set
			if(style != null) //if style was given TODO should we instead do this unconditionally?
				XMLCSSStyles.setXMLCSSStyle(attributeSet, style); //store the CSS style in the attribute set
			return attributeSet; //return the attribute set we created
		}

		/**
		 * Creates an attribute set for the given XML node.
		 * @param xmlNode The XML node, such as an element or text.
		 * @param baseURI The base URI of the document, used for generating full target URIs for quick searching, or <code>null</code> if there is no base URI or if
		 *          the base URI is not applicable.
		 * @return An attribute set reflecting the CSS attributes of the element.
		 */
		protected MutableAttributeSet createAttributeSet(final Node xmlNode, final URI baseURI) {
			final String namespaceURI = xmlNode.getNamespaceURI(); //get the node namespace URI
			final MutableAttributeSet attributeSet = createAttributeSet(namespaceURI != null ? URI.create(namespaceURI) : null, xmlNode.getNodeName()); //create a new attribute for this node
			//TODO give every attribute set a default empty CSS style; later fix this in the application section to create as needed and to clear them before application
			//	TODO del when moved to the set-style routines		XMLCSSStyleConstants.setXMLCSSStyle(attributeSet, new XMLCSSStyleDeclaration());	//give every attribute set a default empty CSS style
			switch(xmlNode.getNodeType()) { //see what type of node for which to create an attribute set
				case Node.ELEMENT_NODE: //if this node is an element
				{
					final org.w3c.dom.Element xmlElement = (org.w3c.dom.Element)xmlNode; //cast the node to an element
					final CSSStyleDeclaration cssStyle = getXMLStylesheetApplier().getStyle(xmlElement); //see if we've already applied a style to this element
					if(cssStyle != null) { //if we know the style of the XML element
						XMLCSSStyles.setXMLCSSStyle(attributeSet, cssStyle); //store the style in the attributes of our Swing element	
					} else {
						//give every attribute set a default empty CSS style; if not, this will cause huge performance hits when trying to create them on the fly when styles are applied TODO recheck
						XMLCSSStyles.setXMLCSSStyle(attributeSet, new XMLCSSStyleDeclaration());
					}
					final NamedNodeMap attributeNodeMap = xmlElement.getAttributes(); //get a reference to the attributes
					//store the XML attributes
					for(int attributeIndex = 0; attributeIndex < attributeNodeMap.getLength(); ++attributeIndex) { //look at each of the attributes
						final Attr xmlAttribute = (Attr)attributeNodeMap.item(attributeIndex); //get a reference to this attribute
						//add this XML attribute to the Swing atribute set as the value of our special XML attribute key
						XMLStyles.addXMLAttribute(attributeSet, xmlAttribute.getNamespaceURI(), xmlAttribute.getNodeName(), xmlAttribute.getNodeValue());
					}
					final String targetID = getTargetID(attributeSet); //get the target ID specified in the attribute set
					if(targetID != null) { //if this attribute set has a target ID
						try {
							final URI targetURI = URIs.resolveRawFragment(baseURI, targetID); //create a full URI from the target ID used as a fragment
							XMLStyles.setTargetURI(attributeSet, targetURI); //store the target URI for quick searching
						} catch(IllegalArgumentException illegalArgumentException) {
						} //ignore any errors and simply don't store the target URL
					}
				}
					break;
				case Node.TEXT_NODE: //if this is a text node
				case Node.CDATA_SECTION_NODE: //if this is a CDATA section node
					break; //do nothing---the node is already set up
			}
			return attributeSet; //return the attribute set we created
		}

	}

}

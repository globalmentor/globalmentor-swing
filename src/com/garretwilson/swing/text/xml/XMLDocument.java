package com.garretwilson.swing.text.xml;

import java.awt.Image;  //G***del when loading routines are placed elsewhere
//G***del import java.awt.MediaTracker;  //G***del when loading routines are placed elsewhere
import java.awt.Toolkit;  //G***del when loading routines are placed elsewhere
import java.lang.ref.*;
import static java.text.MessageFormat.*;
import java.util.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.*;
import javax.mail.internet.ContentType;
import javax.sound.sampled.*;
import javax.swing.event.*;
import javax.swing.text.*;

import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleSheet;

import com.garretwilson.io.*;
import com.garretwilson.lang.*;
import com.garretwilson.rdf.*;
import com.garretwilson.rdf.xpackage.XPackageUtilities;
import com.garretwilson.swing.event.ProgressEvent;
import com.garretwilson.swing.text.BasicStyledDocument;
import com.garretwilson.swing.text.SwingTextUtilities;
import com.garretwilson.text.Characters;
import com.garretwilson.text.xml.stylesheets.css.AbstractXMLCSSStylesheetApplier;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSStyleDeclaration;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSUtilities; //G***maybe move
import com.garretwilson.sound.sampled.SampledSoundUtilities;
import com.garretwilson.swing.text.xml.css.XMLCSSStyleUtilities;
import com.garretwilson.swing.text.xml.css.XMLCSSStyleContext;
import com.garretwilson.util.Debug;
import com.garretwilson.util.NameValuePair;
//G***del when works import com.garretwilson.swing.text.xml.css.XMLCSSSimpleAttributeSet;
import com.globalmentor.marmot.Marmot;

/**A document that models XML.
@see com.garretwilson.text.xml.XMLProcessor
@see com.garretwilson.text.xml.XMLDocument
@author Garret Wilson
*/
public class XMLDocument extends BasicStyledDocument
{

	/**The task of applying a stylesheet.*/
	public final static String APPLY_STYLESHEET_TASK="applyStylesheet";

	/**The character used to mark the end of an element so that caret positioning
		will work correctly at the end of block views.
	*/
//G***fix	final static char ELEMENT_END_CHAR=CharacterConstants.ZERO_WIDTH_NO_BREAK_SPACE_CHAR;	
		//G***fix; the ZWNBSP seems to make Swing want to break a line early or something
//G***fix final static char ELEMENT_END_CHAR=CharacterConstants.ZERO_WIDTH_SPACE_CHAR;	

//G***fix final static char ELEMENT_END_CHAR='\n';	

final static char ELEMENT_END_CHAR=Characters.ZERO_WIDTH_SPACE_CHAR;	
final static String ELEMENT_END_STRING=String.valueOf(ELEMENT_END_CHAR);	
//G***fix final static char ELEMENT_END_CHAR=CharacterConstants.ZERO_WIDTH_NO_BREAK_SPACE_CHAR;	
//G***fix	final static char ELEMENT_END_CHAR=CharacterConstants.PARAGRAPH_SIGN_CHAR;	

	/**A map of soft references to resources that have been loaded.*/
	private final Map<URI, Reference<Object>> resourceReferenceMap=new HashMap<URI, Reference<Object>>();

	/**Returns a cached resource identified by the URI, if the object's memory has not been reclaimed.
	@param resourceURI The URI of the requested resource.
	@return The resource, if it has been cached and is still referenced in the JVM,
		or <code>null</code> if the resource's memory has been reclaimed or the object has never been cached.
	*/
	protected Object getCachedResource(final URI resourceURI)
	{
		final Reference<Object> resourceReference=resourceReferenceMap.get(resourceURI); //return a reference to the cached resource, if available
		if(resourceReference!=null) //if we found a reference to the resource
		{
			final Object resource=resourceReference.get();  //get the resource itself
			if(resource!=null)  //if we still have the resource cached
				return resource;  //return the resource
			else
				resourceReferenceMap.remove(resourceURI);  //remove the reference from the cache, since it is no longer useful
		}
		return null;  //show that either the object wasn't cached, or its memory has been reclaimed
	}

	/**Stores a resource as a reference in the cache. The resource will only
		stay in the cache until the JVM decides its needs to reclaim its memory.
	@param resourceURI The URI of the resource being cached.
	@param resource The resource to cache.
	*/
	protected void putCachedResource(final URI resourceURI, final Object resource)
	{
			//store the resource in the map as a soft reference
	  resourceReferenceMap.put(resourceURI, new SoftReference<Object>(resource));
	}

	/**The object that applies stylesheets to the document.*/
	private final SwingXMLCSSStylesheetApplier swingStylesheetApplier;

		/**@return The object that applies stylesheets to the document.*/
		protected SwingXMLCSSStylesheetApplier getSwingStylesheetApplier() {return swingStylesheetApplier;}

	/**Constructor.
	@param uriInputStreamable The source of input streams for resources.
	@exception NullPointerException if the new source of input streams is <code>null</code>.
	*/
	public XMLDocument(final URIInputStreamable uriInputStreamable)
	{
//G***fix		super(new PureGapContent(BUFFER_SIZE_DEFAULT), new XMLCSSStyleContext());	//construct the parent class, specifying our own type of style context that knows how to deal with CSS attributes
		super(new XMLCSSStyleContext(), uriInputStreamable);	//construct the parent class, specifying our own type of style context that knows how to deal with CSS attributes
		swingStylesheetApplier=new SwingXMLCSSStylesheetApplier();	//create a new Swing stylesheet applier
	}


		/**
		 * Constructs an html document with the default content
		 * storage implementation and the given style/attribute
		 * storage mechanism.
		 *
		 * @param styles the styles
		 */
/*G***fix
		public HTMLDocument(StyleSheet styles) {
	this(new GapContent(BUFFER_SIZE_DEFAULT), styles);
		}
*/




	/**Creates the root element to be used to represent the default document
		structure. G***make this somehow know what type of document to make -- what
		vocabulary. For now, we'll default to HTML.
	@return The element base.
	*/
	protected AbstractElement createDefaultRoot()
	{
		return super.createDefaultRoot(); //G***testing
/*G***fix
		final XMLCSSStyleDeclaration blockCSSStyle=new XMLCSSStyleDeclaration(); //create a new style declaration
		blockCSSStyle.setDisplay(XMLCSSConstants.CSS_DISPLAY_BLOCK);	//make the style declaration display: block
		final MutableAttributeSet htmlAttributeSet=createAttributeSet("html", null, blockCSSStyle);  //G***testing; comment; use a constant; fix namespace
		final MutableAttributeSet bodyAttributeSet=createAttributeSet("body", null, blockCSSStyle);  //G***testing; comment; use a constant; fix namespace
		final MutableAttributeSet pAttributeSet=createAttributeSet("p", null, blockCSSStyle);  //G***testing; comment; use a constant; fix namespace
//G***del		XMLCSSStyleConstants.setParagraphView(pAttributeSet, true);	//show that the paragraph element should have a paragraph view


		writeLock();  //grab a write-lock for this initialization and abandon it during initialization so in normal operation we can detect an illegitimate attempt to mutate attributes
		final Element[] buff=new Element[1];  //create an element array for insertion of elements

		final BranchElement section=new SectionElement(); //create a new section
		final BranchElement html=new BranchElement(section, htmlAttributeSet); //create a new paragraph to represent the document
		final BranchElement body=new BranchElement(html, bodyAttributeSet); //create a new paragraph to represent the HTML body
		final BranchElement p=new BranchElement(body, pAttributeSet); //create a new paragraph to represent the paragraph
		final LeafElement leaf=new LeafElement(p, null, 0, 1);  //create the leaf element
		buff[0]=leaf; //insert the leaf
		p.replace(0, 0, buff);
		buff[0]=p;  //insert the p
		body.replace(0, 0, buff);
		buff[0]=body;  //insert the body
		html.replace(0, 0, buff);
*/

/*G***del

			BranchElement paragraph = new BranchElement(section, null);

			LeafElement brk = new LeafElement(paragraph, null, 0, 1);
			buff[0] = brk;
			paragraph.replace(0, 0, buff);

			final Element[] sectionBuffer=new Element[2];  //G***testing
			sectionBuffer[0] = html;
			sectionBuffer[1] = paragraph;
			section.replace(0, 0, sectionBuffer);
*/
/*G***fix
		buff[0]=html;  //insert the html
		section.replace(0, 0, buff);
		writeUnlock();
		return section;
*/
  }

	/**Creates an attribute set for the given XML node.
	@param node The XML node, such as an element or text.
	@param baseURI The base URI of the document, used for generating full target
		URIs for quick searching, or <code>null</code> if there is no base URI or
		if the base URI is not applicable.
	@return An attribute set reflecting the CSS attributes of the element.
	*/
/*TODO decide if we want this
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
					if(cssStyle!=null)
					{
						XMLCSSStyleUtilities.setXMLCSSStyle(attributeSet, cssStyle);	
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
*/

	/**Gets a particular resource from the given location.
	@param href
G***comment
*/
/*G***fix
	public Object getResource(final String href)
*/

	/**Gets a particular resource from the given location. If the resource is
		cached, the cached copy will be returned. If the document is loaded, it will
		be stored in the local weak cache.
	The return types for particular media types are as follows:
	<ul>
		<li>image/* - <code>java.awt.Image</code> The image may not be loaded.</li>
		<li>audio/* - <code>javax.sound.sampled.Line</code> Usually this will be
			of type <code>javax.sound.sampled.Clip</code> and will have been opened.</li>
	</ul>
	@param href The specified location of the resource.
	@return The specified resource.
	@exception URISyntaxException Thrown if the given location results in a syntactically incorrect URI.
	@exception IOException Thrown if the specified resource cannot be retrieved.
	*/
	public Object getResource(final String href) throws URISyntaxException, IOException
	{
		final ContentType mediaType=getResourceMediaType(href);	//get the media type of the resource
		if(mediaType!=null)	//if we think we know the media type of the file involved
		{
			final URI resourceURI=getResourceURI(href);	//create a URI based upon the base URI and the given file location
		  return getResource(resourceURI, mediaType); //get the resource from its URI and its media type
		}
		else
			throw new IOException(href+" has an unrecognized media type.");	//G***i18n
	}

	/**Gets a particular resource from the given location. If the resource is
		cached, the cached copy will be returned. If the document is loaded, it will
		be stored in the local weak cache.
	The return types for particular media types are as follows:
	<ul>
		<li>image/* - <code>java.awt.Image</code> The image may not be loaded.</li>
		<li>audio/* - <code>javax.sound.sampled.Line</code> Usually this will be
			of type <code>javax.sound.sampled.Clip</code> and will have been opened.</li>
	</ul>
	@param uri The URI location of the resource.
	@param mediaType The media type of the resource.
	@return The specified resource.
	@exception IOException Thrown if the specified resource cannot be retrieved.
	*/
	protected Object getResource(final URI uri, final ContentType mediaType) throws IOException
	{
		Object resource=getCachedResource(uri); //see if the resource is cached
		if(resource!=null)  //if the resource was cached
		{
			if(resource instanceof Clip)  //if this resource is a clip G***hack; fix to have a special getClip() method
			{
				final Clip clip=(Clip)resource; //cast the resource to a clip
				if(clip.isRunning())  //if the clip is already running
					clip.stop();  //stop playing the clip
				clip.setFramePosition(0); //start at the beginning of the clip
			}
			return resource;  //return the resource
		}
		else  //if the resource wasn't cached
			return loadResource(uri, mediaType);  //load and return the resource
	}

	/**Gets the URI of a particular resource. If the given <code>href</code> is
		relative, it is correctly normalized to an absolute URI. This version
		assumes relative locations are relative to the base URI unless the base
		URI is <code>null</code>, in which case the href is assumed to be
		absolute.
	@param href The specified location of the resource.
	@return The URI of the specified resource.
	@exception IllegalArgumentException if the given string violates RFC&nbsp;2396.
	@see #getBaseURI
	*/
	public URI getResourceURI(final String href)
	{
		return getBaseURI().resolve(href);	//create and return a URI based upon the base URI, if any, and the given file location
	}

	/**Gets the media type of a particular resource.
	@param href The specified location of the resource.
	@return The media type of the specified resource, or <code>null</code> if
		the media type cannot be determined.
	*/
	public ContentType getResourceMediaType(final String href)
	{
		ContentType mediaType=null;	//we start out not knowing the media type of the resource
		final RDFResource publication=getPublication();	//get the publication description
		if(publication!=null)	//if there is a description of the publication
		{
/*TODO fix with URF
				//get the manifest resource which represents the requested resource
			final RDFResource resource=XPackageUtilities.getManifestItemByLocationHRef(publication, getBaseURI(), href);
		  if(resource!=null) //if the item is listed in the manifest
			{
				mediaType=Marmot.getMediaType(resource);  //get the resource's media type
			}
*/
		}
		if(mediaType==null)	//if we couldn't find a media type from the publication description
		{
			mediaType=Files.getMediaType(href);  //get the media type from the extension of the href, if any
		}
		return mediaType;	//return the media type we found, if any
	}

	/**Opens an input stream to the given location, based upon the document's
		base URI. The input stream should be closed when it is no longer needed.
	@param href The specified location of the resource.
	@return An open input stream to the resource.
	@exception URISyntaxException Thrown if the given location results in a syntactically incorrect URI.
	@exception IOException Thrown if an input stream to the specified resource
		cannot be created.
	@see #getBaseURI
//G***check about returning null if the resource is not found
	*/
	public InputStream getResourceAsInputStream(final String href) throws URISyntaxException, IOException
	{
		final URI resourceURI=getResourceURI(href);	//create a URI based upon the base URI and the given file location
		return getResourceAsInputStream(resourceURI); //get an input stream from this URI
	}

	/**Opens an input stream to the given URI. The input stream should be closed
		when it is no longer needed.
	This implementation delegates to <code>getInputStream()</code>.
	@param uri The specified location of the resource.
	@return An open input stream to the resource.
	@exception IOException Thrown if an input stream to the specified resource
		cannot be created.
//G***check about returning null if the resource is not found
 	@see #getInputStream(URI)
	*/
	public InputStream getResourceAsInputStream(final URI uri) throws IOException	//TODO del when we can in favor of getInputStream()
	{
		return getInputStream(uri);	//delegate to our URIInputStreamable method
	}

	/**Loads a particular resource from the given location. The loaded resource
		will be stored in the local weak cache.
	The return types for particular media types are as follows:
	<ul>
		<li>image/* - <code>java.awt.Image</code> The image may not be loaded.</li>
		<li>audio/* - <code>javax.sound.sampled.Line</code> Usually this will be
			of type <code>javax.sound.sampled.Clip</code> and will have been opened.</li>
	</ul>
	@param resourceURI The specified location of the resource.
	@param mediaType The media type of the resource.
	@return The specified resource.
	@exception IOException Thrown if the specified resource cannot be retrieved.
	*/
	protected Object loadResource(final URI resourceURI, final ContentType mediaType) throws IOException  //G***change this to loadImage, loadClip, etc.
	{
		Object resource;  //this will be assigned if we run into no errors
		if(mediaType.getPrimaryType().equals(ContentTypeConstants.IMAGE))	//if this is an image
		{
			final String mediaSubType=mediaType.getSubType(); //get the media sub-type
				//if this is a GIF, JPEG, PNG G***fix, or X_BITMAP image
			if(mediaSubType.equals(ContentTypeConstants.GIF_SUBTYPE) || mediaSubType.equals(ContentTypeConstants.JPEG_SUBTYPE) || mediaSubType.equals(ContentTypeConstants.PNG_SUBTYPE)/*G***fix || mediaSubType.equals(MediaTypeConstants.X_BITMAP)*/)
			{
				//G***since we're opening directly from a file, maybe there is a better way to do this
/*G***this works; fix to use our own caching
				final ImageIcon imageIcon=new javax.swing.ImageIcon(resourceURL);	//create an ImageIcon from the file
				resource=imageIcon.getImage();	//G***change to return an image later
*/
/*G***del when works
				final Toolkit toolkit=Toolkit.getDefaultToolkit(); //get the default toolkit
				final Image image=toolkit.createImage(resourceURL);  //G***testing; does this return null if it doesn't exist?
*/
				final InputStream resourceInputStream=getResourceAsInputStream(resourceURI);  //get an input stream to the resource
				try
				{
					final byte[] imageBytes=InputStreamUtilities.getBytes(resourceInputStream);  //read the bytes from the input stream
					final Toolkit toolkit=Toolkit.getDefaultToolkit();	//get the default toolkit
					final Image image=toolkit.createImage(imageBytes);  //create an image from the bytes
					resource=image; //G***testing
				}
				finally
				{
					resourceInputStream.close();  //always close the input stream after we're finished with it
				}

//G***del when works				ImageUtilities.loadImage(image);  //load the image
			}
			else	//if we don't recognize this image type
				throw new IOException("Unrecognized image type: \""+mediaType.getSubType()+"\"; only \""+ContentTypeConstants.JPEG_SUBTYPE+"\", \""+ContentTypeConstants.PNG_SUBTYPE+"\", and \""+ContentTypeConstants.GIF_SUBTYPE+"\" are currently supported.");	//G***i18n G***fix for other image types
		}
		else if(ContentTypeUtilities.isAudio(mediaType))	//if this is an audio media type
		{
			final InputStream inputStream=new BufferedInputStream(getResourceAsInputStream(resourceURI));	//get a buffered input stream to the audio
//G***we should really close the input stream if something goes wrong
			try
			{
				final Clip clip=(Clip)SampledSoundUtilities.getDataLine(inputStream, Clip.class);	//get a clip from the input stream
				resource=clip;	//return the clip
//G***del				return clip;	//return the clip without caching it, because caching a clip doesn't allow it to be played again
			}
			catch(UnsupportedAudioFileException unsupportedAudioFileException)
			{
				throw (IOException)new IOException("The format of "+resourceURI+" of type "+mediaType+" is unsupported.").initCause(unsupportedAudioFileException);	//G***i18n
			}
			catch(LineUnavailableException lineUnavailableException)
			{
				throw (IOException)new IOException("There is no line available to the audio file "+resourceURI+" of type "+mediaType+".").initCause(lineUnavailableException);	//G***i18n
			}
		}
		else	//if we don't recognize this media type
			throw new IOException("Unrecognized media type: "+mediaType);	//G***i18n
		putCachedResource(resourceURI, resource); //cache the resource in case we need to use it again
		return resource;  //return the resource we found
	}

	/**Inserts a group of new elements into the document
	@param offset the starting offset
	@data the element data
	@exception BadLocationException for an invalid starting offset
	@see StyledDocument#insert
	@exception BadLocationException  if the given position does not
	represent a valid location in the associated document.
	*/
	//G***why do we override this?
	protected void insert(int offset, ElementSpec[] data) throws BadLocationException
	{
		super.insert(offset, data);
	}

		/**
		 * Updates document structure as a result of text insertion.  This
		 * will happen within a write lock.  This implementation simply
		 * parses the inserted content for line breaks and builds up a set
		 * of instructions for the element buffer.
		 *
		 * @param chng a description of the document change
		 * @param attr the attributes
		 */
		protected void insertUpdate(DefaultDocumentEvent chng, AttributeSet attr)
		{
Debug.trace("inside XMLDocument insertupdate");
/*G***del; testing bidiarray
Debug.trace("XMLDocument.insertUpdate()");
        final int chngStart = chng.getOffset();
        final int chngEnd =  chngStart + chng.getLength();
Debug.trace("change start: "+chngStart+" change end: "+chngEnd);
        final int firstPStart = getParagraphElement(chngStart).getStartOffset();
        final int lastPEnd = getParagraphElement(chngEnd).getEndOffset();
Debug.trace("first paragrah start: "+firstPStart+" last paragraph end: "+lastPEnd);

*/


/*G***fix
	if(attr == null) {
			attr = contentAttributeSet;
	}

	// If this is the composed text element, merge the content attribute to it
	else if (attr.isDefined(StyleConstants.ComposedTextAttribute)) {
			((MutableAttributeSet)attr).addAttributes(contentAttributeSet);
	}
*/




	super.insertUpdate(chng, attr);
//G***del		applyxStyles(); //G***testing; put in the correct place, and make sure this gets called when repaginating, if we need to

		}

	/**Initialize the document to reflect the given element structure
		(i.e. the structure reported by the <code>getDefaultRootElement</code>
		method. If the document contained any data it will first be removed.
	<p>This version is given public access so that it can be accessed by the editor kit.</p>
	@param elementSpecs The array of element specifications that define the document.
	@see XMLEditorKit#setXML(org.w3c.dom.Document[], URI[], MediaType[], XMLDocument)
	*/
	public void create(ElementSpec[] elementSpecs)
	{
		super.create(elementSpecs);	//create the document normally
		try		//remove the ending dumming '\n' added by Swing
		{
			if(getLength()>0)	//if we have any characters
			{
					//TODO del; this doesn't even work, as getLength() and remove() ignore the ending '\n'---this only removed the '\n' already present in the content
				final String text=getText(getLength()-1, 1);	//TODO comment
				if("\n".equals(text))	//if the document ends with an end-of-line character
				{
					remove(getLength()-1, 1);	//remove the last end-of-line character
				}
			}
/*G***del when works
			if(getLength()>1)	//if we have more than one character
			{
				final String text=getText(getLength()-2, 2);
				if("\n\n".equals(text))	//if the document ends with two end-of-line characters
				{
					remove(getLength()-1, 1);	//remove the last end-of-line character
				}
			}
*/
		}
		catch(BadLocationException badLocationException)
		{
			throw (AssertionError)new AssertionError(badLocationException.getMessage()).initCause(badLocationException);
		}

//	G***fix		applyStyles(); //G***testing; put in the correct place, and make sure this gets called when repaginating, if we need to

/*G***fix
		writeLock();	//lock the document for writing G***do we really need to do this, as applying styles doesn't modify the document?
		final Element rootSwingElement=getRootElements()[0]; //get the first root element of the document -- this contains an element tree for each document loaded
		final int swingDocumentElementCount=rootSwingElement.getElementCount(); //find out how many root elements there are
		for(int swingDocumentElementIndex=0; swingDocumentElementIndex<swingDocumentElementCount; ++swingDocumentElementIndex) //look at each root element, each of which represents an XML document
		{
			final Element swingDocumentElement=rootSwingElement.getElement(swingDocumentElementIndex);  //get the first element, which is the root of the document tree
			insertBlockElementEnds(swingDocumentElement);	//G***testing
		}
		writeUnlock();	//release the document writing lock
*/
	}


	/**Sets the given XML data in the document.
	@param xmlDocumentArray The array of XML documents to set in the Swing document.
	@param baseURIArray The array of base URIs, corresponding to the XML documents.
	@param mediaTypeArray The array of media types of the documents.
	@param swingXMLDocument The Swing document into which the XML will be set.
	*/
/*TODO decide if we want this
	public void create(final org.w3c.dom.Document[] xmlDocumentArray, final URI[] baseURIArray, final ContentType[] mediaTypeArray)
	{
Debug.trace("creating document with XML documents", xmlDocumentArray.length);
		try
		{
Debug.trace("ready to create, old length is:", getLength());
	    if(getLength()!=0)	//if there is any content
	    {
	    	remove(0, getLength());	//remove the content
Debug.trace("tried to remove everything, new length is:", getLength());
	    }
	    writeLock();	//get a write lock on the document
	    try
			{
	    	
	//G***TODO make our own gap content without an implied break			final Content content=getContent();	//get the current content
				final Content content=getContent();	//get the current content
				UndoableEdit contentEdit;
				{
					final StringBuilder stringBuilder=new StringBuilder();	//G***testing
					for(int xmlDocumentIndex=0; xmlDocumentIndex<xmlDocumentArray.length; ++xmlDocumentIndex)	//look at each of the documents they passed to us
					{
		//	G***del Debug.trace("Looking at XML document: ", xmlDocumentIndex); //G***del
						final org.w3c.dom.Document xmlDocument=xmlDocumentArray[xmlDocumentIndex];	//get a reference to this document
			xmlDocument.normalize();	//G***do we want to do this here? probably not---or maybe so. Maybe we can normalize on the fly in the Swing document, not in the source
						if(xmlDocumentIndex>0)	//if this is not the first document to insert
						{
							stringBuilder.append(CharacterConstants.OBJECT_REPLACEMENT_CHAR);	//append a character for the page break element to represent
						}
						getContent(xmlDocument, stringBuilder);
					}
					contentEdit=content.insertString(0, stringBuilder.toString());
				}
	    	final SectionElement sectionElement=new SectionElement();	//create a section element for all the data
	    	final Element[] childElements=new Element[xmlDocumentArray.length*2-1];	//create a new array of child elements, allowing for interspersed page breaks
	    	int offset=0;
				for(int xmlDocumentIndex=0; xmlDocumentIndex<xmlDocumentArray.length; ++xmlDocumentIndex)	//look at each of the documents they passed to us
				{
	//	G***del Debug.trace("Looking at XML document: ", xmlDocumentIndex); //G***del
					final org.w3c.dom.Document xmlDocument=xmlDocumentArray[xmlDocumentIndex];	//get a reference to this document
//G***del		xmlDocument.normalize();	//G***do we want to do this here? probably not---or maybe so. Maybe we can normalize on the fly in the Swing document, not in the source
					final URI baseURI=baseURIArray[xmlDocumentIndex]; //get a reference to the base URI
					final ContentType mediaType=mediaTypeArray[xmlDocumentIndex]; //get a reference to the media type
					final org.w3c.dom.Element xmlDocumentElement=xmlDocument.getDocumentElement();	//get the root of the document
					final XMLCSSStylesheetApplier xmlCSSStylesheetApplier=getXMLStylesheetApplier();	//G***testing
					final CSSStyleSheet[] stylesheets=xmlCSSStylesheetApplier.getStylesheets(xmlDocument, baseURI, mediaType);	//G***testing
					for(int i=0; i<stylesheets.length; xmlCSSStylesheetApplier.applyStyleSheet(stylesheets[i++], xmlDocumentElement));	//G***testing
					if(xmlDocumentIndex>0)	//if this is not the first document to insert
					{
									//G***check to see if we should actually do this, first (from the CSS attributes)
	//	G***del System.out.println("Adding page break element.");	//G***del
						childElements[xmlDocumentIndex*2-1]=createPageBreakElement(sectionElement, offset);	//put a page break before this document
						offset=childElements[xmlDocumentIndex*2-1].getEndOffset();
					}
					childElements[xmlDocumentIndex*2]=createElement(sectionElement, offset, xmlDocument, baseURI);	//add the child element for this document
					offset=childElements[xmlDocumentIndex*2].getEndOffset();
					final MutableAttributeSet documentAttributeSet=(MutableAttributeSet)childElements[xmlDocumentIndex].getAttributes();
	
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

								//add an attribute representing the processing instruction, prepended by the special characters for a processing instruction
//G***del when works							attributeSet.addAttribute(XMLStyleConstants.XML_PROCESSING_INSTRUCTION_ATTRIBUTE_START+processingInstruction.getTarget(), processingInstruction.getData());

						}
						XMLStyleUtilities.setXMLProcessingInstructions(documentAttributeSet, processingInstructions); //add the processing instructions
					}

//G***fix					if(XHTMLSwingTextUtilities.isHTMLDocumentElement(documentAttributeSet);	//see if this is an HTML document
//G***fix					{
//G***fix						if(childAttributeSet instanceof MutableAttributeSet)	//G***testing
//G***fix						{
//G***fix							final MutableAttributeSet mutableChildAttributeSet=(MutableAttributeSet)childAttributeSet;
//G***fix							mutableChildAttributeSet.addAttribute("$hidden", Boolean.TRUE);	//G***testing												
//G***fix						}
//G***fix					}
//G***fix				}
				sectionElement.replace(0, sectionElement.getChildCount(), childElements);	//add the document children to the section

//G***del				Debug.trace("before creating document, content has length", content.length());
//G***del Debug.trace("ready to insert", stringBuilder.length());

//G***fix				content.insertString(0, stringBuilder.toString());	//TODO find a better way to replace the content
				
	
//G***fix				UndoableEdit cEdit = content.insertString(0, stringBuilder.toString());
//G**fix				final int length=content.length();
				
				
				
				
				final int length=sectionElement.getEndOffset();
Debug.trace("we think the amount of content we added is", length);
		    DefaultDocumentEvent event=new DefaultDocumentEvent(0, length, DocumentEvent.EventType.INSERT);
//G***fix		    event.addEdit(cEdit);
//G***fix buffer.create(length, data, evnt);
				buffer=new ElementBuffer(sectionElement);	//TODO testing

		    // update bidi (possibly)
	//G***del	    super.insertUpdate(evnt, null);
//G***fix		    insertUpdate(event, null);
	//G***fix	    event.end();	//TODO notify the listeners?
//G***fix		    fireInsertUpdate(event);
//G***fix		    fireUndoableEditUpdate(new UndoableEditEvent(this, event));
			}
	    finally
	    {
	    	writeUnlock();	//always release our write lock
Debug.trace("after unlock, content is", getContent().length());
	    }
		}
    catch(final BadLocationException badLocationException)
    {
    	throw new AssertionError(badLocationException);
    }
	}

	protected int getContent(final org.w3c.dom.Document xmlDocument, final StringBuilder stringBuilder)
	{
		return getContent(xmlDocument.getDocumentElement(), stringBuilder);
	}

	protected int getContent(final org.w3c.dom.Element xmlElement, final StringBuilder stringBuilder)
	{
		int childContentLength=0;
		final NodeList childNodeList=xmlElement.getChildNodes();  //get the list of child nodes
		final int childNodeCount=childNodeList.getLength();	//see how many child nodes there are
		for(int childIndex=0; childIndex<childNodeCount; ++childIndex)	//look at each child node
		{
			final Node node=childNodeList.item(childIndex);	//look at this node
			switch(node.getNodeType())	//see which type of object this is
			{
				case Node.ELEMENT_NODE:	//if this is an element
					childContentLength+=getContent((org.w3c.dom.Element)node, stringBuilder);
					break;
				case Node.TEXT_NODE:	//if this is a text node
				case Node.CDATA_SECTION_NODE:	//if this is a CDATA section node
					{
						final int begin=stringBuilder.length();	//get the insertion point
						stringBuilder.append(node.getNodeValue());
						childContentLength+=StringBuilderUtilities.collapse(stringBuilder, CharacterConstants.WHITESPACE_CHARS, " ", begin);	//collapse all whitespace into spaces TODO fix across element boundaries
					}
					break;
			}
		}
		if(childContentLength==0)
		{
			stringBuilder.append(CharacterConstants.OBJECT_REPLACEMENT_CHAR);
			++childContentLength;
		}
		return childContentLength;
	}
*/

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
/*TODO decide if we want this
	protected Element createElement(final Element parentElement, final int offset, final org.w3c.dom.Document xmlDocument, final URI baseURI)
	{
		return createElement(parentElement, offset, xmlDocument.getDocumentElement(), baseURI);
	}
*/

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
/*TODO decide if we want this
	protected Element createElement(final Element parentElement, int offset, final org.w3c.dom.Element xmlElement, final URI baseURI)
	{
		final MutableAttributeSet attributeSet=createAttributeSet(xmlElement, baseURI);	//create an attribute set for this element
		final BranchElement branchElement=new BranchElement(parentElement, attributeSet);	//create a branch Swing element to represent this XML element
		final NodeList childNodeList=xmlElement.getChildNodes();  //get the list of child nodes
		final int childNodeCount=childNodeList.getLength();	//see how many child nodes there are
		final List<Element> childElementList=new ArrayList<Element>(childNodeCount>0 ? childNodeCount : 1);	//create a list of child elements we'll create; we'll never have more than there are XML child nodes (unless there are no child nodes)
		if(childNodeCount>0)	//if this element has children
		{
			for(int childIndex=0; childIndex<childNodeCount; childIndex++)	//look at each child node
			{
				final Node node=childNodeList.item(childIndex);	//look at this node
				switch(node.getNodeType())	//see which type of object this is
				{
					case Node.ELEMENT_NODE:	//if this is an element
							//create and add an element for this child element
						final Element childElement=createElement(branchElement, offset, (org.w3c.dom.Element)node, baseURI);
						offset=childElement.getEndOffset();	//G***testing
						childElementList.add(childElement);
						break;
					case Node.TEXT_NODE:	//if this is a text node
					case Node.CDATA_SECTION_NODE:	//if this is a CDATA section node
						{
							final MutableAttributeSet textAttributeSet=createAttributeSet(node, baseURI);	//create and fill an attribute set for the text node
							final Element textElement=createElement(branchElement, offset, node.getNodeValue(), textAttributeSet);	//create and add an element for text
							offset=textElement.getEndOffset();	//G***testing
Debug.trace("we created a text element, we now think the offset is", offset);
							childElementList.add(textElement);	//create and add an element for text
						}
						break;
				}
			}
		}
		if(childElementList.size()==0)	//if there are no child elements
		{
			final SimpleAttributeSet simpleAttributeSet=new SimpleAttributeSet();	//create a new attribute for this content
			XMLStyleUtilities.setXMLElementName(simpleAttributeSet, XMLConstants.TEXT_NODE_NAME);	//set the name of the content to ensure it will not get its name from its parent element (this would happen if there was no name explicitly set)
			childElementList.add(createElement(branchElement, offset, String.valueOf(CharacterConstants.OBJECT_REPLACEMENT_CHAR), simpleAttributeSet));	//create and add an element for the dummy character
		}
		final Element[] childElements=childElementList.toArray(new Element[childElementList.size()]);	//get the child elements as an array
		branchElement.replace(0, branchElement.getChildCount(), childElements);	//add these children to the branch
		return branchElement;	//return the element we created
	}
*/
		
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
//TODO decide if we want this
	protected Element createElement(final Element parentElement, final int offset, final String text, final AttributeSet attributeSet)
	{
Debug.trace("ready to append text", text, "at offset", offset);
		if(text.length()==0)
		{
			throw new IllegalArgumentException("No text with which to create an element.");
		}
//G***fix		final int begin=stringBuilder.length();	//get the insertion point TODO later add an offset parameter so that we can allow the leaf elements to point to non-zero-based offsets
//G***fix Debug.trace("begin", begin, "old length", text.length());
//G***fix		stringBuilder.append(text);	//append text
final StringBuilder stringBuilder=new StringBuilder(text);	//create a string builder with the text
//G***fix final int newLength=StringBuilderUtilities.collapse(stringBuilder, CharacterConstants.WHITESPACE_CHARS, " ", begin, text.length());	//collapse all whitespace into spaces TODO fix across element boundaries
		final int newLength=StringBuilderUtilities.collapse(stringBuilder, Characters.WHITESPACE_CHARS, " ");	//collapse all whitespace into spaces TODO fix across element boundaries
Debug.trace("new length", newLength);
		final int end=offset+newLength;	//see where the inserted, collapsed text ends
Debug.trace("end", end);

/*G***del if not needed
		try
		{
			content.insertString(offset, stringBuilder.toString());	//insert the text
		}
		catch(final BadLocationException badLocationException)	//we should never have a bad location---we get the location from the content length
		{
			throw new AssertionError(badLocationException);
		}
*/
		return new LeafElement(parentElement, attributeSet, offset, end);	//return a new leaf element for the text
	}

	
	/**Creates and returns a page break element.
	*/
/*TODO decide if we want this
	protected Element createPageBreakElement(final Element parentElement, final int offset)
	{
//G***del Debug.trace("XMLDocument.appendElementSpecListPageBreak()");	//G***del
		final SimpleAttributeSet pageBreakAttributeSet=new SimpleAttributeSet();	//create a page break attribute set G***create this and keep it in the constructor for optimization
//G***del if we can get away with it		XMLStyleConstants.setXMLElementName(pageBreakAttributeSet, XMLCSSStyleConstants.AnonymousAttributeValue); //show by its name that this is an anonymous box G***maybe change this to setAnonymous
		XMLStyleUtilities.setPageBreakView(pageBreakAttributeSet, true);	//show that this element should be a page break view
		final XMLCSSStyleDeclaration cssStyle=new XMLCSSStyleDeclaration(); //create a new style declaration
		cssStyle.setDisplay(XMLCSSConstants.CSS_DISPLAY_BLOCK);	//show that the page break element should be a block element, so no anonymous blocks will be created around it
		XMLCSSStyleUtilities.setXMLCSSStyle(pageBreakAttributeSet, cssStyle);	//store the constructed CSS style in the attribute set
		final BranchElement pageBreakElement=new BranchElement(parentElement, pageBreakAttributeSet);	//create a branch Swing element to represent the page break
		final Element childElement=createElement(pageBreakElement, offset, String.valueOf(CharacterConstants.OBJECT_REPLACEMENT_CHAR), null);
		pageBreakElement.replace(0, pageBreakElement.getChildCount(), new Element[]{childElement});	//add these dummy child element to the page break element
		return pageBreakElement;	//return the page break element
	}
*/
		
	protected void insertBlockElementEnds(final Element element)	//G***testing
	{
		Element previousChildElement=null;	//keep track of the last child element
		AttributeSet previousChildAttributeSet=null;	//keep track of the last child element's attributes
		boolean isPreviousChildElementInline=false;	//keep track of whether the last child element was inline
		final int childElementCount=element.getElementCount(); //find out how many child elements there are
		for(int childElementIndex=0; childElementIndex<childElementCount; ++childElementIndex) //look at each child element
		{
			final Element childElement=element.getElement(childElementIndex);  //get this child element
			final AttributeSet childAttributeSet=childElement.getAttributes();	//get the attributes of the child
			final CSSStyleDeclaration childCSSStyle=XMLCSSStyleUtilities.getXMLCSSStyle(childElement.getAttributes()); //get the CSS style of the element (this method makes sure the attributes are present)
			//see if this element is inline (text is always inline, regardless of what the display property says) G***probably make some convenience method for this, and update XMLViewFactory
			final boolean isInline=XMLCSSUtilities.isDisplayInline(childCSSStyle) || AbstractDocument.ContentElementName.equals(childElement.getName());
			if(!isInline)	//if this element is not inline, add an element end character
			{
				try
				{
//G***del					insertString(childElement.getEndOffset(), XMLEditorKit.ELEMENT_END_STRING, childAttributeSet);	//G***testing
					insertString(childElement.getEndOffset(), ELEMENT_END_STRING, null);	//G***testing
						//if an inline child came before a block child, it will make an anonymous view so add an end to it as well
					if(previousChildElement!=null && isPreviousChildElementInline)
					{
//G***del						insertString(previousChildElement.getEndOffset(), XMLEditorKit.ELEMENT_END_STRING, previousChildAttributeSet);	//G***testing
						insertString(previousChildElement.getEndOffset(), ELEMENT_END_STRING, null);	//G***testing
					}
				}
				catch (BadLocationException e)
				{
					throw new AssertionError(e);	//G***fix
				}
			}
			insertBlockElementEnds(childElement);	//insert block ends for this child element's children
			previousChildElement=childElement;						//the current child element now becomes the previous child element
			previousChildAttributeSet=childAttributeSet;	//
			isPreviousChildElementInline=isInline;				//
		}
	}

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     *
     * @param e the event
     * @see EventListenerList
     */
    protected void fireInsertUpdate(DocumentEvent e) {
//G***fix; right now this is only done when the text is first placed in the document		applyStyles(); //G***testing; put in the correct place, and make sure this gets called when repaginating, if we need to
			super.fireInsertUpdate(e);
    }


    /**
     * Calculate the levels array for a range of paragraphs.
     */
/*G***del; testing bidiarray
    private byte[] calculateBidiLevels( int firstPStart, int lastPEnd ) {

        byte levels[] = new byte[ lastPEnd - firstPStart ];
        int  levelsEnd = 0;
	Boolean defaultDirection = null;
	Object d = getProperty(TextAttribute.RUN_DIRECTION);
	if (d instanceof Boolean) {
	    defaultDirection = (Boolean) d;
	}

        // For each paragraph in the given range of paragraphs, get its
        // levels array and add it to the levels array for the entire span.
        for(int o=firstPStart; o<lastPEnd; ) {
            Element p = getParagraphElement( o );
            int pStart = p.getStartOffset();
            int pEnd = p.getEndOffset();

	    // default run direction for the paragraph.  This will be
	    // null if there is no direction override specified (i.e.
	    // the direction will be determined from the content).
            Boolean direction = defaultDirection;
	    d = p.getAttributes().getAttribute(TextAttribute.RUN_DIRECTION);
	    if (d instanceof Boolean) {
		direction = (Boolean) d;
	    }

Debug.trace("updateBidi: paragraph start = " + pStart + " paragraph end = " + pEnd);

            // Create a Bidi over this paragraph then get the level
            // array.
            String pText;
            try {
                pText = getText(pStart, pEnd-pStart);
            } catch (BadLocationException e ) {
                throw new Error("Internal error: " + e.toString());
            }
            // REMIND(bcb) we should really be using a Segment here.
            Bidi bidiAnalyzer;
	    if (direction != null) {
		boolean ltr = direction.equals(TextAttribute.RUN_DIRECTION_LTR);
		bidiAnalyzer = new Bidi(pText.toCharArray(), ltr);
	    } else {
		bidiAnalyzer = new Bidi( pText.toCharArray() );
	    }
            byte[] pLevels = bidiAnalyzer.getLevels();

Debug.trace("Ready to do Bidi arraycopy with pLevels of length: "+pLevels.length+" levels of length: "+levels.length+" levelsEnd: "+levelsEnd);


            System.arraycopy( pLevels, 0, levels, levelsEnd, pLevels.length );
            levelsEnd += pLevels.length;

            o =  p.getEndOffset();
        }

        // REMIND(bcb) remove this code when debugging is done.
        if( levelsEnd != levels.length )
            throw new Error("levelsEnd assertion failed.");

        return levels;
    }
*/

    /**
     * Initialize the document to reflect the given element
     * structure (i.e. the structure reported by the
     * <code>getDefaultRootElement</code> method.  If the
     * document contained any data it will first be removed.
     */
/*G***fix
    protected void create(ElementSpec[] data) {
	try {
	    if (getLength() != 0) {
		remove(0, getLength());
	    }
	    writeLock();

	    // install the content
	    Content c = getContent();
	    int n = data.length;
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < n; i++) {
		ElementSpec es = data[i];
		if (es.getLength() > 0) {
		    sb.append(es.getArray(), es.getOffset(),  es.getLength());
		}
	    }
	    UndoableEdit cEdit = c.insertString(0, sb.toString());

	    // build the event and element structure
	    int length = sb.length();
	    DefaultDocumentEvent evnt =
		new DefaultDocumentEvent(0, length, DocumentEvent.EventType.INSERT);
	    evnt.addEdit(cEdit);
	    buffer.create(length, data, evnt);

	    // update bidi (possibly)
	    super.insertUpdate(evnt, null);

	    // notify the listeners
	    evnt.end();
	    fireInsertUpdate(evnt);
	    fireUndoableEditUpdate(new UndoableEditEvent(this, evnt));
	} catch (BadLocationException ble) {
	    throw new StateInvariantError("problem initializing");
	} finally {
	    writeUnlock();
	}

    }
*/




	/**Finds the element with the matching attribute.
	@param attribute The attribute to compare.
	@param value The value to match.
	@return The element with the matching attribute, or <code>null</code> if none
		could be found.
	*/
//G***maybe make this protected and add a function that only looks for the target ID
	public Element getElement(Object attribute, Object value)
	{
		return getElement(getDefaultRootElement(), attribute, value);	//start searching from the root element
	}

	/**Returns the child element of the specified element that contains the
		desired attribute with the given value, or <code>null</code> if no element
		has an attribute with the desired value. This function is not thread-safe.
//G***del if not needed		If <code>searchLeafAttributes</code> is true, and the element is a leaf,
//G***del if not needed     * a leaf, any attributes that are instances of HTML.Tag with a
//G***del if not needed     * value that is an AttributeSet will also be checked.
	@param element The element on which to start the search
	@param attribute The attribute to compare.
	@param value The value to match.
	@return The element with the matching attribute, or <code>null</code> if none
		could be found.
	*/
	protected Element getElement(Element element, Object attribute, Object value/*G***del if not needed, boolean searchLeafAttributes*/)
	{
Debug.trace("XMLDocument.getElement() comparing value: ", value);
		final AttributeSet attributeSet=element.getAttributes();	//get the attributes of this element
		if(attributeSet!=null && attributeSet.isDefined(attribute))	//if there are attributes and this attribute is defined
		{
Debug.trace("comparing to: ", attributeSet.getAttribute(attribute));	//G***del
	    if(value.equals(attributeSet.getAttribute(attribute)))	//if the value matches
				return element;	//return this element
/*G***del when works; recheck exactly what this kludge was doing
			else	//if the value doesn't match, we'll see if they are trying to match the target ID G***this is a big kludge to get linking to work with OEB in the short term
				//G***this kludge checks to see if we're looking for a target ID; if so,
				//	and we're looking for a file (not a fragment), see if the part before
				//	the '#' matches (the first element, for now, should have at least the
				//	full path for the target ID
			{
Debug.trace("element doesn't match: ", attributeSet.getAttribute(attribute));
				if(attribute.equals(XMLStyleConstants.TARGET_ID_PATH_ATTRIBUTE_NAME))	//if they are looking for the target ID
				{
					final String compareValue=(String)value;	//cast to a string the attribute that we're comparing
Debug.trace("comparing with: ", compareValue);
					if(compareValue.indexOf('#')==-1)	//if we're looking for an absolute target ID (not a fragment)
					{
						String thisValue=(String)attributeSet.getAttribute(attribute);	//get the attribute we're comparing with
						final int poundIndex=thisValue.indexOf('#');	//get the index of any pound symbol in this attribute
						if(poundIndex!=-1)	//if this attribute has a '#'
							thisValue=thisValue.substring(0, poundIndex);	//remove the pound sign and everything after it
				    if(compareValue.equals(thisValue))	//if the value matches
							return element;	//return this element
					}
				}
			}
*/
		}
//G***del if not needed		if(!element.isLeaf())	//if the
//G***del if not needed		{
		for(int elementIndex=0, maxElementIndex=element.getElementCount(); elementIndex<maxElementIndex; ++elementIndex)	//look at each child element
		{
				//see if the child element can find the attribute
			final Element childReturnValue=getElement(element.getElement(elementIndex), attribute, value/*G***del, searchLeafAttributes*/);
			if(childReturnValue!=null)	//if the child find a matching attribute
				return childReturnValue;	//return what the child's found
    }
		return null;	//if we couldn't find matches, return null
	}
/*G***del if not needed
	else if (searchLeafAttributes && attr != null) {
	    // For some leaf elements we store the actual attributes inside
	    // the AttributeSet of the Element (such as anchors).
	    Enumeration names = attr.getAttributeNames();
	    if (names != null) {
		while (names.hasMoreElements()) {
		    Object name = names.nextElement();
		    if ((name instanceof HTML.Tag) &&
			(attr.getAttribute(name) instanceof AttributeSet)) {

			AttributeSet check = (AttributeSet)attr.
			                     getAttribute(name);
			if (check.isDefined(attribute) &&
			    value.equals(check.getAttribute(attribute))) {
			    return e;
			}
		    }
		}
	    }
	}
	return null;
    }
*/

	/**Gets the paragraph element at the offset <code>pos</code>.
		<p>The paragraph elements of <code>XMLDocument</code> can have multiple
		sub-layers of elements, representing nested XML elements such as
		<code>&lt;strong&gt;</code>; these will be translated into a single layer
		of views for each string of content.</p>
		<p>The <code>DefaultStyledDocument</code> version of this element, on the
		other hand, assumes that each paragraph will only have one single layer of
		content elements, so it simply finds the correct content element and returns
		its parent.</p>
		<p>This version finds the first element up the chain that is not an inline
		element. If all elements up the chain are inline, this method
		functions identical to that of <code>DefaultStyledDocument</code>.</p>
		<p>This version of the method is crucial; without it,
		<code>AbstractDocument.calculateBidiLevels()</code> can receive incorrect
		paragraph beginning and ending information and throw an
		<code>ArrayIndexOutOfBoundsException</code>. Editing also requires the
		functionality in this method.</p>
	@param pos The starting offset (>=0);
	@return The element with the paragraph view attribute set, or if none is set,
		the parent element of the leaf element at the given position.
	*/
	public Element getParagraphElement(int pos)
	{
Debug.trace("pos: ", pos);  //G***del
		final Element defaultParagraphElement=super.getParagraphElement(pos); //get the default paragraph element
		final Element rootElement=getDefaultRootElement();  //get the default root element so we'll know when to stop looking up the chain
		Element paragraphElement=defaultParagraphElement; //we'll check the default paragraph element -- perhaps it really is a paragraph element
		while(paragraphElement!=null && paragraphElement!=rootElement)  //stop looking when we've reached the root element or run out of elements
		{
			final AttributeSet paragraphAttributeSet=paragraphElement.getAttributes();  //get the paragraph's attributes
			assert paragraphAttributeSet!=null : "Paragraph has no attributes.";
Debug.trace("this paragraph attribute set: ", com.garretwilson.swing.text.AttributeSetUtilities.getAttributeSetString(paragraphAttributeSet));  //G***del; use relative class name
		  final CSSStyleDeclaration paragraphCSSStyle=XMLCSSStyleUtilities.getXMLCSSStyle(paragraphAttributeSet); //get the CSS style of the element (this method makes sure the attributes are present)
		  if(!XMLCSSUtilities.isDisplayInline(paragraphCSSStyle))  //if this element is marked as a paragraph
//G***del whenw orks			if(XMLStyleConstants.isParagraphView(paragraphAttributeSet))  //if this element is not marked as a paragraph
			{
				Debug.trace("paragraph is paragraph");  //G***del
				return paragraphElement;  //return the paragraph element
			}
			paragraphElement=paragraphElement.getParentElement(); //since this element wasn't a paragraph element, try the one above it
		}
		return defaultParagraphElement; //we couldn't find anything marked as a paragraph, so return the default
	}

	/**Returns true if the text in the range <code>p0</code> to <code>p1</code>
		is left to right.
		Imported from javax.swing.AbstractDocument.text version 1.112 02/02/00 by
		Timothy Prinzing because that version has class access and cannot be called
		from the revised com.garretwilson.swing.text.GlyphPainter, which in turn
		has been taken out of its package so that it can be created by
		com.garretwilson.swing.text.TextLayoutStrategy, which had to be rewritten
		to allow antialised text because of a JDK 1.3.x bug that caused a
		<code>Graphic/code> object not to correctly create a
		<code>FontRenderContext</code> that recognized the antialised font property.
	*/
	public boolean isLeftToRight(int p0, int p1)
	{
		if(!getProperty(JavaConstants.I18N_PROPERTY_NAME).equals(Boolean.TRUE))
		{
	    return true;
		}
		Element bidiRoot = getBidiRootElement();
		int index = bidiRoot.getElementIndex(p0);
		Element bidiElem = bidiRoot.getElement(index);  //G***is this causing problems with our innovations for inline elements?
		if(bidiElem.getEndOffset() >= p1)
		{
			AttributeSet bidiAttrs = bidiElem.getAttributes();
			return ((StyleConstants.getBidiLevel(bidiAttrs) % 2) == 0);
		}
		return true;
	}

	/**Discovers any referenced styles to this document, loads the stylesheets,
		and applies the styles to the Swing element attributes.
	@param swingDocument The Swing document containing the data.
	*/
	public void applyStyles()
	{
Debug.trace("Ready to applystyles");  //G***fix
		writeLock();  //get a lock on the document
		try
		{
Debug.trace("looking at first root element");  //G***fix
			final Element rootSwingElement=getRootElements()[0]; //get the first root element of the document -- this contains an element tree for each document loaded
		  final int swingDocumentElementCount=rootSwingElement.getElementCount(); //find out how many root elements there are
		  for(int swingDocumentElementIndex=0; swingDocumentElementIndex<swingDocumentElementCount; ++swingDocumentElementIndex) //look at each root element, each of which represents an XML document
			{
				final Element swingDocumentElement=rootSwingElement.getElement(swingDocumentElementIndex);  //get the child element, which is the root of the document tree
				final AttributeSet documentAttributeSet=swingDocumentElement.getAttributes();	//get the attribute set of the document element
				final URI documentBaseURI=XMLStyleUtilities.getBaseURI(documentAttributeSet);  //get the URI of this document
				final ContentType documentMediaType=XMLStyleUtilities.getMediaType(documentAttributeSet); //see what media type this document is
				final RDFResource description=XMLStyleUtilities.getDocumentDescription(documentAttributeSet);	//see if there is an RDF resource describing this document
				
				final SwingXMLCSSStylesheetApplier stylesheetApplier=getSwingStylesheetApplier();	//get the stylesheet applier
				//TODO make sure the stylesheet applier correctly distinguishes between document base URI for internal stylesheets and publication base URI for package-level base URIs
					//get all stylesheets for this document
				final CSSStyleSheet[] styleSheets=stylesheetApplier.getStylesheets(swingDocumentElement, documentBaseURI, documentMediaType, description);
				//apply the stylesheets
				for(int i=0; i<styleSheets.length; ++i) //look at each stylesheet
				{
				  	//prepare a progress message: "Applying stylesheet X to XXXXX.html"
					final String progressMessage=format("Applying stylesheet {0} to {1}", Integer.valueOf(i+1), documentBaseURI!=null ? documentBaseURI.toString() : "unknown"); //G***i18n; fix documentURI if null
Debug.trace(progressMessage); //G***del
					fireMadeProgress(new ProgressEvent(this, APPLY_STYLESHEET_TASK, progressMessage, swingDocumentElementIndex, swingDocumentElementCount));	//fire a progress message saying that we're applying a stylesheet
//G***del System.out.println("applying stylesheet: "+i+" of "+styleSheetList.getLength());  //G***del
					final CSSStyleSheet cssStyleSheet=styleSheets[i];  //get a reference to this stylesheet, assuming that it's a CSS stylesheet (that's all that's currently supported)
					stylesheetApplier.applyStyleSheet(cssStyleSheet, swingDocumentElement);  //apply the stylesheet to the document
				}
Debug.trace("applying local styles"); //G***del
				fireMadeProgress(new ProgressEvent(this, APPLY_STYLESHEET_TASK, "Applying local styles", swingDocumentElementIndex, swingDocumentElementCount));	//fire a progress message saying that we're applying local styles G***i18n
				stylesheetApplier.applyLocalStyles(swingDocumentElement);	//apply local styles to the document TODO why don't we create one routine to do all of this?
			}
		}
		finally
		{
			writeUnlock();  //always release the lock on the document
		}
	}

/*G***fix
	public void emphasis()	//G***testing
	{
		writeLock();  //G***testing

		


//G***fix		final Element[] buff=new Element[1];  //create an element array for insertion of elements
		final Element characterElement=getCharacterElement(60);
//G***fix		final AttributeSet emAttributeSet=createAttributeSet("em", XHTMLConstants.XHTML_NAMESPACE_URI.toString());	//G***testirng
		final AttributeSet emAttributeSet=createAttributeSet(XHTMLConstants.XHTML_NAMESPACE_URI, "em");	//G***testirng
//G***fix		final Element branchElement=createBranchElement(characterElement.getParentElement(), emAttributeSet);
//G***fix		buff[0]=branchElement;

	final List elementSpecList=new ArrayList();	//create an array to hold our element specs
	elementSpecList.add(new DefaultStyledDocument.ElementSpec(emAttributeSet, DefaultStyledDocument.ElementSpec.StartTagType));
appendElementSpecListContent(elementSpecList, "test", null, null);	//G***fix
	elementSpecList.add(new DefaultStyledDocument.ElementSpec(emAttributeSet, DefaultStyledDocument.ElementSpec.EndTagType));

	final DefaultStyledDocument.ElementSpec[] elementSpecs=(DefaultStyledDocument.ElementSpec[])elementSpecList.toArray(new DefaultStyledDocument.ElementSpec[elementSpecList.size()]);


DefaultDocumentEvent evnt =	new DefaultDocumentEvent(60, 4, DocumentEvent.EventType.INSERT);
//G***fix		evnt.addEdit(cEdit);
//G***fix		buffer.create(1, buff, evnt);
*/
/*G***fix

	try
	{
		insert(60, elementSpecs);
	}
	catch (BadLocationException e)
	{
		Debug.error(e);
	}
*/
/*G***fix
buffer.insert(60, 4, elementSpecs, evnt);

// update bidi (possibly)
insertUpdate(evnt, null);

// notify the listeners
evnt.end();
fireInsertUpdate(evnt);
fireUndoableEditUpdate(new UndoableEditEvent(this, evnt));

*/

/*G***fix
		// update bidi (possibly)
		super.insertUpdate(evnt, null);

		// notify the listeners
		evnt.end();
		fireInsertUpdate(evnt);
		fireUndoableEditUpdate(new UndoableEditEvent(this, evnt));
*/

/*G***del
		final Element[] buff=new Element[1];  //create an element array for insertion of elements

		createBranchElement()

		final BranchElement section=new SectionElement(); //create a new section
		final BranchElement html=new BranchElement(section, htmlAttributeSet); //create a new paragraph to represent the document
		final BranchElement body=new BranchElement(html, bodyAttributeSet); //create a new paragraph to represent the HTML body
		final BranchElement p=new BranchElement(body, pAttributeSet); //create a new paragraph to represent the paragraph
		final LeafElement leaf=new LeafElement(p, null, 0, 1);  //create the leaf element
		buff[0]=leaf; //insert the leaf
		p.replace(0, 0, buff);
		buff[0]=p;  //insert the p
		body.replace(0, 0, buff);
		buff[0]=body;  //insert the body
		html.replace(0, 0, buff);

			BranchElement paragraph = new BranchElement(section, null);

			LeafElement brk = new LeafElement(paragraph, null, 0, 1);
			buff[0] = brk;
			paragraph.replace(0, 0, buff);

			final Element[] sectionBuffer=new Element[2];  //G***testing
			sectionBuffer[0] = html;
			sectionBuffer[1] = paragraph;
			section.replace(0, 0, sectionBuffer);
*/
/*G***fix
		buff[0]=html;  //insert the html
		section.replace(0, 0, buff);
		writeUnlock();
		return section;
*/

/*G***fix
		writeUnlock();
		
	}
*/

	/**Inserts an XML element into the document around the indicated selection.
	@param offset The offset in the document (>=0).
	@param length The length (>=0).
	@param elementNamespaceURI The namespace of the XML element.
	@param elementQName The qualified name of the XML element.
	*/
/*G***fix
	public void insertXMLElement(final int offset, final int length, final URI elementNamespaceURI, final String elementQName)
	{
		writeLock();  //lock the document for writing
		final Element characterElement=getCharacterElement(offset);	//get the element at the offset
		final AttributeSet elementAttributeSet=createAttributeSet(elementNamespaceURI, elementQName);	//create an attribute set for the element
		final List elementSpecList=new ArrayList();	//create an array to hold our element specs
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(elementAttributeSet, DefaultStyledDocument.ElementSpec.StartTagType));
			//TODO use another Unicode character that has replacement semantics, just to make this neater and more readable
		appendElementSpecListContent(elementSpecList, StringUtilities.makeString('*', length), null, null);	//G***fix; comment
		elementSpecList.add(new DefaultStyledDocument.ElementSpec(elementAttributeSet, DefaultStyledDocument.ElementSpec.EndTagType));
		final DefaultStyledDocument.ElementSpec[] elementSpecs=(DefaultStyledDocument.ElementSpec[])elementSpecList.toArray(new DefaultStyledDocument.ElementSpec[elementSpecList.size()]);

		DefaultDocumentEvent evnt=new DefaultDocumentEvent(offset, length, DocumentEvent.EventType.INSERT);
		buffer.insert(offset, length, elementSpecs, evnt);	//insert the element's specifications
	//G***fix	insertUpdate(evnt, null);	//update after the insert
		evnt.end();	//end the editing
		fireInsertUpdate(evnt);	//notify listeners of the insert
		applyStyles();	//G***testing
		fireUndoableEditUpdate(new UndoableEditEvent(this, evnt));	//notify listeners of the undoable edit
		writeUnlock();	//unlock the document
	}
*/


	/**Class to apply styles to Swing elements.
	@author Garret Wilson
	*/
	protected class SwingXMLCSSStylesheetApplier extends AbstractXMLCSSStylesheetApplier<Element, Element>
	{

		/**Returns an input stream for the given URI.
		<p>The calling class has the responsibility for closing the input stream.</p>
		@param uri A URI to a resource.
		@return An input stream to the contents of the resource represented by the given URI.
		@exception IOException Thrown if an I/O error occurred.
		*/
		public InputStream getInputStream(final URI uri) throws IOException
		{
			return getResourceAsInputStream(uri);	//ask the Swing document for the URI TODO maybe later change getResourceAsInputStream() to getInputStream() so that the XMLDocument is URIInputStreamable, if it isn't already
		}

		/**Returns the object that represents the root element of the given document.
		@param The object representing the XML document.
		@return The object representing the root element of the XML document.
		*/
		protected Element getDocumentElement(final Element document)
		{
			return document;	//in Swing the XML document is represented by the root element in the document hierarchy---in this implementation, the document element hierarchy is a direct descendant of the section element
		}

		/**Retrieves processing instructions from the given document.
		@param document The document that might contain XML processing instructions.
		@return A non-<code>null</code> array of name-value pairs representing
			processing instructions.
		*/
		protected NameValuePair[] getDocumentProcessingInstructions(final Element document)
		{
			return XMLStyleUtilities.getXMLProcessingInstructions(document.getAttributes());  //get the processing instructions from the attributes of the document, which is really a Swing element			
		}

		/**Retrieves the namespace URI of the given element.
		@param element The element for which the namespace URI should be returned.
		@return The namespace URI of the given element.
		*/
		protected String getElementNamespaceURI(final Element element)
		{
			return XMLStyleUtilities.getXMLElementNamespaceURI(element.getAttributes());	//return the element's namespace URI from the Swing element's attributes
		}

		/**Retrieves the local name of the given element.
		@param element The element for which the local name should be returned.
		@return The local name of the given element.
		*/
		protected String getElementLocalName(final Element element)
		{
			return XMLStyleUtilities.getXMLElementLocalName(element.getAttributes());	//return the element's local name from the Swing element's attributes
		}

		/**Retrieves the value of one of the element's attributes.
		@param element The element owner of the attributes.
		@param attributeNamespaceURI The namespace of the attribute to find.
		@param attributeLocalName The local name of the attribute to find.
		@return The value of the specified attribute, or <code>null</code> if there
			is no such attribute.
		*/
		protected String getElementAttributeValue(final Element element, final String attributeNamespaceURI, final String attributeLocalName)
		{
			return XMLStyleUtilities.getXMLAttributeValue(element.getAttributes(), attributeNamespaceURI, attributeLocalName);	//return the XML attribute value from the element's attributes
		}

		/**Retrieves the parent element for the given element.
		@param element The element for which a parent should be found.
		@return The element's parent, or <code>null</code> if no parent could be found.
		 */
		protected Element getParentElement(final Element element)
		{
			final Element parentElement=element.getParentElement(); //get this element's parent
			return parentElement instanceof SectionElement ? null : parentElement;	//return the parent element, unless we've reached the parent section element 
		}
	
		/**Determines the number of child elements the given element has.
		@param element The parent element.
		@return The number of child elements this element has.
		*/
		protected int getChildCount(final Element element)
		{
			return element.getElementCount();	//return the number of child elements
		}

		/**Determines if the given indexed child of an element is an element.
		This version always returns <code>true</code>, as all Swing element children are also elements.
		@param element The parent element.
		@param index The zero-based index of the child.
		@return <code>true</code> if the the child of the element at the given index is an element.
		*/
		protected boolean isChildElement(final Element element, final int index)
		{
			return true;	//there are no non-element children of elements
		}

		/**Retrieves the given indexed child of an element.
		@param element The parent element.
		@param index The zero-based index of the child.
		@return The child of the element at the given index.
		*/
		protected Element getChildElement(final Element element, final int index)
		{
			return element.getElement(index);	//return the child element at the given index
		}

		/**Retrieves all child text of the given element.
		@param element The element for which text should be returned.
		@return The text content of the element.
		*/
		protected String getElementText(final Element element)
		{
			try
			{
				return SwingTextUtilities.getText(element);  //return the text of the element
			}
			catch(BadLocationException badLocationException)	//we should never get a bad location exception
			{
				throw (AssertionError)new AssertionError(badLocationException.getMessage()).initCause(badLocationException);
			}
		}
	
		/**Imports style information into that already gathered for the given element.
		@param element The element for which style information should be imported
		@param cssStyle The style information to import.	
		*/
		protected void importCSSStyle(final Element element, final CSSStyleDeclaration cssStyle)
		{
			final AttributeSet attributeSet=element.getAttributes();	//get the element's attributes
			CSSStyleDeclaration elementStyle=(XMLCSSStyleDeclaration)XMLCSSStyleUtilities.getXMLCSSStyle(attributeSet);  //get this element's style
			if(elementStyle==null) //if there is no existing style (usually the editor kit will have supplied one already to reduce the performance hit here)
			{
				elementStyle=new XMLCSSStyleDeclaration();  //create an empty default style TODO use standard DOM classes if we can
				assert attributeSet instanceof MutableAttributeSet : "Attribute set not mutable";
				XMLCSSStyleUtilities.setXMLCSSStyle((MutableAttributeSet)attributeSet, elementStyle);	//put the style in the attributes
			}
//G***del					Debug.trace("style rule is of type: ", cssStyleRule.getClass().getName());  //G***del
			importStyle(elementStyle, cssStyle);	//import the style
		}
	}


}
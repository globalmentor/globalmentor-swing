package com.garretwilson.swing.text.xml;

import java.awt.Color;
//G***del import java.awt.Component;  //G***del when loading routines are placed elsewhere
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.awt.GraphicsEnvironment;
import java.awt.Image;  //G***del when loading routines are placed elsewhere
//G***del import java.awt.MediaTracker;  //G***del when loading routines are placed elsewhere
import java.awt.Toolkit;  //G***del when loading routines are placed elsewhere
import java.lang.ref.*;
import java.util.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
/*G***bring back as needed
import java.net.URL;
import java.net.URLEncoder;
*/
import java.net.MalformedURLException;
import java.io.*;
import java.text.MessageFormat;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.undo.UndoableEdit;

/*G***bring back as needed
import javax.swing.Icon;
import javax.swing.ImageIcon;
*/
//G***fix maybe import org.w3c.dom.*;
import org.w3c.dom.Attr;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.stylesheets.StyleSheet;
//G***del when works import com.garretwilson.awt.ImageUtilities;
import com.garretwilson.io.*;
import com.garretwilson.lang.JavaConstants;
import com.garretwilson.net.URIUtilities;
import com.garretwilson.rdf.RDF;  //G***move
import com.garretwilson.swing.event.ProgressEvent;
import com.garretwilson.swing.event.ProgressListener;
import com.garretwilson.swing.text.DocumentConstants;
import com.garretwilson.swing.text.DocumentUtilities;
import com.garretwilson.swing.text.SwingTextUtilities;
import com.garretwilson.text.CharacterConstants;
import com.garretwilson.text.xml.XMLConstants;
import com.garretwilson.text.xml.XMLDOMImplementation;
import com.garretwilson.text.xml.XMLText; //G***remove these in favor of W3C DOM
import com.garretwilson.text.xml.XMLElement;
import com.garretwilson.text.xml.XMLNode;
import com.garretwilson.text.xml.XMLUtilities;
import com.garretwilson.text.xml.oeb.OEBConstants;  //G***move
import com.garretwilson.text.xml.stylesheets.XMLStyleSheetConstants;
import com.garretwilson.text.xml.stylesheets.XMLStyleSheetDescriptor;
import com.garretwilson.text.xml.stylesheets.XMLStyleSheetList;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSConstants;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSValue;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSPrimitiveValue;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSProcessor;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSStyleDeclaration;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSSelector; //G***del when fully switched to DOM
import com.garretwilson.text.xml.stylesheets.css.XMLCSSStyleRule; //G***del when fully switched to DOM
import com.garretwilson.text.xml.stylesheets.css.XMLCSSUtilities; //G***maybe move
import com.garretwilson.text.xml.xhtml.XHTMLConstants;  //G***move
import com.garretwilson.text.xml.xhtml.XHTMLUtilities;
import com.garretwilson.sound.sampled.SampledSoundUtilities;
import com.garretwilson.swing.text.Bidi;
import com.garretwilson.swing.text.xml.css.XMLCSSStyleUtilities;
import com.garretwilson.swing.text.xml.css.XMLCSSStyleContext;
import com.garretwilson.swing.text.xml.xhtml.XHTMLSwingTextUtilities;
import com.garretwilson.util.Debug;
import com.garretwilson.util.NameValuePair;
//G***del when works import com.garretwilson.swing.text.xml.css.XMLCSSSimpleAttributeSet;

import com.garretwilson.lang.StringUtilities;	//G***del when we can
import com.garretwilson.lang.StringBufferUtilities;	//G***del if we don't need

import org.w3c.dom.*;
import org.w3c.dom.css.*; //G***maybe move elsewhere
import org.w3c.dom.stylesheets.StyleSheetList;

/**A document that models XML.
	Implements <code>URIInputStreamable</code>, as this class knows how to
	retrieve streams to URIs.
@see com.garretwilson.text.xml.XMLProcessor
@see com.garretwilson.text.xml.XMLDocument
@author Garret Wilson
*/
public class XMLDocument extends DefaultStyledDocument implements URIInputStreamable
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

final static char ELEMENT_END_CHAR=CharacterConstants.ZERO_WIDTH_SPACE_CHAR;	
final static String ELEMENT_END_STRING=String.valueOf(ELEMENT_END_CHAR);	
//G***fix final static char ELEMENT_END_CHAR=CharacterConstants.ZERO_WIDTH_NO_BREAK_SPACE_CHAR;	
//G***fix	final static char ELEMENT_END_CHAR=CharacterConstants.PARAGRAPH_SIGN_CHAR;	

	/**The list of progress event listeners.*/
	private EventListenerList progressListenerList=new EventListenerList();

	/**A map of all full element target IDs (URI+#+id).*/
	private Map linkTargetMap=new HashMap();

		/**Gets the element associated with the target ID.
		@param targetID The full target ID (URI+#+element ID) of the element.
		@return The element to which the target ID refers, or <code>null</code> if
			there is no element associated with the target ID.
		*/
		public Element getLinkTarget(final String targetID)
		{
			return (Element)linkTargetMap.get(targetID);	//return whatever element is associated with the target ID
		}

		/**Associates a target ID with an element.
		@param targetID The full target ID (URI+#+element ID) of the element.
		@param element The element to which the target ID refers.
		*/
		public void setLinkTarget(final String targetID, final Element element)
		{
			linkTargetMap.put(targetID, element);	//put the element in the map, keyed to the target ID
		}

	/**The access to input streams via URIs, if one exists.*/
	private URIInputStreamable uriInputStreamable=null;

		/**@return The access to input streams via URIs, or <code>null</code> if
		  none exists.
		*/
		public URIInputStreamable getURIInputStreamable() {return uriInputStreamable;}

		/**Sets the object for accessing input streams.
		@param newURIInputStreamable The object that allows acces to input streams
			via URIs.
		*/
		public void setURIInputStreamable(final URIInputStreamable newURIInputStreamable) {uriInputStreamable=newURIInputStreamable;}

	/**A map of references to resources that have been loaded.*/
	private final Map resourceReferenceMap=new HashMap();

	/**Returns a cached resource identified by the URI, if the object's memory
		has not been reclaimed.
	@param resourceURI The URI of the requested resource.
	@return The resource, if it has been cached and is still referenced in the
		JVM, or <code>null</code> if the resource's memory has been reclaimed or the
		object has never been cached.
	*/
	protected Object getCachedResource(final URI resourceURI)
	{
		final Reference resourceReference=(Reference)resourceReferenceMap.get(resourceURI); //return a reference to the cached resource, if available
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
	  resourceReferenceMap.put(resourceURI, new SoftReference(resource));
	}

	/**@return The RDF data model where metadata is stored, or <code>null</code>
		if there is no metadata document.*/
	public RDF getRDF()
	{
		return DocumentUtilities.getRDF(this);  //retrieve the RDF property value
	}

	/**Sets the RDF data model where metadata is stored.
	@param rdf The RDF data model.
	*/
	public void setRDF(final RDF rdf)
	{
		DocumentUtilities.setRDF(this, rdf);  //set the RDF
	}

//G***fix	private Map resourceMap

	private Font CODE2000_FONT=null;  //G***testing; make final

	//G***fix; comment
	protected String[] sortedAvailableFontFamilyNameArray;

	/**Default constructor.*/
	public XMLDocument()
	{
		super(new XMLCSSStyleContext());	//construct the parent class, specifying our own type of style context that knows how to deal with CSS attributes
//G***fix		setProperty(AbstractDocument.I18NProperty);  //G***testing i18n
//G***fix		putProperty("i18n", Boolean.TRUE);  //G***testing i18n
Debug.trace("Document i18n property: ", getProperty("i18n")); //G***testing i18n

		//get the list of available font family names
		sortedAvailableFontFamilyNameArray=GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		Arrays.sort(sortedAvailableFontFamilyNameArray);  //sort the array of font family names


		//G***testing
/*G***fix
Debug.trace("loading code2000");
		try
		{
			final InputStream code2000InputStream=new BufferedInputStream(new FileInputStream("Code2000.ttf"));
			try
			{
				CODE2000_FONT=Font.createFont(Font.TRUETYPE_FONT, code2000InputStream);
			}
			finally
			{
				code2000InputStream.close();
			}
		}
		catch(Exception e)
		{
			Debug.error(e);
//G***			CODE2000_FONT=null;
		}
*/
	}
//G***fix	{
//G***fix		this(new GapContent(BUFFER_SIZE_DEFAULT)/*G***fix, new StyleSheet()*/);
//G***fix	}


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

	/**Creates an attribute set for the described element.
	@param elementNamespaceURI The namespace of the XML element.
	@param elementQName The qualified name of the XML element.
	@return An attribute set reflecting the CSS attributes of the element.
	*/
	public static MutableAttributeSet createAttributeSet(final URI elementNamespaceURI, final String elementQName)
	{
		return createAttributeSet(elementNamespaceURI, elementQName, null);  //create an attribute set with no style
	}

	/**Creates an attribute set for the described element.
	@param elementNamespaceURI The namespace of the XML element.
	@param elementQName The qualified name of the XML element.
	@param style The CSS style to be used for the attribute set.
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

	/**Gets the font from an attribute set using CSS names instead of the default
		Swing names. The actual font is obtained from the document's attribute context,
		similar to <code>DefaultStyledDocument#getFont</code> (which this function
		overrides), except that this function	may decide to change the font size
		before actually creating the font using the attribute context. If the
		attribute set specified several fonts, the first available one is used.
	@param attributeSet The attribute set from which to retrieve values.
	@return The constructed font.
	@see DefaultStyledDocument.getFont
	@see XMLStyleContext#getFont
	*/
	public Font getFont(AttributeSet attributeSet)
	{
//G***del		Debug.trace("Getting font from attributes: ", com.garretwilson.swing.text.AttributeSetUtilities.getAttributeSetString(attributeSet));  //G***del

//G***maybe use		sun.java2d.SunGraphicsEnvironment.getLocalGraphicsEnvironment()

/*G***maybe use
		final Graphics2D graphics2D=(Graphics2D)g;  //cast to the 2D version of graphics
		final FontRenderContext fontRenderContext=graphics2D.getFontRenderContext();  //get the font rednering context
*/

		int style=Font.PLAIN;	//start out assuming we'll have a plain font
		if(XMLCSSStyleUtilities.isBold(attributeSet))	//if the attributes specify bold (use XMLCSSStyleConstants so we'll recognize CSS values in the attribute set)
			style|=Font.BOLD;	//add bold to our font style
//G***del Debug.trace("is bold: ", new Boolean(XMLCSSStyleConstants.isBold(attributeSet)));  //G***del
		if(XMLCSSStyleUtilities.isItalic(attributeSet))	//if the font attributes specify italics (use XMLCSSStyleConstants so we'll recognize CSS values in the attribute set)
			style|=Font.ITALIC;	//add italics to our font style
		String family=null; //show that we haven't found a font family
		final String[] fontFamilyNameArray=XMLCSSStyleUtilities.getFontFamilyNames(attributeSet); //get the array of font family names
		for(int i=0; i<fontFamilyNameArray.length; ++i) //look at each of the specified fonts
		{
		  final String fontFamilyName=fontFamilyNameArray[i]; //get this font family name
Debug.trace("Looking for font family name: ", fontFamilyName);
		  if(fontFamilyName.equals("monospace"))  //G***fix all this; tidy; comment
			{
				family="Monospaced";
				break;
			}
		  else if(fontFamilyName.equals("serif"))  //G***fix all this; tidy; comment
			{
				family="Serif";
				break;
			}
		  else if(fontFamilyName.equals("sans-serif"))  //G***fix all this; tidy; comment
			{
//G***fix				family="Lucinda Sans Regular";
				family="SansSerif";
				break;
			}
		  else if(fontFamilyName.equals("symbol"))  //G***fix all this; tidy; comment
			{
				family="Symbol";
				break;
			}
			//G***maybe fix for "Symbol"
				//see if we have the specified font
			final int fontFamilyNameIndex=Arrays.binarySearch(sortedAvailableFontFamilyNameArray, fontFamilyName);
			if(fontFamilyNameIndex>=0)  //if we have the specified font
			{
				family=fontFamilyName;  //show that we found a font family
				break;  //stop searching
			}
		}
		if(family==null)  //if we didn't find a font family
//G***del			family="Code2000";   //G***testing
//G***fix			family="Baraha Devanagari Unicode";   //G***testing
			family="Serif";   //use the default G***use a constant; maybe use a different default
Debug.trace("Decided on font family: ", family); //G***del
//G***del when works		final String family=StyleConstants.getFontFamily(attributeSet);	//get the font family from the attributes (use XMLCSSStyleConstants so we'll recognize CSS values in the attribute set) G***change to use CSS attributes
		float size=XMLCSSStyleUtilities.getFontSize(attributeSet);	//get the font size from the attributes (use XMLCSSStyleConstants so we'll recognize CSS values in the attribute set)

/*G***put this in the style instead
		//if the attributes specify either superscript or subscript (use XMLCSSStyleConstants so we'll recognize CSS values in the attribute set)
		if(StyleConstants.isSuperscript(attributeSet) || StyleConstants.isSubscript(attributeSet))	//G***change to use CSS attributes
			size-=2;	//reduce the font size by two
*/
		final float zoomFactor=DocumentUtilities.getZoomFactor(this, DocumentConstants.DEFAULT_ZOOM_FACTOR);  //get the zoom factor, assuming a default value if no value is specified
/*G***del
		  //we'll try to get the zoom factor from the text pane we're embedded in
		final float zoomFactor=DocumentUtilities.hasZoomFactor(this)
			  ? DocumentUtilities.getZoomFactor(this) //get the zoom factor value
				: 1.20f;   //assume a zoom of 120%
*/
/*G***del
		final Object zoomFactorObject=getProperty(ZOOM_FACTOR_PROPERTY); //get the zoom factor from the document
		if(zoomFactorObject instanceof Float) //if this is a float, as we expect
			zoomFactor=((Float)zoomFactorObject).floatValue();  //get the zoom factor value
		else  //if we don't get a valid zoom factor back
			zoomFactor=1.20f; //assume a zoom of 120%
*/
		size*=zoomFactor;	//increase the font size by the specified amout

		StyleContext styleContext=((StyleContext)getAttributeContext());  //get the style context to be used for retrieving fonts



		Font font=styleContext.getFont(family, style, Math.round(size));	//use the attribute context to get the font based upon the specifications we just got from attribute set
/*G***fix
Debug.trace("after getting font, font is: "+font);
	if(family.equalsIgnoreCase("Code2000") && CODE2000_FONT!=null)  //G***testing
	{
		Debug.trace("Trying to use code2000 font");
		font=CODE2000_FONT.deriveFont(style, size);
	}
*/
/*G***del when works, remove segment parameter
//G***fix Debug.trace("Font has Devanagari ka: "+font.canDisplay((char)0x0915));
//G***put in TextLayout /*G***fix
		//now that we've found a font, make sure the font can show all of the characters in the given segment, if any
		if(segment!=null) //if a segment of text is available
		{
Debug.trace("Looking at text: "+Integer.toHexString(segment.array[segment.getBeginIndex()]));
			  //find the first character this font can't display
			final int undisplayableCharIndex=font.canDisplayUpTo(segment.array, segment.getBeginIndex(), segment.getEndIndex());
		  if(undisplayableCharIndex>=0) //if there's a character this font can't display
			{
				final char undisplayableChar=segment.array[undisplayableCharIndex]; //get the character that can't be displayed
Debug.trace("Found undisplayable character in font: "+Integer.toHexString(undisplayableChar));
				  //G***don't hard-code the ranges in
				if(undisplayableChar>=0x0600 && undisplayableChar<=0x06FF)  //if this is an Arabic character
		  		font=styleContext.getFont("Lucinda Sans Regular", style, Math.round(size));	//use the font which we know has Arabic characters G***use a constant here G***don't round each time; do it only once
			}
		}
*/
Debug.trace("Finally chose font family name: ", font.getFamily()); //G***del
		return font;  //return the font we found
//G***del		return styleContext.getFont("Lucinda Sans Regular", style, Math.round(size));	//use the font which we know has Arabic characters G***use a constant here G***don't round each time; do it only once
//G***del		return styleContext.getFont("Serif", 0, Math.round(size));	//use the font which we know has Arabic characters G***use a constant here G***don't round each time; do it only once

	}

	/**Gets a specific font based upon the font name, style, and size. This
		implementation passes the request on to the associated
		<code>StyleContext</code> which provides a cache of fonts. This method is
		used indirectly through the associated view by
		<code>TextLayoutStrategy</code> to get a font for characters that cannot
		be displayed in the specified font.
	@param family The font family (such as "Monospaced")
	@param style The style of the font (such as <code>Font.PLAIN</code>).
	@param size The point size (>=1)
	@return The new font.
	@see DefaultStyledDocument.getFont
	@see XMLStyleContext#getFont
	*/
	public Font getFont(final String family, final int style, final int size)
	{
		//use the attribute context to get the font based upon the specifications
		return ((StyleContext)getAttributeContext()).getFont(family, style, size);
	}

	/**Gets a new font for the specified character by searching all available fonts.
		Fonts are cached by Unicode block and character, for faster searching in
		future queries.
	@param c The character for which a font should be returned.
	@param style The style of the font (such as <code>Font.PLAIN</cod>).
	@param size The point size (>=1).
	@return The new font, or <code>null</code> if a font could not be found to
		display this character.
	@see XMLCSSStyleContext#getFont
	*/
	public Font getFont(final char c, final int style, final int size)
	{
		return ((XMLCSSStyleContext)getAttributeContext()).getFont(c, style, size);  //return a font that supports the character
	}

	/**Returns a sorted array of names of available fonts. The returned array is
		shared by other objects in the system, and should not be modified.
	@return A sorted array of names of available fonts.
	@see XMLCSSStyleContext#getAvailableFontFamilyNames
	*/
/*G***del when works
	public String[] getAvailableFontFamilyNames()
	{
		return ((XMLCSSStyleContext)getAttributeContext()).getAvailableFontFamilyNames();  //return the array of font family names from the syle context
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
Debug.trace("Inside XMLDocument.getResource() with href: ", href);	//G***del
		final MediaType mediaType=getResourceMediaType(href);	//get the media type of the resource
Debug.trace("Inside XMLDocument.getResource() with media type: ", mediaType);	//G***del
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
	protected Object getResource(final URI uri, final MediaType mediaType) throws IOException
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
		assumes relative locations are relative to the base URI.
	@param href The specified location of the resource.
	@return The URI of the specified resource.
	@exception URISyntaxException Thrown if the a URI could not be created.
	@see #getBaseURI
	*/
	public URI getResourceURI(final String href) throws URISyntaxException
	{
		return URIUtilities.createURI(getBaseURI(), href);	//create and return a URI based upon the base URI and the given file location
	}

	/**Gets the media type of a particular resource.
	@param href The specified location of the resource.
	@return The media type of the specified resource, or <code>null</code> if
		the media type cannot be determined.
	*/
	public MediaType getResourceMediaType(final String href)
	{
//G***del Debug.trace("Getting ready to get media type for: ", href);  //G***del
//G***fix with FileUtilities; fix uppercase/lowercase for file extensions		FileUtilities.getMediaType()
		return FileUtilities.getMediaType(href);  //return the media type from the extension of the href, if any
/*G***del; changed to FileUtilities
		  //G***change all this to use the FileUtilites.getMediaType()
		final int extensionSeparatorIndex=href.lastIndexOf('.');	//find the extension separator character, if there is one
		if(extensionSeparatorIndex!=-1)	//if there is an extension
		{
			final String extension=href.substring(extensionSeparatorIndex+1);	//get the extension
Debug.trace("extension: ", extension);  //G***del
			return MediaType.getMediaType(extension);	//return the media type associated with this extension, if there is one
		}
		return null;	//show that, since this file doesn't have an extension, we don't even want to guess about what media type it represents
*/
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
	@param uri The specified location of the resource.
	@return An open input stream to the resource.
	@exception IOException Thrown if an input stream to the specified resource
		cannot be created.
//G***check about returning null if the resource is not found
	*/
	public InputStream getResourceAsInputStream(final URI uri) throws IOException
	{
		final URIInputStreamable uriInputStreamable=getURIInputStreamable();  //see if we have an input stream locator
		if(uriInputStreamable!=null)  //if we have an input stream locator (if we're reading from a zip file, for instance)
		{
Debug.trace("found input stream locator, getting input stream to URI: ", uri); //G***del
			return uriInputStreamable.getInputStream(uri);  //get an input stream from the URI
		}
		else  //if we don't have an input stream locator
		{
			return uri.toURL().openConnection().getInputStream();	//open a connection to the URI (converted to a URL) and return an input stream to that connection
		}
	}

	/**Returns an input stream from given URI.
		This method is supplied to fulfill the requirements of
		<code>URIInputStreamLocator</code>; this method simply delegates to
		<code>getResourceAsInputStream()</code>, which may be overridden in child
		classes to retrieve input streams differently.
	@param uri A complete URI to a file.
	@return An input stream to the contents of the file represented by the given URI.
	@exception IOException Thrown if an I/O error occurred.
	@see URIInputStreamLocator
	@see #getResourceAsInputStream
	*/
	public final InputStream getInputStream(final URI uri) throws IOException
	{
		return getResourceAsInputStream(uri); //use the method we already had
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
	protected Object loadResource(final URI resourceURI, final MediaType mediaType) throws IOException  //G***change this to loadImage, loadClip, etc.
	{
		Object resource;  //this will be assigned if we run into no errors
		if(mediaType.getTopLevelType().equals(MediaTypeConstants.IMAGE))	//if this is an image
		{
			final String mediaSubType=mediaType.getSubtype(); //get the media sub-type
				//if this is a GIF, JPEG, PNG G***fix, or X_BITMAP image
			if(mediaSubType.equals(MediaTypeConstants.GIF) || mediaSubType.equals(MediaTypeConstants.JPEG) || mediaSubType.equals(MediaTypeConstants.PNG)/*G***fix || mediaSubType.equals(MediaTypeConstants.X_BITMAP)*/)
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
				throw new IOException("Unrecognized image type: \""+mediaType.getSubtype()+"\"; only \""+MediaType.JPEG+"\", \""+MediaType.PNG+"\", and \""+MediaType.GIF+"\" are currently supported.");	//G***i18n G***fix for other image types
		}
		else if(MediaTypeUtilities.isAudio(mediaType))	//if this is an audio media type
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

	/**Gets the location against which to resolve relative URIs. By default this
		will be the document's URI if the document was loaded from a URI.
	@return The location against which to resolve relative URIs, or <code>null</code>
		if there is no base URI.
//G***del	@exception URISyntaxException Thrown if the a URI could not be created.
	@see Document#StreamDescriptionProperty
	*/
/*G***del when works
	public URI getBaseURI()	//G***del throws URISyntaxException
	{
//TODO store the base URI in some other property, so we won't have to switch back and forth between URI and URL

		final Object streamDescription=getProperty(StreamDescriptionProperty); //get the stream description property value
			//G***maybe create a URIUtilities method to do this
		if(streamDescription instanceof URI)	//if the stream description is a URI
			return (URI)streamDescription;	//return the stream description as-is
		else if(streamDescription instanceof URL)	//if the stream description is a URL
		{
			try
			{
				return new URI(streamDescription.toString());	//create a URI from the stream description URL
			}
			catch(URISyntaxException uriSyntaxException)	//if we couldn't create a URI from the URL
			{
				Debug.error(uriSyntaxException);	//if it's a URL, we expect to be able to create a URI from it
				return null;	//don't return any URI
			}
		}
		else if (streamDescription instanceof File)	//if the stream description is a File
			return ((File)streamDescription).toURI();		//convert the File to a URI
		else	//if we don't recognize the string description (or if there isn't one)
			return null;	//show that there is no base URI
	}
*/

	/**Sets the location against which to resolve relative URIs. By default this
		will be the document's URI.
	@param baseURI The new location against which to resolve relative URIs.
	@see #BASE_URI_PROPERTY
	*/
	public void setBaseURI(final URI baseURI)
	{
		DocumentUtilities.setBaseURI(this, baseURI);	//store the base URI
	}

	/**Gets the location against which to resolve relative URIs.
	@return The location against which to resolve relative URIs, or <code>null</code>
		if there is no base URI.
//G***del	@exception URISyntaxException Thrown if the a URI could not be created.
	@see #BASE_URI_PROPERTY
	*/
	public URI getBaseURI()
	{
		return DocumentUtilities.getBaseURI(this);	//return the value of the base URI property
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
Debug.trace("inside XMLDocumet insertupdate");
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
		writeLock();	//lock the document for writing G***do we really need to do this, as applying styles doesn't modify the document?
//	G***fix		applyStyles(); //G***testing; put in the correct place, and make sure this gets called when repaginating, if we need to

/*G***fix
		final Element rootSwingElement=getRootElements()[0]; //get the first root element of the document -- this contains an element tree for each document loaded
		final int swingDocumentElementCount=rootSwingElement.getElementCount(); //find out how many root elements there are
		for(int swingDocumentElementIndex=0; swingDocumentElementIndex<swingDocumentElementCount; ++swingDocumentElementIndex) //look at each root element, each of which represents an XML document
		{
			final Element swingDocumentElement=rootSwingElement.getElement(swingDocumentElementIndex);  //get the first element, which is the root of the document tree
			insertBlockElementEnds(swingDocumentElement);	//G***testing
		}
*/
		writeUnlock();	//release the document writing lock
	}


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
					Debug.error(e);	//G***fix
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
		applyStyles(); //G***testing; put in the correct place, and make sure this gets called when repaginating, if we need to
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

	/**Converts the Swing document to an XML document.
	<p>This is a cover method for <code>createXMLDocument</code>.</p>
	@return A DOM tree representing the XML document.
	@see #createXMLDocument
	*/
	public org.w3c.dom.Document getXML()
	{
		return createXMLDocument();  //create an XML document from the Swing document
	}

	/**Converts the Swing document to an XML document.
	@return A DOM tree representing the XML document.
	*/
	protected org.w3c.dom.Document createXMLDocument()
	{
		final Element rootSwingElement=getRootElements()[0]; //get the first root element of the document -- this contains an element tree for each document loaded
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
	protected static org.w3c.dom.Document createXMLDocument(final Element swingElement)
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
	protected static org.w3c.dom.Node createXMLNode(final org.w3c.dom.Document xmlDocument, final Element swingElement)
	{
		return createXMLNode(xmlDocument, swingElement, 0);	//create an XML node at the bottom level
	}

	/**Converts the given Swing element to an XML node indenting to the given level.
	@param swingElement The Swing element containing the data to be converted to
		an XML node.
	@param level The zero-based level of indentation.
	@return A DOM element representing the Swing node.
	*/
	protected static org.w3c.dom.Node createXMLNode(final org.w3c.dom.Document xmlDocument, final Element swingElement, final int level)
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
					StringBufferUtilities.removeEveryChar(stringBuffer, ELEMENT_END_STRING+'\n');
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
		if(!XMLStyleUtilities.isXMLEmptyElement(attributeSet))  //if this element isn't an empty element, we'll add children
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
					XMLUtilities.appendText(xmlElement, StringUtilities.makeString('\t', level+1));	//indent to the correct level
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
				XMLUtilities.appendText(xmlElement, StringUtilities.makeString('\t', level));	//indent to the correct level
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





	/**Discovers any referenced styles to this document, loads the stylesheets,
		and applies the styles to the Swing element attributes.
	@param swingDocument The Swing document containing the data.
	*/
	public void applyStyles()
	{
Debug.trace("Ready to applystyles");  //G***fix
//G***important; fix		writeLock();  //get a lock on the document
		try
		{
Debug.trace("looking at first root element");  //G***fix
			final Element rootSwingElement=getRootElements()[0]; //get the first root element of the document -- this contains an element tree for each document loaded
	//G***del		for(int swingRootElementIndex=0; swingRootElementIndex<rootSwingElement.getElementCount(); ++swingRootElementIndex) //look at each root element
//G***del			Debug.assert(rootSwingElement.getElementCount()>0, "No Swing root element.");  //assert there is at least one root element
	//G***del		if(rootSwingElement.getElementCount()>0)  //if there is at least one root element
		  final int swingDocumentElementCount=rootSwingElement.getElementCount(); //find out how many root elements there are
		  for(int swingDocumentElementIndex=0; swingDocumentElementIndex<swingDocumentElementCount; ++swingDocumentElementIndex) //look at each root element, each of which represents an XML document
			{
				final Element swingDocumentElement=rootSwingElement.getElement(swingDocumentElementIndex);  //get the first element, which is the root of the document tree
/*G***del
System.out.println("applying stylesheet to document: "+swingDocumentElementIndex+" of "+rootSwingElement.getElementCount());  //G***del
System.out.println("base URL: "+XMLStyleConstants.getBaseURL(swingDocumentElement.getAttributes())); //G***del
*/
//G***del	Debug.trace("Before getting attributes");
//G***del 			final AttributeSet attributeSet=swingDocumentElement.getAttributes();  //get the element's attribute set
	/*G***fix
			Debug.assert(attributeSet!=null, "Missing attributes for document element.");  //assert that we have an attribute set
	//G***del		Debug.notify("Attribute set is: "+attributeSet.getClass().getName());
	//G***del		Debug.notify("Attribute set is mutable: "+new Boolean(attributeSet instanceof MutableAttributeSet));
			((MutableAttributeSet)attributeSet).addAttribute("testAttribute", "testValue"); //G***testing
	*/
/*G**del
	Debug.trace("Before setting test attribute");

			((MutableAttributeSet)attributeSet).addAttribute("testAttribute", "testValue"); //G***testing
	Debug.trace("After setting test attribute");
*/
				final URI documentURI=XMLStyleUtilities.getBaseURI(swingDocumentElement.getAttributes());  //get the URI of this document
				final StyleSheetList styleSheetList=getStylesheets(swingDocumentElement); //G***testing
				//apply the stylesheets
				final int styleSheetCount=styleSheetList.getLength(); //find out how many stylsheets there are
				for(int i=0; i<styleSheetCount; ++i) //look at each stylesheet
				{

				  //prepare a progress message: "Applying stylesheet X to XXXXX.html"
					final String progressMessage=MessageFormat.format("Applying stylesheet {0} to {1}", new Object[]{new Integer(i+1), documentURI!=null ? documentURI.toString() : "unknown"}); //G***i18n; fix documentURI if null
Debug.trace(progressMessage); //G***del
					fireMadeProgress(new ProgressEvent(this, APPLY_STYLESHEET_TASK, progressMessage, swingDocumentElementIndex, swingDocumentElementCount));	//fire a progress message saying that we're applying a stylesheet
//G***del System.out.println("applying stylesheet: "+i+" of "+styleSheetList.getLength());  //G***del
					final CSSStyleSheet cssStyleSheet=(CSSStyleSheet)styleSheetList.item(i);  //get a reference to this stylesheet, assuming that it's a CSS stylesheet (that's all that's currently supported)
				  applyStyleSheet(cssStyleSheet, swingDocumentElement);  //apply the stylesheet to the document
				}
Debug.trace("applying local styles"); //G***del
				fireMadeProgress(new ProgressEvent(this, APPLY_STYLESHEET_TASK, "Applying local styles", swingDocumentElementIndex, swingDocumentElementCount));	//fire a progress message saying that we're applying local styles G***i18n
				applyLocalStyles(swingDocumentElement); //go through and apply local styles in from the "style" attributes G***this is HTML-specific; fix
			}
	/*G***fix
			final String elementName=XMLStyleConstants.getXMLElementName(attributeSet); //get the name of this element
			final XMLDOMImplementation domImplementation=new XMLDOMImplementation();	//create a new DOM implementation G***later use some Java-specific stuff
			final DocumentType documentType;  //we'll create a document type only if we find a system ID
			final String docTypeSystemID=XMLStyleConstants.getXMLDocTypeSystemID(attributeSet); //get the document type system ID if there is one
			if(docTypeSystemID!=null) //if we found a system ID
			{
				final String docTypePublicID=XMLStyleConstants.getXMLDocTypePublicID(attributeSet); //get the document type public ID if there is one
				documentType=domImplementation.createDocumentType(elementName, docTypePublicID, docTypeSystemID);	//create the document type
			}
			else  //if there was no system ID
				documentType=null;  //show that we don't have a document type
	//G***fix		final DocumentType documentType=domImplementation.createDocumentType(elementName, OEB101_DOCUMENT_PUBLIC_ID, OEB101_DOCUMENT_SYSTEM_ID);	//create an OEB document type
			final org.w3c.dom.Document xmlDocument=domImplementation.createDocument(null, elementName, documentType);	//create the document
	//G***fix		final org.w3c.dom.Document xmlDocument=domImplementation.createDocument(OEB1_DOCUMENT_NAMESPACE_URI, ELEMENT_HTML, documentType);	//create an OEB XML document
				//create any processing instructions
			final Enumeration attributeNameEnumeration=attributeSet.getAttributeNames();  //get an enumeration of attribute names
			while(attributeNameEnumeration.hasMoreElements()) //while there are more attributes
			{
				final String attributeName=(String)attributeNameEnumeration.nextElement();  //get this attribute name
					//if this is a processing instruction
				if(attributeName.startsWith(XMLStyleConstants.XML_PROCESSING_INSTRUCTION_ATTRIBUTE_START))
				{
						//trim the "$?" from the beginning of the attribute name to get the name of the processing instruction
					final String xmlProcessingInstructionTarget=attributeName.substring(XMLStyleConstants.XML_PROCESSING_INSTRUCTION_ATTRIBUTE_START.length());
					final Object attributeValue=attributeSet.getAttribute(attributeName);  //get the value of the attribute, which should be a string
					Debug.assert(attributeValue instanceof String, "XML processing instruction value is not a string.");
						//create a processing instruction with the correct value
					final ProcessingInstruction processingInstruction=xmlDocument.createProcessingInstruction(xmlProcessingInstructionTarget, attributeValue.toString());
					xmlDocument.appendChild(processingInstruction); //add this processing instruction
	//G***del				xmlElement.setAttributeNS(null, attributeName, attributeValue.toString());  //set the attribute value G***fix for namespaces


				}
			}
			final org.w3c.dom.Node xmlNode=createXMLNode(xmlDocument, swingElement); //create the root element
			Debug.assert(xmlNode.getNodeType()==xmlNode.ELEMENT_NODE, "Swing root XML node not an XML element."); //make sure we got back an XML element
			xmlDocument.appendChild(xmlNode);	//add the root element to the document
			return xmlDocument; //return the document we constructed
	*/
		}
		finally
		{
//G***important; fix			writeUnlock();  //always release the lock on the document
		}
	}

	/**Retrieves an array of all referenced stylesheets in the document by href.
		The stylesheet descriptors gathered reference:
		<ul>
			<li>External stylesheets referenced from XML processing instructions.</li>
			<li>External stylesheets referenced from HTML/OEB link elements.</li>
		</ul>
	@param swingDocumentElement The Swing element that represents the XML document.
	@return An array of style sheet descriptors, each referencing a stylesheet.
	*/
	protected XMLStyleSheetDescriptor[] getStylesheetDescriptors(final Element swingDocumentElement)
	{
		final List styleSheetDescriptorList=new ArrayList();  //create a list to hold stylesheet descriptors
		final AttributeSet attributeSet=swingDocumentElement.getAttributes();  //get the element's attribute set G***do we know this isn't null?
		final NameValuePair[] processingInstructions=XMLStyleUtilities.getXMLProcessingInstructions(attributeSet);  //get the processing instructions, if any (this will never return null)
		  //find stylesheet references from processing instructions
		for(int processingInstructionIndex=0; processingInstructionIndex<processingInstructions.length; ++processingInstructionIndex) //look at each processing instruction
		{
			final NameValuePair processingInstruction=processingInstructions[processingInstructionIndex];  //get this processing instruction's values
				//if this is a stylesheet processing instruction
		  if(XMLStyleSheetConstants.XML_STYLESHEET_PROCESSING_INSTRUCTION.equals(processingInstruction.getName()))
			{
				final String processingInstructionData=(String)processingInstruction.getValue();  //get the processing instruction's data, assuming it's a string
				//G***check the media type, etc. here
					//get the href pseudo-attribute, if it is defined
//G***del				final XMLCSSStyleSheet styleSheet=new XMLCSSStyleSheet(null);	//create a new stylesheet G***what about a valid document owner? can we get by without one?
				final String href=XMLUtilities.getProcessingInstructionPseudoAttributeValue(processingInstructionData, XMLStyleSheetConstants.HREF_ATTRIBUTE);
				final XMLStyleSheetDescriptor styleSheetDescriptor=new XMLStyleSheetDescriptor(href); //create a new descriptor for this stylesheet G***fix for media type, title, etc.
				styleSheetDescriptorList.add(styleSheetDescriptor); //add the stylesheet descriptor to our list
			}
		}
		//G***fix for stylesheet instructions in HTML/OEB link elements
		return (XMLStyleSheetDescriptor[])styleSheetDescriptorList.toArray(new XMLStyleSheetDescriptor[styleSheetDescriptorList.size()]);  //convert the stylesheet descriptors to an array and return them
	}



	/**Applies all referenced styles in the given XML document. The styles
		applied include, in this order:
		<ul>
			<li>External stylesheets referenced from XML processing instructions.</li>
			<li>External stylesheets referenced from HTML/OEB link elements.</li>
			<li>Internal stylesheets in the HTML/OEB style element..</li>
			<li>Local styles of the "style" attribute of HTML and OEB documents.</li>
		</ul>
	@param swingDocumentElement The Swing element that represents the XML document.
	*/
/*G***fix
	protected applyxStyles(final Element swingDocumentElement)
	{
				final Element swingDocumentElement=rootSwingElement.getElement(swingDocumentElementIndex);  //get the first element, which is the root of the document tree
	}
*/


	/**Finds and loads all stylesheets referenced in the document. The
		external stylesheets are:
		<ul>
			<li>External stylesheets referenced from XML processing instructions.</li>
			<li>External stylesheets referenced from HTML/OEB link elements.</li>
			<li>Internal stylesheets in the HTML/OEB style element.</li>
		</ul>
	@param swingDocumentElement The Swing element that represents the XML document.
	@return A list of stylesheets.
	*/
	protected StyleSheetList getStylesheets(final Element swingDocumentElement)
	{
		final XMLStyleSheetList styleSheetList=new XMLStyleSheetList(); //create a new list to hold the stylesheets
		  //get all default stylesheets
		final String[] namespaceURIArray=getNamespaceURIs(swingDocumentElement);  //get all namespaces used in this document
		for(int i=0; i<namespaceURIArray.length; ++i) //look at each namespace
		{
			final String namespaceURI=namespaceURIArray[i]; //get this namespace
			final CSSStyleSheet cssStyleSheet=XMLCSSUtilities.getDefaultStyleSheet(namespaceURI);  //get the default stylesheet for the given namespace
			if(cssStyleSheet!=null) //if we know about a default stylesheet for this namespace
			{
Debug.trace("Found default stylesheet for namespace: ", namespaceURI);  //G***del
//G***del Debug.trace("Default stylesheet: ", cssStyleSheet);  //G***testing; del
				styleSheetList.add(cssStyleSheet);  //add the default stylesheet to the list
			}
		}
			//create a new CSS processor for parsing all the stylesheets, showing that we'll worry about getting the needed input streams (this allows our subclasses to access zip files, for instance)
		final XMLCSSProcessor cssProcessor=new XMLCSSProcessor(this);
		final AttributeSet documentAttributeSet=swingDocumentElement.getAttributes();  //get the element's attribute set G***do we know this isn't null?
		  //gather all the references to external stylesheets
		final XMLStyleSheetDescriptor[] styleSheetDescriptorArray=getStylesheetDescriptors(swingDocumentElement);
		if(styleSheetDescriptorArray.length>0)  //if there are stylesheet descriptors
		{
			URI baseURI=XMLStyleUtilities.getBaseURI(documentAttributeSet); //get the base URI of the XML document
			if(baseURI==null) //if we couldn't found a base URI in the attributes
				baseURI=getBaseURI();	//get the base URI from the document
//G***del when not needed			final Iterator styleSheetDescriptorIterator=styleSheetDescriptorList.iterator();  //get an iterator to all stylesheet descriptors we've gathered so far
//G***del when not needed			while(styleSheetDescriptorIterator.hasNext()) //while there are more stylesheet descriptors
		  for(int i=0; i<styleSheetDescriptorArray.length; ++i) //look at each stylesheet descriptor
			{
//G***del when not needed				final XMLStyleSheetDescriptor styleSheetDescriptor=(XMLStyleSheetDescriptor)styleSheetDescriptorIterator.next();  //get the next descriptor
				final XMLStyleSheetDescriptor styleSheetDescriptor=styleSheetDescriptorArray[i];  //get the next descriptor
				assert styleSheetDescriptor.getHRef()!=null : "Stylesheet processing instruction has null href";  //G***fix
				//G***do whatever we need to do for the media type, title, etc.

//G***del Debug.trace("Looking at stylesheet descriptor: ", styleSheetDescriptor.getHRef());  //G***del
				try
				{
					final URI styleSheetURI=getResourceURI(styleSheetDescriptor.getHRef());	//create a URI from the original URI of the XML document and the stylesheet href
//G***del					final URL styleSheetURL=URLUtilities.createURL(baseURL, styleSheetDescriptor.getHRef());	//create a URL from the original URL of the XML document and the stylesheet href
					final InputStreamReader styleSheetReader=new InputStreamReader(getResourceAsInputStream(styleSheetURI));	//get an input stream to the stylesheet G***use the document's encoding here
					try
					{
						final CSSStyleSheet cssStyleSheet=cssProcessor.parseStyleSheet(styleSheetReader, styleSheetURI); //parse the stylesheet
//G***del Debug.trace("parsed stylesheet: ", cssStyleSheet);  //G***testing; del
						styleSheetList.add(cssStyleSheet);  //add this stylesheet to our list
					}
					finally
					{
						styleSheetReader.close(); //always close the stylesheet reader
					}
				}
				catch(IOException ioException)  //if there are any I/O exceptions
				{
					Debug.warn(ioException);  //G***fix better
				}
				catch(URISyntaxException uriSyntaxException)  //if there are any URI syntax errors
				{
					Debug.warn(uriSyntaxException);  //G***fix better
				}
			}
		}
		  //process and gather any internal stylesheets G***right now, this is very HTML/OEB specific; fix to check the namespace or something
		final int childElementCount=swingDocumentElement.getElementCount();  //find out how many direct children are in the document
		for(int childIndex=0; childIndex<childElementCount; ++childIndex)  //look at the direct children of the document element
		{
			final Element childElement=swingDocumentElement.getElement(childIndex); //get a reference to the child element
			final AttributeSet childAttributeSet=childElement.getAttributes();  //get the child element's attributes
			final String childElementLocalName=XMLStyleUtilities.getXMLElementLocalName(childAttributeSet);  //get the child element local name
					//if this element is <head> and it's an HTML <head>
			if(XHTMLConstants.ELEMENT_HEAD.equals(childElementLocalName)
				  && XHTMLSwingTextUtilities.isHTMLElement(childAttributeSet, documentAttributeSet))
			{
				getInternalStylesheets(childElement, swingDocumentElement, cssProcessor, styleSheetList); //get the internal stylesheets from the HTML <head> element
			}
		}
		return styleSheetList;  //return the list of stylesheets we collected
	}

	/**Finds and parses any internal stylesheets contained in the given element or
		its children. All stylesheets found in the document will be added to the
		list in the order encoundered in document order.
	@param swingElement The Swing element to check.
	@param swingDocumentElement The Swing element that represents the document
		element of the XML document.
	@param cssProcessor The processor that processes CSS stylesheets.
	@param styleSheetList The list to which stylesheets should be added.
	*/
	protected void getInternalStylesheets(final Element swingElement, final Element swingDocumentElement, final XMLCSSProcessor cssProcessor, final List styleSheetList)
	{
		final AttributeSet attributeSet=swingElement.getAttributes(); //get the attributes of this element
		final String elementLocalName=XMLStyleUtilities.getXMLElementLocalName(attributeSet);  //get the element's local name
//G***del		if(XHTMLConstants.ELEMENT_STYLE.equals(elementLocalName)) //if this is a style element G***check the namespace and/or media type or something, too
		  //if this is an HTML <style>
		if(XHTMLConstants.ELEMENT_STYLE.equals(elementLocalName)
				&& XHTMLSwingTextUtilities.isHTMLElement(attributeSet, swingDocumentElement.getAttributes()))
		{
			try
			{
				final String styleText=SwingTextUtilities.getText(swingElement);  //get the text of the style element
				final String descriptionString="Internal Style Sheet"; //create a string to describe the internal style sheet G***i18n
				final CSSStyleSheet stylesheet=cssProcessor.parseStyleSheet(styleText, descriptionString); //parse the stylesheet
				styleSheetList.add(stylesheet); //add this stylesheet to the list
			}
			catch(BadLocationException badLocationException)  //we should never get a bad location for valid elements
			{
				Debug.error(badLocationException);  //we expect never to get bad element locations
			}
			catch(IOException ioException)  //if there are any I/O exceptions
			{
				Debug.warn(ioException);  //G***fix better
			}
		}
		else  //if this is not a style element
		{
		  final int swingChildElementCount=swingElement.getElementCount(); //find out how many child elements there are
		  for(int swingChildElementIndex=0; swingChildElementIndex<swingChildElementCount; ++swingChildElementIndex) //look at each child element
			{
				final Element swingChildElement=swingElement.getElement(swingChildElementIndex);  //get this child element
				getInternalStylesheets(swingChildElement, swingDocumentElement, cssProcessor, styleSheetList);  //get internal stylesheets contained in this element
			}
		}
	}

	/**Finds the namespaces used by the document.
		<p>If no namespace is indicated, a default namespace is derived from the
		document media type. For example, a document with no namespace indicated
		but with a media type of <code>text/html</code> will include the namespace
		<code>http://www.w3.org/1999/xhtml</code>.</p>
		<p>Currently this method only examines the root XML element, so other
		vocabularies within the document will be ignored.</p>
	@param swingDocumentElement The Swing element that represents the XML document.
	@return A non-<code>null</code> array of namespace URIs.
	*/
	protected String[] getNamespaceURIs(final Element swingDocumentElement)  //G***fix to actually look through all the namespaces, maybe, but that could be intensive -- on the other hand, a subclass could get that information from the OEB package, overriding the intensive part
	{
//G***fix when we support multiple namespaces		final List namespaceList=new ArrayList(); //create a list to store the namespaces
		final AttributeSet attributeSet=swingDocumentElement.getAttributes();  //get the element's attribute set G***do we know this isn't null?
		String namespaceURI=XMLStyleUtilities.getXMLElementNamespaceURI(attributeSet);  //see what namespace the XML document element is in
		if(namespaceURI==null)  //if there is no namespace defined
		{
			final MediaType mediaType=XMLStyleUtilities.getMediaType(attributeSet); //see what media type this document is
			if(mediaType!=null) //if we have a media type G***change this so that these media types aren't hard-coded in
			{
Debug.trace("getting default namespace URI, found media type: ", mediaType);  //G***del
				if(XHTMLUtilities.isHTML(mediaType))	//if this is an HTML media type TODO this can probably be improved or the method placed elsewhere
				{					
					namespaceURI=XHTMLConstants.XHTML_NAMESPACE_URI.toString();  //use the XHTML namespace
					if(mediaType.equals(MediaType.TEXT_X_OEB1_DOCUMENT)) //if the media type is for an OEB document
						namespaceURI=OEBConstants.OEB1_DOCUMENT_NAMESPACE_URI.toString();  //use the OEB document namespace
				}
Debug.trace("namespace URI: ", namespaceURI);  //G***del
			}
		}
		if(namespaceURI!=null)  //if we found a namespace
			return new String[]{namespaceURI};  //return the namespace we found
		else  //if we didn't find a namespace
			return new String[]{};  //G***fix; make efficient
	}



	/**Determines the style of each element in the given element and its children
		based on the given stylesheet.
		This method requires that the document already be locked for writing.
	@param styleSheet The stylesheet to apply to the Swing element.
	@param swingElement The Swing element to which styles should be applied, along
		with its children.
	*/
	protected static void applyStyleSheet(final CSSStyleSheet styleSheet, final Element swingElement) //G***maybe put this in a Swing utility class
	{
		final AttributeSet attributeSet=swingElement.getAttributes(); //get the attributes of this element
		final String elementLocalName=XMLStyleUtilities.getXMLElementLocalName(attributeSet);  //get the element's local name for quick lookup
//G***del Debug.trace("applying stylesheet to element: ", elementLocalName);  //G***del
		final CSSRuleList cssRuleList=styleSheet.getCssRules(); //get the list of CSS rules
		for(int ruleIndex=0; ruleIndex<cssRuleList.getLength(); ++ruleIndex)	//look at each of our rules
		{
			if(cssRuleList.item(ruleIndex).getType()==CSSRule.STYLE_RULE)	//if this is a style rule G***fix for other rule types
			{
				final CSSStyleRule cssStyleRule=(CSSStyleRule)cssRuleList.item(ruleIndex);	//get a reference to this style rule in the stylesheet
				if(isApplicable(cssStyleRule, swingElement, elementLocalName)) //if this style rule applies to this element
				{
//G***del when not needed		  		final AttributeSet attributeSet=swingElement.getAttributes();  //get the element's attribute set G***do we know this isn't null?
				  XMLCSSStyleDeclaration elementStyle=(XMLCSSStyleDeclaration)XMLCSSStyleUtilities.getXMLCSSStyle(attributeSet);  //get this element's style G***fix to use the normal CSS DOM, putting the importStyle() method into a generic Swing utility class
				  if(elementStyle==null) //if there is no existing style (usually the editor kit will have supplied one already to reduce the performance hit here)
					{
						elementStyle=new XMLCSSStyleDeclaration();  //create an empty default style
						assert attributeSet instanceof MutableAttributeSet : "Attribute set not mutable";
						XMLCSSStyleUtilities.setXMLCSSStyle((MutableAttributeSet)attributeSet, elementStyle);	//put the style in the attributes
					}
//G***del					Debug.trace("style rule is of type: ", cssStyleRule.getClass().getName());  //G***del
					elementStyle.importStyle((XMLCSSStyleDeclaration)cssStyleRule.getStyle());  //import the style G***use generic DOM and move this to a utility class
/*G***del when works
					if(elementStyle!=null)  //if there is an existing style
					  elementStyle.importStyle((XMLCSSStyleDeclaration)cssStyleRule.getStyle());  //import the style G***use generic DOM and move this to a utility class
					else  //if a style does not yet exist
						;//G***fix; create a new style
*/
				}
			}
		}
			//apply the stylesheet to the child elements
		for(int childIndex=swingElement.getElementCount()-1; childIndex>=0; --childIndex) //look at each child element, starting from the last to the first because order doesn't matter
		{
			final Element childSwingElement=swingElement.getElement(childIndex);  //get this child
			if(!AbstractDocument.ContentElementName.equals(childSwingElement.getName())) //if this isn't content (content never gets styled)
		  {
			  applyStyleSheet(styleSheet, childSwingElement); //apply this stylesheet to the child element
		  }
		}
	}

	/**Parses any local styles contained in an element as a "style" attribute and
		and applies them to the Swing element. Recursively performs the same
		function on all the element's children.
		Currently ignores the namespace and processes the style information on
		every element with a "style" attribute.
	@param document The XML document that that contains style information.
//G***fix all this exception stuff
	@except IOException Thrown when an i/o error occurs.
	@except ParseUnexpectedDataException Thrown when an unexpected character is found.
	@except ParseEOFException Thrown when the end of the input stream is reached unexpectedly.
	*/
//G***this is HTML-specific; decide where to put this and when to call it
//G***actually, these parseXXXStyleSheets() should probably be inside an XMLStyleSheetProcessor which calls the correct type of processor based upon the style sheet type
	public static void applyLocalStyles(final Element swingElement)  //G***put in a separate utility class  //G**del exceptions throws IOException, ParseUnexpectedDataException, ParseEOFException
	{
		final AttributeSet attributeSet=swingElement.getAttributes(); //get the element's attributes
			//TODO what if xhtml:style is placed on some element?
		if(XMLStyleUtilities.isXMLAttributeDefined(attributeSet, null, "style")) //if this element has the style attribute G***use a constant, fix for a general attribute name case
		{
			final String styleValue=XMLStyleUtilities.getXMLAttributeValue(attributeSet, null, "style"); //get the value of the style attribute G***use a constant, fix for a general attribute name case
			if(styleValue.length()!=0)  //if there is a style value
			{
	//G***del Debug.trace("Found local style value: ", styleValue); //G***del
				final String elementName=XMLStyleUtilities.getXMLElementName(attributeSet); //get the element's name for informational purposes
				final XMLCSSStyleDeclaration cssStyle=new XMLCSSStyleDeclaration(); //create a new style declaration
				try
				{
						//G***we may want to change this to the new style processing methods
					final ParseReader localStyleReader=new ParseReader(styleValue, "Element "+elementName+" Local Style");	//create a string reader from the value of this local style attribute G***i18n
					XMLCSSProcessor.parseRuleSet(localStyleReader, cssStyle); //read the style into our style declaration
					XMLCSSStyleDeclaration elementStyle=(XMLCSSStyleDeclaration)XMLCSSStyleUtilities.getXMLCSSStyle(attributeSet);  //get this element's style G***fix to use the normal CSS DOM, putting the importStyle() method into a generic Swing utility class
				  if(elementStyle==null) //if there is no existing style (usually the editor kit will have supplied one already to reduce the performance hit here)
					{
						elementStyle=new XMLCSSStyleDeclaration();  //create an empty default style
						assert attributeSet instanceof MutableAttributeSet : "Attribute set not mutable";
						XMLCSSStyleUtilities.setXMLCSSStyle((MutableAttributeSet)attributeSet, elementStyle);	//put the style in the attributes
					}
					elementStyle.importStyle((XMLCSSStyleDeclaration)cssStyle);  //import the style G***use generic DOM and move this to a utility class
/*G***del when works
					if(elementStyle!=null)  //if there is an existing style
						elementStyle.importStyle((XMLCSSStyleDeclaration)cssStyle);  //import the style G***use generic DOM and move this to a utility class
					else  //if a style does not yet exist
						;//G***fix; create a new style
*/
				}
				catch(IOException ioException)  //if there is an error with the local style
				{
					Debug.warn(ioException); //G***fix
				}
	//G***del			((XMLElement)element).setLocalCSSStyle(style);  //set the element's style to whatever we constructed G***eventually use a separate style tree instead of the element itself
			}
		}
			//apply local styles to the child elements
		for(int childIndex=swingElement.getElementCount()-1; childIndex>=0; --childIndex) //look at each child element, starting from the last to the first because order doesn't matter
		{
			final Element childSwingElement=swingElement.getElement(childIndex);  //get this child
			if(!AbstractDocument.ContentElementName.equals(childSwingElement.getName())) //if this isn't content (content never gets styled)
		  {
			  applyLocalStyles(childSwingElement); //apply this local styles to the child element
		  }
		}
	}

	/**Determines whether the given selector applies to the specified element.
		Currently this method only checks the selector's tag name and the class,
		in both instances ignoring the element's namespace.
	@param selectorContext The description of this stop in the selector context
		path.
	@param swingElement The Swing element whose name and class should match those
		contained in this selector.
//G***del	@see XMLCSSStyleRule#appliesTo
	*/
	protected static boolean isApplicable(final XMLCSSSelector selectorContext, final Element swingElement)  //G***probably move to a Swing utilitie class -- actually, this method may be modified or go away when we fully switch to the DOM
	{
//G***del Debug.trace("XMLCSSSelector checking to see if "+element.getNodeName()+" matches "+getCssText()); //G***del
		final AttributeSet attributeSet=swingElement.getAttributes(); //get the attributes of this element
		final String localName=XMLStyleUtilities.getXMLElementLocalName(attributeSet);  //get the element's local name
			//G***later, add the CSS ID checking
			//G**later make this more robust for CSS 2 and CSS 3
		if(selectorContext.getTagName().length()==0 || selectorContext.getTagName().equals(localName))  //if the tag names match, or we don't have a tag name to match with (which means we'll be matching class only)
		{
//G***del Debug.trace("Element "+element.getNodeName()+" matched, now checking to see if class: "+element.getAttributeNS(null, "class")+" equals the tag we expect: "+getTagClass());	//G***del
			if(selectorContext.getTagClass().length()==0 || selectorContext.getTagClass().equals(XMLStyleUtilities.getXMLAttributeValue(attributeSet, null, "class")))	//if the class names match as well (or there isn't a specified class in this selector) G***check for namespaces here G***use a constant here
				return true;
		}
		return false;	//if we get to this point, this selector doesn't apply to this element
	}

	/**Determines whether this contextual selector (represented by an array of
		selectors) applies to the specified element.
	@param contextArray The array of nested contexts to compare to the element
		hierarchy.
	@param swingElement The element this context array might apply to.
	@param elementLocalName The element's local name for quick lookup.
//G***del	@see XMLCSSSelector#appliesTo
	*/
	protected static boolean isApplicable(final XMLCSSSelector[] contextArray, Element swingElement, final String elementLocalName) //G***probably move to a Swing utilitie class -- actually, this method may be modified or go away when we fully switch to the DOM
	{
		//first see if we can do a quick comparison on the most common type of selector: name-based selectors
		if(contextArray.length==1)  //if there is only one context in the array
		{
			final XMLCSSSelector selectorContext=contextArray[0];	//get the only context of the selector
				//if this context only looks at the tag name
		  if(selectorContext.getTagName().length()>0 && selectorContext.getTagClass().length()==0)  //G***fix for CSS2 and CSS3
			{
//G***del Debug.trace("we can do a quick CSS selection for element: ", elementLocalName);  //G***del
			  return selectorContext.getTagName().equals(elementLocalName); //compare tag names here without going on to anything more complicated
			}
		}
//G***del Debug.trace("we *cannot* do a quick CSS selection for element: ", elementLocalName);  //G***del
		for(int contextIndex=contextArray.length-1; contextIndex>=0; --contextIndex)	//look at each context for this selector, working from the last one (applying to this element) to the first one (applying to an ancestor of this element)
		{
//G***del Debug.trace("Checking element: "+element.getNodeName());
			final XMLCSSSelector selectorContext=contextArray[contextIndex];	//get this context of the selector
//G***del Debug.trace("against: "+selectorContext.getCssText());
			if(!isApplicable(selectorContext, swingElement))	//if this selector context does not apply to this element
				return false;	//this entire contextual selector does not apply
//G***del Debug.trace("matches");
			if(contextIndex>0)	//if we're still working our way up the chain
			{
				final Element swingParentElement=swingElement.getParentElement(); //get this element's parent
				if(swingParentElement!=null/*G***make sure we haven't reached the very top  && element.getParentNode().getNodeType()==Element.ELEMENT_NODE*/)	//if this element has a parent element node
					swingElement=(Element)swingParentElement;	//we'll check the earlier context with this element's parent node
				else	//if this element has no parent node
					return false;	//since we're not at the top of the context chain, this element can't match since it has no parents to compare
			}
		}
		return true;	//if we make it here, we've worked our way up the selector context chain and every element along the way has applied to the appropriate selector
	}

	/**Determines whether, based upon this style rule's selectors, the given style
		applies to the specified element.
	@param cssStyleRule The style rule to check against.
	@param swingElement The element this style might apply to.
	@param elementLocalName The element's local name for quick lookup.
//G***del	@see XMLCSSSelector#appliesTo
	*/
	protected static boolean isApplicable(final CSSStyleRule cssStyleRule, final Element swingElement, final String elementLocalName) //G***probably move to a Swing utilities class
	{
//G***del Debug.trace("Checking to see if: "+getSelectorText()+" applies to: "+element.getNodeName());
		final XMLCSSStyleRule xmlCSSStyleRule=(XMLCSSStyleRule)cssStyleRule;  //G***fix; right now we use special features of our CSS DOM implementation -- fix to use just the normal CSS DOM
		for(int selectorIndex=0; selectorIndex<xmlCSSStyleRule.getSelectorArrayList().size(); ++selectorIndex)	//look at each selector array
		{
			final XMLCSSSelector[] contextArray=(XMLCSSSelector[])xmlCSSStyleRule.getSelectorArrayList().get(selectorIndex);	//get a reference to this array of selectors
/*G***del
Debug.trace("Checking against the following context, right-to-left:");
		for(int contextIndex=contextArray.length-1; contextIndex>=0; --contextIndex)	//G***del; testing
		{
			final XMLCSSSelector selectorContext=contextArray[contextIndex];	//
Debug.trace("Context "+contextIndex+": "+selectorContext.getTagName());	//G***del
		}
*/
			if(isApplicable(contextArray, swingElement, elementLocalName))	//if this context array applies to the element
			{
//G***del Debug.trace("Context array applies to element "+element.getNodeName());	//G***del
				return true;	//we don't need to check the others; we've found a match
			}
		}
		return false;	//if none of our array of contextual selectors match, show that this style rule doesn't apply to this element
	}

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



}
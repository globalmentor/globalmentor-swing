package com.garretwilson.swing.text;

import java.awt.Font;
import java.net.URI;
import java.io.*;

import javax.swing.event.*;
import javax.swing.text.*;

import com.garretwilson.io.*;
import static com.garretwilson.lang.Objects.*;
import com.garretwilson.rdf.*;
import com.garretwilson.swing.event.ProgressEvent;
import com.garretwilson.swing.event.ProgressListener;
import com.garretwilson.swing.text.xml.css.XMLCSSStyleContext;
import com.garretwilson.util.Debug;

/**A document that provised basic functionality including:
<ul>
	<li>Knowing how to retrieve streams to URIs.</li>
	<li>Knowing how to change the relative font size.</li>
	<li>Supporting progress listeners.</li>
</ul>
@author Garret Wilson
*/
public class BasicStyledDocument extends DefaultStyledDocument implements URIAccessible
{

	/**The name of the document property which may contain the loaded publication description.*/
	public final static String PUBLICATION_PROPERTY_NAME="publication";

	/**The list of progress event listeners.*/
	private EventListenerList progressListenerList=new EventListenerList();
	
	/**The access to input streams via URIs, if one exists.*/
	private final URIInputStreamable uriInputStreamable;
	
		/**@return The access to input streams via URIs*/
		public URIInputStreamable getURIInputStreamable() {return uriInputStreamable;}

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

	/**Constructs a default basic styled document with content with a size of <code>BUFFER_SIZE_DEFAULT</code> 
	 	and a style context that is scoped by the lifetime of the document and is not shared with other documents.
	@param uriInputStreamable The source of input streams for resources.
	@exception NullPointerException if the new source of input streams is <code>null</code>.
	*/
	public BasicStyledDocument(final URIInputStreamable uriInputStreamable)
	{
		this(new XMLCSSStyleContext(), uriInputStreamable);	//construct the class with a new default style context	//TODO refactor out basic style context features for fonts
	}

	/**Constructs a styled document with the default content storage implementation and a shared set of styles.
	@param styles Resources and style definitions which may be shared across documents.
	@param uriInputStreamable The source of input streams for resources.
	@exception NullPointerException if the new source of input streams is <code>null</code>.
	*/
	public BasicStyledDocument(final StyleContext styles, final URIInputStreamable uriInputStreamable)
	{
		this(new GapContent(BUFFER_SIZE_DEFAULT), styles, uriInputStreamable);	//create the document with gap content
  }

	/**Constructs a styled document with given content and styles. 
	@param content The container for the content.
	@param styles Resources and style definitions which may be shared across documents.
	@param uriInputStreamable The source of input streams for resources.
	@exception NullPointerException if the new source of input streams is <code>null</code>.
	*/
	public BasicStyledDocument(final Content content, final StyleContext styles, final URIInputStreamable uriInputStreamable)
	{
		super(content, styles);	//construct the parent class
		this.uriInputStreamable=checkInstance(uriInputStreamable, "Missing URIInputStreamable");	//store the URIInputStreamable
		//G***fix		setProperty(AbstractDocument.I18NProperty);  //G***testing i18n
		//G***fix		putProperty("i18n", Boolean.TRUE);  //G***testing i18n
		Debug.trace("Document i18n property: ", getProperty("i18n")); //G***testing i18n		
	}

	/**Gets the font from an attribute set
		The actual font is obtained from the document's attribute context,
		similar to <code>DefaultStyledDocument.getFont()</code> (which this function
		overrides), except that this function	may decide to change the font size
		before actually creating the font using the attribute context.
	@param attributeSet The attribute set from which to retrieve values.
	@return The constructed font.
	@see DefaultStyledDocument.getFont
	@see XMLStyleContext#getFont
	*/
	public Font getFont(final AttributeSet attributeSet)
	{
		final float zoomFactor=DocumentUtilities.getZoom(this, DocumentConstants.DEFAULT_ZOOM);  //get the zoom factor, assuming a default value if no value is specified
		final StyleContext styles=(StyleContext)getAttributeContext();	//get the style context
		if(styles instanceof XMLCSSStyleContext)	//TODO refactor into a generic style context that supports zoom
		{
			return ((XMLCSSStyleContext)styles).getFont(attributeSet, zoomFactor);	//return the font after figuring in the zoom factor
		}
		else	//if this style context doesn't support zoom
		{
			return styles.getFont(attributeSet);	//just return the font normally
		}
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
		return ((XMLCSSStyleContext)getAttributeContext()).getFont(c, style, size);  //return a font that supports the character TODO refactor
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

	/**@return The description of the publication, or <code>null</code> if there is no publication associated with this document.*/
	public RDFResource getPublication()
	{
		return asInstance(getProperty(PUBLICATION_PROPERTY_NAME), RDFResource.class); //get the publication from the document, if there is one
	}
	
	/**Sets the description of the publication.
	@param publication The publication description.
	*/
	public void setPublication(final RDFResource publication)
	{
		putProperty(PUBLICATION_PROPERTY_NAME, publication);
	}

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

	/**Returns an input stream from a given URI.
	This implementation delegates to the editor kit's <code>URIInputStreamable</code>.
	@param uri A complete URI to a resource.
	@return An input stream to the contents of the resource represented by the given URI.
	@exception IOException Thrown if an I/O error occurred.
	*/
	public final InputStream getInputStream(final URI uri) throws IOException
	{
		return getURIInputStreamable().getInputStream(uri);	//delegate to our own URIInputStreamable
	}

	/**Returns an output stream for the given URI.
	The calling class has the responsibility for closing the output stream.
	@param uri A URI to a resource.
	@return An output stream to the contents of the resource represented by the given URI.
	@exception IOException Thrown if an I/O error occurred.
	*/
	public OutputStream getOutputStream(final URI uri) throws IOException
	{
		throw new IOException("Document output not yet supported.");
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

}
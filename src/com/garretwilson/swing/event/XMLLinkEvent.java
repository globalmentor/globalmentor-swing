package com.garretwilson.swing.event;

import java.net.URI;

import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Element;

import com.garretwilson.net.URIUtilities;

/**An event which represents a link within XML.
Besides the normal <code>HyperlinkEvent</code> properties,
this link is primarily concerned with the link URI rather
than the link URL.
@author Garret Wilson
*/
public class XMLLinkEvent extends HyperlinkEvent
{

	/**The URI to which this link refers.*/
	private final URI uri;

		/**Gets the URI that the link refers to.
		@return The URI to which this link refers.
		*/
		public URI getURI() {return uri;}

	/**Creates a new object representing an XML link event.
	The other constructor is preferred, as it provides more
		information if a URI could not be formed.  This constructor
		is primarily for backward compatibility.
	@param source The object responsible for the event.
	@param type The event type.
	@param uri The affected URI.
	*/
	public XMLLinkEvent(final Object source, final EventType type, final URI uri)
	{
		this(source, type, uri, null);
	}

	/**Creates a new object representing an XML link event.
	@param source The object responsible for the event.
	@param type The event type.
	@param uri The affected URI. This may be <code>null</code> if a valid URI
		could not be created.
	@param description The description of the link. This may be useful
		when attempting to form a URI resulted in a URISyntaxException.
		The description provides the text used when attempting to form the
		URI.
	*/
	public XMLLinkEvent(final Object source, final EventType type, final URI uri, final String description)
	{
		this(source, type, uri, description, null);
	}

	/**Creates a new object representing an XML link event.
	@param source The object responsible for the event.
	@param type The event type.
	@param uri The affected URI. This may be <code>null</code> if a valid URI
		could not be created.
	@param description The description of the link.  This may be useful
		when attempting to form a URI resulted in a URISyntaxException.
	The description provides the text used when attempting to form the
		URI.
	@param sourceElement The Element in the Document representing the
		anchor
	@since 1.4
	*/
	public XMLLinkEvent(final Object source, final EventType type, final URI uri, final String description, final Element sourceElement)
	{
		super(source, type, URIUtilities.toValidURL(uri), description, sourceElement);	//construct the parent class with a URL, if one can be constructed from the URI
		this.uri=uri;	//save the URI
	}
}
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

package com.globalmentor.swing.event;

import java.net.URI;

import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Element;

import com.globalmentor.net.URIs;

/**An event which represents a link within XML.
Besides the normal {@link HyperlinkEvent} properties,
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
		super(source, type, URIs.toValidURL(uri), description, sourceElement);	//construct the parent class with a URL, if one can be constructed from the URI
		this.uri=uri;	//save the URI
	}
}
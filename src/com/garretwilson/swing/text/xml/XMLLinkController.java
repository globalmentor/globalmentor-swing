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

package com.garretwilson.swing.text.xml;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import com.garretwilson.swing.event.XMLLinkEvent;
import com.globalmentor.java.*;
import com.globalmentor.log.Log;
import com.globalmentor.net.*;
import com.globalmentor.text.xml.oeb.*;
import com.globalmentor.text.xml.xlink.*;
import static com.globalmentor.text.xml.xhtml.XHTML.*;

/**Class to watch the associated component and fire hyperlink events on it
	when appropriate. This class interprets links according to the XLink
	specification, but allows other link controllers, which may recognize links
	based on other criteria, to be registered for other namespaces.
	<p>If an XML element is encountered in a particular namespace and a link
	controller has been registered for that namespace, the registered link
	controller will be used. Otherwise, this link controller will be used to
	detect links.</p>
	<p>To add linking functionality for other namespaces, a child class should
	override {@link #isLinkElement(Element)} and
	{@link #getLinkElementHRef(Element)}.
@author Garret Wilson
@see #isLinkElement(Element)
@see #getLinkElementHRef(Element)
*/
public class XMLLinkController extends MouseAdapter implements MouseMotionListener, Serializable
{

	/**Default constructor.*/
	public XMLLinkController()
	{
	}

	/**The element the cursor is currently over; used to know when to generate
		enter and exit hyperlink events.*/
	private Element currentElement=null;

	/**The URI the element currently represents; used to know when to generate
		enter and exit hyperlink events.*/
	private URI currentURI=null;

	/**This is used by viewToModel to avoid allocing a new array each time.*/
	private Position.Bias[] bias = new Position.Bias[1];

	/**Called when a mouse click occurs. If the component is read-only, then the
		clicked event is used to generate a hyperlink event if the clicked area
		is a link.
	@param mouseEvent The mouse event.
	@see MouseListener#mouseClicked
	*/
	public void mouseClicked(MouseEvent mouseEvent)
	{
		final JEditorPane editorPane=(JEditorPane)mouseEvent.getSource();	//get the source of the event
		if(!editorPane.isEditable() && !mouseEvent.isPopupTrigger())	//if the editor pane is read-only, and this isn't the popup trigger
		{
			final Point point=new Point(mouseEvent.getX(), mouseEvent.getY());	//create a point from the mouse click coordinates
			final int pos=editorPane.viewToModel(point);	//get the position of the mouse click
//TODO del	Debug.notify("Mouse clicked on position: "+pos);	//TODO fix
			if(pos>=0)	//if we found a valid position in the document
			{
				activateLink(pos, editorPane, mouseEvent.getX(), mouseEvent.getY());  //try activate a link at that position
			}
		}
	}

	/**Called when the mouse is dragged. This version ignores mouse dragging.
	@param mouseEvent The mouse event.
	*/
	public void mouseDragged(MouseEvent mouseEvent) {}

	/**Called when the mouse is moved.
	@param mouseEvent The mouse event.
	*/
	public void mouseMoved(final MouseEvent mouseEvent)
	{
//TODO del Log.trace("Inside XMLEditorKit.LinkController.mouseMoved()");	//TODO del
		final JEditorPane editorPane=(JEditorPane)mouseEvent.getSource();	//get the source of the event
		final XMLEditorKit editorKit=(XMLEditorKit)editorPane.getEditorKit();	//get the editor kit for the editor pane
		boolean adjustCursor=true;	//we'll assume we'll adjust the cursor unless we're still over the same element
		Cursor newCursor=editorKit.getDefaultCursor();	//get the default cursor; if we determine we're over a link, we'll change this to the link cursor
		if(!editorPane.isEditable())	//if the editor pane isn't editable (it's being used for browsing)
		{
			final Point point=new Point(mouseEvent.getX(), mouseEvent.getY());	//create a new point object with the position of the mouse
				//TODO testing
			final int pos=editorPane.viewToModel(point);	//get the position in the model of the mouse movement

/*TODO fix; see why this functions slightly differently than editorPane.viewToModel()
			int pos=editorPane.getUI().viewToModel(editorPane, point, bias);	//get the position in the model of the mouse click
			if(bias[0]==Position.Bias.Backward && pos>0)
				pos--;
*/
			if(pos>=0)	//if we found a valid position in the document
			{
				final Document document=editorPane.getDocument();	//get a reference to the model's document
				if(document instanceof com.garretwilson.swing.text.xml.XMLDocument)	//if this is an XML document
				{
					XMLDocument xmlDocument=(com.garretwilson.swing.text.xml.XMLDocument)document;	//cast the document to an XMLDocument
					Element element=xmlDocument.getCharacterElement(pos);	//get the element for this position
					if(currentElement!=element)		//if we've moved to a different element
					{
Log.trace("We've moved to a different element; currentURI: ", currentURI);	//TODO del
						currentElement=element;	//show that we have a new current element
						while(element!=null)  //we'll keep looking up the chain for a link element until we find one or run out of elements
						{
						  final XMLLinkController linkController=getLinkController(element); //get a link controller for this element
							if(linkController.isLinkElement(element))	//if this is a link element
							{
//TODO del when works									final String href=getLinkElementHRef(element);	//get the href for the element we're over (we don't know if this is a link element, so this may return null)
								try
								{
									final URI uri=linkController.getLinkElementURI(xmlDocument, element);	//get the URI for the element we're over (we don't know if this is a link element, so this may return null)
Log.trace("URI: ", uri);	//TODO del
//TODO del when works									if(url!=currentURL)	//if we're over a different link
									if(!Objects.equals(uri, currentURI))	//if we're over a different link (comparing the URIs using the URI.equals() method, if possible)
									{
//TODO del when works										final AttributeSet attributeSet=element.getAttributes();	//get the attributes of this element TODO what if this is null?
										fireEntryExitEvents(editorPane, xmlDocument, currentURI, uri, element);	//fire the appropriate events for exiting and entering a link
										currentURI=uri;	//update which link we're over
										if(uri!=null)	//if we're now over a link
											newCursor=editorKit.getLinkCursor(xmlDocument, uri);	//we'll show the appropriate link cursor
									}
									else  //if we're still over the same link
									{
										adjustCursor=false; //don't adjust the cursor
									}
								}
								catch(URISyntaxException uriSyntaxException)  //if the URI could not be formed
								{
									Log.warn(uriSyntaxException); //continue normally TODO should this be something just under a warning, since this is a user-caused error?
								}
								break;  //stop looking for links up the hierarchy; we just found one
							}
							else  //if this isn't a link element
							{
								element=element.getParentElement(); //check the parent element
							}
						}
						if(element==null) //if we were unable to find a link element
						{
							fireEntryExitEvents(editorPane, xmlDocument, currentURI, null, null);	//fire the appropriate events for exiting and entering a link
							currentURI=null; //show that we don't currently have a URI
						}
					}
					else	//if we're not over a different element
						adjustCursor=false;	//don't adjust the cursor
				}
			}
				//if we should adjust the cursor and we're really changing cursors
			if(adjustCursor && editorPane.getCursor()!=newCursor)
				editorPane.setCursor(newCursor);	//update the cursor
		}
	}

	/**Calls <code>linkActivated</code> on the associated <code>JEditorPane</code>
		if the given position represents a link. This method walks its way up the
		element hierarchy to find any enclosing link element, if the element at
		the given position does not represent a link.
		If this was the result of a mouse click, <code>x</code> and <code>y</code>
		will give the location of the mouse, otherwise they will be (&lt;0).
	@param pos The position in the model.
	@param editorPane Tthe editor pane that contains the document.
	@param x The horizontal position of the mouse, if this is the result of a
		mouse click, or (&lt;0).
	@param y The vertical position of the mouse, if this is the result of a
		mouse click, or (&lt;0).
	@return <code>true</code> if a link was successfully found and activated,
		resulting in a hyperlink event being fired.
 */
	protected boolean activateLink(final int pos, final JEditorPane editorPane, final int x, final int y)
	{
//TODO fix super.activateLink(pos, editorPane, x, y);	//TODO testing
//TODO del Log.trace("Inside OEBEditorKit.activateLink()");
		final Document document=editorPane.getDocument();	//get the document in the editor pane
		if(document instanceof XMLDocument)	//if this is an XML document
		{
//TODO del Log.trace("OEBEditorKit.activeLink() is an OEBDocument");
			XMLDocument xmlDocument=(XMLDocument)document;	//cast the document to an XML document
			Element element=xmlDocument.getCharacterElement(pos);	//get the element this position represents
//TODO del when works				final String elementName=(String)attributeSet.getAttribute(StyleConstants.NameAttribute);	//get the name of this element
//TODO del Log.trace("OEBEditorKit.activeLink() is on element: "+(elementName!=null ? elementName : "null"));
//TODO del Log.trace("element: "+element.toString());	//TODO testing

			while(element!=null)  //we'll keep looking up the chain for a link element until we find one or run out of elements
			{
				final XMLLinkController linkController=getLinkController(element); //get a link controller for this element
				if(linkController.isLinkElement(element))	//if the link controller says this is a link element
				{
//TODO del when not needed						final AttributeSet attributeSet=element.getAttributes();	//get the attributes of this element TODO make sure we really need this
//TODO fix		protected String getLinkElementHRef(final Element element)
//TODO del when not needed						final String href=getLinkElementHRef(element);	//get the href value
//TODO del when not needed						final URL url=getLinkElementURL(xmlDocument, element);	//get the link URL
//TODO del Debug.notify("OEBEditorKit.activeLink() found href: "+href);
						//ask the link controller to create a hyperlink event
					final HyperlinkEvent linkEvent=linkController.createHyperlinkEvent(editorPane, xmlDocument, /*TODO del if not needed url, */element);
	//TODO fix for usemap
					if(linkEvent!=null)	//if a hyperlink event was successfully created TODO doesn't this always succeed?
					{
/*TODO del
							//because it's likely the target won't be this same element and may not even be a link
						currentElement=null;  //show that we're no longer on the old element
						currentURL=null;  //show that we're no longer using the old URL
						fireEntryExitEvents(editorPane, xmlDocument, currentURL, null, element);	//fire the appropriate events for exiting the link
						final XMLEditorKit editorKit=(XMLEditorKit)editorPane.getEditorKit();	//get the editor kit for the editor pane
							//adjust the cursor back to the default
						if(editorPane.getCursor()!=editorKit.getDefaultCursor())  //if the editor pane's cursor isn't the editor kit's default
							editorPane.setCursor(editorKit.getDefaultCursor());	//set the cursor back to the default
*/
						editorPane.fireHyperlinkUpdate(linkEvent);	//let the editor pane fire the hyperlink event
						return true;  //show that we activated a link
					}
					break;  //stop looking for links up the hierarchy; we just found one
				}
				else  //if this isn't a link element
					element=element.getParentElement(); //check the parent element
			}
		}
		return false; //show that we were not successful in finding a link and activating it
	}

	/**Determines whether the specified element represents a link. This version
		makes its determination based upon the rules of XLink. Other versions may
		override this function to do their own determinations.
	@param element The element in question.
	@return <code>true</code> if the specified element represents a link.
	*/
	protected boolean isLinkElement(final Element element)
	{
		final AttributeSet attributeSet=element.getAttributes();	//get the attributes of this element
		final String xlinkType=XMLStyles.getXMLAttributeValue(attributeSet, XLink.XLINK_NAMESPACE_URI.toString(), XLink.ATTRIBUTE_TYPE);	//get the xlink:type of this element (if there is one)
		if(xlinkType!=null)	//if this is an XLink element
			return true;	//show that this is a link element
				//TODO for now, until we support default attributes in the DTD, accept a link that only has an xlink:href
				//TODO do we even need this anymore? del this and make xlink:type mandatory like the specification says
//TODO del		else if XMLStyleUtilities.getXMLAttributeValue(attributeSet, XLinkConstants.XLINK_NAMESPACE_URI.toString(), XLinkConstants.ATTRIBUTE_HREF);	//if there is an xlink:href
//TODO del		else if(attributeSet.getAttribute(XLinkConstants.XLINK_HREF)!=null)	
//TODO del			return true;	//TODO del when default attributes are supported in the DTD
		else	//if this is another element
			return false;	//show that this element doesn't have the xlink:type attribute
	}

	/**Gets the link href of the specified element if the specified element
		represents a link. This version finds the href based upon the rules of
		XLink. Other versions may	override this function to return values from
		other attributes.
	@param element The element which may be a link element.
	@return The href of the link, or <code>null</code> if the element does not
		represent a link or if the element's href is not present.
	*/
	protected String getLinkElementHRef(final Element element)
	{
		final AttributeSet attributeSet=element.getAttributes();	//get the attributes of this element
		final String xlinkType=XMLStyles.getXMLAttributeValue(attributeSet, XLink.XLINK_NAMESPACE_URI.toString(), XLink.ATTRIBUTE_TYPE);	//get the xlink:type of this element (if there is one)
//TODO bring back when the DTD supports default attributes			if(xlinkType!=null)	//if this is an XLink element
				//TODO use getDefinedAttribute(), so as not to search up the hierarchy
			return XMLStyles.getXMLAttributeValue(attributeSet, XLink.XLINK_NAMESPACE_URI.toString(), XLink.ATTRIBUTE_HREF);	//return the xlink:href of this element (if there is one)
//TODO bring back when the DTD supports default attributes			return null;	//show that this isn't an XLink element, because it didn't have an "xlink:type" attribute
	}

	/**Gets the full URI specified element if the specified element
		represents a link. The href is first calculated and then a URI is created
		from the base URI specified by the element or one of its ancestors. If
		a base URI cannot be found in the element hierarchy, the document is
		asked for that value.
	@param xmlDocument The XML document which contains the element.
	@param element The element which may be a link element.
	@return The URI of the link, or <code>null</code> if the element does not
		represent a link or if the element's href is not present.
	@exception URISyntaxException Thrown if the element's href and/or the
		base URI do not allow a valid URI to be constructed.
	@see #getLinkElementHRef(Element)
	@see XMLDocument#getBaseURI()
	@see XMLStyles#getBaseURI(AttributeSet)
	*/
	protected URI getLinkElementURI(final XMLDocument xmlDocument, final Element element) throws URISyntaxException
	{
		final String href=getLinkElementHRef(element);  //get the href of the element
		if(href!=null)  //if there is an href
		{
			URI baseURI=XMLStyles.getBaseURI(element.getAttributes()); //get the base URI of the document
			if(baseURI==null) //if we couldn't found a base URI in the attributes
				baseURI=xmlDocument.getBaseURI();	//get the base URI from the document
						//TODO make sure this works for fragments
			return URIs.createURI(baseURI, href);	//convert the href into a full URI, correctly processing URI fragments beginning with "#"
		}
		return null;  //show that we could not find an href for the element
	}


/**
 * Calls linkActivated on the associated JEditorPane
 * if the given position represents a link.<p>This is implemented
 * to forward to the method with the same name, but with the following
 * args both == -1.
       *
       * @param pos the position
       * @param html the editor pane
 */
/*TODO fix
protected void activateLink(int pos, JEditorPane editor) {
		activateLink(pos, editor, -1, -1);
}
*/

/**
 * Calls linkActivated on the associated JEditorPane
 * if the given position represents a link. If this was the result
 * of a mouse click, <code>x</code> and
 * <code>y</code> will give the location of the mouse, otherwise
 * they will be < 0.
       *
       * @param pos the position
       * @param html the editor pane
 */
/*TODO del when not needed
	protected void activateLink(int pos, JEditorPane editorPane, int x, int y)
	{
//TODO del Log.trace("Inside XMLEditorKit.activateLink()");
		return;	//TODO fix for XLink
	}
*/
/*TODO fix
		final Document document=editorPane.getDocument();	//get the document in the editor pane
		if(document instanceof XMLDocument)	//if this is an XML document
		{
			XMLDocument xmlDocument=(XMLDocument)document;	//cast the document to an XML document
			Element element=XMLDocument.getCharacterElement(pos);	//get the element this position represents
			AttributeSet attributeSet=element.getAttributes();	//get the attributes of this element

	AttributeSet anchor = (AttributeSet) a.getAttribute(HTML.Tag.A);
	String href = (anchor != null) ?
			(String) anchor.getAttribute(HTML.Attribute.HREF) : null;
	HyperlinkEvent linkEvent = null;

	if (href != null) {
			linkEvent = createHyperlinkEvent(html, hdoc, href,
							 anchor);
	}
	else if (x >= 0 && y >= 0) {
			// Check for usemap.
			Object useMap = a.getAttribute(HTML.Attribute.USEMAP);
			if (useMap != null && (useMap instanceof String)) {
		Map m = hdoc.getMap((String)useMap);
		if (m != null) {
				Rectangle bounds;
				try {
			bounds = html.modelToView(pos);
			Rectangle rBounds = html.modelToView(pos + 1);
			if (bounds != null && rBounds != null) {
					bounds.union(rBounds);
			}
				} catch (BadLocationException ble) {
			bounds = null;
				}
				if (bounds != null) {
			AttributeSet area = m.getArea
								 (x - bounds.x, y - bounds.y,
						bounds.width, bounds.height);
			if (area != null) {
					href = (String)area.getAttribute
									 (HTML.Attribute.HREF);
					if (href != null) {
				linkEvent = createHyperlinkEvent(html,
								hdoc, href, anchor);

					}
			}
				}
		}
			}
	}
	if (linkEvent != null) {
			html.fireHyperlinkUpdate(linkEvent);
	}
		}
*/




	/**Creates and returns a new instance of a hyperlink event.
	@param editorPane The editor pane for which the event belongs and will be fired.
	@param xmlDocument The document in which the link lies.
//TODO fix	@param eventType
//TODO del if not needed		@param href The hypertext reference.
	@param element The link element.
	@return The new hyperlink event.
	*/
	protected HyperlinkEvent createHyperlinkEvent(final JEditorPane editorPane, final XMLDocument xmlDocument, /*TODO fix final EventType eventType,*/ /*TODO del if not needed String href, */final Element element) //TODO fix so that fireEntryExitEvents() can call this
	{
//TODO del Log.trace("Inside XMLEditorKit.createHyperlinkEvent() with href of: "+href);	//TODO del
		URI uri;	//we'll try get a full URI from the href
		String description; //we'll store a description of the URI here, or the message we get if we fail to form a URI
		try
		{
			uri=getLinkElementURI(xmlDocument, element);  //get the link URI from the element
			description=uri.toString();  //use the URI as the description //TODO fix; this can throw a null pointer exception
		}
		catch(URISyntaxException uriSyntaxException)	//if there are any problems creating a URI
		{
			uri=null;	//don't use the URI
			description=getLinkElementHRef(element);  //store the href that resulted in the invalid URI
		}
//TODO change this later to be an XLinkEvent
			//create a hyperlink event with the specified URI and href
		final HyperlinkEvent linkEvent=new XMLLinkEvent(editorPane, HyperlinkEvent.EventType.ACTIVATED, uri, description);
		return linkEvent;	//return the link we created
	}

	/**Fires the appropriate events for entering and exiting a link. If there
		is record of being over an event before, an exit event is fired. If
		<code>href</code> is not <code>null</code>, an entry event will be fired.
	@param editorPane The editor pane for which the event belongs and will be fired.
	@param xmlDocument The document in which the links lie.
	@param currentURI The URI the element currently represents; used to know
		when to generate exit hyperlink events.
	@param newURI The new link URI, or <code>null</code> if no entry
		event should be fired.
	@param element The link element.
	*/
	protected void fireEntryExitEvents(final JEditorPane editorPane, final XMLDocument xmlDocument, final URI currentURI, final URI newURI, final Element element)
	{
//TODO del Log.trace("XMLEditorKit.fireEntryExitEvents() with href of: "+href);	//TODO del
		if(currentURI!=null)	//if we were over a link before
		{
				//create a hyperlink event to represent exiting a hyperlink TODO eventually use the common hyperlink event factory
			final HyperlinkEvent exitEvent=new XMLLinkEvent(editorPane, HyperlinkEvent.EventType.EXITED, currentURI, currentURI.toString());
//TODO del Log.trace("ready to fire exit event: "+exitEvent);  //TODO del
			editorPane.fireHyperlinkUpdate(exitEvent);	//fire the exit event
		}
		if(newURI!=null)	//if we're over a new link
		{
//TODO fix		final HyperlinkEvent linkEvent=new HyperlinkEvent(editorPane, HyperlinkEvent.EventType.ACTIVATED, uri, description);

				//create a hyperlink event representing entering a hyperlink TODO eventually use the common hyperlink event factory
			final HyperlinkEvent enteredEvent=new XMLLinkEvent(editorPane, HyperlinkEvent.EventType.ENTERED, newURI, newURI.toString());
			editorPane.fireHyperlinkUpdate(enteredEvent);	//fire the enter event
		}
	}

	/**Retrieves a link controller for the given element by looking up any
		registered link controllers for the element's namespace.
	@param element The element for which a link controller should be returned.
	@return A link controller for the given element.
	*/
	protected XMLLinkController getLinkController(final Element element)
	{
		XMLLinkController linkController=null; //we'll determine a link controller (which may be this class itself, if nothing else is registered)
		final AttributeSet attributeSet=element.getAttributes();  //get the element's attribute set
		if(attributeSet!=null)  //if we have an attribute set
		{
				//final a registered XML link controller to which to delegate if we can
			/*TODO fix final */String elementNamespaceURI=XMLStyles.getXMLElementNamespaceURI(attributeSet); //get the namespace of this element, if it has one
//TODO del Log.trace("Looking for view factory for namespace: ", elementNamespaceURI); //TODO del
			if(elementNamespaceURI==null) //if this element has no namespace TODO this code is duplicated from XMLViewFactory---combine somehow
			{
				final ContentType mediaType=XMLStyles.getMediaType(attributeSet); //see if this element's document has a media type defined
				if(mediaType!=null) //if there is a media type defined for this element's document
				{ //TODO probably do all this differently later, like registering a view factory with a media type or something or, better yet, registering a namespace with a media type
					if(ContentType.TEXT_PRIMARY_TYPE.equals(mediaType.getPrimaryType()) && OEB.X_OEB1_DOCUMENT_SUBTYPE.equals(mediaType.getSubType()))
						elementNamespaceURI=OEB.OEB1_DOCUMENT_NAMESPACE_URI.toString(); //TODO testing
					else if(isHTML(mediaType))
						elementNamespaceURI=XHTML_NAMESPACE_URI.toString(); //TODO testing
				}
			}
//TODO del Log.trace("Decided namespace is really: ", elementNamespaceURI); //TODO del
			linkController=getLinkController(elementNamespaceURI); //see if a link controller has been registered for this namespace (which may be null)
		}
		if(linkController==null)  //if we couldn't find a link controller
			linkController=this;  //use ourselves as a link controller
		return linkController;    //return whatever link controller we found
	}

	/**Retreives a link controller for the given namespace.
		This version defaults to returning this link controller. A child class can
		override this method to return a link controller specifically for a given
		namespace.
	@param namespaceURI The namespace URI of the element for which a link
		controller should be returned, or <code>null</code> if the element has no
		namespace.
	@return A view factory for the given namespace.
	*/
	protected XMLLinkController getLinkController(final String namespaceURI)
	{
		return this;  //default to just using this link controller
	}

}

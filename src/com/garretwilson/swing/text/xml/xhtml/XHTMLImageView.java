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

package com.garretwilson.swing.text.xml.xhtml;

import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;

import javax.swing.text.*;
import com.garretwilson.awt.*;
import com.garretwilson.swing.text.xml.*;

import static com.globalmentor.text.xml.xhtml.XHTML.*;

import com.globalmentor.log.Log;

/**A view that displays an image, intended to support the XHTML
<code>&lt;img&gt;</code> element or the <code>&lt;object&gt;</code> element
representing an image.
This class is a extension, rewrite, and bug fix of javax.swing.text.html.ImageView
by Jens Alfke version 1.40 02/02/00 and is based on code from that class.
@author Garret Wilson
@see javax.swing.text.html.ImageView
*/
public class XHTMLImageView extends XMLImageView
{

	/**The element from which we are initialized.*/
	protected final Element initElement;  //TODO probably promote this to XMLObjectView

	/**Creates a new view that represents an XHTML image element.
	@param element The element for which to create the view.
	*/
  public XHTMLImageView(final Element element)
	{
		this(element, element); //do the default constructing, initializing the object from the same element we represent
	}

	/**Creates a new view that represents an XHTML image element. The image is
		initialized using a different element (which may, for example, be a
		descendant element of the original element). This allows us to represent
		an <code>&lt;object&gt;</code> element but initialize ourselves from one
		of its children, as we may recognize the media type of the child element,
		but not of the element itself.
	@param element The element for which to create the view.
	@param initializeElement The element to be used for initializing the image
		information.
	*/
  public XHTMLImageView(final Element element, final Element initializeElement)
	{
   	super(element);	//do the default constructing
		initElement=initializeElement;  //save the element from which we're being initialized
   	initialize(initializeElement);	//do the necessary image value setting TODO perhaps promote this to XMLImageView or higher
			//TODO we should probably save the initialization attribute set and return it for getAttributes()
//TODO fix	StyleSheet sheet = getStyleSheet();
//TODO fix	attr = sheet.getViewAttributes(this);
Log.trace("Finished constructing XHTMLImageView");	//TODO del
	}

	/**Initializes the information needed to render the image.
	@param element The element which contains the image information.
	*/
	protected void initialize(Element element)
	{
Log.trace("XHTMLImageView.initialize()");
		final AttributeSet attributeSet=element.getAttributes();  //get the element's attributes
		final String elementName=XMLStyles.getXMLElementName(attributeSet); //get the name of this element
		final String href=XHTMLSwingText.getImageHRef(element.getAttributes()); //get a reference to the image file represented by the element
			setHRef(href);	//set the href to the value we found
Log.trace("XHTMLImageView.initialize() src: ", getHRef());
				//TODO in the future, get this from the XML document (which will, of course, use OEBDocument)
//TODO fix			fImage=(Image)document.getResource(src);	//get the image resource TODO check to make sure what is returned is really an image
		int height=-1; //assume for now that the image dimensions are not defined in the attributes
		int width=-1;  //
		try //try to get the width and the height from the attributes; if we can, we won't have to load the image, now
		{
			//get the height if it is defined TODO check about namespaces
			final String heightString=XMLStyles.getXMLAttributeValue(attributeSet, null, ELEMENT_IMG_ATTRIBUTE_HEIGHT);
			if(heightString!=null)  //if there is a height defined
				height=Integer.parseInt(heightString);  //turn the height of the image into an integer
			//get the width if it is defined TODO check about namespaces
			final String widthString=XMLStyles.getXMLAttributeValue(attributeSet, null, ELEMENT_IMG_ATTRIBUTE_WIDTH);
			if(widthString!=null)  //if there is a height defined
				width=Integer.parseInt(widthString);  //turn the width of the image into an integer
		}
		catch(NumberFormatException e) {} //ignore any number format exceptions; the width and/or the height will be left at -1 for us to check
		if(height==-1 || width==-1) //if we were unable to find either the width or the height, load the image and get the dimensions directly
		{
			try
			{
				final Image image=getImage(); //get the image, loading it if needed (in initialize() it will usually have to be loaded)
				assert image!=null : "fImage is null";  //TODO fix
  			ImageUtilities.loadImage(image);  //load the image TODO optimize: perhaps there's a way to just load part of the image, to get the image dimensions
				height=image.getHeight(this);	//get the image's height
				width=image.getWidth(this);	//get the image's width
				freeImage();  //free the image memory; this should speed up view flowing
			}
			catch(URISyntaxException e)	//TODO do something better here
			{
				Log.error(e);
			}
			catch(IOException e)	//TODO do something better here
			{
				Log.error(e);
			}
		}
		setHeight(height);	//set the height of the image view to whatever we found
		setWidth(width);	//set the width of the image view to whatever we found
	}

	/**Fetches the attributes to use when rendering.
		This version returns the attributes used for initialization, which may or
		may not be the attribute set the view represents. This allows the view to
		represent an <code>&lt;object&gt;</code> element but be initialized by one
		of the object's children of which we recognize the media type.
	@return The attribute set we represent, which may or may not be the attribute
		set of the element we represent.
	*/
	public AttributeSet getAttributes()
	{
		return initElement.getAttributes(); //return the attribute set of the initialization element
	}

}

package com.garretwilson.swing.text.xml.xhtml;

import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;

import javax.swing.text.*;
import com.garretwilson.awt.*;
import com.garretwilson.swing.text.xml.*;
import com.garretwilson.text.xml.xhtml.XHTMLConstants;
import com.garretwilson.util.*;

/**A view that displays an image, intended to support the XHTML
<code>&lt;img&gt;</code> element or the <code>&lt;object&gt;</code> element
representing an image.
This class is a extension, rewrite, and bug fix of javax.swing.text.html.ImageView
by Jens Alfke version 1.40 02/02/00 and is based on code from that class.
@author Garret Wilson
@see javax.swing.text.html.ImageView
*/
public class XHTMLImageView extends XMLImageView implements XHTMLConstants
{

	/**The element from which we are initialized.*/
	protected final Element initElement;  //G***probably promote this to XMLObjectView

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
   	initialize(initializeElement);	//do the necessary image value setting G***perhaps promote this to XMLImageView or higher
			//G***we should probably save the initialization attribute set and return it for getAttributes()
//G***fix	StyleSheet sheet = getStyleSheet();
//G***fix	attr = sheet.getViewAttributes(this);
Debug.trace("Finished constructing XHTMLImageView");	//G***del
	}

	/**Initializes the information needed to render the image.
	@param element The element which contains the image information.
	*/
	protected void initialize(Element element)
	{
Debug.trace("XHTMLImageView.initialize()");
		final AttributeSet attributeSet=element.getAttributes();  //get the element's attributes
		final String elementName=XMLStyleUtilities.getXMLElementName(attributeSet); //get the name of this element
		final String href=XHTMLSwingTextUtilities.getImageHRef(element.getAttributes()); //get a reference to the image file represented by the element
			setHRef(href);	//set the href to the value we found
//G***del when works		final String src=(String)element.getAttributes().getAttribute("src");	//G***check about resolving parents (we don't want to resolve), use a constant, use namespaces, and comment
//G***del when works		setHRef((String)attributeSet.getAttribute("src"));	//G***check about resolving parents (we don't want to resolve), use a constant, use namespaces, and comment
Debug.trace("XHTMLImageView.initialize() src: ", getHRef());
//G***del when works		final String src="D:/Projects/oeb/understandingoeb/oebobjects_classdiagram.jpg";	//G***fix
				//G***in the future, get this from the XML document (which will, of course, use OEBDocument)
//G***del when works		final OEBDocument document=(OEBDocument)getDocument();
//G***fix			fImage=(Image)document.getResource(src);	//get the image resource G***check to make sure what is returned is really an image

//G***del when works			fImageIcon=(ImageIcon)document.getResource(src);	//get the image resource G***check to make sure what is returned is really an image
//G***del when works Debug.assert(fImageIcon!=null, "fImageIcon is null");
		int height=-1; //assume for now that the image dimensions are not defined in the attributes
		int width=-1;  //
		try //try to get the width and the height from the attributes; if we can, we won't have to load the image, now
		{
			//get the height if it is defined G***check about namespaces
			final String heightString=XMLStyleUtilities.getXMLAttributeValue(attributeSet, null, ELEMENT_IMG_ATTRIBUTE_HEIGHT);
			if(heightString!=null)  //if there is a height defined
				height=Integer.parseInt(heightString);  //turn the height of the image into an integer
			//get the width if it is defined G***check about namespaces
			final String widthString=XMLStyleUtilities.getXMLAttributeValue(attributeSet, null, ELEMENT_IMG_ATTRIBUTE_WIDTH);
			if(widthString!=null)  //if there is a height defined
				width=Integer.parseInt(widthString);  //turn the width of the image into an integer
		}
		catch(NumberFormatException e) {} //ignore any number format exceptions; the width and/or the height will be left at -1 for us to check
		if(height==-1 || width==-1) //if we were unable to find either the width or the height, load the image and get the dimensions directly
		{
			try
			{
				final Image image=getImage(); //get the image, loading it if needed (in initialize() it will usually have to be loaded)
	Debug.assert(image!=null, "fImage is null");  //G***fix
  			ImageUtilities.loadImage(image);  //load the image G***optimize: perhaps there's a way to just load part of the image, to get the image dimensions
				height=image.getHeight(this);	//get the image's height
				width=image.getWidth(this);	//get the image's width
				freeImage();  //free the image memory; this should speed up view flowing
			}
			catch(URISyntaxException e)	//G***do something better here
			{
				Debug.error(e);
			}
			catch(IOException e)	//G***do something better here
			{
				Debug.error(e);
			}
		}
		setHeight(height);	//set the height of the image view to whatever we found
		setWidth(width);	//set the width of the image view to whatever we found
/*G***del
Debug.trace("creating image view for {0}"+
		"parent: {4}\n parent parent: {5}"+
		"\n parent parent parent: {6}"+
		"\n parent parent parent parent: {7}"+
		"\n parent parent parent parent parent: {8}\n container: {9}", new Object[]
			{
				getHRef(),
				(getParent()!=null ? getParent().getClass().getName() : "null"),
				(getParent()!=null && getParent().getParent()!=null ? getParent().getParent().getClass().getName() : "null"),
				(getParent()!=null && getParent().getParent()!=null && getParent().getParent().getParent()!=null ? getParent().getParent().getParent().getClass().getName() : "null"),
				(getParent()!=null && getParent().getParent()!=null && getParent().getParent().getParent()!=null && getParent().getParent().getParent().getParent()!=null ? getParent().getParent().getParent().getParent().getClass().getName() : "null"),
				(getParent()!=null && getParent().getParent()!=null && getParent().getParent().getParent()!=null && getParent().getParent().getParent().getParent()!=null && getParent().getParent().getParent().getParent().getParent()!=null ? getParent().getParent().getParent().getParent().getParent().getClass().getName() : "null"),
				getContainer()
			}
		);
*/
//G***del Debug.trace("XHTMLImageView.initialize(), width: "+fWidth+", height: "+fHeight);	//G***del
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

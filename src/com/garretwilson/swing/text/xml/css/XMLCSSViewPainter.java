package com.garretwilson.swing.text.xml.css;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import javax.swing.text.*;
import com.garretwilson.swing.text.FragmentView;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSConstants;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSUtilities;
import com.garretwilson.util.Debug;

/**Class to paint XML CSS views.
@author Garret Wilson
*/
public class XMLCSSViewPainter implements XMLCSSConstants
{

	/**Paints an XML view using CSS properties.
	@param graphics The rendering surface to use.
	@param allocation The allocated region to render into.
	@param view The view being rendered.
	@see View#paint
	*/
	public static void paint(final Graphics graphics, final Shape allocation, final View view, final AttributeSet attributeSet)
	{
		final Graphics2D graphics2D=(Graphics2D)graphics;  //cast to the 2D version of graphics
		  //turn antialiasing on G***probably do this conditionally, based on some sort of flag
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//get the allocation as a rectangle
		final Rectangle allocRect=(allocation instanceof Rectangle) ? (Rectangle)allocation : allocation.getBounds();
		final boolean isFragmentView=view instanceof FragmentView;  //see if this view is a fragement of a larger, broken view
			//if this is a view fragment, see if it's the first fragment of the larger view
		final boolean isFirstFragment=isFragmentView ? ((FragmentView)view).isFirstFragment() : true;
			//paint the background
//G***del unless gets too slow		final Color backgroundColor=cssView.getBackgroundColor();	//get the background color for the view
		final Color backgroundColor=XMLCSSStyleUtilities.getBackgroundColor(attributeSet);	//get the background color from the attributes
		final Color foregroundColor=XMLCSSStyleUtilities.getForeground(attributeSet);	//get the foreground color from the attributes
		graphics.setColor(foregroundColor); //change to the foreground color for drawing G***do we want to save the old value?
		  //G***testing; move
		if(backgroundColor!=null)	//if we have a background color
		{
Debug.trace("Background for "+XMLCSSStyleUtilities.getXMLElementName(attributeSet)+": "+backgroundColor); //G***del
			final Color originalColor=graphics.getColor();	//get the original graphics color
			graphics.setColor(backgroundColor);	//switch to the background color
			graphics.fillRect(allocRect.x, allocRect.y, allocRect.width, allocRect.height);	//fill the rectangle with the correct color
			graphics.setColor(originalColor);	//set the color back to what it originally was
		}
			//G***right now, we paint the marker to the left of the element, which will cause
			//  it to not be repainted at times; somehow invalidate the region if so
		final String display=XMLCSSStyleUtilities.getDisplay(attributeSet); //get the CSS display property value
		  //if this is a list item, and it's the first fragment (if the list item has been fragmented)
		if(CSS_DISPLAY_LIST_ITEM.equals(display) && isFirstFragment)
		{

Debug.trace("View painter view's class: ", view.getClass().getName());  //G***del
			final View parentView=view.getParent(); //get the parent view of this one
			if(parentView!=null)  //list items must have parents to be rendered
			{
Debug.trace("View painter parent view's class: ", view.getClass().getName());  //G***del

						final Document document=view.getDocument(); //get the view's document
						if(document instanceof StyledDocument)		//if the document is a styled document
						{
							final StyledDocument styledDocument=(StyledDocument)document;	//cast the document to a styled document
							final Font font=styledDocument.getFont(attributeSet);	//let the document get the font from the attributes

				final AttributeSet parentAttributeSet=parentView.getAttributes(); //get the parent's attributes
				final float parentLeftMargin=XMLCSSStyleUtilities.getMarginLeft(parentAttributeSet, font);  //get the parent's left margin G***i18n
				final float markerX=allocRect.x-parentLeftMargin; //find out where the marker should be located horizontally
				View relativeSizeView=view; //we'll get the deepest view we can in order to judge the size of the marker
				while(relativeSizeView.getViewCount()>0)  //while there are child views
				{
					relativeSizeView=relativeSizeView.getView(0);  //get the first child view
				}
				final float relativeHeight=relativeSizeView.getPreferredSpan(View.Y_AXIS);  //find the height of the view we're using as a guide
				final String listStyleType=XMLCSSStyleUtilities.getListStyleType(attributeSet);  //get the type of list style
				if(CSS_LIST_STYLE_TYPE_DISC.equals(listStyleType))  //if the marker should be a disc
				{
					final float markerHeight=relativeHeight/3;  //make the marker partially as high as the deepest child G***make this a constant from somewhere
					final float markerY=allocRect.y+(relativeHeight-markerHeight)/2;  //center the marker vertically
					graphics.fillOval(Math.round(markerX), Math.round(markerY), Math.round(markerHeight), Math.round(markerHeight));		  //G***fix; testing; maybe use fractional coordinates with Graphics2D
				}
				else  //if we shouldn't render a shape, find the model index of this list item
				{
					final Element element=view.getElement();  //get the element the view represents
					final Element parentElement=element.getParentElement(); //get the parent element of this one
					final int siblingCount=parentElement.getElementCount(); //find out how many siblings there are, including the element the view represents
					int listItemIndex=0;  //we'll assign a value here after we discover which index we are
					for(int i=0; i<siblingCount; ++i) //look at each sibling
					{
						final Element siblingElement=parentElement.getElement(i); //get a reference to this sibling
						if(siblingElement!=element)  //if we still haven't found ourselves
						{
							final String siblingDisplay=XMLCSSStyleUtilities.getDisplay(siblingElement.getAttributes()); //get the CSS display property value of the sibling
							if(CSS_DISPLAY_LIST_ITEM.equals(siblingDisplay)) //if this is a list item
								++listItemIndex;  //we've found another list item that isn't us
						}
						else  //if we've found ourselves
							break;  //we now know our list index
					}
					//G***we assume we found ourselves -- is there any instance in which we wouldn't, and how would we know?
				  final String markerString=XMLCSSUtilities.getMarkerString(listStyleType, listItemIndex);  //get a string representing the marker for us to render
				  if(markerString!=null)  //if we found a valid marker string
					{
/*G***del; moved
						final Document document=view.getDocument(); //get the view's document
						if(document instanceof StyledDocument)		//if the document is a styled document
						{
							final StyledDocument styledDocument=(StyledDocument)document;	//cast the document to a styled document
							final Font font=styledDocument.getFont(attributeSet);	//let the document get the font from the attributes
*/
						  graphics.setFont(font); //switch to the same font as the one being used by the list item
//G***del; moved						}
						final FontRenderContext fontRenderContext=graphics2D.getFontRenderContext();  //get the font rendering context
							//G***probably make sure that it is antialiased, here
						final LineMetrics markerLineMetrics=graphics2D.getFont().getLineMetrics(markerString, fontRenderContext); //get the line metrics of the marker string
						final double markerHeight=markerLineMetrics.getAscent()/*G***fix markerLineMetrics.getHeight()*/;  //find out how high the text is G***fix; there's something not right here, especially with the multiple-line leading calculation of the text itself
						final double markerY=allocRect.y+markerHeight;  //align the text with the top of the list item
//G***bring back or del						final double markerY=allocRect.y+(relativeHeight-markerHeight)/2+markerHeight;  //center the marker vertically
							//G***set the font here
						graphics.drawString(markerString, Math.round(markerX), (int)Math.round(markerY));	//G***testing; i18n
					}
				}
						} //G***fix

			}
		}
	}

	/**Returns a string to display on a marker for the given list item.
	@param listStypeType The type of list marker to use, one of the
		<code>XMLCSSConstants.CSS_LIST_STYLE_TYPE_</code> constants.
	@param listItemIndex The index of the list item for which text should be generated
	@return The string to be rendered for the given list item, or
		<code>null</code> if a string rendering is not appropriate for the given
		list style type.
	*/
/*G***del when works; transferred to XMLCSSUtilities
	protected static String getMarkerString(final String listStyleType, final int listItemIndex)  //G***maybe make sure none of these wrap around
	{
		if(CSS_LIST_STYLE_TYPE_DECIMAL.equals(listStyleType)) //decimal
			return String.valueOf(1+listItemIndex); //return the ordinal position as a decimal number
//G***fix	public final static String CSS_LIST_STYLE_TYPE_DECIMAL_LEADING_ZERO="decimal-leading-zero";
		else if(CSS_LIST_STYLE_TYPE_LOWER_ROMAN.equals(listStyleType)) //lower-roman
		{
			switch(listItemIndex) //see which index this is G***fix with a better algorithm
			{
				case 0: return "i"; //G***fix
				case 1: return "ii"; //G***fix
				case 2: return "iii"; //G***fix
				case 3: return "iv"; //G***fix
				case 4: return "v"; //G***fix
				case 5: return "vi"; //G***fix
				case 6: return "vii"; //G***fix
				case 7: return "viii"; //G***fix
				case 8: return "ix"; //G***fix
				case 9: return "x"; //G***fix
				default: return ""; //G***fix
			}
		}
//G***fix	public final static String CSS_LIST_STYLE_TYPE_UPPER_ROMAN="upper-roman";
//G***fix	public final static String CSS_LIST_STYLE_TYPE_LOWER_GREEK="lower-greek";
		else if(CSS_LIST_STYLE_TYPE_LOWER_GREEK.equals(listStyleType)) //lower-greek
		  return String.valueOf((char)('\u03b1'+listItemIndex)); //return the correct lowercase greek character as a string
		else if(CSS_LIST_STYLE_TYPE_LOWER_ALPHA.equals(listStyleType) //lower-alpha
			  || CSS_LIST_STYLE_TYPE_LOWER_LATIN.equals(listStyleType)) //lower-latin
		  return String.valueOf((char)('a'+listItemIndex)); //return the correct lowercase character as a string
		else if(CSS_LIST_STYLE_TYPE_UPPER_ALPHA.equals(listStyleType) //upper-alpha
			  || CSS_LIST_STYLE_TYPE_UPPER_LATIN.equals(listStyleType)) //upper-latin
		  return String.valueOf((char)('A'+listItemIndex)); //return the correct lowercase character as a string
//G***fix	public final static String CSS_LIST_STYLE_TYPE_HEBREW="hebrew";
//G***fix	public final static String CSS_LIST_STYLE_TYPE_ARMENIAN="armenian";
//G***fix	public final static String CSS_LIST_STYLE_TYPE_GEORGIAN="georgian";
//G***fix	public final static String CSS_LIST_STYLE_TYPE_CJK_IDEOGRAPHIC="cjk-ideographic";
//G***fix	public final static String CSS_LIST_STYLE_TYPE_HIRAGANA="hiragana";
//G***fix	public final static String CSS_LIST_STYLE_TYPE_KATAKANA="katakana";
//G***fix	public final static String CSS_LIST_STYLE_TYPE_HIRAGANA_IROHA="hiragana-iroha";
//G***fix	public final static String CSS_LIST_STYLE_TYPE_KATAKANA_IROHA="katakana-iroha";
//G***fix	public final static String CSS_LIST_STYLE_TYPE_NONE="none";
		return null;  //show that we couldn't find an appropriate market string
	}
*/

}
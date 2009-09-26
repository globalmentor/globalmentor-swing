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

package com.garretwilson.swing.text.xml.css;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import javax.swing.text.*;

import com.garretwilson.swing.text.DocumentUtilities;
import com.garretwilson.swing.text.FragmentView;
import com.garretwilson.swing.text.Views;
import com.garretwilson.swing.text.xml.XMLListView;

import static com.globalmentor.text.xml.stylesheets.css.XMLCSS.*;
import com.globalmentor.text.xml.stylesheets.css.XMLCSS;

/**Class to paint XML CSS views.
@author Garret Wilson
*/
public class XMLCSSViewPainter
{

	/**The default list style types for different nestings of ordered lists.*/
	public final static String[] NESTED_ORDERED_LIST_STYLE_TYPES=new String[]{CSS_LIST_STYLE_TYPE_DECIMAL, CSS_LIST_STYLE_TYPE_UPPER_ALPHA, CSS_LIST_STYLE_TYPE_UPPER_ROMAN, CSS_LIST_STYLE_TYPE_LOWER_ALPHA}; 
	/**The default list style types for different nestings of unordered lists.*/
	public final static String[] NESTED_UNORDERED_LIST_STYLE_TYPES=new String[]{CSS_LIST_STYLE_TYPE_DISC}; 
//TODO fix when other circle and square are supported	public final static String[] NESTED_UNORDERED_LIST_STYLE_TYPES=new String[]{CSS_LIST_STYLE_TYPE_DISC, CSS_LIST_STYLE_TYPE_CIRCLE, CSS_LIST_STYLE_TYPE_SQUARE}; 

	/**Paints an XML view using CSS properties.
	@param graphics The rendering surface to use.
	@param allocation The allocated region to render into.
	@param view The view being rendered.
	@see View#paint
	*/
	public static void paint(final Graphics graphics, final Shape allocation, final View view, final AttributeSet attributeSet)
	{
		final boolean antialias=DocumentUtilities.isAntialias(view.getDocument());	//see if the document specifies antialiasing
//TODO del when works		final boolean isAntialias=XMLStyleUtilities.isAntialias(attributeSet);	//see if we should turn antialias on or off
		final Graphics2D graphics2D=(Graphics2D)graphics;  //cast to the 2D version of graphics
	  //turn on fractional metrics TODO probably do this conditionally, based on some sort of flag
		graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		  //turn antialiasing on
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialias? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
//TODO del when works		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//get the allocation as a rectangle
		final Rectangle allocRect=(allocation instanceof Rectangle) ? (Rectangle)allocation : allocation.getBounds();
		final boolean isFragmentView=view instanceof FragmentView;  //see if this view is a fragement of a larger, broken view
			//if this is a view fragment, see if it's the first fragment of the larger view
		final boolean isFirstFragment=isFragmentView ? ((FragmentView)view).isFirstFragment() : true;
			//paint the background
//TODO del unless gets too slow		final Color backgroundColor=cssView.getBackgroundColor();	//get the background color for the view
		final Color backgroundColor=XMLCSSStyles.getBackgroundColor(attributeSet);	//get the background color from the attributes
		final Color foregroundColor=XMLCSSStyles.getForeground(attributeSet);	//get the foreground color from the attributes
		graphics.setColor(foregroundColor); //change to the foreground color for drawing TODO do we want to save the old value?
		  //TODO testing; move
		if(backgroundColor!=null)	//if we have a background color
		{
//TODO del Log.trace("Background for "+XMLCSSStyleUtilities.getXMLElementName(attributeSet)+": "+backgroundColor); //TODO del
			final Color originalColor=graphics.getColor();	//get the original graphics color
			graphics.setColor(backgroundColor);	//switch to the background color
			graphics.fillRect(allocRect.x, allocRect.y, allocRect.width, allocRect.height);	//fill the rectangle with the correct color
			graphics.setColor(originalColor);	//set the color back to what it originally was
		}
			//TODO right now, we paint the marker to the left of the element, which will cause
			//  it to not be repainted at times; somehow invalidate the region if so
		final String display=XMLCSSStyles.getDisplay(attributeSet); //get the CSS display property value
		  //if this is a list item, and it's the first fragment (if the list item has been fragmented)
		if(CSS_DISPLAY_LIST_ITEM.equals(display) && isFirstFragment)
		{

//Log.trace("View painter view's class: ", view.getClass().getName());  //TODO del
			final View parentView=view.getParent(); //get the parent view of this one
			if(parentView!=null)  //list items must have parents to be rendered
			{
//Log.trace("View painter parent view's class: ", view.getClass().getName());  //TODO del

				final Document document=view.getDocument(); //get the view's document
				if(document instanceof StyledDocument)		//if the document is a styled document
				{
					final StyledDocument styledDocument=(StyledDocument)document;	//cast the document to a styled document
					final Font font=styledDocument.getFont(attributeSet);	//let the document get the font from the attributes
					final AttributeSet parentAttributeSet=parentView.getAttributes(); //get the parent's attributes
					final float parentLeftMargin=XMLCSSStyles.getMarginLeft(parentAttributeSet, font);  //get the parent's left margin TODO i18n
					final float markerX=allocRect.x-parentLeftMargin; //find out where the marker should be located horizontally
					View relativeSizeView=view; //we'll get the deepest view we can in order to judge the size of the marker
					while(relativeSizeView.getViewCount()>0)  //while there are child views
					{
						relativeSizeView=relativeSizeView.getView(0);  //get the first child view
					}
					final float relativeHeight=relativeSizeView.getPreferredSpan(View.Y_AXIS);  //find the height of the view we're using as a guide
					String listStyleType=XMLCSSStyles.getListStyleType(attributeSet);  //get the type of list style
						//TODO fix; this implementation will inherit list styles from other enclosing lists
					if(listStyleType==null)	//if no list style type is indicated, determine the list style type based upon the enclosing list views
					{
						Boolean isListOrdered=null;	//we'll find out whether the list is ordered
						int depthIndex=-1;	//we'll find out how many lists are nested (indicating nesting with a zero-based index)
						View logicalParent=Views.getLogicalParent(view);	//start with the logical parent (the real parent could be a fragment, in which case we wouldn't know if it was a list or not)
						while(logicalParent!=null)	//while there is still a parent to check
						{
							if(logicalParent instanceof XMLListView)	//if this parent is a list view
							{
								final XMLListView listView=(XMLListView)logicalParent;	//cast the parent to a list view
								if(isListOrdered==null)	//if we don't yet know whether the list should be ordered
								{
									isListOrdered=Boolean.valueOf(listView.isOrdered());	//we'll go with whether the closest enclosing list is ordered
								}
								if(isListOrdered.booleanValue()==listView.isOrdered())	//if this list matches the ordered or unordered status
								{
									++depthIndex;	//show that this list is counted as an enclosing list
								}
							}
							logicalParent=Views.getLogicalParent(logicalParent);	//look at the logical parent's logical parent, looking behind fragments to the original views
						}

						if(depthIndex>=0)	//if we have at least one enclosing list
						{
							assert isListOrdered!=null : "Found enclosing list but didn't record its ordered condition.";
							if(isListOrdered.booleanValue())	//if the list is ordered
							{
								listStyleType=NESTED_ORDERED_LIST_STYLE_TYPES[depthIndex%NESTED_ORDERED_LIST_STYLE_TYPES.length];	//get the list style type, wrapping around if necessary 
							}
							else	//if the list is not ordered
							{
								listStyleType=NESTED_UNORDERED_LIST_STYLE_TYPES[depthIndex%NESTED_UNORDERED_LIST_STYLE_TYPES.length];	//get the list style type, wrapping around if necessary 
							}
						}
						else	//if we didn't find at least one enclosing list
						{
							listStyleType=CSS_LIST_STYLE_TYPE_DISC;	//default to a disc marker
						}
					}
					if(!CSS_LIST_STYLE_TYPE_NONE.equals(listStyleType))  //if the list style type does not specify "none"
					{
						if(CSS_LIST_STYLE_TYPE_DISC.equals(listStyleType))  //if the marker should be a disc
						{
							final float markerHeight=relativeHeight/3;  //make the marker partially as high as the deepest child TODO make this a constant from somewhere
							final float markerY=allocRect.y+(relativeHeight-markerHeight)/2;  //center the marker vertically
							graphics.fillOval(Math.round(markerX), Math.round(markerY), Math.round(markerHeight), Math.round(markerHeight));		  //TODO fix; testing; maybe use fractional coordinates with Graphics2D
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
									final String siblingDisplay=XMLCSSStyles.getDisplay(siblingElement.getAttributes()); //get the CSS display property value of the sibling
									if(CSS_DISPLAY_LIST_ITEM.equals(siblingDisplay)) //if this is a list item
										++listItemIndex;  //we've found another list item that isn't us
								}
								else  //if we've found ourselves
									break;  //we now know our list index
							}
							//TODO we assume we found ourselves -- is there any instance in which we wouldn't, and how would we know?
						  final String markerString=XMLCSS.getMarkerString(listStyleType, listItemIndex);  //get a string representing the marker for us to render
						  if(markerString!=null)  //if we found a valid marker string
							{
							  graphics.setFont(font); //switch to the same font as the one being used by the list item
								final FontRenderContext fontRenderContext=graphics2D.getFontRenderContext();  //get the font rendering context
									//TODO probably make sure that it is antialiased, here
								final LineMetrics markerLineMetrics=graphics2D.getFont().getLineMetrics(markerString, fontRenderContext); //get the line metrics of the marker string
								final double markerHeight=markerLineMetrics.getAscent()/*TODO fix markerLineMetrics.getHeight()*/;  //find out how high the text is TODO fix; there's something not right here, especially with the multiple-line leading calculation of the text itself
								final double markerY=allocRect.y+markerHeight;  //align the text with the top of the list item
		//TODO bring back or del						final double markerY=allocRect.y+(relativeHeight-markerHeight)/2+markerHeight;  //center the marker vertically
									//TODO set the font here
								graphics.drawString(markerString, Math.round(markerX), (int)Math.round(markerY));	//TODO testing; i18n
							}
						}
					}
				} //TODO fix
			}
		}
	}

}
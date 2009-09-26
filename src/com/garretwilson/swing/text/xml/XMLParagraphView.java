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

import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;
import javax.swing.text.*;

import com.garretwilson.awt.Inset;
import com.garretwilson.swing.text.ContainerBoxView;
import com.garretwilson.swing.text.ContainerView;
import com.garretwilson.swing.text.FragmentViewFactory;
import static com.garretwilson.swing.text.Views.*;
import static com.garretwilson.swing.text.SwingText.*;
import com.garretwilson.swing.text.ViewBreakStrategy;
import com.garretwilson.swing.text.xml.css.XMLCSSStyles;
import com.garretwilson.swing.text.xml.css.XMLCSSView;
import com.garretwilson.swing.text.xml.css.XMLCSSViewPainter;
import com.globalmentor.text.xml.stylesheets.css.XMLCSS;

import com.globalmentor.log.Log;

/**A paragraph that understands CSS styles and knows how to be broken into fragments.
@author Garret Wilson
*/
public class XMLParagraphView extends ParagraphView implements Inset, XMLCSSView, FragmentViewFactory	//TODO del if not needed, ViewReleasable
{

	/**The shared default break strategy for container views.*/
	protected final static ViewBreakStrategy DEFAULT_BREAK_STRATEGY=ContainerBoxView.DEFAULT_BREAK_STRATEGY;

	/**The stategy for breaking this view into fragments.*/
	private ViewBreakStrategy breakStrategy=DEFAULT_BREAK_STRATEGY;

		/**@return The stategy for breaking this view into fragments.*/
		protected ViewBreakStrategy getBreakStrategy() {return breakStrategy;}

		/**Sets the stategy for breaking this view into fragments.
		@param strategy The strategy to use for creating view fragments.
		*/
		protected void setBreakStrategy(final ViewBreakStrategy strategy) {breakStrategy=strategy;}

	
	/**The justification of the paragraph. This is an override of the
		<code>ParagraphView</code> version because ParagraphView provides no
		accessor functions.*/
	private int justification;

		/**Gets the justification of the paragraph. This is present because
			<code>ParagraphView</code> ParagraphView provides no accessor functions for
			justification.
		@preturn The justification of the paragraph.
		*/
		public int getJustification() {return justification;}

		/**Sets the justification of the paragraph. This is present because
			<code>ParagraphView</code> ParagraphView provides no accessor functions for
			justification.
		@param justification The justification of the paragraph.
		*/
		protected void setJustification(final int justification) {this.justification=justification;}


	/**The line spacing of the paragraph. This is an override of the
		<code>ParagraphView</code> version because ParagraphView provides no
		accessor functions.*/
	private float lineSpacing;

		/**Gets the line spacing of the paragraph. This is present because
			<code>ParagraphView</code> ParagraphView provides no accessor functions for
			line spacing.
		@preturn The line spacing of the paragraph.
		*/
		public float getLineSpacing() {return lineSpacing;}

		/**Sets the line spacing of the paragraph. This is present because
			<code>ParagraphView</code> ParagraphView provides no accessor functions for
			line spacing.
		@param lineSpacing The line spacing of the paragraph.
		*/
		protected void setLineSpacing(final float lineSpacing) {this.lineSpacing=lineSpacing;}

	/**Whether the first line should be indented according to the specified
		indentation. A value of <code>false</code> will override any specified setting.
	*/
	private boolean firstLineIndented=true;

		/**@return Whether the first line should be indented, which overrides andy
		  specified setting.
		*/
		protected boolean isFirstLineIndented() {return firstLineIndented;}

		/**Sets whether the first line should be indented. If this is set to
		  <code>false</code> (if, for example, there is an image and no text), any
			specified first-line indent value will be ignored.
		@param newFirstLineIndented <code>true</code> if the first line should be
			indented according to the indention value, <code>false</code> if that
			value should be ignored.
		*/
		protected void setFirstLineIndented(final boolean newFirstLineIndented) {firstLineIndented=newFirstLineIndented;}

	/**@return The indent amount of the first line.*/
	protected float getFirstLineIndent() {return firstLineIndent;}

	/**Constructs a paragraph view for the given element.
	@param element The element for which this view is responsible.
	*/
	public XMLParagraphView(final Element element)
	{
		super(element);	//construct the parent class
//TODO del Log.trace("Creating XMLParagraphView, i18n property: ", getDocument().getProperty("i18n")); //TODO testing
Log.trace(); //TODO testing
//TODO fix antialias and fonts		  strategy=new com.garretwilson.swing.text.TextLayoutStrategy();  //TODO fix; testing i18n
	}

	/**@return The insets of the view.*/
	public Insets getInsets()
	{
		return new Insets(getTopInset(), getLeftInset(), getBottomInset(), getRightInset());	//return the insets of the view
	}

	/**Releases any cached information such as pooled views and flows. This
		version releases the entire pooled view.
	*/
/*TODO del if not needed
	public void release()
	{
		layoutPool=null;  //release the pool of views
		strategy=null;  //TODO testing ViewReleasable
		//TODO later, maybe release the strategy and the child views as well
	}
*/

	/**Sets the cached properties from the attributes. This overrides the version
		in <code>ParagraphView</code> to work with CSS attributes.
	*/
	protected void setPropertiesFromAttributes()
	{
		final AttributeSet attributeSet=getAttributes();	//get our attributes
		if(attributeSet!=null)	//if we have attributes
		{
			setBackgroundColor(XMLCSSStyles.getBackgroundColor(attributeSet));	//set the background color from the attributes
			setParagraphInsets(attributeSet);	//TODO fix
			setJustification(StyleConstants.getAlignment(attributeSet));	//TODO fix
//TODO del			setJustification(StyleConstants.ALIGN_JUSTIFIED);	//TODO fix
//TODO fix			setLineSpacing(StyleConstants.getLineSpacing(attributeSet));	//TODO fix
//TODO fix			setLineSpacing(2);	//TODO fix; testing
//TODO del			setLineSpacing(1.5f);	//TODO fix; testing
			setLineSpacing(XMLCSSStyles.getLineHeight(attributeSet));	//set the line height amount from our CSS attributes TODO fix this to correctly use the number
		  setVisible(!XMLCSS.CSS_DISPLAY_NONE.equals(XMLCSSStyles.getDisplay(attributeSet))); //the paragraph is visible only if it doesn't have a display of "none"
//TODO del			LineSpacing=3;	//TODO fix; testing

			final Document document=getDocument();	//get our document
			if(document instanceof StyledDocument)		//if this is a styled document
			{
				final StyledDocument styledDocument=(StyledDocument)document;	//cast the document to a styled document
				final Font font=styledDocument.getFont(attributeSet);	//let the document get the font from the attributes
//TODO find some way to cache the font in the attributes
				setFirstLineIndent(XMLCSSStyles.getTextIndent(attributeSet, font));	//set the text indent amount from the CSS property in the attributes, providing a font in case the length is in ems



//TODO fix			firstLineIndent = (int)StyleConstants.getFirstLineIndent(attributeSet);	//TODO fix

				//TODO we may want to set the insets in setPropertiesFromAttributes(); for
				//percentages, getPreferredeSpan(), etc. will have to look at the preferred
				//span and make calculations based upon the percentages
				//TODO probably have some other exernal helper class that sets the margins based upon the attributes
				final short marginTop=(short)Math.round(XMLCSSStyles.getMarginTop(attributeSet)); //get the top margin from the attributes
				final short marginLeft=(short)Math.round(XMLCSSStyles.getMarginLeft(attributeSet, font)); //get the left margin from the attributes
				final short marginBottom=(short)Math.round(XMLCSSStyles.getMarginBottom(attributeSet)); //get the bottom margin from the attributes
				final short marginRight=(short)Math.round(XMLCSSStyles.getMarginRight(attributeSet, font)); //get the right margin from the attributes
				setInsets(marginTop, marginLeft, marginBottom, marginRight);	//TODO fix; testing
			}
		}
	}

	/**Returns the constraining span to flow against for the given child index.
		This is called by the <code>FlowStrategy</code> while it is updating the flow.
		This method overrides the standard version to allow first-line indentions.
		@param index The index of the row being updated (>=0 and <getViewCount()).
	@see ParagraphView#getFlowSpan
	@see #getFlowStart
	*/
	public int getFlowSpan(int index)
	{
		int flowSpan=super.getFlowSpan(index);	//get the default flow span
		if(index==0 && isFirstLineIndented())	//if this is the first row, and we should indent the first row
			flowSpan-=getFirstLineIndent();	//show that the first line, if it's indented, has less room to flow inside
		return flowSpan;	//return the flow span, which has been updated to compensate for the indented first line
	}

	/**Returns the location along the flow axis that the flow span will start at.
		This is overridden to correctly indent first lines of paragraphs, if needed
	@param index The index of the row being flowed.
	@return The location along the flow axis that the flow span will start at.
	@see ParagraphView#getFlowStart
	@see #getFlowSpan
	*/
	public int getFlowStart(int index)
	{
		int flowStart=super.getFlowStart(index);	//get the default starting location
		if(index==0 && isFirstLineIndented())	//if this is the first row, and we should indent the first row
			flowStart+=getFirstLineIndent();	//increase this flow start by the amount to indent
		return flowStart;	//return the start of the flow
	}

	/**@return A new view to hold a single line of the paragraph.*/
	protected View createRow()
	{
		return new Line(getElement());	//return a new paragraph line view
  }

	/**Perform layout for the minor axis of the box (i.e. the axis orthoginal to
		the axis that it represents). The results of the layout should be placed in
		the given arrays which represent the allocations to the children along the
		minor axis.
	This version performs the default layout and then adds the correct
		indentation amount to the first row.
	@param targetSpan The total span given to the view, which whould be used to
		layout the children.
	@param axis The axis being layed out.
	@param offsets The offsets from the origin of the view for each of the child
		views. This is a return value and is filled in by the implementation of this
		method.
	@param spans The span of each child view. This is a return value and is
		filled in by the implementation of this method.
	@returns the offset and span for each child view in the offsets and spans
		parameters.
	*/
	protected void layoutMinorAxis(final int targetSpan, final int axis, final int[] offsets, final int[] spans)
	{
		super.layoutMinorAxis(targetSpan, axis, offsets, spans);	//do the default layout
		if(offsets.length>0 && isFirstLineIndented())	//if there is at least one row, and the first line should be indented
			offsets[0]+=getFirstLineIndent();	//add the correct indentation amount to the first rowcan
	}

	/**Renders using the given rendering surface and area on that surface.
	This version paings CSS-specific attributes
	@param graphics The rendering surface to use.
	@param allocation The allocated region to render into.
	*/
  public void paint(final Graphics graphics, final Shape allocation)
  {
		if(isVisible()) //if this view is visible
		{
					//TODO testing fractional metrics; should this be in XMLInlineView?
			final Graphics2D graphics2D=(Graphics2D)graphics;  //cast to the 2D version of graphics
			  //turn on fractional metrics TODO probably do this conditionally, based on some sort of flag
//TODO del; moved to XMLCSSViewPainter			graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//TODO del; moved to XMLCSSViewPainter		  graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			XMLCSSViewPainter.paint(graphics, allocation, this, getAttributes());	//paint our CSS-specific parts
		  super.paint(graphics, allocation);  //let the super class paint the rest of the paragraph
		}
	}

	/**Determines the preferred span for this view. Returns 0 if the view is not
		visible, otherwise it calls the superclass method to get the preferred span.
	@param axis The axis (<code>View.X_AXIS</code> or <code>View.Y_AXIS<code>).
	@return The span the view would like to be rendered into.
	@see javax.swing.text.ParagraphView#getPreferredSpan
	*/
	public float getPreferredSpan(int axis)
	{
		return isVisible() ? super.getPreferredSpan(axis) : 0;  //return 0 if the view isn't visible
	}

	/**Determines the minimum span for this view along an axis. Returns 0 if the
		view is not visible, otherwise it calls the superclass method to get the
		minimum span.
	@param axis The axis (<code>View.X_AXIS</code> or <code>View.Y_AXIS<code>).
	@return The minimum span the view can be rendered into.
	@see javax.swing.text.ParagraphView#getMinimumSpan
	*/
	public float getMinimumSpan(int axis)
	{
		return isVisible() ? super.getMinimumSpan(axis) : 0;  //return 0 if the view isn't visible
	}

	/**Determines the maximum span for this view along an axis. Returns 0 if the
		view is not visible, otherwise it calls the superclass method to get the
		maximum span.
	@param axis The axis (<code>View.X_AXIS</code> or <code>View.Y_AXIS<code>).
	@return The maximum span the view can be rendered into.
	@see javax.swing.text.ParagraphView#getMaximumSpan
	*/
	public float getMaximumSpan(int axis)
	{
		return isVisible() ? super.getMaximumSpan(axis) : 0;  //return 0 if the view isn't visible
	}

	/**Loads all of the children to initialize the view.
		This is called by the <code>setParent</code> method.
		This is reimplemented to not load any children directly, as they are created
		in the process of formatting.
		If the layoutPool variable is <code>null</code>, an instance of
		<code>LinePoolView</code> is created to represent the logical view that is
		used in the process of formatting.
		This version of <code>loadChildren</code> is implemented to return
		specifically a <code>XMLParagraphView.LinePoolView</code>, so that that view
		can implement its own special version of child view construction.
	@param viewFactory The view factory to use for child view creation.
	*/
	protected void loadChildren(final ViewFactory viewFactory)
	{
		if(layoutPool==null)  //if there is no layout pool
		{
		  layoutPool=new LinePoolView(getElement()); //create our own brand of logical view that will correctly create paragraph child views
		}
		super.loadChildren(viewFactory);	//load the pool children normally
		final int poolViewCount=layoutPool.getViewCount();  //find out how many views are in the pool
		if(poolViewCount>0 && getEndOffset()>getStartOffset())  //if we have at least some content, see if there are only object views present, how many inline views there are, etc.
		{
			boolean onlyObjectsPresent=true; //assume there are only objects present in this paragraph
			int inlineViewCount=0;  //we'll keep a record of how many inline views there are
//TODO fix			boolean firstInlineViewHasMultipleWords=false;  //assume the first inline view has multiple words
			for(int i=0; i<poolViewCount; ++i)  //look at each view in the pool
			{
				final View pooledView=layoutPool.getView(i);  //get a reference to this pooled view
				if(!(pooledView instanceof XMLObjectView)) //if this isn't an object
				{
					onlyObjectsPresent=false; //show that there are other things besides objects present
				}
				else if(pooledView instanceof XMLInlineView)
				{
//TODO fix					if(inlineViweCount==0)  //if this is the first inline view we've found
//				TODO fix					{
//				TODO fix						final Segment segment=getText(pooledView.getStartOffset(), pooledView.getEndOffset());  //get the segment of text the inline view represents
//				TODO fix						pooledView.getElement().get
//				TODO fix					}
					++inlineViewCount;  //show that we've found another inline view
				}
			}
			final View parentView=getParent();  //get our parent view
			final String parentDisplay=XMLCSSStyles.getDisplay(parentView.getAttributes()); //see what kind of parent we have
			final boolean isInTableCell=XMLCSS.CSS_DISPLAY_TABLE_CELL.equals(parentDisplay);  //see if we're inside a table cell
			setFirstLineIndented(!onlyObjectsPresent && (inlineViewCount>1 || !isInTableCell)); //if there are only objects present in this paragraph, or if there's only one inline view in a table cell, we won't indent
		}
		else  //if there is no content
		{
			setVisible(false);  //make the entire paragraph invisible
		}
	}

	/**Determines how attractive a break opportunity in this view is.
	This is implemented to forward to the superclass for axis perpendicular to
	the flow axis. Along the flow axis the following values may be returned:
	<dl>
		<dt><code>View.BadBreakWeight</code></dt> <dd>If the desired break location
			is less than the first child is comfortable with (i.e. not even one child view can fit),
			or there are no child views</dd>
		<dt><code>View.GoodBreakWeight</code></dt> <dd>If the other conditions don't occur.</dd>
	</dl>
	This will result in the view being broken with the maximum number of child
	views that can fit within the required span.
	@param axis The breaking axis, either View.X_AXIS or View.Y_AXIS.
	@param pos The potential location of the start of the broken view (>=0).
		This may be useful for calculating tab positions.
	@param len Specifies the relative length from <var>pos</var> where a potential
		break is desired (>=0).
	@return The weight, which should be a value between <code>View.ForcedBreakWeight
		and View.BadBreakWeight.</code>
	*/
	public int getBreakWeight(int axis, float pos, float len)	//TODO add support for requiring a certain number of child views (lines)
	{
		if(axis==getAxis())	//if they want to break along our tiling axis
		{		  	
			final String pageBreakAfter=XMLCSSStyles.getPageBreakAfter(getAttributes());	//see how the view considers breaking after it
				//if we should avoid breaking after this view, and the provided length is more than we need (i.e. we aren't being asked to break in our middle)
			if(XMLCSS.CSS_PAGE_BREAK_AFTER_AVOID.equals(pageBreakAfter) && len>getPreferredSpan(axis))
			{
				return BadBreakWeight;	//don't allow breaking
			}
			else	//if we aren't break-averse, get the highest break weight available; this has the advantage of allowing invisible views to return their low break weight
			{
				final float marginSpan=(axis==X_AXIS) ? getLeftInset()+getRightInset() : getTopInset()+getBottomInset();	//see how much margin we have to allow for
				int bestBreakWeight=BadBreakWeight;	//start out assuming we can't break
				float spanLeft=len-marginSpan;	//find out how much span we have left (the margins will always be there)
				final int viewCount=getViewCount();	//find out how many child views there are
				for(int i=0; i<viewCount && spanLeft>0; ++i)	//look at each child view until we run out of span
				{
					final View view=getView(i);	//get this child view
					final int breakWeight=view.getBreakWeight(axis, pos, spanLeft);	//get the break weight of this view
					if(breakWeight>bestBreakWeight)	//if this break weight is better than the one we already have
					{
						bestBreakWeight=breakWeight;	//update our best break weight
					}
					spanLeft-=view.getPreferredSpan(axis);	//update the amount of span we've used by this point
				}
				return bestBreakWeight;	//return the best break weight we found
			}
		}
		else	//if they want to break along another axis
		{
			return super.getBreakWeight(axis, pos, len);	//return the default break weight
		}
	}

	/**Breaks this view on the given axis at the given length.
	This implementation delegates to the view break strategy.
	@param axis The axis to break along, either View.X_AXIS or View.Y_AXIS.
	@param offset The location in the model where the fragment should start its representation (>=0).
	@param pos The position along the axis that the broken view would occupy (>=0).
	@param length Specifies the distance along the axis where a potential break is desired (>=0).
	@return The fragment of the view that represents the given span, or the view itself if it cannot be broken
	@see ViewBreakStrategy#breakView()
	*/
	public View breakView(final int axis, final int offset, final float pos, final float length)
	{
		final float marginSpan=(axis==X_AXIS) ? getLeftInset()+getRightInset() : getTopInset()+getBottomInset();	//see how much margin we have to allow for
		return getBreakStrategy().breakView(this, axis, offset, pos, length-marginSpan, this);	//ask the view break strategy to break our view, using this view as the view fragment factory
	}
	
	/**Creates a view that represents a portion of the element.
	This implementation delegates to the view break strategy.
	@param p0 The starting offset (>=0). This should be a value greater or equal
		to the element starting offset and less than the element ending offset.
	@param p1 The ending offset (>p0).  This should be a value less than or
		equal to the elements end offset and greater than the elements starting offset.
	@return The view fragment, or itself if the view doesn't support breaking into fragments.
	@see ViewBreakStrategy#createFragment()
	*/
	public View createFragment(int p0, int p1)
	{
		return getBreakStrategy().createFragment(this, p0, p1, this);	//ask the view break strategy to break our view, using this view as the view fragment factory
	}

	/**Creates a fragment view into which pieces of this view will be placed.
	@param isFirstFragment Whether this fragment holds the first part of the
		original view.
	@param isLastFragment Whether this fragment holds the last part of the
		original view.
	*/
	public View createFragmentView(final boolean isFirstFragment, final boolean isLastFragment)
	{
	  return new XMLParagraphFragmentView(getElement(), getAxis(), this, isFirstFragment, isLastFragment);	//create a fragment of this view
	}

	/**Internally created view that holds the views representing a paragraph line.
	@author Garret Wilson
	*/
	protected class Line extends ContainerBoxView
	{
		/**Constructor.
		@param element The paragraph element.
		*/
		public Line(final Element element)
		{
			super(element, XMLParagraphView.this.getAxis()==X_AXIS ? Y_AXIS : X_AXIS);	//tile the line orthogonally to the paragraph
		}

		/**Each line does not need to fill its children, since its parent paragraph will load its children with the views it created.
		This function therefore does nothing.
		*/
		protected void loadChildren(ViewFactory f) {}

		/**Returns the attributes to use for this container view.
	 	Because this view does not directly represent its underlying element,
		the attributes of the parent view is returned, if there is a parent.
		@return The attributes of the parent view, or the attributes of the underlying element if there is no parent.
		*/
		public AttributeSet getAttributes()
		{
			final View parentView=getParent();	//get the parent view
			return parentView!=null ? parentView.getAttributes() : super.getAttributes();	//return the parent's attributes, if we have a parent, or the default attributes if we have no parent
		}

				//TODO comment
				public float getAlignment(int axis) {
						if (axis == View.X_AXIS) {
								switch (getJustification()) {
								case StyleConstants.ALIGN_LEFT:
										return 0;
								case StyleConstants.ALIGN_RIGHT:
										return 1;
								case StyleConstants.ALIGN_CENTER:
								case StyleConstants.ALIGN_JUSTIFIED:
										return 0.5f;
								}
						}
						return super.getAlignment(axis);
				}


        /**
         * Provides a mapping from the document model coordinate space
         * to the coordinate space of the view mapped to it.  This is
         * implemented to let the superclass find the position along 
         * the major axis and the allocation of the row is used 
         * along the minor axis, so that even though the children 
         * are different heights they all get the same caret height.
         *
         * @param pos the position to convert
         * @param a the allocated region to render into
         * @return the bounding box of the given position
         * @exception BadLocationException  if the given position does not represent a
         *   valid location in the associated document
         * @see View#modelToView
         */
        public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {	//TODO recomment; improve
            Rectangle r = a.getBounds();
	    View v = getViewAtPosition(pos, r);
	    if ((v != null) && (!v.getElement().isLeaf())) {
		// Don't adjust the height if the view represents a branch.
		return super.modelToView(pos, a, b);
	    }
	    r = a.getBounds();
            int height = r.height;
            int y = r.y;
            Shape loc = super.modelToView(pos, a, b);
            r = loc.getBounds();
            r.height = height;
            r.y = y;
            return r;
        }

	/**
	 * Perform layout for the minor axis of the box (i.e. the
	 * axis orthoginal to the axis that it represents).  The results
	 * of the layout should be placed in the given arrays which represent
	 * the allocations to the children along the minor axis.
	 * <p>
	 * This is implemented to do a baseline layout of the children
	 * by calling BoxView.baselineLayout.
	 *
	 * @param targetSpan the total span given to the view, which
	 *  whould be used to layout the children.
	 * @param axis the axis being layed out.
	 * @param offsets the offsets from the origin of the view for
	 *  each of the child views.  This is a return value and is
	 *  filled in by the implementation of this method.
	 * @param spans the span of each child view.  This is a return
	 *  value and is filled in by the implementation of this method.
	 * @returns the offset and span for each child view in the
	 *  offsets and spans parameters.
	 */
		protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans)	//TODO verify this method
		{
			baselineLayout(targetSpan, axis, offsets, spans); //do a baseline layout of the row
			for(int i=0; i<offsets.length; ++i) //look at each of the offsets
			{
				final String verticalAlign; //we'll get the vertical alignment value from the view
				final View childView=getView(i);  //get a reference to this child view
				if(childView instanceof XMLInlineView)  //if this is an XML inline view
					verticalAlign=((XMLInlineView)childView).getVerticalAlign();  //get the cached vertical alignment value directly from the view
				else  //if the view is another type
					verticalAlign=XMLCSSStyles.getVerticalAlign(childView.getAttributes()); //get the vertical alignment specified by the inline view
				final int relativeIndex=i>0 ? i-1 : (offsets.length>1 ? i+1 : i);  //we'll move the offset relative to the offset before us or, if this is the first offset, the one after us; if there is only one offset, use ourselves
//TODO del Log.trace("Relative index: ", relativeIndex); //TODO del
				  //TODO this currently doesn't work for subscripts of subscripts or superscripts of superscripts or a superscript followed by a subscript, etc.
				final int relativeOffset=offsets[relativeIndex];  //get the span to which we're relative
				final int relativeSpan=spans[relativeIndex];  //get the span to which we're relative
				if(XMLCSS.CSS_VERTICAL_ALIGN_SUPER.equals(verticalAlign))  //if this is superscript
				{
				  offsets[i]=relativeOffset-Math.round(relativeSpan*.20f); //move the box 20% above the relative box of the relative box's height
				}
				else if(XMLCSS.CSS_VERTICAL_ALIGN_SUB.equals(verticalAlign))  //if this is subscript
				{
						//TODO fix; right now, it doesn't compensate for the linespacing
				  offsets[i]=relativeOffset+Math.round(relativeSpan*.60f); //move the box 20% below the relative box of the relative box's height
				}
			}
		}

		protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r)
		{
			return baselineRequirements(axis, r);
		}

		public float getPreferredSpan(int axis)
		{
			return axis==Y_AXIS ? super.getPreferredSpan(axis)*getLineSpacing() : super.getPreferredSpan(axis);	//TODO testing
		}

		public float getMaximumSpan(int axis)
		{
			return axis==Y_AXIS ? super.getMaximumSpan(axis)*getLineSpacing() : super.getMaximumSpan(axis);	//TODO testing
		}

		public float getMinimumSpan(int axis)
		{
			return axis==Y_AXIS ? super.getMinimumSpan(axis)*getLineSpacing() : super.getMinimumSpan(axis);	//TODO testing
		}
	}

	/**The class that serves as a pool of views representing the logical view of
		the paragraph. It keeps the children updated to reflect the state of the
		model, gives the logical child views access to the view hierarchy, and
		calculates a preferred span. It doesn't do any rendering, layout, or
		model/view translation. This class replicates functionality of
		<code>FlowView.LogicalView</code> with special view loading for paragraph
		children to accomodate nested style tags. This class does not descend from
		<code>FlowView.LogicalView</code> because that class has package visibility.
	@see FlowView#LogicalView
	*/
	protected class LinePoolView extends ContainerView
	{
		
		/**Logical view constructor.
		@param element The element this logical view represents.
		*/
		public LinePoolView(Element element)
		{
		  super(element, XMLParagraphView.this.getAxis()==X_AXIS ? Y_AXIS : X_AXIS);	//tile the line orthogonally to the paragraph
		}

		/**Returns the attributes to use for this container view.
	 	Because this view does not directly represent its underlying element,
		the attributes of the parent view is returned, if there is a parent.
		@return The attributes of the parent view, or the attributes of the underlying element if there is no parent.
		*/
		public AttributeSet getAttributes()
		{
			final View parentView=getParent();	//get the parent view
			return parentView!=null ? parentView.getAttributes() : super.getAttributes();	//return the parent's attributes, if we have a parent, or the default attributes if we have no parent
		}

		/**Loads all of the children to initialize the view.
			This is called by the <a href="#setParent">setParent</a> method.
			This version initially attempts to create a view for each child element,
			as normal. If the view factory returns <code>null</code> for an element,
			however, the element's children will be iterated and the same process will
			take place. This allows nested inline elements to be correctly turned into
			inline views by a view factory which recognizes this procedure, with the
			view factory determining which element hierarchies should be iterated
			(such as XHTML <code>&lt;em&gt;</code>) and which should not (such as
			XHTML <code>&img;</code>). View factories which do not support this
			procedure will function as normal.
		@param viewFactory The view factory.
		@see #setParent
		*/
		protected void loadChildren(ViewFactory viewFactory)
		{
		  if(viewFactory==null) //if there is no view factory, the parent view has somehow changed
		    return; //don't create children
			final Element[] childElements=getChildElements(getElement());	//put our child elements into an array
			final View[] views=createViews(childElements, viewFactory);	//create views for the child elements
	    replace(0, getViewCount(), views);  //add the views to our view
    }

		/**Updates the child views in response to receiving notification that the 
			model changed, and there is change record for the element this view is
			responsible for.
		<p>This version correctly collapses the child inline view hierarchy.</p>
		@param elementChange The change information for the element this view is
			responsible for. This should not be <code>null</code> if this method gets
			called.
		@param documentEvent The change information from the associated document.
		@param viewFactory The factory to use to build child views.
		@return Whether or not the child views represent the child elements of the
			element this view is responsible for.
		*/
		protected boolean updateChildren(final DocumentEvent.ElementChange elementChange, final DocumentEvent documentEvent, final ViewFactory viewFactory)
		{
			final Element[] removedElems=elementChange.getChildrenRemoved();	//TODO comment and verify
			final Element[] addedElems=elementChange.getChildrenAdded();
			View[] added=null;
			if(addedElems!=null)
			{
				added=createViews(addedElems, viewFactory);	//create views for the added elements
			}
			int nremoved=0;
			int index=elementChange.getIndex();
			if(removedElems!=null)
			{
				nremoved= removedElems.length;
			}
			replace(index, nremoved, added);
			return true;
		}

		/**Forward the document event to the given child view.
		This implementation first reparents the child to the logical view, as the child
		may have been given a parent line if the child could fit without breaking.
		@param view The child view to forward the event to.
		@param event The change information from the associated document.
		@param allocation The current allocation of the view.
		@param factory The factory to use to rebuild if the view has children.
		*/
		protected void forwardUpdateToView(final View view, final DocumentEvent event, final Shape allocation, final ViewFactory factory)
		{
			view.setParent(this);	//reparent the view to the pool TODO check about hierarchy reparenting; see XMLPagedView.PagePoolView
			super.forwardUpdateToView(view, event, allocation, factory);	//forward the update normally
		}

	}

	/**The class that serves as a fragment if the paragraph is broken.
	@author Garret Wilson
	*/
	protected class XMLParagraphFragmentView extends XMLFragmentBlockView
	{

		/**Constructs a fragment view for the paragraph.
		@param element The element this view is responsible for.
		@param axis The tiling axis, either View.X_AXIS or View.Y_AXIS.
		@param wholeView The original, unfragmented view from which this fragment (or one or more intermediate fragments) was broken.
		@param firstFragment Whether this is the first fragement of the original view.
		@param lastFragment Whether this is the last fragment of the original view.
		*/
		public XMLParagraphFragmentView(final Element element, final int axis, final View wholeView, final boolean firstFragment, final boolean lastFragment)
		{
			super(element, axis, wholeView, firstFragment, lastFragment); //do the default construction
		}

		/**Creates a fragment view into which pieces of this view will be placed.
		@param isFirstFragment Whether this fragment holds the first part of the original view.
		@param isLastFragment Whether this fragment holds the last part of the original view.
		*/
		public View createFragmentView(final boolean isFirstFragment, final boolean isLastFragment)
		{
		  return new XMLParagraphFragmentView(getElement(), getAxis(), getWholeView(), isFirstFragment, isLastFragment);	//create a fragment of this view, indicating the original view
		}

		/**Perform layout for the minor axis of the box (i.e. the axis orthoginal to
			the axis that it represents). The results of the layout should be placed in
			the given arrays which represent the allocations to the children along the
			minor axis.
			This version performs the default layout and then adds the correct
			indentation amount to the first row.
		@param targetSpan The total span given to the view, which whould be used to
			layout the children.
		@param axis The axis being layed out.
		@param offsets The offsets from the origin of the view for each of the child
			views. This is a return value and is filled in by the implementation of this
			method.
		@param spans The span of each child view. This is a return value and is
			filled in by the implementation of this method.
		@returns the offset and span for each child view in the offsets and spans
			parameters.
		*/
		protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans)  //TODO testing
		{
			super.layoutMinorAxis(targetSpan, axis, offsets, spans);	//do the default layout
		  //if we're the first fragement, there is at least one row, and the first line should be indented
		  if(isFirstFragment() && offsets.length>0 && isFirstLineIndented())
//TODO del when works			if(offsets.length>0)	//if there is at least one row, and the first line should be indented
				offsets[0]+=firstLineIndent;	//add the correct indentation amount to the first row TODO use an accessor function here for firstLineIndent, if we can
		}

	}

  /**
   * Child views can call this on the parent to indicate that
   * the preference has changed and should be reconsidered
   * for layout.  By default this just propagates upward to 
   * the next parent.  The root view will call 
   * <code>revalidate</code> on the associated text component.
   *
   * @param child the child view
   * @param width true if the width preference has changed
   * @param height true if the height preference has changed
   * @see javax.swing.JComponent#revalidate
   */
  public void preferenceChanged(View child, boolean width, boolean height)
  {
//TODO del  	removeAll();	//TODO testing
  	super.preferenceChanged(child, width, height);
  	
  }

	//newswing

	/**Whether this view is visible.*/
	private boolean visible;

		/**@return Whether this view should be visible.*/
		public boolean isVisible() {return visible;}

		/**Sets whether this view is visible.
		@param newVisible Whether the view should be visible.
		*/
		protected void setVisible(final boolean newVisible) {visible=newVisible;}

	/**The background color of the block view.*/
	private Color backgroundColor;

		/**Gets the background color of the block view.
		@return The background color of the block view.
		*/
		public Color getBackgroundColor()
		{
//TODO del; ParagraphView doesn't seem to do the same type of caching			synchronize();	//make sure we have the correct cached property values
			return backgroundColor;
		}

		/**Sets the background color of the block view.
		@param newBackgroundColor The color of the background.
		*/
		protected void setBackgroundColor(final Color newBackgroundColor) {backgroundColor=newBackgroundColor;}

}

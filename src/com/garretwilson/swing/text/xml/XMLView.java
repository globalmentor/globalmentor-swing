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

/**A generic view for displaying XML. Also provides static utility functions
	used in other non-descendant views.
@author Garret Wilson
*/
public class XMLView	//TODO maybe delete this class
{

	/**Constructs an X	/**Determines how attractive a break opportunity in this view is. This can be
		used for determining which view is the most attractive to call
		<code>breakView</code> on in the process of formatting.  The higher the
		weight, the more attractive the break.  A value equal to or lower than
		<code>View.BadBreakWeight</code> should not be considered for a break. A
		value greater than or equal to <code>View.ForcedBreakWeight</code> should
		be broken.<br/>
		This is implemented to forward to the superclass for axis perpendicular to
		the flow axis. Along the flow axis the following values may be returned:
		<ul>
//TODO fix			<li>View.ExcellentBreakWeight: If there is whitespace proceeding the desired break
//TODO fix		 *   location.
			<li>View.BadBreakWeight: If the desired break location results in a break
				location of the starting offset (i.e. not even one child view can fit.</li>
			<li>View.GoodBreakWeight: If the other conditions don't occur.
		</ul>
		This will result in the view being broken with the maximum number of child
		views that can fit within the required span.
		@param axis The breaking axis, either View.X_AXIS or View.Y_AXIS.
		@param pos The potential location of the start of the broken view (>=0).
			This may be useful for calculating tab positions.
		@param len Specifies the relative length from <em>pos</em> where a potential
			break is desired (>=0).
		@return The weight, which should be a value between View.ForcedBreakWeight
			and View.BadBreakWeight.
		@see LabelView
		@see ParagraphView
		@see BadBreakWeight
		@see GoodBreakWeight
		@see ExcellentBreakWeight
		@see ForcedBreakWeight
	*/
/*TODO fix or del
	public int getBreakWeight(final View view, int axis, float pos, float len)
	{
//TODO del System.out.println("Inside XMLBlockView.getBreakWeight axis: "+axis+" pos: "+pos+" len: "+len+" name: "+XMLStyleConstants.getXMLElementName(getElement().getAttributes()));	//TODO del
System.out.println("Inside XMLView.getBreakWeight axis: "+axis+" pos: "+pos+" len: "+len+" name: "+view.getAttributes().getAttribute(StyleConstants.NameAttribute));	//TODO del

//TODO del		final int tileAxis=getAxis();	//get our axis for tiling (this view's axis)
		if(axis==view.getAxis())	//if they want to break along our tiling axis
		{
//TODO bring back when works			return View.GoodBreakWeight;	//show that this break spot will work
			return View.GoodBreakWeight;	//show that this break spot will work
		}
		else	//if they want to break along another axis besides the one we know about
			return super.getBreakWeight(axis, pos, len);	//return the default break weight
	}
*/


	/**Creates a view that represents a portion of the element. This is
		potentially useful during formatting operations for taking measurements of
		fragments of the view. If the view doesn't support fragmenting, it should
		return itself.<br/>
		This view does support fragmenting. It is implemented to return a new view
		that contains the required child views.
		@param p0 The starting offset (>=0). This should be a value greater or equal
			to the element starting offset and less than the element ending offset.
		@param p1 The ending offset (>p0).  This should be a value less than or
			equal to the elements end offset and greater than the elements starting offset.
		@returns The view fragment, or itself if the view doesn't support breaking
			into fragments.
		@see View#createFragment
	*/
/*TODO fix
	public View createFragment(int p0, int p1)
	{
//TODO del System.out.println("Inside createFragment(), p0: "+p0+" p1: "+p1+" name: "+XMLStyleConstants.getXMLElementName(getElement().getAttributes()));	//TODO del
System.out.println("Inside XMLBLockView.createFragment(), p0: "+p0+" p1: "+p1+" name: "+getAttributes().getAttribute(StyleConstants.NameAttribute));	//TODO del
System.out.println("Our startOffset: "+getStartOffset()+" endOffset: "+getEndOffset());	//TODO del
//TODO del System.out.println("Inside createFragment(), p0: "+p0+" p1: "+p1);	//TODO del
//TODO del System.out.println("Our startOffset: : "+getStartOffset()+" endOffset: "+getEndOffset());	//TODO del





//TODO del		XMLBlockView fragmentView=(XMLBlockView)clone();	//create a clone of this view
		if(p0<=getStartOffset() && p1>=getEndOffset())	//if the range they want encompasses all of our view
			return this;	//return ourselves; there's no use to try to break ourselves up
		else	//if the range they want only includes part of our view
		{




//TODO del			final BoxView fragmentView=new BoxView(getElement(), getAxis());	//TODO testing! highly unstable! trying to fix vertical spacing bug

			//TODO maybe start looking somewhere here to find the vertical spacing bug

			final XMLBlockView fragmentView=(XMLBlockView)clone();	//create a clone of this view


	//TODO fix		final XMLBlockFragmentView fragmentView=new XMLBlockFragmentView(this);	//create a fragment to hold part of our content
			for(int i=0; i<getViewCount(); ++i)	//look at each child view
			{
				final View childView=getView(i);	//get a reference to this child view
				if(childView.getStartOffset()<p1 && childView.getEndOffset()>p0)	//if this view is within our range
				{
	//TODO del when works			if(childView.getStartOffset()>=p0 && childView.getEndOffset()<=p1)	//if this view is within our range
					final int startPos=Math.max(p0, childView.getStartOffset());	//find out where we want to start, staying within this child view
					final int endPos=Math.min(p1, childView.getEndOffset());	//find out where we want to end, staying within this child view
					fragmentView.append(childView.createFragment(startPos, endPos));	//add a portion (or all) of this child to our fragment
				}
			}
			return fragmentView;	//return the fragment view we constructed
		}
	}
*/

		/**Each fragment is a subset of the content in the breaking <code>XMLBlockView</code>.
		@return The starting offset of this page, which is the starting offset of the
			view with the lowest starting offset
		@see View#getRange
		*/
//TODO testing
/*TODO fix
		public int getStartOffset()
		{
			int startOffset=Integer.MAX_VALUE;	//we'll start out with a high number, and we'll end up with the lowest starting offset of all the views
			final int numViews=getViewCount();	//find out how many view are on this page
//TODO del System.out.println("getStartOffset() viewCount: "+numViews+" name: "+(String)getElement().getAttributes().getAttribute(XMLCSSStyleConstants.XMLElementNameName)+" super: "+super.getStartOffset());	//TODO del
			if(numViews>0)	//if we have child views
			{
				for(int viewIndex=0; viewIndex<numViews; ++viewIndex)	//look at each view on this page
				{
					final View view=getView(viewIndex);	//get a reference to this view
					startOffset=Math.min(startOffset, view.getStartOffset());	//if this view has a lower starting offset, use its starting offset
//TODO del System.out.println("  View: "+(String)getElement().getAttributes().getAttribute(XMLCSSStyleConstants.XMLElementNameName)+" child: "+(String)view.getElement().getAttributes().getAttribute(XMLCSSStyleConstants.XMLElementNameName)+" startOffset: "+view.getStartOffset()+" New start offset: "+startOffset);	//TODO del
				}
				return startOffset;	//return the starting offset we found
			}
			else	//if we don't have any child views
				return super.getStartOffset();	//return the default starting offset
		}
*/

		/**Each fragment is a subset of the content in the breaking <code>XMLBlockView</code>.
		@return The ending offset of this page, which is the ending offset of the
			view with the largest ending offset
		@see View#getRange
		*/
//TODO testing
/*TODO fix
		public int getEndOffset()
		{
			int endOffset=0;	//start out with a low ending offset, and we'll wind up with the largest ending offset
			final int numViews=getViewCount();	//find out how many view are on this page
//TODO del System.out.println("getEndOffset() viewCount: "+numViews+" name: "+(String)getElement().getAttributes().getAttribute(XMLCSSStyleConstants.XMLElementNameName)+" super: "+super.getEndOffset());	//TODO del
			if(numViews>0)	//if we have child views
			{
				for(int viewIndex=0; viewIndex<numViews; ++viewIndex)	//look at each view on this page
				{
					final View view=getView(viewIndex);	//get a reference to this view
					endOffset=Math.max(endOffset, view.getEndOffset());	//if this view has a larger ending offset, use that instead
//TODO del System.out.println("  View: "+(String)getElement().getAttributes().getAttribute(XMLCSSStyleConstants.XMLElementNameName)+" child: "+(String)view.getElement().getAttributes().getAttribute(XMLCSSStyleConstants.XMLElementNameName)+" endOffset: "+view.getEndOffset()+" New end offset: "+endOffset);	//TODO del
				}
				return endOffset;	//return the largest ending offset we found
			}
			else	//if we don't have any child views
				return super.getEndOffset();	//return the default ending offset
		}
*/

}


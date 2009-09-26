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

package com.garretwilson.swing.text;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.*;

/**An unconstrained view that contains other views.
No assumptions are made about the order, size, or offsets of the child views, unlike <code>CompositeView</code>.
This class consequently determines view index based upon document position without relying on underlying
element order and offsets.
Beginning and ending offsets are determined based upon the contained views.
This view does not provide for fragmentation.
@author Garret Wilson
*/
public class ContainerView extends CompositeView
{

	/**<code>true</code> if the cache is based upon recent information, even though there may not be a cached value available.*/
	private boolean cacheValid=false;

	/**Invalidates the cached values so that they can be recalculated when needed.*/
	protected void invalidateCache() {cacheValid=false;}

	/**The last known element starting offset; used to ensure that changed content hasn't changed our cached starting or ending offset values.*/
	private int lastElementStartOffset;
	
	/**The last known element ending offset; used to ensure that changed content hasn't changed our cached starting or ending offset values.*/
	private int lastElementEndOffset;
	
	/**The cached starting offset, or <code>-1</code> if there is no cached value.*/
	private int startOffset=-1;

	/**The cached ending offset, or <code>-1</code> if there is no cached value.*/
	private int endOffset=-1;

	/**Constructor.
	@param element The element this view is responsible for.
	@param axis Either <code>View.X_AXIS</code> or <code>View.Y_AXIS</code>
	*/
	public ContainerView(final Element element, final int axis)
	{
		super(element);	//construct the parent class
	}

	/**Replaces child views, which can function as an insert or a remove.
	This version invalidates the cached values so that they can be recalculated when needed. 
	@param offset The starting index (0&lt;=<var>offset</var>&lt;=<code>getViewCount()</code>) into the child views to insert the new views.
	@param length The number of existing child views (0&lt;=<var>length</var>&lt;=<code>getViewCount()</code>-<var>offset</var>) to remove.
	@param views The child views to add, or <code>null</code> if no children are being added.
	@see #invalidateCache()
	*/
  public void replace(final int offset, final int length, final View[] views)
  {
  	super.replace(offset, length, views);	//do the default replacement
  	invalidateCache();	//invalidate the cache
  }

	/**@return <code>true</code> if the cache is based upon recent information, even though there may not be a cached value available.
	This implementation not only checks the cache valid flag, but also verifies the underlying element's starting and ending offset
	to make sure the content hasn't changed. 
	*/
	protected boolean isCacheValid()
	{
		final Element element=getElement();	//get the underlying element
		return cacheValid && lastElementStartOffset==element.getStartOffset() && lastElementEndOffset==element.getEndOffset();	//make sure the cache is valid and the content hasn't changed
	}

  /**Checks the cache and updates it if necessary.
  @see #updateCache() 
  */
  protected void verifyCache()
  {
  	if(!isCacheValid())	//if the cache is not valid
  	{
  		updateCache();	//update the cache
  	}
  }

  /**Updates the cached values, including beginning and ending offset.*/
  protected void updateCache()
  {
		final int viewCount=getViewCount();	//find out how many view are contained in this view
		if(viewCount>0)	//if we have child views
		{
			startOffset=Integer.MAX_VALUE;	//we'll start out with a high number, and we'll end up with the lowest starting offset of all the views
			endOffset=0;	//start out with a low ending offset, and we'll wind up with the largest ending offset
			for(int viewIndex=viewCount-1; viewIndex>=0; --viewIndex)	//look at each child view in the container view
			{
				final View childView=getView(viewIndex);	//get a reference to this child view
				final int childStartOffset=childView.getStartOffset();  //get the child's starting offset
				if(childStartOffset<startOffset)	//if this child start offset is lower than what we've alredy found
				{
					startOffset=childStartOffset;	//update our starting offset
				}				
				final int childEndOffset=childView.getEndOffset();  //get the child's ending offset
				if(childEndOffset>endOffset)	//if this child end offset is higher than what we've alredy found
				{
					endOffset=childEndOffset;	//update our ending offset
				}				
			}
		}
		else	//if we don't have any child views
		{
			startOffset=endOffset=-1;	//show that there are no cached values; the beginning and ending offsets of the underlying element will have to be returned
		}
		final Element element=getElement();	//get the underlying element
		lastElementStartOffset=getElement().getStartOffset();	//update our record of the underlying element's offsets to see if the content changes
		lastElementEndOffset=getElement().getEndOffset();
		cacheValid=true;	//show that the cached values are now valid
	}

	/**Fetches the portion of the model for which this view is responsible.
	This version uses the cached starting offset, if available; otherwise, the default starting offset is returned.
	@return The starting offset into the model (>=0).
	*/
	public int getStartOffset()
	{
		verifyCache();	//make sure the cache is valid
		return startOffset>=0 ? startOffset : super.getStartOffset();	//return the cached value or the default if there is no cached value
	}

	/**Fetches the portion of the model for which this view is responsible.
	This version uses the cached ending offset, if available; otherwise, the default ending offset is returned.
	@return The ending offset into the model (>=0).
	*/
	public int getEndOffset()
	{
		verifyCache();	//make sure the cache is valid
		return endOffset>=0 ? endOffset : super.getEndOffset();	//return the cached value or the default if there is no cached value
	}

	/**Returns the index of the child at the given model position in the container.
	This implementation queries each view directly and does not assume that there is
	a direct correspondence between each child view and the underlying element hierarchy.
	@param pos The position (>=0) in the model.
	@return The index of the view representing the given position, or -1 if there
		is no view on this container which represents that position.
	*/
	protected int getViewIndexAtPosition(final int pos)
	{
		return getViewIndexAtPosition(this, pos);	//delegate to the utility method
	}

	/**Returns the index of the child of the given view at the given model position in the container.
	This implementation queries each view directly and does not assume that there is
	a direct correspondence between each child view and the underlying element hierarchy.
	@param view The view the children of which should be examined.
	@param pos The position (>=0) in the model.
	@return The index of the view representing the given position, or -1 if there
		is no view on this container which represents that position.
	*/
	public static int getViewIndexAtPosition(final View view, final int pos)
	{
		if(pos>=view.getStartOffset() && pos<view.getEndOffset())	//if the position is within the content this view is responsible for (this will save us time if the request is for content outside of this view)
		{
			for(int viewIndex=view.getViewCount()-1; viewIndex>=0; --viewIndex)	//look at all the views from the last to the first
			{
				final View childView=view.getView(viewIndex);	//get a reference to this child view
				if(pos>=childView.getStartOffset() && pos<childView.getEndOffset())	//if this child view holds the requested position
					return viewIndex;	//return the index to this child view
			}
		}
		return -1;	//if we make it to this point, we haven't been able to find a view with the specified position
	}

	/**
	 * Determines the preferred span for this view along an
	 * axis.
	 *
	 * @param axis may be either View.X_AXIS or View.Y_AXIS
	 * @return   the span the view would like to be rendered into.
	 *           Typically the view is told to render into the span
	 *           that is returned, although there is no guarantee.  
	 *           The parent may choose to resize or break the view.
	 * @see View#getPreferredSpan
	 */
        public float getPreferredSpan(int axis) {	//TODO maybe cache these values
	    float maxpref = 0;
	    float pref = 0;
	    int n = getViewCount();
	    for (int i = 0; i < n; i++) {
		View v = getView(i);
		pref += v.getPreferredSpan(axis);
		if (v.getBreakWeight(axis, 0, Integer.MAX_VALUE) >= ForcedBreakWeight) {
		    maxpref = Math.max(maxpref, pref);
		    pref = 0;
		}
	    }
	    maxpref = Math.max(maxpref, pref);
	    return maxpref;
	}

	/**
	 * Determines the minimum span for this view along an
	 * axis.  The is implemented to find the minimum unbreakable
	 * span.
	 *
	 * @param axis may be either View.X_AXIS or View.Y_AXIS
	 * @return  the span the view would like to be rendered into.
	 *           Typically the view is told to render into the span
	 *           that is returned, although there is no guarantee.  
	 *           The parent may choose to resize or break the view.
	 * @see View#getPreferredSpan
	 */
        public float getMinimumSpan(int axis) {	//TODO maybe cache these values
	    float maxmin = 0;
	    float min = 0;
	    boolean nowrap = false;
	    int n = getViewCount();
	    for (int i = 0; i < n; i++) {
		View v = getView(i);
		if (v.getBreakWeight(axis, 0, Integer.MAX_VALUE) == BadBreakWeight) {
		    min += v.getPreferredSpan(axis);
		    nowrap = true;
		} else if (nowrap) {
		    maxmin = Math.max(min, maxmin);
		    nowrap = false;
		    min = 0;
		}
	    }
	    maxmin = Math.max(maxmin, min);
	    return maxmin;
	}

	//TODO recomment dummy methods

	/**
	 * Renders using the given rendering surface and area on that
	 * surface.  This is implemented to do nothing, the logical
	 * view is never visible.
	 *
	 * @param g the rendering surface to use
	 * @param allocation the allocated region to render into
	 * @see View#paint
	 */
        public void paint(Graphics g, Shape allocation) {
	}

	/**
	 * Tests whether a point lies before the rectangle range.
	 * Implemented to return false, as hit detection is not
	 * performed on the logical view.
	 *
	 * @param x the X coordinate >= 0
	 * @param y the Y coordinate >= 0
	 * @param alloc the rectangle
	 * @return true if the point is before the specified range
	 */
        protected boolean isBefore(int x, int y, Rectangle alloc) {
	    return false;
	}

	/**
	 * Tests whether a point lies after the rectangle range.
	 * Implemented to return false, as hit detection is not
	 * performed on the logical view.
	 *
	 * @param x the X coordinate >= 0
	 * @param y the Y coordinate >= 0
	 * @param alloc the rectangle
	 * @return true if the point is after the specified range
	 */
        protected boolean isAfter(int x, int y, Rectangle alloc) {
	    return false;
	}

	/**
	 * Fetches the child view at the given point.
	 * Implemented to return null, as hit detection is not
	 * performed on the logical view.
	 *
	 * @param x the X coordinate >= 0
	 * @param y the Y coordinate >= 0
	 * @param alloc the parent's allocation on entry, which should
	 *   be changed to the child's allocation on exit
	 * @return the child view
	 */
        protected View getViewAtPoint(int x, int y, Rectangle alloc) {
	    return null;
	}

	/**
	 * Returns the allocation for a given child.
	 * Implemented to do nothing, as the logical view doesn't
	 * perform layout on the children.
	 *
	 * @param index the index of the child, >= 0 && < getViewCount()
	 * @param a  the allocation to the interior of the box on entry, 
	 *   and the allocation of the child view at the index on exit.
	 */
        protected void childAllocation(int index, Rectangle a) {
	}
}

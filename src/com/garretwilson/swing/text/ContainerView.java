package com.garretwilson.swing.text;

import javax.swing.text.*;

/**A view that contains other views, constrained in a box.
No assumptions are made about the order, size, or offsets of the child views, unlike <code>BoxView</code>.
This class consequently determines view index based upon document position without relying on underlying
element order and offsets.
Beginning and ending offsets are determined based upon the contained views.
@author Garret Wilson
*/
public class ContainerView extends BoxView
{

	/**Shared constant empty array of views.*/
	protected final static View[] NO_VIEWS=new View[0];

	/**<code>true</code> if the cache is based upon recent information, even though there may not be a cached value available.*/
	private boolean cacheValid=false;

	/**@return <code>true</code> if the cache is based upon recent information, even though there may not be a cached value available.*/
	protected boolean isCacheValid() {return cacheValid;}

	/**Invalidates the cached values so that they can be recalculated when needed.*/
	protected void invalidateCache() {cacheValid=false;}

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
		super(element, axis);	//construct the parent class
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
	@param pos The position (>=0) in the model.
	@return The index of the view representing the given position, or -1 if there
		is no view on this container which represents that position.
	*/
	protected int getViewIndexAtPosition(final int pos)
	{
		if(pos>=getStartOffset() && pos<getEndOffset())	//if the position is within the content this view is responsible for (this will save us time if the request is for content outside of this view)
		{
			for(int viewIndex=getViewCount()-1; viewIndex>=0; --viewIndex)	//look at all the views from the last to the first
			{
				final View childView=getView(viewIndex);	//get a reference to this child view
				if(pos>=childView.getStartOffset() && pos<childView.getEndOffset())	//if this child view holds the requested position
					return viewIndex;	//return the index to this child view
			}
		}
		return -1;	//if we make it to this point, we haven't been able to find a view with the specified position
	}

}

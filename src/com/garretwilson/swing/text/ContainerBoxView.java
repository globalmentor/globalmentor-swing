package com.garretwilson.swing.text;

import java.util.*;

import javax.swing.text.*;
import static com.garretwilson.swing.text.ViewUtilities.*;
import com.garretwilson.util.Debug;

/**A view that contains other views, constrained in a box.
No assumptions are made about the order, size, or offsets of the child views, unlike <code>BoxView</code>.
This class consequently determines view index based upon document position without relying on underlying
element order and offsets.
Beginning and ending offsets are determined based upon the contained views.
This view knows how to break into fragments along the tiling axis.
@author Garret Wilson
*/
public class ContainerBoxView extends BoxView
{

	/**The shared default break strategy for container views.*/
	public final static ViewBreakStrategy DEFAULT_BREAK_STRATEGY=new ContainerBreakStrategy();

	/**The stategy for breaking this view into fragments.*/
	private ViewBreakStrategy breakStrategy=DEFAULT_BREAK_STRATEGY;

		/**@return The stategy for breaking this view into fragments.*/
		protected ViewBreakStrategy getBreakStrategy() {return breakStrategy;}

		/**Sets the stategy for breaking this view into fragments.
		@param strategy The strategy to use for creating view fragments.
		*/
		protected void setBreakStrategy(final ViewBreakStrategy strategy) {breakStrategy=strategy;}

	/**Shared constant empty array of views.*/
	protected final static View[] NO_VIEWS=new View[0];

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
	public ContainerBoxView(final Element element, final int axis)
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
		return ContainerView.getViewIndexAtPosition(this, pos);	//delegate to the utility method
	}

	/**A break strategy for views that contain other views and may be parceled up into fragments.
	If a view and its fragment both support managed components, this strategy transfers managed components to the fragment.
	@author Garret Wilson
	*/
	public static class ContainerBreakStrategy implements ViewBreakStrategy
	{
		/**Breaks a view on the given axis at the given length. This is implemented
			to attempt to break on the largest number of child views that can fit within
			the given length.
		@param view The view to break.
		@param axis The axis to break along, either <code>View.X_AXIS</code> or <code>View.Y_AXIS</code>.
		@param offset The location in the model where the fragment should start its representation (>=0).
		@param pos the position along the axis that the broken view would occupy (>=0).
		@param length The distance along the axis where a potential break is desired (>=0).
		@param fragmentViewFactory The source of fragment views.
		@return The fragment of the view that represents the given span, if the view
			can be broken. If the view doesn't support breaking behavior, the view itself
			is returned.
		@see View#breakView
		*/
		public View breakView(final BoxView view, final int axis, final int offset, final float pos, final float length, final FragmentViewFactory fragmentViewFactory)
		{
	//G***del Debug.trace("Inside XMLBlockView.breakView axis: ", axis, "p0:", p0, "pos:", pos, "len:", len, "name:", XMLStyleUtilities.getXMLElementName(getAttributes()));	//G***del
			if(axis==view.getAxis() && length<view.getPreferredSpan(axis))	//if they want to break along our tiling axis and they want less of us than we prefer, we'll try to break
			{
			  final int childViewCount=view.getViewCount();  //get the number of child views we have
				if(childViewCount>0)	//if we have child views
				{
					final List<View> childViewList=new ArrayList<View>();	//create a new list for accumulating child views
					final boolean isViewFragment=view instanceof FragmentView;	//see if the view we are breaking is itself a fragment
					final boolean viewRepresentsFirst=!isViewFragment || ((FragmentView)view).isFirstFragment();	//see if the view represents the first fragment of the original view (or the view is the original view)
					final boolean viewRepresentsLast=!isViewFragment || ((FragmentView)view).isLastFragment();	//see if the view represents the last fragment of the original view (or the view is the original view)
					boolean isFirstFragment=false;  //start out assuming this is not the first fragment
					boolean isLastFragment=false;  //start out assuming this is not the last fragment
					float totalSpan=0;	//we'll use this to accumulate the size of each view to be included
					int startOffset=offset;	//we'll continually update this as we create new child view fragments
					int childIndex;	//start looking at the first child to find one that can be included in our break
					for(childIndex=0; childIndex<childViewCount && view.getView(childIndex).getEndOffset()<=startOffset; ++childIndex);	//find the first child that ends after our first model location
					for(; childIndex<childViewCount && totalSpan<length; ++childIndex)	//look at each child view at and including the first child we found that will go inside this fragment, and keep adding children until we find enough views to fill up the space or we run out of views
					{
						final View childView=view.getView(childIndex);	//get a reference to this child view; we may change this variable if we have to break one of the child views
						final float childPreferredSpan=childView.getPreferredSpan(axis);	//get the child's preferred span along the axis
	Debug.trace("looking to include child view: ", childView.getClass().getName()); //G***del
	Debug.trace("child view preferred span: ", childPreferredSpan); //G***del
						final float remainingSpan=length-totalSpan;	//calculate the span we have left
						final View newChildView;	//we'll determine which child to add
						final float newChildPreferredSpan;	//we'll determine the size of the new child
						if(childPreferredSpan>remainingSpan)	//if this view is too big to fit into our space
						{
	//G***old comment: we really only need to add something if this is the root XMLBlockView -- but how do we know that?
							final boolean mustBreak=childViewList.size()==0;	//if we haven't fit any views into our fragment, we must have at least one, even if it doesn't fit
							final boolean canBreak=mustBreak || childView.getBreakWeight(axis, pos, remainingSpan)>BadBreakWeight;	//see if this view can be broken to be made to fit
							if(canBreak)	//if we can break
							{
								newChildView=childView.breakView(axis, Math.max(childView.getStartOffset(), startOffset), pos, remainingSpan);	//break the view to fit, taking into account that the view might have started before our break point
								newChildPreferredSpan=newChildView.getPreferredSpan(axis);	//get the new child's preferred span along the axis
								if(!mustBreak && newChildPreferredSpan>remainingSpan)	//if this view is still too big to fit into our space, and we don't have to break
								{
									break;	//stop trying to fit things
								}
							}
							else	//if this view can't break and we're not required to break
							{
								break;	//stop trying to fit things
							}
						}
						else	//if we can take the whole view
						{
							newChildView=childView;	//take the whole view as it is
							newChildPreferredSpan=childPreferredSpan;	//the view prefers as much as it originally did
						}
						childViewList.add(newChildView);	//add the new child view, may could have been chopped up into a fragment itself
						totalSpan+=newChildPreferredSpan;	//show that we've used up more space
						if(viewRepresentsFirst && childIndex==0)	//if we just added the first child and we would know if it really is the first
						{
							isFirstFragment=true;	//show that the new fragment will be the first fragment
						}
						if(viewRepresentsLast && childIndex==childViewCount-1)	//if we just added the last child and we would know if it really is the last
						{
							isLastFragment=true;	//show that the new fragment will be the last fragment
						}
					  if(newChildView!=childView) //if we were not able to return the whole view
					  {
						  break;  //stop trying to add more child views; more views may fit (especially hidden views), but they would be skipping content that we've already lost by breaking this child view
					  }
					}
					final View fragmentView=createFragmentView(fragmentViewFactory, view, childViewList.toArray(new View[childViewList.size()]), isFirstFragment, isLastFragment);	//create a fragment view with the collected children
					try	//TODO later modify this code to see if the component locations fall within our span
					{
						if(fragmentView instanceof ViewComponentManageable && view instanceof ViewComponentManageable)	//if the original view and the fragment both support managed components
						{
							final ViewComponentManager componentManager=((ViewComponentManageable)view).getComponentManager();	//get the original view's component manager
							final ViewComponentManager fragmentComponentManager=((ViewComponentManageable)fragmentView).getComponentManager();	//get the fragment view's component manager
							for(final ViewComponentManager.ComponentInfo componentInfo:componentManager.getComponentInfos())	//for each component information
							{
								final boolean transferComponent;	//we'll determine whether we should transfer this component
								final ViewComponentManager.Border border=componentInfo.getBorder();	//get the component border
								if(border==ViewComponentManager.Border.SOUTH || border==ViewComponentManager.Border.PAGE_END)	//if this component is at the bottom TODO update to match orientation
								{
									transferComponent=isLastFragment;	//only include bottom components in the last fragment
								}
								else	//for all other components
								{
									transferComponent=isFirstFragment;	//include all non-bottom components only in the first fragment
								}
								if(transferComponent)	//if we should transfer this component
								{
									fragmentComponentManager.add((ViewComponentManager.ComponentInfo)componentInfo.clone());	//add a clone of the component information								
								}
							}					
						}
					}
					catch(final CloneNotSupportedException cloneNotSupportedException)	//cloning should always be supported for the objects we use
					{
						throw new AssertionError(cloneNotSupportedException);
					}
					return fragmentView;	//return the fragment view we created
				}
			}
			return view;	//if they want to break along another axis or we weren't able to break, return our entire view
		}

		/**Creates a view that represents a portion of the element.
	 	If the view doesn't support fragmenting, the view itself will be returned.
		This implementation returns a new view that contains the required child views.
		@param view The view to break.
		@param p0 The starting offset (>=0). This should be a value greater or equal
			to the element starting offset and less than the element ending offset.
		@param p1 The ending offset (>p0).  This should be a value less than or
			equal to the elements end offset and greater than the elements starting offset.
		@param fragmentViewFactory The source of fragment views.
		@return The view fragment, or the view itself if the view doesn't support breaking into fragments.
		@see View#createFragment
		*/
		public View createFragment(final View view, int p0, int p1, final FragmentViewFactory fragmentViewFactory)
		{
			if(p0<=view.getStartOffset() && p1>=view.getEndOffset())	//if the range they want encompasses all of our view
				return view;	//return the whole view; there's no use to try to break it up
			else	//if the range they want only includes part of our view
			{
				final List<View> childViewList=new ArrayList<View>();	//create a new list for accumulating child views
				final boolean isViewFragment=view instanceof FragmentView;	//see if the view we are breaking is itself a fragment
				final boolean viewRepresentsFirst=!isViewFragment || ((FragmentView)view).isFirstFragment();	//see if the view represents the first fragment of the original view (or the view is the original view)
				final boolean viewRepresentsLast=!isViewFragment || ((FragmentView)view).isLastFragment();	//see if the view represents the last fragment of the original view (or the view is the original view)
				final int childViewCount=view.getViewCount();  //find out how many child views there are
					//see if we'll include the first child in any form; if so, we're the first fragment
			  final boolean isFirstFragment=viewRepresentsFirst && childViewCount>0 && p0<=view.getView(0).getStartOffset();
					//see if we'll include the last child in any form; if so, we're the last fragment
			  final boolean isLastFragment=viewRepresentsLast && childViewCount>0 && p1>=view.getView(childViewCount-1).getEndOffset();
				for(int i=0; i<childViewCount; ++i)	//look at each child view
				{
					final View childView=view.getView(i);	//get a reference to this child view
					final int childViewStartOffset=childView.getStartOffset();	//get the child view's starting offset
					final int childViewEndOffset=childView.getEndOffset();	//get the child view's ending offset
					if(childViewEndOffset>p0 && childViewStartOffset<p1)	//if this view is within our range
					{
						final int startPos=Math.max(p0, childViewStartOffset);	//find out where we want to start, staying within this child view
						final int endPos=Math.min(p1, childViewEndOffset);	//find out where we want to end, staying within this child view
						childViewList.add(childView.createFragment(startPos, endPos));	//add a portion (or all) of this child to our list of views
					}
				}
				final View fragmentView=createFragmentView(fragmentViewFactory, view, childViewList.toArray(new View[childViewList.size()]), isFirstFragment, isLastFragment);	//create a fragment view with the collected children
				try	//TODO later modify this code to check component locations
				{
					if(fragmentView instanceof ViewComponentManageable && view instanceof ViewComponentManageable)	//if the original view and the fragment both support managed components
					{
						final ViewComponentManager componentManager=((ViewComponentManageable)view).getComponentManager();	//get the original view's component manager
						final ViewComponentManager fragmentComponentManager=((ViewComponentManageable)fragmentView).getComponentManager();	//get the fragment view's component manager
						for(final ViewComponentManager.ComponentInfo componentInfo:componentManager.getComponentInfos())	//for each component information
						{
							final boolean transferComponent;	//we'll determine whether we should transfer this component
							final ViewComponentManager.Border border=componentInfo.getBorder();	//get the component border
							if(border==ViewComponentManager.Border.SOUTH || border==ViewComponentManager.Border.PAGE_END)	//if this component is at the bottom TODO update to match orientation
							{
								transferComponent=isLastFragment;	//only include bottom components in the last fragment
							}
							else	//for all other components
							{
								transferComponent=isFirstFragment;	//include all non-bottom components only in the first fragment
							}
							if(transferComponent)	//if we should transfer this component
							{
								fragmentComponentManager.add((ViewComponentManager.ComponentInfo)componentInfo.clone());	//add a clone of the component information								
							}
						}					
					}
				}
				catch(final CloneNotSupportedException cloneNotSupportedException)	//cloning should always be supported for the objects we use
				{
					throw new AssertionError(cloneNotSupportedException);
				}
				return fragmentView;	//return the fragment view we created
			}
		}

		/**Creates a fragment view into which pieces of this view will be placed.
		The fragment view will be given the correct parent.
		@param fragmentViewFactory The source of fragment views.
		@param view The view to break.
		@param childViews the child views to include in the fragment
		@param isFirstFragment Whether this fragment holds the first part of the
			original view.
		@param isLastFragment Whether this fragment holds the last part of the
			original view.
		*/
		protected View createFragmentView(final FragmentViewFactory fragmentViewFactory, final View parentView, final View[] childViews, final boolean isFirstFragment, final boolean isLastFragment)
		{
		  final View fragmentView=fragmentViewFactory.createFragmentView(isFirstFragment, isLastFragment);	//create a fragment of the view
		  fragmentView.setParent(parentView);	//give the fragment the correct parent
		  fragmentView.replace(0, fragmentView.getViewCount(), childViews);	//add the child views to the fragment
				//TODO reparenting when fragmenting seems to fix the problems that have been encountered, but what other break strategies? what about unfragmented views whose children have been fragmented and then unfragmented, yet the parent views never see this method so they can never have their hierarchies reparented?
			reparentHierarchy(fragmentView);	//reparent the entire hierarchy of the fragment view, because some of the children's children may have been fragmented and then unfragmented
		  return fragmentView;	//return the fragment view we created
		}

	}
}

package com.garretwilson.swing.text.xml;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.lang.ref.*;
import java.util.*;
import javax.swing.SwingUtilities;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.SizeRequirements;
import com.garretwilson.io.MediaType;
import com.garretwilson.lang.JavaUtilities;
import com.garretwilson.swing.XMLTextPane;	//G***del when we can find a better place to set the paged view variable of XMLTextPane
import com.garretwilson.swing.event.PageEvent;
import com.garretwilson.swing.event.PageListener;
import com.garretwilson.swing.event.ProgressEvent;
import com.garretwilson.swing.event.ProgressListener;
import com.garretwilson.swing.text.AnonymousElement;
//G***del if not needed import com.garretwilson.swing.text.ViewHidable;
import com.garretwilson.swing.text.ViewReleasable;
import com.garretwilson.swing.text.ViewUtilities;
import com.garretwilson.swing.text.xml.css.XMLCSSStyleUtilities;
import com.garretwilson.swing.text.xml.xhtml.XHTMLSwingTextUtilities;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSConstants; //G***maybe make everything that comes in through the XMLCSSStyleConstants class be agnostic of CSS, and remove the CSS-specific style strings
import com.garretwilson.text.xml.oeb.OEBConstants;
import com.garretwilson.text.xml.xhtml.XHTMLConstants;
//G***del; not used import com.garretwilson.util.ArrayUtilities;
import com.garretwilson.util.Debug;

/**View of several pages.
	<p>Although this class overrides <code>FlowView</code>, much of the layout is
	actually a replication of code in <code>BoxView</code>, which is necessary
	because the latter hides a lot of layout-related variable, which we need
	to update since we extend some of the functionality of <code>BoxView</code>.</p>
	<p>This class, before hiding a page of views, informs each view that implements
	<code>ViewHidable</code> that it is about to be hidden.</p>
@see com.garretwilson.swing.text.ViewHidable
@see FlowView
*/
public class XMLPagedView extends XMLFlowView
{

	/**The task of paginating a document.*/
	public final static String PAGINATE_TASK="PAGINATE";

	/**The list of page event listeners.*/
	private EventListenerList pageListenerList=new EventListenerList();

	/**The list of progress event listeners.*/
	private EventListenerList progressListenerList=new EventListenerList();

	/**The font used for painting page number.*/
	private final static Font PAGE_NUMBER_FONT=new Font("Serif", Font.PLAIN, 12); //G***fix

	/**Whether or not the flowing of this view is threaded.*/
	private boolean threaded=true;	//(newswing threadlayout)

		/**@return Whether the view flowing should be threaded.*/
		public boolean isThreaded() {return threaded;}	//(newswing threadlayout)

		/**Sets whether the flowing of this view should be threaded.
		@param newThreaded <code>true</code> if the view flowing should occur in a
			separate thread.
		*/
		public void setThreaded(final boolean newThreaded) {threaded=newThreaded;}	//(newswing threadlayout)

	/**The width used the last time layout began.*/
	protected int lastLayoutWidth=-1;	//(newswing threadlayout)

	/**The height used the last time layout began.*/
	protected int lastLayoutHeight=-1;	//(newswing threadlayout)

	/**The thread used to create the flow, if threading is used.*/
	protected Thread layoutStrategyThread=null;	//(newswing threadlayout)

	/**The cached information about our layout.*/
	protected PageFlowLayoutInfo flowLayoutInfo;

	/**Constructor specifying an element.
	@param element The element this view is responsible for.
	*/
	public XMLPagedView(Element element)
	{
		super(element, View.X_AXIS);	//construct the parent, showing that we're tiled along the X axis (but flowing vertically)
//G***del if we don't need		setPropertiesFromAttributes();	//set our properties from the attributes
		//G***fix setting flow stategy if we need to
//G***fix threadlayout		strategy=new PageFlowStrategy();	//G***testing
		setThreaded(false);	//make this view threaded by default (newswing threadlayout)
//G***bring back		setThreaded(true);	//make this view threaded by default (newswing threadlayout)
		strategy=new PageFlowStrategy(this);	//create a flow strategy which may or may not be used in a threaded way (newswing threadlayout)
//G***del		strategy=null;	//the strategy will be created each time it is needed, whether or not threading is used (newswing threadlayout)
/*G***del
		setXAllocationValid(false);	//show that we need to update the X axis, since we're just getting created
		setYAllocationValid(false);	//show that we need to update the Y axis, since we're just getting created
*/
//G***fix		setInsets((short)25, (short)25, (short)25, (short)25);	//G***fix; testing
	}


	/*G***fix
A copy of our...
	private Graphics paintGraphics=null;
	private Shape paintAllocation=null;
*/

	/**A temporary rectangle object used for painting.*/
	protected Rectangle TempRectangle=new Rectangle();

	/**@return The number of pages this view contains.*/
	public int getPageCount()
	{
//G***fix		return getViewCount();  //return the number of child views we have
//G***del Debug.trace("getPageCount(): ", flowLayoutInfo!=null ? flowLayoutInfo.getLength() : -50);
		  //return the number of pages we've paginated or, if we have no layout info, return zero
		return flowLayoutInfo!=null ? flowLayoutInfo.getLength() : 0;
	}

	/**The current index we're showing, or -1 for no page.*/
	private int PageIndex=0;

	/**@return The current index we're showing, or -1 for no page.*/
	public int getPageIndex() {return getPageCount()>0 ? PageIndex : -1;}	//G***decide how we want to do all this

	/**Sets the new current page index. If necessary, the page index is modified
		so that the displayed page is within the page range and that the page index
		represents the first of any set of displayed pages.
	@param newPageIndex The index of the page which should be shown.
	@see #getPageIndex
	@see #getPageCount
	@see #getDisplayPageCount
	*/
	public void setPageIndex(int newPageIndex)
	{
		if(getPageCount()>0)	//if we have pages
		{
			if(newPageIndex<0)		//if they want to set the page number to less than we have
				newPageIndex=0;	//we'll go to the first page
			else if(newPageIndex>getPageCount()-1)	//if they specified too high of a page number
				newPageIndex=getPageCount()-1;	//we'll go to the last page
			final int displayPageCount=getDisplayPageCount(); //get the number of pages being displayed
			newPageIndex=(newPageIndex/displayPageCount)*displayPageCount;	//make sure the page index is on the first of any page sets
		}
		else	//if we don't have any pages
			newPageIndex=-1;	//we'll have to set the page index to -1
		if(PageIndex!=newPageIndex)	//if we're really changing the page number
		{
//G***del Debug.trace("Changing from page: "+PageIndex+" to page "+newPageIndex+" pageCount: "+getPageCount()); //G***del
				//first, tell all the views on the current pages they are about to be hidden
			if(PageIndex!=-1 && getPageCount()>0) //if there is a page already being displayed, and we have at least one page
			{
				final int pageBeginIndex=PageIndex;	//see which page we're showing first
				final int pageEndIndex=pageBeginIndex+getDisplayPageCount();	//see which page we're showing last (actually, this is the page right *after* the page we're showing) G***what if
				final int pageCount=getPageCount();	//see how many pages we have
				for(int i=pageBeginIndex; i<pageEndIndex; ++i)	//look at each page to paint
				{
					if(isLaidOut(i))	//if this page has been laid out (this function works for threading and non-threading situations)
					{
//G***del	public int getChildIndex(final int pageIndex)
						final int childIndex=getPageChildIndex(i);  //get the index of the child view that represents this page
						if(childIndex>=0) //if we found a child view that represents this page
						{
							final View pageView=getView(childIndex); //get a reference to this view
							ViewUtilities.hideView(pageView); //tell the view that it is being hidden
						}
					}
				}
			}
			firePageEvent(new PageEvent(this, newPageIndex, getPageCount())); //fire a page event with our new page number
			PageIndex=newPageIndex;	//actually change the page number
	//G***probably repaint in a separate thread
			final Container container=getContainer();	//get a reference to our container
			if(container!=null)	//if we're in a container
				container.repaint();	//repaint our container G***check
		}
/*G***del when works; why wasn't this inside the block for when the page number *really* changes?
		firePageEvent(new PageEvent(this, newPageIndex, getPageCount())); //fire a page event with our new page number
		PageIndex=newPageIndex;	//actually change the page number
//G***probably repaint in a separate thread
		final Container container=getContainer();	//get a reference to our container
		if(container!=null)	//if we're in a container
			container.repaint();	//repaint our container G***check
*/
	}

	/**The number of pages to display at a time.*/
	private int DisplayPageCount=1;

		/**@return The number of pages to display at a time.*/
		public int getDisplayPageCount() {return DisplayPageCount;}

		/**Sets the number of pages to display at a time.
		@param displayPageCount The new number of pages to display at a time.
		*/
		public void setDisplayPageCount(final int displayPageCount)
		{
			if(getDisplayPageCount()!=displayPageCount)	//if our page count is really changing
			{
				DisplayPageCount=displayPageCount;	//set the new display page count
				layoutChanged(getAxis());	//show that our layout has changed along this axis

//G***probably repaint in the even thread
				final Container container=getContainer();	//get a reference to our container
				if(container!=null)	//if we're in a container
					container.repaint();	//repaint our container
			}
		}

	/**Advances to the next page(s), if one is available, correctly taking into
		account the number of pages displayed.
	*/
	public void goNextPage()
	{
//G***del System.out.println("XMLPagedView.goNextPage(), pageIndex: "+getPageIndex()+" pageCount: "+getPageCount());	//G***del
		final int nextPageIndex=getPageIndex()+getDisplayPageCount();	//see what our next page index would be
		if(nextPageIndex>=0 && nextPageIndex<getPageCount())	//if going to the next page would give us a valid index
			setPageIndex(nextPageIndex);	//set the new page index
	}

	/**Changes to the previous page(s), if one is available, correctly taking into
		account the number of pages displayed.
	*/
	public void goPreviousPage()
	{
		final int previousPageIndex=getPageIndex()-getDisplayPageCount();	//see what our next page index would be
		if(previousPageIndex>=0 && previousPageIndex<getPageCount())	//if going to the previous page would give us a valid index
			setPageIndex(previousPageIndex);	//set the new page index
	}

	/**The width of each page.*/
	private float PageWidth;

		/**@return The width of each page.*/
		public float getPageWidth() {return PageWidth;}

		/**Sets the width of each page.
		@param pageWidth The new page width.
		*/
		protected void setPageWidth(final float pageWidth) {PageWidth=pageWidth;}

	/**The height of each page.*/
	private float PageHeight;

		/**@return The height of each page.*/
		public float getPageHeight() {return PageHeight;}

		/**Sets the height of each page.
		@param pageHeight The new page height.
		*/
		protected void setPageHeight(final float pageHeight) {PageHeight=pageHeight;}



	//G***comment
	//G***comment or -1 if...
	public int getPageIndex(final int pos)
	{
		return flowLayoutInfo.getFlowSegmentIndex(pos); //use our calculated offsets to find which page this position represents
/*G***del
		final int viewCount=getViewCount();	//find out how many child views we have
		for(int i=0; i<viewCount; ++i)		//look at each child view
		{
			final View childView=getView(i);	//G***testing
//G***del			final Element childElement=childView.getElement();	//G***testing
//G***del Debug.trace("Child view "+i+" is "+childView.getClass().getName());	//G***del
//G***del Debug.trace("For page: "+i+" the start offset is: "+childView.getStartOffset()+" end offset is: "+childView.getEndOffset());
			if(pos>=childView.getStartOffset() && pos<childView.getEndOffset())	//G***testing
				return i;
		}
		return -1;	//G***testing
*/
	}

	//G***fix this with the correct modelToView() stuff
	public int getPageStartOffset(final int pageIndex)
	{
		return isLaidOut(pageIndex) ? flowLayoutInfo.getStartOffset(pageIndex) : -1;
/*G***del
		final View childView=getView(pageIndex);	//get the view for the given page
		return childView!=null ? childView.getStartOffset() : -1;  //return the start offset of the page, or -1 if there is no such page G***testing
*/
	}

	//G***fix this with the correct modelToView() stuff
	public int getPageEndOffset(final int pageIndex)
	{
		return isLaidOut(pageIndex) ? flowLayoutInfo.getEndOffset(pageIndex) : -1;
/*G***del
		final View childView=getView(pageIndex);	//get the view for the given page
		return childView!=null ? childView.getEndOffset() : -1;  //return the start offset of the page, or -1 if there is no such page G***testing
*/
	}

	/**@return <code>true</code> if the specified page is one of the pages being
		displayed.
	@see #getPageIndex
	@see #getDisplayPageCount
	@see #getPageCount
	*/
	public boolean isPageShowing(final int pageIndex)
	{
		return pageIndex>=getPageIndex() && pageIndex<getPageIndex()+getDisplayPageCount() && pageIndex<getPageCount();
	}

	/**Returns the child index of a particular page. Each page may or may not
		have been prepaginated and added as a child view. If this page has, its
		child index will be returned, otherwise -1.
	@param pageIndex The logical index of this page.
	@return The child index of the page, if it has been paginated, otherwise -1.
	@exception IllegalArgumentException Thrown if the page layout information for
		the given page has not yet been determined.
	*/
	protected int getPageChildIndex(final int pageIndex)
	{
			//find out where this page begins in the model
		final int startOffset=flowLayoutInfo!=null ? flowLayoutInfo.getStartOffset(pageIndex) : 0;
//G***del		int childIndex=-1; //we'll find the child using this variable; assume for now that we won't find it
		final int childViewCount=getViewCount(); //find out how many child views we have
		for(int childViewIndex=0; childViewIndex<childViewCount; ++childViewIndex)  //look at each of our children to see if we've already paginated this view
		{
			final View childView=getView(childViewIndex); //get a reference to this view
			if(childView.getStartOffset()==startOffset) //if this view starts at the right place
				return childViewIndex;  //return the index of this child
		}
		return -1;  //show that there is no child view starting at the correct index
	}

	/**Retrieves a page by its index. The paged view is first checked to see if
		the page has already been paginated and placed as a child view. If not, the
		page is paginated and placed as a child view.
	@param The paginated page, cached as a child view of this view.
	*/
	protected Page getPage(final int pageIndex) //G***change Page to PageView
	{
/*G***del
		try
		{
*/
			int childIndex=getPageChildIndex(pageIndex);  //get the index of the child view that represents this page
			if(childIndex<0)  //if none of our children start at the correct location
			{
				final int startOffset=flowLayoutInfo.getStartOffset(pageIndex); //find out where this page begins in the model
				final int endOffset=flowLayoutInfo.getEndOffset(pageIndex); //find out where this page begins in the model
	//G***del Debug.trace("found ending offset: ", endOffset); //G***del
				final FlowRange flowRange=flowLayoutInfo.getFlowRange(startOffset);  //find the range that matches this offset
	//G***del Debug.trace("Found flow range: ", flowRange);
				Debug.assert(flowRange!=null, "No flow range for position "+startOffset);
					//get a page pool view from which to paginate
				final PagePoolView pagePoolView=getPagePoolView(flowRange.getStartOffset(), flowRange.getEndOffset());
	//G***del Debug.trace("retrieved page pool view with parent: ", pagePoolView.getParent()!=null ? pagePoolView.getParent().getClass().getName() : "null"); //G***del
	/*G***del
			for(int i=0; i<pagePoolView.getViewCount(); ++i) //G***del; testing
			{
				final View view=pagePoolView.getView(i);
	Debug.trace("pagePoolView Child view: "+i+" is: ", view.getClass().getName()); //G***del
	Debug.trace("pagePoolView Child view parent: ", view.getParent()!=null ? view.getParent().getClass().getName() : "null");

			}
	*/
				//dynamically create the specified view from the pool
				//note that we must use the ending offset for all the content we own, so that
				//  a page can retrieve all of its content instead of being cut off
				//  (perhaps the layout algorithm needs more examination to determine why)
				final View childView=((PageFlowStrategy)strategy).createRow(this, pagePoolView, startOffset, getEndOffset(), true); //G***fix cast
	//G***del Debug.trace("Created page view with end offset: ", childView.getEndOffset());  //G***del
//G***del 	Debug.trace("Created page view with start offset: ", childView.getStartOffset());  //G***del
//G***del 	Debug.trace("Created page view with parent: ", childView.getParent()!=null ? childView.getParent().getClass().getName() : "null"); //G***del
				childIndex=getViewCount()-1;  //the child to use is the one we just added
				if(getViewCount()>getDisplayPageCount()*3) //if we have too many pages cached as child views
					remove(0);  //remove the first one
			}
	/*G***del
			final View pagedView=(Page)getView(childIndex); //G***del; testing
			for(int i=0; i<pagedView.getViewCount(); ++i) //G***del; testing
			{
				final View view=pagedView.getView(i);
	Debug.trace("page Child view: "+i+" is: ", view.getClass().getName()); //G***del
	Debug.trace("page Child view parent: ", view.getParent()!=null ? view.getParent().getClass().getName() : "null");

			}
	*/
			return (Page)getView(childIndex); //the child index should now point to a child view that represents the page
/*G***del
		}
		catch(Throwable throwable)  //G***testing memory
		{
			Debug.error(throwable); //G***fix
			return null;
		}
*/
	}

	/**A list of references to page pool views.*/
	protected final java.util.List pagePoolViewReferenceList=new LinkedList();

	/**Returns a pooled view of page views that represents the given range. These
		page pool views are cached with soft references so that if a page pool has
		been created before, the same one will be returned again as long as its
		memory has not been reclaimed. If a pool with the given range has not yet
		been created, or if its memory has been reclaimed, a new one is created and
		cached.
	@param startOffset The beginning model position the pooled view should represent.
	@param endOffset The non-inclusive ending model position the pooled view
		should represent.
	*/
	protected	PagePoolView getPagePoolView(final int startOffset, final int endOffset)  //G***maybe synchronize on the list, later
	{
//G***del System.out.println("getting page pool view, reference list count: "+pagePoolViewReferenceList.size()); //G***del
		final Iterator iterator=pagePoolViewReferenceList.iterator(); //get an iterator to the page pool view references
		while(iterator.hasNext()) //while there are more page pool view references
		{
			final Reference pagePoolViewReference=(Reference)iterator.next(); //get the next page pool view reference
		  final PagePoolView pagePoolView=(PagePoolView)pagePoolViewReference.get();  //get the page pool view itself
		  if(pagePoolView!=null)  //if this page pool's memory has not been reclaimed
			{
					//if this page pool view matches the range that was given
				if(pagePoolView.getStartOffset()==startOffset && pagePoolView.getEndOffset()==endOffset)
					return pagePoolView;  //we've found one; return the page pool view
/*G***del
				else //G***del if(pagePoolViewReferenceList.size()>2) //G***testing; del
					iterator.remove();  //G***testing; del
*/
			}
			else  //if this page pool's memory has been reclaimed
			{
			  iterator.remove();  //remove the reference to the page pool
			}
		}
		  //if we can't find a matching page pool view, create a new one and add it to the cache
		final PagePoolView pagePoolView=new PagePoolView(getElement(), getFlowAxis(), startOffset, endOffset);  //create a new page pool view G***we may want to hard-code this to Y_AXIS later; this mistake has caused hours of debugging
		pagePoolView.setParent(this);  //show that we're the parent of this pooled view
		pagePoolView.setSize((int)getPageWidth(), (int)getPageHeight());	//make sure the layout pool has the correct dimensions of the page so that it will do unrestrained layout correctly
		pagePoolViewReferenceList.add(new SoftReference(pagePoolView)); //create a soft reference to this page pool view and add it to our cache
		return pagePoolView;  //return the page pool view we created
	}

	/**Removes all cached page pool views.*/
	protected void clearPagePoolViews()
	{
		pagePoolViewReferenceList.clear();  //remove all page pool view references from our list
	}

	/**If the view implements <code>ViewReleasable</code> it is notified that it
		is should release as much information as it can in order to free up memory.
		All child views of the view are notified as well, and so on down the
		hierarchy.
	*/
	protected void releaseView(final View view) //G***del if not needed; if keep, comment view param
	{
		if(view!=null)  //if we have a valid view
		{
			if(view instanceof ViewReleasable) //if this is a releasable view
			{
//G***del	Debug.trace("Inside releaseView(), found hidable: "+view);  //G**del
				((ViewReleasable)view).release(); //tell the view to release memory
			}
			final int viewCount=view.getViewCount();  //get the number of child views
			for(int i=0; i<viewCount; ++i)  //look at each child view
				releaseView(view.getView(i));  //tell this child view to release memory
		}
	}

	/**Removes one of the children at the given position. This is a convenience
		call to replace.
		Because this view uses its children as a type of cache for pages, removing
		a page view will set the entire view hierarchy of the removed view to
		<code>null</code>. This allows a newly created view to realize that some
		child views have been cross-parented to an old page view during layout, and
		to reparent then correctly.
	@param viewIndex The index of the view to remove.
	@see ViewUtilities.set
	@see View#remove
	@see #getPage
	*/
	public void remove(final int viewIndex)
	{
		final View view=getView(viewIndex); //get the view at the given index
		ViewUtilities.hideView(view); //tell the view that it is being hidden (this is important for applet views, for instance)
		super.remove(viewIndex);  //remove the view normally
//G***see if we really need this, now; will this help memory leaks? if so, why do we have to reparent everything, not just null parents?		ViewUtilities.setParentHierarchyNull(view); //set the entire parent hierarchy of the removed page to null
	}

	/*Invalidates the view and asks the container to repaint itself.
	This simply calls <code>XMLBlockView.relayout()</code>.
	@see XMLBlockView#relayout
	*/
/*G***del if not needed
	public void repaginate()
	{
		relayout();
	}
*/

	/**Invalidates the layout and asks the container to repaint itself. Because
		paged views keep caches of layout pools and, as children, cached views
		which may reference layout structures, all of these are first cleared.
	@see XMLBlockView#relayout
	*/
	public void relayout()
	{
		while(getViewCount()>0) //while there are more views
			remove(0);  //remove "cached" child views until there are no more
		clearPagePoolViews(); //clear all of our cached page pool views
		super.relayout(); //do the default relayout, which actually invalidates the views and repaints the component
	}

	/**Because a paged view merely uses its child views as cached pages, it is not
		interested in knowing if their preferences have changed. Furthermore,
		because we are the parent view of layout pool views, we should not invalid
		our layout just because a layout pool is being constructed. We therefore
		ignore all preference changes by our child views.
	@param child The child view.
	@param width <code>true</code> if the width preference should change.
	@param height <code>true</code> if the height preference should change.
	*/
	public void preferenceChanged(View child, boolean width, boolean height) {} //G***testing


	/* ***View methods*** */

	/**Because a paged view merely uses its child views as cached pages, it is not
		interested in knowing if have had updates. This method therefore does
		nothing.
	@param changes The event containing change information.
	@param area The area being changed.
	@param viewFactory The view factory responsible for creating views
	*/
//G***fix	public void insertUpdate(DocumentEvent changes, Shape area, ViewFactory viewFactory) {} //G***maybe later fix these to update appropriate cached layout pools

	/**Because a paged view merely uses its child views as cached pages, it is not
		interested in knowing if have had updates. This method therefore does
		nothing.
	@param changes The event containing change information.
	@param area The area being changed.
	@param viewFactory The view factory responsible for creating views
	*/
//G***fix	public void removeUpdate(DocumentEvent changes, Shape area, ViewFactory viewFactory) {}

	/**Because a paged view merely uses its child views as cached pages, it is not
		interested in knowing if have had updates. This method therefore does
		nothing.
	@param changes The event containing change information.
	@param area The area being changed.
	@param viewFactory The view factory responsible for creating views
	*/
//G***fix	public void changedUpdate(DocumentEvent changes, Shape a, ViewFactory f) {}

	/**Renders using the given rendering surface and area on that surface. This
		function only paints the currently selected page.
	@param g The rendering surface to use.
	@param allocation The allocated region to render into.
	@see View#paint
	@see BoxView#paint
	@see XMLPagedView.getPageCount
	@see XMLPagedView.getPageIndex
	@see #paintPage
	*/
	public void paint(Graphics g, Shape allocation)
	{
			//get a rectangle that outlines our allocation
		final Rectangle allocationRectangle=(allocation instanceof Rectangle) ? (Rectangle)allocation : allocation.getBounds();
//G***del Debug.trace("Before setSize()");
		setSize(allocationRectangle.width, allocationRectangle.height);	//make sure we're the correct size to cover the allocated area; this will reflow the contents if necessary
//G***del Debug.trace("After setSize()");
		final int displayPageCount=getDisplayPageCount();	//find out how many pages we should display at a time
		final int pageBeginIndex=getPageIndex();	//see which page we're showing first
		final int pageEndIndex=pageBeginIndex+displayPageCount;	//see which page we're showing last (actually, this is the page right *after* the page we're showing) G***what if
		final int pageCount=getPageCount();	//see how many pages we have
//G***del Debug.trace("pageBeginIndex: ", pageBeginIndex);
//G***del Debug.trace("pageEndIndex: ", pageBeginIndex);
//G***del Debug.trace("pageCount: ", pageCount);
//G***del Debug.error("In XMLPagedView.paint(), page count: "+pageCount);		  //G***del
/*G***del
		final int pageWidth=(int)getPageWidth();	//find out the width of each page
		final int pageHeight=(int)getPageHeight();	//find out the height of each page
*/
		TempRectangle.x=allocationRectangle.x+getLeftInset();	//find out where to start horizontally
		TempRectangle.y=allocationRectangle.y+getTopInset();	//find out where to start vertically
		TempRectangle.width=(int)getPageWidth();	//find out how wide to make each page
		TempRectangle.height=(int)getPageHeight();	//find out how hight to make each page
		final int xDelta=getAxis()==X_AXIS ? TempRectangle.width : 0;	//find out how much we should move each page horizontally
		final int yDelta=getAxis()==Y_AXIS ? TempRectangle.height : 0;	//find out how much we should move each page vertically
		final Rectangle clipRectangle=g.getClipBounds();	//find out the clipping bounds
		//paint each page
		for(int pageIndex=pageBeginIndex; pageIndex<pageEndIndex; ++pageIndex)	//look at each page to paint (although this may be more pages than we have to paint)
		{
			if(isLaidOut(pageIndex)/*G***del if we can && pageIndex>=0 && pageIndex<pageCount*/)	//if this page has been laid out (this function works for threading and non-threading situations) (newswing threadlayout)
			{
				if(TempRectangle.intersects(clipRectangle))	//if this area needs painted and this is a valid page
				{
					final Page pageView=getPage(pageIndex); //get a reference to this page, which will paginate it if needed
//G***del Debug.trace("Ready to paint page "+pageIndex+" with parent: ", pageView.getParent()!=null ? pageView.getParent().getClass().getName() : "null"); //G***del
				//G***move this line up so that only pages that need to get repainted
				  paintPage(g, TempRectangle, pageView, pageIndex); //paint this page
				}
//G***del					paintChild(g, TempRectangle, childIndex);	//paint this child
			}
			TempRectangle.x+=xDelta;	//advance to the next horizontal page position
			TempRectangle.y+=yDelta;	//advance to the next vertical page position
		}
		//paint the dividers and page numbers for each page G***probably put this in a separate function
			//G***maybe save these parameters and change back to them later
		g.setColor(Color.black);  //change to black for the divider
		g.setFont(PAGE_NUMBER_FONT);  //set the font for the page number
		TempRectangle.x=allocationRectangle.x+getLeftInset();	//reset the page rectangle
		TempRectangle.y=allocationRectangle.y+getTopInset();	//reset the page rectangle
		final Graphics2D graphics2D=(Graphics2D)g;  //cast to the 2D version of graphics
		final FontRenderContext fontRenderContext=graphics2D.getFontRenderContext();  //get the font rendering context
		for(int pageIndex=pageBeginIndex; pageIndex<pageEndIndex; ++pageIndex)	//look at each page (although this may be more pages than we have to paint)
		{
			if(pageIndex<pageEndIndex-1)	//if this isn't the last page, draw the vertical divider between pages
			{

					//G***fix this so that we draw the divider nicely
//G***shouldn't the first y argument not have "TempRectanglex+"?
//G***fix				g.drawLine(TempRectangle.x+TempRectangle.width, TempRectangle.x+TempRectangle.y, TempRectangle.x+TempRectangle.width, TempRectangle.y+TempRectangle.height);
				final int outerSpineHalfWidth=(int)Math.round(TempRectangle.width*0.025); //G***testing
				final int innerSpineHalfWidth=(int)Math.round(TempRectangle.width*0.003); //G***testing

				paintSpineSection(graphics2D, TempRectangle.x+TempRectangle.width, TempRectangle.y, TempRectangle.height, outerSpineHalfWidth, Color.lightGray, Color.white); //G***testnig
				paintSpineSection(graphics2D, TempRectangle.x+TempRectangle.width, TempRectangle.y, TempRectangle.height, -outerSpineHalfWidth, Color.lightGray, Color.white); //G***testnig
				paintSpineSection(graphics2D, TempRectangle.x+TempRectangle.width, TempRectangle.y, TempRectangle.height, innerSpineHalfWidth, Color.darkGray, Color.lightGray); //G***testnig
				paintSpineSection(graphics2D, TempRectangle.x+TempRectangle.width, TempRectangle.y, TempRectangle.height, -innerSpineHalfWidth, Color.darkGray, Color.lightGray); //G***testnig
/*G***del when works
				final GradientPaint gradientPaint=new GradientPaint(TempRectangle.x+TempRectangle.width, TempRectangle.y, Color.lightGray, TempRectangle.x+TempRectangle.width+5, TempRectangle.y, Color.white);  //G***testing
				graphics2D.setPaint(gradientPaint); //G***fix
				graphics2D.fillRect(TempRectangle.x+TempRectangle.width, TempRectangle.y, 5, TempRectangle.height);

				final GradientPaint gradientPaint2=new GradientPaint(TempRectangle.x+TempRectangle.width, TempRectangle.y, Color.lightGray, TempRectangle.x+TempRectangle.width-5, TempRectangle.y, Color.white);  //G***testing
				graphics2D.setPaint(gradientPaint2); //G***fix
				graphics2D.fillRect(TempRectangle.x+TempRectangle.width-5, TempRectangle.y, 5, TempRectangle.height);

				g.drawLine(TempRectangle.x+TempRectangle.width, TempRectangle.y, TempRectangle.x+TempRectangle.width, TempRectangle.y+TempRectangle.height);
*/


			}
			if(isLaidOut(pageIndex))	//if we actually have a page view for this page, and it has been laid out
			{
					//G***maybe put page number painting in each page; maybe not
				final Page pageView=getPage(pageIndex); //get a reference to this view G***rename to PageView sometime
				final int pageLeftInset=pageView.getPublicLeftInset();  //get the page's left inset
				final int pageRightInset=pageView.getPublicRightInset();  //get the page's right inset
				final int pageBottomInset=pageView.getPublicBottomInset();  //get the page's right inset
				final String pageNumberString=String.valueOf(pageIndex+1);  //create a string with the page number to paint G***use a getPageNumber() method instead
				final Rectangle2D pageNumberBounds=graphics2D.getFont().getStringBounds(pageNumberString, fontRenderContext); //get the bounds of the string
	//G***del Debug.trace("page number left inset: "+getLeftInset()+" right inset: "+getRightInset());  //G***del
				int pageNumberX;  //we'll determine which side of the page the number goes on
				if(pageIndex==pageEndIndex-1)  //if we're on the last page
					pageNumberX=TempRectangle.x+TempRectangle.width-pageRightInset+(int)((float)(pageRightInset-pageNumberBounds.getWidth())/2);	//G***fix; comment; use local variable
				else  //if we're not on the last page G***put code here for the middle of a three-page spread
					pageNumberX=TempRectangle.x+(int)((float)(pageLeftInset-pageNumberBounds.getWidth())/2);	//G***fix; comment; use local variable
				final int pageNumberY=TempRectangle.y+TempRectangle.height-pageBottomInset+(int)((float)(pageBottomInset-pageNumberBounds.getHeight())/2);	//G***fix; comment; use local variable
					//G***take into account the size of the font, make it a nicer color, etc.
				g.drawString(pageNumberString, pageNumberX, pageNumberY);	//G***testing; i18n
			}
			TempRectangle.x+=xDelta;	//advance to the next horizontal page position
			TempRectangle.y+=yDelta;	//advance to the next vertical page position
		}
		  //G***testing to see if paintChild() now messes up the allocation validity
				setXAllocValid(true);	//show that our horizontal allocation is valid again
				setYAllocValid(true);	//show that our vertical allocation is valid again

//G***del Debug.trace();
	}

	protected void paintSpineSection(final Graphics2D graphics2D, final int topX, final int topY, final int height, int horizontalDelta, final Color color1, final Color color2)
	{
		final Paint originalPaint=graphics2D.getPaint(); //get the current paint used
//G***del		final float startX=horizontalDelta>0 ? x1 : x1+horizontalDelta; //see where we should start
		final GradientPaint gradientPaint=new GradientPaint(topX, topY, color1, topX+horizontalDelta, topY, color2);  //G***testing
		graphics2D.setPaint(gradientPaint); //G***fix
		if(horizontalDelta>0) //if we have a positive delta
			graphics2D.fillRect(topX, topY, horizontalDelta, height); //G***fix; comment
		else  //if we have a negative delta
			graphics2D.fillRect(topX+horizontalDelta, topY, -horizontalDelta, height); //G***fix; comment
		graphics2D.setPaint(originalPaint); //set the paint back to its original paint
	}

	/**Paints a page.
	@param graphics The graphics context
	@param allocation The allocated region to paint into.
	@param pageView The that represents this page.
	@param pageIndex The logical index of the page, (>=0 && <getPageCount()).
	*/
	protected void paintPage(final Graphics graphics, final Rectangle allocation, final Page pageView, final int pageIndex)
	{
//G***del		final View childView=getView(index);  //G***comment
		pageView.paint(graphics, allocation);  //paint the page view
	}

	/**Whether we are currently in the midst of paginating the document.*/
	private boolean paginating=false;

	/**@return <code>true</code> if this view supports threading in some form and
		there the layout process is currently occurring.
	*/
	public boolean isPaginating()
	{
		return paginating;  //G***test; comment
/*G***del when works
		final Thread thread=layoutStrategyThread;	//get a reference to the layout strategy thread that we know won't change to null while we're checking it (unless, of course, it is already null)
		return isThreaded() && thread!=null && thread.isAlive();	//return if we thread and there is a layout thread currently running
*/
	}

	/**Sets whether pagination is occurring.
	@param newPaginating Whether pagination is occurring.
	*/
	protected void setPaginating(final boolean newPaginating)
	{
		paginating=newPaginating; //update the paginating variable
	}

	/**A paged view cannot be broken and therefore always starts where the element starts.
	@return The starting offset into the model (>0).
	@see View#getStartOffset
	*/
	public int getStartOffset()
	{
		return getElement().getStartOffset();	//return the start of the element
	}

	/**A paged view cannot be broken and therefore always ends where the element ends.
	@return The ending offset into the model (>0).
	@see View#getEndOffset
	*/
	public int getEndOffset()
	{
		return getElement().getEndOffset();	//return the end of the element
	}


	/* ***FlowView methods*** */

		/**
		 * Fetch the constraining span to flow against for
		 * the given child index.
		 */

	/*Returns the span of the view at the given child index, minus any insets.
	@index The index of the child view.
	@return The layout span minus this child's vertical insets.
	*/
	public int getFlowSpan(int index)
	{
		final View childView=getView(index);	//get a reference to the requested child view
		int adjustAmount=0;	//we'll assume we won't have to adjust the span any to account for insets
		if(childView instanceof Page)	//if this is a page (that's what we expect it to be)
		{
			final Page page=(Page)childView;	//cast the child view to a page
			if(getFlowAxis()==X_AXIS)	//if we're flowing horizontally
				adjustAmount=page.getPublicLeftInset()+page.getPublicRightInset();	//add the left and right insets together
			else	//if we're flowing vertically
				adjustAmount=page.getPublicTopInset()+page.getPublicBottomInset();	//add the top and bottom insets together
					//G***should we check for an invalid flow axis?
		}
//G***del System.out.println("XMLPagedView.getFlowSpan found an adjust amount of: "+adjustAmount);	//G***del
//G***fix		return super.getFlowSpan(index)-(adjustAmount*4)/*G***testing *4*/;	//return the default span, accounting for any insets
//G***fix		return layoutSpan-(adjustAmount*4)/*G***testing *4*/;	//return the default span, accounting for any insets
//G***del		return layoutSpan;	//G***fix

			//G***put this line in a lower view such as XMLBlockView
		return layoutSpan-adjustAmount;	//return the default span, accounting for any insets
	}

	/**Calculates the location along the flow axis at which the flow span will start,
		accounting for insets.
	@index The index of the child view.
	@return The starting location along the flow axis.
	*/
	public int getFlowStart(int index)
	{
		final View childView=getView(index);	//get a reference to the requested child
		int adjustAmount=0;	//we'll assume we won't have to adjust the span any to account for insets
		if(childView instanceof Page)	//if this is a page (that's what we expect it to be)
		{
			final Page page=(Page)childView;	//cast the child view to a page
			if(getFlowAxis()==X_AXIS)	//if we're flowing horizontally
				adjustAmount=page.getPublicLeftInset();	//compensate for the left inset
			else	//if we're flowing vertically
				adjustAmount=page.getPublicTopInset();	//compensate for the top inset
					//G***should we check for an invalid flow axis?
		}
//G***del System.out.println("XMLPagedView.getFlowStart found an adjust amount of: "+adjustAmount);	//G***del
//G***fix		return super.getFlowStart(index)+adjustAmount;	//return the default star of the flow, accounting for any inset
//G***fix		return adjustAmount;	//return the default star of the flow, accounting for any inset
//G***del		return 0;

			//G***put this line in a lower view such as XMLBlockView
		return adjustAmount;	//return the default star of the flow, accounting for any inset
	}

	/**Creates a view to hold a page-worth of children. This version of the
		function returns an XMLPagedView#Page
	@return The new page view.
	@see XMLPagedView#Page
	*/
	protected View createRow()
	{
		return new Page(getElement());	//create a new page based upon the same element we're based on
	}

	/* ***BoxView methods*** */

//G***fix getViewAtPoint()

//G***fix childAllocation()

	/**Sets the size of the view. This is overridden to support threading of the
		layout routines.
	@param width The new width (>=0).
	@param height The new height (>=0).
	*/
	public void setSize(float width, float height)	//(newswing threadlayout)
	{
//G***del Debug.trace("setSize(): width: "+width+", height: "+height+" from oldWidth:"+getWidth()+", oldHeight: "+getHeight());	//G***del
		if(/*G***del isThreaded() && */isPaginating())	//if we are currently laying out the view, either in a separate thread or periodically G***maybe take put the isThreaded() inside the block and decide how to restart paginating if we're simply repaginating later in the AWT thread
		{

//G***important; this needs fixed, because it now thinks things are threaded if pagination is going on

//G***del System.out.println("setSize(): layout already in progress");	//G***del
			if(lastLayoutWidth!=width || lastLayoutHeight!=height)	//if our width or height is actually changing, we'll want to start the threading process over
			{
//G***del System.out.println("setSize(): new layout parameters: width: "+width+", height: "+height+" from oldWidth:"+lastLayoutWidth+", oldHeight: "+lastLayoutHeight);	//G***del

//G***del				synchronized(this)	//G***test this!
				{
//G***del System.out.println("Setting stopLayout to true.");	//G***del
					((PageFlowStrategy)strategy).stopLayout=true;	//show that we want to stop the layout process
					try
					{
//G***del System.out.println("Thread "+Thread.currentThread().getName()+" joining layout thread: "+layoutStrategyThread.getName());	//G***del
						if(layoutStrategyThread!=null)  //if there is a thread currently laying out
						{
							layoutStrategyThread.join();	//wait until the layout thread has stopped
//G***del System.out.println("Joined layout thread.");	//G***del
							layoutStrategyThread=null;
						}
					}
					catch(InterruptedException e) 	//G***check this
					{
System.out.println(e);	//G***del
					}
//G***del					strategy=null;
				}
			}
		}
		else  //if we're not paginating
//G***del		if(!isThreaded() || layoutStrategyThread==null)	//if we don't support threading, or we aren't threading at the moment
			super.setSize(width, height);
	}

	/**Performs layout of the children. The size is the area inside of the insets.
		This version simply repaginates, creating new page views, and does not call
		the parent verions.
	@param width The width (>=0).
	@param height The height (>=0).
	@see XMLPagedLayout#Page
	*/
	protected void layout(int width, int height)
	{
		if(flowLayoutInfo==null)  //if we have no flow layout information G***find out why this isn't being created sometimes
		{
Debug.traceStack("no flowlayout"); //G***del

//G***fix			Debug.warn("flow layout information not created; this needs fixed");  //G***del
		  return; //G***fix; this means that pagination has started before children has been loaded; what to do?
		}
//G***del Debug.traceStack(); //G***del
//G***del Debug.trace("XMLPagedView.layout() width: "+width+" height: "+height);
		if(!isThreaded() || layoutStrategyThread==null)	//only do the layout if we don't support threading, or we aren't currently threading
		{
//G***del System.out.println("Inside layout() with width: "+width+" height: "+height);	//G***del
			final int flowAxis=getFlowAxis();	//get the flow axis
			final int axis=getAxis();	//get our tiling axis
			final int displayPageCount=getDisplayPageCount();	//see how many pages we're displaying
			if(axis==X_AXIS)	//if we're tiling on the X axis
			{
				setPageWidth(width/displayPageCount);	//show that the pages will be a fraction of our width
				setPageHeight(height);	//show that the pages will be the same height
			}
			else	//if we're tiling on the Y axis
			{
				setPageWidth(width);	//show that the widths will all be the same
				setPageHeight(height/displayPageCount);	//show that the pages will each be a fraction of the total height
			}
			final int newSpan=(flowAxis==X_AXIS) ? width : height;	//get our new span
					//G***del; testing
				layoutChanged(flowAxis);	//invalidate our flow axis
				layoutChanged(getAxis());	//invalidate our tiling axis G***perhaps we might want to change this to just invalidate X_AXIS and then Y_AXIS, so that we'll make sure we get them both

			if(layoutSpan!=newSpan)	//if our flow axis has changed
			{
				layoutChanged(flowAxis);	//invalidate our flow axis
				layoutChanged(getAxis());	//invalidate our tiling axis G***perhaps we might want to change this to just invalidate X_AXIS and then Y_AXIS, so that we'll make sure we get them both
				layoutSpan=newSpan;	//show that our span has changed
			//G***probably put an isAllocationValid() here
			}

	//G***testing
		// repair the flow if necessary
			if(!isPaginating() && !isAllocationValid()) //if we're not already paginating and the allocation is no longer valid
			{
				setPaginating(true);  //show that we're now paginating
				pagePoolViewReferenceList.clear();  //remove all our cached page pool views, since they will be invalid
				flowLayoutInfo.setLength(0);  //show that we haven't laid out the flow information, yet
				removeAll();  //remove all of our children (cached pages)
//G***del if we can				System.gc();  //before we start repagination, do garbage collection if we can
				int oldBoxSpan = (axis == X_AXIS) ? width : height;
Debug.trace("Getting ready to repaginate all pages.");	//G***del
				if(isThreaded())	//if we support threading
				{
Debug.trace("Threading supported.");  //G***del

//G***del				try{Thread.sleep(3000);}catch(Exception e){}				  //G***del; testing applet

	//G***del		final PageFlowStrategy flowStrategy=new PageFlowStrategy(this);	//G***testing threadlayout
						lastLayoutWidth=width;	//show what width we're laying out (newswing threadlayout)
						lastLayoutHeight=height;	//show what height we're laying out (newswing threadlayout)
//G***del if not needed						strategy=new PageFlowStrategy(this);	//create a new flow strategy (we have to create one each time,
						layoutStrategyThread=new Thread((PageFlowStrategy)strategy);	//create a new thread for flowing (newswing threadlayout)
						layoutStrategyThread.setName("Layout Strategy Thread");	//G***del; testing
						layoutStrategyThread.start();	//start the layout process (newswing threadlayout)
				}
				else	//if we do not support threading (newswing threadlayout)
				{
//G***del when works					strategy.layout(this);	//do layout the normal way



//G***del				final XMLFlowView flowView=this; //make a note of the flow view to use layout
				lastLayoutWidth=width;	//show what width we're laying out (newswing threadlayout) G***maybe bring this up so that it always gets set
				lastLayoutHeight=height;	//show what height we're laying out (newswing threadlayout)
				//create an instance of a layout object and queue it to layout the next section in the AWT thread
				SwingUtilities.invokeLater(new FlowRangeLayerOut(this, (PageFlowStrategy)strategy, flowLayoutInfo, true));  //G***fix cast
/*G***del

				SwingUtilities.invokeLater(new Runnable()	//G***testing
					{
						public void run()
						{

					((PageFlowStrategy)strategy).layout(flowView, new FlowLayoutInfo());	//do layout the normal way G***fix cast
Debug.trace("Finished paged view layout");
			setXAllocValid(true);	//show that our horizontal allocation is valid again
			setYAllocValid(true);	//show that our vertical allocation is valid again
			setPaginating(false); //show that we're not paginating anymore
						}
					});

*/












/*G***testing ViewReleasable
					((PageFlowStrategy)strategy).layout(this, new FlowLayoutInfo());	//do layout the normal way G***fix cast
Debug.trace("Finished paged view layout");
*/
//G***del Debug.stackTrace(); //G***del
				}

/*G***see how we can make this work with threading; this is probably needed if this is not the top-level view
							int newBoxSpan = (int) getPreferredSpan(axis);
							if (oldBoxSpan != newBoxSpan) {
									View p = getParent();
									p.preferenceChanged(this, (axis == X_AXIS), (axis == Y_AXIS));
							}
*/
		}

	//G***fix System.out.println("Getting ready to repaginate all pages.");	//G***del
	//G***fix 			strategy.layout(this);	//G***decide where to put this eventually


	/*G***fix
		// repair the flow if necessary
		if (! isAllocationValid()) {
				int oldBoxSpan = (axis == X_AXIS) ? width : height;
				strategy.layout(this);
							int newBoxSpan = (int) getPreferredSpan(axis);
							if (oldBoxSpan != newBoxSpan) {
									View p = getParent();
									p.preferenceChanged(this, (axis == X_AXIS), (axis == Y_AXIS));
							}
		}
	*/

		if(!isThreaded())	//if we're not threading (newswing threadlayout)
		{
//G***del Debug.trace("Setting xAllocValid and yAllocValid to true");

/*G***bring back testing ViewReleasable
			setXAllocValid(true);	//show that our horizontal allocation is valid again
			setYAllocValid(true);	//show that our vertical allocation is valid again
*/

/*G***del if not needed; this really hangs up the system on low-memory machines
			final int viewCount=getViewCount();	//find out how many child views we have
			for(int i=0; i<viewCount; ++i)		//look at each child view
			{
				getView(i).setSize(getPageWidth(), getPageHeight());	//update the size of each view so they will already be resized
//G***del Debug.trace("after setting page size index "+i+", xAllocValid: "+isXAllocValid()+" yAllocValid: "+isYAllocValid());
			}
*/
				//G***right now we're forcing valid allocation, because in some works 800X600 will cause infinite cycles
				//G***don't leave these in; find out why they are changing
/*G***del
			setXAllocValid(true);	//show that our horizontal allocation is valid again
			setYAllocValid(true);	//show that our vertical allocation is valid again
*/

		}


		// repair the flow if necessary
	//G***del if we don't need	if (! isAllocationValid()) {
	//G***del if not needed			int oldBoxSpan = (axis == X_AXIS) ? width : height;
	/*G***del when works
	System.out.println("Getting ready to repaginate all pages.");	//G***del
				strategy.layout(this);
	*/
	//G***del			setPageIndex(0);	//go to the first page G***make a method here that will make sure we stay on the same page
	//G***del System.out.println("Resetting back to page zero");
	/*G***del if not needed
							int newBoxSpan = (int) getPreferredSpan(axis);
							if (oldBoxSpan != newBoxSpan) {
									View p = getParent();
									p.preferenceChanged(this, (axis == X_AXIS), (axis == Y_AXIS));
							}
	*/
			}
		}

	/**Callback function called by the flow strategy to indicate that another row
		is being laid out.
	@param rowIndex The index of the row undergoing layout.
	*/
	synchronized protected void onLayoutRow(final int rowIndex)	//(newswing threadlayout)
	{
/*G***del this enire function, maybe
Debug.trace("Inside XMLPagedView.onLayoutRow() with rowIndex: ", rowIndex);	//G***del
//G***del System.out.println("Inside XMLPagedView.onLayoutRow() with rowIndex: "+rowIndex);	//G***del
		if(isThreaded())	//if we're threading (otherwise, this would probably cause endless loops)
		{
			final Container container=getContainer();	//get a reference to our container
			if(container!=null)	//if we're in a container
				container.repaint();	//repaint our container
*/
/*G***del
			SwingUtilities.invokeLater(new Runnable()	//don't repaint the container here (in this thread); invoke it later in the event thread
				{
					public void run()
					{
						final Container container=getContainer();	//get a reference to our container
						if(container!=null)	//if we're in a container
							container.repaint();	//repaint our container
					}
				});
*/
//G***del		}
	}

	/**Callback function called by the flow strategy to indicate that another page
		has been laid out.
	@param pageIndex The index of the page undergoing layout.
	*/
	protected void onPageLayoutComplete(final int pageIndex)
	{
		//fire a page event with our current page number, since our page count changed
		firePageEvent(new PageEvent(this, getPageIndex(), getPageCount()));
		if(pageIndex>getPageIndex() && pageIndex<getPageIndex()+getDisplayPageCount())  //if this is one of the pages we're showing
		{
			final Container container=getContainer();	//get a reference to our container
			if(container!=null)	//if we're in a container
				container.repaint();	//repaint our container
		}
/*G***del
			SwingUtilities.invokeLater(new Runnable()	//don't repaint the container here (in this thread); invoke it later in the event thread
				{
					public void run()
					{
						final Container container=getContainer();	//get a reference to our container
						if(container!=null)	//if we're in a container
							container.repaint();	//repaint our container
					}
				});
*/
	}

	/**Indicates that the layout has completed. This is synchronized so that the
		layout thread will never by set to <code>null</code> while it is being
		checked.
		@see #isLaidOut
	*/
	public synchronized void onLayoutComplete()	//(newswing threadlayout)
	{
/*G***fix
						int newBoxSpan = (int) getPreferredSpan(axis);
						if (oldBoxSpan != newBoxSpan) {
								View p = getParent();
								p.preferenceChanged(this, (axis == X_AXIS), (axis == Y_AXIS));
						}
*/
//G***delSystem.out.println("onLayoutComplete()");	//G***del
//G***del		strategy=null;
		layoutStrategyThread=null;	//set the layout thread to null, since we don't need it anymore
		setXAllocValid(true);	//show that our horizontal allocation is valid again
		setYAllocValid(true);	//show that our vertical allocation is valid again
		setPaginating(false); //show that we're not paginating anymore
		final int viewCount=getViewCount();	//find out how many child views we have

		if(isThreaded())	//if we're threading (otherwise, this would probably cause endless loops) G***fix this so that we don't needlessly paint the page if it's already been painted
		{
			final Container container=getContainer();	//get a reference to our container
			if(container!=null)	//if we're in a container
				container.repaint();	//repaint our container
		}

/*G***del
		for(int i=0; i<viewCount; ++i)		//look at each child view
			getView(i).setSize(getPageWidth(), getPageHeight());	//update the size of each view so they will already be resized
*/
	}

	/**Returns whether or not a page index has been laid out.
		This is synchronized so that the
		strategy will never by set to null while it is being checked.
		@return <code>true</code> if the indicated view has been laid out.
		@see #onLayoutComplete
//G***we need to change this to make sure that the page given is valid
	*/
	public synchronized boolean isLaidOut(final int pageIndex)	//(newswing threadlayout)
	{		//if we don't thread or if we're currently not threading, the view is laid out; if we are threading, ask the flow strategy for the answer G***what if the rowIndex is out of bounds?
//G***del System.out.println("XMLPagedView.isLaidOut() rowIndex: "+rowIndex+" layoutStrategyThread not null: "+(layoutStrategyThread!=null)+" strategy not null: "+(strategy!=null));	//G***del
		return ((PageFlowStrategy)strategy).isLaidOut(pageIndex);  //ask the strategy whether the page is laid out G***fix cast
//G***del		return !isThreaded() || layoutStrategyThread==null || ((PageFlowStrategy)strategy).isLaidOut(rowIndex);
	}

	/**Checks the request cache and update if needed. This overrides the default
	functionality to simply return the size of the pages.*/
/*G***del
	protected void checkRequests()
	{
//G***del System.out.println("Checking requests, width: "+getWidth()+" height: "+getHeight());	//G***del
		super.checkRequests();	//G***fix
//G***del System.out.println("X request: "+xRequest);	//G***del
//G***del System.out.println("Y request: "+yRequest);	//G***del
*/
/*G***fix
		final int axis=getAxis();	//get our tiling axis
		final int displayPageCount=getDisplayPageCount();	//see how many pages we're displaying
//G***fix		if(axis==X_AXIS)	//if we're tiling on the X axis
		{
			if(!isXValid())	//if our horizontal preferences aren't valid
			{
				xRequest=new SizeRequirements(getWidth(), getWidth(), getWidth(), 0);	//G***testing
			}
			if(!isYValid())	//if our vertical preferences aren't valid
			{
				yRequest=new SizeRequirements(getHeight(), getHeight(), getHeight(), 0);	//G***testing
			}
		}
		setXValid(true);	//show that our horizontal preference is valid again
		setYValid(true);	//show that our vertical preference is valid again
*/
//G***del	}


	/**Returns the allocation for a specified child view.
	@param pageIndex The index of the page to allocate, (>=0 &&
		<getViewCount()).
	@param alloc The allocated region caluclated and returned.
	*/
	protected void childAllocation(final int pageIndex, Rectangle alloc)
	{
//G***del		final int displayPageCount=getDisplayPageCount();	//see how many pages we're displaying
		final int displayIndex=pageIndex%getDisplayPageCount();	//find out which position this page would land in
		final int xDelta=getAxis()==X_AXIS ? (int)getPageWidth() : 0;	//find out how much we should move each page horizontally G***do we want to cast to an int here?
		final int yDelta=getAxis()==Y_AXIS ? (int)getPageHeight() : 0;	//find out how much we should move each page vertically
			//G***it might be better in the long run to eventually actually update the xAlloc[] and such arrays, like BoxView does
		alloc.x+=getLeftInset()+displayIndex*xDelta;	//calculate where this page would be horizontally
		alloc.y+=getTopInset()+displayIndex*yDelta;	//calculate where this page would be vertically
		alloc.width=(int)getPageWidth();	//each view has the same width
		alloc.height=(int)getPageHeight();	//each view has the same height
	}

	/**Finds the child view at the given point.
	@param x The horizontal coordinate (>=0).
	@param y The vertical coordinate (>=0).
	@param alloc The parent's inner allocation on entry, which should
		be changed to the childs allocation on exit.
	@return The child view at the specified point.
	*/
	protected View getViewAtPoint(int x, int y, Rectangle alloc)
	{
		final int displayPageCount=getDisplayPageCount();	//see how many pages we're displaying
		final int pageCount=getPageCount();	//see how many pages we have in total
		final int pageBeginIndex=getPageIndex();	//see which page we're showing first
		final int pageEndIndex=Math.min(pageBeginIndex+displayPageCount, pageCount);	//see which page we're showing last (actually, this is one more than the last page we're showing)
		final int pageWidth=(int)getPageWidth();	//get the width of each page
		final int pageHeight=(int)getPageHeight();	//get the height of each page
//G***del when works		int n = getViewCount();
		int pageIndex;	//we'll store here the index of the page we find
		if(axis==View.X_AXIS)	//if this view flows horizontally (the default)
		{
	    if(x<(alloc.x+getLeftInset()))	//if this location is before our first displayed page G***here again we could use those calculated values in the xAlloc[] array
			{
//G***del Debug.trace("XMLPagedView.getViewAtPoint() coordinate is before first horizontally displayed page.");
				pageIndex=pageBeginIndex;	//use the first page
/*G***del
				childAllocation(pageBeginIndex, alloc);	//get the allocation of the first page
				return getView(pageBeginIndex);	//return the first displayed page
*/
	    }
			else	//if this location isn't before the first displayed page
			{
/*G***del
Debug.trace("XMLPagedView.getViewAtPoint() coordinate is not before first horizontally displayed page.");
Debug.trace("alloc.x: "+alloc.x+" leftInset: "+getLeftInset()+" alloc.x+getLeftInset(): "+(alloc.x+getLeftInset()));
Debug.trace("x: "+x+" x-start: "+(x-(alloc.x+getLeftInset())));
Debug.trace("pageWidth: "+pageWidth+" pageWidth/pos: "+pageWidth/(x-(alloc.x+getLeftInset())));
*/
				if(pageWidth>0) //if we have a valid page width G***fix; this next line could throw a DivideByZero when the mouse move during pagination, and this is just a temporary workaround
				{
					pageIndex=pageBeginIndex+((x-(alloc.x+getLeftInset()))/pageWidth);	//starting at the first page, see in which page the coordinate lies (since all pages are the same width)
					if(pageIndex>=pageEndIndex)	//if the coordinate is past the last displayed page
						pageIndex=pageEndIndex-1;		//use the last page
				}
				else  //if there is no page width (e.g. we might be paginating) G***fix
				{
					return null;  //we can't get a view
				}
			}
		}
		else	//if this view flows vertically
		{
	    if(y<(alloc.y+getTopInset()))	//if this location is before our first displayed page G***here again we could use those calculated values in the xAlloc[] array
			{
//G***del Debug.trace("XMLPagedView.getViewAtPoint() coordinate is before first vertically displayed page.");
				pageIndex=pageBeginIndex;	//use the first page
	    }
			else	//if this location isn't before the first displayed page
			{
//G***del Debug.trace("XMLPagedView.getViewAtPoint() coordinate is not before first vertically displayed page.");
				pageIndex=pageBeginIndex+((y-(alloc.y+getTopInset()))/pageHeight);	//starting at the first page, see in which page the coordinate lies (since all pages are the same height)
				if(pageIndex>=pageEndIndex)	//if the coordinate is past the last displayed page G***maybe break this out of the statements to avoid duplication
					pageIndex=pageEndIndex-1;		//use the last page
			}
		}
		if(isLaidOut(pageIndex))  //if this page is laid out
		{
//G***del Debug.trace("XMLPagedView.getViewAtPoint() found page index: "+pageIndex);
	    childAllocation(pageIndex, alloc);	//get the allocation of the page
//G***del Debug.trace("XMLPagedView.getViewAtPoint() ready to return view: "+pageIndex);
      return getPage(pageIndex);	//return the page itself
		}
		else  //if this page is not yet laid out
			return null;  //show that we could not find a matching page
//G***del    return getView(pageIndex);	//return the page that was found
	}



/*G***del when works
	    for(int i=1; i<pageEndIndex-pageBeginIndex; i++)	//look at each displayed page except the first one
			{
				if(x<(alloc.x+getLeftInset()+pageWidth*i))	//if the point is to the left of this page G***here we could use those precalculated xOffsets[i]
				{
					childAllocation(pageBeginIndex+i-1, alloc);	//get the allocation for the previous page
					return getView(pageBeginIndex+i-1);	//return the previous page
				}
	    }
	    childAllocation(pageEndIndex-1, alloc);	//if the point is not before the last page, get the allocation for the last page
	    return getView(pageEndIndex-1);	//return the last page
*/

/*G***del




			if(axis==X_AXIS)	//if we're tiling on the X axis
			{
				setPageWidth(width/displayPageCount);	//show that the pages will be a fraction of our width
				setPageHeight(height);	//show that the pages will be the same height
			}
			else	//if we're tiling on the Y axis
			{
				setPageWidth(width);	//show that the widths will all be the same
				setPageHeight(height/displayPageCount);	//show that the pages will each be a fraction of the total height
			}
			final int newSpan=(flowAxis==X_AXIS) ? width : height;	//get our new span




	    if (x < (alloc.x + xOffsets[0])) {
		childAllocation(0, alloc);
		return getView(0);
	    }
	    for (int i = 0; i < n; i++) {
		if (x < (alloc.x + xOffsets[i])) {
		    childAllocation(i - 1, alloc);
		    return getView(i - 1);
		}
	    }
	    childAllocation(n - 1, alloc);
	    return getView(n - 1);
	} else {
	    if (y < (alloc.y + yOffsets[0])) {
		childAllocation(0, alloc);
		return getView(0);
	    }
	    for (int i = 0; i < n; i++) {
		if (y < (alloc.y + yOffsets[i])) {
		    childAllocation(i - 1, alloc);
		    return getView(i - 1);
		}
	    }
	    childAllocation(n - 1, alloc);
	    return getView(n - 1);
	}
    }

*/

	/**     * Provides a mapping from the document model coordinate space
     * to the coordinate space of the view mapped to it.  This makes
     * sure the allocation is valid before letting the superclass
     * do its thing.
     *
     * @param pos the position to convert >= 0
     * @param a the allocated region to render into
     * @return the bounding box of the given position
     * @exception BadLocationException  if the given position does
     *  not represent a valid location in the associated document
     * @see View#modelToView
     */
/*G***fix
    public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
	if (! isAllocationValid()) {
	    Rectangle alloc = a.getBounds();
	    setSize(alloc.width, alloc.height);
	}
	return super.modelToView(pos, a, b);
    }
*/

	/**Provides a mapping from the view coordinate space to the logical
		coordinate space of the model.
	@param x The horizontal coordinate of the view location to convert (>=0).
	@param y The vertical coordinate of the view location to convert (>=0).
	@param a The allocated region to render into.
	@return The location within the model that best represents the given point in
		the view (>=0).
	@see View#viewToModel
	*/
	public int viewToModel(float x, float y, Shape a, Position.Bias[] bias)
	{
//G***what if we're in the middle of paginating?
//G***del Debug.trace("Inside XMLPagedView.viewToModel() for x: "+x+" y: "+y);
		if(!isAllocationValid())	//if our allocation isn't value
		{
//G***del Debug.trace("Inside XMLPagedView.viewToModel(), allocation isn't valid; changing size");
			final Rectangle allocationRect=a.getBounds();	//get the bounds of the area into which the caller thinks we've been rendered G***this probably isn't the best thing to do under a threaded scenario
			setSize(allocationRect.width, allocationRect.height);	//update our size
		}
//G***fix	return super.viewToModel(x, y, a, bias);
		final Rectangle insideAlloc=getInsideAllocation(a);	//get the inside allocation, with insets removed
		if(isBefore((int)x, (int)y, insideAlloc))	//if the point is before the information that is showing
		{
//G***del Debug.trace("XMLPagedView.viewToModel() is before");
	    int returnValue=-1;	//we'll default to a -1 if we can't find a correct position
			try
			{
				returnValue=getNextVisualPositionFrom(-1, Position.Bias.Forward, a, EAST, bias);
	    }
			catch (BadLocationException ble) {}	//ignore bad location errors
	    catch (IllegalArgumentException iae) {}	//ignore illegal argument errors
			if(returnValue==-1)	//if we were unable to find a previous position
			{
				returnValue=getStartOffset();	//return the starting offset
				bias[0]=Position.Bias.Forward;
	    }
	    return returnValue;	//return whatever value we found
		}
		else if(isAfter((int)x, (int)y, insideAlloc))	//if the point is after the information that is showing
		{
//G***del Debug.trace("XMLPagedView.viewToModel() is after");
	    int returnValue=-1;	//we'll default to a -1 if we can't find a correct position
	    try
			{
				returnValue=getNextVisualPositionFrom(-1, Position.Bias.Forward, a, WEST, bias);
	    }
			catch (BadLocationException ble) {}	//ignore bad location errors
	    catch (IllegalArgumentException iae) {}	//ignore illegal argument errors
			if(returnValue==-1)	//if we were unable to find a next position
			{
				returnValue=getEndOffset()-1;	//return one less than the ending offset
				bias[0]=Position.Bias.Forward;
	    }
	    return returnValue;	//return whatever value we found
		}
		else	//if the location is within our displayed content area
		{
//G***del Debug.trace("XMLPagedView.viewToModel() is within our displayed content; ready to call getViewAtPoint()");
			final View view=getViewAtPoint((int)x, (int) y, insideAlloc);	//locate the appropriate child
//G***del Debug.trace("XMLPagedView.viewToModel() found view: "+Debug.getNullStatus(view));
	    if(view!=null)	//if we found a view at the specified point
	      return view.viewToModel(x, y, insideAlloc, bias);	//ask that view to find the model location at the specified point
		}
		return -1;	//if we were unable, for some reason, to find a view, return -1 G***is this correct?
	}

		/**
		 * Determines the desired alignment for this view along an
		 * axis.  This is implemented to give the alignment to the
		 * center of the first row along the y axis, and the default
		 * along the x axis.
		 *
		 * @param axis may be either View.X_AXIS or View.Y_AXIS
		 * @returns the desired alignment.  This should be a value
		 *   between 0.0 and 1.0 inclusive, where 0 indicates alignment at the
		 *   origin and 1.0 indicates alignment to the full span
		 *   away from the origin.  An alignment of 0.5 would be the
		 *   center of the view.
		 */
		public float getAlignment(int axis) {
			return 0;
/*G***fix
				switch (axis) {
				case Y_AXIS:
			float a = 0.5f;
			if (getViewCount() != 0) {
		int paragraphSpan = (int) getPreferredSpan(View.Y_AXIS);
		View v = getView(0);
		int rowSpan = (int) v.getPreferredSpan(View.Y_AXIS);
		a = (paragraphSpan != 0) ? ((float)(rowSpan / 2)) / paragraphSpan : 0;
			}
						return a;
	case X_AXIS:
			return 0.5f;
	default:
						throw new IllegalArgumentException("Invalid axis: " + axis);
	}
*/
		}

	/**Breaks this view on the given axis at the current length. Currently, since
		an <code>XMLPagedView</code> is a top-level view which <em>does</em> all the
		breaking, it itself does not support being broken.
	@param axis The axis to break along, either View.X_AXIS or View.Y_AXIS.
	@param len The location of a potential break along the given axis (>=0).
	@param a The current allocation of the view.
	@return the fragment of the view that represents the given span. Currently,
		since this view cannot be broken, the view itself is returned.
	@see View#breakView
	*/
	public View breakView(int axis, float len, Shape a)
	{
		return this;	//this view cannot be broken; return a reference to ourself
	}

	/**Calculates the break weight for a given location. Currently, since an
		<code>XMLPagedView</code> is a top-level view which <em>does</em> all the
		breaking, it itself does not support being broken.
	@param axis The axis to break along, either View.X_AXIS or View.Y_AXIS.
	@param len The location of the potential break (>=0).
	@return A value indicating the attractiveness of breaking at the specified
		location; currently returns BadBreakWeight for each axis.
	@see View#getBreakWeight
	*/
	public int getBreakWeight(int axis, float len)
	{
		return BadBreakWeight;	//paged views cannot be broken
	}


	/**Because a paged view uses its child views as cached pages, adding or
		removing pages does not affect our layout. Don't therefore allow
		replacement operations to change whether our allocation is valid.
	@param index The starting index into the child views to insert the new views
		(>=0 && <=<code>getViewCount()</code>).
	@param length The number of existing child views to remove (>=0 && <=
		<code>(getViewCount()-offset)</code>).
	@param views The child views to add, or <code>null</code> to indicate that no
		children are being added (i.e. children are being removed).
	*/
	public void replace(int index, int length, View[] views)
	{
		final boolean xValid=isXValid();  //make a note of all our allocation states so that we can restore them
		final boolean yValid=isYValid();
		final boolean xAllocValid=isXAllocValid();
		final boolean yAllocValid=isYAllocValid();
	  super.replace(index, length, views);  //do the default replacement
		setXValid(xValid);    //reset all the allocation flags to how we found them
		setYValid(yValid);
		setXAllocValid(xAllocValid);
		setYAllocValid(yAllocValid);
	}

	/**Notification from the document that attributes were changed in an area
		this paged view is responsible for.
	@param changes The change information from the associated document.
	@param a The current allocation of the view.
	@param f The factory to use to rebuild if the view has children.
	@see View#changedUpdate
	*/
/*G***del	when do-nothing replacement works
	public void changedUpdate(DocumentEvent changes, Shape a, ViewFactory f)
	{
//G***del if we don't need		setPropertiesFromAttributes();	//update our properties from the changed attributes G***do we need to do this on a paged view? can there be page-specific attributes?
		super.changedUpdate(changes, a, f);	//do the default updating
	}
*/

//G***later we might want to have a vertical justification variable:		private int justification;

	/**Loads all of the children to initialize the view. This is called by the
		<code>setParent</code> method. This is reimplemented to not load any
		children directly, as they are created in the process of formatting.
		This function does make sure that a layout pool is created that will store
		logical views and do preliminary sizing.
	@param f The view factor to be used to create views
	*/
	protected void loadChildren(ViewFactory f)
	{
/*G***use, maybe
		final View[] createdViews=XMLBlockView.createBlockElementChildViews(getElement(), viewFactory);  //create the child views
		replace(0, 0, createdViews);  //load our created views as children
*/

		final Container container=getContainer();	//get a reference to our container G***all this should probably go somewhere else
		if(container instanceof XMLTextPane)	//if the container is an XML text pane
			((XMLTextPane)container).setPagedView(this);	//tell it that it has a paged view
/*G***del, put in the right place
							//G***there's probably a better way to do this
						final Document document=element.getDocument();	//see which document owns this element
						if(document instanceof OEBDocument)	//if it's an OEB document
							((OEBDocument)document).BodyView=bodyView;	//let it know which view represents the body G***do this much better
	//G***fix						(OEBDocument)document.setBodyView(bodyView);	//let it know which view holds the
*/

/*G***del
		if(layoutPool==null)	//if we don't have a layout pool, yet //G***del all of this when we can
		{
//G***del				//get a page pool view from which to paginate
//G***del; this actually loads things			layoutPool=getPagePoolView(getElement().getStartOffset(), getElement().getEndOffset());
		  layoutPool=new PagePoolView(getElement(), getFlowAxis(), getElement().getStartOffset(), getElement().getEndOffset());	//create a pool for the pages G***we may want to hard-code this to Y_AXIS later; this mistake has caused hours of debugging
		}
*/


Debug.trace("create flowlayout"); //G***del

		//if we haven't already, create flow layout information about ranges that can be flowed independently of others
		if(flowLayoutInfo==null)  //if we haven't created flow layout information, yet
		{
			flowLayoutInfo=new PageFlowLayoutInfo();  //create new flow layout information
/*G***del when works
			final Element element=getElement(); //get the element we represent
			final int childElementCount=element.getElementCount(); //find out how many elements we represent
*/
			final Element[] viewChildElements=getViewChildElements(getStartOffset(), getEndOffset()); //get all the child elements
			final int childElementCount=viewChildElements.length;  //find out how many child elements there are
			int startOffset=getStartOffset(); //start with the first offset
	//G***del		int endOffset=getEndOffset(); //assume we won't find any place before the ending offset
			for(int i=0; i<childElementCount; ++i) //look at each child element
			{
				int endOffset=-1;  //we'll set this to a positive number if we find the end of a new flow range

//G***del when works				final Element childElement=element.getElement(i); //get a reference to this element
				final Element childElement=viewChildElements[i]; //get a reference to this element
				final AttributeSet childAttributeSet=childElement.getAttributes();  //get the attributes of the element
//G***del Debug.trace("looking at child element: ", XMLCSSStyleConstants.getXMLElementName(childAttributeSet)); //G***del
				final String pageBreakBefore=XMLCSSStyleUtilities.getPageBreakBefore(childAttributeSet); //get the value of the page-break-before CSS property, if any
				final String pageBreakAfter=XMLCSSStyleUtilities.getPageBreakAfter(childAttributeSet); //get the value of the page-break-after CSS property, if any
//G***del Debug.trace("page break before: ", pageBreakBefore); //G***del
					//if this element always wants breaks before it and this isn't the first child element
				if(i>0 && XMLCSSConstants.CSS_PAGE_BREAK_BEFORE_ALWAYS.equals(pageBreakBefore))
				{
//G***del when works					endOffset=element.getElement(i-1).getEndOffset(); //this flow range ends after the last element
					endOffset=viewChildElements[i-1].getEndOffset(); //this flow range ends after the last element
				}
					//if this element always wants breaks after it
				else if(XMLCSSConstants.CSS_PAGE_BREAK_AFTER_ALWAYS.equals(pageBreakAfter))
				{
					endOffset=childElement.getEndOffset(); //find the end of this element
				}
				else if(XMLStyleUtilities.isPageBreakView(childElement.getAttributes())) //G***maybe just check for the page-break-after attribute
				{
					endOffset=childElement.getEndOffset(); //find the end of this element
				}
				if(endOffset>startOffset) //if we found the end of a flow range
				{
					final FlowRange flowRange=new FlowRange(startOffset, endOffset);  //create a new flow range, ending with this element
//G***del 	Debug.trace("Found flow range: ", flowRange);
					flowLayoutInfo.addFlowRange(flowRange); //add this flow range to our list
					startOffset=endOffset;  //we'll start the next flow range where this one left off
				}
			}
			final int endOffset=getEndOffset(); //find the end of our entire paged view
			if(endOffset-startOffset>0) //if there is any content we didn't account for
			{
				final FlowRange flowRange=new FlowRange(startOffset, endOffset);  //create a new flow range, ending with the end of the content we know about
				flowLayoutInfo.addFlowRange(flowRange); //add this flow range to our list
//G***del 	Debug.trace("Found flow range: ", flowRange);
			}
//G***del Debug.trace("created flow ranges: ", flowLayoutInfo.flowRangeList.size()); //G***del
		}
//G***del //G***testing; bring back		layoutPool.setParent(this);	//set the parent of the logical view pool, which will make it load its children
	}

	/**Fetches the child view index representing the given position in the model.
		Since the logical view of a paged view does not necessarily have one view
		for each child element (a child element in a logical view may be
		an entire document, represented by several views), this method is implemented
		to correctly iterate pages to locate one matching the position.
		Only pages that have been paginated and are cached as child views are
		searched.
	@param pos The requested position (>=0).
	@returns The index of the view representing the given position, or -1 if no
		view represents that position.
	*/
/*G***del; this is already implemented in XMLFlowView
	protected int getViewIndexAtPosition(int pos)
	{
//G***del Debug.trace("Searching paged view for position: ", pos);  //G***del
		for(int childViewIndex=getViewCount()-1; childViewIndex>=0; --childViewIndex)  //look at each of our children to see if we've already paginated this view
		{
			final View childView=getView(childViewIndex); //get a reference to this view
//G***del Debug.trace("Searching paged view for position: ", pos);  //G***del
			if(pos>=childView.getStartOffset() && pos<childView.getEndOffset()) //if this position falls within the range of this view
				return childViewIndex;  //return the index of this child
		}
		return -1;  //show that there is no child view that contains the given position
	}
*/

	/**Returns all child elements for which views should be created. If
		a paged view holds multiple documents, for example, the children of those
		document elements will be included. An XHTML document, furthermore, will
		return the contents of its <code>&lt;body&gt;</code> element.
		It is assumed that the ranges precisely enclose any child elements within
		that range, so any elements that start within the given range will be
		included.
	@param newStartOffset This range's starting offset.
	@param newEndOffset This range's ending offset.
	@return An array of elements for which views should be created.
	*/
	protected Element[] getViewChildElements(final int startOffset, final int endOffset)
	{
//G***del Debug.trace("Getting view child elements"); //G***del
//G***del Debug.trace("start offset: ", startOffset); //G***del
//G***del Debug.trace("end offset: ", endOffset); //G***del
		final java.util.List viewChildElementList=new ArrayList();  //create a list in which to store the elements as we find them
		final Element element=getElement(); //get a reference to our element
		final int documentElementCount=element.getElementCount();  //find out how many child elements there are (representing XML documents)
		for(int documentElementIndex=0; documentElementIndex<documentElementCount; ++documentElementIndex) //look at each element representing an XML document
		{
//G***del Debug.trace("looking at document: ", documentElementIndex); //G***del
		  final Element documentElement=element.getElement(documentElementIndex); //get a reference to this child element
//G***del Debug.trace("document start offset: ", documentElement.getStartOffset()); //G***del
//G***del Debug.trace("document end offset: ", documentElement.getEndOffset()); //G***del
		    //if this document's range overlaps with our range
//G***del; only takes care of one case of overlapping			if(documentElement.getStartOffset()>=startOffset && documentElement.getStartOffset()<endOffset)
			if(documentElement.getStartOffset()<endOffset && documentElement.getEndOffset()>startOffset)
		  {
//G***del Debug.trace("document within our range"); //G***del
				final AttributeSet documentAttributeSet=documentElement.getAttributes();  //get the attributes of the document element
				if(XMLStyleUtilities.isPageBreakView(documentAttributeSet)) //if this is a page break element
				{
//G***del Debug.trace("found page break view"); //G***del
					viewChildElementList.add(documentElement);  //add this element to our list of elements; it's not a top-level document like the others G***this is a terrible hack; fix
				}
				else
				{
	//G***del if not needed				Element baseElement=documentElement;  //we'll find out which element to use as the parent; in most documents, that will be the document element; in HTML elements, it will be the <body> element
//G***del					final MediaType documentMediaType=XMLStyleConstants.getMediaType(documentAttributeSet);  //get the media type of the document
//G***del					final String documentElementLocalName=XMLStyleConstants.getXMLElementLocalName(documentAttributeSet);  //get the document element local name
//G***del					final String documentElementNamespaceURI=XMLStyleConstants.getXMLElementNamespaceURI(documentAttributeSet);  //get the document element local name
					final int childElementCount=documentElement.getElementCount();  //find out how many children are in the document
					for(int childIndex=0; childIndex<childElementCount; ++childIndex)  //look at the children of the document element
					{
						final Element childElement=documentElement.getElement(childIndex); //get a reference to the child element

//G***del; only accounts for one case of overlapping						if(childElement.getStartOffset()>=startOffset && childElement.getStartOffset()<endOffset) //if this child element starts within our range
						  //if this child element's range overlaps with our range
						if(childElement.getStartOffset()<endOffset && childElement.getEndOffset()>startOffset)
						{
							final AttributeSet childAttributeSet=childElement.getAttributes();  //get the child element's attributes
							final String childElementLocalName=XMLStyleUtilities.getXMLElementLocalName(childAttributeSet);  //get the child element local name
//G***del Debug.trace("Looking at child: ", childElementLocalName); //G***del
//G***del							boolean isHTMLBody=false; //we'll determine if this element is a <body> element of XHTML
							if(XHTMLConstants.ELEMENT_BODY.equals(childElementLocalName))  //if this element is <body>
							{
/*G***del
								//we'll determine if this body element is HTML by one of following:
								//  * the element is in the XHTML or OEB namespace
								//  * the element is in no namespace but the document is of type text/html or text/x-oeb1-document
								//  * the element is in no namespace and the document element is
								//      an <html> element in the XHTML or OEB namespace
								final String childElementNamespaceURI=XMLStyleConstants.getXMLElementNamespaceURI(childAttributeSet);  //get the child element local name
								if(childElementNamespaceURI!=null)  //if the body element has a namespace
								{
										//if it's part of the XHTML or OEB namespace
									if(XHTMLConstants.XHTML_NAMESPACE_URI.equals(childElementNamespaceURI)
											|| OEBConstants.OEB1_DOCUMENT_NAMESPACE_URI.equals(childElementNamespaceURI))
										isHTMLBody=true;  //show that this is an HTML body element
								}
								else  //if the body element has no namespace
								{
										//if the document type is text/html or text/x-oeb1-document
									if(documentMediaType!=null && (documentMediaType.equals(MediaType.TEXT_HTML) || documentMediaType.equals(MediaType.TEXT_X_OEB1_DOCUMENT)))
										isHTMLBody=true;  //is an HTML body element
									else if(XHTMLConstants.ELEMENT_HTML.equals(documentElementLocalName)  //if the document element is an XHTML or OEB <html> element
											&& (XHTMLConstants.XHTML_NAMESPACE_URI.equals(documentElementNamespaceURI) || OEBConstants.OEB1_DOCUMENT_NAMESPACE_URI.equals(documentElementNamespaceURI)))
										isHTMLBody=true;  //is an HTML body element
								}
							}
							if(isHTMLBody)  //if this element is an XHTML <body> element
							{
*/
								if(XHTMLSwingTextUtilities.isHTMLElement(childAttributeSet, documentAttributeSet)) //if this is an HTML element
								{
//G***del Debug.trace("is HTML body");  //G***del
									final int bodyChildElementCount=childElement.getElementCount(); //find out how many children the body element has
									for(int bodyChildIndex=0; bodyChildIndex<bodyChildElementCount; ++bodyChildIndex) //look at each of the body element's children
									{
	//G***del Debug.trace("Adding body child element: ", bodyChildIndex);
										final Element bodyChildElement=childElement.getElement(bodyChildIndex); //get this child element of the body element
										if(bodyChildElement.getStartOffset()>=startOffset && bodyChildElement.getStartOffset()<endOffset) //if this child element starts within our range G***should we merely check for overlaps here?
											viewChildElementList.add(bodyChildElement);  //add this body child element to our list of elements
									}
								}
							}
							else  //if this element is not an XHTML <body> element
							{
//G***del 		Debug.trace("Adding child element: ", childIndex);
								viewChildElementList.add(childElement);  //add this child element to our list of elements
							}
						}
					}
				}
		  }
		}
	  return (Element[])viewChildElementList.toArray(new Element[viewChildElementList.size()]); //return the views as an array of views
	}

	//G***we need to override this so that the parent class doesn't try to use the layout pool, which we don't use or even create in this class
	//G***check, comment
	protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r)
	{
		return baselineRequirements(axis, r); //G***does this have anything to do with what we want to return?
	}


	//page events

	/**Adds a listener that will be notified when the page is changed.
	@param listener The object to be notified.
	*/
	public void addPageListener(PageListener listener)
	{
		pageListenerList.add(PageListener.class, listener);	//add this listener to our list
	}

	/**Removes a listener that no longer wants to be notified when the page is changed.
	@param listener The object to be removed.
	*/
	public void removePageListener(PageListener listener)
	{
		pageListenerList.remove(PageListener.class, listener);	//remove this listener from our list
	}

	/**Notifies all page listeners that the page has been changed.
	@param e The notification event.
	*/
	protected void firePageEvent(PageEvent e)
	{
		final Object[] listeners=pageListenerList.getListenerList();	//get an array of listeners, guaranteed to be non-null
		for(int i=listeners.length-2; i>=0; i-=2)	//look at each of the listener classes (the fact that we're going from back to front is not significant)
		{
			if(listeners[i]==PageListener.class)	//if this is a page listener
				((PageListener)listeners[i+1]).pageChanged(e);	//tell this listener our displayed page has changed
		}
	}

	//progress events

	/**Adds a progress listener.
	@param listener The listener to be notified of progress.
	*/
	public void addProgressListener(ProgressListener listener)
	{
		progressListenerList.add(ProgressListener.class, listener);	//add this listener
	}

	/**Removes a progress listener.
	@param listener The listener that should no longer be notified of progress.
	*/
	public void removeProgressListener(ProgressListener listener)
	{
		progressListenerList.remove(ProgressListener.class, listener);
	}

	/**Notifies all listeners that have registered interest for progress that
		progress has been made.
	@param status The status to display.
	*/
	protected void fireMadeProgress(final ProgressEvent progressEvent)
	{
		final Object[] listeners=progressListenerList.getListenerList();	//get the non-null array of listeners
		for(int i=listeners.length-2; i>=0; i-=2)	//look at each listener, from last to first
		{
			if(listeners[i]==ProgressListener.class)	//if this is a progress listener (it should always be)
				((ProgressListener)listeners[i+1]).madeProgress(progressEvent);
     }
	}

	/* ***XMLPagedView.PagePoolView*** */

	/**This class is used to layout our child views behind the scenes and store
		them as a layout pool from which we will create physical views.
		This class descends from <code>BoxView</code> so that the size of the views
		can be set automatically.
	*/
	class PagePoolView extends BoxView
	{

		/**The start of this range.*/
		private int startOffset;

		  /**@return The start of this range.*/
			public int getStartOffset() {return startOffset;}

		/**The non-inclusive end of this range.*/
		private int endOffset;

		  /**@return The non-inclusive end of this range.*/
			public int getEndOffset() {return endOffset;}

		/**Constructor that requires an element.
		@param element The element which hold the information the views in this pool
			represent.
		@param axis The layout axis, either View.X_AXIS or View.Y_AXIS.
		@param newStartOffset This range's starting offset.
		@param newEndOffset This range's ending offset.
		*/
		PagePoolView(Element element, int axis, final int newStartOffset, final int newEndOffset)
		{
			super(element, axis);	//construct the parent class
			startOffset=newStartOffset; //save the starting offset
			endOffset=newEndOffset; //save the ending offset
		  setInsets((short)25, (short)25, (short)25, (short)25);	//G***fix; testing
		}

		/**Loads the children into this view pool. Only the children within the
			view's range (specified in the constructor) will be loaded.
		@param viewFactory The factory to use to create the child views.
		*/
		protected void loadChildren(ViewFactory viewFactory)
		{
			if(viewFactory==null) //if there is no view factory, we can't load the children
				return; //we can't do anything
			final int startOffset=getStartOffset(); //find out where we should start
			final int endOffset=getEndOffset(); //find out where we should end
//G***del Debug.trace("loading children for page pool, offsets "+startOffset+" to "+endOffset);
			  //G***testing; comment; eventually put in the view factory
			final Element[] viewChildElements=getViewChildElements(startOffset, endOffset); //get the child elements that fall within our range
		  //create an anonymous element that simply holds the elements we just loaded
			//this temporary element will go away after we've created views
		  final Element anonymousElement=new AnonymousElement(getElement(), null, viewChildElements, 0, viewChildElements.length);
				//G***is it good to make an anonymous element simply for enumerating child elements to XMLBlockView?
			final View[] createdViews=XMLBlockView.createBlockElementChildViews(anonymousElement, viewFactory);  //create the child views
			this.replace(0, 0, createdViews);  //add the views as child views to this view pool (use this to show that we shouldn't use the XMLPagedView version)
		}

		/**Returns the index of the child at the given model position in the pool.
		@param pos The position (>=0) in the model.
		@return The index of the view representing the given position, or -1 if there
			is no view on this pool which represents that position.
		*/
		protected int getViewIndexAtPosition(int pos)
		{
//G***del Debug.trace("looking for view at position: ", pos); //G***del
//G***del Debug.trace("startoffset: ", getStartOffset());
//G***del Debug.trace("endoffset: ", getEndOffset());

//G***del Debug.trace("child views: ", getViewCount());

			//this is an expensive operation, but this class usually contains only a partial list of views, which may not correspond to the complete list of original elements
			if(pos<getStartOffset() || pos>=getEndOffset())	//if the position is before or after the content
				return -1;	//show that the given position is not on this page
			for(int viewIndex=getViewCount()-1; viewIndex>=0; --viewIndex)	//look at all the views from the last to the first
			{
				final View view=getView(viewIndex);	//get a reference to this view
//G***del Debug.trace("View "+viewIndex+" is of class: ", view.getClass().getName());
//G***del Debug.trace("startoffset: ", view.getStartOffset());
//G***del Debug.trace("endoffset: ", view.getEndOffset());
				if(pos>=view.getStartOffset() && pos<view.getEndOffset())	//if this view holds the requested position
					return viewIndex;	//return the index to this view
			}
			return -1;	//if we make it to this point, we haven't been able to find a view with the specified position
		}

		//G***testing
/*G***del
    public void loadChildren(final int startOffset, final int endOffset)
		{
			final ViewFactory f=getViewFactory(); //G***testing
    }
*/
	}

	/***XMLPagedView.Page***/

	/**Internally-created view that holds the view representing child views
		arranged in pages. This class descends from <code>XMLBLockView</code>, which
		correctly returns starting and ending offsets based upon the child views
		it contains, not the element it represents.
	*/
	class Page extends XMLBlockView	//G***testing BoxView
	{
		/**Page constructor that specifies the element from which the information
			will come.
		@param element The element that contains the information to display.
		*/
		Page(Element element)
		{
			super(element, View.Y_AXIS);	//the information inside each page will be flowed vertically
		  setInsets((short)25, (short)25, (short)25, (short)25);	//G***fix; testing; need here and in setPropertiesFromAttributes() for hack to work
		}

		/**Sets the cached properties from the attributes. This version forces the
		  margins to a particular size.
		*/
		protected void setPropertiesFromAttributes()  //G***fix; hack because of new style-based margins
		{
			super.setPropertiesFromAttributes();  //set the attributes normally
			setInsets((short)25, (short)25, (short)25, (short)25);	//G***fix; testing
		}

		//G***fix, comment; these are present because they aren't public in CompositeView
		public short getPublicLeftInset() {return getLeftInset();}
		public short getPublicRightInset() {return getRightInset();}
		public short getPublicTopInset() {return getTopInset();}
		public short getPublicBottomInset() {return getBottomInset();}


		/**Each page does not need to fill its children, since its parent
			<code>XMLPagedView</code> will load its children with the views it created.
			This function therefore does nothing.
		*/
		protected void loadChildren(ViewFactory f) {}


    /**
     * This is called by a child to indicated its
     * preferred span has changed.  This is implemented to
     * throw away cached layout information so that new
     * calculations will be done the next time the children
     * need an allocation.
     *
     * @param child the child view
     * @param width true if the width preference should change
     * @param height true if the height preference should change
     */
    public void preferenceChanged(View child, boolean width, boolean height) {
/*G***testing table repaginate
	if (width) {
	    xValid = false;
	    xAllocValid = false;
	}
	if (height) {
	    yValid = false;
	    yAllocValid = false;
	}
*/
//G***del Debug.stackTrace(); //G***del
	super.preferenceChanged(child, width, height);
    }


		/**Returns the attributes to use when rendering the page. Since this view
			is just a placeholder, it returns the attributes of its parents, the
			<code>XMLPagedView</code> which created it.
		@return The attributes of the parent view.
		*/
		public AttributeSet getAttributes()
		{
			final View parentView=getParent();	//get the parent view
			return (parentView!=null) ? parentView.getAttributes() : null;	//return the parent's attributes, if we have a parent (we should), or null if we have no parent
		}

/*G***we might override this later for vertical alignment
				public float getAlignment(int axis) {
						if (axis == View.X_AXIS) {
								switch (justification) {
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
*/

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
/*G***fix
				public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
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
*/

		/**Each page in a <code>XMLPagedView</code> is a subset of the content
			in the parent <code>XMLPagedView</code>.
		@return The starting offset of this page, which is the starting offset of the
			view with the lowest starting offset
		@see View#getRange
		*/
/*G***del if not needed by descending from XMLBlockView
		public int getStartOffset()
		{
			int startOffset=Integer.MAX_VALUE;	//we'll start out with a high number, and we'll end up with the lowest starting offset of all the views
			final int numViews=getViewCount();	//find out how many view are on this page
			for(int viewIndex=0; viewIndex<numViews; ++viewIndex)	//look at each view on this page
			{
				final View view=getView(viewIndex);	//get a reference to this view
				startOffset=Math.min(startOffset, view.getStartOffset());	//if this view has a lower starting offset, use its starting offset
			}
			return startOffset;	//return the starting offset we found
		}
*/

		/**Each page in a <code>XMLPagedView</code> is a subset of the content
			in the parent <code>XMLPagedView</code>.
		@return The ending offset of this page, which is the ending offset of the
			view with the largest ending offset
		@see View#getRange
		*/
/*G***del if not needed by descending from XMLBlockView
		public int getEndOffset()
		{
			int endOffset=0;	//start out with a low ending offset, and we'll wind up with the largest ending offset
			final int numViews=getViewCount();	//find out how many view are on this page
			for(int viewIndex=0; viewIndex<numViews; ++viewIndex)	//look at each view on this page
			{
				final View view=getView(viewIndex);	//get a reference to this view
				endOffset=Math.max(endOffset, view.getEndOffset());	//if this view has a larger ending offset, use that instead
			}
			return endOffset;	//return the largest ending offset we found
		}
*/

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
		/**Performs layout for the minor axis of the page (usually the left-to-right
			X axis).
		@param targetSpan the total span given to the view, which
			whould be used to layout the children.
		@param axis the axis being layed out.
		@param offsets the offsets from the origin of the view for
			each of the child views.  This is a return value and is
			filled in by the implementation of this method.
		@param spans the span of each child view.  This is a return
			value and is filled in by the implementation of this method.
		@returns the offset and span for each child view in the
			offsets and spans parameters.
		*/
		//G***check all this
/*G***del if not needed
		protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans)
		{
			baselineLayout(targetSpan, axis, offsets, spans);	//do the default baseline layout
		}
*/

/*G***del if we don't need
		//G***check, comment
		protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r)
		{
			return baselineRequirements(axis, r); //G***does this have anything to do with what we want to return?
		}
*/

		/**Returns the index of the child at the given model position for this page.
		@param pos The position (>=0) in the model.
		@return The index of the view representing the given position, or -1 if there
			is no view on this page which represents that position.
		*/
		protected int getViewIndexAtPosition(int pos)
		{
//G***del Debug.trace("XMLPagedView.getViewIndexAtPosition(): pos: "+pos+" startOffset: "+this.getStartOffset()+" endOffset: "+this.getEndOffset());
			// This is expensive, but are views are not necessarily layed
			// out in model order.

			if(pos<this.getStartOffset() || pos>=this.getEndOffset())	//if the position is before or after the content this page holds
				return -1;	//show that the given position is not on this page
			for(int viewIndex=getViewCount()-1; viewIndex>=0; --viewIndex)	//look at all the views from the last to the first (as the JDK comments note, this is an expensive operation, but there's no guarantee the views are laid out in model order)
			{
				final View view=getView(viewIndex);	//get a reference to this view
				if(pos>=view.getStartOffset() && pos<view.getEndOffset())	//if this view holds the requested position
					return viewIndex;	//return the index to this view
			}
			return -1;	//if we make it to this point, we haven't been able to find a view with the specified position
		}
	}


	/**
	 * Determines the preferred span for this view along an
	 * axis.
	 *
	 * @param axis may be either View.X_AXIS or View.Y_AXIS
	 * @returns  the span the view would like to be rendered into.
	 *           Typically the view is told to render into the span
	 *           that is returned, although there is no guarantee.
	 *           The parent may choose to resize or break the view.
	 * @see View#getPreferredSpan
	 */
/*G***fix
	public float getPreferredSpan(int axis)
	{
		if(axis==X_AXIS)	//G***check, change to flowaxis or something
			return layoutSpan;	//G***testing
		else
			return super.getPreferredSpan(axis);
*/
/*G***fix; testing
			float maxpref = 0;
			float pref = 0;
			int n = getViewCount();
			for (int i = 0; i < n; i++) {
		View v = getView(i);
		pref += v.getPreferredSpan(axis);
		if (v.getBreakWeight(axis, 0, Short.MAX_VALUE) >= ForcedBreakWeight) {
				maxpref = Math.max(maxpref, pref);
				pref = 0;
		}
			}
			maxpref = Math.max(maxpref, pref);
			return maxpref;
*/
//G***fix	}



	/* ***XMLPagedView.PageFlowStrategy*** */

		/**
		 * Strategy for maintaining the physical form
		 * of the flow.  The default implementation is
		 * completely stateless, and recalculates the
		 * entire flow if the layout is invalid on the
		 * given FlowView.  Alternative strategies can
		 * be implemented by subclassing, and might
		 * perform incrementatal repair to the layout
		 * or alternative breaking behavior.
		 */
//G***testing
	/**Strategy for paginating views from the layout pool and putting them into
		child views. This strategy is stateful, keeping track of last layout
		position and related information.
	*/
//G***del threadlayout	public static class PageFlowStrategy extends XMLFlowView.FlowStrategy
	public class PageFlowStrategy extends XMLFlowView.FlowStrategy implements Runnable
	{

		/**Whether or not the layout process should stop; used for threading.*/
		protected boolean stopLayout=false;	//(newswing threadlayout)

		/**The index of the last row that was laid out; used for threading.*/
//G***del		private int lastLayoutRowIndex=-1;	//(newswing threadlayout)

		/**The index of the last page index that was laid out; used for threading.*/
//G***del		private int currentPageIndex=-1;	//(newswing threadlayout)

		/**The view being flowed; used for threading.*/
		private XMLPagedView flowView=null;	//(newswing threadlayout)

		/**The offset at which layout is occurring.*/
		private int offset;

			/**@return The offset at which layout is occurring.*/
			protected int getOffset() {return offset;}

			/**Sets the offset at which layout is occurring.
		  @param newOffset The new offset at which layout is occurring.
		  */
			protected void setOffset(final int newOffset) {offset=newOffset;}

		/**The page index being laid out.*/
		private int pageIndex=0;

		  /**@return The page index being laid out.*/
			protected int getPageIndex() {return pageIndex;}

		  /**Sets the page index being laid out.
			@param newPageIndex The new index of the page being laid out.
		  */
			protected void setPageIndex(final int newPageIndex) {pageIndex=newPageIndex;}


//G***del int p0=0; //G***testing

//G***del PagePoolView segmentLayoutPool=null;  //G***testnig

//G***del int pageIndex=0;  //G***fix

		/**Resets the stateful flowing information so that layout will occur at the
		  beginning of the model the next time <code>layout()</code> is called.
		@see #layout
		*/
		public void reset()
		{
			offset=0; //start at the beginning
//G***del			segmentLayoutPool=null; //remove any pooled information
			setPageIndex(0);  //show that we're starting at the first page index
		}

		/*Constructor that accepts the view to be flowed.
		@param newFlowView The view to be flowed.
		*/
		public PageFlowStrategy(final XMLPagedView newFlowView)	//(newswing threadlayout)
		{
			flowView=newFlowView;	//save a reference to the view to be flowed
		}

		public void run()	//G***testing threadlayout
		{
try
{
Debug.trace("Inside layout thread, ready to begin layout.");
			layout(flowView);
Debug.trace("Inside layout thread, just finished layout.");
			if(!stopLayout)	//if the layout wasn't interruped
				flowView.onLayoutComplete();
Debug.trace("Inside layout thread, exiting run() method.");
}
catch(Throwable throwable)  //G***fix; testing
{
System.err.println("caught error in PageFlowStrategy.run(): "+throwable);
throwable.printStackTrace();
}
		}

		/**Indicates if a page has been laid out, which includes checking its
		  validity (i.e. that is it not below zero).
		  This does not need to be synchronized,
			as it	simply checks an atomic variable. The only race condition that could
			occur is that the row could be laid out before the function returns, in
			which case <code>false</code> would be incorrectly returned, meaning
			painting would simply occur slightly later.
		@param pageIndex The index of the page to check.
		@return <code>true</code> if the row indicated has been laid out.
		*/
		public boolean isLaidOut(final int pageIndex)	//(newswing threadlayout)
		{
//G***del Debug.trace("layout page index: ", getPageIndex());  //G***del
			return pageIndex>=0 && pageIndex<getPageIndex(); //show whether or not this page is before the one we're laying out, meaning it has already been laid out
//G***del			return rowIndex<=lastLayoutRowIndex;	//show whether or not we have a record of this row being laid out
		}

	/**
	 * Gives notification that something was inserted into the document
	 * in a location that the given flow view is responsible for.  The
	 * strategy should update the appropriate changed region (which
	 * depends upon the strategy used for repair).
	 *
	 * @param e the change information from the associated document
	 * @param alloc the current allocation of the view inside of the insets.
	 *   This value will be null if the view has not yet been displayed.
	 * @see View#insertUpdate
	 */
/*G***fix
				public void insertUpdate(FlowView fv, DocumentEvent e, Rectangle alloc) {
			// force layout, should do something more intelligent about
			// incurring damage and triggering a new layout.  This is just
			// about as brute force as it can get.
			if (alloc != null) {
		fv.setSize(alloc.width, alloc.height);
		Component host = fv.getContainer();
		host.repaint(alloc.x, alloc.y, alloc.width, alloc.height);
			} else {
		// use existing size, if it has been set
		int w = fv.getWidth();
		int h = fv.getHeight();
		if ((w > 0) && (h > 0)) {
				fv.setSize(w, h);
		}
			}
	}
*/

	/**
	 * Gives notification that something was removed from the document
	 * in a location that the given flow view is responsible for.
	 *
	 * @param e the change information from the associated document
	 * @param alloc the current allocation of the view inside of the insets.
	 * @see View#removeUpdate
	 */
/*G***fix
				public void removeUpdate(FlowView fv, DocumentEvent e, Rectangle alloc) {
			// force layout, should do something more intelligent about
			// incurring damage and triggering a new layout.
			if (alloc != null) {
		fv.setSize(alloc.width, alloc.height);
		Component host = fv.getContainer();
		host.repaint(alloc.x, alloc.y, alloc.width, alloc.height);
			} else {
		// use existing size, if it has been set
		int w = fv.getWidth();
		int h = fv.getHeight();
		if ((w > 0) && (h > 0)) {
				fv.setSize(w, h);
		}
			}
	}
*/

	/**
	 * Gives notification from the document that attributes were changed
	 * in a location that this view is responsible for.
	 *
	 * @param changes the change information from the associated document
	 * @param a the current allocation of the view
	 * @param f the factory to use to rebuild if the view has children
	 * @see View#changedUpdate
	 */
/*G***fix
				public void changedUpdate(FlowView fv, DocumentEvent e, Rectangle alloc) {
			// force layout, should do something more intelligent about
			// incurring damage and triggering a new layout.
			if (alloc != null) {
		fv.setSize(alloc.width, alloc.height);
		Component host = fv.getContainer();
		host.repaint(alloc.x, alloc.y, alloc.width, alloc.height);
			} else {
		// use existing size, if it has been set
		int w = fv.getWidth();
		int h = fv.getHeight();
		if ((w > 0) && (h > 0)) {
				fv.setSize(w, h);
		}
			}
	}
*/

		/**Updates the flow on the given <code>FlowView</code> This causes all of the
			views in the layout pool to be repaginated to match the given constraints
			for each row. This is called by <code>XMLPagedView</code> to update the pages.
		@param flowView The flow view which will be repaginated.
		*/
		public void layout(XMLFlowView flowView)
		{
			reset();  //reset the strategy
		  layout(flowView, null); //layout the flow view, specifying that we don't just want the flow layout information -- we want layout to actually occur
		}

/*G***del
int p0=0; //G***testing

PagePoolView segmentLayoutPool=null;  //G***testnig

int pageIndex=0;  //G***fix
*/

//G***del FlowRange flowRange=null;


		/**Calculates the flow on the given <code>FlowView</code>, causing all he
		  views in the layout pool to be repaginated to match the given constraints
			for each row. If a flow layout information object is given, this method
			updates the flow layout information; otherwise, this method adds the
			paginated views to the flow view as children.
			This is called by <code>XMLPagedView</code> to update the pages.
		@param flowView The flow view which will be repaginated.
		@param flowLayoutInfo A new flow layout information object to be updated
			with the pagination information, or <code>null</code> if the flow view
			itself should be updated by directly adding the paginated views as
			children.
		@return <code>true</code> if this call to <code>layout()</code> suceeeded
			in laying out all views for the content that the paged view represents.
		*/
		public boolean layout(final XMLFlowView flowView, final FlowLayoutInfo layoutInfo)
		{
//G***Del Debug.traceStack(); //G***del
Debug.trace();
//G***del if we can System.gc();  //G***testing ViewReleasable
//G***del if we can Thread.yield();  //G***testing ViewReleasable

//G***del Debug.trace(this, "layout() of paged view: "+flowView.getClass().getName());
/*G***del
Debug.trace("Inside XMLPagedView.PageFlowStrategy.layout()"); //G***fix
Debug.trace("View looks like this:"); //G***del
if(Debug.isDebug()) //G***del
	com.globalmentor.mentoract.ReaderFrame.displayView(flowView, 0, flowView.getDocument()); //G***del; place display logic somewhere better
*/


//G***bring back			fireMadeProgress(new ProgressEvent(flowView, PAGINATE_TASK, "Repaginating pages...", 0, 1));	//show that we are ready to start paginating pages, but we haven't really started, yet G***i18n

			stopLayout=false;	//show that we haven't been notified to stop, yet
//G***del; replaced by pageIndex			lastLayoutRowIndex=-1;	//show that we have not yet laid out any rows

//G***bring back; testing	    int p0 = flowView.getStartOffset();	//G***testing
		  int p0=getOffset(); //find out at which position we're doing the layout
	    int p1 = flowView.getEndOffset();
//G***del			int pageIndex=getPageIndex(); //see which page we're paginating now


//G***del if(flowRange==null) //G***testing
//G***del {
//G***del Debug.trace("Looking for flow range for position: ", p0);
	final FlowRange flowRange=flowLayoutInfo.getFlowRange(p0);  //find the range that matches this offset
//G***del Debug.trace("Found flow range: ", flowRange);

	if(flowRange==null) //G***testing
	{
		relayout();
		return true;
	}

	Debug.assert(flowRange!=null, "No flow range for position "+p0);
//G***del Debug.trace("Flow view: ", flowView);



		//get a page pool view from which to paginate
	final PagePoolView logicalView=getPagePoolView(flowRange.getStartOffset(), flowRange.getEndOffset());
/*G***del when works
	PagePoolView segmentLayoutPool=new PagePoolView(flowView.getElement(), flowView.getFlowAxis(), flowRange.getStartOffset(), flowRange.getEndOffset());	//create a pool for the pages G***we may want to hard-code this to Y_AXIS later; this mistake has caused hours of debugging
	segmentLayoutPool.setParent(flowView);  //G***testing
*/
//G***del	final ViewFactory factory=flowView
		//G***put these next lines in a PagePoolView.
/*G***del
	final Element flowElement=flowView.getElement();
	int endOffset=flowElement.getElement(flowElement.getElementCount()-1).getEndOffset();
	for(int i=0; i<flowElement.getElementCount(); ++i)
	{
		final Element childElement=flowElement.getElement(i);
		if(childElement.getStartOffset()>=0 && XMLStyleConstants.isPageBreakView(childElement.getAttributes()))
		{
			endOffset=childElement.getEndOffset();
			break;
		}
	}
	segmentLayoutPool.loadChildren(p0, endOffset);
*/
//G***del }


/*G***del when works, maybe
if(p0==0) //G***testing
	    flowView.removeAll();
*/


//G***bring back			final View logicalView=getLogicalView(flowView);	//get a reference to the logical view
//G***del			final View logicalView=segmentLayoutPool; //G***testing; comment
/*G***del
Debug.trace("Logical views look like like this:"); //G***del
if(Debug.isDebug()) //G***del
	com.globalmentor.mentoract.ReaderFrame.displayView(logicalView, 0, logicalView.getDocument()); //G***del; place display logic somewhere better
*/

//G***bring back if(p0==0) //G***testing
{


				//G***testing setParent()
//G***fix		  layoutPool.setParent(flowView);	//set the parent of the logical view pool, which will make it load its children

//G***del							hideView(pageView); //tell the view that it is being hidden

//G***del			hideView(logicalView); //G***testing; since we're getting ready to layout, make sure the view knows it should be hidden (this is necessary for applet views, for example)

	    final int numViews=logicalView.getViewCount();	//get the number of views
//G***del Debug.trace("Ready to look at: "+numViews+" paged view child views");
//G***del Debug.trace("Ready to look through views.");
				//even though XMLFlowView.layout() will do this as well, we need to set the parent (which will invalidate preferences down the hierarchy) *before* we set the size (G***see if we want to take code from XMLFlowView and put it here to keep this from being done twice)
			for(int i=0; i<numViews; ++i)	//look at each view and invalidate the layout (this is required for correct table row reflowing)
			{
				final View view=logicalView.getView(i); //get the child view
//G***del Debug.trace("view is of type: "+view.getClass().getName());
				ViewUtilities.hideView(view); //since we're getting ready to layout, make sure the view knows it should be hidden (this is necessary for applet views, for example)

//G***del Debug.trace("before setting parent, view parent is: ", view.getParent()!=null ? view.getParent().getClass().getName() : "null");
				  //G***do we really need this view.setParent()? it seems to work without out, at least initially
				view.setParent(logicalView);	//set this view's parent, which (with our Swing code changes) will invalidate the layout down the hierarchy
			}

//G***del Debug.trace("Ready to set size of logical view.");
			if(flowView instanceof XMLPagedView)	//if this is a paged view we're laying out
			{

//G***del logicalView.preferenceChanged(null, flowView.getFlowAxis()==X_AXIS, flowView.getFlowAxis()==Y_AXIS);	//G***testing
				//G***optimize: it may be that we want to set the width (or height, depending on flow direction) to a constant, and the other to a very high number; this way, doesn't pagination occur? this may not matter, though, since the logical view doesn't flow
//G***del Debug.trace("Before setting the logical view to width: "+(int)((XMLPagedView)flowView).getPageWidth()+" height: "+(int)((XMLPagedView)flowView).getPageHeight());	//G***del
				logicalView.setSize((int)((XMLPagedView)flowView).getPageWidth(), (int)((XMLPagedView)flowView).getPageHeight());	//make sure the layout pool has the correct dimensions of the page so that it will do unrestrained layout correctly
//G***del Debug.trace("After setting the logical view to width: "+(int)((XMLPagedView)flowView).getPageWidth()+" height: "+(int)((XMLPagedView)flowView).getPageHeight());	//G***del
			}
			else	//if this is just a normal flow view we're laying out
				logicalView.setSize(flowView.getWidth(), flowView.getHeight());	//make sure the layout pool has the correct dimensions so that it will do unrestrained layout correctly
//G***del Debug.trace("Finished setting size of logical view.");
}

//G***del		  int flowSegmentIndex=0; //this keeps track of how many flow segments (rows) we've paginated, regardless of how many we keep
		  int rowIndex=0; //indicates the literal child index at which the row will be added, *if* we add the paginated views to the flow view
		  //layout rows until we reach the end of our content
		  for(int flowSegmentIndex=0; p0<p1; flowSegmentIndex++)
			{
				final float progressMultiplier=(float)(p1+1)/(p0+1);  //find out how many times we should multiply our progress by to get our goal; the ratio of goal to progress
//G***del Debug.trace("progressMultiplier: "+progressMultiplier);
				final int estimatedLastPageIndex=((int)((pageIndex+1)*progressMultiplier))-1;  //get an estimate of the number of pages
				fireMadeProgress(new ProgressEvent(flowView, PAGINATE_TASK, "Paginating page "+(pageIndex+1)+" of ~"+(estimatedLastPageIndex+1)+"...", pageIndex, estimatedLastPageIndex+1));	//show that we are paginating the specified page, and the number of pages we guess there will be G***i18n
//G***del fireMadeProgress(new ProgressEvent(flowView, PAGINATE_TASK, "Paginating page "+pageIndex+"...", flowSegmentIndex+1, estimatedLastRowIndex+1));	//G***fix

/*G***del when works
				final View row=flowView.createRow();  //create a "row" representing a page
				flowView.append(row); //add that page
				int next=layoutRow(flowView, logicalView, rowIndex, p0, flowRange.getEndOffset()); //fit as much as we can on this page
				if (row.getViewCount()==0)  //if  nothing would fit on the page
				{
				  row.append(createView(flowView, logicalView, p0, Integer.MAX_VALUE, rowIndex));  //append a view so that we'll have something on the page
				  next=row.getEndOffset();  //for the next page, we'll start at the next position in the model
				}
*/
//G***del Debug.trace("Creating new row starting at flow range: ", flowRange.getStartOffset()); //G***del
//G***del Debug.trace("Creating new row ending at flow range: ", flowRange.getEndOffset()); //G***del
				//create the next row
				final View row=createRow(flowView, logicalView, p0, flowRange.getEndOffset(), layoutInfo==null);
				if(row==null) //if we ran out of content G***why would this happen? perhaps inaccessible content as part of the base hierarchy? This problem probably needs to be fixed closer to the root cause
				  break;  //stop trying to paginate
				final int next=row.getEndOffset();  //for the next page, we'll start at the next position in the model
//G***del Debug.trace("Finished paginating row: ", flowSegmentIndex+1);
//G***del Debug.trace("We think we're at page index: ", pageIndex);
//G***del Debug.trace("Next position is: ", next);
//G***del Debug.trace("Flow view end is: ", p1);
				if(layoutInfo!=null)  //if we're only supposed to give layout information
				{


/*G***del
//G***del; testing image repaint
	      final int logicalViewCount=logicalView.getViewCount();	//get the number of logical views
				for(int logicalViewIndex=0; logicalViewIndex<logicalViewCount; ++logicalViewIndex)	//look at each logical view to see if it's one that was removed
				{
				  final View childView=logicalView.getView(logicalViewIndex); //get the child view
					if(childView.getParent()!=logicalView)  //if this view doesn't have a parent (it was probably one that was just removed)
					{
//G***del Debug.trace("Reparenting view: "+view.getClass().getName());
						childView.setParent(logicalView);	//set this view's parent so that it will no longer be null
					}
				}
*/



					final int startOffset=row.getStartOffset(); //get the flow segment's start offset
					final int endOffset=row.getEndOffset(); //get the flow segment's ending offset
					layoutInfo.setStartOffset(pageIndex, startOffset);  //update this segment's start offset
					layoutInfo.setEndOffset(pageIndex, row.getEndOffset());  //update this segment's end offset
					layoutInfo.setLength(pageIndex+1);  //update the number of pages we have
//G***del when works					flowView.remove(rowIndex);  //remove the row from the flow view G***do we even need to add it in the first place, if we modify the other methods?



	//G***testing; make more efficient; comment
/*G***fix
					final int logicalViewCount=logicalView.getViewCount();	//get the number of logical views
					for(int logicalViewIndex=0; logicalViewIndex<logicalViewCount; ++logicalViewIndex)	//look at each logical view to see if it's one that was removed
					{
						final View childView=logicalView.getView(logicalViewIndex); //get the child view
						if(childView.getEndOffset()<startOffset)  //if this view in the layout pool can no longer be involved in paginations
							releaseView(childView); //ask this child to release its memory
//G***del						System.gc();  //G***testing ViewReleasable
//G***del						Thread.yield();  //G***testing ViewReleasable
					}
*/

				}
				else  //if we're actually adding rows to the flow view
					++rowIndex; //go to the next row
				if(flowView instanceof XMLPagedView)	//if this is a paged view we're laying out
				{
					((XMLPagedView)flowView).onPageLayoutComplete(pageIndex); //show that we've completed the layout of this page
				}
				++pageIndex;  //show that we're on the next page
//G***del				setPageIndex(pageIndex); //update our persistent page index record
				if(next>p0) //if we've advanced, as we should have
				{
//G***del Debug.trace("Advancing to position: ", next);

			    p0=next;  //update our position
					setOffset(p0);  //update our layout offset
//G***del Debug.trace("new p0: ", p0);
//G***del Debug.trace("new p1: ", p1);
					if(p0<p1 && p0>=flowRange.getEndOffset()) //G***testing; comment; comment important p0<p1
					{
//G***del						logicalView=null; //G***maybe pass this from method to method; fix
						return false;  //G***del; testing
					}
				}
				else  //if we've somehow gone backwards
				{
return false;	//G***fix		    throw new StateInvariantError("infinite loop in formatting");
				}
	    }
//G***remove the layout pool or something
//G***del or bring back; testing			super.layout(flowView);	//G***testing
//G***del			lastLayoutRowIndex=getViewCount()-1;	//at this point, we've laid out all the rows (newswing threadlayout) G***update for new flow segment info
//G***del Debug.trace("Updated lastLayoutRowIndex to: ", lastLayoutRowIndex);
			fireMadeProgress(new ProgressEvent(flowView, PAGINATE_TASK, "Paginated all "+getViewCount()+" pages.", getViewCount(), getViewCount()));	//show that we paginated all the pages G***i18n
//G***del Debug.trace("Ready to garbage collect");
//G***del if we can			System.gc();  //now that we've finished repaginating, try to garbage collect unused objects
//G***del Debug.trace("Garbage collected; returning true");
		  return true;  //show that we finished layout out all the information
		}

		//G***comment
		protected View createRow(final XMLFlowView flowView, final View layoutPoolView, final int startOffset, final int endOffset, final boolean isPersistent)
		{
//G***del Debug.trace("Requested to create row for start offset: ", startOffset);
//G***del Debug.trace("Requested to create row for end offset: ", endOffset);
			final View rowView=flowView.createRow();  //create a "row" representing a page
		  flowView.append(rowView); //add that page
			final int rowIndex=flowView.getViewCount()-1; //the row index will be that of the last view added
//G***del Debug.trace("added row end offset: ", rowView.getEndOffset());  //G***del
//G***del Debug.trace("ready to layout row ending: ", endOffset); //G***del
			layoutRow(flowView, layoutPoolView, rowView, rowIndex, startOffset, endOffset); //fit as much as we can on this page
			if(rowView.getViewCount()==0)  //if  nothing would fit on the page
			{
//G***del Debug.trace("nothing would fit on the page"); //G***del
				final View view=createView(flowView, layoutPoolView, startOffset, Integer.MAX_VALUE, -1); //try to create a view with as much information as possible
				if(view!=null)	//if got a view back (there is at least some information left) G***note that we added this check for null to stop a pagination problem when there was no information -- we probably should really fix the source of the problem, which might be a badly-formed default element hierarchy
					rowView.append(view);  //append a view so that we'll have something on the page G***fix -1 (rowIndex)
//G***del				next=row.getEndOffset();  //for the next page, we'll start at the next position in the model
			}
/*G***fix later
			if(isPersistent)  //G***fix; allow calling method to do this
				flowView.append(rowView); //add that page
*/

		  if(isPersistent)  //if we should keep the rows we lay out
			{
/*G***del
Debug.trace("created and keeping view:");
ViewUtilities.printView(rowView, Debug.getOutput());
*/
			  ViewUtilities.reparentHierarchy(rowView); //make sure all the child views have correct parents (previous layouts could cause, for instance, a paragraph row to think it has a parent of a now-unused paragraph fragement)
/*G***del
Debug.trace("after reparenting:");
ViewUtilities.printView(rowView, Debug.getOutput());
*/
			}
			else  //if we shouldn't keep the rows we lay out
			{
//G***del Debug.trace("removing page view with start offset: ", rowView.getStartOffset());  //G***del
				flowView.remove(rowIndex);  //remove the row from the flow view G***do we even need to add it in the first place, if we modify the other methods?
			}
			return rowView; //return the row we created
		}


		/**Creates a row of views that will fit within the layout span of the row.
			This is called by the layout method. This is overridden to update the record
			of the last row laid out for threading.
		@param rowIndex the index of the row to fill in with views. The
			row is assumed to be empty on entry.
		@param pos  The current position in the children of
			this views element from which to start.
		@param endOffset The last offset (noninclusive) to lay out.
		@return The position to start the next row.
		*/
		protected int layoutRow(XMLFlowView flowView, final View layoutPoolView, final View rowView, int rowIndex, int pos, final int endOffset)	//(newswing threadlayout)
		{
//G***del Debug.trace("Inside XMLPagedView.layoutRow(), rowIndex: ", rowIndex);	//G***del
//G***del; replaced by pageIndex			lastLayoutRowIndex=rowIndex-1;	//assume we've already laid out the row before this one
			if(stopLayout)	//if we should stop the layout process
			{
Debug.trace("We should stop now.");	//G***del
				final View row=flowView.getView(rowIndex);	//get a reference to this empty row that was added earlier in layout()
		    row.append(createView(flowView, layoutPoolView, pos, Integer.MAX_VALUE, rowIndex));	//G***comment
Debug.trace("Appended a big row.");	//G***del
				return flowView.getEndOffset();	//return the end offset of the view, pretending this row contains everything, in order to stop the layout process
			}
			else	//if we shouldn't stop, yet
				return super.layoutRow(flowView, layoutPoolView, rowView, rowIndex, pos, endOffset);	//do the default layout
		}

		/**Adjusts the given row (page) to fit within the layout span.
			By default this will try to find the highest break weight possible nearest
			the end of the row.  If a forced break is encountered, the break will be
			positioned there.
		@param flowView The view being paginated.
		@param rowIndex The index of the row to adjust to the current layout span.
		@param desiredSpan The current layout span (>=0).
		@param x The location the page starts at.
		*/
		protected void adjustRow(XMLFlowView flowView, final View layoutPoolView, int rowIndex, int desiredSpan, int x)
		{
//G***del Debug.trace("adjustRow() with rowIndex: "+rowIndex+" desiredSpan: "+desiredSpan);	//G***del
//G***del System.out.println("adjustRow() with rowIndex: "+rowIndex+" desiredSpan: "+desiredSpan);	//G***del
			final View row=flowView.getView(rowIndex);	//get a reference to the row (representing a page) at this index
			final int flowAxis=flowView.getFlowAxis();	//find out which axis we're flowing along
			final int viewCount=row.getViewCount();		//find out how many views there are on this page
//G***bring back			final View logicalView=getLogicalView(flowView);	//get a reference to the logical view
//G***del			final View logicalView=segmentLayoutPool; //G***testing; comment
			int span=0;	//G***comment these
			int bestWeight=BadBreakWeight;
			int bestSpan=0;
			int bestIndex=-1;
			int bestOffset=0;
//G***del			View view;	//this will hold each view we look at
			for(int viewIndex=0; viewIndex<viewCount; ++viewIndex)
			{
				final View view=row.getView(viewIndex);	//get a reference to this view
//G***del Debug.trace("adjustRow() looking at view: "+viewIndex+" with preferredSpan: "+view.getPreferredSpan(flowAxis));	//G***del
//G***del System.out.println("adjustRow() looking at view: "+viewIndex+" with preferredSpan: "+view.getPreferredSpan(flowAxis));	//G***del

/*G***del
				for(int i=0; i<view.getViewCount(); ++i)	//G***del
System.out.println("  child view: "+i+" preferredSpan: "+view.getView(i).getPreferredSpan(flowAxis));	//G***del
*/

				final int spanLeft=desiredSpan-span;	//find out how much room is left
				final int breakWeight=view.getBreakWeight(flowAxis, x+span, spanLeft);	//get the break weight for this view based upon the amount of room left

				final AttributeSet attributeSet=view.getAttributes(); //get the view's attributes
				  //see how the view considers breaking after it
//G***fix				final String pageBreakAfter=XMLCSSStyleConstants.getPageBreakAfter(attributeSet);
				  //see how the view considers breaking before it
				final String pageBreakBefore=XMLCSSStyleUtilities.getPageBreakBefore(attributeSet);
/*G***fix; not having this only works for page-break-before because the pre-paginate information gather already divides page breaks
						//if this view always wants page breaks before it, and this isn't the first view in the row
				if(viewIndex>0 && XMLCSSConstants.CSS_PAGE_BREAK_BEFORE_ALWAYS.equals(pageBreakBefore))
				{
					bestWeight=ForcedBreakWeight; //show that we're being forced to break
					bestIndex=viewIndex;  //G***testing
					bestSpan=span;  //G***del
					break;  //G***testing
				}
				else
*/
				if(breakWeight>=bestWeight)	//if this is as good or better a place to break than we've found so far
				{
					bestWeight=breakWeight;	//show that we've found a new weight to break on
					bestIndex=viewIndex;	//show that we've found a new index to break at
//G***del System.out.println("adding "+span+" to bestSpan");	//G***del
					bestSpan=span;		//show that we've found a new best space to cover
					if(breakWeight>=ForcedBreakWeight)	//if this break weight means a break should be forced
						break;	//don't look for any more places to break
				}
				span+=view.getPreferredSpan(flowAxis);	//find out how much room this view wants to take up
			}
			if(bestIndex<0)	//if we couldn't find anything else to break
				return;	//leave everything the way it is
			int spanLeft=desiredSpan-bestSpan;	//since we found a place to break, subtract that view's space from the space we have left
//G***del Debug.trace("best view to break: ", bestIndex); //G***del
			final View breakView=row.getView(bestIndex);	//get the view to break on
//G***del Debug.trace("break view is a: ", breakView.getClass().getName()); //G***del
				//see if the view we'll break fits in the space provided
			final boolean breakViewFits=breakView.getPreferredSpan(flowAxis)<=spanLeft;
				//ignoring for the moment the condition of if the view can be broken,
				//  if we have to break on a boundary, it will be after this view if it fits
				//  and if it doesn't fit we'll break before the view
//G***del		  int breakBeforeIndex=breakViewFits ? bestIndex+1 : bestIndex;
			final View brokenView=breakView.breakView(flowAxis, breakView.getStartOffset(), x+bestSpan, spanLeft);	//break the view
//G***del Debug.trace("Broke view is a: ", brokenView.getClass().getName());
//G***del Debug.trace("Broke view has an ending: ", brokenView.getEndOffset());
//G***del Debug.trace("Broke view, has a preferred span of: "+brokenView.getPreferredSpan(flowAxis));
//G***del Debug.trace("span left: ", spanLeft); //G***del
//G***we need to do something somewhere up here for the case in which the
//breaking point naturally falls between views
			View[] replacementViewArray;	//we'll place the replacement views, if any, in the array we create for this variable
			//G***this works! maybe make it check to make sure there is at least one view on the page; actually, the function before this probably takes care of checking that
			if(brokenView.getPreferredSpan(flowAxis)>spanLeft)	//if the view we broke still can't fit within this view
			{
				replacementViewArray=null;
//G***del when works				replacementViewArray=new View[0];
//G***del				--breakBeforeIndex;  //show that we're now breaking
			}
			else
			{
				replacementViewArray=new View[1];	//create a new array
				replacementViewArray[0]=brokenView;	//put our view in the array
//G***del				breakBeforeIndex=-1;  //show that we're not breaking on the border between views
			}
				//If we're not replacing the view with anything, or if the original
				//  view fit, that means we're breaking on a boundary; make sure it's
				//  a boundray on which a page break can be made.
			if(replacementViewArray==null || breakViewFits)
			{
			  int breakBeforeIndex=breakViewFits ? bestIndex+1 : bestIndex; //see if we're breaking before or after the view
				while(breakBeforeIndex>0) //if there are views before this one, make sure they don't care if we break after them{
				{
					final View previousView=row.getView(breakBeforeIndex-1);	//get a reference to the view before the one being broken
					final AttributeSet attributeSet=previousView.getAttributes(); //get the view's attributes
					  //see how the view considers breaking after it
					final String pageBreakAfter=XMLCSSStyleUtilities.getPageBreakAfter(attributeSet);
						//if we should avoid breaking after this view
					if(XMLCSSConstants.CSS_PAGE_BREAK_AFTER_AVOID.equals(pageBreakAfter))
						--breakBeforeIndex; //we'll try breaking sooner
					else  //if the view before doesn't mind a break after it
						break;  //this break will work for us
				}
					//if we found a border for which the previous view didn't mind breaking,
					//  and it wasn't the first view, and it's not more than where we were
					//  breaking already (the latter is a special case meaning the view
					//  fit originally, so don't disturb the replacement view array)
				if(breakBeforeIndex>0 && breakBeforeIndex<=bestIndex)
				{
					bestIndex=breakBeforeIndex; //we'll now break on this border
					replacementViewArray=null;  //don't replace the view with anything, since we're breaking on a border
				}
			}
		  final int adjustOffset=bestIndex; //find the index at which we should do the adjustment
			final int adjustLength=viewCount-bestIndex; //we'll remove all the views in this row starting with our adjustment index

				//G***we probably should instead have a replace() method here that returns the views to the pool directly instead of afterwards
			row.replace(adjustOffset, adjustLength, replacementViewArray);	//replace the extra views with our broken one
//G***del			row.replace(bestIndex, viewCount-bestIndex, viewArray);	//replace the extra views with our broken one
/*G***del
		  for(int removedViewIndex=0; removedViewIndex<removedViews.length; ++removedViewIndex) //look at all the views we removed to see if any of them should be reparented back into the pool
			{
				final View removedView=removedViews[removedViewIndex]; //get a reference to this removed view
*/
				  //G***do we want to replace this with ViewUtilities.reparentNullHierarchy()?
	      final int logicalViewCount=layoutPoolView.getViewCount();	//get the number of logical views
				for(int logicalViewIndex=0; logicalViewIndex<logicalViewCount; ++logicalViewIndex)	//look at each logical view to see if it's one that was removed
				{
				  final View childView=layoutPoolView.getView(logicalViewIndex); //get the child view
					if(childView.getParent()==null)  //if this view doesn't have a parent (it was probably one that was just removed)
					{
//G***del Debug.trace("Reparenting view: "+view.getClass().getName());
						childView.setParent(layoutPoolView);	//set this view's parent so that it will no longer be null
					}
				}
		}


	/**
	 * Creates a view that can be used to represent the current piece
	 * of the flow.  This can be either an entire view from the
	 * logical view, or a fragment of the logical view.
	 *
	 * @param fv the view holding the flow
	 * @param startOffset the start location for the view being created
	 * @param spanLeft the about of span left to fill in the row
	 * @param rowIndex the row the view will be placed into
		@return A view covering the requested area, or <code>null</code> if there is
			no view that contains the requested model position.
		*/
//G***do we even need rowIndex?
	protected View createView(XMLFlowView fv, final View layoutPoolView, int startOffset, int spanLeft, int rowIndex)
	{
//G***del Debug.traceStack(); //G***del

	    // Get the child view that contains the given starting position
//G***bring back	    View lv = getLogicalView(fv);
//G***del			final View lv=segmentLayoutPool; //G***testing; comment
//G***del Debug.trace("startOffset: ", startOffset);
//G***del Debug.trace("logical view start offset: ", layoutPoolView.getStartOffset());
//G***del Debug.trace("logical view end offset: ", layoutPoolView.getEndOffset());
//G***del Debug.trace("logical view is of class: ", layoutPoolView.getClass().getName());
	    int childIndex = layoutPoolView.getViewIndex(startOffset, Position.Bias.Forward);
//G***del Debug.trace("childIndex: ", childIndex);

			if(childIndex<0)  //if there is no view in the pool that contains this offset (this can happen with the page-break-before behavior, for example)
				return null;  //show that we have no view that covers the requested model position

	    View v = layoutPoolView.getView(childIndex);

	    if (startOffset==v.getStartOffset()) {
		// return the entire view
		return v;
	    }

	    // return a fragment.
//G***del Debug.trace("there's no view that starts at this location; we'll create a fragment at start offset: ", startOffset);
//G***del Debug.trace("our fragment will end: ", v.getEndOffset());
	    v = v.createFragment(startOffset, v.getEndOffset());
	    return v;
		}


	}

	/**Information about a range that can be flowed independently of other
		sections.
	@author Garret Wilson
	*/
	protected static class FlowRange
	{
		/**The start of this range.*/
		private int startOffset;

		  /**@return The start of this range.*/
			public int getStartOffset() {return startOffset;}

		/**The non-inclusive end of this range.*/
		private int endOffset;

		  /**@return The non-inclusive end of this range.*/
			public int getEndOffset() {return endOffset;}

		/**Creates a range with new starting and ending offsets.
		@param newStartOffset This range's starting offset.
		@param newEndOffset This range's ending offset.
		*/
		public FlowRange(final int newStartOffset, final int newEndOffset)
		{
			startOffset=newStartOffset; //save the starting offset
			endOffset=newEndOffset; //save the ending offset
		}

		/**@return A string representation of this flow range.*/
		public String toString()
		{
			return getClass().getName()+" ["+getStartOffset()+", "+getEndOffset()+"]";
		}
	}

	/**Lightweight object containing information about the flow layout offsets and
		groups of flow layout offsets. This class adds the functionality of
		determining groups of flow segments that can be repaginated independently
		of the others.
	@author Garret Wilson
	*/
	public static class PageFlowLayoutInfo extends XMLFlowView.FlowLayoutInfo
	{
		/**The list of ranges that can be flowed independently of the others.*/
		private java.util.List flowRangeList=new ArrayList();

		/**Adds a flow range to the list.
		@flowRange The range to add to the list of flow ranges.
		*/
		public void addFlowRange(final FlowRange flowRange)
		{
			flowRangeList.add(flowRange);
		}

		/**Returns the range that includes the given offset.
		@param offset The offset for which to locate a range.
		@return The range that includes the offset, or <code>null</code> if a range
			could not be found for the given offset.
		*/
		public FlowRange getFlowRange(final int offset)
		{
			final Iterator iterator=flowRangeList.iterator(); //get an iterator to look through the ranges
			while(iterator.hasNext()) //while there are more ranges
			{
				final FlowRange flowRange=(FlowRange)iterator.next(); //get the next flow range
//G***del Debug.trace("Looking at flow range: ", flowRange);  //G***del
				if(offset>=flowRange.getStartOffset() && offset<flowRange.getEndOffset()) //if this offset lies within this range
					return flowRange; //return this flow range
			}
			return null;  //show that we couldn't find a matching range
		}


	}


	/**Runnable object which allows a section of the flow to be laid out in
		the AWT thread.
	@see #setPaginating
	@see #PageFlowStrategy
	*/
	protected class FlowRangeLayerOut implements Runnable
	{

		/**The view to lay out.*/
		private final XMLFlowView flowView;

		/**The layout strategy to be used for laying out the next range.*/
		private final PageFlowStrategy pageFlowStrategy;

		/**The object containing the layout information to be gathered.*/
		private final FlowLayoutInfo flowLayoutInfo;

		/**Whether layout should be reset before it starts.*/
		private boolean shouldResetLayout;

		/**Creates an object that can lay out a range of a view in another thread
		  using the specified strategy.
		@param view The view a range of which should be reflowed.
		@param strategy The strategy to use to lay out the view.
		@param layoutInfo The object which will receive layout information, or
			<code>null</code> if persistent layout should take place.
		@param reset Whether the layout should be reset before layout begins; in
			other words, <code>true</code> if this is the first step in the layout
			process.
		*/
		public FlowRangeLayerOut(final XMLFlowView view, final PageFlowStrategy strategy, final FlowLayoutInfo layoutInfo, final boolean reset)
		{
			flowView=view;  //save the view
			pageFlowStrategy=strategy;  //save the strategy
			flowLayoutInfo=layoutInfo;  //save the layout info object
			shouldResetLayout=reset;  //save whether we should reset the layout
		}

		/**Lays out a range of the flow using the page flow strategy. If this range
		  completes our range, paginating is indicated to have finished; otherwise,
			another class is queued into the AWT thread for layout out the next
			range.
		 */
		public void run()
		{
try
{
Debug.trace();
		  if(shouldResetLayout) //if we should reset the layout (i.e. this is the first layout call)
				pageFlowStrategy.reset(); //reset the strategy so that we'll start laying out information at the beginning
			final boolean finishedLayout=pageFlowStrategy.layout(flowView, flowLayoutInfo);	//G***testing; comment
		  if(finishedLayout)  //if we finished the layout //G***probably put this in onPagelayoutComplete or something
			{
				Debug.trace("Finished paged view layout");
				setXAllocValid(true);	//show that our horizontal allocation is valid again
				setYAllocValid(true);	//show that our vertical allocation is valid again
				setPaginating(false); //show that we're not paginating anymore
			}
			else  //if we haven't finished
			{
//G***del Debug.notify("getting ready to fire page even with page index: "+getPageIndex()); //G***del
/*G***del when works
				//fire a page event with our current page number, since our page number changed
				firePageEvent(new PageEvent(XMLPagedView.this, getPageIndex(), getPageCount()));
//G***del				PageIndex=newPageIndex;	//actually change the page number
				final Container container=getContainer();	//get a reference to our container
				if(container!=null)	//if we're in a container
					container.repaint();	//repaint our container G***check
*/
				//create another instance of ourselves and queue it to layout the next section in the AWT thread
				SwingUtilities.invokeLater(new FlowRangeLayerOut(flowView, pageFlowStrategy, flowLayoutInfo, false));
			}
}
catch(Throwable throwable)  //G***fix; testing
{
System.err.println("caught error in FlowRangeLayerOut.run(): "+throwable);
throwable.printStackTrace();
}

		}
	}

	/**Class for iterating through child elements, G***del if not needed
	private class ChildIterator
	{


	}
	*/

}

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
import javax.mail.internet.ContentType;
import com.garretwilson.lang.JavaUtilities;
import com.garretwilson.swing.XMLTextPane;	//G***del when we can find a better place to set the paged view variable of XMLTextPane
import com.garretwilson.swing.event.PageEvent;
import com.garretwilson.swing.event.PageListener;
import com.garretwilson.swing.event.ProgressEvent;
import com.garretwilson.swing.event.ProgressListener;
import com.garretwilson.swing.text.AnonymousElement;
//G***del if not needed import com.garretwilson.swing.text.ViewHidable;
import com.garretwilson.swing.text.ContainerView;
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
	because the latter hides a lot of layout-related variables, which we need
	to update since we extend some of the functionality of <code>BoxView</code>.</p>
	<p>This class, before hiding a page of views, informs each view that implements
	<code>ViewHidable</code> that it is about to be hidden.</p>
@see com.garretwilson.swing.text.ViewHidable
@see FlowView
*/
//G***fix public class XMLPagedView extends XMLFlowView
public class XMLPagedView extends FlowView
{

	/**The task of paginating a document.*/
	public final static String PAGINATE_TASK="PAGINATE";

	/**The list of page event listeners.*/
	private EventListenerList pageListenerList=new EventListenerList();

	/**The list of progress event listeners.*/
	private EventListenerList progressListenerList=new EventListenerList();

	/**The font used for painting page number.*/
	private final static Font PAGE_NUMBER_FONT=new Font("Serif", Font.PLAIN, 12); //G***fix

	/**The insets for each page.*/
	private short pageTopInset, pageLeftInset, pageBottomInset, pageRightInset; 

	/**Sets the insets for each page.
	@param top The page top inset (>=0).
	@param left The pageleft inset (>=0).
	@param bottom The page bottom inset (>=0).
	@param right The page right inset (>=0).
	*/
	protected void setPageInsets(final short top, final short left, final short bottom, final short right)
	{
		pageTopInset=top;
		pageLeftInset=left;
		pageRightInset=right;
		pageBottomInset=bottom;
	}

	/**@return The left inset of each page (>=0).*/
	protected short getPageLeftInset() {return pageLeftInset;}

	/**@return The right inset of each page (>=0).*/
	protected short getPageRightInset() {return pageRightInset;}

	/**@return The top inset of each page (>=0).*/
	protected short getPageTopInset() {return pageTopInset;}

	/**@return The bottom inset of each page (>=0).*/
	protected short getPageBottomInset() {return pageBottomInset;}

	/**Constructor specifying an element.
	@param element The element this view is responsible for.
	*/
	public XMLPagedView(Element element)
	{
		super(element, View.X_AXIS);	//construct the parent, showing that we're tiled along the X axis (but flowing vertically)
//G***del if we don't need		setPropertiesFromAttributes();	//set our properties from the attributes
		//G***fix setting flow stategy if we need to
//G***fix threadlayout		strategy=new PageFlowStrategy();	//G***testing
//TODO fix		strategy=new PageFlowStrategy(this);	//create a flow strategy which may or may not be used in a threaded way (newswing threadlayout)

strategy=new PaginateStrategy();	//G***testing

		setPageInsets((short)25, (short)25, (short)25, (short)25);	//set the page insets TODO allow this to be customized
	}


	/*G***fix
A copy of our...
	private Graphics paintGraphics=null;
	private Shape paintAllocation=null;
*/

	/**@return The layout pool for the pages.*/
	protected View getPagePoolView() {return layoutPool;}

	/**Sets the layout pool for the pages.
	@param logicalView The logical view from which pages will be created.
	*/
	protected void setPagePoolView(final View logicalView) {layoutPool=logicalView;}

	/**A temporary rectangle object used for painting.*/
	protected Rectangle TempRectangle=new Rectangle();

	/**@return The number of pages this view contains.*/
	public int getPageCount()
	{
		return getViewCount();  //return the number of child views we have
//G***fix		return getViewCount();  //return the number of child views we have
//G***del Debug.trace("getPageCount(): ", flowLayoutInfo!=null ? flowLayoutInfo.getLength() : -50);
		  //return the number of pages we've paginated or, if we have no layout info, return zero
//TODO fix		return flowLayoutInfo!=null ? flowLayoutInfo.getLength() : 0;
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
	private float pageWidth;

		/**@return The width of each page.*/
		public float getPageWidth() {return pageWidth;}

	/**The height of each page.*/
	private float pageHeight;

		/**@return The height of each page.*/
		public float getPageHeight() {return pageHeight;}

	/**Sets the size of the pages.
	@param width The new page width.
	@param height The new page height.
	*/
	protected void setPageSize(final float width, final float height)
	{
		pageWidth=width;	//set the width
		pageHeight=height;	//set the height
			//make sure the layout pool has the correct dimensions of the page so that it will do unrestrained layout correctly
		getPagePoolView().setSize((int)width-getPageLeftInset()-getPageRightInset(), (int)height-getPageTopInset()-getPageBottomInset());	//set the size of the page pool to be exactly the size of the displayed page; giving insets to the page pool results in incorrect layout
	}


		//G***comment
		//G***comment or -1 if...
		public int getPageIndex(final int pos)
		{
//TODO fix			return flowLayoutInfo.getFlowSegmentIndex(pos); //use our calculated offsets to find which page this position represents
			final int viewCount=getViewCount();	//find out how many child views we have
			for(int i=0; i<viewCount; ++i)		//look at each child view
			{
				final View childView=getView(i);	//G***testing
//	G***del			final Element childElement=childView.getElement();	//G***testing
//	G***del Debug.trace("Child view "+i+" is "+childView.getClass().getName());	//G***del
//	G***del Debug.trace("For page: "+i+" the start offset is: "+childView.getStartOffset()+" end offset is: "+childView.getEndOffset());
				if(pos>=childView.getStartOffset() && pos<childView.getEndOffset())	//G***testing
					return i;
			}
			return -1;	//G***testing
		}

		//G***fix this with the correct modelToView() stuff
		public int getPageStartOffset(final int pageIndex)
		{
//TODO fix			return isLaidOut(pageIndex) ? flowLayoutInfo.getStartOffset(pageIndex) : -1;
			final View childView=getView(pageIndex);	//get the view for the given page
			return childView!=null ? childView.getStartOffset() : -1;  //return the start offset of the page, or -1 if there is no such page G***testing
		}

		//G***fix this with the correct modelToView() stuff
		public int getPageEndOffset(final int pageIndex)
		{
//TODO fix			return isLaidOut(pageIndex) ? flowLayoutInfo.getEndOffset(pageIndex) : -1;
			final View childView=getView(pageIndex);	//get the view for the given page
			return childView!=null ? childView.getEndOffset() : -1;  //return the start offset of the page, or -1 if there is no such page G***testing
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

	/**Returns whether or not a page index has been laid out.
		This is synchronized so that the
		strategy will never by set to null while it is being checked.
		@return <code>true</code> if the indicated view has been laid out.
		@see #onLayoutComplete
	//G***we need to change this to make sure that the page given is valid
	*/
	public synchronized boolean isLaidOut(final int pageIndex)	//(newswing threadlayout)
	{
		return pageIndex>=0 && pageIndex<getViewCount();
/*G***fix all this		
		//if we don't thread or if we're currently not threading, the view is laid out; if we are threading, ask the flow strategy for the answer G***what if the rowIndex is out of bounds?
	//G***del System.out.println("XMLPagedView.isLaidOut() rowIndex: "+rowIndex+" layoutStrategyThread not null: "+(layoutStrategyThread!=null)+" strategy not null: "+(strategy!=null));	//G***del
		return ((PageFlowStrategy)strategy).isLaidOut(pageIndex);  //ask the strategy whether the page is laid out G***fix cast
	//G***del		return !isThreaded() || layoutStrategyThread==null || ((PageFlowStrategy)strategy).isLaidOut(rowIndex);
*/
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
		return pageIndex;
		/*G***fix
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
*/
	}

	/**Retrieves a page by its index. The paged view is first checked to see if
		the page has already been paginated and placed as a child view. If not, the
		page is paginated and placed as a child view.
	@param The paginated page, cached as a child view of this view.
	*/
	protected Page getPage(final int pageIndex) //G***change Page to PageView
	{
		return (Page)getView(pageIndex);	//G***testing
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
/*TODO fix
	public void relayout()
	{
		while(getViewCount()>0) //while there are more views
			remove(0);  //remove "cached" child views until there are no more
		clearPagePoolViews(); //clear all of our cached page pool views
		super.relayout(); //do the default relayout, which actually invalidates the views and repaints the component
	}
*/

	/**Because a paged view merely uses its child views as cached pages, it is not
		interested in knowing if their preferences have changed. Furthermore,
		because we are the parent view of layout pool views, we should not invalidate
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
	public void insertUpdate(DocumentEvent changes, Shape area, ViewFactory viewFactory) {} //G***maybe later fix these to update appropriate cached layout pools

	/**Because a paged view merely uses its child views as cached pages, it is not
		interested in knowing if have had updates. This method therefore does
		nothing.
	@param changes The event containing change information.
	@param area The area being changed.
	@param viewFactory The view factory responsible for creating views
	*/
	public void removeUpdate(DocumentEvent changes, Shape area, ViewFactory viewFactory) {}

	/**Because a paged view merely uses its child views as cached pages, it is not
		interested in knowing if have had updates. This method therefore does
		nothing.
	@param changes The event containing change information.
	@param area The area being changed.
	@param viewFactory The view factory responsible for creating views
	*/
	public void changedUpdate(DocumentEvent changes, Shape a, ViewFactory f) {}

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
Debug.trace("Ready to paint page "+pageIndex+" with parent: ", pageView.getParent()!=null ? pageView.getParent().getClass().getName() : "null"); //G***del
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
				final int pageLeftInset=getPageLeftInset();  //get the page's left inset
				final int pageRightInset=getPageRightInset();  //get the page's right inset
				final int pageBottomInset=getPageBottomInset();  //get the page's right inset
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
/*G***test refactor
				setXAllocValid(true);	//show that our horizontal allocation is valid again
				setYAllocValid(true);	//show that our vertical allocation is valid again
*/

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
		assert childView instanceof Page : "Expected a page child.";
		final Page page=(Page)childView;	//cast the child view to a page
		if(getFlowAxis()==X_AXIS)	//if we're flowing horizontally
		{
			adjustAmount=getPageLeftInset()+getPageRightInset();	//add the left and right page insets together
		}
		else	//if we're flowing vertically
		{
			adjustAmount=getPageTopInset()+getPageBottomInset();	//add the top and bottom page insets together
		}
			//G***put this line in a lower view such as XMLBlockView
//G***testing		return layoutSpan-adjustAmount;	//return the default span, accounting for any insets
		return (int)getPageHeight()-adjustAmount;
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
		assert childView instanceof Page : "Expected a page child.";
		final Page page=(Page)childView;	//cast the child view to a page
		if(getFlowAxis()==X_AXIS)	//if we're flowing horizontally
		{
			adjustAmount=getPageLeftInset();	//compensate for the left page inset
		}
		else	//if we're flowing vertically
		{
			adjustAmount=getPageTopInset();	//compensate for the top page inset
		}
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

	/**Loads all of the children to initialize the view.
	This version ensures that our container knows about this paged view.
	@param viewFactory The view factor to be used to create views.
	*/
	protected void loadChildren(final ViewFactory viewFactory)
	{
		final Container container=getContainer();	//get a reference to our container G***all this should probably go somewhere else
		if(container instanceof XMLTextPane)	//if the container is an XML text pane
			((XMLTextPane)container).setPagedView(this);	//tell it that it has a paged view
		if(getPagePoolView()==null)	//if there is no layout pool, yet
		{
	    setPagePoolView(new PagePoolView(getElement()));	//create our own special layout pool before the super class gets a chance to create one
		}
		super.loadChildren(viewFactory);	//load our children normally
	}

  /**Sets the size of the view, causing layout to occur if needed. 
  This version updates the page size and then calls the layout method with the
  page size inside of the insets.
  @param width the width >= 0
  @param height the height >= 0
   */
	public void setSize(float width, float height)
	{
		final int flowAxis=getFlowAxis();	//get the flow axis
		final int axis=getAxis();	//get our tiling axis
		final int displayPageCount=getDisplayPageCount();	//see how many pages we're displaying
		final float pageWidth, pageHeight;
		if(axis==X_AXIS)	//if we're tiling on the X axis
		{
			pageWidth=(width/displayPageCount);	//show that the pages will be a fraction of our width
			pageHeight=height;	//show that the pages will be the same height
		}
		else	//if we're tiling on the Y axis
		{
			pageWidth=width;	//show that the widths will all be the same
			pageHeight=(height/displayPageCount);	//show that the pages will each be a fraction of the total height
		}
		setPageSize(pageWidth, pageHeight);	//update the page size, which will update the page pool and the pooled child views TODO maybe do this when reparenting
		super.setSize(width, height);
//G***del		super.setSize(getPageWidth(), getPageHeight());
/*G***fix
layout((int)(width - getLeftInset() - getRightInset()), 
       (int)(height - getTopInset() - getBottomInset()));
*/
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
	 * @return the offset and span for each child view in the
	 *  offsets and spans parameters
	 */
protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans)
{
	final int pageWidth=(int)getPageWidth();
	int offset=0;
for(int i=0; i<offsets.length; ++i)
{
	offsets[i]=offset;
	offset+=pageWidth;
	spans[i]=pageWidth;
}
//G***del        	baselineLayout(targetSpan, axis, offsets, spans);
	}

protected SizeRequirements calculateMajorAxisRequirements(int axis, SizeRequirements r)
{
  if (r == null) {
    r = new SizeRequirements();
}
//G***del  r=baselineRequirements(axis, r);
r.alignment=0;
r.minimum=0;
r.maximum=(int)getPageWidth();
r.preferred=(int)getPageWidth();
return r;
	}
//G***fix getViewAtPoint()

//G***fix childAllocation()

	/**Sets the size of the view. This is overridden to support threading of the
		layout routines.
	@param width The new width (>=0).
	@param height The new height (>=0).
	*/
/*G***determine if this needs to be used
	public void setSize(float width, float height)	//(newswing threadlayout)
	{
//G***del Debug.trace("setSize(): width: "+width+", height: "+height+" from oldWidth:"+getWidth()+", oldHeight: "+getHeight());	//G***del
		if(isPaginating())	//if we are currently laying out the view, either in a separate thread or periodically G***maybe take put the isThreaded() inside the block and decide how to restart paginating if we're simply repaginating later in the AWT thread
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
*/


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
		if(getAxis()==View.X_AXIS)	//if this view flows horizontally (the default)
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
/*G***decide what to do with this; (refactor)
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
*/

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

	/***XMLPagedView.Page***/

	/**Internally-created view that holds the view representing child views
		arranged in pages. This class descends from <code>ContainerView</code>, which
		correctly returns starting and ending offsets based upon the child views
		it contains, not the element it represents.
	@author Garret Wilson
	*/
	protected class Page extends ContainerView
	{
		/**Page constructor that specifies the element from which the information will come.
		@param element The element that contains the information to display.
		*/
		public Page(final Element element)
		{
			super(element, View.Y_AXIS);	//the information inside each page will be flowed vertically
			setInsets(getPageTopInset(), getPageLeftInset(), getPageBottomInset(), getPageRightInset());	//set the page insets from the paged view
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

		/**Sets the cached properties from the attributes. This version forces the
		  margins to a particular size.
		*/
/*G***fix
		protected void setPropertiesFromAttributes()  //G***fix; hack because of new style-based margins
		{
			super.setPropertiesFromAttributes();  //set the attributes normally
			setInsets((short)25, (short)25, (short)25, (short)25);	//G***fix; testing
		}
*/
		/**Each page does not need to fill its children, since its parent
			<code>XMLPagedView</code> will load its children with the views it created.
			This function therefore does nothing.
		*/
		protected void loadChildren(ViewFactory f) {}

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
/*TODO fix paragraph analog
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

//TODO fix paragraph analogous layoutMinorAxis and calculateMinorAxisRequirements
		
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
Debug.trace("looking at document: ", documentElementIndex); //G***del
	  final Element documentElement=element.getElement(documentElementIndex); //get a reference to this child element
Debug.trace("document start offset: ", documentElement.getStartOffset()); //G***del
Debug.trace("document end offset: ", documentElement.getEndOffset()); //G***del
	    //if this document's range overlaps with our range
//G***del; only takes care of one case of overlapping			if(documentElement.getStartOffset()>=startOffset && documentElement.getStartOffset()<endOffset)
		if(documentElement.getStartOffset()<endOffset && documentElement.getEndOffset()>startOffset)
	  {
Debug.trace("document within our range"); //G***del
			final AttributeSet documentAttributeSet=documentElement.getAttributes();  //get the attributes of the document element
Debug.trace("document attribute set", documentAttributeSet); //G***del
			if(XMLStyleUtilities.isPageBreakView(documentAttributeSet)) //if this is a page break element
			{
Debug.trace("found page break view"); //G***del
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
								if(documentMediaType!=null && (documentMediaType.match(MediaType.TEXT_HTML) || documentMediaType.match(MediaType.TEXT_X_OEB1_DOCUMENT)))
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




/**
 * Lays out the children.  If the span along the flow
 * axis has changed, layout is marked as invalid which
 * which will cause the superclass behavior to recalculate
 * the layout along the box axis.  The FlowStrategy.layout
 * method will be called to rebuild the flow rows as 
 * appropriate.  If the height of this view changes 
 * (determined by the perferred size along the box axis),
 * a preferenceChanged is called.  Following all of that,
 * the normal box layout of the superclass is performed.
 *
 * @param width  the width to lay out against >= 0.  This is
 *   the width inside of the inset area.
 * @param height the height to lay out against >= 0 This
 *   is the height inside of the inset area.
 */
protected void layout(int width, int height) {
final int faxis = getFlowAxis();
int newSpan;
if (faxis == X_AXIS) {
  newSpan = (int)width;
} else {
  newSpan = (int)height;
}
if (layoutSpan != newSpan) {
  layoutChanged(faxis);
  layoutChanged(getAxis());
  layoutSpan = newSpan;
}

// repair the flow if necessary
if (! isLayoutValid(faxis)) {
  final int heightAxis = getAxis();
  int oldFlowHeight = (int)((heightAxis == X_AXIS)? getWidth() : getHeight());
  strategy.layout(this);
  int newFlowHeight = (int) getPreferredSpan(heightAxis);
  if (oldFlowHeight != newFlowHeight) {
View p = getParent();
if (p != null) {
    p.preferenceChanged(this, (heightAxis == X_AXIS), (heightAxis == Y_AXIS));
}

            // PENDING(shannonh)
            // Temporary fix for 4250847
            // Can be removed when TraversalContext is added
Component host = getContainer();
if (host != null) {
    //nb idk 12/12/2001 host should not be equal to null. We need to add assertion here
    host.repaint();
}
  }
}
super.layout(width, height);
/*G***fix
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
*/
}














	/**Strategy for pagination.
	@author Garret Wilson
	*/
	public static class PaginateStrategy extends FlowStrategy
	{

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
    public void insertUpdate(FlowView fv, DocumentEvent e, Rectangle alloc) {
  if (alloc != null) {
Component host = fv.getContainer();
if (host != null) {
    host.repaint(alloc.x, alloc.y, alloc.width, alloc.height);
}
  } else {
fv.layoutChanged(View.X_AXIS);
fv.layoutChanged(View.Y_AXIS);
  }
}

/**
* Gives notification that something was removed from the document
* in a location that the given flow view is responsible for.
*
* @param e the change information from the associated document
* @param alloc the current allocation of the view inside of the insets.
* @see View#removeUpdate
*/
    public void removeUpdate(FlowView fv, DocumentEvent e, Rectangle alloc) {
  if (alloc != null) {
Component host = fv.getContainer();
if (host != null) {
    host.repaint(alloc.x, alloc.y, alloc.width, alloc.height);
}
  } else {
fv.layoutChanged(View.X_AXIS);
fv.layoutChanged(View.Y_AXIS);
  }
}

/**
* Gives notification from the document that attributes were changed
* in a location that this view is responsible for.
*
     * @param fv     the <code>FlowView</code> containing the changes
     * @param e      the <code>DocumentEvent</code> describing the changes
     *               done to the Document
     * @param alloc  Bounds of the View
* @see View#changedUpdate
*/
    public void changedUpdate(FlowView fv, DocumentEvent e, Rectangle alloc) {
  if (alloc != null) {
Component host = fv.getContainer();
if (host != null) {
    host.repaint(alloc.x, alloc.y, alloc.width, alloc.height);
}
  } else {
fv.layoutChanged(View.X_AXIS);
fv.layoutChanged(View.Y_AXIS);
  }
}

/** 
* Update the flow on the given FlowView.  By default, this causes 
* all of the rows (child views) to be rebuilt to match the given 
* constraints for each row.  This is called by a FlowView.layout 
* to update the child views in the flow.
*
* @param fv the view to reflow
*/
public void layout(FlowView fv) {
  int p0 = fv.getStartOffset(); 
  int p1 = fv.getEndOffset();

  // we want to preserve all views from the logicalView from being 
  // removed
  View lv = getLogicalView(fv);
  int n = lv.getViewCount();
  for( int i = 0; i < n; i++ ) {
View v = lv.getView(i);
v.setParent(lv);
  }
  fv.removeAll();
  for (int rowIndex = 0; p0 < p1; rowIndex++) {
View row = ((XMLPagedView)fv).createRow();
fv.append(row);

// layout the row to the current span.  If nothing fits,
// force something.
int next = layoutRow(fv, rowIndex, p0);
if (row.getViewCount() == 0) {
    row.append(createView(fv, p0, Integer.MAX_VALUE, rowIndex));
    next = row.getEndOffset();
}
if (next <= p0) {
    throw new AssertionError("infinite loop in formatting");
} else {
    p0 = next;
}
  }
}

/**
* Creates a row of views that will fit within the 
* layout span of the row.  This is called by the layout method.
* This is implemented to fill the row by repeatedly calling
* the createView method until the available span has been
* exhausted, a forced break was encountered, or the createView
* method returned null.  If the remaining span was exhaused, 
* the adjustRow method will be called to perform adjustments
* to the row to try and make it fit into the given span.
*
* @param rowIndex the index of the row to fill in with views.  The
*   row is assumed to be empty on entry.
* @param pos  The current position in the children of
*   this views element from which to start.  
* @return the position to start the next row
*/
protected int layoutRow(FlowView fv, int rowIndex, int pos) {
  View row = fv.getView(rowIndex);
  int x = fv.getFlowStart(rowIndex);
  int spanLeft = fv.getFlowSpan(rowIndex);
  int end = fv.getEndOffset();
  TabExpander te = (fv instanceof TabExpander) ? (TabExpander)fv : null;

  // Indentation.
  int preX = x;
  int availableSpan = spanLeft;
  preX = x;
  
  final int flowAxis = fv.getFlowAxis();
  boolean forcedBreak = false;
  while (pos < end  && spanLeft >= 0) {
View v = createView(fv, pos, spanLeft, rowIndex);
if ((v == null) 
                || (spanLeft == 0 
                    &&  v.getPreferredSpan(flowAxis) > 0)) {
    break;
}

int chunkSpan;
if ((flowAxis == X_AXIS) && (v instanceof TabableView)) {
    chunkSpan = (int) ((TabableView)v).getTabbedSpan(x, te);
} else {
    chunkSpan = (int) v.getPreferredSpan(flowAxis);
}

// If a forced break is necessary, break
if (v.getBreakWeight(flowAxis, pos, spanLeft) >= ForcedBreakWeight) {
    int n = row.getViewCount();
    if (n > 0) {
	/* If this is a forced break and it's not the only view
	 * the view should be replaced with a call to breakView.
	 * If it's it only view, it should be used directly.  In
	 * either case no more children should be added beyond this
	 * view.
	 */
	v = v.breakView(flowAxis, pos, x, spanLeft);
	if (v != null) {
	    if ((flowAxis == X_AXIS) && (v instanceof TabableView)) {
		chunkSpan = (int) ((TabableView)v).getTabbedSpan(x, te);
	    } else {
		chunkSpan = (int) v.getPreferredSpan(flowAxis);
	    }
	} else {
	    chunkSpan = 0;
	}
    }
    forcedBreak = true;
}

spanLeft -= chunkSpan;
x += chunkSpan;
if (v != null) {
    row.append(v);
    pos = v.getEndOffset();
}
if (forcedBreak) {
    break;
}

  }
  if (spanLeft < 0) {
// This row is too long and needs to be adjusted.
adjustRow(fv, rowIndex, availableSpan, preX);
  } else if (row.getViewCount() == 0) {
// Impossible spec... put in whatever is left.
View v = createView(fv, pos, Integer.MAX_VALUE, rowIndex);
row.append(v);
  }
  return row.getEndOffset();
}

/**
* Adjusts the given row if possible to fit within the
* layout span.  By default this will try to find the 
* highest break weight possible nearest the end of
* the row.  If a forced break is encountered, the
* break will be positioned there.
* 
* @param rowIndex the row to adjust to the current layout
*  span.
* @param desiredSpan the current layout span >= 0
* @param x the location r starts at.
*/
    protected void adjustRow(FlowView fv, int rowIndex, int desiredSpan, int x) {
  final int flowAxis = fv.getFlowAxis();
  View r = fv.getView(rowIndex);
  int n = r.getViewCount();
  int span = 0;
  int bestWeight = BadBreakWeight;
  int bestSpan = 0;
  int bestIndex = -1;
  int bestOffset = 0;
  View v;
  for (int i = 0; i < n; i++) {
v = r.getView(i);
int spanLeft = desiredSpan - span;

int w = v.getBreakWeight(flowAxis, x + span, spanLeft);
if ((w >= bestWeight) && (w > BadBreakWeight)) {
    bestWeight = w;
    bestIndex = i;
    bestSpan = span;
    if (w >= ForcedBreakWeight) {
	// it's a forced break, so there is
	// no point in searching further.
	break;
    }
}
span += v.getPreferredSpan(flowAxis);
  }
  if (bestIndex < 0) {
// there is nothing that can be broken, leave
// it in it's current state.
return;
  }

  // Break the best candidate view, and patch up the row.
  int spanLeft = desiredSpan - bestSpan;
  v = r.getView(bestIndex);
  v = v.breakView(flowAxis, v.getStartOffset(), x + bestSpan, spanLeft);
  View[] va = new View[1];
  va[0] = v;
  View lv = getLogicalView(fv);
  for (int i = bestIndex; i < n; i++) {
View tmpView = r.getView(i);
if (contains(lv,tmpView)) {
    tmpView.setParent(lv);
} else if (tmpView.getViewCount() > 0) {
    recursiveReparent(tmpView, lv);
}
  }
  r.replace(bestIndex, n - bestIndex, va);
}

private void recursiveReparent(View v, View logicalView) {
  int n = v.getViewCount();
  for (int i = 0; i < n; i++) {
View tmpView = v.getView(i);
if (contains(logicalView,tmpView)) {
    tmpView.setParent(logicalView);
} else {
    recursiveReparent(tmpView, logicalView);
}
  }
}

private boolean contains(View logicalView, View v) {
  int n = logicalView.getViewCount();
  for (int i = 0; i < n; i++) {
if (logicalView.getView(i) == v) {
    return true;
}
  }
  return false;
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
*/
protected View createView(FlowView fv, int startOffset, int spanLeft, int rowIndex) {
  // Get the child view that contains the given starting position
  View lv = getLogicalView(fv);
  int childIndex = lv.getViewIndex(startOffset, Position.Bias.Forward);
  View v = lv.getView(childIndex);
  if (startOffset==v.getStartOffset()) {
// return the entire view
return v;
  }
  
  // return a fragment.
  v = v.createFragment(startOffset, v.getEndOffset());
  return v;
}
}

	/**The logical view of child elements which will be paginated into pages.
	Because pagination is a meta-flowing that requires underlying layout on paragraphs and
	other flowing views, this view allows for pre-pagination by setting its size.
	@author Garret Wilson
	*/
	protected class PagePoolView extends ContainerView
	{

		/**Constructor that uses a tiling axis orthogonal to the tiling axis of individual pages.
		@param element The element that contains the children to be paginated.
		*/
		public PagePoolView(final Element element)
		{
			super(element, XMLPagedView.this.getAxis()==View.X_AXIS ? View.Y_AXIS : View.X_AXIS);	//determine the tiling axis orthogonal to the paged view tiling axis
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
    
		/**Forward the document event to the given child view.
		This implementation first reparents the child to the logical view,
		as the child may have been given a parent page if the child
		could fit without break.
		@param view The child view to forward the event to.
		@param event The change information from the associated document.
		@param allocation The current allocation of the view.
		@param factory The factory to use to rebuild if the view has children.
		*/
		protected void forwardUpdateToView(final View view, final DocumentEvent event, final Shape allocation, final ViewFactory factory)
		{
			view.setParent(this);	//reparent the view to the pool
			super.forwardUpdateToView(view, event, allocation, factory);	//forward the update normally
		}
	}

}

package com.garretwilson.swing.text.xml;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.lang.ref.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.SizeRequirements;
import javax.mail.internet.ContentType;

import com.garretwilson.awt.EventQueueUtilities;
import com.garretwilson.swing.ComponentUtilities;
import com.garretwilson.swing.SwingApplication;
import com.garretwilson.swing.XMLTextPane;	//G***del when we can find a better place to set the paged view variable of XMLTextPane
import com.garretwilson.swing.event.PageEvent;
import com.garretwilson.swing.event.PageListener;
import com.garretwilson.swing.event.ProgressEvent;
import com.garretwilson.swing.event.ProgressListener;
import com.garretwilson.swing.text.AnonymousElement;
//G***del if not needed import com.garretwilson.swing.text.ViewHidable;
import com.garretwilson.swing.text.ContainerBoxView;
import com.garretwilson.swing.text.ContainerView;
import com.garretwilson.swing.text.ViewUtilities;
import com.garretwilson.swing.text.xml.css.XMLCSSStyleUtilities;
import com.garretwilson.swing.text.xml.xhtml.XHTMLSwingTextUtilities;
//G***del; not used import com.garretwilson.util.ArrayUtilities;
import com.globalmentor.java.Java;
import com.globalmentor.java.Objects;
import com.globalmentor.text.xml.oeb.OEBConstants;
import com.globalmentor.text.xml.stylesheets.css.XMLCSSConstants;
import com.globalmentor.text.xml.xhtml.XHTML;
import com.globalmentor.util.Debug;

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
public class XMLPagedView extends FlowView
{

	protected final static boolean THREADED=false;	//TODO fix threading; currently unstable; recheck after fixing ispaginating ? (pagecount-1) bug

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

	protected PaginateStrategy getStrategy() {return (PaginateStrategy)strategy;}
	
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

		strategy=new PaginateStrategy();	//install our custom paginate strategy

		setPageInsets((short)25, (short)25, (short)25, (short)25);	//set the page insets TODO allow this to be customized
		if(THREADED)	//if we should thread layout
		{
			new Thread(layoutRunner, "Layout Thread").start();	//start a thread for layout TODO improve variable access
		}
	}


	/*G***fix
A copy of our...
	private Graphics paintGraphics=null;
	private Shape paintAllocation=null;
*/

	/**@return The layout pool for the pages.*/
	protected PagePoolView getPagePoolView() {return (PagePoolView)layoutPool;}

	/**Sets the layout pool for the pages.
	@param logicalView The logical view from which pages will be created.
	*/
	protected void setPagePoolView(final View logicalView) {layoutPool=logicalView;}

	/**A temporary rectangle object used for painting.*/
	protected Rectangle tempRectangle=new Rectangle();

	/**@return The number of pages this view contains.*/
	public int getPageCount()
	{
		return getViewCount();  //return the number of child views we have
//G***fix		return getViewCount();  //return the number of child views we have
//G***del Debug.trace("getPageCount(): ", flowLayoutInfo!=null ? flowLayoutInfo.getLength() : -50);
		  //return the number of pages we've paginated or, if we have no layout info, return zero
//TODO fix		return flowLayoutInfo!=null ? flowLayoutInfo.getLength() : 0;
	}

	/**The first logical page index of the current set we're showing, or -1 for no page.*/
	private int pageIndex=0;

	/**@return The first logical page index of the current set we're showing, or -1 for no page.*/
	public int getPageIndex() {return getPageCount()>0 ? pageIndex : -1;}

	/**Sets the new current logical page index. If necessary, the page index is modified
		so that the displayed page is within the page range and that the page index
		represents the first of any set of displayed pages.
	@param newPageIndex The index of the page which should be shown.
	@see #getPageIndex
	@see #getPageCount
	@see #getDisplayPageCount
	@see #getCanonicalPageIndex(int)
	*/
	public void setPageIndex(int newPageIndex)
	{
//G***del Debug.trace("requesting page", newPageIndex);
		newPageIndex=getCanonicalPageIndex(newPageIndex);	//make sure the page passed is the canonical one
		if(pageIndex!=newPageIndex)	//if we're really changing the page number
		{
//G***del Debug.trace("Changing from page: "+PageIndex+" to page "+newPageIndex+" pageCount: "+getPageCount()); //G***del
				//first, tell all the views on the current pages they are about to be hidden
			if(pageIndex!=-1 && getPageCount()>0) //if there is a page already being displayed, and we have at least one page
			{
				final int pageBeginIndex=getPageBeginIndex();	//see which page we're showing first
				final int pageEndIndex=getPageEndIndex();	//see which page we're showing last (actually, this is the page right *after* the page we're showing)
				for(int i=pageBeginIndex; i<pageEndIndex; ++i)	//look at each page to hide
				{
					if(isLaidOut(i))	//if this page has been laid out (this function works for threading and non-threading situations)
					{
						final View pageView=getView(i); //get a reference to this view
						ViewUtilities.hideView(pageView); //tell the view that it is being hidden
					}
				}
			}
			pageIndex=newPageIndex;	//actually change the page number, so firing the page event won't cause infinite loop backs when any sliders are updated, for instance
			Debug.trace("ready to fire page event for new page index", newPageIndex, "out of page count", getPageCount());
			firePageEvent(new PageEvent(this, newPageIndex, getPageCount())); //fire a page event with our new page number
			final Container container=getContainer();	//get a reference to our container
			if(container!=null)	//if we're in a container
			{
				container.repaint();	//repaint our container G***check
			}
		}
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

	/**Determines the base page index of the set of pages including the given index.
	In this implementation, the base page index for the first page is always zero.
	If there are no pages, -1 will be returned.
	If the page index is less than zero, the zero page index will be returned.
	If the page index is greater than the number of available pages,
		the base index of the last logical page will be returned. 
	@param pageIndex The logical page index to include.
	@return The base logical page index of the set of pages including the given index. 
	*/
	protected int getCanonicalPageIndex(int pageIndex)
	{
		final int pageCount=getPageCount();	//see how many pages there are
		if(pageCount>0)	//if we have pages
		{
			if(pageIndex<0)		//if they want to set the page number to less than we have
			{
				pageIndex=0;	//we'll go to the first page
			}
			else	//if the page number is not too low
			{
				if(pageIndex>=pageCount)	//if they specified too high of a page number
				{
					pageIndex=pageCount-1;	//we'll go to the last page
				}
			}
			if(pageIndex>0)	//if another page besides the first is being requested (there's only one page on the first set)
			{
				final int displayPageCount=getDisplayPageCount(); //get the number of pages being displayed
				final int delta=displayPageCount-1;	//the first page will be displayed on the last of the displayed pages
				pageIndex=((pageIndex+delta)/displayPageCount)*displayPageCount-delta;	//make sure the page index is on the first of any page sets
			}
		}
		else	//if we don't have any pages
		{
			pageIndex=-1;	//there is never a valid page if there are no pages
		}
		return pageIndex;	//return the canonical page index
	}

	/**@return The logical index of the first displayed page.*/
	protected int getPageBeginIndex()
	{
		return getCanonicalPageIndex(getPageIndex());	//make sure the page index is in canonical form which is the first page index
	}

	/**@return The index directly after the last visible valid pages, not counting page slots that aren't filled and compensating for pagination.*/
	protected int getPageEndIndex()
	{
		final int firstPageIndex=getPageBeginIndex();	//get the index of the first visible page
		final int displayPageCount=firstPageIndex==0 ? 1 : getDisplayPageCount();	//there's only one page in the first set
		final int pageCount=isPaginating() ? Math.max(getPageCount()-1, 0) : getPageCount();	//don't count the last page if we are paginating---it is being laid out
		return Math.min(firstPageIndex+displayPageCount, pageCount);	//return the last visible page, making sure we don't go beyond our page count
	}

	/**Determines the absolute index from the beginning of allowable page display positions.
	This implementation compensates for the empty page slots on the first set of pages.
	@param logicalPageIndex The zero-based logical page index.
	@return The absolute page index including all positions.
	*/
	public int getAbsolutePageIndex(final int logicalPageIndex)
	{
		final int delta=getDisplayPageCount()-1;	//see how many empty page slots are in the first set
		return logicalPageIndex+delta;	//compensate for the empty page slots in the first set
	}

	/**Determines the logical index of available pages.
	This implementation compensates for the empty page slots on the first set of pages.
	@param absolutePageIndex The zero-based index taking into account all page positions.
	@return The logical index of the position out of available pages, or <code>-1</code> if the
		given absolute page index does not correspond to an available logical page index.
	*/
	public int getLogicalPageIndex(final int absolutePageIndex)
	{
		final int delta=getDisplayPageCount()-1;	//see how many empty page slots are in the first set
		return absolutePageIndex>=delta && absolutePageIndex<getPageCount()+delta ? absolutePageIndex-delta : -1;	//compensate for the empty page slots in the first set
	}

	/**Advances to the next page(s), if one is available, correctly taking into
		account the number of pages displayed.
	*/
	public void goNextPage()
	{
//G***del System.out.println("XMLPagedView.goNextPage(), pageIndex: "+getPageIndex()+" pageCount: "+getPageCount());	//G***del
		final int pageIndex=getPageIndex();	//get the current page index
		final int nextPageIndex=pageIndex==0 ? 1 : pageIndex+getDisplayPageCount();	//see what our next page index would be, compensating for the first page set with only one page
		if(nextPageIndex>=0 && nextPageIndex<getPageCount())	//if going to the next page would give us a valid index
			setPageIndex(nextPageIndex);	//set the new page index
	}

	/**Changes to the previous page(s), if one is available, correctly taking into
		account the number of pages displayed.
	*/
	public void goPreviousPage()
	{
		final int pageIndex=getPageIndex();	//get the current page index
		final int previousPageIndex=pageIndex==1 ? 0 : pageIndex-getDisplayPageCount();	//see what our next page index would be, compensating for the first page set with only one page
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
//TODO del when works		getPagePoolView().setSize((int)width-getPageLeftInset()-getPageRightInset(), (int)height-getPageTopInset()-getPageBottomInset());	//set the size of the page pool to be exactly the size of the displayed page; giving insets to the page pool results in incorrect layout
	}

	/**Determines the index of the page at the given position
	@param pos The position (>=0) in the model.
	@return The logical index of the page representing the given position,
		or -1 if there is no page that represents that position.
	*/
	public int getPageIndex(final int pos)
	{
		return getViewIndexAtPosition(pos);	//see which child view is responsible for the requested position
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

	/**Determines if the page at the given index is currently showing
	@Param pageIndex The logical index of the page to check.
	@return <code>true</code> if the specified page is one of the pages being displayed.
	@see #getPageBeginIndex()
	@see #getPageEndIndex()
	*/
	public boolean isPageShowing(final int pageIndex)
	{
		return pageIndex>=getPageBeginIndex() && pageIndex<getPageEndIndex();	//see if the given index is within our showing range
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

  /**Sets the parent of the view.
  This version hides the entire hierarchy if the parent is being set to <code>null</code>,
  	meaning that the view hierchy is being unloaded. (This assumes this view is the direct
  	parent of the UI root view.
  @param parent The parent of the view, <code>null</code> if none.
  @see ViewUtilities#hideView(View)
	*/
	public void setParent(final View parent)	//TODO maybe put this in some more primitive parent class
	{
		super.setParent(parent);	//set the parent normally
		if(parent==null)	//if this view is being uninstalled
		{
			ViewUtilities.hideView(this); //hide this entire view hierarchy (this is important for component views, for instance)			
		}
  }

	/**Replaces child views. If there are no views to remove this acts as an insert.
	If there are no views to add this acts as a remove.
	This version first hides the view hierarchy being removed so that views
	may remove components and hide other information as needed. 
	@param offset The starting index (0&lt;=<var>offset</var>&lt;<=<code>getViewCount()</code>) into the child views to insert the new views.
	@param length the number of existing child views (0&lt;=<var>length</var>&lt;<=<code>getViewCount()-<var>offset</var></code>) to remove.
	@param views the child views to add, or <code>null</code> if no children are being added.
	@see ViewUtilities#hideView(View)
	*/
	public void replace(final int offset, final int length, final View[] views)
	{
		for(int i=offset+length-1; i>=offset; --i)	//look at each component to remove (order doesn't matter)
		{
			final View view=getView(i); //get the view at the given index
			ViewUtilities.hideView(view); //tell the view that it is being hidden (this is important for applet views, for instance)
		}
		super.replace(offset, length, views);	//do the default replacement
	}

	/**Invalidates the view and schedules a repagination.*/
	public void repaginate()
	{
			//TODO is changing the cursor more trouble than it's worth? check the time it takes to do this
		final Container container=getContainer();	//see if the flow view has a container (it always should);
		final Cursor originalCursor=container!=null ? ComponentUtilities.setCursor(container, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)) : null;	//show the wait cursor
		try
		{
			ViewUtilities.invalidateHierarchy(getPagePoolView());	//invalidate the page pool, which will notify this view that it needs laid out
		}
		finally	//always put the cursor back to how we found it
		{
			if(container!=null && originalCursor!=null)	//if there is a container and we know the original cursor
			{
				container.setCursor(originalCursor); //set the cursor back to its original form
			}
		}
	}

	/* ***View methods*** */

  /**
   * Gives notification from the document that attributes were changed
   * in a location that this view is responsible for.
   *
   * @param changes the change information from the 
   *	associated document
   * @param a the current allocation of the view
   * @param f the factory to use to rebuild if the view has children
   * @see View#changedUpdate
   */
  public void changedUpdate(DocumentEvent changes, Shape a, ViewFactory f) {	//TODO check; do we need this?
      // update any property settings stored, and layout should be 
// recomputed 
//G***fix setPropertiesFromAttributes();
layoutChanged(X_AXIS);
layoutChanged(Y_AXIS);
super.changedUpdate(changes, a, f);
  }

	/**Renders using the given rendering surface and area on that surface. This
		function only paints the currently selected page.
	@param graphics The rendering surface to use.
	@param allocation The allocated region to render into.
	@see View#paint
	@see BoxView#paint
	@see XMLPagedView.getPageCount
	@see XMLPagedView.getPageIndex
	@see #paintPage
	*/
	public void paint(final Graphics graphics, final Shape allocation)
	{
		if(!isPaginating())
		{
		
		
		setPageIndex(getCanonicalPageIndex(getPageIndex()));	//make sure the current page index is a canonical one, so that a page in the middle of the set won't be indicated, for instance
			//get a rectangle that outlines our allocation
		final Rectangle allocationRectangle=(allocation instanceof Rectangle) ? (Rectangle)allocation : allocation.getBounds();
		final int displayPageCount=getDisplayPageCount();	//find out how many pages we should display at a time
		final int pageBeginIndex=getPageBeginIndex();	//see which page we're showing first
		final int pageEndIndex=getPageEndIndex();	//see which page we're showing last (actually, this is the page right *after* the page we're showing)
		final int left=allocationRectangle.x+getLeftInset();	//get the left side of the inside allocation
		final int top=allocationRectangle.y+getTopInset();	//get the top of the inside allocation
		tempRectangle.x=left;	//find out where to start horizontally
		tempRectangle.y=top;	//find out where to start vertically
		tempRectangle.width=(int)getPageWidth();	//find out how wide to make each page
		tempRectangle.height=(int)getPageHeight();	//find out how hight to make each page
		final Rectangle clipRectangle=graphics.getClipBounds();	//find out the clipping bounds
		//paint the dividers and page numbers for each page G***probably put this in a separate function
		final Color originalColor=graphics.getColor();	//get the original graphics color
		final Font originalFont=graphics.getFont();	//get the original graphics font
		graphics.setColor(Color.black);  //change to black for the divider
		graphics.setFont(PAGE_NUMBER_FONT);  //set the font for the page number
		final Graphics2D graphics2D=(Graphics2D)graphics;  //cast to the 2D version of graphics
		final FontRenderContext fontRenderContext=graphics2D.getFontRenderContext();  //get the font rendering context
		for(int pageIndex=pageBeginIndex; pageIndex<pageEndIndex && isLaidOut(pageIndex); ++pageIndex)	//look at each page (although this may be more pages than we have to paint)
		{
			tempRectangle.x=left+getOffset(X_AXIS, pageIndex);	//calculate the page position and spans based upon our precalculated values
			tempRectangle.y=top+getOffset(Y_AXIS, pageIndex);
			tempRectangle.width=getSpan(X_AXIS, pageIndex);
			tempRectangle.height=getSpan(Y_AXIS, pageIndex);
			if(pageIndex<pageEndIndex-1)	//if this isn't the last page, draw the vertical divider between pages
			{

					//G***fix this so that we draw the divider nicely
//G***shouldn't the first y argument not have "TempRectanglex+"?
//G***fix				g.drawLine(TempRectangle.x+TempRectangle.width, TempRectangle.x+TempRectangle.y, TempRectangle.x+TempRectangle.width, TempRectangle.y+TempRectangle.height);
				final int outerSpineHalfWidth=(int)Math.round(tempRectangle.width*0.025); //G***testing
				final int innerSpineHalfWidth=(int)Math.round(tempRectangle.width*0.003); //G***testing

				paintSpineSection(graphics2D, tempRectangle.x+tempRectangle.width, tempRectangle.y, tempRectangle.height, outerSpineHalfWidth, Color.lightGray, Color.white); //paint this section of the spine
				paintSpineSection(graphics2D, tempRectangle.x+tempRectangle.width, tempRectangle.y, tempRectangle.height, -outerSpineHalfWidth, Color.lightGray, Color.white); //paint this section of the spine
				paintSpineSection(graphics2D, tempRectangle.x+tempRectangle.width, tempRectangle.y, tempRectangle.height, innerSpineHalfWidth, Color.darkGray, Color.lightGray); //paint this section of the spine
				paintSpineSection(graphics2D, tempRectangle.x+tempRectangle.width, tempRectangle.y, tempRectangle.height, -innerSpineHalfWidth, Color.darkGray, Color.lightGray); //paint this section of the spine
			}
			if(isLaidOut(pageIndex))	//if we actually have a page view for this page, and it has been laid out
			{
					//G***maybe put page number painting in each page; maybe not
				final View pageView=getView(pageIndex); //get a reference to this view
				final int pageLeftInset=getPageLeftInset();  //get the page's left inset
				final int pageRightInset=getPageRightInset();  //get the page's right inset
				final int pageBottomInset=getPageBottomInset();  //get the page's right inset
				final String pageNumberString=String.valueOf(pageIndex+1);  //create a string with the page number to paint G***use a getPageNumber() method instead
				final Rectangle2D pageNumberBounds=graphics2D.getFont().getStringBounds(pageNumberString, fontRenderContext); //get the bounds of the string
	//G***del Debug.trace("page number left inset: "+getLeftInset()+" right inset: "+getRightInset());  //G***del
				int pageNumberX;  //we'll determine which side of the page the number goes on
				if(pageIndex==pageEndIndex-1)  //if we're on the last page
					pageNumberX=tempRectangle.x+tempRectangle.width-pageRightInset+(int)((float)(pageRightInset-pageNumberBounds.getWidth())/2);	//G***fix; comment; use local variable
				else  //if we're not on the last page G***put code here for the middle of a three-page spread
					pageNumberX=tempRectangle.x+(int)((float)(pageLeftInset-pageNumberBounds.getWidth())/2);	//G***fix; comment; use local variable
				final int pageNumberY=tempRectangle.y+tempRectangle.height-pageBottomInset+(int)((float)(pageBottomInset-pageNumberBounds.getHeight())/2);	//G***fix; comment; use local variable
					//G***take into account the size of the font, make it a nicer color, etc.
				graphics.drawString(pageNumberString, pageNumberX, pageNumberY);	//G***testing; i18n
			}
		}
		graphics.setColor(originalColor);  //revert to the original color
		graphics.setFont(originalFont);  //revert to the original font
		//paint each page
		for(int pageIndex=pageBeginIndex; pageIndex<pageEndIndex && isLaidOut(pageIndex); ++pageIndex)	//look at each page to paint (although this may be more pages than we have to paint)
		{		
			tempRectangle.x=left+getOffset(X_AXIS, pageIndex);	//calculate the page position and spans based upon our precalculated values
			tempRectangle.y=top+getOffset(Y_AXIS, pageIndex);
			tempRectangle.width=getSpan(X_AXIS, pageIndex);
			tempRectangle.height=getSpan(Y_AXIS, pageIndex);
			if(isLaidOut(pageIndex)/*G***del if we can && pageIndex>=0 && pageIndex<pageCount*/)	//if this page has been laid out (this function works for threading and non-threading situations) (newswing threadlayout)
			{
				if(tempRectangle.intersects(clipRectangle))	//if this area needs painted and this is a valid page
				{
					paintChild(graphics, tempRectangle, pageIndex);	//paint this page
				}
			}
		}
		}
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

	/**@return <code>true</code> if this view supports threading in some form and
		there the layout process is currently occurring.
	*/
	public boolean isPaginating()
	{
		return layoutRunner.isLayingOut();	//return whether the layout runner is currently performing layout
	}

	/* ***FlowView methods*** */

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
/*TODO del when works
	public void setSize(float width, float height)
	{
	  	final PaginateStrategy strategy=getStrategy();
	  	synchronized(strategy)
	  	{
	  		if(!THREADED || layoutThread==null)
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
		setPageSize(pageWidth, pageHeight);	//update the page size, which will update the page pool and the pooled child views
		super.setSize(width, height);	//set the size normally
	  		}
	  	}
	}
*/

	/**The dimensions queued for layout, if threaded.*/
	protected Queue<Dimension> layoutDimensionQueue=new ConcurrentLinkedQueue<Dimension>();

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
  protected void layout(int width, int height)
  {
  	if(THREADED)	//if we should thread layout
  	{
  		layoutDimensionQueue.add(new Dimension(width, height));
  		synchronized(layoutRunner)	//make sure the thread isn't checking the stop flag
  		{
  			layoutRunner.notify();	//tell the thread to wake up and check the stop flag if it is sleeping  			
  		}
  	}
  	else	//if layout should be synchronous
  	{
  		layoutImmediately(width, height);	//do the layout now
  	}
  }

  /**Immediately lays out the view.
  This version updates the page size and then calls the super layout method.
  @param width The layout width (>=0).
  @param height The layout height (>=0).
  */
  protected void layoutImmediately(final int width, int height)
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
		setPageSize(pageWidth, pageHeight);	//update the page size, which will update the page pool and the pooled child views
  	super.layout(width, height);	//do the default layout
  }
  
  /**The instance of the class that performs layout; in this implementation, in a separate thread.*/
  private final LayoutRunner layoutRunner=new LayoutRunner();
    
  /**The class that performs layout.
  This implementation runs this class in a separate thread.
  @author Garret Wilson.
  */
  protected class LayoutRunner implements Runnable
  {
  	/**Whether layout should be stopped.*/
  	private boolean stopped=false;

	  	/**Stops the current series of layouts and ends the thread.*/
	  	public void stop()
	  	{
	  		stopped=false;	//set the stop flag to false
	  		synchronized(this)	//make sure the thread isn't checking the stop flag
	  		{
	    		notify();	//tell the thread to wake up and check the stop flag if it is sleeping  			
	  		}
	  	}
	  
	  /**Whether layout is currently occurring.*/
	  private boolean isLayingOut=false;

		  /**@return Whether layout is currently occurring.*/
		  public boolean isLayingOut() {return isLayingOut;}

  	/**Performs layout on all dimensions in the queue and then sleeps and waits for more to arrive.*/
  	public void run()
  	{
  		do
  		{
  			try
  			{
	 				final Dimension dimension=layoutDimensionQueue.poll();	//get another dimension from the queue
	 				if(dimension!=null)	//if there is another dimension ready to lay out
	 				{
	 					isLayingOut=true;	//show that layout has started
	 					try
	 					{
	 						Debug.trace("ready to lay out");
	 						layoutImmediately(dimension.width, dimension.height);	//do the layout
	 					}
	 					finally
	 					{
	 						isLayingOut=false;	//always show that layout has stopped
	 					}
	  			}
	  			synchronized(this)	//make sure nothing is added to the queue after we check it but before we go to sleep
	  			{
	  				if(!stopped && layoutDimensionQueue.size()==0)	//if nothing is left in the queue (and we should keep going)
	  				{
	  					try
							{
								wait();	//wait for more dimensions to be added to the queue
							}
	  					catch(final InterruptedException interruptedException)	//if we are interrupted waiting for more dimensions to lay out, don't do anything special
							{
							}
	  				}
	  			}
  			}
  			catch(final Exception exception)	//if anything wrong happens in the main thread loop, report it and keep going
  			{
 		  		SwingApplication.displayApplicationError(getContainer(), exception);
  			}
  		}
  		while(!stopped);	//keep going until we are stopped
  	}
  }
  
	/**Performs layout for the minor axis of the view (the page width).
	This version assumes all pages have the same width and are layed out
	<code>displayPageCount</code> at a time.
	@param targetSpan The total span given to the view.
	@param axis The axis being layed out.
	@param offsets The offsets from the origin of the view for each of the child views to be calculated.
	@param spans The span of each child view to be calculated.
	*/
	protected void layoutMajorAxis(final int targetSpan, final int axis, final int[] offsets, final int[] spans)
	{
		final int pageWidth=(int)getPageWidth();	//find out how wide the page is
		final int displayPageCount=getDisplayPageCount();	//find out how many pages are displayed at a time
		final int delta=displayPageCount-1;	//the first page will be displayed on the last of the displayed pages
		for(int i=getViewCount()-1; i>=0; --i)	//for each view
		{
			offsets[i]=pageWidth*((i+delta)%displayPageCount);	//find out the index of the page out of displayPageCount total and multiply that by the page width
Debug.trace("laying out major axis, page", i, "gets offset", offsets[i]);
			spans[i]=pageWidth;	//every page is the same width
		}
	}

	/**Performs layout for the minor axis of the view (the page height). 
	This version assumes all pages are the same offset and height.
	@param targetSpan The total span given to the view.
	@param axis The axis being layed out.
	@param offsets The offsets from the origin of the view for each of the child views to be calculated.
	@param spans The span of each child view to be calculated.
	*/
	protected void layoutMinorAxis(final int targetSpan, final int axis, final int[] offsets, final int[] spans)
	{
		final int pageHeight=(int)getPageHeight();	//get the page height
		for(int i=getViewCount()-1; i>=0; --i)	//for each view
		{
			offsets[i]=0;	//each page starts at the same place vertically
			spans[i]=pageHeight;	//each page is the same height
		}
  }

	/**Calculates the size requirements for the major axis (the page width).
	@param axis The axis being studied.
	@param sizeRequirements The <code>SizeRequirements</code> object, or <code>null</code> if one should be created.
	@return The newly initialized <code>SizeRequirements</code> object.
	*/
	protected SizeRequirements calculateMajorAxisRequirements(final int axis, SizeRequirements sizeRequirements)
	{
		if(sizeRequirements==null)	//if we don't have a requirements object
		{
			sizeRequirements=new SizeRequirements();	//create new size requirements
		}
		final int width=(int)(getDisplayPageCount()*getPageWidth());	//find out how wide all the displayed pages are in total
		sizeRequirements.alignment=0;	//left-align
		sizeRequirements.minimum=width;	//we require each page to be the same width
		sizeRequirements.maximum=width;
		sizeRequirements.preferred=width;
		return sizeRequirements;	//return the size requirements
	}

	/**Calculates the size requirements for the minor axis (the page height).
	@param axis The axis being studied.
	@param sizeRequirements The <code>SizeRequirements</code> object, or <code>null</code> if one should be created.
	@return The newly initialized <code>SizeRequirements</code> object.
	*/
	protected SizeRequirements calculateMinorAxisRequirements(final int axis, SizeRequirements sizeRequirements)
	{
		if(sizeRequirements==null)	//if we don't have a requirements object
		{
			sizeRequirements=new SizeRequirements();	//create new size requirements
		}
		final int height=(int)getPageHeight();	//find out how high the page is
		sizeRequirements.alignment=0;	//top-align
		sizeRequirements.minimum=height;	//we require each page to be the same height
		sizeRequirements.maximum=height;
		sizeRequirements.preferred=height;
		return sizeRequirements;	//return the size requirements
	}

	/**Finds the child view at the given point.
	@param x The horizontal coordinate (>=0).
	@param y The vertical coordinate (>=0).
	@param allocation The parent's inner allocation on entry, which should
		be changed to the child's allocation on exit.
	@return The child view at the specified point.
	*/
	protected View getViewAtPoint(final int x, final int y, final Rectangle allocation)
	{
		final int pageBeginIndex=getPageBeginIndex();	//see which page we're showing first
		final int pageEndIndex=getPageEndIndex();	//see which page we're showing last (actually, this is one more than the last page we're showing)
		final int axis=getAxis();	//get the axis
		final int extent=axis==X_AXIS ? x : y;	//get the coordinate to test for an outlier
		final int allocationExtent=axis==X_AXIS ? allocation.x : allocation.y;	//see which 
		for(int i=pageBeginIndex+1; i<pageEndIndex; ++i)	//look at each page after the first
		{
			if(extent<allocationExtent+getOffset(axis, i))	//if the extent comes before this child
			{
				childAllocation(i-1, allocation);	//get the previous child's allocation
				return getView(i-1);	//return the previous child
			}			
		}
		childAllocation(pageEndIndex-1, allocation);	//if the point was not before any of the children, get the last child's allocation
		return getView(pageEndIndex-1);	//return the last child
	}

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

	/**Internally-created view that holds the view representing child views
		arranged in pages. This class descends from <code>ContainerView</code>, which
		correctly returns starting and ending offsets based upon the child views
		it contains, not the element it represents.
	@author Garret Wilson
	*/
	protected class Page extends ContainerBoxView
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
		
	}

	

	
	/**Flow strategy with no customizations&mdash;only used for debugging. 
	@author Garret Wilson
	*/
	public class DebugFlowStrategy extends FlowStrategy
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
	
	/**Strategy for pagination.
	@author Garret Wilson
	*/
	public class PaginateStrategy extends DebugFlowStrategy
	{
		/**Updates the flow on the given flow view.
		@param flowView The view to reflow.
		*/
		public void layout(final FlowView flowView)
		{
			final Container container=flowView.getContainer();	//see if the flow view has a container (it always should)
			final Cursor originalCursor=container!=null ? ComponentUtilities.setCursor(container, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)) : null;	//show the wait cursor
			try
			{
				fireMadeProgress(new ProgressEvent(flowView, PAGINATE_TASK, "Repaginating pages...", 0, 1));	//show that we are ready to start paginating pages, but we haven't really started, yet G***i18n
					//make sure the layout pool has the correct dimensions of the page so that it will do unrestrained layout correctly TODO check the axis to make sure we use the correct insets
				getPagePoolView().setSize((int)getPageWidth()-getPageLeftInset()-getPageRightInset(), (int)getPageHeight()-getPageTopInset()-getPageBottomInset());	//set the size of the page pool to be exactly the size of the displayed page; giving insets to the page pool results in incorrect layout
				super.layout(flowView);	//do the layout normally
/*TODO fix end-of-pagination repainting
				if(container!=null)	//if we're in a container
				{
					container.repaint();	//repaint our container G***check
				}
*/
			}
			finally	//always put the cursor back to how we found it
			{
				if(container!=null && originalCursor!=null)	//if there is a container and we know the original cursor
				{
					container.setCursor(originalCursor); //set the cursor back to its original form
				}
			}
			fireMadeProgress(new ProgressEvent(flowView, PAGINATE_TASK, "Paginated all "+flowView.getViewCount()+" pages.", flowView.getViewCount(), flowView.getViewCount()));	//show that we paginated all the pages G***i18n
				//fire a page event with our current page number, since our page count changed
			firePageEvent(new PageEvent(this, getPageIndex(), getPageCount()));
		}

		/**Creates a row of views that will fit within the layout span of the row.
		This is called by the layout method.
		@param flowView The view to reflow.
		@param rowIndex The index of the row to fill in with views.
			The row is assumed to be empty on entry.
		@param pos The current position in the children of this views element from which to start.  
		@return The position to start the next row.
		*/
		protected int layoutRow(final FlowView flowView, final int rowIndex, final int pos)
		{
			final float progress=(float)flowView.getEndOffset()/pos;	//find out how far we are along the content
			final float estimatedRowCount=rowIndex*progress;	//find out the total rows by multiplying the number of rows *already* collected by the progress
			fireMadeProgress(new ProgressEvent(flowView, PAGINATE_TASK, "Paginating page "+(rowIndex+1)+" of ~"+Math.round(estimatedRowCount)+"...", rowIndex, estimatedRowCount));	//show that we are paginating the specified page, and the number of pages we guess there will be G***i18n
			final int nextPos=super.layoutRow(flowView, rowIndex, pos);	//do the default layout
/*TODO
			final Container container=getContainer();	//get a reference to our container
			if(container!=null)	//if we're in a container
			{
				container.repaint();	//repaint our container G***check
			}
*/
			return nextPos;	//return the next position for layout
		}

	}

	/**The logical view of child elements which will be paginated into pages.
	Because pagination is a meta-flowing that requires underlying layout on paragraphs and
	other flowing views, this view allows for pre-pagination by setting its size.
	@author Garret Wilson
	*/
	protected class PagePoolView extends ContainerBoxView
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
	//G***del Debug.trace("loading children for page pool, offsets "+startOffset+" to "+endOffset);
			final Element parentElement=getElement();	//get the parent element 
			final Element[] childElements=XMLSectionView.getSectionChildElements(parentElement, getStartOffset(), getEndOffset()); //get the child elements that fall within our range
			final View[] views=XMLBlockView.createBlockViews(parentElement, childElements, viewFactory);  //create the child views, ensuring they are block elements
			replace(0, getViewCount(), views);  //add the views as child views to this view pool
		}
    
		/**Forward the document event to the given child view.
		This implementation first reparents the child to the logical view, as the child
		may have been given a parent page if the child could fit without breaking.
		@param view The child view to forward the event to.
		@param event The change information from the associated document.
		@param allocation The current allocation of the view.
		@param factory The factory to use to rebuild if the view has children.
		*/
		protected void forwardUpdateToView(final View view, final DocumentEvent event, final Shape allocation, final ViewFactory factory)
		{
			view.setParent(this);	//reparent the view to the pool G***is this needed, with the new layout() below? should this reparent the hierarchy? investigate super versions to see exactly what this does
			super.forwardUpdateToView(view, event, allocation, factory);	//forward the update normally
		}

		/**Performs layout on the page pool.
		This implementation first takes the important step of reparenting the entire hierarchy of views in the pool.
		Some whole views could have been parented to fragments, and without reparenting, layout cannot reliably
		occur. Without this step, some views would prefer a size under the old layout, resulting in vertical
		gaps if layout changed from small to large. 
		@param width The width inside the insets (>=0).
		@param height The height inside the insets (>=0).
		*/
		protected void layout(int width, int height)	//TODO eventually something similar should go into all layout-able views; this works here because it is in the top-most hierarchy and reparents the entire hierarchy
		{
/*TODO del
			for(int i=getViewCount()-1 ; i>=0; --i) //look at each child view
			{
				final View childView=getView(i); //get a reference to the child view
			  if(childView.getParent()!=XMLPagedView.this)  //if this view has a different parent than this one
			  {
					childView.setParent(XMLPagedView.this);	//set this view's parent to the parent view
			  }
				ViewUtilities.reparentHierarchy(childView);
			}
*/
			/*TODO fix
			for(int i=getViewCount()-1; i>=0; --i) //look at each child view
			{
				ViewUtilities.reparentHierarchy(getView(i));  //reparent all views under this one
			}
*/
			ViewUtilities.reparentHierarchy(this); //make sure all the child views have correct parents (previous layouts could cause, for instance, a paragraph row to think it has a parent of a now-unused paragraph fragement)
			super.layout(width, height);	//do the default layout
		}
	
	}

}

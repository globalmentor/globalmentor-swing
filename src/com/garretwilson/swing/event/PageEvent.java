package com.garretwilson.swing.event;

import java.util.EventObject;


/**PageEvent is used to notify interested parties that the displayed page has
	changed in the event source.
@author Garret Wilson
@see com.garretwilson.swing.XMLTextPane
@see com.garretwilson.swing.OEBBook
*/
public class PageEvent extends EventObject
{

	/**The zero-based index of the page being displayed, or <code>-1</code> if
		no page is being displayed.
	*/
	private int pageIndex;

		/**@return The new zero-based page index, or <code>-1</code> if no page is
		  being displayed.
		*/
		public int getPageIndex() {return pageIndex;}

	/**The number of pages available.*/
	private int pageCount;

		/**@return The number of pages available.*/
		public int getPageCount() {return pageCount;}

	/*Constructor for a page event specifying the source of the page change.
	@param source The object responsible for the event.
	@param newPageIndex The new zero-based index of the page.
	@param newPageCount The number of pages available.
	*/
	public PageEvent(Object source, final int newPageIndex, final int newPageCount)
	{
		super(source);	//construct the parent class
		pageIndex=newPageIndex;	//set the page index
		pageCount=newPageCount;	//set the number of pages
	}

}


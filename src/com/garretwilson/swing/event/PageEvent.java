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

	/**Constructor for a page event specifying the source of the page change.
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


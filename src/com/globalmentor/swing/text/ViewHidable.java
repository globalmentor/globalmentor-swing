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

package com.globalmentor.swing.text;

import com.globalmentor.swing.text.xml.XMLPagedView;

/**Indicates that this view can be hidden. Although all views can be "hidden"
	by their being covered by another window, implementing this interface means
	that the view expects it might be a child of a view that shows only a portion
	of its views at a time, such as an {@link XMLPagedView}. In such an
	example, <em>all</em> views would be hidden at times, but only those that
	implement this interface will be informed that they are about to be hidden.
	The implementation, therefore, functions much like an even listener that is
	automatically added as a listener when added to the view hierarchy.
	<p>A component view, for instance, might need to know when it is being hidden
	so as to tell its component to be made not visible.</p>
	<p>Note that hiding a view should not necessarily make that view not visible,
	although a view may need to set a related component to be not visible. Normal
	views need to take no action (and therefore do not need to implement this
	interface) when being hidden; they simply will not be painted.</p>
@author Garret Wilson
@see XMLPagedView
*/
public interface ViewHidable
{
		/**Called when a view is being hidden by a parent that hides views, such as
		  a paged view; in that instance, <code>newShowing</code> will be set to
			<code>false</code> This function may or may not be called with an argument
			of <code>true</code> to report that the view needs showing.
		@param showing <code>true</code> if the view is beginning to be shown,
			<code>false</code> if the view is beginning to be hidden.
		*/
		public void setShowing(final boolean showing);

	/**Called when the view is being hidden by a parent that hides views, such
		as a paged view.
	*/
//TODO del	void hide();
}

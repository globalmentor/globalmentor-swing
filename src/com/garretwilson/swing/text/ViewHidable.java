package com.garretwilson.swing.text;

/**Indicates that this view can be hidden. Although all views can be "hidden"
	by their being covered by another window, implementing this interface means
	that the view expects it might be a child of a view that shows only a portion
	of its views at a time, such as an <code>XMLPagedView</code>. In such an
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
@see com.garretwilson.swing.text.xml.XMLPagedView
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
//G***del	void hide();
}

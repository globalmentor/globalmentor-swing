package com.garretwilson.swing.text;

import java.util.List;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**Indicates that this factory can create multiple views for one element if it
	needs to, allowing a hierarchy of views to be represented by one level of
	multiple views, for example.
@author Garret Wilson
*/
public interface ViewsFactory extends ViewFactory
{

	/**Creates one or more views for the given element, storing the views in
		the given list.
		This method allows one element (such as a nested inline element within a
		paragraph) to be represented by one level of multiple views rather than
		a hierarchy of views.
	@param element The element the view or views will represent.
	@param addViewList The list of views to which the views should be added.
	*/
	public void create(final Element element, final List addViewList);

	/**Creates a view for the given element. This method can optionally indicate
		multiple views are needed by returning <code>null</code>.
	@param element The element this view will represent.
	@param indicateMultipleViews Whether <code>null</code> should be returned to
		indicate multiple views should represent the given element.
	@return A view to represent the given element, or <code>null</code>
		indicating the element should be represented by multiple views.
	*/
	public View create(final Element element, final boolean indicateMultipleViews);

}

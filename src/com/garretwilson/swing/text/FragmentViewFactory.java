package com.garretwilson.swing.text;

import javax.swing.text.View;

/**An object that can create fragment views for views containing other views.
@author Garret Wilson
*/
public interface FragmentViewFactory
{
	/**Creates a fragment view into which pieces of this view will be placed.
	@param isFirstFragment Whether this fragment holds the first part of the
		original view.
	@param isLastFragment Whether this fragment holds the last part of the
		original view.
	*/
	public View createFragmentView(final boolean isFirstFragment, final boolean isLastFragment);
	
}

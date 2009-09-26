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

package com.globalmentor.swing.text.xml;

import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import com.globalmentor.swing.text.FragmentView;

/**A view that can represent not only a fragment of a block view but of other
	views as well. It is correctly marked as a fragment view by implementing
	{@link FragmentView}, and keeps track of whether the view is the first
	fragment.
//TODO in this and all fragment views, fix the replace() method so that it reparents the view to the whole view if it was originally a child of the whole view; then, update the CompositeBoxView break strategy and see if we can remove the reparenting; make sure that any reparenting works with flow views with their logical view pools
@author Garret Wilson
*/
public class XMLFragmentBlockView extends XMLBlockView implements FragmentView
{
	/**The original, unfragmented view from which this fragment (or one or more intermediate fragments) was broken.*/
	private final View wholeView;
	
		/**@return The original, unfragmented view from which this fragment (or one or more intermediate fragments) was broken.*/
		public View getWholeView() {return wholeView;}

	/**Whether this is the first fragment of the original view.*/
	private final boolean isFirstFragment;

		/**@return <code>true</code> if this is the first fragment of the original view.*/
		public boolean isFirstFragment() {return isFirstFragment;}

	/**Whether this is the last fragment of the original view.*/
	private final boolean isLastFragment;

		/**@return <code>true</code> if this is the last fragment of the original view.*/
		public boolean isLastFragment() {return isLastFragment;}

	/**Constructs a fragment block view.
	@param element The element this view is responsible for.
	@param axis The tiling axis, either View.X_AXIS or View.Y_AXIS.
	@param wholeView The original, unfragmented view from which this fragment (or one or more intermediate fragments) was broken.
	@param firstFragment Whether this is the first fragment of the original view.
	@param lastFragment Whether this is the last fragment of the original view.
	*/
	public XMLFragmentBlockView(final Element element, final int axis, final View wholeView, final boolean firstFragment, final boolean lastFragment)
	{
		super(element, axis); //do the default consructing
		this.wholeView=wholeView;	//save the original whole view
		isFirstFragment=firstFragment;  //save whether we are the first fragment of the original view
		isLastFragment=lastFragment;  //save whether we are the last fragment of the original view
	}

	/**Creates a fragment view into which pieces of this view will be placed.
	@param isFirstFragment Whether this fragment holds the first part of the original view.
	@param isLastFragment Whether this fragment holds the last part of the original view.
	*/
	public View createFragmentView(final boolean isFirstFragment, final boolean isLastFragment)
	{
	  return new XMLFragmentBlockView(getElement(), getAxis(), getWholeView(), isFirstFragment, isLastFragment);	//create a fragment of this view, indicating the original view
	}

	/**Returns the attributes to use for this view.
 	This version returns the attributes of the whole view.
	@return The attributes of the parent view, or the attributes of the underlying element if there is no parent.
	*/
	public AttributeSet getAttributes()
	{
		return getWholeView().getAttributes();	//return the attributes of the whole view
	}

	/**Fragment views do not load their own children. Instead, the method
		fragmenting the original view will manually add children to this view.
	@param viewFactory The view factory used to create child views.
	*/
	protected void loadChildren(ViewFactory viewFactory) {}

}

package com.garretwilson.swing.text.xml;

import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import com.garretwilson.swing.text.FragmentView;

/**A view that can represent not only a fragment of a block view but of other
	views as well. It is correctly marked as a fragment view by implementing
	<code>FragmentView</code>, and keeps track of whether the view is the first
	fragment.
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

	/**Constructs a fragment block view..
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

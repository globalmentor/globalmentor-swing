package com.garretwilson.swing.text.xml;

import javax.swing.text.Element;
import javax.swing.text.ViewFactory;
import com.garretwilson.swing.text.FragmentView;

/**A view that can represent not only a fragment of a block view but of other
	views as well. It is correctly marked as a fragment view by implementing
	<code>FragmentView</code>, and keeps track of whether the view is the first
	fragment.
@author Garret Wilson
*/
public class XMLFragmentBlockView extends XMLBlockView implements FragmentView //G***add isLastFragment() to FragmentView interface
{

	/**Whether this is the first fragment of the original view.*/
	private boolean isFirstFragment;

		/**@return <code>true</code> if this is the first fragment of the original view.*/
		public boolean isFirstFragment() {return isFirstFragment;}

	/**Whether this is the last fragment of the original view.*/
	private boolean isLastFragment;

		/**@return <code>true</code> if this is the last fragment of the original view.*/
		public boolean isLastFragment() {return isLastFragment;}

	/**Constructs an a fragment block view..
	@param element The element this view is responsible for.
	@param axis The tiling axis, either View.X_AXIS or View.Y_AXIS.
	@param firstFragment Whether this is the first fragement of the original view.
	@param lastFragment Whether this is the last fragement of the original view.
	*/
	public XMLFragmentBlockView(final Element element, final int axis, final boolean firstFragment, final boolean lastFragment)
	{
		super(element, axis); //do the default consructing
		isFirstFragment=firstFragment;  //save whether we are the first fragment of the original view
		isLastFragment=lastFragment;  //save whether we are the last fragment of the original view
	}

	/**Fragment views do not load their own children. Instead, the method
		fragmenting the original view will manually add children to this view.
	@param viewFactory The view factory used to create child views.
	*/
	protected void loadChildren(ViewFactory viewFactory) {}

}

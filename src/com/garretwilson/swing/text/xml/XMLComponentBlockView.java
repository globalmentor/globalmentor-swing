package com.garretwilson.swing.text.xml;

import java.awt.*;

import javax.swing.text.*;

import com.garretwilson.swing.text.ViewComponentManageable;
import com.garretwilson.swing.text.ViewComponentManager;
import com.garretwilson.swing.text.ViewHidable;
import com.garretwilson.swing.text.xml.XMLComponentParagraphView.XMLComponentParagraphFragmentView;
import com.garretwilson.swing.text.xml.XMLParagraphView.XMLParagraphFragmentView;
import com.garretwilson.util.Debug;

/**Provides a block view with one or more components embedded. The children will
	be loaded as with a normal block view, except that the added components will
	be visible in the view. The components are added using the constants from
	<code>BorderLayout</code> except for <code>BorderLayout.CENTER</code>.
	Implements <code>ViewHidable</code> so that it can be notified if the view
	is being hidden so that it can hide the object.
@see BorderLayout
@author Garret Wilson
//G***maybe rename to XMLBorderComponentBlockView

//G***later make a way to free the embedded component, just as we should do with the XMLAbstractComponentView and children
//G***this could perhaps be generalized even more to an XMLPrefixedComponentView or Something
*/
public class XMLComponentBlockView extends XMLBlockView implements ViewComponentManageable, ViewHidable
{

	/**The object that manages the components this view owns.*/
	private final ViewComponentManager componentManager;

		/**@return The object that manages the components this view owns.*/
		public ViewComponentManager getComponentManager() {return componentManager;}

	/**Whether the object is showing; this defaults to <code>false</code>.*/
	private boolean showing=false;

		/**Returns whether the object is showing. This is not necessarily the same
			as visible, because the associated object could be set as visible, yet
			not be showing because it is displayed in a paged view, for example.
		@return Whether the object is showing.
		*/
		public boolean isShowing() {return showing;}
	
	/**Constructs a block view with the given component.
	@param element The element this view is responsible for.
	@param axis The tiling axis, either <code>View.X_AXIS</code> or <code>View.Y_AXIS</code>.
	*/
	public XMLComponentBlockView(final Element element, final int axis)
	{
		super(element, axis, true, true); //construct the parent, allowing expansion in both direction
		componentManager=new ViewComponentManager(this);  //create a component manager to manage our components
	}

	/**Called when the view is being hidden by a parent that hides views, such
		as a paged view. This implementation hides associated components, if
		available.
	@param showing <code>true</code> if the view is beginning to be shown,
		<code>false</code> if the view is beginning to be hidden.
	@see #getComponent
	*/
	public void setShowing(final boolean showing)
	{
		componentManager.setShowing(showing); //tell the component manager our new status
		this.showing=showing;	//update our showing status
	}
	
	/**Paints the component.
		Informs the component manager that it should update the components if needed.
	@param graphics The rendering surface to use.
	@param allocation The allocated region to render into.
	@see XMLImageView#paint
	*/
	public void paint(final Graphics graphics, final Shape allocation)
	{
		componentManager.setLocation(allocation); //tell the component manager our new location
		if(!isShowing())  //if the object isn't currently showing
			setShowing(true); //show the object
		super.paint(graphics, allocation);  //do the default painting, which will update our dimensions if needed
	}
	
	/**Sets the size of the object, while keeping the object in the same proportions.
		Informs the component manager that it should update the components if needed.
	@param width The width (>=0).
	@param height The height (>=0).
	*/
	public void setSize(float width, float height)
	{
		componentManager.setSize(width, height); //tell the component manager our new size
		super.setSize(width, height); //do the default size setting
	}

	/**Creates a fragment view into which pieces of this view will be placed.
	@param isFirstFragment Whether this fragment holds the first part of the original view.
	@param isLastFragment Whether this fragment holds the last part of the original view.
	*/
	public View createFragmentView(final boolean isFirstFragment, final boolean isLastFragment)
	{
	  return new XMLComponentFragmentBlockView(getElement(), getAxis(), this, isFirstFragment, isLastFragment);	//create a fragment of this view
	}

	/**The class that serves as a fragment if the paragraph is broken.
	@author Garret Wilson
	*/
	protected class XMLComponentFragmentBlockView extends XMLFragmentBlockView implements ViewComponentManageable, ViewHidable
	{

		/**The object that manages the components this view owns.*/
		private final ViewComponentManager componentManager;

			/**@return The object that manages the components this view owns.*/
			public ViewComponentManager getComponentManager() {return componentManager;}

		/**Whether the object is showing; this defaults to <code>false</code>.*/
		private boolean showing=false;

			/**Returns whether the object is showing. This is not necessarily the same
				as visible, because the associated object could be set as visible, yet
				not be showing because it is displayed in a paged view, for example.
			@return Whether the object is showing.
			*/
			public boolean isShowing() {return showing;}

		/**Constructs a component fragment block view.
		@param element The element this view is responsible for.
		@param axis The tiling axis, either View.X_AXIS or View.Y_AXIS.
		@param wholeView The original, unfragmented view from which this fragment (or one or more intermediate fragments) was broken.
		@param firstFragment Whether this is the first fragment of the original view.
		@param lastFragment Whether this is the last fragment of the original view.
		*/
		public XMLComponentFragmentBlockView(final Element element, final int axis, final View wholeView, final boolean firstFragment, final boolean lastFragment)
		{
			super(element, axis, wholeView, firstFragment, lastFragment); //do the default construction
			componentManager=new ViewComponentManager(this);  //create a component manager to manage our components
		}

		/**Creates a fragment view into which pieces of this view will be placed.
		@param isFirstFragment Whether this fragment holds the first part of the original view.
		@param isLastFragment Whether this fragment holds the last part of the original view.
		*/
		public View createFragmentView(final boolean isFirstFragment, final boolean isLastFragment)
		{
		  return new XMLComponentFragmentBlockView(getElement(), getAxis(), getWholeView(), isFirstFragment, isLastFragment);	//create a fragment of this view, indicating the original view
		}

		/**Called when the view is being hidden by a parent that hides views, such
			as a paged view. This implementation hides associated components, if
			available.
		@param showing <code>true</code> if the view is beginning to be shown,
			<code>false</code> if the view is beginning to be hidden.
		@see #getComponent
		*/
		public void setShowing(final boolean showing)
		{
			componentManager.setShowing(showing); //tell the component manager our new status
			this.showing=showing;	//update our showing status
		}
		
		/**Paints the component.
			Informs the component manager that it should update the components if needed.
		@param graphics The rendering surface to use.
		@param allocation The allocated region to render into.
		@see XMLImageView#paint
		*/
		public void paint(final Graphics graphics, final Shape allocation)
		{
			componentManager.setLocation(allocation); //tell the component manager our new location
			if(!isShowing())  //if the object isn't currently showing
				setShowing(true); //show the object
			super.paint(graphics, allocation);  //do the default painting, which will update our dimensions if needed
		}
		
		/**Sets the size of the object, while keeping the object in the same proportions.
			Informs the component manager that it should update the components if needed.
		@param width The width (>=0).
		@param height The height (>=0).
		*/
		public void setSize(float width, float height)
		{
			componentManager.setSize(width, height); //tell the component manager our new size
			super.setSize(width, height); //do the default size setting
		}
	}

}
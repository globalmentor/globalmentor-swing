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

package com.garretwilson.swing.text.xml;

import static com.globalmentor.java.Maths.max;

import java.awt.*;
import javax.swing.text.*;
import com.garretwilson.swing.text.*;

/**An XML paragraph that allows embedded components.
@author Garret Wilson
*/
public class XMLComponentParagraphView extends XMLParagraphView implements ViewComponentManageable, ViewHidable
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

	/**<code>true</code> if the view should update minimum insets to compensate for components located in the insets, or <code>false</code> if the normal insets should be used.*/
	private final boolean componentInsetsUpdated;

		/**@return <code>true</code> if the view should update minimum insets to compensate for components located in the insets, or <code>false</code> if the normal insets should be used.*/
		protected boolean isComponentInsetsUpdated() {return componentInsetsUpdated;}

	/**@return The left inset, (&gt;=0);*/
	protected short getLeftInset()
	{
		final short inset=super.getLeftInset();	//get the normal inset
		return isComponentInsetsUpdated() ? max(inset, (short)getComponentManager().getMinimumLeftInset()) : inset;	//compensate for components in the inset if we should
	}

	/**@return The right inset, (&gt;=0);*/
	protected short getRightInset()
	{
		final short inset=super.getRightInset();	//get the normal inset
		return isComponentInsetsUpdated() ? max(inset, (short)getComponentManager().getMinimumRightInset()) : inset;	//compensate for components in the inset if we should
	}

	/**@return The top inset, (&gt;=0);*/
	protected short getTopInset()
	{
		final short inset=super.getTopInset();	//get the normal inset
		return isComponentInsetsUpdated() ? max(inset, (short)getComponentManager().getMinimumTopInset()) : inset;	//compensate for components in the inset if we should
	}

	/**@return The bottom inset, (&gt;=0);*/
	protected short getBottomInset()
	{
		final short inset=super.getBottomInset();	//get the normal inset
		return isComponentInsetsUpdated() ? max(inset, (short)getComponentManager().getMinimumBottomInset()) : inset;	//compensate for components in the inset if we should
	}

	/**Constructs a paragraph view for the given element, compensating for components in the insets.
	@param element The element for which this view is responsible.
	*/
	public XMLComponentParagraphView(final Element element)
	{
		this(element, true);	//default to compensating for inset components
	}

	/**Constructs a paragraph view for the given element.
	@param element The element for which this view is responsible.
	@param compensateInsets <code>true</code> if the view should update minimum insets to compensate for components located in the insets, or <code>false</code> if the normal insets should be used.
	*/
	public XMLComponentParagraphView(final Element element, final boolean compensateInsets)
	{
		super(element);	//construct the parent class
		componentManager=new ViewComponentManager(this);  //create a component manager to manage our components
		this.componentInsetsUpdated=compensateInsets;	//save whether we should compensate for components in the insets
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
	  return new XMLComponentParagraphFragmentView(getElement(), getAxis(), this, isFirstFragment, isLastFragment, isComponentInsetsUpdated());	//create a fragment of this view
	}

	/**The class that serves as a fragment if the paragraph is broken.
	@author Garret Wilson
	*/
	protected class XMLComponentParagraphFragmentView extends XMLParagraphFragmentView implements ViewComponentManageable, ViewHidable
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

		/**<code>true</code> if the view should update minimum insets to compensate for components located in the insets, or <code>false</code> if the normal insets should be used.*/
		private final boolean componentInsetsUpdated;

			/**@return <code>true</code> if the view should update minimum insets to compensate for components located in the insets, or <code>false</code> if the normal insets should be used.*/
			protected boolean isComponentInsetsUpdated() {return componentInsetsUpdated;}

		/**@return The left inset, (&gt;=0);*/
		protected short getLeftInset()
		{
			final short inset=super.getLeftInset();	//get the normal inset
			return isComponentInsetsUpdated() ? max(inset, (short)getComponentManager().getMinimumLeftInset()) : inset;	//compensate for components in the inset if we should
		}

		/**@return The right inset, (&gt;=0);*/
		protected short getRightInset()
		{
			final short inset=super.getRightInset();	//get the normal inset
			return isComponentInsetsUpdated() ? max(inset, (short)getComponentManager().getMinimumRightInset()) : inset;	//compensate for components in the inset if we should
		}

		/**@return The top inset, (&gt;=0);*/
		protected short getTopInset()
		{
			final short inset=super.getTopInset();	//get the normal inset
			return isComponentInsetsUpdated() ? max(inset, (short)getComponentManager().getMinimumTopInset()) : inset;	//compensate for components in the inset if we should
		}

		/**@return The bottom inset, (&gt;=0);*/
		protected short getBottomInset()
		{
			final short inset=super.getBottomInset();	//get the normal inset
			return isComponentInsetsUpdated() ? max(inset, (short)getComponentManager().getMinimumBottomInset()) : inset;	//compensate for components in the inset if we should
		}

		/**Constructs a fragment view for the paragraph.
		@param element The element this view is responsible for.
		@param axis The tiling axis, either View.X_AXIS or View.Y_AXIS.
		@param wholeView The original, unfragmented view from which this fragment (or one or more intermediate fragments) was broken.
		@param firstFragment Whether this is the first fragement of the original view.
		@param lastFragment Whether this is the last fragment of the original view.
		@param compensateInsets <code>true</code> if the view should update minimum insets to compensate for components located in the insets, or <code>false</code> if the normal insets should be used.
		*/
		public XMLComponentParagraphFragmentView(final Element element, final int axis, final View wholeView, final boolean firstFragment, final boolean lastFragment, final boolean compensateInsets)
		{
			super(element, axis, wholeView, firstFragment, lastFragment); //do the default construction
			componentManager=new ViewComponentManager(this);  //create a component manager to manage our components
			this.componentInsetsUpdated=compensateInsets;	//save whether we should compensate for components in the insets
		}

		/**Creates a fragment view into which pieces of this view will be placed.
		@param isFirstFragment Whether this fragment holds the first part of the original view.
		@param isLastFragment Whether this fragment holds the last part of the original view.
		*/
		public View createFragmentView(final boolean isFirstFragment, final boolean isLastFragment)
		{
		  return new XMLComponentParagraphFragmentView(getElement(), getAxis(), getWholeView(), isFirstFragment, isLastFragment, isComponentInsetsUpdated());	//create a fragment of this view, indicating the original view
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

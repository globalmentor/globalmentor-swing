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

import java.awt.*;
import javax.swing.text.*;

import com.globalmentor.swing.text.*;

/**
 * A view that maintains components that are displayed on the image.
 * @author Garret Wilson
 */
public abstract class XMLComponentImageView extends XMLImageView implements ViewComponentManageable {

	/** The object that manages the components this view owns. */
	private final ViewComponentManager componentManager;

	/** @return The object that manages the components this view owns. */
	public ViewComponentManager getComponentManager() {
		return componentManager;
	}

	/**
	 * Creates a new view that represents an image that can contain components.
	 * @param element The element for which to create the view.
	 */
	public XMLComponentImageView(final Element element) {
		super(element); //do the default constructing
		componentManager = new ViewComponentManager(this); //create a component manager to manage our components
	}

	/**
	 * Called when the view is being installed or removed. This method makes sure the component manager properly removes the associated components from the
	 * container before the veiw is removed.
	 * @param parent The new parent, or <code>null</code> if the view is being removed from a parent to which it was previously added.
	 */
	/*TODO del; doesn't work; a deep view won't necessarily have its parent set to null
		public void setParent(final View parent)
		{
	Log.trace("setting parent of component image view: ", parent!=null ? parent.getClass().getName() : null);
			if(parent==null) {	//if the view is being removed from its parent
	Log.trace("view being removed");
				setShowing(false);  //show that we're not showing anymore, so that the component manager will remove owned components from the container
	    }
			super.setParent(parent); //set the parent normally
		}
	*/

	/**
	 * Called when the view is being hidden by a parent that hides views, such as a paged view. This implementation hides associated components, if available.
	 * @param showing <code>true</code> if the view is beginning to be shown, <code>false</code> if the view is beginning to be hidden.
	 * @see #getComponentManager
	 */
	public void setShowing(final boolean showing) {
		componentManager.setShowing(showing); //tell the component manager our new status
		super.setShowing(showing); //update showing in the parent class
	}

	/**
	 * Paints the component. Informs the component manager that it should update the components if needed.
	 * @param graphics The rendering surface to use.
	 * @param allocation The allocated region to render into.
	 * @see XMLImageView#paint
	 */
	public void paint(final Graphics graphics, final Shape allocation) {
		componentManager.setLocation(allocation); //tell the component manager our new location
		super.paint(graphics, allocation); //do the default painting, which will update our dimensions if needed
	}

	/**
	 * Sets the size of the object, while keeping the object in the same proportions. Informs the component manager that it should update the components if
	 * needed.
	 * @param width The width (&gt;=0).
	 * @param height The height (&gt;=0).
	 */
	public void setSize(float width, float height) {
		componentManager.setSize(getWidth(), getHeight(), getCurrentWidth(), getCurrentHeight()); //tell the component manager our new size
		super.setSize(width, height); //do the default size setting
	}

}

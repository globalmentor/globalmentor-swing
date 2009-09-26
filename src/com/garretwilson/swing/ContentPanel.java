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

package com.garretwilson.swing;

import java.awt.*;
import com.garretwilson.awt.*;
import com.globalmentor.util.*;

/**A generic panel that allows easy setup of a content component which defaults to the center.
<p>This panel uses a {@link BasicGridBagLayout} as its layout manager,
	defaulting to the border paradigm.</p>
<p>If the panel is inside a <code>JOptionPane</code>, the window containing
	to ensure the component has enough room every time the content
	component changes.</p>  
<p>If the content component implements <code>DefaultFocusable</code> and knows
	which component should get the default focus, any request for the default
	focus component will be delegated to the content component.</p>
<p>If the content component implements <code>Modifiable</code>, any
	modifications of the content component will cause this panel to be
	modified. Setting the modified status of this panel to <code>false</code>
	will also set the modified status of any <code>Modifiable</code> content
	component to <code>false</code>.</p>
@author Garret Wilson
@see BasicGridBagLayout
@see DefaultFocusable
@see com.globalmentor.model.Modifiable
*/
public class ContentPanel extends ModifiablePanel implements CanClosable
{

	/**The constraints to use in positioning the content component;
		defaults to <code>BorderLayout.CENTER</code>. 
	*/
	private final Object contentConstraints;

		/**The constraints to use in positioning the content component;
			defaults to <code>BorderLayout.CENTER</code>. 
		*/
		protected Object getContentConstraints() {return contentConstraints;}

		/**Sets the constraints to use in positioning the content component.
		@param constraints The position of the content component, such as
			<code>BorderLayout.CENTER</code> or a <code>GridBagConstraints</code>
			instance. 
		*/
//TODO del if not needed		public void setContentConstraints(final Object constraints) {contentConstraints=constraints;}

	/**The main content component of the panel.*/
	private Component contentComponent=null;

		/**@return The main content component of the panel, or
			<code>null</code> if there is no content component.
		*/
		public Component getContentComponent()
		{
			return contentComponent;  //return the content component
		}
	
		/**Sets the main content component of the panel.
		<p>If the panel is inside a <code>JOptionPane</code>, the window containing
			to ensure the component has enough room.</p>  
		@param newContentComponent The new content component for the panel,
			or <code>null</code> for no content component.
		*/
		public void setContentComponent(final Component newContentComponent)
		{
			if(contentComponent!=newContentComponent) //if the content component is really changing
			{
			  if(contentComponent!=null)  //if we already have an content component
			  {
					remove(contentComponent);   //remove the current one
			  }
				contentComponent=newContentComponent; //store the content component
				if(newContentComponent!=null)	//if we were given a new content component
				{
					add(newContentComponent, getContentConstraints());  //put the content component in the center of the panel
				}
				revalidate();	//update the layout
				if(newContentComponent!=null)	//if we were given a new content component
				{
					newContentComponent.repaint();	//repaint the component (important if we're inside a JOptionPane, for instance)
				}
				if(newContentComponent instanceof DefaultFocusable)	//if the content component knows what should be focused by default
				{
					((DefaultFocusable)newContentComponent).requestDefaultFocusComponentFocus();	//let the component request the focus for its default focus component
				}
			}
		}

	/**Returns the default focus component.
	If the content component implements <code>DefaultFocusable</code> and knows
		which component should get the default focus, any request for the default
		focus component will be delegated to the content component. Otherwise,
		whichever default focus component is set will be returned.
	@return The component that should get the default focus, or
		<code>null</code> if no component should get the default focus or it is
		unknown which component should get the default focus.
	*/
	public Component getDefaultFocusComponent()
	{
		if(getContentComponent() instanceof DefaultFocusable)	//if the content component knows what should be focused by default
		{
			final DefaultFocusable defaultFocusable=(DefaultFocusable)getContentComponent();	//cast the content component to a default focusable object
			if(defaultFocusable.getDefaultFocusComponent()!=null)	//if the component knows what should get the default focus
			{	 
				return defaultFocusable.getDefaultFocusComponent();	//return the default focus component specified by the content component 
			}
		}
		return super.getDefaultFocusComponent();	//if we can't get the default focus component from the content component, return whatever had been set with this panel
	}

	/**Default constructor.*/
	public ContentPanel()
	{
		this(true); //initialize the panel
	}

	/**Content component constructor.
		The content component is guaranteed to be set before
		<code>initializeUI</code> is called.
	@param contentComponent The new component for the panel, or
		null if there should be no content component.
	*/
	public ContentPanel(final Component contentComponent)
	{
		this(contentComponent, true); //create and initialize the panel
	}

	/**Initialize constructor.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ContentPanel(final boolean initialize)
	{
		this(null, initialize);	//construct the panel with no content component and initialize the panel if we should
	}

	/**Position constructor.
	@param constraints The position of the content component, such as
		<code>BorderLayout.CENTER</code> or a <code>GridBagConstraints</code>
		instance. 
	*/
	public ContentPanel(final Object constraints)
	{
		this(constraints, true);	//construct and initialize the panel with no content component
	}

	/**Position constructor with optional initialization.
	@param constraints The position of the content component, such as
		<code>BorderLayout.CENTER</code> or a <code>GridBagConstraints</code>
		instance. 
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ContentPanel(final Object constraints, final boolean initialize)
	{
		this(null, constraints, initialize);	//construct the component with constraints and optionally initialize it
	}

	/**Content component constructor with center position and optional initialization.
		The content component is guaranteed to be set before
		<code>initializeUI</code> is called.
	@param contentComponent The new component for the center of the panel, or
		null if there should be no content component.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ContentPanel(final Component contentComponent, final boolean initialize)
	{
		this(contentComponent, BorderLayout.CENTER, initialize);	//construct the panel with the content component in the center
	}

	/**Content component constructor with constraints and optional initialization.
		The content component is guaranteed to be set before
		<code>initializeUI</code> is called.
	@param contentComponent The new component for the panel, or
		null if there should be no content component.
	@param constraints The position of the content component, such as
		<code>BorderLayout.CENTER</code> or a <code>GridBagConstraints</code>
		instance. 
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public ContentPanel(final Component contentComponent, final Object constraints, final boolean initialize)
	{
		super(new BasicGridBagLayout(), false);	//construct the default panel without initializing it
		contentConstraints=constraints;	//save the constraints to use for the content component
	  setContentComponent(contentComponent);  //set the content component, if any
		if(initialize)  //if we should initialize
			initialize();   //initialize the panel
	}

	/**@return <code>true</code> if the panel can close.
		This request is delegated to the content pane if possible, and therefore
		any class that overrides this one should call this version. 
	@see ApplicationFrame#canClose
	*/
	public boolean canClose()
	{
		final Component contentComponent=getContentComponent();	//get the content component
		if(contentComponent instanceof CanClosable)	//if the content component knows how to ask about closing
			return ((CanClosable)contentComponent).canClose();	//return whether the content component can close
		else	//if the content component doesn't know anything about closing		
			return super.canClose();  //ask the default implementation if we can close
	}
	
}

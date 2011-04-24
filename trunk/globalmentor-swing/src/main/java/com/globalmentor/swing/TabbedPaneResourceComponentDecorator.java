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

package com.globalmentor.swing;

import java.awt.Component;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.globalmentor.net.Resource;

/**Class that manages resource components in a tabbed pane.
@param <R> The type of resource the components of which are being managed.
@author Garret Wilson
@see ResourceComponentManager
*/
public class TabbedPaneResourceComponentDecorator<R extends Resource> extends ResourceComponentDecorator<R>
{

	/**The tabbed pane that hosts the resource components.*/
	private final JTabbedPane tabbedPane;
	
		/**@return The tabbed pane that hosts the resource components.*/
		protected JTabbedPane getTabbedPane() {return tabbedPane;}

	/**Constructs a decorator for a tabbed pane, using the given
		resource component manager to manager resource components.
	@param tabbedPane The tabbed pane that hosts the resource components.
	@param resourceComponentManager The delegate manager of resource components.
	*/
	public TabbedPaneResourceComponentDecorator(final JTabbedPane tabbedPane, final ResourceComponentManager<R> resourceComponentManager)
	{
		super(resourceComponentManager);	//construct the parent class
		this.tabbedPane=tabbedPane;	//save the tabbed pane
		tabbedPane.addChangeListener(new ChangeListener()	//listen for tab changes, and update the selected resource in response
				{
					public void stateChanged(final ChangeEvent changeEvent)	//when the tab changes
					{
						final Component selectedComponent=tabbedPane.getSelectedComponent();	//see which tab is now selected
						if(selectedComponent!=null)	//if there is a selected tab
						{
							final ResourceComponentManager<R> resourceComponentManager=getResourceComponentManager();	//get the resource component manager
								//get the resource state corresponding to this component
							final ResourceComponentManager<R>.ResourceComponentState resourceComponentState=resourceComponentManager.getResourceComponentState(selectedComponent);
							if(resourceComponentState!=null)	//if there is a corresponding state
							{
								resourceComponentManager.setResourceComponentState(resourceComponentState);	//switch to that state
							}
						}
					}
				});
	}

	/**Called when a resource component is added.
	This implementation adds a tab to the tabbed pane corresponding to the resource component.
	@param resourceComponentState The resource and component added.
	*/
	protected void onResourceComponentAdded(final ResourceComponentManager<R>.ResourceComponentState resourceComponentState)
	{
		getTabbedPane().addTab(resourceComponentState.getLabel(), resourceComponentState.getComponent());	//add a new tab to the tabbed pane		
	}

	/**Called when a resource component is removed.
	This implementation removes the resource component from the tabbed pane.
	@param resourceComponentState The resource and component removed.
	*/
	protected void onResourceComponentRemoved(final ResourceComponentManager<R>.ResourceComponentState resourceComponentState)
	{
		getTabbedPane().remove(resourceComponentState.getComponent());	//remove the component from the tabbed pane
	}

	/**Called when a resource component is selected.
	@param oldResourceComponentState The previously selected resource and component.
	@param newResourceComponentState The newly selected resource and component.
	*/
	protected void onResourceComponentSelected(final ResourceComponentManager<R>.ResourceComponentState oldResourceComponentState, final ResourceComponentManager<R>.ResourceComponentState newResourceComponentState)
	{
		final Component newComponent=newResourceComponentState!=null ? newResourceComponentState.getComponent() : null;	//get the new component, if there is one
		if(newComponent!=null)	//if there is a new component being selected (ignore null components---we can't select "no component")
		{
			getTabbedPane().setSelectedComponent(newComponent);	//select the new component in the tabbed pane
/*TODO fix; doesn't work
			if(newComponent instanceof DefaultFocusable)	//if the new component is default focusable
			{
				((DefaultFocusable)newComponent).requestDefaultFocusComponentFocus();	//tell the focus component to request focus for its default component
			}
			else	//if the component doesn't itself know about default focus components
			{
				newComponent.requestFocusInWindow();	//give the focus to the new tab component TODO see if this is the best place to put all this, or if this should to in some JTabbedPane descendant, or in some BasicPanel DefaultFocusable routine
			}
*/
		}
	}

}

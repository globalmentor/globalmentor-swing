package com.garretwilson.swing;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**Class that manages resource components in a content panel using a
	single document interface paradigm.
<p>The content panel's content component is updated whenever the delegate
	resource component manager indicates the resource component state has
	changed.</p>
@author Garret Wilson
@see ResourceComponentManager
@see ContentPanel#setContentComponent(Component)
*/
public class ContentPanelSDIManager
{

	/**The content panel that hosts the resource components.*/
	private final ContentPanel contentPanel;
	
		/**@return The content panel that hosts the resource components.*/
		protected ContentPanel getContentPanel() {return contentPanel;}

	/**The delegate manager of resource components.*/
	private final ResourceComponentManager resourceComponentManager;

		/**@return The delegate manager of resource components.*/
		public ResourceComponentManager getResourceComponentManager() {return resourceComponentManager;}

	/**Constructs an SDI manager for a content panel, using the given
		resource component manager to manager resource components.
	@param contentPanel The content panel that hosts the resource components.
	@param The delegate manager of resource components.
	*/
	public ContentPanelSDIManager(final ContentPanel contentPanel, final ResourceComponentManager resourceComponentManager)
	{
		this.contentPanel=contentPanel;	//save the content panel
		this.resourceComponentManager=resourceComponentManager;	//save the resource component manager
		resourceComponentManager.addPropertyChangeListener(ResourceComponentManager.RESOURCE_COMPONENT_STATE_PROPERTY, new PropertyChangeListener()
				{
					public void propertyChange(final PropertyChangeEvent propertyChangeEvent)	//when the resource component state changes, call the appropriate method
					{
						onResourceComponentStateChange((ResourceComponentManager.ResourceComponentState)propertyChangeEvent.getOldValue(), (ResourceComponentManager.ResourceComponentState)propertyChangeEvent.getNewValue()); 
					}
				});
	}

	/**Called in response to a change in resource component.
	@param oldResourceComponentState The old resource and component, or
		<code>null</code> if there is no old component.
	@param newResourceComponentState The new resource and component, or
		<code>null</code> if there is no new component.
	*/
	protected void onResourceComponentStateChange(final ResourceComponentManager.ResourceComponentState oldResourceComponentState, final ResourceComponentManager.ResourceComponentState newResourceComponentState)
	{
//G***del if not needed		final Component oldComponent=oldResourceComponentState!=null ? oldResourceComponentState.getComponent() : null;	//get the old component, if there is one
		final Component newComponent=newResourceComponentState!=null ? newResourceComponentState.getComponent() : null;	//get the new component, if there is one
		getContentPanel().setContentComponent(newComponent);	//change the content component in the panel
	} 

}

package com.garretwilson.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**Abstract class that can decorate a user interface from component changes
	of a resource component manager.
@author Garret Wilson
@see ResourceComponentManager
*/
public abstract class ResourceComponentDecorator
{

	/**The delegate manager of resource components.*/
	private final ResourceComponentManager resourceComponentManager;

		/**@return The delegate manager of resource components.*/
		public ResourceComponentManager getResourceComponentManager() {return resourceComponentManager;}

	/**Constructs a decorator that uses the given resource component manager
		to manager resource components.
	@param resourceComponentManager The delegate manager of resource components.
	*/
	public ResourceComponentDecorator(final ResourceComponentManager resourceComponentManager)
	{
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
	protected abstract void onResourceComponentStateChange(final ResourceComponentManager.ResourceComponentState oldResourceComponentState, final ResourceComponentManager.ResourceComponentState newResourceComponentState);

}

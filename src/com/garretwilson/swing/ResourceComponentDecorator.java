package com.garretwilson.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.garretwilson.model.Resource;
import com.garretwilson.util.CanClosable;

/**Abstract class that can decorate a user interface from component changes
	of a resource component manager.
@author Garret Wilson
@param <R> The type of resource the components of which are being managed.
@see ResourceComponentManager
*/
public abstract class ResourceComponentDecorator<R extends Resource> implements CanClosable
{

	/**The delegate manager of resource components.*/
	private final ResourceComponentManager<R> resourceComponentManager;

		/**@return The delegate manager of resource components.*/
		public ResourceComponentManager<R> getResourceComponentManager() {return resourceComponentManager;}

	/**Constructs a decorator that uses the given resource component manager
		to manager resource components.
	@param resourceComponentManager The delegate manager of resource components.
	*/
	public ResourceComponentDecorator(final ResourceComponentManager<R> resourceComponentManager)
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
	protected abstract void onResourceComponentStateChange(final ResourceComponentManager.ResourceComponentState<R> oldResourceComponentState, final ResourceComponentManager.ResourceComponentState<R> newResourceComponentState);

	/**Determines if the resource component manager can close.
	@return <code>true</code> if resource component manager can close
	*/
	public boolean canClose()
	{
		return getResourceComponentManager().canClose();	//see if the resource component manager can close
	}

}

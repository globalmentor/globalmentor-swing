package com.garretwilson.swing;

import com.garretwilson.model.Resource;

/**Abstract class that can decorate a user interface from component changes
	of a resource component manager.
@author Garret Wilson
@param <R> The type of resource the components of which are being managed.
@see ResourceComponentManager
*/
public abstract class ResourceComponentDecorator<R extends Resource>
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
		resourceComponentManager.addResourceComponentListener(new ResourceComponentListener<R>()	//listen for resource component changes, and forward to our local methods
				{
					public void onResourceComponentAdded(final ResourceComponentManager<R>.ResourceComponentState resourceComponentState) {ResourceComponentDecorator.this.onResourceComponentAdded(resourceComponentState);}
					public void onResourceComponentRemoved(final ResourceComponentManager<R>.ResourceComponentState resourceComponentState) {ResourceComponentDecorator.this.onResourceComponentRemoved(resourceComponentState);}
					public void onResourceComponentSelected(final ResourceComponentManager<R>.ResourceComponentState oldResourceComponentState, final ResourceComponentManager<R>.ResourceComponentState newResourceComponentState) {ResourceComponentDecorator.this.onResourceComponentSelected(oldResourceComponentState, newResourceComponentState);}
				});
	}

	/**Called when a resource component is added.
	@param resourceComponentState The resource and component added.
	*/
	protected abstract void onResourceComponentAdded(final ResourceComponentManager<R>.ResourceComponentState resourceComponentState);

	/**Called when a resource component is removed.
	@param resourceComponentState The resource and component removed.
	*/
	protected abstract void onResourceComponentRemoved(final ResourceComponentManager<R>.ResourceComponentState resourceComponentState);

	/**Called when a resource component is selected.
	@param oldResourceComponentState The previously selected resource and component.
	@param newResourceComponentState The newly selected resource and component.
	*/
	protected abstract void onResourceComponentSelected(final ResourceComponentManager<R>.ResourceComponentState oldResourceComponentState, final ResourceComponentManager<R>.ResourceComponentState newResourceComponentState);

	/**Determines if the resource component manager can close all open resources.
	@return <code>true</code> if resource component manager can close all open resources.
	*/
	public boolean canCloseAll()
	{
		return getResourceComponentManager().canCloseAll();	//see if the resource component manager can close all open resources
	}

}

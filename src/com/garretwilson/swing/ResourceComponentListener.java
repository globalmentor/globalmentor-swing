package com.garretwilson.swing;

import java.util.EventListener;

import com.globalmentor.net.Resource;

/**Indicates the implementing class can listen for resource component changes.
@param <R> The type of resource the components of which are being managed.
@author Garret Wilson
*/
public interface ResourceComponentListener<R extends Resource> extends EventListener
{

	/**Called when a resource component is added.
	@param resourceComponentState The resource and component added.
	*/
	public void onResourceComponentAdded(final ResourceComponentManager<R>.ResourceComponentState resourceComponentState);

	/**Called when a resource component is removed.
	@param resourceComponentState The resource and component removed.
	*/
	public void onResourceComponentRemoved(final ResourceComponentManager<R>.ResourceComponentState resourceComponentState);

	/**Called when a resource component is selected.
	@param oldResourceComponentState The previously selected resource and component.
	@param newResourceComponentState The newly selected resource and component.
	*/
	public void onResourceComponentSelected(final ResourceComponentManager<R>.ResourceComponentState oldResourceComponentState, final ResourceComponentManager<R>.ResourceComponentState newResourceComponentState);

}
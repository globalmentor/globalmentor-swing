package com.garretwilson.swing;

import java.awt.Component;

import com.garretwilson.model.Resource;

/**Class that manages resource components in a content panel.
<p>The content panel's content component is updated whenever the delegate
	resource component manager indicates the resource component state has
	changed.</p>
@param <R> The type of resource the components of which are being managed.
@author Garret Wilson
@see ResourceComponentManager
@see ContentPanel#setContentComponent(Component)
*/
public class ContentPanelResourceComponentDecorator<R extends Resource> extends ResourceComponentDecorator<R>
{

	/**The content panel that hosts the resource components.*/
	private final ContentPanel contentPanel;
	
		/**@return The content panel that hosts the resource components.*/
		protected ContentPanel getContentPanel() {return contentPanel;}

	/**Constructs an SDI manager for a content panel, using the given
		resource component manager to manager resource components.
	@param contentPanel The content panel that hosts the resource components.
	@param resourceComponentManager The delegate manager of resource components.
	*/
	public ContentPanelResourceComponentDecorator(final ContentPanel contentPanel, final ResourceComponentManager<R> resourceComponentManager)
	{
		super(resourceComponentManager);	//construct the parent class
		this.contentPanel=contentPanel;	//save the content panel
	}

	/**Called in response to a change in resource component.
	@param oldResourceComponentState The old resource and component, or
		<code>null</code> if there is no old component.
	@param newResourceComponentState The new resource and component, or
		<code>null</code> if there is no new component.
	*/
	protected void onResourceComponentStateChange(final ResourceComponentManager<R>.ResourceComponentState oldResourceComponentState, final ResourceComponentManager<R>.ResourceComponentState newResourceComponentState)
	{
//G***del if not needed		final Component oldComponent=oldResourceComponentState!=null ? oldResourceComponentState.getComponent() : null;	//get the old component, if there is one
		final Component newComponent=newResourceComponentState!=null ? newResourceComponentState.getComponent() : null;	//get the new component, if there is one
		getContentPanel().setContentComponent(newComponent);	//change the content component in the panel
	} 

	/**Called when a resource component is added.
	This implementation does nothing and waits until a new resource is selected.
	@param resourceComponentState The resource and component added.
	*/
	protected void onResourceComponentAdded(final ResourceComponentManager<R>.ResourceComponentState resourceComponentState) {}

	/**Called when a resource component is removed.
	This implementation does nothing and waits until a new resource is selected.
	@param resourceComponentState The resource and component removed.
	*/
	protected void onResourceComponentRemoved(final ResourceComponentManager<R>.ResourceComponentState resourceComponentState) {}

	/**Called when a resource component is selected.
	@param oldResourceComponentState The previously selected resource and component.
	@param newResourceComponentState The newly selected resource and component.
	*/
	protected void onResourceComponentSelected(final ResourceComponentManager<R>.ResourceComponentState oldResourceComponentState, final ResourceComponentManager<R>.ResourceComponentState newResourceComponentState)
	{
		final Component newComponent=newResourceComponentState!=null ? newResourceComponentState.getComponent() : null;	//get the new component, if there is one
		getContentPanel().setContentComponent(newComponent);	//change the content component in the panel		
	}

}

package com.garretwilson.swing;

import java.awt.Component;

/**Class that manages resource components in a content panel using a
	single document interface paradigm.
<p>The content panel's content component is updated whenever the delegate
	resource component manager indicates the resource component state has
	changed.</p>
@author Garret Wilson
@see ResourceComponentManager
@see ContentPanel#setContentComponent(Component)
*/
public class ContentPanelSDIDecorator extends ResourceComponentDecorator
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
	public ContentPanelSDIDecorator(final ContentPanel contentPanel, final ResourceComponentManager resourceComponentManager)
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
	protected void onResourceComponentStateChange(final ResourceComponentManager.ResourceComponentState oldResourceComponentState, final ResourceComponentManager.ResourceComponentState newResourceComponentState)
	{
//G***del if not needed		final Component oldComponent=oldResourceComponentState!=null ? oldResourceComponentState.getComponent() : null;	//get the old component, if there is one
		final Component newComponent=newResourceComponentState!=null ? newResourceComponentState.getComponent() : null;	//get the new component, if there is one
		getContentPanel().setContentComponent(newComponent);	//change the content component in the panel
	} 

}

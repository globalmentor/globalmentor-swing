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

import com.globalmentor.net.Resource;

/**
 * Class that manages resource components in a content panel.
 * <p>
 * The content panel's content component is updated whenever the delegate resource component manager indicates the resource component state has changed.
 * </p>
 * @param <R> The type of resource the components of which are being managed.
 * @author Garret Wilson
 * @see ResourceComponentManager
 * @see ContentPanel#setContentComponent(Component)
 */
public class ContentPanelResourceComponentDecorator<R extends Resource> extends ResourceComponentDecorator<R> {

	/** The content panel that hosts the resource components. */
	private final ContentPanel contentPanel;

	/** @return The content panel that hosts the resource components. */
	protected ContentPanel getContentPanel() {
		return contentPanel;
	}

	/**
	 * Constructs an decorator for a content panel, using the given resource component manager to manager resource components.
	 * @param contentPanel The content panel that hosts the resource components.
	 * @param resourceComponentManager The delegate manager of resource components.
	 */
	public ContentPanelResourceComponentDecorator(final ContentPanel contentPanel, final ResourceComponentManager<R> resourceComponentManager) {
		super(resourceComponentManager); //construct the parent class
		this.contentPanel = contentPanel; //save the content panel
	}

	/**
	 * Called when a resource component is added. This implementation does nothing and waits until a new resource is selected.
	 * @param resourceComponentState The resource and component added.
	 */
	protected void onResourceComponentAdded(final ResourceComponentManager<R>.ResourceComponentState resourceComponentState) {
	}

	/**
	 * Called when a resource component is removed. This implementation does nothing and waits until a new resource is selected.
	 * @param resourceComponentState The resource and component removed.
	 */
	protected void onResourceComponentRemoved(final ResourceComponentManager<R>.ResourceComponentState resourceComponentState) {
	}

	/**
	 * Called when a resource component is selected.
	 * @param oldResourceComponentState The previously selected resource and component.
	 * @param newResourceComponentState The newly selected resource and component.
	 */
	protected void onResourceComponentSelected(final ResourceComponentManager<R>.ResourceComponentState oldResourceComponentState,
			final ResourceComponentManager<R>.ResourceComponentState newResourceComponentState) {
		final Component newComponent = newResourceComponentState != null ? newResourceComponentState.getComponent() : null; //get the new component, if there is one
		getContentPanel().setContentComponent(newComponent); //change the content component in the panel		
	}

}

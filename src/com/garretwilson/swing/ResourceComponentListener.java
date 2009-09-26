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
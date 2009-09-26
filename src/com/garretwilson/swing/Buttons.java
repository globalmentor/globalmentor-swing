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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractButton;

/**Helper utilities for buttons.
@author Garret Wilson
*/
public class Buttons
{

	/**The property representing the <code>Boolean</code> value of whether text should be hidden.*/
	public final static String HIDE_ACTION_TEXT_PROPERTY="hideActionText";

	/**Creates a property change listener that will, in response to a toggle
		action's "selected" property changing, will update the button's selected
		state.
	@param button The button the state of which to update
	@return A property change listener to respond to an action's selected state
		changing.
	@see AbstractToggleAction#SELECTED_KEY
	@see AbstractButton#setSelected(boolean)
	*/
	public static PropertyChangeListener createToggleActionSelectedPropertyChangeListener(final AbstractButton button)
	{
		return new PropertyChangeListener()
				{
					public void propertyChange(final PropertyChangeEvent propertyChangeEvent)
					{
						if(AbstractToggleAction.SELECTED_KEY.equals(propertyChangeEvent.getPropertyName()))	//if this is the "selected" property
						{
							if(propertyChangeEvent.getNewValue() instanceof Boolean)	//if the new value is a boolean
								button.setSelected(((Boolean)propertyChangeEvent.getNewValue()).booleanValue());	//set the new selected state of the button
						}
					}
				};
	}

}

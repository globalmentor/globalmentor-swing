package com.garretwilson.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractButton;

/**Helper utilities for buttons.
@author Garret Wilson
*/
public class ButtonUtilities
{

	/**Creates a property change listener that will, in response to a toggle
	 * action's "selected" property changing, will update the button's selected
	 * state.
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

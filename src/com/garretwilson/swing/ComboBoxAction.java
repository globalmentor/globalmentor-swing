package com.garretwilson.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.*;
import java.beans.*;
import java.lang.ref.*;

import javax.swing.*;
import javax.swing.event.*;

import static com.garretwilson.lang.ObjectUtilities.*;
import static com.garretwilson.swing.ComponentConstants.*;
import com.garretwilson.util.Debug;

/**An action represented by a combo box.
@author Garret Wilson
*/
public class ComboBoxAction extends ComponentAction<JComboBox>
{

	/**The model for combo box components.*/
	private final ComboBoxModel comboBoxModel;

		/**@return The model for combo box components.*/
		public ComboBoxModel getComboBoxModel() {return comboBoxModel;}

	/**Defines a proxy action object with a default description string and default icon.
	@param comboBoxModel The model for combo box components.
	*/
	public ComboBoxAction(final ComboBoxModel comboBoxModel)
	{
		super();  //construct the parent
		this.comboBoxModel=comboBoxModel;	//save the combo box model
	}

	/**Defines a proxy action object with the specified description string and a default icon.
	@param name The name description of the action.
	@param comboBoxModel The model for combo box components.
	*/
	public ComboBoxAction(final String name, final ComboBoxModel comboBoxModel)
	{
		this(comboBoxModel);  //do the default construction
		putValue(NAME, name);
	}

	/**Defines a proxy action object with the specified description string and the specified icon.
	@param name The name description of the action.
	@param icon The icon to represent the action.
	@param comboBoxModel The model for combo box components.
	*/
	public ComboBoxAction(final String name, final Icon icon, final ComboBoxModel comboBoxModel)
	{
		this(name, comboBoxModel);  //do the default construction
		putValue(Action.SMALL_ICON, icon);
	}

	/**Creates a component for the action.
	@return A new component for the action.
	*/
	protected JComboBox createComponent()
	{
		return new JComboBox(getComboBoxModel());	//create a new combo box component with the combo box model
	}

	/**Called when the action should be performed.
	This version does nothing.
	@param actionEvent The event causing the action.
	*/
	public void actionPerformed(final ActionEvent actionEvent)
	{
	}

}

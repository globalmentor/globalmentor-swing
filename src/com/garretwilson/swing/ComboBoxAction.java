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

import java.awt.event.*;

import javax.swing.*;

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

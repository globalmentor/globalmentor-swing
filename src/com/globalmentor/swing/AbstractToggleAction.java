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

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.globalmentor.java.Booleans;

/**Action that that represents a toggled binary selected state.
A component factory may create a checkbox or, if the action indicates a group,
	a radio button to represent the action.
@author Garret Wilson
*/
public abstract class AbstractToggleAction extends AbstractAction
{

	/**The <code>Boolean</code> key used for storing the selected state of the action.*/
	public static final String SELECTED_KEY="selected";

	/**@return <code>true</code> if the selected property value is <code>true</code>.
	@see #SELECTED_KEY
	*/
	public boolean isSelected() {return Booleans.booleanValue(getValue(SELECTED_KEY));}

	/**Sets the selected state of the action. Any component that represents this
		action should automatically update its visual status in response to a
		change of this property.
	@see #SELECTED_KEY
	*/
	public void setSelected(final boolean selected)
	{
		putValue(SELECTED_KEY, Boolean.valueOf(selected));	//store a boolean value indicating the new selected state		
	}

	/**The group with which the action is associated, or <code>null</code> if no
		group is indicated.
	*/
	private final ActionGroup group;

		/**@return The group with which the action is associated, or
			<code>null</code> if no group is indicated.
		*/
		public ActionGroup getGroup() {return group;}

	/**Default constructor with no group.*/
	public AbstractToggleAction()
	{
		this((ActionGroup)null);  //construct the class with no group
	}

	/**Group constructor.
	@param group The group with which the action is associated, or
		<code>null</code> for new group.
	*/
	public AbstractToggleAction(final ActionGroup group)
	{
		super();  //construct the parent
		this.group=group;	//set the group
	}

	/**Name constructor with no group.
	@param name The name description of the action.
	*/
	public AbstractToggleAction(final String name)
	{
		this(name, (ActionGroup)null);	//construct the class with no group
	}

	/**Name and group constructor.
	@param name The name description of the action.
	@param group The group with which the action is associated, or
		<code>null</code> for new group.
	*/
	public AbstractToggleAction(final String name, final ActionGroup group)
	{
		super(name);  //construct the parent
		this.group=group;	//set the group
	}

	/**Name and icon constructor with no target.
	@param name The name description of the action.
	@param icon The icon to represent the action.
	*/
	public AbstractToggleAction(final String name, final Icon icon)
	{
		this(name, icon, null);  //construct the class with no group
	}

	/**Name, icon, and target constructor.
	@param name The name description of the action.
	@param icon The icon to represent the action.
	@param group The group with which the action is associated, or
		<code>null</code> for new group.
	*/
	public AbstractToggleAction(final String name, final Icon icon, final ActionGroup group)
	{
		super(name, icon);  //construct the parent
		this.group=group;	//set the group
	}

}

package com.garretwilson.swing;

import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.globalmentor.java.BooleanUtilities;

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
	public boolean isSelected() {return BooleanUtilities.booleanValue(getValue(SELECTED_KEY));}

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

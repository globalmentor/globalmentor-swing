package com.garretwilson.swing.text;

import java.awt.Component;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import com.garretwilson.swing.FocusTargetAction;

/**Action that only considers focused text components as targets.
@author Garret Wilson
@see JTextComponent
*/
public abstract class TextComponentFocusTargetAction extends FocusTargetAction
{

	/**Gets the current focus owner.
	<p>This version only considers focus text components.</p> 
	@return The current focus owner, or <code>null</code> if there is no current
		focus owner or the focus owner is not a text component.
	*/
	protected Component getFocusOwner()
	{
		final Component focusOwner=super.getFocusOwner();	//get the default focus owner
		return focusOwner instanceof JTextComponent ? focusOwner : null;	//only return the focused component if it is a test component
	}

	/**Default constructor with no default target.*/
	public TextComponentFocusTargetAction()
	{
		super();  //construct the parent
	}

	/**Name constructor with no default target.
	@param name The name description of the action.
	*/
	public TextComponentFocusTargetAction(final String name)
	{
		super(name);	//construct the parent
	}

	/**Name and target constructor.
	@param name The name description of the action.
	@param defaultTarget The default target component, or <code>null</code> if there
		should be no default target.
	*/
	public TextComponentFocusTargetAction(final String name, final JTextComponent defaultTarget)
	{
		super(name, defaultTarget);	//construct the parent
	}

	/**Name and icon constructor with no default target.
	@param name The name description of the action.
	@param icon The icon to represent the action.
	*/
	public TextComponentFocusTargetAction(final String name, final Icon icon)
	{
		super(name, icon);  //construct the parent
	}

	/**Name, icon, and default target constructor.
	@param name The name description of the action.
	@param icon The icon to represent the action.
	@param defaultTarget The default target component, or <code>null</code> if there
		should be no default target.
	*/
	public TextComponentFocusTargetAction(final String name, final Icon icon, final JTextComponent defaultTarget)
	{
		super(name, icon, defaultTarget);  //construct the parent
	}

}

package com.garretwilson.swing;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import javax.swing.*;

/**Action that considers the focused component as its target.
<p>In this version, <code>setTarget()</code> sets the default target for the
	situation in which there is no focus owner.</p>
@author Garret Wilson
*/
public abstract class FocusTargetAction extends TargetAction
{

	/**Determines the target of the action.
	<p>This version checks to see if any component owns the focus. If so, that
		component is returns. If not, the default target is returned.</p>
	@return The current focus owner component, or the default target of the
		action if there is no focus owner, or <code>null</code> if there is no
		default target.
	@see #getFocusOwner()
	*	*/
	public Object getTarget()
	{
		final Component focusOwner=getFocusOwner();	//get the current focus owner
		return focusOwner!=null ? focusOwner : super.getTarget();	//return the focus owner or the default target
	}

	/**Gets the current focus owner.
	<p>If a derived class wants to only recognize certain focused targets, it
		should override this method and return <code>null</code> if the currently
		focused component is not of the correct type.</p> 
	@return The current focus owner, or <code>null</code> if there is no current
		focus owner.
	@see KeyboardFocusManager#getFocusOwner()
	*/
	protected Component getFocusOwner()
	{
		return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();	//return the current focus owner
	}

	/**Default constructor with no default target.*/
	public FocusTargetAction()
	{
		super();  //construct the parent
	}

	/**Name constructor with no default target.
	@param name The name description of the action.
	*/
	public FocusTargetAction(final String name)
	{
		super(name);	//construct the parent
	}

	/**Name and target constructor.
	@param name The name description of the action.
	@param defaultTarget The default target component, or <code>null</code> if there
		should be no default target.
	*/
	public FocusTargetAction(final String name, final Component defaultTarget)
	{
		super(name, defaultTarget);	//construct the parent
	}

	/**Name and icon constructor with no default target.
	@param name The name description of the action.
	@param icon The icon to represent the action.
	*/
	public FocusTargetAction(final String name, final Icon icon)
	{
		super(name, icon);  //construct the parent
	}

	/**Name, icon, and default target constructor.
	@param name The name description of the action.
	@param icon The icon to represent the action.
	@param defaultTarget The default target component, or <code>null</code> if there
		should be no default target.
	*/
	public FocusTargetAction(final String name, final Icon icon, final Component defaultTarget)
	{
		super(name, icon, defaultTarget);  //construct the parent
	}

}

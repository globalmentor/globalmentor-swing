package com.garretwilson.swing;

import javax.swing.*;

/**Action that keeps track of a object that represents the target of the action.
@author Garret Wilson
*/
public abstract class TargetAction extends AbstractAction
{

	/**The target of the action, or <code>null</code> if there is no target.*/
	private Object target=null;

		/**@return The target of the action, or <code>null</code> if there is no target.*/
		public Object getTarget() {return target;}

		/**Sets the target of the action.
		@param newTarget The new action target, or <code>null</code> if there should
			be no target.
		*/
		protected void setTarget(final Object newTarget) {target=newTarget;}

	/**Default constructor with no target.*/
	public TargetAction()
	{
		super();  //construct the parent
	}

	/**Name constructor with no target.
	@param name The name description of the action.
	*/
	public TargetAction(final String name)
	{
		this(name, null);	//construct the class with no target
	}

	/**Name and target constructor.
	@param name The name description of the action.
	@param target The new action target, or <code>null</code> if there should
		be no target.
	*/
	public TargetAction(final String name, final Object target)
	{
		super(name);  //construct the parent
		setTarget(target);	//set the target
	}

	/**Name and icon constructor with no target.
	@param name The name description of the action.
	@param icon The icon to represent the action.
	*/
	public TargetAction(final String name, final Icon icon)
	{
		this(name, icon, null);  //construct the class with no target
	}

	/**Name, icon, and target constructor.
	@param name The name description of the action.
	@param icon The icon to represent the action.
	@param target The new action target, or <code>null</code> if there should
		be no target.
	*/
	public TargetAction(final String name, final Icon icon, final Object target)
	{
		super(name, icon);  //construct the parent
		setTarget(target);	//set the target
	}

}

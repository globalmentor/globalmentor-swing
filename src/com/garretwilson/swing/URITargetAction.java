package com.garretwilson.swing;

import java.net.URI;
import javax.swing.*;

/**Action that keeps track of a URI that represents the target of the action.
@author Garret Wilson
*/
public abstract class URITargetAction extends TargetAction
{

	/**@return The URI target of the action, or <code>null</code> if there is no target.*/
	public URI getURITarget() {return (URI)getTarget();}

	/**Sets the URI target of the action.
	@param newURITarget The new action target, or <code>null</code> if there should
		be no target.
	*/
	protected void setURITarget(final URI newURITarget) {setTarget(newURITarget);}

	/**Default constructor with no target.*/
	public URITargetAction()
	{
		super();  //construct the parent
	}

	/**Name constructor with no target.
	@param name The name description of the action.
	*/
	public URITargetAction(final String name)
	{
		super(name, null);	//construct the parent class with no target
	}

	/**Name and target constructor.
	@param name The name description of the action.
	@param target The new action target, or <code>null</code> if there should
		be no target.
	*/
	public URITargetAction(final String name, final URI target)
	{
		super(name, target);  //construct the parent
	}

	/**Name and icon constructor with no target.
	@param name The name description of the action.
	@param icon The icon to represent the action.
	*/
	public URITargetAction(final String name, final Icon icon)
	{
		super(name, icon, null);  //construct the parent class with no target
	}

	/**Name, icon, and target constructor.
	@param name The name description of the action.
	@param icon The icon to represent the action.
	@param target The new action target, or <code>null</code> if there should
		be no target.
	*/
	public URITargetAction(final String name, final Icon icon, final URI target)
	{
		super(name, icon, target);  //construct the parent
	}

}

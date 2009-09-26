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

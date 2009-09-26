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

import java.awt.*;
import javax.swing.*;

/**Utility methods for working with tabbed panes.
@author Garret Wilson
@see JTabbedPane
*/
public class TabbedPaneUtilities
{

	/**If the given component has a tabbed pane as a parent, selects the tab
		of the tabbed pane that contains this component recursively up the
		hierarchy.
	@param component The component that may or may not be contained in one or
		more tabbed panes, or <code>null</code> if there is no component.
	*/
	public static void setSelectedParentTabs(Component component)
	{
		while(component!=null)	//while we haven't went up the whole hierarchy
		{
			final Component parent=component.getParent();	//get this component's parent
			if(parent instanceof JTabbedPane)	//if this parent is a tabbed pane
			{
				((JTabbedPane)parent).setSelectedComponent(component);	//select this component tab in the tabbed pane
			}
			component=parent;	//move up the hierarchy
		}
	}

}

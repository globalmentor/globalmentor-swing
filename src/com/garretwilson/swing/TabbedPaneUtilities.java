package com.garretwilson.swing;

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

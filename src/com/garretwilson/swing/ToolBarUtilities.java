package com.garretwilson.swing;

import javax.swing.JToolBar;

/**Various convenience methods for working with Swing toolbars.
@author Garret Wilson
 */
public class ToolBarUtilities
{

	/**Creates a default toolbar with rollover buttons.
	Using this method promotes consistency across components.
	@see JToolBar#setRollover(boolean)
	*/
	public static JToolBar createDefaultToolBar()
	{
		final JToolBar toolBar=new JToolBar();  //create the toolbar
		toolBar.setRollover(true);	//default to a rollover toolbar
		return toolBar; //return the toolbar we created
	}

	/**Creates a toolbar separator of default size.
	The default size is determined by the current look and feel.
	*/
/*G***del if not needed
	public JToolBar.Separator createSeparator()
	{
		return createSeparator(null);	//create a separator of default size
	}
*/

	/**Creates a toolbar separator of a specified size.
	@param size The size of the separator, or <code>null</code> if the default
		size determined by the current look and feel should be used.
	*/
/*G***del if not needed
	public void addSeparator(final Dimension size )
	{
		final JToolBar.Separator separator=new JToolBar.Separator(size);	//create a new separator
		switch()
		if (getOrientation() == VERTICAL) {
		s.setOrientation(JSeparator.HORIZONTAL);
} else {
		s.setOrientation(JSeparator.VERTICAL);
}
			add(s);
	}
*/
	
}

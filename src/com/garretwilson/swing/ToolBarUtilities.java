package com.garretwilson.swing;

import java.awt.*;
import java.util.*;
import javax.swing.*;

/**Various convenience methods for working with Swing toolbars.
@author Garret Wilson
 */
public class ToolBarUtilities
{

	/**Creates a default application toolbar with rollover buttons and no text.
	Using this method promotes consistency across components.
	@see JToolBar#setRollover(boolean)
	*/
/*G***del when not needed
	public static BasicToolBar createApplicationToolBar()
	{
		final BasicToolBar toolBar=new BasicToolBar();  //create the toolbar
		toolBar.setRollover(true);	//default to a rollover toolbar
		toolBar.setButtonTextVisible(false);	//don't show text by default
		return toolBar; //return the toolbar we created
	}
*/

	/**Creates a default toolbar from the tool actions of the given action manager.
	@param actionManager The manager that contains the tool actions
	@see #setupToolBar
	*/
/*G***del when not needed
	public static BasicToolBar createApplicationToolBar(final ActionManager actionManager)
	{
		final BasicToolBar toolBar=createApplicationToolBar();	//create a default toolbar
		setupToolBar(toolBar, actionManager);	//set up the toolbar with the action manager tool actions
		return toolBar;	//return the toolbar we created and initialized
	}
*/

	/**Creates a toolbar separator of default size, with an orientation
		appropriate for the given toolbar.
	The default size is determined by the current look and feel.
	@param toolBar The toolbar for which a separator should be created.
	@param orientation The orientation of the separator, either
		<code>JSeparator.HORIZONTAL</code> or <code>JSeparator.VERTICAL</code>.
		The separator should usually be the opposite orientation of the
		toolbar within which it will be placed.
	@return The created toolbar separator.  
	*/
	public static JToolBar.Separator createToolBarSeparator(final JToolBar toolBar)
	{
		return createToolBarSeparator(toolBar, null);	//create a separator of the default size
	}

	/**Creates a toolbar separator of a specified size, with an orientation
		appropriate for the given toolbar.
	@param toolBar The toolbar for which a separator should be created.
	@param size The size of the separator, or <code>null</code> if the default
		size determined by the current look and feel should be used.
	*/
	public static JToolBar.Separator createToolBarSeparator(final JToolBar toolBar, final Dimension size)
	{
		final int orientation;	//decide which orientation to use, depending on the toolbar's orientation
		switch(toolBar.getOrientation())	//see which orientation the toolbar has
		{
			case JToolBar.HORIZONTAL:	//if we have a horizontal toolbar
				orientation=JSeparator.VERTICAL;	//create a vertical separator
				break;
			case JToolBar.VERTICAL:	//if we have a vertical toolbar
				orientation=JSeparator.HORIZONTAL;	//create a horizontal separator
				break;
			default:	//if we don't recotnize the orientation of the toolbar
				throw new IllegalArgumentException("Unrecognized toolbar orientation: "+toolBar.getOrientation());
		}
		return createToolBarSeparator(orientation, size);	//create the toolbar separator with the orientation we decided on
	}

	/**Creates a toolbar separator of default size.
	The default size is determined by the current look and feel.
	@param orientation The orientation of the separator, either
		<code>JSeparator.HORIZONTAL</code> or <code>JSeparator.VERTICAL</code>.
		The separator should usually be the opposite orientation of the
		toolbar within which it will be placed.
	@return The created toolbar separator.  
	*/
	public static JToolBar.Separator createToolBarSeparator(final int orientation)
	{
		return createToolBarSeparator(orientation, null);	//create a separator of default size
	}

	/**Creates a toolbar separator of a specified size.
	@param orientation The orientation of the separator, either
		<code>JSeparator.HORIZONTAL</code> or <code>JSeparator.VERTICAL</code>.
		The separator should usually be the opposite orientation of the
		toolbar within which it will be placed.  
	@param size The size of the separator, or <code>null</code> if the default
		size determined by the current look and feel should be used.
	*/
	public static JToolBar.Separator createToolBarSeparator(final int orientation, final Dimension size)
	{
		final JToolBar.Separator separator=new JToolBar.Separator(size);	//create a new separator
		separator.setOrientation(orientation);	//set the orientation of the separator
		return separator;	//return the separator we created
	}
}

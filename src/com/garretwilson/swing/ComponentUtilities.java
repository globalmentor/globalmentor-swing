package com.garretwilson.swing;

import java.awt.*;
import javax.swing.*;
import com.garretwilson.util.Debug;

/**Convenience methods to be used for Swing components.
Some of these methods apply to AWT components as well, but use the Swing utilities
to acess the AWT event queue and are therefore included in the
<code>com.garretwilson.swing</code> package.
@see SwingUtilities
@author Garret Wilson
*/
public class ComponentUtilities
{

	/**This class cannot be publicly instantiated.*/
	private ComponentUtilities(){}

	/**Sets the cursor for a component immediately, returning the original cursor.
	@param component The component whose cursor should be set.
	@param cursor The new cursor the component should use.
	@return The cursor currently used by the component.
	@see Component#getCursor
	@see Component#setCursor
	*/
	public static Cursor setCursor(final Component component, final Cursor cursor)
	{
		final Cursor originalCursor=component.getCursor();  //see which cursor is currently being used
		component.setCursor(cursor);	//set the new cursor
		return originalCursor;  //return the original cursor used
	}

	/**Sets the cursor using the AWT event queue, after all AWT events have been
		processed. Immediately returns the original cursor.
		This is useful for setting the cursor back to a particular value after invoking
		an operation in the AWT event queue and ensuring the cursor gets set back
		to its value after that operation finishes.
	@param component The component whose cursor should be set.
	@param cursor The new cursor the component should use.
	@return The cursor currently used by the component.
	@see Component#getCursor
	@see Component#setCursor
	@see SwingUtilities#invokeLater
	*/
	public static Cursor setCursorLater(final Component component, final Cursor cursor)
	{
		final Cursor originalCursor=component.getCursor();  //see which cursor is currently being used
		SwingUtilities.invokeLater(new Runnable()	//don't update the cursor until later
		{
			public void run()
			{
Debug.trace("Ready to set cursor: ", cursor.getName());  //G***del
				component.setCursor(cursor);	//set the new cursor
			}
		});
		return originalCursor;  //return the original cursor used
	}

}
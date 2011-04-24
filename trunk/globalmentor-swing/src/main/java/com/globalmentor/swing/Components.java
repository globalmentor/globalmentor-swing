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

/**Convenience methods to be used for Swing components.
Some of these methods apply to AWT components as well, but use the Swing utilities
to acess the AWT event queue and are therefore included in this package.
@see SwingUtilities
@author Garret Wilson
*/
public class Components
{

	/**The enabled property.*/
	public final static String ENABLED_PROPERTY="enabled";

	/**This class cannot be publicly instantiated.*/
	private Components(){}

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

	/**Sets a predefined cursor for a component immediately, returning the original cursor.
	@param component The component whose cursor should be set.
	@param type The type of predefined cursor the component should use.
	@return The cursor currently used by the component.
	@see Component#getCursor
	@see Component#setCursor
	@see #setCursor(Component, Cursor)
	*/
	public static Cursor setPredefinedCursor(final Component component, final int type)
	{
		return setCursor(component, Cursor.getPredefinedCursor(type));	//get the predefined cursor and set it
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
				component.setCursor(cursor);	//set the new cursor
			}
		});
		return originalCursor;  //return the original cursor used
	}

	/**Scrolls the given rectangle to the origin of the parent.
	This is most useful if used with a component embedded in a <code>JViewport</code>.
	@param component The component the rectangle represents.
	@param rectangle The visible <code>Rectangle</code>.
	@see JViewport
	*/
	public static void scrollRectToOrigin(final JComponent component, final Rectangle rectangle)	//TODO maybe search up the hierarchy for the viewport
	{
		final int originalWidth=rectangle.width;	//get the original rectangle width
		final int originalHeight=rectangle.height;	//get the original rectangle height
		try
		{
			final Container parent=component.getParent();	//get the parent
			if(parent!=null)	//if there is a parent
			{
				rectangle.width=parent.getWidth();	//change the width of the rectangle to the width of the parent
				rectangle.height=parent.getHeight();	//change the height of the rectangle to the height of the parent
			}
			component.scrollRectToVisible(rectangle);	//make the rectangle visible
		}
		finally
		{
			rectangle.width=originalWidth;	//reset the rectangle width
			rectangle.height=originalHeight;	//reset the rectangle height
		}
	}

}
package com.garretwilson.swing.draw;

import java.awt.Graphics;
import java.awt.Point;

/**An object that can be drawn onto a graphics context.
@author Garret Wilson
*/
public interface Drawable
{

	/**Retrieves the location of the object.
	@return The starting point of the drawing shape.
	*/
	public Point getLocation();

	/**Sets the location of the object.
	@param newLocation The new location of the object.
	*/
	public void setLocation(final Point newLocation);

	/**Sets the location of the object.
	@param x The new horizontal location.
	@param y The new vertical location.
	*/
	public void setLocation(final int x, final int y);

	/**Draws the object using the provided graphics.
	@param graphics The graphics with which to draw the object.
	*/
	public void draw(final Graphics graphics);
}
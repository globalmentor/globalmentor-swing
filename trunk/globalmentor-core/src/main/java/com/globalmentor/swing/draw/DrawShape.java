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

package com.globalmentor.swing.draw;

import java.awt.*;

/**An abstract shape that can be dynamically drawn on the screen.
	<p>Inspired by <code>JFCBook.Chapter4.DrawnShape</code> from <cite>Core Java
	Foundation Classes</cite> by Kim Topley, ISBN 0-13-080301-4.</p>
@author Garret Wilson
*/
public abstract class DrawShape implements Drawable
{

	/**The color of the shape; defaults to <code>Color.black</code>.*/
	private Color color=Color.black;

		/**@return The color of the shape; defaults to <code>Color.black</code>.*/
		public Color getColor() {return color;}

		/**Sets the color of the shape.
		@param newColor The color in which the shape should be drawn.
		*/
		public void setColor(final Color newColor) {color=newColor;}

	/**The starting point of the drawing shape; defaults to (0, 0).*/
	private final Point location=new Point(0, 0);

		/**@return The starting point of the drawing shape; defaults to (0, 0).*/
		public Point getLocation() {return location;}

		/**Sets the starting point of the drawing shape.
		@param newLocation The new location to start.
		*/
		public void setLocation(final Point newLocation) {setLocation(newLocation.x, newLocation.y);}

		/**Sets the starting point of the drawing shape.
		@param x The new horizontal location to start.
		@param y The new vertical location to start.
		*/
		public void setLocation(final int x, final int y)
		{
			location.x=x; //set the x coordinate
			location.y=y; //set the y coordinate
		}

	/**The ending point of the drawing shape; defaults to (0, 0).*/
	private final Point endLocation=new Point(0, 0);

		/**@return The ending point of the drawing shape; defaults to (0, 0).*/
		public Point getEndLocation() {return endLocation;}

		/**Sets the starting point of the drawing shape.
		@param newEndLocation The new location to end.
		*/
		public void setEndLocation(final Point newEndLocation) {setEndLocation(newEndLocation.x, newEndLocation.y);}

		/**Sets the ending point of the drawing shape.
		@param x The new horizontal location to end.
		@param y The new vertical location to end.
		*/
		public void setEndLocation(final int x, final int y)
		{
			endLocation.x=x; //set the x coordinate
			endLocation.y=y; //set the y coordinate
		}

	/**Whether the shape has been accepted; defaults to <code>true</code>.*/
	private boolean permanent=true;

		/**@return Whether the shape has been accepted; defaults to <code>true</code>.*/
		public boolean isPermanent() {return permanent;}

		/**Sets whether the shape has been accepted.
		@param newPermanent <code>true</code> if the shape and location have been
			accepted, else <code>false</code> if the object is still being modified.
		*/
		public void setPermanent(final boolean newPermanent) {permanent=newPermanent;}

	/**@return The rectangular width of the drawing area, from the starting
		location to the ending location.
	*/
	protected int getWidth()
	{
			//return the absolute width
		return endLocation.x>location.x ? endLocation.x-location.x : location.x-endLocation.x;
	}

	/**@return The rectangular height of the drawing area, from the starting
		location to the ending location.
	*/
	protected int getHeight()
	{
			//return the absolute height
		return endLocation.y>location.y ? endLocation.y-location.y : location.y-endLocation.y;
	}

	/**@return The rectangular bounds of the shape.*/
	public Rectangle getBounds()
	{
		return new Rectangle(Math.min(location.x, endLocation.x), Math.min(location.y, endLocation.y), getWidth(), getHeight());
	}

	/**@return A shape object describing the drawn shape.*/
	public abstract Shape getShape();

	/**Sets the location and dimensions of the drawn shape.
	@param shape The object specifying the new location and shape of the drawn
		shape; must be appropriate for the particular instance of shape being drawn.
	@exception IllegalArgumentException Thrown if the shape is inappropriate for
		the shape being drawn.
	*/
	public abstract void setShape(final Shape shape) throws IllegalArgumentException;

	/**Constructs a shape with a given color and location.
	@param color The color in which the shape should be drawn.
	@param x The horizontal starting location.
	@param y The vertical starting location.
	*/
	public DrawShape(final Color color, final int x, final int y)
	{
		setColor(color);  //set the color
		setLocation(x, y);  //set the starting location
		setEndLocation(x, y);  //set the ending location to be identical to the starting location
	}

	/**Constructs a shape with a given color.
	@param color The color in which the shape should be drawn.
	*/
	public DrawShape(final Color color)
	{
		setColor(color);  //set the color
	}

	/**Draws the object using the provided graphics.
	@param graphics The graphics with which to draw the object.
	*/
	public abstract void draw(final Graphics graphics);

}
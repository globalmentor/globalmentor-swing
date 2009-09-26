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

package com.garretwilson.swing.draw;

import java.awt.*;

/**A rectangle that can be dynamically drawn on the screen.
@author Garret Wilson
*/
public class DrawRectangle extends DrawShape
{

	/**@return A <code>Rectangle</code> describing the drawn shape.
	@see Rectangle
	*/
	public Shape getShape()
	{
		return getBounds(); //return the rectangular bounds of the shape
	}

	/**Sets the location and dimensions of the drawn shape.
	@param shape The object specifying the new location and shape of the drawn
		shape.
	@exception IllegalArgumentException Thrown if the shape is not appropriate
		for a draw rectangle.
	*/
	public void setShape(final Shape shape) throws IllegalArgumentException
	{
		final Rectangle rectangle=shape.getBounds(); //get the bounds of the shape
		setLocation(rectangle.x, rectangle.y);  //set the location
		setEndLocation(rectangle.x+rectangle.width, rectangle.y+rectangle.height);  //set the end location to correspond to the size of the rectangle passed
	}

	/**Constructs a draw rectangle with a given color and location.
	@param color The color in which the shape should be drawn.
	@param x The horizontal starting location.
	@param y The vertical starting location.
	*/
	public DrawRectangle(final Color color, final int x, final int y)
	{
		super(color, x, y); //constrct the parent class
	}

	/**Constructs a draw rectangle with a given color.
	@param color The color in which the shape should be drawn.
	*/
	public DrawRectangle(final Color color)
	{
		super(color); //construct the parent class
	}

	/**Constructs a draw rectangle with a given color and shape.
	@param color The color in which the shape should be drawn.
	@param shape The shape of the draw rectangle.
	@exception IllegalArgumentException Thrown if the shape is not appropriate
		for a draw rectangle.
	*/
	public DrawRectangle(final Color color, final Shape shape)
	{
		super(color); //construct the parent class
		setShape(shape);  //set the shape to the rectangle
	}

	/**Draws the rectangle using the provided graphics.
	@param graphics The graphics with which to draw the object.
	*/
	public void draw(final Graphics graphics)
	{
	  graphics.setColor(getColor());  //set the graphics to the correct color
		if(isPermanent()) //if the shape and location has been accepted
		{
		  graphics.setXORMode(Color.white); //TODO testing; comment
//TODO del if not needed			graphics.setPaintMode();  //paint normally
			graphics.fillRect(
					Math.min(getLocation().x, getEndLocation().x),
					Math.min(getLocation().y, getEndLocation().y),
					getWidth(), getHeight()); //draw a filled rectangle
		}
		else  //if the shape is still being modified
		{
//TODO del Log.trace("not permanent"); //TODO del
		  graphics.setXORMode(Color.white); //TODO testing; comment
			graphics.drawRect(
					Math.min(getLocation().x, getEndLocation().x),
					Math.min(getLocation().y, getEndLocation().y),
					getWidth(), getHeight()); //draw a rectangle outline
		}

	}

}
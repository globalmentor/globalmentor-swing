package com.garretwilson.swing.draw;

import java.awt.*;
import java.awt.geom.RectangularShape;

import com.globalmentor.util.Debug;

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
/*G***del
Debug.trace("ready to draw rectangle from point: ", getLocation()); //G***del
Debug.trace("ready to draw rectangle to point: ", getEndLocation()); //G***del
Debug.trace("width: ", getWidth()); //G***del
Debug.trace("height: ", getHeight()); //G***del
*/
	  graphics.setColor(getColor());  //set the graphics to the correct color
		if(isPermanent()) //if the shape and location has been accepted
		{
		  graphics.setXORMode(Color.white); //G***testing; comment
//G***del if not needed			graphics.setPaintMode();  //paint normally
			graphics.fillRect(
					Math.min(getLocation().x, getEndLocation().x),
					Math.min(getLocation().y, getEndLocation().y),
					getWidth(), getHeight()); //draw a filled rectangle
		}
		else  //if the shape is still being modified
		{
//G***del Debug.trace("not permanent"); //G***del
		  graphics.setXORMode(Color.white); //G***testing; comment
			graphics.drawRect(
					Math.min(getLocation().x, getEndLocation().x),
					Math.min(getLocation().y, getEndLocation().y),
					getWidth(), getHeight()); //draw a rectangle outline
		}

	}

}
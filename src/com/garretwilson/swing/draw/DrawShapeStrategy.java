package com.garretwilson.swing.draw;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import com.garretwilson.util.Debug;
import com.globalmentor.java.Objects;

/**Strategy for drawing a shape on a component.
	An instance of the strategy must be registered as both a
	<code>MouseListener</code> and a <code>MouseMotionListener</code> with the
	component on which the shape will be drawn.
@see Drawable
@author Garret Wilson
*/
public class DrawShapeStrategy extends MouseInputAdapter
{

	/**The component in which the object will be drawn.*/
	protected final Component component;

	/**The object being drawn, or <code>null</code> if no shape is being drawn.*/
	private DrawShape drawShape;

		/**@return The object being drawn, or <code>null</code> if no shape is being
		  drawn.*/
		public DrawShape getDrawShape() {return drawShape;}

		/**Sets the object being drawn.
		@param newDrawShape The new shape to draw, or <code>null</code> if no shape
			is to be drawn.
		*/
		public void setDrawShape(final DrawShape newDrawShape)
		{
			if(!Objects.equals(drawShape, newDrawShape))  //if the drawing shape is actuallychanging
			{
				if(drawShape!=null) //if we already had a shape
				  draw(drawShape);  //undraw the shape
				drawShape=newDrawShape; //actually change the drawing shape
				if(drawShape!=null) //if we now have a shape
					draw(drawShape);  //draw the new shape
			}
		}

	/**Constructs a strategy with a drawable object.
	@param newComponent The component in which the object will be drawn.
	@param newDrawShape The object to draw.
	*/
	public DrawShapeStrategy(final Component newComponent, final DrawShape newDrawShape)
	{
		component=newComponent; //save the component in which the component should be drawn G***should we automatically register ourselves with the component?
		drawShape=newDrawShape; //save the object being drawn
	}

	/**Constructs a strategy with no drawable object.
	@param newComponent The component in which the object will be drawn.
	*/
	public DrawShapeStrategy(final Component newComponent)
	{
		this(newComponent, null); //do the default construction without a shape to draw
	}

	/**Called when the mouse button is pressed in the associated component.
	@param mouseEvent The mouse event.
	*/
	public void mousePressed(final MouseEvent mouseEvent)
	{
//G***del Debug.trace("mouse pressed");  //G***del
		if(SwingUtilities.isLeftMouseButton(mouseEvent)) //if the left button is pressed
		{
			final DrawShape drawShape=getDrawShape(); //get the shape being drawn
			if(drawShape!=null) //if we have a shape
			{
				draw(drawShape);  //undraw the shape
				drawShape.setLocation(mouseEvent.getX(), mouseEvent.getY()); //set the location of the shape being drawn
				drawShape.setEndLocation(mouseEvent.getX(), mouseEvent.getY()); //set the ending of the shape being drawn to be the same as the beginning location
				drawShape.setPermanent(false); //show that, as the shape is being constructed, it is not yet permanent
				draw(drawShape);  //draw the shape
			}
		}
	}

	/**Called when the mouse button is released in the associated component.
	@param mouseEvent The mouse event.
	*/
	public void mouseReleased(final MouseEvent mouseEvent)
	{
		if(SwingUtilities.isLeftMouseButton(mouseEvent)) //if the left button was released
		{
			final DrawShape drawShape=getDrawShape(); //get the shape being drawn
			if(drawShape!=null) //if we have a shape
			{
				draw(drawShape);  //undraw the shape
				drawShape.setPermanent(true); //show that the shape is now permanent
				draw(drawShape);  //draw the shape
			}
		}
	}

	/**Called when the mouse is dragged in the associated component.
	@param mouseEvent The mouse event.
	*/
	public void mouseDragged(final MouseEvent mouseEvent)
	{
		if(SwingUtilities.isLeftMouseButton(mouseEvent)) //if the left button was released
		{
			final DrawShape drawShape=getDrawShape(); //get the shape being drawn
			if(drawShape!=null) //if we have a shape being drawn
			{
	//G***del Debug.trace("trying to move shape");  //G***del
				draw(drawShape);  //undraw the shape
				drawShape.setEndLocation(mouseEvent.getX(), mouseEvent.getY()); //update the ending position
				draw(drawShape);  //redraw the shape
			}
		}
	}

	/**Paints the shapes managed by this object.
	@param graphics The graphics context used for painting.
	*/
	public void paint(final Graphics graphics)
	{
		final DrawShape drawShape=getDrawShape(); //get the shape being drawn
		if(drawShape!=null) //if we have a shape
		{
		  draw(graphics, drawShape);  //draw the shape using the graphics context
		}
	}

	/**Draws a drawable object on the component.
	@param drawable The object to draw.
	*/
	protected void draw(final Drawable drawable) //G***maybe move this to a DrawComponent or to some DrawUtilities
	{
		final Graphics graphics=component.getGraphics();  //get the graphics context
		if(graphics!=null)  //if graphics are available
		{
		  try
			{
				draw(graphics, drawable); //draw the object using the graphics context we just acquired
			}
			finally
			{
				graphics.dispose(); //always dispose of the graphics context we acquired
			}
		}
	}

	/**Draws a drawable object on the component.
	@param graphics The graphics context to use to draw the object.
	@param drawable The object to draw.
	*/
	protected static void draw(final Graphics graphics, final Drawable drawable) //G***maybe move this to a DrawComponent or to some DrawUtilities
	{
		drawable.draw(graphics);  //tell the object to draw itself
	}

}
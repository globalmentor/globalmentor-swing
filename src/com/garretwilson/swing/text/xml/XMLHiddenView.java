package com.garretwilson.swing.text.xml;

import java.awt.*;
import javax.swing.text.*;

/**View class of an invisible element.
@author Garret Wilson
*/
public class XMLHiddenView extends View //G***should this be put in com.garretwilson.swing.text?
{

	/**Constructor which specifies an element.
	@param element The element this view represents.
	*/
	public XMLHiddenView(Element element)
	{
		super(element);	//construct the parent class
	}

	/**Performs no rendering for the hidden view.
	@param graphics The rendering surface to use.
	@param allocation The allocated region to render into.
	@see View#paint
	*/
	public void paint(Graphics graphics, Shape allocation) {}

	/**Determines the preferred span for this view. Returns 0 as the view is not
		visible.
	@param axis The axis (<code>View.X_AXIS</code> or <code>View.Y_AXIS<code>).
	@return The span the view would like to be rendered into.
	@see View#getPreferredSpan
	*/
	public float getPreferredSpan(int axis)
	{
		return 0;  //return 0 because the view isn't visible
	}

	/**Determines the minimum span for this view along an axis. Returns 0 as the
		view is not visible.
	@param axis The axis (<code>View.X_AXIS</code> or <code>View.Y_AXIS<code>).
	@return The minimum span the view can be rendered into.
	@see View#getMinimumSpan
	*/
	public float getMinimumSpan(int axis)
	{
		return 0;  //return 0 because the view isn't visible
	}

	/**Determines the maximum span for this view along an axis. Returns 0 as the
		view is not visible.
	@param axis The axis (<code>View.X_AXIS</code> or <code>View.Y_AXIS<code>).
	@return The maximum span the view can be rendered into.
	@see View#getMaximumSpan
	*/
	public float getMaximumSpan(int axis)
	{
		return 0;  //return 0 because the view isn't visible
	}

	/**Provides a mapping from the view coordinate space to the logical coordinate
		space of the model.
	@param x The X coordinate (>= 0).
	@param y The Y coordinate (>= 0).
	@param allocation The allocated region to render into.
	@return The location within the model that best represents the given point in
		the view
	@see View#viewToModel
	*/
	public int viewToModel(final float x, final float y, final Shape allocation, final Position.Bias[] bias)
	{
		final Rectangle rectangle=allocation instanceof Rectangle ? (Rectangle)allocation : allocation.getBounds();  //get the allocation rectangle
		if(x<rectangle.x+rectangle.width/2) //if the position is on the left side of this view
		{
		  bias[0]=Position.Bias.Forward;  //set the bias forward
		  return getStartOffset();  //return the start offset
		}
		else  //if the position is to the right side of the object
		{
		  bias[0]=Position.Bias.Backward; //set the bias backward
		  return getEndOffset();  //return the ending offset
		}
	}

	/**Provides a mapping from the coordinate space of the model to that of the
		view.
	@param pos The position to convert (>=0).
	@param allocation The allocated region to render into.
	@return The bounding box of the given position.
	@exception BadLocationException Thrown if the given position does not
		represent a valid location governed by the view in the associated document.
	@see View#modelToView
	*/
	public Shape modelToView(final int pos, final Shape allocation, final Position.Bias b) throws BadLocationException
	{
		final int p0=getStartOffset();  //get the starting offset of the view
		final int p1=getEndOffset();  //get the ending offset of the view
		if((pos>=p0) && (pos<=p1))  //if the given position is valid
		{
			final Rectangle rectangle=(allocation instanceof Rectangle) ? (Rectangle)allocation : allocation.getBounds();  //get the bounding rectangle of the painting area
	    if(pos==p1) //if the position given is the last position we govern
			{
				rectangle.x+=rectangle.width; //move the rectangle to the right
	    }
	    rectangle.width=0;  //set the width of the rectangle to zero
	    return rectangle; //return the rectangle
	  }
		else  //if the position isn't valid
		  throw new BadLocationException(pos+" not in range "+p0+","+p1, pos);  //report that we don't recognize the position
	}

}

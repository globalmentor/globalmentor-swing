package com.garretwilson.swing.text.xml;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import com.garretwilson.swing.text.ViewHidable;
import com.globalmentor.util.Debug;

/**Abstract base class to represent a resizable object, such as a component,
	image, or applet. The object is automatically resized to be proportional to
	the requested width.
	Implements <code>ViewHidable</code> so that it can be notified if the view
	is being hidden so that it can hide the object.
	A class should be derived from this abstract class that sets the appropriate
	width and height of the object.
@see #setWidth
@see #setWidth
@author Garret Wilson
*/
public abstract class XMLObjectView extends View implements ViewHidable //G***should we change this to Hidable and make ViewComponentManager implement it as well?
{

	/**The minimum amount of size to allow for changing; any change lower than
		this will result in no size change.
	*/
	protected final static int MINIMUM_SIZE_CHANGE=2;

	/**The current bounds of the object.*/
	private Rectangle bounds=new Rectangle();

		/**@return The painting bounds of the object, only valid if painting has
		  occurred. The size of this rectangle may not match the size of the object.
		@see #paint
		*/
		public Rectangle getBounds() {return bounds;}

	/**The standard, maximum height of the object.*/
	private int height;

		/**@return The standard, maximum height of the object.*/
		public int getHeight() {return height;}

		/**Sets the standard, maximum height of the object and resets the current
			height.
		@param newHeight The new standard height of the object.
		@see #setCurrentHeight
		*/
		public void setHeight(final int newHeight)
		{
			height=newHeight;
			setCurrentHeight(newHeight);  //set the current height to match the standard height
		}

	/**The standard, maximum width of the object.*/
	private int width;

		/**@return The standard, maximum width of the object.*/
		public int getWidth() {return width;}

		/**Sets the standard, maximum width of the object and resets the current
		  width.
		@param newWidth The new standard width of the object.
		@see #setCurrentWidth
		*/
		public void setWidth(final int newWidth)
		{
			width=newWidth;
			setCurrentWidth(newWidth); //set the current width to match the standard width
		}

	/**The current, resized height of the object.*/
	private int currentHeight;

		/**@return The current, resized height of the object.*/
		public int getCurrentHeight() {return currentHeight;}

		/**Sets the current, resized height of the object.
		@param newCurrentHeight The new current height of the object.
		*/
		public void setCurrentHeight(final int newCurrentHeight) {currentHeight=newCurrentHeight;}

	/**The current, resized width of the object.*/
	private int currentWidth;

		/**@return The current, resized width of the object.*/
		public int getCurrentWidth() {return currentWidth;}

		/**Sets the current, resized width of the object.
		@param newCurrentWidth The new current width of the object.
		*/
		public void setCurrentWidth(final int newCurrentWidth) {currentWidth=newCurrentWidth;}

		  //G***testing
	/**The height of the object requested by the parent view.*/
	private int requestedHeight;

		/**@return The height of the object requested by the parent view.*/
		public int getRequestedHeight() {return requestedHeight;}

		/**Sets the height of the object requested by the parent view.
		@param newRequestedHeight The new requested height of the object.
		*/
		public void setRequestedHeight(final int newRequestedHeight) {requestedHeight=newRequestedHeight;}

	/**The width of the object requested by the parent view.*/
	private int requestedWidth;

		/**@return The width of the object requested by the parent view.*/
		public int getRequestedWidth() {return requestedWidth;}

		/**Sets the height of the object requested by the parent view.
		@param newRequestedWidth The new requested width of the object.
		*/
		public void setRequestedWidth(final int newRequestedWidth) {requestedWidth=newRequestedWidth;}

	/**Whether the object is showing; this defaults to <code>false</code>.*/
	private boolean showing=false;

		/**Returns whether the object is showing. This is not necessarily the same
			as visible, because the associated object could be set as visible, yet
			not be showing because it is displayed in a paged view, for example.
		@return Whether the object is showing.
		*/
		public boolean isShowing() {return showing;}

		/**Sets whether or not an view is showing. This method is called to show
			the view when needed if <code>isShowing()</code> returns <code>false</code>,
			and is called when the view is being hidden by a parent that hides views,
			such as a paged view.
			If this view is overridden, this version should be called to correctly
			update the variable for <code>isShowing()</code> to function correctly.
		@param newShowing <code>true</code> if the view is beginning to be shown,
			<code>false</code> if the view is beginning to be hidden.
		@see #isShowing
		*/
		public void setShowing(final boolean newShowing) {showing=newShowing;}

	/**Creates a new view that represents an object.
	@param element The element for which to create the view.
	*/
  public XMLObjectView(final Element element)
	{
   	super(element);	//do the default constructing
	}

	/**Paints the object. This version does not actually do any painting. Instead,
		the method shows the object if needed by calling <code>setShowing()</code>.
	@param graphics The rendering surface to use.
	@param allocation The allocated region to render into.
	@see View#paint
	@see #setShowing
	*/
	public void paint(Graphics graphics, Shape allocation)
	{
		//get the bounding rectangle of the painting area and update our bounds variable
		getBounds().setBounds((allocation instanceof Rectangle) ? (Rectangle)allocation : allocation.getBounds());
		if(!isShowing())  //if the object isn't currently showing
			setShowing(true); //show the object
	}

	/**Determines the preferred span for this view along an axis. This returns the
		currently calculated spans.
	@param axis The axis, either <code>X_AXIS</code> or <code>Y_AXIS</code>.
	@returns  The span the view would like to be rendered into.
		Typically the view is told to render into the span that is returned,
		although there is no guarantee. The parent may choose to resize or break
		the view, although object views cannot normally be broken.
	@exception IllegalArgumentException Thrown if the axis is not recognized.
	@see #getCurrentHeight
	@see #getCurrentWidth
	*/
	public float getPreferredSpan(int axis)
	{
		switch(axis)  //see which axis is being requested
		{
		  case View.X_AXIS: //if the x-axis is requested
			  return getCurrentWidth(); //return the currently calculated width
		  case View.Y_AXIS: //if the y-axis is requested
		    return getCurrentHeight(); //return the currently calculated height
			default:  //if we don't recognize the axis
				throw new IllegalArgumentException("Invalid axis: "+axis);  //report that we don't recognize the axis
		}
	}

	/**Determines the minimum span for this view along an axis. Since object views
		are by default resizable, this method returns zero. This zero is required
		because <code>setSize()</code> expects this value and will refuse to be set
		to this value, preventing objects from being scaled down to nothing.
	@param axis The axis, either <code>X_AXIS</code> or <code>Y_AXIS</code>.
	@returns Zero as the minimum span the view can be rendered into.
	@exception IllegalArgumentException Thrown if the axis is not recognized.
	@see View#getPreferredSpan
	@see #setSize
	*/
	public float getMinimumSpan(int axis)
	{
		return 0;  //report that the object can be scaled away to nothing, although setSize() will not actually allow this to occur
	}

	/**Determines the maximum span for this view along an axis. This currently
		returns the permanent size of the object, initialized by
		<code>setWidth()</code> and <code>setHeight()</code>.
	@param axis The axis, either <code>X_AXIS</code> or <code>Y_AXIS</code>.
	@returns The maximum span the view can be rendered into.
	@exception IllegalArgumentException Thrown if the axis is not recognized.
	@see View#getPreferredSpan
	@see #getHeight
	@see #getWidth
	*/
	public float getMaximumSpan(int axis)
	{
		switch(axis)  //see which axis we're looking at
		{
		  case View.X_AXIS: //if the x-axis is requested
				return getWidth();  //return the width of the object as the maximum width
		  case View.Y_AXIS: //if the y-axis is requested
				return getHeight();  //return the height of the object as the maximum height
			default:  //if we don't recognize the axis
				throw new IllegalArgumentException("Invalid axis: "+axis);  //report that we don't recognize the axis
		}
	}


    /**
     * Determines the desired alignment for this view along an
     * axis.  This is implemented to give the alignment to the
     * bottom of the icon along the y axis, and the default
     * along the x axis.
     *
     * @param axis may be either X_AXIS or Y_AXIS
     * @returns the desired alignment.  This should be a value
     *   between 0.0 and 1.0 where 0 indicates alignment at the
     *   origin and 1.0 indicates alignment to the full span
     *   away from the origin.  An alignment of 0.5 would be the
     *   center of the view.
     */
/*G***fix
    public float getAlignment(int axis) {
	switch (axis) {
	case View.Y_AXIS:
	    return 1.0f;
	default:
	    return super.getAlignment(axis);
	}
    }
*/

    /**
     * Provides a mapping from the document model coordinate space
     * to the coordinate space of the view mapped to it.
     *
     * @param pos the position to convert
     * @param a the allocated region to render into
     * @return the bounding box of the given position
     * @exception BadLocationException  if the given position does not represent a
     *   valid location in the associated document
     * @see View#modelToView
     */
/*G***fix
    public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
	int p0 = getStartOffset();
	int p1 = getEndOffset();
	if ((pos >= p0) && (pos <= p1)) {
	    Rectangle r = a.getBounds();
	    if (pos == p1) {
		r.x += r.width;
	    }
	    r.width = 0;
	    return r;
	}
	return null;
    }
*/

	/**Provides a mapping from the coordinate space of the model to that of the
		view.
	@param pos The position to convert (>=0).
	@param allocation The allocated region to render into.
	@return The bounding box of the given position.
	@exception BadLocationException Thrown if the given position does not
		represent a valid location governed by the view in the associated document.
	@see View#modelToView
	*/
	public Shape modelToView(int pos, Shape allocation, Position.Bias b) throws BadLocationException
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


    /**
     * Provides a mapping from the view coordinate space to the logical
     * coordinate space of the model.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param a the allocated region to render into
     * @return the location within the model that best represents the
     *  given point of view
     * @see View#viewToModel
     */
/*G***fix
    public int viewToModel(float x, float y, Shape a, Position.Bias[] bias) {
	Rectangle alloc = (Rectangle) a;
	if (x < alloc.x + alloc.width) {
	    bias[0] = Position.Bias.Forward;
	    return getStartOffset();
	}
	bias[0] = Position.Bias.Backward;
	return getEndOffset();
    }
*/

	/**Provides a mapping from the view coordinate space to the logical coordinate
		space of the model.
	@param x The X coordinate (>= 0).
	@param y The Y coordinate (>= 0).
	@param allocation The allocated region to render into.
	@return The location within the model that best represents the given point in
		the view
	@see View#viewToModel
	*/
	public int viewToModel(float x, float y, Shape allocation, Position.Bias[] bias)
	{
		final Rectangle rectangle=allocation instanceof Rectangle ? (Rectangle)allocation : allocation.getBounds();  //get the allocation rectangle
//G***del; this code would only accept mouse clicks on the left side of the object, for example: 		if(x<rectangle.x+(rectangle.width/2)) //if the position is on the left side of the object G***do we want width/2, or just width?
		if(x<rectangle.x+rectangle.width) //if the position is on the actual object
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


    /**
     * Set the size of the view. (Ignored.)
     *
     * @param width the width
     * @param height the height
     */
/*G***fix
    public void setSize(float width, float height) {
			//G***fix comments
    	// Ignore this -- image size is determined by the tag attrs and
    	// the image itself, not the surrounding layout!
    }
*/

	/**Sets the size of the object, while keeping the object in the same proportions.
	@param width The width (>=0).
	@param height The height (>=0).
	*/
	public void setSize(float width, float height)
	{
/*G***del
		setRequestedWidth(width); //G***testing
		setRequestedHeight(height); //G***testing
*/
//G***the problem here seems to be that a table wants to fill the entire page, and tries to set the
//G***size of the iamge to fill the table; the image tries to stay in proportion, tells the table
//G***of that fact, and the table tries to resize again, starting the whole process over

//G***del Debug.trace("XMLObjectView.setSize(), width: "+width+" height: "+height);
		if(width!=0 && height!=0) //if valid sizes are passed (this might be set to zero from our getPreferredSize(), and checking that here keeps the object from being scaled away to nothing)
		{
		  final int oldCurrentWidth=getCurrentWidth();  //get the current current width
		  final int oldCurrentHeight=getCurrentHeight();  //get the current current height
		  //Sometimes, when an object is in a table, the table will keep readjusting
			//  the size forever if we keep readjusting the size to match. Therefore,
			//  we'll only change the size if the size is being changed significantly.
			//  This is not the best way to do this; we should instead modify the
			//  resizing logic in the table. This is a short-term fix. G***
//G***del Debug.trace("old currents, width: "+oldCurrentWidth+" height: "+oldCurrentHeight);
//G***del Debug.trace("absolute width difference: "+Math.abs(width-oldCurrentWidth)+" absolute heigh difference: "+Math.abs(height-oldCurrentHeight));
//G***fix		  if(Math.abs(width-oldCurrentWidth)>MINIMUM_SIZE_CHANGE || Math.abs(height-oldCurrentHeight)>MINIMUM_SIZE_CHANGE)
		  {
	//G***del Debug.trace("old currents, width: "+currentWidth+" height: "+currentHeight);
				final int newCurrentWidth=(int)width; //set the current width to the new value
				int newCurrentHeight;  //we'll put the new current height here
				if(newCurrentWidth==getWidth()) //if the width is the same as our standard width
					newCurrentHeight=getHeight(); //the way to ensure proportionality is to set the height to what was originally set as the standard height, since that's what the width is set to
				else  //if the object is being scaled
				{
//G***fix					if(newCurrentWidth!=oldCurrentWidth)  //G***testing
					{

	//G***del Debug.trace("Object is being scaled.");
					final float ratio=(float)getWidth()/(float)getHeight(); //find the ratio of the object G***optimize perhaps keep this updated automatically when the size is set
	//G***del Debug.trace("making proportional with ratio: "+ratio);
					newCurrentHeight=(int)((float)newCurrentWidth/ratio); //set the height to be proportional to the width
					}
//G***fix					else
					{
//G***fix						newCurrentHeight=(int)height; //G***testing
					}
				}

//G***del newCurrentHeight=(int)height; //G***testing; del


				setCurrentWidth(newCurrentWidth); //set the new current width
				setCurrentHeight(newCurrentHeight); //set the new current height
	//G***del Debug.trace("old currents, width: "+oldCurrentWidth+" height: "+oldCurrentHeight);
	//G***del Debug.trace("new currents, width: "+newCurrentWidth+" height: "+newCurrentHeight);
				if(newCurrentWidth!=oldCurrentWidth || newCurrentHeight!=oldCurrentHeight)  //if the size has changed
				{
//G***del 	Debug.trace("old currents, width: "+oldCurrentWidth+" height: "+oldCurrentHeight);
//G***del 	Debug.trace("new currents, width: "+newCurrentWidth+" height: "+newCurrentHeight);
//G***del 	Debug.trace("Notifying parent that preferences have changed.");
					final View parent=getParent();  //get the parent view
					if(parent!=null)  //if there is a parent view
						parent.preferenceChanged(this, newCurrentWidth!=oldCurrentWidth, newCurrentHeight!=oldCurrentHeight); //report to the parent view that this view's preferences have changed
				}
		  }
		}
	}

}
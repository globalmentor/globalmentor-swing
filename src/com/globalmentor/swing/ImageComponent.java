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

/**Component that displays an image.
	The image can be zoomed using a bound property
	Allows scrolling within a {@link JScrollPane}.
@author Garret Wilson
@see JScrollPane
*/
public class ImageComponent extends JComponent implements Scrollable
{

	/**The image to be displayed.*/
	private Image image;

		/**@return The image to be displayed.*/
		public Image getImage() {return image;}

		/**Sets the image to be displayed.
		@param newImage The new image to be displayed.
		*/
		public void setImage(final Image newImage)
		{
			image=newImage; //update the image
	    updateImageSize(image.getWidth(this), image.getHeight(this)); //update the sizes, including the zoom size
			repaint();  //repaint the component
		}

	/**The property representing the zoom factor, the value of which is a <code>Double</code>.*/
	public final static String ZOOM_FACTOR_PROPERTY="zoomFactor";

	/**The amount to resize the image, with <code>1</code> equal to no resizing.*/
	private double zoomFactor=1.0f;

		/**@return The amount to resize the image, with <code>1</code> equal to no resizing.*/
		public double getZoomFactor() {return zoomFactor;}

		/**Sets the amount to resize the image.
		This is a bound property, <code>ZOOM_PROPERTY</code>.
		@param newZoomFactor The zoom multiplier, with with <code>1</code> equal to
			no resizing.
		*/
		public void setZoomFactor(final double newZoomFactor)
		{
			final double oldZoomFactor=getZoomFactor(); //get the current zoom factor
			if(oldZoomFactor!=newZoomFactor)  //if the zoom factor is actually changing
			{
			  zoomFactor=newZoomFactor; //save the zoom factor
				zoomSize=null;  //show that we no longer know the zoom size
				  //show that the property has changed
				firePropertyChange(ZOOM_FACTOR_PROPERTY, new Double(oldZoomFactor), new Double(newZoomFactor));
			}
			final Image image=getImage(); //get the image we're associated with
			if(image!=null)  //if we have an image
			{
		    updateImageSize(image.getWidth(this), image.getHeight(this)); //update the sizes, including the zoom size
			}
		}

	/**The continuously updated image size, or <code>null</code> if the size is
		not known.*/
	private Dimension imageSize=null;

		/**@return The actual size of the image, or <code>null</code> if the size is
			not known.
		*/
		protected Dimension getImageSize() {return imageSize;}

	/**The continuously updated zoom size of the image, or <code>null</code> if
		the zoom size is not known.
	*/
	private Dimension zoomSize=null;

		/**@return The zoom size of the image, or <code>null</code> if the zoom size
		  is not known.
		*/
		protected Dimension getZoomSize() {return zoomSize;}

		/**Sets the zoom size of the image, updating the preferred size and
			propogating the changes up the container chain.
		@param newZoomSize The new zoomed image size, or <code>null</code> if the
			zoom size is not known.
		*/
		protected void setZoomSize(final Dimension newZoomSize)
		{
			if(newZoomSize==null || !newZoomSize.equals(getZoomSize())) //if the zoom size is really changing (or if the new size is null)
			{
				if(newZoomSize!=null) //if we know a new zoom size
				{
				  setPreferredSize(newZoomSize);  //update our preferred size to the zoom size
				  revalidate(); //send our size change up the chain to any JScrollPane, for instance
				}
				zoomSize=newZoomSize; //save the updated zoom size
				repaint();  //repaint the component
			}
		}

	/**Default constructor.*/
	public ImageComponent()
	{
		image=null; //set the image to null
	}

	/**Image constructor.
	@param image The new image to be displayed.
	*/
	public ImageComponent(final Image image)
	{
		setImage(image);  //set the image
	}

	/**Updates the size of the image (actual and zoom) based upon the given
		actual image dimensions. The
	@param width The actual width of the image.
	@param height The actual height of the image.
	*/
	protected void updateImageSize(final int width, final int height)
	{
		if(width>=0 && height>=0) //if the width and the height are both valid
		{
			if(imageSize==null  //if we haven't yet updated the image size
					|| imageSize.getWidth()!=width  //or if the image has a different width
					|| imageSize.getHeight()!=height)  //or if the image has a different height
			{
				imageSize=new Dimension(width, height); //update the image size
				zoomSize=null;  //show that we need to update the zoom size
			}
			if(zoomSize==null)  //if we need to update the zoom size of the image
			{
				final double zoomFactor=getZoomFactor(); //get the zoom factor
				final int zoomWidth=(int)(width*zoomFactor); //calculate the zoom width TODO should we eventually support partial dimensions?
				final int zoomHeight=(int)(height*zoomFactor); //calculate the zoom height TODO should we eventually support partial dimensions?
				setZoomSize(new Dimension(zoomWidth, zoomHeight));  //update the zoom size
			}
		}
		else  //if the image dimensions given aren't valid
		{
			imageSize=null; //show that we don't yet know the image size
			zoomSize=null;  //show that we don't yet know the zoom size
		}
	}

	/**Modifies the zoom factor down if needed so that the image is completely
		contained within the bounds of its container. The image is made as large
		as possible without enlargement. If the image is already contained within
		its container at normal size, or the image component has no parent
		container, no action occurs.
//TODO fix		The zoom factor is not changed immediately, but is changed later in the AWT
		thread so that any container sizes may first be correctly setup.
	@see #setZoomfactor
	*/
	public void fitImage()
	{
		final Dimension imageSize=getImageSize(); //get the size of the image
		if(imageSize!=null && imageSize.width>0 && imageSize.height>0) //if we know the size of the image, and it's non-zero
		{
			final Container container=getParent();  //get the parent container
			if(container!=null) //if we are in a container
			{
				final Dimension containerSize=container.getSize();  //get the size of the container
				if(containerSize!=null && containerSize.width>0 && containerSize.height>0)  //if there is a non-zero container size
				{
					double zoomFactor=1.0;  //start out assuming we don't need to zoom the image
					int zoomWidth=imageSize.width;  //start out assuming the zoomed width will be same as the image width
					int zoomHeight=imageSize.height;  //start out assuming the zoomed height will be same as the image height
//TODO del Log.trace("original zoom width: ", zoomWidth);  //TODO del
//TODO del Log.trace("original zoom height: ", zoomHeight);  //TODO del
//TODO del Log.trace("container width: ", containerSize.width);  //TODO del
//TODO del Log.trace("container height: ", containerSize.height);  //TODO del
					if(zoomWidth>containerSize.width)  //if the image is wider than the container
					{
						zoomFactor=(double)containerSize.width/imageSize.width; //find the percentage of the original image width we should use for reduction
						zoomWidth=(int)(imageSize.width*zoomFactor);  //reduce the image width to fit within the container
						zoomHeight=(int)(imageSize.height*zoomFactor); //reduce the image height to match the original image proportions
					}
//TODO del Log.trace("1 new zoom width: ", zoomWidth);  //TODO del
//TODO del Log.trace("1 new zoom height: ", zoomHeight);  //TODO del
//TODO del Log.trace("1 new zoom factor: "+zoomFactor);  //TODO del
					if(zoomHeight>containerSize.height)  //if the image is higher than the container
					{
						zoomFactor=(double)containerSize.height/imageSize.height; //find the percentage of the original image heiht we should use for reduction
						zoomWidth=(int)(imageSize.width*zoomFactor);  //reduce the image width to match the original image proportions
						zoomHeight=(int)(imageSize.height*zoomFactor); //reduce the image height to fit within the container
					}
//TODO del Log.trace("2 new zoom width: ", zoomWidth);  //TODO del
//TODO del Log.trace("2 new zoom height: ", zoomHeight);  //TODO del
//TODO del Log.trace("2 new zoom factor: "+zoomFactor);  //TODO del
					setZoomFactor(zoomFactor);  //update the zoom factor we finally settled on
				}
			}
		}
	}


	//TODO instead of overriding paint, we should override paintComponent()
	//TODO see http://java.sun.com/docs/books/tutorial/uiswing/painting/overview.html#repaint

	/**Paints the image.
	@param graphics The graphics context used for painting.
	*/
	public void paintComponent(final Graphics graphics)
	{
		super.paintComponent(graphics);  //do the default painting
//TODO del Log.trace("painting image");  //TODO del
		final Image image=getImage(); //get the image to paint
		final int width=image.getWidth(this); //get the width of the image
//TODO del Log.trace("image width: ", width);  //TODO del
		final int height=image.getHeight(this);  //get the height of the image
//TODO del Log.trace("image height: ", height);  //TODO del
		final Dimension zoomSize=getZoomSize(); //get the zoomed size of the image
		if(zoomSize!=null) //if the image size is valid
		  graphics.drawImage(image, 0, 0, zoomSize.width, zoomSize.height, this);  //paint the image
	}

	/**Called when information about an image which was previously requested
		using an asynchronous interface becomes available.
		This version updates the preferred scrollable viewport size before
		delegating to the dfeault parent class functionality.
	@param image The image being observed.
	@param infoFlags The bitwise inclusive OR of the following flags:
		<code>WIDTH</code>, <code>HEIGHT</code>, <code>PROPERTIES</code>,
		<code>SOMEBITS</code>, <code>FRAMEBITS</code>, <code>ALLBITS</code>,
		<code>ERROR</code>, <code>ABORT</code>.
	@param x The <i>x</i> coordinate.
	@param y The <i>y</i> coordinate.
	@param width The width.
	@param height The height.
	@return <code>false</code> if the infoflags indicate that the image is
		completely loaded; <code>true</code> otherwise.
	*/
	public boolean imageUpdate(final Image image, final int infoFlags, final int x, final int y, final int width, final int height)
	{
//TODO del Log.trace("updating image, width: ", width);
//TODO del Log.trace("updating image, height: ", height);
//TODO del		boolean shouldFit=false;  //TODO testing
		updateImageSize(width, height); //update the size of the image based upon our most recent knowledge
//TODO del		if((infoFlags&ALLBITS)==0) //TODO testing
//TODO fix		if(width>0 && height>0) //TODO testing
		if((infoFlags&ALLBITS)!=0) //TODO testing
		{
//TODO del			if(getImageSize()==null && width>0 && height>0))
		  fitImage(); //TODO testing

		}

/*TODO del
		if(width>=0 && height>=0) //if the width and the height are both valid now
		{
			if(preferredScrollableViewportSize==null  //if we haven't yet updated the preferred scrolling dimensions
				  || preferredScrollableViewportSize.getWidth()!=width  //or it has a different width
				  || preferredScrollableViewportSize.getHeight()!=height)  //or it has a different height
			{
//TODO del Log.trace("updating preferred scrollable viewport size"); //TODO del
				preferredScrollableViewportSize=new Dimension(width, height); //update the preferred scroll size with the size of the image
				setPreferredSize(preferredScrollableViewportSize);  //TODO testing
//TODO del				invalidate(); //TODO fix
				revalidate(); //TODO testing
			}
		}
*/
		return super.imageUpdate(image, infoFlags, x, y, width, height); //perform the default functionality
	}

	/**Draws a drawable object on the component.
	@param drawable The object to draw.
	*/
/*TODO del
	public void draw(final Drawable drawable) //TODO maybe move this to a DrawComponent or to some DrawUtilities
	{
		if(drawable!=null)  //if we have a valid drawable object
		{
		  final Graphics graphics=getGraphics();  //get the graphics context
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
*/

	/**Draws a drawable object on the component.
	@param graphics The graphics context to use to draw the object.
	@param drawable The object to draw.
	*/
/*TODO del
	public void draw(final Graphics graphics, final Drawable drawable) //TODO maybe move this to a DrawComponent or to some DrawUtilities
	{
		drawable.draw(graphics);  //tell the object to draw itself
	}
*/

	/**If the image has been loaded, returns the size of the image. If the image
		has not been loaded, delagates to the parent version.
	@return The preferred dimensions of the image component.
	*/
/*TODO fix
	public Dimension getPreferredSize()
	{
		final Dimension imageSize=getImageSize(); //get the size of the image
		return imageSize!=null ? imageSize : super.getPreferredSize();  //return the image size if we know it; otherwise, let the parent make a guess
	}
*/

	/**@return This component's preferred size for a scroll pane, which is the
		full size of the scaled image.
	@see JViewport#getPreferredSize
	*/
	public Dimension getPreferredScrollableViewportSize()
	{
//TODO del Log.trace("getting preferred scrollable viewport size"); //TODO del
				//TODO fix to take into account border size, etc.
		//return the preferred scroll size, if we've calculated it; if not, use this component's preferred size
		return getImageSize()!=null ? getImageSize() : super.getPreferredSize();
	}

	/**
     * Components that display logical rows or columns should compute
     * the scroll increment that will completely expose one new row
     * or column, depending on the value of orientation.  Ideally,
     * components should handle a partially exposed row or column by
     * returning the distance required to completely expose the item.
     * <p>
     * Scrolling containers, like JScrollPane, will use this method
     * each time the user requests a unit scroll.
     *
     * @param visibleRect The view area visible within the viewport
     * @param orientation Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
     * @param direction Less than zero to scroll up/left, greater than zero for down/right.
     * @return The "unit" increment for scrolling in the specified direction
     * @see JScrollBar#setUnitIncrement
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
		{
			return 10;  //TODO fix
		}


    /**
     * Components that display logical rows or columns should compute
     * the scroll increment that will completely expose one block
     * of rows or columns, depending on the value of orientation.
     * <p>
     * Scrolling containers, like JScrollPane, will use this method
     * each time the user requests a block scroll.
     *
     * @param visibleRect The view area visible within the viewport
     * @param orientation Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
     * @param direction Less than zero to scroll up/left, greater than zero for down/right.
     * @return The "block" increment for scrolling in the specified direction.
     * @see JScrollBar#setBlockIncrement
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
		{
			return 100; //TODO fix
		}


    /**
     * Return true if a viewport should always force the width of this
     * Scrollable to match the width of the viewport.  For example a noraml
     * text view that supported line wrapping would return true here, since it
     * would be undesirable for wrapped lines to disappear beyond the right
     * edge of the viewport.  Note that returning true for a Scrollable
     * whose ancestor is a JScrollPane effectively disables horizontal
     * scrolling.
     * <p>
     * Scrolling containers, like JViewport, will use this method each
     * time they are validated.
     *
     * @return True if a viewport should force the Scrollables width to match its own.
     */
    public boolean getScrollableTracksViewportWidth()
		{
			return false;  //TODO testing; fix
		}

    /**
     * Return true if a viewport should always force the height of this
     * Scrollable to match the height of the viewport.  For example a
     * columnar text view that flowed text in left to right columns
     * could effectively disable vertical scrolling by returning
     * true here.
     * <p>
     * Scrolling containers, like JViewport, will use this method each
     * time they are validated.
     *
     * @return True if a viewport should force the Scrollables height to match its own.
     */
    public boolean getScrollableTracksViewportHeight()
		{
			return false;  //TODO testing; fix

		}


}
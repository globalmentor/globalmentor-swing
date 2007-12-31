package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.garretwilson.awt.event.RolloverMouseAdapter;
import com.garretwilson.lang.Objects;
import com.garretwilson.net.BrowserLauncher;
import com.garretwilson.util.Debug;

/**A label that serves as a link to an Internet destination.
@author Garret Wilson
*/
public class LinkLabel extends JLabel
{

	/**The mouse adapter responsible for handling rollovers for the label.*/
	protected final RolloverMouseAdapter rolloverMouseAdapter;

	/**The property representing the rollover color.*/
	public final static String ROLLOVER_COLOR_PROPERTY="rolloverColor";

		/**@return The rollover color; defaults to <code>Color.red</code>.*/
		public Color getRolloverColor() {return rolloverMouseAdapter.getRolloverColor();}

		/**Sets the rollover color property.
		  This is a bound property, <code>ROLLOVER_COLOR_PROPERTY</code>.
		@param newRolloverColor The new rollover color, or <code>null</code> if the
			component color should not be changed for mouse rollovers.
		*/
		public void setRolloverColor(final Color newRolloverColor)
		{
			final Color oldRolloverColor=getRolloverColor();  //get the current rollover color
		  if(!Objects.equals(oldRolloverColor, newRolloverColor)) //if the color is really changing
			{
				rolloverMouseAdapter.setRolloverColor(newRolloverColor);  //update the value
				  //show that the property has changed
				firePropertyChange(ROLLOVER_COLOR_PROPERTY, oldRolloverColor, newRolloverColor);
			}
		}

	/**The property representing the link target URL.*/
	public final static String TARGET_PROPERTY="target";

	/**The link target URL; defaults to <code>null</code>.*/
	private String target=null;

		/**@return The link target; defaults to <code>null</code>.*/
		public String getTarget() {return target;}

		/**Sets the link target property.
		  This is a bound property, <code>TARGET_PROPERTY</code>.
		@param newTarget The new link target, or <code>null</code> if the
			link has no destination.
		*/
		public void setTarget(final String newTarget)
		{
			final String oldTarget=target;  //get the current target
		  if(!Objects.equals(oldTarget, newTarget)) //if the target is really changing
			{
				target=newTarget; //update the value
				firePropertyChange(TARGET_PROPERTY, oldTarget, newTarget);  //show that the property has changed
			}
		}

	/**Creates a link label instance with the specified text, image, and
		horizontal alignment.
		The label is centered vertically in its display area.
		The text is on the trailing edge of the image.
	@param text The text to be displayed by the label.
	@param icon The image to be displayed by the label.
	@param horizontalAlignment  One of the following constants
		defined in <code>SwingConstants</code>:
		  <code>LEFT</code>, <code>CENTER</code>, <code>RIGHT</code>,
			<code>LEADING</code> or <code>TRAILING</code>.
	*/
	public LinkLabel(final String text, final Icon icon, final int horizontalAlignment)
	{
		super(text, icon, horizontalAlignment); //construct the parent class
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));  //use the hand cursor when the mouse is over the label
		//create a rollover adapter that not only handles mouse rollovers but also
		//  handles mouse clicks
		rolloverMouseAdapter=new RolloverMouseAdapter()
		{
		  /**Invoked when the mouse has been clicked on a component.
		  @param mouseEvent The mouse event.
		  */
		  public void mouseClicked(final MouseEvent mouseEvent)
			{
				try
				{
					BrowserLauncher.openURL(getTarget());	//attempt to browse to the location designated by the target
				}
				catch(final Exception exception)
				{
					Debug.warn(exception);  //only warn about any errors that occur
				}
			}
		};
		  //add the rollover adapter as a listener;
			//  it will now automatically handle rollovers and mouse clicks
    addMouseListener(rolloverMouseAdapter);
	}

	/**Creates a link label instance with the specified
		text and horizontal alignment.
	  The label is centered vertically in its display area.
	@param text The text to be displayed by the label.
	@param horizontalAlignment  One of the following constants
		defined in <code>SwingConstants</code>:
		  <code>LEFT</code>, <code>CENTER</code>, <code>RIGHT</code>,
			<code>LEADING</code> or <code>TRAILING</code>.
	*/
	public LinkLabel(final String text, final int horizontalAlignment)
	{
		this(text, null, horizontalAlignment);  //do the default construction
	}

	/**Creates a link label instance with the specified text.
		The label is aligned against the leading edge of its display area,
		and centered vertically.
	@param text The text to be displayed by the label.
	*/
	public LinkLabel(final String text)
	{
		this(text, null, LEADING);  //do the default construction
	}

	/**Creates a link label instance with the specified
		image and horizontal alignment.
		The label is centered vertically in its display area.
	@param icon The image to be displayed by the label.
	@param horizontalAlignment  One of the following constants
		defined in <code>SwingConstants</code>:
		  <code>LEFT</code>, <code>CENTER</code>, <code>RIGHT</code>,
			<code>LEADING</code> or <code>TRAILING</code>.
	*/
	public LinkLabel(final Icon icon, final int horizontalAlignment)
	{
		this(null, icon, horizontalAlignment); //do the default construction
	}

	/**Creates a link label instance with the specified image.
	@param icon The image to be displayed by the label.
	The label is centered vertically and horizontally in its display area.
	*/
	public LinkLabel(final Icon icon)
	{
		this(null, icon, CENTER); //do the default construction
	}

	/**Default constructor. Creates a link label instance with
		no image and with an empty string for the title.
		The label is centered vertically in its display area.
		The label's contents, once set, will be displayed on the leading edge
		of the label's display area.
	*/
	public LinkLabel()
	{
		this("", null, LEADING);  //do the default construction
	}

}
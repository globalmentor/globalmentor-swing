package com.garretwilson.swing.text;

import java.awt.*;
import javax.swing.text.*;

/**A view that is not visible.
@author Garret Wilson
*/
public class InvisibleView extends ZeroSpanView
{

	/**Constructor which specifies an element.
	@param element The element this view represents.
	*/
	public InvisibleView(Element element)
	{
		super(element);	//construct the parent class
	}

	/**Returns a boolean value that indicates whether the view is visible or not.
	<p>Hidden views always return <code>false</code>.</p> 
	@return <code>false</code>, indicating that the view is not visible.
	*/
	public boolean isVisible()
	{
		return false;
	}

	/**Provides a way to determine the next visually represented model 
		location at which one might place a caret.
	<p>As this view is always hidden, it always returns -1.</p>
	@param pos The position to convert (>=0).
	@param a The allocated region in which to render.
	@param direction The direction from the current position that can
		be thought of as the arrow keys typically found on a keyboard.
		This will be one of the following values:
		<ul>
			<li>SwingConstants.WEST</li>
			<li>SwingConstants.EAST</li>
			<li>SwingConstants.NORTH</li>
			<li>SwingConstants.SOUTH</li>
	</ul>
	@return The location within the model that best represents the next
		location visual position. This version always returns -1.
	@exception BadLocationException
	@exception IllegalArgumentException if <code>direction</code>
		doesn't have one of the legal values above
	*/
	public int getNextVisualPositionFrom(int pos, Position.Bias b, Shape a, int direction, Position.Bias[] biasRet) throws BadLocationException
	{
		return -1;	//show that no visual positions are valid in a hidden view
	}

}

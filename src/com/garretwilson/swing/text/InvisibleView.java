package com.garretwilson.swing.text;

import java.awt.*;
import javax.swing.text.*;

import com.garretwilson.swing.text.xml.css.XMLCSSStyleUtilities;
import com.globalmentor.text.xml.stylesheets.css.XMLCSS;

/**A view that is not visible and is breakable only as a last resort.
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

	/**Determines how attractive a break opportunity in this view is.
	An invisible view is designed to be breakable, but only as a last resort;
	this implementation therefore returns <code>BadBreakWeight+1</code>.
	This prevents invisible views from coming between and breaking visible views
	that should not be separated (the first has page-break-after: avoid, for example).
	@param axis The breaking axis, either View.X_AXIS or View.Y_AXIS.
	@param pos The potential location of the start of the broken view (>=0).
	@param len Specifies the relative length from <var>pos</var> where a potential break is desired (>=0).
	@return The weight, which should be a value between <code>View.ForcedBreakWeight</code> and <code>View.BadBreakWeight.</code>
	*/
	public int getBreakWeight(int axis, float pos, float len)
	{
		return BadBreakWeight+1;	//an invisible view is the worst possible break there could be G***is it really *that* bad? maybe it should be GoodBreakWeight-1
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

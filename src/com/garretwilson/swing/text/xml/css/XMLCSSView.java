package com.garretwilson.swing.text.xml.css;

import java.awt.Color;

/**Represents a view which contains properties for and can be painted as CSS
	elements.
@author Garret Wilson
*/
//G***probably remove this class, now that XMLCSSViewPainter uses attributes instead of this interface
public interface XMLCSSView
{

	/**Gets the background color of the view.
	@return The background color of the view.
	*/
	public Color getBackgroundColor();

}
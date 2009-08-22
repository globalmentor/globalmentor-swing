package com.garretwilson.swing.text.xml;

import javax.swing.text.*;

/**Represents a CSS1-like list item.
@author Garret Wilson
*/
//G***probably delete this entire class
public class XMLListItemView extends XMLBlockView
{

	/**Constructs a fragment view for the paragraph.
	@param element The element this view is responsible for.
	@param axis The tiling axis, either View.X_AXIS or View.Y_AXIS.
	*/
	public XMLListItemView(Element element, int axis)
	{
		super(element, axis); //do the default construction
//G***del Log.trace("Creating a list view object.");
//G***del		setInsets((short)50, (short)50, (short)50, (short)50);	//G***fix; testing
//G***del		setInsets((short)0, (short)25, (short)0, (short)0);	//G***fix; testing
	}

}
package com.garretwilson.swing.text.xml;

import javax.swing.text.*;

/**Represents a CSS-like list, either ordered or unordered.
@author Garret Wilson
*/
public class XMLListView extends XMLBlockView
{

	/**Whether this list is ordered.*/
	private final boolean ordered;

		/**@return Whether this list is ordered.*/
		public boolean isOrdered() {return ordered;}

	/**Constructs a fragment view for the paragraph.
	@param element The element this view is responsible for.
	@param axis The tiling axis, either View.X_AXIS or View.Y_AXIS.
	@param ordered Whether the list is ordered.
	*/
	public XMLListView(Element element, int axis, final boolean ordered)
	{
		super(element, axis); //do the default construction
		this.ordered=ordered;	//record whether this list is ordered
	}

}
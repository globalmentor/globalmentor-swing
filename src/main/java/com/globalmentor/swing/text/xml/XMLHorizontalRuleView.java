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

package com.globalmentor.swing.text.xml;

import java.awt.*;

import javax.swing.text.*;

import com.globalmentor.log.Log;

/**
 * Represents a horizontal rule. This class was written referencing <code>javax.swing.text.html.HRuleView</code> by Timothy Prinzing and Sara Swanson version
 * 1.27 02/02/00, and the original version was based on code from that class.
 * @author Garret Wilson
 */
public class XMLHorizontalRuleView extends View //TODO later move to XHTML package
{

	/** The space above a horizontal rule. */
	private static final int SPACE_ABOVE = 3;

	/** The space below a horizontal rule. */
	private static final int SPACE_BELOW = 3;

	/**
	 * Constructs an horizontal rule view.
	 * @param element The element for which a view should be created.
	 */
	public XMLHorizontalRuleView(final Element element) {
		super(element); //construct the parent
		Log.trace("created horizontal rule view");
		//TODO del	setPropertiesFromAttributes();
	}

	/**
	 * Update any cached values that come from attributes.
	 */
	/*TODO fix
	    protected void setPropertiesFromAttributes() {
		StyleSheet sheet = ((HTMLDocument)getDocument()).getStyleSheet();
		AttributeSet eAttr = getElement().getAttributes();
		attr = sheet.getViewAttributes(this);

		alignment = StyleConstants.ALIGN_LEFT;
		size = 0;
		noshade = null;
		widthValue = null;
		// Get the width/height
		Enumeration attributes = attr.getAttributeNames();
		while (attributes.hasMoreElements()) {
		    Object key = attributes.nextElement();
		}
		if (attr != null) {
	            alignment = StyleConstants.getAlignment(attr);
		    noshade = (String)eAttr.getAttribute("noshade");
		    Object value = attr.getAttribute("size");
		    if (value != null && (value instanceof String))
			size = Integer.parseInt((String)value);
		    value = attr.getAttribute(CSS.Attribute.WIDTH);
		    if (value != null && (value instanceof CSS.LengthValue)) {
			widthValue = (CSS.LengthValue)value;
		    }
		    topMargin = getLength(CSS.Attribute.MARGIN_TOP, attr);
		    bottomMargin = getLength(CSS.Attribute.MARGIN_BOTTOM, attr);
		    leftMargin = getLength(CSS.Attribute.MARGIN_LEFT, attr);
		    rightMargin = getLength(CSS.Attribute.MARGIN_RIGHT, attr);
		}
		else {
		    topMargin = bottomMargin = leftMargin = rightMargin = 0;
		}
		if (bevel == null) {
		    bevel = BorderFactory.createLoweredBevelBorder();
		}
	    }
	*/

	// This will be removed and centralized at some point, need to unify this
	// and avoid private classes.
	/*TODO fix
	    private float getLength(CSS.Attribute key, AttributeSet a) {
		CSS.LengthValue lv = (CSS.LengthValue) a.getAttribute(key);
		float len = (lv != null) ? lv.getValue() : 0;
		return len;
	    }
	*/

	/**
	 * Paints the view.
	 * @param graphics The graphics context.
	 * @param allocation The allocated region for the view
	 * @see View#paint
	 */
	public void paint(final Graphics graphics, Shape allocation) {
		final Rectangle rectangle = (allocation instanceof Rectangle) ? (Rectangle)allocation : allocation.getBounds(); //get the bounding rectangle of the painting area
		/*TODO fix
				rectangle.x=getLeftInset(); //TODO testing
				rectangle.width-=getLeftInset()+getRightInset(); //TODO testing
		*/
		//TODO del Debug.notify("Rectangle width: "+rectangle.width);  //TODO del
		final int y = rectangle.y + SPACE_ABOVE; //compensate for space above
		final int height = rectangle.height - (SPACE_ABOVE + SPACE_BELOW); //subtract the space above and below from the height
		graphics.fillRect(rectangle.x, y, rectangle.width, height); //fill the horizontal rule
	}

	/**
	 * Calculates the desired size of the horizontal rule.
	 * @param axis The axis (<code>X_AXIS</code> or <code>Y_AXIS</code>).
	 * @return The desired span.
	 * @see View#getPreferredSpan
	 */
	public float getPreferredSpan(final int axis) {
		//TODO fix	Insets i = bevel.getBorderInsets(getContainer());
		switch(axis) {
			case View.X_AXIS:
				//TODO del				return Float.MAX_VALUE; //TODO testing
				return 0; //TODO testing; see why a max value doesn't work; actually, this may be correct -- the Swing HTML code only adds suggested margins to this
				//TODO fix				return i.left + i.right;
			case View.Y_AXIS:
				return 2 + SPACE_ABOVE + SPACE_BELOW; //TODO don't hard-code this in
			default:
				throw new IllegalArgumentException("Invalid axis: " + axis);
		}
	}

	/**
	 * Determines the minimum span for this view along an axis. Since horizontal rule views are by default resizable, this method returns zero. This zero is
	 * required because <code>setSize()</code> expects this value and will refuse to be set to this value, preventing objects from being scaled down to nothing.
	 * @param axis The axis, either <code>X_AXIS</code> or <code>Y_AXIS</code>.
	 * @return Zero as the minimum span the view can be rendered into.
	 * @throws IllegalArgumentException Thrown if the axis is not recognized.
	 * @see View#getPreferredSpan
	 * @see #setSize
	 */
	/*TODO del; doesn't work
		public float getMinimumSpan(int axis)
		{
			return 0;  //report that the object can be scaled away to nothing, although setSize() will not actually allow this to occur
		}
	*/

	/**
	 * Gets the resize weight for the axis. The horizontal rule is rigid vertically and flexible horizontally. TODO maybe give something similar to the image
	 * view; in particular, try no resize weight vertially; try this for tables as well
	 * @param axis The axis (<code>X_AXIS</code> or <code>Y_AXIS</code>).
	 * @return The resize weight for the axis.
	 */
	public int getResizeWeight(final int axis) {
		switch(axis) {
			case View.X_AXIS:
				return 1; //TODO comment
			case View.Y_AXIS:
				return 0; //TODO comment
			default:
				return 0; //TODO comment
		}
	}

	/**
	 * Determines how attractive a break opportunity in this view is. This is implemented to request a forced break.
	 * @param axis The axis (<code>X_AXIS</code> or <code>Y_AXIS</code>).
	 * @param pos The potential location of the start of the broken view (&gt;=0). This may be useful for calculating tab positions.
	 * @param len Specifies the relative length from <em>pos</em> where a potential break is desired (&gt;=0).
	 * @return The break weight, a value between <code>ForcedBreakWeight</code> and <code>BadBreakWeight</code>, inclusive.
	 */
	public int getBreakWeight(final int axis, final float pos, final float len) {
		if(axis == X_AXIS) { //try to break along the X axis
			return ForcedBreakWeight;
		}
		return BadBreakWeight; //don't break anywhere else
	}

	/**
	 * Provides a mapping from the document model coordinate space to the coordinate space of the view mapped to it.
	 * @param pos The position to convert.
	 * @param allocation the allocated region to render into.
	 * @return The bounding box of the given position, or <code>null</code> if the position is outside the model range the view represents.
	 * @throws BadLocationException Thrown if the given position does not represent a valid location in the associated document.
	 * @see View#modelToView
	 */
	public Shape modelToView(final int pos, final Shape allocation, final Position.Bias b) throws BadLocationException {
		final int startOffset = getStartOffset(); //get the element starting offset
		final int endOffset = getEndOffset(); //get the element ending offset
		if((pos >= startOffset) && (pos <= endOffset)) { //if the position is within our range
			final Rectangle rectangle = (allocation instanceof Rectangle) ? (Rectangle)allocation : allocation.getBounds(); //get the bounding rectangle of the area
			if(pos == endOffset) { //if the position is at the end of our range
				rectangle.x += rectangle.width; //move to the right side of the view
			}
			rectangle.width = 0; //set the rectangle to zero width
			return rectangle; //return the rectangle
		}
		return null; //if the position is outside our range, return null
	}

	/**
	 * Provides a mapping from the view coordinate space to the logical coordinate space of the model.
	 * @param x The X coordinate.
	 * @param y The Y coordinate.
	 * @param allocation The allocated region to render into.
	 * @return The location within the model that best represents the given point of view
	 * @see View#viewToModel
	 */
	public int viewToModel(final float x, final float y, final Shape allocation, final Position.Bias[] bias) {
		final Rectangle rectangle = (allocation instanceof Rectangle) ? (Rectangle)allocation : allocation.getBounds(); //get the bounding rectangle of the area
		if(x < rectangle.x + (rectangle.width / 2)) //if the point falls in the left half
		{
			bias[0] = Position.Bias.Forward;
			return getStartOffset(); //get the starting offste
		}
		bias[0] = Position.Bias.Backward; //if the point falls in the right half, return the ending offset
		return getEndOffset(); //return the ending offset
	}

	/**
	 * Fetches the attributes to use when rendering. This is implemented to multiplex the attributes specified in the model with a StyleSheet.
	 */
	/*TODO fix
	    public AttributeSet getAttributes() {
		return attr;
	    }
	*/

	/*TODO fix
	    public void changedUpdate(DocumentEvent changes, Shape a, ViewFactory f) {
		super.changedUpdate(changes, a, f);
		int pos = changes.getOffset();
		if (pos <= getStartOffset() && (pos + changes.getLength()) >=
		    getEndOffset()) {
		    setPropertiesFromAttributes();
		}
	    }
	*/

	// --- variables ------------------------------------------------
	/*TODO fix
	    private Border bevel;
	    private float topMargin;
	    private float bottomMargin;
	    private float leftMargin;
	    private float rightMargin;
	    private int alignment = StyleConstants.ALIGN_LEFT;
	    private String noshade = null;
	    private int size = 0;
	    private CSS.LengthValue widthValue;
	*/

	/** View Attributes. */
	//TODO fix    private AttributeSet attr;
}

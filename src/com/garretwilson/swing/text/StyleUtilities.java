package com.garretwilson.swing.text;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import static com.garretwilson.swing.text.StyleConstants.*;

/**A collection of utilities for working with Swing styles in attribute sets.
@author Garret Wilson
*/
public class StyleUtilities
{

	/**Returns an attribute in the attribute set only if it is defined; the attribute
		is not resolved by searching the parent attribute set hierarchy.
	@param attributeSet The attribute set, which may be <code>null</code>.
	@return The attribute if the attribute is defined, else <code>null</code>.
	*/
	public static Object getDefinedAttribute(final AttributeSet attributeSet, final String attributeName)
	{
		return attributeSet!=null ? //make sure there is an attribute set
			(attributeSet.isDefined(attributeName) ? attributeSet.getAttribute(attributeName) : null) :
			null;	//return the attribute if it is defined, or null if it isn't
	}

	/**Checks whether the element has been specified as visible or hidden, without
		checking up the element hierarchy.
	@param attributeSet The attribute set.
	@return <code>true</code> if the visible attribute is <code>true</code> or
		not set, else <code>false</code> if the visible attribute is set to
		<code>false</code>.
	@see VISIBLE_ATTRIBUTE_NAME
	*/
	public static boolean isVisible(final AttributeSet attributeSet)
	{
		final Object visibleAttribute=getDefinedAttribute(attributeSet, VISIBLE_ATTRIBUTE_NAME);	//get the visible attribute, if there is one
		return !Boolean.FALSE.equals(visibleAttribute);	//the element is visible unless the visible attribute is set to false
	}

	/**Sets the visibility of the element.
	@param attributeSet The attribute set.
	@param visible <code>true</code> if the element should be visible, else
		<code>false</code> if the view created from the element should not be shown.
	*/
	public static void setVisible(final MutableAttributeSet attributeSet, final boolean visible)
	{
		attributeSet.addAttribute(VISIBLE_ATTRIBUTE_NAME, Boolean.valueOf(visible));	//set the value of the visible attribute
	}
}

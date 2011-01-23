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

package com.globalmentor.swing.text;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;

/**A collection of utilities for working with Swing styles in attribute sets.
@author Garret Wilson
*/
public class Styles
{

	/**The characters to prepend to Swing-specific attribute names.*/
	public final static String SWING_ATTRIBUTE_START="$";

	/**Whether a view for this element should be visible or hidden.*/
	public final static String VISIBLE_ATTRIBUTE_NAME=SWING_ATTRIBUTE_START+"visible";

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
	@see #VISIBLE_ATTRIBUTE_NAME
	*/
	public static boolean isVisible(final AttributeSet attributeSet)
	{
		final Object visibleAttribute=getDefinedAttribute(attributeSet, Styles.VISIBLE_ATTRIBUTE_NAME);	//get the visible attribute, if there is one
		return !Boolean.FALSE.equals(visibleAttribute);	//the element is visible unless the visible attribute is set to false
	}

	/**Sets the visibility of the element.
	@param attributeSet The attribute set.
	@param visible <code>true</code> if the element should be visible, else
		<code>false</code> if the view created from the element should not be shown.
	*/
	public static void setVisible(final MutableAttributeSet attributeSet, final boolean visible)
	{
		attributeSet.addAttribute(Styles.VISIBLE_ATTRIBUTE_NAME, Boolean.valueOf(visible));	//set the value of the visible attribute
	}

}

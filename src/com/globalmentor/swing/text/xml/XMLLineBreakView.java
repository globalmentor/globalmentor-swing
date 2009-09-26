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

import java.awt.Graphics;
import java.awt.Shape;

import javax.swing.text.Element;
import javax.swing.text.View;

/**View that causes a line break.
@author Garret Wilson
*/
public class XMLLineBreakView extends XMLInlineView
{

	/**Constructor which specifies an element.
	@param element The element this view represents.
	*/
	public XMLLineBreakView(Element element)
	{
		super(element);	//construct the parent class
	}

	/**Performs no rendering for the line break view.
	@param graphics The rendering surface to use.
	@param allocation The allocated region to render into.
	@see View#paint
	*/
	public void paint(Graphics graphics, Shape allocation) {}

	/**Determines the preferred span for this view. Returns 0 for the X axis.
	@param axis The axis (<code>View.X_AXIS</code> or <code>View.Y_AXIS<code>).
	@return The span the view would like to be rendered into.
	@see View#getPreferredSpan
	*/
	public float getPreferredSpan(final int axis)
	{
		return axis==X_AXIS ? 0 : super.getPreferredSpan(axis);  //return 0 for the X axis
	}

	/**Determines the minimum span for this view along an axis. Returns 0 for the X axis.
	@param axis The axis (<code>View.X_AXIS</code> or <code>View.Y_AXIS<code>).
	@return The minimum span the view can be rendered into.
	@see View#getMinimumSpan
	*/
	public float getMinimumSpan(int axis)
	{
		return axis==X_AXIS ? 0 : super.getMinimumSpan(axis);  //return 0 for the X axis
	}

	/**Determines the maximum span for this view along an axis. Returns 0 for the X axis.
	@param axis The axis (<code>View.X_AXIS</code> or <code>View.Y_AXIS<code>).
	@return The maximum span the view can be rendered into.
	@see View#getMaximumSpan
	*/
	public float getMaximumSpan(int axis)
	{
		return axis==X_AXIS ? 0 : super.getMinimumSpan(axis);  //return 0 for the X axis
	}


	/**Forces a line break on the horizontal axis.
	@param axis The axis to get the break weight for.
	@param pos The position in the document.
	@param len The length available for this view.
	@return The preference for breaking at this location: ForcedBreakWeight for
		the X axis, and the default break weight for the other axis.
	*/
	public int getBreakWeight(int axis, float pos, float len)
	{
		if(axis==X_AXIS)	//if they want the break weight for the X axis
		{
			return ForcedBreakWeight;	//show that we're forcing a break on this axis
		}
		else	//if they want the break weight on the Y axis
		{
			return super.getBreakWeight(axis, pos, len);	//let our parent class determine the break weight
		}
	}

}

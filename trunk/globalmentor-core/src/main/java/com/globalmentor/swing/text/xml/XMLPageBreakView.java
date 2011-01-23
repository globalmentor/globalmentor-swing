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

import javax.swing.text.Element;

import com.globalmentor.swing.text.InvisibleView;

/**View class that causes a page break.
@see XMLBlockView
@author Garret Wilson
*/
public class XMLPageBreakView extends InvisibleView	//TODO maybe go back to XMLParagraphView, if that class knows how to be hidden, but what are the consequences of either? XMLParagraphView
{

	/**Constructor which specifies an element.
	@param element The element this view represents.
	*/
	public XMLPageBreakView(Element element)
	{
		super(element);	//construct the parent class
//TODO del Log.trace("Creating view for element: ", element.getAttributes().getAttribute(StyleConstants.NameAttribute));	//TODO del
//TODO del		StyleSheet sheet = getStyleSheet();
//TODO del	attr = sheet.getViewAttributes(this);
//TODO del		AttributeSet=new SimpleAttributeSet(element.getAttributes());	//TODO testing

//TODO del		changedUpdate(null, null, null);	//TODO testing
	}

	/**Forces a line break on the vertical axis.
	@param axis The axis to get the break weight for.
	@param pos The position in the document.
	@param len The length available for this view.
	@return The preference for breaking at this location: ForcedBreakWeight for
		the Y axis, and the default break weight for the other axis.
	*/
	public int getBreakWeight(int axis, float pos, float len)
	{
		if(axis==Y_AXIS)	//if they want the break weight for the Y axis
		{
//TODO del System.out.println("<page-break> view is trying to force a page break.");	//TODO del
			return ForcedBreakWeight;	//show that we're forcing a break on this axis
		}
		else	//if they want the break weight on the X axis
			return super.getBreakWeight(axis, pos, len);	//let our parent class determine the break weight
	}

}

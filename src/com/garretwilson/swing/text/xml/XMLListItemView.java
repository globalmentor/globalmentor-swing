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

package com.garretwilson.swing.text.xml;

import javax.swing.text.*;

/**Represents a CSS1-like list item.
@author Garret Wilson
*/
public class XMLListItemView extends XMLBlockView	//TODO probably delete this entire class
{

	/**Constructs a fragment view for the paragraph.
	@param element The element this view is responsible for.
	@param axis The tiling axis, either View.X_AXIS or View.Y_AXIS.
	*/
	public XMLListItemView(Element element, int axis)
	{
		super(element, axis); //do the default construction
//TODO del Log.trace("Creating a list view object.");
//TODO del		setInsets((short)50, (short)50, (short)50, (short)50);	//TODO fix; testing
//TODO del		setInsets((short)0, (short)25, (short)0, (short)0);	//TODO fix; testing
	}

}
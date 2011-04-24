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
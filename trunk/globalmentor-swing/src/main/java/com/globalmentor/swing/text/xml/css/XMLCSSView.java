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

package com.globalmentor.swing.text.xml.css;

import java.awt.Color;

/**Represents a view which contains properties for and can be painted as CSS
	elements.
@author Garret Wilson
*/
public interface XMLCSSView	//TODO probably remove this class, now that XMLCSSViewPainter uses attributes instead of this interface
{

	/**Gets the background color of the view.
	@return The background color of the view.
	*/
	public Color getBackgroundColor();

}
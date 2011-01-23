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

package com.globalmentor.swing.draw;

import java.awt.Graphics;
import java.awt.Point;

/**An object that can be drawn onto a graphics context.
@author Garret Wilson
*/
public interface Drawable
{

	/**Retrieves the location of the object.
	@return The starting point of the drawing shape.
	*/
	public Point getLocation();

	/**Sets the location of the object.
	@param newLocation The new location of the object.
	*/
	public void setLocation(final Point newLocation);

	/**Sets the location of the object.
	@param x The new horizontal location.
	@param y The new vertical location.
	*/
	public void setLocation(final int x, final int y);

	/**Draws the object using the provided graphics.
	@param graphics The graphics with which to draw the object.
	*/
	public void draw(final Graphics graphics);
}
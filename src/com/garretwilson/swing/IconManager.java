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

package com.garretwilson.swing;

import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import com.globalmentor.util.ResourceManager;

/**Manages icons bundled with an application.
	Loads icons  from the class loader of a given class.
	This class keeps weak references to the icons it loads so that they may
	be reused if they have not been garbage collected. This class does have a
	small overhead of references to icons that are no longer used and have
	been garbage collected.
@author Garret Wilson
*/
@Deprecated
public class IconManager extends ResourceManager
{

	/**Class constructor.
	@param iconLoaderClass The class used for loading icons.
	*/
	public IconManager(final Class iconLoaderClass)
	{
		super(iconLoaderClass); //construct the parent class
	}

	/**Loads an icon resource. The filename may either be the name of a file
		stored in the same path as the loader class, or the full path to an image
		file.
		<p>This is a convenience method that calls <code>getResource(String)</code>.</p>
	@param filename The filename of the icon.
	@return An icon object representing the image.
	*/
	public Icon getIcon(final String filename)
	{
		return (Icon)getResource(filename); //get the resource and cast it to an icon
	}

	/**Retrieves an icon from a URL.
	@param url The URL of the icon.
	@return The icon, loaded from the URL.
	*/
	protected Object getResource(final URL url)
	{
	  return new ImageIcon(url);	//load the icon from the URL
	}

}
package com.garretwilson.resources.icon;

import java.util.*;
import javax.swing.ImageIcon;

import com.globalmentor.collections.DecoratorReadWriteLockMap;
import com.globalmentor.collections.PurgeOnWriteWeakValueHashMap;
import com.globalmentor.util.*;

/**Manages icon resources bundled with an application. This class keeps weak
	references to the icons it loads so that they may be reused if they have not
	been garbage collected.
@author Garret Wilson
*/
public class IconResources implements IconResourceConstants
{

	/**The thread-safe map of icons that will be released when no longer is use.*/
	protected static final Map<String, ImageIcon> iconMap=new DecoratorReadWriteLockMap<String, ImageIcon>(new PurgeOnWriteWeakValueHashMap<String, ImageIcon>());

	/**This class cannot be publicly instantiated.*/
	private IconResources() {}

	/**Loads an icon resource. The filename may either be the name of a file
		stored in /com/garretwilson/resources/icons, or the full path to an image
		file.
	@param filename The filename of the icon.
	@return An icon object representing the image.
	*/
	public static ImageIcon getIcon(final String filename)
	{
		ImageIcon imageIcon=iconMap.get(filename); //see if we have the icon
		if(imageIcon==null)  //if we haven't loaded this icon yet, or the icon has been garage collected
		{
		  imageIcon=new ImageIcon(IconResources.class.getResource(filename));	//load the icon
			iconMap.put(filename, imageIcon);  //store a weak reference to the icon
		}
		return imageIcon;  //return the icon that we loaded or already had loaded
	}

}
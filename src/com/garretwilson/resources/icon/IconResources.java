package com.garretwilson.resources.icon;

import java.lang.ref.*;
import java.util.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**Manages icon resources bundled with an application. This class keeps weak
	references to the icons it loads so that they may be reused if they have not
	been garbage collected. This class does have a small overhead of references
	to icons that are no longer used and have been garbage collected.
@author Garret Wilson
*/
public class IconResources implements IconResourceConstants
{

	/**The map of references to icons, each keyed to an icon filename.*/
	protected static final Map iconReferenceMap=new HashMap();

	/**This class cannot be publicly instantiated.*/
	private IconResources() {}

	/**Loads an icon resource. The filename may either be the name of a file
		stored in /com/garretwilson/resources/icons, or the full path to an image
		file.
	@param filename The filename of the icon.
	@return An icon object representing the image.
	*/
	public static Icon getIcon(final String filename)
	{
		Icon icon=null; //we'll try to get the icon if we already have it loaded
		final Reference iconReference=(Reference)iconReferenceMap.get(filename); //see if we have an icon reference
		if(iconReference!=null) //if we have an icon reference
		{
			icon=(Icon)iconReference.get(); //get the icon stored in the reference
		}
		if(icon==null)  //if we haven't loaded this icon yet, or the icon has been garage collected
		{
		  icon=new ImageIcon(IconResources.class.getResource(filename));	//load the icon
			iconReferenceMap.put(filename, new WeakReference(icon));  //store a weak reference to the icon
		}
		return icon;  //return the icon that we loaded or already had loaded
	}

}
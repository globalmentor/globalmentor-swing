package com.garretwilson.swing.text;

/**Indicates that this view can have cached information released in order to
	free up memory.
@author Garret Wilson
@see com.garretwilson.swing.text.xml.XMLPagedView
*/
public interface ViewReleasable	//TODO del if not needed
{

	/**Releases any cached information such as pooled views and flows.*/
	public void release();
}

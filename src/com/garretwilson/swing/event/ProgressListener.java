package com.garretwilson.swing.event;

import java.util.EventListener;

/**Represents an object which monitors the progress of a particular action by
	listening for <code>ProgressEvent</code>
@see ProgressEvent
@author Garret Wilson
*/
public interface ProgressListener extends EventListener
{
	/**Invoked when progress has been made.
	@param e The event object representing the progress made.
	*/
	void madeProgress(ProgressEvent e);
}

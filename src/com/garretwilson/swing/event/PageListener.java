package com.garretwilson.swing.event;

import java.util.EventListener;

/**Listener for changes in displayed page of an XMLTextPane or OEBBook.
@author Garret Wilson
@see com.garretwilson.swing.XMLTextPane
@see com.garretwilson.swing.OEBBook
*/
public interface PageListener extends EventListener
{

	/**Called when the displayed page has changed.
	@param e The page event.
	*/
	void pageChanged(PageEvent e);
}


package com.garretwilson.swing.event;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**Class that provides an empty implementations of the
	<code>DocumentListener</code> interface.
@author Garret Wilson	
*/
public class DocumentAdapter implements DocumentListener
{
	/**Gives notification that there was an insert into the document. The 
		range given by the DocumentEvent bounds the freshly inserted region.
	@param documentEvent The document event.
	*/
	public void insertUpdate(final DocumentEvent documentEvent) {}

	/**Gives notification that a portion of the document has been 
		removed. The range is given in terms of what the view last
		saw (that is, before updating sticky positions).
	@param documentEvent The document event.
	*/
	public void removeUpdate(final DocumentEvent documentEvent) {}

	/**Gives notification that an attribute or set of attributes changed.
	@param documentEvent The document event.
	*/
	public void changedUpdate(final DocumentEvent documentEvent) {}
}

package com.garretwilson.swing.event;

import javax.swing.event.DocumentEvent;

/**Class implements the <code>DocumentListener</code> interface and provides a
	new <code>modifyUpdate()</code> method that is called in response to
	either <code>insertUpdate()</code> or <code>removeUpdate()</code> being
	called.
@author Garret Wilson
@see DocumentListener#insertUpdate	
@see DocumentListener#removeUpdate	
*/
public class DocumentModifyAdapter extends DocumentAdapter
{
	/**Gives notification that there was an insert into the document. The 
		range given by the DocumentEvent bounds the freshly inserted region.
	@param documentEvent The document event.
	This version simply calls <code>modifyUpdate()</code>.
	*/
	public void insertUpdate(final DocumentEvent documentEvent)
	{
		modifyUpdate(documentEvent);	//notify of modification
		super.insertUpdate(documentEvent);	//do the default functionality
	}

	/**Gives notification that a portion of the document has been 
		removed. The range is given in terms of what the view last
		saw (that is, before updating sticky positions).
	@param documentEvent The document event.
	This version simply calls <code>modifyUpdate()</code>.
	*/
	public void removeUpdate(final DocumentEvent documentEvent)
	{
		modifyUpdate(documentEvent);	//notify of modification
		super.removeUpdate(documentEvent);	//do the default functionality
	}

	/**Gives notification that the document has been modified.
	@param documentEvent The document event.
	*/
	public void modifyUpdate(final DocumentEvent documentEvent) {}
}

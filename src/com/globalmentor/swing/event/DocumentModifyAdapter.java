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

package com.globalmentor.swing.event;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**Class implements the {@link DocumentListener} interface and provides a
	new {@link #modifyUpdate(DocumentEvent)} method that is called in response to
	either {@link #insertUpdate(DocumentEvent)} or {@link #removeUpdate(DocumentEvent)} being
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

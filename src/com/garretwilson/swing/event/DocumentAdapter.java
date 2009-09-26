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

package com.garretwilson.swing.event;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**Class that provides an empty implementations of the
	{@link DocumentListener} interface.
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

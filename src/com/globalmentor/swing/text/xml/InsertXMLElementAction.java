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

package com.globalmentor.swing.text.xml;

import java.awt.event.ActionEvent;
import java.net.URI;
import javax.swing.*;
import javax.swing.text.*;

import com.globalmentor.swing.XMLTextPane;
import com.globalmentor.swing.text.TextComponentFocusTargetAction;

/**Action that inserts an XML element into the currently focused {@link XMLTextPane}
@author Garret Wilson
@see XMLTextPane
*/
public class InsertXMLElementAction extends TextComponentFocusTargetAction
{

	/**The namespace of the XML element to insert.*/
	private final URI elementNamespaceURI;

		/**@return The namespace of the XML element to insert.*/
		protected URI getElementNamespaceURI() {return elementNamespaceURI;}

	/**The qualified name of the XML element to insert.*/
	private final String elementQName;

		/**@return The qualified name of the XML element to insert.*/
		protected String getElementQName() {return elementQName;}

	/**Element information constructor.
	@param elementNamespaceURI The namespace of the XML element.
	@param elementQName The qualified name of the XML element.
	*/
	public InsertXMLElementAction(final URI elementNamespaceURI, final String elementQName)
	{
		this(null, elementNamespaceURI, elementQName);	//construct the class with no name TODO maybe construct our own name
	}

	/**Name constructor with no default target.
	@param name The name description of the action.
	@param elementNamespaceURI The namespace of the XML element.
	@param elementQName The qualified name of the XML element.
	*/
	public InsertXMLElementAction(final String name, final URI elementNamespaceURI, final String elementQName)
	{
		this(name, elementNamespaceURI, elementQName, null);	//construct the class with no default target
	}

	/**Name and target constructor.
	@param name The name description of the action.
	@param elementNamespaceURI The namespace of the XML element.
	@param elementQName The qualified name of the XML element.
	@param defaultTarget The default target component, or <code>null</code> if there
		should be no default target.
	*/
	public InsertXMLElementAction(final String name, final URI elementNamespaceURI, final String elementQName, final JTextComponent defaultTarget)
	{
		this(name, null, elementNamespaceURI, elementQName, defaultTarget);	//construct the class with no icon
	}

	/**Name, icon, and default target constructor.
	@param name The name description of the action.
	@param icon The icon to represent the action.
	@param elementNamespaceURI The namespace of the XML element.
	@param elementQName The qualified name of the XML element.
	@param defaultTarget The default target component, or <code>null</code> if there
		should be no default target.
	*/
	public InsertXMLElementAction(final String name, final Icon icon, final URI elementNamespaceURI, final String elementQName, final JTextComponent defaultTarget)
	{
		super(name, icon, defaultTarget);  //construct the parent
		this.elementNamespaceURI=elementNamespaceURI;	//save the namespace URI
		this.elementQName=elementQName;	//save the qualified name
	}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			final JTextComponent textComponent=(JTextComponent)getTarget();	//get the target of our action
			if(textComponent!=null)	//if we have a target
			{
				final Caret caret=textComponent.getCaret();	//get the text component caret
				final int start=Math.min(caret.getMark(), caret.getDot());	//get the start of the selection
				final int end=Math.max(caret.getMark(), caret.getDot());	//get the end of the selection
				if(end>start)	//if there is a selection TODO fix to work for nonselections
				{
					final Document document=textComponent.getDocument();	//get the text component's document
					if(document instanceof XMLDocument)	//if this is an XML document
					{
						final XMLDocument xmlDocument=(XMLDocument)document;	//cast the document to an XML document
//TODO fix XML element insertion						xmlDocument.insertXMLElement(start, end-start, getElementNamespaceURI(), getElementQName());
					}
				}
			}						 
		}
}

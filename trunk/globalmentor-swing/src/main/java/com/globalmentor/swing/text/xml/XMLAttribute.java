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

import com.globalmentor.text.xml.XMLNamedObject;

/**An XML attribute stored in a Swing attribute set. 
@author Garret
*/
public class XMLAttribute extends XMLNamedObject
{
	/**The value of the attribute.*/
	private final String value;

		/**@return The value of the attribute.*/
		public String getValue() {return value;}

	/**Constructor specifying the attribute namespace, qname, and value.
	@param namespaceURI The URI of the attribute namespace, or <code>null</code>
		if there is no namespace.
	@param qname The qualified name of the attribute
	@param value The value of the attribute.
	*/
	public XMLAttribute(final String namespaceURI, final String qname, final String value)
	{
		super(namespaceURI, qname);	//construct the super class
		this.value=value;	//set the value
	}

	/**@return A string representation of the XML attribute in the form (namespace) qname="value".*/
	public String toString()
	{
		final StringBuffer stringBuffer=new StringBuffer();	//create a new string buffer
		if(getNamespaceURI()!=null)	//if we have a namespace
		{
			stringBuffer.append('(').append(getNamespaceURI()).append(')').append(' ');	//append the namespace if there is one
		}
		stringBuffer.append(getQName()).append('=').append('"').append(getValue()).append('"');
		return stringBuffer.toString();	//return the value we constructed
	}
}

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

package com.garretwilson.swing.text;

import java.util.Enumeration;
import javax.swing.text.*;
import javax.swing.text.StyleConstants;

/**Allows Java Swing attribute sets to be manipulated.
@author Garret Wilson
*/
public class AttributeSets	//TODO do we really want this class with a Styles class?
{

	/**This class cannot be publicly instantiated.*/
	private AttributeSets() {}

	/**Constructs a string representing the names and values of the attribute set.
		This method is modified from the code found in <code>ElementTreePanel</code>,
		Copyright (c) 1998, 1999 by Sun Microsystems, Inc., written by Scott Violet
		version 1.9 04/23/99.
	@param attributeSet The attribute set for which a representational string
		should be returned.
	@return A string representation of the contents of the attribute set, or the
		empty string if the attribute set is <code>null</code>.
	*/
	public static String getAttributeSetString(final AttributeSet attributeSet)
	{
		if(attributeSet!=null)  //if a valid attribute set was passed
		{
		  final StringBuffer stringBuffer=new StringBuffer("[");  //create a new string buffer to hold the string informaiton
		  final Enumeration names=attributeSet.getAttributeNames(); //get the enumeration of names
			while(names.hasMoreElements())  //while there are more names
			{
				final Object nextName=names.nextElement();  //get the next name
				if(nextName!=StyleConstants.ResolveAttribute)
				{
			    stringBuffer.append(" ");
			    stringBuffer.append(nextName);
			    stringBuffer.append("=");
			    stringBuffer.append(attributeSet.getAttribute(nextName));
				}
		  }
		  stringBuffer.append(" ]");
			return stringBuffer.toString(); //return the string buffer as a string
		}
		else  //if valid attribute set was returned
		  return ""; //return an empty string
	}

}
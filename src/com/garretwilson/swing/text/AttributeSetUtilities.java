package com.garretwilson.swing.text;

import java.util.Enumeration;
import javax.swing.text.*;

/**Allows Java Swing attribute sets to be manipulated.
@author Garret Wilson
*/
public class AttributeSetUtilities
{

	/**This class cannot be publicly instantiated.*/
	private AttributeSetUtilities() {}

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
package com.garretwilson.swing.border;

import javax.swing.*;
import javax.swing.border.*;

/**Various convenience methods for working with Swing component borders.
@author Garret Wilson
 */
public class BorderUtilities
{

	/**Creates a default border.
	Using this method promotes consistency across components.
	*/
	public static Border createDefaultBorder()
	{
		return BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);	//return a lowered etched border
	}
	
	/**Creates a default titled border based on a default border.
	Using this method promotes consistency across components.
	@see #createDefaultBorder
	*/
	public static TitledBorder createDefaultTitledBorder()
	{
		return BorderFactory.createTitledBorder(createDefaultBorder());	//create a titled border based upon a default border
	}
	
}

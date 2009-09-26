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

package com.globalmentor.swing.border;

import javax.swing.*;
import javax.swing.border.*;

/**Various convenience methods for working with Swing component borders.
@author Garret Wilson
 */
public class Borders
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
	@see #createDefaultBorder()
	*/
	public static TitledBorder createDefaultTitledBorder()
	{
		return BorderFactory.createTitledBorder(createDefaultBorder());	//create a titled border based upon a default border
	}
	
}

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

package com.garretwilson.swing.text.directory.vcard;

import java.awt.*;
import javax.swing.*;
import com.garretwilson.swing.*;

/**A base panel for editing types of a vCard
	<code>text/directory</code>	profile as defined in
	<a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
<p>By default a <code>GridBagLayout</code> is used as a layout manager.</p>
@author Garret Wilson
@see java.awt.GridBagLayout
*/
public class BasicVCardPanel extends ModifiablePanel
{
	/**Default constructor that uses a <code>GridBagLayout</code>.*/
	public BasicVCardPanel()
	{
		this(true); //initialize the panel
	}

	/**Constructor with optional initialization using a <code>GridBagLayout</code>.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public BasicVCardPanel(final boolean initialize)
	{
		this(new GridBagLayout(), initialize);	//construct the panel with a grid bag layout by default
	}

	/**Layout constructor.
	@param layout The layout manager to use.
	*/
	public BasicVCardPanel(final LayoutManager layout)
	{
		this(layout, true);	//construct the class with the layout, initializing the panel
	}

	/**Layout constructor with optional initialization.
	@param layout The layout manager to use.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public BasicVCardPanel(final LayoutManager layout, final boolean initialize)
	{
		super(layout, false);	//construct the parent class but don't initialize
		if(initialize)  //if we should initialize
			initialize();   //initialize the panel
	}

	/**Creates a default styled button to be used for invoking the given select language
		action.
	@param selectLanguageAction The action for which a button should be created.
	@return A default styled button to invoke the select language action.
	*/
	public static JButton createSelectLanguageButton(final SelectLanguageAction selectLanguageAction)
	{
		final JButton selectLanguageButton=new JButton(selectLanguageAction);	//create a button from the action
		selectLanguageButton.setText("");	//turn off the text
		selectLanguageButton.setBorder(null);	//remove the border
		return selectLanguageButton;	//return the button we created
	}

}

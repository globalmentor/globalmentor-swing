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

package com.globalmentor.swing.text.directory.vcard;

import java.awt.*;
import com.globalmentor.awt.BasicGridBagLayout;
import com.globalmentor.swing.*;

/**A panel containing fields for the identification and address types
	of a vCard <code>text/directory</code>	profile as defined in
	<a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class NameAddressPanel extends BasicPanel
{
	/**The identification panel.*/
	private final IdentificationPanel identificationPanel;

		/**@return The identification panel.*/
		public IdentificationPanel getIdentificationPanel() {return identificationPanel;}

	/**The addresses panel.*/
	private final AddressesPanel addressesPanel;

		/**@return The addresses panel.*/
		public AddressesPanel getAddressesPanel() {return addressesPanel;}

	/**The organization panel.*/
	private final OrganizationPanel organizationPanel;

		/**@return The organization panel.*/
		public OrganizationPanel getOrganizationPanel() {return organizationPanel;}

	/**The telecommunications panel.*/
	private final TelecommunicationsPanel telecommunicationsPanel;

		/**@return The telecommunications panel.*/
		public TelecommunicationsPanel getTelecommunicationsPanel() {return telecommunicationsPanel;}
	
	/**Default constructor.*/
	public NameAddressPanel()
	{
		super(new BasicGridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		identificationPanel=new IdentificationPanel();
		addressesPanel=new AddressesPanel();
		organizationPanel=new OrganizationPanel();
		telecommunicationsPanel=new TelecommunicationsPanel();
		setDefaultFocusComponent(identificationPanel);	//set the default focus component
		initialize();	//initialize the panel
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		add(identificationPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(addressesPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(organizationPanel, new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(telecommunicationsPanel, new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
/*TODO fix
		add(identificationPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		add(addressesPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(organizationPanel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(telecommunicationsPanel, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
*/
	}

}

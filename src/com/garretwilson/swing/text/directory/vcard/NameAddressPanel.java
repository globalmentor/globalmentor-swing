package com.garretwilson.swing.text.directory.vcard;

import java.awt.*;
import com.garretwilson.swing.*;
import com.garretwilson.util.*;

/**A panel containing fields for the identification and address types
	of a vCard <code>text/directory</code>	profile as defined in
	<a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class NameAddressPanel extends GridBagPanel implements Verifiable
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
		super(false);	//construct the panel using a grid bag layout, but don't initialize the panel
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
/*G***fix
		add(identificationPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		add(addressesPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(organizationPanel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(telecommunicationsPanel, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
*/
	}

	/**Verifies the component.
	@return <code>true</code> if the component contents are valid, <code>false</code>
		if not.
	*/
	public boolean verify()
	{
		return getTelecommunicationsPanel().verify();	//verify all the subcomponents that can be verified
	}

}

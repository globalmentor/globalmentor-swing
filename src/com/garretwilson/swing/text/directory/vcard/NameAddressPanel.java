package com.garretwilson.swing.text.directory.vcard;

import java.awt.*;
import com.garretwilson.swing.*;

/**A panel containing fields for the identification and address types
	of a vCard <code>text/directory</code>	profile as defined in
	<a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class NameAddressPanel extends DefaultPanel
{
	/**The identification panel.*/
	private final IdentificationPanel identificationPanel;

	/**The addresses panel.*/
	private final AddressesPanel addressesPanel;
	
	/**Default constructor.*/
	public NameAddressPanel()
	{
		super(new GridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		identificationPanel=new IdentificationPanel();
		addressesPanel=new AddressesPanel();
		setDefaultFocusComponent(identificationPanel);	//set the default focus component
		initialize();	//initialize the panel
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		add(identificationPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(addressesPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	}

}

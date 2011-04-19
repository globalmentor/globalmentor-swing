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
import java.util.EnumSet;
import java.util.Set;

import javax.swing.*;
import com.globalmentor.awt.BasicGridBagLayout;
import com.globalmentor.swing.*;
import com.globalmentor.text.directory.vcard.*;

/**A panel allowing specification of the types of address of the <code>ADR</code>
	or <code>LABEL</code> types of a vCard <code>text/directory</code>
	profile as defined in <a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class AddressTypePanel extends BasicPanel
{
	
	/**The checkbox for a domestic delivery address.*/
	private final JCheckBox domesticCheckBox;

		/**The checkbox for a domestic delivery address.*/
		public JCheckBox getDomesticCheckbox() {return domesticCheckBox;}

	/**The checkbox for an international delivery address.*/
	private final JCheckBox internationalCheckBox;

		/**@return The checkbox for an international delivery address.*/
		public JCheckBox getInternationalCheckbox() {return internationalCheckBox;}

	/**The checkbox for a postal delivery address.*/
	private final JCheckBox postalCheckBox;

		/**@return The checkbox for an international delivery address.*/
		public JCheckBox getPostalCheckbox() {return postalCheckBox;}

	/**The checkbox for a parcel delivery address.*/
	private final JCheckBox parcelCheckBox;

		/**@return The checkbox for an international delivery address.*/
		public JCheckBox getParcelCheckbox() {return parcelCheckBox;}

	/**The checkbox for a delivery address for a residence.*/
	private final JCheckBox homeCheckBox;

		/**@return The checkbox for an international delivery address.*/
		public JCheckBox getHomeCheckbox() {return homeCheckBox;}

	/**The checkbox for a delivery address for a place of work.*/
	private final JCheckBox workCheckBox;

		/**@return The checkbox for an international delivery address.*/
		public JCheckBox getWorkCheckbox() {return workCheckBox;}

	/**The checkbox for the preferred delivery address.*/
	private final JCheckBox preferredCheckBox;

		/**@return The checkbox for an international delivery address.*/
		public JCheckBox getPreferredCheckbox() {return preferredCheckBox;}

	/**Places the delivery address types.
	@param addressTypes The new delivery address types.
	@see Address
	*/
	public void setAddressTypes(final Set<Address.Type> addressTypes)
	{
		domesticCheckBox.setSelected(addressTypes.contains(Address.Type.DOM));
		internationalCheckBox.setSelected(addressTypes.contains(Address.Type.INTL));
		postalCheckBox.setSelected(addressTypes.contains(Address.Type.POSTAL));
		parcelCheckBox.setSelected(addressTypes.contains(Address.Type.PARCEL));
		homeCheckBox.setSelected(addressTypes.contains(Address.Type.HOME));
		workCheckBox.setSelected(addressTypes.contains(Address.Type.WORK));
		preferredCheckBox.setSelected(addressTypes.contains(Address.Type.PREF));
	}
	
	/**@return The delivery address types.
	@see Address
	*/
	public Set<Address.Type> getAddressTypes()
	{
		final Set<Address.Type> addressTypes=EnumSet.noneOf(Address.Type.class);	//start out without knowing the address types
		if(domesticCheckBox.isSelected())
		{
			addressTypes.add(Address.Type.DOM);
		}
		if(internationalCheckBox.isSelected())
		{
			addressTypes.add(Address.Type.INTL);
		}
		if(postalCheckBox.isSelected())
		{
			addressTypes.add(Address.Type.POSTAL);
		}
		if(parcelCheckBox.isSelected())
		{
			addressTypes.add(Address.Type.PARCEL);
		}
		if(homeCheckBox.isSelected())
		{
			addressTypes.add(Address.Type.HOME);
		}
		if(workCheckBox.isSelected())
		{
			addressTypes.add(Address.Type.WORK);
		}
		if(preferredCheckBox.isSelected())
		{
			addressTypes.add(Address.Type.PREF);
		}
		return addressTypes;	//return the address types
	}

	/**Address type constructor.
	@param addressTypes The new delivery address types.
	*/
	public AddressTypePanel(final Set<Address.Type> addressTypes)
	{
		super(new BasicGridBagLayout(), false);	//construct the panel using a grid bag layout
		domesticCheckBox=new JCheckBox();
		internationalCheckBox=new JCheckBox();
		postalCheckBox=new JCheckBox();
		parcelCheckBox=new JCheckBox();
		homeCheckBox=new JCheckBox();
		workCheckBox=new JCheckBox();
		preferredCheckBox=new JCheckBox();
		setDefaultFocusComponent(preferredCheckBox);	//set the default focus component
		initialize();	//initialize the panel
		setAddressTypes(addressTypes);	//set the given address type
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		domesticCheckBox.setText("Domestic");	//TODO i18n
		internationalCheckBox.setText("International");	//TODO i18n
		postalCheckBox.setText("Postal");	//TODO i18n
		parcelCheckBox.setText("Parcel");	//TODO i18n
		homeCheckBox.setText("Home");	//TODO i18n
		workCheckBox.setText("Work");	//TODO i18n
		preferredCheckBox.setText("Preferred");	//TODO i18n
		preferredCheckBox.setFont(preferredCheckBox.getFont().deriveFont(Font.BOLD));
		add(preferredCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(workCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(homeCheckBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(domesticCheckBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(internationalCheckBox, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(postalCheckBox, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(parcelCheckBox, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		setAddressTypes(EnumSet.noneOf(Address.Type.class));	//clear the fields
	}

}

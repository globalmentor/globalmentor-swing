package com.garretwilson.swing.text.directory.vcard;

import java.awt.*;
import javax.swing.*;
import com.garretwilson.text.directory.vcard.*;
import com.garretwilson.swing.*;

/**A panel allowing specification of the types of address of the <code>ADR</code>
	or <code>LABEL</code> types of a vCard <code>text/directory</code>
	profile as defined in <a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class AddressTypePanel extends DefaultPanel
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

	/**Places the delivery address type into the various fields.
	@param addressType The new delivery address type, one of the
		<code>Address.XXX_ADDRESS_TYPE</code> constants.
	@see Address
	*/
	public void setAddressType(final int addressType)
	{
		domesticCheckBox.setSelected((addressType & Address.DOMESTIC_ADDRESS_TYPE)!=0);
		internationalCheckBox.setSelected((addressType & Address.INTERNATIONAL_ADDRESS_TYPE)!=0);
		postalCheckBox.setSelected((addressType & Address.POSTAL_ADDRESS_TYPE)!=0);
		parcelCheckBox.setSelected((addressType & Address.PARCEL_ADDRESS_TYPE)!=0);
		homeCheckBox.setSelected((addressType & Address.HOME_ADDRESS_TYPE)!=0);
		workCheckBox.setSelected((addressType & Address.WORK_ADDRESS_TYPE)!=0);
		preferredCheckBox.setSelected((addressType & Address.PREFERRED_ADDRESS_TYPE)!=0);
	}
	
	/**@return The delivery address type, one of the
		<code>Address.XXX_ADDRESS_TYPE</code> constants.
	@see Address
	*/
	public int getAddressType()
	{
		int addressType=Address.NO_ADDRESS_TYPE;	//start out without knowing the address type
		if(domesticCheckBox.isSelected())
			addressType|=Address.DOMESTIC_ADDRESS_TYPE;
		if(internationalCheckBox.isSelected())
			addressType|=Address.INTERNATIONAL_ADDRESS_TYPE;
		if(postalCheckBox.isSelected())
			addressType|=Address.POSTAL_ADDRESS_TYPE;
		if(parcelCheckBox.isSelected())
			addressType|=Address.PARCEL_ADDRESS_TYPE;
		if(homeCheckBox.isSelected())
			addressType|=Address.HOME_ADDRESS_TYPE;
		if(workCheckBox.isSelected())
			addressType|=Address.WORK_ADDRESS_TYPE;
		if(preferredCheckBox.isSelected())
			addressType|=Address.PREFERRED_ADDRESS_TYPE;
		return addressType;	//return the address type
	}

	/**Default constructor.*/
	public AddressTypePanel()
	{
		super(new GridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		domesticCheckBox=new JCheckBox();
		internationalCheckBox=new JCheckBox();
		postalCheckBox=new JCheckBox();
		parcelCheckBox=new JCheckBox();
		homeCheckBox=new JCheckBox();
		workCheckBox=new JCheckBox();
		preferredCheckBox=new JCheckBox();
		setDefaultFocusComponent(preferredCheckBox);	//set the default focus component
		initialize();	//initialize the panel
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		domesticCheckBox.setText("Domestic");	//G***i18n
		internationalCheckBox.setText("International");	//G***i18n
		postalCheckBox.setText("Postal");	//G***i18n
		parcelCheckBox.setText("Parcel");	//G***i18n
		homeCheckBox.setText("Home");	//G***i18n
		workCheckBox.setText("Work");	//G***i18n
		preferredCheckBox.setText("Preferred");	//G***i18n
		preferredCheckBox.setFont(preferredCheckBox.getFont().deriveFont(Font.BOLD));
		add(preferredCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(workCheckBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(homeCheckBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(domesticCheckBox, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(internationalCheckBox, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(postalCheckBox, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(parcelCheckBox, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		setAddressType(Address.NO_ADDRESS_TYPE);	//clear the fields
	}

}

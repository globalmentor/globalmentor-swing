package com.garretwilson.swing.text.directory.vcard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.*;
import javax.swing.*;
import com.garretwilson.lang.*;
import com.garretwilson.text.directory.vcard.*;
import com.garretwilson.swing.*;
import com.garretwilson.util.*;

/**A panel allowing entry of the "ADR" type of a vCard <code>text/directory</code>
	profile as defined in <a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class AddressPanel extends DefaultPanel
{
	/**The panel allowing selection of the address type.*/
	private final AddressTypePanel addressTypePanel;

		/**@return The panel allowing selection of the address type.*/
		public AddressTypePanel getAddressTypePanel() {return addressTypePanel;}

	/**The label of the post office box.*/
	private final JLabel postOfficeBoxLabel;

	/**The post office box text field.*/
	private final JTextField postOfficeBoxTextField;

		/**@return The post office box text field.*/
		public JTextField getPostOfficeBoxTextField() {return postOfficeBoxTextField;}

	/**The extended addresses, which we won't edit but will save to keep from losing them.*/
	private String[] extendedAddresses=new String[]{};

	/**The label of the street address text fields.*/
	private final JLabel streetAddressesLabel;

	/**The label of the first street address text field.*/
//G***del	private final JLabel streetAddress1Label;

	/**The first street address text field.*/
	private final JTextField streetAddress1TextField;

		/**@return The first street address text field.*/
		public JTextField getStreetAddress1TextField() {return streetAddress1TextField;}
		
	/**The label of the second street address text field.*/
//G***del	private final JLabel streetAddress2Label;

	/**The second street address text field.*/
	private final JTextField streetAddress2TextField;

		/**@return The second street address text field.*/
		public JTextField getStreetAddress2TextField() {return streetAddress2TextField;}

	/**The label of the third street address text field.*/
//G***del	private final JLabel streetAddress3Label;

	/**The third street address text field.*/
	private final JTextField streetAddress3TextField;

		/**@return The third street address text field.*/
		public JTextField getStreetAddress3TextField() {return streetAddress3TextField;}

	/**An array of the street address text fields.*/
	private final JTextField[] streetAddressTextFields;

	/**The label of the locality.*/
	private final JLabel localityLabel;

	/**The locality text field.*/
	private final JTextField localityTextField;

		/**@return The locality text field.*/
		public JTextField getLocalityTextField() {return localityTextField;}

	/**The label of the region.*/
	private final JLabel regionLabel;

	/**The region text field.*/
	private final JTextField regionTextField;

		/**@return The region text field.*/
		public JTextField getRegionTextField() {return regionTextField;}

	/**The label of the postal code.*/
	private final JLabel postalCodeLabel;

	/**The postal code text field.*/
	private final JTextField postalCodeTextField;

		/**@return The postal code text field.*/
		public JTextField getPostalCodeTextField() {return postalCodeTextField;}

	/**The label of the country name.*/
	private final JLabel countryNameLabel;

	/**The country name combo box.*/
	private final JComboBox countryNameComboBox;

		/**@return The country name combo box.*/
		public JComboBox getCountryNameComboBox() {return countryNameComboBox;}

	/**Places the address information into the various fields.
	@param address The address to place in the fields, or <code>null</code> if no
		information should be displayed.
	*/
	public void setAddress(final Address address)
	{
		if(address!=null)	//if there is a name
		{
			postOfficeBoxTextField.setText(address.getPostOfficeBox()!=null ? address.getPostOfficeBox() : "");
			final String[] streetAddresses=address.getStreetAddresses();	//get the street addresses
			for(int i=0; i<streetAddressTextFields.length; ++i)	//look at each of the street address text fields
			{
				if(i<streetAddresses.length)	//if we have a street address for this text field
				{
					streetAddressTextFields[i].setText(streetAddresses[i]);	//set the text of this street address
				}
				else	//if there is no street address for this text field
				{
					streetAddressTextFields[i].setText("");	//clear the text field
				}
			}
			localityTextField.setText(address.getLocality()!=null ? address.getLocality() : "");
			regionTextField.setText(address.getRegion()!=null ? address.getRegion() : "");
			postalCodeTextField.setText(address.getPostalCode()!=null ? address.getPostalCode() : "");
			countryNameComboBox.setSelectedItem(address.getCountryName()!=null ? address.getCountryName() : "");
			addressTypePanel.setAddressType(address.getAddressType());
		}
		else	//if there is no addres, clear the fields
		{
			postOfficeBoxTextField.setText("");
			for(int i=0; i<streetAddressTextFields.length; streetAddressTextFields[i++].setText(""));
			localityTextField.setText("");
			regionTextField.setText("");
			postalCodeTextField.setText("");
			countryNameComboBox.setSelectedItem("");
			addressTypePanel.setAddressType(Address.NO_ADDRESS_TYPE);
		}
	}
	
	/**@return An object representing the VCard address information entered.*/
	public Address getAddress()
	{
		final String postOfficeBox=StringUtilities.getNonEmptyString(postOfficeBoxTextField.getText().trim());
		final List streetAddressList=new ArrayList();	//create a list in which to hold the street names that have content
		for(int i=0; i<streetAddressTextFields.length; ++i)	//look at each of the street address text fields
		{
			if(streetAddressTextFields[i].getText().trim().length()>0)	//if the street address has content
				streetAddressList.add(streetAddressTextFields[i].getText().trim());	//add the street address to the list
		}
		final String[] streetAddresses=(String[])streetAddressList.toArray(new String[streetAddressList.size()]);
		final String locality=StringUtilities.getNonEmptyString(localityTextField.getText().trim());
		final String region=StringUtilities.getNonEmptyString(regionTextField.getText().trim());
		final String postalCode=StringUtilities.getNonEmptyString(postalCodeTextField.getText().trim());
		final String countryName=StringUtilities.getNonEmptyString(countryNameComboBox.getSelectedItem().toString().trim());
		final int addressType=addressTypePanel.getAddressType();
		return new Address(postOfficeBox, extendedAddresses, streetAddresses, locality, region, postalCode, countryName, addressType);	//create and return an address representing the entered information
	}

	/**Default constructor.*/
	public AddressPanel()
	{
		this(new Address());	//initialize with a default addressaddress
	}

	/**Address constructor.
	@param address The address to place in the fields, or <code>null</code> if no
		information should be displayed.
	*/
	public AddressPanel(final Address address)
	{
		super(new GridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		addressTypePanel=new AddressTypePanel();
		postOfficeBoxLabel=new JLabel();
		postOfficeBoxTextField=new JTextField();
		streetAddressesLabel=new JLabel();
//	G***del		streetAddress1Label=new JLabel();
		streetAddress1TextField=new JTextField();
//	G***del		streetAddress2Label=new JLabel();
		streetAddress2TextField=new JTextField();
//	G***del		streetAddress3Label=new JLabel();
		streetAddress3TextField=new JTextField();
		localityLabel=new JLabel();
		localityTextField=new JTextField();
		regionLabel=new JLabel();
		regionTextField=new JTextField();
		postalCodeLabel=new JLabel();
		postalCodeTextField=new JTextField();
		countryNameLabel=new JLabel();
		countryNameComboBox=new JComboBox();
		streetAddressTextFields=new JTextField[]{streetAddress1TextField, streetAddress2TextField, streetAddress3TextField};
		setDefaultFocusComponent(streetAddress1TextField);	//set the default focus component
		initialize();	//initialize the panel
		setAddress(address);	//set the address
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		postOfficeBoxLabel.setText("PO Box");	//G***i18n
		postOfficeBoxTextField.setColumns(5);
		streetAddressesLabel.setText("Street Addresses");	//G***i18n
//G***del		streetAddress1Label.setText("Street Address 1");	//G***i18n
		streetAddress1TextField.setColumns(16);
//	G***del		streetAddress2Label.setText("Street Address 2");	//G***i18n
		streetAddress2TextField.setColumns(16);
//	G***del		streetAddress3Label.setText("Street Address 3");	//G***i18n
		streetAddress3TextField.setColumns(16);
		localityLabel.setText("City");	//G***i18n
		localityTextField.setColumns(8);
		regionLabel.setText("State or Province");	//G***i18n
		regionTextField.setColumns(8);
		postalCodeLabel.setText("Postal Code");	//G***i18n
		postalCodeTextField.setColumns(8);
		countryNameLabel.setText("Country");	//G***i18n
		countryNameComboBox.setEditable(true);
		countryNameComboBox.setModel(new DefaultComboBoxModel(LocaleUtilities.getAvailableDisplayCountries()));	//G***i18n
		
		add(addressTypePanel, new GridBagConstraints(0, 1, 1, 8, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));

		add(postOfficeBoxLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(postOfficeBoxTextField, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		add(streetAddressesLabel, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
//G***del		add(streetAddress1Label, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(streetAddress1TextField, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
//G***del		add(streetAddress2Label, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(streetAddress2TextField, new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
//G***del		add(streetAddress3Label, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(streetAddress3TextField, new GridBagConstraints(1, 4, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		add(localityLabel, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(localityTextField, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		add(regionLabel, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(regionTextField, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		add(postalCodeLabel, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(postalCodeTextField, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		add(countryNameLabel, new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(countryNameComboBox, new GridBagConstraints(2, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
	}

}

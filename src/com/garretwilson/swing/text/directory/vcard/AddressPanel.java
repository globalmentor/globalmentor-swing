package com.garretwilson.swing.text.directory.vcard;

import java.awt.Dimension;
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

	/**The street address text pane.*/
	private final JTextPane streetAddressTextPane;

		/**@return The street address text pane.*/
		public JTextPane getStreetAddressTextPane() {return streetAddressTextPane;}

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

	/**The action for selecting the language of the name.*/
	private final SelectLanguageAction selectLanguageAction;

		/**@return The action for selecting the language of the name.*/
		public SelectLanguageAction getSelectLanguageAction() {return selectLanguageAction;}

	/**Places the address information into the various fields.
	@param address The address to place in the fields, or <code>null</code> if no
		information should be displayed.
	*/
	public void setAddress(final Address address)
	{
		if(address!=null)	//if there is a name
		{
			postOfficeBoxTextField.setText(address.getPostOfficeBox()!=null ? address.getPostOfficeBox() : "");
			extendedAddresses=address.getExtendedAddresses();	//save the extended addresses so we won't lose them
			streetAddressTextPane.setText(StringUtilities.concat(address.getStreetAddresses(), '\n'));	//separate the street addresses with newlines and place them in the text pane
			localityTextField.setText(address.getLocality()!=null ? address.getLocality() : "");
			regionTextField.setText(address.getRegion()!=null ? address.getRegion() : "");
			postalCodeTextField.setText(address.getPostalCode()!=null ? address.getPostalCode() : "");
			countryNameComboBox.setSelectedItem(address.getCountryName()!=null ? address.getCountryName() : "");
			addressTypePanel.setAddressType(address.getAddressType());
			selectLanguageAction.setLocale(address.getLocale());
		}
		else	//if there is no address, clear the fields
		{
			postOfficeBoxTextField.setText("");
			streetAddressTextPane.setText("");
			localityTextField.setText("");
			regionTextField.setText("");
			postalCodeTextField.setText("");
			countryNameComboBox.setSelectedItem("");
			addressTypePanel.setAddressType(Address.NO_ADDRESS_TYPE);
			selectLanguageAction.setLocale(null);
		}
	}
	
	/**@return An object representing the address information entered.*/
	public Address getAddress()
	{
		final String postOfficeBox=StringUtilities.getNonEmptyString(postOfficeBoxTextField.getText().trim());
			//trim the string in the street address text field and tokenize the lines
		final String[] streetAddresses=StringTokenizerUtilities.getTokens(new StringTokenizer(streetAddressTextPane.getText().trim(), "\r\n"));
		final String locality=StringUtilities.getNonEmptyString(localityTextField.getText().trim());
		final String region=StringUtilities.getNonEmptyString(regionTextField.getText().trim());
		final String postalCode=StringUtilities.getNonEmptyString(postalCodeTextField.getText().trim());
		final String countryName=StringUtilities.getNonEmptyString(countryNameComboBox.getSelectedItem().toString().trim());
		final int addressType=addressTypePanel.getAddressType();
		final Locale locale=selectLanguageAction.getLocale();
		return new Address(postOfficeBox, extendedAddresses, streetAddresses, locality, region, postalCode, countryName, addressType, locale);	//create and return an address representing the entered information
	}

	/**Default constructor.*/
	public AddressPanel()
	{
		this(new Address());	//initialize with a default address
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
		selectLanguageAction=new SelectLanguageAction(null, this);
		streetAddressTextPane=new JTextPane();
		localityLabel=new JLabel();
		localityTextField=new JTextField();
		regionLabel=new JLabel();
		regionTextField=new JTextField();
		postalCodeLabel=new JLabel();
		postalCodeTextField=new JTextField();
		countryNameLabel=new JLabel();
		countryNameComboBox=new JComboBox();
		setDefaultFocusComponent(streetAddressTextPane);	//set the default focus component
		initialize();	//initialize the panel
		setAddress(address);	//set the address
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		postOfficeBoxLabel.setText("PO Box");	//G***i18n
		postOfficeBoxTextField.setColumns(5);
		streetAddressesLabel.setText("Street Address");	//G***i18n
			//TODO turn off tab-handling for streetAddressTextPane
//G***del; fix; this doesn't work		streetAddressTextPane.setFocusTraversalKeysEnabled(true);
		localityLabel.setText("City");	//G***i18n
		localityTextField.setColumns(8);
//G***fix		streetAddressTextPane.setMinimumSize(new Dimension(streetAddressTextPane.getMinimumSize().width, localityTextField.getPreferredSize().height*3));
//G***fix		streetAddressTextPane.setPreferredSize(streetAddressTextPane.getMinimumSize());

//G***del		streetAddressTextPane.setBorder(localityTextField.getBorder());
		regionLabel.setText("State or Province");	//G***i18n
		regionTextField.setColumns(8);
		postalCodeLabel.setText("Postal Code");	//G***i18n
		postalCodeTextField.setColumns(8);
		postalCodeTextField.setText("G***del");
		countryNameLabel.setText("Country");	//G***i18n
		countryNameComboBox.setEditable(true);
		countryNameComboBox.setModel(new DefaultComboBoxModel(LocaleUtilities.getAvailableDisplayCountries()));	//G***i18n

		final JScrollPane streetAddressScrollPane=new JScrollPane(streetAddressTextPane);
		streetAddressScrollPane.setMinimumSize(new Dimension(streetAddressScrollPane.getMinimumSize().width, localityTextField.getPreferredSize().height*3));
		streetAddressScrollPane.setPreferredSize(streetAddressScrollPane.getMinimumSize());

		final JButton selectLanguageButton=new JButton(getSelectLanguageAction());
		selectLanguageButton.setText("");	//TODO create common routine for this
		selectLanguageButton.setBorder(null);

		//TODO move everything up a row if we leave the address type panel at the bottom

		add(postOfficeBoxLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(postOfficeBoxTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, NO_INSETS, 0, 0));
		add(streetAddressesLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(selectLanguageButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(streetAddressScrollPane, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, NO_INSETS, 0, 0));
		add(localityLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(localityTextField, new GridBagConstraints(0, 5, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, NO_INSETS, 0, 0));
		add(regionLabel, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(regionTextField, new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, NO_INSETS, 0, 0));
		add(postalCodeLabel, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(postalCodeTextField, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, NO_INSETS, 0, 0));
		add(countryNameLabel, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(countryNameComboBox, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, NO_INSETS, 0, 0));

		add(addressTypePanel, new GridBagConstraints(0, 8, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
//G***del when works		add(addressTypePanel, new GridBagConstraints(3, 2, 1, 7, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, NO_INSETS, 0, 0));

	}

}

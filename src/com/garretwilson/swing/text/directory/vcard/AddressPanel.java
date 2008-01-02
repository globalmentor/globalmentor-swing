package com.garretwilson.swing.text.directory.vcard;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.util.*;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import com.garretwilson.text.directory.vcard.*;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.swing.*;
import com.garretwilson.util.*;
import com.globalmentor.java.*;

/**A panel allowing entry of the "ADR" type of a vCard <code>text/directory</code>
	profile as defined in <a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class AddressPanel extends BasicVCardPanel
{

	/**The action for editing the address type.*/
	private final Action editAddressTypeAction;

		/**@return The action for editing the address type.*/
		public Action getEditAddressTypeAction() {return editAddressTypeAction;}
	
	/**The address type button.*/
	private final JButton addressTypeButton;

	/**The local copy of the address type.*/
	private int addressType;

		/**@return The delivery address type, a combination of
			<code>Address.XXX_ADDRESS_TYPE</code> constants ORed together.
		*/
		protected int getAddressType() {return addressType;}

		/**Sets the address type.
		@param addressType The delivery address type, one or more of the
			<code>Address.XXX_ADDRESS_TYPE</code> constants ORed together.
		*/
		protected void setAddressType(final int addressType)
		{
			final int oldAddressType=this.addressType;	//get the old address type
			if(oldAddressType!=addressType)	//if the address type is really changing
			{
				this.addressType=addressType;	//store the address type locally
				setModified(true);	//show that we've changed the address type
				addressTypeButton.setText(	//update the address type button
						addressType!=Address.NO_ADDRESS_TYPE	//if there is an address type
						? Address.getAddressTypeString(addressType)	//show it
						: "");	//if there is no address type, show nothing
			}
		}

	/**The panel allowing selection of the address type.*/
//G***del	private final AddressTypePanel addressTypePanel;

		/**@return The panel allowing selection of the address type.*/
//G***del		public AddressTypePanel getAddressTypePanel() {return addressTypePanel;}

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
		if(address!=null)	//if there is an address
		{
			postOfficeBoxTextField.setText(address.getPostOfficeBox()!=null ? address.getPostOfficeBox() : "");
			extendedAddresses=address.getExtendedAddresses();	//save the extended addresses so we won't lose them
			streetAddressTextPane.setText(Strings.concat(address.getStreetAddresses(), '\n'));	//separate the street addresses with newlines and place them in the text pane
			localityTextField.setText(address.getLocality()!=null ? address.getLocality() : "");
			regionTextField.setText(address.getRegion()!=null ? address.getRegion() : "");
			postalCodeTextField.setText(address.getPostalCode()!=null ? address.getPostalCode() : "");
			countryNameComboBox.setSelectedItem(address.getCountryName()!=null ? address.getCountryName() : "");
			setAddressType(address.getAddressType());
//G***del			addressTypePanel.setAddressType(address.getAddressType());
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
			setAddressType(Address.DEFAULT_ADDRESS_TYPE);
//G***del			addressTypePanel.setAddressType(Address.NO_ADDRESS_TYPE);
			selectLanguageAction.setLocale(null);
		}
	}
	
	/**@return An object representing the address information entered, or
		<code>null</code> if no address was entered.
	*/
	public Address getAddress()
	{
		final String postOfficeBox=Strings.getNonEmptyString(postOfficeBoxTextField.getText().trim());
			//trim the string in the street address text field and tokenize the lines
		final String[] streetAddresses=StringTokenizerUtilities.getTokens(new StringTokenizer(streetAddressTextPane.getText().trim(), "\r\n"));
		final String locality=Strings.getNonEmptyString(localityTextField.getText().trim());
		final String region=Strings.getNonEmptyString(regionTextField.getText().trim());
		final String postalCode=Strings.getNonEmptyString(postalCodeTextField.getText().trim());
		final String countryName=Strings.getNonEmptyString(countryNameComboBox.getSelectedItem().toString().trim());
		final int addressType=getAddressType();
		final Locale locale=selectLanguageAction.getLocale();
			//if address information was entered
		if(postOfficeBox!=null || streetAddresses.length>0 || locality!=null || region!=null || postalCode!=null || countryName!=null)
		{
			return new Address(postOfficeBox, extendedAddresses, streetAddresses, locality, region, postalCode, countryName, addressType, locale);	//create and return an address representing the entered information
		}
		else	//if no address information was entered
		{
			return null;	//show that there was no address
		}
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
		editAddressTypeAction=new EditAddressTypeAction();
		addressTypeButton=new JButton(getEditAddressTypeAction());
		addressTypeButton.setHorizontalTextPosition(SwingConstants.LEFT);
//G***fix		addressTypeButton.setBorder(null);
//G***del		addressTypePanel=new AddressTypePanel();
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
		setIcon(IconResources.getIcon(IconResources.MAIL_ICON_FILENAME));
		setDefaultFocusComponent(streetAddressTextPane);	//set the default focus component
		initialize();	//initialize the panel
		setAddress(address);	//set the address
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		final PropertyChangeListener modifyLocalePropertyChangeListener=createModifyPropertyChangeListener(LocaleConstants.LOCALE_PROPERTY_NAME);	//create a property change listener to change the modified status when the locale property changes
		postOfficeBoxLabel.setText("PO Box");	//G***i18n
		postOfficeBoxTextField.setColumns(5);
		postOfficeBoxTextField.getDocument().addDocumentListener(getModifyDocumentListener());
		streetAddressesLabel.setText("Street Address");	//G***i18n
			//TODO turn off tab-handling for streetAddressTextPane
//G***del; fix; this doesn't work		streetAddressTextPane.setFocusTraversalKeysEnabled(true);
		streetAddressTextPane.getDocument().addDocumentListener(getModifyDocumentListener());
		localityLabel.setText("City");	//G***i18n
		localityTextField.setColumns(8);
		localityTextField.getDocument().addDocumentListener(getModifyDocumentListener());
//G***fix		streetAddressTextPane.setMinimumSize(new Dimension(streetAddressTextPane.getMinimumSize().width, localityTextField.getPreferredSize().height*3));
//G***fix		streetAddressTextPane.setPreferredSize(streetAddressTextPane.getMinimumSize());

//G***del		streetAddressTextPane.setBorder(localityTextField.getBorder());
		regionLabel.setText("State or Province");	//G***i18n
		regionTextField.setColumns(8);
		regionTextField.getDocument().addDocumentListener(getModifyDocumentListener());
		postalCodeLabel.setText("Postal Code");	//G***i18n
		postalCodeTextField.setColumns(8);
		postalCodeTextField.setText("G***del");
		postalCodeTextField.getDocument().addDocumentListener(getModifyDocumentListener());
		countryNameLabel.setText("Country");	//G***i18n
		countryNameComboBox.setEditable(true);
		countryNameComboBox.setModel(new DefaultComboBoxModel(LocaleUtilities.getAvailableDisplayCountries()));	//G***i18n
		countryNameComboBox.addActionListener(getModifyActionListener());
		final JScrollPane streetAddressScrollPane=new JScrollPane(streetAddressTextPane);
			//TODO fix this with a derived text pane that is scrollable and allows tracksViewport... to be set
		streetAddressScrollPane.setMinimumSize(new Dimension(streetAddressScrollPane.getMinimumSize().width, localityTextField.getPreferredSize().height*3));
		streetAddressScrollPane.setPreferredSize(streetAddressScrollPane.getMinimumSize());

		getSelectLanguageAction().addPropertyChangeListener(modifyLocalePropertyChangeListener);
		final JButton selectLanguageButton=createSelectLanguageButton(getSelectLanguageAction());

		//TODO move everything up a row if we leave the address type panel at the bottom

		add(addressTypeButton, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, NO_INSETS, 0, 0));


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

//G***del		add(addressTypePanel, new GridBagConstraints(0, 8, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
//G***del when works		add(addressTypePanel, new GridBagConstraints(3, 2, 1, 7, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, NO_INSETS, 0, 0));

	}

	/**Asks the user for a new delivery address type and updates the value.
	@return <code>true</code> if the user accepted the changes and the type was
		updated, otherwise <code>false</code> if the user cancelled.
	*/
	public boolean editAddressType()
	{
		final AddressTypePanel addressTypePanel=new AddressTypePanel(getAddressType());	//create a new panel with our current address type 
			//ask for the new address type; if they accept the changes
		if(BasicOptionPane.showConfirmDialog(this, addressTypePanel, "Delivery Address Type", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)	//G***i18n
		{
			setAddressType(addressTypePanel.getAddressType());	//update the address type
			return true;	//show that the user accepted the changes and that they were updated		
		}
		else	//if the user cancels
		{
			return false;	//show that the action was cancelled
		}
	}

	/**Action for editing the delivery address type.*/
	class EditAddressTypeAction extends AbstractAction
	{
		/**Default constructor.*/
		public EditAddressTypeAction()
		{
			super("Type");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Edit type");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Edit the delivery address type for this address.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_T));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.PROPERTY_ICON_FILENAME)); //load the correct icon
		}
	
		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			editAddressType();	//edit the address type
		}
	}

}

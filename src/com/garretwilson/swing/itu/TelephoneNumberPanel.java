package com.garretwilson.swing.itu;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import com.garretwilson.itu.*;
import com.garretwilson.swing.*;
import com.garretwilson.util.*;

/**A panel allowing entry of an international public telecommunication number
	for geographic areas as defined in ITU-T E.164,
	"The international public telecommunication numbering plan".
	The telephone number is formatted according to ITU-T E.123,
	"Notation for national and international telephone numbers, e-mail addresses
	and Web addresses".
@author Garret Wilson
*/
public class TelephoneNumberPanel extends BasicPanel implements Verifiable
{

	/**The label of the country code.*/
	private final JLabel countryCodeLabel;

	/**The country code combo box.*/
	private final JComboBox countryCodeComboBox;

		/**@return The country code combo box.*/
		public JComboBox getCountryCodeComboBox() {return countryCodeComboBox;}

	/**The label of the national destination code.*/
	private final JLabel nationalDestinationCodeLabel;

	/**The national destination code text field.*/
	private final JTextField nationalDestinationCodeTextField;

		/**@return The national destination code text field.*/
		public JTextField getNationalDestinationCodeTextField() {return nationalDestinationCodeTextField;}

	/**The label of the subscriber number.*/
	private final JLabel subscriberNumberLabel;

	/**The subscriber number text field.*/
	private final JTextField subscriberNumberTextField;

		/**@return The subscriber number text field.*/
		public JTextField getSubscriberNumberCodeTextField() {return subscriberNumberTextField;}

	/**Shows or hides the telphone number labels.
	@param visible <code>true</code> if the labels should be shown,
		<code>false</code> if they should be hidden.
	*/
	public void setLabelsVisible(final boolean visible)
	{
		countryCodeLabel.setVisible(visible);
		nationalDestinationCodeLabel.setVisible(visible);
		subscriberNumberLabel.setVisible(visible);
	}
	
	/**@return Whether all the telephone number labels are visible.*/ 
	public boolean isLabelsVisible()
	{
		return countryCodeLabel.isVisible() && nationalDestinationCodeLabel.isVisible() && subscriberNumberLabel.isVisible();
	}

	/**Places the telephone number into the various fields.
	@param telephoneNumber The telephone number to place in the fields, or
		<code>null</code> if default information should be displayed.
	*/
	public void setTelephoneNumber(final TelephoneNumber telephoneNumber)
	{
		if(telephoneNumber!=null)	//if there is a telephone number
		{
			countryCodeComboBox.setSelectedItem(telephoneNumber.getCountryCode());
			nationalDestinationCodeTextField.setText(telephoneNumber.getNationalDestinationCode());
			subscriberNumberTextField.setText(telephoneNumber.getSubscriberNumber());
		}
		else	//if there is no telephone number, clear the fields
		{
			countryCodeComboBox.setSelectedItem("1");	//G***i18n; fix in conjunction with com.garretwilson.itu.CountryCode
			nationalDestinationCodeTextField.setText("");
			subscriberNumberTextField.setText("");
		}
	}
	
	/**@return An object representing the telephone number information entered,
		or <code>null</code> if no telephone number was entered or the values
		violate ITU-T E.164.
	*/
	public TelephoneNumber getTelephoneNumber()
	{
		final String countryCode=((String)countryCodeComboBox.getSelectedItem()).trim();
		final String nationalDestinationCode=nationalDestinationCodeTextField.getText().trim();
		final String subscriberNumber=subscriberNumberTextField.getText().trim();
		if(countryCode.length()>0 && (nationalDestinationCode.length()>0 || subscriberNumber.length()>0))	//if a country code was given, along with information in either of the other fields
		{
			try
			{
				return new TelephoneNumber(countryCode, nationalDestinationCode, subscriberNumber);	//create and return a telephone number representing the entered information
			}
			catch(TelephoneNumberSyntaxException telephoneNumberSyntaxException)	//if the information isn't a valid telephone number
			{
				return null;	//show that we don't understand the entered information
			}
		}
		else	//if no information was given in any of the fields
		{
			return null;	//show that we don't have a telephone number
		}
	}

	/**Default constructor.*/
	public TelephoneNumberPanel()
	{
		this(null);	//construct a panel with no telephone number	
	}

	/**Telephone number constructor.
	@param telephoneNumber The telephone number to place in the fields, or
		<code>null</code> if default information should be displayed.
	*/
	public TelephoneNumberPanel(final TelephoneNumber telephoneNumber)
	{

		super(new GridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		countryCodeLabel=new JLabel();
		countryCodeComboBox=new JComboBox();
		nationalDestinationCodeLabel=new JLabel();
		nationalDestinationCodeTextField=new JTextField();
		subscriberNumberLabel=new JLabel();
		subscriberNumberTextField=new JTextField();
		setDefaultFocusComponent(nationalDestinationCodeTextField);	//set the default focus component
		initialize();	//initialize the panel
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		final DocumentListener modifyDocumentListener=createModifyDocumentListener();	//create a document listener to change the modified status when the document is modified
		final ActionListener modifyActionListener=createModifyActionListener();	//create an action listener to change the modified status upon an action
		countryCodeLabel.setText("Country");	//G***i18n
		countryCodeComboBox.setEditable(true);
		countryCodeComboBox.setPrototypeDisplayValue("000");	//G***testing
//G***gix		honorificPrefixComboBox.setModel(new DefaultComboBoxModel(HONORIFIC_PREFIX_EXAMPLES));	//set up the example honorific prefixes
		countryCodeComboBox.addActionListener(modifyActionListener);
		nationalDestinationCodeLabel.setText("Area Code");	//G***i18n
		nationalDestinationCodeTextField.setColumns(8);
		nationalDestinationCodeTextField.getDocument().addDocumentListener(modifyDocumentListener);
		subscriberNumberLabel.setText("Number");	//G***i18n
		subscriberNumberTextField.setColumns(10);
		subscriberNumberTextField.getDocument().addDocumentListener(modifyDocumentListener);
//G***del when works		final JLabel imageLabel=new JLabel(IconResources.getIcon(IconResources.PHONE_ICON_FILENAME)); //create a label with the image		
//G***del when works		add(imageLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(countryCodeLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(countryCodeComboBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(nationalDestinationCodeLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(nationalDestinationCodeTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(subscriberNumberLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(subscriberNumberTextField, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		setTelephoneNumber(null);	//clear the fields
	}

	/**Verifies the component.
	@return <code>true</code> if the component contents are valid, <code>false</code>
		if not.
	*/
	public boolean verify()
	{
		final String countryCode=((String)countryCodeComboBox.getSelectedItem()).trim();
		final String nationalDestinationCode=nationalDestinationCodeTextField.getText().trim();
		final String subscriberNumber=subscriberNumberTextField.getText().trim();
		if(countryCode.length()>0 && (nationalDestinationCode.length()>0 || subscriberNumber.length()>0))	//if a country code was given, along with information in either of the other fields
		{
			try
			{
				new TelephoneNumber(countryCode, nationalDestinationCode, subscriberNumber);	//try to create a telephone number representing the entered information
			}
			catch(TelephoneNumberSyntaxException telephoneNumberSyntaxException)	//if the information isn't a valid telephone number
			{
				JOptionPane.showMessageDialog(this, "The telephone number you entered is invalid: "+telephoneNumberSyntaxException.getMessage(), "Invalid telephone number", JOptionPane.ERROR_MESSAGE);	//G***i18n
				nationalDestinationCodeTextField.requestFocusInWindow(); //focus on part of the telephone number text field
				return false; //show that verification failed
			}
		}
		return true;  //if we couldn't find any problems, verification succeeded
	}
}

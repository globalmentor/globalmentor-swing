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

package com.globalmentor.swing.itu;

import java.awt.*;
import javax.swing.*;
import com.globalmentor.awt.BasicGridBagLayout;
import com.globalmentor.awt.Containers;
import com.globalmentor.itu.*;
import com.globalmentor.swing.*;
import com.globalmentor.text.ArgumentSyntaxException;

/**A panel allowing entry of an international public telecommunication number
	for geographic areas as defined in ITU-T E.164,
	"The international public telecommunication numbering plan".
	The telephone number is formatted according to ITU-T E.123,
	"Notation for national and international telephone numbers, e-mail addresses
	and Web addresses".
@author Garret Wilson
*/
public class TelephoneNumberPanel extends ModifiablePanel
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
			countryCodeComboBox.setSelectedItem(telephoneNumber.getCCString());
			nationalDestinationCodeTextField.setText(telephoneNumber.getNDCString());
			subscriberNumberTextField.setText(telephoneNumber.getSNString());
		}
		else	//if there is no telephone number, clear the fields
		{
			countryCodeComboBox.setSelectedItem("1");	//TODO i18n; fix in conjunction with com.globalmentor.itu.CountryCode
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
			catch(final ArgumentSyntaxException SyntaxException)	//if the information isn't a valid telephone number
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
		super(new BasicGridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		countryCodeLabel=new JLabel();
		countryCodeComboBox=new JComboBox();
		nationalDestinationCodeLabel=new JLabel();
		nationalDestinationCodeTextField=new JTextField();
		subscriberNumberLabel=new JLabel();
		subscriberNumberTextField=new JTextField();
		setDefaultFocusComponent(nationalDestinationCodeTextField);	//set the default focus component
		initialize();	//initialize the panel
		setTelephoneNumber(telephoneNumber);	//set the telephone number
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		countryCodeLabel.setText("Country");	//TODO i18n
		countryCodeComboBox.setEditable(true);
		countryCodeComboBox.setPrototypeDisplayValue("000");	//TODO testing
//TODO gix		honorificPrefixComboBox.setModel(new DefaultComboBoxModel(HONORIFIC_PREFIX_EXAMPLES));	//set up the example honorific prefixes
		countryCodeComboBox.addActionListener(getModifyActionListener());
		nationalDestinationCodeLabel.setText("Area Code");	//TODO i18n
		nationalDestinationCodeTextField.setColumns(8);
		nationalDestinationCodeTextField.getDocument().addDocumentListener(getModifyDocumentListener());
		subscriberNumberLabel.setText("Number");	//TODO i18n
		subscriberNumberTextField.setColumns(10);
		subscriberNumberTextField.getDocument().addDocumentListener(getModifyDocumentListener());
//TODO del when works		final JLabel imageLabel=new JLabel(IconResources.getIcon(IconResources.PHONE_ICON_FILENAME)); //create a label with the image		
//TODO del when works		add(imageLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(countryCodeLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(countryCodeComboBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Containers.NO_INSETS, 0, 0));
		add(nationalDestinationCodeLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(nationalDestinationCodeTextField, new GridBagConstraints(1, 1, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Containers.NO_INSETS, 0, 0));
		add(subscriberNumberLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(subscriberNumberTextField, new GridBagConstraints(2, 1, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Containers.NO_INSETS, 0, 0));
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
		if(nationalDestinationCode.length()>0 || subscriberNumber.length()>0)	//if any part of the telephone number besides the country code was given
		{
			try
			{
				new TelephoneNumber(countryCode, nationalDestinationCode, subscriberNumber);	//try to create a telephone number representing the entered information
			}
			catch(final ArgumentSyntaxException syntaxException)	//if the information isn't a valid telephone number
			{
				JOptionPane.showMessageDialog(this, "The telephone number you entered is invalid: "+syntaxException.getMessage(), "Invalid telephone number", JOptionPane.ERROR_MESSAGE);	//TODO i18n
				nationalDestinationCodeTextField.requestFocusInWindow(); //focus on part of the telephone number text field
				return false; //show that verification failed
			}
		}
		return super.verify();  //if we couldn't find any problems, verify the parent class
	}
}

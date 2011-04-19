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

import static com.globalmentor.collections.Sets.*;

import java.awt.*;
import java.util.EnumSet;
import java.util.Set;

import javax.swing.*;
import com.globalmentor.awt.BasicGridBagLayout;
import com.globalmentor.awt.Containers;
import com.globalmentor.java.*;
import com.globalmentor.swing.*;
import com.globalmentor.text.directory.vcard.*;

/**A panel containing fields for the <code>EMAIL</code> type of a vCard
	<code>text/directory</code>	profile as defined in
	<a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class EmailPanel extends ModifiablePanel
{

	/**The email text field.*/
	private final JTextField addressTextField;
	
	/**The local copy of the email types.*/
	private Set<Email.Type> emailTypes=EnumSet.noneOf(Email.Type.class);

		/**@return The email addressing types.*/
		protected Set<Email.Type> getEmailTypes() {return emailTypes;}

		/**Sets the email types.
		@param emailTypes The email addressing types.
		*/
		protected void setEmailTypes(final Set<Email.Type> emailTypes)
		{
			this.emailTypes=immutableSetOf(emailTypes);	//store the email type locally
		}

	/**Shows or hides the telephone number labels.
	@param visible <code>true</code> if the labels should be shown,
		<code>false</code> if they should be hidden.
	@see TelephoneNumberPanel#setLabelsVisible
	*/
	public void setLabelsVisible(final boolean visible)
	{
//TODO fix		telephoneNumberPanel.setLabelsVisible(visible);	//pass the request on to the telephone number panel
	}
	
	/**@return Whether all the telephone number labels are visible. 
	@see TelephoneNumberPanel#isLabelsVisible
	*/
/*TODO fix
	public boolean isLabelsVisible()
	{
		return telephoneNumberPanel.isLabelsVisible();	//return the answer of the telephone number panel
	}
*/

	/**Places the email address information into the various fields.
	@param emailAddress The email address number to place in the fields, or
		<code>null</code> if default information should be displayed.
	*/
	public void setEmailAddress(final String emailAddress)
	{
		addressTextField.setText(emailAddress);
	}

	/**Places the email information into the various fields.
	@param email The email information to place in the fields, or
		<code>null</code> if default information should be displayed.
	*/
	public void setEmail(final Email email)
	{
		if(email!=null)	//if there is telephone information
		{
			setEmailAddress(email.getAddress());	//set the email address
			setEmailTypes(email.getTypes());	//set and update the email type
		}
		else	//if there is no email information, clear the fields
		{
			setEmailAddress("");	//clear the email address
			setEmailTypes(EnumSet.of(Email.DEFAULT_TYPE));	//set the default email type
		}
	}
	
	/**@return An object representing the email information entered, or
		<code>null</code> if no email information was entered.
	*/
	public Email getEmail()
	{
		final String emailAddress=Strings.getNonEmptyString(addressTextField.getText().trim());	//get the email address number from the panel
		if(emailAddress!=null)	//if an email address was given
		{
			return new Email(emailAddress, getEmailTypes());	//create and return email information representing the entered information
		}
		else	//if no email address was given
		{
			return null;	//there's no email information to return
		}
	}

	/**Default constructor.*/
	public EmailPanel()
	{
		this((Email)null);	//construct a default telephone panel
	}

	/**Email constructor for a default telephone type of {@value Email#DEFAULT_TYPE}.
	@param email The email information to place in the fields, or
		<code>null</code> if default information should be displayed.
	*/
	public EmailPanel(final Email email)
	{
		this(email!=null ? email.getAddress() : null, EnumSet.of(Email.DEFAULT_TYPE)); 
	}

	/**Email address and type constructor for a default telephone type of {@value Email#DEFAULT_TYPE}.
	@param emailAddress The email address to place in the fields, or
		<code>null</code> if default information should be displayed.
	@param emailTypess The email address types.
	*/
	public EmailPanel(final String emailAddress)
	{
		this(emailAddress, EnumSet.of(Email.DEFAULT_TYPE));
	}
	
	/**Email address and type constructor.
	@param emailAddress The email address to place in the fields, or
		<code>null</code> if default information should be displayed.
	@param emailTypess The email address types.
	*/
	public EmailPanel(final String emailAddress, final Set<Email.Type> emailTypes)
	{
		super(new BasicGridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
//TODO del		editTelephoneTypeAction=new EditTelephoneTypeAction();
		addressTextField=new JTextField();
		setDefaultFocusComponent(addressTextField);	//set the default focus component
		initialize();	//initialize the panel
		setEmailAddress(emailAddress);	//set the given email address
		setEmailTypes(emailTypes);	//set the given email type
		setModified(false);	//show that the information has not yet been modified
	}

	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		addressTextField.setColumns(20);
		addressTextField.getDocument().addDocumentListener(getModifyDocumentListener());
		final JLabel imageLabel=new JLabel(IconResources.getIcon(IconResources.EMAIL_ICON_FILENAME)); //create a label with the image		
		add(imageLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(addressTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Containers.NO_INSETS, 0, 0));
	}
	
}

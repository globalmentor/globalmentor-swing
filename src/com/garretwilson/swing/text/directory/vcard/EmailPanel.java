package com.garretwilson.swing.text.directory.vcard;

import java.awt.*;
import javax.swing.*;
import com.garretwilson.awt.BasicGridBagLayout;
import com.garretwilson.lang.*;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.swing.*;
import com.garretwilson.text.directory.vcard.*;

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
	
	/**The local copy of the email type.*/
	private int emailType;

		/**@return The email addressing type, a combination of
			<code>Email.XXX_EMAIL_TYPE</code> constants ORed together.
		*/
		protected int getEmailType() {return emailType;}

		/**Sets the email type.
		@param emailType The email addressing type, one or more of the
			<code>Email.XXX_EMAIL_TYPE</code> constants ORed together.
		*/
		protected void setEmailType(final int emailType)
		{
			this.emailType=emailType;	//store the email type locally
		}

	/**Shows or hides the telphone number labels.
	@param visible <code>true</code> if the labels should be shown,
		<code>false</code> if they should be hidden.
	@see TelephoneNumberPanel#setLabelsVisible
	*/
	public void setLabelsVisible(final boolean visible)
	{
//G***fix		telephoneNumberPanel.setLabelsVisible(visible);	//pass the request on to the telephone number panel
	}
	
	/**@return Whether all the telephone number labels are visible. 
	@see TelephoneNumberPanel#isLabelsVisible
	*/
/*G***fix
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
			setEmailType(email.getEmailType());	//set and update the email type
		}
		else	//if there is no email information, clear the fields
		{
			setEmailAddress("");	//clear the email address
			setEmailType(Email.DEFAULT_EMAIL_TYPE);	//set the default email type
		}
	}
	
	/**@return An object representing the email information entered, or
		<code>null</code> if no email information was entered.
	*/
	public Email getEmail()
	{
		final String emailAddress=StringUtilities.getNonEmptyString(addressTextField.getText().trim());	//get the email address number from the panel
		if(emailAddress!=null)	//if an email address was given
		{
			final int emailType=getEmailType();	//get the email type
			return new Email(emailAddress, emailType);	//create and return email information representing the entered information
		}
		else	//if no email address was given
		{
			return null;	//there's no email information to return
		}
	}

	/**Default constructor.*/
	public EmailPanel()
	{
		this(null);	//construct a default telephone panel
	}

	/**Email constructor for a defualt telephone type of
		<code>Email.INTERNET_EMAIL_TYPE</code>.
	@param email The email information to place in the fields, or
		<code>null</code> if default information should be displayed.
	*/
	public EmailPanel(final Email email)
	{
		this(email!=null ? email.getAddress() : null, Email.INTERNET_EMAIL_TYPE); 
	}

	/**Email address and type constructor.
	@param emailAddress The email address to place in the fields, or
		<code>null</code> if default information should be displayed.
	@param emailType The email address type, one or more of the
		<code>Email.XXX_EMAIL_TYPE</code> constants ORed together.
	*/
	public EmailPanel(final String emailAddress, final int emailType)
	{
		super(new BasicGridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
//G***del		editTelephoneTypeAction=new EditTelephoneTypeAction();
		addressTextField=new JTextField();
		setDefaultFocusComponent(addressTextField);	//set the default focus component
		initialize();	//initialize the panel
		setEmailAddress(emailAddress);	//set the given email address
		setEmailType(emailType);	//set the given email type
		setModified(false);	//show that the information has not yet been modified
	}

	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		addressTextField.setColumns(20);
		addressTextField.getDocument().addDocumentListener(getModifyDocumentListener());
		final JLabel imageLabel=new JLabel(IconResources.getIcon(IconResources.EMAIL_ICON_FILENAME)); //create a label with the image		
		add(imageLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(addressTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, NO_INSETS, 0, 0));
	}
	
}

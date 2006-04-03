package com.garretwilson.swing;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.*;
import com.garretwilson.awt.BasicGridBagLayout;
import static com.garretwilson.lang.ObjectUtilities.*;

/**Allows entry and optional verification of a password.
@author Garret Wilson
*/
public class UserPasswordPanel extends BasicPanel
{

	/**An object specifying insets for labels.*/
	private final static Insets LABEL_INSETS=new Insets(0, 0, 0, 4);
	/**An object specifying insets for fields.*/
	private final static Insets FIELD_INSETS=new Insets(0, 0, 8, 0);

	/**The label of the username entry.*/
	protected final JLabel usernameLabel;
	/**The username text field.*/
	protected final JTextField usernameField;

		/**@return The current username value.*/
		public String getUsername() {return usernameField.getText();}

		/**Sets the current username value.
		@param username The new username value.
		@exception NullPointerException if the given username is <code>null</code>.
		*/
		public void setUsername(final String username) {usernameField.setText(checkInstance(username, "User name cannot be null."));}

	/**The panel for password input.*/
	protected final PasswordPanel passwordPanel;

		/**@return The current password value.*/
		public char[] getPassword() {return passwordPanel.getPassword();}

	/**Whether the entered password should be verified; defaults to <code>false</code>.*/
	private boolean verifyPassword;

		/**@return Whether the entered password should be verified; defaults to <code>false</code>.*/
		public boolean isVerifyPassword() {return passwordPanel.isVerifyPassword();}

		/**Specifies whether the entered password should be verified.
		@param verify Whether the entered password should be verified.
		*/
		public void setVerifyPassword(final boolean verify) {passwordPanel.setVerifyPassword(verify);}

	/**Whether the username should be editable; defaults to <code>true</code>.*/
	private boolean usernameEditable;

		/**@return Whether the username should be editable; defaults to <code>true</code>.*/
		public boolean isUsernameEditable() {return usernameEditable;}

		/**Sets whether the username should be editable.
		@param editable <code>true</code> if the user is allowed to edit the username.
		*/
		public void setUsernameEditable(final boolean editable)
		{
			usernameField.setEditable(editable);
			setDefaultFocusComponent(editable ? usernameField : passwordPanel);	//set the default focus component depending on whether the username is editable
		}

	/**Default constructor with no password verification.*/
	public UserPasswordPanel()
	{
		this(false);	//construct the panel with no password verification
	}

	/**Verification constructor.
	@param verify The <code>true</code> if a password verification entry field
		should be provided.
	*/
	public UserPasswordPanel(final boolean verify)
	{
		super(new BasicGridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		usernameLabel=new JLabel();
		usernameField=new JTextField();
		passwordPanel=new PasswordPanel();
		setDefaultFocusComponent(usernameField);	//set the default focus component
		initialize();	//initialize the panel
		setUsernameEditable(true);	//default to allowing username editing
		setVerifyPassword(verify);	//set whether we should verify the password
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		usernameLabel.setText("User");	//G***i18n
		usernameField.setColumns(16);
		add(usernameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, LABEL_INSETS, 0, 0));
		add(usernameField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, FIELD_INSETS, 0, 0));
		add(passwordPanel, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, LABEL_INSETS, 0, 0));
	}

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
/*G***del
	public void updateStatus()
	{
		super.updateStatus();	//do the default updating
		passwordVerificationLabel.setVisible(isVerifyPassword());	//only show the password verification if we should verify the password
		passwordVerificationField.setVisible(isVerifyPassword());	//only show the password verification if we should verify the password
	}
*/

	/**Verifies the component.
	@return <code>true</code> if the component contents are valid, <code>false</code>
		if not.
	*/
/*G***del
	public boolean verify()
	{
			//if we should verify password and the passwords don't match
		if(isVerifyPassword() && !Arrays.equals(passwordField.getPassword(), passwordVerificationField.getPassword()))
		{
			JOptionPane.showMessageDialog(this, "Please verify the entered password by entering the same password in both fields.", "Invalid user", JOptionPane.ERROR_MESSAGE);	//G***i18n
			passwordVerificationField.requestFocusInWindow(); //focus on the password verificiation field
			return false; //show that verification failed			
		}
		return super.verify();  //if we couldn't find any problems, verify the parent class
	}
*/
}

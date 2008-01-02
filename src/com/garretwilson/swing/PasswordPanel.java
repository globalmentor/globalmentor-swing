package com.garretwilson.swing;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Arrays;
import javax.swing.*;
import com.garretwilson.awt.BasicGridBagLayout;

import static com.globalmentor.java.Objects.*;

/**Allows entry and optional verification of a password.
@author Garret Wilson
*/
public class PasswordPanel extends BasicPanel
{

	/**An object specifying insets for labels.*/
	private final static Insets LABEL_INSETS=new Insets(0, 0, 0, 4);
	/**An object specifying insets for fields.*/
	private final static Insets FIELD_INSETS=new Insets(0, 0, 8, 0);

	/**The label of the password entry.*/
	protected final JLabel passwordLabel;
	/**The password text field.*/
	protected final JPasswordField passwordField;

		/**@return The current password value.*/
		public char[] getPassword() {return passwordField.getPassword();}

	/**The label of the password verifications entry.*/
	protected final JLabel passwordVerificationLabel;
	/**The password verification text field.*/
	protected final JPasswordField passwordVerificationField;

	/**Whether the entered password should be verified; defaults to <code>false</code>.*/
	private boolean verifyPassword;

		/**@return Whether the entered password should be verified; defaults to <code>false</code>.*/
		public boolean isVerifyPassword() {return verifyPassword;}

		/**Specifies whether the entered password should be verified.
		@param verify Whether the entered password should be verified.
		*/
		public void setVerifyPassword(final boolean verify)
		{
			verifyPassword=verify;	//update the password verification
			updateStatus();	//show or hide the password verification
		}

	/**Default constructor with no password verification.*/
	public PasswordPanel()
	{
		this(false);	//construct the panel with no password verification
	}

	/**Verification constructor.
	@param verify The <code>true</code> if a password verification entry field
		should be provided.
	*/
	public PasswordPanel(final boolean verify)
	{
		super(new BasicGridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		passwordLabel=new JLabel();
		passwordField=new JPasswordField();
		passwordVerificationLabel=new JLabel();
		passwordVerificationField=new JPasswordField();
		setDefaultFocusComponent(passwordField);	//set the default focus component
		initialize();	//initialize the panel
		setVerifyPassword(verify);	//set whether we should verify the password
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		passwordLabel.setText("Password");	//G***i18n
		passwordField.setColumns(16);
		passwordVerificationLabel.setText("Password Verification");	//G***i18n
		passwordVerificationLabel.setVisible(false);	//start out assuming we shouldn't verify the password
		passwordVerificationField.setColumns(16);
		passwordVerificationField.setVisible(false);	//start out assuming we shouldn't verify the password
		add(passwordLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, LABEL_INSETS, 0, 0));
		add(passwordField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, FIELD_INSETS, 0, 0));
		add(passwordVerificationLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, LABEL_INSETS, 0, 0));
		add(passwordVerificationField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, FIELD_INSETS, 0, 0));
	}

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
	public void updateStatus()
	{
		super.updateStatus();	//do the default updating
		passwordVerificationLabel.setVisible(isVerifyPassword());	//only show the password verification if we should verify the password
		passwordVerificationField.setVisible(isVerifyPassword());	//only show the password verification if we should verify the password
	}

	/**Verifies the component.
	@return <code>true</code> if the component contents are valid, <code>false</code>
		if not.
	*/
	public boolean verify()
	{
			//if we should verify password and the passwords don't match
		if(isVerifyPassword() && !Arrays.equals(passwordField.getPassword(), passwordVerificationField.getPassword()))
		{
			JOptionPane.showMessageDialog(this, "Please verify the entered password by entering the same password in both fields.", "Invalid password", JOptionPane.ERROR_MESSAGE);	//G***i18n
			passwordVerificationField.requestFocusInWindow(); //focus on the password verificiation field
			return false; //show that verification failed			
		}
		return super.verify();  //if we couldn't find any problems, verify the parent class
	}

}

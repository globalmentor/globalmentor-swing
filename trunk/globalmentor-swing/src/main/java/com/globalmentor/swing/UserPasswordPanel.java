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

package com.globalmentor.swing;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.*;

import com.globalmentor.awt.BasicGridBagLayout;

import static com.globalmentor.java.Objects.*;

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
		usernameLabel.setText("User");	//TODO i18n
		usernameField.setColumns(16);
		add(usernameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, LABEL_INSETS, 0, 0));
		add(usernameField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, FIELD_INSETS, 0, 0));
		add(passwordPanel, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, LABEL_INSETS, 0, 0));
	}

}

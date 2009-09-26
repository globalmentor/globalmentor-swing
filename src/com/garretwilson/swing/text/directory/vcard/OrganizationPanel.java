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

package com.garretwilson.swing.text.directory.vcard;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.util.*;
import javax.swing.*;

import com.garretwilson.awt.Containers;
import com.garretwilson.swing.*;
import com.garretwilson.swing.border.*;
import com.globalmentor.java.*;
import com.globalmentor.model.LocaledText;
import com.globalmentor.util.*;

/**A panel allowing entry of the organization types of a vCard <code>text/directory</code>
	profile as defined in <a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class OrganizationPanel extends BasicVCardPanel
{
	/**The character to use when separating multiple units.*/
	protected final static char UNIT_SEPARATOR_CHAR=',';

	/**The string to use when separating multiple units.*/ 
	protected final static String UNIT_SEPARATOR=" "+UNIT_SEPARATOR_CHAR+" ";

	/**The characters that can delimit units entered by users.*/
	protected final static String UNIT_DELIMITERS=";"+UNIT_SEPARATOR_CHAR;

	/**The label of the organization name.*/
	private final JLabel nameLabel;

	/**The action for selecting the language of the organization name.*/
	private final SelectLanguageAction selectOrganizationNameLanguageAction;

		/**@return The action for selecting the language of the organization name.*/
		public SelectLanguageAction getSelectOrganizationNameLanguageAction() {return selectOrganizationNameLanguageAction;}

	/**The name text field.*/
	private final JTextField nameTextField;

		/**@return The name text field.*/
		public JTextField getNameTextField() {return nameTextField;}

	/**The label of the organizational units.*/
	private final JLabel unitsLabel;

	/**The units text field.*/
	private final JTextField unitsTextField;

		/**@return The units text field.*/
		public JTextField getUnitsTextField() {return unitsTextField;}

	/**The label of the job title.*/
	private final JLabel titleLabel;

	/**The action for selecting the language of the title.*/
	private final SelectLanguageAction selectTitleLanguageAction;

		/**@return The action for selecting the language of the title.*/
		public SelectLanguageAction getSelectTitleLanguageAction() {return selectTitleLanguageAction;}

	/**The job title text field.*/
	private final JTextField titleTextField;

		/**@return The job title text field.*/
		public JTextField getTitleTextField() {return titleTextField;}

	/**The label of the organizational role.*/
	private final JLabel roleLabel;

	/**The action for selecting the language of the role.*/
	private final SelectLanguageAction selectRoleLanguageAction;

		/**@return The action for selecting the language of the role.*/
		public SelectLanguageAction getSelectRoleLanguageAction() {return selectRoleLanguageAction;}

	/**The role text field.*/
	private final JTextField roleTextField;

		/**@return The role text field.*/
		public JTextField getRoleTextField() {return roleTextField;}

	/**Sets the organization name.
	@param name The name of the organization, or <code>null</code> for no name.
	*/
	public void setOrganizationName(final LocaledText name)
	{
		if(name!=null)	//if there is text
		{
			nameTextField.setText(name.getText());
			selectOrganizationNameLanguageAction.setLocale(name.getLocale());
		}
		else	//if there is no text
		{
			nameTextField.setText("");
			selectOrganizationNameLanguageAction.setLocale(null);
		}
	}

	/**@return The organization name, or <code>null</code> for no name.
	*/
	public LocaledText getOrganizationName()
	{
		final String name=Strings.getNonEmptyString(nameTextField.getText().trim());
		return name!=null ? new LocaledText(name, selectOrganizationNameLanguageAction.getLocale()) : null;
	}

	/**Sets the organizational units. The locales are ignored.
	@param units The organizational units.
	*/
	public void setUnits(final LocaledText[] units)
	{
		unitsTextField.setText(Strings.concat(units, UNIT_SEPARATOR));
	}

	/**@return The organizational units.*/
	public LocaledText[] getUnits()
	{
			//get the units TODO make sure each nickname is trimmed
		return LocaledText.toLocaleTextArray(StringTokenizers.getTokens(new StringTokenizer(unitsTextField.getText().trim(), UNIT_DELIMITERS)), selectOrganizationNameLanguageAction.getLocale());
	}

	/**Sets the job title.
	@param title The job title, functional position or function at the
		organization, or <code>null</code> for no title.
	*/
	public void setJobTitle(final LocaledText title)
	{
		if(title!=null)	//if there is text
		{
			titleTextField.setText(title.getText());
			selectTitleLanguageAction.setLocale(title.getLocale());
		}
		else	//if there is no text
		{
			titleTextField.setText("");
			selectTitleLanguageAction.setLocale(null);
		}
	}

	/**@return The job title, functional position or function at the,
		organization or <code>null</code> for no title.
	*/
	public LocaledText getJobTitle()
	{
		final String title=Strings.getNonEmptyString(titleTextField.getText().trim());
		return title!=null ? new LocaledText(title, selectTitleLanguageAction.getLocale()) : null;
	}

	/**Sets the role.
	@param role The role, occupation, or business category at the organization,
		or <code>null</code> for no role.
	*/
	public void setRole(final LocaledText role)
	{
		if(role!=null)	//if there is text
		{
			roleTextField.setText(role.getText());
			selectRoleLanguageAction.setLocale(role.getLocale());
		}
		else	//if there is no text
		{
			roleTextField.setText("");
			selectRoleLanguageAction.setLocale(null);
		}
	}

	/**@return The role, occupation, or business category at the
		organization, or <code>null</code> for no role.
	*/
	public LocaledText getRole()
	{
		final String role=Strings.getNonEmptyString(roleTextField.getText().trim());
		return role!=null ? new LocaledText(role, selectRoleLanguageAction.getLocale()) : null;
	}

	/**Default constructor.*/
	public OrganizationPanel()
	{
		this(null, new LocaledText[]{}, null, null);	//create a panel with no initial values
	}

	/**Full organization constructor.
	@param name The name of the organization, or <code>null</code> for no name.
	@param units The organizational units.
	@param title The job title, functional position or function at the
		organization, or <code>null</code> for no title.
	@param role The role, occupation, or business category at the organization,
		or <code>null</code> for no role.
	*/
	public OrganizationPanel(final LocaledText name, final LocaledText[] units, final LocaledText title, final LocaledText role)
	{
		super(new GridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		nameLabel=new JLabel();
		nameTextField=new JTextField();
		selectOrganizationNameLanguageAction=new SelectLanguageAction(null, nameTextField);
		unitsLabel=new JLabel();
		unitsTextField=new JTextField();
		titleLabel=new JLabel();
		titleTextField=new JTextField();
		selectTitleLanguageAction=new SelectLanguageAction(null, titleTextField);
		roleLabel=new JLabel();
		roleTextField=new JTextField();
		selectRoleLanguageAction=new SelectLanguageAction(null, roleTextField);
		setDefaultFocusComponent(nameTextField);	//set the default focus component
		initialize();	//initialize the panel
		setOrganizationName(name);	//set the given name
		setUnits(units);	//set the given units
		setJobTitle(title);	//set the given title
		setRole(role);	//set the given role
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		setBorder(Borders.createDefaultTitledBorder());	//set a titled border
		setTitle("Organization");	//TODO i18n
		final PropertyChangeListener modifyLocalePropertyChangeListener=createModifyPropertyChangeListener(SelectLanguageAction.LOCALE_PROPERTY_NAME);	//create a property change listener to change the modified status when the locale property changes
		nameLabel.setText("Organization Name");	//TODO i18n
		getSelectOrganizationNameLanguageAction().addPropertyChangeListener(modifyLocalePropertyChangeListener);
		final JButton selectOrganizationNameLanguageButton=createSelectLanguageButton(getSelectOrganizationNameLanguageAction());
		nameTextField.setColumns(16);
		nameTextField.getDocument().addDocumentListener(getModifyDocumentListener());
		unitsLabel.setText("Unit(s)");	//TODO i18n
		unitsTextField.setColumns(10);
		unitsTextField.getDocument().addDocumentListener(getModifyDocumentListener());
		titleLabel.setText("Job Title");	//TODO i18n
		getSelectTitleLanguageAction().addPropertyChangeListener(modifyLocalePropertyChangeListener);
		final JButton selectTitleLanguageButton=createSelectLanguageButton(getSelectTitleLanguageAction());
		titleTextField.setColumns(16);
		titleTextField.getDocument().addDocumentListener(getModifyDocumentListener());
		roleLabel.setText("Role");	//TODO i18n
		getSelectRoleLanguageAction().addPropertyChangeListener(modifyLocalePropertyChangeListener);
		final JButton selectRoleLanguageButton=createSelectLanguageButton(getSelectRoleLanguageAction());
		roleTextField.setColumns(12);
		roleTextField.getDocument().addDocumentListener(getModifyDocumentListener());
		add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(selectOrganizationNameLanguageButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(nameTextField, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Containers.NO_INSETS, 0, 0));
		add(unitsLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(unitsTextField, new GridBagConstraints(2, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Containers.NO_INSETS, 0, 0));
		add(titleLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(selectTitleLanguageButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(titleTextField, new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Containers.NO_INSETS, 0, 0));
		add(roleLabel, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(selectRoleLanguageButton, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(roleTextField, new GridBagConstraints(2, 3, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Containers.NO_INSETS, 0, 0));
	}

}

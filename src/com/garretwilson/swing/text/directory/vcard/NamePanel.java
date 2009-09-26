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
import java.beans.*;
import java.util.*;
import javax.swing.*;
import com.garretwilson.swing.*;
import com.globalmentor.java.*;
import com.globalmentor.text.directory.vcard.*;
import com.globalmentor.util.*;

/**A panel allowing entry of the "N" type of a vCard <code>text/directory</code>
	profile as defined in <a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class NamePanel extends BasicVCardPanel
{
	/**The character to use when separating multiple values.*/
	protected final static char VALUE_SEPARATOR_CHAR='|';

	/**The string to use when separating multiple values.*/ 
	protected final static String VALUE_SEPARATOR=" "+VALUE_SEPARATOR_CHAR+" ";

	/**The characters that can delimit values entered by users.*/
	protected final static String VALUE_DELIMITERS=",;"+VALUE_SEPARATOR_CHAR;
	
	/**Example honorific prefixes for populating the combo box.*/
	protected final static String[] HONORIFIC_PREFIX_EXAMPLES=new String[]
			{"Dr.", "Miss.", "Mr.", "Mrs.", "Ms.", "Prof."};	//TODO i18n

	/**Example honorific suffixes for populating the combo box.*/
	protected final static String[] HONORIFIC_SUFFIX_EXAMPLES=new String[]
			{"I", "II", "III", "Jr.", "Sr."};	//TODO i18n

	/**The label of the family name text field.*/
	private final JLabel familyNameLabel;

	/**The family name text field.*/
	private final JTextField familyNameTextField;

		/**@return The family name text field.*/
		public JTextField getFamilyNameTextField() {return familyNameTextField;}
	
	/**The label of the given name text field.*/
	private final JLabel givenNameLabel;

	/**The given name text field.*/
	private final JTextField givenNameTextField;

		/**@return The given name text field.*/
		public JTextField getGivenNameTextField() {return givenNameTextField;}

	/**The label of the additional name text field.*/
	private final JLabel additionalNameLabel;

	/**The additional name text field.*/
	private final JTextField additionalNameTextField;

		/**@return The additional name text field.*/
		public JTextField getAdditionalNameTextField() {return additionalNameTextField;}

	/**The label of the honorific prefix.*/
	private final JLabel honorificPrefixLabel;

	/**The honorific prefix combo box.*/
	private final JComboBox honorificPrefixComboBox;

		/**@return The honorific prefix combo box.*/
		public JComboBox getHonorificPrefixComboBox() {return honorificPrefixComboBox;}

	/**The label of the honorific suffix.*/
	private final JLabel honorificSuffixLabel;

	/**The honorific suffix combo box.*/
	private final JComboBox honorificSuffixComboBox;

		/**@return The honorific suffix combo box.*/
		public JComboBox getHonorificSuffixComboBox() {return honorificSuffixComboBox;}

	/**The action for selecting the language of the name.*/
	private final SelectLanguageAction selectLanguageAction;

		/**@return The action for selecting the language of the name.*/
		public SelectLanguageAction getSelectLanguageAction() {return selectLanguageAction;}

	/**Places the name information into the various fields.
	@param name The name to place in the fields, or <code>null</code> if no
		information should be displayed.
	*/
	public void setVCardName(final Name name)
	{
		if(name!=null)	//if there is a name
		{
			familyNameTextField.setText(Strings.concat(name.getFamilyNames(), VALUE_SEPARATOR));
			givenNameTextField.setText(Strings.concat(name.getGivenNames(), VALUE_SEPARATOR));
			additionalNameTextField.setText(Strings.concat(name.getAdditionalNames(), VALUE_SEPARATOR));
			honorificPrefixComboBox.setSelectedItem(Strings.concat(name.getHonorificPrefixes(), VALUE_SEPARATOR));
			honorificSuffixComboBox.setSelectedItem(Strings.concat(name.getHonorificSuffixes(), VALUE_SEPARATOR));
			selectLanguageAction.setLocale(name.getLocale());
		}
		else	//if there is no name, clear the fields
		{
			familyNameTextField.setText("");
			givenNameTextField.setText("");
			additionalNameTextField.setText("");
			honorificPrefixComboBox.setSelectedItem("");
			honorificSuffixComboBox.setSelectedItem("");
			selectLanguageAction.setLocale(null);
		}
	}
	
	/**@return An object representing the VCard name information entered, or
		<code>null</code> if no name was entered.
	*/
	public Name getVCardName()
	{
			//get the values from the components
		final String[] familyNames=StringTokenizers.getTokens(new StringTokenizer(familyNameTextField.getText().trim(), VALUE_DELIMITERS));
		final String[] givenNames=StringTokenizers.getTokens(new StringTokenizer(givenNameTextField.getText().trim(), VALUE_DELIMITERS));
		final String[] additionalNames=StringTokenizers.getTokens(new StringTokenizer(additionalNameTextField.getText().trim(), VALUE_DELIMITERS));
		final String[] honorificPrefixes=StringTokenizers.getTokens(new StringTokenizer(honorificPrefixComboBox.getSelectedItem().toString().trim(), VALUE_DELIMITERS));
		final String[] honorificSuffixes=StringTokenizers.getTokens(new StringTokenizer(honorificSuffixComboBox.getSelectedItem().toString().trim(), VALUE_DELIMITERS));
		final Locale locale=selectLanguageAction.getLocale();
			//if any part(s) of the name was given 
		if(familyNames.length>0 || givenNames.length>0 || additionalNames.length>0 || honorificPrefixes.length>0 || honorificSuffixes.length>0)
		{
			return new Name(familyNames, givenNames, additionalNames, honorificPrefixes, honorificSuffixes, locale);	//create and return a name representing the entered information
		}
		else	//if no name was given
		{
			return null;	//no name was entered
		}
	}

	/**Default constructor.*/
	public NamePanel()
	{
		this(null);	//construct a panel with no name	
	}

	/**Name constructor.
	@param name The name to place in the fields, or <code>null</code> if no
		information should be displayed.
	*/
	public NamePanel(final Name name)
	{
		super(new GridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		familyNameLabel=new JLabel();
		familyNameTextField=new JTextField();
		givenNameLabel=new JLabel();
		givenNameTextField=new JTextField();
		additionalNameLabel=new JLabel();
		additionalNameTextField=new JTextField();
		honorificPrefixLabel=new JLabel();
		honorificPrefixComboBox=new JComboBox();
		honorificSuffixLabel=new JLabel();
		honorificSuffixComboBox=new JComboBox();
		selectLanguageAction=new SelectLanguageAction(null, this);
		setDefaultFocusComponent(givenNameTextField);	//set the default focus component
		initialize();	//initialize the panel
		setVCardName(name);	//set the given name
		setModified(false);	//show that the information has not yet been modified
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		final PropertyChangeListener modifyLocalePropertyChangeListener=createModifyPropertyChangeListener(SelectLanguageAction.LOCALE_PROPERTY_NAME);	//create a property change listener to change the modified status when the locale property changes
		familyNameLabel.setText("Family");	//TODO i18n
		familyNameTextField.setColumns(8);
		familyNameTextField.getDocument().addDocumentListener(getModifyDocumentListener());
		givenNameLabel.setText("Given Name");	//TODO i18n
		givenNameTextField.setColumns(8);
		givenNameTextField.getDocument().addDocumentListener(getModifyDocumentListener());
		additionalNameLabel.setText("Additional");	//TODO i18n
		additionalNameTextField.setColumns(8);
		additionalNameTextField.getDocument().addDocumentListener(getModifyDocumentListener());
		honorificPrefixLabel.setText("Prefix");	//TODO i18n
		honorificPrefixComboBox.setEditable(true);
		honorificPrefixComboBox.setModel(new DefaultComboBoxModel(HONORIFIC_PREFIX_EXAMPLES));	//set up the example honorific prefixes
		honorificPrefixComboBox.setPrototypeDisplayValue("Prof.");
		honorificPrefixComboBox.addActionListener(getModifyActionListener());
		honorificSuffixLabel.setText("Suffix");	//TODO i18n
		honorificSuffixComboBox.setEditable(true);
		honorificSuffixComboBox.setModel(new DefaultComboBoxModel(HONORIFIC_SUFFIX_EXAMPLES));	//set up the example honorific suffixes
		honorificSuffixComboBox.setPrototypeDisplayValue("Sr.");
		honorificSuffixComboBox.addActionListener(getModifyActionListener());
		getSelectLanguageAction().addPropertyChangeListener(modifyLocalePropertyChangeListener);
		final JButton selectLanguageButton=createSelectLanguageButton(getSelectLanguageAction());
		add(honorificPrefixLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(honorificPrefixComboBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(givenNameLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(givenNameTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, NO_INSETS, 0, 0));
		add(additionalNameLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(additionalNameTextField, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, NO_INSETS, 0, 0));
		add(familyNameLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(selectLanguageButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(familyNameTextField, new GridBagConstraints(3, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, NO_INSETS, 0, 0));
		add(honorificSuffixLabel, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(honorificSuffixComboBox, new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
	}
}

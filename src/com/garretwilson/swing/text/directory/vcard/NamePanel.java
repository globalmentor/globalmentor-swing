package com.garretwilson.swing.text.directory.vcard;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.garretwilson.lang.*;
import com.garretwilson.text.directory.vcard.*;
import com.garretwilson.swing.*;
import com.garretwilson.util.*;

/**A panel allowing entry of the "N" type of a vCard <code>text/directory</code>
	profile as defined in <a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class NamePanel extends DefaultPanel
{
	/**The character to use when separating multiple values.*/
	protected final static char VALUE_SEPARATOR_CHAR='|';

	/**The string to use when separating multiple values.*/ 
	protected final static String VALUE_SEPARATOR=" "+VALUE_SEPARATOR_CHAR+" ";

	/**The characters that can delimit values entered by users.*/
	protected final static String VALUE_DELIMITERS=",;"+VALUE_SEPARATOR_CHAR;
	
	/**Example honorific prefixes for populating the combo box.*/
	protected final static String[] HONORIFIC_PREFIX_EXAMPLES=new String[]
			{"Dr.", "Miss.", "Mr.", "Mrs.", "Ms.", "Prof."};	//G***i18n

	/**Example honorific suffixes for populating the combo box.*/
	protected final static String[] HONORIFIC_SUFFIX_EXAMPLES=new String[]
			{"I", "II", "III", "Jr.", "Sr."};	//G***i18n

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

	/**The label of the honorific prefix text field.*/
	private final JLabel honorificPrefixLabel;

	/**The honorific prefix combo box.*/
	private final JComboBox honorificPrefixComboBox;

		/**@return The honorific prefix combo box.*/
		public JComboBox getHonorificPrefixComboBox() {return honorificPrefixComboBox;}

	/**The label of the honorific suffix text field.*/
	private final JLabel honorificSuffixLabel;

	/**The honorific suffix combo box.*/
	private final JComboBox honorificSuffixComboBox;

		/**@return The honorific suffix combo box.*/
		public JComboBox getHonorificSuffixComboBox() {return honorificSuffixComboBox;}

	/**Places the name information in the various fields.
	@param name The name to place in the fields, or <code>null</code> if no
		information should be displayed.
	*/
	public void setVCardName(final Name name)
	{
		if(name!=null)	//if there is a name
		{
			familyNameTextField.setText(StringUtilities.concat(name.getFamilyNames(), VALUE_SEPARATOR));
			givenNameTextField.setText(StringUtilities.concat(name.getGivenNames(), VALUE_SEPARATOR));
			additionalNameTextField.setText(StringUtilities.concat(name.getAdditionalNames(), VALUE_SEPARATOR));
			honorificPrefixComboBox.setSelectedItem(StringUtilities.concat(name.getHonorificPrefixes(), VALUE_SEPARATOR));
			honorificSuffixComboBox.setSelectedItem(StringUtilities.concat(name.getHonorificSuffixes(), VALUE_SEPARATOR));
		}
		else	//if there is no name
		{
			honorificPrefixComboBox.setSelectedItem("");	//clear the fields
			honorificSuffixComboBox.setSelectedItem("");
		}
	}
	
	/**@return An object representing the VCard name information entered.*/
	public Name getVCardName()
	{
			//get the values from the components
		final String[] familyNames=StringTokenizerUtilities.getTokens(new StringTokenizer(familyNameTextField.getText(), VALUE_DELIMITERS));
		final String[] givenNames=StringTokenizerUtilities.getTokens(new StringTokenizer(givenNameTextField.getText(), VALUE_DELIMITERS));
		final String[] additionalNames=StringTokenizerUtilities.getTokens(new StringTokenizer(additionalNameTextField.getText(), VALUE_DELIMITERS));
		final String[] honorificPrefixes=StringTokenizerUtilities.getTokens(new StringTokenizer(honorificPrefixComboBox.getSelectedItem().toString(), VALUE_DELIMITERS));
		final String[] honorificSuffixes=StringTokenizerUtilities.getTokens(new StringTokenizer(honorificSuffixComboBox.getSelectedItem().toString(), VALUE_DELIMITERS));
		return new Name(familyNames, givenNames, additionalNames, honorificPrefixes, honorificSuffixes);	//create and return a name representing the entered information
	}

	/**Default constructor.*/
	public NamePanel()
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
		setDefaultFocusComponent(givenNameTextField);	//set the default focus component
		initialize();	//initialize the panel
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		familyNameLabel.setText("Family Name");	//G***i18n
		familyNameTextField.setColumns(8);
		givenNameLabel.setText("Given Name");	//G***i18n
		givenNameTextField.setColumns(8);
		additionalNameLabel.setText("Additional");	//G***i18n
		additionalNameTextField.setColumns(8);
		honorificPrefixLabel.setText("Prefix");	//G***i18n
		honorificPrefixComboBox.setEditable(true);
		honorificPrefixComboBox.setModel(new DefaultComboBoxModel(HONORIFIC_PREFIX_EXAMPLES));	//set up the example honorific prefixes
		honorificSuffixLabel.setText("Suffix");	//G***i18n
		honorificSuffixComboBox.setEditable(true);
		honorificSuffixComboBox.setModel(new DefaultComboBoxModel(HONORIFIC_SUFFIX_EXAMPLES));	//set up the example honorific suffixes
		add(honorificPrefixLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(honorificPrefixComboBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(givenNameLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(givenNameTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(additionalNameLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(additionalNameTextField, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(familyNameLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(familyNameTextField, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(honorificSuffixLabel, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(honorificSuffixComboBox, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		setVCardName(null);	//clear the fields
	}

	/**Updates the constructed URI based upon current user input.*/
	protected void updateStatus()
	{
		super.updateStatus();	//do the default status updating
	}
	
}

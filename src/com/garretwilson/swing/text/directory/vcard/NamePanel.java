package com.garretwilson.swing.text.directory.vcard;

import java.awt.*;
import javax.swing.*;
import com.garretwilson.lang.*;
import com.garretwilson.text.directory.vcard.*;
import com.garretwilson.swing.*;

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
	protected final static String VALUE_DELIMITERS=",; "+VALUE_SEPARATOR_CHAR;

	/**The label of the family name text field.*/
	private final JLabel familyNameLabel;

	/**The family name text field.*/
	private final JTextField familyNameTextField;
	
	/**The label of the given name text field.*/
	private final JLabel givenNameLabel;

	/**The given name text field.*/
	private final JTextField givenNameTextField;

	/**The label of the additional name text field.*/
	private final JLabel additionalNameLabel;

	/**The additional name text field.*/
	private final JTextField additionalNameTextField;

	/**The label of the honorific prefix text field.*/
	private final JLabel honorificPrefixLabel;

	/**The honorific prefix text field.*/
	private final JTextField honorificPrefixTextField;

	/**The label of the honorific suffix text field.*/
	private final JLabel honorificSuffixLabel;

	/**The honorific suffix text field.*/
	private final JTextField honorificSuffixTextField;

	/**Places the name information in the various fields.
	@param name The name to place in the fields.
	*/
	public void setName(final Name name)
	{
		familyNameTextField.setText(StringUtilities.concat(name.getFamilyNames(), VALUE_SEPARATOR));
		givenNameTextField.setText(StringUtilities.concat(name.getGivenNames(), VALUE_SEPARATOR));
		additionalNameTextField.setText(StringUtilities.concat(name.getAdditionalNames(), VALUE_SEPARATOR));
		honorificPrefixTextField.setText(StringUtilities.concat(name.getHonorificPrefixes(), VALUE_SEPARATOR));
		honorificPrefixTextField.setText(StringUtilities.concat(name.getHonorificSuffixes(), VALUE_SEPARATOR));
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
		honorificPrefixTextField=new JTextField();
		honorificSuffixLabel=new JLabel();
		honorificSuffixTextField=new JTextField();
		setDefaultFocusComponent(givenNameTextField);	//set the default focus component
		initialize();	//initialize the panel
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		familyNameLabel.setText("Family Name");	//G***i18n
		familyNameTextField.setColumns(16);
		givenNameLabel.setText("Given Name");	//G***i18n
		givenNameTextField.setColumns(16);
		additionalNameLabel.setText("Additional Name(s)");	//G***i18n
		additionalNameTextField.setColumns(16);
		honorificPrefixLabel.setText("Prefix");	//G***i18n
		honorificPrefixTextField.setColumns(5);
		honorificSuffixLabel.setText("Suffix");	//G***i18n
		honorificSuffixTextField.setColumns(5);
		add(honorificPrefixLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(honorificPrefixTextField, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(givenNameLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(givenNameTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(additionalNameLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(additionalNameTextField, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(familyNameLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(familyNameTextField, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(honorificSuffixLabel, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(honorificSuffixTextField, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	}

	/**Updates the constructed URI based upon current user input.*/
	protected void updateStatus()
	{
		super.updateStatus();	//do the default status updating
	}
	
}

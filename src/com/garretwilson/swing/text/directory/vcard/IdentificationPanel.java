package com.garretwilson.swing.text.directory.vcard;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import com.garretwilson.lang.*;
import com.garretwilson.swing.*;
import com.garretwilson.swing.border.*;
import com.garretwilson.swing.event.*;
import com.garretwilson.text.directory.vcard.*;
import com.garretwilson.util.*;

/**A panel containing fields for the identification types of a vCard
	<code>text/directory</code>	profile as defined in
	<a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class IdentificationPanel extends DefaultPanel
{

	/**The character to use when separating multiple values.*/
	protected final static char VALUE_SEPARATOR_CHAR=',';

	/**The string to use when separating multiple values.*/ 
	protected final static String VALUE_SEPARATOR=""+VALUE_SEPARATOR_CHAR+" ";

	/**The characters that can delimit values entered by users.*/
	protected final static String VALUE_DELIMITERS=";"+VALUE_SEPARATOR_CHAR;

	/**The name panel.*/
	private final NamePanel namePanel;
	
	/**The label of the formatted name.*/
	private final JLabel formattedNameLabel;

	/**The formatted name text field.*/
	private final JTextField formattedNameTextField;
	
	/**The label of the nickname text field.*/
	private final JLabel nicknameLabel;

	/**The nickname text field.*/
	private final JTextField nicknameTextField;

	/**Whether, the last time the formatted name was updated, it matched the
		the default formatting of the name.
	*/
	private boolean shouldUpdateFormattedName=true;

	/**Places the name information into the various fields.
	@param name The name to place in the fields, or <code>null</code> if no
		information should be displayed.
	@see NamePanel#setVCardName
	*	*/
	public void setVCardName(final Name name)
	{
		namePanel.setVCardName(name);	//set the name in the name panel
	}
	
	/**@return An object representing the VCard name information entered.*/
	public Name getVCardName()
	{
		return namePanel.getVCardName();	//get the name from the name panel
	}

	/**Places the formatted name into the field.
	@param formattedName The name to place in the field, or <code>null</code> if
		no information should be displayed.
	*/
	public void setFormattedName(final String formattedName)
	{
		formattedNameTextField.setText(formattedName!=null ? formattedName : "");
	}
	
	/**@return The formatted name entered, or <code>null</code> if
		no formatted name was entered.
	*/
	public String getFormattedName()
	{
		return StringUtilities.getNonEmptyString(formattedNameTextField.getText().trim());
	}

	/**Places the nicknames into the field.
	@param nicknames The nicknames to place in the field, or <code>null</code> if
		no information should be displayed.
	*/
	public void setNicknames(final String[] nicknames)
	{
		if(nicknames!=null)	//if there are nicknames
		{
			nicknameTextField.setText(StringUtilities.concat(nicknames, VALUE_SEPARATOR));
		}
		else	//if there are no nicknames
		{
			nicknameTextField.setText("");
		}
	}
	
	/**@return The nicknames entered.
	*/
	public String[] getNicknames()
	{
			//get the nicknames TODO make sure each nickname is trimmed
		return StringTokenizerUtilities.getTokens(new StringTokenizer(nicknameTextField.getText().trim(), VALUE_DELIMITERS));
	}	

	/**Default constructor.*/
	public IdentificationPanel()
	{
		super(new GridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		namePanel=new NamePanel();
		formattedNameLabel=new JLabel();
		formattedNameTextField=new JTextField();
		nicknameLabel=new JLabel();
		nicknameTextField=new JTextField();
		setDefaultFocusComponent(namePanel);	//set the default focus component
		initialize();	//initialize the panel
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		setBorder(BorderUtilities.createDefaultTitledBorder());	//set a titled border
		setTitle("Identification");	//G***i18n
			//add listeners to all the components of the name panel to update the status when modified
		namePanel.getFamilyNameTextField().getDocument().addDocumentListener(createUpdateStatusDocumentListener()); 		
		namePanel.getGivenNameTextField().getDocument().addDocumentListener(createUpdateStatusDocumentListener()); 		
		namePanel.getAdditionalNameTextField().getDocument().addDocumentListener(createUpdateStatusDocumentListener());
		namePanel.getHonorificPrefixComboBox().addActionListener(createUpdateStatusActionListener());
		namePanel.getHonorificSuffixComboBox().addActionListener(createUpdateStatusActionListener());
		formattedNameLabel.setText("Formatted Name");	//G***i18n
		formattedNameTextField.setColumns(10);
		formattedNameTextField.getDocument().addDocumentListener(new DocumentModifyAdapter()
				{
					public void modifyUpdate(final DocumentEvent documentEvent)	//if the formatted name text field is modified
					{
							//see if the formatted name matches the default formatted version of the information in the name field, or if there's no information in the formatted text field
						shouldUpdateFormattedName=formattedNameTextField.getText().equals(namePanel.getVCardName().toString())
								|| formattedNameTextField.getText().trim().length()==0;
					}
				});
		nicknameLabel.setText("Nickname");	//G***i18n
		nicknameTextField.setColumns(8);
		add(namePanel, new GridBagConstraints(0, 0, 2, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		add(nicknameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(nicknameTextField, new GridBagConstraints(0, 2, 1, 1, 0.4, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		add(formattedNameLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(formattedNameTextField, new GridBagConstraints(1, 2, 1, 1, 0.6, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
	}

	/**Updates the constructed URI based upon current user input.*/
	protected void updateStatus()
	{
		super.updateStatus();	//do the default status updating
		if(shouldUpdateFormattedName)	//if the formatted name matched the information in the name panel the last time the formatted name was modified
		{
			formattedNameTextField.setText(namePanel.getVCardName().toString());	//update the formatted name label to reflect the current state of the name information
		}
	}
	
}

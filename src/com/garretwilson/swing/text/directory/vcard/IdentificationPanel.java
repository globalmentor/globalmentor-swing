package com.garretwilson.swing.text.directory.vcard;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import com.garretwilson.swing.*;
import com.garretwilson.swing.event.*;

/**A panel containing fields for the identification types of a vCard
	<code>text/directory</code>	profile as defined in
	<a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class IdentificationPanel extends DefaultPanel
{
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
	private boolean formattedNameMatchesName=true;

	/**Places the name information in the various fields.
	@param name The name to place in the fields.
	*/
/*G***fix
	public void setName(final Name name)
	{
		familyNameTextField.setText(StringUtilities.concat(name.getFamilyNames(), VALUE_SEPARATOR));
		givenNameTextField.setText(StringUtilities.concat(name.getGivenNames(), VALUE_SEPARATOR));
		additionalNameTextField.setText(StringUtilities.concat(name.getAdditionalNames(), VALUE_SEPARATOR));
		honorificPrefixTextField.setText(StringUtilities.concat(name.getHonorificPrefixes(), VALUE_SEPARATOR));
		honorificPrefixTextField.setText(StringUtilities.concat(name.getHonorificSuffixes(), VALUE_SEPARATOR));
	}
*/

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
			//add listeners to all the components of the name panel to update the status when modified
		namePanel.getFamilyNameTextField().getDocument().addDocumentListener(createUpdateStatusDocumentListener()); 		
		namePanel.getGivenNameTextField().getDocument().addDocumentListener(createUpdateStatusDocumentListener()); 		
		namePanel.getAdditionalNameTextField().getDocument().addDocumentListener(createUpdateStatusDocumentListener());
		namePanel.getHonorificPrefixComboBox().addActionListener(createUpdateStatusActionListener());
		namePanel.getHonorificSuffixComboBox().addActionListener(createUpdateStatusActionListener());
		formattedNameLabel.setText("Formatted Name");	//G***i18n
		formattedNameTextField.setColumns(20);
		formattedNameTextField.getDocument().addDocumentListener(new DocumentModifyAdapter()
				{
					public void modifyUpdate(final DocumentEvent documentEvent)	//if the formatted name text field is modified
					{
							//see if the formatted name matches the default formatted version of the information in the name field
						formattedNameMatchesName=formattedNameTextField.getText().equals(namePanel.getVCardName().toString());
					}
				});
		nicknameLabel.setText("Nickname");	//G***i18n
		nicknameTextField.setColumns(20);
		add(namePanel, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(formattedNameLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(formattedNameTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(nicknameLabel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(nicknameTextField, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	}

	/**Updates the constructed URI based upon current user input.*/
	protected void updateStatus()
	{
		super.updateStatus();	//do the default status updating
		if(formattedNameMatchesName)	//if the formatted name matched the information in the name panel the last time the formatted name was modified
		{
			formattedNameTextField.setText(namePanel.getVCardName().toString());	//update the formatted name label to reflect the current state of the name information
		}
	}
	
}

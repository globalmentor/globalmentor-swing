package com.garretwilson.swing.text.directory.vcard;

import java.awt.*;
import java.awt.event.ItemListener;
import java.beans.*;
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
public class IdentificationPanel extends BasicVCardPanel
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

	/**The action for selecting the language of the formatted name.*/
	private final SelectLanguageAction selectFormattedNameLanguageAction;

		/**@return The action for selecting the language of the formatted name.*/
		public SelectLanguageAction getSelectFormattedNameLanguageAction() {return selectFormattedNameLanguageAction;}

	/**The formatted name text field.*/
	private final JTextField formattedNameTextField;
	
	/**The label of the nickname text field.*/
	private final JLabel nicknameLabel;

	/**The action for selecting the language of the nickname.*/
	private final SelectLanguageAction selectNicknameLanguageAction;

		/**@return The action for selecting the language of the nickname.*/
		public SelectLanguageAction getSelectNicknameLanguageAction() {return selectNicknameLanguageAction;}

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
	
	/**@return An object representing the VCard name information entered, or
		<code>null</code> if no name was entered.
	*/
	public Name getVCardName()
	{
		return namePanel.getVCardName();	//get the name from the name panel
	}

	/**Places the formatted name into the field.
	@param formattedName The name to place in the field, or <code>null</code> if
		no information should be displayed.
	*/
	public void setFormattedName(final LocaleText formattedName)
	{
		if(formattedName!=null)	//if there is text
		{
			formattedNameTextField.setText(formattedName.getText());
			selectFormattedNameLanguageAction.setLocale(formattedName.getLocale());
		}
		else	//if there is no text
		{
			formattedNameTextField.setText("");
			selectFormattedNameLanguageAction.setLocale(null);
		}
	}
	
	/**@return The formatted name entered, or <code>null</code> if
		no formatted name was entered.
	*/
	public LocaleText getFormattedName()
	{
		final String fn=Strings.getNonEmptyString(formattedNameTextField.getText().trim());
		return fn!=null ? new LocaleText(fn, selectFormattedNameLanguageAction.getLocale()) : null;
	}

	/**Places the nicknames into the field.
	@param nicknames The nicknames to place in the field, or <code>null</code> if
		no information should be displayed.
	*/
	public void setNicknames(final LocaleText[] nicknames)
	{
		if(nicknames!=null)	//if there are nicknames
		{
			nicknameTextField.setText(Strings.concat(nicknames, VALUE_SEPARATOR));
			if(nicknames.length>0)	//if there is at least one nickname
			{
				selectNicknameLanguageAction.setLocale(nicknames[0].getLocale());
			}
			else
			{
				selectNicknameLanguageAction.setLocale(null);
			}
		}
		else	//if there are no nicknames
		{
			nicknameTextField.setText("");
			selectNicknameLanguageAction.setLocale(null);
		}
	}
	
	/**@return The nicknames entered.
	*/
	public LocaleText[] getNicknames()
	{
			//get the nicknames TODO make sure each nickname is trimmed
		return LocaleText.toLocaleTextArray(StringTokenizerUtilities.getTokens(new StringTokenizer(nicknameTextField.getText().trim(), VALUE_DELIMITERS)), selectNicknameLanguageAction.getLocale());
	}	

	/**Default constructor.*/
	public IdentificationPanel()
	{
		super(new GridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		namePanel=new NamePanel();
		formattedNameLabel=new JLabel();
		formattedNameTextField=new JTextField();
		selectFormattedNameLanguageAction=new SelectLanguageAction(null, formattedNameTextField);
		nicknameLabel=new JLabel();
		nicknameTextField=new JTextField();
		selectNicknameLanguageAction=new SelectLanguageAction(null, nicknameTextField);
		setDefaultFocusComponent(namePanel);	//set the default focus component
		initialize();	//initialize the panel
		setModified(false);	//show that the information has not yet been modified
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		setBorder(BorderUtilities.createDefaultTitledBorder());	//set a titled border
		setTitle("Identification");	//G***i18n
		final PropertyChangeListener modifyLocalePropertyChangeListener=createModifyPropertyChangeListener(LocaleConstants.LOCALE_PROPERTY_NAME);	//create a property change listener to change the modified status when the locale property changes
			//add listeners to all the components of the name panel to update the status when modified
		namePanel.getFamilyNameTextField().getDocument().addDocumentListener(createUpdateStatusDocumentListener()); 		
		namePanel.getGivenNameTextField().getDocument().addDocumentListener(createUpdateStatusDocumentListener()); 		
		namePanel.getAdditionalNameTextField().getDocument().addDocumentListener(createUpdateStatusDocumentListener());
		final ItemListener updateStatusItemListener=createUpdateStatusItemListener();	//create an item listener that will update the status			
		namePanel.getHonorificPrefixComboBox().addItemListener(updateStatusItemListener);
		namePanel.getHonorificSuffixComboBox().addItemListener(updateStatusItemListener);
		formattedNameLabel.setText("Formatted Name");	//G***i18n
		getSelectFormattedNameLanguageAction().addPropertyChangeListener(modifyLocalePropertyChangeListener);
		final JButton selectFormattedNameLanguageButton=createSelectLanguageButton(getSelectFormattedNameLanguageAction());
		formattedNameTextField.setColumns(10);
		formattedNameTextField.getDocument().addDocumentListener(new DocumentModifyAdapter()
				{
					public void modifyUpdate(final DocumentEvent documentEvent)	//if the formatted name text field is modified
					{
							//see if the formatted name matches the default formatted version of the information in the name field, or if there's no information in the formatted text field
						shouldUpdateFormattedName=formattedNameTextField.getText().trim().length()==0
								|| (getVCardName()!=null && formattedNameTextField.getText().equals(getVCardName().toString()));
					}
				});
		formattedNameTextField.getDocument().addDocumentListener(getModifyDocumentListener());
		nicknameLabel.setText("Nickname");	//G***i18n
		getSelectNicknameLanguageAction().addPropertyChangeListener(modifyLocalePropertyChangeListener);
		final JButton selectNicknameLanguageButton=createSelectLanguageButton(getSelectNicknameLanguageAction());
		nicknameTextField.setColumns(8);
		nicknameTextField.getDocument().addDocumentListener(getModifyDocumentListener());
		add(namePanel, new GridBagConstraints(0, 0, 4, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, NO_INSETS, 0, 0));
		add(nicknameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(selectNicknameLanguageButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(nicknameTextField, new GridBagConstraints(0, 2, 2, 1, 0.4, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, NO_INSETS, 0, 0));
		add(formattedNameLabel, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(selectFormattedNameLanguageButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(formattedNameTextField, new GridBagConstraints(2, 2, 2, 1, 0.6, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, NO_INSETS, 0, 0));
	}

	/**Updates the constructed URI based upon current user input.*/
	public void updateStatus()
	{
		super.updateStatus();	//do the default status updating
		if(shouldUpdateFormattedName)	//if the formatted name matched the information in the name panel the last time the formatted name was modified
		{
			formattedNameTextField.setText(getVCardName()!=null ? getVCardName().toString() : "");	//update the formatted name label to reflect the current state of the name information
			getSelectFormattedNameLanguageAction().setLocale(getVCardName()!=null ? getVCardName().getLocale() : null);	//update the formatted name language
		}
	}
	
}

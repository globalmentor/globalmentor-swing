package com.garretwilson.swing.text.directory.vcard;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.garretwilson.lang.*;
import com.garretwilson.text.directory.vcard.*;
import com.garretwilson.swing.*;
import com.garretwilson.swing.border.*;
import com.garretwilson.util.*;

/**A panel allowing entry of the organization types of a vCard <code>text/directory</code>
	profile as defined in <a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class OrganizationPanel extends DefaultPanel
{
	/**The character to use when separating multiple units.*/
	protected final static char UNIT_SEPARATOR_CHAR=',';

	/**The string to use when separating multiple units.*/ 
	protected final static String UNIT_SEPARATOR=" "+UNIT_SEPARATOR_CHAR+" ";

	/**The characters that can delimit units entered by users.*/
	protected final static String UNIT_DELIMITERS=";"+UNIT_SEPARATOR_CHAR;

	/**The label of the organization name.*/
	private final JLabel nameLabel;

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

	/**The job title text field.*/
	private final JTextField titleTextField;

		/**@return The job title text field.*/
		public JTextField getTitleTextField() {return titleTextField;}

	/**The label of the organizational role.*/
	private final JLabel roleLabel;

	/**The role text field.*/
	private final JTextField roleTextField;

		/**@return The role text field.*/
		public JTextField getRoleTextField() {return roleTextField;}

	/**Places the organizational information into the various fields.
	@param organization The organization to place in the fields, or
		<code>null</code> if no information should be displayed.
	*/
	public void setOrganization(final Organization organization)
	{
		if(organization!=null)	//if there is an organization
		{
			nameTextField.setText(organization.getName()!=null ? organization.getName().toString() : null);
			unitsTextField.setText(StringUtilities.concat(organization.getUnits(), UNIT_SEPARATOR));
			titleTextField.setText(organization.getTitle());
			roleTextField.setText(organization.getRole());
		}
		else	//if there is no organization, clear the fields
		{
			nameTextField.setText("");
			unitsTextField.setText("");
			titleTextField.setText("");
			roleTextField.setText("");
		}
	}
	
	/**@return An object representing the organization information entered.*/
	public Organization getOrganization()
	{
		final String name=StringUtilities.getNonEmptyString(nameTextField.getText().trim());
		final String[] units=StringTokenizerUtilities.getTokens(new StringTokenizer(unitsTextField.getText().trim(), UNIT_DELIMITERS));
		final String title=StringUtilities.getNonEmptyString(titleTextField.getText().trim());
		final String role=StringUtilities.getNonEmptyString(roleTextField.getText().trim());
		return new Organization(name, units, title, role);	//create and return an organization representing the entered information
	}

	/**Default constructor.*/
	public OrganizationPanel()
	{
		this(new Organization());	//initialize with a default organization
	}

	/**Organization constructor.
	@param organization The organization to place in the fields, or <code>null</code>
		if no information should be displayed.
	*/
	public OrganizationPanel(final Organization organization)
	{
		super(new GridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		nameLabel=new JLabel();
		nameTextField=new JTextField();
		unitsTextField=new JTextField();
		unitsLabel=new JLabel();
		titleTextField=new JTextField();
		titleLabel=new JLabel();
		roleTextField=new JTextField();
		roleLabel=new JLabel();
		setDefaultFocusComponent(nameTextField);	//set the default focus component
		initialize();	//initialize the panel
		setOrganization(organization);	//set the organization
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		setBorder(BorderUtilities.createDefaultTitledBorder());	//set a titled border
		setTitle("Organization");	//G***i18n
		nameLabel.setText("Organization Name");	//G***i18n
		nameTextField.setColumns(16);
		unitsLabel.setText("Unit(s)");	//G***i18n
		unitsTextField.setColumns(10);
		titleLabel.setText("Job Title");	//G***i18n
		titleTextField.setColumns(16);
		roleLabel.setText("Role");	//G***i18n
		roleTextField.setColumns(12);
		add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(nameTextField, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, NO_INSETS, 0, 0));
		add(unitsLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(unitsTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, NO_INSETS, 0, 0));
		add(titleLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(titleTextField, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, NO_INSETS, 0, 0));
		add(roleLabel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(roleTextField, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, NO_INSETS, 0, 0));

	}

}

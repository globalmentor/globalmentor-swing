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

	/**Sets the organization name.
	@param name The name of the organization, or <code>null</code> for no name.
	*/
	public void setOrganizationName(final String name)
	{
		nameTextField.setText(name!=null ? name : "");
	}

	/**@return The organization name, or <code>null</code> for no name.
	*/
	public String getOrganizationName()
	{
		return StringUtilities.getNonEmptyString(nameTextField.getText().trim());
	}

	/**Sets the organizational units.
	@param units The organizational units.
	*/
	public void setUnits(final String[] units)
	{
		unitsTextField.setText(StringUtilities.concat(units, UNIT_SEPARATOR));
	}

	/**@return The organizational units.*/
	public String[] getUnits()
	{
		return StringTokenizerUtilities.getTokens(new StringTokenizer(unitsTextField.getText().trim(), UNIT_DELIMITERS));
	}

	/**Sets the job title.
	@param title The job title, functional position or function at the
		organization, or <code>null</code> for no title.
	*/
	public void setTitle(final String title)
	{
		titleTextField.setText(title!=null ? title : "");
	}

	/**@return The job title, functional position or function at the,
		organization or <code>null</code> for no title.
	*/
	public String getTitle()
	{
		return StringUtilities.getNonEmptyString(titleTextField.getText().trim());
	}

	/**Sets the role.
	@param role The role, occupation, or business category at the organization,
		or <code>null</code> for no role.
	*/
	public void setRole(final String role)
	{
		roleTextField.setText(role!=null ? role : "");
	}

	/**@return The role, occupation, or business category at the
		organization, or <code>null</code> for no role.
	*/
	public String getRole()
	{
		return StringUtilities.getNonEmptyString(roleTextField.getText().trim());
	}

	/**Default constructor.*/
	public OrganizationPanel()
	{
		this(null, new String[]{}, null, null);	//create a panel with no initial values
	}

	/**Full organization constructor.
	@param name The name of the organization, or <code>null</code> for no name.
	@param units The organizational units.
	@param title The job title, functional position or function at the
		organization, or <code>null</code> for no title.
	@param role The role, occupation, or business category at the organization,
		or <code>null</code> for no role.
	*/
	public OrganizationPanel(final String name, final String[] units, final String title, final String role)
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
		setOrganizationName(name);	//set the given name
		setUnits(units);	//set the given units
		setTitle(title);	//set the given title
		setRole(role);	//set the given role
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

package com.garretwilson.swing.text.directory.vcard;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.garretwilson.itu.*;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.swing.*;
import com.garretwilson.swing.itu.*;
import com.garretwilson.text.directory.vcard.*;

/**A panel containing fields for the <code>TEL</code> type of a vCard
	<code>text/directory</code>	profile as defined in
	<a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
<p>This panel allows actions to be added and automatically positioned as
	buttons.</p>
@author Garret Wilson
*/
public class TelephonePanel extends BasicVCardPanel
{

	/**The action for editing the telephone type.*/
	private final Action editTelephoneTypeAction;

		/**@return The action for editing the telephone type.*/
		public Action getEditTelephoneTypeAction() {return editTelephoneTypeAction;}
		
	/**The telephone number panel.*/
	private final TelephoneNumberPanel telephoneNumberPanel;
	
	/**The telephone type label.*/
	private final JButton telephoneTypeButton;

	/**The local copy of the telephone type.*/
	private int telephoneType;

		/**@return The intended use, a combination of
			<code>Telephone.XXX_TELEPHONE_TYPE</code> constants ORed together.
		*/
		protected int getTelephoneType() {return telephoneType;}

		/**Sets the telephone type.
		@param telephoneType The intended use, one or more of the
			<code>Telephone.XXX_TELEPHONE_TYPE</code> constants ORed together.
		*/
		protected void setTelephoneType(final int telephoneType)
		{
			final int oldTelephoneType=this.telephoneType;	//get the old telephone type
			if(oldTelephoneType!=telephoneType)	//if the telephone type is really changing
			{
				this.telephoneType=telephoneType;	//store the telephone type locally
				setModified(true);	//show that we've changed the telephone type
				telephoneTypeButton.setText(	//update the telephone type button
						telephoneType!=Telephone.NO_TELEPHONE_TYPE	//if there is a telephone type
						? Telephone.getTelephoneTypeString(telephoneType)	//show it
						: "");	//if there is no telephone type, show nothing
			}
		}

	/**Shows or hides the telphone number labels.
	@param visible <code>true</code> if the labels should be shown,
		<code>false</code> if they should be hidden.
	@see TelephoneNumberPanel#setLabelsVisible
	*/
	public void setLabelsVisible(final boolean visible)
	{
		telephoneNumberPanel.setLabelsVisible(visible);	//pass the request on to the telephone number panel
	}
	
	/**@return Whether all the telephone number labels are visible. 
	@see TelephoneNumberPanel#isLabelsVisible
	*/
	public boolean isLabelsVisible()
	{
		return telephoneNumberPanel.isLabelsVisible();	//return the answer of the telephone number panel
	}

	/**Places the telephone number information into the various fields.
	@param telephoneNumber The telephone number to place in the fields, or
		<code>null</code> if default information should be displayed.
	*/
	public void setTelephoneNumber(final TelephoneNumber telephoneNumber)
	{
		telephoneNumberPanel.setTelephoneNumber(telephoneNumber);
	}

	/**Places the telephone information into the various fields.
	@param telephone The telephone information to place in the fields, or
		<code>null</code> if default information should be displayed.
	*/
	public void setTelephone(final Telephone telephone)
	{
		setTelephoneNumber(telephone);	//set the telephone number
		if(telephone!=null)	//if there is telephone information
		{
			setTelephoneType(telephone.getTelephoneType());	//set and update the telephone type
		}
		else	//if there is no telephone information, clear the fields
		{
			setTelephoneType(Telephone.DEFAULT_TELEPHONE_TYPE);	//set the default telephone type
		}
	}
	
	/**@return An object representing the telephone information entered, or
		<code>null</code> if no telephone number was entered or the values violate
		ITU-T E.164.
	*/
	public Telephone getTelephone()
	{
		final TelephoneNumber telephoneNumber=telephoneNumberPanel.getTelephoneNumber();	//get the telephone number from the panel
		if(telephoneNumber!=null)	//if a valid telephone number was entered
		{		
			final int telephoneType=getTelephoneType();	//get the telephone type
			try
			{
				return new Telephone(telephoneNumber, telephoneType);	//create and return telephone information representing the entered information
			}
			catch(TelephoneNumberSyntaxException telephoneNumberSyntaxException)	//if the information isn't a valid telephone number (this should never happen, as we just received a valid telephone number)
			{
				return null;	//show that we don't understand the entered information
			}
		}
		else	//if no telephone number was entered
		{
			return null;	//don't return a telephone number
		}
	}

	/**The number of buttons on the panel.*/
//G***del	private int buttonCount;

	/**Default constructor.*/
	public TelephonePanel()
	{
		this(null);	//construct a default telephone panel
	}

	/**Telephone constructor.
	@param telephone The telephone information to place in the fields, or
		<code>null</code> if default information should be displayed.
	*/
	public TelephonePanel(final Telephone telephone)
	{
			//construct a telephone panel with the telephone number and type, or the defeault type if no telephone is given
		this(telephone, telephone!=null ? telephone.getTelephoneType() : Telephone.DEFAULT_TELEPHONE_TYPE); 
	}

	/**Telephone number and telephone type constructor.
	@param telephone The telephone information to place in the fields, or
		<code>null</code> if default information should be displayed.
	@param telephoneType The intended use, one or more of the
		<code>Telephone.XXX_TELEPHONE_TYPE</code> constants ORed together.
	*/
	public TelephonePanel(final TelephoneNumber telephoneNumber, final int telephoneType)
	{
		super(new GridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		editTelephoneTypeAction=new EditTelephoneTypeAction();
		telephoneTypeButton=new JButton(getEditTelephoneTypeAction());
		telephoneTypeButton.setHorizontalTextPosition(SwingConstants.LEFT);	//G***testing
//G***fix		telephoneTypeButton.setBorder(null);	//G**testing
		telephoneNumberPanel=new TelephoneNumberPanel();	
//G***del		buttonCount=0;	//G***fix all this now that we don't allow extra buttons
		setDefaultFocusComponent(telephoneNumberPanel);	//set the default focus component
		initialize();	//initialize the panel
		setTelephoneNumber(telephoneNumber);	//set the given telephone number
		setTelephoneType(telephoneType);	//set the given telephone type
		setModified(false);	//show that the information has not yet been modified
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		final JLabel imageLabel=new JLabel(IconResources.getIcon(IconResources.PHONE_ICON_FILENAME)); //create a label with the image		
		add(telephoneTypeButton, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(imageLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(telephoneNumberPanel, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, NO_INSETS, 0, 0));
	}
	
	/**Adds a new button based upon the given action and positions it correctly.
	@param action The new action for which a button should be added.
	@return The added button representing the given action.
	*/
/*G***del
	public JButton addButton(final Action action)
	{
		final JButton button=new JButton(action);	//create a button from the action
			//place the button vertically based upon the number of buttons we already have
		add(button, new GridBagConstraints(1, buttonCount, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		return button;	//return the button we created and added
	}
*/

	/**Asks the user for a new telephone type.
	@param parentComponent The component that determines the <code>Frame</code>
		in which the dialog should be shown, or <code>null</code> if a default
		frame should be used.
	@param telephoneType The current telephone type, one or more of the
			<code>Telephone.XXX_TELEPHONE_TYPE</code> constants ORed together.
	@return The new intended use, a combination of
		<code>Telephone.XXX_TELEPHONE_TYPE</code> constants ORed together.
	*/
/*G***del if not needed	
	public static int askTelephoneType(final Component parentComponent, final int telephoneType)
	{
		final TelephoneTypePanel telephoneTypePanel=new TelephoneTypePanel(telephoneType);	//create a new panel with our current telephone type 
			//ask for the new telephone type; if they accept the changes
		if(OptionPane.showConfirmDialog(parentCompent, newResourcePanel, "New Resource", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)	//G***i18n
		{
		
		}
*/
	
	/**Asks the user for a new telephone type and updates the value.
	@return <code>true</code> if the user accepted the changes and the type was
		updated, otherwise <code>false</code> if the user cancelled.
	*/
	public boolean editTelephoneType()
	{
		final TelephoneTypePanel telephoneTypePanel=new TelephoneTypePanel(getTelephoneType());	//create a new panel with our current telephone type 
			//ask for the new telephone type; if they accept the changes
		if(BasicOptionPane.showConfirmDialog(this, telephoneTypePanel, "Telephone Intended Uses", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)	//G***i18n
		{
			setTelephoneType(telephoneTypePanel.getTelephoneType());	//update the telephone type
			return true;	//show that the user accepted the changes and that they were updated		
		}
		else	//if the user cancels
		{
			return false;	//show that the action was cancelled
		}
	}

	/**Action for editing the telephone type.*/
	class EditTelephoneTypeAction extends AbstractAction
	{
		/**Default constructor.*/
		public EditTelephoneTypeAction()
		{
			super("Type");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Edit type");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Edit the intended usage type for this telephone number.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_T));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.PROPERTY_ICON_FILENAME)); //load the correct icon
		}
	
		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			editTelephoneType();	//edit the telephone type
		}
	}
	
}

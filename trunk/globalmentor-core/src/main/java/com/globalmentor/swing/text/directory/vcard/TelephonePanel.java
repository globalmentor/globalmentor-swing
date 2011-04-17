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

package com.globalmentor.swing.text.directory.vcard;

import java.awt.*;
import java.awt.event.*;
import java.util.EnumSet;
import java.util.Set;

import javax.swing.*;

import com.globalmentor.awt.Containers;
import com.globalmentor.itu.*;
import com.globalmentor.swing.*;
import com.globalmentor.swing.itu.*;
import com.globalmentor.text.ArgumentSyntaxException;
import com.globalmentor.text.directory.vcard.*;

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

	/**The local copy of the telephone types.*/
	private Set<Telephone.Type> telephoneTypes=EnumSet.noneOf(Telephone.Type.class);

		/**@return The intended use.*/
		protected Set<Telephone.Type> getTelephoneTypes() {return telephoneTypes;}

		/**Sets the telephone type.
		@param telephoneTypes The intended use.
		*/
		protected void setTelephoneTypes(final Set<Telephone.Type> telephoneTypes)
		{
			final Set<Telephone.Type> oldTelephoneTypes=this.telephoneTypes;	//get the old telephone types
			if(!oldTelephoneTypes.equals(telephoneTypes))	//if the telephone types are really changing
			{
				this.telephoneTypes=telephoneTypes;	//store the telephone types locally
				setModified(true);	//show that we've changed the telephone type
				telephoneTypeButton.setText(	//update the telephone type button
						!telephoneTypes.isEmpty()	//if there is a telephone type
						? Telephone.getTelephoneTypeString(telephoneTypes)	//show it
						: "");	//if there is no telephone type, show nothing
			}
		}

	/**Shows or hides the telephone number labels.
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
			setTelephoneTypes(telephone.getTelephoneTypes());	//set and update the telephone type
		}
		else	//if there is no telephone information, clear the fields
		{
			setTelephoneTypes(EnumSet.of(Telephone.DEFAULT_TELEPHONE_TYPE));	//set the default telephone type
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
			final Set<Telephone.Type> telephoneTypes=getTelephoneTypes();	//get the telephone type
			try
			{
				return new Telephone(telephoneNumber, telephoneTypes);	//create and return telephone information representing the entered information
			}
			catch(final ArgumentSyntaxException syntaxException)	//if the information isn't a valid telephone number (this should never happen, as we just received a valid telephone number)
			{
				return null;	//show that we don't understand the entered information
			}
		}
		else	//if no telephone number was entered
		{
			return null;	//don't return a telephone number
		}
	}

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
			//construct a telephone panel with the telephone number and type, or the default type if no telephone is given
		this(telephone, telephone!=null ? telephone.getTelephoneTypes() : EnumSet.of(Telephone.DEFAULT_TELEPHONE_TYPE)); 
	}

	/**Telephone number and telephone type constructor.
	@param telephone The telephone information to place in the fields, or
		<code>null</code> if default information should be displayed.
	@param telephoneTypes The intended use.
	*/
	public TelephonePanel(final TelephoneNumber telephoneNumber, final Set<Telephone.Type> telephoneTypes)
	{
		super(new GridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		editTelephoneTypeAction=new EditTelephoneTypeAction();
		telephoneTypeButton=new JButton(getEditTelephoneTypeAction());
		telephoneTypeButton.setHorizontalTextPosition(SwingConstants.LEFT);	//TODO testing
//TODO fix		telephoneTypeButton.setBorder(null);	//G**testing
		telephoneNumberPanel=new TelephoneNumberPanel();	
//TODO del		buttonCount=0;	//TODO fix all this now that we don't allow extra buttons
		setDefaultFocusComponent(telephoneNumberPanel);	//set the default focus component
		initialize();	//initialize the panel
		setTelephoneNumber(telephoneNumber);	//set the given telephone number
		setTelephoneTypes(telephoneTypes);	//set the given telephone type
		setModified(false);	//show that the information has not yet been modified
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		final JLabel imageLabel=new JLabel(IconResources.getIcon(IconResources.PHONE_ICON_FILENAME)); //create a label with the image		
		add(telephoneTypeButton, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(imageLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(telephoneNumberPanel, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Containers.NO_INSETS, 0, 0));
	}
	
	/**Adds a new button based upon the given action and positions it correctly.
	@param action The new action for which a button should be added.
	@return The added button representing the given action.
	*/
/*TODO del
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
/*TODO del if not needed	
	public static int askTelephoneType(final Component parentComponent, final int telephoneType)
	{
		final TelephoneTypePanel telephoneTypePanel=new TelephoneTypePanel(telephoneType);	//create a new panel with our current telephone type 
			//ask for the new telephone type; if they accept the changes
		if(OptionPane.showConfirmDialog(parentCompent, newResourcePanel, "New Resource", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)	//TODO i18n
		{
		
		}
*/
	
	/**Asks the user for a new telephone type and updates the value.
	@return <code>true</code> if the user accepted the changes and the type was
		updated, otherwise <code>false</code> if the user cancelled.
	*/
	public boolean editTelephoneType()
	{
		final TelephoneTypePanel telephoneTypePanel=new TelephoneTypePanel(getTelephoneTypes());	//create a new panel with our current telephone type 
			//ask for the new telephone type; if they accept the changes
		if(BasicOptionPane.showConfirmDialog(this, telephoneTypePanel, "Telephone Intended Uses", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)	//TODO i18n
		{
			setTelephoneTypes(telephoneTypePanel.getTelephoneTypes());	//update the telephone type
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
			super("Type");	//create the base class TODO i18n
			putValue(SHORT_DESCRIPTION, "Edit type");	//set the short description TODO i18n
			putValue(LONG_DESCRIPTION, "Edit the intended usage type for this telephone number.");	//set the long description TODO i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_T));  //set the mnemonic key TODO i18n
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

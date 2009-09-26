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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.util.*;
import javax.swing.*;

import com.globalmentor.awt.Containers;
import com.globalmentor.java.*;
import com.globalmentor.swing.*;
import com.globalmentor.text.directory.vcard.*;

/**A panel allowing entry of the "LABEL" type of a vCard <code>text/directory</code>
	profile as defined in <a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class LabelPanel extends BasicVCardPanel
{

	/**The action for editing the address type.*/
	private final Action editAddressTypeAction;

		/**@return The action for editing the address type.*/
		public Action getEditAddressTypeAction() {return editAddressTypeAction;}
	
	/**The address type button.*/
	private final JButton addressTypeButton;

	/**The local copy of the address type.*/
	private int addressType;

		/**@return The delivery address type, a combination of
			<code>Address.XXX_ADDRESS_TYPE</code> constants ORed together.
		*/
		protected int getAddressType() {return addressType;}

		/**Sets the address type.
		@param addressType The delivery address type, one or more of the
			<code>Address.XXX_ADDRESS_TYPE</code> constants ORed together.
		*/
		protected void setAddressType(final int addressType)
		{
			final int oldAddressType=this.addressType;	//get the old address type
			if(oldAddressType!=addressType)	//if the address type is really changing
			{
				this.addressType=addressType;	//store the address type locally
				setModified(true);	//show that we've changed the address type
				addressTypeButton.setText(	//update the address type button
						addressType!=Address.NO_ADDRESS_TYPE	//if there is an address type
						? Address.getAddressTypeString(addressType)	//show it
						: "");	//if there is no address type, show nothing
			}
		}

	/**The label text pane.*/
	private final JTextPane labelTextPane;

		/**@return The label text pane.*/
		public JTextPane getLabelTextPane() {return labelTextPane;}

	/**The action for selecting the language of the name.*/
	private final SelectLanguageAction selectLanguageAction;

		/**@return The action for selecting the language of the name.*/
		public SelectLanguageAction getSelectLanguageAction() {return selectLanguageAction;}

	/**Places the label information into the various fields.
	@param label The label to place in the fields, or <code>null</code> if no
		information should be displayed.
	*/
	public void setLabel(final Label label)
	{
		if(label!=null)	//if there is a label
		{
			labelTextPane.setText(label.getText());	//put the label in the text pane
			setAddressType(label.getAddressType());
			selectLanguageAction.setLocale(label.getLocale());
		}
		else	//if there is no address, clear the fields
		{
			labelTextPane.setText("");
			setAddressType(Address.DEFAULT_ADDRESS_TYPE);
			selectLanguageAction.setLocale(null);
		}
	}
	
	/**@return An object representing the label information entered, or
		<code>null</code> if no label was entered.
	*/
	public Label getLabel()
	{
		final String labelText=Strings.getNonEmptyString(labelTextPane.getText().trim());
		if(labelText!=null)	//if label information was entered
		{
			final int addressType=getAddressType();
			final Locale locale=selectLanguageAction.getLocale();			
			return new Label(labelText, addressType, locale);	//create and return a label representing the entered information
		}
		else	//if no label information was entered
		{
			return null;	//show that there was no address
		}
	}

	/**Default constructor.*/
	public LabelPanel()
	{
		this(new Label());	//initialize with a default address
	}

	/**Label constructor.
	@param label The label to place in the fields, or <code>null</code> if no
		information should be displayed.
	*/
	public LabelPanel(final Label label)
	{
		super(new GridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		editAddressTypeAction=new EditAddressTypeAction();
		addressTypeButton=new JButton(getEditAddressTypeAction());
		addressTypeButton.setHorizontalTextPosition(SwingConstants.LEFT);
		selectLanguageAction=new SelectLanguageAction(null, this);
		labelTextPane=new JTextPane();
		setIcon(IconResources.getIcon(IconResources.NOTE_ICON_FILENAME));
		setDefaultFocusComponent(labelTextPane);	//set the default focus component
		initialize();	//initialize the panel
		setLabel(label);	//set the label
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		final PropertyChangeListener modifyLocalePropertyChangeListener=createModifyPropertyChangeListener(SelectLanguageAction.LOCALE_PROPERTY_NAME);	//create a property change listener to change the modified status when the locale property changes
			//TODO turn off tab-handling for labelAddressTextPane
		labelTextPane.getDocument().addDocumentListener(getModifyDocumentListener());
		final JScrollPane labelScrollPane=new JScrollPane(labelTextPane);
/*G***del if not needed
		labelScrollPane.setMinimumSize(new Dimension(streetAddressScrollPane.getMinimumSize().width, localityTextField.getPreferredSize().height*3));
		streetAddressScrollPane.setPreferredSize(streetAddressScrollPane.getMinimumSize());
*/
		getSelectLanguageAction().addPropertyChangeListener(modifyLocalePropertyChangeListener);
		final JButton selectLanguageButton=createSelectLanguageButton(getSelectLanguageAction());
		add(addressTypeButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Containers.NO_INSETS, 0, 0));
		add(selectLanguageButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, Containers.NO_INSETS, 0, 0));
		add(labelScrollPane, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Containers.NO_INSETS, 0, 0));
	}

	/**Asks the user for a new delivery address type and updates the value.
	@return <code>true</code> if the user accepted the changes and the type was
		updated, otherwise <code>false</code> if the user cancelled.
	*/
	public boolean editAddressType()
	{
		final AddressTypePanel addressTypePanel=new AddressTypePanel(getAddressType());	//create a new panel with our current address type 
			//ask for the new address type; if they accept the changes
		if(BasicOptionPane.showConfirmDialog(this, addressTypePanel, "Delivery Address Type", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)	//G***i18n
		{
			setAddressType(addressTypePanel.getAddressType());	//update the address type
			return true;	//show that the user accepted the changes and that they were updated		
		}
		else	//if the user cancels
		{
			return false;	//show that the action was cancelled
		}
	}

	/**Action for editing the delivery address type.*/
	class EditAddressTypeAction extends AbstractAction
	{
		/**Default constructor.*/
		public EditAddressTypeAction()
		{
			super("Type");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Edit type");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Edit the delivery address type for this label.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_T));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.PROPERTY_ICON_FILENAME)); //load the correct icon
		}
	
		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			editAddressType();	//edit the address type
		}
	}

}

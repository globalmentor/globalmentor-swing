package com.garretwilson.swing.text.directory.vcard;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.garretwilson.awt.*;
import com.garretwilson.text.directory.vcard.*;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.swing.*;
import com.garretwilson.swing.border.*;

/**A panel allowing entry of one or more telecommunication types
	(e.g. <code>TEL</code> and <code>EMAIL</code>) of a vCard <code>text/directory</code>
	profile as defined in <a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class TelecommunicationsPanel extends ContentPanel
{

	/**The action for adding a new telephone.*/
	private final Action addTelephoneAction;

		/**@return The action for adding a new telephone.*/
		public Action getAddTelephoneAction() {return addTelephoneAction;}

	/**The action for adding a new email address.*/
	private final Action addEmailAction;

		/**@return The action for adding a new email address.*/
		public Action getAddEmailAction() {return addEmailAction;}

	/**The action for removing an address.*/
//G***fix	private final Action removeAddressAction;

		/**@return The action for removing an address.*/
//G***fix		public Action getRemoveAddressAction() {return removeAddressAction;}

	/**@return The content component in which the telecommunications panels will be placed.*/
	protected DefaultPanel getContentPanel() {return (DefaultPanel)getContentComponent();}

	/**Places the addresses into the tabs.
	@param addresses The addresses to place in the tabs. If the array is empty,
		a default address will be placed in the first tab.
	*/
/*G***fix
	public void setAddresses(Address[] addresses)
	{
		if(addresses.length<1)	//if there isn't at least one address
			addresses=new Address[]{new Address()};	//create a new array containing a single default address
		getTabbedPane().removeAll();	//remove all tabs
		for(int i=0; i<addresses.length; ++i)	//look at each of the addresses
		{
			addAddress(addresses[i]);	//add this address to the tabbed pane
		}
		setDefaultFocusComponent(getTabbedPane().getComponentAt(0));	//set the default focus component to be the first tab (we'll always have at least one tab)
	}
*/
	
	/**@return An array of entered addresses.*/
/*G***fix
	public Address[] getAddresses()
	{
		final Address[] addresses=new Address[getTabbedPane().getTabCount()];	//create an array of addresses, based upon the number of tabs
		for(int i=0; i<addresses.length; ++i)	//look at each tab
		{
			final AddressPanel addressPanel=(AddressPanel)getTabbedPane().getComponentAt(i);	//get this address panel
			addresses[i]=addressPanel.getAddress();	//get this address
		}
		return addresses;	//return the addresses we collected
	}
*/

	/**Default constructor.*/
	public TelecommunicationsPanel()
	{
		this(null, null);	//construct a telecommunications panel with default telephones and email addresses
	}

	/**Telephone and email constructor.
	@param telephones The telephone numbers to display, or <code>null</code> if
		default telephones should be added.
	@param emails The email addresses to display, or <code>null</code> if
		default email addresses should be added.
	*/
	public TelecommunicationsPanel(final Telephone[] telephones, final String[] emails)
	{
		super(new DefaultPanel(new GridBagLayout()), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		addTelephoneAction=new AddTelephoneAction();
		addEmailAction=new AddEmailAction();
//G***fix		removeAddressAction=new RemoveAddressAction();
		initialize();	//initialize the panel
//G***fix		setAddresses(addresses);	//set the addresses to those given
	}

	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		setBorder(BorderUtilities.createDefaultTitledBorder());	//set a titled border
		setTitle("Telecommunications");	//G***i18n
			//add a work voice telephone
		addComponent(new TelephonePanel(null, Telephone.WORK_TELEPHONE_TYPE|Telephone.VOICE_TELEPHONE_TYPE));	
			//add a home voice telephone
		addComponent(new TelephonePanel(null, Telephone.HOME_TELEPHONE_TYPE|Telephone.VOICE_TELEPHONE_TYPE));
			//add a mobile voice telephone
		addComponent(new TelephonePanel(null, Telephone.CELL_TELEPHONE_TYPE|Telephone.VOICE_TELEPHONE_TYPE));
		final JPanel buttonPanel=new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(new JButton(getAddTelephoneAction()));
		buttonPanel.add(new JButton(getAddEmailAction()));
//G***fix		buttonPanel.add(new JButton(getRemoveAddressAction()));
		add(buttonPanel, BorderLayout.SOUTH);
/*G***fix gridbag
		add(buttonPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tabbedPane, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
*/
	}

	/**Updates the user interface.*/
	protected void updateStatus()
	{
		super.updateStatus();	//do the default status updating
//G***fix		getRemoveAddressAction().setEnabled(getTabbedPane().getTabCount()>1);	//don't allow all the tabs to be removed
	}

	/**Adds a telephone to the content panel.
	@param telephone The telephone to add.
	@return The telephone panel that represents the added telephone.
	*/
	public TelephonePanel addTelephone(final Telephone telephone)
	{
		final TelephonePanel telephonePanel=new TelephonePanel(telephone);	//create a new telephone panel for this telephone
		addComponent(telephonePanel);	//add the panel to the content panel
		return telephonePanel;	//return the panel we creatd for the telephone		
	}

	/**Adds a component to the content panel, positioning it correctly based upon
		the other components present.
	@param component The component to add.
	*/
	protected void addComponent(final Component component)
	{
			//add the component to the content panel, in a location based upon the components already present 
		getContentPanel().add(component, new GridBagConstraints(0, getContentPanel().getComponentCount(), 1, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, NO_INSETS, 0, 0));
		if(getParentOptionPane()!=null)	//if this panel is inside an option pane
		{
			WindowUtilities.packWindow(this);	//pack the window we're inside, if there is one, to ensure there's enough room to view this component
		}
		repaint();	//repaint ourselves (important if we're inside a JOptionPane, for instance)
	}

	/**Removes the currently selected address.
	@return <code>true</code> if the address was moved, or <code>false</code> if
		the action was cancelled or if there is only one address remaining.
	*/
/*G***fix
	public boolean removeAddress()
	{
		if(getTabbedPane().getTabCount()>1)	//if we have more than one address
		{
			final Component selectedComponent=getTabbedPane().getSelectedComponent();	//get the component selected in the tabbed pane
			if(selectedComponent!=null)	//if a component is selected
			{
					//if they really want to delete the address
				if(JOptionPane.showConfirmDialog(this, "Are you sure you want to permanently remove this address?", "Remove Address", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)	//G***i18n
				{
					getTabbedPane().remove(selectedComponent);	//remove the selected component
					return true;	//show that we removed the address
				}
			}
		}
		return false;	//show that we didn't remove the address
	}
*/

	/**Action for adding a telephone.*/
	class AddTelephoneAction extends AbstractAction
	{
		/**Default constructor.*/
		public AddTelephoneAction()
		{
			super("Add Telephone");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Add a telephone");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Add a new telephone number.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer('t'));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.PHONE_ICON_FILENAME)); //load the correct icon
		}
	
		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			final TelephonePanel telephonePanel=new TelephonePanel();	//create a default telephone panel
			if(telephonePanel.editTelephoneType())	//ask the user for the telephone type; if they accept the changes
			{
				addComponent(telephonePanel);	//add the new telephone panel
			}
			telephonePanel.requestDefaultFocusComponentFocus();	//focus on the new telephone panel
//G**del if not needed			updateStatus();	//update the status
		}
	}

	/**Action for adding an email.*/
	class AddEmailAction extends AbstractAction
	{
		/**Default constructor.*/
		public AddEmailAction()
		{
			super("Add Email");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Add an email");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Add a new email address.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer('e'));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.MAIL_ICON_FILENAME)); //load the correct icon
		}
	
		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
/**G***fix			
			final TelephonePanel telephonePanel=new TelephonePanel();	//create a default telephone panel
			if(telephonePanel.editTelephoneType())	//ask the user for the telephone type; if they accept the changes
			{
				addComponent(telephonePanel);	//add the new telephone panel
			}
			telephonePanel.requestDefaultFocusComponentFocus();	//focus on the new telephone panel
//G**del if not needed			updateStatus();	//update the status
*/
		}
	}

}

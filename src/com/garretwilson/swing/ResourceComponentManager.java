package com.garretwilson.swing;

import java.awt.Component;
import java.awt.Event;
import java.awt.event.*;
import java.io.*;
import java.net.URI;

import javax.swing.*;
import com.garretwilson.model.*;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.util.*;

/**An abstract class that manages resources, their views, and their modified
	states. This class does not actually change the displayed component in any
	container, relying on some other class to perform that function in response
	to a change in resource component state.
<p>Bound properties:</p>
<dl>
	<dt><code>RESOURCE_COMPONENT_STATE_PROPERTY</code> (<code>ResourceComponentManager.ResourceComponentState</code>)</dt>
	<dd>Indicates that the resource component state has been changed.</dd>
</dl>
@see ResourceComponentManager#ResourceComponentState
@author Garret Wilson
*/
public abstract class ResourceComponentManager extends BoundPropertyObject
{

	/**The property indicating the current resource and component.*/
	public final static String RESOURCE_COMPONENT_STATE_PROPERTY="resourceComponentState";

	/**The action for opening a resource.*/
	private final Action openAction;

		/**@return The action for opening a resource.
		@see #open
		*/
		public Action getOpenAction() {return openAction;}

	/**The action for saving the resource.*/
	private final Action saveAction;

		/**@return The action for saving the resource.
		@see #save
		*/
		public Action getSaveAction() {return saveAction;}

	/**The component to serve as a parent for file dialogs.*/
	private final Component parentComponent;

		/**@return The component to serve as a parent for file dialogs.*/
		protected Component getParentComponent() {return parentComponent;}

	/**The implementation for selecting resources.*/
	private final ResourceSelector resourceSelector;

		/**@return The implementation for selecting resources.*/
		public ResourceSelector getResourceSelector() {return resourceSelector;}

	/**The state of the resource and its view.*/
	private ResourceComponentState resourceComponentState;

		/**@return The state of the resource and its view.*/
		protected ResourceComponentState getResourceComponentState() {return resourceComponentState;}

		/**Sets the state of the resource and its view.
		<p>The component will be added appropriately to the parent component.</p>
		This is a bound property.
		@param newResourceComponentState The new state of the resource and its view.
		@see #getParentComponent()
		*/
		public void setResourceComponentState(final ResourceComponentState newResourceComponentState)
		{
			final ResourceComponentState oldResourceComponentState=resourceComponentState; //get the old value
			if(oldResourceComponentState!=newResourceComponentState)  //if the value is really changing
			{
				resourceComponentState=newResourceComponentState; //update the value
				getSaveAction().setEnabled(newResourceComponentState!=null);	//only enable the save action when there is a component open
					//show that the property has changed
				firePropertyChange(RESOURCE_COMPONENT_STATE_PROPERTY, oldResourceComponentState, newResourceComponentState);
			}
		}

	/**Parent component and resource selector constructor
	@param parentComponent The component to serve as a parent for error messages.
	@param resourceSelector The implementation to use for selecting resources.
	*/
	public ResourceComponentManager(final Component parentComponent, final ResourceSelector resourceSelector)
	{
		this.parentComponent=parentComponent;	//save the parent component
		this.resourceSelector=resourceSelector;	//save the resource selector 
		saveAction=new SaveAction();  //create the save action
		saveAction.setEnabled(false);	//the save action is disable by default, as there's nothing to save
		openAction=new OpenAction();  //create the open action
	}

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
/*G***fix
	public void updateStatus()
	{
		super.updateStatus();	//do the default updating
		getSaveAction().setEnabled(isModified());	//only enable saving when the resource is modified
	}
*/

	/**Determines if the panel can close. If the document is modified, this
		version asks if the information should be be saved (yes, no, or cancel).
		If so, <code>save()</code> is called.
	@return <code>true</code> if the panel is unmodified or saving was successful
		or declined, <code>false</code> if the panel cannot close because saving
		was canceled.
	@see #save
	*/
/*TODO fix canClose()
	public boolean canClose()
	{
		boolean canClose=super.canClose();	//ask the parent class if it can close 
		if(canClose)	//if the parent class doesn't care if we close
		{
			if(isModified())	//see if the document has been modified
			{
				final Resource resource=getModel().getResource();	//get the resource we represent
				if(resource!=null)	//if we represent a resource
					//see if they want to save the changes
				switch(JOptionPane.showConfirmDialog(this, "Save modified resource "+getModel().getRDFResource().getReferenceURI()+ "?", "Resource Modified", JOptionPane.YES_NO_CANCEL_OPTION))	//G***i18n
				{
					case JOptionPane.YES_OPTION:	//if they want to save the changes
						return save();	//save the selected resource and report whether the save was successful
					case JOptionPane.NO_OPTION:	//if they do not want to save the changes
						return true;	//allow the resource to close
					default:	//if they want to cancel (they pressed the cancel button *or* they just hit Esc)
						return false;	//don't allow the resource to close
				}
			}
			else	//if the resource isn't modified
				return true;	//allow it to close
		}
		return canClose;	//return whether or not we can close
	}
*/

	/**Unloads the open resource, if any, and resets to defaults.
	<p>This version does nothing.</p>
	*/
	public void close()	//TODO should this be renamed? right now, we're using it as an unconditional close, so canClose() is not checked; we could add a force parameter
	{
	}

	/**Opens a resource.
	@return <code>true</code> if the resource was successfully opened, or
		<code>false</code>if the operation was canceled.
	@see #open(Resource)
	*/
	public boolean open()
	{
		return open((Resource)null);	//open without yet knowing which resource to open
	}

	/**Opens a resource from the location specified.
	@param referenceURI The URI of the resource to open.
	@return <code>true</code> if the resource was successfully opened, or
		<code>false</code>if the operation was canceled.
	*/
	public boolean open(final URI referenceURI)
	{
		try
		{
			final Resource resource=getResourceSelector().getResource(referenceURI);	//get a description of the resource
			return open(resource);	//open the resource
		}
		catch(final IOException ioException)	//if there is an error opening the resource
		{
			SwingApplication.displayApplicationError(getParentComponent(), ioException);	//display the error to the user
		}
		return false;	//show that we couldn't open anything, for some reason
	}

	/**Opens the specified resource.
	@param resource The resource to open, or <code>null</code> if a resource
		should be chosen.
	@return <code>true</code> if the resource was successfully opened, or
		<code>false</code>if the operation was canceled.
	@see ResourceSelector#selectInputResource(Resource)
	@see #setResourceComponentState(ResourceComponentState)
	*/
	protected boolean open(Resource resource)
	{
		try
		{
			if(resource==null)	//if no resource was indicated
			{
				final ResourceComponentState resourceComponentState=getResourceComponentState();	//get the current resource component state
					//ask for a resource for input
				resource=getResourceSelector().selectInputResource(resourceComponentState!=null ? resourceComponentState.getResource() : null);
			}
			if(resource!=null)  //if we now have a valid resource
			{
				assert resource.getReferenceURI()!=null : "Selected resource has no URI.";
				final ResourceComponentState newResourceComponentState=read(resource);	//try to open the resource
				if(newResourceComponentState!=null)	//if we succeed in opening the resource
				{
					setResourceComponentState(newResourceComponentState);	//change to the new state
					return true;	//show that we successfully opened the resource
				}
			}
		}
		catch(final IOException ioException)	//if there is an error opening the resource
		{
			SwingApplication.displayApplicationError(getParentComponent(), ioException);	//display the error to the user
		}
		return false;	//show that we couldn't open anything, for some reason
	}

	/**Reads the specified resource.
	@param resource The resource to open.
	@return An object representing the opened resource and its state, or
		<code>null</code> if the process was canceled.
	@exception IOException Thrown if there was an error reading the resource.
	*/
	protected ResourceComponentState read(final Resource resource) throws IOException
	{
//TODO change the cursor while we open
			//get an input stream to the resource
		final InputStream inputStream=getResourceSelector().getInputStream(resource.getReferenceURI());
		try
		{
			final Component component=read(resource, inputStream);	//read the component from the input stream
			final ResourceComponentState resourceComponentState=new ResourceComponentState(resource, component);	//create a new state for the resource
			return resourceComponentState;	//save the component state
		}
		finally
		{
			inputStream.close();	//always close the input stream
		}
	}

	/**Reads a resource from an input stream.
	@param resource The resource to open.
	@param inputStream The input stream from which to read the data.
	@throws IOException Thrown if there is an error reading the data.
	*/ 
	protected abstract Component read(final Resource resource, final InputStream inputStream) throws IOException;

	/**Saves the current resource.
	<p>If the current resource component is verifiable, the component is first
		verified.</p>
	<p>By default if no location is available, the <code>saveAs</code> method is
		called. If a location is available, <code>save(Resource)</code> is called.</p> 
	<p>For normal operation, this method should not be modified and
		<code>save(Resource)</code> should be overridden.</p>
	@return <code>true</code> if there was a resource to save and the operation
		was not canceled.
	@see #save(Resource)
	@see #saveAs
	@see #getResourceComponentState()
	*/
	public boolean save()
	{
		final ResourceComponentState resourceComponentState=getResourceComponentState();	//get the current resource component state
		if(resourceComponentState!=null)	//if a resource is open
		{
				//if the component is verifiable, make sure it verifies before we save the contents
			if(!(resourceComponentState.getComponent() instanceof Verifiable) || ((Verifiable)resourceComponentState.getComponent()).verify())
			{
				if(resourceComponentState.getResource()!=null && resourceComponentState.getResource().getReferenceURI()!=null) //if we have a URI
				{
					try
					{
						write(resourceComponentState.getResource(), resourceComponentState.getComponent()); //save using the resource we already have
						return true;
					}
					catch(IOException ioException)	//if there is an error saving the resource
					{
						SwingApplication.displayApplicationError(getParentComponent(), ioException);	//display the error to the user
					}
				}
				else  //if we don't have a URI
				{
					return saveAs(resourceComponentState); //save with a user-specified URI
				}
			}
		}
		return false;	//show that we couldn't save the contents because the component didn't verify or there was no resource to save, for example
	}

	/**Saves the current resource after first asking the user for a URI.
	@return <code>true</code> if there was a resource to save and the operation
		was not canceled.
	*/
	public boolean saveAs()
	{
		final ResourceComponentState resourceComponentState=getResourceComponentState();	//get the current resource component state
		if(resourceComponentState!=null)	//if a resource is open
		{
			return saveAs(resourceComponentState); //save the resource using a user-specified URI
		}
		else  //if we have no resource
			return false; //show that we couldn't save the resource
	}

	/**Saves the open resource after first asking for a URI.
	@param resourceComponentState The state information of the resource to save.
	@return <code>true</code> if the operation was not canceled.
	@see #setResourceComponentState(ResourceComponentState)
	*/
	protected boolean saveAs(final ResourceComponentState resourceComponentState)
	{
		try
		{
			final Resource resource=getResourceSelector().selectOutputResource(resourceComponentState.getResource());  //get the resource to use for saving
			if(resource!=null)  //if a valid resource was returned
			{
				write(resource, resourceComponentState.getComponent()); //save the resource
				resourceComponentState.setResource(resource);	//change the resource of the component state
/*G***del if not needed
				if(!ObjectUtilities.equals(resourceComponentState.getResource().getReferenceURI(), uri))	//if the URI isn't the same //TODO fix or delete comment: wasn't updated (e.g. the overridden saveFile() didn't call the version in this class)
				{
					resourceComponentState.getResource().setReferenceURI(uri);	//update the resource description's URI
				}
*/
	//G***fix or del				setFile(file);  //update the file, just in case they override saveFile() and don't call this version
				return true;	//show that the resource was successfully saved
			}
		}
		catch(IOException ioException)	//if there is an error saving the resource
		{
			SwingApplication.displayApplicationError(getParentComponent(), ioException);	//display the error to the user
		}
		return false;  //show that we couldn't save the resource		
	}

	/**Writes a resource.
	@param resource The resource to save.
	@param component The component that contains the data to save.
	@exception IOException Thrown if there was an error writing the resource.
	*/
	protected void write(final Resource resource, final Component component) throws IOException
	{
//TODO change the cursor while we save
		final OutputStream outputStream=getResourceSelector().getOutputStream(resource.getReferenceURI());	//get an output stream to this resource's URI
		try
		{
			write(resource, component, outputStream);	//write the component to the output stream
		}
		finally
		{
			outputStream.close();	//always close the output stream
		}
	}

	/**Saves a resource to an output stream.
	@param resource The resource to save.
	@param component The component that contains the data to save.
	@param outputStream The input stream to which to write the data.
	@throws IOException Thrown if there is an error writing the data.
	*/ 
	protected abstract void write(final Resource resource, final Component component, final OutputStream outputStream) throws IOException;

	/**Action for opening a resource.*/
	class OpenAction extends AbstractAction
	{
		/**Default constructor.*/
		public OpenAction()
		{
			super("Open...");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Open a resource");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Display a dialog to select a file, and then load the selected resource.");	//set the long description G***Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.OPEN_ICON_FILENAME)); //load the correct icon
			putValue(ActionManager.MENU_ORDER_PROPERTY, new Integer(ActionManager.FILE_OPEN_MENU_ACTION_ORDER));	//set the order
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			open();	//open a resource
		}
	}
	
	/**Action for saving a resource.*/
	class SaveAction extends AbstractAction
	{
		/**Default constructor.*/
		public SaveAction()
		{
			super("Save");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Save the open resource");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Save the currently open resource.");	//set the long description G***Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_V));  //set the mnemonic key; for some reason, 's' causes the action to be activated when Alt+F4 is pressed G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.SAVE_ICON_FILENAME)); //load the correct icon
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK)); //add the accelerator G***i18n
			putValue(ActionManager.MENU_ORDER_PROPERTY, new Integer(ActionManager.FILE_SAVE_MENU_ACTION_ORDER));	//set the order
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			save();	//save the resource
		}
	}

	/**A representation of a resource and its associated view.
	@author Garret Wilson
	*/
	public class ResourceComponentState extends DefaultResourceState
	{

		/**Sets the resource being described.
		<p>This method delegates to the parent class, and is declared here solely
			so that the component manager can change the resource represented.
		@param resource The new resource to describe.
		@exception IllegalArgumentException Thrown if the resource is <code>null</code>.
		*/
		protected void setResource(final Resource resource)
		{
			super.setResource(resource);	//set the resource object
		}

		/**The component that acts as a view to the resource.*/
		private Component component;

			/**@return The component that acts as a view to the resource.*/
			public Component getComponent() {return component;}

		/**Constructs a resource state with a resource and a component.
		@param resource The description of the resource.
		@param component The component that represents a view of the resource.
		*/
		public ResourceComponentState(final Resource resource, final Component component)
		{
			super(resource);	//construct the parent class
			this.component=component;	//save the resource component
		}

	}

}

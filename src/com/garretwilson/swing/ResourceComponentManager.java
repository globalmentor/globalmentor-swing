package com.garretwilson.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Event;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
@param <R> The type of resource the components of which are being managed.
@see ResourceComponentManager#ResourceComponentState
@author Garret Wilson
*/
public abstract class ResourceComponentManager<R extends Resource> extends BoundPropertyObject implements CanClosable
{

	/**The property indicating the current resource and component.*/
	public final static String RESOURCE_COMPONENT_STATE_PROPERTY="resourceComponentState";

	/**The action for opening a resource.*/
	private final Action openAction;

		/**@return The action for opening a resource.
		@see #open
		*/
		public Action getOpenAction() {return openAction;}

	/**The action for closing a resource.*/
	private final Action closeAction;

		/**@return The action for closing a resource.
		@see #close
		*/
		public Action getCloseAction() {return closeAction;}

	/**The action for saving the resource.*/
	private final Action saveAction;

		/**@return The action for saving the resource.
		@see #save
		*/
		public Action getSaveAction() {return saveAction;}

	/**The action for reverting a resource.*/
	private final Action revertAction;

		/**@return The action for reverting a resource.
		@see #revert
		*/
		public Action getRevertAction() {return revertAction;}

	/**The component to serve as a parent for file dialogs.*/
	private final Component parentComponent;

		/**@return The component to serve as a parent for file dialogs.*/
		protected Component getParentComponent() {return parentComponent;}

	/**The implementation for selecting resources.*/
	private final ResourceSelector<R> resourceSelector;

		/**@return The implementation for selecting resources.*/
		public ResourceSelector<R> getResourceSelector() {return resourceSelector;}

	/**The state of the resource and its view.*/
	private ResourceComponentState<R> resourceComponentState;

		/**@return The state of the resource and its view.*/
		protected ResourceComponentState<R> getResourceComponentState() {return resourceComponentState;}

		/**Sets the state of the resource and its view.
		<p>The component will be added appropriately to the parent component.</p>
		This is a bound property.
		@param newResourceComponentState The new state of the resource and its view.
		@see #getParentComponent()
		*/
		public void setResourceComponentState(final ResourceComponentState<R> newResourceComponentState)
		{
			final ResourceComponentState<R> oldResourceComponentState=resourceComponentState; //get the old value
			if(oldResourceComponentState!=newResourceComponentState)  //if the value is really changing
			{
				if(oldResourceComponentState!=null && oldResourceComponentState.getComponent() instanceof Modifiable)	//if the old component was modifiable
				{
					oldResourceComponentState.getComponent().removePropertyChangeListener(Modifiable.MODIFIED_PROPERTY, getUpdateStatusModifiedPropertyChangeListener());	//remove our modifiable listener
				}
				resourceComponentState=newResourceComponentState; //update the value
				if(newResourceComponentState!=null && newResourceComponentState.getComponent() instanceof Modifiable)	//if the new component is modifiable
				{
					newResourceComponentState.getComponent().addPropertyChangeListener(Modifiable.MODIFIED_PROPERTY, getUpdateStatusModifiedPropertyChangeListener());	//listen for component modifications
				}
				updateStatus();	//update the status of the actions
					//show that the property has changed
				firePropertyChange(RESOURCE_COMPONENT_STATE_PROPERTY, oldResourceComponentState, newResourceComponentState);
			}
		}

	/**The listener that updates the status when the component is modified.*/
	private final PropertyChangeListener updateStatusModifiedPropertyChangeListener;

		/**@return The listener that updates the status when the component is modified.*/
		protected PropertyChangeListener getUpdateStatusModifiedPropertyChangeListener() {return updateStatusModifiedPropertyChangeListener;}

	/**Parent component and resource selector constructor
	@param parentComponent The component to serve as a parent for error messages.
	@param resourceSelector The implementation to use for selecting resources.
	*/
	public ResourceComponentManager(final Component parentComponent, final ResourceSelector<R> resourceSelector)
	{
		this.parentComponent=parentComponent;	//save the parent component
		this.resourceSelector=resourceSelector;	//save the resource selector
		updateStatusModifiedPropertyChangeListener=new PropertyChangeListener()	//create a property change listener to update our status when modification occurs
			{
				public void propertyChange(final PropertyChangeEvent propertyChangeEvent)
				{
					updateStatus();
				}
			};
		openAction=new OpenAction();  //create the open action
		closeAction=new CloseAction();  //create the close action
		closeAction.setEnabled(false);	//the close action is disable by default, as there's nothing to close
		saveAction=new SaveAction();  //create the save action
		saveAction.setEnabled(false);	//the save action is disable by default, as there's nothing to save
		revertAction=new RevertAction();  //create the revert action
		revertAction.setEnabled(false);	//the revert action is disable by default, as there's nothing to revert
	}

	/**Updates the states of the actions, including enabled/disabled status, proxied actions, etc.*/
	public void updateStatus()
	{
		final ResourceComponentState<R> resourceComponentState=getResourceComponentState();	//get the current state of the resource component
		getCloseAction().setEnabled(resourceComponentState!=null);	//only enable the close action when there is a component open
		if(resourceComponentState!=null)	//if we have a resource and its component
		{
			final Component component=resourceComponentState.getComponent();	//get the resource component
			assert component!=null : "No component associated with resource.";
			if(component instanceof Modifiable)	//if the component is modifiable
			{
				final boolean isModified=((Modifiable)component).isModified();	//see if the component has been modified
				getSaveAction().setEnabled(isModified);	//only enable the save action when the component is modified
				getRevertAction().setEnabled(isModified);	//only enable the revert action when the component is modified				
			}
			else	//if the component is not modifiable
			{
				getSaveAction().setEnabled(true);	//always allow save
				getRevertAction().setEnabled(true);	//always allow revert
				
			}
		}
		else	//if there is no resource and component
		{
			getSaveAction().setEnabled(false);	//there's nothing to save
			getRevertAction().setEnabled(false);	//there's nothing to revert
		}
	}

	/**Determines if the current resource, if any, and its component can close.
	@return <code>true</code> if the resource, if any, and its component can close.
	@see #canClose(ResourceComponentState)
	*/
	public boolean canClose()
	{
		final ResourceComponentState<R> resourceComponentState=getResourceComponentState();	//get the current resource component state
			//if we have a resource component state, see if we can close it
		return resourceComponentState!=null? canClose(resourceComponentState) : true;
	}
	
	/**Determines if a resource and its component can close.
		This verion asks the resource component if it can close, if that component
		implements <code>CanClosable</code>.
	@param resourceComponentState The state information of the resource that
		should be checked for closing.
	@return <code>true</code> if the resource and its component can close.
	@see CanClosable#canClose()
	*/
	protected boolean canClose(final ResourceComponentState<R> resourceComponentState)
	{
		final Component component=resourceComponentState.getComponent();	//get the resource component
			//if the component can be checked for closing, and it doesn't wish to close
		if(component instanceof CanClosable && !((CanClosable)component).canClose())
		{
			return false;	//the component doesn't want to close for some reason
		}
		if(component instanceof Modifiable && ((Modifiable)component).isModified())	//if the component says it can close, but it has been modified
		{
			final R resource=resourceComponentState.getResource();	//get the resource
			final String resourceURIString=resource.getReferenceURI()!=null ? resource.getReferenceURI().toString() : "";	//get a string representing the resource URI, if there is one
				//see if they want to save the changes
			switch(BasicOptionPane.showConfirmDialog(component, "Save modified resource "+resourceURIString+ "?", "Resource Modified", BasicOptionPane.YES_NO_CANCEL_OPTION))	//G***i18n
			{
				case BasicOptionPane.YES_OPTION:	//if they want to save the changes
					return save();	//save the selected resource and report whether the save was successful TODO use a save method that allows the resource to be specified
				case BasicOptionPane.NO_OPTION:	//if they do not want to save the changes
					return true;	//allow the resource to close
				default:	//if they want to cancel (they pressed the cancel button *or* they just hit Esc)
					return false;	//don't allow the resource to close
			}
		}
		return true;	//default to allowing the component to be closed
	}

	/**Unloads the open resource, if any.
	If no resource is open, no action occurs.
	@see #getResourceComponentState()
	*/
	public void close()
	{
		/*G***decide which semantics we like
		final ResourceComponentState resourceComponentState=getResourceComponentState();	//get the current resource component state
		if(resourceComponentState!=null)	//if a resource is open
		{
			if(canClose(resourceComponentState))	//if we can close the open resource
			{
				close(resourceComponentState);	//close this resource component state
			}
		}
*/
		final ResourceComponentState<R> resourceComponentState=getResourceComponentState();	//get the current resource component state
		if(resourceComponentState!=null)	//if a resource is open
		{
			close(resourceComponentState);	//close this resource component state
		}
	}

	/**Closes the given resource.
	This version simply sets the current resource state to <code>null</code>.
	@param resourceComponentState The state information of the resource that
		should be checked for closing.
	*/
	protected void close(final ResourceComponentState<R> resourceComponentState)
	{
		setResourceComponentState(null);	//close the resource by switching to no resource G***maybe transfer this up to close()---or maybe do nothing at all
	}

	/**Opens a resource.
	@return <code>true</code> if the resource was successfully opened, or
		<code>false</code>if the operation was canceled.
	@see #open(Resource)
	*/
	public boolean open()
	{
		return open((R)null);	//open without yet knowing which resource to open
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
			final R resource=getResourceSelector().getResource(referenceURI);	//get a description of the resource
			final Cursor originalCursor=ComponentUtilities.setPredefinedCursor(getParentComponent(), Cursor.WAIT_CURSOR);	//change the cursor
			try
			{
				return open(resource);	//open the resource
			}
			finally
			{
				getParentComponent().setCursor(originalCursor);	//always change the component's cursor back to normal
			}
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
	protected boolean open(R resource)
	{
		try
		{
			if(resource==null)	//if no resource was indicated
			{
				final ResourceComponentState<R> resourceComponentState=getResourceComponentState();	//get the current resource component state
					//ask for a resource for input
				resource=getResourceSelector().selectInputResource(resourceComponentState!=null ? resourceComponentState.getResource() : null);
			}
			if(resource!=null)  //if we now have a valid resource
			{
				assert resource.getReferenceURI()!=null : "Selected resource has no URI.";
				final ResourceComponentState<R> newResourceComponentState=read(resource);	//try to open the resource
				if(newResourceComponentState!=null)	//if we succeed in opening the resource
				{
					setResourceComponentState(newResourceComponentState);	//change to the new state
					return true;	//show that we successfully opened the resource
				}
			}
		}
		catch(final SecurityException securityException)	//if there is an error selecting the resource
		{
			SwingApplication.displayApplicationError(getParentComponent(), securityException);	//display the error to the user
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
	protected ResourceComponentState<R> read(final R resource) throws IOException
	{
//TODO change the cursor while we open
			//get an input stream to the resource
		final InputStream inputStream=getResourceSelector().getInputStream(resource.getReferenceURI());
		try
		{
			final Component component=read(resource, inputStream);	//read the component from the input stream
			final ResourceComponentState<R> resourceComponentState=new ResourceComponentState<R>(resource, component);	//create a new state for the resource
			return resourceComponentState;	//return the component state
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
	protected abstract Component read(final R resource, final InputStream inputStream) throws IOException;

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
		final ResourceComponentState<R> resourceComponentState=getResourceComponentState();	//get the current resource component state
		if(resourceComponentState!=null)	//if a resource is open
		{
				//if the component is verifiable, make sure it verifies before we save the contents
			if(!(resourceComponentState.getComponent() instanceof Verifiable) || ((Verifiable)resourceComponentState.getComponent()).verify())
			{
				assert resourceComponentState.getResource()!=null : "Resource component state does not represent a valid resource.";
				if(resourceComponentState.getResource().getReferenceURI()!=null) //if we have a URI
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
		final ResourceComponentState<R> resourceComponentState=getResourceComponentState();	//get the current resource component state
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
	protected boolean saveAs(final ResourceComponentState<R> resourceComponentState)
	{
		try
		{
			final R resource=getResourceSelector().selectOutputResource(resourceComponentState.getResource());  //get the resource to use for saving
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
		catch(final SecurityException securityException)	//if there is an error selecting the resource
		{
			SwingApplication.displayApplicationError(getParentComponent(), securityException);	//display the error to the user
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
	protected void write(final R resource, final Component component) throws IOException
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
	protected abstract void write(final R resource, final Component component, final OutputStream outputStream) throws IOException;

	/**Reverts the open resource, if any.
	If no resource is open, no action occurs.
	@see #getResourceComponentState()
	*/
	public void revert()
	{
		final ResourceComponentState<R> resourceComponentState=getResourceComponentState();	//get the current resource component state
		if(resourceComponentState!=null)	//if a resource is open
		{
//G***del if not needed			if(canClose(resourceComponentState))	//if we can close the open resource
			{
				revert(resourceComponentState);	//revert this resource component state
			}
		}
	}

	/**Reverts the given resource.
	This version does nothing.
	@param resourceComponentState The state information of the resource that
		should be checked for closing.
	*/
	protected void revert(final ResourceComponentState<R> resourceComponentState)
	{
	}

	/**Action for creating a new file.*/
	public static class NewAction extends AbstractAction
	{
		/**Default constructor.*/
		public NewAction()
		{
			super("New...");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Create a new file ");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Create a new file.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.NEW_ICON_FILENAME)); //load the correct icon
			putValue(ActionManager.MENU_ORDER_PROPERTY, new Integer(ActionManager.FILE_NEW_MENU_ACTION_ORDER));	//set the order
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
		}
	}

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

	/**Action for closing a resource.*/
	protected class CloseAction extends AbstractAction
	{
		/**Default constructor.*/
		public CloseAction()
		{
			super("Close");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Close the open resource");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Close the currently open resource.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.CLOSE_ICON_FILENAME)); //load the correct icon
		  putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.CTRL_MASK)); //add the accelerator
			putValue(ActionManager.MENU_ORDER_PROPERTY, new Integer(ActionManager.FILE_CLOSE_MENU_ACTION_ORDER));	//set the order
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			close();	//close the resource
		}
	}

	/**Action for saving a resource.*/
	protected class SaveAction extends AbstractAction
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

	/**Action for reverting a book.*/
	protected class RevertAction extends AbstractAction
	{
		/**Default constructor.*/
		public RevertAction()
		{
			super("Revert");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Revert the open resource");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Revert the currently open resource.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.REDO_ICON_FILENAME)); //load the correct icon
			putValue(ActionManager.MENU_ORDER_PROPERTY, new Integer(ActionManager.FILE_REVERT_MENU_ACTION_ORDER));	//set the order
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			revert();	//revert the resource
		}
	}

	/**A representation of a resource and its associated view.
	A resource component state always represents a valid resource and a valid
		component, although the resource may be anonymous with no reference URI.
	@param <R2> The type of resource the state of which is being stored.
	@author Garret Wilson
	*/
	public static class ResourceComponentState<R2 extends Resource> extends DefaultResourceState<R2>
	{

		/**Sets the resource being described.
		<p>This method delegates to the parent class, and is declared here solely
			so that the component manager can change the resource represented.
		@param resource The new resource to describe.
		@exception NullPointerException Thrown if the resource is <code>null</code>.
		*/
		protected void setResource(final R2 resource)
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
		@exception NullPointerException Thrown if the resource is <code>null</code>.
		*/
		public ResourceComponentState(final R2 resource, final Component component)
		{
			super(resource);	//construct the parent class
			this.component=component;	//save the resource component
		}

	}

}

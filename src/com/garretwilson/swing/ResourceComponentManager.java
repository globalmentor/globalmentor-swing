package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import com.garretwilson.io.*;
import com.garretwilson.lang.ObjectUtilities;
import com.garretwilson.model.*;
import com.garretwilson.rdf.DefaultRDFResource;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.util.*;

/**An abstract class that manages resources, their views, and their modified
	states.
@author Garret Wilson
*/
public abstract class ResourceComponentManager extends DefaultURIAccessible
{
	
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

	/**The container that hosts the resource views.*/
	private final Container container;

		/**@return The container that hosts the resource views.*/
		public Container getContainer() {return container;}

	/**The state of the resource and its view.*/
	private ResourceComponentState resourceComponentState;

		/**@return The state of the resource and its view.*/
		protected ResourceComponentState getResourceViewState() {return resourceComponentState;}

		/**Sets the state of the resource and its view.
		<p>The component will be added appropriately to the parent component.</p>
//TODO		This is a bound property.
		@param newResourceComponentState The new state of the resource and its view.
		@see #getParentComponent()
		*/
		public void setResourceComponentState(final ResourceComponentState newResourceComponentState)
		{
			final ResourceComponentState oldResourceViewState=resourceComponentState; //get the old value
			if(oldResourceViewState!=newResourceComponentState)  //if the value is really changing
			{
				resourceComponentState=newResourceComponentState; //update the value
					//TODO fix all this to be robust and generic
				((ContentPanel)getContainer()).setContentComponent(newResourceComponentState.getComponent());	//G***fix; testing
/*G***fix
				if(oldResourceViewState!=null)	//if there was an old resource component state TODO fix so that it works when we switch to no view state
				{
					getContainer().remove(oldResourceViewState.getComponent());	//remove the old component
				}
				if(newResourceComponentState!=null)	//if we now have a resource component state
				{
					container.add(newResourceComponentState.getComponent(), BorderLayout.CENTER);	//show the new component
				}
*/
					//show that the property has changed
//TODO make the resource view state bound, if needed				firePropertyChange(Model.MODEL_PROPERTY, oldModel, newModel);
			}
		}

	/**Container constructor with default access to URIs.
	@param parentContainer The component that hosts the resource views.
	*/
	public ResourceComponentManager(final Container container)
	{
		this(container, null, null);	//use the default accessibility to URIs
	}

	/**Container and URI accessibility constructor.
	@param container The container that hosts the resource views.
	@param uriInputStreamable The implementation to use for accessing a URI for
		input, or <code>null</code> if the default implementation should be used.
	@param uriOutputStreamable The implementation to use for accessing a URI for
		output, or <code>null</code> if the default implementation should be used.
	*/
	public ResourceComponentManager(final Container container, final URIInputStreamable uriInputStreamable, final URIOutputStreamable uriOutputStreamable)
	{
		super(uriInputStreamable, uriOutputStreamable);	//construct the parent class
		this.container=container;	//save the container 
		saveAction=new SaveAction();  //create the save action
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
	<p>For normal operation, this method should not be modified and
		<code>open(URI)</code> should be overridden. Multiple document
		applications may also override <code>getResourceComponentState()</code> and
		<code>setResourceComponentState()</code>.</p>
	@return <code>true</code> if the resource was successfully opened, or
		<code>false</code>if the operation was canceled.
	@see #askOpen
	@see #setResourceViewState
	*/
	public boolean open()
	{
		//G***what if we're already open?
		//G***should this be load() instead of open(?
		try
		{
			final URI uri=askOpen();	//get the URI to use
			if(uri!=null)  //if a valid URI was returned
			{
				final ResourceComponentState resourceComponentState=open(uri);	//try to open the resource
				if(resourceComponentState!=null)	//if we succeed in opening the resource
				{
					setResourceComponentState(resourceComponentState);	//change to the new state
					return true;	//show that we successfully opened the resource
				}
			}
		}
		catch(final IOException ioException)	//if there is an error opening the resource
		{
			SwingApplication.displayApplicationError(getContainer(), ioException);	//display the error to the user
		}
		return false;	//show that we couldn't open anything, for some reason
	}

	/**Asks the user for URI for opening.
	@return The URI to use for opening the resource, or <code>null</code> if the
		resource should not be opened or if the user cancels.
	*/
	protected abstract URI askOpen();

	/**Loads the resource from the location specified.
	@param URI The URI of the resource to open.
	@return An object representing the opened resource and its state, or
		<code>null</code> if the process was canceled.
	@exception IOException Thrown if there was an error reading the resource.
	*/
	protected ResourceComponentState open(final URI uri) throws IOException
	{
//TODO change the cursor while we open
			//get an input stream to the resource
		final InputStream inputStream=getInputStream(uri);
		try
		{
			final Component component=open(inputStream, uri);	//read the component from the input stream
			final Resource resource=new DefaultRDFResource(uri);	//create a new resource from the URI TODO get this from some method
			final ResourceComponentState resourceComponentState=new ResourceComponentState(resource, component);	//create a new state for the resource
			return resourceComponentState;	//save the component state
		}
		finally
		{
			inputStream.close();	//always close the input stream
		}
	}

	/**Opens a resource from an input stream.
	@param inputStream The input stream from which to read the data.
	@param baseURI The base URI of the content, or <code>null</code> if no base
		URI is available.
	@throws IOException Thrown if there is an error reading the data.
	*/ 
	public abstract Component open(final InputStream inputStream, final URI baseURI) throws IOException;

	/**Saves the current resource.
	<p>If the current resource component is verifiable, the component is first
		verified.</p>
	<p>By default if no location is available, the <code>saveAs</code> method is
		called. If a location is available, <code>save(URI)</code> is called.</p> 
	<p>For normal operation, this method should not be modified and
		<code>save(URI)</code> should be overridden.</p>
	@return <code>true</code> if there was a resource to save and the operation
		was not canceled.
	@see #save(URI)
	@see #saveAs
	@see #getResourceViewState()
	*/
	public boolean save()
	{
		final ResourceComponentState resourceComponentState=getResourceViewState();	//get the current resource component state
		if(resourceComponentState!=null)	//if a resource is open
		{
				//if the component is verifiable, make sure it verifies before we save the contents
			if(!(resourceComponentState.getComponent() instanceof Verifiable) || ((Verifiable)resourceComponentState.getComponent()).verify())
			{
				if(resourceComponentState.getResource()!=null && resourceComponentState.getResource().getReferenceURI()!=null) //if we have a URI
				{
					try
					{
						save(resourceComponentState, resourceComponentState.getResource().getReferenceURI()); //save using the URI we already have
						return true;
					}
					catch(IOException ioException)	//if there is an error saving the resource
					{
						SwingApplication.displayApplicationError(getContainer(), ioException);	//display the error to the user
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
		final ResourceComponentState resourceComponentState=getResourceViewState();	//get the current resource component state
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
			final URI uri=askSave();  //get the URI to use for saving
			if(uri!=null)  //if a valid URI was returned
			{
				save(resourceComponentState, uri); //save the resource
				if(!ObjectUtilities.equals(resourceComponentState.getResource().getReferenceURI(), uri))	//if the URI isn't the same //TODO fix or delete comment: wasn't updated (e.g. the overridden saveFile() didn't call the version in this class)
				{
					resourceComponentState.getResource().setReferenceURI(uri);	//update the resource description's URI
				}
	//G***fix or del				setFile(file);  //update the file, just in case they override saveFile() and don't call this version
				return true;	//show that the resource was successfully saved
			}
		}
		catch(IOException ioException)	//if there is an error saving the resource
		{
			SwingApplication.displayApplicationError(getContainer(), ioException);	//display the error to the user
		}
		return false;  //show that we couldn't save the resource		
	}

	/**Asks the user for a URI for saving.
	@return The URI to use for saving the resource, or <code>null</code>
		if the file should not be saved or if the user cancels.
	*/
	protected abstract URI askSave();

	/**Saves a resource at the location specified.
	@param resourceComponentState The state information of the resource to save.
	@param uri The URI at which the file should be saved.
	@exception IOException Thrown if there was an error writing the resource.
	*/
	protected void save(final ResourceComponentState resourceComponentState, final URI uri) throws IOException
	{
//TODO change the cursor while we open
		final OutputStream outputStream=getOutputStream(uri);	//get an output stream to this URI
		try
		{
			save(resourceComponentState.getComponent(), outputStream);	//write the component to the output stream
		}
		finally
		{
			outputStream.close();	//always close the output stream
		}
	}

	/**Saves a resource to an output stream.
	@param component The component that contains the data to save.
	@param outputStream The input stream to which to write the data.
	@throws IOException Thrown if there is an error writing the data.
	*/ 
	public abstract void save(final Component component, final OutputStream outputStream) throws IOException;

	/**Action for opening a resource.*/
	class OpenAction extends AbstractAction
	{
		/**Default constructor.*/
		public OpenAction()
		{
			super("Open...");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Open a resource");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Display a dialog to select a file, and then load the selected resource.");	//set the long description G***Int
			putValue(MNEMONIC_KEY, new Integer('o'));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.OPEN_ICON_FILENAME)); //load the correct icon
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
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
			putValue(MNEMONIC_KEY, new Integer('v'));  //set the mnemonic key; for some reason, 's' causes the action to be activated when Alt+F4 is pressed G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.SAVE_ICON_FILENAME)); //load the correct icon
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK)); //add the accelerator G***i18n
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
			save();	//save the resource
		}
	}

	/**A representation of a resource and its associated view.
	@author Garret Wilson
	*/
	public class ResourceComponentState extends DefaultResourceState
	{

		/**The component that acts as a view to the resource.*/
		private Component component;

			/**@return The burrow in which the resource is located.*/
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

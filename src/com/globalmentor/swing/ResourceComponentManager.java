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

package com.globalmentor.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Event;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.event.EventListenerList;

import com.globalmentor.log.Log;
import com.globalmentor.model.DefaultObjectState;
import com.globalmentor.model.Modifiable;
import com.globalmentor.model.Verifiable;
import com.globalmentor.net.Resource;
import com.globalmentor.net.ResourceSelector;

import static com.globalmentor.net.URIs.*;
import static com.globalmentor.swing.Components.*;

import com.globalmentor.util.prefs.Preferencesable;

/**An abstract class that manages resources, their views, and their modified
	states. This class does not actually change the displayed component in any
	container, relying on some other class to perform that function in response
	to a change in resource component state.
@param <R> The type of resource the components of which are being managed.
@see ResourceComponentManager#ResourceComponentState
@author Garret Wilson
*/
public abstract class ResourceComponentManager<R extends Resource> implements Preferencesable
{

	/**The list of event listeners.*/
	protected final EventListenerList eventListenerList=new EventListenerList();

	/**The preferences that should be used for this object, or <code>null</code> if the default preferences for this class should be used.*/
	private Preferences preferences=null;
	
		/**@return The preferences that should be used for this object, or the default preferences for this class if no preferences are specifically set.
		@exception SecurityException Thrown if a security manager is present and it denies <code>RuntimePermission("preferences")</code>.
		*/
		public Preferences getPreferences() throws SecurityException
		{
			return preferences!=null ? preferences: Preferences.userNodeForPackage(getClass());	//return the user preferences node for whatever class extends this one 
		}
		
		/**Sets the preferences to be used for this panel.
		@param preferences The preferences that should be used for this panel, or <code>null</code> if the default preferences for this class should be used
		*/
		public void setPreferences(final Preferences preferences)
		{
			this.preferences=preferences;	//store the preferences
		}

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

	/**The maximum number of resources (&gt;0) that can be loaded at one time, effectively determining SDI or MDI functionality.*/
	private final int maxResourceCount;

		/**@return The maximum number of resources (&gt;0) that can be loaded at one time, effectively determining SDI or MDI functionality.*/
		public int getMaxResourceCount() {return maxResourceCount;}

	/**The list of all resources and corresponding views.*/
	private final List<ResourceComponentState> resourceComponentStateList;

		/**@return The list of all resources and corresponding views.*/
		protected final Iterable<ResourceComponentState> getResourceComponentStates() {return resourceComponentStateList;}

		/**Determines if a resource and component are already present for the given URI.
		@param uri The reference URI of the resource to retrieve.
		@return The component state of the resource, if that resource is already known,
			or <code>null</code> if no resource with that URI is present.
		*/
		protected ResourceComponentState getResourceComponentState(final URI uri)
		{
			for(final ResourceComponentState resourceComponentState:resourceComponentStateList)	//for each known resource component state; this is an expensive iterative search, but a simple map won't help because some resources may not yet have assigned URIs
			{
				if(uri.equals(resourceComponentState.getResource().getURI()))	//if this resource has the requested URI
				{
					return resourceComponentState;	//return this resource and its component
				}
			}
			return null;	//indicate that we could find no corresponding resource and component
		}

		/**Determines if a resource and component are present for the given component.
		@param component The component of the resource to retrieve.
		@return The component state of the resource, if there is a resource with the given component,
			or <code>null</code> if no resource with that component is present.
		*/
		protected ResourceComponentState getResourceComponentState(final Component component)
		{
			for(final ResourceComponentState resourceComponentState:resourceComponentStateList)	//for each known resource component state; a map could be used to ameliorate this expensive operation, but component-changing functionality may be desired in the future
			{
				if(component.equals(resourceComponentState.getComponent()))	//if this resource has the requested component
				{
					return resourceComponentState;	//return this resource and its component
				}
			}
			return null;	//indicate that we could find no corresponding resource and component
		}

		/**Adds a resource and its corresponding view and fires an event.
		@param resourceComponentState The resource and component to add.
		*/	
		protected void addResourceComponentState(final ResourceComponentState resourceComponentState)
		{
			resourceComponentStateList.add(resourceComponentState);	//add the state to our list
			fireResourceComponentAdded(resourceComponentState);	//indicate that the resource component state was added
		}

		/**Removes a resource and its corresponding view and fires an event.
		@param resourceComponentState The resource and component to remove.
		*/	
		protected void removeResourceComponentState(final ResourceComponentState resourceComponentState)
		{
			resourceComponentStateList.remove(resourceComponentState);	//remove the state from our list
			fireResourceComponentRemoved(resourceComponentState);	//indicate that the resource component state was removed
		}
	
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
					//show that the selected resource has changed
				fireResourceComponentSelected(oldResourceComponentState, newResourceComponentState);
			}
		}

	/**The listener that updates the status when the component is modified.*/
	private final PropertyChangeListener updateStatusModifiedPropertyChangeListener;

		/**@return The listener that updates the status when the component is modified.*/
		protected PropertyChangeListener getUpdateStatusModifiedPropertyChangeListener() {return updateStatusModifiedPropertyChangeListener;}

	/**The number of resources that have been added; used for generating a unique ID.*/
	private long resourceCount=0;

		/**@return A unique number representing the number of resources added.*/
		protected synchronized long getNextResourceCount()
		{
			return ++resourceCount;	//increase the resource count and return it
		}
		
	/**Parent component and resource selector constructor with unlimited resources (as many as an integer can hold) managed.
	@param parentComponent The component to serve as a parent for error messages.
	@param resourceSelector The implementation to use for selecting resources.
	*/
	public ResourceComponentManager(final Component parentComponent, final ResourceSelector<R> resourceSelector)
	{
		this(parentComponent, resourceSelector, Integer.MAX_VALUE);	//use the maximum number of resources
	}

	/**Parent component and resource selector constructor
	@param parentComponent The component to serve as a parent for error messages.
	@param resourceSelector The implementation to use for selecting resources.
	@param maxResourceCount The maximum number of resources (&gt;0) that can be loaded at one time, effectively determining SDI or MDI functionality.
	*/
	public ResourceComponentManager(final Component parentComponent, final ResourceSelector<R> resourceSelector, final int maxResourceCount)
	{
		this.parentComponent=parentComponent;	//save the parent component
		this.resourceSelector=resourceSelector;	//save the resource selector
		this.maxResourceCount=maxResourceCount;	//save the maximum resource count
		resourceComponentStateList=new ArrayList<ResourceComponentState>();	//create the list of resource component states; we can't use a map, because many of the states may not yet have URIs assigned
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
		try
		{
			resourceSelector.setPreferences(getPreferences());	//tell the resource selector to use the same preferences we use
		}
		catch(final SecurityException securityException)	//if we can't get preferences
		{
			Log.warn(securityException);	//don't do anything drastic
		}		
	}

	/**Updates the states of the actions, including enabled/disabled status, proxied actions, etc.*/
	public void updateStatus()
	{
		final ResourceComponentState resourceComponentState=getResourceComponentState();	//get the current state of the resource component
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

	/**Determines if all open resources and associated components can close.
	@return <code>true</code> if all open resources and associated components can close.
	@see #canClose(ResourceComponentState)
	*/
	public boolean canCloseAll()
	{
			//for a better user experience, check the current resource component state first
		final ResourceComponentState currentResourceComponentState=getResourceComponentState();	//get the current resource component state
		if(currentResourceComponentState!=null && !canClose(currentResourceComponentState))	//if we have a resource component state, see if we can close it
		{
			return false;	//show that the current resource cannot be closed
		}
		for(final ResourceComponentState resourceComponentState:getResourceComponentStates())	//for each resource component state
		{
				//if this isn't the current resource (which we already checked), see if it can be closed
			if(resourceComponentState!=currentResourceComponentState && !canClose(resourceComponentState))
			{
				return false;	//show that this resource cannot be closed
			}
		}
		return true;	//report that all open resources can be closed
	}

	/**Determines if the current resource, if any, and its component can close.
	@return <code>true</code> if the resource, if any, and its component can close.
	@see #canClose(ResourceComponentState)
	*/
/*TODO fix
	public boolean canClose()
	{
		final ResourceComponentState resourceComponentState=getResourceComponentState();	//get the current resource component state
			//if we have a resource component state, see if we can close it
		return resourceComponentState!=null? canClose(resourceComponentState) : true;
	}
*/
	
	/**Determines if a resource and its component can close.
		This verion asks the resource component if it can close, if that component
		implements <code>CanClosable</code>.
	@param resourceComponentState The state information of the resource that should be checked for closing.
	@return <code>true</code> if the resource and its component can close.
	@see CanClosable#canClose()
	*/
	protected boolean canClose(final ResourceComponentState resourceComponentState)
	{
		final Component component=resourceComponentState.getComponent();	//get the resource component
			//if the component can be checked for closing, and it doesn't wish to close
		if(component instanceof CanClosable && !((CanClosable)component).canClose())
		{
			return false;	//the component doesn't want to close for some reason
		}
		if(component instanceof Modifiable && ((Modifiable)component).isModified())	//if the component says it can close, but it has been modified
		{
			setResourceComponentState(resourceComponentState);	//switch to this resource
			final R resource=resourceComponentState.getResource();	//get the resource
			final String resourceURIString=resource.getURI()!=null ? resource.getURI().toString() : "";	//get a string representing the resource URI, if there is one
				//see if they want to save the changes
			switch(BasicOptionPane.showConfirmDialog(component, "Save modified resource "+resourceURIString+ "?", "Resource Modified", BasicOptionPane.YES_NO_CANCEL_OPTION))	//TODO i18n
			{
				case BasicOptionPane.YES_OPTION:	//if they want to save the changes
					return save(resourceComponentState);	//save the selected resource and report whether the save was successful
				case BasicOptionPane.NO_OPTION:	//if they do not want to save the changes
					return true;	//allow the resource to close
				default:	//if they want to cancel (they pressed the cancel button *or* they just hit Esc)
					return false;	//don't allow the resource to close
			}
		}
		return true;	//default to allowing the component to be closed
	}

	/**Ensures that less that <code>maxResourceCount</code> resources are loaded by closing resources as necessary.
	@return <code>true</code> if the number of resources were successfully brought below the maximum.
	@see #getMaxResourceCount()
	*/
	protected boolean ensureNotMaxCount()
	{
		while(resourceComponentStateList.size()>=getMaxResourceCount())	//while there are too many resources loaded
		{
			final ResourceComponentState resourceComponentState=resourceComponentStateList.get(0);	//get the first resource
			if(canClose(resourceComponentState))	//if we can close this resource
			{
				close(resourceComponentState);	//close the resource
			}
			else	//if we can't close the resource
			{
				return false;	//show that we can't ensure that enough resources are closed
			}
		}
		return true;	//show that there are less than the maximum number of resources
	}


	/**Unloads all open resources, if any.
	If no resource is open, no action occurs.
	@see #getResourceComponentState()
	*/
/*TODO fix
	public void closeAll()
	{
		final ResourceComponentState resourceComponentState=getResourceComponentState();	//get the current resource component state
		if(resourceComponentState!=null)	//if a resource is open
		{
			close(resourceComponentState);	//close this resource component state
		}
	}
*/

	/**Unloads the open resource, if any, after checking to see if the resource can close.
	If no resource is open, no action occurs.
	@return <code>true</code> if there was a resource to close and the operation was not canceled.
	@see #getResourceComponentState()
	*/
	public boolean close()
	{
		final ResourceComponentState resourceComponentState=getResourceComponentState();	//get the current resource component state
		if(resourceComponentState!=null)	//if a resource is open
		{
			if(canClose(resourceComponentState))	//if we can close the open resource
			{
				close(resourceComponentState);	//close this resource component state
			}
		}
		return false;	//show that there was no resource to close, or closing was canceled
/*TODO decide which semantics we like
		final ResourceComponentState resourceComponentState=getResourceComponentState();	//get the current resource component state
		if(resourceComponentState!=null)	//if a resource is open
		{
			close(resourceComponentState);	//close this resource component state
		}
*/
	}

	/**Closes the given resource, selecting a new resource if the closed resource was the selected resource.
	@param oldResourceComponentState The state information of the resource that should be closed.
	*/
	protected void close(final ResourceComponentState oldResourceComponentState)
	{
		final int oldIndex=resourceComponentStateList.indexOf(oldResourceComponentState);	//see where the resource component state is in our list
		assert oldIndex>=0 : "Unrecognized resource component state.";
		final int newIndex=oldIndex<resourceComponentStateList.size()-1 ? oldIndex : oldIndex-1;	//if the last resource was removed, move back one, which may return a negative index if there are no more resource component states
		final ResourceComponentState selectedResourceComponentState=getResourceComponentState();	//see which resource is selected
		removeResourceComponentState(oldResourceComponentState);	//remove this resource component state
		if(oldResourceComponentState==selectedResourceComponentState)	//if the closed resource was previously selected, switch to a different resource
		{
			final ResourceComponentState newResourceComponentState=newIndex>=0 ? resourceComponentStateList.get(newIndex) : null;	//get the new resource component state, if there is one left
			setResourceComponentState(newResourceComponentState);	//switch to the new resource
		}
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
	@return <code>true</code> if the resource was successfully opened, or <code>false</code>if the operation was canceled.
	*/
	public boolean open(final URI referenceURI)
	{
		try
		{
			final R resource=getResourceSelector().getResource(referenceURI);	//get a description of the resource
			final Cursor originalCursor=Components.setPredefinedCursor(getParentComponent(), Cursor.WAIT_CURSOR);	//change the cursor
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
			AbstractSwingApplication.displayApplicationError(getParentComponent(), ioException);	//display the error to the user
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
	public boolean open(R resource)	//TODO test; this is being made public to retrofit the old MentoractTeacherPanel; tread carefully, lest we corrupt the API needlessly
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
				assert resource.getURI()!=null : "Selected resource has no URI.";
				final ResourceComponentState existingResourceComponentState=getResourceComponentState(resource.getURI());	//see if there is already a resource with that URI
				if(existingResourceComponentState!=null)	//if we already know about that resource
				{
					setResourceComponentState(existingResourceComponentState);	//switch to the existing resource component state
				}
				else	//if this is a different resource
				{
					if(ensureNotMaxCount())	//make sure there aren't too many resources open already; if we can ensure that
					{
						final ResourceComponentState newResourceComponentState=read(resource);	//try to open the resource
						if(newResourceComponentState!=null)	//if we succeed in opening the resource
						{
							setResourceComponentState(newResourceComponentState);	//change to the new state
							return true;	//show that we successfully opened the resource
						}
					}
				}
			}
		}
		catch(final SecurityException securityException)	//if there is an error selecting the resource
		{
			AbstractSwingApplication.displayApplicationError(getParentComponent(), securityException);	//display the error to the user
		}
		catch(final IOException ioException)	//if there is an error opening the resource
		{
			AbstractSwingApplication.displayApplicationError(getParentComponent(), ioException);	//display the error to the user
		}
		return false;	//show that we couldn't open anything, for some reason
	}

	/**Reads the specified resource.
	@param resource The resource to open.
	@return An object representing the opened resource and its state, or
		<code>null</code> if the process was canceled.
	@exception IOException Thrown if there was an error reading the resource.
	*/
	protected ResourceComponentState read(final R resource) throws IOException
	{
		final Cursor oldCursor=setCursor(getParentComponent(), Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));	//switch to the wait cursor on the parent component
		try
		{
				//get an input stream to the resource
			final InputStream inputStream=getResourceSelector().getInputStream(resource.getURI());
			try
			{
				final Component component=read(resource, inputStream);	//read the component from the input stream
				final ResourceComponentState resourceComponentState=new ResourceComponentState(resource, component, getNextResourceCount());	//create a new state for the resource
				addResourceComponentState(resourceComponentState);	//add the resource component state
				return resourceComponentState;	//return the component state
			}
			finally
			{
				inputStream.close();	//always close the input stream
			}
		}
		finally
		{
			setCursor(getParentComponent(), oldCursor);	//always switch back to the original cursor
		}
	}

	/**Reads a resource from an input stream.
	@param resource The resource to open.
	@param inputStream The input stream from which to read the data.
	@throws IOException Thrown if there is an error reading the data.
	*/ 
	protected abstract Component read(final R resource, final InputStream inputStream) throws IOException;

	/**Saves the current resource.
	<p>If the current resource component is verifiable, the component is first verified.</p>
	<p>By default if no location is available, the <code>saveAs</code> method is
		called. If a location is available, <code>save(Resource)</code> is called.</p> 
	<p>For normal operation, this method should not be modified and
		<code>save(Resource)</code> should be overridden.</p>
	@return <code>true</code> if there was a resource to save and the operation was not canceled.
	@see #save(Resource)
	@see #saveAs
	@see #getResourceComponentState()
	*/
	public boolean save()
	{
		final ResourceComponentState resourceComponentState=getResourceComponentState();	//get the current resource component state
		return resourceComponentState!=null ? save(resourceComponentState) : false;	//save the resource, returning false if there is no resource to save
	}

	/**Saves the current resource.
	<p>If the current resource component is verifiable, the component is first verified.</p>
	<p>By default if no location is available, the <code>saveAs</code> method is
		called. If a location is available, <code>save(Resource)</code> is called.</p> 
	<p>For normal operation, this method should not be modified and
		<code>save(Resource)</code> should be overridden.</p>
	@param resourceComponentState The state information of the resource to be saved.
	@return <code>true</code> if the operation was not canceled.
	@see #save(Resource)
	@see #saveAs
	@see #getResourceComponentState()
	*/
	protected boolean save(final ResourceComponentState resourceComponentState)
	{
			//if the component is verifiable, make sure it verifies before we save the contents
		if(!(resourceComponentState.getComponent() instanceof Verifiable) || ((Verifiable)resourceComponentState.getComponent()).verify())
		{
			assert resourceComponentState.getResource()!=null : "Resource component state does not represent a valid resource.";
			if(resourceComponentState.getResource().getURI()!=null) //if we have a URI
			{
				try
				{
					write(resourceComponentState.getResource(), resourceComponentState.getComponent()); //save using the resource we already have
					return true;
				}
				catch(IOException ioException)	//if there is an error saving the resource
				{
					AbstractSwingApplication.displayApplicationError(getParentComponent(), ioException);	//display the error to the user
				}
			}
			else  //if we don't have a URI
			{
				return saveAs(resourceComponentState); //save with a user-specified URI
			}
		}
		return false;	//show that we couldn't save the contents because the component didn't verify or there was some other error
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
			final R resource=getResourceSelector().selectOutputResource(resourceComponentState.getResource());  //get the resource to use for saving
			if(resource!=null)  //if a valid resource was returned
			{
				write(resource, resourceComponentState.getComponent()); //save the resource
				resourceComponentState.setResource(resource);	//change the resource of the component state
/*TODO del if not needed
				if(!ObjectUtilities.equals(resourceComponentState.getResource().getReferenceURI(), uri))	//if the URI isn't the same //TODO fix or delete comment: wasn't updated (e.g. the overridden saveFile() didn't call the version in this class)
				{
					resourceComponentState.getResource().setReferenceURI(uri);	//update the resource description's URI
				}
*/
	//TODO fix or del				setFile(file);  //update the file, just in case they override saveFile() and don't call this version
				return true;	//show that the resource was successfully saved
			}
		}
		catch(final SecurityException securityException)	//if there is an error selecting the resource
		{
			AbstractSwingApplication.displayApplicationError(getParentComponent(), securityException);	//display the error to the user
		}
		catch(IOException ioException)	//if there is an error saving the resource
		{
			AbstractSwingApplication.displayApplicationError(getParentComponent(), ioException);	//display the error to the user
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
		final Cursor oldCursor=setCursor(getParentComponent(), Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));	//switch to the wait cursor on the parent component
		try
		{
			final OutputStream outputStream=getResourceSelector().getOutputStream(resource.getURI());	//get an output stream to this resource's URI
			try
			{
				write(resource, component, outputStream);	//write the component to the output stream
			}
			finally
			{
				outputStream.close();	//always close the output stream
			}
		}
		finally
		{
			setCursor(getParentComponent(), oldCursor);	//always switch back to the original cursor
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
		final ResourceComponentState resourceComponentState=getResourceComponentState();	//get the current resource component state
		if(resourceComponentState!=null)	//if a resource is open
		{
//TODO del if not needed			if(canClose(resourceComponentState))	//if we can close the open resource
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
	protected void revert(final ResourceComponentState resourceComponentState)
	{
	}

	/**Adds a <code>ResourceComponentListener</code> to the list.
	@param resourceComponentListener The <code>ResourceComponentListener</code> to be added.
	*/
	public void addResourceComponentListener(final ResourceComponentListener<R> resourceComponentListener)
	{
		eventListenerList.add(ResourceComponentListener.class, resourceComponentListener);
	}

	/**Removes a <code>ResourceComponentListener</code> from the list.
	@param resourceComponentListener the listener to be removed
	*/
	public void removeActionListener(final ResourceComponentListener<R> resourceComponentListener)
	{
		eventListenerList.remove(ResourceComponentListener.class, resourceComponentListener);
	}

	/**Notifies all listeners that a resource component has been added.
	@param resourceComponentState The resource and component added.
	*/
  protected void fireResourceComponentAdded(final ResourceComponentState resourceComponentState)
	{
		for(final Object listener:eventListenerList.getListeners(ResourceComponentListener.class))  //for each resource component listener
		{
			((ResourceComponentListener<R>)listener).onResourceComponentAdded(resourceComponentState);	//notify this listener that this component state was added
		}
  }

	/**Notifies all listeners that a resource component has been removed.
	@param resourceComponentState The resource and component removed.
	*/
  protected void fireResourceComponentRemoved(final ResourceComponentState resourceComponentState)
	{
		for(final Object listener:eventListenerList.getListeners(ResourceComponentListener.class))  //for each resource component listener
		{
			((ResourceComponentListener<R>)listener).onResourceComponentRemoved(resourceComponentState);	//notify this listener that this component state was removed
		}
  }

	/**Notifies all listeners that a resource component has been selected.
	@param oldResourceComponentState The previously selected resource and component.
	@param newResourceComponentState The newly selected resource and component.
	*/
  protected void fireResourceComponentSelected(final ResourceComponentState oldResourceComponentState, final ResourceComponentState newResourceComponentState)
	{
		for(final Object listener:eventListenerList.getListeners(ResourceComponentListener.class))  //for each resource component listener
		{
			((ResourceComponentListener<R>)listener).onResourceComponentSelected(oldResourceComponentState, newResourceComponentState);	//notify this listener that this component state was selected
		}
  }

	/**Action for creating a new file.*/
	public static class NewAction extends AbstractAction
	{
		/**Default constructor.*/
		public NewAction()
		{
			super("New...");	//create the base class TODO i18n
			putValue(SHORT_DESCRIPTION, "Create a new file ");	//set the short description TODO i18n
			putValue(LONG_DESCRIPTION, "Create a new file.");	//set the long description TODO i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));  //set the mnemonic key TODO i18n
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
			super("Open...");	//create the base class TODO Int
			putValue(SHORT_DESCRIPTION, "Open a resource");	//set the short description TODO Int
			putValue(LONG_DESCRIPTION, "Display a dialog to select a file, and then load the selected resource.");	//set the long description TODO Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));  //set the mnemonic key TODO i18n
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
			super("Close");	//create the base class TODO Int
			putValue(SHORT_DESCRIPTION, "Close the open resource");	//set the short description TODO i18n
			putValue(LONG_DESCRIPTION, "Close the currently open resource.");	//set the long description TODO i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));  //set the mnemonic key TODO i18n
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
			super("Save");	//create the base class TODO Int
			putValue(SHORT_DESCRIPTION, "Save the open resource");	//set the short description TODO Int
			putValue(LONG_DESCRIPTION, "Save the currently open resource.");	//set the long description TODO Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_V));  //set the mnemonic key; for some reason, 's' causes the action to be activated when Alt+F4 is pressed TODO i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.SAVE_ICON_FILENAME)); //load the correct icon
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK)); //add the accelerator TODO i18n
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
			super("Revert");	//create the base class TODO i18n
			putValue(SHORT_DESCRIPTION, "Revert the open resource");	//set the short description TODO i18n
			putValue(LONG_DESCRIPTION, "Revert the currently open resource.");	//set the long description TODO i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));  //set the mnemonic key TODO i18n
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
	public class ResourceComponentState extends DefaultObjectState<R>
	{
		
		/**The unique number representing the order created.*/
		private final long number;

			/**@return The unique number representing the order created.*/
			public long getNumber() {return number;}

		/**@return The non-<code>null</code> resource being described by delegating to <code>getObject()</code>.
		@see DefaultObjectState#getObject()
		*/
		public R getResource() {return getObject();}

		/**Sets the resource being described.
		<p>This method delegates to the parent class, and is declared here solely
			so that the component manager can change the resource represented.
		@param resource The new resource to describe.
		@exception NullPointerException Thrown if the resource is <code>null</code>.
		*/
		protected void setResource(final R resource)
		{
			super.setObject(resource);	//set the resource object
		}

		/**The component that acts as a view to the resource.*/
		private Component component;

			/**@return The component that acts as a view to the resource.*/
			public Component getComponent() {return component;}

		/**Constructs a resource state with a resource and a component.
		@param resource The description of the resource.
		@param component The component that represents a view of the resource.
		@param number A unique number representing the order created.
		@exception NullPointerException Thrown if the resource is <code>null</code>.
		*/
		public ResourceComponentState(final R resource, final Component component, final long number)
		{
			super(resource);	//construct the parent class
			this.component=component;	//save the resource component
			this.number=number;	//save the number
		}

		/**@return A label representing this resource component.*/
		public String getLabel()
		{
			final URI uri=getResource().getURI();	//get the resource reference URI
			return uri!=null ? getName(uri) : "Resource "+getNumber();	//return the URI filename if there is a URI; otherwise, generate a unique label TODO i18n
		}		
	}

}

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

package com.garretwilson.swing;

import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.*;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import static com.globalmentor.java.Booleans.*;

import com.globalmentor.net.Resource;

/**Class that manages resource components in a desktop pane.
@param <R> The type of resource the components of which are being managed.
@author Garret Wilson
@see ResourceComponentManager
*/
public class DesktopPaneResourceComponentDecorator<R extends Resource> extends ResourceComponentDecorator<R> implements InternalFrameListener
{

	/**The desktop pane that hosts the resource components.*/
	private final JDesktopPane desktopPane;
	
		/**@return The desktop pane that hosts the resource components.*/
		protected JDesktopPane getDesktopPane() {return desktopPane;}

	/**A map of internal frames weakly keyed to resource components.*/
	protected final Map<Component, JInternalFrame> componentInternalFrameMap=new WeakHashMap<Component, JInternalFrame>();

	/**A map of resouce components weakly keyed to internal frames.*/
	protected final Map<JInternalFrame, Component> internalFrameComponentMap=new WeakHashMap<JInternalFrame, Component>();

	/**The vetoable change listener that ensures a particular frame trying to close can close.*/
	private final VetoableChangeListener internalFrameCanCloseListener;

		/**@return The vetoable change listener that ensures a particular frame trying to close can close.*/
		protected VetoableChangeListener getInternalFrameCanCloseListener() {return internalFrameCanCloseListener;}

	/**Constructs a decorator for a desktop pane, using the given
		resource component manager to manager resource components.
	@param desktopPane The desktop pane that hosts the resource components.
	@param resourceComponentManager The delegate manager of resource components.
	*/
	public DesktopPaneResourceComponentDecorator(final JDesktopPane desktopPane, final ResourceComponentManager<R> resourceComponentManager)
	{
		super(resourceComponentManager);	//construct the parent class
		this.desktopPane=desktopPane;	//save the tabbed pane
		internalFrameCanCloseListener=new VetoableChangeListener()	//create a reusable listener to make sure frames can close
			  {
					/**Asks the component manager if the resource component associated with the closing internal frame can close.*/ 
			    public void vetoableChange(final PropertyChangeEvent propertyChangeEvent) throws PropertyVetoException
			    {
						final Object source=propertyChangeEvent.getSource();	//get the source of the event
						if(source instanceof JInternalFrame)	//if the event came from an internal frame
						{
							final JInternalFrame internalFrame=(JInternalFrame)source;	//get the source as an internal frame
								//if the internal frame is trying to close
							if(JInternalFrame.IS_CLOSED_PROPERTY.equals(propertyChangeEvent.getPropertyName())
								  && booleanValue(propertyChangeEvent.getNewValue())==true)
							{
								final Component component=internalFrameComponentMap.get(internalFrame);	//get the component associated with the internal frame
								if(component!=null)	//if there is a selected component
								{
									final ResourceComponentManager<R> resourceComponentManager=getResourceComponentManager();	//get the resource component manager
										//get the resource state corresponding to this component
									final ResourceComponentManager<R>.ResourceComponentState resourceComponentState=resourceComponentManager.getResourceComponentState(component);
									if(resourceComponentState!=null)	//if there is a corresponding state
									{
										if(!resourceComponentManager.canClose(resourceComponentState))	//ask the resource component manager if this resource component state can close; if it can't
										{
											throw new PropertyVetoException("Resource manager prevented component from closing.", propertyChangeEvent); //cancel the closing TODO i18n
										}
									}
								}
							}
						}
			    }
			  }; 
		
		
/*TODO fix
		desktopPane.addChangeListener(new ChangeListener()	//listen for tab changes, and update the selected resource in response
				{
					public void stateChanged(final ChangeEvent changeEvent)	//when the tab changes
					{
						final Component selectedComponent=tabbedPane.getSelectedComponent();	//see which tab is now selected
						if(selectedComponent!=null)	//if there is a selected tab
						{
								//get the resource state corresponding to this component
							final ResourceComponentManager<R>.ResourceComponentState resourceComponentState=getResourceComponentManager().getResourceComponentState(selectedComponent);
							if(resourceComponentState!=null)	//if there is a corresponding state
							{
								getResourceComponentManager().setResourceComponentState(resourceComponentState);	//switch to that state
							}
						}
					}
				});
*/
	}

	/**Called when a resource component is added.
	This implementation adds an internal frame to the desktop pane corresponding to the resource component.
	@param resourceComponentState The resource and component added.
	*/
	protected void onResourceComponentAdded(final ResourceComponentManager<R>.ResourceComponentState resourceComponentState)
	{
		final JInternalFrame internalFrame=createInternalFrame(resourceComponentState);	//create an internal frame for the component
		componentInternalFrameMap.put(resourceComponentState.getComponent(), internalFrame);	//associate the internal frame with the resource component
		internalFrameComponentMap.put(internalFrame, resourceComponentState.getComponent());	//associate the resource component with the internal frame
		addInternalFrameListeners(internalFrame);	//add the internal frame
		getDesktopPane().add(internalFrame, JDesktopPane.DEFAULT_LAYER);  //add the frame to the default layer
		getDesktopPane().getDesktopManager().activateFrame(internalFrame);  //activate the internal frame we just added
		//TODO fix		updateTitle(internalFrame);  //update the internal frame's title
	}

	/**Called when a resource component is removed.
	This implementation removes the internal frame associated with the component from the desktop pane.
	@param resourceComponentState The resource and component removed.
	*/
	protected void onResourceComponentRemoved(final ResourceComponentManager<R>.ResourceComponentState resourceComponentState)
	{
		final Component component=resourceComponentState.getComponent();	//get the resource component
		final JInternalFrame internalFrame=componentInternalFrameMap.get(component);	//see if we have an internal frame associated with this component
		if(internalFrame!=null)	//if we have an internal frame associated with the component
		{
			removeInternalFrameListeners(internalFrame);	//remove the listeners from the internal frame
			getDesktopPane().remove(internalFrame);	//remove the internal frame from the desktop
			componentInternalFrameMap.remove(component);	//remove the component-internal frame association
			internalFrameComponentMap.remove(internalFrame);	//remove the internal frame association-component association
		}
	}

	/**Called when a resource component is selected.
	@param oldResourceComponentState The previously selected resource and component.
	@param newResourceComponentState The newly selected resource and component.
	*/
	protected void onResourceComponentSelected(final ResourceComponentManager<R>.ResourceComponentState oldResourceComponentState, final ResourceComponentManager<R>.ResourceComponentState newResourceComponentState)
	{
		final Component newComponent=newResourceComponentState!=null ? newResourceComponentState.getComponent() : null;	//get the new component, if there is one
		if(newComponent!=null)	//if there is a new component being selected (ignore null components---we can't select "no component")
		{
			final JInternalFrame internalFrame=componentInternalFrameMap.get(newComponent);	//see if we have an internal frame associated with the new component
			if(internalFrame!=null)	//if we have an internal frame associated with the component
			{
				getDesktopPane().setSelectedFrame(internalFrame);	//select the frame associated with the component
			}
		}
	}

	/**Creates a default internal frame that is resizable, closable, maximizable, and iconifiable.
	@param title The title for the internal frame.
	@return A new default internal frame with the given title.
	*/
	protected JInternalFrame createInternalFrame(final String title)	//TODO maybe put this in a utility class
	{
		final JInternalFrame internalFrame=new JInternalFrame(title, true, true, true, true); //create a default internal frame
		internalFrame.setVisible(true); //TODO testing
		internalFrame.setBounds(0, 0, 640, 480); //TODO testing
		return internalFrame; //return the frame we created
	}
	
	/**Creates an internal frame for the given resource component.
	@param resourceComponentState The resource and component for which an internal frame should be created.
	@return A new internal frame containing the resource component.
	*/
	protected JInternalFrame createInternalFrame(final ResourceComponentManager<R>.ResourceComponentState resourceComponentState)
	{
		return createInternalFrame(resourceComponentState.getLabel(), resourceComponentState.getComponent());	//create and return an internal frame for the resource component with the correct title
	}

	/**Creates an internal frame for the given component.
	@param title The title for the internal frame.
	@param component The component the new internal frame should contain.
	@return A new internal frame containing the component.
	*/
	public JInternalFrame createInternalFrame(final String title, final Component component)
	{
		final JInternalFrame internalFrame=createInternalFrame(title);  //create a default internal frame with the given title
		final Container container;	//we'll determine a container to put in the internal frame
		if(component instanceof Container)	//if the component is a container
		{
			container=(Container)component;	//use the component as-is
		}
		else	//if the component is not a container
		{
			container=new ContentPanel(component);	//wrap the component in a content panel
		}
		internalFrame.setContentPane(container);  //put the resource component in the internal frame
		return internalFrame; //return the internal frame we created
	}

	/**Adds an internal frame to the desktop pane and activates it.
	@param internalFrame The internal frame to add.
	*/
/*TODO del if not needed; this is really out of the scope of the component decorator
	public void addInternalFrame(final JInternalFrame internalFrame)
	{
		final JDesktopPane desktopPane=getDesktopPane();	//get the desktop pane
		desktopPane.add(internalFrame, JDesktopPane.DEFAULT_LAYER);  //add the frame to the default layer
		desktopPane.getDesktopManager().activateFrame(internalFrame);  //activate the internal frame we just added		
	}
*/

	/**Adds appropriate listeners to the frame so that appropriate updates can occur when the frame is closed, etc.
	@param internalFrame The frame to add to the desktop.
	*/
	protected void addInternalFrameListeners(final JInternalFrame internalFrame)
	{
		internalFrame.addInternalFrameListener(this);  //show that we want to hear about internal frame events	
		internalFrame.addVetoableChangeListener(getInternalFrameCanCloseListener());	//listen for the frame trying to close
/*TODO fix
			//listen for modifications of the "modified" property of the content pane
		internalFrame.getContentPane().addPropertyChangeListener(Modifiable.MODIFIED_PROPERTY, new java.beans.PropertyChangeListener()
	  {
	    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) //if the "modified" property changes in the explore panel
	    {
	//TODO fix				getApplicationFrame().updateStatus();  //update our actions
	    }
	  });
*/
	}

	/**Removes listeners as appropriate from the internal frame.
	@param internalFrame The frame be removed from the desktop.
	*/
	protected void removeInternalFrameListeners(final JInternalFrame internalFrame)
	{
		internalFrame.removeVetoableChangeListener(getInternalFrameCanCloseListener());	//stop listening for the frame trying to close
		internalFrame.removeInternalFrameListener(this);  //show that we no longer want to hear about internal frame events	
	}

  /**Invoked when a internal frame has been opened.
	@param internalFrameEvent The event giving informatoin about the internal frame.
	@see javax.swing.JInternalFrame#show
	*/
  public void internalFrameOpened(final InternalFrameEvent internalFrameEvent)
	{
	}

  /**Invoked when an internal frame is in the process of being closed.
		The close operation can be overridden at this point.
	@param internalFrameEvent The event giving informatoin about the internal frame.
	@see javax.swing.JInternalFrame#setDefaultCloseOperation
	*/
  public void internalFrameClosing(final InternalFrameEvent internalFrameEvent)
	{
	}

  /**Invoked when an internal frame has been closed.
	@param internalFrameEvent The event giving informatoin about the internal frame.
	@see javax.swing.JInternalFrame#setClosed
	*/
  public void internalFrameClosed(final InternalFrameEvent internalFrameEvent)
	{
/*TODO fix
Log.trace("internal frame closed"); //TODO del
		if(getDesktopPane().getAllFrames().length==0) //if this is the last frame closed
;//TODO fix		  getApplicationFrame().updateStatus();  //update our actions to reflect that all frames have been closed
*/
		final JInternalFrame internalFrame=internalFrameEvent.getInternalFrame();	//get the internal frame being activated
		removeInternalFrameListeners(internalFrame);	//remove all listeners from the internal frame
		final Component component=internalFrameComponentMap.get(internalFrame);	//get the component associated with the newly activated internal frame
		if(component!=null)	//if there is a selected component
		{
			componentInternalFrameMap.remove(component);	//remove the component-internal frame association
			internalFrameComponentMap.remove(internalFrame);	//remove the internal frame association-component association
			final ResourceComponentManager<R> resourceComponentManager=getResourceComponentManager();	//get the resource component manager
				//get the resource state corresponding to this component
			final ResourceComponentManager<R>.ResourceComponentState resourceComponentState=resourceComponentManager.getResourceComponentState(component);
			if(resourceComponentState!=null)	//if there is a corresponding state
			{
				resourceComponentManager.removeResourceComponentState(resourceComponentState);	//remove that state
			}
		}
	}

  /**Invoked when an internal frame is iconified.
	@param internalFrameEvent The event giving informatoin about the internal frame.
	@see javax.swing.JInternalFrame#setIcon
	*/
  public void internalFrameIconified(final InternalFrameEvent internalFrameEvent)
	{
	}

  /**Invoked when an internal frame is de-iconified.
	@param internalFrameEvent The event giving informatoin about the internal frame.
	@see javax.swing.JInternalFrame#setIcon
	*/
  public void internalFrameDeiconified(final InternalFrameEvent internalFrameEvent)
	{
	}

  /**Invoked when an internal frame is activated.
	@param internalFrameEvent The event giving informatoin about the internal frame.
	@see javax.swing.JInternalFrame#setSelected
	*/
  public void internalFrameActivated(final InternalFrameEvent internalFrameEvent)
	{
//TODO fix		getApplicationFrame().updateStatus();  //update our actions to reflect the new frame selected
		final JInternalFrame internalFrame=internalFrameEvent.getInternalFrame();	//get the internal frame being activated
		final Component selectedComponent=internalFrameComponentMap.get(internalFrame);	//get the component associated with the newly activated internal frame
		if(selectedComponent!=null)	//if there is a selected component
		{
			final ResourceComponentManager<R> resourceComponentManager=getResourceComponentManager();	//get the resource component manager
				//get the resource state corresponding to this component
			final ResourceComponentManager<R>.ResourceComponentState resourceComponentState=resourceComponentManager.getResourceComponentState(selectedComponent);
			if(resourceComponentState!=null)	//if there is a corresponding state
			{
				resourceComponentManager.setResourceComponentState(resourceComponentState);	//switch to that state
			}
		}
	}

  /**Invoked when an internal frame is de-activated.
	@param internalFrameEvent The event giving informatoin about the internal frame.
	@see javax.swing.JInternalFrame#setSelected
	*/
  public void internalFrameDeactivated(final InternalFrameEvent internalFrameEvent)
	{
	}

}

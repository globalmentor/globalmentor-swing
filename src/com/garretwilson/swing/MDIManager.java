package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import com.garretwilson.rdf.*;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.util.Debug;
import com.garretwilson.util.Modifiable;

/**Class that manages multiple documents in a <code>JDesktopPane</code>.
@author Garret Wilson
*/
public class MDIManager implements InternalFrameListener
{

  /**The main MDI pane in which internal frames will be displayed.*/
	private final JDesktopPane desktopPane;

		/**@return The main MDI pane in which internal frames will be displayed.*/
		protected JDesktopPane getDesktopPane() {return desktopPane;}

	/**The application frame for which this object is a manager.*/
	private final ApplicationFrame applicationFrame;

		/**@return The application frame for which this object is a manager.*/
		protected ApplicationFrame getApplicationFrame() {return applicationFrame;}

	/**Creates a MDI destop manager that manages internal framems in a desktop
		pane.
	@param newApplicationFrame The application frame for which this object is a
		manager.
	@param newDesktopPane The desktop pane that will holds the internal frames.
	*/
	public MDIManager(final ApplicationFrame newApplicationFrame, final JDesktopPane newDesktopPane)
	{
		desktopPane=newDesktopPane; //save the desktop pane
		applicationFrame=newApplicationFrame; //save the application frame
	}

	/**Adds an internal frame to the desktop, adding the appropriate listeners to
		the frame so that appropriate updates can occur when the frame is closed, etc.
	@param internalFrame The frame to add to theh desktop.
	*/
	public void addInternalFrame(final JInternalFrame internalFrame)
	{
		internalFrame.addInternalFrameListener(this);  //show that we want to hear about internal frame events
			//listen for the frame trying to close
		internalFrame.addVetoableChangeListener(new VetoableChangeListener()
    {
      public void vetoableChange(final PropertyChangeEvent propertyChangeEvent) throws PropertyVetoException
      {
/*G***del
Debug.trace("Vetoable change: ", propertyChangeEvent);  //G***del
Debug.trace("property name: ", propertyChangeEvent.getPropertyName());  //G***del
Debug.trace("old value: ", propertyChangeEvent.getOldValue());  //G***del
Debug.trace("new value: ", propertyChangeEvent.getNewValue());  //G***del
*/
					//if the internal frame is trying to close
				if(JInternalFrame.IS_CLOSED_PROPERTY.equals(propertyChangeEvent.getPropertyName())
					  && ((Boolean)propertyChangeEvent.getNewValue()).booleanValue()==true)
				{
					if(canCloseInternalFrame(internalFrame)) //if this frame can close
					{
//G***fix						setFile(null);  //disassociate the file from the internal frame G***check; see if we need to explicitly pass a key
					}
					else  //if the frame cannot close
						throw new PropertyVetoException("canceled", propertyChangeEvent); //cancel the closing G***i18n
				}
      }
    });
			//listen for modifications of the "modified" property of the content pane
		internalFrame.getContentPane().addPropertyChangeListener(Modifiable.MODIFIED_PROPERTY_NAME, new java.beans.PropertyChangeListener()
    {
      public void propertyChange(final PropertyChangeEvent propertyChangeEvent) //if the "modified" property changes in the explore panel
      {
				getApplicationFrame().updateStatus();  //update our actions
      }
    });
		getDesktopPane().add(internalFrame, getDesktopPane().DEFAULT_LAYER);  //add the frame to the default layer
		getDesktopPane().getDesktopManager().activateFrame(internalFrame);  //activate the internal frame we just added
//G***fix		updateTitle(internalFrame);  //update the internal frame's title
	}

	/**@return The currently active <code>JInternalFrame</code> on the desktop,
		or <code>null</code> if not internal fram is currently active.
	*/
	public JInternalFrame getSelectedFrame()
	{
		return getDesktopPane().getSelectedFrame(); //return the desktop's selected frame
	}

	/**Set the currently active <code>JInternalFrame</code> on the desktop.
	@param internalFrame The internal frame that should be currently selected.
	*/
	public void setSelectedFrame(final JInternalFrame internalFrame)
	{
		getDesktopPane().setSelectedFrame(internalFrame);  //let the desktop select the given frame
	}

	/**Retrieves the currently selected MDI document, if an internal frame is
		selected that contains an MDI document.
	@return The currently selected MDI document, or <code>null</code> if no
		MDI document is selected.
	*/
	public RDFResourceState getMDIDocument()
	{
		return getMDIDocument(getSelectedFrame());  //get the MDI document from the currently selected internal frame
	}

	/**Retrieves the MDI document from an internal frame.
	@param internalFrame The internal frame for which an MDI document should be
		returned, or <code>null</code>.
	@return The internal frame's MDI document, or <code>null</code> if the
		internal frame has no MDI document.
	*/
	public static RDFResourceState getMDIDocument(final JInternalFrame internalFrame)
	{
		if(internalFrame!=null) //if there is an internal frame
		{
			final Container contentPane=internalFrame.getContentPane(); //get the content pane of the internal frame
			if(contentPane instanceof RDFResourceState) //if this is an MDI document
			{
				return (RDFResourceState)contentPane; //return the content pane cast to an MDI document

			}
		}
		return null;  //show that we couldn't find an MDI document
	}

	/**Determines if all internal frames can be closed.
	@return <code>true</code> if the currently open internal frames can be
		closed, else <code>false</code> if closing should be canceled.
	*/
	public boolean canCloseInternalFrames()
	{
		final JInternalFrame[] internalFrames=getDesktopPane().getAllFrames();  //get all open internal frames
		for(int i=0; i<internalFrames.length; ++i)  //look at each of the internal frames
		{
			final JInternalFrame internalFrame=internalFrames[i]; //get a reference to this internal frame
			if(internalFrame.isClosable())  //if this is a frame that can be closed
			{
				if(!canCloseInternalFrame(internalFrame))  //if we can't close this frame
				{
					return false; //show that we can't close this frame
				}
			}
		}
		return true; //show that we can close all internal frames
	}

	/**Determines if the internal frame can be closed.
	@param internalFrame The internal frame requesting to be closed.
	@return <code>true</code> if the currently open internal frame can be
		closed, else <code>false</code> if closing should be canceled.
	*/
	public boolean canCloseInternalFrame(final JInternalFrame internalFrame)
	{
		final RDFResourceState mdiDocument=getMDIDocument(internalFrame);  //see if there is an MDI document in the frame
		if(mdiDocument!=null) //if there is an MDI document
		{
			return getApplicationFrame().canClose(mdiDocument); //see if we can close the MDI document
		}
		return true;  //default to allowing the frame to be closed
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
Debug.trace("internal frame closed"); //G***del
		if(getDesktopPane().getAllFrames().length==0) //if this is the last frame closed
		  getApplicationFrame().updateStatus();  //update our actions to reflect that all frames have been closed
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
		getApplicationFrame().updateStatus();  //update our actions to reflect the new frame selected
	}

  /**Invoked when an internal frame is de-activated.
	@param internalFrameEvent The event giving informatoin about the internal frame.
	@see javax.swing.JInternalFrame#setSelected
	*/
  public void internalFrameDeactivated(final InternalFrameEvent internalFrameEvent)
	{
	}

	/**Creates a default internal frame that is resizable, closable, maximizable,
		and iconifyable.
	@param title The title for the internal frame.
	@return A new default internal frame with the given title.
	*/
	public static JInternalFrame createDefaultInternalFrame(final String title)
	{
		final JInternalFrame internalFrame=new JInternalFrame(title, true, true, true, true); //create a default internal frame
		internalFrame.setVisible(true); //G***testing
		internalFrame.setBounds(0, 0, 640, 480); //G***testing
		return internalFrame; //return the frame we created
	}

	/**Creates a default internal frame with the given container as its content
		pane.
	@param title The title for the internal frame.
	@param contentPane The container to use as the main content pane.
	@return A new default internal frame with the given title and content pane.
	*/
	public static JInternalFrame createDefaultInternalFrame(final String title, final Container contentPane)
	{
		final JInternalFrame internalFrame=createDefaultInternalFrame(title);  //create a default internal frame
		internalFrame.setContentPane(contentPane);  //set the content pane to the one given
		return internalFrame; //return the internal frame we created
	}

}
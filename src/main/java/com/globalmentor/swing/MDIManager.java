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

import java.awt.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;

import com.globalmentor.log.Log;
import com.globalmentor.model.Modifiable;
import com.globalmentor.model.ObjectState;
import com.globalmentor.rdf.*;

/**
 * Class that manages multiple documents in a {@link JDesktopPane}.
 * @author Garret Wilson
 */
public class MDIManager implements InternalFrameListener //TODO replace this class with a DesktopPaneResourceComponentDecorator
{

	/** The main MDI pane in which internal frames will be displayed. */
	private final JDesktopPane desktopPane;

	/** @return The main MDI pane in which internal frames will be displayed. */
	protected JDesktopPane getDesktopPane() {
		return desktopPane;
	}

	/** The application frame for which this object is a manager. */
	//TODO fix	private final ObsoleteApplicationFrame applicationFrame;

	/** @return The application frame for which this object is a manager. */
	//TODO fix		protected ObsoleteApplicationFrame getApplicationFrame() {return applicationFrame;}

	/**
	 * Creates a MDI desktop manager that manages internal frames in a desktop pane.
	 * @param newDesktopPane The desktop pane that will holds the internal frames.
	 */
	public MDIManager(/*TODO fix final ObsoleteApplicationFrame newApplicationFrame, */final JDesktopPane newDesktopPane) {
		desktopPane = newDesktopPane; //save the desktop pane
		//TODO fix		applicationFrame=newApplicationFrame; //save the application frame
	}

	/**
	 * Adds an internal frame to the desktop, adding the appropriate listeners to the frame so that appropriate updates can occur when the frame is closed, etc.
	 * @param internalFrame The frame to add to theh desktop.
	 */
	public void addInternalFrame(final JInternalFrame internalFrame) {
		internalFrame.addInternalFrameListener(this); //show that we want to hear about internal frame events
		//listen for the frame trying to close
		internalFrame.addVetoableChangeListener(new VetoableChangeListener() {

			public void vetoableChange(final PropertyChangeEvent propertyChangeEvent) throws PropertyVetoException {
				/*TODO del
				Log.trace("Vetoable change: ", propertyChangeEvent);  //TODO del
				Log.trace("property name: ", propertyChangeEvent.getPropertyName());  //TODO del
				Log.trace("old value: ", propertyChangeEvent.getOldValue());  //TODO del
				Log.trace("new value: ", propertyChangeEvent.getNewValue());  //TODO del
				*/
				//if the internal frame is trying to close
				if(JInternalFrame.IS_CLOSED_PROPERTY.equals(propertyChangeEvent.getPropertyName())
						&& ((Boolean)propertyChangeEvent.getNewValue()).booleanValue() == true) {
					if(canCloseInternalFrame(internalFrame)) { //if this frame can close
					//TODO fix						setFile(null);  //disassociate the file from the internal frame TODO check; see if we need to explicitly pass a key
					} else
						//if the frame cannot close
						throw new PropertyVetoException("canceled", propertyChangeEvent); //cancel the closing TODO i18n
				}
			}
		});
		//listen for modifications of the "modified" property of the content pane
		internalFrame.getContentPane().addPropertyChangeListener(Modifiable.MODIFIED_PROPERTY, new java.beans.PropertyChangeListener() {

			public void propertyChange(final PropertyChangeEvent propertyChangeEvent) { //if the "modified" property changes in the explore panel
			//TODO fix				getApplicationFrame().updateStatus();  //update our actions
			}
		});
		getDesktopPane().add(internalFrame, getDesktopPane().DEFAULT_LAYER); //add the frame to the default layer
		getDesktopPane().getDesktopManager().activateFrame(internalFrame); //activate the internal frame we just added
		//TODO fix		updateTitle(internalFrame);  //update the internal frame's title
	}

	/**
	 * @return The currently active <code>JInternalFrame</code> on the desktop, or <code>null</code> if not internal fram is currently active.
	 */
	public JInternalFrame getSelectedFrame() {
		return getDesktopPane().getSelectedFrame(); //return the desktop's selected frame
	}

	/**
	 * Set the currently active <code>JInternalFrame</code> on the desktop.
	 * @param internalFrame The internal frame that should be currently selected.
	 */
	public void setSelectedFrame(final JInternalFrame internalFrame) {
		getDesktopPane().setSelectedFrame(internalFrame); //let the desktop select the given frame
	}

	/**
	 * Retrieves the currently selected MDI document, if an internal frame is selected that contains an MDI document.
	 * @return The currently selected MDI document, or <code>null</code> if no MDI document is selected.
	 */
	public ObjectState<RDFResource> getMDIDocument() {
		return getMDIDocument(getSelectedFrame()); //get the MDI document from the currently selected internal frame
	}

	/**
	 * Retrieves the MDI document from an internal frame.
	 * @param internalFrame The internal frame for which an MDI document should be returned, or <code>null</code>.
	 * @return The internal frame's MDI document, or <code>null</code> if the internal frame has no MDI document.
	 */
	public static ObjectState<RDFResource> getMDIDocument(final JInternalFrame internalFrame) {
		if(internalFrame != null) { //if there is an internal frame
			final Container contentPane = internalFrame.getContentPane(); //get the content pane of the internal frame
			if(contentPane instanceof ObjectState) { //if this is an MDI document
				return (ObjectState<RDFResource>)contentPane; //return the content pane cast to an MDI document

			}
		}
		return null; //show that we couldn't find an MDI document
	}

	/**
	 * Determines if all internal frames can be closed.
	 * @return <code>true</code> if the currently open internal frames can be closed, else <code>false</code> if closing should be canceled.
	 */
	public boolean canCloseInternalFrames() {
		final JInternalFrame[] internalFrames = getDesktopPane().getAllFrames(); //get all open internal frames
		for(int i = 0; i < internalFrames.length; ++i) { //look at each of the internal frames
			final JInternalFrame internalFrame = internalFrames[i]; //get a reference to this internal frame
			if(internalFrame.isClosable()) { //if this is a frame that can be closed
				if(!canCloseInternalFrame(internalFrame)) { //if we can't close this frame
					return false; //show that we can't close this frame
				}
			}
		}
		return true; //show that we can close all internal frames
	}

	/**
	 * Determines if the internal frame can be closed.
	 * @param internalFrame The internal frame requesting to be closed.
	 * @return <code>true</code> if the currently open internal frame can be closed, else <code>false</code> if closing should be canceled.
	 */
	public boolean canCloseInternalFrame(final JInternalFrame internalFrame) {
		final ObjectState<RDFResource> mdiDocument = getMDIDocument(internalFrame); //see if there is an MDI document in the frame
		if(mdiDocument != null) { //if there is an MDI document
		//TODO fix			return getApplicationFrame().canClose(mdiDocument); //see if we can close the MDI document
		}
		return true; //default to allowing the frame to be closed
	}

	/**
	 * Invoked when a internal frame has been opened.
	 * @param internalFrameEvent The event giving informatoin about the internal frame.
	 * @see javax.swing.JInternalFrame#show
	 */
	public void internalFrameOpened(final InternalFrameEvent internalFrameEvent) {
	}

	/**
	 * Invoked when an internal frame is in the process of being closed. The close operation can be overridden at this point.
	 * @param internalFrameEvent The event giving informatoin about the internal frame.
	 * @see javax.swing.JInternalFrame#setDefaultCloseOperation
	 */
	public void internalFrameClosing(final InternalFrameEvent internalFrameEvent) {
	}

	/**
	 * Invoked when an internal frame has been closed.
	 * @param internalFrameEvent The event giving informatoin about the internal frame.
	 * @see javax.swing.JInternalFrame#setClosed
	 */
	public void internalFrameClosed(final InternalFrameEvent internalFrameEvent) {
		Log.trace("internal frame closed"); //TODO del
		if(getDesktopPane().getAllFrames().length == 0) //if this is the last frame closed
			;//TODO fix		  getApplicationFrame().updateStatus();  //update our actions to reflect that all frames have been closed
	}

	/**
	 * Invoked when an internal frame is iconified.
	 * @param internalFrameEvent The event giving informatoin about the internal frame.
	 * @see javax.swing.JInternalFrame#setIcon
	 */
	public void internalFrameIconified(final InternalFrameEvent internalFrameEvent) {
	}

	/**
	 * Invoked when an internal frame is de-iconified.
	 * @param internalFrameEvent The event giving informatoin about the internal frame.
	 * @see javax.swing.JInternalFrame#setIcon
	 */
	public void internalFrameDeiconified(final InternalFrameEvent internalFrameEvent) {
	}

	/**
	 * Invoked when an internal frame is activated.
	 * @param internalFrameEvent The event giving informatoin about the internal frame.
	 * @see javax.swing.JInternalFrame#setSelected
	 */
	public void internalFrameActivated(final InternalFrameEvent internalFrameEvent) {
		//TODO fix		getApplicationFrame().updateStatus();  //update our actions to reflect the new frame selected
	}

	/**
	 * Invoked when an internal frame is de-activated.
	 * @param internalFrameEvent The event giving informatoin about the internal frame.
	 * @see javax.swing.JInternalFrame#setSelected
	 */
	public void internalFrameDeactivated(final InternalFrameEvent internalFrameEvent) {
	}

	/**
	 * Creates a default internal frame that is resizable, closable, maximizable, and iconifyable.
	 * @param title The title for the internal frame.
	 * @return A new default internal frame with the given title.
	 */
	public static JInternalFrame createDefaultInternalFrame(final String title) {
		final JInternalFrame internalFrame = new JInternalFrame(title, true, true, true, true); //create a default internal frame
		internalFrame.setVisible(true); //TODO testing
		internalFrame.setBounds(0, 0, 640, 480); //TODO testing
		return internalFrame; //return the frame we created
	}

	/**
	 * Creates a default internal frame with the given container as its content pane.
	 * @param title The title for the internal frame.
	 * @param contentPane The container to use as the main content pane.
	 * @return A new default internal frame with the given title and content pane.
	 */
	public static JInternalFrame createDefaultInternalFrame(final String title, final Container contentPane) {
		final JInternalFrame internalFrame = createDefaultInternalFrame(title); //create a default internal frame
		internalFrame.setContentPane(contentPane); //set the content pane to the one given
		return internalFrame; //return the internal frame we created
	}

}
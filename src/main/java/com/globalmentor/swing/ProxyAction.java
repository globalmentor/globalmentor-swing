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

import java.awt.event.*;
import java.beans.*;
import javax.swing.*;

/**
 * Action that stands as a proxy for another action. This action will change its properties to reflect the proxied action, and will activate the proxied action
 * when activated.
 * <p>
 * Some code based upon {@link AbstractButton}
 * </p>
 * @author Garret Wilson
 */
public class ProxyAction extends AbstractAction implements PropertyChangeListener {

	/** The action being proxied, or <code>null</code> if no action is being proxied. */
	private Action proxiedAction = null; //TODO maybe later make this a weak reference so that if it goes away, we remove ourselves as a listener or something

	//TODO can we ever get garbage collected? won't the proxied action still be referencing us?

	/**
	 * @return The action being proxied, or <code>null</code> if no action is being proxied.
	 */
	public Action getProxiedAction() {
		return proxiedAction;
	}

	/** The listener we use to find out when the proxied action is changing. */
	//TODO del if not needed	private PropertyChangeListener actionPropertyChangeListener;

	/** Whether action properties should be updated when the proxied action changes. */
	//TODO fix	private boolean shouldUpdateProperties=true;

	/**
	 * Defines a proxy action object with a default description string and default icon.
	 */
	public ProxyAction() {
		super(); //construct the parent
		setProxiedAction(null); //show that we don't have a proxied action, yet (this will correctly disable the action)
		setEnabled(true); //TODO testing
	}

	/**
	 * Defines a proxy action object with the specified description string and a default icon.
	 * @param name The name description of the action.
	 */
	public ProxyAction(final String name) {
		super(name); //construct the parent
		setProxiedAction(null); //show that we don't have a proxied action, yet (this will correctly disable the action)
	}

	/**
	 * Defines a proxy action object with the specified description string and the specified icon.
	 * @param name The name description of the action.
	 * @param icon The icon to represent the action.
	 */
	public ProxyAction(final String name, final Icon icon) {
		super(name, icon); //construct the parent
		setProxiedAction(null); //show that we don't have a proxied action, yet (this will correctly disable the action)
	}

	/**
	 * Defines a proxy action object initialized with a proxied action.
	 * @param action The action to proxy.
	 * @see #setProxiedAction
	 */
	public ProxyAction(final Action action) {
		super(); //construct the parent
		setProxiedAction(action); //set the action being proxied
	}

	/**
	 * Sets the proxying <code>Action</code> properties according to values from the proxied <code>Action</code> instance. Be default all properties are updated.
	 * //TODO fix
	 * <p>
	 * If the <code>Action</code> passed in is <code>null</code>, the following things will occur:
	 * </p>
	 * <ul>
	 * <li>the text is set to <code>null</code>,
	 * <li>the icon is set to <code>null</code>,
	 * <li>enabled is set to true,
	 * <li>the tooltip text is set to <code>null</code>
	 * </ul>
	 * @param action The <code>Action</code> from which to get the properties, or <code>null</code>.
	 * @see Action
	 * @see #setProxiedAction
	 */
	protected void configurePropertiesFromAction(final Action action) {
		//TODO Del Log.trace("configuring properties from actions: ", action); //TODO del
		if(action != null) { //if there is an action
			if(action instanceof AbstractAction) { //if the action is an abstract action, we can iterate its properties
				final Object[] keys = ((AbstractAction)action).getKeys(); //get the keys to the action's properties
				if(keys != null) { //if there are keys
					for(int i = keys.length - 1; i >= 0; --i) { //look at each key
						final Object key = keys[i]; //get the key
						if(key instanceof String) { //if the key is a string
							final String keyString = (String)key; //cast the key to a string
							putValue(keyString, action.getValue(keyString)); //put the key's value in our action
						}
					}
				}
			} else { //if the given action is a normal action, update the values that we know about
				//TODO don't change the name to work around a Java bug that adds the text to buttons even if they shouldn't be changed
				//TODO fix; see above comment		  putValue(Action.NAME, action.getValue(Action.NAME));
				putValue(NAME, action.getValue(NAME)); //TODO see if this works in JDK 1.4.1
				putValue(SMALL_ICON, action.getValue(SMALL_ICON));
				putValue(SHORT_DESCRIPTION, action.getValue(SHORT_DESCRIPTION));
				putValue(LONG_DESCRIPTION, action.getValue(LONG_DESCRIPTION));
				/*TODO fix; with a proxied action with a mnemonic key of Integer('s'), this traps Alt+F4
						  if(action.getValue(Action.MNEMONIC_KEY)!=null)  //TODO testing; why is this needed, and not for the others?
						    putValue(Action.MNEMONIC_KEY, action.getValue(Action.MNEMONIC_KEY));
				*/
				//TODO fix; this gives strange results sometimes		  putValue(Action.ACTION_COMMAND_KEY, action.getValue(Action.ACTION_COMMAND_KEY));
				//TODO fix; this gives strange results sometimes		  putValue(Action.ACCELERATOR_KEY, action.getValue(Action.ACCELERATOR_KEY));
				putValue(ACCELERATOR_KEY, action.getValue(ACCELERATOR_KEY)); //TODO is this fixed in JDK1.4.x?
			}
			setEnabled(action.isEnabled()); //update the enabled property (which isn't stored using a normal key/value pair, as are the other properties)
		} else { //if there is no action
			//TODO del Log.trace("no action; disabling"); //TODO del
			//TODO fix the properties appropriately
			setEnabled(false); //disable this action
		}
	}

	/**
	 * Sets the proxied <code>Action</code> to receive action events and to notify this action of any property changes. The new <code>Action</code> replaces any
	 * previously set <code>Action</code> but does not affect <code>ActionListeners</code> independently added with <code>addActionListener</code>. If the
	 * <code>Action</code> is already a registered <code>ActionListener</code> for the button, it is not re-registered.
	 * <p>
	 * A side-effect of setting the <code>Action</code> is that the <code>ActionEvent</code> source's properties are immediately set from the values in the
	 * <code>Action</code> (performed by the method <code>configurePropertiesFromAction</code>) and subsequently updated as the <code>Action</code>'s properties
	 * change (via a <code>PropertyChangeListener</code> created by the method <code>createActionPropertyChangeListener</code>).
	 * </p>
	 * @param action The <code>Action</code> to be proxied, or <code>null</code> if proxying should be discontinued.
	 * @see AbstractAction
	 * @see #getProxiedAction
	 * @see #configurePropertiesFromAction
	 */
	public void setProxiedAction(final Action action) {
		final Action oldAction = getProxiedAction(); //see what action we had before
		if(proxiedAction == null || !proxiedAction.equals(action)) { //if we didn't have an action or if they want a new action
			proxiedAction = action; //update the action
			if(oldAction != null) { //if we had an action before
				//TODO del				removeActionListener(oldAction);  //don't listen for the old action anymore
				oldAction.removePropertyChangeListener(this); //don't listen for the old action anymore
				//TODO del if not needed				actionPropertyChangeListener=null;  //we no longer need the listener for the old action
			}
			configurePropertiesFromAction(proxiedAction); //now that we have a new action, configure our properties from it
			if(proxiedAction != null) { //if we now have an action
				/*TODO do we need this?
						// Don't add if it is already a listener
						if (!isListener(ActionListener.class, action)) {
						    addActionListener(action);
							}
				*/
				proxiedAction.addPropertyChangeListener(this); //add ourselves as a listener to the property changes of the proxied action
				/*TODO del if not needed
							//create a listener to find out when the proxied action changes properties
						  actionPropertyChangeListener=createActionPropertyChangeListener(action);
						  proxiedAction.addPropertyChangeListener(actionPropertyChangeListener);  //add the listener to the proxied action
				*/
			}
		}
		firePropertyChange("proxiedAction", oldAction, proxiedAction); //show that our proxied action has changed TODO use a constant here
	}

	/**
	 * Called whenever a bound property in the proxied action is changed.
	 * @param propertyChangeEvent A <code>PropertyChangeEvent</code> object describing the event source and the property that has changed.
	 */
	public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
		final String propertyName = propertyChangeEvent.getPropertyName(); //get the property name being changed
		//TODO del Log.trace("proxied property changed: ", propertyName);  //TODO del when fixed enabled/disabled
		//TODO del Log.trace("new value: ", propertyChangeEvent.getNewValue());  //TODO del when fixed enabled/disabled
		/*TODO fix for when/if we have a weak reference to the proxied action
				if(proxyAction==null) {	//if we no longer have a proxy action (its weak reference has been garbage collected)
					final Action proxiedAction=(Action)e.getSource(); //get the action being proxied
					proxiedAction.removePropertyChangeListener(this); //remove ourselves as a listener from the action being proxied
				}
				else {	//if we still have a proxy action
		*/
		//TODO del Log.trace("property being changed: ", propertyName);  //TODO del
		/*TODO del; not needed; "enabled" property changes apparently property propogate
				if("enabled".equals(propertyName)) {	//if the enabled property is changing, we'll have to call the method manually, apparently
		
				}
				else
		*/
		if(Components.ENABLED_PROPERTY.equals(propertyName)) { //if the enabled state is changing, we must change the actual property (which will change the property value), rather than just changing the underlying value or the actual property won't change
			setEnabled(((Boolean)propertyChangeEvent.getNewValue()).booleanValue()); //TODO testing
		} else if(!"proxiedAction".equals(propertyName)) { //if the proxied action is itself proxied, don't respond when it changes its proxied action TODO use constant here
			if(!NAME.equals(propertyName)) //TODO don't change the name to work around a Java bug that adds the text to buttons even if they shouldn't be changed TODO check with JDK 1.4.x
				putValue(propertyName, propertyChangeEvent.getNewValue()); //unconditionally update the value in the proxied action
		}
	}

	/**
	 * Called when the action should be performed. Forwards the event on to the proxied action. A descendant class can override this event for special processing
	 * before and/or after the proxied action receives the event.
	 * @param actionEvent The event causing the action.
	 */
	public void actionPerformed(final ActionEvent actionEvent) {
		if(proxiedAction != null) //if we have a proxied action
			proxiedAction.actionPerformed(actionEvent); //forward the action
	}

	/**
	 * Factory method which creates the <code>PropertyChangeListener</code> used to update the <code>ActionEvent</code> source as properties change on its
	 * <code>Action</code> instance. The default property change listener simply updates all properties.
	 * <p>
	 * Note that <code>PropertyChangeListeners</code> should avoid holding strong references to the <code>ActionEvent</code> source, as this may hinder garbage
	 * collection of the <code>ActionEvent</code> source and all components in its containment hierarchy.
	 * @param proxiedAction The new action the property changes of which should be monitored.
	 * @see Action
	 * @see #setProxiedAction
	 */
	/*TODO del when not needed
		protected PropertyChangeListener createActionPropertyChangeListener(final Action proxiedAction)
		{
			return new ProxyActionPropertyChangeListener(this, proxiedAction);  //create a listener and return it
		}
	
		private static class ProxiedActionPropertyChangeListener extends AbstractActionPropertyChangeListener
		{
			ProxiedActionPropertyChangeListener(final ProxyAction proxyAction, final Action proxiedAction)
			{
		    super(proxyAction, proxiedAction);
		  }
	
			public void propertyChange(PropertyChangeEvent e)
			{
				final String propertyName=e.getPropertyName();  //get the property name being changed
		    final ProxyAction proxyAction=(ProxyAction)getTarget(); //get the proxy action we represent
		    if(proxyAction==null) {	//if we no longer have a proxy action (its weak reference has been garbage collected)
					final Action proxiedAction=(Action)e.getSource(); //get the action being proxied
					proxiedAction.removePropertyChangeListener(this); //remove ourselves as a listener from the action being proxied
			  }
				else {	//if we still have a proxy action
					proxyAction.setProperty(propertyName, e.getNewValue());  //unconditionally update the value in the proxied action
				}
			}
		}
	*/

}

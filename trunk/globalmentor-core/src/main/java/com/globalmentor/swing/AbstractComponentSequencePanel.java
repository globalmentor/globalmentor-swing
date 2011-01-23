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

import com.globalmentor.model.Verifiable;

/**Base class for panels that allow progression from one contained panel to
	another. Useful for wizard panels.
<p>In order for the correct buttons to automatically be shown, the preferred
	method is to call {@link #showSequenceDialog(Component, String)}.
<p>The panel's content component represents the current component in the
	sequence.</p>
<p>Each of the components in the sequence that implement {@link Verifiable}
	will be verified before moving to the next component.</p>
@author Garret Wilson
@see ContentPanel#getContentComponent()
@see #showSequenceDialog(Component, String)
*/
public abstract class AbstractComponentSequencePanel extends AbstractSequencePanel
{

	/**Default constructor.*/
	public AbstractComponentSequencePanel()
	{
		this(true, true); //construct and initialize the panel with toolbar and status bar
	}

	/**Toolbar and status bar option constructor.
	@param hasToolBar Whether this panel should have a toolbar.
	@param hasStatusBar Whether this panel should have a status bar.
	*/
	public AbstractComponentSequencePanel(final boolean hasToolBar, final boolean hasStatusBar)
	{
		this(hasToolBar, hasStatusBar, true); //do the default construction and initialize
	}

	/**Toolbar and status bar option constructor with optional initialization
	@param hasToolBar Whether this panel should have a toolbar.
	@param hasStatusBar Whether this panel should have a status bar.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public AbstractComponentSequencePanel(final boolean hasToolBar, final boolean hasStatusBar, boolean initialize)
	{
		super(hasToolBar, hasStatusBar, false);	//construct the panel, but don't initialize
		if(initialize)  //if we should initialize the panel
			initialize();   //initialize everything		
	}

	/**Initializes the user interface.*/
	protected void initializeUI()
	{
		super.initializeUI();	//do the default initialization
//TODO del when works		start();	//start the sequence
	}

	/**Goes to the first step in the sequence.*/
	protected void first()
	{
		final Component firstComponent=getFirstComponent();	//get the first component
		setContentComponent(firstComponent!=null ? firstComponent : getDefaultComponent());	//start with the first component in the sequence, if there is one		
	}

	/**Goes to the previous component in the sequence. If there is no previous
		component, no action occurs.
	*/
	protected void previous()
	{
		final Component previousComponent=getPreviousComponent();	//get the previous component
		setContentComponent(previousComponent!=null ? previousComponent : getDefaultComponent());	//go to the previous component in the sequence, if there is one
	}

	/**Goes to the next component in the sequence.*/
	protected void next()
	{
		final Component nextComponent=getNextComponent();	//get the next component
		setContentComponent(nextComponent!=null ? nextComponent : getDefaultComponent());	//go to the next component in the sequence, if there is one
	}
	
	/**@return The first component to be displayed in the sequence.*/
	protected abstract Component getFirstComponent();

	/**@return <code>true</code> if there is a next component after the current one.*/
	protected abstract boolean hasNext();

	/**@return The next component to be displayed in the sequence.*/
	protected abstract Component getNextComponent();

	/**@return <code>true</code> if there is a previous component before the current one.*/
	protected abstract boolean hasPrevious();

	/**@return The previous component to be displayed in the sequence.*/
	protected abstract Component getPreviousComponent();

}

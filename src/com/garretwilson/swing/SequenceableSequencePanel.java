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

import java.awt.*;
import java.util.*;
import static java.util.Collections.*;
import com.globalmentor.model.Sequenceable;

/**A sequence panel that determines the sequence based upon components that
	implement the {@link Sequenceable} interface.
<p>The class is constructed with an initial component to display. Subsequently,
	if the component implements {@link Sequenceable}, returns
	<code>true</code> for {@link Sequenceable#hasNext()}, and the
	<code>Sequenceable.getNext()</code> method returns a {@link Component},
	that component will be displayed next in the sequence, etc. The sequence will
	be allowed to finish once a component is reached that returns
	<code>false</code> for {@link Sequenceable#hasNext()}.</p>
<p>The current collected sequence of components are kept in an internal stack,
	an can be iterated at any time using
	{@link #getSequenceHistoryIterator()}. This iterator will return all
	components starting with the initial component up to but not including the
	current component, which can be returned using
	{@link #getContentComponent()}./p>
@author Garret Wilson
@see Sequenceable
@see ContentPanel#getContentComponent()
*/
public class SequenceableSequencePanel extends AbstractComponentSequencePanel	//TODO fix or del implements DefaultFocusable
{

	/**The first component in the sequence.*/
	private final Component firstComponent;

	/**The list of visited components in the sequence.*/
	private final LinkedList sequenceHistoryList;

		/**@return The list of visited components in the sequence.*/
		protected LinkedList getSequenceHistoryList() {return sequenceHistoryList;}
		
		/**@return A read-only iterator to the visited components in the sequence.*/
		public Iterator getSequenceHistoryIterator() {return unmodifiableList(sequenceHistoryList).iterator();}

	/**Initial component initialization constructor.
	@param firstComponent The first component in the sequence.
	*/
	public SequenceableSequencePanel(final Component firstComponent)
	{
		this(firstComponent, true); //construct and automatically initialize the object
	}

	/**Constructor that specifies the first component and allows optional
		initialization.
	@param firstComponent The first component in the sequence.
	@param hasToolBar Whether this panel should have a toolbar.
	@param hasStatusBar Whether this panel should have a status bar.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public SequenceableSequencePanel(final Component firstComponent, final boolean initialize)
	{
		this(firstComponent, true, true, initialize);	//construct the panel with a toolbar and status bar
	}

	/**Constructor that specifies the first component and allows optional
		initialization.
	@param firstComponent The first component in the sequence.
	@param hasToolBar Whether this panel should have a toolbar.
	@param hasStatusBar Whether this panel should have a status bar.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public SequenceableSequencePanel(final Component firstComponent, final boolean hasToolBar, final boolean hasStatusBar, final boolean initialize)
	{
		super(hasToolBar, hasStatusBar, false);	//create a sequence panel but don't initialize
		this.firstComponent=firstComponent;	//save the first component
		sequenceHistoryList=new LinkedList();	//create the list of visited components
//TODO del		sequenceHistoryList.addLast(initialComponent);	//push the initial component onto the stack
		if(initialize)  //if we should initialize the panel
			initialize();   //initialize everything		
	}
	
	/**@return The component that should get the initial focus.*/
//TODO fix	public Component getInitialFocusComponent() {return labelTextField;}

	/**@return The first component to be displayed in the sequence.*/
	protected Component getFirstComponent()
	{
		return firstComponent;	//return the first component, which we saved at the first
			//return the first component in the list, or null if there are no components in the list (which should never happen)
//TODO del		return getComponentSequenceList().size()>0 ? (Component)getComponentSequenceList().get(0) : null;
	}
	
	/**@return <code>true</code> if there is a next component after the current
		one, which for this version means the current component implements
		<code>Sequenceable</code> and return <code>true</code> for
		<code>hasNext()</code>.
	@see Sequenceable#hasNext()
	*/
	protected boolean hasNext()
	{
			//return true if the content component is sequenceable and has a next component
		return getContentComponent() instanceof Sequenceable && ((Sequenceable)getContentComponent()).hasNext();
	}

	/**@return The next component to be displayed in the sequence, the
		<code>Component</code> returned from <code>Sequenceable.getNext()</code>.
	@see Sequenceable#getNext()
	*/
	protected Component getNextComponent()
	{
		if(getContentComponent() instanceof Sequenceable)	//if the current component is sequenceable
		{
			final Object nextObject=((Sequenceable)getContentComponent()).getNext();	//get the next object in the sequence
			if(nextObject instanceof Component)	//if the next object is a component
			{
				getSequenceHistoryList().addLast(getContentComponent());	//add the current component to our stack 
				return (Component)nextObject;	//return the next component
			}
		}
		return null;	//if the content component doesn't know the next component, return null
	}

	/**@return <code>true</code> if there is a previous component before the
		current one, in this case if there is at least one component on the stack.
	*/
	protected boolean hasPrevious()
	{
		return getSequenceHistoryList().size()>0;	//there is a previous component if there is a component on the stack
	}

	/**@return The previous component to be displayed in the sequence, the top
		component on the stack.
	*/
	protected Component getPreviousComponent()
	{
		return getSequenceHistoryList().size()>0 ? (Component)getSequenceHistoryList().removeLast() : null;	//return and remove the top component on the stack or null if nothing is on the stack
	}
}

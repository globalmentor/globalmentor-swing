package com.garretwilson.swing;

import java.awt.*;
import java.util.*;
import com.garretwilson.swing.*;
import com.garretwilson.util.*;

/**A sequence panel that determines the sequence based upon components that
	implement the <code>Sequenceable</code> interface.
<p>The class is constructed with an initial component to display. Subsequently,
	if the component implements <code>Sequenceable</code>, returns
	<code>true</code> for <code>Sequenceable.hasNext()</code>, and the
	<code>Sequenceable.getNext()</code> method returns a <code>Component</code>,
	that component will be displayed next in the sequence, etc. The sequence will
	be allowed to finish once a component is reached that returns
	<code>false</code> for <code>Sequenceable.hasNext()</code>.</p>
<p>The current collected sequence of components are kept in an internal stack,
	an can be iterated at any time using
	<code>getSequenceHistoryIterator()</code>. This iterator will return all
	components starting with the initial component up to but not including the
	current component, which can be returned using
	<code>getContentComponent()</code>./p>
@author Garret Wilson
@see Sequenceable
@see ContentPanel#getContentComponent()
*/
public class SequenceableSequencePanel extends AbstractSequencePanel	//G***fix or del implements DefaultFocusable
{

	/**The first component in the sequence.*/
	private final Component firstComponent;

	/**The list of visited components in the sequence.*/
	private final LinkedList sequenceHistoryList;

		/**@return The list of visited components in the sequence.*/
		protected LinkedList getSequenceHistoryList() {return sequenceHistoryList;}
		
		/**@return A read-only iterator to the visited components in the sequence.*/
		public Iterator getSequenceHistoryIterator() {return Collections.unmodifiableList(sequenceHistoryList).iterator();}

	/**Initial component initialization constructor.
	@param firstComponent The first component in the sequence.
	*/
	public SequenceableSequencePanel(final Component firstComponent)
	{
		this(firstComponent, false, false); //default to having no toolbar or status bar
	}

	/**Application component constructor that allows options to be set, such as 
		the presence of a status bar.
	@param firstComponent The first component in the sequence.
	@param hasToolBar Whether this panel should have a toolbar.
	@param hasStatusBar Whether this panel should have a status bar.
	*/
	public SequenceableSequencePanel(final Component firstComponent, final boolean hasToolBar, final boolean hasStatusBar)
	{
		this(firstComponent, hasToolBar, hasStatusBar, true); //construct and automatically initialize the object
	}

	/**Constructor that allows options to be set, such as the presence of a status
		bar, and allows optional initialization.
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
//G***del		sequenceHistoryList.addLast(initialComponent);	//push the initial component onto the stack
		initialize();	//initialize the panel
	}
	
	/**@return The component that should get the initial focus.*/
//G***fix	public Component getInitialFocusComponent() {return labelTextField;}

	/**@return The first component to be displayed in the sequence.*/
	protected Component getFirstComponent()
	{
		return firstComponent;	//return the first component, which we saved at the first
			//return the first component in the list, or null if there are no components in the list (which should never happen)
//G***del		return getComponentSequenceList().size()>0 ? (Component)getComponentSequenceList().get(0) : null;
	}
	
	/**@return <code>true</code> if there is a next component after the current
		one, which for this version means the current component implements
		<code>Sequenceable</code> and return <code>true</code> for
		<code>hasNext()</code>.
	@see Sequenceable#hasNext()
	*/
	protected boolean hasNextComponent()
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
				return (Component)nextObject;	//return the next component
			}
		}
		return null;	//if the content component doesn't know the next component, return null
	}

	/**@return <code>true</code> if there is a previous component before the
		current one, in this case if there is at least one component on the stack.
	*/
	protected boolean hasPreviousComponent()
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

package com.garretwilson.swing;

import java.awt.*;
import javax.swing.*;

/**Base class for panels that allow progression from one contained panel to
	another. Useful for wizard panels.
<p>In order for the correct buttons to automatically be shown, the preferred
	method is to call <code>showSequenceDialog</code>.
<p>The panel's content component represents the current component in the
	sequence.</p>
<p>Each of the components in the sequence that implement <code>Verifiable</code>
	will be verified before moving to the next component.</p>
@author Garret Wilson
@see ContentPanel#getContentComponent
@see #showSequenceDialog
*/
public abstract class AbstractComponentSequencePanel extends AbstractSequencePanel
{

	/**Default constructor.*/
	public AbstractComponentSequencePanel()
	{
		this(false, false); //default to having no toolbar or status bar
	}

	/**Constructor that allows options to be set, such as	the presence of a
		status bar.
	@param hasToolBar Whether this panel should have a toolbar.
	@param hasStatusBar Whether this panel should have a status bar.
	*/
	public AbstractComponentSequencePanel(final boolean hasToolBar, final boolean hasStatusBar)
	{
		this(hasToolBar, hasStatusBar, true); //construct and automatically initialize the object
	}
	
	/**Constructor that allows options to be set, such as the presence of a status
		bar, and allows optional initialization.
	@param hasToolBar Whether this panel should have a toolbar.
	@param hasStatusBar Whether this panel should have a status bar.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public AbstractComponentSequencePanel(final boolean hasToolBar, final boolean hasStatusBar, final boolean initialize)
	{
		super(hasToolBar, hasStatusBar, false);	//construct the panel with the first component, using a status bar and an optional toolbar, but don't initialize
//G***initialize something
		if(initialize)  //if we should initialize the panel
			initialize();   //initialize everything		
	}

	/**Initializes the user interface.*/
	protected void initializeUI()
	{
		super.initializeUI();	//do the default initialization
		setContentComponent(getFirstComponent());	//start with the first component in the sequence
	}

	/**Initializes the toolbar components.
		Any derived class that overrides this method should call this version.
	@param toolBar The toolbar to be initialized.
	*/
/*G***del
	protected void initializeToolBar(final JToolBar toolBar)
	{
		super.initializeToolBar(toolBar); //do the default toolbar initialization
		toolBar.setRollover(false);	//turn off rollovers on the toolbar
		toolBar.add(getPreviousButton()); //previous
		toolBar.add(getNextButton()); //next
		toolBar.addSeparator();  //--
		toolBar.add(getFinishButton()); //finish
	}
*/
	
	/**Goes to the previous component in the sequence. If there is no previous
		component, no action occurs.
	*/
	public void previous()
	{
		setContentComponent(getPreviousComponent());	//go to the previous component in the sequence
	}

	/**Goes to the next component in the sequence.*/
	protected void next()
	{
		setContentComponent(getNextComponent());	//go to the next component in the sequence
	}
	
	/**Finishes the sequence by setting the option panel value to
		<code>JOptionPane.OK_OPTION</code>, if this panel is embedded in an option
		pane.
	@see JOptionPane#OK_OPTION
	@see BasicPanel#setOptionPaneValue
	*/
	protected void finish()
	{
		setOptionPaneValue(new Integer(JOptionPane.OK_OPTION));	//set the value of the option pane to OK, if we're embedded in an option pane
	}
	
	/**Creates and displays a sequence dialog based upon <code>JOptionPane</code>,
		showing this sequence panel.
	@param parentComponent Determines the <code>Frame</code> in which the
		dialog is displayed; if <code>null</code>, or if the
		<code>parentComponent</code> has no <code>Frame</code>, a default
		<code>Frame</code> is used.
	@param title The title string for the dialog.
	@return One of the <code>JOptionPane</code> result constants.
	@see JOptionPane#showOptionDialog
	*/
	public int showSequenceDialog(final Component parentComponent, final String title)
	{	
			//show an option pane in the parent component using the given title
		return OptionPane.showOptionDialog(parentComponent, this, title,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				new Object[]{getPreviousButton(), getNextButton(), getFinishButton()}, getNextButton());
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

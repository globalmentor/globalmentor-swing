package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.util.*;

/**Base class for a panel that allows progression in a sequence.
@author Garret Wilson
*/
public abstract class AbstractSequencePanel extends ApplicationPanel	//G***probably change this to ContentPanel
{
	
	/**The action for going to the previous component.*/
	private final Action previousAction;

		/**@return The action for going to the previous component.*/
		public Action getPreviousAction() {return previousAction;}

	/**The action for going to the next component.*/
	private final Action nextAction;

		/**@return The action for going to the next component.*/
		public Action getNextAction() {return nextAction;}

	/**The action for finishing the sequence.*/
	private final Action finishAction;

		/**@return The action for finishing the sequence.*/
		public Action getFinishAction() {return finishAction;}

	/**The button for going to the previous component; created from the corresponding action.*/
	private final JButton previousButton;

		/**@return The action for going to the previous component; created from the corresponding action.
		@see #getPreviousAction
		*/
		public JButton getPreviousButton() {return previousButton;}
		
	/**The button for going to the next component; created from the corresponding action.*/
	private final JButton nextButton;

		/**@return The action for going to the next component; created from the corresponding action.
		@see #getNextAction
		*/
		public JButton getNextButton() {return nextButton;}
		
	/**The button for finishing the sequence; created from the corresponding action.*/
	private final JButton finishButton;

		/**@return The action for finishing the sequence; created from the corresponding action.
		@see #getFinishAction
		*/
		public JButton getFinishButton() {return finishButton;}		

	/**Default constructor.*/
	public AbstractSequencePanel()
	{
		this(false, false); //default to having no toolbar or status bar
	}

	/**Constructor that allows options to be set, such as	the presence of a
		status bar.
	@param hasToolBar Whether this panel should have a toolbar.
	@param hasStatusBar Whether this panel should have a status bar.
	*/
	public AbstractSequencePanel(final boolean hasToolBar, final boolean hasStatusBar)
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
	public AbstractSequencePanel(final boolean hasToolBar, final boolean hasStatusBar, final boolean initialize)
	{
		super(hasToolBar, hasStatusBar, false);	//construct the panel with the first component, using a status bar and an optional toolbar, but don't initialize
		previousAction=new PreviousAction();  //create the actions
		nextAction=new NextAction();
		finishAction=new FinishAction();
		previousButton=new JButton(previousAction);	//create the buttons
		nextButton=new JButton(nextAction);
		finishButton=new JButton(finishAction);
		if(initialize)  //if we should initialize the panel
			initialize();   //initialize everything		
	}

	/**Initializes the user interface.*/
	protected void initializeUI()
	{
		super.initializeUI();	//do the default initialization
//G***del; let child classes customize this		setStatusBarPosition(BorderLayout.NORTH);  //put the status bar at the top of the panel G***fix both of these
//G***del; let child classes customize this		setToolBarPosition(BorderLayout.SOUTH);  //put the toolbar at the bottom of the panel
		setPreferredSize(new Dimension(300, 200));	//set an arbitrary preferred size
	}

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
	protected void updateStatus()
	{
		super.updateStatus(); //update the default actions
		getPreviousAction().setEnabled(hasPrevious()); //only allow going backwards if we have a previous step
		getNextAction().setEnabled(hasNext()); //only allow going backwards if we have a next step
		getFinishAction().setEnabled(!hasNext()); //only allow finishing if there are no next components
		final JRootPane rootPane=getRootPane();	//get the ancestor root pane, if there is one
		if(rootPane!=null)	//if there is a root pane
		{
				//set the next button as the default unless we're finished; in that case, set the finish button as the default
			rootPane.setDefaultButton(hasNext() ? getNextButton() : getFinishButton());
		}
	}
	
	
	/**Goes to the previous step in the sequence. If there is no previous
		component, no action occurs.
	<p>Derived classes should override <code>previous()</code>.</p>
	@see #previous
	*/
	public void goPrevious()
	{
		if(hasPrevious())	//if there is a previous step
		{
			previous();	//go the previous step
			updateStatus();	//update the status
		}
	}
	
	/**Goes to the previous step in the sequence. If there is no previous
		step, no action occurs.
	*/
	protected abstract void previous();

	/**Goes to the next step in the sequence. If there is no next step,
		no action occurs.
	<p>Derived classes should override <code>next()</code>.</p>
	@see #next
	*/
	public void goNext()
	{
		if(hasNext())  //if there is a next step
		{
			previous();	//go the next step
			updateStatus();	//update the status
		}
	}

	/**Goes to the next step in the sequence. If there is no next
		step, no action occurs.
	*/
	protected abstract void next();
	
	/**Verifies the contents and finishes the sequence.
	Usually a derived class will not modify this method and instead override
		<code>finish()</code>, which this method calls if the contents of the
		current component verifies.
	@see Verifiable#verify
	@see #finish
	*	*/
	public void goFinish()
	{
		final Component currentComponent=getContentComponent();	//get the current content component
		if(currentComponent instanceof Verifiable)	//if we can verify the component's contents
		{
			if(!((Verifiable)currentComponent).verify())	//if the current component's contents do not verify
			{
				return;	//don't finish if the component's contents do not verify
			}
		}
		finish();	//actually finish
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
	
	/**@return <code>true</code> if there is a next step after the current one.*/
	protected abstract boolean hasNext();

	/**@return <code>true</code> if there is a previous step before the current one.*/
	protected abstract boolean hasPrevious();

	/**Action for going to the previous component.*/
	class PreviousAction extends AbstractAction
	{
		/**Default constructor.*/
		public PreviousAction()
		{
			super("Previous");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Previous step");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Go to the previous step.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer('p'));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.HAND_POINT_LEFT_ICON_FILENAME)); //load the correct icon
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			goPrevious();  //go to the previous component
		}
	}

	/**Action for going to the next component.*/
	class NextAction extends AbstractAction
	{
		/**Default constructor.*/
		public NextAction()
		{
			super("Next");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Next step");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Go to the next step.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer('n'));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.HAND_POINT_RIGHT_ICON_FILENAME)); //load the correct icon
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			goNext();  //go to the next component
		}
	}

	/**Action for finishing the progresion.*/
	class FinishAction extends AbstractAction
	{

		/**Default constructor.*/
		public FinishAction()
		{
			super("Finish");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Finish sequence");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Finish the sequence.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer('f'));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.EXIT_ICON_FILENAME)); //load the correct icon
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			goFinish(); //try to finish the sequence
		}
	}

}

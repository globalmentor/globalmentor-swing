package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.util.*;

/**Base class for a panel that allows progression in a sequence.
<p>When progressing through the sequence, this panel attempts to verify
	 the content component, if it is <code>Verifiable</code>, before changing
	 position in the sequence and before finishing.</p>
@author Garret Wilson
@see Verifiable
*/
public abstract class AbstractSequencePanel extends ToolStatusPanel
{

	/**The lazily-created default default component.*/
	private Component defaultComponent;

	/**The action for starting the sequence.*/
	private final Action startAction;

		/**@return The action for starting the sequence.*/
		public Action getStartAction() {return startAction;}
	
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

	/**The action for advancing; serves as a proxy for the start, next,
		and finish actions, depending on the state of the sequence.
	*/
	private final ProxyAction advanceAction;

		/**The action for advancing; serves as a proxy for the start, next,
			and finish actions, depending on the state of the sequence.
		@see #getStartAction()
		@see #getNextAction()
		@see #getFinishAction()
		*/
		public ProxyAction getAdvanceAction() {return advanceAction;}

	/**The button for staring the sequence; created from the corresponding action.*/
	private final JButton startButton;

		/**@return The action for starting the sequence; created from the corresponding action.
		@see #getStartAction
		*/
		public JButton getStartButton() {return startButton;}		

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

	/**The button for advancing in the sequence; created from the corresponding action.*/
	private final JButton advanceButton;

		/**@return The action for advancing in sequence; created from the corresponding action.
		@see #getAdvanceAction
		*/
		public JButton getAdvanceButton() {return advanceButton;}		

	/**Whether the advance buttons are distinct or dual-duty.*/
	private boolean distinctAdvance;

		/**@return Whether the advance buttons are distinct or dual-duty;
			this defaults to <code>false</code>.
		*/
		public boolean isDistinctAdvance() {return distinctAdvance;}

		/**Sets whether the advance buttons are distinct or dual-duty.
		@param distinct <code>true</code> if there should be distinct buttons for
			start, next, and finish, or <code>false</code> if one button should share
			these responsibilitiese.
		*/
		public void setDistinctAdvance(final boolean distinct) {distinctAdvance=distinct;}

	/**Default constructor.*/
	public AbstractSequencePanel()
	{
		this(true, true); //construct and initialize the panel with toolbar and status bar
	}

	/**Toolbar and status bar option constructor.
	@param hasToolBar Whether this panel should have a toolbar.
	@param hasStatusBar Whether this panel should have a status bar.
	*/
	public AbstractSequencePanel(final boolean hasToolBar, final boolean hasStatusBar)
	{
		this(hasToolBar, hasStatusBar, true); //do the default construction and initialize
	}

	/**Toolbar and status bar option constructor with optional initialization
	@param hasToolBar Whether this panel should have a toolbar.
	@param hasStatusBar Whether this panel should have a status bar.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public AbstractSequencePanel(final boolean hasToolBar, final boolean hasStatusBar, boolean initialize)
	{
		super(hasToolBar, hasStatusBar, false);	//construct the panel, but don't initialize
		defaultComponent=null;	//show that we haven't used the default component, yet
		startAction=new StartAction();			//create the actions
		previousAction=new PreviousAction(); 
		nextAction=new NextAction();
		finishAction=new FinishAction();
		advanceAction=new ProxyAction(startAction);	//the advance action will initially proxy the start action
		startButton=new JButton(startAction);	//create the buttons
		previousButton=new JButton(previousAction);
		nextButton=new JButton(nextAction);
		finishButton=new JButton(finishAction);
		advanceButton=new JButton(advanceAction);
		distinctAdvance=false;	//default to shared actions for advancing
		setStatusBarPosition(BorderLayout.NORTH);  //put the status bar at the top of the panel by default
		setToolBarPosition(BorderLayout.SOUTH);  //put the toolbar at the bottom of the panel by default
		if(initialize)  //if we should initialize the panel
			initialize();   //initialize everything		
	}

	/**Initializes actions in the action manager.
	@param actionManager The implementation that manages actions.
	*/
	protected void initializeActions(final ActionManager actionManager)
	{
		super.initializeActions(actionManager);	//do the default initialization
		if(isDistinctAdvance())	//if we should have distinct advance, use separate actions
		{
			actionManager.addToolAction(getStartAction());
			actionManager.addToolAction(new ActionManager.SeparatorAction());
			actionManager.addToolAction(getPreviousAction());
			actionManager.addToolAction(getNextAction());
		}
		else	//if we should not have distinct advance, use a dual-use action
		{
			actionManager.addToolAction(getPreviousAction());
			actionManager.addToolAction(getAdvanceAction());
		} 
	}

	/**Initializes the user interface.*/
	protected void initializeUI()
	{
		if(getToolBar()!=null)	//if we have a toolbar
			getToolBar().setButtonTextVisible(true);	//show text on the toolbar buttons
		super.initializeUI();	//do the default initialization
		previousButton.setHorizontalTextPosition(SwingConstants.LEADING);	//change the text position of the previous button
		setContentComponent(getDefaultComponent());	//start with the default component		
		setPreferredSize(new Dimension(300, 200));	//set an arbitrary preferred size
	}

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
	public void updateStatus()
	{
		super.updateStatus(); //update the default actions
		getStartAction().setEnabled(getAdvanceAction().getProxiedAction()!=getStartAction()); //only allow starting if we haven't started, yet
		getPreviousAction().setEnabled(hasPrevious()); //only allow going backwards if we have a previous step
		getNextAction().setEnabled(hasNext()); //only allow going backwards if we have a next step
		getFinishAction().setEnabled(!hasNext()); //only allow finishing if there are no next components
		if(getAdvanceAction().getProxiedAction()!=getStartAction())	//if we've already started
		{
				//determine if advancing should go to the next item in the sequence or finish
			getAdvanceAction().setProxiedAction(hasNext() ? getNextAction() : getFinishAction());			
		}
		final JRootPane rootPane=getRootPane();	//get the ancestor root pane, if there is one
		if(rootPane!=null)	//if there is a root pane
		{
			final JButton defaultButton;	//determind the default button
			if(isDistinctAdvance())	//if we're using distinct buttons for advance
			{
				defaultButton=hasNext() ? getNextButton() : getFinishButton();	//set the next button as the default unless we're finished; in that case, set the finish button as the default
			}
			else	//if we're using a dual-use button for advance
			{					
				defaultButton=getAdvanceButton();	//set the advance button as the default	
			}
			rootPane.setDefaultButton(defaultButton);	//update the default button	
		}
	}

	/**Returns the default component displayed before the sequence begins or
		if there is no sequence available.
	<p>This implementation returns an empty spacer component.</p>
	@return The default component displayed before the sequence begins.
	*/
	protected Component getDefaultComponent()
	{
		if(defaultComponent==null)	//if we haven't created the default component, yet
		{
			defaultComponent=Box.createGlue();	//create glue to fill the panel
		}
		return defaultComponent;	//return our shared default component
	}

	/**Starts the sequence by going to the first step in the sequence.
	<p>Derived classes should override <code>start()</code>.</p>
	@see #start
	*/
	public void goStart()
	{
		getAdvanceAction().setProxiedAction(getNextAction());	//from now on, advancing will go to the next item in the sequence
		start();	//go the start
	}
	
	/**Starts the sequence by going to the first step in the sequence.
	@see #goFirst
	*/
	protected void start()
	{
		goFirst();	//go to the first step in the sequence
	}

	/**Goes to the first step in the sequence.
	<p>Derived classes should override <code>first()</code>.</p>
	@see #first
	*/
	public void goFirst()
	{
		first();	//go the first
		updateStatus();	//update the status
	}
	
	/**Goes to the first step in the sequence.*/
	protected abstract void first();

	/**Goes to the previous step in the sequence. If there is no previous
		component, no action occurs.
	<p>Derived classes should override <code>previous()</code>.</p>
	@see #previous
	*/
	public void goPrevious()
	{
		if(hasPrevious() && verifyCurrentComponent())	//if there is a previous step and the current component verifies
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
		if(hasNext() && verifyCurrentComponent())  //if there is a next step and the current component verifies
		{
			next();	//go the next step
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
	*/
	public void goFinish()
	{
		if(verifyCurrentComponent())	//if the current component verifies
		{
			finish();	//actually finish
		}
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

	/**Verifies the contents of the current component, if the current component
		can be verified.
	@return <code>true</code> if the current component was verified or is not
		verifiable, else <code>false</code> if the current component was verifiable
		but returned <code>false</code> when verified.
	@see Verifiable#verify
	*/
	protected boolean verifyCurrentComponent()
	{
		final Component currentComponent=getContentComponent();	//get the current content component
		if(currentComponent instanceof Verifiable)	//if we can verify the component's contents
		{
			if(!((Verifiable)currentComponent).verify())	//if the current component's contents do not verify
			{
				return false;	//show that the component verified incorrectly
			}
		}
		return true;	//show that the component didn't verify incorrectly
	}
	
	/**@return <code>true</code> if there is a next step after the current one.*/
	protected abstract boolean hasNext();

	/**@return <code>true</code> if there is a previous step before the current one.*/
	protected abstract boolean hasPrevious();

	/**Creates and displays a sequence dialog based upon <code>JOptionPane</code>,
		showing this sequence panel.
	<p>This method does not start the sequence. The calling method may start
		the sequence before calling this method by invoking <code>goStart()</code>.</p>
	@param parentComponent Determines the <code>Frame</code> in which the
		dialog is displayed; if <code>null</code>, or if the
		<code>parentComponent</code> has no <code>Frame</code>, a default
		<code>Frame</code> is used.
	@param title The title string for the dialog.
	@return One of the <code>JOptionPane</code> result constants.
	@see JOptionPane#showOptionDialog
	@see #goStart()
	*/
	public int showSequenceDialog(final Component parentComponent, final String title)
	{	
		final JButton[] buttons;
		final JButton initialButton;
		if(isDistinctAdvance())	//if we should have distinct advance, use separate actions
		{
			buttons=new JButton[]{getPreviousButton(), getNextButton(), getFinishButton()};
			initialButton=getNextButton();
		}
		else	//if we should not have distinct advance, use a dual-use action
		{
			buttons=new JButton[]{getPreviousButton(), getAdvanceButton()};
			initialButton=getAdvanceButton();
		} 
			//show an option pane in the parent component using the given title
		return OptionPane.showOptionDialog(parentComponent, this, title,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				buttons, initialButton);
	}

	/**Action for starting the progression.*/
	protected class StartAction extends AbstractAction
	{

		/**Default constructor.*/
		public StartAction()
		{
			super("Start");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Start sequence");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Start the sequence.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.ENTER_ICON_FILENAME)); //load the correct icon
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			goStart(); //try to finish the sequence
		}
	}

	/**Action for going to the previous component.*/
	protected class PreviousAction extends AbstractAction
	{
		/**Default constructor.*/
		public PreviousAction()
		{
			super("Previous");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Previous step");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Go to the previous step.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_P));  //set the mnemonic key G***i18n
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
	protected class NextAction extends AbstractAction
	{
		/**Default constructor.*/
		public NextAction()
		{
			super("Next");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Next step");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Go to the next step.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));  //set the mnemonic key G***i18n
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

	/**Action for finishing the progression.*/
	protected class FinishAction extends AbstractAction
	{

		/**Default constructor.*/
		public FinishAction()
		{
			super("Finish");	//create the base class G***i18n
			putValue(SHORT_DESCRIPTION, "Finish sequence");	//set the short description G***i18n
			putValue(LONG_DESCRIPTION, "Finish the sequence.");	//set the long description G***i18n
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));  //set the mnemonic key G***i18n
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

package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.util.*;

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
public abstract class AbstractSequencePanel extends ApplicationPanel
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
	
	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
	protected void updateStatus()
	{
		super.updateStatus(); //update the default actions
		getPreviousAction().setEnabled(hasPreviousComponent()); //only allow going backwards if we have a previous component
		getNextAction().setEnabled(hasNextComponent()); //only allow going backwards if we have a next component
		getFinishAction().setEnabled(!hasNextComponent()); //only allow finishing if there are no next components
		final JRootPane rootPane=getRootPane();	//get the ancestor root pane, if there is one
		if(rootPane!=null)	//if there is a root pane
		{
				//set the next button as the default unless we're finished; in that case, set the finish button as the default
			rootPane.setDefaultButton(hasNextComponent() ? getNextButton() : getFinishButton());
		}
	}
	
	
	/**Goes to the previous component in the sequence. If there is no previous
		component, no action occurs.
	*/
	public void goPrevious()
	{
		if(hasPreviousComponent())	//if there is a previous component
		{
			setContentComponent(getPreviousComponent());	//go to the previous component in the sequence
			updateStatus();	//update the status
		}
	}

	/**Goes to the next component in the sequence. If there is no next component,
		no action occurs.
	*/
	public void goNext()
	{
		if(hasNextComponent())  //if there is a next component
		{
			final Component currentComponent=getContentComponent();	//get the current content component
			if(currentComponent instanceof Verifiable)	//if we can verify the component's contents
			{
				if(!((Verifiable)currentComponent).verify())	//if the current component's contents do not verify
				{
					return;	//don't change to another step
				}
			}
			setContentComponent(getNextComponent());	//go to the next component in the sequence
			updateStatus();	//update the status
		}
	}
	
	/**Verifies the contents and finishes the sequence.
	Usually a derived class will not modify this method and instead override
		<code>finish()</code>, which this method calls if the contents of the
		current component verifies.
	@see Verifiable#verify
	*/
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
	protected abstract boolean hasNextComponent();

	/**@return The next component to be displayed in the sequence.*/
	protected abstract Component getNextComponent();

	/**@return <code>true</code> if there is a previous component before the current one.*/
	protected abstract boolean hasPreviousComponent();

	/**@return The previous component to be displayed in the sequence.*/
	protected abstract Component getPreviousComponent();

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

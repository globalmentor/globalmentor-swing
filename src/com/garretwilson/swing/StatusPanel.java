package com.garretwilson.swing;

import java.awt.*;
import javax.swing.*;
import com.garretwilson.awt.BasicGridBagLayout;

/**A panel showing status, such as text labels and progress bars.
@author Garret Wilson
*/
public class StatusPanel extends BasicPanel
{

	/**Insets to use for the major status components.*/
	protected final Insets STATUS_INSETS=new Insets(2, 2, 2, 2);

	/**The zero-based relative size of the progress bar font.*/
	protected final int PROGRESS_BAR_RELATIVE_FONT_SIZE=-1;

	/**The progress bar.*/
	private final JProgressBar progressBar;

		/**@return The progress bar.*/
		protected JProgressBar getProgressBar() {return progressBar;}

	/**The panel on which custom status components can be placed.*/
	private final BasicPanel statusComponentPanel;

		/**@return The panel on which custom status components can be placed.*/
		protected BasicPanel getStatusComponentPanel() {return statusComponentPanel;}

	/**Default constructor.*/
	public StatusPanel()
	{
		this(true);	//construct a panel and initialize it	
	}

	/**Constructor with optional initialization.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	public StatusPanel(final boolean initialize)
	{
		super(new BasicGridBagLayout(), false);	//construct the parent panel without initializing it
		statusComponentPanel=new BasicPanel(new BasicGridBagLayout());	//create the panel on which custom status components can be placed
		progressBar=new JProgressBar();	//create the progress bar
		if(initialize)  //if we should initialize
			initialize();   //initialize the panel
	}

	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		setBorder(BorderFactory.createEtchedBorder());	//set the status border
//G***fix		statusStatusLabel.setFont(statusBar.getFont().deriveFont((float)statusBar.getFont().getSize()-1));	//G***testing
//G**fix		statusBar.add(statusStatusLabel, new GridBagConstraints(0, 0, 1, 1, 0.5, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));	//add the status label to the status bar
				//TODO i18n add these status components correctly west or east based upon component orientation, and make sure the orientation of the status component panel matches 
			//add the status component panel
		add(statusComponentPanel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, STATUS_INSETS, 0, 0));
		progressBar.setStringPainted(true);	//set the progresss bar to display text
			//change the font size of the progress bar
		progressBar.setFont(progressBar.getFont().deriveFont((float)progressBar.getFont().getSize()+PROGRESS_BAR_RELATIVE_FONT_SIZE));
		add(progressBar, new GridBagConstraints(1, 0, 1, 1, 0.5, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, STATUS_INSETS, 0, 0));
	}

	/**Adds a status component to the status panel.
	@param component The component to add to the status panel.
	*/
	public void addStatusComponent(final Component component)
	{
			//add the status component to the status component panel in the next position horizontally
		getStatusComponentPanel().add(component, ((BasicGridBagLayout)getStatusComponentPanel().getLayout()).createNextBoxConstraints(BasicGridBagLayout.X_AXIS));
	}

	/**Sets the range of values for the progress bar.
	@param minimum The minumum progress value.
	@param maximum The maximum progress value.
	*/
	public void setProgressRange(final int minimum, final int maximum)
	{
		getProgressBar().setMinimum(minimum);	//set the progress bar minimum
		getProgressBar().setMaximum(maximum);	//set the progress bar maximum
	}

	/**Sets the text to be displayed on the progress bar.
	@param text The text to be displayed on the progress bar, or <code>null</code>
		if no text should be displayed.
	*/
	public void setProgress(final String text)
	{
		getProgressBar().setString(text);	//set the text of the progress bar
	}

	/**Sets the text to be displayed on the progress bar.
	@param text The text to be displayed on the progress bar, or <code>null</code>
		if no text should be displayed.
	@param value The amount of progress.
	*/
	public void setProgress(final String text, final int value)
	{
		setProgress(text);	//sets the text of the progress
		getProgressBar().setValue(value);	//set the text of the progress bar
	}

	/**@return A new custom label appropriate for placing on the status panel.*/
	public static JLabel createStatusLabel()
	{
		final JLabel label=new JLabel();	//create a label
		label.setBorder(BorderFactory.createLoweredBevelBorder());	//set the label border
		return label;	//return the label we created
	}

}

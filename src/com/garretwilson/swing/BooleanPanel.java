package com.garretwilson.swing;

import javax.swing.*;
import com.garretwilson.awt.BasicGridBagLayout;

/**A panel that allows selection of a boolean choice, with radio buttons
	representing true and false.
@author Garret Wilson
@see com.garretwilson.swing.rdf.maqro.QuestionPanel
*/
public class BooleanPanel extends ModifiablePanel implements SwingConstants
{

	private final ButtonGroup buttonGroup;
	private final JRadioButton trueRadioButton;
	private final JRadioButton falseRadioButton;

	/**The layout of the radio buttons, either <code>SwingConstants.HORIZONTAL</code>
		or <code>SwingConstants.VERTICAL</code>.
	*/
	private final int orientation;

		/**@return The layout of the radio buttons, either <code>HORIZONTAL</code>
			or <code>VERTICAL</code>.
		*/
		public int getOrientation() {return orientation;}

	/**@return <code>Boolean.TRUE</code> or <code>Boolean.TRUE</code>, depending
		on the selected radio button, or <code>null</code> if neither is selected.
	*/
	public Boolean getValue()
	{
		final ButtonModel selectedButtonModel=buttonGroup.getSelection();	//get the selected button model
		if(selectedButtonModel==trueRadioButton.getModel())
			return Boolean.TRUE;
		else if(selectedButtonModel==falseRadioButton.getModel())
			return Boolean.FALSE;
		else	//if neither button is selected
			return null;	//show that there is no selection
	}

	/**Sets the selected value.
	@param value <code>Boolean.TRUE</code> or <code>Boolean.TRUE</code>.
	*/
	public void setValue(final Boolean value)
	{
		trueRadioButton.setSelected(value.booleanValue()==true);
		falseRadioButton.setSelected(value.booleanValue()==false);
	}

	/**Default constructor with vertical orientation.*/
	public BooleanPanel()
	{
		this(HORIZONTAL);	//construct a horizontal boolean panel
	}

	/**Orientation constructor.
	@param orientation The layout of the radio buttons, either
		<code>HORIZONTAL</code> or <code>VERTICAL</code>.
	@see SwingConstants#HORIZONTAL
	@see SwingConstants#VERTICAL
	*/
	public BooleanPanel(final int orientation)
	{
		super(new BasicGridBagLayout(), false);	//construct the parent class but don't initialize it
		this.orientation=orientation;	//save the orientation
		buttonGroup=new ButtonGroup();
		trueRadioButton=new JRadioButton();
		falseRadioButton=new JRadioButton();
		initialize();   //initialize the panel
		setDefaultFocusComponent(trueRadioButton);
	}

	/**Initialize the user interface.*/
	protected void initializeUI()
	{
		super.initializeUI(); //do the default UI initialization
		buttonGroup.add(trueRadioButton);
		buttonGroup.add(falseRadioButton);
		trueRadioButton.setText("True");	//G***i18n
		falseRadioButton.setText("False");	//G***i18n
		final int axis;	//determine the axis to use
		switch(getOrientation())	//see which orientation we're using
		{
			case HORIZONTAL:
				axis=BasicGridBagLayout.X_AXIS;
				break;
			case VERTICAL:
				axis=BasicGridBagLayout.Y_AXIS;
				break;
			default:
				throw new AssertionError("Unrecognized orientation: "+getOrientation());
		}
		trueRadioButton.addItemListener(getModifyItemListener());
		falseRadioButton.addItemListener(getModifyItemListener());
		add(trueRadioButton, ((BasicGridBagLayout)getLayout()).createNextBoxConstraints(axis));	//add the true button
		add(falseRadioButton, ((BasicGridBagLayout)getLayout()).createNextBoxConstraints(axis));	//add the false button
	}

	/**Updates the states of the actions, including enabled/disabled status,
		proxied actions, etc.
	*/
	public void updateStatus()
	{
		super.updateStatus();	//do the default updating
		final boolean isEnabled=isEnabled();	//see if we're enabled
		trueRadioButton.setEnabled(isEnabled);	//enable or disable the true radio button
		falseRadioButton.setEnabled(isEnabled);	//enable or disable the false radio button
	}

}

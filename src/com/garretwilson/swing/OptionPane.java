package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import com.garretwilson.awt.*;
import com.garretwilson.util.*;

/**An option pane that knows how to verify its contents before closing the
	dialog.
<p>This class offers several improvements over <code>JOptionPane</code>:</p>
<ul>
	<li>If the message object implements <code>InitialFocusable</code>, the
		initial focus component is given the focus when the pane is displayed.</li>  
	<li>If the message object implements <code>Verifiable</code>, the input
		is verified before the pane is allowed to close.</li>
</ul>
@author Garret Wilson
@see InitialFocusable
@see Verifiable
*/
public class OptionPane extends JOptionPane
{

    /**
     * Creates a <code>OptionPane</code> with a test message.
     */
    public OptionPane() {
        super();
    }

    /**
     * Creates a instance of <code>OptionPane</code> to display a
     * message using the
     * plain-message message type and the default options delivered by
     * the UI.
     *
     * @param message the <code>Object</code> to display
     */
    public OptionPane(Object message) {
        super(message);
    }

    /**
     * Creates an instance of <code>OptionPane</code> to display a message
     * with the specified message type and the default options,
     *
     * @param message the <code>Object</code> to display
     * @param messageType the type of message to be displayed:
     *                    ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
     *                    QUESTION_MESSAGE, or PLAIN_MESSAGE
     */
    public OptionPane(Object message, int messageType) {
        super(message, messageType);
    }

    /**
     * Creates an instance of <code>OptionPane</code> to display a message
     * with the specified message type and options.
     *
     * @param message the <code>Object</code> to display
     * @param messageType the type of message to be displayed:
     *                    ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
     *                    QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @param optionType the options to display in the pane:
     *                   DEFAULT_OPTION, YES_NO_OPTION, YES_NO_CANCEL_OPTION
     *                   OK_CANCEL_OPTION
     */
    public OptionPane(Object message, int messageType, int optionType) {
        super(message, messageType, optionType);
    }

    /**
     * Creates an instance of <code>OptionPane</code> to display a message
     * with the specified message type, options, and icon.
     *
     * @param message the <code>Object</code> to display
     * @param messageType the type of message to be displayed:
     *                    ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
     *                    QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @param optionType the options to display in the pane:
     *                   DEFAULT_OPTION, YES_NO_OPTION, YES_NO_CANCEL_OPTION
     *                   OK_CANCEL_OPTION
     * @param icon the <code>Icon</code> image to display
     */
    public OptionPane(Object message, int messageType, int optionType,
                       Icon icon) {
        super(message, messageType, optionType, icon);
    }

    /**
     * Creates an instance of <code>OptionPane</code> to display a message
     * with the specified message type, icon, and options.
     * None of the options is initially selected.
     * <p>
     * The options objects should contain either instances of
     * <code>Component</code>s, (which are added directly) or
     * <code>Strings</code> (which are wrapped in a <code>JButton</code>).
     * If you provide <code>Component</code>s, you must ensure that when the
     * <code>Component</code> is clicked it messages <code>setValue</code>
     * in the created <code>OptionPane</code>.
     *
     * @param message the <code>Object</code> to display
     * @param messageType the type of message to be displayed:
     *                    ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
     *                    QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @param optionType the options to display in the pane:
     *                   DEFAULT_OPTION, YES_NO_OPTION, YES_NO_CANCEL_OPTION
     *                   OK_CANCEL_OPTION; only meaningful if the
     *                   <code>options</code> parameter is <code>null</code>
     * @param icon the <code>Icon</code> image to display
     * @param options  the choices the user can select
     */
    public OptionPane(Object message, int messageType, int optionType,
                       Icon icon, Object[] options) {
        super(message, messageType, optionType, icon, options);
    }

    /**
     * Creates an instance of <code>OptionPane</code> to display a message
     * with the specified message type, icon, and options, with the
     * initially-selected option specified.
     *
     * @param message the <code>Object</code> to display
     * @param messageType the type of message to be displayed:
     *                    ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
     *                    QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @param optionType the options to display in the pane:
     *                   DEFAULT_OPTION, YES_NO_OPTION, YES_NO_CANCEL_OPTION
     *                   OK_CANCEL_OPTION; only meaningful if the
     *                   <code>options</code> parameter is <code>null</code>
     * @param icon the Icon image to display
     * @param options  the choices the user can select
     * @param initialValue the choice that is initially selected
     */
    public OptionPane(Object message, int messageType, int optionType,
                       Icon icon, Object[] options, Object initialValue) {
				super(message, messageType, optionType, icon, options, initialValue);
    }

//G***add other methods here


    /**
     * Brings up a modal dialog with the options Yes, No and Cancel; with the
     * title, "Select an Option".
     *
     * @param parentComponent determines the <code>Frame</code> in which the
     *			dialog is displayed; if <code>null</code>,
     *			or if the <code>parentComponent</code> has no
     *			<code>Frame</code>, a
     *                  default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @return an integer indicating the option selected by the user
     */
    public static int showConfirmDialog(Component parentComponent, Object message) {
        return showConfirmDialog(parentComponent, message,
            UIManager.getString("OptionPane.titleText"), YES_NO_CANCEL_OPTION);
    }

    /**
     * Brings up a modal dialog where the number of choices is determined
     * by the <code>optionType</code> parameter.
     *
     * @param parentComponent determines the <code>Frame</code> in which the
     *			dialog is displayed; if <code>null</code>,
     *			or if the <code>parentComponent</code> has no
     *			<code>Frame</code>, a
     *                  default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @param title     the title string for the dialog
     * @param optionType an int designating the options available on the dialog:
     *                   YES_NO_OPTION, or YES_NO_CANCEL_OPTION
     * @return an int indicating the option selected by the user
     */
    public static int showConfirmDialog(Component parentComponent, Object message,
                                        String title, int optionType) {

//G***del Debug.trace("show confirm dialog 1"); //G***del
        return showConfirmDialog(parentComponent, message, title, optionType,
                                 QUESTION_MESSAGE);
    }

    /**
     * Brings up a modal dialog where the number of choices is determined
     * by the <code>optionType</code> parameter, where the
     * <code>messageType</code>
     * parameter determines the icon to display.
     * The <code>messageType</code> parameter is primarily used to supply
     * a default icon from the Look and Feel.
     *
     * @param parentComponent determines the <code>Frame</code> in
     *			which the dialog is displayed; if <code>null</code>,
     *			or if the <code>parentComponent</code> has no
     *			<code>Frame</code>, a
     *                  default <code>Frame</code> is used.
     * @param message   the <code>Object</code> to display
     * @param title     the title string for the dialog
     * @param optionType an integer designating the options available
     *			on the dialog:
     *                  YES_NO_OPTION, or YES_NO_CANCEL_OPTION
     * @param messageType an integer designating the kind of message this is,
     *                  primarily used to determine the icon from the
     *			pluggable
     *                  Look and Feel: ERROR_MESSAGE, INFORMATION_MESSAGE,
     *                  WARNING_MESSAGE, QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @return an integer indicating the option selected by the user
     */
    public static int showConfirmDialog(Component parentComponent, Object message,
                                        String title, int optionType,
                                        int messageType) {
//G***del Debug.trace("show confirm dialog 2"); //G***del
        return showConfirmDialog(parentComponent, message, title, optionType,
                                messageType, null);
    }

    /**
     * Brings up a modal dialog with a specified icon, where the number of
     * choices is determined by the <code>optionType</code> parameter.
     * The <code>messageType</code> parameter is primarily used to supply
     * a default icon from the Look and Feel.
     *
     * @param parentComponent determines the <code>Frame</code> in which the
     *			dialog is displayed; if <code>null</code>,
     *			or if the <code>parentComponent</code> has no
     *			<code>Frame</code>, a
     *			default <code>Frame</code> is used
     * @param message   The Object to display
     * @param title     the title string for the dialog
     * @param optionType an int designating the options available on the dialog:
     *                   YES_NO_OPTION, or YES_NO_CANCEL_OPTION
     * @param messageType an int designating the kind of message this is,
     *                    primarily used to determine the icon from the pluggable
     *                    Look and Feel: ERROR_MESSAGE, INFORMATION_MESSAGE,
     *                    WARNING_MESSAGE, QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @param icon      the icon to display in the dialog
     * @return an int indicating the option selected by the user
     */
    public static int showConfirmDialog(Component parentComponent, Object message,
                                        String title, int optionType,
                                        int messageType, Icon icon) {
//G***del Debug.trace("show confirm dialog 3"); //G***del
        return showOptionDialog(parentComponent, message, title, optionType,
                                messageType, icon, null, null);
    }


    /**
     * Brings up a modal dialog with a specified icon, where the initial
     * choice is dermined by the <code>initialValue</code> parameter and
     * the number of choices is determined by the <code>optionType</code>
     * parameter.
     * <p>
     * If <code>optionType</code> is YES_NO_OPTION, or YES_NO_CANCEL_OPTION
     * and the <code>options</code> parameter is <code>null</code>,
     * then the options are
     * supplied by the Look and Feel.
     * <p>
     * The <code>messageType</code> parameter is primarily used to supply
     * a default icon from the Look and Feel.
     *
     * @param parentComponent determines the <code>Frame</code>
     *			in which the dialog is displayed;  if
     *                  <code>null</code>, or if the
     *			<code>parentComponent</code> has no
     *			<code>Frame</code>, a
     *                  default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @param title     the title string for the dialog
     * @param optionType an integer designating the options available on the
     *			dialog: YES_NO_OPTION, or YES_NO_CANCEL_OPTION
     * @param messageType an integer designating the kind of message this is,
     *                  primarily used to determine the icon from the
     *			pluggable
     *                  Look and Feel: ERROR_MESSAGE, INFORMATION_MESSAGE,
     *                  WARNING_MESSAGE, QUESTION_MESSAGE, or PLAIN_MESSAGE
     * @param icon      the icon to display in the dialog
     * @param options   an array of objects indicating the possible choices
     *                  the user can make; if the objects are components, they
     *                  are rendered properly; non-<code>String</code>
     *			objects are
     *                  rendered using their <code>toString</code> methods;
     *                  if this parameter is <code>null</code>,
     *			the options are determined by the Look and Feel.
     * @param initialValue the object that represents the default selection
     *                     for the dialog
     * @return an integer indicating the option chosen by the user,
     *         or CLOSED_OPTION if the user closed the Dialog
     */
    public static int showOptionDialog(Component parentComponent, Object message,
                                       String title, int optionType,
                                       int messageType, Icon icon,
                                       Object[] options, Object initialValue) {
//G***del  Debug.trace("inside custom showOptionDialog()"); //G***del
        JOptionPane             pane = new OptionPane(message, messageType,
                                                       optionType, icon,
                                                       options, initialValue);

        pane.setInitialValue(initialValue);

        JDialog         dialog = pane.createDialog(parentComponent, title);

        pane.selectInitialValue();

        dialog.show();

        Object        selectedValue = pane.getValue();

        if(selectedValue == null)
            return CLOSED_OPTION;
        if(options == null) {
            if(selectedValue instanceof Integer)
                return ((Integer)selectedValue).intValue();
            return CLOSED_OPTION;
        }
        for(int counter = 0, maxCounter = options.length;
            counter < maxCounter; counter++) {
            if(options[counter].equals(selectedValue))
                return counter;
        }
        return CLOSED_OPTION;
    }

    /**
     * Creates and returns a new <code>JDialog</code> wrapping
     * <code>this</code> centered on the <code>parentComponent</code>
     * in the <code>parentComponent</code>'s frame.
     * <code>title</code> is the title of the returned dialog.
     * The returned <code>JDialog</code> will be set up such that
     * once it is closed, or the user clicks on the OK button,
     * the dialog will be disposed and closed.
		  <p>This version first verifies the content pane, if it is
			<code>Verifiable</code>, and only closes the dialog if the object
			verifies correctly or is not <code>Verifiable</code>.</p>
     * @param parentComponent determines the frame in which the dialog
     *		is displayed; if the <code>parentComponent</code> has
     *		no <code>Frame</code>, a default <code>Frame</code> is used
     * @param title     the title string for the dialog
     * @return a new <code>JDialog</code> containing this instance
     */
    public JDialog createDialog(Component parentComponent, String title)
		{
//G***del			final JDialog dialog=super.createDialog(parentComponent, title);  //create the default dialog

        final JDialog dialog;

        Window window = getWindowForComponent(parentComponent);
        if (window instanceof Frame) {
            dialog = new JDialog((Frame)window, title, true);
        } else {
            dialog = new JDialog((Dialog)window, title, true);
        }

        final Container             contentPane = dialog.getContentPane();
					//see if the message is verifiable
				final Verifiable verifiableMessage=message instanceof Verifiable ? (Verifiable)message : null;  //G***newswing

        contentPane.setLayout(new BorderLayout());
        contentPane.add(this, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(parentComponent);
				dialog.setDefaultCloseOperation(dialog.DO_NOTHING_ON_CLOSE);  //don't automatically close the dialog G***newswing
        dialog.addWindowListener(new WindowAdapter() {
            boolean gotFocus = false;
            public void windowClosing(WindowEvent we) {
                setValue(null);
            }
            public void windowActivated(WindowEvent we) {
                // Once window gets focus, set initial focus
                if (!gotFocus) {
                    selectInitialValue();
                    gotFocus = true;
                }
            }
        });
        addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if(dialog.isVisible() && event.getSource() == OptionPane.this &&
                   (event.getPropertyName().equals(VALUE_PROPERTY) ||
                    event.getPropertyName().equals(INPUT_VALUE_PROPERTY)))
								{
										//if the user is pressing "OK", try to verify the message
									if(event.getNewValue() instanceof Integer
										&& ((Integer)event.getNewValue()).intValue()==OK_OPTION)  //if the user is pressing "OK"
									{
	//G***del									if(OptionPane.this.getValue().equals(new )
										if(verifiableMessage!=null) //if the message is verifiable G***newswing
										{
											if(!verifiableMessage.verify()) //verify the content pane; if it doesn't verify
											{
												value=null; //change the value back to nothing so that pressing the button again will bring us back here (but don't call setValue(null), because that will close the dialog)
												return; //cancel the closing process
											}
										}
									}
                  dialog.setVisible(false);
                  dialog.dispose();
                }
            }
        });
        return dialog;
    }
    
	/**Requests that the initial value be selected, which will set
		focus to the initial value.
	<p>This version first checks to see if the message implements
		<code>InitialFocusable</code>, and if so the message is asked which
		component should get the focus. Otherwise, the default initial value
		is selected.</p>
	@see #getMessage
	@see InitialFocusable	
	*/
	public void selectInitialValue()
	{
		final Object message=getMessage();	//get the message object used in the pane 
		if(message instanceof InitialFocusable)	//if the message object knows which component should first get the focus 
			((InitialFocusable)message).getInitialFocusComponent().requestFocus();	//request focus for the initial component
		else	//if the message object doesn't know what to focus
			super.selectInitialValue();	//do the default initial value selection
	}
    

    /**
     * Returns the specified component's toplevel <code>Frame</code> or
     * <code>Dialog</code>.
     *
     * @param parentComponent the <code>Component</code> to check for a
     *		<code>Frame</code> or <code>Dialog</code>
     * @return the <code>Frame</code> or <code>Dialog</code> that
     *		contains the component, or the default
     *         	frame if the component is <code>null</code>,
     *		or does not have a valid
     *         	<code>Frame</code> or <code>Dialog</code> parent
     */
    static Window getWindowForComponent(Component parentComponent) {
        if (parentComponent == null)
            return getRootFrame();
        if (parentComponent instanceof Frame || parentComponent instanceof Dialog)
            return (Window)parentComponent;
        return getWindowForComponent(parentComponent.getParent());
    }


}
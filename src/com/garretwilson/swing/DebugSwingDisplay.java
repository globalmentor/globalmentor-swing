package com.garretwilson.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import com.garretwilson.lang.Strings;
import com.garretwilson.util.DebugDisplay;

/**Interface for displaying debug information using Swing.
@author Garret Wilson
@see com.garretwilson.awt.DebugAWTDisplay
*/
public class DebugSwingDisplay implements DebugDisplay
{

	/**The single copy of the debug frame class that's allowed to be created if needed.*/
	private JFrame debugFrame=null;

	/**The text area used to log visible output.*/
	private JTextArea debugTextArea;

	/**Whether this displayer is enabled.*/
//G***del if not needed	private boolean enabled=false;

	/**Default constructor.*/
	public DebugSwingDisplay()
	{
	}

	/**@return Whether this debug displayer is enabled.*/
	public boolean isEnabled()
	{
		return debugFrame!=null;	//if we have a debug frame, we're enabled
	}

	/**Sets whether displaying of debug information should be enabled. The first
		time this displayer is enabled (that is, <code>newEnabled</code> is set to
		<code>true</code>), the appropriate window will be created for displaying
		information. When <code>newEnabled</code> is set to <code>false</code>,
		that window is destroyed.
	@param newEnabled Whether this displayer will display information.
	*/
	public void setEnabled(final boolean newEnabled)
	{
		if(newEnabled)	//if they want to enable this displayer
		{
			if(!isEnabled())	//if we're not already enabled
			{
				final JFrame frame=new JFrame();	//create a new frame
	  		final JScrollPane scrollPane=new JScrollPane();	//create a new scroll pane
				final JTextArea textArea=new JTextArea();	//create a new text area
				frame.setSize(new Dimension(800, 600));	//give the frame a default size
		    textArea.setText("Debug output\n");	//G***put the current date here or something
		    textArea.setEditable(false);	//don't allow the text to be edited
		    frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		    scrollPane.getViewport().add(textArea, null);
				frame.validate();	//validate the frame, now that we've added all the components
				frame.setVisible(true);	//show the debug frame
				debugFrame=frame;	//set the debug frame
				debugTextArea=textArea;	//set the debug frame
			}
			else	//if they want to disable this displayer
			{
				if(isEnabled())	//if we're enabled now
				{
				  debugFrame.dispose();	//dispose the frame
					debugFrame=null;	//turn off visible debugging
					debugTextArea=null;	//remove our reference to the debug text area
				}
			}
		}
	}

	/**Outputs a debug trace message.
	Meant for messages that show the path of program execution.
	@param traceString The string to output.
	@see Debug#trace
	*/
	public void trace(final String traceString)
	{
		if(debugTextArea!=null)  //if we have a debug text area
		  debugTextArea.append(traceString+'\n');	//append this text to the debug text area, along with a line break
	}

	/**Displays a message dialog with the given message.
	@param message The message to display
	@see Debug#notify
	*/
	public void notify(final String message)
	{
		final String wrappedMessage=Strings.wrap(message, 100);	//wrap the error message at 100 characters G***probably use a constant here
			//G***maybe later use a specified frame as the parent
		JOptionPane.showMessageDialog(null, wrappedMessage, "Debug Message", JOptionPane.INFORMATION_MESSAGE);	//G***i18n; comment
	}

	/**Displays an error message.
	Meant for errors that are not expected to occur during normal program operations
		 -- program logic errors, and exceptions that are not expected to be thrown.
	@param errorString The error message.
	@see Debug#error
	*/
	public void error(final String errorString)
	{
		final String wrappedErrorString=Strings.wrap(errorString, 100);	//wrap the error message at 100 characters G***probably use a constant here
			//G***maybe later use a specified frame as the parent
			//G***maybe later allow the title to be changed
		JOptionPane.showMessageDialog(null, wrappedErrorString, "Error", JOptionPane.ERROR_MESSAGE);	//G***i18n; comment
	}

}
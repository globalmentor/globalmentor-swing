package com.garretwilson.swing;

import java.net.URI;
import javax.swing.*;
import com.garretwilson.util.*;

/**An application that relies on Swing.
@author Garret Wilson
*/
public class SwingApplication extends Application
{

	/**Reference URI constructor.
	@param referenceURI The reference URI of the application.
	*/
	public SwingApplication(final URI referenceURI)
	{
		super(referenceURI);	//construct the parent class
	}

	/**Displays the given error to the user
	@param errorString The error to display. 
	*/
	public void displayError(final String errorString)	//TODO add a title or something, and fix the ApplicationFrame error display
	{
		JOptionPane.showMessageDialog(null, errorString, "Error", JOptionPane.ERROR_MESSAGE);	//G***i18n
	}

	/**Starts an application.
	@param application The application to start. 
	@param args The command line arguments.
	@return The application status.
	*/
	public static int run(final Application application, final String[] args)
	{
		try
		{
			initialize(application, args);	//initialize the environment
			if(!application.canStart())	//perform the pre-run checks; if something went wrong, exit
				return -1;	//show that there was a problem
			return application.main();	//run the application
		}
		catch(Throwable throwable)  //if there are any errors
		{
			error(throwable);	//report the error
			return -1;	//show that there was an error
		}
	}

	/**Initializes the environment for the application.
	@param application The application to start. 
	@param args The command line arguments.
	@exception Thrown if anything goes wrong.
	*/
	protected static void initialize(final Application application, final String[] args) throws Exception
	{
		Application.initialize(application, args);	//initialize the default environment
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());	//set the Swing look and feel to the system default
	}

}

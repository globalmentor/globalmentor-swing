/*
 * Copyright © 1996-2009 GlobalMentor, Inc. <http://www.globalmentor.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.garretwilson.swing;

import java.awt.Component;
import java.awt.Frame;
import java.net.URI;
import javax.swing.*;
import com.globalmentor.java.*;
import com.globalmentor.log.Log;
import com.globalmentor.util.*;

/**An application that relies on Swing.
This version creates a default authenticator for client authentication using Swing.
@author Garret Wilson
*/
public abstract class SwingApplication extends Application
{

	/**Reference URI constructor.
	@param referenceURI The reference URI of the application.
	*/
	public SwingApplication(final URI referenceURI)
	{
		this(referenceURI, NO_ARGUMENTS);	//construct the class with no arguments
	}

	/**Reference URI and arguments constructor.
	@param referenceURI The reference URI of the application.
	@param args The command line arguments.
	*/
	public SwingApplication(final URI referenceURI, final String[] args)
	{
		super(referenceURI, args);	//construct the parent class
		setAuthenticator(new SwingAuthenticator());	//create a default authenticator for HTTP connections on the default HTTP client
	}

	/**Displays an error message to the user for a throwable object.
	@param throwable The condition that caused the error.
	*/
	public void displayError(final Throwable throwable)
	{
		displayError(null, throwable);	//display the error in the default frame with the default title	
	}

	/**Displays the given error to the user
	@param message The error to display. 
	*/
	public void displayError(final String message)
	{
		displayError(null, null, message);	//display the error in the default frame with the default title
	}

	/**Displays an error message to the user for a throwable object, using a
		default title.
	@param parentComponent The <code>Frame</code> in which the dialog is
		displayed, or <code>null</code> if a parent <code>Frame</code>, if any,
		of the <code>parentComponent</code> should be used.
	@param throwable The condition that caused the error.
	@see #displayError(Component, String, Throwable)
	*/
	public void displayError(final Component parentComponent, final Throwable throwable)
	{
		displayError(parentComponent, null, throwable);	//display the error with a default title
	}

	/**Displays an error message to the user for a throwable object.
	@param parentComponent The <code>Frame</code> in which the dialog is
		displayed, or <code>null</code> if a parent <code>Frame</code>, if any,
		of the <code>parentComponent</code> should be used.
	@param title The error message dialog title, or <code>null</code> for the
		default application throwable error title.
	@param throwable The condition that caused the error.
	@see #displayError(Component, String, Throwable)
	*/
	public void displayError(final Component parentComponent, final String title, final Throwable throwable)
	{
			//display the error using the default method, using the default error title if we weren't given one
		displayErrorDefault(parentComponent, title!=null ? title : getDefaultErrorTitle(throwable), throwable);
	}

	/**Displays an error message to the user using a default title.
	<p>This version uses the default error display method.</p>
	@param parentComponent The <code>Frame</code> in which the dialog is
		displayed, or <code>null</code> if a parent <code>Frame</code>, if any,
		of the <code>parentComponent</code> should be used.
	@param message The error to display.
	@see #displayErrorDefault(Component, String, String) 
	*/
	public void displayError(final Component parentComponent, final String message)
	{
		displayError(parentComponent, null, message);	//display the error with a default title
	}

	/**Displays an error message to the user.
	<p>This version uses the default error display method.</p>
	@param parentComponent The <code>Frame</code> in which the dialog is
		displayed, or <code>null</code> if a parent <code>Frame</code>, if any,
		of the <code>parentComponent</code> should be used.
	@param title The error message dialog title, or <code>null</code> for the
		default application error title.
	@param message The error to display.
	@see #displayErrorDefault(Component, String, String) 
	*/
	public void displayError(final Component parentComponent, final String title, final String message)
	{
			//display the error using the default method, using the default error title if we weren't given one
		displayErrorDefault(parentComponent, title!=null ? title : getDefaultErrorTitle(), message);
	}

	/**Displays an error message to the user for a throwable object, using a
		default title.
	@param parentComponent The <code>Frame</code> in which the dialog is
		displayed, or <code>null</code> if a parent <code>Frame</code>, if any,
		of the <code>parentComponent</code> should be used.
	@param throwable The condition that caused the error.
	*/
	public static void displayApplicationError(final Component parentComponent, final Throwable throwable)
	{
		displayApplicationError(parentComponent, null, throwable);	//display the error with a default title
	}

	/**Displays an error message to the user for a throwable object.
	@param parentComponent The <code>Frame</code> in which the dialog is
		displayed, or <code>null</code> if a parent <code>Frame</code>, if any,
		of the <code>parentComponent</code> should be used.
	@param title The error message dialog title, or <code>null</code> for the
		default application error title.
	@param throwable The condition that caused the error.
	*/
	public static void displayApplicationError(final Component parentComponent, final String title, final Throwable throwable)
	{
		final SwingApplication application=getSwingApplication(parentComponent);	//see if we can find an application object from the component
		if(application!=null)	//if we found an application
		{
			application.displayError(parentComponent, title, throwable);	//let the application display the error
		}
		else	//if we found no application
		{
			displayErrorDefault(parentComponent, title, throwable);	//display the error using the default method
		}		
	}

	/**Displays an error message to the user, using the Swing application that
		owns the given parent component, if possible, with a default title.
	@param parentComponent The <code>Frame</code> in which the dialog is
		displayed, or <code>null</code> if a parent <code>Frame</code>, if any,
		of the <code>parentComponent</code> should be used.
	@param message The error to display.
	@see #getSwingApplication(Component) 
	*/
	public static void displayApplicationError(final Component parentComponent, final String message)
	{
		displayApplicationError(parentComponent, null, message);	//display the error with a default title
	}

	/**Displays an error message to the user, using the Swing application that
		owns the given parent component, if possible.
	@param parentComponent The <code>Frame</code> in which the dialog is
		displayed, or <code>null</code> if a parent <code>Frame</code>, if any,
		of the <code>parentComponent</code> should be used.
	@param title The error message dialog title, or <code>null</code> for the
		default application error title.
	@param message The error to display.
	@see #getSwingApplication(Component) 
	*/
	public static void displayApplicationError(final Component parentComponent, final String title, final String message)
	{
		final SwingApplication application=getSwingApplication(parentComponent);	//see if we can find an application object from the component
		if(application!=null)	//if we found an application
		{
			application.displayError(parentComponent, title, message);	//let the application display the error
		}
		else	//if we found no application
		{
			displayErrorDefault(parentComponent, title, message);	//display the error using the default method
		}
	}

	/**Default method to displays an error message to the user for a throwable object.
	@param parentComponent The <code>Frame</code> in which the dialog is
		displayed, or <code>null</code> if a parent <code>Frame</code>, if any,
		of the <code>parentComponent</code> should be used.
	@param title The error message dialog title, or <code>null</code> for the
		default throwable error title.
	@param throwable The condition that caused the error.
	*/
	public static void displayErrorDefault(final Component parentComponent, final String title, final Throwable throwable)
	{
		Log.error(throwable);	//log the error
			//display a message for the throwable, using the throwable local class name for the title if no title was given
		displayErrorDefault(parentComponent, title!=null ? title : Classes.getLocalName(throwable.getClass()), getDisplayErrorMessage(throwable));
	}

	/**Default method to display an error message to the user.
	@param parentComponent The <code>Frame</code> in which the dialog is
		displayed, or <code>null</code> if a parent <code>Frame</code>, if any,
		of the <code>parentComponent</code> should be used.
	@param title The error message dialog title, or <code>null</code> for no title.
	@param message The error to display. 
	*/
	protected static void displayErrorDefault(final Component parentComponent, final String title, final String message)
	{
		System.err.println(message);	//display the error in the error output
		final String wrappedMessage=Strings.wrap(message, 100);	//wrap the error message at 100 characters TODO probably use a constant here
			//show the error in a dialog box, using the default error title if we weren't given one
		BasicOptionPane.showMessageDialog(parentComponent, wrappedMessage, title, BasicOptionPane.ERROR_MESSAGE);	
	}

	/**Attempts to determine the <code>SwingApplication</code> for the given
		component. If the component has a parent <code>Frame</code> that is an
		<code>ApplicationFrame</code> that frame's application is returned if
		it has one.
	@param component The component that may have a parent
		<code>ApplicationFrame</code>.
	@return The Swing application object that owns this component, or
		<code>null</code> if a Swing application object could not be located.
	@see JOptionPane#getFrameForComponent(Component)
	@see ApplicationFrame
	*/
	public static SwingApplication getSwingApplication(final Component component)
	{
		final Frame frame=JOptionPane.getFrameForComponent(component);	//see if this component is in a frame
		if(frame instanceof ApplicationFrame)	//if the frame is an application frame
		{
			return ((ApplicationFrame)frame).getApplication();	//return the application, if any, of the application frame
		}
		return null;	//show that we could not locate a Swing application
	}


	/**@return The default title for error messages.*/
	protected String getDefaultErrorTitle()
	{
		return getDefaultErrorTitle(null);	//get a default error title with no cause specified
	}

	/**Determines a default title for error messages.
	@param throwable The condition that caused the error, or <code>null</code> if
		the cause is unknown.
	@return The default title for error messages.
	*/
	protected String getDefaultErrorTitle(final Throwable throwable)
	{
		final String title=getTitle();	//get the application title
		final StringBuilder stringBuilder=new StringBuilder();	//construct the default title
		if(title!=null)	//if we have a title
		{
			stringBuilder.append(title);	//append the string version of the title
		}
		if(throwable!=null)	//if we were given a throwable
		{
			if(title!=null)	//if there was a title object
			{
				stringBuilder.append(' ');	//separate the title from the throwable class name
			}
			stringBuilder.append(Classes.getLocalName(throwable.getClass()));	//append the throwable local class name
		}
		return stringBuilder.toString();	//return the default string we constructed
	}

	/**Starts an application.
	This method should never return, as it directly calls <code>exit()</code>.
	@param application The application to start. 
	@param args The command line arguments.
	@return The application status.
	*/
	public static int run(final Application application, final String[] args)
	{
		int result=0;	//start out assuming a neutral result TODO use a constant and a unique value
		try
		{
			initialize(application, args);	//initialize the environment
			application.initialize();	//initialize the application
			if(application.canStart())	//perform the pre-run checks; if everything went OK
			{
				result=application.main();	//run the application
			}
			else	//if something went wrong
			{
				result=-1;	//show that we couldn't start TODO use a constant and a unique value
			}
		}
		catch(final Throwable throwable)  //if there are any errors
		{
			result=-1;	//show that there was an error TODO use a constant and a unique value
			application.displayError(throwable);	//report the error
		}
		if(result<0)	//if we something went wrong, exit (if everything is going fine, keep running, because we may have a server or frame running)
		{
			try
			{
				application.exit(result);	//exit with the result (we can't just return, because the main frame, if initialized, will probably keep the thread from stopping)
			}
			catch(final Throwable throwable)  //if there are any errors
			{
				result=-1;	//show that there was an error during exit TODO use a constant and a unique value
				application.displayError(throwable);	//report the error
			}
			finally
			{
				System.exit(result);	//provide a fail-safe way to exit		
			}
		}
		return result;	//always return the result		
	}

	/**Initializes the environment for the application.
	@param application The application to start. 
	@param args The command line arguments.
	@exception Exception Thrown if anything goes wrong.
	*/
	protected static void initialize(final Application application, final String[] args) throws Exception
	{
		Application.initialize(application, args);	//initialize the default environment
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());	//set the Swing look and feel to the system default
	}

}

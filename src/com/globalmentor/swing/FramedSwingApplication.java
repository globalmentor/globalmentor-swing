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

package com.globalmentor.swing;

import java.awt.Component;
import java.awt.Frame;

import java.net.URI;

/**A Swing application uses one main application frame.
@author Garret Wilson
*/
public abstract class FramedSwingApplication extends SwingApplication
{

	/**The main application frame, or <code>null</code> if the application frame
		has not yet been created.
	*/
	private Frame applicationFrame;

		/**@return The main application frame, or <code>null</code> if the
			application frame has not yet been created.
		*/
		public Frame getApplicationFrame() {return applicationFrame;}

		/**Sets the main application frame.
		@param frame The main frame for the application.
		*/
		protected void setApplicationFrame(final Frame frame) {applicationFrame=frame;}
	
	/**Reference URI constructor.
	@param referenceURI The reference URI of the application.
	*/
	public FramedSwingApplication(final URI referenceURI)
	{
		this(referenceURI, NO_ARGUMENTS);	//construct the class with no arguments
	}

	/**Reference URI and arguments constructor.
	@param referenceURI The reference URI of the application.
	@param args The command line arguments.
	*/
	public FramedSwingApplication(final URI referenceURI, final String[] args)
	{
		super(referenceURI, args);	//construct the parent class
		applicationFrame=null;	//show that we do not yet have an application frame
	}

	/**The main application method.
	This version creates the main application frame and displays it.
	@return The application status.
	@see #createApplicationFrame()
	*/ 
	public int main()
	{
		final Frame applicationFrame=createApplicationFrame();	//create the application frame
		setApplicationFrame(applicationFrame);	//set the application frame
		applicationFrame.setVisible(true);	//show the frame
		return super.main();	//perform the default functionality
	}

	/**Creates the main frame for the application.
	@return The main application frame
	*/ 
	protected abstract Frame createApplicationFrame();

	/**Displays an error message to the user for a throwable object.
	This version uses the application frame if no parent component is given.
	@param parentComponent The <code>Frame</code> in which the dialog is
		displayed, or <code>null</code> if a parent <code>Frame</code>, if any,
		of the <code>parentComponent</code> should be used.
	@param title The error message dialog title, or <code>null</code> for the
		default application throwable error title.
	@param throwable The condition that caused the error.
	@see #getApplicationFrame()
	*/
	public void displayError(final Component parentComponent, final String title, final Throwable throwable)
	{
			//use the application frame if a parent component was not given
		super.displayError(parentComponent!=null ? parentComponent : getApplicationFrame(), title, throwable);
	}

	/**Displays an error message to the user.
	<p>This version uses the default error display method.</p>
	@param parentComponent The <code>Frame</code> in which the dialog is
		displayed, or <code>null</code> if a parent <code>Frame</code>, if any,
		of the <code>parentComponent</code> should be used.
	@param title The error message dialog title, or <code>null</code> for the
		default application error title.
	@param message The error to display.
	@see #getApplicationFrame()
	*/
	public void displayError(final Component parentComponent, final String title, final String message)
	{
			//use the application frame if a parent component was not given
		super.displayError(parentComponent!=null ? parentComponent : getApplicationFrame(), title, message);
	}

}
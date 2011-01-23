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

import java.io.*;
import java.net.URI;

import static com.globalmentor.java.OperatingSystem.*;

import com.globalmentor.io.*;
import com.globalmentor.java.*;
import com.globalmentor.log.Log;
import com.globalmentor.model.Modifiable;

/**A framed Swing application that maintains configuration information.
<p>If a configuration is provided via <code>setConfiguration()</code>, that
	configuration is automatically loaded and saved.</p>
@param <C> The type of configuration object.
@author Garret Wilson
*/
public abstract class AbstractConfiguredFramedSwingApplication<C> extends AbstractFramedSwingApplication implements Modifiable
{

	/**The filename of the configuration file.*/
	public final static String CONFIGURATION_FILENAME="configuration.rdf";

	/**Whether the object has been modified; the default is not modified.*/
	private boolean modified=false;

	/**The name of the configuration directory, or <code>null</code> if the default should be used.*/
	private String configurationDirectoryName=null;

		/**@return The name of the configuration directory. If no configuration
		 * directory has been assigned, a default is returned constructed from the
		 * local name of the application class in lowercase, preceded by a full
		 * stop character ('.').*/
		protected String getConfigurationDirectoryName()
		{
			return configurationDirectoryName!=null	//if a configuration directory name has been assigned
					? configurationDirectoryName	//return the configuration directory name
					: String.valueOf(Files.FILENAME_EXTENSION_SEPARATOR)+Classes.getLocalName(getClass()).toLowerCase();	//otherwise, return ".applicationname"
		}
		
		/**Sets the name of the configuration directory.
		@param configurationDirectoryName The name of the configuration directory,
			or <code>null</code> if the default should be used.
		*/
		protected void setConfigurationDirectoryName(final String configurationDirectoryName) {this.configurationDirectoryName=configurationDirectoryName;}

	/**@return The configuration directory for the application.
	@exception SecurityException if a security manager exists and its <code>checkPropertyAccess</code> method doesn't allow
		access to the user home system property.
	@see OperatingSystem#getUserHomeDirectory()
	@see #getConfigurationDirectoryName()
	*/
	public File getConfigurationDirectory() throws SecurityException
	{
		return new File(getUserHomeDirectory(), getConfigurationDirectoryName());	//return the configuration directory inside the user home directory
	}

	/**@return An object representing the file containing the configuration information.
	@exception SecurityException if a security manager exists and its <code>checkPropertyAccess</code> method doesn't allow
		access to the user home system property.
	@see #getConfigurationDirectory()
	*/
	public File getConfigurationFile() throws SecurityException
	{
		return new File(getConfigurationDirectory(), CONFIGURATION_FILENAME);	//return the configuration file inside the configuration directory		
	}
	
	/**The application configuration, or <code>null</code> if there is no configuration.*/
	private C configuration=null;

		/**@return The application configuration, or <code>null</code> if there is no configuration.*/
		public C getConfiguration() {return configuration;}

		/**Sets the application configuration.
		@param config The application configuration, or <code>null</code> if there
			should be no configuration.
		*/
		private void setConfiguration(final C config) {configuration=config;}

	/**The configuration storage I/O kit, or <code>null</code> if there is no configuration storage.*/
	private IOKit<C> configurationIOKit=null;

		/**@return The configuration storage I/O kit, or <code>null</code> if there is no configuration storage.*/
		public IOKit<C> getConfigurationIOKit() {return configurationIOKit;}

		/**Sets the configuration storage I/O kit.
		@param ioKit The configuration storage I/O kit, or <code>null</code> if
			there should be no configuration.
		*/
		protected void setConfigurationIOKit(final IOKit<C> ioKit) {configurationIOKit=ioKit;}


	/**Reference URI constructor.
	@param referenceURI The reference URI of the application.
	*/
	public AbstractConfiguredFramedSwingApplication(final URI referenceURI)
	{
		this(referenceURI, NO_ARGUMENTS);	//construct the class with no arguments
	}

	/**Reference URI and arguments constructor.
	@param referenceURI The reference URI of the application.
	@param args The command line arguments.
	*/
	public AbstractConfiguredFramedSwingApplication(final URI referenceURI, final String[] args)
	{
		super(referenceURI, args);	//construct the parent class
	}

	/**Initializes the application.
	This method is called after construction but before application execution.
	This version loads the configuration information, if it exists.
	@exception Exception Thrown if anything goes wrong.
	*/
	public void initialize() throws Exception	//TODO create a flag that only allows initialization once
	{
		super.initialize();	//do the default initialization
		loadConfiguration();	//load the configuration
		if(getConfiguration()==null)	//if we were unable to load the configuration
		{
			final C configuration=createDefaultConfiguration();	//create a default configuration
			if(configuration!=null)	//if we created a default configuration
			{
				setConfiguration(configuration);	//set the configuration
			}
		}
	}

	/**Creates a default configuration if one cannot be loaded.
	This version returns <code>null</code>.
	@return A default configuration, or <code>null</code> if the application
		does not need a configuration.
	*/
	protected C createDefaultConfiguration()
	{
		return null;	//this version doesn't create a configuration
	}

	/**Loads configuration information.
	@throws IOException if there is an error loading the configuration information.
	*/
	protected void loadConfiguration() throws IOException
	{
		final IOKit<C> configurationIOKit=getConfigurationIOKit();	//see if we can access the configuration
		if(configurationIOKit!=null)	//if we can load application configuration information
		{
			C configuration=null;	//we'll try to get the configuration from somewhere
			try
			{
				final File configurationFile=getConfigurationFile();	//get the configuration file
				if(Files.checkExists(configurationFile))	//if there is a configuration file (or a backup configuration file)
				{
					configuration=configurationIOKit.load(Files.toURI(configurationFile));	//ask the I/O kit to load the configuration file
				}
			}
			catch(SecurityException securityException)	//if we can't access the configuration file
			{
				Log.warn(securityException);	//warn of the security problem			
			}
			setConfiguration(configuration);	//set the configuration to whatever we found
			setModified(false);	//the application has not been modified, as its configuration has just been loaded
		}
	}

	/**Saves the configuration.
	@throws IOException if there is an error saving the configuration information.
	*/
	public void saveConfiguration() throws IOException
	{
		final IOKit<C> configurationIOKit=getConfigurationIOKit();	//see if we can access the configuration
		final C configuration=getConfiguration();	//get the configuration
		if(configurationIOKit!=null && configuration!=null)	//if we can save application configuration information, and there is configuration information to save
		{
			try
			{
				final File configurationFile=getConfigurationFile();	//get the configuration file
				final File configurationDirectory=configurationFile.getParentFile();	//get the directory of the file
				if(!configurationDirectory.exists() || !configurationDirectory.isDirectory())	//if the directory doesn't exist as a directory
				{
					Files.mkdirs(configurationDirectory);	//create the directory
				}
				final File tempFile=Files.getTempFile(configurationFile);  //get a temporary file to write to

				final File backupFile=Files.getBackupFile(configurationFile);  //get a backup file
				configurationIOKit.save(configuration, Files.toURI(tempFile));	//ask the I/O kit to save the configuration to the temporary file
				Files.moveFile(tempFile, configurationFile, backupFile); //move the temp file to the normal file, creating a backup if necessary
				setModified(false);	//the application has not been modified, as its configuration has just been saved
			}
			catch(SecurityException securityException)	//if we can't access the configuration file
			{
				Log.warn(securityException);	//warn of the security problem			
			}
		}
	}
	
	/**Determines whether the application can exit.
	This method may query the user.
	If the application has been modified, the configuration is saved if possible.
	If there is no configuration I/O kit, no action is taken.
	If an error occurs, the user is notified.
	@return <code>true</code> if the application can exit, else <code>false</code>.
	*/
	protected boolean canExit()
	{
		if(!super.canExit())	//do the default checks
		{
			return false;
		}
			//if there is configuration information and it has been modified
		if(isModified())	//if the application has been modified
		{
			try
			{
				saveConfiguration();	//save the configuration
			}
			catch(IOException ioException)	//if there is an error saving the configuration
			{
				displayError(ioException);	//alert the user of the error
/*TODO fix for Swing
					//ask if we can close even though we can't save the configuration information
				canClose=JOptionPane.showConfirmDialog(this,
					"Unable to save configuration information; are you sure you want to close?",
					"Unable to save configuration", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;	//TODO i18n
*/
			}
		}
		return true;	//show that we can exit
	}

	/**@return Whether the object been modified.*/
	public boolean isModified() {return modified;}

	/**Sets whether the object has been modified.
	This is a bound property.
	@param newModified The new modification status.
	*/
	public void setModified(final boolean newModified)
	{
		final boolean oldModified=modified; //get the old modified value
		if(oldModified!=newModified)  //if the value is really changing
		{
			modified=newModified; //update the value
				//show that the modified property has changed
//TODO fix			firePropertyChange(MODIFIED_PROPERTY, Boolean.valueOf(oldModified), Boolean.valueOf(newModified));
		}
	}

}

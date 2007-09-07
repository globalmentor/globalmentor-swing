package com.garretwilson.swing;

import java.awt.Component;
import java.awt.Rectangle;
import java.io.*;
import java.net.URI;
import java.util.prefs.Preferences;

import javax.swing.*;
import com.garretwilson.io.*;
import com.garretwilson.model.*;
import static com.garretwilson.net.URIConstants.*;
import com.garretwilson.net.http.HTTPClient;
import com.garretwilson.rdf.DefaultRDFResource;
import com.garretwilson.rdf.RDFFileProcessor;
import com.garretwilson.rdf.RDFResource;
import com.garretwilson.util.Debug;
import com.garretwilson.util.prefs.PreferencesUtilities;

/**An implementation of a resource selector that selects resources from a
	file system and returns RDF resources as descriptions of selected files.
@author Garret Wilson
*/
public class FileRDFResourceSelector extends DefaultURIAccessible implements ResourceSelector<RDFResource>
{

	/**The preference for storing the current directory.*/
	protected final String CURRENT_DIRECTORY_PREFERENCE=PreferencesUtilities.getPreferenceName(getClass(), "current.directory");

	/**The preferences that should be used for this object, or <code>null</code> if the default preferences for this class should be used.*/
	private Preferences preferences=null;
	
		/**@return The preferences that should be used for this object, or the default preferences for this class if no preferences are specifically set.
		@exception SecurityException Thrown if a security manager is present and it denies <code>RuntimePermission("preferences")</code>.
		*/
		public Preferences getPreferences() throws SecurityException
		{
			return preferences!=null ? preferences: Preferences.userNodeForPackage(getClass());	//return the user preferences node for whatever class extends this one 
		}
		
		/**Sets the preferences to be used for this panel.
		@param preferences The preferences that should be used for this panel, or <code>null</code> if the default preferences for this class should be used
		*/
		public void setPreferences(final Preferences preferences)
		{
			this.preferences=preferences;	//store the preferences
		}

	/**The component to serve as a parent for file dialogs.*/
	private final Component parentComponent;

		/**@return The component to serve as a parent for file dialogs.*/
		protected Component getParentComponent() {return parentComponent;}

	/**Parent component constructor using a default HTTP client.
	@param parentComponent The component to serve as a parent for file dialogs.
	*/
	public FileRDFResourceSelector(final Component parentComponent)
	{
		this(parentComponent, HTTPClient.getInstance());	//construct the class with the default HTTP client
	}

	/**Parent component and HTTP client constructor.
	@param parentComponent The component to serve as a parent for file dialogs.
	@param httpClient The client used to access HTTP URIs.
	*/
	public FileRDFResourceSelector(final Component parentComponent, final HTTPClient httpClient)
	{
		super(httpClient);	//call the default constructor, using the default implementation for accessing files and the given HTTP client for accessing HTTP
		this.parentComponent=parentComponent;	//save the parent component
	}

	/**Retrieves a description of the resource with the given reference URI.
	@param referenceURI The reference URI of the resource in question.
	@return A description of the identified resource.
	@exception IOException Thrown if there is an error retrieving the resource
		description.
	*/
	public RDFResource getResource(final URI referenceURI) throws IOException
	{
		if(FILE_SCHEME.equals(referenceURI.getScheme()))	//if this is a file:// URI
		{ 
			final File file=new File(referenceURI);	//create a new file from the reference URI
			return new RDFFileProcessor().createResource(file);	//return a resource representing the selected file
		}
		else	//if this is not a file URI
		{
			return new DefaultRDFResource(referenceURI);	//create a default resource from the reference URI
		}
	}

	/**Selects a resource for input.
	@param oldResource The currently selected resource, if applicable, or
		<code>null</code> if there is no selected resource.
	@return The selected resource, or <code>null</code> if selection was
		canceled.
	@exception SecurityException Thrown if selecting an input resource is not allowed.
	@exception IOException Thrown if there is an error locating a resource.
	*/
	public RDFResource selectInputResource(final RDFResource oldResource) throws SecurityException, IOException
	{
			//if we were given a resource with a valid file URI, create a file from that URI and get the parent directory of that file
		File currentDirectory=oldResource!=null && oldResource.getURI()!=null && FILE_SCHEME.equals(oldResource.getURI().getScheme())
				? new File(oldResource.getURI()).getParentFile()	//get the parent file
				: null;	//if this was not a file URI, we can't get the current directory
		if(currentDirectory==null)	//if we don't know the current directory, try to use the one from the preferences
		{
			try
			{
				final Preferences preferences=getPreferences();	//get the preferences
				final String currentDirectoryString=preferences.get(CURRENT_DIRECTORY_PREFERENCE, null);	//get the current directory from the preferences
				if(currentDirectoryString!=null)	//if we know a current directory from the preferences
				{
					currentDirectory=new File(currentDirectoryString);	//create a file with the current directory
				}
			}
			catch(SecurityException securityException)	//if we can't access preferences
			{
				Debug.warn(securityException);	//warn of the security problem			
			}			
		}
		final JFileChooser fileChooser=new JFileChooser(currentDirectory);	//create a new dialog for listing files
		final int option=fileChooser.showOpenDialog(getParentComponent());	//show the open dialog
		if(option==JFileChooser.APPROVE_OPTION)	//if they chose a file
		{
			try
			{
				final Preferences preferences=getPreferences();	//get the preferences
				preferences.put(CURRENT_DIRECTORY_PREFERENCE, fileChooser.getCurrentDirectory().toString());	//store the current directory name in the preferences
			}
			catch(SecurityException securityException)	//if we can't access preferences
			{
				Debug.warn(securityException);	//warn of the security problem			
			}
			final File selectedFile=fileChooser.getSelectedFile();	//get the file they chose
			if(selectedFile!=null)	//if they chose a file
			{
				return getResource(selectedFile.toURI());	//convert the file into a URI and create a resource to describe it
			}
		}
		return null;	//show that we couldn't select a file	
		
	}

	/**Selects a resource for output.
	@param oldResource The currently selected resource, if applicable, or
		<code>null</code> if there is no selected resource.
	@return The selected resource, or <code>null</code> if selection was
		canceled.
	@exception SecurityException Thrown if selecting an output resource is not allowed.
	@exception IOException Thrown if there is an error locating a resource.
	*/
	public RDFResource selectOutputResource(final RDFResource oldResource) throws SecurityException, IOException
	{
		throw new UnsupportedOperationException();	//indicate that this operation is not yet supported 
	}

}

package com.garretwilson.swing;

import java.awt.Component;
import java.io.*;
import java.net.URI;
import javax.swing.*;
import com.garretwilson.io.*;
import com.garretwilson.model.*;
import static com.garretwilson.net.URIConstants.*;
import com.garretwilson.net.http.HTTPClient;
import com.garretwilson.rdf.DefaultRDFResource;
import com.garretwilson.rdf.RDFFileProcessor;

/**An implementation of a resource selector that selects resources from a
	file system and returns RDF resources as descriptions of selected files.
@author Garret Wilson
*/
public class FileRDFResourceSelector extends DefaultURIAccessible implements ResourceSelector
{

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
	public Resource getResource(final URI referenceURI) throws IOException
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
	public Resource selectInputResource(final Resource oldResource) throws SecurityException, IOException
	{
			//if we were given a resource with a valid file URI, create a file from that URI and get the parent directory of that file
		final File currentDirectory=oldResource!=null && oldResource.getReferenceURI()!=null && FILE_SCHEME.equals(oldResource.getReferenceURI().getScheme())
				? new File(oldResource.getReferenceURI()).getParentFile()	//get the parent file
				: null;	//if this was not a file URI, we can't get the current directory
		final JFileChooser fileChooser=new JFileChooser(currentDirectory);	//create a new dialog for listing files TODO set the current directory
//G***fix current directory				fileChooser.setCurrentDirectory(getReaderConfig().getFileLocations().getCurrentDirectory());	//change the file chooser directory to the reader's current directory
		final int option=fileChooser.showOpenDialog(getParentComponent());	//show the open dialog
		if(option==JFileChooser.APPROVE_OPTION)	//if they chose a file
		{
//TODO update current directlry					getReaderConfig().getFileLocations().setCurrentDirectory(fileChooser.getCurrentDirectory());	//save the new directory they changed to
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
	public Resource selectOutputResource(final Resource oldResource) throws SecurityException, IOException
	{
	return null;	//TODO fix save dialog
	}

}
